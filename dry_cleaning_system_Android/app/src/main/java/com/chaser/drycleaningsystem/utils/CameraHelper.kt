package com.chaser.drycleaningsystem.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

/**
 * 相机帮助类 - 处理拍照和缩略图生成
 */
class CameraHelper(private val context: Context) {

    private val photoDirectory: File by lazy {
        File(context.filesDir, "photos").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * 获取订单照片目录
     */
    fun getOrderPhotoDir(orderId: Long): File {
        return File(photoDirectory, "order_$orderId").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * 创建照片文件
     */
    fun createPhotoFile(orderId: Long): File {
        val dir = getOrderPhotoDir(orderId)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(dir, "IMG_$timestamp.jpg")
    }

    /**
     * 拍照
     */
    fun takePhoto(
        imageCapture: ImageCapture,
        orderId: Long,
        executor: Executor,
        onPhotoTaken: (photoPath: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val photoFile = createPhotoFile(orderId)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // 照片保存成功，生成缩略图
                    generateThumbnail(photoFile.absolutePath, orderId)
                    onPhotoTaken(photoFile.absolutePath)
                    Log.d(TAG, "Photo capture succeeded: ${photoFile.absolutePath}")
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    onError("拍照失败：${exception.message}")
                }
            }
        )
    }

    /**
     * 生成缩略图
     */
    fun generateThumbnail(photoPath: String, orderId: Long): String {
        return try {
            val originalBitmap = BitmapFactory.decodeFile(photoPath)
            
            // 计算旋转角度
            val matrix = Matrix()
            // 这里可以添加 EXIF 方向处理
            
            // 计算缩放比例
            val thumbnailSize = 200 // 200x200
            val scale = thumbnailSize.toFloat() / maxOf(originalBitmap.width, originalBitmap.height)
            
            // 生成缩略图
            val thumbnail = Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * scale).toInt(),
                (originalBitmap.height * scale).toInt(),
                true
            )
            
            // 保存缩略图
            val thumbnailFile = File(photoPath.replace(".jpg", "_thumb.jpg"))
            FileOutputStream(thumbnailFile).use { out ->
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            
            // 回收内存
            originalBitmap.recycle()
            thumbnail.recycle()
            
            thumbnailFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Thumbnail generation failed: ${e.message}", e)
            photoPath // 如果生成失败，返回原图路径
        }
    }

    /**
     * 删除订单的所有照片
     */
    fun deleteOrderPhotos(orderId: Long): Boolean {
        return try {
            val orderDir = getOrderPhotoDir(orderId)
            if (orderDir.exists()) {
                orderDir.deleteRecursively()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Delete photos failed: ${e.message}", e)
            false
        }
    }

    /**
     * 获取订单的所有照片缩略图
     */
    fun getOrderThumbnails(orderId: Long): List<String> {
        val orderDir = getOrderPhotoDir(orderId)
        if (!orderDir.exists()) return emptyList()
        
        return orderDir.listFiles { file ->
            file.name.endsWith("_thumb.jpg")
        }?.map { it.absolutePath } ?: emptyList()
    }

    companion object {
        private const val TAG = "CameraHelper"
    }
}
