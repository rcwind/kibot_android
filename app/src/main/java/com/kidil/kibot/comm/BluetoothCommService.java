package com.kidil.kibot.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.kidil.kibot.activity.BaseActivity;
import com.kidil.kibot.utils.Constants;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class BluetoothCommService {

	public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 2;  // now connected to a remote device
	public static final int STATE_DISCONNECTED = 3;  // now connected to a remote device
	public static final int STATE_CONNECTION_LOST = 4; // now initiating an outgoing connection
	public static final int STATE_CONNECT_FAILED = 5; // now initiating an outgoing connection
	public static int send_failed_time=0;
	public String connectedDeviceName;
	private final String TAG = "BluetoothCommService";
	private BluetoothAdapter mBluetoothAdapter;
	private InputStream mInStream;
	private OutputStream mOutStream;
	public BluetoothDevice bluetoothDevice;
	private BluetoothSocket mBluetoothSocket = null;
	public ThreadSendPacket threadSendPacket;
	private boolean needWakeup = false;
	private DataRecvBuffer dataRecvBuffer;
	private int mState;
	// 每次读出来的可能不是一行，如果含\r\n的某部分延迟才读出来，前面的丢掉就是不合理的，所以给予再次判断的机会
	private boolean drop = false;
	//private boolean handleDataState;
	//private Context mContext;
	public BluetoothCommService(Context context){
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.mState   = STATE_DISCONNECTED;
		//	this.mContext = context;
	}
	/**
	 * Set the current state of the chat connection
	 * @param state  An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		mState = state;
		Message msg = new Message();
		msg.what = Constants.MESSAGE_BT_STATE_CHANGE;
		Bundle bundle = new Bundle();
		msg.arg1 = state;
		msg.setData(bundle);
		BaseActivity.sendMessage(msg);
	}
	/**
	 * Return the current connection state. */
	public synchronized int getState() {
		return mState;
	}

	public void connect(BluetoothDevice device)
	{
		bluetoothDevice = device;
		connectedDeviceName = device.getName();
		ConnectThread mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}
	public class ConnectThread extends Thread {
		public ConnectThread(BluetoothDevice device) {
			try {
				//创建一个Socket连接：只需要服务器在注册时的UUID号
				mBluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				Log.d(TAG, "register UUID successfully");
			}catch(IOException e){
				Log.d(TAG, "register UUID failed");
			}
		}
		public void run() {
			mBluetoothAdapter.cancelDiscovery();// Always cancel discovery because it will slow down a connection	
			try{
				//连接
				mBluetoothSocket.connect();
				Log.d(TAG, "mBluetoothSocket has been connected");
			}catch(IOException e){
				setState(STATE_CONNECT_FAILED);
				Log.e(TAG, e.getMessage());
				return;
			}
			try{
				mOutStream 	= mBluetoothSocket.getOutputStream();
				mInStream 	= mBluetoothSocket.getInputStream();
			}catch(IOException e)
			{
				Log.e("stream get failed", e.getMessage());
			}
			setState(STATE_CONNECTED);
			new Thread(receiveDataRunnable).start();
			threadSendPacket = new ThreadSendPacket();
			threadSendPacket.start();
		}
	}
	public Runnable receiveDataRunnable =new Runnable(){
		public synchronized void run(){
			// TODO Auto-generated method stub
			Log.i("receiveDataRunnable", "Thread receiveDataRunnable start");
			int length=0;
			dataRecvBuffer = new DataRecvBuffer();
			byte[] tmpBuffer = new byte[1024];
			new Thread(HandleDataRunnable).start();
			while (mState == STATE_CONNECTED) {
				try
				{
					length = mInStream.read(tmpBuffer);
					/*****/
 /*   	    				Log.i(TAG,"收数据");
    	    				byte[] data = new byte[length];

    	    				System.arraycopy(tmpBuffer, 0, data, 0, length);
							Log.d(TAG, new String(data));
    	    				StringBuilder builder = new StringBuilder();
    	    				for(int i=0;i<data.length;i++){
    	    					builder.append(String.format("%02x",data[i]));
    	    				}
    	    				Log.d(TAG,builder.toString());
    	    				Log.d(TAG,new String(data));
    	    				*/
					/*****/
					if(length<=0)	continue;
					int j = 0;
					for(;j<length;){
						if(!dataRecvBuffer.isFull())//如果队列没满
						{
							dataRecvBuffer.pushByte(tmpBuffer[j]);
							j++;
						}
						else {
							Log.e("队列满","队列满");
							//Log.d(TAG, "j="+j);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

					//Log.i("dataRecvBuffer.pTail",Integer.toString(dataRecvBuffer.pTail));
				}catch (IOException e) {
					Log.e(TAG,"connection lost/"+e.getMessage());
					setState(STATE_CONNECTION_LOST);
					threadSendPacket.stopThread();
				}
			}
			Log.e("receiveDataRunnable", "Thread receiveDataRunnable finished");
		}
	};
	public int findLine(){
		int pHead = dataRecvBuffer.getpHead();
		//Log.d(TAG, "head="+pHead);
		//Log.d(TAG, "tail="+dataRecvBuffer.getpTail());
		int ix1 = 0;
		int ix2 = 0;
		for(int j = 0; j < dataRecvBuffer.getDataCnt(); j++){
			ix1 = dataRecvBuffer.getIndex(pHead+j);
			ix2 = dataRecvBuffer.getIndex(pHead+j+1);
			if((dataRecvBuffer.getByte(ix1) == '\r') && (dataRecvBuffer.getByte(ix2) == '\n'))
			{
				return ix2;
			}
			//Log.d(TAG, "ix2="+ix2);
		}
		return -1;
	}
	public Runnable HandleDataRunnable =new Runnable(){
		@Override
		public synchronized void run() {
			// TODO Auto-generated method stub
			Log.i("HandleDataRunnable", "Thread HandleDataRunnable start");
			while(mState == STATE_CONNECTED)	{
				while(!dataRecvBuffer.isEmpty()){//如果队列不空
					int p = findLine();
					//Log.d(TAG, "p="+p);
					if(p != -1){
						byte[] frame = new byte[dataRecvBuffer.getDataCnt(p)];
						//Log.d(TAG, "frame len="+frame.length);
						dataRecvBuffer.getData(frame);
						dataRecvBuffer.headInc(frame.length);
						sendMessage(Constants.MESSAGE_RECV, frame);
					}
					else{
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				try{
					//Log.d(TAG, "sleep");
					Thread.sleep(500);
				}
				catch(Exception e){
					Log.e("HandleDataRunnable",e.getMessage());
				}

			}
			Log.e("HandleDataRunnable", "Thread HandleDataRunnable finished");
		}


	};

	private boolean sendData(byte[] buffer){
		if(mOutStream != null){
			try{
				mOutStream.write(buffer);
				//mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
				return true;
			}catch(IOException e){
				Log.e("sendData failed",e.getMessage());
				return false;
			}
		}
		else {
			sendMessage("发送失败,蓝牙未连接或已断开");
			return false;
		}
	}
	private void sendMessage(String message){

		Message msg = new Message();
		msg.what = Constants.MESSAGE_TOAST;
		Bundle bundle = new Bundle();
		bundle.putString("toast", message);
		msg.setData(bundle);
		BaseActivity.sendMessage(msg);
	}
	private void sendMessage(int what, byte[] frame) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = what;
		msg.obj = frame;
		BaseActivity.sendMessage(msg);
	}
	public void sendFailedMessage(){
		sendMessage("发送失败");
	}
	public boolean sendData(byte oneByte){
		if(mOutStream != null){
			try{
				mOutStream.write(oneByte);
				//mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, oneByte).sendToTarget();
				return true;
			}catch(IOException e){
				Log.e("sendData failed",e.getMessage());
				return false;
			}
		}
		else {
			sendMessage("发送失败,蓝牙未连接或已断开");
			return false;
		}
	}
	public void close() {
		try {
			if(mBluetoothSocket != null){
				mBluetoothSocket.close();
				setState(STATE_DISCONNECTED);
			}
		}
		catch (IOException e) {
			Log.e("mBluetoothSocket close", e.getMessage());
		}
		if(threadSendPacket!=null){
			threadSendPacket.stopThread();
			threadSendPacket.sendQueue.clear();
		}

	}
	public void addPacket(OutputPacket packet) {
		if(threadSendPacket!=null&&threadSendPacket.isWorking)
			threadSendPacket.addPacket(packet);
		else
			sendFailedMessage();

	}
	public void interruptThreadSendPacket(){
		threadSendPacket.interrupt();
	}
	class ThreadSendPacket extends Thread {

		private final String TAG = "ThreadSendPacket";
		public Queue<OutputPacket> sendQueue;
		public Queue<OutputPacket> confirmQueue;
		public boolean isWorking=false;
		public ThreadSendPacket() {
			sendQueue 	= new ConcurrentLinkedQueue<OutputPacket>();
			confirmQueue = new ConcurrentLinkedQueue<OutputPacket>();
			isWorking=true;
		}
		public void addPacket(OutputPacket packet) {
			sendQueue.add(packet);
			if(needWakeup)
				wakeUp();
		}
		protected synchronized void wakeUp() {
			notify();
		}
		public void stopThread(){
			isWorking =false;
			wakeUp();//避免等待，唤醒结束
			sendQueue.clear();
			confirmQueue.clear();

		}
		@Override
		public synchronized void run() {

			// TODO Auto-generated method stub
			Log.i(TAG,"ThreadSendMessage start run");
			while (isWorking) {
				needWakeup = false;
				while (sendQueue.size() > 0 || confirmQueue.size()>0) {
					while(stopSend){
						needWakeup = true;
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					OutputPacket packet = null;
					{
						packet = sendQueue.peek();
						if(sendData(packet.getData())){
							Log.i(TAG,"send successfully");
							if(packet.hasMessage())
								packet.sendMessage(true);
							sendQueue.poll();//从队列中删除
						}
						else{
							Log.e(TAG,"send failed");
							if(packet.hasMessage())
								packet.sendMessage(false);

							sendFailedMessage();

						}

					}
					try {
						Thread.sleep(1000);//数据包发送间隔
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						Log.e(TAG,"thread send packet interrupted");
						e.printStackTrace();
					}//休眠
				}
				try {
					if(isWorking){
						needWakeup = true;
						wait();
					}
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.e(TAG,"ThreadSendPacket stop run");

		}
	}
	private boolean stopSend = false;
	public void stopSend(){
		stopSend = true;
	}
	public void contiuneSend(){
		stopSend = false;
	}
}