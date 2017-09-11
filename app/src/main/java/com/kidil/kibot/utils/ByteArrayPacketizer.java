package com.kidil.kibot.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 
 * @author �����
 * @since 2014.8.11
 */
public class ByteArrayPacketizer implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private ByteArrayOutputStream byteArrayOutputStream;
	private DataOutputStream dataOutputStream;

	public ByteArrayPacketizer() {

		if(byteArrayOutputStream==null)
			byteArrayOutputStream = new ByteArrayOutputStream();
		if(dataOutputStream==null)
			dataOutputStream = new DataOutputStream(byteArrayOutputStream);
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
		if(b==null)
			return;
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
