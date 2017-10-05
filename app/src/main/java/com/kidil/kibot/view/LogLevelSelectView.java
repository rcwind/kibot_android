package com.kidil.kibot.view;

import com.kidil.kibot.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LogLevelSelectView extends View{
	private final String TAG = "LogLevelSelectView";
	public View view;
	private ListView listView;
	private String level = null;
	AlertDialog dialog;
	private final String[] logLevel = new String[] {
		    "debug", "info", "warn", "error", "cmd"
		    };
	public LogLevelSelectView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		view = LayoutInflater.from(context).inflate(R.layout.dialog_content_view_log_level_select, null);
		listView =  (ListView) view.findViewById(R.id.list_view);
		listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, logLevel));

		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				level = logLevel[arg2];
				dialog.dismiss();
			}
			
		});
		
	}
	public String getLogLevel(){
		return level;
	}
	public void setDialog(AlertDialog dialog ){
		this.dialog =dialog;
	}
}
