package edu.sjsu.android.accgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

public class SimulationView extends View implements SensorEventListener {
    // sensor manager, sensor, and display
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Display mDisplay;

    private long mSensorTimeStamp = System.nanoTime();
    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;

    // variables for GUI
    private Bitmap mField;
    private Bitmap mBasket;
    private Bitmap mBitMAP;
    private static final int BALL_SIZE = 64;
    private static final int BASKET_SIZE_W = 500;
    private static final int BASKET_SIZE_H = 650;

    private float mXOrigin;
    private float mYOrigin;
    private float mHorizontalBound;
    private float mVerticalBound;

    private Particle mBall = new Particle();

    public SimulationView(Context context) {
        super(context);

        // Initialize images from drawable
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitMAP = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE_W, BASKET_SIZE_H, true);
        BitmapFactory.Options opts = new BitmapFactory.Options();

        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mField = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);

        // initialize display
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // initialize sensors / listener
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Initialize images from drawable
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitMAP = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE_W, BASKET_SIZE_H, true);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mField = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);

        //initialize variables
        mXOrigin = w * .5f;
        mYOrigin = h * .5f;

        mHorizontalBound = (w - BALL_SIZE) * .5f;
        mVerticalBound = (h - BALL_SIZE) * .5f;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mSensorTimeStamp = event.timestamp;

            if (mDisplay.getRotation() == Surface.ROTATION_0 ){
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                mSensorZ = event.values[2];
            }
            else if (mDisplay.getRotation() == Surface.ROTATION_90){
                mSensorX = event.values[1];
                mSensorY = event.values[0];
                mSensorZ = event.values[2];
            }
            else if(mDisplay.getRotation() == Surface.ROTATION_180){
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                mSensorZ = event.values[2];
            }
            else {
                mSensorX = event.values[1];
                mSensorY = event.values[0];
                mSensorZ = event.values[2];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void startSimulation() {
        // register listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopSimulation(){
        // unregister listener
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mField, 0, 0, null);
        canvas.drawBitmap(mBasket, mXOrigin - BASKET_SIZE_W / 2, mYOrigin - BASKET_SIZE_H / 2, null);

        mBall.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
        mBall.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);

        canvas.drawBitmap(mBitMAP,
                (mXOrigin - BALL_SIZE / 2) + mBall.mPosX,
                (mYOrigin - BALL_SIZE / 2) - mBall.mPosY, null);

        invalidate();
    }

}
