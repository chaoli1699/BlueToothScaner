package com.example.bluetoothscaner;

import java.util.ArrayList;
import java.util.List;

import com.example.bluetoothscaner.bean.BleDevice;
import com.example.bluetoothscaner.weight.ScanView;
import com.example.bluetoothscaner.weight.ScanView.OnPointUpdateListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button scanBtn;
	private TextView callbackText;
	private RelativeLayout container;
	private ScanView scanView;
	private ListView bleList;

	private BluetoothAdapter bluetoothAdapter;
	private List<BleDevice> deviceList;
	private ScanThread scanThread;
	private static final int A_NEW_DEVICE_SCANED=0;
	private static final int SCAN_DEVICE_COMPLATE=1;
	private static final int REQUEST_BLE_ENABLE=2;
	private ArrayAdapter<BleDevice> adapter;
	private boolean blePrepared=false;
	private int count=0;
	private List<Integer> pointX;
	private List<Integer> pointY;

	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case A_NEW_DEVICE_SCANED:
//			    adapter.notifyDataSetChanged();
				count++;
				setMTextColor("已扫描到蓝牙设备("+count+")个...", "#ff00ff00");
				pointX.add((int)(300*(Math.random()-1)));
				pointY.add((int)(200*(Math.random()-1)));
				break;

			case SCAN_DEVICE_COMPLATE:
				setMTextColor("已获得所有蓝牙设备("+count+"个)","#6699FF");
				scanBtn.setText("重新扫描");
				scanThread.interrupt();
				count=0;

				if (scanView!=null) {
					scanView.stop();
					scanView.setVisibility(View.GONE);
				}

				if (bleList==null) {
					ViewGroup.LayoutParams bleListPL=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
							ViewGroup.LayoutParams.MATCH_PARENT);
					bleList=new ListView(MainActivity.this);
					bleList.setLayoutParams(bleListPL);
					bleList.setVerticalScrollBarEnabled(false);
					container.addView(bleList);	
					adapter=new ArrayAdapter<BleDevice>(MainActivity.this, android.R.layout.simple_list_item_1, deviceList);
					bleList.setAdapter(adapter);
				}else {
					if (bleList.getVisibility()==View.GONE) {
						bleList.setVisibility(View.VISIBLE);
						adapter.notifyDataSetChanged();
					}
				}
				
				break;
			default:
				break;
			}
		};
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scanBtn=findViewById(R.id.main_scanBtn);
		scanBtn.setText("开始扫描");

		scanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				scanBle();
			}
		});

		container=findViewById(R.id.main_container);
	}

	private void scanBle() {
		// TODO Auto-generated method stub
		//		bleList=findViewById(R.id.main_BleList);
		
		ViewStub infoStub=findViewById(R.id.main_infoStub);
		if (infoStub!=null) {
			callbackText=infoStub.inflate().findViewById(R.id.tv_callback);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

			bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter.isEnabled()) {
				blePrepared=true;
			}else {
				blePrepared=false;
			}

			if (blePrepared) {
				setMTextColor("正在扫描蓝牙设备...", "#ff00ff00");
				scanBtn.setText("正在扫描...");
				
				if (pointX==null) {
					pointX=new ArrayList<Integer>();
				}
				if (pointY==null) {
					pointY=new ArrayList<Integer>();
				}
				pointX.clear();
				pointY.clear();
				
				if (deviceList==null) {
					deviceList=new ArrayList<BleDevice>();
				}
				
				if (bleList!=null) {
					bleList.setVisibility(View.GONE);
				}
				
				if (scanView==null) {			
					ViewGroup.LayoutParams scanViewLP=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT);
					scanView=new ScanView(this);
					scanView.setLayoutParams(scanViewLP);
					container.addView(scanView);
					
					scanView.setOnPointUpdateListener(new OnPointUpdateListener() {
						
						@Override
						public void OnUpdate(Canvas canvas, Paint paintPoint, float cx, float cy, float radius) {
							// TODO Auto-generated method stub
							for (int i = 0; i < pointX.size(); i++) {
								canvas.drawCircle(cx+pointX.get(i), cy+pointY.get(i), 5, paintPoint);
							}
						}
					});
				}else {
					if (scanView.getVisibility()==View.GONE) {
						scanView.setVisibility(View.VISIBLE);
					}
				}	
				scanView.start();
				
				if (scanThread==null) {
					scanThread=new ScanThread(bluetoothAdapter, handler);
				}
				if (!scanThread.isAlive()) {
					scanThread.run();
				}
			}else {
				setMTextColor("本地蓝牙设备不可用", "#ffff0000");
				Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
				startActivityForResult(enabler, REQUEST_BLE_ENABLE); 
			}
		}else {
			setMTextColor("当前系统不支持该功能", "#ffff0000");
		}
	}

	private void setMTextColor(String text, String color){
		callbackText.setText(text);
		callbackText.setTextColor(Color.WHITE);
		callbackText.setBackgroundColor(Color.parseColor(color));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case REQUEST_BLE_ENABLE:
			Log.i("BLE", resultCode+"");
			if (resultCode == RESULT_OK) {
				scanBle();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	private class ScanThread extends Thread{

		private BluetoothLeScanner bluetoothLeScanner;
		private Handler sHandler;
		private static final int TIME_OUT=10;

		public ScanThread(BluetoothAdapter bluetoothAdapter, Handler handler) {
			// TODO Auto-generated constructor stub
			this.sHandler=handler;
			bluetoothLeScanner=bluetoothAdapter.getBluetoothLeScanner();
		}

		private ScanCallback callback=new ScanCallback() {

			public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {

				BluetoothDevice device=result.getDevice();
				boolean isExits=false;
				for(BleDevice info: deviceList){
					if (info.getAddress().equals(device.getAddress())) {
						isExits=true;
					}
				}

				if (!isExits) {
					deviceList.add(new BleDevice(device.getAddress(), "", result.getRssi()));
					handler.sendEmptyMessage(A_NEW_DEVICE_SCANED);
				}
			};
		};

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//			super.run();
			deviceList.clear();
			bluetoothLeScanner.startScan(callback);

			sHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					bluetoothLeScanner.stopScan(callback);
					sHandler.sendEmptyMessage(1);
				}
			}, TIME_OUT*1000);
		}
	}


}
