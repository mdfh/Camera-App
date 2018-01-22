package com.github.mdfh.cameraapp.camera

/**
 * Created by Faraz on 1/18/2018.
 */

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager

import android.content.Context.WINDOW_SERVICE
import javax.inject.Inject
import javax.inject.Singleton

/** A basic Camera preview class  */
@Singleton
class CameraPreview @Inject constructor(private val mContext: Context) : SurfaceView(mContext), SurfaceHolder.Callback {

    private lateinit var  mHolder: SurfaceHolder
    private var isPreviewRunning: Boolean = false
    private var mSurfaceHolder: SurfaceHolder? = null

    private var mCamera: Camera? = null

    fun init(camera: Camera)
    {
        this.mCamera = camera
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = holder
        mHolder.addCallback(this)
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    fun previewCamera() {
        try {
            mCamera!!.setPreviewDisplay(mSurfaceHolder)
            mCamera!!.startPreview()
            isPreviewRunning = true
        } catch (e: Exception) {
            Log.d(TAG, "Cannot start preview", e)
        }

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        mSurfaceHolder = holder
        previewCamera()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        try {
            mCamera!!.stopPreview()
            isPreviewRunning = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (isPreviewRunning) {
            mCamera!!.stopPreview()
        }
        Log.d(TAG, "Test")
        val parameters = mCamera!!.parameters
        val display = (mContext.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay

        if (display.rotation == Surface.ROTATION_0) {
            parameters.setPreviewSize(height, width)
            mCamera!!.setDisplayOrientation(90)
        }

        if (display.rotation == Surface.ROTATION_90) {
            parameters.setPreviewSize(width, height)
        }

        if (display.rotation == Surface.ROTATION_180) {
            parameters.setPreviewSize(height, width)
        }

        if (display.rotation == Surface.ROTATION_270) {
            parameters.setPreviewSize(width, height)
            mCamera!!.setDisplayOrientation(180)
        }

        mCamera!!.parameters = parameters
        previewCamera()
    }

    companion object {
        private val TAG = "CameraPreview"
    }

    fun getCamera(): Camera? {
        return mCamera
    }
}
