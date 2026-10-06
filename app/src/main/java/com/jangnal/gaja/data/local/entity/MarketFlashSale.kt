package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a real-time market closing flash sale (장날 마감 타임세일 / 떨이)
 * Automatically expires after 2~4 hours to maintain 100% fresh data.
 */
@Entity(tableName = "market_flash_sales")
data class MarketFlashSale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val marketId: Long,
    val marketName: String,
    val shopName: String,
    val itemTitle: String,              // 품목명 (예: "갓 찧은 쑥인절미 3팩", "제철 꿀사과 1박스")
    val originalPrice: Int = 0,         // 기존 가격 (원)
    val discountPrice: Int = 0,         // 마감 할인가 (원)
    val quantityInfo: String = "",      // 잔여 수량 (예: "잔여 3세트", "선착순 5박스")
    val createdTimestamp: Long = System.currentTimeMillis(),
    val expireTimestamp: Long = System.currentTimeMillis() + (3 * 3600 * 1000), // 기본 3시간 뒤 만료
    val isVerifiedMerchant: Boolean = false // 상인회/상인 인증 여부
) {
    fun isExpired(nowMillis: Long = System.currentTimeMillis()): Boolean {
        return nowMillis > expireTimestamp
    }

    fun getRemainingMinutes(nowMillis: Long = System.currentTimeMillis()): Int {
        val diff = expireTimestamp - nowMillis
        return if (diff > 0) (diff / 60000).toInt() else 0
    }

    fun getDiscountPercentage(): Int {
        if (originalPrice <= 0 || discountPrice <= 0 || originalPrice <= discountPrice) return 0
        return ((originalPrice - discountPrice) * 100) / originalPrice
    }
}
