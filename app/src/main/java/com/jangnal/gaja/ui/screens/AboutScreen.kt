package com.jangnal.gaja.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onDismiss: () -> Unit,
    currentScale: Float,
    onScaleChange: (Float) -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // 전체 화면 사용
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("앱 정보 및 도움말") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = com.jangnal.gaja.ui.theme.JangnalYellow,
                        titleContentColor = com.jangnal.gaja.ui.theme.JangnalBrown,
                        navigationIconContentColor = com.jangnal.gaja.ui.theme.JangnalBrown
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 0. 글자 크기 설정
                TextSizeSettingCard(currentScale, onScaleChange)

                // 1. 5일장이란?
                InfoCard(
                    title = "🍎 5일장이란 무엇인가요?",
                    content = "5일장은 조선시대부터 이어져 온 우리의 전통 시장입니다. \n\n" +
                            "5일 간격으로 장이 열린다는 뜻으로, 예를 들어 '1, 6일장'이라면 매달 날짜의 끝자리가 1일과 6인 날에 열립니다.\n\n" +
                            "예) 1일, 6일, 11일, 16일, 21일, 26일, 31일"
                )

                // 2. 서비스 소개
                InfoCard(
                    title = "📱 '장날가자' 서비스 소개",
                    content = "복잡한 장날 계산과 시장 장보기 정보, 이제 '장날가자'가 편리하게 안내합니다!\n\n" +
                            "'장날가자'는 전국 방방곡곡의 5일장과 전통시장 정보를 쉽고 즐겁게 탐색할 수 있는 전통시장 지도 및 라이프 플랫폼 서비스입니다.\n\n" +
                            "✅ 시장별 실시간 맛집 대기줄 확인 및 혼잡도 제보\n" +
                            "✅ 사진 및 하트 평점이 결합된 생생한 맛집 한줄평 갤러리\n" +
                            "✅ 국산 3대 내비게이션(네이버, 카카오, 티맵) 즉시 연동 및 길찾기\n" +
                            "✅ 단골 등록 시장 장날 아침 개장 알람 자동 수신\n" +
                            "✅ 화장실, 주차장 등 편의시설 유무 직접 제보\n" +
                            "✅ '오늘 개장' 필터를 통한 직관적인 오늘 장날 탐색"
                )

                // 3. 사용 팁
                InfoCard(
                    title = "💡 알뜰 사용 팁",
                    content = "• [시장 목록] 탭에서 상단 '오늘 개장' 칩을 켜면 오늘 여는 시장만 선별하여 볼 수 있습니다.\n" +
                            "• [지도] 탭은 위치 정보를 켜면 내 위치 중심의 시장들로 바로 자동 포커싱됩니다.\n" +
                            "• 가게 우측의 [한줄평 💬]을 누르면 사진과 하트 평점을 남겨 제보할 수 있으며, 용량 최적화 필터가 작동하여 데이터 걱정 없이 가볍게 공유됩니다.\n" +
                            "• [길찾기] 버튼을 누르면 스마트폰에 설치된 지도 앱(네이버, 카카오, 티맵)으로 즉시 길안내를 연결해 줍니다.\n" +
                            "• 시장 상세페이지 내의 편의시설 카드를 직접 탭하여 실제 존재 유무를 제보할 수 있습니다.\n" +
                            "• 각 가게들의 '대기줄 현황 제보'는 40분 뒤 자동 만료되어 가장 생생한 찐정보만 제공합니다."
                )

                // 4. 스마트상점 & 상인 입점 파트너십
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🏪 전통시장 상인 전용: 스마트상점 국비 지원 안내",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "소상공인시장진흥공단 '스마트상점 기술보급사업'을 통해 '장날가자 스마트 웨이팅/오더 시스템' 도입 시 최대 70%(최대 700만 원)를 국비로 보조받으실 수 있습니다.\n\n" +
                                    "• 대상: 전국 5일장 및 전통시장 등록 점포\n" +
                                    "• 혜택: 카카오톡 웨이팅 알림톡, 모바일 포장 주문, 디지털 온누리 가맹 검증 대시보드 무상 연동",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:support@collco.co.kr?subject=" + Uri.encode("[장날가자] 전통시장 상인 스마트상점 도입 문의"))
                                }
                                try { context.startActivity(emailIntent) } catch (_: Exception) {}
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("스마트상점 도입 & 상점 입점 문의 📝", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 4. 개인정보처리방침
                InfoCard(
                    title = "🔒 개인정보 처리방침",
                    content = "1. 개인정보의 처리 목적\n" +
                            "'장날가자'는 사용자의 개인정보를 별도로 수집, 저장, 전송하지 않습니다.\n\n" +
                            "2. 위치 정보의 사용\n" +
                            "가까운 시장 찾기 및 지도 표시, 그리고 대기줄 제보의 현장 인증을 위해 위치 정보를 사용합니다. 이 정보는 기기 내부에서만 일시적으로 처리되며 외부 서버로 전송되거나 저장되지 않습니다.\n\n" +
                            "3. 앱 권한 안내\n" +
                            "- 위치: 내 주변 시장 찾기, 지도 이동 및 현장인증 제보\n" +
                            "- 전화: 전통시장 관리실 전화 연결\n\n" +
                            "본 앱은 사용자의 프라이버시를 최우선으로 보호합니다."
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "버전 1.1.9 | 만든이: 콜코(COLLCO)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun InfoCard(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TextSizeSettingCard(currentScale: Float, onScaleChange: (Float) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "🔎 글자 크기 조절",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SizeButton("기본", 1.0f, currentScale, onScaleChange, Modifier.weight(1f))
                SizeButton("크게", 1.25f, currentScale, onScaleChange, Modifier.weight(1f))
                SizeButton("왕크게", 1.5f, currentScale, onScaleChange, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SizeButton(text: String, scale: Float, currentScale: Float, onScaleChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    val isSelected = (Math.abs(currentScale - scale) < 0.01f)
    Button(
        onClick = { onScaleChange(scale) },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.Gray) else null
    ) {
        Text(text, fontSize = 14.sp * scale, maxLines = 1) 
    }
}
