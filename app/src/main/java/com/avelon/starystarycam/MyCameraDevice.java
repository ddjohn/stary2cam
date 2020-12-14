package com.avelon.starystarycam;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MyCameraDevice extends CameraDevice.StateCallback {
    private static final String TAG = MyCameraDevice.class.getCanonicalName();

    private final File filesDir;
    private SurfaceView surfaceView;
    private ImageReader imageReader;

    public MyCameraDevice(SurfaceView surfaceView, File filesDir) {
        this.surfaceView = surfaceView;
        this.filesDir = filesDir;
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        Log.i(TAG, "onOpened(): " + camera);
        Log.i(TAG, "" + camera.getId());

        Log.i(TAG, "Create image reader");
        imageReader = ImageReader.newInstance(1920, 1088, ImageFormat.JPEG, 2 /* images buffered */);
        imageReader.setOnImageAvailableListener(reader -> {
            Log.e(TAG, "onImageAvailable(): " + reader);
        }, new Handler());
        Log.d(TAG, "imageReader created");

        try {
            List<Surface> surfaces = new ArrayList<Surface>();
            surfaces.add(surfaceView.getHolder().getSurface());
            surfaces.add(imageReader.getSurface());
            camera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "onConfigured(): " + session);
                    try {
                        MyImageCapture capture1 = new MyImageCapture(camera, imageReader);
                        session.setRepeatingRequest(capture1.getRequest(), null, new MyHandler("image"));

                        MyCameraCapture capture2 = new MyCameraCapture(camera, surfaceView);
                        session.setRepeatingRequest(capture2.getRequest(), null, new MyHandler("camera"));
                    }
                    catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.i(TAG, "onConfiguredFailed()");
                }
            }, new MyHandler("session"));
        }
        catch (CameraAccessException e) {
            Log.e(TAG, "exception", e);
        }
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        Log.e(TAG, "onDisconnected(): " + camera);
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        Log.e(TAG, "onError(): " + camera);
    }

    public void aquireLatestImage() throws IOException {
        Log.e(TAG, "aquireLatestImage()");
        Image img = imageReader.acquireLatestImage();
        if (img != null) {
            processImage(img);
            img.close();
        }
    }

    private void processImage(Image image) throws IOException {
        Log.e(TAG, "processImage()");

        File file = new File(filesDir, "image.jpg");
        Log.e(TAG, "file: " + file.getAbsolutePath());

        if(image.getFormat() != ImageFormat.JPEG) {
            return;
        }

        ByteBuffer buf = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buf.remaining()];
        buf.get(bytes);

        try {
            FileOutputStream output = new FileOutputStream(file);
            output.write(bytes);
            output.close();
        }
        finally {
            image.close();
        }
    }
}

