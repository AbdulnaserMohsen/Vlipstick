package com.luxand.mirrorreality;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLES11;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luxand.FSDK;
import com.luxand.MR;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.hdodenhof.circleimageview.CircleImageView;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    public static final String TAG = "MainRenderer";

    public static final int DEVICE_ORIENTATION_PORTRAIT = 0;
    public static final int DEVICE_ORIENTATION_INVERSE_PORTRAIT = 1;
    public static final int DEVICE_ORIENTATION_LANDSCAPE = 2;
    public static final int DEVICE_ORIENTATION_INVERSE_LANDSCAPE = 3;

    public volatile int deviceOrientation = DEVICE_ORIENTATION_PORTRAIT;


    private FSDK.HTracker mTracker;

    private int[] mTextures;

    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;
    private boolean mUpdateSurfaceTexture = false;

    private FSDK.FSDK_Features [] mTrackingFeatures;
    private MR.MaskFeatures mMaskCoords;
    private int [] mIsMaskTexture1Created = new int[] {0};
    private int [] mIsMaskTexture2Created = new int[] {0};

    private int mWidth;
    private int mHeight;

    private ByteBuffer mPixelBuffer;

    private FSDK.HImage mCameraImage = new FSDK.HImage();
    private FSDK.FSDK_IMAGEMODE mCameraImageMode = new FSDK.FSDK_IMAGEMODE();
    private FSDK.HImage mSnapshotImage = new FSDK.HImage();
    private FSDK.FSDK_IMAGEMODE mSnapshotImageMode = new FSDK.FSDK_IMAGEMODE();

    private MainView mMainView;
    private MainActivity mMainActivity;

    private volatile boolean mIsResizeCalled = false;
    private volatile boolean mIsResized = false;

    public long IDs[] = new long[MR.MAX_FACES];
    public long face_count[] = new long[1];

    private long mFrameCount = 0;
    private long mStartTime = 0;

    private AtomicBoolean isTakingSnapshot = new AtomicBoolean(false);

    public static final int [][] MASKS = new int[][]
    {
            {R.raw.glass1, R.drawable.color1, R.drawable.color1, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color2, R.drawable.color2, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color3, R.drawable.color3, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color4, R.drawable.color4, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color5, R.drawable.color5, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color6, R.drawable.color6, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color7, R.drawable.color7, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color8, R.drawable.color8, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color9, R.drawable.color9, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color10, R.drawable.color10, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color11, R.drawable.color11, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color12, R.drawable.color12, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color13, R.drawable.color13, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color14, R.drawable.color14, MR.SHIFT_TYPE_NO},
            {R.raw.glass1, R.drawable.color15, R.drawable.color15, MR.SHIFT_TYPE_NO},


    };

    private int mMask = 0;
    private int mMaskLoaded = 0;
    private volatile boolean isMaskChanged = false;

    public synchronized void nextMask() {
        mMask = (mMask+1) % MASKS.length;
        isMaskChanged = true;
    }

    public synchronized void previousMask() {
        --mMask;
        if (mMask < 0) mMask = MASKS.length - 1;
        isMaskChanged = true;
    }

    public synchronized void selectMask(int index)
    {
        mMask = index;
        isMaskChanged = true;
    }


    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }

    public MainRenderer(MainView view) {
        mTracker = Application.tracker;

        mMainView = view;
        mMainActivity = (MainActivity) mMainView.getContext();

        mTrackingFeatures = new FSDK.FSDK_Features[MR.MAX_FACES];
        for (int i=0; i<MR.MAX_FACES; ++i) {
            mTrackingFeatures[i] = new FSDK.FSDK_Features();
            for (int j=0; j<FSDK.FSDK_FACIAL_FEATURE_COUNT; ++j) {
                mTrackingFeatures[i].features[j] = new FSDK.TPoint();
            }
        }

        mMaskCoords = new MR.MaskFeatures();
    }

    public void close()
    {
        mUpdateSurfaceTexture = false;
        mSurfaceTexture.release();
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        deleteTex();
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) { //call opengl functions only inside these functions!
        Log.d(TAG, "surfaceCreated");
        mIsResizeCalled = false;
        mIsResized = false;

        initTex();

        loadMask(mMask);

        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        // Find the ID of the camera
        int cameraId = 0;
        boolean frontCameraFound = false;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            //if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                frontCameraFound = true;
            }
        }

        if (frontCameraFound) {
            mCamera = Camera.open(cameraId);
        } else {
            mCamera = Camera.open();
        }

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        GLES11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); //background color
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 16384;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    // must be called from the thread with OpenGL context!
    public void loadMask(int maskNumber) {
        GLES11.glDisable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        Log.d(TAG, "Loading mask...");

        int [] mask = MASKS[maskNumber];

        if (mIsMaskTexture1Created[0] > 0) {
            GLES11.glDeleteTextures(1, mTextures, 1);
        }
        if (mIsMaskTexture2Created[0] > 0) {
            GLES11.glDeleteTextures(1, mTextures, 2);
        }

        mIsMaskTexture1Created[0] = 0;
        mIsMaskTexture2Created[0] = 0;

        InputStream stream = mMainView.getResources().openRawResource(mask[0]);
        int res = MR.LoadMaskCoordsFromStream(stream, mMaskCoords);
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res != FSDK.FSDKE_OK) {
            Log.e(TAG, "Error loading mask coords from stream: " + res);
            GLES11.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
            return;
        }


        BitmapFactory.Options bitmapDecodingOptions = new BitmapFactory.Options();
        bitmapDecodingOptions.inScaled = false; // to load original image without scaling

        FSDK.HImage img1 = new FSDK.HImage();
        if (mask[1] == -1) { // if no image
            FSDK.CreateEmptyImage(img1);
        } else {
            stream = mMainView.getResources().openRawResource(0 + mask[1]);
            byte[] data = null;
            try {
                data = readBytes(stream);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (data != null) {
                res = FSDK.LoadImageFromPngBufferWithAlpha(img1, data, data.length);
                Log.d(TAG, "Load mask image of size " + data.length + " with result " + res);
                int [] w = new int[]{0};
                int [] h = new int[]{0};
                FSDK.GetImageWidth(img1, w);
                FSDK.GetImageHeight(img1, h);
                Log.d(TAG, "Mask image size: " + w[0] + " x " + h[0]);
            } else {
                Log.w(TAG, "Error loading mask image, using empty image");
                FSDK.CreateEmptyImage(img1);
            }
        }

        FSDK.HImage img2 = new FSDK.HImage();
        if (mask[2] == -1) { // if no normal image
            FSDK.CreateEmptyImage(img2);
        } else {
            stream = mMainView.getResources().openRawResource(0 + mask[2]);
            byte[] data = null;
            try {
                data = readBytes(stream);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (data != null) {
                res = FSDK.LoadImageFromPngBufferWithAlpha(img2, data, data.length);
                Log.d(TAG, "Load mask normal image of size " + data.length + " with result " + res);
                int [] w = new int[]{0};
                int [] h = new int[]{0};
                FSDK.GetImageWidth(img2, w);
                FSDK.GetImageHeight(img2, h);
                Log.d(TAG, "Mask normal image size: " + w[0] + " x " + h[0]);
            } else {
                Log.w(TAG, "Error loading mask normal image, using empty image");
                FSDK.CreateEmptyImage(img2);
            }
        }

        res = MR.LoadMask(img1, img2, mTextures[1], mTextures[2], mIsMaskTexture1Created, mIsMaskTexture2Created);
        FSDK.FreeImage(img1);
        FSDK.FreeImage(img2);

        Log.d(TAG, "Mask loaded with result " + res + " texture1Created:" + mIsMaskTexture1Created[0] + " texture2Created:" + mIsMaskTexture2Created[0]);
        Log.d(TAG, "Mask textures: " + mTextures[1] + " " + mTextures[2]);

        GLES11.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    public void onDrawFrame ( GL10 unused ) { //call opengl functions only inside these functions!
        GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT);

        if (!mIsResized) {
            return;
        }

        synchronized(this) {
            if (mUpdateSurfaceTexture) {
                mSurfaceTexture.updateTexImage();
                mUpdateSurfaceTexture = false;
            }
        }

        if (isMaskChanged) {
            mMaskLoaded = mMask;
            loadMask(mMask);
            isMaskChanged = false;
        }

        int rotation = 1;

        // First, drawing without mask to get image buffer
        int res = MR.DrawGLScene(mTextures[0], 0, mTrackingFeatures, rotation, MR.SHIFT_TYPE_NO, mTextures[1], mTextures[2], mMaskCoords, 0, 0, mWidth, mHeight);
        if (FSDK.FSDKE_OK != res) {
            Log.e(TAG, "Error in the first MR.DrawGLScene call: " + res);
        }

        face_count[0] = 0;

        int err = GLES11.glGetError(); //reset previous error
        GLES11.glReadPixels(0, 0, mWidth, mHeight, GLES11.GL_RGB, GLES11.GL_UNSIGNED_BYTE, mPixelBuffer);
        err = GLES11.glGetError();
        if (err == 0) {
            processCameraImage(FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT);
        } else { // not all devices support glReadPixels in rgb
            GLES11.glReadPixels(0, 0, mWidth, mHeight, GLES11.GL_RGBA, GLES11.GL_UNSIGNED_BYTE, mPixelBuffer);
            processCameraImage(FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_32BIT);
        }

        // Second, drawing with mask atop of image
        res = MR.DrawGLScene(mTextures[0], (int)face_count[0], mTrackingFeatures, rotation, MASKS[mMaskLoaded][3], mTextures[1], mTextures[2], mMaskCoords, mIsMaskTexture1Created[0], mIsMaskTexture2Created[0], mWidth, mHeight);
        if (FSDK.FSDKE_OK != res) {
            Log.e(TAG, "Error in the second MR.DrawGLScene call: " + res);
        }

        // Save snapshot if needed
        if (isTakingSnapshot.compareAndSet(true, false)) {
            GLES11.glReadPixels(0, 0, mWidth, mHeight, GLES11.GL_RGBA, GLES11.GL_UNSIGNED_BYTE, mPixelBuffer);
            mSnapshotImageMode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_32BIT;
            res = FSDK.LoadImageFromBuffer(mSnapshotImage, mPixelBuffer.array(), mWidth, mHeight, mWidth*4, mSnapshotImageMode);
            if (FSDK.FSDKE_OK != res) {
                Log.e(TAG, "Error loading snapshot image to FaceSDK: " + res);
            } else {
                FSDK.MirrorImage(mSnapshotImage, false);

                String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                final String filename = galleryPath + "/youmask_" + System.currentTimeMillis() + ".png";
                res = FSDK.SaveImageToFile(mSnapshotImage, filename);
                Log.d(TAG, "saving snapshot to " + filename);

                FSDK.FreeImage(mSnapshotImage);

                if (FSDK.FSDKE_OK == res) {
                    mMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            File f = new File(filename);
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            mMainActivity.sendBroadcast(mediaScanIntent);

                            Toast.makeText(mMainActivity, "Saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        /*// Show fps
        ++mFrameCount;
        long timeCurrent = System.currentTimeMillis();
        if (mStartTime == 0) mStartTime = timeCurrent;
        long diff = timeCurrent - mStartTime;
        if (diff >= 3000) {
            final float fps = mFrameCount / (diff / 1000.0f);
            mFrameCount = 0;
            mStartTime = 0;

            final TextView fpsTextView = mMainActivity.fpsTextView();
            mMainActivity.fpsTextView().post(new Runnable() {
                @Override
                public void run() {
                    if (!mMainActivity.isFinishing()) {
                        fpsTextView.setText("" + fps + " FPS");
                    }
                }
            });
        }*/



    }

    private void processCameraImage(int imageMode) {
        //clear previous features
        for (int i=0; i<MR.MAX_FACES; ++i) {
            for (int j=0; j<FSDK.FSDK_FACIAL_FEATURE_COUNT; ++j) {
                mTrackingFeatures[i].features[j].x = 0;
                mTrackingFeatures[i].features[j].y = 0;
            }
        }

        int res = FSDK.FSDKE_FAILED;
        mCameraImageMode.mode = imageMode;
        if (imageMode == FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT) {
            res = FSDK.LoadImageFromBuffer(mCameraImage, mPixelBuffer.array(), mWidth, mHeight, mWidth * 3, mCameraImageMode);
        } else if (imageMode == FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_32BIT) {
            res = FSDK.LoadImageFromBuffer(mCameraImage, mPixelBuffer.array(), mWidth, mHeight, mWidth * 4, mCameraImageMode);
        }
        if (FSDK.FSDKE_OK != res) {
            Log.e(TAG, "Error loading camera image to FaceSDK: " + res);
            return;
        }

        FSDK.MirrorImage(mCameraImage, false);
        int [] widthByReference = new int[1];
        int [] heightByReference = new int[1];
        FSDK.GetImageWidth(mCameraImage, widthByReference);
        FSDK.GetImageHeight(mCameraImage, heightByReference);
        int width = widthByReference[0];
        int height = heightByReference[0];

        int rotation = 0;
        if (deviceOrientation == DEVICE_ORIENTATION_INVERSE_PORTRAIT) {
            rotation = 2;
        } else if (deviceOrientation == DEVICE_ORIENTATION_LANDSCAPE) {
            rotation = 3;
        } else if (deviceOrientation == DEVICE_ORIENTATION_INVERSE_LANDSCAPE) {
            rotation = 1;
        }

        if (rotation > 0) {
            FSDK.HImage rotated = new FSDK.HImage();
            FSDK.CreateEmptyImage(rotated);
            FSDK.RotateImage90(mCameraImage, rotation, rotated);
            FSDK.FeedFrame(mTracker, 0, rotated, face_count, IDs);
            FSDK.FreeImage(rotated);
        } else {
            FSDK.FeedFrame(mTracker, 0, mCameraImage, face_count, IDs);
        }

        for (int i = 0; i < (int) face_count[0]; ++i) {
            FSDK.GetTrackerFacialFeatures(mTracker, 0, IDs[i], mTrackingFeatures[i]);
            if (rotation > 0) {
                if (rotation == 1) {
                    for (int j = 0; j<FSDK.FSDK_FACIAL_FEATURE_COUNT; ++j) {
                        int x = mTrackingFeatures[i].features[j].x;
                        mTrackingFeatures[i].features[j].x = mTrackingFeatures[i].features[j].y;
                        mTrackingFeatures[i].features[j].y = height-1 -x;
                    }
                } else if (rotation == 2) {
                    for (int j = 0; j<FSDK.FSDK_FACIAL_FEATURE_COUNT; ++j) {
                        mTrackingFeatures[i].features[j].x = width-1 - mTrackingFeatures[i].features[j].x;
                        mTrackingFeatures[i].features[j].y = height-1 - mTrackingFeatures[i].features[j].y;
                    }
                } else if (rotation == 3) {
                    for (int j = 0; j<FSDK.FSDK_FACIAL_FEATURE_COUNT; ++j) {
                        int x = mTrackingFeatures[i].features[j].x;
                        mTrackingFeatures[i].features[j].x = width-1 - mTrackingFeatures[i].features[j].y;
                        mTrackingFeatures[i].features[j].y = x;
                    }
                }
            }
        }

        FSDK.FreeImage(mCameraImage);
    }

    public void onSurfaceChanged ( GL10 unused, int width, int height ) { //call opengl functions only inside these functions!
        Log.d(TAG, "surfaceChanged");
        if (!mIsResizeCalled) {
            mIsResizeCalled = true;
            mMainView.resizeForPerformance(width, height);
            return;
        }

        GLES11.glViewport(0, 0, width, height);
        Camera.Parameters param = mCamera.getParameters();
        List<Camera.Size> psize = param.getSupportedPreviewSizes();
        if ( psize.size() > 0 ) {
            int i = 0;
            int optDistance = Integer.MAX_VALUE;
            Log.d(TAG, "Choosing preview resolution closer to " + width + " x " + height);

            double neededScale = height/(double)width;
            for (int j=0; j<psize.size(); ++j) {
                double scale = psize.get(j).width / (double)psize.get(j).height;
                int distance = (int)(10000*Math.abs(scale - neededScale));

                Log.d(TAG, "Choosing preview resolution, probing " + psize.get(j).width + " x " + psize.get(j).height + " distance: " + distance);
                if (distance < optDistance) {
                    i = j;
                    optDistance = distance;
                } else if (distance == optDistance) {
                    // try to avoid too low resolution
                    if ((psize.get(i).width < 300 || psize.get(i).height < 300)
                            && psize.get(j).width > psize.get(i).width && psize.get(j).height > psize.get(i).height) {
                        i = j;
                    }
                }
            }

            Log.d(TAG, "Using optimal preview size: " + psize.get(i).width + " x " + psize.get(i).height);
            param.setPreviewSize(psize.get(i).width, psize.get(i).height);

            // adjusting viewport to camera aspect ratio 
            int viewportWidth = width;
            int viewportHeight = (int) (width*(psize.get(i).width*1.0f/psize.get(i).height));

            GLES11.glViewport(0, 0, viewportWidth, viewportHeight);

            mWidth = viewportWidth;
            mHeight = viewportHeight;
            mPixelBuffer = ByteBuffer.allocateDirect(mWidth * mHeight * 4).order(ByteOrder.nativeOrder());
        }

        param.set("orientation", "landscape");
        mCamera.setParameters(param);
        mCamera.startPreview();
        mIsResized = true;
    }

    private void initTex() {
        mTextures = new int[3];
        GLES11.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        GLES11.glEnable(GL10.GL_TEXTURE_2D);
        GLES11.glGenTextures(3, mTextures, 0);
        GLES11.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextures[0]);
        GLES11.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES11.GL_TEXTURE_WRAP_S, GLES11.GL_CLAMP_TO_EDGE);
        GLES11.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES11.GL_TEXTURE_WRAP_T, GLES11.GL_CLAMP_TO_EDGE);
        GLES11.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES11.GL_TEXTURE_MIN_FILTER, GLES11.GL_NEAREST);
        GLES11.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_NEAREST);
    }

    private void deleteTex() {
        GLES11.glDeleteTextures(3, mTextures, 0);
    }

    public synchronized void onFrameAvailable ( SurfaceTexture st ) {
        mUpdateSurfaceTexture = true;
        mMainView.requestRender();
    }

    public synchronized void snapshot() {
        isTakingSnapshot.set(true);
    }



}
