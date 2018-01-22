package com.github.mdfh.cameraapp.ui.main

import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton

import com.github.mdfh.R
import com.github.mdfh.cameraapp.camera.CameraPreview
import com.github.mdfh.cameraapp.camera.service.CameraService
import com.github.mdfh.cameraapp.utils.Util
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * TODO add keyguard when recording. (Cannot leave app when it is recording)
 */
class MainActivity : AppCompatActivity() {

    private var mHandlingEvent: Boolean = false
    private var mChangeCameraBtn: ImageButton? = null
    private var mRecordingButton: ToggleButton? = null
    private var preview: FrameLayout? = null
    private var mCamera: Camera? = null
    private var cameraType = CameraType.BACK

    @Inject
    lateinit var mPreview: CameraPreview

    private val cameraId: Int
        get() = if (cameraType == CameraType.FRONT) {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        } else Camera.CameraInfo.CAMERA_FACING_BACK

    internal enum class CameraType {
        FRONT, BACK
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preview = findViewById<View>(R.id.camera_preview) as FrameLayout

        mChangeCameraBtn = findViewById<View>(R.id.change_camera_btn) as ImageButton
        mChangeCameraBtn!!.setOnClickListener {
            if (cameraType == CameraType.FRONT)
                cameraType = CameraType.BACK
            else
                cameraType = CameraType.FRONT

            changeCamera()
        }

        mRecordingButton = findViewById<View>(R.id.recording_button) as ToggleButton
        mRecordingButton!!.text = null
        mRecordingButton!!.textOn = null
        mRecordingButton!!.textOff = null
        mRecordingButton!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                mChangeCameraBtn!!.isEnabled = true
                stopRecording()
            } else {
                mChangeCameraBtn!!.isEnabled = false
                startRecording()
            }
        }

        if (!Util.isCameraExist(this)) {
            mChangeCameraBtn!!.isEnabled = false
            mRecordingButton!!.isEnabled = false

            val noCameraTextView = findViewById<View>(R.id.no_camera_text_view) as TextView
            noCameraTextView.visibility = View.VISIBLE
        }

        changeCamera()
    }

    private fun changeCamera() {
        // Create an instance of Camera
        mCamera = Util.getCameraInstance(cameraId)
        // Create our Preview view and set it as the content of our activity.
        mPreview.init(mCamera!!)
        preview!!.removeAllViews()
        preview!!.addView(mPreview)
        preview!!.invalidate()
    }

    private fun startRecording() {
        if (!mHandlingEvent) {
            mHandlingEvent = true
            val receiver = object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    handleStartRecordingResult(resultCode, resultData)
                    mHandlingEvent = false
                }
            }
            CameraService.startToStartRecording(this, cameraId, receiver)
        }
    }

    private fun stopRecording() {
        if (!mHandlingEvent) {
            mHandlingEvent = true
            val receiver = object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    handleStopRecordingResult(resultCode, resultData)
                    mHandlingEvent = false
                }
            }
            CameraService.startToStopRecording(this, receiver)
        }
    }

    private fun handleStartRecordingResult(resultCode: Int, resultData: Bundle) {
        if (resultCode == CameraService.RECORD_RESULT_OK) {
            Toast.makeText(this, "Start recording...", Toast.LENGTH_SHORT).show()
        } else {
            // start recording failed.
            Toast.makeText(this, "Start recording failed...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleStopRecordingResult(resultCode: Int, resultData: Bundle) {
        if (resultCode == CameraService.RECORD_RESULT_OK) {
            val videoPath = resultData.getString(CameraService.VIDEO_PATH)
            Toast.makeText(this, "Record succeed, file saved in " + videoPath!!,
                    Toast.LENGTH_LONG).show()
        } else if (resultCode == CameraService.RECORD_RESULT_UNSTOPPABLE) {
            Toast.makeText(this, "Stop recording failed...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Recording failed...", Toast.LENGTH_SHORT).show()
        }
    }
}
