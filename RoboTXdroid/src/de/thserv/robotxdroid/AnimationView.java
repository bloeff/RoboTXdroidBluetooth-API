package de.thserv.robotxdroid;

import java.util.List;
import de.thserv.robotxdroid.api.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class AnimationView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
	
	// Der Sensormanager um Werte des Neigunssensor abzufragen
	public static SensorManager mSensorManager;
    
	private float sensorX = 0;
	private float sensorY = 0;	
    
    private int rad = 0;
	
	class AnimationThread extends Thread{

    	/** Lauft der Thread noch ? */
    	private boolean mRun;

        /** Used to figure out elapsed time between frames */
        private long mLastTime;      

        /** Variables for the counter */
        private int timeCount = 0;


        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;

        /** How to display the text */
        private Paint textPaint;
        private Paint dotPaint;
        private Paint fadeCrossPaint;
        

        public AnimationThread(SurfaceHolder surfaceHolder) {
        	
        	mSurfaceHolder = surfaceHolder;

            /** Initiate the text painter */
            textPaint = new Paint();
            textPaint.setARGB(255,255,255,255);
            textPaint.setTextSize(32);
            
            dotPaint = new Paint();
            dotPaint.setAntiAlias(true);
            dotPaint.setColor(Color.YELLOW);
            
            fadeCrossPaint = new Paint();
            fadeCrossPaint.setColor(Color.GRAY);
            fadeCrossPaint.setStyle(Style.STROKE);
    		
        }
    	
        /**
         * The actual game loop!
         */
        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                    	
                    	updatePhysics();
                        doDraw(c);
                    }
                }finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Figures the gamestate based on the passage of
         * realtime. Called at the start of draw().
         * Only calculates the FPS for now.
         */
        
        int i = 0;
        private void updatePhysics() {
        	long now = System.currentTimeMillis();

            if (mLastTime != 0) {

            	//Time difference between now and last time we were here
        		int time = (int) (now - mLastTime);
        		
        		timeCount += time;
        		//After 100 frames
        		if (timeCount > 100) {
        			doMotorMovement(-sensorY, sensorX);
        			timeCount = 0;
        		}
        	}
            mLastTime = now;
        }

        /**
         * Draws to the provided Canvas.
         */
        private void doDraw(Canvas canvas) {

            // Draw the background color. Operations on the Canvas accumulate
            // so this is like clearing the screen. In a real game you can
        	// put in a background image of course
        	try{
	        	if(canvas != null){
	        		canvas.drawColor(Color.BLACK);
	
	        		//Draw fps center screen
	        		rad = getWidth() / 2 - 30;
	
	        		canvas.drawCircle( getWidth() / 2 + sensorX*2, getHeight() / 2 + sensorY*2, 15, dotPaint);
	        		canvas.drawCircle( getWidth() / 2, getHeight() / 2, rad, fadeCrossPaint);
	        		canvas.drawLine(getWidth() / 2, getHeight() / 2 - rad, getWidth() / 2, getHeight() / 2 + rad, fadeCrossPaint);
	        		canvas.drawLine(getWidth() / 2 - rad, getHeight() / 2 , getWidth() / 2 + rad, getHeight() / 2, fadeCrossPaint);
	        		canvas.restore();
	        	}
        	}catch(Exception e){
        		
        	}
        }

        /**
         * So we can stop/pauze the game loop
         */
        public void setRunning(boolean b) {
            mRun = b;
        }      

    }

    /** The thread that actually draws the animation */
    private AnimationThread thread;

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
		List<Sensor> sensorenListe = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensorenListe.size() > 0) {
			mSensorManager.registerListener(this, 
					mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_UI);
		}
		
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        
        
        // create thread only; it's started in surfaceCreated()
        thread = new AnimationThread(holder);

    }

    /**
     * Obligatory method that belong to the:implements SurfaceHolder.Callback
     */

    /**
     * Callback invoked when the surface dimensions change.Â 
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    /**
     * Callback invoked when the Surface has been destroyed and must no longer 
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */ 
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			sensorX = 0 - event.values[2];
			sensorY = 0 - event.values[1];

		}
	}
	
	int tryOut;
	int i  = 0;
	public void doMotorMovement(float pitch, float roll) {
		int left=0;
		int right=0;
		// only when phone is little bit tilted
		if ((Math.abs(pitch) > 10.0) || (Math.abs(roll) > 10.0)) {

			// limit pitch and roll
			if (pitch > 33.3){
				pitch = (float) 33.3;
			}else if (pitch < -33.3){
				pitch = (float) -33.3;}

			if (roll > 33.3){
				roll = (float) 33.3;
			}else if (roll < -33.3){
				roll = (float) -33.3;
			}

			// when pitch is very small then do a special turning function    
			if (Math.abs(pitch) > 10.0) {
				left = (int) Math.round(3.3 * pitch * (1.0 + roll / 60.0));
				right = (int) Math.round(3.3 * pitch * (1.0 - roll / 60.0));
			} else {
				left = (int) Math.round(3.3 * roll - Math.signum(roll) * 3.3 * Math.abs(pitch));
				right = -left;
			}
				
			left *= 5;
			right *= 5;
			// limit the motor outputs
			if (left > 512)
				left = 512;
			else if (left < -512)
				left = -512;

			if (right > 512)
				right = 512;
			else if (right < -512)
				right = -512;
		}
		
		try {
			ApiEntry.getInstance().doMovement(left, right);
			ApiEntry.getInstance().SetOutPwmValues(6, (short)512);
			ApiEntry.getInstance().SetOutPwmValues(7, (short)512);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}