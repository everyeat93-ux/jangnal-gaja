package com.jangnal.gaja.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.jangnal.gaja.data.local.entity.Market
import com.jangnal.gaja.data.local.entity.Festival
import com.jangnal.gaja.ui.components.MarketItem
import com.jangnal.gaja.ui.viewmodel.MarketViewModel
import kotlinx.coroutines.launch

import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth

import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import android.location.Location
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun MarketListScreen(
    viewModel: MarketViewModel,
    onMarketClick: (Market) -> Unit,
    currentTextScale: Float,
    onTextScaleChange: (Float) -> Unit
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    
    // 위치 권한 요청 Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            try {
                val fused = LocationServices.getFusedLocationProviderClient(context)
                fused.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        userLocation = loc
                    } else {
                        fused.getCurrentLocation(
                            com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            null
                        ).addOnSuccessListener { cur ->
                            if (cur != null) userLocation = cur
                        }
                    }
                }
            } catch (_: SecurityException) {}
        }
    }

    val fetchLocation: () -> Unit = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                val fused = LocationServices.getFusedLocationProviderClient(context)
                fused.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        userLocation = loc
                    } else {
                        fused.getCurrentLocation(
                            com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            null
                        ).addOnSuccessListener { cur ->
                            if (cur != null) userLocation = cur
                        }
                    }
                }
            } catch (_: SecurityException) {}
        } else {
            android.widget.Toast.makeText(context, "📍 가까운 시장 거리순 정렬을 위해 위치 권한을 허용해 주세요.", android.widget.Toast.LENGTH_SHORT).show()
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }
    
    LaunchedEffect(Unit) {
        fetchLocation()
    }

    val todayMarkets by viewModel.todayMarkets.collectAsState()
    val allMarkets by viewModel.allMarkets.collectAsState()
    val todayDate by viewModel.today.collectAsState()
    val allFestivals by viewModel.allFestivals.collectAsState()
    
    // 날짜 포맷팅
    val dateFormat = remember { SimpleDateFormat("M월 d일 (E)", Locale.KOREA) }
    val formattedDate = dateFormat.format(Date(todayDate))

    val tabs = listOf("지도", "시장 목록")
    val pagerState = rememberPagerState(
        initialPage = 0, // 지도 탭을 기본으로
        pageCount = { tabs.size }
    )
    val scope = rememberCoroutineScope()
    
    // State for Detail BottomSheet
    var selectedMarket by remember { androidx.compose.runtime.mutableStateOf<Market?>(null) }
    
    // State for About Screen
    var showAbout by remember { mutableStateOf(false) }

    // State for Community Feed Sheet
    var showCommunityFeed by remember { mutableStateOf(false) }

    // State for Passport Sheet
    var showPassport by remember { mutableStateOf(false) }

    val recentCommunityPosts by viewModel.recentCommunityFeed.collectAsState()
    val allMarketsList by viewModel.allMarkets.collectAsState()
    val allStamps by viewModel.allStamps.collectAsState()
    val uniqueMarketStampCount by viewModel.uniqueMarketCount.collectAsState()

    LaunchedEffect(selectedMarket) {
        viewModel.clearSearchResults()
        selectedMarket?.let { market ->
            viewModel.loadShopsForMarket(market)
        }
    }

    if (selectedMarket != null) {
        val activeShops by viewModel.activeMarketShops.collectAsState()
        val searchResults by viewModel.searchResults.collectAsState()
        val activeReviews by viewModel.activeShopReviews.collectAsState()
        val activeFestivals by viewModel.activeMarketFestivals.collectAsState()
        val activeCommunityPosts by viewModel.activeMarketCommunityPosts.collectAsState()
        val activeComments by viewModel.activeMarketComments.collectAsState()
        val activeFlashSales by viewModel.activeMarketFlashSales.collectAsState()
        val kakaoApiKey = context.getString(com.jangnal.gaja.R.string.kakao_rest_api_key)

        com.jangnal.gaja.ui.components.MarketDetailSheet(
            market = selectedMarket!!,
            shops = activeShops,
            searchResults = searchResults,
            festivals = activeFestivals,
            communityPosts = activeCommunityPosts,
            comments = activeComments,
            reviews = activeReviews,
            stamps = allStamps,
            flashSales = activeFlashSales,
            userLocation = userLocation,
            onFavoriteToggle = { viewModel.toggleFavorite(it) },
            onVoteClick = { marketId, isOpen -> viewModel.voteMarketStatus(marketId, isOpen) },
            onReportQueue = { shopId, status ->
                selectedMarket?.let { market ->
                    viewModel.reportShopQueue(shopId, status, market.latitude, market.longitude, userLocation)
                }
            },
            onAddShop = { name, category ->
                selectedMarket?.let { market ->
                    viewModel.addShopToMarket(
                        context = context,
                        marketId = market.id,
                        name = name,
                        category = category,
                        lat = market.latitude,
                        lon = market.longitude,
                        onSuccess = { shop ->
                            android.widget.Toast.makeText(context, "✅ '${shop.shopName}' 상점이 등록되었습니다! 🏪\n(10분 이내 직접 취소 가능)", android.widget.Toast.LENGTH_LONG).show()
                        },
                        onDuplicate = { dup ->
                            android.widget.Toast.makeText(context, "⚠️ 이미 등록된 상점입니다: '${dup.shopName}'", android.widget.Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            onDeleteShop = { shopName ->
                selectedMarket?.let { market ->
                    viewModel.deleteShopFromMarket(context, market.id, shopName)
                    android.widget.Toast.makeText(context, "🗑️ '${shopName}' 상점 등록이 취소(삭제)되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
                }
            },
            onReportShopIssue = { shopName, reason, detail ->
                selectedMarket?.let { market ->
                    viewModel.reportShopIssue(context, market.id, shopName, reason, detail)
                    android.widget.Toast.makeText(context, "🚨 '${shopName}' 관련 제보가 접수되었습니다.\n검토 후 신속히 반영하겠습니다. 감사합니다!", android.widget.Toast.LENGTH_LONG).show()
                }
            },
            onSearchShops = { query ->
                selectedMarket?.let { market ->
                    viewModel.searchShopsNearby(query, market.latitude, market.longitude, kakaoApiKey, market.id)
                }
            },
            onClearSearchShops = {
                viewModel.clearSearchResults()
            },
            onReportAmenity = { amenityType, hasIt ->
                selectedMarket?.let { market ->
                    viewModel.reportMarketAmenity(market.id, amenityType, hasIt)
                }
            },
            onSubmitReview = { shopName, rating, content, imageUri ->
                selectedMarket?.let { market ->
                    viewModel.submitShopReview(context, market.id, shopName, rating, content, imageUri)
                }
            },
            onConfirmOnnuri = { shopId ->
                viewModel.confirmOnnuriPayment(shopId)
            },
            onSubmitCommunityPost = { nickname, category, content, photoUri ->
                selectedMarket?.let { market ->
                    viewModel.submitCommunityPost(
                        context = context,
                        market = market,
                        nickname = nickname,
                        category = category,
                        content = content,
                        photoUri = photoUri,
                        userLocation = userLocation,
                        onSuccess = {}
                    )
                }
            },
            onLikeCommunityPost = { postId ->
                selectedMarket?.let { market ->
                    viewModel.likeCommunityPost(market.id, postId)
                }
            },
            onReportCommunityPost = { postId, authorHash, reason ->
                selectedMarket?.let { market ->
                    viewModel.reportCommunityPost(context, market.id, postId, authorHash, reason)
                }
            },
            onBlockCommunityAuthor = { authorHash ->
                viewModel.blockCommunityAuthor(context, authorHash)
            },
            onSubmitCommunityComment = { postId, nickname, content ->
                selectedMarket?.let { market ->
                    viewModel.submitCommunityComment(
                        context = context,
                        market = market,
                        postId = postId,
                        nickname = nickname,
                        content = content,
                        userLocation = userLocation
                    )
                }
            },
            onDeleteCommunityComment = { commentId ->
                selectedMarket?.let { market ->
                    viewModel.deleteCommunityComment(context, market.id, commentId)
                }
            },
            onReportCommunityComment = { postId, commentId, authorHash, reason ->
                selectedMarket?.let { market ->
                    viewModel.reportCommunityComment(context, market.id, postId, commentId, authorHash, reason)
                }
            },
            onClaimStamp = { memo, photoUri ->
                selectedMarket?.let { market ->
                    viewModel.claimStamp(
                        context = context,
                        market = market,
                        memo = memo,
                        photoUri = photoUri,
                        userLocation = userLocation
                    )
                }
            },
            onSubmitFlashSale = { shopName, itemTitle, origPrice, discPrice, qtyInfo, durationHours, isVerified ->
                selectedMarket?.let { market ->
                    viewModel.submitFlashSale(
                        context = context,
                        market = market,
                        shopName = shopName,
                        itemTitle = itemTitle,
                        originalPrice = origPrice,
                        discountPrice = discPrice,
                        quantityInfo = qtyInfo,
                        durationHours = durationHours,
                        isVerifiedMerchant = isVerified
                    )
                }
            },
            onDeleteFlashSale = { saleId ->
                viewModel.deleteFlashSale(saleId) {
                    android.widget.Toast.makeText(context, "🗑️ 마감 특가가 종료(삭제)되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
                }
            },
            onDismissRequest = { selectedMarket = null }
        )
    }
    
    if (showAbout) {
        AboutScreen(
            onDismiss = { showAbout = false },
            currentScale = currentTextScale,
            onScaleChange = onTextScaleChange
        )
    }

    if (showCommunityFeed) {
        com.jangnal.gaja.ui.components.CommunityFeedSheet(
            posts = recentCommunityPosts,
            markets = allMarketsList,
            onSelectMarket = { market ->
                showCommunityFeed = false
                selectedMarket = market
            },
            onLikePost = { marketId, postId ->
                viewModel.likeCommunityPost(marketId, postId)
            },
            onReportPost = { marketId, postId, authorHash, reason ->
                viewModel.reportCommunityPost(context, marketId, postId, authorHash, reason)
            },
            onBlockAuthor = { authorHash ->
                viewModel.blockCommunityAuthor(context, authorHash)
            },
            onDismissRequest = { showCommunityFeed = false }
        )
    }

    if (showPassport) {
        com.jangnal.gaja.ui.components.PassportSheet(
            stamps = allStamps,
            allMarkets = allMarketsList,
            onSelectMarket = { market ->
                showPassport = false
                selectedMarket = market
            },
            onDeleteStamp = { stampId ->
                viewModel.deleteStamp(stampId)
            },
            onDismissRequest = { showPassport = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.jangnal.gaja.R.drawable.header_logo),
                        contentDescription = "장날 가자",
                        modifier = Modifier.height(40.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.jangnal.gaja.ui.theme.JangnalYellow,
                    titleContentColor = com.jangnal.gaja.ui.theme.JangnalBrown,
                    actionIconContentColor = com.jangnal.gaja.ui.theme.JangnalBrown
                ),
                actions = {
                    // 📘 나의 여권 버튼
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF162433),
                        border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                        modifier = Modifier.clickable { showPassport = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (uniqueMarketStampCount > 0) "📘 여권 ${uniqueMarketStampCount}곳" else "📘 여권",
                                fontSize = 11.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = com.jangnal.gaja.ui.theme.JangnalBrown,
                        modifier = Modifier.clickable { showCommunityFeed = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "💬 동네마당",
                                fontSize = 11.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = com.jangnal.gaja.ui.theme.JangnalYellow
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    IconButton(onClick = { showAbout = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "앱 정보"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).background(com.jangnal.gaja.ui.theme.BackgroundCream)) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = com.jangnal.gaja.ui.theme.JangnalYellow,
                contentColor = com.jangnal.gaja.ui.theme.JangnalBrown,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = com.jangnal.gaja.ui.theme.JangnalBrown,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { 
                            Text(
                                title, 
                                fontWeight = if(pagerState.currentPage == index) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                                fontSize = 18.sp
                            ) 
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        selectedContentColor = com.jangnal.gaja.ui.theme.JangnalBrown,
                        unselectedContentColor = com.jangnal.gaja.ui.theme.JangnalBrown.copy(alpha = 0.6f)
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = pagerState.currentPage != 0 // 지도 탭(0번)에서는 스와이프 비활성화
            ) { page ->
                when (page) {
                    0 -> { // Map (맨 앞으로 이동)
                        val mapMarkets = allMarkets
                        if (mapMarkets.isEmpty()) {
                            EmptyState("지도에 표시할 시장이 없습니다.")
                        } else {
                            KakaoMapScreen(markets = mapMarkets, onMarketClick = { selectedMarket = it })
                        }
                    }
                    1 -> { // All List + Today Filter
                        MarketList(
                            markets = allMarkets,
                            festivals = allFestivals,
                            dateLabel = formattedDate,
                            userLocation = userLocation,
                            onRequestLocation = fetchLocation,
                            onMarketClick = { selectedMarket = it }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun MarketList(
    markets: List<Market>, 
    festivals: List<Festival> = emptyList(),
    dateLabel: String?, 
    userLocation: Location?,
    onRequestLocation: () -> Unit = {},
    onMarketClick: (Market) -> Unit
) {
    if (markets.isEmpty()) {
         EmptyState("데이터가 없습니다.")
    } else {
        var searchQuery by remember { mutableStateOf("") }
        // 0: 가나다순, 1: 거리순 (위치 정보가 있으면 거리순 기본)
        var sortType by remember(userLocation != null) { mutableIntStateOf(if (userLocation != null) 1 else 0) }
        var filterOpenToday by remember { mutableStateOf(false) }
        var filterOpenWeekend by remember { mutableStateOf(false) }
        // 0: 전국, 1: 서울, 2: 경기, 3: 인천, 4: 강원, 5: 충청/대전, 6: 전라/광주, 7: 경상/부산, 8: 제주
        var selectedRegionIndex by remember { mutableIntStateOf(0) }
        val regions = listOf("전국", "서울", "경기", "인천", "강원", "충청/대전", "전라/광주", "경상/부산", "제주")
        
        // 3차 필터: 시장 유형 및 장날 주기 필터
        var selectedScheduleFilter by remember { mutableStateOf("전체 유형") }
        val scheduleFilters = listOf("전체 유형", "🗺️ 여행 코스 추천 장", "🎪 축제·행사 열리는 장", "🏪 상설시장", "🎪 5일장", "1·6일장", "2·7일장", "3·8일장", "4·9일장", "5·10일장")
        
        val activeFestivalMarketIds = remember(festivals) {
            festivals.filter { !it.isExpired() }.map { it.marketId }.toSet()
        }

        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val nationwideMatches = remember(markets, searchQuery) {
            if (searchQuery.isBlank()) emptyList()
            else markets.filter {
                it.marketName.contains(searchQuery, ignoreCase = true) ||
                it.getDisplayName().contains(searchQuery, ignoreCase = true) ||
                it.addressRoad.contains(searchQuery, ignoreCase = true) ||
                it.addressJibun.contains(searchQuery, ignoreCase = true)
            }
        }
        
        val filteredMarkets = remember(markets, searchQuery, sortType, filterOpenToday, filterOpenWeekend, selectedRegionIndex, selectedScheduleFilter, userLocation, activeFestivalMarketIds) {
            var filtered = if (searchQuery.isBlank()) markets
            else markets.filter { 
                it.marketName.contains(searchQuery, ignoreCase = true) ||
                it.getDisplayName().contains(searchQuery, ignoreCase = true) ||
                it.addressRoad.contains(searchQuery, ignoreCase = true) ||
                it.addressJibun.contains(searchQuery, ignoreCase = true)
            }
            
            if (filterOpenToday) {
                val todayMillis = System.currentTimeMillis()
                filtered = filtered.filter { it.isPermanent() || it.isOpenOn(todayMillis) }
            }
            if (filterOpenWeekend) {
                filtered = filtered.filter { it.isOpenThisWeekend() }
            }
            
            // 2차: 광역 지역 필터링 (수도권을 서울/경기/인천으로 세분화)
            filtered = when (selectedRegionIndex) {
                1 -> filtered.filter { m -> // 서울
                    m.addressRoad.contains("서울") || m.addressJibun.contains("서울")
                }
                2 -> filtered.filter { m -> // 경기
                    m.addressRoad.contains("경기") || m.addressJibun.contains("경기")
                }
                3 -> filtered.filter { m -> // 인천
                    m.addressRoad.contains("인천") || m.addressJibun.contains("인천")
                }
                4 -> filtered.filter { m -> // 강원
                    m.addressRoad.contains("강원") || m.addressJibun.contains("강원")
                }
                5 -> filtered.filter { m -> // 충청/대전/세종
                    m.addressRoad.contains("충북") || m.addressJibun.contains("충북") ||
                    m.addressRoad.contains("충남") || m.addressJibun.contains("충남") ||
                    m.addressRoad.contains("충청") || m.addressJibun.contains("충청") ||
                    m.addressRoad.contains("대전") || m.addressJibun.contains("대전") ||
                    m.addressRoad.contains("세종") || m.addressJibun.contains("세종")
                }
                6 -> filtered.filter { m -> // 전라/광주
                    m.addressRoad.contains("전북") || m.addressJibun.contains("전북") ||
                    m.addressRoad.contains("전남") || m.addressJibun.contains("전남") ||
                    m.addressRoad.contains("전라") || m.addressJibun.contains("전라") ||
                    m.addressRoad.contains("광주") || m.addressJibun.contains("광주")
                }
                7 -> filtered.filter { m -> // 경상/부산/대구/울산
                    m.addressRoad.contains("경북") || m.addressJibun.contains("경북") ||
                    m.addressRoad.contains("경남") || m.addressJibun.contains("경남") ||
                    m.addressRoad.contains("경상") || m.addressJibun.contains("경상") ||
                    m.addressRoad.contains("부산") || m.addressJibun.contains("부산") ||
                    m.addressRoad.contains("대구") || m.addressJibun.contains("대구") ||
                    m.addressRoad.contains("울산") || m.addressJibun.contains("울산")
                }
                8 -> filtered.filter { m -> // 제주
                    m.addressRoad.contains("제주") || m.addressJibun.contains("제주")
                }
                else -> filtered // 전국
            }

            // 3차: 시장 유형 및 장날 주기 필터링
            if (selectedScheduleFilter == "🗺️ 여행 코스 추천 장") {
                val famousNames = listOf("정선", "속초", "강릉", "구로", "광장", "모란", "서문", "전주", "순천", "통인", "망원", "수원", "자갈치", "예산")
                filtered = filtered.filter { m -> famousNames.any { m.marketName.contains(it) } }
            } else if (selectedScheduleFilter == "🎪 축제·행사 열리는 장") {
                filtered = filtered.filter { activeFestivalMarketIds.contains(it.id) }
            } else if (selectedScheduleFilter != "전체 유형") {
                filtered = filtered.filter { it.matchesScheduleType(selectedScheduleFilter) }
            }
            
            when (sortType) {
                1 -> { // 거리순
                    if (userLocation != null) {
                         filtered.sortedBy { market ->
                             val results = FloatArray(1)
                             Location.distanceBetween(
                                 userLocation.latitude, userLocation.longitude,
                                 market.latitude, market.longitude,
                                 results
                             )
                             results[0]
                         }
                    } else {
                        filtered.sortedBy { it.getDisplayName() }
                    }
                }
                else -> filtered.sortedBy { it.getDisplayName() } // 가나다순
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("시장 이름(예: 구로, 속초), 특산물 또는 주소 검색", fontSize = 13.sp) },
                leadingIcon = { 
                    Icon(imageVector = Icons.Default.Search, contentDescription = "검색") 
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchQuery = "" 
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "지우기")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        // 검색 결과가 현재 지역에서 0건이지만 전국에 있을 경우 자동으로 전국으로 전환
                        if (filteredMarkets.isEmpty() && nationwideMatches.isNotEmpty() && selectedRegionIndex != 0) {
                            selectedRegionIndex = 0
                        }
                    }
                ),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            
            // 1차 필터: 정렬 & 개장일 필터 옵션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortType == 0,
                    onClick = { sortType = 0 },
                    label = { Text("가나다순") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = com.jangnal.gaja.ui.theme.JangnalYellow,
                        selectedLabelColor = com.jangnal.gaja.ui.theme.JangnalBrown
                    )
                )
                FilterChip(
                    selected = sortType == 1,
                    onClick = { 
                        sortType = 1
                        if (userLocation == null) {
                            onRequestLocation()
                        }
                    },
                    label = { Text(if (userLocation != null) "거리순 📍" else "거리순 📍 (위치 켜기)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = com.jangnal.gaja.ui.theme.JangnalYellow,
                        selectedLabelColor = com.jangnal.gaja.ui.theme.JangnalBrown
                    )
                )
                FilterChip(
                    selected = filterOpenToday,
                    onClick = { 
                        filterOpenToday = !filterOpenToday 
                        if (filterOpenToday) filterOpenWeekend = false
                    },
                    label = { Text(if (filterOpenToday) "오늘 개장 ☀️ ✓" else "오늘 개장 ☀️") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = com.jangnal.gaja.ui.theme.JangnalYellow,
                        selectedLabelColor = com.jangnal.gaja.ui.theme.JangnalBrown
                    )
                )
                FilterChip(
                    selected = filterOpenWeekend,
                    onClick = { 
                        filterOpenWeekend = !filterOpenWeekend 
                        if (filterOpenWeekend) filterOpenToday = false
                    },
                    label = { Text(if (filterOpenWeekend) "이번 주말 개장 🚗 ✓" else "이번 주말 개장 🚗") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = com.jangnal.gaja.ui.theme.JangnalYellow,
                        selectedLabelColor = com.jangnal.gaja.ui.theme.JangnalBrown
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 2차 필터: 전국 시/도 광역 지역 칩 (서울/경기/인천 세분화)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                regions.forEachIndexed { index, regionName ->
                    val isSelected = selectedRegionIndex == index
                    val icon = when (index) {
                        0 -> "🌐"
                        1 -> "🏢" // 서울
                        2 -> "🏙️" // 경기
                        3 -> "⚓" // 인천
                        4 -> "🌲" // 강원
                        5 -> "🌾" // 충청
                        6 -> "🍲" // 전라
                        7 -> "🌊" // 경상
                        else -> "🍊" // 제주
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRegionIndex = index },
                        label = { Text("$icon $regionName", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))

            // 3차 필터: 시장 유형 및 장날 주기 필터 (신설!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                scheduleFilters.forEach { schedule ->
                    val isSelected = selectedScheduleFilter == schedule
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedScheduleFilter = schedule },
                        label = { Text(schedule, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 💡 친절한 안내 팁 (상황에 따라 동적 가이드)
            val guideTipText = when {
                searchQuery.isNotBlank() -> "🔍 '${searchQuery}' 검색 결과: 총 ${filteredMarkets.size}곳의 시장"
                selectedScheduleFilter != "전체 유형" -> "📅 '${selectedScheduleFilter}' 조건의 시장을 모아보고 있습니다."
                filterOpenToday -> "☀️ 오늘 열리는 5일장 및 매일 열리는 상설시장 목록입니다."
                filterOpenWeekend -> "🚗 이번 주말(토/일)에 열리는 시장 목록입니다."
                else -> "💡 '오늘 개장'이나 장날 주기(1·6일, 2·7일 등)를 선택해 맞춤 시장을 찾아보세요."
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guideTipText,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredMarkets.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    color = Color.Transparent
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🔍", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (searchQuery.isNotBlank() && nationwideMatches.isNotEmpty() && selectedRegionIndex != 0) {
                            Text(
                                text = "'${regions[selectedRegionIndex]}' 지역에는 '${searchQuery}' 검색 결과가 없습니다.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 '전국'에 총 ${nationwideMatches.size}개의 일치하는 시장이 있습니다!",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    selectedRegionIndex = 0
                                    selectedScheduleFilter = "전체 유형"
                                    filterOpenToday = false
                                    filterOpenWeekend = false
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("🌐 전국 검색 결과 보기 (${nationwideMatches.size}개)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(
                                text = if (searchQuery.isNotBlank()) "'${searchQuery}' 검색 결과가 없습니다." else "조건에 일치하는 시장이 없습니다.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "다른 검색어를 입력하시거나 필터를 초기화해 보세요.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = {
                                searchQuery = ""
                                selectedRegionIndex = 0
                                selectedScheduleFilter = "전체 유형"
                                filterOpenToday = false
                                filterOpenWeekend = false
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ) {
                            Text("필터 및 검색어 전체 초기화 🔄", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (dateLabel != null) {
                        item {
                            Text(
                                text = "오늘 날짜: $dateLabel",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    items(filteredMarkets) { market ->
                        MarketItem(
                            market = market,
                            hasActiveFestival = activeFestivalMarketIds.contains(market.id),
                            onItemClick = onMarketClick
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
