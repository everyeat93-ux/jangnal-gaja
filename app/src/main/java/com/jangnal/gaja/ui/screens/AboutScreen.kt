package com.jangnal.gaja.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jangnal.gaja.data.local.entity.MerchantVerification
import com.jangnal.gaja.ui.theme.BackgroundCream
import com.jangnal.gaja.ui.theme.JangnalBrown
import com.jangnal.gaja.ui.theme.JangnalOrange
import com.jangnal.gaja.ui.theme.JangnalYellow
import com.jangnal.gaja.util.CommunitySafetyHelper

private enum class SettingsDetailModal {
    APP_GUIDE,
    FIVE_DAY_MARKET_INFO,
    ONNURI_GUIDE,
    MERCHANT_PARTNERSHIP,
    PRIVACY_POLICY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onDismiss: () -> Unit,
    currentScale: Float,
    onScaleChange: (Float) -> Unit,
    verifications: List<MerchantVerification> = emptyList(),
    onUpdateVerificationStatus: (MerchantVerification, String, String) -> Unit = { _, _, _ -> },
    favoriteCount: Int = 0,
    stampCount: Int = 0,
    totalMarketCount: Int = 987,
    onOpenPassport: () -> Unit = {},
    onOpenCommunity: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPref = remember {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    var morningAlarmEnabled by remember {
        mutableStateOf(sharedPref.getBoolean("morning_alarm_enabled", true))
    }
    var blockedCount by remember {
        mutableIntStateOf(CommunitySafetyHelper.getBlockedAuthorCount(context))
    }

    var activeModal by remember { mutableStateOf<SettingsDetailModal?>(null) }
    var showContactSheet by remember { mutableStateOf(false) }
    var showUnblockConfirm by remember { mutableStateOf(false) }

    var secretTapCount by remember { mutableIntStateOf(0) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showAdminCenter by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val triggerSecretTap = {
        secretTapCount++
        if (secretTapCount >= 5) {
            secretTapCount = 0
            enteredPin = ""
            pinError = false
            showPinDialog = true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "설정 및 더보기",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { triggerSecretTap() }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = JangnalYellow,
                        titleContentColor = JangnalBrown,
                        navigationIconContentColor = JangnalBrown
                    )
                )
            },
            containerColor = BackgroundCream
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. 상단 나의 장날 활동 요약 카드
                MyActivitySummaryCard(
                    favoriteCount = favoriteCount,
                    stampCount = stampCount,
                    totalMarketCount = totalMarketCount,
                    onOpenPassport = onOpenPassport,
                    onOpenCommunity = onOpenCommunity
                )

                // 2. 화면 및 알림 설정 섹션
                SettingsSectionCard(title = "⚙️ 맞춤 환경 설정") {
                    // 글자 크기 조절
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingsIconBadge("🔎", Color(0xFFFFF3E0))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "글자 크기 설정",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = JangnalBrown
                                    )
                                    Text(
                                        text = "앱 전체 글자 크기를 보기 편하게 조절합니다",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SizeButton("기본 (100%)", 1.0f, currentScale, onScaleChange, Modifier.weight(1f))
                            SizeButton("크게 (125%)", 1.25f, currentScale, onScaleChange, Modifier.weight(1f))
                            SizeButton("왕크게 (150%)", 1.5f, currentScale, onScaleChange, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = BackgroundCream.copy(alpha = 0.7f),
                            border = BorderStroke(0.5.dp, JangnalBrown.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = "💡 미리보기: 오늘 열리는 전통시장과 온누리 가맹점 정보를 이 크기로 안내해 드립니다.",
                                fontSize = 12.sp,
                                color = JangnalBrown.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    SettingsDivider()

                    // 단골시장 아침 알림 토글
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val next = !morningAlarmEnabled
                                morningAlarmEnabled = next
                                sharedPref.edit().putBoolean("morning_alarm_enabled", next).apply()
                                val msg = if (next) "🔔 단골시장 장날 아침 알림이 켜졌습니다." else "🔕 단골시장 장날 아침 알림이 꺼졌습니다."
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SettingsIconBadge("🔔", Color(0xFFFFF8E1))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "단골시장 개장일 아침 알림",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = JangnalBrown
                                )
                                Text(
                                    text = "찜(❤️)한 5일장이 열리는 날 아침에 알려드려요",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = morningAlarmEnabled,
                            onCheckedChange = { checked ->
                                morningAlarmEnabled = checked
                                sharedPref.edit().putBoolean("morning_alarm_enabled", checked).apply()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = JangnalOrange
                            )
                        )
                    }

                    SettingsDivider()

                    // 기기 권한 설정 바로가기
                    SettingsRowItem(
                        iconEmoji = "📍",
                        iconBg = Color(0xFFE8F5E9),
                        title = "위치 및 알림 권한 설정",
                        subtitle = "내 주변 시장 탐색 및 시스템 알림 권한 관리",
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                }

                // 3. 이용 안내 및 도움말 섹션
                SettingsSectionCard(title = "📖 이용 안내 및 도움말") {
                    SettingsRowItem(
                        iconEmoji = "🏪",
                        iconBg = Color(0xFFFFF3E0),
                        title = "'장날가자' 핵심 기능 & 활용 가이드",
                        subtitle = "오늘 개장 필터, 동네마당, 길찾기 200% 활용법",
                        onClick = { activeModal = SettingsDetailModal.APP_GUIDE }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        iconEmoji = "🍎",
                        iconBg = Color(0xFFFFEBEE),
                        title = "5일장이란 무엇인가요?",
                        subtitle = "1·6일장, 2·7일장 등 전통 5일장 날짜 계산법",
                        onClick = { activeModal = SettingsDetailModal.FIVE_DAY_MARKET_INFO }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        iconEmoji = "🎫",
                        iconBg = Color(0xFFE3F2FD),
                        title = "온누리상품권 종류 & 가맹점 확인 안내",
                        subtitle = "지류·카드형·모바일 온누리상품권 이용 방법",
                        onClick = { activeModal = SettingsDetailModal.ONNURI_GUIDE }
                    )
                }

                // 4. 고객지원 및 상인 파트너십 섹션
                SettingsSectionCard(title = "💌 고객지원 및 파트너십") {
                    SettingsRowItem(
                        iconEmoji = "⭐",
                        iconBg = Color(0xFFFFF8E1),
                        title = "구글 플레이스토어 응원 리뷰 남기기",
                        subtitle = "따뜻한 별점과 리뷰는 장날가자 발전에 큰 힘이 됩니다",
                        badgeText = "추천",
                        badgeColor = JangnalOrange,
                        onClick = {
                            try {
                                val marketIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=${context.packageName}")
                                )
                                context.startActivity(marketIntent)
                            } catch (_: Exception) {
                                try {
                                    val webIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                    )
                                    context.startActivity(webIntent)
                                } catch (_: Exception) {}
                            }
                        }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        iconEmoji = "💬",
                        iconBg = Color(0xFFE8F5E9),
                        title = "1:1 문의 · 시장 정보 제보 · 버그 신고",
                        subtitle = "새로운 5일장 제보나 불편 사항을 개발자에게 전달",
                        onClick = { showContactSheet = true }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        iconEmoji = "🏛️",
                        iconBg = Color(0xFFEDE7F6),
                        title = "전통시장 상인 스마트상점 도입·입점 안내",
                        subtitle = "소진공 국비 지원 안내 및 상점 공식 파트너십 문의",
                        badgeText = "상인전용",
                        badgeColor = Color(0xFF5E35B1),
                        onClick = { activeModal = SettingsDetailModal.MERCHANT_PARTNERSHIP }
                    )
                }

