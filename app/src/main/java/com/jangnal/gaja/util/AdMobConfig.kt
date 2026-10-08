package com.jangnal.gaja.util

import com.jangnal.gaja.BuildConfig

/**
 * Google AdMob Central Configuration
 * 
 * Publisher ID: pub-5159285704643995
 */
object AdMobConfig {
    // Google 공식 테스트 배너 ID (개발/디버그 시 안전하게 사용)
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    
    // 사용자의 실제 애드몹 배너 광고 단위 ID
    const val PROD_BANNER_ID = "ca-app-pub-5159285704643995/1218721006"

    /**
     * 현재 빌드 상태에 맞춰 광고 단위 ID를 반환합니다.
     * - Debug 빌드: Google 안전 테스트 배너 ID (자가 클릭 방지 및 계정 정지 보호)
     * - Release 빌드: 실제 상용 배너 ID (실제 광고 및 수익 발생)
     */
    fun getBannerAdUnitId(): String {
        return if (BuildConfig.DEBUG) {
            TEST_BANNER_ID
        } else {
            PROD_BANNER_ID
        }
    }
}
