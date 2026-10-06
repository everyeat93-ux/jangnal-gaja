package com.jangnal.gaja.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.jangnal.gaja.data.local.entity.Market
import com.jangnal.gaja.data.local.entity.MarketStamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * 🎖️ 나의 전국 5일장 도장여권 (Market Stamp Passport) 바텀시트
 * 전국 전통시장 GPS 방문 스탬프, 황금도장 수집, 지역별 도장깨기 및 레벨 시스템 UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassportSheet(
    stamps: List<MarketStamp>,
    allMarkets: List<Market>,
    onSelectMarket: (Market) -> Unit = {},
    onDeleteStamp: (Long) -> Unit = {},
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var selectedProvince by remember { mutableStateOf("전체") }
    var viewingStamp by remember { mutableStateOf<MarketStamp?>(null) }
    var stampToDelete by remember { mutableStateOf<MarketStamp?>(null) }

    // 고유 방문 시장 수 & 황금 스탬프 수
    val uniqueVisitedMarketIds = remember(stamps) { stamps.map { it.marketId }.toSet() }
    val uniqueCount = uniqueVisitedMarketIds.size
    val goldenCount = remember(stamps) { stamps.count { it.isMarketDay } }

    // 방문한 시도 권역 수
    val visitedProvinces = remember(stamps) { stamps.map { it.province }.filter { it.isNotEmpty() && it != "전국" }.toSet() }
    val visitedProvincesCount = visitedProvinces.size

    // 탐험가 레벨 계산
    val (userLevelTitle, userLevelBadge, levelColor) = remember(uniqueCount, goldenCount) {
        when {
            uniqueCount >= 20 -> Triple("Lv.5 전국 5일장 명예 마스터", "💎", Color(0xFF00838F))
            uniqueCount >= 10 -> Triple("Lv.4 팔도 대동여지도 마스터", "👑", Color(0xFFE65100))
            uniqueCount >= 5 -> Triple("Lv.3 전통 장날 유랑단", "🥇", Color(0xFFD84315))
            uniqueCount >= 3 -> Triple("Lv.2 골목 핫플 탐험가", "🥈", Color(0xFF1565C0))
            uniqueCount >= 1 -> Triple("Lv.1 풋풋한 장돌뱅이", "🥉", Color(0xFF5D4037))
            else -> Triple("Lv.0 예비 장날 여행자", "🌱", Color(0xFF689F38))
        }
    }

    val provincesList = listOf(
        "전체", "서울", "경기", "인천", "강원", "충북", "충남·대전", "전북", "전남·광주", "경북·대구", "경남·부산·울산", "제주"
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFFFAF7F0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
        ) {
            // Header: 닫기 버튼 및 타이틀
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎖️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "나의 전국 5일장 도장여권",
                            color = Color(0xFF3E2723),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "TRADITIONAL MARKET STAMP PASSPORT",
                            color = Color(0xFFD84315),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = Color(0xFF5D4037)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. 도장여권 커버 & 유저 레벨 카드 (따뜻한 한지 / 황금 도장 테마)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF9E6),
                shadowElevation = 2.dp,
                border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFE5A93C), Color(0xFFFFCA28))))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFECC8),
                                border = BorderStroke(1.dp, Color(0xFFE5A93C)),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(userLevelBadge, fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = userLevelTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = levelColor
                                )
                                Text(
                                    text = "발급: 대한민국 전통 5일장 연합",
                                    fontSize = 11.sp,
                                    color = Color(0xFF8D6E63)
                                )
                            }
                        }

                        // SNS 공유 버튼
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF3E0),
                            border = BorderStroke(1.dp, Color(0xFFFFCC80))
                        ) {
                            IconButton(
                                onClick = {
                                    val shareText = buildString {
                                        appendLine("🎖️ [장날가자] 나의 전국 5일장 도장여권 기록!")
                                        appendLine("🏆 $userLevelTitle 달성")
                                        appendLine("📍 총 방문 시장: ${uniqueCount}곳")
                                        appendLine("✨ 황금 장날 스탬프: ${goldenCount}개")
                                        appendLine("🗺️ 정복 권역: ${visitedProvincesCount}개 시도")
                                        appendLine()
                                        appendLine("전국의 정겨운 5일장과 맛있는 먹거리를 탐험해보세요! #장날가자 #5일장 #전통시장 #스탬프투어")
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    try {
                                        context.startActivity(Intent.createChooser(intent, "나의 장날 도장여권 공유"))
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "여권 공유",
                                    tint = Color(0xFFD84315),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color(0xFFFFE0B2), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 통계 3열
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PassportStatItem(title = "정복 시장", value = "${uniqueCount}곳", sub = "전국 5일장", valueColor = Color(0xFF2E7D32))
                        PassportStatItem(title = "황금 도장", value = "${goldenCount}개", sub = "장날 당일 방문", highlight = true, valueColor = Color(0xFFD84315))
                        PassportStatItem(title = "정복 권역", value = "${visitedProvincesCount}곳", sub = "팔도 유람", valueColor = Color(0xFF1565C0))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. 시도 권역 필터 칩
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                provincesList.forEach { prov ->
                    val isSelected = selectedProvince == prov
                    val provCount = if (prov == "전체") stamps.size else stamps.count { it.province.contains(prov) || prov.contains(it.province) }
                    
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedProvince = prov },
                        label = {
                            Text(
                                text = if (provCount > 0) "$prov ($provCount)" else prov,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF5D4037)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE65100),
                            containerColor = Color(0xFFFFF3E0)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) Color(0xFFE65100) else Color(0xFFFFCC80),
                            selectedBorderColor = Color(0xFFE65100)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. 스탬프북 그리드
            val filteredStamps = remember(stamps, selectedProvince) {
                if (selectedProvince == "전체") stamps
                else stamps.filter { it.province.contains(selectedProvince) || selectedProvince.contains(it.province) }
            }

            val targetMarkets = remember(allMarkets, selectedProvince) {
                if (selectedProvince == "전체") allMarkets.take(30)
                else allMarkets.filter { it.getProvince().contains(selectedProvince) || selectedProvince.contains(it.getProvince()) }.take(30)
            }

            if (filteredStamps.isEmpty() && targetMarkets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "해당 지역에 등록된 시장이 없습니다.",
                        color = Color(0xFF8D6E63),
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // 1) 방문 완료 도장 목록
                    items(filteredStamps, key = { "stamp_${it.id}" }) { stamp ->
                        StampedItemCard(
                            stamp = stamp,
                            onClick = { viewingStamp = stamp }
                        )
                    }

                    // 2) 미방문 시장 목록 (도장깨기 유도)
                    val unvisitedMarkets = targetMarkets.filter { !uniqueVisitedMarketIds.contains(it.id) }
                    items(unvisitedMarkets, key = { "market_${it.id}" }) { market ->
                        UnstampedItemCard(
                            market = market,
                            onClick = {
                                onDismissRequest()
                                onSelectMarket(market)
                            }
                        )
                    }
                }
            }
        }
    }

    // 도장 상세 팝업 (사진, 메모, 날짜, 삭제)
    if (viewingStamp != null) {
        val currentStamp = viewingStamp!!
        val dateStr = SimpleDateFormat("yyyy년 M월 d일 (E) HH:mm", Locale.KOREA).format(Date(currentStamp.visitTimestamp))
        
        AlertDialog(
            onDismissRequest = { viewingStamp = null },
            containerColor = Color(0xFFFFFDF9),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (currentStamp.isMarketDay) "✨" else "💮", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentStamp.marketName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF3E2723)
                        )
                    }

                    IconButton(
                        onClick = {
                            stampToDelete = currentStamp
                            viewingStamp = null
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "도장 삭제",
                            tint = Color(0xFFE57373)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (currentStamp.isMarketDay) Color(0xFFFFF8E1) else Color(0xFFFFEBEE),
                        border = BorderStroke(1.dp, if (currentStamp.isMarketDay) Color(0xFFFFB300) else Color(0xFFEF9A9A))
                    ) {
                        Text(
                            text = if (currentStamp.isMarketDay) "🌟 정기 장날 당일 방문 (황금 스탬프 획득)" else "🏪 상설/시장 현장 방문 완료",
                            color = if (currentStamp.isMarketDay) Color(0xFFD84315) else Color(0xFFC62828),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "📅 방문일시: $dateStr",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        text = "🗺️ 권역: ${currentStamp.province}",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037)
                    )

                    if (!currentStamp.photoUri.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEEEEE))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(currentStamp.photoUri),
                                contentDescription = "현장 인증 사진",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    if (currentStamp.userMemo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "📝 나의 여행 메모",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF5EFE6),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentStamp.userMemo,
                                fontSize = 13.sp,
                                color = Color(0xFF4E342E),
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingStamp = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 도장 삭제 확인 다이얼로그
    if (stampToDelete != null) {
        AlertDialog(
            onDismissRequest = { stampToDelete = null },
            title = { Text("스탬프 삭제", fontWeight = FontWeight.Bold) },
            text = { Text("'${stampToDelete!!.marketName}' 방문 스탬프 기록을 여권에서 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = stampToDelete!!.id
                        onDeleteStamp(id)
                        stampToDelete = null
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { stampToDelete = null }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
private fun PassportStatItem(
    title: String,
    value: String,
    sub: String,
    highlight: Boolean = false,
    valueColor: Color = Color(0xFF3E2723)
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 11.sp,
            color = Color(0xFF8D6E63)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = valueColor
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = sub,
            fontSize = 10.sp,
            color = Color(0xFF8D6E63)
        )
    }
}

/**
 * 💮 전통 인주 도장 날인 카드 UI (밝고 정겨운 장날 테마)
 */
@Composable
private fun StampedItemCard(
    stamp: MarketStamp,
    onClick: () -> Unit
) {
    val isGolden = stamp.isMarketDay
    val sealColor = if (isGolden) Color(0xFFD4AF37) else Color(0xFFD32F2F)
    val sealBgColor = if (isGolden) Color(0xFFFFF8E1) else Color(0xFFFFEBEE)
    val dateStr = SimpleDateFormat("yy.MM.dd", Locale.KOREA).format(Date(stamp.visitTimestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(
            1.2.dp,
            if (isGolden) Color(0xFFFFB300) else Color(0xFFEF9A9A)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 뱃지
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isGolden) Color(0xFFFFF8E1) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = if (isGolden) "✨ 황금장날" else "📍 현장인증",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = sealColor,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = Color(0xFF8D6E63),
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 💮 전통 인주 낙관 도장 심볼
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(sealBgColor)
                    .border(2.dp, sealColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val cleanName = stamp.marketName.replace("전통시장", "").replace("시장", "").take(3)
                    Text(
                        text = cleanName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = sealColor,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (isGolden) "장날 認" else "방문 認",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = sealColor.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stamp.marketName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (stamp.userMemo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "💬 \"${stamp.userMemo}\"",
                    fontSize = 11.sp,
                    color = Color(0xFF757575),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 🔒 미방문 잠금 시장 카드 (도장깨기 유도)
 */
@Composable
private fun UnstampedItemCard(
    market: Market,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFCFBF9),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFEFE8DD))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = market.getSimpleTypeText(),
                    fontSize = 9.sp,
                    color = Color(0xFF8D6E63),
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = "도장깨기 도전 ▾",
                    fontSize = 9.sp,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5EFE6))
                    .border(1.dp, Color(0xFFE0D8CC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🔒", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = market.getDisplayName(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "다음: ${market.getNextMarketText()}",
                fontSize = 10.sp,
                color = Color(0xFFE65100)
            )
        }
    }
}
