package com.jangnal.gaja.util

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar
import java.util.Locale

/**
 * 사용자 투표 및 제보 중복 방지 (1인 1회 일일 락킹 & 쿨다운 관리)
 */
object VoteTracker {
    private const val PREF_NAME = "jangnal_vote_tracker"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getTodayString(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    // 1. 시장 개장/폐장 투표 (오늘 열렸어요 / 닫혔어요)
    fun getMarketVoteStatus(context: Context, marketId: Long): Boolean? {
        val today = getTodayString()
        val key = "market_open_vote_${marketId}_$today"
        if (!getPrefs(context).contains(key)) return null
        return getPrefs(context).getBoolean(key, true)
    }

    fun setMarketVoteStatus(context: Context, marketId: Long, isOpen: Boolean) {
        val today = getTodayString()
        val key = "market_open_vote_${marketId}_$today"
        getPrefs(context).edit().putBoolean(key, isOpen).apply()
    }

    // 2. 편의시설 제보 (화장실, 주차장)
    fun hasReportedAmenity(context: Context, marketId: Long, amenityType: String): Boolean {
        val today = getTodayString()
        val key = "amenity_report_${marketId}_${amenityType}_$today"
        return getPrefs(context).getBoolean(key, false)
    }

    fun setAmenityReported(context: Context, marketId: Long, amenityType: String) {
        val today = getTodayString()
        val key = "amenity_report_${marketId}_${amenityType}_$today"
        getPrefs(context).edit().putBoolean(key, true).apply()
    }

    // 3. 상점 결제확인 / 리뷰 제보 (하루 1회)
    fun hasConfirmedPayment(context: Context, shopId: Long): Boolean {
        val today = getTodayString()
        val key = "shop_payment_confirmed_${shopId}_$today"
        return getPrefs(context).getBoolean(key, false)
    }

    fun setPaymentConfirmed(context: Context, shopId: Long) {
        val today = getTodayString()
        val key = "shop_payment_confirmed_${shopId}_$today"
        getPrefs(context).edit().putBoolean(key, true).apply()
    }

    // 4. 상점 대기줄 제보 (15분 쿨다운)
    fun getLastQueueReportTime(context: Context, shopId: Long): Long {
        val key = "shop_queue_report_time_$shopId"
        return getPrefs(context).getLong(key, 0L)
    }

    fun setQueueReported(context: Context, shopId: Long) {
        val key = "shop_queue_report_time_$shopId"
        getPrefs(context).edit().putLong(key, System.currentTimeMillis()).apply()
    }

    fun canReportQueue(context: Context, shopId: Long): Boolean {
        val lastTime = getLastQueueReportTime(context, shopId)
        val elapsed = System.currentTimeMillis() - lastTime
        return elapsed > 15 * 60 * 1000 // 15분 쿨다운
    }

    // 5. 내가 직접 등록한 상점 기록 (10분 이내 실수 삭제 가능)
    private fun normalizeShopKey(marketId: Long, shopName: String): String {
        val cleanName = shopName.replace("\\s+".toRegex(), "").lowercase(Locale.ROOT)
        return "my_created_shop_${marketId}_$cleanName"
    }

    fun recordMyCreatedShop(context: Context, marketId: Long, shopName: String) {
        val key = normalizeShopKey(marketId, shopName)
        getPrefs(context).edit().putLong(key, System.currentTimeMillis()).apply()
    }

    fun isMyCreatedShop(context: Context, marketId: Long, shopName: String): Boolean {
        val key = normalizeShopKey(marketId, shopName)
        return getPrefs(context).contains(key)
    }

    fun canDeleteMyCreatedShop(context: Context, marketId: Long, shopName: String): Boolean {
        val key = normalizeShopKey(marketId, shopName)
        val createdTime = getPrefs(context).getLong(key, 0L)
        if (createdTime == 0L) return false
        val elapsed = System.currentTimeMillis() - createdTime
        return elapsed <= 10 * 60 * 1000 // 10분 이내
    }

    fun getRemainingDeleteMinutes(context: Context, marketId: Long, shopName: String): Int {
        val key = normalizeShopKey(marketId, shopName)
        val createdTime = getPrefs(context).getLong(key, 0L)
        if (createdTime == 0L) return 0
        val elapsed = System.currentTimeMillis() - createdTime
        val remaining = (10 * 60 * 1000 - elapsed) / 60000
        return remaining.coerceAtLeast(0).toInt()
    }

    fun removeMyCreatedShop(context: Context, marketId: Long, shopName: String) {
        val key = normalizeShopKey(marketId, shopName)
        getPrefs(context).edit().remove(key).apply()
    }

    // 6. 상점 폐업 / 정보 오류 제보 (1인 1일 1상점 1회)
    fun hasReportedShopIssue(context: Context, marketId: Long, shopName: String): Boolean {
        val today = getTodayString()
        val cleanName = shopName.replace("\\s+".toRegex(), "").lowercase(Locale.ROOT)
        val key = "shop_issue_reported_${marketId}_${cleanName}_$today"
        return getPrefs(context).getBoolean(key, false)
    }

    fun setShopIssueReported(context: Context, marketId: Long, shopName: String) {
        val today = getTodayString()
        val cleanName = shopName.replace("\\s+".toRegex(), "").lowercase(Locale.ROOT)
        val key = "shop_issue_reported_${marketId}_${cleanName}_$today"
        getPrefs(context).edit().putBoolean(key, true).apply()
    }
}
