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
    // 애드몹 콘솔에서 생성한 배너 광고 단위 ID로 지정됩니다.
    const val PROD_BANNER_ID = "ca-app-pub-5159285704643995/6300978111"

    /**
     * 현재 빌드 상태에 맞춰 광고 단위 ID를 반환합니다.
     */
    fun getBannerAdUnitId(): String {
        return if (BuildConfig.DEBUG) {
            TEST_BANNER_ID
        } else {
            // 프로덕션 배너 ID (미설정 시 테스트 ID로 안전하게 fallback)
            TEST_BANNER_ID
        }
    }
}
