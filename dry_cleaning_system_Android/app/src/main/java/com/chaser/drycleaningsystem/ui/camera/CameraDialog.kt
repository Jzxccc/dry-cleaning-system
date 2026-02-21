package com.chaser.drycleaningsystem.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.chaser.drycleaningsystem.utils.CameraHelper
import java.io.File
import java.util.concurrent.Executor

/**
 * 拍照对话框 - 全屏相机预览
 */
@Composable
fun CameraDialog(
    orderId: Long,
    onPhotoTaken: (photoPath: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cameraHelper = remember { CameraHelper(context) }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var executor by remember { mutableStateOf<Executor?>(null) }
    var showPreview by remember { mutableStateOf(false) }
    var capturedPhotoPath by remember { mutableStateOf<String?>(null) }

    // 权限请求
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // 请求权限
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            color = Color.Black
        ) {
            if (!hasCameraPermission) {
                // 权限被拒绝
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "需要相机权限才能拍照",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("取消", color = Color.White)
                        }
                        Button(onClick = {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }) {
                            Text("授予权限")
                        }
                    }
                }
            } else {
                // 相机预览
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CameraPreview(
                        onImageCaptured = { imageCaptureInstance, exec ->
                            imageCapture = imageCaptureInstance
                            executor = exec
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // 顶部栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "拍照记录衣物",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // 底部拍照按钮
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showPreview && capturedPhotoPath != null) {
                            // 已拍照，显示确认/重拍按钮
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // 重拍
                                IconButton(
                                    onClick = {
                                        showPreview = false
                                        capturedPhotoPath = null
                                    },
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "重拍",
                                        tint = Color.White
                                    )
                                }

                                // 确认使用
                                IconButton(
                                    onClick = {
                                        capturedPhotoPath?.let { path ->
                                            onPhotoTaken(path)
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "使用",
                                        tint = Color.White
                                    )
                                }
                            }
                        } else {
                            // 拍照按钮
                            IconButton(
                                onClick = {
                                    imageCapture?.let { capture ->
                                        executor?.let { exec ->
                                            cameraHelper.takePhoto(
                                                imageCapture = capture,
                                                orderId = orderId,
                                                executor = exec,
                                                onPhotoTaken = { path ->
                                                    capturedPhotoPath = path
                                                    showPreview = true
                                                },
                                                onError = { error ->
                                                    // TODO: 显示错误提示
                                                }
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "拍照",
                                    tint = Color.Black,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
