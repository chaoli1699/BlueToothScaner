package com.example.bluetoothscaner.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class ScanView extends View {

	//控件宽度<=高度     //雷达直径
	private float viewSize;
	//画笔
	private Paint mPaintSector;
	private Paint mPaintCircle;
	private Paint mPaintLine;
	private Paint mPaintPoint;

	//线程是否在执行
	private boolean isRunning  = false;
	//任务是否已经开始
	private boolean isStart = false;

	//记录并设置旋转角度
	private int start = 0;

	//雷达旋转方向  
	     //顺时针
	public final static int CLOCK_WISE = 1;
	    //逆时针
	public final static int ANSI_CLOCK_WISE = -1;
	public final static int DEFAULT_DIRECTION = CLOCK_WISE;

	private int direction = DEFAULT_DIRECTION;

	private ScanThread scanThread;  //线程
	private Matrix matrix;  //矩阵

	public ScanView(Context context, AttributeSet attrs) {
	    this(context, attrs,0);
	}

	public ScanView(Context context) {
	    this(context,null);
	}

	public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
	    super(context, attrs, defStyleAttr);
	    
	    initPaint();
	}

	private void initPaint() {

	    //用来绘画直线的画笔
	    mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
	    mPaintLine.setAntiAlias(true);
	    mPaintLine.setColor(Color.WHITE);
	    mPaintLine.setStyle(Style.STROKE);
	    mPaintLine.setStrokeWidth(5);
	    
	    //用来绘画背景圆的画笔
	    mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mPaintCircle.setAntiAlias(true);
	    mPaintCircle.setColor(0x99000000);
	    //实心圆style
	    mPaintCircle.setStyle(Style.FILL);
	    
	    //绘画圆渐变色的画笔
	    mPaintSector = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mPaintSector.setAntiAlias(true);
	    mPaintSector.setStyle(Style.FILL);
	    //绘画实点
	    mPaintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mPaintPoint.setAntiAlias(true);
	    mPaintPoint.setColor(Color.WHITE);
	    mPaintPoint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
	    
	    //绘制背景圆
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/2.0f, mPaintCircle);
	    //绘制空心圆
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/4.0f, mPaintLine);
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/8.0f, mPaintLine);
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/2.0f, mPaintLine);
	    //绘制水平垂直两个直径
	    canvas.drawLine(0, viewSize/2.0f,viewSize, viewSize/2.0f, mPaintLine);
	    canvas.drawLine(viewSize/2.0f,0,viewSize/2.0f,viewSize, mPaintLine);
	    
	    //显示实心点
	    if(mListener != null){
	        mListener.OnUpdate(canvas,mPaintPoint,viewSize/2.0f,viewSize/2.0f,viewSize/2.0f);
	    }
	    
	    //把画布的多有对象与matrix联系起来
	    if(matrix != null){
	        canvas.concat(matrix);
	    }
	    //绘制颜色渐变圆
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/2.0f, mPaintSector);
	    
	    super.onDraw(canvas);
	}

	//重写onMeasure
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    //获得控件的宽度   宽度<=高度
	    viewSize = getMeasuredWidth();
	    
	    //初始化一个颜色渲染器
	    SweepGradient shader = new SweepGradient(viewSize /2.0f, viewSize/2.0f, Color.TRANSPARENT, Color.GREEN);
	    //mPaintSector设置颜色渐变渲染器
	    mPaintSector.setShader(shader);
	}
	//设置循环的方向
	public void setDirection(int d){
	    if(d != CLOCK_WISE && d != ANSI_CLOCK_WISE){
	        throw new IllegalStateException("only contonst CLOCK_WISE  ANSI_CLOCK_WISE");
	    }
	    this.direction = d;
	}

	//线程开启
	public void start(){
	    scanThread = new ScanThread(this);
	    scanThread.setName("radar");
	    isRunning = true;
	    isStart = true;
	    scanThread.start();
	}
	//线程结束
	public void stop(){
	    if(isStart){
	        isStart = false;
	        isRunning = false;
	    }
	}
	class ScanThread extends Thread{
	    private View view;
	    
	    public ScanThread(View view) {
	        super();
	        this.view = view;
	    }

	    @Override
	    public void run() {
	        while(isRunning){
	            if(isStart){
	                start += 1;
	                view.post(new Runnable() {
	                    @Override
	                    public void run() {
	                        //创建一个矩阵
	                        matrix = new Matrix();
	                        //矩阵设置旋转
	                        matrix.preRotate(start*direction, viewSize/2.0f, viewSize/2.0f);
	                        //重画
	                        view.invalidate();
	                    }
	                });
	                try {
	                    Thread.sleep(5);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	}


	public OnPointUpdateListener mListener;
	//更新point的接口
	public interface OnPointUpdateListener{
	    public void OnUpdate(Canvas canvas,Paint paintPoint,float cx,float cy,float radius);
	}
	public void setOnPointUpdateListener(OnPointUpdateListener listener){
	    this.mListener = listener;
	}
}
