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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import com.jangnal.gaja.data.local.entity.Market
import com.jangnal.gaja.data.local.entity.Shop
import com.jangnal.gaja.util.VoteTracker
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.text.style.TextOverflow
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
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.jangnal.gaja.data.local.entity.Festival
import com.jangnal.gaja.data.local.entity.CommunityPost
import com.jangnal.gaja.data.local.entity.CommunityComment
import com.jangnal.gaja.util.CommunitySafetyHelper

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun MarketDetailSheet(
    market: Market,
    shops: List<Shop> = emptyList(),
    searchResults: List<Shop> = emptyList(),
    festivals: List<Festival> = emptyList(),
    communityPosts: List<CommunityPost> = emptyList(),
    comments: Map<String, List<CommunityComment>> = emptyMap(),
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
    onConfirmOnnuri: (Long) -> Unit = {},
    onDeleteShop: (String) -> Unit = {},
    onReportShopIssue: (String, String, String) -> Unit = { _, _, _ -> },
    onSubmitCommunityPost: (String, String, String, Uri?) -> Unit = { _, _, _, _ -> },
    onLikeCommunityPost: (String) -> Unit = {},
    onReportCommunityPost: (String, String, String) -> Unit = { _, _, _ -> },
    onBlockCommunityAuthor: (String) -> Unit = {},
    onSubmitCommunityComment: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteCommunityComment: (String) -> Unit = {},
    onReportCommunityComment: (String, String, String, String) -> Unit = { _, _, _, _ -> },
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
                .fillMaxHeight(0.92f)
        ) {
            // P2: 스크롤 가능한 본문 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header - 시장 이름 및 즐겨찾기 (P3: 장날 알림 즉각 피드백)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = market.getDisplayName(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    androidx.compose.material3.IconButton(
                        onClick = { 
                            val willBeFavorite = !market.isFavorite
                            onFavoriteToggle(market)
                            if (willBeFavorite) {
                                val alertMsg = if (market.isPermanent()) {
                                    "❤️ '${market.getDisplayName()}' 단골 시장으로 등록되었습니다!"
                                } else {
                                    "❤️ '${market.getDisplayName()}' 단골 시장으로 등록되었습니다!\n🔔 다음 장날(${market.getNextMarketText()}) 아침에 알림을 전해드립니다."
                                }
                                android.widget.Toast.makeText(context, alertMsg, android.widget.Toast.LENGTH_LONG).show()
                            } else {
                                android.widget.Toast.makeText(context, "즐겨찾기에서 해제되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
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
            
                // 💡 상단 사용자 안내 팁 배너
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "상점의 별점과 결제수단을 확인하고, 한줄평과 실시간 대기줄을 제보해 보세요!",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 시장 유형 뱃지 (FlowRow로 좁은 화면에서도 줄바꿈 자연스럽게 처리)
                val onnuriCount = shops.count { it.isOnnuri }
                val onnuriBadgeText = when {
                    shops.isNotEmpty() && onnuriCount > 0 -> "💳 온누리 가맹 ${onnuriCount}곳 (${onnuriCount * 100 / shops.size}%)"
                    shops.isNotEmpty() && onnuriCount == 0 -> "💳 온누리 가맹 확인중"
                    market.isPermanent() -> "💳 온누리 10% 가맹 상설시장"
                    else -> "💳 온누리상품권 10% 가맹"
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Badge(
                        text = market.getSimpleTypeText(),
                        bgColor = MaterialTheme.colorScheme.primaryContainer,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (market.isOpenToday()) {
                        Badge(
                            text = "🟢 오늘 개장",
                            bgColor = Color(0xFFE8F5E9),
                            textColor = Color(0xFF2E7D32)
                        )
                    } else if (!market.isPermanent()) {
                        Badge(
                            text = "📅 다음: ${market.getNextMarketText()}",
                            bgColor = Color(0xFFFFF3E0),
                            textColor = Color(0xFFE65100)
                        )
                    }
                    Badge(
                        text = onnuriBadgeText,
                        bgColor = Color(0xFFE0F2F1),
                        textColor = Color(0xFF00695C)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                val detailTabs = remember(festivals.size, shops.size, communityPosts.size) {
                    listOfNotNull(
                        "shops" to "🏪 상점·맛집 (${shops.size})",
                        if (festivals.isNotEmpty()) "festivals" to "🎪 축제·공연 (${festivals.size})" else null,
                        "community" to "💬 동네마당 (${communityPosts.size})",
                        "info" to "ℹ️ 시장정보·장날"
                    )
                }
                var selectedTabIndex by remember { mutableIntStateOf(0) }
                val currentTabKey = detailTabs.getOrNull(selectedTabIndex)?.first ?: "shops"

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex.coerceIn(0, detailTabs.size - 1),
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = { Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)) }
                ) {
                    detailTabs.forEachIndexed { index, (_, title) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (currentTabKey) {
                    "shops" -> {
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
                            onSubmitReview = onSubmitReview,
                            onConfirmOnnuri = onConfirmOnnuri,
                            onDeleteShop = onDeleteShop,
                            onReportShopIssue = onReportShopIssue
                        )
                    }
                    "festivals" -> {
                        MarketFestivalSection(
                            market = market,
                            festivals = festivals
                        )
                    }
                    "community" -> {
                        MarketCommunitySection(
                            market = market,
                            posts = communityPosts,
                            comments = comments,
                            userLocation = userLocation,
                            onSubmitPost = onSubmitCommunityPost,
                            onLikePost = onLikeCommunityPost,
                            onReportPost = onReportCommunityPost,
                            onBlockAuthor = onBlockCommunityAuthor,
                            onSubmitComment = onSubmitCommunityComment,
                            onDeleteComment = onDeleteCommunityComment,
                            onReportComment = onReportCommunityComment
                        )
                    }
                    "info" -> {
                        // 3. [P1 & P5] 🛒 장날 LIVE DROP 고전환 커머스 카드 (구체적 음식명 + 고화질 사진 1장 + 명확한 택배 주문 CTA)
                        val dropItem = remember(market.marketName, displaySpecialty) {
                    val name = market.marketName
                    when {
                        name.contains("구로") -> LiveDropItem(
                            headline = "🔥 줄 서는 [구로시장 칠공주 떡볶이 & 수제 모듬전]",
                            badge = "한정 40세트",
                            subtext = "40년 전통 즉석 떡볶이와 바삭한 모듬전 풀세트 · 당일 신선 포장",
                            image = "https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("신림") || name.contains("관악") -> LiveDropItem(
                            headline = "🔥 오늘 30분 대기 [신림 원조 백순대·곱창볶음 3분 밀키트]",
                            badge = "한정 50세트",
                            subtext = "30년 전통 비법 양념장과 들깨가루·깻잎 포함 · 집에서 3분 완성",
                            image = "https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("속초") -> LiveDropItem(
                            headline = "🔥 현장 1시간 대기 [속초 수제 조청 닭강정 본점]",
                            badge = "한정 100상자",
                            subtext = "가마솥 조청으로 갓 튀겨 당일 발송 · 식어도 바삭한 원조 닭강정",
                            image = "https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("정선") -> LiveDropItem(
                            headline = "🔥 2·7일 정선 장날 [햇생곤드레(1kg) & 저온압착 들기름]",
                            badge = "장날 당일 채취",
                            subtext = "해발 700m 정선 새벽 수확 생곤드레와 시골 방앗간 햇들기름 세트",
                            image = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("광장") || name.contains("종로") -> LiveDropItem(
                            headline = "🔥 광장시장 줄 서는 [원조 맷돌 빈대떡 & 마약김밥 세트]",
                            badge = "한정 50세트",
                            subtext = "100% 녹두 맷돌 반죽 3장 + 톡 쏘는 겨자소스 마약김밥 2팩",
                            image = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("서문") || name.contains("대구") -> LiveDropItem(
                            headline = "🔥 서문시장 명물 [50년 원조 납작만두(30개) & 옛날손국수]",
                            badge = "한정 60세트",
                            subtext = "50년 전통 얇은 피 만두 30개 + 진한 남해 멸치육수 손국수",
                            image = "https://images.unsplash.com/photo-1541544741938-0af808871cc0?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("망원") || name.contains("마포") -> LiveDropItem(
                            headline = "🔥 망원시장 핫플 [수제 닭강정 & 훈훈 찹쌀호떡 밀키트]",
                            badge = "한정 50세트",
                            subtext = "망리단길 줄 서는 명물 양념 닭강정과 쫄깃한 호떡 반죽",
                            image = "https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("통인") -> LiveDropItem(
                            headline = "🔥 통인시장 명물 [원조 기름떡볶이(매콤/간장) 밀키트]",
                            badge = "한정 40세트",
                            subtext = "무쇠 솥뚜껑에 볶아내는 겉바속촉 60년 전통 원조 기름떡볶이",
                            image = "https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("부산") || name.contains("자갈치") || name.contains("부평") || name.contains("깡통") || name.contains("국제") -> LiveDropItem(
                            headline = "🔥 부산 깡통시장 [원조 씨앗호떡 & 비빔당면 밀키트]",
                            badge = "한정 50세트",
                            subtext = "부산 대표 먹거리! 매콤새콤 비빔당면과 고소한 씨앗호떡 풀세트",
                            image = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=400&q=80"
                        )
                        name.contains("강릉") -> LiveDropItem(
                            headline = "🔥 강릉 중앙시장 [수제 배니 닭강정 & 오징어순대 밀키트]",
                            badge = "한정 50세트",
                            subtext = "동해안 속 꽉 찬 오징어순대와 50년 비법 닭강정 당일 발송",
                            image = "https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?auto=format&fit=crop&w=400&q=80"
                        )
                        else -> {
                            val rawWords = displaySpecialty.split(",", " ", "/").map { it.trim() }
                            val cleanWords = rawWords.filter { w -> 
                                w.isNotEmpty() && 
                                !w.contains("가공식품") && 
                                !w.contains("농산물") && 
                                !w.contains("수산물") && 
                                !w.contains("축산물") && 
                                !w.contains("잡화") && 
                                !w.contains("의류") && 
                                !w.contains("공산품") && 
                                !w.contains("식료품") && 
                                !w.contains("근린") && 
                                !w.contains("기타") &&
                                !w.contains("특산물")
                            }
                            val cleanSpecialty = cleanWords.firstOrNull() ?: "30년 손맛 대표 먹거리"
                            LiveDropItem(
                                headline = "🔥 오늘 줄 서는 [${market.marketName} $cleanSpecialty 밀키트]",
                                badge = "장날 한정 드롭",
                                subtext = "30년 전통 비법 양념과 신선 재료 밀키트 · 특수 냉매 안심 포장",
                                image = "https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=400&q=80"
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                    color = Color(0xFFFFF8F0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE65100)
                                ) {
                                    Text(
                                        text = "장날 LIVE DROP",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text(
                                        text = "온누리 10%↓",
                                        color = Color(0xFF2E7D32),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFEBEE)
                            ) {
                                Text(
                                    text = dropItem.badge,
                                    color = Color(0xFFC62828),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = dropItem.image,
                                contentDescription = "음식 썸네일",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFFE0B2))
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dropItem.headline,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E1B18),
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = dropItem.subtext,
                                    fontSize = 11.sp,
                                    color = Color(0xFF616161),
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val webUrl = "https://jangnal-gaja.vercel.app/?market=" + Uri.encode(market.marketName)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    val cleanSpecialty = displaySpecialty.split(",").firstOrNull()?.trim() ?: ""
                                    val cleanMarketName = market.marketName.replace("전통시장", "").replace("시장", "").trim()
                                    val query = if (cleanSpecialty.isNotEmpty()) "$cleanMarketName $cleanSpecialty" else market.marketName
                                    val fallbackUrl = "https://search.shopping.naver.com/search/all?query=" + Uri.encode(query)
                                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))) } catch (_: Exception) {}
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE65100),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                        ) {
                            Text(
                                text = "⚡ 장날 갓 만든 밀키트 택배 주문 (온누리 10% 할인)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                Spacer(modifier = Modifier.height(20.dp))

                // 4. [P3] 📅 개장 일정 & 스마트 캘린더 (상설시장은 슬림 요약 칩 + 아코디언)
                Text(
                    text = "📅 개장 정보 및 일정",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (market.isPermanent()) {
                    var showFullCalendar by remember { mutableStateOf(false) }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏪", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("상설 시장 (연중무휴 매일 개장 🟢)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF2E7D32))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("🕒 권장 방문 시간: 09:00 ~ 21:00 (점포별 상이)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("📅 정기 휴무: 점포별 자율 휴무 (일요일/공휴일 정상 영업 점포 다수)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            if (showFullCalendar) {
                                Spacer(modifier = Modifier.height(12.dp))
                                MarketCalendarView(market = market)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showFullCalendar = !showFullCalendar },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(if (showFullCalendar) "달력 접기 ▴" else "월간 달력 보기 ▾", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    MarketCalendarView(market = market)
                    Spacer(modifier = Modifier.height(12.dp))
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
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                Spacer(modifier = Modifier.height(20.dp))

                // 5. 💬 실시간 제보 및 투표
                Text(
                    text = "💬 실시간 장날 제보",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 현장에 계신가요? 오늘 시장이 열렸는지 이웃들에게 실시간으로 알려주세요!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                val todayVoteDate = remember {
                    val cal = Calendar.getInstance()
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH) + 1
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
                }
                val isVoteToday = market.lastVoteDate == todayVoteDate
                val displayOpenVotes = if (isVoteToday) market.voteOpenTodayCount else 0
                val displayClosedVotes = if (isVoteToday) market.voteClosedTodayCount else 0

                var localVotedType by remember(market.id) { 
                    mutableStateOf(VoteTracker.getMarketVoteStatus(context, market.id)) 
                }
                var optimisticOpenDelta by remember(market.id) { mutableIntStateOf(0) }
                var optimisticClosedDelta by remember(market.id) { mutableIntStateOf(0) }

                val totalOpenVotes = displayOpenVotes + optimisticOpenDelta
                val totalClosedVotes = displayClosedVotes + optimisticClosedDelta

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isOpenVoted = localVotedType == true
                    Button(
                        onClick = {
                            if (localVotedType != null) {
                                android.widget.Toast.makeText(context, "오늘 이미 시장 현장 제보를 완료하셨습니다 😊", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                localVotedType = true
                                optimisticOpenDelta = 1
                                VoteTracker.setMarketVoteStatus(context, market.id, true)
                                onVoteClick(market.id, true)
                                android.widget.Toast.makeText(context, "✅ 소중한 제보 감사합니다! '오늘 열렸어요'가 즉시 반영되었습니다 👏", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        colors = if (isOpenVoted) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                        border = BorderStroke(
                            if (isOpenVoted) 2.dp else 1.5.dp,
                            if (isOpenVoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isOpenVoted) "✓ 오늘 열렸어요" else "👍 오늘 열렸어요",
                                fontWeight = FontWeight.Bold,
                                color = if (isOpenVoted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${totalOpenVotes}명 제보",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    val isClosedVoted = localVotedType == false
                    Button(
                        onClick = {
                            if (localVotedType != null) {
                                android.widget.Toast.makeText(context, "오늘 이미 시장 현장 제보를 완료하셨습니다 😊", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                localVotedType = false
                                optimisticClosedDelta = 1
                                VoteTracker.setMarketVoteStatus(context, market.id, false)
                                onVoteClick(market.id, false)
                                android.widget.Toast.makeText(context, "✅ 소중한 제보 감사합니다! '닫혔어요'가 즉시 반영되었습니다 👏", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        colors = if (isClosedVoted) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                        border = BorderStroke(
                            if (isClosedVoted) 2.dp else 1.5.dp,
                            if (isClosedVoted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isClosedVoted) "✓ 닫혔어요" else "👎 닫혔어요",
                                fontWeight = FontWeight.Bold,
                                color = if (isClosedVoted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${totalClosedVotes}명 제보",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider()
                Spacer(modifier = Modifier.height(20.dp))

                // 6. 편의 시설 & 위치 및 연락처
                Text(
                    text = "🏗 편의 시설 및 위치",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 카드를 터치하여 화장실/주차장 정보를 제보하거나 길안내를 받을 수 있습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                var activeAmenityReport by remember { mutableStateOf<Pair<String, String>?>(null) }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                                        if (VoteTracker.hasReportedAmenity(context, market.id, amenityType)) {
                                            android.widget.Toast.makeText(context, "오늘 이미 '${label}' 이용 가능 제보를 완료하셨습니다 😊", android.widget.Toast.LENGTH_SHORT).show()
                                        } else {
                                            VoteTracker.setAmenityReported(context, market.id, amenityType)
                                            onReportAmenity(amenityType, true)
                                            android.widget.Toast.makeText(context, "✅ '${label}' 있음 정보가 제보되었습니다! 감사합니다 👏", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        activeAmenityReport = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("있음 / 이용 가능 🟢")
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (VoteTracker.hasReportedAmenity(context, market.id, amenityType)) {
                                            android.widget.Toast.makeText(context, "오늘 이미 '${label}' 제보를 완료하셨습니다 😊", android.widget.Toast.LENGTH_SHORT).show()
                                        } else {
                                            VoteTracker.setAmenityReported(context, market.id, amenityType)
                                            onReportAmenity(amenityType, false)
                                            android.widget.Toast.makeText(context, "✅ '${label}' 없음/정보없음 제보가 등록되었습니다! 👏", android.widget.Toast.LENGTH_SHORT).show()
                                        }
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

                Spacer(modifier = Modifier.height(14.dp))
                DetailRow(Icons.Default.LocationOn, market.addressRoad.ifEmpty { market.addressJibun })
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(Icons.Default.ShoppingBag, "주요 품목: $displaySpecialty")
                Spacer(modifier = Modifier.height(12.dp))

                if (displayFeature.isNotEmpty()) {
                    Text(
                        text = "💡 특징",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = displayFeature,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                if (market.phoneNumber.isNotEmpty()) {
                    Row(
                       modifier = Modifier
                           .fillMaxWidth()
                           .clickable {
                               val intent = Intent(Intent.ACTION_DIAL).apply {
                                   data = "tel:${market.phoneNumber}".toUri()
                               }
                               try { context.startActivity(intent) } catch (_: Exception) {}
                           }
                    ) {
                        DetailRow(Icons.Default.Phone, market.phoneNumber)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
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
                    }
                }
            }

            // [P4] Sticky 고정 하단 액션바 (좁은 화면 대응 및 줄바꿈 방지)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { shareMarket(context, market) },
                        modifier = Modifier.weight(1.1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("공유", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, softWrap = false)
                    }

                    Button(
                        onClick = { openMap(context, market) },
                        modifier = Modifier.weight(2.3f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📍 길찾기 안내", fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

private data class LiveDropItem(
    val headline: String,
    val badge: String,
    val subtext: String,
    val image: String
)

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
        val isPermanent = market.isPermanent()
        
        for (r in 0 until rowsCount) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                for (c in 0..6) {
                    val cellIdx = r * 7 + c
                    if (cellIdx < startDayOfWeek || dayCounter > maxDay) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val day = dayCounter
                        val isMarketDay = marketDays.contains(day)
                        val isToday = day == currentDay
                        
                        val cellBg = when {
                            isToday -> MaterialTheme.colorScheme.primary
                            isMarketDay && !isPermanent -> Color(0xFFFFF3E0)
                            else -> Color.Transparent
                        }
                        
                        val cellTextCol = when {
                            isToday -> MaterialTheme.colorScheme.onPrimary
                            isMarketDay && !isPermanent -> Color(0xFFE65100)
                            c == 0 -> Color.Red.copy(alpha = 0.8f)
                            c == 6 -> Color.Blue.copy(alpha = 0.8f)
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cellBg)
                                .then(
                                    if (isMarketDay && !isPermanent && !isToday) 
                                        Modifier.border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(8.dp))
                                    else Modifier
                                )
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isToday || (isMarketDay && !isPermanent)) FontWeight.Bold else FontWeight.Normal,
                                    color = cellTextCol,
                                    textAlign = TextAlign.Center
                                )
                                if (isMarketDay && !isPermanent && !isToday) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(Color(0xFFE65100), androidx.compose.foundation.shape.CircleShape)
                                    )
                                }
                            }
                        }
                        dayCounter++
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        
        // 범례 (Legend)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isPermanent) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFE65100), androidx.compose.foundation.shape.CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("장날", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Text("🏪 상설시장 (매일 개장)", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
            }
            Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("오늘", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

private fun matchesShopCategory(shop: Shop, category: String): Boolean {
    val name = shop.shopName.lowercase(Locale.KOREA)
    val cat = shop.category.lowercase(Locale.KOREA)
    return when (category) {
        "전체" -> true
        "식당·먹거리" -> {
            listOf("식당", "먹거리", "음식", "한식", "중식", "일식", "분식", "칼국수", "국밥", "족발", "순대", "전", "튀김", "통닭", "치킨", "닭강정", "만두", "호떡", "도너츠", "꽈배기", "핫바", "김밥", "냉면", "보리밥", "비빔밥", "찌개", "탕", "구이", "백반", "포차", "주막", "국수").any { name.contains(it) || cat.contains(it) }
        }
        "축산·정육" -> {
            listOf("축산", "정육", "정육점", "한우", "고기", "축산물", "암소", "돈육", "식육", "삼겹살", "생고기").any { name.contains(it) || cat.contains(it) }
        }
        "수산·건어물" -> {
            listOf("수산", "건어물", "젓갈", "활어", "생선", "조개", "해산물", "멸치", "굴", "낙지", "오징어", "미역", "어물", "수산물", "건어", "해물").any { name.contains(it) || cat.contains(it) }
        }
        "청과·채소" -> {
            listOf("청과", "과일", "야채", "채소", "농산", "농산물", "상회", "청과물", "마늘", "양파", "배추", "나물", "유통").any { name.contains(it) || cat.contains(it) }
        }
        "떡·방앗간" -> {
            listOf("특산물", "특산", "떡", "방앗간", "참기름", "들기름", "약초", "인삼", "건재", "한약", "곡물", "잡곡", "쌀", "떡집").any { name.contains(it) || cat.contains(it) }
        }
        "카페·디저트" -> {
            listOf("카페", "커피", "음료", "디저트", "베이커리", "빵", "빙수", "찻집", "주스").any { name.contains(it) || cat.contains(it) }
        }
        "의류·잡화" -> {
            !matchesShopCategory(shop, "식당·먹거리") &&
            !matchesShopCategory(shop, "축산·정육") &&
            !matchesShopCategory(shop, "수산·건어물") &&
            !matchesShopCategory(shop, "청과·채소") &&
            !matchesShopCategory(shop, "떡·방앗간") &&
            !matchesShopCategory(shop, "카페·디저트")
        }
        else -> cat.contains(category) || name.contains(category)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
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
    onSubmitReview: (String, Float, String, Uri?) -> Unit = { _, _, _, _ -> },
    onConfirmOnnuri: (Long) -> Unit = {},
    onDeleteShop: (String) -> Unit = {},
    onReportShopIssue: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var showAddShopDialog by remember { mutableStateOf(false) }
    var prefilledShopName by remember { mutableStateOf("") }
    var activeVotingShop by remember { mutableStateOf<Shop?>(null) }
    var shopToDelete by remember { mutableStateOf<Shop?>(null) }
    var shopToReport by remember { mutableStateOf<Shop?>(null) }
    var selectedCategory by remember { mutableStateOf("전체") }
    var shopSearchText by remember { mutableStateOf("") }
    val confirmedShopIds = remember { mutableStateListOf<Long>() }
    var closedWarningMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
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
                text = "🔥 인기 상점 실시간 대기줄 & 온누리 가맹",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            
            TextButton(
                onClick = { 
                    prefilledShopName = ""
                    showAddShopDialog = true 
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+ 상점 등록",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Item 5: 미매칭 시장 빈 상태 카드 & 첫 등록 유도
        if (shops.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏪", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "아직 등록된 상점이 없는 시장입니다",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "방문하셨던 단골 가게나 맛있는 상점을 직접 등록하여\n다른 방문자들에게 첫 번째 꿀팁을 공유해 보세요!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { 
                            prefilledShopName = ""
                            showAddShopDialog = true 
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("➕ 첫 번째 상점 등록하기", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            var activeReviewShop by remember { mutableStateOf<Shop?>(null) }
            
            // Item 1: 상점 검색창
            OutlinedTextField(
                value = shopSearchText,
                onValueChange = { 
                    shopSearchText = it 
                    if (selectedCategory != "전체" && it.isNotBlank()) {
                        selectedCategory = "전체"
                    }
                },
                placeholder = { Text("시장 내 상호명 또는 메뉴 검색", fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                ),
                leadingIcon = {
                    Text("🔍", fontSize = 14.sp)
                },
                trailingIcon = {
                    if (shopSearchText.isNotEmpty()) {
                        IconButton(onClick = { 
                            shopSearchText = "" 
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }) {
                            Text("✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Item 1: 카테고리 퀵 필터 칩 & 5개 단위 컴팩트 페이징
            var visibleShopCount by remember(selectedCategory, shopSearchText, market.id) { mutableIntStateOf(5) }

            val categoryDefs = listOf(
                "전체" to "전체",
                "식당·먹거리" to "🍚 식당·먹거리",
                "축산·정육" to "🥩 축산·정육",
                "수산·건어물" to "🐟 수산·건어물",
                "청과·채소" to "🍎 청과·채소",
                "떡·방앗간" to "🌾 떡·방앗간",
                "카페·디저트" to "☕ 카페·디저트",
                "의류·잡화" to "👕 의류·잡화"
            )

            val availableCategories = remember(shops) {
                categoryDefs.map { (key, label) ->
                    val count = if (key == "전체") shops.size else shops.count { matchesShopCategory(it, key) }
                    Triple(key, label, count)
                }.filter { it.first == "전체" || it.third > 0 }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableCategories.forEach { (catKey, label, count) ->
                    val isSelected = selectedCategory == catKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { 
                            selectedCategory = catKey 
                            visibleShopCount = 5
                        },
                        label = {
                            Text(
                                text = if (catKey == "전체") "전체 ($count)" else "$label ($count)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val filteredShops = remember(shops, selectedCategory, shopSearchText) {
                shops.filter { shop ->
                    val matchesCat = matchesShopCategory(shop, selectedCategory)
                    val matchesQuery = if (shopSearchText.isBlank()) true else {
                        shop.shopName.contains(shopSearchText, ignoreCase = true) || shop.category.contains(shopSearchText, ignoreCase = true)
                    }
                    matchesCat && matchesQuery
                }
            }
            val displayedShops = remember(filteredShops, visibleShopCount) {
                filteredShops.take(visibleShopCount)
            }

            if (filteredShops.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "총 ${filteredShops.size}곳 중 ${displayedShops.size}곳 표시",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (visibleShopCount > 5) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                modifier = Modifier.clickable { visibleShopCount = 5 }
                            ) {
                                Text(
                                    text = "5개만 보기 ▴",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        if (visibleShopCount < filteredShops.size) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.clickable { visibleShopCount = filteredShops.size }
                            ) {
                                Text(
                                    text = "전체 펼치기 ▾",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (filteredShops.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (shopSearchText.isNotBlank()) "🔍 '${shopSearchText}' 상점을 찾을 수 없습니다." else "🔍 조건에 일치하는 상점이 없습니다.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (shopSearchText.isNotBlank()) "찾으시는 가게가 아직 등록되지 않았나요?\n직접 상점으로 등록해 첫 리뷰와 정보를 남겨보세요!" else "다른 카테고리를 선택하시거나 검색어를 변경해 보세요.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        if (shopSearchText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    prefilledShopName = shopSearchText
                                    showAddShopDialog = true
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("➕ '${shopSearchText}' 상점 등록하기", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = {
                                selectedCategory = "전체"
                                shopSearchText = ""
                                visibleShopCount = 10
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ) {
                            Text("필터 및 검색어 초기화 🔄", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                displayedShops.forEach { shop ->
                    val hasRecentReport = shop.lastReportTime > 0 && 
                            (System.currentTimeMillis() - shop.lastReportTime) < 40 * 60 * 1000 // 40 minutes

                    val shopReviews = reviews[shop.shopName] ?: emptyList()
                    val hasReviews = shopReviews.isNotEmpty()
                    val avgRating = if (hasReviews) shopReviews.map { it.rating }.average() else 0.0
                    val avgStr = String.format(Locale.US, "%.1f", avgRating)
                    val isConfirmedByUser = VoteTracker.hasConfirmedPayment(context, shop.id) || confirmedShopIds.contains(shop.id)
                    val displayConfirmCount = shop.onnuriConfirmedCount + (if (isConfirmedByUser && !confirmedShopIds.contains(shop.id) && shop.onnuriConfirmedCount == 0) 1 else 0)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // 1. 상단: 상호명 + 카테고리 태그 + 평점
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f, fill = false),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = shop.category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = shop.shopName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                val isMine = VoteTracker.isMyCreatedShop(context, market.id, shop.shopName)
                                val canDelete = VoteTracker.canDeleteMyCreatedShop(context, market.id, shop.shopName)
                                val remainingMins = VoteTracker.getRemainingDeleteMinutes(context, market.id, shop.shopName)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (hasReviews) {
                                        Text(
                                            text = "⭐ $avgStr (${shopReviews.size})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100)
                                        )
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                            modifier = Modifier.clickable { activeReviewShop = shop }
                                        ) {
                                            Text(
                                                text = "⭐ 첫 리뷰",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    if (isMine && canDelete) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFEBEE),
                                            border = BorderStroke(0.5.dp, Color(0xFFEF9A9A)),
                                            modifier = Modifier.clickable { shopToDelete = shop }
                                        ) {
                                            Text(
                                                text = "🗑️ 취소(${remainingMins}분)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC62828),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        IconButton(
                                            onClick = { 
                                                if (VoteTracker.hasReportedShopIssue(context, market.id, shop.shopName)) {
                                                    android.widget.Toast.makeText(context, "오늘 이미 '${shop.shopName}'에 대한 제보를 접수하셨습니다 😊", android.widget.Toast.LENGTH_SHORT).show()
                                                } else {
                                                    shopToReport = shop 
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text("🚨", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 2. 중단: 온누리/카드 뱃지 및 현장 확인 칩
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (shop.isOnnuri) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE3F2FD),
                                        border = BorderStroke(0.5.dp, Color(0xFF90CAF9))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "🎫 온누리 공식가맹",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1565C0)
                                            )
                                            Text(
                                                text = " (${shop.onnuriType})",
                                                fontSize = 10.sp,
                                                color = Color(0xFF1976D2)
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE8F5E9),
                                    border = BorderStroke(0.5.dp, Color(0xFFA5D6A7))
                                ) {
                                    Text(
                                        text = "💳 카드결제 가능",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                if (displayConfirmCount > 0 || isConfirmedByUser) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isConfirmedByUser) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                        border = BorderStroke(0.5.dp, if (isConfirmedByUser) Color(0xFFA5D6A7) else Color(0xFFFFCC80))
                                    ) {
                                        Text(
                                            text = if (isConfirmedByUser) "🟢 내 결제인증 완료" else "🟢 결제인증됨 (${displayConfirmCount}명)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isConfirmedByUser) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 3. 대기줄 상태 요약 라인 (영업시간 및 개장일 가드)
                            val currentCal = Calendar.getInstance()
                            val currentHour = currentCal.get(Calendar.HOUR_OF_DAY)
                            val isPeakHour = (currentHour in 11..13) || (currentHour in 17..19)
                            val isOperatingHours = currentHour in 7..20 // 07:00 ~ 20:59
                            val isMarketOpenToday = market.isPermanent() || market.isOpenOn(System.currentTimeMillis())

                            val statusText = if (!isMarketOpenToday) {
                                "비개장일 📅 (다음 장날: ${market.getNextMarketText()})"
                            } else if (!isOperatingHours) {
                                "영업 종료 🌙 (내일 09:00 개장)"
                            } else if (hasRecentReport) {
                                when (shop.queueStatus) {
                                    0 -> if (isPeakHour) "한산함 🟢 (AI 피크: 5~10분)" else "한산함 🟢 (AI 예상: 즉시 입장)"
                                    1 -> if (isPeakHour) "보통 🟡 (AI 피크: 20~30분)" else "보통 🟡 (AI 예상: 10~15분)"
                                    2 -> if (isPeakHour) "혼잡함 🔴 (AI 피크: 40분 이상)" else "혼잡함 🔴 (AI 예상: 25~35분)"
                                    else -> "제보 없음 ⚪ (AI 평시 분석)"
                                }
                            } else {
                                if (isPeakHour) "대기제보 없음 ⚪ (AI 장날 피크: 15~25분 예상)"
                                else "대기제보 없음 ⚪ (AI 평시: 5~10분 예상)"
                            }
                            val statusColor = if (!isMarketOpenToday || !isOperatingHours) {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            } else if (hasRecentReport) {
                                when (shop.queueStatus) {
                                    0 -> Color(0xFF2E7D32)
                                    1 -> Color(0xFFE65100)
                                    2 -> Color(0xFFC62828)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⏱️ $statusText",
                                    fontSize = 12.sp,
                                    color = statusColor,
                                    fontWeight = FontWeight.Medium
                                )

                                if (hasRecentReport && isMarketOpenToday && isOperatingHours) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val mins = ((System.currentTimeMillis() - shop.lastReportTime) / 60000).toInt()
                                    Text(
                                        text = "(${mins}분 전)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // 4. 하단 버튼 바: 2개 직관적이고 널찍한 버튼 (리뷰/결제인증 & 실시간 대기제보)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { activeReviewShop = shop },
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                                ) {
                                    Text("💬 한줄평 · 결제인증", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }

                                OutlinedButton(
                                    onClick = { 
                                        if (!isMarketOpenToday) {
                                            closedWarningMessage = "오늘은 '${market.getDisplayName()}' 장날이 아닙니다.\n다음 장날(${market.getNextMarketText()}) 운영 시간에 현장 대기줄을 제보해 주세요! 📅"
                                        } else if (!isOperatingHours) {
                                            closedWarningMessage = "현재는 전통시장 야간 영업 종료 시간(통상 09:00~19:00)입니다.\n내일 아침 개장 시간(09:00~) 이후 현장 대기줄을 제보해 주세요! 🌙"
                                        } else if (!VoteTracker.canReportQueue(context, shop.id)) {
                                            val lastTime = VoteTracker.getLastQueueReportTime(context, shop.id)
                                            val minsRemaining = 15 - ((System.currentTimeMillis() - lastTime) / 60000).toInt()
                                            android.widget.Toast.makeText(context, "⏱️ 방금 대기줄 제보를 완료하셨습니다.\n(${minsRemaining}분 후 다시 제보 가능)", android.widget.Toast.LENGTH_SHORT).show()
                                        } else {
                                            activeVotingShop = shop
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("⏱️ 실시간 대기제보", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
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

                // 5개 단위 페이징 / 더보기 / 펼치기 / 접기 컨트롤
                if (filteredShops.size > 5) {
                    Spacer(modifier = Modifier.height(10.dp))
                    if (visibleShopCount < filteredShops.size) {
                        val nextCount = minOf(5, filteredShops.size - visibleShopCount)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { visibleShopCount += nextCount },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "👇 상점 ${nextCount}개 더보기 (${visibleShopCount}/${filteredShops.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { visibleShopCount = filteredShops.size },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "전체 펼치기 (${filteredShops.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else if (visibleShopCount > 5) {
                        OutlinedButton(
                            onClick = { visibleShopCount = 5 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                        ) {
                            Text(
                                text = "상점 목록 접기 ▴ (상위 5개만 보기)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (activeReviewShop != null) {
                val reviewTargetContext = LocalContext.current
                val targetShop = activeReviewShop!!
                var ratingVal by remember { mutableStateOf(5f) }
                var textContent by remember { mutableStateOf("") }
                var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                var selectedPaymentTags by remember { mutableStateOf(setOf<String>()) }
                val paymentOptions = listOf("지류 온누리", "카드형 온누리", "모바일 온누리", "신용/체크카드", "현금/이체")
                
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    selectedImageUri = uri
                }

                AlertDialog(
                    onDismissRequest = { activeReviewShop = null },
                    title = { Text("${targetShop.shopName} 한줄평 & 결제인증", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("💡 소진공 공공데이터 온누리 가맹점입니다. 이용하신 결제수단과 함께 솔직한 평점/한줄평을 남겨주시면 다른 방문자들에게 큰 도움이 됩니다!")
                            
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
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    paymentOptions.forEach { opt ->
                                        val isChecked = selectedPaymentTags.contains(opt)
                                        val icon = when {
                                            opt.contains("온누리") -> "🎫"
                                            opt.contains("카드") -> "💳"
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
                                placeholder = { Text("호떡 피가 엄청 쫄깃하고 맛있어요! 온누리 카드 결제 잘 됩니다.") },
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
                                
                                if (selectedPaymentTags.isNotEmpty() && !VoteTracker.hasConfirmedPayment(context, targetShop.id)) {
                                    VoteTracker.setPaymentConfirmed(context, targetShop.id)
                                    confirmedShopIds.add(targetShop.id)
                                    onConfirmOnnuri(targetShop.id)
                                }
                                
                                android.widget.Toast.makeText(reviewTargetContext, "✅ '${targetShop.shopName}' 한줄평 및 결제인증이 완료되었습니다! 📝", android.widget.Toast.LENGTH_SHORT).show()
                                activeReviewShop = null
                            },
                            enabled = canSubmit,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("제보 & 인증 등록 🟢")
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

    // --- Dialogs (Item 6: 위치 힌트 지원 & 카테고리 선택) ---
    if (showAddShopDialog) {
        var searchQuery by remember(prefilledShopName) { mutableStateOf(prefilledShopName) }
        var locationHint by remember { mutableStateOf("") }
        var directCategory by remember { mutableStateOf("먹거리") }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        fun checkDuplicateAndAdd(rawName: String, category: String) {
            val cleanInput = rawName.replace(Regex("[\\s\\p{Punct}]"), "").lowercase()
            val duplicate = shops.find { s ->
                val cleanExisting = s.shopName.replace(Regex("[\\s\\p{Punct}]"), "").lowercase()
                cleanExisting == cleanInput || (cleanInput.length >= 4 && cleanExisting == cleanInput)
            }
            if (duplicate != null) {
                android.widget.Toast.makeText(context, "⚠️ 이미 등록된 상점입니다: '${duplicate.shopName}'", android.widget.Toast.LENGTH_LONG).show()
                return
            }
            val fullShopName = if (locationHint.isNotBlank()) "$rawName (${locationHint.trim()})" else rawName
            onAddShop(fullShopName, category)
            showAddShopDialog = false
            prefilledShopName = ""
            onClearSearchShops()
        }

        LaunchedEffect(prefilledShopName) {
            if (prefilledShopName.isNotBlank()) {
                onSearchShops(prefilledShopName)
            }
        }

        AlertDialog(
            onDismissRequest = { 
                showAddShopDialog = false
                prefilledShopName = ""
                onClearSearchShops()
            },
            title = { Text("주변 상점/맛집 검색 및 등록", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "📍 시장 반경 500m 이내 실제 상점을 검색하거나 직접 등록할 수 있습니다.",
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
                            placeholder = { Text("상호명 (예: 호떡, 칼국수)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    onSearchShops(searchQuery)
                                }
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Button(
                            onClick = { 
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                onSearchShops(searchQuery)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("검색")
                        }
                    }

                    // Item 6: 위치 힌트 입력 필드
                    OutlinedTextField(
                        value = locationHint,
                        onValueChange = { locationHint = it },
                        placeholder = { Text("위치 힌트 (예: 2번 게이트 앞, 먹거리 골목)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Divider()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        if (searchResults.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isBlank()) "상점명을 검색하거나 직접 등록하세요." else "검색 결과가 없습니다.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (searchQuery.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // 직접 등록 카테고리 선택
                                    val catList = listOf("먹거리", "식당", "카페", "기타")
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        catList.forEach { c ->
                                            FilterChip(
                                                selected = directCategory == c,
                                                onClick = { directCategory = c },
                                                label = { Text(c, fontSize = 10.sp) },
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val customName = searchQuery.trim().take(30)
                                            if (customName.isNotEmpty()) {
                                                checkDuplicateAndAdd(customName, directCategory)
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
                                                checkDuplicateAndAdd(shop.shopName, shop.category)
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
                        prefilledShopName = ""
                        onClearSearchShops()
                    }
                ) {
                    Text("닫기")
                }
            }
        )
    }

    if (shopToDelete != null) {
        val target = shopToDelete!!
        val remaining = VoteTracker.getRemainingDeleteMinutes(context, market.id, target.shopName)
        AlertDialog(
            onDismissRequest = { shopToDelete = null },
            title = { Text("상점 등록 취소 (삭제)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("방금 등록하신 '${target.shopName}' 상점을 목록에서 삭제하시겠습니까?")
                    Text(
                        text = "💡 등록 후 10분 이내(남은 시간: 약 ${remaining}분)에만 등록자 본인이 직접 취소할 수 있습니다.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteShop(target.shopName)
                        shopToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("등록 취소 (삭제)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { shopToDelete = null }) {
                    Text("유지하기")
                }
            }
        )
    }

    if (shopToReport != null) {
        val target = shopToReport!!
        var reportReason by remember { mutableStateOf("폐업 / 사라진 상점 🛑") }
        var reportDetail by remember { mutableStateOf("") }
        val reportReasons = listOf(
            "폐업 / 사라진 상점 🛑",
            "위치 또는 시장 불일치 🗺️",
            "상호명 또는 메뉴 오류 ✏️",
            "중복 또는 부적절한 등록 ⚠️"
        )

        AlertDialog(
            onDismissRequest = { shopToReport = null },
            title = { Text("🚨 '${target.shopName}' 정보 오류/제보", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "상점의 변경사항이나 폐업 정보를 알려주시면 검토 후 다른 이용자분들에게 안전하게 반영됩니다.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text("제보 사유 선택", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        reportReasons.forEach { r ->
                            val isSel = reportReason == r
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { reportReason = r }
                            ) {
                                Text(
                                    text = r,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reportDetail,
                        onValueChange = { if (it.length <= 100) reportDetail = it },
                        placeholder = { Text("상세 내용 (선택: 예: 옆 골목으로 이전함)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportShopIssue(target.shopName, reportReason, reportDetail)
                        shopToReport = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("제보 접수하기 🚨", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { shopToReport = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("취소")
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
                        val distanceText = if (distance == Float.MAX_VALUE) {
                            "위치 확인 불가"
                        } else if (distance >= 1000) {
                            String.format(java.util.Locale.KOREA, "%.1fkm", distance / 1000f)
                        } else {
                            "${distance.toInt()}m"
                        }
                        Text(
                            text = if (distance == Float.MAX_VALUE) "⚠️ 시장 외부 제보 (위치 확인 불가, 현장인증 제외)" else "⚠️ 시장 외부 제보 (현장인증 불가: 약 $distanceText 떨어져 있음)",
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
                            VoteTracker.setQueueReported(context, shop.id)
                            onReportQueue(shop.id, 0)
                            android.widget.Toast.makeText(context, "✅ '${shop.shopName}' 대기줄 [한산함] 제보가 반영되었습니다! 👏", android.widget.Toast.LENGTH_SHORT).show()
                            activeVotingShop = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("한산함 (대기 적음 / 바로 입장) 🟢")
                    }
                    Button(
                        onClick = {
                            VoteTracker.setQueueReported(context, shop.id)
                            onReportQueue(shop.id, 1)
                            android.widget.Toast.makeText(context, "✅ '${shop.shopName}' 대기줄 [보통] 제보가 반영되었습니다! 👏", android.widget.Toast.LENGTH_SHORT).show()
                            activeVotingShop = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("보통 (대기 10~25분) 🟡")
                    }
                    Button(
                        onClick = {
                            VoteTracker.setQueueReported(context, shop.id)
                            onReportQueue(shop.id, 2)
                            android.widget.Toast.makeText(context, "✅ '${shop.shopName}' 대기줄 [혼잡함] 제보가 반영되었습니다! 👏", android.widget.Toast.LENGTH_SHORT).show()
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

    if (closedWarningMessage != null) {
        AlertDialog(
            onDismissRequest = { closedWarningMessage = null },
            title = { Text("대기줄 제보 안내", fontWeight = FontWeight.Bold) },
            text = { Text(closedWarningMessage ?: "") },
            confirmButton = {
                Button(onClick = { closedWarningMessage = null }) {
                    Text("확인")
                }
            }
        )
    }
}

@Composable
fun MarketFestivalSection(
    market: Market,
    festivals: List<Festival>
) {
    if (festivals.isEmpty()) return

    // 진행중/예정 축제를 우선 정렬, 종료된 축제는 후순위
    val sortedFestivals = festivals.sortedWith(compareBy({ it.isExpired() }, { it.startDate }))
    val activeCount = festivals.count { !it.isExpired() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🎪 시장 축제 & 문화공연",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (activeCount > 0) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (activeCount > 0) "${activeCount}개 진행/예정" else "종료 행사",
                        color = if (activeCount > 0) Color(0xFFC62828) else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "💡 '${market.getDisplayName()}'에서 열리는 1:1 전용 축제, 버스킹, 노래자랑 및 온누리 환급 행사입니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            sortedFestivals.forEach { fest ->
                val isExpired = fest.isExpired()
                val badgeColor = when {
                    isExpired -> Color.Gray
                    fest.category == "문화공연" -> Color(0xFF7B1FA2)
                    fest.category == "할인행사" -> Color(0xFF2E7D32)
                    fest.category == "야시장" -> Color(0xFFE65100)
                    else -> Color(0xFFC2185B)
                }
                val badgeBg = when {
                    isExpired -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    fest.category == "문화공연" -> Color(0xFFF3E5F5)
                    fest.category == "할인행사" -> Color(0xFFE8F5E9)
                    fest.category == "야시장" -> Color(0xFFFFF3E0)
                    else -> Color(0xFFFFEBEE)
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isExpired) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isExpired) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = if (isExpired) 0.dp else 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = badgeBg
                                ) {
                                    Text(
                                        text = "✨ ${fest.category}",
                                        color = badgeColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (fest.isOngoing()) Color(0xFFFFEBEE) else if (isExpired) Color(0xFFEEEEEE) else Color(0xFFE3F2FD)
                                ) {
                                    Text(
                                        text = fest.getStatusText(),
                                        color = if (fest.isOngoing()) Color(0xFFC62828) else if (isExpired) Color.Gray else Color(0xFF1976D2),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "📅 ${fest.startDate} ~ ${fest.endDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isExpired) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (fest.posterUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = fest.posterUrl,
                                    contentDescription = "축제 포스터",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFFE0B2))
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fest.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (fest.venue.isNotEmpty()) {
                                    Text(
                                        text = "📍 장소: ${fest.venue}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = fest.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                                if (fest.hostOrg.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🏛️ 주최: ${fest.hostOrg}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun MarketCommunitySection(
    market: Market,
    posts: List<CommunityPost>,
    comments: Map<String, List<CommunityComment>> = emptyMap(),
    userLocation: android.location.Location?,
    onSubmitPost: (String, String, String, Uri?) -> Unit,
    onLikePost: (String) -> Unit,
    onReportPost: (String, String, String) -> Unit,
    onBlockAuthor: (String) -> Unit,
    onSubmitComment: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteComment: (String) -> Unit = {},
    onReportComment: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    var showWriteDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("전체") }
    var postToReport by remember { mutableStateOf<CommunityPost?>(null) }
    var postToBlock by remember { mutableStateOf<CommunityPost?>(null) }
    var commentToReport by remember { mutableStateOf<CommunityComment?>(null) }
    var zoomPhotoUrl by remember { mutableStateOf<String?>(null) }
    val myDeviceIdHash = remember { CommunitySafetyHelper.getDeviceIdHash(context) }

    val categories = listOf("전체", "실시간 꿀팁", "온누리 장바구니", "축제소식", "동네수다")

    // Filter posts by tab & blocked authors
    val filteredPosts = remember(posts, selectedTab) {
        posts.filter { post ->
            !CommunitySafetyHelper.isAuthorBlocked(context, post.authorDeviceIdHash) &&
            !post.isBlind &&
            (selectedTab == "전체" || post.category.contains(selectedTab))
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💬 ${market.getDisplayName()} 동네마당",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Button(
                onClick = { showWriteDialog = true },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("➕ 소식 올리기", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "💡 장날 꿀팁, 온누리상품권 사용 장바구니 후기, 현장 소식을 이웃들과 자유롭게 나눠보세요!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 카테고리 필터 칩
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSel = selectedTab == cat
                FilterChip(
                    selected = isSel,
                    onClick = { selectedTab = cat },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredPosts.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🗣️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedTab == "전체") "아직 등록된 동네마당 소식이 없습니다." else "'$selectedTab' 관련 소식이 없습니다.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "오늘 방문하신 후기나 장날 꿀팁, 온누리 득템 소식을 첫 번째로 남겨보세요!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { showWriteDialog = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("📝 첫 번째 소식 올리기 ➕", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredPosts.forEach { post ->
                    val isNear = post.isNearMarket
                    var likedLocally by remember(post.postId) { mutableStateOf(false) }
                    val currentLikes = post.likeCount + (if (likedLocally) 1 else 0)
                    val dateStr = remember(post.createdAt) {
                        SimpleDateFormat("M/d HH:mm", Locale.KOREA).format(Date(post.createdAt))
                    }
                    val postComments = comments[post.postId] ?: emptyList()
                    var isCommentsExpanded by remember { mutableStateOf(false) }
                    var commentInputText by remember { mutableStateOf("") }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = post.authorNickname,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (isNear) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Text(
                                                text = "📍 현장인증 🟢",
                                                color = Color(0xFF2E7D32),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = post.category,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = dateStr,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    IconButton(
                                        onClick = { postToReport = post },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Text("🚨", fontSize = 11.sp)
                                    }
                                    IconButton(
                                        onClick = { postToBlock = post },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Text("🚫", fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = post.content,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )

                            if (post.photoUrl.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                AsyncImage(
                                    model = post.photoUrl,
                                    contentDescription = "동네마당 첨부 사진",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { zoomPhotoUrl = post.photoUrl }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 액션 버튼 행 (댓글 토글 + 공감)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCommentsExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.clickable { isCommentsExpanded = !isCommentsExpanded }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = "💬", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (postComments.isEmpty()) "댓글 쓰기" else "댓글 ${postComments.size}개",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (likedLocally) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = BorderStroke(0.5.dp, if (likedLocally) Color(0xFFEF9A9A) else Color.Transparent),
                                    modifier = Modifier.clickable {
                                        if (!likedLocally) {
                                            likedLocally = true
                                            onLikePost(post.postId)
                                        }
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = if (likedLocally) "❤️" else "🤍", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "공감 $currentLikes",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (likedLocally) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // 댓글 스레드 영역
                            if (isCommentsExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        if (postComments.isEmpty()) {
                                            Text(
                                                text = "아직 댓글이 없습니다. 첫 번째 댓글을 남겨보세요! 😊",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                postComments.forEach { c ->
                                                    val isCommentNear = c.isNearMarket
                                                    val isMyComment = c.authorDeviceIdHash == myDeviceIdHash
                                                    val cDateStr = remember(c.createdAt) {
                                                        SimpleDateFormat("M/d HH:mm", Locale.KOREA).format(Date(c.createdAt))
                                                    }
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                                                            .padding(8.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = c.authorNickname,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 11.sp
                                                                )
                                                                if (isCommentNear) {
                                                                    Text(
                                                                        text = "📍현장인증",
                                                                        color = Color(0xFF2E7D32),
                                                                        fontSize = 9.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                            }
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = cDateStr,
                                                                    fontSize = 9.sp,
                                                                    color = MaterialTheme.colorScheme.outline
                                                                )
                                                                if (isMyComment) {
                                                                    IconButton(
                                                                        onClick = { onDeleteComment(c.commentId) },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Text("🗑️", fontSize = 10.sp)
                                                                    }
                                                                } else {
                                                                    IconButton(
                                                                        onClick = { commentToReport = c },
                                                                        modifier = Modifier.size(16.dp)
                                                                    ) {
                                                                        Text("🚨", fontSize = 10.sp)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = c.content,
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            lineHeight = 16.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // 댓글 입력창
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = commentInputText,
                                                onValueChange = { if (it.length <= 150) commentInputText = it },
                                                placeholder = { Text("따뜻한 댓글을 남겨보세요...", fontSize = 11.sp) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                maxLines = 2
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Button(
                                                onClick = {
                                                    if (commentInputText.isNotBlank()) {
                                                        onSubmitComment(post.postId, "", commentInputText.trim())
                                                        commentInputText = ""
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                enabled = commentInputText.isNotBlank()
                                            ) {
                                                Text("등록", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---
    if (showWriteDialog) {
        var hasAgreed by remember { mutableStateOf(CommunitySafetyHelper.hasAgreedToGuidelines(context)) }
        var showGuidelinesDialog by remember { mutableStateOf(!hasAgreed) }
        var customNickname by remember { mutableStateOf(CommunitySafetyHelper.generateRandomNickname()) }
        var postCategory by remember { mutableStateOf("실시간 꿀팁") }
        var postContent by remember { mutableStateOf("") }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

        if (showGuidelinesDialog) {
            AlertDialog(
                onDismissRequest = { showWriteDialog = false },
                title = { Text("📜 정겨운 동네마당 이용 약속", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("쾌적하고 따뜻한 전통시장 소통을 위해 아래 가이드라인을 준수해 주세요.")
                        Text("1. 🚫 욕설, 비속어, 특정 상인 비방 금지", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("2. 🚫 개인 간 계좌이체/금전거래 및 사기 유도 금지", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("3. 🚫 불법 광고, 도박, 외부 링크 홍보 금지", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("💡 위반 시 게시글이 즉시 블라인드 처리되며 이용이 영구 제한될 수 있습니다.", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            CommunitySafetyHelper.setAgreedToGuidelines(context)
                            hasAgreed = true
                            showGuidelinesDialog = false
                        }
                    ) {
                        Text("동의하고 글쓰기")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWriteDialog = false }) {
                        Text("취소")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showWriteDialog = false },
                title = { Text("💬 '${market.getDisplayName()}' 소식 올리기", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 닉네임 입력
                        OutlinedTextField(
                            value = customNickname,
                            onValueChange = { if (it.length <= 12) customNickname = it },
                            label = { Text("작성자 닉네임") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // 카테고리 선택
                        Text("카테고리 선택", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        val postCats = listOf("실시간 꿀팁", "온누리 장바구니", "축제소식", "동네수다")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            postCats.forEach { c ->
                                val isSel = postCategory == c
                                FilterChip(
                                    selected = isSel,
                                    onClick = { postCategory = c },
                                    label = { Text(c, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(6.dp)
                                )
                            }
                        }

                        // 내용 입력
                        val validationError = remember(postContent) {
                            if (postContent.isBlank()) null else CommunitySafetyHelper.validateContent(postContent)
                        }

                        OutlinedTextField(
                            value = postContent,
                            onValueChange = { if (it.length <= 300) postContent = it },
                            placeholder = { Text("오늘 시장에서 겪은 재미있는 일이나 온누리상품권 사용 꿀팁, 맛있는 먹거리를 공유해 주세요!") },
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (validationError != null) {
                                        Text(text = "⚠️ $validationError", color = MaterialTheme.colorScheme.error, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                    Text(text = "${postContent.length}/300자", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            isError = validationError != null,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            shape = RoundedCornerShape(8.dp)
                        )

                        // 사진 첨부
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
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                                ) {
                                    androidx.compose.foundation.Image(
                                        painter = coil.compose.rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = "첨부 사진",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                Text("첨부 없음", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                },
                confirmButton = {
                    val canSubmit = postContent.trim().isNotBlank() && CommunitySafetyHelper.validateContent(postContent) == null
                    Button(
                        onClick = {
                            onSubmitPost(customNickname, postCategory, postContent, selectedImageUri)
                            showWriteDialog = false
                        },
                        enabled = canSubmit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("동네마당에 소식 올리기 🟢", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showWriteDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }

    // 신고 다이얼로그
    if (postToReport != null) {
        val target = postToReport!!
        var reportReason by remember { mutableStateOf("욕설 및 비방 🛑") }
        val reasons = listOf("욕설 및 비방 🛑", "사기 및 개인정보/계좌 노출 ⚠️", "불법 광고 및 도박 스팸 🚫", "부적절한 내용 ✏️")

        AlertDialog(
            onDismissRequest = { postToReport = null },
            title = { Text("🚨 게시글 신고", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("해당 글을 신고하시는 사유를 선택해 주세요. 누적 3회 이상 신고 시 즉시 자동 숨김 처리됩니다.")
                    reasons.forEach { r ->
                        val isSel = reportReason == r
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier.fillMaxWidth().clickable { reportReason = r }
                        ) {
                            Text(text = r, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportPost(target.postId, target.authorDeviceIdHash, reportReason)
                        postToReport = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("신고 접수")
                }
            },
            dismissButton = {
                TextButton(onClick = { postToReport = null }, modifier = Modifier.fillMaxWidth()) {
                    Text("취소")
                }
            }
        )
    }

    // 댓글 신고 다이얼로그
    if (commentToReport != null) {
        val target = commentToReport!!
        var reportReason by remember { mutableStateOf("욕설 및 비방 🛑") }
        val reasons = listOf("욕설 및 비방 🛑", "사기 및 개인정보/계좌 노출 ⚠️", "불법 광고 및 도박 스팸 🚫", "부적절한 내용 ✏️")

        AlertDialog(
            onDismissRequest = { commentToReport = null },
            title = { Text("🚨 댓글 신고", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("해당 댓글을 신고하시는 사유를 선택해 주세요. 누적 3회 이상 신고 시 즉시 자동 숨김 처리됩니다.")
                    reasons.forEach { r ->
                        val isSel = reportReason == r
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier.fillMaxWidth().clickable { reportReason = r }
                        ) {
                            Text(text = r, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportComment(target.postId, target.commentId, target.authorDeviceIdHash, reportReason)
                        commentToReport = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("신고 접수")
                }
            },
            dismissButton = {
                TextButton(onClick = { commentToReport = null }, modifier = Modifier.fillMaxWidth()) {
                    Text("취소")
                }
            }
        )
    }

    // 차단 다이얼로그
    if (postToBlock != null) {
        val target = postToBlock!!
        AlertDialog(
            onDismissRequest = { postToBlock = null },
            title = { Text("🚫 작성자 차단", fontWeight = FontWeight.Bold) },
            text = {
                Text("정말 '${target.authorNickname}' 사용자를 차단하시겠습니까?\n\n차단하시면 이 사용자가 작성한 모든 글과 댓글이 내 화면에서 영구적으로 숨겨집니다.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBlockAuthor(target.authorDeviceIdHash)
                        postToBlock = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("차단하기", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { postToBlock = null }) {
                    Text("취소")
                }
            }
        )
    }

    // 사진 크게보기 다이얼로그
    if (zoomPhotoUrl != null) {
        AlertDialog(
            onDismissRequest = { zoomPhotoUrl = null },
            title = { Text("사진 크게보기", fontWeight = FontWeight.Bold) },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = zoomPhotoUrl,
                        contentDescription = "확대 사진",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { zoomPhotoUrl = null }) {
                    Text("닫기")
                }
            }
        )
    }
}
