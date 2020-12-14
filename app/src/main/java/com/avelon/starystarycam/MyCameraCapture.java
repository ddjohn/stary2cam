package com.avelon.starystarycam;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.view.SurfaceView;

public class MyCameraCapture {
    private CaptureRequest request;

    public MyCameraCapture(CameraDevice cameraDevice, SurfaceView surfaceView) throws CameraAccessException {
        CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        builder.addTarget(surfaceView.getHolder().getSurface());
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        request = builder.build();
    }

    public CaptureRequest getRequest() {
        return request;
    }
}
