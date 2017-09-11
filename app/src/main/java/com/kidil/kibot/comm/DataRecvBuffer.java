package com.kidil.kibot.comm;

public class DataRecvBuffer {

	private int pHead = 0;
	private int pTail = 0;
	private byte[] data = new byte[4096];//ѭ�����л�������С4KB
	
	/**
	 * �ӵ�ǰͷ����ʼ��ȡdata����
	 * @param data
	 */
	public void getData(byte[] bytes){
		for(int j = 0; j < bytes.length; j++){		
			bytes[j] = (byte)data[(pHead+j)%data.length];
			//Log.d("DataRecvBuffer",String.format("%02x",bytes[j]));
		}
	}
	public byte getByte(int ix){
		return data[ix];
	}
	public int getLength(){
		return data.length;
	}
	public int getpHead() {
		return pHead;
	}
	public void setpHead(int pHead) {
		this.pHead = pHead;
	}
	public int getpTail() {
		return pTail;
	}
	public void setpTail(int pTail) {
		this.pTail = pTail;
	}
	public void pushByte(byte b){
		data[pTail] = b;
		//Log.d("DataRecvBuffer",String.format("push %02x",b));
		tailInc();
	}
	public int getIndex(int j){
		return j % data.length;
	}
	public int getDataCnt(int ix){
		return (data.length + ix + 1  - pHead) % data.length;
	}
	public int getDataCnt(){
		return (data.length + pTail - pHead) % data.length;
	}
	public boolean isFull(){
		return pHead == getIndex(pTail+1);
	}
	public boolean isEmpty(){
		return pHead == pTail;
	}
	public int tailInc(){
		pTail = (pTail+1) % data.length;
		return pTail;
	}
	public int headInc(){
		pHead = (pHead+1) % data.length;
		return pHead;
	}
	public int headInc(int i){
		pHead = (pHead+i) % data.length;
		return pHead;
	}
}
