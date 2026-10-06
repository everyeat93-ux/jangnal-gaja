package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
 * 시장 동네마당 커뮤니티 댓글 엔티티
 * (구글 플레이 UGC 정책 준수: 신고, 차단, 자동 블라인드)
 */
@Entity(
    tableName = "community_comments",
    indices = [Index("postId"), Index("marketId")]
)
data class CommunityComment(
    @PrimaryKey val commentId: String,          // Firestore Document ID 또는 UUID
    val postId: String,                         // 대상 게시글 ID
    val marketId: Long,                         // 대상 시장 ID
    val authorNickname: String,                 // 작성자 닉네임 (예: 구로동 장보기달인)
    val authorDeviceIdHash: String,             // 기기 고유 해시 (차단 및 제재용)
    val content: String,                        // 댓글 내용 (최대 150자)
    val isNearMarket: Boolean = false,           // GPS 반경 300m 이내 현장인증 🟢
    val reportCount: Int = 0,                   // 누적 신고 횟수
    val isBlind: Boolean = false,                // 3회 신고 시 자동 숨김
    val createdAt: Long = System.currentTimeMillis()
)
