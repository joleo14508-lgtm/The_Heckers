package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.ui.SampleOnionType
import com.example.ui.components.ReticleViewfinder
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

enum class FlashState(val label: String) {
    OFF("Flash Off"),
    ON("Flash Armed"),
    TORCH("Torch Always On")
}

@Composable
fun SmartCaptureScreen(
    onImageCaptured: (Bitmap) -> Unit,
    onBack: () -> Unit,
    onLoadSample: (SampleOnionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var hasCameraPermission by remember { mutableStateOf(false) }
    var flashState by remember { mutableStateOf(FlashState.OFF) }
    var isShutterFlashing by remember { mutableStateOf(false) }
    var activeCamera by remember { mutableStateOf<Camera?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Smart grading, 1: Every lot
    var showSamplePicker by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .build()
    }

    fun applyFlash(state: FlashState) {
        val isTorch = (state == FlashState.TORCH)
        val isFlashOn = (state == FlashState.ON || state == FlashState.TORCH)

        // 1. CameraX torch
        runCatching {
            activeCamera?.let { cam ->
                if (cam.cameraInfo.hasFlashUnit()) {
                    cam.cameraControl.enableTorch(isTorch)
                }
            }
        }

        // 2. Android CameraManager direct torch fallback
        runCatching {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? android.hardware.camera2.CameraManager
            cameraManager?.cameraIdList?.forEach { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                val hasFlash = chars.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                if (hasFlash) {
                    cameraManager.setTorchMode(id, isTorch)
                }
            }
        }

        // 3. ImageCapture flash mode
        imageCapture.flashMode = if (isFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgrigradeBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = HeadingsLogo
                )
            }

            Text(
                text = "Smart Capture",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = HeadingsLogo
            )

            IconButton(
                onClick = { showSamplePicker = !showSamplePicker }
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Presets",
                    tint = ButtonGreen
                )
            }
        }

        // Mode Pill Switcher (Smart grading / Every lot - from Slide 03)
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(20.dp))
                .background(LightLayer)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            Surface(
                color = if (selectedTab == 0) ButtonGreen else Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .clickable { selectedTab = 0 }
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "Smart grading",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 0) Color.White else TextMuted,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Surface(
                color = if (selectedTab == 1) ButtonGreen else Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .clickable { selectedTab = 1 }
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "Every lot",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 1) Color.White else TextMuted,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Viewfinder Frame with Reticle Brackets (mockup 03)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEFE8DB))
                .border(2.dp, ButtonGreen.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                val cam = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                                activeCamera = cam
                                applyFlash(flashState)
                            } catch (_: Exception) {}
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sample_onion_lot_1790773055908),
                        contentDescription = "Specimen target",
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Align specimen inside frame",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HeadingsLogo
                    )
                    Text(
                        text = "Camera access required for live optical stream",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Continuous Torch Screen Illumination / Ring Light
            if (flashState == FlashState.TORCH) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(8.dp, Color(0xFFFFF9C4), RoundedCornerShape(24.dp))
                        .background(Color(0x33FFFDE7))
                )
            }

            // Green Reticle Viewfinder overlay corners
            ReticleViewfinder(
                reticleColor = LeafGreen,
                bracketLength = 54.dp,
                strokeWidth = 4.dp
            )

            // Shutter Flash Strobe Overlay (Optical Xenon burst simulation)
            if (isShutterFlashing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }

            // Flash/Torch ON overlay indicator
            if (flashState != FlashState.OFF) {
                Surface(
                    color = Color(0xCC000000),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (flashState == FlashState.TORCH) Icons.Default.Highlight else Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (flashState == FlashState.TORCH) "TORCH ON" else "FLASH ON",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Helper guidance text (from mockup 03)
        Text(
            text = "Align the potato/onion bulb within the reticle frame",
            fontSize = 12.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        // Sample Quick Pick Row when toggled
        if (showSamplePicker) {
            Surface(
                color = LightLayer,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Instant Produce Test Presets:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeadingsLogo
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = ButtonGreen,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onLoadSample(SampleOnionType.GOOD_POTATO_GRADE_A) }
                                .padding(vertical = 6.dp)
                        ) {
                            Text("Good Potato (A)", fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Surface(
                            color = Color(0xFFFFCDD2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onLoadSample(SampleOnionType.FAILED_POTATO_SPROUTED) }
                                .padding(vertical = 6.dp)
                        ) {
                            Text("Failed Potato", fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        }
                        Surface(
                            color = SagePanel,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onLoadSample(SampleOnionType.FRESH_GRADE_A_ONION) }
                                .padding(vertical = 6.dp)
                        ) {
                            Text("Grade A Onion", fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = HeadingsLogo)
                        }
                    }
                }
            }
        }

        // Bottom Controls Row (mockup 03: Flash, Large Shutter Button, Preset menu)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Multi-state Flash & Torch toggle
            Surface(
                shape = CircleShape,
                color = when (flashState) {
                    FlashState.OFF -> LightLayer
                    FlashState.ON -> Color(0xFFFFF9C4)
                    FlashState.TORCH -> Color(0xFFFFE082)
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (flashState != FlashState.OFF) ButtonGreen else CardBorder
                ),
                modifier = Modifier
                    .size(52.dp)
                    .clickable {
                        val nextState = when (flashState) {
                            FlashState.OFF -> FlashState.ON
                            FlashState.ON -> FlashState.TORCH
                            FlashState.TORCH -> FlashState.OFF
                        }
                        flashState = nextState
                        applyFlash(nextState)

                        val msg = when (nextState) {
                            FlashState.ON -> "Flash Armed (fires on photo capture)"
                            FlashState.TORCH -> "Torch Enabled (continuous inspection light)"
                            FlashState.OFF -> "Flash Disabled"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    .testTag("flash_toggle_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (flashState) {
                            FlashState.OFF -> Icons.Default.FlashOff
                            FlashState.ON -> Icons.Default.FlashOn
                            FlashState.TORCH -> Icons.Default.Highlight
                        },
                        contentDescription = flashState.label,
                        tint = if (flashState != FlashState.OFF) Color(0xFFE65100) else TextMuted,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Big Shutter Button (White ring with ButtonGreen center)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, ButtonGreen, CircleShape)
                    .clickable {
                        // Flash strobe trigger on shutter
                        if (flashState != FlashState.OFF) {
                            isShutterFlashing = true
                            coroutineScope.launch {
                                delay(160)
                                isShutterFlashing = false
                            }
                        }

                        if (hasCameraPermission) {
                            val executor = Executors.newSingleThreadExecutor()
                            imageCapture.takePicture(
                                executor,
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        val buffer = image.planes[0].buffer
                                        val bytes = ByteArray(buffer.remaining())
                                        buffer.get(bytes)
                                        val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                        val rotation = image.imageInfo.rotationDegrees
                                        val rotated = if (rotation != 0) {
                                            val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                                            Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
                                        } else {
                                            rawBitmap
                                        }
                                        image.close()
                                        ContextCompat.getMainExecutor(context).execute {
                                            onImageCaptured(rotated)
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        ContextCompat.getMainExecutor(context).execute {
                                            onLoadSample(SampleOnionType.GOOD_POTATO_GRADE_A)
                                        }
                                    }
                                }
                            )
                        } else {
                            onLoadSample(SampleOnionType.GOOD_POTATO_GRADE_A)
                        }
                    }
                    .testTag("shutter_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(ButtonGreen)
                )
            }

            // Sample / Specimen quick selector
            Surface(
                shape = CircleShape,
                color = LightLayer,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .size(52.dp)
                    .clickable { showSamplePicker = !showSamplePicker }
                    .testTag("sample_picker_toggle")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sample Presets",
                        tint = ButtonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
