package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.OnionQualityAnalyzer
import com.example.data.local.AppDatabase
import com.example.data.local.InspectionRepository
import com.example.data.model.InspectionRecord
import com.example.data.model.OnionGrade
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class AppScreen {
    SPLASH,
    HOME,
    SMART_CAPTURE,
    PROCESSING,
    ANALYSIS_RESULT,
    ANALYTICS,
    QUALITY_REPORT,
    BATCH_HISTORY
}

enum class PipelineStage(val title: String, val subtitle: String) {
    PREPROCESSING("Preprocessing", "Edge detection, color calibration & normalization"),
    COMPUTER_VISION("Computer Vision", "Contour segmentation, diameter & skin analysis"),
    DEFECT_DETECTION("Defect Detection", "Sprouting, black mold, wrinkling & decay analysis"),
    COMPLETED("Finalizing Grade", "Standardized procurement rating compiled")
}

data class ProcessingUiState(
    val isProcessing: Boolean = false,
    val stage: PipelineStage = PipelineStage.PREPROCESSING,
    val progress: Float = 0f,
    val stageStatus: Map<PipelineStage, Boolean> = mapOf(
        PipelineStage.PREPROCESSING to false,
        PipelineStage.COMPUTER_VISION to false,
        PipelineStage.DEFECT_DETECTION to false,
        PipelineStage.COMPLETED to false
    )
)

class AgrigradeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InspectionRepository
    val inspections: StateFlow<List<InspectionRecord>>

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeLotNumber = MutableStateFlow("LOT-AG-2026-4142")
    val activeLotNumber: StateFlow<String> = _activeLotNumber.asStateFlow()

    private val _procurementCenter = MutableStateFlow("APMC Procurement Center #4")
    val procurementCenter: StateFlow<String> = _procurementCenter.asStateFlow()

    private val _inspectorName = MutableStateFlow("Inspector Rajesh Patil")
    val inspectorName: StateFlow<String> = _inspectorName.asStateFlow()

    private val _processingState = MutableStateFlow(ProcessingUiState())
    val processingState: StateFlow<ProcessingUiState> = _processingState.asStateFlow()

    private val _currentInspectedRecord = MutableStateFlow<InspectionRecord?>(null)
    val currentInspectedRecord: StateFlow<InspectionRecord?> = _currentInspectedRecord.asStateFlow()

    private val _currentImageBitmap = MutableStateFlow<Bitmap?>(null)
    val currentImageBitmap: StateFlow<Bitmap?> = _currentImageBitmap.asStateFlow()

    private val _selectedReport = MutableStateFlow<InspectionRecord?>(null)
    val selectedReport: StateFlow<InspectionRecord?> = _selectedReport.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = InspectionRepository(db.inspectionDao())
        inspections = repository.allInspections.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setLotDetails(lot: String, center: String, inspector: String) {
        _activeLotNumber.value = lot
        _procurementCenter.value = center
        _inspectorName.value = inspector
    }

    fun processCapturedImage(bitmap: Bitmap) {
        _currentImageBitmap.value = bitmap
        _currentScreen.value = AppScreen.PROCESSING

        viewModelScope.launch {
            // Save bitmap to cache dir
            var savedPath: String? = null
            runCatching {
                val file = File(getApplication<Application>().cacheDir, "scan_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                savedPath = file.absolutePath
            }

            // Animate realistic 3-stage computer vision pipeline (matching Slide 04)
            _processingState.value = ProcessingUiState(
                isProcessing = true,
                stage = PipelineStage.PREPROCESSING,
                progress = 0.25f,
                stageStatus = mapOf(
                    PipelineStage.PREPROCESSING to false,
                    PipelineStage.COMPUTER_VISION to false,
                    PipelineStage.DEFECT_DETECTION to false,
                    PipelineStage.COMPLETED to false
                )
            )
            delay(400)

            _processingState.value = ProcessingUiState(
                isProcessing = true,
                stage = PipelineStage.COMPUTER_VISION,
                progress = 0.65f,
                stageStatus = mapOf(
                    PipelineStage.PREPROCESSING to true,
                    PipelineStage.COMPUTER_VISION to false,
                    PipelineStage.DEFECT_DETECTION to false,
                    PipelineStage.COMPLETED to false
                )
            )
            delay(500)

            _processingState.value = ProcessingUiState(
                isProcessing = true,
                stage = PipelineStage.DEFECT_DETECTION,
                progress = 0.90f,
                stageStatus = mapOf(
                    PipelineStage.PREPROCESSING to true,
                    PipelineStage.COMPUTER_VISION to true,
                    PipelineStage.DEFECT_DETECTION to false,
                    PipelineStage.COMPLETED to false
                )
            )

            // Perform AI & Computer Vision Assessment
            val result = OnionQualityAnalyzer.analyzeOnion(
                bitmap = bitmap,
                lotNumber = _activeLotNumber.value,
                procurementCenter = _procurementCenter.value,
                inspectorName = _inspectorName.value,
                imagePath = savedPath
            )

            val insertedId = repository.saveInspection(result)
            val savedRecord = result.copy(id = insertedId)

            _processingState.value = ProcessingUiState(
                isProcessing = false,
                stage = PipelineStage.COMPLETED,
                progress = 1.0f,
                stageStatus = mapOf(
                    PipelineStage.PREPROCESSING to true,
                    PipelineStage.COMPUTER_VISION to true,
                    PipelineStage.DEFECT_DETECTION to true,
                    PipelineStage.COMPLETED to true
                )
            )
            delay(250)

            _currentInspectedRecord.value = savedRecord
            _selectedReport.value = savedRecord
            _currentScreen.value = AppScreen.ANALYSIS_RESULT
        }
    }

    fun verifyAndConfirmGrade(
        record: InspectionRecord,
        finalGrade: OnionGrade,
        notes: String
    ) {
        viewModelScope.launch {
            val isPassed = finalGrade != OnionGrade.REJECT
            val updated = record.copy(
                overallGrade = finalGrade,
                isPassed = isPassed,
                isFresh = isPassed,
                failureReasons = if (isPassed) "" else (record.failureReasons.ifBlank { "Manually marked as Failed / Reject by inspector." }),
                verificationStatus = if (finalGrade == record.overallGrade) VerificationStatus.HUMAN_VERIFIED else VerificationStatus.OVERRIDDEN,
                inspectorNotes = notes,
                recommendedPricePerKg = when (finalGrade) {
                    OnionGrade.GRADE_A -> 28.50
                    OnionGrade.GRADE_B -> 22.00
                    OnionGrade.REJECT -> 6.50
                }
            )
            repository.updateInspection(updated)
            _currentInspectedRecord.value = updated
            _selectedReport.value = updated
        }
    }

    // Allows inspector to modify any aspect of the quality report
    fun updateFullInspectionRecord(record: InspectionRecord) {
        viewModelScope.launch {
            repository.updateInspection(record)
            _currentInspectedRecord.value = record
            _selectedReport.value = record
        }
    }

    fun viewReport(record: InspectionRecord) {
        _selectedReport.value = record
        _currentScreen.value = AppScreen.QUALITY_REPORT
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteInspection(recordId)
            if (_selectedReport.value?.id == recordId) {
                _selectedReport.value = null
            }
        }
    }

    fun loadSampleOnion(sampleType: SampleOnionType) {
        viewModelScope.launch {
            val sampleBitmap = SampleOnionGenerator.generateSampleBitmap(sampleType)
            processCapturedImage(sampleBitmap)
        }
    }
}

enum class SampleOnionType(val label: String, val subtitle: String) {
    GOOD_POTATO_GRADE_A("Good Potato (Grade A)", "Russet Potato, smooth skin, firm, 0% sprouts or rot"),
    FAILED_POTATO_SPROUTED("Failed Potato (Reject)", "Sprouted eyes with green shoots, mold & wrinkled skin"),
    FRESH_GRADE_A_ONION("Grade A Onion", "Nashik Red, 55mm, 0% defects, tight dry skin"),
    REJECT_ONION_MOLD("Reject Onion (Moldy)", "Black mold & soft neck decay")
}

object SampleOnionGenerator {
    fun generateSampleBitmap(type: SampleOnionType): Bitmap {
        val width = 480
        val height = 480
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)

        val bgColor = android.graphics.Color.rgb(245, 242, 236)
        pixels.fill(bgColor)

        val centerX = 240
        val centerY = 240

        when (type) {
            SampleOnionType.GOOD_POTATO_GRADE_A -> {
                // Russet potato smooth skin (warm tan, zero defects, dormant eyes)
                val potatoColor = android.graphics.Color.rgb(195, 150, 95)
                val eyeColor = android.graphics.Color.rgb(160, 115, 65)

                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val dx = (x - centerX).toFloat() / 130f
                        val dy = (y - centerY).toFloat() / 170f
                        if (dx * dx + dy * dy <= 1.0f) {
                            pixels[y * width + x] = potatoColor
                        }
                    }
                }

