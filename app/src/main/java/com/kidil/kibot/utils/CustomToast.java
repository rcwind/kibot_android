package com.kidil.kibot.utils;

import com.kidil.kibot.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast{
	public static Toast makeText(Context context, CharSequence text, int duration) {
		Toast result = new Toast(context);
		//��ȡLayoutInflater����
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		//��layout�ļ�����һ��View����
		View layout = inflater.inflate(R.layout.custom_toast, null);
		
		//ʵ����ImageView��TextView����
		//ImageView imageView = (ImageView) layout.findViewById(R.id.image0);
		TextView textView = (TextView) layout.findViewById(R.id.text);		
		textView.setText(text);
		
		result.setView(layout);
		result.setGravity(Gravity.CENTER_VERTICAL, 0, -300);
		result.setDuration(duration);
		
		return result;
	}
	public static void makeTextShort(Context context,String text) {
		CustomToast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void makeTextLong(Context context,String text) {
		CustomToast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
}
