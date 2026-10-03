package com.jangnal.gaja.util

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.util.UUID

/**
 * 구글 플레이 UGC(User Generated Content) 정책 준수를 위한 커뮤니티 안전 관리자
 * 1) 비속어/욕설 및 불법 스팸 자동 차단
 * 2) 개인 계좌번호/전화번호 노출 차단
 * 3) 악성 사용자 로컬 차단(Block) 관리
 * 4) 커뮤니티 이용 가이드라인 동의 관리
 */
object CommunitySafetyHelper {

    private const val PREFS_NAME = "community_safety_prefs"
    private const val KEY_GUIDELINES_AGREED = "key_guidelines_agreed"
    private const val KEY_BLOCKED_AUTHORS = "key_blocked_authors"
    private const val KEY_DEVICE_HASH = "key_device_hash"

    // 금지어 / 비속어 / 스팸 키워드 리스트
    private val PROFANITY_KEYWORDS = listOf(
        "시발", "씨발", "개새", "병신", "존나", "지랄", "새끼", "미친놈", "미친년", "좆",
        "토토", "카지노", "바카라", "조건만남", "불법대출", "선불유심", "오피", "출장마사지",
        "수익보장", "리딩방", "코인투자", "텔레그램", "계좌이체요구"
    )

    // 계좌번호 패턴 (예: 110-123-456789 또는 연속된 11자리 이상 숫자)
    private val ACCOUNT_NUMBER_REGEX = Regex("\\b\\d{3,6}[-\\s]?\\d{2,6}[-\\s]?\\d{3,8}\\b")
    
    // 전화번호 패턴 (예: 010-1234-5678, 01012345678)
    private val PHONE_NUMBER_REGEX = Regex("01[016789][-\\s]?\\d{3,4}[-\\s]?\\d{4}")

    // 오픈채팅 / 외부 유도 링크 패턴
    private val LINK_REGEX = Regex("(open\\.kakao\\.com|t\\.me|bit\\.ly|cutt\\.ly)")

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 텍스트에 비속어, 스팸, 또는 개인 계좌/전화번호가 포함되어 있는지 검사
     * @return 위반 사유 문자열 (정상인 경우 null)
     */
    fun validateContent(text: String): String? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return "내용을 입력해 주세요."
        if (trimmed.length > 300) return "내용은 최대 300자까지 작성할 수 있습니다."

        // 1. 비속어/도박/불법 키워드 검사
        for (kw in PROFANITY_KEYWORDS) {
            if (trimmed.contains(kw, ignoreCase = true)) {
                return "부적절하거나 금지된 단어가 포함되어 있습니다: '$kw'"
            }
        }

        // 2. 계좌번호 및 금전 거래 유도 검사
        if (ACCOUNT_NUMBER_REGEX.containsMatchIn(trimmed) && trimmed.contains(Regex("(은행|계좌|입금|송금|계좌번호)"))) {
            return "사기 및 개인정보 보호를 위해 계좌번호나 금전 거래 유도 글은 등록할 수 없습니다."
        }

        // 3. 전화번호 노출 검사
        if (PHONE_NUMBER_REGEX.containsMatchIn(trimmed)) {
            return "개인정보 보호를 위해 휴대전화 번호는 입력하실 수 없습니다."
        }

        // 4. 외부 링크 유도 검사
        if (LINK_REGEX.containsMatchIn(trimmed)) {
            return "외부 메신저나 링크 유도는 금지되어 있습니다."
        }

        return null
    }

    /**
     * 커뮤니티 이용 가이드라인 동의 여부 확인
     */
    fun hasAgreedToGuidelines(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_GUIDELINES_AGREED, false)
    }

    /**
     * 커뮤니티 이용 가이드라인 동의 완료 처리
     */
    fun setAgreedToGuidelines(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_GUIDELINES_AGREED, true).apply()
    }

    /**
     * 기기 식별용 고유 해시 반환 (악성 사용자 식별 및 차단에 사용)
     */
    fun getDeviceIdHash(context: Context): String {
        val prefs = getPrefs(context)
        var hash = prefs.getString(KEY_DEVICE_HASH, null)
        if (hash == null) {
            val randomUuid = UUID.randomUUID().toString()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(randomUuid.toByteArray())
            hash = digest.fold("") { str, it -> str + "%02x".format(it) }.take(16)
            prefs.edit().putString(KEY_DEVICE_HASH, hash).apply()
        }
        return hash
    }

    /**
     * 특정 작성자를 차단했는지 확인
     */
    fun isAuthorBlocked(context: Context, authorDeviceIdHash: String): Boolean {
        val blockedSet = getPrefs(context).getStringSet(KEY_BLOCKED_AUTHORS, emptySet()) ?: emptySet()
        return blockedSet.contains(authorDeviceIdHash)
    }

    /**
     * 특정 작성자 차단 (해당 작성자의 모든 글이 내 화면에서 숨겨짐)
     */
    fun blockAuthor(context: Context, authorDeviceIdHash: String) {
        val prefs = getPrefs(context)
        val currentSet = prefs.getStringSet(KEY_BLOCKED_AUTHORS, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(authorDeviceIdHash)
        prefs.edit().putStringSet(KEY_BLOCKED_AUTHORS, currentSet).apply()
    }

    /**
     * 특정 작성자 차단 해제
     */
    fun unblockAuthor(context: Context, authorDeviceIdHash: String) {
        val prefs = getPrefs(context)
        val currentSet = prefs.getStringSet(KEY_BLOCKED_AUTHORS, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.remove(authorDeviceIdHash)
        prefs.edit().putStringSet(KEY_BLOCKED_AUTHORS, currentSet).apply()
    }

    /**
     * 기본 랜덤 닉네임 생성기 (어르신들도 친근하게 느끼는 장날 테마)
     */
    fun generateRandomNickname(): String {
        val adjectives = listOf("즐거운", "행복한", "단골손님", "장보기달인", "온누리마스터", "신토불이", "맛있는", "알뜰한", "정겨운", "발빠른")
        val nouns = listOf("호떡러버", "순대매니아", "장터이웃", "전통지킴이", "동네주민", "국밥러버", "나물박사", "과일대장", "시장탐험가")
        val randomNum = (100..999).random()
        return "${adjectives.random()} ${nouns.random()}$randomNum"
    }
}
