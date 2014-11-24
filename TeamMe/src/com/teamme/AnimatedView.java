package com.teamme;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;


//http://www.techrepublic.com/blog/software-engineer/bouncing-a-ball-on-androids-canvas/
public class AnimatedView extends ImageView{

	private Context mContext;

	int xArray[];

	int yArray[];

	private int height;
	private int width;
	
	private Random rand;

	private int xVelocityArray[];

	private int yVelocityArray[];

	private Handler h;

	private int numObjects = 20;
	private final int FRAME_RATE = 10;

	private BitmapDrawable[] bitMapArray;
	
	
	private void setPrefs(){
		
				SharedPreferences mPrefs = mContext.getSharedPreferences("ttt_prefs", mContext.MODE_PRIVATE);  
		
				if (mPrefs.getBoolean("animationOn", true) == false)
					numObjects = 0;
					
	}
	public AnimatedView(Context context, AttributeSet attrs)  {

		
		super(context, attrs);

		
		
		mContext = context;
		setPrefs();
		if (numObjects != 0){
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		rand = new Random();
		initializeBitMap();
		initializeArrays();
		initializeVelocities();
		h = new Handler();
		}
	}
	public AnimatedView(Context context)  {

		super(context);

		mContext = context;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		rand = new Random();
		initializeBitMap();
		initializeArrays();
		initializeVelocities();
		h = new Handler();

	}
	public AnimatedView (Context context, AttributeSet attrs, int defStyle){

		super(context,attrs, defStyle);

		mContext = context;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		rand = new Random();
		initializeBitMap();
		initializeArrays();
		initializeVelocities();
		h = new Handler();

	}


	private void initializeVelocities() {
		xVelocityArray = new int[numObjects];
		yVelocityArray = new int[numObjects];
		int negative = 1;
		for (int i = 0 ; i < numObjects; i++){
			if (rand.nextInt(2) == 1)
				negative *= -1;
			xVelocityArray[i] = (1+ rand.nextInt(2)) * negative;
			yVelocityArray[i] = (1+ rand.nextInt(2)) * negative;
		}

	}

	private void initializeArrays() {
		xArray = new int[numObjects];
		yArray = new int[numObjects];
		for (int i = 0 ; i < numObjects; i++){
			xArray[i] = 20+ rand.nextInt(width);
			yArray[i] = 20 + rand.nextInt(height);
		}

	}

	private void initializeBitMap() {

		bitMapArray = new BitmapDrawable[numObjects];
		bitMapArray[0] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.soccer);
		bitMapArray[1] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.football);
		bitMapArray[2] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.frisbee);
		bitMapArray[3] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.tennis);
		bitMapArray[4] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bike);
		bitMapArray[5] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bowling);
		bitMapArray[6] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.climbing);
		bitMapArray[7] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.volleyball);
		bitMapArray[8] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.customgame);

		for (int i = 0 ; i < numObjects-9 ; i++){
			bitMapArray[i+9] = bitMapArray[i];
		}

	}

	private Runnable r = new Runnable() {

		@Override

		public void run() {

			invalidate();

		}

	};

	protected void onDraw(Canvas c) {
		if (numObjects == 0)
			return;
		BitmapDrawable ball = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.soccer);

		for (int i =0 ; i < numObjects ; i++){
			ball = bitMapArray[i];
			int  x = xArray[i];
			int y= yArray[i];
			int xVelocity = xVelocityArray[i];
			int yVelocity = yVelocityArray[i];
			if (x<0 && y <0) {

				x = width/2;

				y = height/2;

			} else {

				x += xVelocity;

				y += yVelocity;

				if ((x > width - ball.getBitmap().getWidth()) || (x < 0)) {

					xVelocity = xVelocity*-1;

				}

				if ((y > height) || (y < 0)) {

					yVelocity = yVelocity*-1;

				}

			}


			c.drawBitmap(ball.getBitmap(), x, y, null);
			xArray[i] =x;
			yArray[i] = y;
			xVelocityArray[i] = xVelocity;
			yVelocityArray[i] = yVelocity;



		}
		h.postDelayed(r, FRAME_RATE);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
	    super.onMeasure(widthMeasureSpec, height);
	    setMeasuredDimension(widthMeasureSpec, resolveSize(height, heightMeasureSpec));
	}
}