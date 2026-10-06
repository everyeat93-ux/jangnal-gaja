package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a traditional market GPS visit stamp (장날 방문 스탬프)
 * Used in the "나의 장날 여권 (Passport)" feature.
 */
@Entity(tableName = "market_stamps")
data class MarketStamp(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val marketId: Long,
    val marketName: String,
    val visitTimestamp: Long = System.currentTimeMillis(),
    val isMarketDay: Boolean,         // 장날 당일 방문 여부 (황금 스탬프 vs 일반 방문 스탬프)
    val userMemo: String = "",        // 현장 한줄평 / 여행 메모
    val photoUri: String? = null,     // 현장 인증 사진 (로컬 Uri)
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val province: String = "전국"      // 시도 구분 (서울/경기/강원/충청/전라/경상/제주 등)
)
