package com.avelon.starystarycam;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.view.SurfaceView;

public class MyImageCapture {
    private CaptureRequest request;

    public MyImageCapture(CameraDevice cameraDevice, ImageReader imageReader) throws CameraAccessException {
        CaptureRequest.Builder builder = cameraDevice.createCaptureRequest (CameraDevice.TEMPLATE_PREVIEW);
        builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
        builder.addTarget(imageReader.getSurface ());
        request = builder.build ();
    }

    public CaptureRequest getRequest() {
        return request;
    }
}
