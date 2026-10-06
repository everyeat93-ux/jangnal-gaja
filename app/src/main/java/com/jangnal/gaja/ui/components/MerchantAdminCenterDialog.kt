package com.jangnal.gaja.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.jangnal.gaja.data.local.entity.MerchantVerification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🏛️ 장날가자 마스터 관리자 심사 센터 (운영자 전용 시크릿 포털)
 * - 사업자등록증 / 상인회원증 원본 사진 검토
 * - 대표자, 사업자번호(10자리), 연락처 확인 및 원터치 전화걸기
 * - [✅ 공식 승인] / [❌ 심사 반려] 실시간 처리
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantAdminCenterDialog(
    verifications: List<MerchantVerification>,
    onUpdateStatus: (MerchantVerification, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 대기, 1: 승인완료, 2: 반려
    var searchQuery by remember { mutableStateOf("") }
    var selectedDocForViewing by remember { mutableStateOf<MerchantVerification?>(null) }
    var itemToReject by remember { mutableStateOf<MerchantVerification?>(null) }

    val pendingList = remember(verifications, searchQuery) {
        verifications.filter { it.isPending() && (searchQuery.isBlank() || it.shopName.contains(searchQuery, true) || it.marketName.contains(searchQuery, true)) }
    }
    val approvedList = remember(verifications, searchQuery) {
        verifications.filter { it.isApproved() && (searchQuery.isBlank() || it.shopName.contains(searchQuery, true) || it.marketName.contains(searchQuery, true)) }
    }
    val rejectedList = remember(verifications, searchQuery) {
        verifications.filter { it.isRejected() && (searchQuery.isBlank() || it.shopName.contains(searchQuery, true) || it.marketName.contains(searchQuery, true)) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏛️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("마스터 관리자 심사 센터", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            }
                            Text(
                                text = "전국 전통시장 공식 상인 서류 심사 및 승인 관리",
                                fontSize = 11.sp,
                                color = Color(0xFFC8E6C9)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "닫기", tint = Color.White)
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = "운영자 모드",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1B5E20),
                        titleContentColor = Color.White
                    )
                )
            },
            containerColor = Color(0xFFF5F7F6)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 상단 통계 뱃지 바
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "총 접수: ${verifications.size}건",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("대기 ${verifications.count { it.isPending() }}", fontSize = 11.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                            Text("·", fontSize = 11.sp, color = Color.Gray)
                            Text("승인 ${verifications.count { it.isApproved() }}", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            Text("·", fontSize = 11.sp, color = Color.Gray)
                            Text("반려 ${verifications.count { it.isRejected() }}", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 탭 선택 (대기 / 승인완료 / 반려내역)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF1B5E20)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "⏳ 심사 대기 (${verifications.count { it.isPending() }})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "✅ 승인 완료 (${verifications.count { it.isApproved() }})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "❌ 반려 내역 (${verifications.count { it.isRejected() }})",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }

                // 검색 바
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("상점명 또는 시장명 검색", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White
                    )
                )

                // 본문 리스트 영역
                val currentList = when (selectedTab) {
                    0 -> pendingList
                    1 -> approvedList
                    else -> rejectedList
                }

                if (currentList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when (selectedTab) {
                                    0 -> "🎉 현재 심사 대기 중인 서류가 없습니다!"
                                    1 -> "아직 승인 완료된 상점이 없습니다."
                                    else -> "반려된 내역이 없습니다."
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "새로운 상인 인증 신청이 접수되면 이곳에 표시됩니다.",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        currentList.forEach { verif ->
                            MerchantVerificationAdminCard(
                                verif = verif,
                                onOpenPhoto = { selectedDocForViewing = verif },
                                onApprove = { onUpdateStatus(verif, "APPROVED", "") },
                                onReject = { itemToReject = verif },
                                onResetToPending = { onUpdateStatus(verif, "PENDING", "") }
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // 📷 서류 원본 사진 고화질 뷰어 다이얼로그
    selectedDocForViewing?.let { item ->
        Dialog(
            onDismissRequest = { selectedDocForViewing = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.95f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${item.shopName} - ${item.documentType}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "대표자: ${item.ownerName} | 사업자번호: ${item.businessNumber}",
                                color = Color(0xFFA5D6A7),
                                fontSize = 12.sp
                            )
                        }
                        IconButton(onClick = { selectedDocForViewing = null }) {
                            Icon(Icons.Default.Close, contentDescription = "닫기", tint = Color.White)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.documentPhotoUri.isNotBlank()) {
                            AsyncImage(
                                model = item.documentPhotoUri,
                                contentDescription = "제출된 증빙 서류",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text("서류 사진이 첨부되지 않았습니다.", color = Color.White)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onUpdateStatus(item, "APPROVED", "")
                                selectedDocForViewing = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("✅ 바로 공식 승인", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                selectedDocForViewing = null
                                itemToReject = item
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF5350)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350))
                        ) {
                            Text("❌ 반려 처리", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ❌ 반려 사유 입력 다이얼로그
    itemToReject?.let { item ->
        var rejectReason by remember { mutableStateOf("서류 사진의 글씨가 식별되지 않음") }
        val commonReasons = listOf(
            "서류 사진의 글씨가 식별되지 않음",
            "입력한 사업자등록번호와 서류 불일치",
            "상점 상호명과 사업자등록증 상호 불일치",
            "전통시장 상인회 회원 확인 불가",
            "유효하지 않은 서류 양식"
        )

        AlertDialog(
            onDismissRequest = { itemToReject = null },
            title = {
                Text("❌ 서류 심사 반려 처리", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "'${item.shopName}' 상점의 인증 신청을 반려합니다. 사유를 선택하거나 입력해 주세요.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    commonReasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { rejectReason = reason }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = rejectReason == reason,
                                onClick = { rejectReason = reason }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(reason, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("반려 사유 직접 입력", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStatus(item, "REJECTED", rejectReason.trim())
                        itemToReject = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("반려 확정", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToReject = null }) {
                    Text("취소")
                }
            }
        )
    }
}

/**
 * 관리자 심사 개별 상점 서류 카드
 */
@Composable
fun MerchantVerificationAdminCard(
    verif: MerchantVerification,
    onOpenPhoto: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onResetToPending: () -> Unit
) {
    val context = LocalContext.current
    val dateStr = SimpleDateFormat("yy.MM.dd HH:mm", Locale.KOREA).format(Date(verif.submitTimestamp))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(
            1.dp,
            when (verif.status) {
                "APPROVED" -> Color(0xFF81C784)
                "REJECTED" -> Color(0xFFEF9A9A)
                else -> Color(0xFFFFB74D)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // 헤더: 상점명 / 시장명 / 상태 뱃지
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = verif.shopName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "📍 ${verif.marketName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (verif.status) {
                        "APPROVED" -> Color(0xFFE8F5E9)
                        "REJECTED" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF3E0)
                    }
                ) {
                    Text(
                        text = when (verif.status) {
                            "APPROVED" -> "✓ 공식 인증됨"
                            "REJECTED" -> "✕ 심사 반려"
                            else -> "⏳ 심사 대기"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (verif.status) {
                            "APPROVED" -> Color(0xFF2E7D32)
                            "REJECTED" -> Color(0xFFC62828)
                            else -> Color(0xFFE65100)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(10.dp))

            // 상세 정보 행
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("• 대표자: ${verif.ownerName}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("• ${verif.documentType} 번호: ${verif.businessNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• 연락처: ${verif.contactPhone}", fontSize = 12.sp)
                        if (verif.contactPhone.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE3F2FD),
                                modifier = Modifier.clickable {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${verif.contactPhone.replace("-", "")}"))
                                    try { context.startActivity(dialIntent) } catch (_: Exception) {}
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(11.dp), tint = Color(0xFF1976D2))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("전화연결", fontSize = 10.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("• 신청일시: $dateStr", fontSize = 11.sp, color = Color.Gray)

                    if (verif.isRejected() && verif.rejectionReason.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠️ 반려 사유: ${verif.rejectionReason}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                // 사진 썸네일
                if (verif.documentPhotoUri.isNotBlank()) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEEEEE))
                            .clickable { onOpenPhoto() }
                    ) {
                        AsyncImage(
                            model = verif.documentPhotoUri,
                            contentDescription = "서류 사진 썸네일",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "📷 사진확대",
                                color = Color.White,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 하단 조작 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (verif.isPending()) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF5350)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("❌ 심사 반려", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("✅ 공식 승인 완료", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (verif.isApproved()) {
                    OutlinedButton(
                        onClick = onResetToPending,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("⏳ 대기 상태로 되돌리기", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF5350)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("인증 취소 (반려)", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text("✅ 재심사 및 승인하기", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
