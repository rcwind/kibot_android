package com.kidil.kibot.fragment;

import com.kidil.kibot.R;
import com.kidil.kibot.activity.MainActivity;
import com.kidil.kibot.activity.MainActivity.OnMessageListener;
import com.kidil.kibot.comm.OutputPacket;
import com.kidil.kibot.utils.Constants;
import com.kidil.kibot.utils.SdcardUtils;
import com.kidil.kibot.utils.TimeRender;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author rcwind
 */
public class FragmentDebug extends Fragment implements OnClickListener, OnMessageListener{
	
	//private final String TAG = "FragmentTerminalSettings";
	private View view;
	private Context context;
	private TextView logTextView;
	private EditText inputEditText;
	private ScrollView scrollView;
	private boolean canScroll = true;
	private int logLineCnt;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.fragment_debug, container, false);
		context = view.getContext();
		scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
		view.findViewById(R.id.send_button).setOnClickListener(this);
		view.findViewById(R.id.clear_button).setOnClickListener(this);
		logTextView = (TextView) view.findViewById(R.id.log_tv);
		logTextView.setMovementMethod(new ScrollingMovementMethod());
		inputEditText = (EditText) view.findViewById(R.id.input_cmd);
		/*Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.obj = "[INFO]aaaaaaaaaaaaaaaaaaaaaaaa\r\n".getBytes();
				msg.what = Constants.MESSAGE_RECV;
				BaseActivity.sendMessage(msg);
			}}, 500, 500);*/
		
		scrollView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_MOVE){
					
					int scrollY=v.getScrollY();
	                int height=v.getHeight();
	                int scrollViewMeasuredHeight=scrollView.getChildAt(0).getMeasuredHeight();
	                if((scrollY+height)==scrollViewMeasuredHeight){
	                        //System.out.println("滑动到了底部 scrollY="+scrollY);
	                	canScroll = true;
	                }
	                else
	                	canScroll = false;
					
				}

				return false;
			}});
		
		return view;
	}
	
	public Spanned getTextByLevel(String log){
		if(!log.contains("\r\n"))
			Log.e("getTextByLevel", "no \\r\\n found");
		String setText = log.replace("\r\n", "<br>");
		String color = null;
		boolean showTime = true;
		if(log.contains("[DEBUG]"))
			color = "#000000";
		else if(log.contains("[INFO]"))
			color = "#2abb25";
		else if(log.contains("[WARN]"))
			color = "#cf9527";
		else if(log.contains("[ERROR]"))
			color = "#cd0533";
		else if(log.contains("[CMD]"))
			color = "#2234e8";
		else{
			color = "#000000";
			showTime = false;
		}
		if(showTime)	
			return Html.fromHtml("<font color='"+color+"'>[" + TimeRender.getDate("HH:mm:ss") + "]" + setText+"</font>");
		else
			return Html.fromHtml("<font color='"+color+"'>" + setText + "</font>");

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.send_button:
			// 隐藏键盘
			InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);
			String input = inputEditText.getText().toString().trim();
			if(input.equals("")){
				MainActivity.bluetoothCommService.addPacket(new OutputPacket());
				break;
			}
			MainActivity.bluetoothCommService.addPacket(new OutputPacket(input));
			break;
		case R.id.clear_button:
			inputEditText.setText("");break;	
		}
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		((MainActivity)activity).registerOnMessageListener(this);
	}
	@Override
	public boolean onEvent(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
			case Constants.MESSAGE_RECV:{
				byte[] frame = (byte[]) msg.obj;
				String log = new String(frame);
//				if (log.contains("\033["))
				String prompt1 = "[kidil ~$] ";
				String prompt2 = "\033[2K\r";
				if (log.startsWith(prompt1)){
					log = log.substring(prompt1.length(), log.length());
				}
				if (log.startsWith(prompt2)){
					log = log.substring(prompt2.length(), log.length());
				}

				if (log.startsWith("\033[m")){
					log = log.substring(3, log.length() - 5);
					log += "\r\n";
				}
				else if (log.startsWith("\033[0;3")){
					log = log.substring(7, log.length() - 5);
					log += "\r\n";
				}
				else if (log.startsWith("\033[2K\r")){
					return false;
				}
				else
					return false;

//				if((logTextView.getHeight() / logTextView.getTextSize()) > 500)
				if(logLineCnt > 1000) {
					saveLog();
					logLineCnt = 0;
					logTextView.setText("");
				}
				logLineCnt++;
				logTextView.append(getTextByLevel(log));
				if(canScroll)
					scroll2Bottom(scrollView, logTextView);
				break;
			}
		}
		return false;
	}
	private void saveLog(){
		String text = logTextView.getText().toString();
		if(text.equals(""))
			return;
		new SdcardUtils().saveLog(TimeRender.getDate("yyyy-MM-dd HH-mm-ss")+".txt", text);
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		saveLog();
	}
	public void scroll2Bottom(final ScrollView scroll, final View inner) {
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (scroll == null || inner == null) {
					return;
				}
				// 内层高度超过外层
				int offset = inner.getMeasuredHeight() - scroll.getMeasuredHeight();
				if (offset < 0) {
					//System.out.println("定位...");
					offset = 0;
				}
				scroll.scrollTo(0, offset);
			}
		});
	}	
}
