package com.jangnal.gaja.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageCompressionHelper {

    fun compressImage(context: Context, imageUri: Uri, maxDimension: Int = 1024, quality: Int = 75): ByteArray? {
        val contentResolver = context.contentResolver
        var inputStream: InputStream? = null
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            inputStream = contentResolver.openInputStream(imageUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            val srcWidth = options.outWidth
            val srcHeight = options.outHeight
            var inSampleSize = 1
            if (srcWidth > maxDimension || srcHeight > maxDimension) {
                val halfWidth = srcWidth / 2
                val halfHeight = srcHeight / 2
                while ((halfWidth / inSampleSize) >= maxDimension && (halfHeight / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = inSampleSize
            }
            inputStream = contentResolver.openInputStream(imageUri)
            var bitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            inputStream?.close()

            if (bitmap == null) return null

            val orientation = getOrientation(context, imageUri)
            if (orientation != 0) {
                val matrix = Matrix().apply { postRotate(orientation.toFloat()) }
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                if (rotatedBitmap != bitmap) {
                    bitmap.recycle()
                    bitmap = rotatedBitmap
                }
            }

            val width = bitmap.width
            val height = bitmap.height
            var finalWidth = width
            var finalHeight = height
            if (width > maxDimension || height > maxDimension) {
                if (width > height) {
                    finalWidth = maxDimension
                    finalHeight = (maxDimension * height) / width
                } else {
                    finalHeight = maxDimension
                    finalWidth = (maxDimension * width) / height
                }
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
                if (scaledBitmap != bitmap) {
                    bitmap.recycle()
                    bitmap = scaledBitmap
                }
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val bytes = outputStream.toByteArray()
            bitmap.recycle()
            return bytes

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                inputStream?.close()
            } catch (_: Exception) {}
        }
    }

    private fun getOrientation(context: Context, uri: Uri): Int {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}
