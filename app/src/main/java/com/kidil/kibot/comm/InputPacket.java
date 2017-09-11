package com.kidil.kibot.comm;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;


/**
 * ���ݰ�
 * 
 * @author �����
 *
 */
public class InputPacket implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private ByteArrayInputStream byteArrayInputStream;
	//private final String TAG = "InputPacket";
	private DataInputStream dataInputStream;
	private boolean needAck = false;//�Ƿ���Ҫȷ�Ͻ���
	//private short frameSeqNum;//֡���
	private byte src;//Դ��ַ
	private byte dst;//Ŀ���ַ
	private byte object;//����
	private byte funcName;//������
	private short paraLength;//�����ֽ�����
	private byte[] begin16 = new byte[16];//ǰ16���ֽ�
	public InputPacket(byte[] vdata) {
		System.arraycopy(vdata, 0, begin16, 0, 16);
		byteArrayInputStream = new ByteArrayInputStream(vdata);
		dataInputStream = new DataInputStream(byteArrayInputStream);
		skipBytes(1);//0x0f
		skipBytes(4);//length
		skipBytes(2);//crc16
		needAck = ((readByte()& 0x01)==0x01)?true:false;
		skipBytes(2);//frameSeqNum = readShort();
		src = readByte();
		dst = readByte();
		object = readByte();
		funcName = readByte();
		paraLength = readShort();
	}
	/**
	 * ���ù��췽���������ø÷������ж�Ŀ�ĵ�ַ�Ƿ���ȷ
	 * @return
	 */
	public boolean isPacketLegal(){
		return dst==3?true:false;
	}
	public short getParaLength(){
		return paraLength;
	}
	public byte getFuncName() {
		// TODO Auto-generated method stub
		return funcName;
	}
	public byte getSrc() {
		return src;
	}
	public byte getDst() {
		return dst;
	}
	public byte[] getBegin16() {
		return begin16;
	}
	public byte getObject() {
		// TODO Auto-generated method stub
		return object;
	}
	public boolean needAck() {
		return needAck;
	}
	public void skipBytes(int n){
		try {
			dataInputStream.skipBytes(n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int available() {
		try {
			return dataInputStream.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	public byte[] read(){
		try {
			byte[] data = new byte[available()];
			dataInputStream.read(data);
			return data;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public byte readByte() {
		try {
			return dataInputStream.readByte();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	public int readData(byte[]dst,int offset,int length) {
		try {
			return dataInputStream.read(dst, offset, length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	public int read(byte[] data) {
		try {
			return dataInputStream.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public byte[] readData(int length){
		byte[] data = new byte[length];
		int ch;
		int pos = 0;
		try {
			while ((ch = dataInputStream.read()) != -1) {
				data[pos] = (byte) ch;
				pos++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public int readInt() {
		try {
			return (int)(dataInputStream.readByte()&0xff)|(int)(dataInputStream.readByte()&0xff)<<8|(int)(dataInputStream.readByte()&0xff)<<16|(int)(dataInputStream.readByte()&0xff)<<24;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public String readUTF() {
		try {
			return dataInputStream.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public long readLong() {
		try {
			return dataInputStream.readLong();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public double readDouble() {
		try {
			return dataInputStream.readDouble();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public boolean readBoolean() {
		try {
			return dataInputStream.readBoolean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public short readShort() {
		try {
			byte low = dataInputStream.readByte();
			byte high = dataInputStream.readByte();
			return (short) ((low&0xff)|(high&0xff)<<8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public void close() {
		try {
			byteArrayInputStream.close();
			dataInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
