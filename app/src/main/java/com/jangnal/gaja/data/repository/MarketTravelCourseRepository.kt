package com.jangnal.gaja.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jangnal.gaja.data.local.entity.Market

data class CourseSpot(
    val order: Int,
    val name: String,
    val category: String, // "전통시장", "관광명소", "자연·힐링", "카페·디저트", "문화·체험"
    val timeEstimate: String,
    val travelFromPrev: String, // "출발지", "도보 5분", "차량 15분 (7km)"
    val highlight: String,
    val tip: String,
    val searchQuery: String
)

data class MarketTravelCourse(
    val id: String,
    val marketId: Long,
    val title: String,
    val theme: String, // "원조 먹방 투어", "가족 나들이", "레트로 감성", "자연 힐링", "주말 드라이브"
    val durationText: String, // "당일 약 4~5시간", "당일 약 6시간", "1박 2일"
    val targetAudience: String, // "가족과 함께", "연인·친구와", "뚜벅이 여행", "미식가"
    val summary: String,
    val spots: List<CourseSpot>
)

object MarketTravelCourseRepository {

    fun getTravelCoursesForMarket(market: Market): List<MarketTravelCourse> {
        val name = market.marketName
        val displayName = market.getDisplayName()
        val specialty = market.specialty.split("\n\n").firstOrNull()?.replace("+", ", ") ?: "지역 대표 먹거리"
        val province = market.getProvince()

        val curated = when {
            name.contains("정선") -> listOf(
                MarketTravelCourse(
                    id = "jeongseon_food_healing",
                    marketId = market.id,
                    title = "🥢 [정선 5일장] 콧등치기국수 & 병방치 짚와이어 힐링 코스",
                    theme = "원조 먹방 & 자연 힐링",
                    durationText = "당일 약 5시간 (도보 + 차량 20분)",
                    targetAudience = "가족·연인 추천 (주말 드라이브)",
                    summary = "해발 700m 정선의 맛있는 장날 먹거리와 아찔한 한반도 지형 전망, 레트로 간이역 카페를 하루에 즐기는 완벽한 코스입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "정선 아리랑시장 (정선 5일장)",
                            category = "전통시장",
                            timeEstimate = "11:00 ~ 12:40",
                            travelFromPrev = "여행 출발지",
                            highlight = "원조 콧등치기국수, 수수부꾸미, 모듬 산나물 전 & 곤드레밥 점심 식사",
                            tip = "장날(2, 7일) 11시 30분에는 야외 공연장에서 무료 정선아리랑 공연이 열립니다.",
                            searchQuery = "정선아리랑시장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "정선 아라리촌 민속마을",
                            category = "문화·체험",
                            timeEstimate = "12:50 ~ 13:40",
                            travelFromPrev = "도보 10분 (차량 3분)",
                            highlight = "조선시대 전통 가옥과 물레방아, 고즈넉한 조양강 산책로",
                            tip = "입장권 구매 시 정선아리랑상품권으로 전액 환급되어 시장에서 바로 쓸 수 있습니다.",
                            searchQuery = "정선 아라리촌"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "병방치 스카이워크 & 짚와이어",
                            category = "관광명소",
                            timeEstimate = "14:00 ~ 15:30",
                            travelFromPrev = "차량 15분 (6.5km)",
                            highlight = "해발 583m 절벽 위 유리 바닥에서 내려다보는 동강 한반도 지형 파노라마",
                            tip = "스카이워크 덧신 착용 필수! 바람이 시원해 인생샷 명소로 유명합니다.",
                            searchQuery = "병방치 스카이워크"
                        ),
                        CourseSpot(
                            order = 4,
                            name = "나전역 레트로 감성 간이역 카페",
                            category = "카페·디저트",
                            timeEstimate = "15:50 ~ 17:00",
                            travelFromPrev = "차량 18분 (12km)",
                            highlight = "실제 기차가 정차하는 국내 1호 간이역 카페 & 시그니처 나전역 크림라떼",
                            tip = "옛 역무원 모자와 유니폼을 입고 무료로 기념사진을 남길 수 있습니다.",
                            searchQuery = "나전역 카페"
                        )
                    )
                )
            )

