package com.avelon.starystarycam;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private MyCameraDevice cameraDevice = null;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Surface
        surfaceView = (SurfaceView)findViewById(R.id.camera_preview);
        surfaceView.getHolder().addCallback(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View.OnClickListener) view -> {
            Log.e(TAG, "onClick()");
            Snackbar.make(view, "Click!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            try {
                cameraDevice.aquireLatestImage();
            }
            catch (IOException e) {
                Log.e(TAG, "exception", e);
            }

            ImageView mImageView;
            mImageView = (ImageView)findViewById(R.id.imageView);
            mImageView.setImageBitmap(BitmapFactory.decodeFile("/data/user/0/com.avelon.starystarycam/files/image.jpg"));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated()");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for(String id : cameraManager.getCameraIdList()) {
                Log.e(TAG, "id=" + id);

                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                for(CameraCharacteristics.Key key : characteristics.getKeys()) {
                    Log.e(TAG, "key=" + key);
                    Log.e(TAG, "value=" + characteristics.get(key));
                }
            }
            cameraManager.openCamera("0", cameraDevice = new MyCameraDevice(surfaceView, this.getFilesDir()), null);
        }
        catch (CameraAccessException e) {
            Log.e(TAG, "exception", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed()");
    }
}