                // Dormant shallow eyes (tiny dots, non-sprouted)
                val eyes = listOf(Pair(200, 180), Pair(275, 260), Pair(220, 330), Pair(290, 140))
                for ((ex, ey) in eyes) {
                    for (dy in -3..3) {
                        for (dx in -3..3) {
                            if (dx * dx + dy * dy <= 9) {
                                val py = ey + dy
                                val px = ex + dx
                                if (px in 0 until width && py in 0 until height) {
                                    pixels[py * width + px] = eyeColor
                                }
                            }
                        }
                    }
                }
            }

            SampleOnionType.FAILED_POTATO_SPROUTED -> {
                // Defective potato: shriveled body with active green sprouts, mold, rot
                val potatoColor = android.graphics.Color.rgb(165, 120, 75)
                val greenSproutColor = android.graphics.Color.rgb(45, 195, 30) // Prominent green sprout
                val moldWhiteColor = android.graphics.Color.rgb(205, 205, 200) // Surface mold
                val rotDarkColor = android.graphics.Color.rgb(28, 24, 18) // Dark rot / decay

                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val dx = (x - centerX).toFloat() / 140f
                        val dy = (y - centerY).toFloat() / 160f
                        if (dx * dx + dy * dy <= 1.0f) {
                            pixels[y * width + x] = potatoColor
                        }
                    }
                }

                // Active green sprouts erupting from eyes (multiple distinct clusters)
                val sproutCenters = listOf(
                    Pair(160, 150),
                    Pair(280, 90),
                    Pair(350, 230),
                    Pair(215, 380)
                )
                for ((sx, sy) in sproutCenters) {
                    for (dy in -18..18) {
                        for (dx in -18..18) {
                            if (dx * dx + dy * dy <= 324) {
                                val py = sy + dy
                                val px = sx + dx
                                if (px in 0 until width && py in 0 until height) {
                                    pixels[py * width + px] = greenSproutColor
                                }
                            }
                        }
                    }
                }

                // Mold patches
                val moldCenters = listOf(Pair(300, 310), Pair(320, 280))
                for ((mx, my) in moldCenters) {
                    for (dy in -20..20) {
                        for (dx in -20..20) {
                            if (dx * dx + dy * dy <= 400) {
                                val py = my + dy
                                val px = mx + dx
                                if (px in 0 until width && py in 0 until height) {
                                    pixels[py * width + px] = moldWhiteColor
                                }
                            }
                        }
                    }
                }

                // Necrotic dark decay spots
                val rotCenters = listOf(Pair(190, 300))
                for ((rx, ry) in rotCenters) {
                    for (dy in -14..14) {
                        for (dx in -14..14) {
                            if (dx * dx + dy * dy <= 196) {
                                val py = ry + dy
                                val px = rx + dx
                                if (px in 0 until width && py in 0 until height) {
                                    pixels[py * width + px] = rotDarkColor
                                }
                            }
                        }
                    }
                }
            }

            SampleOnionType.FRESH_GRADE_A_ONION -> {
                val onionRed = android.graphics.Color.rgb(180, 55, 60)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val dx = (x - centerX).toFloat() / 150f
                        val dy = (y - centerY).toFloat() / 145f
                        if (dx * dx + dy * dy <= 1.0f) {
                            pixels[y * width + x] = onionRed
                        }
                    }
                }
            }

            SampleOnionType.REJECT_ONION_MOLD -> {
                val onionRed = android.graphics.Color.rgb(140, 55, 60)
                val moldBlack = android.graphics.Color.rgb(20, 20, 20)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val dx = (x - centerX).toFloat() / 150f
                        val dy = (y - centerY).toFloat() / 145f
                        if (dx * dx + dy * dy <= 1.0f) {
                            pixels[y * width + x] = onionRed
                        }
                    }
                }
                for (dy in -25..25) {
                    for (dx in -25..25) {
                        if (dx * dx + dy * dy <= 625) {
                            val py = 300 + dy
                            val px = 210 + dx
                            if (px in 0 until width && py in 0 until height) {
                                pixels[py * width + px] = moldBlack
                            }
                        }
                    }
                }
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}
