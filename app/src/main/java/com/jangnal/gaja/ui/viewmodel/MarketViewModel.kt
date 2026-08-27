package com.jangnal.gaja.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jangnal.gaja.data.local.entity.Market
import com.jangnal.gaja.data.local.entity.Shop
import com.jangnal.gaja.data.repository.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MarketViewModel(
    private val repository: MarketRepository
) : ViewModel() {

    // 오늘 날짜 (Timestamp)
    private val _today = MutableStateFlow(System.currentTimeMillis())
    val today: StateFlow<Long> = _today.asStateFlow()

    val allMarkets: StateFlow<List<Market>> = repository.allMarkets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 오늘 열리는 시장 (5일장 + 전통시장 통합)
    val todayMarkets: StateFlow<List<Market>> = combine(allMarkets, _today) { markets, dateMillis ->
        markets.filter { market ->
            if (market.isPermanent()) {
                true // 전통시장(상설)은 오늘 항상 열림!
            } else {
                // 오늘 열리는 날짜인지 확인
                val cal = Calendar.getInstance()
                cal.timeInMillis = dateMillis
                val dayDigit = cal.get(Calendar.DAY_OF_MONTH) % 10
                
                val targetDigits = market.openingCycle.filter { it.isDigit() }.map { it.toString().toInt() }
                targetDigits.contains(dayDigit)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 상설시장 목록
    val permanentMarkets: StateFlow<List<Market>> = allMarkets
        .map { markets -> markets.filter { it.isPermanent() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 즐겨찾기 시장 목록
    val favoriteMarkets: StateFlow<List<Market>> = repository.favoriteMarkets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(market: Market) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(market.id, !market.isFavorite)
        }
    }

    fun voteMarketStatus(marketId: Long, isOpenToday: Boolean) {
        viewModelScope.launch {
            val todayStr = getTodayDateString()
            repository.submitVote(marketId, isOpenToday, todayStr)
        }
    }

    private fun getTodayDateString(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month, day)
    }


    // Called from Activity on creation
    fun checkAndLoadInitialData(context: Context) {
        viewModelScope.launch {
            if (repository.allMarkets.stateIn(viewModelScope).value.isEmpty()) {
                // If DB is empty, try loading from CSV
                try {
                    val inputStream = context.assets.open("markets.csv")
                    repository.loadDataFromCsv(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback to sample data if no CSV
                    seedSampleData()
                }
            }
            
            // Trigger background Gist sync periodically
            launch {
                val gistUrl = "https://gist.githubusercontent.com/Brasshun/ad574306a1414bf4bf60dc38416683f4/raw/markets.json"
                val result = repository.syncDataFromJson(gistUrl, context)
                if (result) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "최신 장날 정보가 업데이트되었습니다. 🏪", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun seedSampleData() {
        viewModelScope.launch {
             val sampleMarkets = listOf(
                Market(
                    marketName = "예시 시장 (데이터 로딩 실패)",
                    addressRoad = "서울특별시 중구",
                    addressJibun = "서울 중구",
                    latitude = 37.5665,
                    longitude = 126.9780,
                    openingCycle = "매일", 
                    specialty = "맛있는 것들",
                    phoneNumber = "02-123-4567"
                )
            )
             repository.insertAll(sampleMarkets)
        }
    }

    // --- Shop / Wait Times State and Actions ---
    private val _activeMarketShops = MutableStateFlow<List<Shop>>(emptyList())
    val activeMarketShops: StateFlow<List<Shop>> = _activeMarketShops.asStateFlow()

    private var shopsCollectJob: kotlinx.coroutines.Job? = null

    fun loadShopsForMarket(market: Market) {
        shopsCollectJob?.cancel()
        shopsCollectJob = viewModelScope.launch {
            // 1. Prepopulate default mock shops if empty
            repository.getShopsForMarketWithPrepopulate(
                market.id,
                market.marketName,
                market.latitude,
                market.longitude
            )
            // 2. Observe changes in real time
            repository.getShopsForMarketFlow(market.id).collect {
                _activeMarketShops.value = it
            }
        }
    }

    fun addShopToMarket(marketId: Long, name: String, category: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            val newShop = Shop(
                marketId = marketId,
                shopName = name,
                category = category,
                latitude = lat,
                longitude = lon
            )
            repository.insertShop(newShop)
        }
    }

    fun reportShopQueue(shopId: Long, status: Int, marketLat: Double, marketLon: Double, userLocation: android.location.Location?) {
        viewModelScope.launch {
            var isVerified = false
            if (userLocation != null) {
                val results = FloatArray(1)
                android.location.Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    marketLat, marketLon,
                    results
                )
                isVerified = results[0] <= 100f // Verified if user is within 100m of the market center
            }
            repository.updateShopQueue(shopId, status, isVerified)
        }
    }

    // --- Kakao Places Search State and Actions ---
    private val _searchResults = MutableStateFlow<List<Shop>>(emptyList())
    val searchResults: StateFlow<List<Shop>> = _searchResults.asStateFlow()

    fun searchShopsNearby(query: String, lat: Double, lon: Double, apiKey: String, marketId: Long) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }
            val results = repository.searchKakaoPlaces(query, lat, lon, apiKey, marketId)
            _searchResults.value = results
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun reportMarketAmenity(marketId: Long, amenityType: String, hasIt: Boolean) {
        viewModelScope.launch {
            val value = if (hasIt) "Y" else "N"
            if (amenityType == "toilet") {
                repository.updateMarketToilet(marketId, value)
            } else if (amenityType == "parking") {
                repository.updateMarketParking(marketId, value)
            }
        }
    }
}

class MarketViewModelFactory(private val repository: MarketRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
