package com.example.wei07;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class myView extends View {
    private Bitmap ball_bmp;
    private Activity activity;
    private Resources resources;
    private Paint paint;
    private int viewH,viewW;
    private float ballH,ballW,ballX,ballY,dx,dy;
    private boolean isInit;//考慮抓螢幕大小時

    private Timer timer;//週期性任務用
    private GestureDetector gd;//手勢偵測

    private HashMap<String, Float> temDir;//暫存滑動方向
    /*
        手勢偵測需要listener物件，才能使用GestureDetector裡的方法
        listener這個物件需要設定我們要的屬性
        因此可以有兩種做法:
        1. 做一個Listener物件，一個一個設定屬性跟方法
        2. 建一個類別繼承他，在這個類別裡設定屬性跟方法，要使用時再new
     */


    public myView(Context context) {
        super(context);
        setBackgroundResource(R.drawable.background_1);//會縮放
        activity=(MainActivity)context;
        resources=activity.getResources();
        timer=new Timer();
        gd=new GestureDetector(new GDListener());
    }

    public void init(){//調整獲取螢幕長寬的次數為一次
        //在建構式裡---X:因為畫面還沒出，只能抓到0
        //在ondraw裡---X:因為每畫一次就要抓一次，一旦畫面要畫多次時，將托慢速度
        //因此解決辦法是在ondraw裡設定條件，畫一次
        isInit=true;
        viewW=getWidth();
        viewH=getHeight();
        //同理，順道把剛剛放在建構式裡的畫筆設定、球的物件產生抓過來
        paint=new Paint();
        ball_bmp = BitmapFactory.decodeResource(resources,R.drawable.ball_1);//無縮放
        //設定縮放
        ballH=viewH/2f;ballW=ballH;
        //設定裁切
        Matrix matrix=new Matrix();
        matrix.postScale(ballW/ball_bmp.getWidth(),ballH/ball_bmp.getHeight());
        ball_bmp=Bitmap.createBitmap(ball_bmp,0,0,ball_bmp.getWidth(),ball_bmp.getHeight(),matrix,false);
        //ball position
        ballX=ballY=0;
        //speed
        dx=dy=100;
        //設定週期任務
        timer.schedule(new refreshView(),0,17);
        timer.schedule(new BallTask(),1*1000,30);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isInit)init();//條件
        canvas.drawBitmap(ball_bmp,ballX,ballY,null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);//把事件傳給偵測的物件
    }

    public class BallTask extends TimerTask{//做一個週期性類別
        @Override
        public void run() {//注意invalidate()
            if (ballX<0||ballX+ballW>viewW){//隱含繪製圖片的0,0是位於圖片左上角
                dx*=-1;
            }else if (ballY<0||ballY+ballH > viewH){
                dy*=-1;
            }
            ballX+=dx;
            ballY+=dy;
            postInvalidate();
        }
    }
    public class refreshView extends TimerTask{//目的:
        @Override
        public void run() {
            postInvalidate();
        }
    }

    public class GDListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;//super.onDown(e);
        }
    }
}

