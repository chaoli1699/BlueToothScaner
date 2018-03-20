package com.example.bluetoothscaner.bean;

public class BleDevice {

	private String address;
	private String name;
	private int rssi;
	
	public BleDevice(String address, String name, int rssi) {
		// TODO Auto-generated constructor stub
		this.address=address;
		this.name=name;
		this.rssi=rssi;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRssi() {
		return rssi;
	}
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append("Addr£º");
		sBuffer.append(address);
		sBuffer.append(", Rssi:");
		sBuffer.append(rssi);
		return sBuffer.toString();
	}
	
}
