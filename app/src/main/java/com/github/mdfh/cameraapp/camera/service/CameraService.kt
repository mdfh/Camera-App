package com.github.mdfh.cameraapp.camera.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.util.Log
import android.view.SurfaceHolder
import android.view.WindowManager
import com.github.mdfh.cameraapp.camera.CameraPreview
import com.github.mdfh.cameraapp.utils.Util
import dagger.android.DaggerService

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class CameraService : DaggerService() {

    private var mCamera: Camera? = null
    private var mMediaRecorder: MediaRecorder? = null
    private var mRecording = false
    private var mRecordingPath: String? = null

    @Inject
    lateinit var mPreview: CameraPreview

    /**
     * Used to take picture.
     */
    private val mPicture = Camera.PictureCallback { data, camera ->
        val pictureFile = Util.getOutputMediaFile(Util.MEDIA_TYPE_IMAGE) ?: return@PictureCallback

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            throw IllegalStateException("Must start the service with intent")
        }
        when (intent.getIntExtra(START_SERVICE_COMMAND, COMMAND_NONE)) {
            COMMAND_START_RECORDING -> handleStartRecordingCommand(intent)
            COMMAND_STOP_RECORDING -> handleStopRecordingCommand(intent)
            else -> throw UnsupportedOperationException("Cannot start service with illegal commands")
        }

        return Service.START_NOT_STICKY
    }

    private fun handleStartRecordingCommand(intent: Intent) {
        if (!Util.isCameraExist(this)) {
            throw IllegalStateException("There is no device, not possible to start recording")
        }

        val resultReceiver = intent.getParcelableExtra<ResultReceiver>(RESULT_RECEIVER)

        if (mRecording) {
            // Already recording
            resultReceiver.send(RECORD_RESULT_ALREADY_RECORDING, null)
            return
        }
        mRecording = true

        val cameraId = intent.getIntExtra(SELECTED_CAMERA_FOR_RECORDING,
                Camera.CameraInfo.CAMERA_FACING_BACK)
        mCamera = Util.getCameraInstance(cameraId)
        if (mCamera != null) {

            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val params = WindowManager.LayoutParams(1, 1,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT)

            val sh = mPreview.holder

            mPreview.setZOrderOnTop(true)
            sh.setFormat(PixelFormat.TRANSPARENT)

            sh.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    val params = mCamera!!.parameters
                    mCamera!!.parameters = params
                    val p = mCamera!!.parameters

                    var listSize: List<Camera.Size>

                    listSize = p.supportedPreviewSizes
                    val mPreviewSize = listSize[2]
                    Log.v(TAG, "preview width = " + mPreviewSize.width
                            + " preview height = " + mPreviewSize.height)
                    p.setPreviewSize(mPreviewSize.width, mPreviewSize.height)

                    listSize = p.supportedPictureSizes
                    val mPictureSize = listSize[2]
                    Log.v(TAG, "capture width = " + mPictureSize.width
                            + " capture height = " + mPictureSize.height)
                    p.setPictureSize(mPictureSize.width, mPictureSize.height)
                    mCamera!!.parameters = p

                    try {
                        mCamera!!.setPreviewDisplay(holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    mCamera!!.startPreview()

                    mCamera!!.unlock()

                    mMediaRecorder = MediaRecorder()
                    mMediaRecorder!!.setCamera(mCamera)

                    mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                    mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)

                    if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        mMediaRecorder!!.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
                    } else {
                        mMediaRecorder!!.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P))
                    }

                    mRecordingPath = Util.getOutputMediaFile(Util.MEDIA_TYPE_VIDEO)!!.path
                    mMediaRecorder!!.setOutputFile(mRecordingPath)

                    mMediaRecorder!!.setPreviewDisplay(holder.surface)

                    try {
                        mMediaRecorder!!.prepare()
                    } catch (e: IllegalStateException) {
                        Log.d(TAG, "IllegalStateException when preparing MediaRecorder: " + e.message)
                    } catch (e: IOException) {
                        Log.d(TAG, "IOException when preparing MediaRecorder: " + e.message)
                    }

                    mMediaRecorder!!.start()

                    resultReceiver.send(RECORD_RESULT_OK, null)
                    Log.d(TAG, "Recording is started")
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                override fun surfaceDestroyed(holder: SurfaceHolder) {}
            })


            wm.addView(mPreview, params)

        } else {
            Log.d(TAG, "Get Camera from service failed")
            resultReceiver.send(RECORD_RESULT_GET_CAMERA_FAILED, null)
        }
    }

    private fun handleStopRecordingCommand(intent: Intent) {
        val resultReceiver = intent.getParcelableExtra<ResultReceiver>(RESULT_RECEIVER)

        if (!mRecording) {
            // have not recorded
            resultReceiver.send(RECORD_RESULT_NOT_RECORDING, null)
            return
        }

        try {
            mMediaRecorder!!.stop()
            mMediaRecorder!!.release()
        } catch (e: RuntimeException) {
            mMediaRecorder!!.reset()
            resultReceiver.send(RECORD_RESULT_UNSTOPPABLE, Bundle())
            return
        } finally {
            mMediaRecorder = null
            mCamera!!.stopPreview()
            mCamera!!.release()

            mRecording = false
        }

        val b = Bundle()
        b.putString(VIDEO_PATH, mRecordingPath)
        resultReceiver.send(RECORD_RESULT_OK, b)

        Log.d(TAG, "recording is finished.")
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    companion object {
        private val TAG = "CameraService"

        val RESULT_RECEIVER = "resultReceiver"
        val VIDEO_PATH = "recordedVideoPath"

        val RECORD_RESULT_OK = 0
        val RECORD_RESULT_DEVICE_NO_CAMERA = 1
        val RECORD_RESULT_GET_CAMERA_FAILED = 2
        val RECORD_RESULT_ALREADY_RECORDING = 3
        val RECORD_RESULT_NOT_RECORDING = 4
        val RECORD_RESULT_UNSTOPPABLE = 5

        private val START_SERVICE_COMMAND = "startServiceCommands"
        private val COMMAND_NONE = -1
        private val COMMAND_START_RECORDING = 0
        private val COMMAND_STOP_RECORDING = 1

        private val SELECTED_CAMERA_FOR_RECORDING = "cameraForRecording"

        fun startToStartRecording(context: Context, cameraId: Int,
                                  resultReceiver: ResultReceiver) {
            val intent = Intent(context, CameraService::class.java)
            intent.putExtra(START_SERVICE_COMMAND, COMMAND_START_RECORDING)
            intent.putExtra(SELECTED_CAMERA_FOR_RECORDING, cameraId)
            intent.putExtra(RESULT_RECEIVER, resultReceiver)
            context.startService(intent)
        }

        fun startToStopRecording(context: Context, resultReceiver: ResultReceiver) {
            val intent = Intent(context, CameraService::class.java)
            intent.putExtra(START_SERVICE_COMMAND, COMMAND_STOP_RECORDING)
            intent.putExtra(RESULT_RECEIVER, resultReceiver)
            context.startService(intent)
        }
    }
}
