package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing an official merchant verification application & status
 * (사업자등록증 / 상인회원증 공식 서류 심사 데이터)
 */
@Entity(tableName = "merchant_verifications")
data class MerchantVerification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val marketId: Long,
    val marketName: String,
    val shopName: String,
    val ownerName: String,                  // 대표자 성명
    val businessNumber: String,             // 사업자등록번호 (10자리) 또는 상인회원 고유번호
    val contactPhone: String,               // 연락처
    val documentType: String = "사업자등록증", // 서류 유형 (사업자등록증, 상인회원증, 입점계약서 등)
    val documentPhotoUri: String = "",      // 서류 사진 로컬 URI / URL
    val status: String = "PENDING",         // PENDING(심사중), APPROVED(승인완료), REJECTED(반려)
    val submitTimestamp: Long = System.currentTimeMillis(),
    val reviewTimestamp: Long = 0L,
    val rejectionReason: String = ""
) {
    fun isApproved(): Boolean = status == "APPROVED"
    fun isPending(): Boolean = status == "PENDING"
    fun isRejected(): Boolean = status == "REJECTED"
}
