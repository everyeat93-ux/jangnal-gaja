package com.jangnal.gaja.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jangnal.gaja.data.local.entity.Market
import com.jangnal.gaja.data.repository.CourseSpot
import com.jangnal.gaja.data.repository.MarketTravelCourse
import com.jangnal.gaja.data.repository.MarketTravelCourseRepository

/**
 * 🗺️ 시장 중심 로컬 당일/1박2일 추천 여행 코스 섹션
 */
@Composable
fun MarketTravelCourseSection(
    market: Market,
    userLocation: android.location.Location? = null
) {
    val context = LocalContext.current
    val courses = remember(market.id) {
        MarketTravelCourseRepository.getTravelCoursesForMarket(market)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🗺️", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "시장 연계 로컬 추천 코스",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "당일 힐링",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "💡 장날 맛있는 먹거리와 함께 둘러보기 좋은 반경 10km 이내 알짜배기 여행 코스입니다.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        courses.forEach { course ->
            CourseCard(
                course = course,
                context = context,
                userLocation = userLocation
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CourseCard(
    course: MarketTravelCourse,
    context: Context,
    userLocation: android.location.Location?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, Color(0xFFFFB74D).copy(alpha = 0.6f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Course Title & Theme Chips
            Text(
                text = course.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Theme Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CourseBadge(text = course.theme, bgColor = Color(0xFFFFF3E0), textColor = Color(0xFFE65100))
                CourseBadge(text = course.durationText, bgColor = Color(0xFFE8F5E9), textColor = Color(0xFF2E7D32))
                CourseBadge(text = course.targetAudience, bgColor = Color(0xFFE1F5FE), textColor = Color(0xFF0277BD))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = course.summary,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.height(14.dp))

            // Timeline Spot Items
            course.spots.forEachIndexed { index, spot ->
                TimelineSpotItem(
                    spot = spot,
                    isLast = index == course.spots.size - 1,
                    onNavigate = {
                        MarketTravelCourseRepository.launchNavigationToSpot(
                            context = context,
                            spotName = spot.searchQuery,
                            lat = null,
                            lon = null
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-stop Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val shareText = buildString {
                            appendLine("🗺️ [장날가자] ${course.title}")
                            appendLine("⏱️ 소요: ${course.durationText} · 테마: ${course.theme}")
                            appendLine()
                            course.spots.forEach { s ->
                                appendLine("${s.order}️⃣ ${s.name} (${s.timeEstimate})")
                                appendLine("   • ${s.highlight}")
                            }
                            appendLine()
                            appendLine("전국의 정겨운 장날 여행 코스를 함께 떠나요! #장날가자 #5일장여행")
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "여행 코스 공유"))
                    },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("코스 공유", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        MarketTravelCourseRepository.launchMultiRouteNavigation(context, course)
                    },
                    modifier = Modifier.weight(1.6f).height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("🧭 전체 동선 지도 길안내", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TimelineSpotItem(
    spot: CourseSpot,
    isLast: Boolean,
    onNavigate: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Left Column: Step Circle + Vertical Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = when (spot.category) {
                    "전통시장" -> Color(0xFFE65100)
                    "자연·힐링" -> Color(0xFF2E7D32)
                    "카페·디저트" -> Color(0xFF6D4C41)
                    "문화·체험" -> Color(0xFF5E35B1)
                    else -> Color(0xFF0277BD)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${spot.order}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(84.dp)
                        .background(Color(0xFFFFB74D).copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right Column: Spot Detail Box
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = spot.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = spot.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Small Navigation Button
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.clickable { onNavigate() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "길안내",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⏱️ ${spot.timeEstimate}",
                    fontSize = 11.sp,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Medium
                )
                if (spot.travelFromPrev != "여행 출발지") {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${spot.travelFromPrev}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "✨ ${spot.highlight}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 15.sp
            )

            if (spot.tip.isNotEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFFDE7),
                    border = BorderStroke(0.5.dp, Color(0xFFFFF59D))
                ) {
                    Text(
                        text = "💡 꿀팁: ${spot.tip}",
                        fontSize = 10.sp,
                        color = Color(0xFF795548),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseBadge(text: String, bgColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
