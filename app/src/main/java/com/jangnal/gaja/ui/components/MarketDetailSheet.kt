package com.jangnal.gaja.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.jangnal.gaja.data.local.entity.Market
import com.jangnal.gaja.data.local.entity.Shop
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.InputChip

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun MarketDetailSheet(
    market: Market,
    shops: List<Shop> = emptyList(),
    searchResults: List<Shop> = emptyList(),
    reviews: Map<String, List<com.jangnal.gaja.ui.viewmodel.ShopReview>> = emptyMap(),
    userLocation: android.location.Location? = null,
    onFavoriteToggle: (Market) -> Unit = {},
    onVoteClick: (Long, Boolean) -> Unit = { _, _ -> },
    onReportQueue: (Long, Int) -> Unit = { _, _ -> },
    onAddShop: (String, String) -> Unit = { _, _ -> },
    onSearchShops: (String) -> Unit = {},
    onClearSearchShops: () -> Unit = {},
    onReportAmenity: (String, Boolean) -> Unit = { _, _ -> },
    onSubmitReview: (String, Float, String, Uri?) -> Unit = { _, _, _, _ -> },
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val specialtyParts = market.specialty.split("\n\n💡 특징: ")
        val displaySpecialty = specialtyParts[0].replace("+", ", ")
        val displayFeature = if (specialtyParts.size > 1) specialtyParts[1] else ""

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header - 시장 이름 및 즐겨찾기
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = market.marketName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                androidx.compose.material3.IconButton(
                    onClick = { onFavoriteToggle(market) }
                ) {
                    Icon(
                        imageVector = if (market.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "즐겨찾기",
                        tint = if (market.isFavorite) Color.Red else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // 시장 유형 뱃지
            Badge(
                text = market.getSimpleTypeText(),
                bgColor = MaterialTheme.colorScheme.primaryContainer,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // 개장 정보 섹션 (헬스케어/달력 스타일 반영)
            Text(
                text = "📅 개장 정보 및 달력",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // 캘린더 형태의 시각적 요소 제공
            MarketCalendarView(market = market)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!market.isPermanent()) {
                InfoItem(label = "시장 유형", value = market.getMarketTypeName())
                Spacer(modifier = Modifier.height(8.dp))
                
                val (cycle, _) = market.parseCyclePublic()
                if (cycle != null) {
                    InfoItem(label = "개장 주기", value = "${cycle}일 주기")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                InfoItem(
                    label = "다음 개장일", 
                    value = market.getNextMarketText(),
                    highlight = market.isOpenToday()
                )
            } else {
                InfoItem(label = "운영 주기", value = "매일 상설 운영")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // B2G Onnuri & Card Verification Rate Calculation
            val totalShopsCount = shops.size
            val paymentVerifiedCount = shops.count { shop ->
                val shopRevs = reviews[shop.shopName] ?: emptyList()
                shopRevs.any { it.content.contains("온누리") || it.content.contains("카드") || it.content.contains("간편결제") }
            }
            val hasShops = totalShopsCount > 0
            val dynamicPercent = if (hasShops) {
                if (paymentVerifiedCount > 0) {
                    ((paymentVerifiedCount.toFloat() / totalShopsCount.toFloat()) * 100).toInt().coerceIn(0, 100)
                } else {
                    50 // 점포 등록되었으나 아직 리뷰 검증 전
                }
            } else 0
            
            val isStandardMet = dynamicPercent >= 70
            val rateDescText = if (hasShops) {
                if (isStandardMet) "$dynamicPercent% (지자체 인증 표준 충족 🟢)" else "$dynamicPercent% (검증 진행 중 🟡)"
            } else {
                "데이터 수집 중 ⚪"
            }
            val progressVal = if (hasShops) (dynamicPercent / 100f).coerceIn(0f, 1f) else 0.0f
            val infoExplainText = if (hasShops) {
                if (isStandardMet) "💡 현장 방문객 제보 검증을 통해 지자체 디지털 전통시장 가맹 기준(70% 이상)을 달성한 시장입니다."
                else "💡 현재 ${paymentVerifiedCount}개 점포의 결제 수단이 검증되었습니다. (목표 기준 70%)"
            } else {
                "💡 등록된 상점이 없습니다. 상점 등록 후 결제 태그를 남겨주시면 지자체 표준 가맹률이 실시간 산출됩니다."
            }

            // B2G Onnuri & Card Verification Rate Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📊 온누리·카드 가맹 검증률", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(rateDescText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isStandardMet) Color(0xFF2E7D32) else if (hasShops) Color(0xFFE65100) else Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = progressVal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isStandardMet) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = infoExplainText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            
            // 편의 시설 섹션 (카드/아이콘화)
            Text(
                text = "🏗 편의 시설",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            var activeAmenityReport by remember { mutableStateOf<Pair<String, String>?>(null) } // amenityType to Label
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AmenityCard(
                    label = "공중화장실",
                    icon = "🚻",
                    hasAmenity = market.hasToilet == "Y",
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            activeAmenityReport = Pair("toilet", "공중화장실")
                        }
                )
                AmenityCard(
                    label = "주차 공간",
                    icon = "🅿️",
                    hasAmenity = market.hasParking == "Y",
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            activeAmenityReport = Pair("parking", "주차 공간")
                        }
                )
            }

            if (activeAmenityReport != null) {
                val (amenityType, label) = activeAmenityReport!!
                AlertDialog(
                    onDismissRequest = { activeAmenityReport = null },
                    title = { Text("$label 정보 제보", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("이 시장에 ${label}이(가) 실제로 존재하고 이용 가능한가요? 현장 기여를 통해 실시간으로 편의시설 정보를 업데이트할 수 있습니다.")
                    },
                    confirmButton = {},
                    dismissButton = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onReportAmenity(amenityType, true)
                                    activeAmenityReport = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("있음 / 이용 가능 🟢")
                            }
                            OutlinedButton(
                                onClick = {
                                    onReportAmenity(amenityType, false)
                                    activeAmenityReport = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("없음 / 정보 없음 ⚪")
                            }
                            if (amenityType == "parking") {
                                OutlinedButton(
                                    onClick = {
                                        launchNavigationToParking(context, market)
                                        activeAmenityReport = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("🅿️ 주변 공영주차장 길안내 검색 🚗", fontWeight = FontWeight.Bold)
                                }
                            }
                            TextButton(
                                onClick = { activeAmenityReport = null },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("취소")
                            }
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // 실시간 제보 및 투표 (크라우드소싱)
            Text(
                text = "💬 실시간 장날 제보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "오늘 시장이 열렸는지 현장의 소식을 실시간으로 공유해 주세요!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onVoteClick(market.id, true) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("👍 오늘 열렸어요", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${market.voteOpenTodayCount}명 제보", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                OutlinedButton(
                    onClick = { onVoteClick(market.id, false) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("👎 닫혔어요/안열려요", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${market.voteClosedTodayCount}명 제보", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
            
            // 위치 및 주요 품목 정보
            DetailRow(Icons.Default.LocationOn, market.addressRoad.ifEmpty { market.addressJibun })
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(Icons.Default.ShoppingBag, "주요 품목: $displaySpecialty")
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedButton(
                onClick = {
                    val cleanSpecialty = displaySpecialty.split(",").firstOrNull()?.trim() ?: ""
                    val cleanMarketName = market.marketName.replace("전통시장", "").replace("시장", "").trim()
                    val query = if (cleanSpecialty.isNotEmpty()) "$cleanMarketName $cleanSpecialty" else market.marketName
                    val shoppingUrl = "https://search.shopping.naver.com/search/all?query=" + Uri.encode(query)
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(shoppingUrl))
                    try { context.startActivity(intent) } catch (_: Exception) {}
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text("📦 ${market.marketName} 특산물 산지직송 / 온라인 택배 주문", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (displayFeature.isNotEmpty()) {
                Text(
                    text = "💡 특징",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = displayFeature,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (market.phoneNumber.isNotEmpty()) {
                Row(
                   modifier = Modifier
                       .fillMaxWidth()
                       .clickable {
                           val intent = Intent(Intent.ACTION_DIAL).apply {
                               data = "tel:${market.phoneNumber}".toUri()
                           }
                           try {
                               context.startActivity(intent)
                           } catch (_: Exception) {
                               // Ignore
                           }
                       }
                ) {
                    DetailRow(Icons.Default.Phone, market.phoneNumber)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:collcokorea@gmail.com?subject=" + Uri.encode("[장날가자] ${market.marketName} 시장 정보 수정/오류 제보"))
                        }
                        try { context.startActivity(emailIntent) } catch (_: Exception) {}
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚨 시장 정보가 다르거나 변경되었나요? (오류/수정 제보)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // 대기줄 정보 섹션
            ShopQueueSection(
                market = market,
                shops = shops,
                searchResults = searchResults,
                reviews = reviews,
                userLocation = userLocation,
                onReportQueue = onReportQueue,
                onAddShop = onAddShop,
                onSearchShops = onSearchShops,
                onClearSearchShops = onClearSearchShops,
                onSubmitReview = onSubmitReview
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { shareMarket(context, market) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("공유하기")
                }
                
                Button(
                    onClick = { openMap(context, market) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("길찾기")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Close Button
            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("닫기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MarketCalendarView(market: Market) {
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    
    // 이번 달 장날 목록 계산
    val marketDays = market.getMarketDaysInMonth(maxDay).toSet()
    
    // 달력 행렬 계산을 위해 이번 달 1일의 요일 계산
    val firstDayCal = Calendar.getInstance()
    firstDayCal.set(Calendar.DAY_OF_MONTH, 1)
    val startDayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK) - 1 // 0 (일) ~ 6 (토)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "${year}년 ${month}월 장날 예측 달력",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 요일 헤더
        val weekDays = listOf("일", "월", "화", "수", "목", "금", "토")
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEachIndexed { idx, day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (idx == 0) Color.Red.copy(alpha = 0.7f) else if (idx == 6) Color.Blue.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 날짜 렌더링
        var dayCounter = 1
        val rowsCount = (maxDay + startDayOfWeek + 6) / 7
        
        for (r in 0 until rowsCount) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                for (c in 0..6) {
                    val cellIdx = r * 7 + c
                    if (cellIdx < startDayOfWeek || dayCounter > maxDay) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val day = dayCounter
                        val isMarketDay = marketDays.contains(day)
                        val isToday = day == currentDay
                        val isPermanent = market.isPermanent()
                        val isOpened = isMarketDay || isPermanent
                        
                        val cellBg = when {
                            isToday && isOpened -> MaterialTheme.colorScheme.primary
                            isOpened -> MaterialTheme.colorScheme.primaryContainer
                            isToday -> MaterialTheme.colorScheme.surfaceVariant
                            else -> Color.Transparent
                        }
                        
                        val cellTextCol = when {
                            isToday && isOpened -> MaterialTheme.colorScheme.onPrimary
                            isOpened -> MaterialTheme.colorScheme.onPrimaryContainer
                            isToday -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cellBg)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isToday || isOpened) FontWeight.Bold else FontWeight.Normal,
                                color = cellTextCol,
                                textAlign = TextAlign.Center
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
fun AmenityCard(
    label: String,
    icon: String,
    hasAmenity: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (hasAmenity) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    val borderCol = if (hasAmenity) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val textCol = if (hasAmenity) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = BorderStroke(1.dp, borderCol)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = textCol
                )
                Text(
                    text = if (hasAmenity) "이용 가능" else "정보 없음",
                    style = MaterialTheme.typography.labelSmall,
                    color = textCol
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun openMap(context: Context, market: Market) {
    if (market.latitude == 0.0 || market.longitude == 0.0) {
        val address = market.addressRoad.ifEmpty { market.addressJibun }
        val webUri = "https://m.map.naver.com/search.naver?query=${Uri.encode(address)}".toUri()
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        } catch (_: Exception) {}
        return
    }

    // App schemes for routing
    val naverUri = "nmap://navigation?dlat=${market.latitude}&dlng=${market.longitude}&dname=${Uri.encode(market.marketName)}&appname=com.jangnal.gaja".toUri()
    val kakaoUri = "kakaomap://route?ep=${market.latitude},${market.longitude}&by=CAR".toUri()
    val tmapUri = "tmap://route?rGoName=${Uri.encode(market.marketName)}&rGoX=${market.longitude}&rGoY=${market.latitude}".toUri()
    val webFallbackUri = "https://map.kakao.com/link/to/${Uri.encode(market.marketName)},${market.latitude},${market.longitude}".toUri()

    // 1. Try Naver Map App
    try {
        val intent = Intent(Intent.ACTION_VIEW, naverUri)
        context.startActivity(intent)
        return
    } catch (_: Exception) {}

    // 2. Try Kakao Map App
    try {
        val intent = Intent(Intent.ACTION_VIEW, kakaoUri)
        context.startActivity(intent)
        return
    } catch (_: Exception) {}

    // 3. Try Tmap App
    try {
        val intent = Intent(Intent.ACTION_VIEW, tmapUri)
        context.startActivity(intent)
        return
    } catch (_: Exception) {}

    // 4. Fallback to KakaoMap Web routing
    try {
        val intent = Intent(Intent.ACTION_VIEW, webFallbackUri)
        context.startActivity(intent)
    } catch (_: Exception) {
        // Last-resort fallback to standard geo intent
        val geoUri = "geo:${market.latitude},${market.longitude}?q=${Uri.encode(market.marketName)}".toUri()
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
        } catch (_: Exception) {}
    }
}

private fun launchNavigationToParking(context: Context, market: Market) {
    val parkingQuery = "${market.marketName} 공영주차장"
    val naverUri = "nmap://search?query=${Uri.encode(parkingQuery)}&appname=com.jangnal.gaja".toUri()
    val kakaoUri = "kakaomap://search?q=${Uri.encode(parkingQuery)}".toUri()
    val tmapUri = "tmap://search?name=${Uri.encode(parkingQuery)}".toUri()
    val webFallbackUri = "https://map.kakao.com/link/search/${Uri.encode(parkingQuery)}".toUri()

    // 1. Try Naver App
    try {
        val intent = Intent(Intent.ACTION_VIEW, naverUri)
        context.startActivity(intent)
        return
    } catch (_: Exception) {}

    // 2. Try KakaoMap App
    try {
        val intent = Intent(Intent.ACTION_VIEW, kakaoUri)
        context.startActivity(intent)
        return
    } catch (_: Exception) {}

    // 3. Try Tmap App
    try {
        val intent = Intent(Intent.ACTION_VIEW, tmapUri)
        context.startActivity(intent)
        return
    } catch (_: Exception) {}

    // 4. Fallback to KakaoMap Web
    try {
        val intent = Intent(Intent.ACTION_VIEW, webFallbackUri)
        context.startActivity(intent)
    } catch (_: Exception) {
        val geoUri = "geo:${market.latitude},${market.longitude}?q=${Uri.encode(parkingQuery)}".toUri()
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
        } catch (_: Exception) {}
    }
}

private fun shareMarket(context: Context, market: Market) {
    val shareText = buildString {
        appendLine("[장날가자] ${market.marketName}")
        appendLine()
        if (market.isOpenToday()) {
            appendLine("🎉 오늘 장 열리는 날!")
        }
        appendLine("📍 주소: ${market.addressRoad.ifEmpty { market.addressJibun }}")
        appendLine("📅 일정: ${if(market.isPermanent()) "매일 운영" else market.openingCycle}")
        appendLine("🍎 주요 품목: ${market.getCleanSpecialty()}")
        appendLine()
        appendLine("더 자세한 정보는 '장날가자' 앱에서 확인하세요!")
    }
    
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    
    val chooser = Intent.createChooser(intent, "시장 정보 공유하기")
    try {
        context.startActivity(chooser)
    } catch (_: Exception) {
        // Ignore
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShopQueueSection(
    market: Market,
    shops: List<Shop>,
    searchResults: List<Shop>,
    reviews: Map<String, List<com.jangnal.gaja.ui.viewmodel.ShopReview>> = emptyMap(),
    userLocation: android.location.Location?,
    onReportQueue: (Long, Int) -> Unit,
    onAddShop: (String, String) -> Unit,
    onSearchShops: (String) -> Unit,
    onClearSearchShops: () -> Unit,
    onSubmitReview: (String, Float, String, Uri?) -> Unit = { _, _, _, _ -> }
) {
    var showAddShopDialog by remember { mutableStateOf(false) }
    var activeVotingShop by remember { mutableStateOf<Shop?>(null) }
    
    // Calculate distance
    val distance = remember(userLocation, market) {
        if (userLocation != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                market.latitude, market.longitude,
                results
            )
            results[0]
        } else {
            Float.MAX_VALUE
        }
    }
    val isNear = distance <= 100f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥 인기 상점 실시간 대기줄",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            
            TextButton(
                onClick = { showAddShopDialog = true }
            ) {
                Text("+ 상점 추가", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        if (shops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "등록된 상점이 없습니다. 직접 상점을 제보해 보세요!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            var activeReviewShop by remember { mutableStateOf<Shop?>(null) }
            
            shops.forEach { shop ->
                val hasRecentReport = shop.lastReportTime > 0 && 
                        (System.currentTimeMillis() - shop.lastReportTime) < 40 * 60 * 1000 // 40 minutes

                val shopReviews = reviews[shop.shopName] ?: emptyList()
                val avgRating = shopReviews.map { it.rating }.average()
                val avgStr = String.format(Locale.US, "%.1f", if (avgRating.isNaN()) 0.0 else avgRating)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = shop.shopName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "[${shop.category}]",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    val hasOnnuri = shopReviews.any { it.content.contains("온누리") }
                                    val hasCard = shopReviews.any { it.content.contains("카드") }
                                    if (hasOnnuri) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "🎫온누리",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1565C0),
                                            modifier = Modifier
                                                .background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    if (hasCard) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "💳카드",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier
                                                .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                
                                val currentCal = Calendar.getInstance()
                                val currentHour = currentCal.get(Calendar.HOUR_OF_DAY)
                                val isPeakHour = (currentHour in 11..13) || (currentHour in 17..19)
                                val isMarketOpenToday = market.isPermanent() || market.isOpenOn(System.currentTimeMillis())

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val statusText = if (hasRecentReport) {
                                        when (shop.queueStatus) {
                                            0 -> if (isPeakHour) "한산함 🟢 (AI 피크: 5~10분)" else "한산함 🟢 (AI 예상: 즉시 입장)"
                                            1 -> if (isPeakHour) "보통 🟡 (AI 피크: 20~30분)" else "보통 🟡 (AI 예상: 10~15분)"
                                            2 -> if (isPeakHour) "혼잡함 🔴 (AI 피크: 40분 이상)" else "혼잡함 🔴 (AI 예상: 25~35분)"
                                            else -> "제보 없음 ⚪ (AI 평시 분석)"
                                        }
                                    } else {
                                        if (isMarketOpenToday && isPeakHour) "제보 없음 ⚪ (AI 장날 피크: 15~25분 예상)"
                                        else "제보 없음 ⚪ (AI 평시: 5~10분 예상)"
                                    }
                                    val statusColor = if (hasRecentReport) {
                                        when (shop.queueStatus) {
                                            0 -> Color(0xFF2E7D32)
                                            1 -> Color(0xFFE65100)
                                            2 -> Color(0xFFC62828)
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                    
                                    Text(
                                        text = statusText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                    
                                    if (hasRecentReport) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val mins = ((System.currentTimeMillis() - shop.lastReportTime) / 60000).toInt()
                                        Text(
                                            text = "${mins}분 전",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        if (shop.isVerifiedReport) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "현장인증됨",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32),
                                                modifier = Modifier
                                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                if (shopReviews.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = "⭐ $avgStr (${shopReviews.size}개 한줄평)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { activeVotingShop = shop },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("제보", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { activeReviewShop = shop },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("한줄평 💬", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        val photos = shopReviews.map { it.photoUrl }.filter { it.isNotEmpty() }
                        if (photos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                photos.forEach { url ->
                                    var showFullscreenPhoto by remember { mutableStateOf(false) }
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { showFullscreenPhoto = true }
                                    ) {
                                        androidx.compose.foundation.Image(
                                            painter = coil.compose.rememberAsyncImagePainter(url),
                                            contentDescription = "리뷰 사진",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                    
                                    if (showFullscreenPhoto) {
                                        AlertDialog(
                                            onDismissRequest = { showFullscreenPhoto = false },
                                            title = { Text("${shop.shopName} 사진 보기", fontWeight = FontWeight.Bold) },
                                            text = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(280.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    androidx.compose.foundation.Image(
                                                        painter = coil.compose.rememberAsyncImagePainter(url),
                                                        contentDescription = "리뷰 사진 크게 보기",
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                                    )
                                                }
                                            },
                                            confirmButton = {
                                                TextButton(onClick = { showFullscreenPhoto = false }) {
                                                    Text("닫기")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        val textReviews = shopReviews.filter { it.content.isNotEmpty() }.take(2)
                        if (textReviews.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                textReviews.forEach { rev ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "⭐ ${rev.rating.toInt()}점 - ${rev.reporter}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                val dateStr = SimpleDateFormat("M/d HH:mm", Locale.KOREA).format(Date(rev.timestamp))
                                                Text(
                                                    text = dateStr,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                )
                                            }
                                            Text(
                                                text = rev.content,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (activeReviewShop != null) {
                val context = LocalContext.current
                val targetShop = activeReviewShop!!
                var ratingVal by remember { mutableStateOf(5f) }
                var textContent by remember { mutableStateOf("") }
                var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                var selectedPaymentTags by remember { mutableStateOf(setOf<String>()) }
                val paymentOptions = listOf("온누리", "카드", "간편결제", "현금")
                
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    selectedImageUri = uri
                }

                AlertDialog(
                    onDismissRequest = { activeReviewShop = null },
                    title = { Text("${targetShop.shopName} 한줄평 제보", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("맛과 위생, 서비스 등에 대한 솔직한 평가를 평점과 사진을 남겨주세요!")
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..5).forEach { star ->
                                    val isSelected = star <= ratingVal
                                    IconButton(
                                        onClick = { ratingVal = star.toFloat() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "$star 점",
                                            tint = if (isSelected) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Text(" ${ratingVal.toInt()}점", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("결제 수단 태그 (선택)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    paymentOptions.forEach { opt ->
                                        val isChecked = selectedPaymentTags.contains(opt)
                                        val icon = when(opt) {
                                            "온누리" -> "🎫"
                                            "카드" -> "💳"
                                            "간편결제" -> "📱"
                                            else -> "💵"
                                        }
                                        FilterChip(
                                            selected = isChecked,
                                            onClick = {
                                                selectedPaymentTags = if (isChecked) selectedPaymentTags - opt else selectedPaymentTags + opt
                                            },
                                            label = { Text("$icon $opt", fontSize = 11.sp) },
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                    }
                                }
                            }
                            
                            OutlinedTextField(
                                value = textContent,
                                onValueChange = { 
                                    if (it.length <= 150) textContent = it 
                                },
                                placeholder = { Text("호떡 피가 엄청 쫄깃하고 맛있어요! 추천합니다.") },
                                supportingText = {
                                    Text(
                                        text = "${textContent.length}/150자",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("🖼 사진 첨부")
                                }

                                if (selectedImageUri != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                                    ) {
                                        androidx.compose.foundation.Image(
                                            painter = coil.compose.rememberAsyncImagePainter(selectedImageUri),
                                            contentDescription = "첨부 프리뷰",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                } else {
                                    Text("첨부 없음", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        val canSubmit = textContent.trim().isNotEmpty() || selectedImageUri != null || selectedPaymentTags.isNotEmpty()
                        Button(
                            onClick = {
                                val tagPrefix = if (selectedPaymentTags.isNotEmpty()) {
                                    "[" + selectedPaymentTags.joinToString("/") + "] "
                                } else ""
                                val finalContent = (tagPrefix + textContent).trim()
                                onSubmitReview(targetShop.shopName, ratingVal, finalContent, selectedImageUri)
                                activeReviewShop = null
                            },
                            enabled = canSubmit,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("제보 등록 🟢")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { activeReviewShop = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("취소")
                        }
                    }
                )
            }
        }
    }

    // --- Dialogs ---
    if (showAddShopDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

        AlertDialog(
            onDismissRequest = { 
                showAddShopDialog = false 
                onClearSearchShops()
            },
            title = { Text("주변 상점/맛집 검색 등록", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "📍 시장 반경 500m 이내 실제 상점을 카카오 지도에서 검색합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("예: 닭강정, 호떡, 분식") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Button(
                            onClick = { 
                                focusManager.clearFocus()
                                onSearchShops(searchQuery)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("검색")
                        }
                    }
                    
                    Divider()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        if (searchResults.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isBlank()) "상점명이나 메뉴를 검색해 보세요." else "검색 결과가 없습니다.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (searchQuery.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            val customName = searchQuery.trim().take(30)
                                            if (customName.isNotEmpty()) {
                                                onAddShop(customName, "노점/먹거리")
                                                showAddShopDialog = false
                                                onClearSearchShops()
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("'+ $searchQuery' 직접 등록하기 ➕", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            androidx.compose.foundation.lazy.LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(searchResults.size) { idx ->
                                    val shop = searchResults[idx]
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onAddShop(shop.shopName, shop.category)
                                                showAddShopDialog = false
                                                onClearSearchShops()
                                            },
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = shop.shopName,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "[${shop.category}]",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = "추가 ➕",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddShopDialog = false 
                        onClearSearchShops()
                    }
                ) {
                    Text("닫기")
                }
            }
        )
    }

    if (activeVotingShop != null) {
        val shop = activeVotingShop!!
        AlertDialog(
            onDismissRequest = { activeVotingShop = null },
            title = { Text("${shop.shopName} 대기줄 제보", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isNear) {
                        Text(
                            text = "🟢 시장 내부 현장 제보 (현장인증 적용됨)",
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        val distanceText = if (distance == Float.MAX_VALUE) "알 수 없음" else "${distance.toInt()}m"
                        Text(
                            text = "⚠️ 시장 외부 제보 (현장인증 불가: 약 $distanceText 이격)",
                            color = Color(0xFFE65100),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("현재 맛집의 대기줄/대기 강도는 어떤가요?")
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onReportQueue(shop.id, 0)
                            activeVotingShop = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("한산함 (대기 적음) 🟢")
                    }
                    Button(
                        onClick = {
                            onReportQueue(shop.id, 1)
                            activeVotingShop = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("보통 (대기 10~25분) 🟡")
                    }
                    Button(
                        onClick = {
                            onReportQueue(shop.id, 2)
                            activeVotingShop = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("혼잡함 (대기 30분 이상) 🔴")
                    }
                    OutlinedButton(
                        onClick = { activeVotingShop = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("취소")
                    }
                }
            }
        )
    }
}
