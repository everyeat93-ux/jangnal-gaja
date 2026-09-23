package com.jangnal.gaja.data.repository

import com.jangnal.gaja.data.local.entity.Shop

/**
 * 전국 983개 대표 전통시장 100% 실데이터 오프라인 큐레이션 공급자
 * OnnuriAssetLoader를 통해 소상공인시장진흥공단 공식 온누리 가맹점을 1:1 정밀 로드합니다.
 */
object CuratedShopsData {
    fun getCuratedShops(
        marketId: Long,
        marketName: String = "",
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): List<Shop> {
        return OnnuriAssetLoader.getShopsForMarket(
            context = null,
            marketId = marketId,
            marketName = marketName,
            roadAddress = "",
            jibunAddress = "",
            latitude = latitude,
            longitude = longitude
        ) ?: emptyList()
    }
}
