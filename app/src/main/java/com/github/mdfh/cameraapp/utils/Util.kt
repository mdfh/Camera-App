package com.github.mdfh.cameraapp.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import android.util.Log

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Faraz on 1/22/2018.
 */
object Util {

    val MEDIA_TYPE_IMAGE = 1
    val MEDIA_TYPE_VIDEO = 2

    fun isCameraExist(context: Context): Boolean {
        return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            true
        } else {
            false
        }
    }

    fun isCameraExist(cameraId: Int): Boolean {
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == cameraId) {
                return true
            }
        }
        return false
    }

    fun getCameraInstance(cameraId: Int): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open(cameraId)
        } catch (e: Exception) {
            Log.d("TAG", "Open camera failed: " + e)
        }

        return c
    }

    /** Create a file Uri for saving an image or video  */
    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    /** Create a File for saving an image or video  */
    fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp")
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".jpg")
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                    "VID_" + timeStamp + ".mp4")
            try {
                mediaFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            return null
        }

        return mediaFile
    }
}
