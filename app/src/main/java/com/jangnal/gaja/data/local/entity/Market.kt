package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a traditional 5-day market (5일장)
 * 
 * @property id Unique identifier for the market
 * @property marketName Name of the market (시장 이름)
 * @property addressRoad Road address (도로명 주소)
 * @property addressJibun Jibun address (지번 주소)
 * @property latitude Latitude coordinate (위도)
 * @property longitude Longitude coordinate (경도)
 * @property openingCycle Opening cycle pattern (e.g., "2+7", "1+6") - represents which days of the month the market operates
 * @property specialty Main products/specialties sold at the market (주요 취급 품목/특산물)
 * @property phoneNumber Contact phone number (전화번호)
 */
@Entity(tableName = "markets")
data class Market(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val marketName: String,
    
    val addressRoad: String,
    
    val addressJibun: String,
    
    val latitude: Double,
    
    val longitude: Double,
    
    val openingCycle: String, // Format: "2+7" means opens on days ending in 2 and 7
    
    val specialty: String,
    
    val phoneNumber: String,
    
    val hasToilet: String = "N",
    
    val hasParking: String = "N",

    val isFavorite: Boolean = false,
    
    val voteOpenTodayCount: Int = 0,
    
    val voteClosedTodayCount: Int = 0,
    
    val lastVoteDate: String = ""
) {
    fun getDisplayName(): String {
        return marketName
            .replace(Regex("^\\s*\\(유\\)"), "")
            .replace(Regex("^\\s*\\(주\\)"), "")
            .replace(Regex("^\\s*\\(사\\)"), "")
            .replace(Regex("^\\s*\\(재\\)"), "")
            .replace(Regex("^\\s*\\(합\\)"), "")
            .replace(Regex("^\\s*\\(합자\\)"), "")
            .replace(Regex("^\\s*\\(합명\\)"), "")
            .replace(Regex("^\\s*주식회사"), "")
            .replace(Regex("^\\s*유한회사"), "")
            .replace(Regex("^\\s*사단법인"), "")
            .replace(Regex("^\\s*재단법인"), "")
            .trim()
    }

    fun getCleanSpecialty(): String {
        return specialty.replace("+", ", ")
    }
    
    // 상설시장 여부 판단
    fun isPermanent(): Boolean {
        if (openingCycle.contains("매일") || openingCycle.contains("상설") || openingCycle.contains("0+1+2")) return true
        
        // 0부터 9까지 모든 숫자가 포함되어 있으면 상설시장으로 간주
        val digits = openingCycle.filter { it.isDigit() }.map { it.toString().toInt() }.toSet()
        return (0..9).all { digits.contains(it) }
    }
    
    // 주기와 시작일 파싱 (예: "1+6" or "1일+6일" -> Pair(5, listOf(1, 6)))
    fun parseCyclePublic(): Pair<Int?, List<Int>> {
        // 숫자만 추출하되, "10"과 같이 두 자리 수도 처리하기 위해 Regex 사용
        val startDays = Regex("\\d+").findAll(openingCycle)
            .map { it.value.toInt() }
            .distinct()
            .filter { it > 0 && it <= 31 } // 0일 제외, 1~31일만 유효
            .sorted()
            .toList()
        
        if (startDays.size >= 2) {
            val cycle = startDays[1] - startDays[0]
            return Pair(cycle, startDays)
        }
        
        return Pair(null, startDays)
    }
    
    // 한 달 동안의 실제 장날 계산
    fun getMarketDaysInMonth(maxDay: Int = 31): List<Int> {
        if (isPermanent()) {
            return (1..maxDay).toList()
        }
        
        val (cycle, startDays) = parseCyclePublic()
        
        // 유효하지 않은 데이터 처리
        if (startDays.isEmpty()) {
            return emptyList()
        }
        
        // cycle이 null이면 startDays를 그대로 반환 (예: "1+7+8" -> [1, 7, 8])
        if (cycle == null) {
            return startDays.filter { it <= maxDay }
        }
        
        // 주기가 0이거나 음수인 경우 (잘못된 데이터)
        if (cycle <= 0) {
            return startDays.filter { it <= maxDay }
        }
        
        val allDays = mutableSetOf<Int>()
        for (start in startDays) {
            if (start < 1 || start > 31) continue // 유효하지 않은 시작일 스킵
            
            var day = start
            while (day <= maxDay) {
                allDays.add(day)
                day += cycle
            }
        }
        
        return allDays.sorted()
    }
    
    // 시장 유형 이름 (3일장, 4일장, 5일장 등)
    fun getMarketTypeName(): String {
        if (isPermanent()) return "전통시장"
        
        val (cycle, _) = parseCyclePublic()
        return when (cycle) {
            3 -> "3일장"
            4 -> "4일장"
            5 -> "5일장"
            6 -> "6일장"
            7 -> "7일장"
            10 -> "10일장"
            else -> "정기시장"
        }
    }
    
    // 시작일 표시 (예: "1일부터" 또는 "1일 또는 6일부터")
    fun getStartDayText(): String {
        if (isPermanent()) return "매일"
        
        val (_, startDays) = parseCyclePublic()
        return when (startDays.size) {
            0 -> ""
            1 -> "${startDays[0]}일부터"
            2 -> "${startDays[0]}일 또는 ${startDays[1]}일부터"
            else -> startDays.joinToString(", ") { "${it}일" } + "부터"
        }
    }

    // 5일장 주기 축약 표기 (예: "1·6일장", "2·7일장", "상설시장")
    fun getShortCycleName(): String {
        if (isPermanent()) return "상설시장"
        val cleanCycle = openingCycle.replace(" ", "")
        val (_, startDays) = parseCyclePublic()
        val allDays = (getMarketDaysInMonth(31) + startDays).map { it % 10 }.toSet()
        
        return when {
            cleanCycle.contains("1+6") || cleanCycle.contains("1,6") || cleanCycle.contains("1·6") || (allDays.contains(1) && allDays.contains(6)) -> "1·6일장"
            cleanCycle.contains("2+7") || cleanCycle.contains("2,7") || cleanCycle.contains("2·7") || (allDays.contains(2) && allDays.contains(7)) -> "2·7일장"
            cleanCycle.contains("3+8") || cleanCycle.contains("3,8") || cleanCycle.contains("3·8") || (allDays.contains(3) && allDays.contains(8)) -> "3·8일장"
            cleanCycle.contains("4+9") || cleanCycle.contains("4,9") || cleanCycle.contains("4·9") || (allDays.contains(4) && allDays.contains(9)) -> "4·9일장"
            cleanCycle.contains("5+10") || cleanCycle.contains("5+0") || cleanCycle.contains("0+5") || cleanCycle.contains("5,10") || cleanCycle.contains("5·10") || (allDays.contains(5) && allDays.contains(0)) -> "5·10일장"
            startDays.isNotEmpty() -> "${startDays.joinToString("·")}일장"
            else -> "5일장"
        }
    }

    // 시장 유형 및 장날 필터 매칭 함수
    fun matchesScheduleType(type: String): Boolean {
        if (type == "전체" || type == "전체 유형") return true
        if (type.contains("상설")) return isPermanent()
        if (type == "5일장" || type == "5일장 전체" || type == "🎪 5일장") return !isPermanent()
        
        if (isPermanent()) return false
        
        val cleanCycle = openingCycle.replace(" ", "")
        val (_, startDays) = parseCyclePublic()
        val allDays = (getMarketDaysInMonth(31) + startDays).map { it % 10 }.toSet()
        
        return when {
            type.contains("1·6") -> cleanCycle.contains("1+6") || cleanCycle.contains("1,6") || cleanCycle.contains("1·6") || allDays.contains(1) || allDays.contains(6)
            type.contains("2·7") -> cleanCycle.contains("2+7") || cleanCycle.contains("2,7") || cleanCycle.contains("2·7") || allDays.contains(2) || allDays.contains(7)
            type.contains("3·8") -> cleanCycle.contains("3+8") || cleanCycle.contains("3,8") || cleanCycle.contains("3·8") || allDays.contains(3) || allDays.contains(8)
            type.contains("4·9") -> cleanCycle.contains("4+9") || cleanCycle.contains("4,9") || cleanCycle.contains("4·9") || allDays.contains(4) || allDays.contains(9)
            type.contains("5·10") || type.contains("5·0") -> cleanCycle.contains("5+10") || cleanCycle.contains("5+0") || cleanCycle.contains("0+5") || cleanCycle.contains("5,10") || cleanCycle.contains("5·10") || allDays.contains(5) || allDays.contains(0)
            else -> true
        }
    }

    // 목록 화면용 간단 표기 (예: "5일장 (1·6일장)")
    fun getSimpleTypeText(): String {
        if (isPermanent()) return "전통시장 (상설)"
        return "${getMarketTypeName()} (${getShortCycleName()})"
    }
    
    // 상세 화면용 장날 목록 (예: "매월 1, 6, 11, 16, 21, 26, 31일")
    fun getDetailedScheduleText(): String {
        if (isPermanent()) return "매일 운영"
        
        val days = getMarketDaysInMonth()
        return if (days.isNotEmpty()) {
            "매월 ${days.joinToString(", ")}일"
        } else {
            "일정 정보 없음"
        }
    }
    
    // 다음 장날 계산
    fun getNextMarketDate(): Pair<Int, Int>? { // (month, day)
        if (isPermanent()) return null
        
        val calendar = java.util.Calendar.getInstance()
        val currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
        
        // 이번 달 남은 장날 확인
        val thisMonthDays = getMarketDaysInMonth()
        val nextDayThisMonth = thisMonthDays.firstOrNull { it > currentDay }
        
        if (nextDayThisMonth != null) {
            return Pair(currentMonth, nextDayThisMonth)
        }
        
        // 다음 달 첫 장날
        calendar.add(java.util.Calendar.MONTH, 1)
        val nextMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val maxDayNextMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val nextMonthDays = getMarketDaysInMonth(maxDayNextMonth)
        
        return if (nextMonthDays.isNotEmpty()) {
            Pair(nextMonth, nextMonthDays.first())
        } else {
            null
        }
    }
    
    // 다음 장날까지 남은 일수
    fun getDaysUntilNextMarket(): Int? {
        if (isPermanent()) return 0
        
        val nextDate = getNextMarketDate() ?: return null
        val (nextMonth, nextDay) = nextDate
        
        val calendar = java.util.Calendar.getInstance()
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        
        if (nextMonth == currentMonth) {
            return nextDay - currentDay
        } else {
            // 이번 달 남은 일수 + 다음 달 날짜
            val daysLeftThisMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) - currentDay
            return daysLeftThisMonth + nextDay
        }
    }
    
    // 통상 운영 시작 시간 (상설 08시, 5일장 07시)
    fun getOpeningHour(): Int = if (isPermanent()) 8 else 7

    // 파장/마감 임박 주의 시간 (상설 19시, 5일장 17시)
    fun getClosingWarningHour(): Int = if (isPermanent()) 19 else 17

    // 통상 영업 종료 시간 (상설 20시, 5일장 18시)
    fun getClosingHour(): Int = if (isPermanent()) 20 else 18

    // 오늘 현장 투표에서 '닫혔어요'가 더 많은지 여부
    fun isReportedClosedToday(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        val todayStr = String.format(
            java.util.Locale.US,
            "%04d-%02d-%02d",
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
        return lastVoteDate == todayStr && voteClosedTodayCount > voteOpenTodayCount && voteClosedTodayCount >= 1
    }

    // 두 시각이 같은 날짜(연/월/일)인지 확인
    private fun isSameDay(millis1: Long, millis2: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = millis1 }
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = millis2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    // 현재 시각 기준 실제 운영 중인지 여부 (날짜 + 영업시간 + 현장제보 모두 반영)
    fun isCurrentlyOperating(nowMillis: Long = System.currentTimeMillis()): Boolean {
        if (!isOpenOn(nowMillis)) return false
        if (isReportedClosedToday(nowMillis)) return false
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        return hour in getOpeningHour() until getClosingHour()
    }

    // 현재 마감/파장 임박 시간대인지 여부 (5일장 17~18시, 상설 19~20시)
    fun isClosingSoon(nowMillis: Long = System.currentTimeMillis()): Boolean {
        if (!isCurrentlyOperating(nowMillis)) return false
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        return hour >= getClosingWarningHour()
    }

    // 선택된 날짜(selectedDateMillis) 기준 영업 상태 (오늘이면 현재 시간까지 반영, 다른 날짜면 해당 날짜 개장 여부)
    fun isOperatingOnDateAndTime(selectedDateMillis: Long, nowMillis: Long = System.currentTimeMillis()): Boolean {
        return if (isSameDay(selectedDateMillis, nowMillis)) {
            isCurrentlyOperating(nowMillis)
        } else {
            isPermanent() || isOpenOn(selectedDateMillis)
        }
    }

    // 지도 하단 카드용 컴팩트 상태 문구
    fun getMapCardStatusText(selectedDateMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
        if (!isSameDay(selectedDateMillis, nowMillis)) {
            return if (isPermanent() || isOpenOn(selectedDateMillis)) "개장 예정 ☀️" else "휴장 ⚪"
        }
        if (!isOpenOn(nowMillis)) {
            val nextDate = getNextMarketDate()
            return if (nextDate != null) "휴장 ⚪ (${nextDate.first}/${nextDate.second} 장)" else "휴장 ⚪"
        }
        if (isReportedClosedToday(nowMillis)) {
            return "현장제보 닫힘 ⚠️"
        }
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val openHr = getOpeningHour()
        val warnHr = getClosingWarningHour()
        val closeHr = getClosingHour()

        return when {
            hour < openHr -> "개장 전 🌅 (${openHr}시~)"
            hour >= closeHr -> if (isPermanent()) "영업 종료 🌙 (${closeHr}시 마감)" else "장 마감 🌙 (${closeHr}시 종료)"
            hour >= warnHr -> if (isPermanent()) "마감 임박 ⚠️ (~${closeHr}시)" else "파장 무렵 ⚠️ (~${closeHr}시)"
            else -> if (isPermanent()) "영업중 ☀️ (~${closeHr}시)" else "장날 영업중 ☀️ (~${closeHr}시)"
        }
    }

    // 오늘 기준 상태 텍스트 (목록용 - 간결하고 중복 없는 표기)
    fun getStatusText(nowMillis: Long = System.currentTimeMillis()): String {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val openHr = getOpeningHour()
        val warnHr = getClosingWarningHour()
        val closeHr = getClosingHour()

        if (isPermanent()) {
            if (isReportedClosedToday(nowMillis)) return "⚠️ 현장제보 금일 휴무"
            return when {
                hour < openHr -> "🌅 개장 준비중 (08:00~20:00)"
                hour >= closeHr -> "🌙 금일 영업 종료 (내일 08:00 개장)"
                hour >= warnHr -> "🟠 마감 임박 (통상 ~20:00 마감)"
                else -> "🟢 현재 영업중 (08:00~20:00)"
            }
        }
        
        if (isOpenToday()) {
            if (isReportedClosedToday(nowMillis)) return "⚠️ 오늘 장날 · 현장제보 닫힘"
            return when {
                hour < openHr -> "🌅 오늘 장날 · 개장 준비중 (07:00~18:00)"
                hour >= closeHr -> "🌙 오늘 장 마감 (18:00 종료)"
                hour >= warnHr -> "🟠 오늘 장날 · 파장 무렵 (~18:00 마감)"
                else -> "🟢 오늘 장날! 현재 영업중 (07:00~18:00)"
            }
        }
        
        val nextDate = getNextMarketDate()
        val daysUntil = getDaysUntilNextMarket()
        
        return if (nextDate != null && daysUntil != null) {
            val (month, day) = nextDate
            "⚪ 금일 휴장 · 다음 장날: ${month}월 ${day}일"
        } else {
            "⚪ 일정 확인 필요"
        }
    }
    
    // 상세 화면용 다음 장날 텍스트
    fun getNextMarketText(): String {
        if (isPermanent()) return "매일 운영"
        
        val nextDate = getNextMarketDate()
        val daysUntil = getDaysUntilNextMarket()
        
        return if (nextDate != null && daysUntil != null) {
            val (month, day) = nextDate
            if (daysUntil == 0) {
                "오늘!"
            } else {
                "${month}월 ${day}일 (${daysUntil}일 후)"
            }
        } else {
            "일정 확인 필요"
        }
    }

    fun isOpenToday(): Boolean {
        return isOpenOn(System.currentTimeMillis())
    }

    fun isOpenOn(dateInMillis: Long): Boolean {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

        if (isPermanent()) return true
        
        val maxDay = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val targetDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        
        return getMarketDaysInMonth(maxDay).contains(targetDay)
    }

    fun isOpenThisWeekend(nowMillis: Long = System.currentTimeMillis()): Boolean {
        if (isPermanent()) return true
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = nowMillis }
        val currentDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
        
        val satCal = java.util.Calendar.getInstance().apply {
            timeInMillis = nowMillis
            val daysToSat = (java.util.Calendar.SATURDAY - currentDayOfWeek + 7) % 7
            add(java.util.Calendar.DAY_OF_YEAR, daysToSat)
        }
        val sunCal = java.util.Calendar.getInstance().apply {
            timeInMillis = nowMillis
            val daysToSun = if (currentDayOfWeek == java.util.Calendar.SUNDAY) 0 else (java.util.Calendar.SUNDAY - currentDayOfWeek + 7) % 7
            add(java.util.Calendar.DAY_OF_YEAR, daysToSun)
        }
        
        return isOpenOn(satCal.timeInMillis) || isOpenOn(sunCal.timeInMillis)
    }

    fun getProvince(): String {
        val addr = addressRoad.ifEmpty { addressJibun }
        return when {
            addr.startsWith("서울") -> "서울"
            addr.startsWith("경기") -> "경기"
            addr.startsWith("인천") -> "인천"
            addr.startsWith("강원") -> "강원"
            addr.startsWith("충북") || addr.startsWith("충청북") -> "충북"
            addr.startsWith("충남") || addr.startsWith("충청남") || addr.startsWith("대전") || addr.startsWith("세종") -> "충남·대전"
            addr.startsWith("전북") || addr.startsWith("전라북") -> "전북"
            addr.startsWith("전남") || addr.startsWith("전라남") || addr.startsWith("광주") -> "전남·광주"
            addr.startsWith("경북") || addr.startsWith("경상북") || addr.startsWith("대구") -> "경북·대구"
            addr.startsWith("경남") || addr.startsWith("경상남") || addr.startsWith("부산") || addr.startsWith("울산") -> "경남·부산·울산"
            addr.startsWith("제주") -> "제주"
            else -> "전국"
        }
    }
}
