package com.example.bluetoothscaner;

public class BlueToothInfo {

	private String address;
	private String rssi;
	
	public BlueToothInfo(String address, String rssi) {
		// TODO Auto-generated constructor stub
		this.address=address;
		this.rssi=rssi;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append("Adss£º");
		sBuffer.append(address);
		sBuffer.append("      Rssi:");
		sBuffer.append(rssi);
		return sBuffer.toString();
	}
	
}
