package com.avelon.starystarycam;

import android.graphics.BitmapFactory;
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
import android.widget.ImageView;

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
    private CameraDevice camera;
    private static CameraCaptureSession session;
    private ImageView imageView;

    public MyCameraDevice(SurfaceView surfaceView, File filesDir) {
        this.surfaceView = surfaceView;
        this.filesDir = filesDir;
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        Log.d(TAG, "onOpened(): " + camera);
        this.camera = camera;

        Log.i(TAG, "Create image reader");
        imageReader = ImageReader.newInstance(1920, 1088, ImageFormat.JPEG, 2);
        imageReader.setOnImageAvailableListener(reader -> {

            Log.i(TAG, "onImageAvailable(): " + reader);
            aquireLatestImage();

            imageView.setImageBitmap(BitmapFactory.decodeFile("/data/user/0/com.avelon.starystarycam/files/image.jpg"));

            startCameraSession(session, camera);

        }, new MyHandler("image-available"));
        Log.i(TAG, "imageReader created");

        try {
            List<Surface> surfaces = new ArrayList<Surface>();
            surfaces.add(surfaceView.getHolder().getSurface());
            surfaces.add(imageReader.getSurface());
            camera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "onConfigured(): " + session);

                    MyCameraDevice.session = session;
                    startCameraSession(session, camera);
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
        Log.i(TAG, "onDisconnected(): " + camera);
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        Log.i(TAG, "onError(): " + camera);
    }

    public void aquireLatestImage() {
        Log.d(TAG, "aquireLatestImage()");

        Image img = imageReader.acquireLatestImage();
        if (img != null) {
            processImage(img);
        }
    }

    private void processImage(Image image) {
        Log.d(TAG, "processImage()");

        File file = new File(filesDir, "image.jpg");
        Log.i(TAG, "file: " + file.getAbsolutePath());

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
        catch(IOException e) {
            Log.e(TAG, "exception", e);
        }
        finally {
            image.close();
        }
    }

    public void snap(ImageView mImageView) {
        Log.d(TAG, "snap()");
        this.imageView = mImageView;
        
        startImageSession();
    }

    private void startImageSession() {
        try {
            MyImageCapture capture = new MyImageCapture(camera, imageReader);
            session.setRepeatingRequest(capture.getRequest(), null, new MyHandler("image"));
        }
        catch (CameraAccessException e) {
            Log.e(TAG, "exception", e);
        }
    }

    private void startCameraSession(@NonNull CameraCaptureSession session, @NonNull CameraDevice camera) {
        try {
            MyCameraCapture capture = new MyCameraCapture(camera, surfaceView);
            session.setRepeatingRequest(capture.getRequest(), null, new MyHandler("camera"));
        }
        catch (CameraAccessException e) {
            Log.e(TAG, "exception", e);
        }
    }
}

