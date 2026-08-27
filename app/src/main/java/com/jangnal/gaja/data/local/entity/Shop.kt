package com.jangnal.gaja.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "shops",
    foreignKeys = [
        ForeignKey(
            entity = Market::class,
            parentColumns = ["id"],
            childColumns = ["marketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("marketId")]
)
data class Shop(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val marketId: Long,
    val shopName: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    // 대기 상태 (0: 한산, 1: 보통, 2: 혼잡, -1: 정보 없음)
    val queueStatus: Int = -1,
    val lastReportTime: Long = 0,
    val isVerifiedReport: Boolean = false
)
