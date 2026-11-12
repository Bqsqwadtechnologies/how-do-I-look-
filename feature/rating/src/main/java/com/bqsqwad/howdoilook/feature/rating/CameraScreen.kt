package com.bqsqwad.howdoilook.feature.rating

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onBack: () -> Unit,
    onPhotoCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            errorMessage = "Camera permission is needed to take a photo."
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take photo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (hasPermission) {
                        IconButton(
                            onClick = {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                    CameraSelector.LENS_FACING_BACK
                                } else {
                                    CameraSelector.LENS_FACING_FRONT
                                }
                            }
                        ) {
                            Icon(Icons.Default.Cameraswitch, contentDescription = "Switch camera")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            when {
                !hasPermission -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(48.dp))
                        Text(
                            "Camera access is required to capture your look.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("Grant permission")
                        }
                        errorMessage?.let {
                            Spacer(Modifier.height(12.dp))
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                else -> {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { previewView ->
                            bindCamera(
                                context = context,
                                lifecycleOwner = lifecycleOwner,
                                previewView = previewView,
                                imageCapture = imageCapture,
                                lensFacing = lensFacing,
                                onError = { errorMessage = it }
                            )
                        }
                    )

                    IconButton(
                        onClick = {
                            if (isCapturing) return@IconButton
                            isCapturing = true
                            takePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                executor = cameraExecutor,
                                onSuccess = { uri ->
                                    isCapturing = false
                                    onPhotoCaptured(uri)
                                },
                                onError = { msg ->
                                    isCapturing = false
                                    errorMessage = msg
                                }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp)
                            .size(80.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "Capture",
                            modifier = Modifier.size(36.dp),
                            tint = Color.Black
                        )
                    }

                    errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun bindCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    imageCapture: ImageCapture,
    lensFacing: Int,
    onError: (String) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val selector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            onError("Could not start camera: ${e.message ?: "unknown error"}")
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: ExecutorService,
    onSuccess: (Uri) -> Unit,
    onError: (String) -> Unit
) {
    val dir = File(context.cacheDir, "captures").apply { mkdirs() }
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val file = File(dir, "LOOK_$name.jpg")
    val output = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        output,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = try {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } catch (_: Exception) {
                    outputFileResults.savedUri ?: Uri.fromFile(file)
                }
                onSuccess(uri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError("Capture failed: ${exception.message ?: "unknown error"}")
            }
        }
    )
}
