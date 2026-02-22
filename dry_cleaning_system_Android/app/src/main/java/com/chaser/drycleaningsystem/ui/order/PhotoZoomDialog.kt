package com.chaser.drycleaningsystem.ui.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import java.io.File

/**
 * 照片放大查看对话框
 */
@Composable
fun PhotoZoomDialog(
    photoPaths: List<String>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    
    // 获取当前图片路径（可能是原图或缩略图）
    val currentImagePath = photoPaths.getOrNull(currentIndex) ?: ""
    
    // 判断是缩略图还是原图，并获取对应的原图路径
    val currentOriginalPath = getOriginalImagePath(currentImagePath)
    
    // 添加调试日志
    android.util.Log.d("DRY CLEAN SYSTEM LOG", "PhotoZoomDialog: currentIndex=$currentIndex")
    android.util.Log.d("DRY CLEAN SYSTEM LOG", "PhotoZoomDialog: inputPath=$currentImagePath")
    android.util.Log.d("DRY CLEAN SYSTEM LOG", "PhotoZoomDialog: originalPath=$currentOriginalPath")
    android.util.Log.d("DRY CLEAN SYSTEM LOG", "PhotoZoomDialog: fileExists=${File(currentOriginalPath).exists()}")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            // 关闭按钮
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 图片查看器
            ZoomableImageViewer(
                imagePath = currentOriginalPath,
                modifier = Modifier.fillMaxSize()
            )
            
            // 多张图片时显示底部导航
            if (photoPaths.size > 1) {
                PhotoNavigationIndicator(
                    photoPaths = photoPaths,
                    currentIndex = currentIndex,
                    onPhotoSelected = { currentIndex = it }
                )
            }
        }
    }
}

/**
 * 可缩放的图片查看器
 */
@Composable
fun ZoomableImageViewer(
    imagePath: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    // 检查文件是否存在
    val fileExists = remember(imagePath) {
        File(imagePath).exists()
    }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(File(imagePath))
            .crossfade(false)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        onState = { state ->
            android.util.Log.d("DRY CLEAN SYSTEM LOG", "ZoomableImageViewer state: $state")
            when (state) {
                is AsyncImagePainter.State.Success -> {
                    isLoading = false
                    isError = false
                    android.util.Log.d("DRY CLEAN SYSTEM LOG", "ZoomableImageViewer: Image loaded successfully")
                }
                is AsyncImagePainter.State.Error -> {
                    isLoading = false
                    isError = true
                    errorMsg = state.result.throwable.message ?: "加载失败"
                    android.util.Log.e("DRY CLEAN SYSTEM LOG", "ZoomableImageViewer: Image load error: ${state.result.throwable.message}", state.result.throwable)
                }
                is AsyncImagePainter.State.Loading -> {
                    android.util.Log.d("DRY CLEAN SYSTEM LOG", "ZoomableImageViewer: Loading...")
                }
                else -> {}
            }
        }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (!fileExists) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "图片文件不存在",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = imagePath,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (isError) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "图片加载失败",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = errorMsg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Image(
                painter = painter,
                contentDescription = "放大图片",
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offsetX += pan.x
                            offsetY += pan.y
                            
                            // 当缩放比例小于 1 时，重置位置
                            if (scale <= 1f) {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }
    }
}

/**
 * 照片导航指示器
 */
@Composable
fun PhotoNavigationIndicator(
    photoPaths: List<String>,
    currentIndex: Int,
    onPhotoSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    
    // 当 currentIndex 变化时，滚动到对应位置
    LaunchedEffect(currentIndex) {
        listState.animateScrollToItem(currentIndex)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            tonalElevation = 4.dp
        ) {
            Column {
            // 图片计数
            Text(
                text = "${currentIndex + 1} / ${photoPaths.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            
            // 缩略图列表
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(photoPaths, key = { it }) { path ->
                    val thumbnailPath = getThumbnailPath(path)
                    val isSelected = photoPaths.indexOf(path) == currentIndex
                    
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable { onPhotoSelected(photoPaths.indexOf(path)) },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        shape = MaterialTheme.shapes.small,
                        shadowElevation = if (isSelected) 4.dp else 2.dp
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = "file://$thumbnailPath"
                            ),
                            contentDescription = "缩略图",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
            }
        }
        }
    }
}

/**
 * 获取原图路径（从缩略图路径转换）
 */
private fun getOriginalImagePath(imagePath: String): String {
    // 如果是缩略图路径（包含 _thumb.jpg），转换为原图路径
    return if (imagePath.contains("_thumb.jpg")) {
        imagePath.replace("_thumb.jpg", ".jpg")
    } else {
        // 已经是原图路径，直接返回
        imagePath
    }
}

/**
 * 获取缩略图路径
 */
private fun getThumbnailPath(originalPath: String): String {
    return if (originalPath.endsWith(".jpg") && !originalPath.contains("_thumb.jpg")) {
        originalPath.replace(".jpg", "_thumb.jpg")
    } else {
        originalPath
    }
}
