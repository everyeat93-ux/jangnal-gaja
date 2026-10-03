package com.jangnal.gaja.util

import com.jangnal.gaja.data.local.entity.Festival
import com.jangnal.gaja.data.local.entity.Market

/**
 * 전통시장 1:1 고유 축제 및 문화행사 정밀 매칭 헬퍼
 * (프로덕션 환경: 허구/가상/샘플 축제 데이터를 배제하고 실제 공식 검증된 데이터만 취급)
 */
object FestivalDataHelper {

    /**
     * 특정 시장(Market)에 1:1로 고유하게 귀속되는 공식 축제/공연/행사 목록 반환
     * (실제 서비스에서는 가상/샘플 데이터를 일절 반환하지 않고 빈 목록 반환)
     */
    fun getCuratedFestivalsForMarket(market: Market): List<Festival> {
        // 프로덕션 실서비스 원칙: 허위/가상 데이터 노출 방지를 위해 빈 목록 반환.
        // 실제 축제는 Firebase Firestore 실시간 관리자/지자체 등록 또는 TourAPI 1:1 검증 데이터만 노출됩니다.
        return emptyList()
    }

    /**
     * TourAPI 축제 데이터와 우리 전통시장이 1:1로 일치하는지 정밀 검증
     * (시/군/구 광역 축제가 인접 시장에 겹치거나 왜곡되는 것을 방지)
     */
    fun isTourApiMatchExact(market: Market, festivalTitle: String, festivalVenue: String, festivalAddress: String): Boolean {
        val rawMarketName = market.marketName
        val coreName = rawMarketName.replace("전통시장", "").replace("시장", "").replace("민속", "").replace("오일장", "").replace("5일장", "").trim()
        
        if (coreName.length < 2) return false

        // 축제 제목, 행사장소, 또는 주소에 해당 시장 이름이 명시되어 있는 경우에만 1:1 매칭 인정
        val combinedText = "$festivalTitle $festivalVenue $festivalAddress"
        return combinedText.contains(rawMarketName) || combinedText.contains("${coreName}시장") || combinedText.contains("${coreName}5일장")
    }
}
