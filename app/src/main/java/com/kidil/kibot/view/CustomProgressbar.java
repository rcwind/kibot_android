package com.kidil.kibot.view;

import com.kidil.kibot.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomProgressbar {
	//private Context context;
	private final AlertDialog mAlertDialog;
	private Button negativeButton;
	private TextView titleTV;
	private ProgressBar progressBar;

	public CustomProgressbar(Context context) {
		// TODO Auto-generated constructor stub
		//this.context=context;
		mAlertDialog = new AlertDialog.Builder(context).create();
		show();
		Window window = mAlertDialog.getWindow();
		// ���ô��ڵ�����ҳ��
		//window.requestFeature(Window.FEATURE_CUSTOM_TITLE);
		//window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.my_dialog);
		window.setContentView(R.layout.my_progressbar);
		// Ϊȷ�ϰ�ť����¼�,ִ���˳�Ӧ�ò���
		negativeButton = (Button) window.findViewById(R.id.btn_cancel);
		titleTV= (TextView) window.findViewById(R.id.title_text_view);
		progressBar = (ProgressBar)window.findViewById(R.id.update_progress);
		progressBar.setIndeterminate(false);
		//���ý����������ֵ
		//progressBar.setMax(100);
		//progressBar.setProgress(0);
		//progressBar.setX(0);
		//progressBar.setY(0);

	}
	public void setProgress(int progress){
		progressBar.setProgress(progress);
		
	}
	public void setTitle(String str){
		titleTV.setVisibility(View.VISIBLE);
		titleTV.setText(str);
		
	};
	public void setNegativeButton(String str,OnClickListener listener){
		negativeButton.setVisibility(View.VISIBLE);
		negativeButton.setText(str);
		negativeButton.setOnClickListener((android.view.View.OnClickListener) listener);	
	};
	public void show(){
		mAlertDialog.show();	
	}
	public void dismiss(){
		mAlertDialog.dismiss();	
	}
}
