package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.OnionQualityAnalyzer
import com.example.data.model.InspectionRecord
import com.example.data.model.OnionGrade
import com.example.data.model.SizeCategory
import com.example.data.model.VerificationStatus
import com.example.ui.SampleOnionGenerator
import com.example.ui.SampleOnionType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Agrigrade", appName)
    }

    @Test
    fun `inspection record initialization and digital hash`() {
        val record = InspectionRecord(
            reportId = "AO-2026-4142",
            lotNumber = "LOT-MH-2026-4142",
            overallGrade = OnionGrade.GRADE_A,
            diameterMm = 55.4f,
            sizeCategory = SizeCategory.MEDIUM,
            confidencePercent = 94
        )
        assertEquals("AO-2026-4142", record.reportId)
        assertEquals(OnionGrade.GRADE_A, record.overallGrade)
        assertTrue(record.digitalVerificationHash().startsWith("AGRIG-AI-2.3-"))
    }

    @Test
    fun `sample generator produces valid bitmap for all specimen types`() {
        SampleOnionType.values().forEach { type ->
            val bitmap = SampleOnionGenerator.generateSampleBitmap(type)
            assertNotNull(bitmap)
            assertEquals(480, bitmap.width)
            assertEquals(480, bitmap.height)
        }
    }

    @Test
    fun `good potato sample analysis passes with Grade A`() = runBlocking {
        val goodBitmap = SampleOnionGenerator.generateSampleBitmap(SampleOnionType.GOOD_POTATO_GRADE_A)
        val result = OnionQualityAnalyzer.analyzeOnion(goodBitmap)

        assertEquals(OnionGrade.GRADE_A, result.overallGrade)
        assertTrue("Expected good specimen to be marked isPassed = true", result.isPassed)
        assertTrue("Expected good specimen to be marked isFresh = true", result.isFresh)
        assertFalse("Expected no sprouting in good specimen", result.sproutingDetected)
        assertFalse("Expected no rot in good specimen", result.rotFungalDetected)
    }

    @Test
    fun `failed potato sample analysis rejects with explicit failure reasons`() = runBlocking {
        val failedBitmap = SampleOnionGenerator.generateSampleBitmap(SampleOnionType.FAILED_POTATO_SPROUTED)
        val result = OnionQualityAnalyzer.analyzeOnion(failedBitmap)

        assertEquals(OnionGrade.REJECT, result.overallGrade)
        assertFalse("Expected defective specimen to fail inspection", result.isPassed)
        assertFalse("Expected defective specimen to not be fresh", result.isFresh)
        assertTrue("Expected non-empty failure reasons", result.failureReasons.isNotBlank())
        assertTrue("Expected sprouting or fungal or wrinkling in failure reasons",
            result.failureReasons.contains("sprout", ignoreCase = true) ||
            result.failureReasons.contains("wrinkle", ignoreCase = true) ||
            result.failureReasons.contains("mold", ignoreCase = true) ||
            result.failureReasons.contains("Failed", ignoreCase = true)
        )
    }

    @Test
    fun `potato on marble countertop background analysis passes with Grade A`() = runBlocking {
        // Construct a bitmap simulating a potato on a white/gray marble kitchen countertop with olive oil dish
        val width = 480
        val height = 480
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)

        // Marble countertop texture (neutral off-white / light gray with vein variations)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val noise = ((x * 7 + y * 13) % 15) - 7
                val base = 215 + noise
                pixels[y * width + x] = android.graphics.Color.rgb(base, base, base)
            }
        }

        // Clean sound russet potato in center
        val centerX = 240
        val centerY = 240
        val potatoColor = android.graphics.Color.rgb(195, 145, 90) // Warm russet tan
        for (y in 0 until height) {
            for (x in 0 until width) {
                val dx = (x - centerX).toFloat() / 125f
                val dy = (y - centerY).toFloat() / 165f
                if (dx * dx + dy * dy <= 1.0f) {
                    pixels[y * width + x] = potatoColor
                }
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        val result = OnionQualityAnalyzer.analyzeOnion(bitmap)

        assertEquals("Expected clean potato on marble to be Grade A", OnionGrade.GRADE_A, result.overallGrade)
        assertTrue("Expected isPassed to be true", result.isPassed)
        assertTrue("Expected isFresh to be true", result.isFresh)
        assertFalse("Expected no sprouting", result.sproutingDetected)
        assertFalse("Expected no fungal rot", result.rotFungalDetected)
    }
}
