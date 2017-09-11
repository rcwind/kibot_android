package com.kidil.kibot.activity;

import java.util.Iterator;
import java.util.Set;

import com.kidil.kibot.utils.Constants;
import com.kidil.kibot.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/**
 * @author rcwind
 * @since 2014.6.10
 *
 */
public class DeviceListActivity extends Activity{
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Button buttonScan;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			// TODO Auto-generated method stub
			 DeviceListActivity.this.bluetoothAdapter.cancelDiscovery();
		     String str1 = ((TextView)arg1).getText().toString();
		     String str2 = str1.substring(-17 + str1.length());
		     Intent mIntent = new Intent();
		     mIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, str2);
		     setResult(Constants.DEVICE_SELECTED, mIntent);
		     finish();
		}
	};
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(5);
	    setContentView(R.layout.activity_bounded_devices);
	    //setResult(0);
	    buttonScan = (Button)findViewById(R.id.button_scan);
	    buttonScan.setOnClickListener(new View.OnClickListener(){
	      public void onClick(View view)
	      {
	        discoveryDevices();
	        view.setVisibility(8);
	      }
	    });
	    mPairedDevicesArrayAdapter = new ArrayAdapter<String>(DeviceListActivity.this, R.layout.device_info);
	    mNewDevicesArrayAdapter = new ArrayAdapter<String>(DeviceListActivity.this, R.layout.device_info);
	    
	    ListView boundedListView = (ListView)findViewById(R.id.paired_devices);
	    boundedListView.setAdapter(mPairedDevicesArrayAdapter);
	    boundedListView.setOnItemClickListener(mDeviceClickListener);
	    
	    ListView newListView = (ListView)findViewById(R.id.new_devices);
	    newListView.setAdapter(mNewDevicesArrayAdapter);
	    newListView.setOnItemClickListener(mDeviceClickListener);
	    IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, discoveryFilter);
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, foundFilter);
		
	    Set<BluetoothDevice> boundedDevice = bluetoothAdapter.getBondedDevices();
	    //findViewById(R.id.title_paired_devices).setVisibility(0);
	    if (boundedDevice.size() > 0){
		      for(Iterator<BluetoothDevice> iterator = boundedDevice.iterator();iterator.hasNext();){
			      BluetoothDevice localBluetoothDevice = (BluetoothDevice)iterator.next();
			      mPairedDevicesArrayAdapter.add(localBluetoothDevice.getName() + "\n" + localBluetoothDevice.getAddress());
		      }
	    }
	    else {
	    	String str = DeviceListActivity.this.getResources().getText(R.string.none_paired).toString();
		    mPairedDevicesArrayAdapter.add(str);
	    }
	  }
	private void discoveryDevices(){
	    Log.d("DeviceListActivity", "discoveryDevices()");
	    setProgressBarIndeterminateVisibility(true);
	    setTitle(R.string.scanning);
	    findViewById(R.id.title_new_devices).setVisibility(0);
	    if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
	    bluetoothAdapter.startDiscovery();
	  }
	protected void onDestroy(){
	    super.onDestroy();
	    if (bluetoothAdapter != null) 	bluetoothAdapter.cancelDiscovery();
	    unregisterReceiver(mReceiver);
	  }
	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
	    public void onReceive(Context context, Intent intent)
	    {
	      String state = intent.getAction();
	      if (BluetoothDevice.ACTION_FOUND.equals(state)){
	    	  BluetoothDevice localBluetoothDevice = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
	    	  if (localBluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED){
	    		  mNewDevicesArrayAdapter.add(localBluetoothDevice.getName() + "\n" + localBluetoothDevice.getAddress());
	    	  }
	      }
	      if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(state)){
	          setProgressBarIndeterminateVisibility(false);
	          setTitle(R.string.select_device);
	          if (mNewDevicesArrayAdapter.getCount() == 0){
		          String str = DeviceListActivity.this.getResources().getText(R.string.none_found).toString();
		          mNewDevicesArrayAdapter.add(str);
	          }
	       }
	    }
	  };
}