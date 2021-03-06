/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.photoaffix.engine.bitmaps

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapFactory.decodeStream
import android.net.Uri
import com.afollestad.photoaffix.engine.photos.Photo
import com.afollestad.photoaffix.utilities.IoManager
import com.afollestad.photoaffix.utilities.ext.closeQuietely
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

/** @author Aidan Follestad (afollestad) */
interface BitmapManipulator {

  fun decodePhoto(
    photo: Photo,
    options: Options
  ): Bitmap?

  fun decodeUri(
    uri: Uri,
    options: Options
  ): Bitmap?

  fun createOptions(onlyGetBounds: Boolean = true): Options

  fun createEmptyBitmap(
    width: Int,
    height: Int
  ): Bitmap

  fun encodeBitmap(
    bitmap: Bitmap,
    format: CompressFormat,
    quality: Int,
    file: File
  )
}

class RealBitmapManipulator @Inject constructor(
  private val ioManager: IoManager
) : BitmapManipulator {

  override fun decodePhoto(
    photo: Photo,
    options: Options
  ) = decodeUri(photo.uri, options)

  override fun decodeUri(
    uri: Uri,
    options: Options
  ): Bitmap? {
    var inputStream: InputStream? = null
    try {
      inputStream = ioManager.openStream(uri)
      return decodeStream(inputStream, null, options)
    } finally {
      inputStream.closeQuietely()
    }
  }

  override fun createOptions(onlyGetBounds: Boolean) = Options()
      .apply {
        inJustDecodeBounds = onlyGetBounds
      }

  override fun createEmptyBitmap(
    width: Int,
    height: Int
  ): Bitmap = Bitmap.createBitmap(width, height, ARGB_8888)

  override fun encodeBitmap(
    bitmap: Bitmap,
    format: CompressFormat,
    quality: Int,
    file: File
  ) {
    var os: FileOutputStream? = null
    try {
      os = FileOutputStream(file)
      bitmap.compress(format, quality, os)
    } finally {
      os.closeQuietely()
    }
  }
}
