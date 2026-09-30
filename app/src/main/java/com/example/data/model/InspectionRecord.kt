package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class OnionGrade(val displayName: String, val description: String) {
    GRADE_A("Grade A (Premium / Passed)", "Uniform size, sound skin, 0% rot, no sprouting, excellent quality"),
    GRADE_B("Grade B (Standard Market)", "Minor cosmetic blemishes or slight size variance, sound flesh"),
    REJECT("Reject / Failed", "Failed quality inspection: Rot, severe sprouting, mold, or shriveled")
}

enum class SizeCategory(val displayName: String, val range: String) {
    SMALL("Small", "< 40 mm"),
    MEDIUM("Medium", "40 - 70 mm"),
    LARGE("Large", "> 70 mm")
}

enum class VerificationStatus {
    AI_ASSESSED,
    HUMAN_VERIFIED,
    OVERRIDDEN
}

@Entity(tableName = "inspections")
data class InspectionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reportId: String = generateReportId(),
    val lotNumber: String = "LOT-AG-2026-4142",
    val procurementCenter: String = "APMC Procurement Center #4",
    val inspectorName: String = "Rajesh Patil (QA Lead)",
    val cropName: String = "Potato (Solanum tuberosum)",
    val variety: String = "Russet Potato",
    val overallGrade: OnionGrade = OnionGrade.GRADE_A,
    val isFresh: Boolean = true,
    val isPassed: Boolean = true,
    val confidencePercent: Int = 94,
    val diameterMm: Float = 58.4f,
    val sizeCategory: SizeCategory = SizeCategory.MEDIUM,
    val skinIntegrityPercent: Int = 96,
    val sproutingDetected: Boolean = false,
    val sproutLengthMm: Float = 0.0f,
    val rotFungalDetected: Boolean = false,
    val rotDescription: String = "None detected",
    val wrinklingDetected: Boolean = false,
    val greeningDetected: Boolean = false,
    val isDoubleBulb: Boolean = false,
    val neckTightness: String = "Firm & Well-cured",
    val colorUniformityPercent: Int = 92,
    val recommendedPricePerKg: Double = 28.50,
    val verificationStatus: VerificationStatus = VerificationStatus.AI_ASSESSED,
    val inspectorNotes: String = "Passed: clean smooth skin, no sprouts, firm texture, excellent Grade A quality.",
    val failureReasons: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String? = null
) {
    fun formattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun digitalVerificationHash(): String {
        return "AGRIG-AI-2.3-${reportId.hashCode().toString(16).uppercase()}-${timestamp % 100000}"
    }

    companion object {
        fun generateReportId(): String {
            val random = (1000..9999).random()
            return "AGR-2026-$random"
        }
    }
}
