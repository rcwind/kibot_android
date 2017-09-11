package com.kidil.kibot.comm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import android.os.Message;
import android.util.Log;

import com.kidil.kibot.activity.BaseActivity;


public class OutputPacket implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private ByteArrayOutputStream byteArrayOutputStream;
	private DataOutputStream dataOutputStream;

	private Message msg;

	
	public OutputPacket(String input) {
		// TODO Auto-generated constructor stub
		if(byteArrayOutputStream==null)
			byteArrayOutputStream = new ByteArrayOutputStream();
		if(dataOutputStream==null)
			dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		this.write(input.getBytes());
		this.write("\r\n".getBytes());
	}
	public OutputPacket() {
		// TODO Auto-generated constructor stub
		if(byteArrayOutputStream==null)
			byteArrayOutputStream = new ByteArrayOutputStream();
		if(dataOutputStream==null)
			dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		this.write("\r\n".getBytes());
	}
	//注册通知消息
	public void registerMessage(int what){
		msg = new Message();

		this.msg.what = what;
	}
	public boolean hasMessage(){
		if(msg == null)
			return false;
		return true;
	}
	public void sendMessage(boolean success){

		if(success)
			BaseActivity.sendMessage(msg);//fail比success msg值大1
		else{
			msg.what = msg.what + 1;
			BaseActivity.sendMessage(msg);
		}
	}

	public void write(byte[] b, int off, int len) {
		try {
			dataOutputStream.write(b, off, len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(byte[] b) {
		try {
			dataOutputStream.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeBoolean(boolean v) {
		try {
			dataOutputStream.writeBoolean(v);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeByte(byte v) {
		try {
			dataOutputStream.writeByte(v);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeInt(int v) {
		try {
			dataOutputStream.writeByte(v&0xff);
			dataOutputStream.writeByte(v>>8&0xff);
			dataOutputStream.writeByte(v>>16&0xff);
			dataOutputStream.writeByte(v>>24&0xff);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeShort(short val) {
		try {
			dataOutputStream.writeByte(val&0xff);
			dataOutputStream.writeByte((val>>8)&0xff);
			//dataOutputStream.writeShort(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeUTF(String str) {
		try {
			dataOutputStream.writeUTF(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeDouble(double v) {
		try {
			dataOutputStream.writeDouble(v);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] getData() {
		try {
			byteArrayOutputStream.flush();
			/*****/
			byte[] data = byteArrayOutputStream.toByteArray();
			StringBuilder builder = new StringBuilder();
			for(int i=0;i<data.length;i++){
				builder.append(String.format("%02x",data[i]));
			}
			Log.d("OutputPcket",builder.toString());
			//Log.d(TAG,new String(data));
			/*****/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public void close() {
		try {
			byteArrayOutputStream.close();
			dataOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
