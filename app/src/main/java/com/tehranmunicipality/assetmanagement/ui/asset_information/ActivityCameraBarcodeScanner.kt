package com.tehranmunicipality.assetmanagement.ui.asset_information

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.tehranmunicipality.assetmanagement.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class ActivityCameraBarcodeScanner : AppCompatActivity() {

    companion object {
        const val EXTRA_BARCODE = "barcodeResult"
        private const val TAG = "BarcodeScanner"
    }

    private lateinit var previewView: PreviewView
    private lateinit var scanner: BarcodeScanner

    private val cameraExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()

    private val isBarcodeDetected = AtomicBoolean(false)
    private val isProcessingFrame = AtomicBoolean(false)

    private var cameraProvider: ProcessCameraProvider? = null
    private var isActivityClosed = false

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                startCamera()
            } else {
                Log.e(TAG, "Camera permission denied")
                finishScanner()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera_barcode_scanner)

        previewView = findViewById(R.id.previewView)

        // Back فقط همین Activity اسکنر را می‌بندد.
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "Back pressed in scanner")
                    finishScanner()
                }
            }
        )

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODABAR,
                Barcode.FORMAT_ITF
            )
            .build()

        scanner = BarcodeScanning.getClient(options)

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {

        if (isFinishing || isDestroyed || isActivityClosed) return

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            {
                if (isFinishing || isDestroyed || isActivityClosed) {
                    return@addListener
                }

                try {
                    val provider = cameraProviderFuture.get()
                    cameraProvider = provider

                    val preview = Preview.Builder()
                        .build()

                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->

                        if (
                            isActivityClosed ||
                            isBarcodeDetected.get() ||
                            !isProcessingFrame.compareAndSet(false, true)
                        ) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        val mediaImage = imageProxy.image

                        if (mediaImage == null) {
                            isProcessingFrame.set(false)
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        try {
                            val inputImage = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            scanner.process(inputImage)
                                .addOnSuccessListener { barcodes ->

                                    if (isActivityClosed || isBarcodeDetected.get()) {
                                        return@addOnSuccessListener
                                    }

                                    val barcodeValue = barcodes
                                        .firstOrNull {
                                            !it.rawValue.isNullOrBlank()
                                        }
                                        ?.rawValue
                                        ?.trim()

                                    if (
                                        !barcodeValue.isNullOrBlank() &&
                                        isBarcodeDetected.compareAndSet(false, true)
                                    ) {
                                        runOnUiThread {
                                            if (
                                                !isFinishing &&
                                                !isDestroyed &&
                                                !isActivityClosed
                                            ) {
                                                returnBarcode(barcodeValue)
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(
                                        TAG,
                                        "Barcode detection failed",
                                        exception
                                    )
                                }
                                .addOnCompleteListener {
                                    isProcessingFrame.set(false)
                                    imageProxy.close()
                                }

                        } catch (exception: Exception) {
                            Log.e(TAG, "Frame processing error", exception)
                            isProcessingFrame.set(false)
                            imageProxy.close()
                        }
                    }

                    provider.unbindAll()

                    provider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )

                    Log.d(TAG, "Camera started successfully")

                } catch (exception: Exception) {
                    Log.e(TAG, "Camera initialization failed", exception)
                    finishScanner()
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun returnBarcode(value: String) {

        if (isActivityClosed || isFinishing || isDestroyed) return

        val cleanedBarcode = value.replace(Regex("\\s+"), "")

        if (cleanedBarcode.isBlank()) {
            isBarcodeDetected.set(false)
            return
        }

        Log.d(TAG, "Returning barcode: $cleanedBarcode")

        val resultIntent = Intent().apply {
            putExtra(EXTRA_BARCODE, cleanedBarcode)
        }

        isActivityClosed = true

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun finishScanner() {

        if (isActivityClosed) return

        isActivityClosed = true

        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onDestroy() {

        isActivityClosed = true

        try {
            cameraProvider?.unbindAll()
            cameraProvider = null
        } catch (exception: Exception) {
            Log.e(TAG, "Error while unbinding camera", exception)
        }

        if (::scanner.isInitialized) {
            try {
                scanner.close()
            } catch (exception: Exception) {
                Log.e(TAG, "Error while closing scanner", exception)
            }
        }

        cameraExecutor.shutdown()

        super.onDestroy()
    }
}