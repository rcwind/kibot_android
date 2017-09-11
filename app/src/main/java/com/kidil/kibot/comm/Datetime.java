package com.kidil.kibot.comm;



public class Datetime {
	public byte year;
	public byte month;
	public byte day;
	public byte hour;
	public byte minute;
	public byte second;
	public short ms;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ""+(year + 2000)+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+":"+ms;
	}	

}
