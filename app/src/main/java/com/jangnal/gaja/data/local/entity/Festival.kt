package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * 1:1 전통시장 고유 축제 및 문화행사 엔티티
 * (엉뚱한 인접 시장과 겹치지 않고 오직 지정된 시장에만 정확하게 매핑)
 */
@Entity(
    tableName = "festivals",
    foreignKeys = [
        ForeignKey(
            entity = Market::class,
            parentColumns = ["id"],
            childColumns = ["marketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("marketId")]
)
data class Festival(
    @PrimaryKey val id: String,           // 고유 ID (예: tour_12345 또는 user_event_98765)
    val marketId: Long,                   // 1:1 고유 매칭된 전통시장 ID
    val marketName: String,               // 시장 이름 (예: 구로시장)
    val title: String,                    // 축제/공연/행사 제목
    val category: String,                 // 축제, 문화공연, 야시장, 노래자랑, 할인행사
    val startDate: String,                // 시작일 (YYYY-MM-DD)
    val endDate: String,                  // 종료일 (YYYY-MM-DD)
    val posterUrl: String = "",           // 대표 포스터/사진 이미지 URL
    val venue: String = "",               // 상세 장소 (예: 시장 중앙통로 무대, 2번 게이트 앞)
    val description: String = "",         // 행사 상세 내용
    val hostOrg: String = "",             // 주최/주관 (예: 시장 상인회, 지자체)
    val isOfficial: Boolean = true,       // 공공데이터/지자체 공식 연동 여부
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getNormalizedStartDate(): String = startDate.replace("-", ".").trim()
    fun getNormalizedEndDate(): String = endDate.replace("-", ".").trim()

    fun isExpired(): Boolean {
        val end = getNormalizedEndDate()
        if (end.isEmpty()) return false
        val today = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.KOREA).format(java.util.Date())
        return end < today
    }

    fun isOngoing(): Boolean {
        val start = getNormalizedStartDate()
        val end = getNormalizedEndDate()
        val today = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.KOREA).format(java.util.Date())
        return (start.isEmpty() || start <= today) && (end.isEmpty() || today <= end)
    }

    fun getStatusText(): String {
        if (isExpired()) return "종료"
        if (isOngoing()) return "진행중 🔥"
        return "예정 ✨"
    }
}
