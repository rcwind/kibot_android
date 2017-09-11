package com.kidil.kibot.activity;

import java.util.Iterator;
import java.util.LinkedList;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
/**
 * @author rcwind
 *
 */
public abstract class BaseActivity extends FragmentActivity {
	
	private static final String TAG = "BaseActivity";
	public static String rootpath =null;
	protected static LinkedList<BaseActivity> queue =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(queue==null)
			queue = new LinkedList<BaseActivity>();
		if (!queue.contains(this)){
			queue.add(this);
			Log.i(TAG, "queue.size="+queue.size());
		}
		if(Environment.getExternalStorageDirectory() != null)
			rootpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/kibot";
	}

	
	public static BaseActivity getActivity(int index) {
		if (index < 0 || index >= queue.size())
			throw new IllegalArgumentException("out of queue");
		return queue.get(index);
	}

	public static BaseActivity getCurrentActivity() {
		if(!queue.isEmpty())
			return queue.getLast();
		else
			return null;
	}
	public static void finishAllActivity(){
		while(queue.size()>0){
			Log.i(TAG, "exit...finish");
			queue.getLast().finish();
		}
	}
	public abstract void processMessage(Message msg);

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "onPause...");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume...");
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG, "onStart...");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG, "onStop...");
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy...");
    	//退出时销毁定位
       // if (mLocationClient != null)
           // mLocationClient.stop();
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		if(!queue.isEmpty())
			queue.removeLast();
		Log.i(TAG, "finish...queue.size="+queue.size());
	}

	public static void sendMessage(int cmd, String text) {
		Message msg = new Message();
		msg.obj = text;
		msg.what = cmd;
		sendMessage(msg);
	}

	public static void sendMessage(Message msg) {
		handler.sendMessage(msg);
	}

	public static void sendEmptyMessage(int what) {
		handler.sendEmptyMessage(what);
	}

	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//Log.i(TAG,"msg.what="+msg.what);
			switch (msg.what) {
			default:
				dispatchBaseActivityMessage(msg);//其他activity需要用到
				break;
			}
		}
	};
	private synchronized static void dispatchBaseActivityMessage(Message msg){
		if(queue==null)
			return;
        Iterator<BaseActivity> e = queue.iterator();  
        while(e.hasNext()){  
        	BaseActivity activity = e.next();
			activity.processMessage(msg);
        }
	}
	public static Handler getHandler(){
		return handler;
		
	}
	public static void exit() {
		finishAllActivity();
		queue=null;
		/*
		 * 杀死进程会使activity的声明周期异常，一些方法不会调用，比如ondestroy，onstop
		 */
		//android.os.Process.killProcess(android.os.Process.myPid());//结束当前进程，否则会出现形如java.net.BindException: bind failed: EADDRINUSE (Address already in use)错误
		try {
			System.gc();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
