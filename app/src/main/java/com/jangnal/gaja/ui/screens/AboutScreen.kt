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
                    title = "🏪 '장날가자' 이용 가이드 & 주요 기능",
                    content = "복잡한 장날 계산과 전국 전통시장 장보기 정보, 이제 '장날가자' 하나로 편리하게 확인하세요!\n\n" +
                            "☀️ 오늘 개장 & 🚗 주말 개장 5일장 모아보기\n" +
                            "• '오늘 개장' 및 '이번 주말 개장' 필터 칩을 누르면 오늘 또는 이번 주말에 열리는 전국의 5일장을 1초 만에 쏙쏙 골라볼 수 있습니다.\n\n" +
                            "💬 전국 동네마당 & 실시간 댓글 소통 (신규!)\n" +
                            "• 메인 상단 [💬 동네마당] 버튼을 누르면 전국 1,400개 전통시장의 생생한 꿀팁, 장바구니 인증, 축제 소식을 실시간 피드로 한눈에 볼 수 있습니다.\n" +
                            "• 각 시장 상세 화면에서 이웃 및 상인들과 댓글/답글로 정겨운 질문과 답변을 나눌 수 있습니다.\n\n" +
                            "🎪 1:1 고유 축제 · 문화공연 · 야시장 실데이터 연동 (신규!)\n" +
                            "• 한국관광공사 TourAPI 공공데이터와 1:1 연동되어 각 시장에서 열리는 실제 축제, 먹거리 페스타, 야시장, 노래자랑 일정을 놓치지 않고 확인하세요.\n\n" +
                            "🎫 온누리상품권 10% 공식 가맹점 & 결제수단 확인\n" +
                            "• 상점 카드에서 소진공 공식 온누리 가맹 뱃지(지류·카드·모바일)와 카드결제 가능 여부를 미리 확인할 수 있습니다.\n" +
                            "• 10% 선할인 온누리상품권으로 물가를 절약하고 알뜰하게 장을 보세요!\n\n" +
                            "⭐ 상점별 평균 별점 & 결제인증 한줄평\n" +
                            "• 방문객들이 직접 남긴 솔직한 별점과 결제수단(온누리/카드/현금) 인증 리뷰를 확인하고, 내 방문 후기와 사진을 공유할 수 있습니다.\n\n" +
                            "🏪 상점 직접 등록 & 10분 내 실수 취소 · 폐업 제보(🚨)\n" +
                            "• 시장 내 숨은 맛집과 상점을 직접 등록할 수 있으며, 10분 이내 실수 등록 취소 및 폐업/정보오류 제보 시스템을 지원합니다.\n\n" +
                            "⏱️ 실시간 대기줄 제보 & AI 혼잡도 예측\n" +
                            "• 줄 서는 인기 맛집의 실시간 대기열(한산/보통/혼잡)을 확인하고, 현장 상황을 제보해 이웃들과 나눌 수 있습니다.\n\n" +
                            "🅿️ 공영주차장 & 원터치 내비게이션 길안내\n" +
                            "• 시장 상세 화면 하단 '📍 길찾기 안내' 버튼으로 네이버 지도, 카카오맵, 티맵 앱을 통해 시장 및 주변 공영주차장으로 바로 안내받으실 수 있습니다."
                )

                // 2. 알뜰 사용 팁
                InfoCard(
                    title = "💡 200% 알뜰 활용 팁",
                    content = "• [지도] 탭에서 위치 권한을 켜면 내 위치 중심의 가까운 시장들로 바로 자동 이동합니다.\n" +
                            "• 마음에 드는 시장의 ❤️ 하트를 눌러 단골로 등록해 두시면 다음 장날 아침에 알림을 받아보실 수 있습니다.\n" +
                            "• 상점의 [💬 한줄평 · 결제인증]이나 [💬 동네마당]을 이용할 때 로그인 없이 닉네임만으로 간편하게 사진과 글을 공유할 수 있습니다.\n" +
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
                    title = "🔒 개인정보 처리방침 및 UGC 이용 정책",
                    content = "1. 개인정보의 수집 및 처리 목적\n" +
                            "• '장날가자'는 회원가입 없이 익명/닉네임 기반으로 누구나 편리하게 이용할 수 있으며, 불필요한 주민번호, 실명, 연락처 등의 개인정보를 수집하지 않습니다.\n" +
                            "• 동네마당 커뮤니티 및 상점 리뷰 작성 시 사용자가 자발적으로 입력한 닉네임, 본문 내용, 첨부 사진은 서비스 제공 및 다른 이용자와의 정보 공유를 위해 서버에 저장·게시됩니다.\n\n" +
                            "2. 위치 정보의 사용\n" +
                            "• 가까운 5일장 찾기, 지도 탐색, 대기줄/방문 현장인증을 위해 단말기 위치 정보를 활용합니다. 이 정보는 단말기 내부에서만 일시적으로 처리되며 외부 서버로 저장되지 않습니다.\n\n" +
                            "3. 악성 이용자 제재 및 단말 식별자 해시 처리 (안전 정책)\n" +
                            "• 구글 플레이의 사용자 생성 콘텐츠(UGC) 정책에 따라 불법 광고, 욕설/비방, 음란물 등의 악성 게시글을 차단하기 위해 단말기 고유 식별값을 단방향 암호화(SHA-256 Hash)하여 보관합니다.\n" +
                            "• 원본 단말 식별자는 역추적할 수 없으며, 오직 누적 신고 3회 시 자동 블라인드 및 작성자 차단 기능 목적으로만 안전하게 사용됩니다.\n\n" +
                            "4. 게시글 삭제 및 이용자 권리\n" +
                            "• 사용자는 본인이 작성한 게시글 및 댓글을 언제든지 직접 즉시 삭제할 수 있으며, 삭제 즉시 서버에서도 파기됩니다.\n\n" +
                            "5. 앱 접근 권한 안내\n" +
                            "• 위치: 내 주변 5일장 찾기 및 지도 길안내\n" +
                            "• 사진/미디어: 동네마당 및 상점 리뷰 사진 첨부\n" +
                            "• 전화: 전통시장 관리사무소 전화 걸기 연결\n\n" +
                            "본 앱은 사용자의 프라이버시와 따뜻한 커뮤니티 문화를 최우선으로 보호합니다."
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