            name.contains("속초") -> listOf(
                MarketTravelCourse(
                    id = "sokcho_sea_food",
                    marketId = market.id,
                    title = "🌊 [속초 중앙시장] 닭강정·오징어순대 & 갯배 바다 낭만 코스",
                    theme = "원조 먹방 & 바다 낭만",
                    durationText = "당일 약 4~5시간 (도보 위주)",
                    targetAudience = "뚜벅이·커플 여행 강력 추천",
                    summary = "속초 대표 먹거리 천국인 중앙시장에서 배를 채우고 아바이마을 갯배와 영금정 동해 바다 뷰를 즐기는 핵심 코스입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "속초관광수산시장 (속초 중앙시장)",
                            category = "전통시장",
                            timeEstimate = "11:30 ~ 13:00",
                            travelFromPrev = "여행 출발지",
                            highlight = "수제 가마솥 닭강정, 겉바속촉 오징어순대, 씨앗호떡, 홍게 샌드위치",
                            tip = "지하 수산물회센터에서 신선한 활어회를 포장해 바닷가에서 먹기 좋습니다.",
                            searchQuery = "속초관광수산시장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "아바이마을 청초호 갯배 체험",
                            category = "문화·체험",
                            timeEstimate = "13:10 ~ 14:00",
                            travelFromPrev = "도보 5분",
                            highlight = "와이어를 직접 끌어 배를 움직이는 70년 전통 무동력 갯배 탑승 체험",
                            tip = "편도 이용료 500원(현금/교통카드)! 짧지만 속초만의 이색 추억을 만듭니다.",
                            searchQuery = "속초 갯배 선착장"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "영금정 & 속초 등대전망대",
                            category = "관광명소",
                            timeEstimate = "14:20 ~ 15:30",
                            travelFromPrev = "차량 7분 (도보 20분)",
                            highlight = "파도가 바위에 부딪히며 거문고 소리를 내는 바다 정자와 탁 트인 동해 파노라마",
                            tip = "해돋이 정자에서 바다를 배경으로 사진 찍으면 푸른 동해가 한눈에 담깁니다.",
                            searchQuery = "속초 영금정"
                        ),
                        CourseSpot(
                            order = 4,
                            name = "속초아이 대관람차 & 속초해수욕장 카페거리",
                            category = "자연·힐링",
                            timeEstimate = "15:45 ~ 17:00",
                            travelFromPrev = "차량 10분",
                            highlight = "속초 해변의 푸른 파도와 감성 오션뷰 카페에서 즐기는 시원한 커피 한잔",
                            tip = "일몰 30분 전에 방문하면 노을빛으로 물드는 바다를 감상할 수 있습니다.",
                            searchQuery = "속초해수욕장"
                        )
                    )
                )
            )

            name.contains("강릉") -> listOf(
                MarketTravelCourse(
                    id = "gangneung_coffee_market",
                    marketId = market.id,
                    title = "☕ [강릉 중앙시장] 소머리국밥·배니닭강정 & 안목 커피거리 코스",
                    theme = "미식 & 커피 힐링",
                    durationText = "당일 약 5시간",
                    targetAudience = "친구·연인·미식가",
                    summary = "월화거리의 활기와 중앙시장의 깊은 손맛, 그리고 안목해변의 커피 향기를 만끽하는 강릉 대표 여행 코스입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "강릉 중앙시장 & 월화거리",
                            category = "전통시장",
                            timeEstimate = "11:00 ~ 13:00",
                            travelFromPrev = "여행 출발지",
                            highlight = "원조 소머리국밥, 배니 닭강정, 수제 어묵고로케, 팡파미유 마늘빵",
                            tip = "시장 2층 청년몰과 야외 월화거리 광장에서 야외 피크닉처럼 즐길 수 있습니다.",
                            searchQuery = "강릉 중앙시장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "강릉 오죽헌 & 선교장",
                            category = "문화·체험",
                            timeEstimate = "13:30 ~ 15:00",
                            travelFromPrev = "차량 12분 (5km)",
                            highlight = "신사임당과 율곡 이이의 생가, 300년 고택 선교장의 울창한 솔숲 산책",
                            tip = "오죽헌 솔숲길은 그늘이 풍부하여 사계절 산책하기 좋습니다.",
                            searchQuery = "강릉 오죽헌"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "안목해변 커피거리",
                            category = "카페·디저트",
                            timeEstimate = "15:20 ~ 17:00",
                            travelFromPrev = "차량 15분",
                            highlight = "바다를 바라보며 마시는 핸드드립 커피와 바다 산책로",
                            tip = "루프탑 카페 3~4층 자리를 선점하면 에메랄드빛 동해 바다가 파노라마로 펼쳐집니다.",
                            searchQuery = "안목해변 커피거리"
                        )
                    )
                )
            )

            name.contains("구로") || name.contains("남구로") -> listOf(
                MarketTravelCourse(
                    id = "guro_retro_rail",
                    marketId = market.id,
                    title = "🚂 [구로 5일장] 칠공주 떡볶이 & 항동 푸른수목원 철길 힐링 코스",
                    theme = "레트로 감성 & 수목원 산책",
                    durationText = "당일 약 4시간 (대중교통 편리)",
                    targetAudience = "주말 도심 힐링·가족 나들이",
                    summary = "40년 전통의 정겨운 시장 먹거리와 서울 최초의 시립 수목원, 감성 철길을 함께 걷는 도심 속 힐링 코스입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "구로시장 & 남구로시장 먹자골목",
                            category = "전통시장",
                            timeEstimate = "11:30 ~ 13:00",
                            travelFromPrev = "여행 출발지",
                            highlight = "40년 전통 칠공주 즉석 떡볶이, 수제 모듬전, 즉석 가마솥 족발",
                            tip = "온누리상품권 10% 모바일 결제 가능 점포가 많아 가성비 최고입니다.",
                            searchQuery = "구로시장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "항동철길 & 푸른수목원",
                            category = "자연·힐링",
                            timeEstimate = "13:30 ~ 15:30",
                            travelFromPrev = "차량 15분 (지하철/버스 편리)",
                            highlight = "옛 단선 철길을 따라 걷는 감성 산책로와 2,100여 종의 식물이 가득한 저수지 뷰",
                            tip = "철길 중간중간 쓰인 감성 문구 팻말 앞에서 인증샷을 남겨보세요.",
                            searchQuery = "푸른수목원 항동철길"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "항동 감성 베이커리 카페",
                            category = "카페·디저트",
                            timeEstimate = "15:45 ~ 17:00",
                            travelFromPrev = "도보 5분",
                            highlight = "수목원 푸른 전망을 바라보는 소금빵 & 아인슈페너 디저트 타임",
                            tip = "수목원 산책 후 여유롭게 당 충전하기에 최적의 동선입니다.",
                            searchQuery = "구로 항동 카페"
                        )
                    )
                )
            )

            name.contains("광장") || name.contains("종로") -> listOf(
                MarketTravelCourse(
                    id = "gwangjang_seoul_history",
                    marketId = market.id,
                    title = "🏮 [광장시장] 맷돌 녹두전·육회 & 청계천·익선동 한옥 코스",
                    theme = "원조 먹방 & 서울 역사 산책",
                    durationText = "당일 약 4~5시간 (도보 100%)",
                    targetAudience = "서울 도심 투어·외국인 친구 추천",
                    summary = "100년 전통 대한민국 최초 상설시장의 활기와 청계천 징검다리, 익선동 감성 한옥마을을 잇는 클래식 투어입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "광장시장 먹거리 골목",
                            category = "전통시장",
                            timeEstimate = "11:30 ~ 13:30",
                            travelFromPrev = "여행 출발지",
                            highlight = "100% 맷돌 녹두빈대떡, 원조 마약김밥, 신선한 육회 탕탕이, 찹쌀 꽈배기",
                            tip = "시장 중앙 전골목에서 갓 부친 바삭한 빈대떡에 양파 장아찌를 얹어 드세요!",
                            searchQuery = "광장시장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "청계천 징검다리 산책로",
                            category = "자연·힐링",
                            timeEstimate = "13:40 ~ 14:30",
                            travelFromPrev = "도보 3분",
                            highlight = "시원한 물소리와 도심 빌딩 숲 사이를 가로지르는 버드나무 그늘 산책",
                            tip = "광장시장 바로 앞 배오개다리에서 청계천으로 바로 내려갈 수 있습니다.",
                            searchQuery = "청계천 배오개다리"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "익선동 한옥마을 & 감성 카페거리",
                            category = "카페·디저트",
                            timeEstimate = "14:50 ~ 16:30",
                            travelFromPrev = "도보 15분",
                            highlight = "100년 된 좁은 골목길에 자리잡은 한옥 카페와 개화기 감성 베이커리",
                            tip = "골목마다 이색 포토존이 가득해 카메라를 챙기면 더욱 좋습니다.",
                            searchQuery = "익선동 한옥마을"
                        )
                    )
                )
            )

            name.contains("모란") || name.contains("성남") -> listOf(
                MarketTravelCourse(
                    id = "moran_heritage_trail",
                    marketId = market.id,
                    title = "🔥 [성남 모란 5일장] 전국 최대 장날 투어 & 남한산성 성곽길 코스",
                    theme = "대형 5일장 & 세계유산 힐링",
                    durationText = "당일 약 5~6시간",
                    targetAudience = "가족·산행·장날 마니아",
                    summary = "끝자리가 4, 9일에 열리는 전국 최대 규모 모란민속장의 생생한 열기와 유네스코 세계유산 남한산성의 절경을 함께 즐깁니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "성남 모란민속 5일장 (4·9일 장날)",
                            category = "전통시장",
                            timeEstimate = "10:30 ~ 12:30",
                            travelFromPrev = "여행 출발지",
                            highlight = "장터 가마솥 손칼국수, 기름 갓 짠 참기름 골목, 팥죽, 가마솥 옛날통닭",
                            tip = "전국 최대 규모(점포 1,000개 이상)이므로 편한 운동화를 착용하세요.",
                            searchQuery = "모란민속5일장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "남한산성 도립공원 & 수어장대",
                            category = "관광명소",
                            timeEstimate = "13:30 ~ 15:30",
                            travelFromPrev = "차량 20분 (버스 연계)",
                            highlight = "유네스코 세계유산 성곽길을 따라 걷는 서울·성남 파노라마 뷰",
                            tip = "산성 로터리 주차장 이용 시 산성 둘레길 1코스(소요 1시간 20분)가 가장 걷기 좋습니다.",
                            searchQuery = "남한산성 수어장대"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "남한산성 계곡 숲속 카페",
                            category = "카페·디저트",
                            timeEstimate = "15:45 ~ 17:00",
                            travelFromPrev = "차량 5분",
                            highlight = "푸른 솔숲과 맑은 계곡 물소리를 들으며 즐기는 따뜻한 쌍화차 & 쑥라떼",
                            tip = "야외 테라스에서 산바람을 맞으며 산행 피로를 풀기에 최적입니다.",
                            searchQuery = "남한산성 카페"
                        )
                    )
                )
            )

            name.contains("서문") || name.contains("대구") -> listOf(
                MarketTravelCourse(
                    id = "seomun_daegu_history",
                    marketId = market.id,
                    title = "🥟 [대구 서문시장] 납작만두·칼제비 & 청라언덕 근대골목 코스",
                    theme = "원조 먹방 & 근대 문화",
                    durationText = "당일 약 5시간 (도보 위주)",
                    targetAudience = "뚜벅이·친구·미식가",
                    summary = "조선 3대 시장 서문시장의 푸짐한 손맛과 대구 100년 역사가 숨쉬는 근대문화골목을 걷는 코스입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "대구 서문시장 (동산상가·야시장)",
                            category = "전통시장",
                            timeEstimate = "11:30 ~ 13:30",
                            travelFromPrev = "여행 출발지",
                            highlight = "50년 원조 납작만두와 새콤한 비빔당면, 멸치육수 손칼제비, 삼각만두",
                            tip = "2지구 지하와 4지구 주변 먹자골목에 현지인 단골 노포가 집중되어 있습니다.",
                            searchQuery = "대구 서문시장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "청라언덕 & 대구 3·1만세운동길",
                            category = "문화·체험",
                            timeEstimate = "13:50 ~ 15:00",
                            travelFromPrev = "도보 8분",
                            highlight = "선교사 주택과 담쟁이덩굴, 90계단 3·1만세운동길의 이국적인 풍경",
                            tip = "한국관광의 별로 선정된 아름다운 근대 건축물에서 사진 찍기 좋습니다.",
                            searchQuery = "대구 청라언덕"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "계산성당 & 이상화 고택",
                            category = "관광명소",
                            timeEstimate = "15:10 ~ 16:00",
                            travelFromPrev = "도보 5분",
                            highlight = "1902년 완공된 영남 최초의 고딕 양식 성당과 민족시인 이상화의 고택",
                            tip = "성당 내부 스테인드글라스에는 한복을 입은 조선 천주교 성인들이 묘사되어 있습니다.",
                            searchQuery = "대구 계산성당"
                        ),
                        CourseSpot(
                            order = 4,
                            name = "서문시장 감성 카페 & 야시장 버스킹",
                            category = "카페·디저트",
                            timeEstimate = "16:30 ~ 18:30",
                            travelFromPrev = "도보 10분",
                            highlight = "해질녘 서문시장 야시장 푸드트럭과 버스킹 공연 관람",
                            tip = "야시장 개장(19:00~)에 맞춰 가면 전국 최대 야시장 먹거리를 즐길 수 있습니다.",
                            searchQuery = "서문시장 야시장"
                        )
                    )
                )
            )

            name.contains("전주") || name.contains("남부") -> listOf(
                MarketTravelCourse(
                    id = "jeonju_hanok_market",
                    marketId = market.id,
                    title = "🍲 [전주 남부시장] 피순대국밥·청년몰 & 전주 한옥마을 달빛 코스",
                    theme = "원조 먹방 & 한옥 감성",
                    durationText = "당일 약 5~6시간 (도보 100%)",
                    targetAudience = "가족·연인·뚜벅이 여행",
                    summary = "남부시장의 깊고 진한 피순대국밥과 2층 청년몰, 경기전과 전주천변을 잇는 가장 완벽한 전주 여행 코스입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "전주 남부시장 & 2층 청년몰",
                            category = "전통시장",
                            timeEstimate = "11:30 ~ 13:30",
                            travelFromPrev = "여행 출발지",
                            highlight = "진한 사골 국물의 원조 피순대국밥, 콩나물국밥, 청년몰 감성 수공예품",
                            tip = "금/토요일 밤에는 야시장이 열려 수십 가지 세계 길거리 음식이 가득합니다.",
                            searchQuery = "전주 남부시장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "전주 한옥마을 & 경기전",
                            category = "문화·체험",
                            timeEstimate = "13:45 ~ 15:30",
                            travelFromPrev = "도보 5분 (풍남문 건너편)",
                            highlight = "조선 태조 이성계의 어진(초상화)을 모신 경기전과 700여 채의 전통 한옥길",
                            tip = "한복 대여 후 경기전 대나무숲길에서 인생 사진을 남겨보세요.",
                            searchQuery = "전주 경기전"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "자만벽화마을 & 오목대 전망대",
                            category = "관광명소",
                            timeEstimate = "15:45 ~ 17:00",
                            travelFromPrev = "도보 10분",
                            highlight = "오목대에 올라 한눈에 내려다보는 기와지붕 파노라마와 알록달록 벽화마을",
                            tip = "오목대에서 바라보는 해질녘 한옥마을 노을 풍경은 전주 8경 중 하나입니다.",
                            searchQuery = "전주 오목대"
                        )
                    )
                )
            )

            name.contains("순천") -> listOf(
                MarketTravelCourse(
                    id = "suncheon_bay_market",
                    marketId = market.id,
                    title = "🌾 [순천 아랫장] 국밥골목 & 순천만습지 갈대밭 일몰 코스",
                    theme = "생태 힐링 & 남도 미식",
                    durationText = "당일 약 5~6시간",
                    targetAudience = "가족·연인·자연 탐방",
                    summary = "전국 최대 규모 남도 5일장(2·7일)의 푸짐한 국밥과 100만 평 순천만 갈대밭의 붉은 노을을 만끽하는 여행입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = "순천 아랫장 (2·7일 장날)",
                            category = "전통시장",
                            timeEstimate = "11:00 ~ 13:00",
                            travelFromPrev = "여행 출발지",
                            highlight = "돼지머리국밥 주문 시 수육과 순대가 서비스로 나오는 남도 국밥의 정수",
                            tip = "남도의 제철 해산물(꼬막, 바지락, 굴)을 가장 신선하고 저렴하게 구매할 수 있습니다.",
                            searchQuery = "순천아랫장"
                        ),
                        CourseSpot(
                            order = 2,
                            name = "순천만국가정원",
                            category = "자연·힐링",
                            timeEstimate = "13:30 ~ 15:30",
                            travelFromPrev = "차량 8분 (3.5km)",
                            highlight = "대한민국 1호 국가정원, 네덜란드 풍차 정원과 꿈의 다리",
                            tip = "스카이큐브(무인궤도차)를 타면 순천만습지까지 편안하게 이동할 수 있습니다.",
                            searchQuery = "순천만국가정원"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "순천만습지 갈대밭 & 용산전망대 일몰",
                            category = "관광명소",
                            timeEstimate = "15:50 ~ 17:30",
                            travelFromPrev = "차량 10분 (스카이큐브 연계)",
                            highlight = "황금빛 갈대밭 데크길과 S자 갯벌 수로를 붉게 물들이는 전국 최고 일몰",
                            tip = "일몰 1시간 전 용산전망대에 도착하면 S자 갯벌 노을을 완벽하게 감상할 수 있습니다.",
                            searchQuery = "순천만습지"
                        )
                    )
                )
            )

            else -> listOf(
                MarketTravelCourse(
                    id = "smart_local_course_${market.id}",
                    marketId = market.id,
                    title = "✨ [${displayName}] $specialty & 로컬 명소 힐링 코스",
                    theme = "장날 먹거리 & 로컬 산책",
                    durationText = "당일 약 4~5시간",
                    targetAudience = "가족·연인 추천",
                    summary = "${displayName}의 활기찬 장터 손맛과 주변 대표 관광명소, 감성 카페를 묶은 알찬 추천 코스입니다.",
                    spots = listOf(
                        CourseSpot(
                            order = 1,
                            name = displayName,
                            category = "전통시장",
                            timeEstimate = "11:30 ~ 13:00",
                            travelFromPrev = "여행 출발지",
                            highlight = "시장 대표 명물 $specialty 및 장터 즉석 먹거리 점심 식사",
                            tip = "장날 개장 시간(${market.getStatusText()})에 방문하면 가장 풍성한 먹거리를 만날 수 있습니다.",
                            searchQuery = displayName
                        ),
                        CourseSpot(
                            order = 2,
                            name = "$displayName 주변 로컬 관광명소 / 문화공원",
                            category = "관광명소",
                            timeEstimate = "13:30 ~ 15:00",
                            travelFromPrev = "차량 10~15분 (도보 가능)",
                            highlight = "${province}의 정취가 느껴지는 역사 유적지 및 고즈넉한 둘레길 산책",
                            tip = "현장 안내소에서 스탬프 투어 리플렛을 받아보세요.",
                            searchQuery = "${market.addressRoad.split(" ").take(2).joinToString(" ")} 명소"
                        ),
                        CourseSpot(
                            order = 3,
                            name = "인근 전망 좋은 로컬 카페",
                            category = "카페·디저트",
                            timeEstimate = "15:20 ~ 16:30",
                            travelFromPrev = "차량 5~10분",
                            highlight = "지역 특산물을 활용한 시그니처 음료와 여유로운 티타임",
                            tip = "시장에서 구매한 특산품을 정리하며 하루 여행을 마무리하기 좋습니다.",
                            searchQuery = "${market.addressRoad.split(" ").take(2).joinToString(" ")} 카페"
                        )
                    )
                )
            )
        }

        return curated
    }

    fun launchNavigationToSpot(context: Context, spotName: String, lat: Double?, lon: Double?) {
        val kakaoUrl = if (lat != null && lon != null) {
            "kakaomap://look?p=$lat,$lon"
        } else {
            "kakaomap://search?q=" + Uri.encode(spotName)
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(kakaoUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val naverWeb = "https://map.naver.com/v5/search/" + Uri.encode(spotName)
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(naverWeb)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            } catch (_: Exception) {}
        }
    }

    fun launchMultiRouteNavigation(context: Context, course: MarketTravelCourse) {
        val spotNames = course.spots.map { it.name }.joinToString(" ➜ ")
        val query = course.spots.firstOrNull()?.name ?: course.title
        val naverWeb = "https://map.naver.com/v5/search/" + Uri.encode(query)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(naverWeb)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
