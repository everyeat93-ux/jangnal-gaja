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

                // 1. 서비스 소개 & 핵심 기능 가이드
                InfoCard(
                    title = "🏪 '장날가자' v2.0.0 이용 가이드 & 주요 기능",
                    content = "복잡한 장날 계산과 전통시장 장보기 정보, 이제 '장날가자' 하나로 편리하게 확인하세요!\n\n" +
                            "☀️ 오늘 개장 & 🚗 주말 개장 5일장 모아보기\n" +
                            "• '오늘 개장' 및 '이번 주말 개장' 필터 칩을 누르면 오늘 또는 이번 주말에 열리는 전국의 5일장을 1초 만에 쏙쏙 골라볼 수 있습니다.\n\n" +
                            "🎫 온누리상품권 10% 공식 가맹점 & 결제수단 확인\n" +
                            "• 상점 카드에서 소진공 공식 온누리 가맹 뱃지(지류·카드·모바일)와 카드결제 가능 여부를 미리 확인할 수 있습니다.\n" +
                            "• 10% 선할인 온누리상품권으로 물가를 절약하고 알뜰하게 장을 보세요!\n\n" +
                            "⭐ 상점별 평균 별점 & 결제인증 한줄평\n" +
                            "• 방문객들이 직접 남긴 솔직한 별점과 결제수단(온누리/카드/현금) 인증 리뷰를 확인하고, 내 방문 후기와 사진을 공유할 수 있습니다.\n\n" +
                            "🏪 상점 직접 등록 & 10분 내 실수 취소 · 폐업 제보(🚨)\n" +
                            "• 시장 내 숨은 맛집과 상점을 직접 등록할 수 있으며, 10분 이내 실수 등록 취소 및 폐업/정보오류 제보 시스템을 지원합니다.\n\n" +
                            "⏱️ 실시간 대기줄 제보 & AI 혼잡도 예측\n" +
                            "• 줄 서는 인기 맛집의 실시간 대기열(한산/보통/혼잡)을 확인하고, 현장 상황을 제보해 이웃들과 나눌 수 있습니다.\n" +
                            "• 제보가 없어도 AI가 시간대와 요일별 평시/피크 예상 대기시간을 친절히 안내합니다.\n\n" +
                            "🅿️ 공영주차장 & 원터치 내비게이션 길안내\n" +
                            "• 시장 상세 화면 하단 '📍 길찾기 안내' 버튼으로 네이버 지도, 카카오맵, 티맵 앱을 통해 시장 및 주변 공영주차장으로 바로 안내받으실 수 있습니다.\n\n" +
                            "🛒 장날 LIVE DROP 로컬 명물 밀키트 배송\n" +
                            "• 전국 5일장/전통시장의 대표 명물 먹거리를 온누리 10% 할인 혜택으로 집에서 신선하게 택배 주문할 수 있습니다."
                )

                // 2. 알뜰 사용 팁
                InfoCard(
                    title = "💡 200% 알뜰 활용 팁",
                    content = "• [지도] 탭에서 위치 권한을 켜면 내 위치 중심의 가까운 시장들로 바로 자동 이동합니다.\n" +
                            "• 마음에 드는 시장의 ❤️ 하트를 눌러 단골로 등록해 두시면 다음 장날 아침에 알림을 받아보실 수 있습니다.\n" +
                            "• 상점의 [💬 한줄평 · 결제인증]을 누르면 사진과 평점, 사용하신 결제수단을 손쉽게 인증할 수 있으며, 사진 용량 최적화 필터가 작동하여 데이터 걱정 없이 가볍게 공유됩니다.\n" +
                            "• 각 가게들의 '대기줄 현황 제보'는 40분 뒤 자동 만료되어 가장 최신의 생생한 현장 정보만 유지됩니다."
                )

                // 3. 개발자 문의 및 제보 센터 (1:1 소통)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "문의",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "💌 개발자 문의 및 제보 센터",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "'장날가자'는 사용자 여러분의 소중한 제보와 피드백으로 매주 발전하고 있습니다. 기능 개선 요청이나 새로운 5일장 제보, 버그 신고를 언제든 편하게 보내주세요!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // 제보 버튼 3종 세트
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:collcokorea@gmail.com?subject=" + Uri.encode("[장날가자] 기능 개선 / 건의사항 제안"))
                                    }
                                    try { context.startActivity(emailIntent) } catch (_: Exception) {}
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text("💡 기능 제안", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:collcokorea@gmail.com?subject=" + Uri.encode("[장날가자] 새로운 5일장 / 시장 정보 제보"))
                                    }
                                    try { context.startActivity(emailIntent) } catch (_: Exception) {}
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text("🏪 시장 제보", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:collcokorea@gmail.com?subject=" + Uri.encode("[장날가자] 버그 및 오류 신고"))
                                    }
                                    try { context.startActivity(emailIntent) } catch (_: Exception) {}
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text("🐞 버그 신고", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 4. 스마트상점 & 상인 입점 파트너십
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "🏪 전통시장 상인 전용: 스마트상점 국비 지원 안내",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "소상공인시장진흥공단 '스마트상점 기술보급사업'을 통해 '장날가자 스마트 웨이팅/오더 시스템' 도입 시 최대 70%(최대 700만 원)를 국비로 보조받으실 수 있습니다.\n\n" +
                                    "• 대상: 전국 5일장 및 전통시장 등록 점포\n" +
                                    "• 혜택: 카카오톡 웨이팅 알림톡, 모바일 포장 주문, 디지털 온누리 가맹 검증 대시보드 무상 연동",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:collcokorea@gmail.com?subject=" + Uri.encode("[장날가자] 전통시장 상인 스마트상점 도입 및 입점 문의"))
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

                // 5. 5일장이란?
                InfoCard(
                    title = "🍎 5일장이란 무엇인가요?",
                    content = "5일장은 조선시대부터 이어져 온 우리의 전통 시장입니다.\n\n" +
                            "5일 간격으로 장이 열린다는 뜻으로, 예를 들어 '1, 6일장'이라면 매달 날짜의 끝자리가 1일과 6인 날에 열립니다.\n\n" +
                            "예) 1일, 6일, 11일, 16일, 21일, 26일, 31일"
                )

                // 6. 개인정보처리방침
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
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "장날가자 v2.0.0 | 상호명: 콜코(COLLCO) | 대표자: 김문정",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "사업자등록번호: 850-64-00732 | 통신판매업: 2024-서울구로-0598",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "고객센터/제휴문의: collcokorea@gmail.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
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
