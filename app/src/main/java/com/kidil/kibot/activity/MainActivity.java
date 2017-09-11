package com.kidil.kibot.activity;


import java.util.ArrayList;
import java.util.List;
import com.kidil.kibot.adapter.FragmentTabAdapter;
import com.kidil.kibot.comm.BluetoothCommService;
import com.kidil.kibot.comm.OutputPacket;
import com.kidil.kibot.fragment.FragmentOneKey;
import com.kidil.kibot.fragment.FragmentDebug;
import com.kidil.kibot.utils.Constants;
import com.kidil.kibot.R;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author rcwind
 */
public class MainActivity extends BaseActivity implements OnClickListener {
	private final String TAG = "MainActivity";
	private long exitTime;
	private Context context;
	private BluetoothDevice mBluetoothDevice ;
	public static BluetoothCommService bluetoothCommService;
	private BluetoothAdapter mBluetoothAdapter ;
	private final String MENU_ENCODER = "编码器?";
	private final String MENU_PWM = "pwm?";
	private final String MENU_HEADING = "heading?";
//	private final String MENU_ENCRYPT = "密钥?";
	private final String MENU_IMUCAL = "校准";
	private final String MENU_ROTATE = "旋转";
	private final String MENU_FORWARD = "前进";
	private final String MENU_BACKWARD = "后退";
	private final String MENU_BATTERY = "电池电压?";
	private List<Fragment> fragmentArray = new ArrayList<Fragment>();
	private RadioGroup radioGroup;	
	private AlertDialog alertDialog;
	private TextView tv_connect_bt;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.activity_main);
		context =this;
		(tv_connect_bt = (TextView) findViewById(R.id.tv_connect_bt)).setOnClickListener(this);
		fragmentArray.add(new FragmentDebug());
		fragmentArray.add(new FragmentOneKey());
		radioGroup = (RadioGroup) findViewById(R.id.tabs_radio_group);
		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragmentArray, R.id.tab_content, radioGroup);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {}
        });
        init();
	}
	private void init() {
		mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",Toast.LENGTH_SHORT).show();
        }
        else 
        	bluetoothCommService = new BluetoothCommService(this);
        
        if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
        	mBluetoothAdapter.enable();
        }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		Log.v(TAG,"onActivityResult");
   		switch(resultCode) {
		case Constants.DEVICE_SELECTED:String str = intent.getExtras().getString(BluetoothDevice.EXTRA_DEVICE);
				mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(str);
				if(bluetoothCommService.getState()!=BluetoothCommService.STATE_CONNECTED)
					bluetoothCommService.connect(mBluetoothDevice);//连接
				else if(bluetoothCommService.getState()==BluetoothCommService.STATE_CONNECTED)
					Toast.makeText(context,"设备已连接",Toast.LENGTH_SHORT).show();
				break;

		default:break;
		}	
	}	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG,"onDestroy...");
		bluetoothCommService.close();
		//android.os.Process.killProcess(android.os.Process.myPid());//确保在最后一步杀死该进程
	}

	@Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 

	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){ 
	    	
	         if((System.currentTimeMillis()-exitTime) > 2000){ 
	             Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show(); 
	             exitTime = System.currentTimeMillis(); 
	         } 
	         else { 
	        	 if(mBluetoothAdapter.isEnabled())
	        		 mBluetoothAdapter.disable();
	        	 //android.os.Process.killProcess(android.os.Process.myPid());//确保在最后一步杀死该进程
	        	 finish();
	       	 } 
	         return true; 
	         
	    } 
	    else if((keyCode == KeyEvent.KEYCODE_MENU ) && event.getAction() == KeyEvent.ACTION_DOWN){		
	    }	
	    return super.onKeyDown(keyCode, event);
    }

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
        case Constants.MESSAGE_BT_STATE_CHANGE:
            switch (msg.arg1) {
                case BluetoothCommService.STATE_CONNECTING:
                	tv_connect_bt.setVisibility(View.VISIBLE);
                	tv_connect_bt.setText("正在连接蓝牙设备[ "+(bluetoothCommService.bluetoothDevice!=null?bluetoothCommService.bluetoothDevice.getAddress():"ERROR")+" ]...");
                	tv_connect_bt.setClickable(false);
                	break;
                case BluetoothCommService.STATE_CONNECTED:
                	Toast.makeText(context,"已连接上[ "+(bluetoothCommService.bluetoothDevice!=null?bluetoothCommService.bluetoothDevice.getAddress():"ERROR")+" ]",Toast.LENGTH_LONG).show();
                	tv_connect_bt.setVisibility(View.GONE);
                	break;
                case BluetoothCommService.STATE_CONNECTION_LOST:
                case BluetoothCommService.STATE_DISCONNECTED:
                	tv_connect_bt.setClickable(true);
                	tv_connect_bt.setVisibility(View.VISIBLE);
            		tv_connect_bt.setText("连接已断开，点击重新连接>>");
            		break;
                case BluetoothCommService.STATE_CONNECT_FAILED:
                	tv_connect_bt.setClickable(true);
            		tv_connect_bt.setVisibility(View.VISIBLE);
            		tv_connect_bt.setText("连接失败，点击重新连接>>");
            		break;
            } break;
        case Constants.MESSAGE_TOAST:
        	String message = msg.getData().getString("toast");
        	if(message.equals("发送失败")){
        		if(alertDialog!=null)
        			alertDialog.dismiss();
        	}
            Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
          
            break;
        case Constants.MESSAGE_RECV:
        	//Log.d(TAG, "recv");
        	//Log.i(TAG, new String((byte[]) (msg.obj)));
        	for(OnMessageListener listener:onMessageListeners){
	    		if(listener.onEvent(msg)){
	    			break;
	    		}
	    	}        	
        	break;

		}
	}
	/*
	 * 事件接口
	 */
	public interface OnMessageListener {
	    public boolean onEvent(Message msg);
	}
	private ArrayList<OnMessageListener> onMessageListeners = new ArrayList<OnMessageListener>();
	public void registerOnMessageListener(OnMessageListener onMessageListener) {
		//Log.i(TAG,"registerMyOnEventListener");
		onMessageListeners.add(onMessageListener);
	}
	public void removeMyOnEventListener(OnMessageListener onMessageListener) {
		//Log.i(TAG,"registerMyOnEventListener");
		onMessageListeners.remove(onMessageListener);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.tv_connect_bt:{
			Intent intent = new Intent(context,DeviceListActivity.class);
			startActivityForResult(intent,Constants.DEVICE_SELECTED);
			}break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(this.MENU_BATTERY);
		menu.add(this.MENU_ENCODER);
		menu.add(this.MENU_PWM);
		menu.add(this.MENU_HEADING);
//		menu.add(this.MENU_ENCRYPT);
		menu.add(this.MENU_IMUCAL);
		menu.add(this.MENU_ROTATE);
		menu.add(this.MENU_FORWARD);
		menu.add(this.MENU_BACKWARD);
		return super.onCreateOptionsMenu(menu);
	}
	/*处理menu的事件*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//得到当前选中的MenuItem的ID,
		String menuString = (String) item.getTitle();

		if(menuString.equals(this.MENU_BACKWARD))
			bluetoothCommService.addPacket(new OutputPacket("setvr -40 0"));
		else if(menuString.equals(this.MENU_ENCODER))
			bluetoothCommService.addPacket(new OutputPacket("getenc"));
//		else if(menuString.equals(this.MENU_ENCRYPT))
//			bluetoothCommService.addPacket(new OutputPacket("encrypt=?"));
		else if(menuString.equals(this.MENU_FORWARD))
			bluetoothCommService.addPacket(new OutputPacket("setvr 40 0"));
		else if(menuString.equals(this.MENU_HEADING))
			bluetoothCommService.addPacket(new OutputPacket("getheading"));
		else if(menuString.equals(this.MENU_IMUCAL))
			bluetoothCommService.addPacket(new OutputPacket("imucali"));
		else if(menuString.equals(this.MENU_PWM))
			bluetoothCommService.addPacket(new OutputPacket("getpwm"));
		else if(menuString.equals(this.MENU_ROTATE))
			bluetoothCommService.addPacket(new OutputPacket("setvr 40 1"));
		else if(menuString.equals(this.MENU_BATTERY))
			bluetoothCommService.addPacket(new OutputPacket("getbatv"));
		return true;
	}
}
