package com.example.data.local

import com.example.data.model.InspectionRecord
import com.example.data.model.OnionGrade
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class InspectionRepository(private val dao: InspectionDao) {

    val allInspections: Flow<List<InspectionRecord>> = dao.getAllInspections()

    suspend fun getById(id: Long): InspectionRecord? = dao.getInspectionById(id)

    suspend fun getByReportId(reportId: String): InspectionRecord? = dao.getInspectionByReportId(reportId)

    suspend fun saveInspection(record: InspectionRecord): Long = dao.insertInspection(record)

    suspend fun updateInspection(record: InspectionRecord) = dao.updateInspection(record)

    suspend fun deleteInspection(id: Long) = dao.deleteInspection(id)

    suspend fun seedInitialDataIfEmpty() {
        val current = dao.getAllInspections().firstOrNull()
        if (current.isNullOrEmpty()) {
            val sample1 = InspectionRecord(
                reportId = "AO-2026-4142",
                lotNumber = "LOT-MH-2026-4142",
                procurementCenter = "Lasalgaon Mandi Hub - Nashik",
                inspectorName = "Inspector Rajesh Patil",
                cropName = "Onion",
                variety = "Nashik Red Onion",
                overallGrade = OnionGrade.GRADE_A,
                isFresh = true,
                confidencePercent = 94,
                diameterMm = 55.4f,
                skinIntegrityPercent = 96,
                sproutingDetected = false,
                rotFungalDetected = false,
                rotDescription = "No visible rot or mold",
                isDoubleBulb = false,
                neckTightness = "Tight / Cured",
                colorUniformityPercent = 92,
                recommendedPricePerKg = 28.50,
                verificationStatus = VerificationStatus.HUMAN_VERIFIED,
                inspectorNotes = "Exceptional lot. Export grade quality, tight dry skin, uniform deep pinkish red.",
                timestamp = System.currentTimeMillis() - 25 * 60 * 1000
            )

            val sample2 = InspectionRecord(
                reportId = "AO-2026-3829",
                lotNumber = "LOT-MH-2026-3829",
                procurementCenter = "Lasalgaon Mandi Hub - Nashik",
                inspectorName = "Inspector Rajesh Patil",
                cropName = "Onion",
                variety = "Garwa Onion",
                overallGrade = OnionGrade.GRADE_B,
                isFresh = true,
                confidencePercent = 88,
                diameterMm = 68.2f,
                skinIntegrityPercent = 81,
                sproutingDetected = false,
                rotFungalDetected = false,
                rotDescription = "Minor partial skin shedding, flesh completely sound",
                isDoubleBulb = false,
                neckTightness = "Moderate",
                colorUniformityPercent = 84,
                recommendedPricePerKg = 22.00,
                verificationStatus = VerificationStatus.AI_ASSESSED,
                inspectorNotes = "Grade B acceptable for regional wholesale markets.",
                timestamp = System.currentTimeMillis() - 2 * 3600 * 1000
            )

            val sample3 = InspectionRecord(
                reportId = "AO-2026-2910",
                lotNumber = "LOT-MH-2026-2910",
                procurementCenter = "Dindori Sub-Center, Nashik",
                inspectorName = "Suresh Shinde",
                cropName = "Onion",
                variety = "Nashik Red Onion",
                overallGrade = OnionGrade.REJECT,
                isFresh = false,
                confidencePercent = 96,
                diameterMm = 48.0f,
                skinIntegrityPercent = 62,
                sproutingDetected = true,
                sproutLengthMm = 14.5f,
                rotFungalDetected = true,
                rotDescription = "Black mold (Aspergillus niger) visible at basal plate",
                isDoubleBulb = true,
                neckTightness = "Soft neck / Moist",
                colorUniformityPercent = 58,
                recommendedPricePerKg = 7.50,
                verificationStatus = VerificationStatus.AI_ASSESSED,
                inspectorNotes = "Rejected due to sprouting, twin bulb, and fungal presence.",
                timestamp = System.currentTimeMillis() - 5 * 3600 * 1000
            )

            dao.insertInspection(sample1)
            dao.insertInspection(sample2)
            dao.insertInspection(sample3)
        }
    }
}
