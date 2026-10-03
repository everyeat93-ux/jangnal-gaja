package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * 시장 동네마당 커뮤니티 게시글 엔티티
 * (구글 플레이 UGC 정책 완벽 준수: 신고, 차단, 자동 블라인드 지원)
 */
@Entity(
    tableName = "community_posts",
    foreignKeys = [
        ForeignKey(
            entity = Market::class,
            parentColumns = ["id"],
            childColumns = ["marketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("marketId")]
)
data class CommunityPost(
    @PrimaryKey val postId: String,       // Firestore Document ID 또는 UUID
    val marketId: Long,                   // 대상 시장 ID
    val marketName: String,               // 시장 이름
    val authorNickname: String,           // 작성자 닉네임 (예: 구로동 장보기달인)
    val authorDeviceIdHash: String,       // 기기 고유 해시 (차단 및 제재용)
    val category: String = "꿀팁",         // 꿀팁, 장바구니, 축제제보, 동네수다
    val content: String = "",              // 내용 (최대 300자)
    val photoUrl: String = "",             // 첨부 사진 URL (선택)
    val isNearMarket: Boolean = false,     // GPS 반경 200m 이내 현장인증 🟢
    val likeCount: Int = 0,                // 공감/좋아요 수
    val reportCount: Int = 0,              // 누적 신고 횟수
    val isBlind: Boolean = false,          // 신고 3회 이상 자동 숨김
    val createdAt: Long = System.currentTimeMillis()
)
