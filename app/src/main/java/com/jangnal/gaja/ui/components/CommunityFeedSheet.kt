package com.jangnal.gaja.ui.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jangnal.gaja.data.local.entity.CommunityPost
import com.jangnal.gaja.data.local.entity.Market
import com.jangnal.gaja.util.CommunitySafetyHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 전국 전통시장 동네마당 실시간 피드 모아보기 모달 시트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedSheet(
    posts: List<CommunityPost>,
    markets: List<Market>,
    onSelectMarket: (Market) -> Unit,
    onLikePost: (Long, String) -> Unit,
    onReportPost: (Long, String, String, String) -> Unit,
    onBlockAuthor: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf("전체") }
    var zoomPhotoUrl by remember { mutableStateOf<String?>(null) }
    var postToReport by remember { mutableStateOf<CommunityPost?>(null) }
    var postToBlock by remember { mutableStateOf<CommunityPost?>(null) }

    val categories = listOf("전체", "💡 실시간 꿀팁", "🛍️ 온누리 장바구니", "🎪 축제소식", "💬 동네수다")

    // Filter posts by tab & blocked authors
    val filteredPosts = remember(posts, selectedCategory) {
        posts.filter { post ->
            !CommunitySafetyHelper.isAuthorBlocked(context, post.authorDeviceIdHash) &&
            !post.isBlind &&
            (selectedCategory == "전체" || post.category.contains(selectedCategory.replace("💡 ", "").replace("🛍️ ", "").replace("🎪 ", "").replace("💬 ", "")))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💬 전국 시장 동네마당 피드",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color(0xFF2E7D32),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "닫기")
                }
            }

            Text(
                text = "전국 전통시장의 실시간 꿀팁과 온누리 장바구니 후기를 한눈에 확인하세요!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 카테고리 필터 칩
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("🧺", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "아직 등록된 동네마당 소식이 없습니다.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "시장을 선택하고 첫 번째 장날 꿀팁이나\n장바구니 득템 자랑을 남겨보세요!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredPosts, key = { it.postId }) { post ->
                        var likedLocally by remember(post.postId) { mutableStateOf(false) }
                        val currentLikes = post.likeCount + (if (likedLocally) 1 else 0)
                        val dateStr = remember(post.createdAt) {
                            SimpleDateFormat("M/d HH:mm", Locale.KOREA).format(Date(post.createdAt))
                        }
                        val targetMarket = remember(post.marketId) { markets.find { it.id == post.marketId } }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // 시장 배지 & 작성자
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // 시장명 바로가기 배지
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.clickable {
                                                targetMarket?.let { onSelectMarket(it) }
                                            }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Store,
                                                    contentDescription = "시장",
                                                    modifier = Modifier.size(12.dp),
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = post.marketName,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }

                                        Text(
                                            text = post.authorNickname,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )

                                        if (post.isNearMarket) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFE8F5E9)
                                            ) {
                                                Text(
                                                    text = "📍현장인증",
                                                    color = Color(0xFF2E7D32),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = dateStr,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline,
                                            maxLines = 1,
                                            softWrap = false
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

                                Spacer(modifier = Modifier.height(8.dp))

                                // 본문 내용
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
                                            .size(110.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { zoomPhotoUrl = post.photoUrl }
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // 하단 액션 행
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (targetMarket != null) {
                                        TextButton(
                                            onClick = { onSelectMarket(targetMarket) },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = "🏪 '${post.marketName}' 상세보기 ➔",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(1.dp))
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (likedLocally) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(0.5.dp, if (likedLocally) Color(0xFFEF9A9A) else Color.Transparent),
                                        modifier = Modifier.clickable {
                                            if (!likedLocally) {
                                                likedLocally = true
                                                onLikePost(post.marketId, post.postId)
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
                            }
                        }
                    }
                }
            }
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
                        onReportPost(target.marketId, target.postId, target.authorDeviceIdHash, reportReason)
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
