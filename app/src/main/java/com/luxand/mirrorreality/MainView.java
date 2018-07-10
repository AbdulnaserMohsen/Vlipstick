package com.luxand.mirrorreality;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MainView extends GLSurfaceView
{
    public static final String TAG = "MainView";

    private MainRenderer mRenderer;

    private GestureDetector mSwipeDetector;

    private final class SwipeListener extends GestureDetector.SimpleOnGestureListener
    {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            boolean result = false;
            try
            {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY))
                {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)
                    {
                        if (diffX > 0)
                        {
                            //onSwipeRight();
                        }
                        else
                        {
                            //onSwipeLeft();
                        }
                    }
                    result = true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
    }

    private void onSwipeLeft() {
        mRenderer.nextMask();
    }

    private void onSwipeRight() {
        mRenderer.previousMask();
    }

    public MainRenderer renderer () {
        return mRenderer;
    }

    private void init(Context context) {
        mRenderer = new MainRenderer(this);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mSwipeDetector = new GestureDetector(context, new SwipeListener());

        registerSensorEventListener();
    }

    public MainView (Context context) {
        super(context);
        init(context);
    }

    @Override
    public void onPause() {
        // do not calling super.onPause() here to allow keep running in background
    }

    public void pause() {
        super.onPause();
    }

    public void surfaceCreated ( SurfaceHolder holder ) {
        super.surfaceCreated(holder);
    }

    public void surfaceDestroyed ( SurfaceHolder holder ) {
        mRenderer.close();
        super.surfaceDestroyed(holder);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
    }

    public void resizeForPerformance(final int width, final int height) {
        this.post(new Runnable() {
            @Override
            public void run() {
                // Applying scaling to increase performance
                int renderingWidth = 400;
                int renderingHeight = (int) (renderingWidth * (height / (double) width));
                //mView.getHolder().setFixedSize(480, 640);
                Log.d(TAG, "Setting rendering size: " + renderingWidth + " x " + renderingHeight + " basing on " + width + " x " + height);
                getHolder().setFixedSize(renderingWidth, renderingHeight);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return mSwipeDetector.onTouchEvent(e);
    }

    private void registerSensorEventListener() {
        SensorEventListener sensorEventListener;
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            private final int THRESHOLD = 2;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mRenderer == null) return;
                if (Math.abs(event.values[1]) < THRESHOLD && Math.abs(event.values[0]) < THRESHOLD) return;
                if (Math.abs(event.values[1]) > Math.abs(event.values[0])) {
                    if (event.values[1] < 0) {
                        mRenderer.deviceOrientation = MainRenderer.DEVICE_ORIENTATION_INVERSE_PORTRAIT;
                    } else {
                        mRenderer.deviceOrientation = MainRenderer.DEVICE_ORIENTATION_PORTRAIT;
                    }
                } else {
                    if (event.values[0] < 0) {
                        mRenderer.deviceOrientation = MainRenderer.DEVICE_ORIENTATION_INVERSE_LANDSCAPE;
                    } else {
                        mRenderer.deviceOrientation = MainRenderer.DEVICE_ORIENTATION_LANDSCAPE;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        if (!sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI)) {
            sensorEventListener = null;
        }
    }


}
