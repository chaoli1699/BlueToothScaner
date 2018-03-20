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

	//�ؼ����<=�߶�     //�״�ֱ��
	private float viewSize;
	//����
	private Paint mPaintSector;
	private Paint mPaintCircle;
	private Paint mPaintLine;
	private Paint mPaintPoint;

	//�߳��Ƿ���ִ��
	private boolean isRunning  = false;
	//�����Ƿ��Ѿ���ʼ
	private boolean isStart = false;

	//��¼��������ת�Ƕ�
	private int start = 0;

	//�״���ת����  
	     //˳ʱ��
	public final static int CLOCK_WISE = 1;
	    //��ʱ��
	public final static int ANSI_CLOCK_WISE = -1;
	public final static int DEFAULT_DIRECTION = CLOCK_WISE;

	private int direction = DEFAULT_DIRECTION;

	private ScanThread scanThread;  //�߳�
	private Matrix matrix;  //����

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

	    //�����滭ֱ�ߵĻ���
	    mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG); // �������
	    mPaintLine.setAntiAlias(true);
	    mPaintLine.setColor(Color.WHITE);
	    mPaintLine.setStyle(Style.STROKE);
	    mPaintLine.setStrokeWidth(5);
	    
	    //�����滭����Բ�Ļ���
	    mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mPaintCircle.setAntiAlias(true);
	    mPaintCircle.setColor(0x99000000);
	    //ʵ��Բstyle
	    mPaintCircle.setStyle(Style.FILL);
	    
	    //�滭Բ����ɫ�Ļ���
	    mPaintSector = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mPaintSector.setAntiAlias(true);
	    mPaintSector.setStyle(Style.FILL);
	    //�滭ʵ��
	    mPaintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mPaintPoint.setAntiAlias(true);
	    mPaintPoint.setColor(Color.WHITE);
	    mPaintPoint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
	    
	    //���Ʊ���Բ
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/2.0f, mPaintCircle);
	    //���ƿ���Բ
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/4.0f, mPaintLine);
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/8.0f, mPaintLine);
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/2.0f, mPaintLine);
	    //����ˮƽ��ֱ����ֱ��
	    canvas.drawLine(0, viewSize/2.0f,viewSize, viewSize/2.0f, mPaintLine);
	    canvas.drawLine(viewSize/2.0f,0,viewSize/2.0f,viewSize, mPaintLine);
	    
	    //��ʾʵ�ĵ�
	    if(mListener != null){
	        mListener.OnUpdate(canvas,mPaintPoint,viewSize/2.0f,viewSize/2.0f,viewSize/2.0f);
	    }
	    
	    //�ѻ����Ķ��ж�����matrix��ϵ����
	    if(matrix != null){
	        canvas.concat(matrix);
	    }
	    //������ɫ����Բ
	    canvas.drawCircle(viewSize/2.0f,viewSize/2.0f, viewSize/2.0f, mPaintSector);
	    
	    super.onDraw(canvas);
	}

	//��дonMeasure
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    //��ÿؼ��Ŀ��   ���<=�߶�
	    viewSize = getMeasuredWidth();
	    
	    //��ʼ��һ����ɫ��Ⱦ��
	    SweepGradient shader = new SweepGradient(viewSize /2.0f, viewSize/2.0f, Color.TRANSPARENT, Color.GREEN);
	    //mPaintSector������ɫ������Ⱦ��
	    mPaintSector.setShader(shader);
	}
	//����ѭ���ķ���
	public void setDirection(int d){
	    if(d != CLOCK_WISE && d != ANSI_CLOCK_WISE){
	        throw new IllegalStateException("only contonst CLOCK_WISE  ANSI_CLOCK_WISE");
	    }
	    this.direction = d;
	}

	//�߳̿���
	public void start(){
	    scanThread = new ScanThread(this);
	    scanThread.setName("radar");
	    isRunning = true;
	    isStart = true;
	    scanThread.start();
	}
	//�߳̽���
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
	                        //����һ������
	                        matrix = new Matrix();
	                        //����������ת
	                        matrix.preRotate(start*direction, viewSize/2.0f, viewSize/2.0f);
	                        //�ػ�
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
	//����point�Ľӿ�
	public interface OnPointUpdateListener{
	    public void OnUpdate(Canvas canvas,Paint paintPoint,float cx,float cy,float radius);
	}
	public void setOnPointUpdateListener(OnPointUpdateListener listener){
	    this.mListener = listener;
	}
}