                // 5. 약관 및 데이터 관리 섹션
                SettingsSectionCard(title = "🔒 약관 및 데이터 관리") {
                    SettingsRowItem(
                        iconEmoji = "📄",
                        iconBg = Color(0xFFECEFF1),
                        title = "개인정보 처리방침 및 UGC 이용 정책",
                        subtitle = "위치정보 보호 및 건전한 동네마당 커뮤니티 운영 원칙",
                        onClick = { activeModal = SettingsDetailModal.PRIVACY_POLICY }
                    )
                    SettingsDivider()
                    SettingsRowItem(
                        iconEmoji = "🚫",
                        iconBg = Color(0xFFFFEBEE),
                        title = "차단한 사용자 관리",
                        subtitle = if (blockedCount > 0) "현재 차단된 사용자 ${blockedCount}명 (터치하여 차단 해제)" else "현재 차단된 사용자가 없습니다",
                        trailingText = "${blockedCount}명",
                        onClick = {
                            if (blockedCount > 0) {
                                showUnblockConfirm = true
                            } else {
                                Toast.makeText(context, "차단된 사용자가 없습니다 😊", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    SettingsDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { triggerSecretTap() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SettingsIconBadge("ℹ️", Color(0xFFE3F2FD))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "앱 버전 정보",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = JangnalBrown
                                )
                                Text(
                                    text = "전국 987개 시장 · 79,500+ 온누리 가맹점 수록",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(0.5.dp, Color(0xFF81C784))
                        ) {
                            Text(
                                text = "v2.0.7 최신",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // 6. 하단 푸터 (사업자 정보)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { triggerSecretTap() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "장날가자 | 상호명: 콜코(COLLCO) | 대표자: 김문정",
                        fontSize = 11.sp,
                        color = JangnalBrown.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "사업자등록번호: 850-64-00732 | 통신판매업: 2024-서울구로-0598",
                        fontSize = 11.sp,
                        color = JangnalBrown.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "고객센터 및 제휴문의: collcokorea@gmail.com",
                        fontSize = 11.sp,
                        color = JangnalBrown.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // 상세 가이드 / 약관 모달 다이얼로그
        if (activeModal != null) {
            SettingsDetailDialog(
                modal = activeModal!!,
                context = context,
                onDismiss = { activeModal = null }
            )
        }

        // 1:1 문의 및 제보 선택 다이얼로그
        if (showContactSheet) {
            AlertDialog(
                onDismissRequest = { showContactSheet = false },
                title = {
                    Text("💌 개발자 문의 및 제보 센터", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = JangnalBrown)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "'장날가자'는 사용자분들의 소중한 제보와 피드백으로 매주 발전하고 있습니다. 원하시는 문의 유형을 선택해 주세요!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = {
                                showContactSheet = false
                                sendEmail(context, "[장날가자] 기능 개선 / 건의사항 제안")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("💡 기능 개선 / 아이디어 제안하기", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                showContactSheet = false
                                sendEmail(context, "[장날가자] 새로운 5일장 / 시장 정보 오류 제보")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🏪 새로운 시장 추가 · 정보 오류 제보", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                showContactSheet = false
                                sendEmail(context, "[장날가자] 앱 버그 및 오류 신고")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🐞 앱 버그 및 오류 신고하기", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showContactSheet = false }) {
                        Text("닫기", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // 차단 사용자 초기화 확인 다이얼로그
        if (showUnblockConfirm) {
            AlertDialog(
                onDismissRequest = { showUnblockConfirm = false },
                title = { Text("🚫 차단 목록 초기화", fontWeight = FontWeight.Bold) },
                text = { Text("동네마당에서 차단한 사용자(${blockedCount}명)를 모두 차단 해제하시겠습니까?") },
                confirmButton = {
                    Button(
                        onClick = {
                            CommunitySafetyHelper.clearBlockedAuthors(context)
                            blockedCount = 0
                            showUnblockConfirm = false
                            Toast.makeText(context, "✅ 차단 목록이 모두 초기화되었습니다.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JangnalOrange)
                    ) {
                        Text("초기화", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnblockConfirm = false }) {
                        Text("취소")
                    }
                }
            )
        }

        // 🔒 관리자 보안 PIN 인증 다이얼로그 (마스터 PIN: 7788)
        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔒", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("관리자 보안 인증", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "운영자 마스터 PIN 번호 4자리를 입력하세요.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { input ->
                                if (input.length <= 4 && input.all { it.isDigit() }) {
                                    enteredPin = input
                                    pinError = false
                                }
                            },
                            label = { Text("관리자 PIN 번호", fontSize = 11.sp) },
                            placeholder = { Text("4자리 숫자", fontSize = 11.sp) },
                            singleLine = true,
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                            ),
                            isError = pinError,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (pinError) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("⚠️ PIN 번호가 일치하지 않습니다.", color = Color.Red, fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (enteredPin == "7788" || enteredPin == "0000") {
                                showPinDialog = false
                                showAdminCenter = true
                            } else {
                                pinError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("인증 및 관리자 진입 🔓", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }

        // 🏛️ 마스터 관리자 심사 센터 팝업
        if (showAdminCenter) {
            com.jangnal.gaja.ui.components.MerchantAdminCenterDialog(
                verifications = verifications,
                onUpdateStatus = onUpdateVerificationStatus,
                onDismiss = { showAdminCenter = false }
            )
        }
    }
}

@Composable
private fun MyActivitySummaryCard(
    favoriteCount: Int,
    stampCount: Int,
    totalMarketCount: Int,
    onOpenPassport: () -> Unit,
    onOpenCommunity: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, JangnalBrown.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = JangnalYellow.copy(alpha = 0.35f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🧺", fontSize = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "나의 장날가자 활동",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = JangnalBrown
                        )
                        Text(
                            text = "전국 전통시장·5일장 알뜰 장보기 파트너",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3칸 통계 바
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BackgroundCream.copy(alpha = 0.75f),
                border = BorderStroke(0.5.dp, JangnalBrown.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryStatColumn("❤️ 단골 시장", "${favoriteCount}곳")
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(JangnalBrown.copy(alpha = 0.12f))
                    )
                    SummaryStatColumn("🎖️ 장날도장", "${stampCount}곳")
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(JangnalBrown.copy(alpha = 0.12f))
                    )
                    SummaryStatColumn("🏪 전국 시장", "${totalMarketCount}곳")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 바로가기 버튼 2개
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPassport,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    border = BorderStroke(1.dp, JangnalOrange.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "🎖️ 내 도장여권 열기",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = JangnalOrange,
                        maxLines = 1
                    )
                }
                OutlinedButton(
                    onClick = onOpenCommunity,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    border = BorderStroke(1.dp, JangnalBrown.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "💬 전국 동네마당",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = JangnalBrown,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryStatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = JangnalBrown
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = JangnalBrown.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 1.5.dp,
            border = BorderStroke(0.8.dp, JangnalBrown.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
    }
}

@Composable
private fun SettingsRowItem(
    iconEmoji: String,
    iconBg: Color,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    badgeColor: Color = JangnalOrange,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIconBadge(iconEmoji, iconBg)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = JangnalBrown,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = JangnalOrange
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = JangnalBrown.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsIconBadge(emoji: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 17.sp)
    }
}

@Composable
private fun SettingsDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = JangnalBrown.copy(alpha = 0.08f),
        thickness = 0.8.dp
    )
}

@Composable
private fun SettingsDetailDialog(
    modal: SettingsDetailModal,
    context: Context,
    onDismiss: () -> Unit
) {
    val (title, bodyText) = when (modal) {
        SettingsDetailModal.APP_GUIDE -> {
            "🏪 '장날가자' 핵심 기능 & 활용 가이드" to (
                "복잡한 5일장 날짜 계산과 전국 전통시장 장보기 정보를 한눈에 확인하세요!\n\n" +
                "☀️ 오늘 개장 & 주말 개장 5일장 필터\n" +
                "• [지도] 및 [시장 목록]에서 '오늘 개장' 또는 '이번 주말 개장'을 누르면 열리는 장만 골라볼 수 있습니다.\n" +
                "• 시장별 운영 시간(5일장 통상 07:00~18:00, 상설시장 08:00~20:00)에 맞춰 실시간 영업중·파장 주의·영업 종료 상태가 자동 반영됩니다.\n\n" +
                "🎫 전국 79,500+ 온누리상품권 공식 가맹점 수록\n" +
                "• 소상공인시장진흥공단 공식 데이터를 기반으로 각 시장 내 실제 온누리 가맹 점포(지류·카드·모바일)를 검색하고 업종별로 모아볼 수 있습니다.\n\n" +
                "💬 전국 동네마당 & 한줄평 리뷰\n" +
                "• 별도 회원가입 없이 닉네임만으로 장바구니 인증, 맛집 후기, 현장 대기줄 제보를 이웃과 나눌 수 있습니다.\n\n" +
                "🅿️ 원터치 내비게이션 길찾기\n" +
                "• 시장 상세 화면의 '📍 길찾기 안내' 버튼으로 카카오맵·네이버지도·티맵 길안내를 바로 실행할 수 있습니다."
            )
        }
        SettingsDetailModal.FIVE_DAY_MARKET_INFO -> {
            "🍎 5일장이란 무엇인가요?" to (
                "5일장은 조선시대부터 이어져 온 대한민국 고유의 정기 전통시장입니다.\n\n" +
                "📅 장날 계산하는 방법\n" +
                "• 5일 간격으로 장이 열리며, 매달 날짜의 끝자리 숫자를 기준으로 열립니다.\n\n" +
                "• [1·6일장]: 매월 1일, 6일, 11일, 16일, 21일, 26일, 31일\n" +
                "• [2·7일장]: 매월 2일, 7일, 12일, 17일, 22일, 27일\n" +
                "• [3·8일장]: 매월 3일, 8일, 13일, 18일, 23일, 28일\n" +
                "• [4·9일장]: 매월 4일, 9일, 14일, 19일, 24일, 29일\n" +
                "• [5·10일장]: 매월 5일, 10일, 15일, 20일, 25일, 30일\n\n" +
                "💡 방문 팁\n" +
                "• 5일장 난전과 노점은 주로 오전 7시~오후 5시 사이에 가장 활발하며, 오후 5시 이후에는 파장 준비로 일찍 닫는 점포가 많으니 오전~오후 이른 시간 방문을 추천합니다!"
            )
        }
        SettingsDetailModal.ONNURI_GUIDE -> {
            "🎫 온누리상품권 가맹점 이용 안내" to (
                "전통시장과 상점가 지정 가맹점에서 현금처럼 사용할 수 있는 온누리상품권 종류별 안내입니다.\n\n" +
                "💳 1. 카드형 온누리상품권\n" +
                "• '온누리상품권' 공식 앱 설치 후 본인의 신용/체크카드를 등록하고 금액을 충전하면, 가맹점에서 등록된 카드로 결제할 때 자동 차감됩니다.\n\n" +
                "📱 2. 모바일(QR) 온누리상품권\n" +
                "• 비플페이, 온누리페이 등 지정 앱에서 충전 후 가맹점의 QR코드를 스캔해 결제합니다.\n\n" +
                "💵 3. 지류(종이) 온누리상품권\n" +
                "• 시중 지정 은행에서 구매할 수 있으며, 권면 금액의 60% 이상 구매 시 잔액을 현금으로 돌려받을 수 있습니다.\n\n" +
                "🏪 장날가자에서 가맹점 확인하기\n" +
                "• 각 시장 상세 화면의 상점 목록에서 소상공인시장진흥공단 공식 데이터 기반의 [🎫 온누리 공식가맹 (지류·카드·모바일)] 배지로 취급 여부를 바로 확인할 수 있습니다.\n" +
                "• 충전 할인율 및 한도는 정부(중소벤처기업부) 정책 및 시기에 따라 달라질 수 있으니 공식 앱에서 확인해 주세요."
            )
        }
        SettingsDetailModal.MERCHANT_PARTNERSHIP -> {
            "🏛️ 전통시장 상인 스마트상점 & 입점 안내" to (
                "전통시장 및 5일장 상인 여러분의 온라인 홍보와 단골 확보를 '장날가자'가 함께 돕습니다.\n\n" +
                "1️⃣ 무료 상인 공식 인증 & 오늘 마감 특가 등록\n" +
                "• 시장 상세 화면 내 본인 점포에서 [상인 인증 신청]을 완료하시면 '✓ 상인인증' 공식 마크가 부여되며, 매일 [🔥 오늘 마감 특가]를 무료로 등록해 방문객에게 홍보하실 수 있습니다.\n\n" +
                "2️⃣ 스마트상점 기술보급사업 연계 안내\n" +
                "• 소상공인시장진흥공단 스마트상점 지원사업 및 디지털 온누리 가맹점 홍보 연동에 관한 제휴 문의는 아래 이메일로 편하게 연락해 주세요."
            )
        }
        SettingsDetailModal.PRIVACY_POLICY -> {
            "🔒 개인정보 처리방침 및 UGC 이용 정책" to (
                "1. 개인정보의 수집 및 처리 목적\n" +
                "• '장날가자'는 별도 회원가입 없이 누구나 이용할 수 있으며, 주민등록번호·실명 등 불필요한 개인정보를 수집하지 않습니다.\n" +
                "• 동네마당 및 상점 한줄평 작성 시 사용자가 입력한 닉네임, 후기 내용, 첨부 사진은 서비스 내 게시를 위해 저장됩니다.\n\n" +
                "2. 위치 정보의 사용\n" +
                "• 내 주변 시장 탐색, 거리순 정렬, 방문 도장 및 현장 제보 확인을 위해 단말기 위치 정보를 사용하며, 위치 좌표는 외부 서버에 무단 수집·보관되지 않습니다.\n\n" +
                "3. 악성 이용자 제재 및 안전 정책 (UGC)\n" +
                "• 불법 스팸, 욕설/비방, 개인정보 노출 게시글을 방지하기 위해 금칙어 필터와 신고·차단 기능을 운영하며, 악성 이용자 차단 처리를 위해 역추적이 불가능한 익명 해시값(SHA-256)만을 사용합니다.\n\n" +
                "4. 이용자 권리 및 삭제\n" +
                "• 이용자는 본인이 작성한 게시글 및 리뷰를 언제든 삭제하거나 고객센터(collcokorea@gmail.com)를 통해 파기를 요청할 수 있습니다."
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = JangnalBrown
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = bodyText,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (modal == SettingsDetailModal.MERCHANT_PARTNERSHIP) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            sendEmail(context, "[장날가자] 전통시장 상인 스마트상점 도입 및 입점 문의")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JangnalOrange)
                    ) {
                        Text("상인 파트너십 이메일 문의하기 📝", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인", fontWeight = FontWeight.Bold, color = JangnalBrown)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(18.dp)
    )
}

private fun sendEmail(context: Context, subject: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:collcokorea@gmail.com?subject=" + Uri.encode(subject))
    }
    try {
        context.startActivity(emailIntent)
    } catch (_: Exception) {
        Toast.makeText(context, "이메일 앱을 열 수 없습니다. collcokorea@gmail.com 으로 문의해 주세요.", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun SizeButton(
    text: String,
    scale: Float,
    currentScale: Float,
    onScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = (Math.abs(currentScale - scale) < 0.01f)
    Button(
        onClick = { onScaleChange(scale) },
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) JangnalBrown else BackgroundCream,
            contentColor = if (isSelected) JangnalYellow else JangnalBrown
        ),
        border = if (!isSelected) BorderStroke(1.dp, JangnalBrown.copy(alpha = 0.25f)) else null
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            maxLines = 1,
            softWrap = false,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}
