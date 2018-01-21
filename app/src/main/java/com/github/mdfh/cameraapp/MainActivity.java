package com.github.mdfh.cameraapp;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mdfh.R;

/**
 * TODO add keyguard when recording. (Cannot leave app when it is recording)
 */
public class MainActivity extends Activity  {

    enum CameraType
    {
        FRONT, BACK
    }

    private boolean mHandlingEvent;
    private ImageButton mChangeCameraBtn;
    private ToggleButton mRecordingButton;
    private FrameLayout preview;
    private Camera mCamera;
    private CameraPreview mPreview;
    private CameraType cameraType = CameraType.FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = (FrameLayout) findViewById(R.id.camera_preview);

        mChangeCameraBtn = (ImageButton) findViewById(R.id.change_camera_btn);
        mChangeCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraType == CameraType.FRONT)
                    cameraType = CameraType.BACK;
                else
                    cameraType = CameraType.FRONT;

                changeCamera();
            }
        });

        mRecordingButton = (ToggleButton) findViewById(R.id.recording_button);
        mRecordingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mChangeCameraBtn.setEnabled(true);
                    stopRecording();
                } else {
                    mChangeCameraBtn.setEnabled(false);
                    startRecording();
                }
            }
        });

        if (!Util.isCameraExist(this)) {
            mChangeCameraBtn.setEnabled(false);
            mRecordingButton.setEnabled(false);

            TextView noCameraTextView = (TextView) findViewById(R.id.no_camera_text_view);
            noCameraTextView.setVisibility(View.VISIBLE);
        }

        changeCamera();
    }

    private void changeCamera() {
        // Create an instance of Camera
        mCamera = Util.getCameraInstance(getCameraId());
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview.removeAllViews();
        preview.addView(mPreview);
    }

    private void startRecording() {
        if (!mHandlingEvent) {
            mHandlingEvent = true;
            ResultReceiver receiver = new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    handleStartRecordingResult(resultCode, resultData);
                    mHandlingEvent = false;
                }
            };
            CameraService.startToStartRecording(this, getCameraId(), receiver);
        }
    }

    private int getCameraId()
    {
        if (cameraType == CameraType.FRONT) {
            return Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        return Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    private void stopRecording() {
        if (!mHandlingEvent) {
            mHandlingEvent = true;
            ResultReceiver receiver = new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    handleStopRecordingResult(resultCode, resultData);
                    mHandlingEvent = false;
                }
            };
            CameraService.startToStopRecording(this, receiver);
        }
    }

    private void handleStartRecordingResult(int resultCode, Bundle resultData) {
        if (resultCode == CameraService.RECORD_RESULT_OK) {
            Toast.makeText(this, "Start recording...", Toast.LENGTH_SHORT).show();
        } else {
            // start recording failed.
            Toast.makeText(this, "Start recording failed...", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleStopRecordingResult(int resultCode, Bundle resultData) {
        if (resultCode == CameraService.RECORD_RESULT_OK) {
            String videoPath = resultData.getString(CameraService.VIDEO_PATH);
            Toast.makeText(this, "Record succeed, file saved in " + videoPath,
                    Toast.LENGTH_LONG).show();
        } else if (resultCode == CameraService.RECORD_RESULT_UNSTOPPABLE) {
            Toast.makeText(this, "Stop recording failed...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Recording failed...", Toast.LENGTH_SHORT).show();
        }
    }
}
