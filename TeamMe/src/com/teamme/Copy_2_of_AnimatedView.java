package com.teamme;

import java.util.Random;

import android.content.Context;

import android.graphics.Canvas;

import android.graphics.drawable.BitmapDrawable;

import android.os.Handler;

import android.util.AttributeSet;

import android.widget.ImageView;

public class Copy_2_of_AnimatedView extends ImageView{

	private Context mContext;

	private int x = -1;
	private int y = -1;

	private int index = 0;
	private Random rand;

	private int xVelocity = 3;

	private int yVelocity = 0;

	private Handler h;

	private final int FRAME_RATE = 10;

	private BitmapDrawable[] bitMapArray;
	public Copy_2_of_AnimatedView(Context context, AttributeSet attrs)  {

		super(context, attrs);

		mContext = context;

		rand = new Random();
		initializeBitMap();
		h = new Handler();

	}





	private void initializeBitMap() {
  
    	 bitMapArray = new BitmapDrawable[8];
    	 bitMapArray[0] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.soccer);
    	 bitMapArray[1] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.football);
    	 bitMapArray[2] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.frisbee);
    	 bitMapArray[3] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.tennis);
    	 bitMapArray[4] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bike);
    	 bitMapArray[5] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bowling);
    	 bitMapArray[6] =(BitmapDrawable) mContext.getResources().getDrawable(R.drawable.climbing);
    	 bitMapArray[7] = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.volleyball);
    	 
    	 
}


	private Runnable r = new Runnable() {

		@Override

		public void run() {

			invalidate();

		}

	};

	protected void onDraw(Canvas c) {

		BitmapDrawable ball = bitMapArray[index];


		if (x<0 && y <0) {

			x = this.getWidth()/2;

			y = 110;

		} else {

			x += xVelocity;
			

			if ((x > this.getWidth() - ball.getBitmap().getWidth() - 100) || (x < 100)) {
				if (index > 6)
					index = 0;
				else
					index++;
				ball = bitMapArray[index];
				
				xVelocity = xVelocity*-1;

			}

		

		}


		c.drawBitmap(ball.getBitmap(), x, y, null);





		h.postDelayed(r, FRAME_RATE);

	}

}