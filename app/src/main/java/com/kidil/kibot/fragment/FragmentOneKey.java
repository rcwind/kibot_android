package com.kidil.kibot.fragment;

import com.kidil.kibot.R;
import com.kidil.kibot.activity.MainActivity;
import com.kidil.kibot.activity.MainActivity.OnMessageListener;
import com.kidil.kibot.comm.OutputPacket;
import com.kidil.kibot.utils.Constants;
import com.kidil.kibot.utils.LogUtils;
import com.kidil.kibot.utils.SdcardUtils;
import com.kidil.kibot.view.LogLevelSelectView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

/**
 * @author rcwind
 */
public class FragmentOneKey extends Fragment implements OnClickListener, OnMessageListener {
	private final String TAG = "FragmentOneKey";
	private View view ;
	private Context context;
//	private TextView leftPwmTextView;
//	private TextView rightPwmTextView;
	private TextView batteryTextView;

	private EditText pEditText;
	private EditText iEditText;
	private EditText dEditText;

	private EditText forwardDistanceEditText;
	private EditText forwardVelEditText;
	private EditText turnAngleEditText;
	private EditText turnVelEditText;

	private EditText encrypt1EditText;
	private EditText encrypt2EditText;

	private EditText buzzerCntEditText;
	private EditText buzzerTimeEditText;
	private TextView logLevelTextView;
//	private int preLeftPwm;
//	private int preRightPwm;
//	private final int MIN_PWM = 5;
//	private SeekBar leftSeekBar;
//	private SeekBar rightSeekBar;
//	 限幅处理，应避免pwm变化太大
//	private final int MAX_DELTA_PWM = 15;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.i(TAG,"onCreateView...");
		view=inflater.inflate(R.layout.fragment_one_key, container, false);
		context = view.getContext();
//		view.findViewById(R.id.left_pwm_reset_btn).setOnClickListener(this);
//		view.findViewById(R.id.right_pwm_reset_btn).setOnClickListener(this);
		view.findViewById(R.id.set_forward_btn).setOnClickListener(this);
		view.findViewById(R.id.set_encrypt_btn).setOnClickListener(this);
		view.findViewById(R.id.get_encrypt_btn).setOnClickListener(this);
		view.findViewById(R.id.set_turn_btn).setOnClickListener(this);
		view.findViewById(R.id.mergency_stop_btn).setOnClickListener(this);
		view.findViewById(R.id.buzzer_set_btn).setOnClickListener(this);
		view.findViewById(R.id.get_pid_btn).setOnClickListener(this);
		view.findViewById(R.id.set_pid_btn).setOnClickListener(this);
		view.findViewById(R.id.controller_reset_btn).setOnClickListener(this);
		view.findViewById(R.id.log_level_select_btn).setOnClickListener(this);
//		leftPwmTextView 	= (TextView) view.findViewById(R.id.left_wheel_pwm_tv);
//		rightPwmTextView 	= (TextView) view.findViewById(R.id.right_wheel_pwm_tv);
		forwardDistanceEditText = (EditText) view.findViewById(R.id.forward_distance_et);
		forwardVelEditText 		= (EditText) view.findViewById(R.id.forward_velocity_et);
		turnAngleEditText 		= (EditText) view.findViewById(R.id.turn_angle_et);
		turnVelEditText 		= (EditText) view.findViewById(R.id.turn_angle_velocity_et);
		batteryTextView 	= (TextView) view.findViewById(R.id.battery_tv);
		logLevelTextView 	= (TextView) view.findViewById(R.id.log_level_tv);
		pEditText 			= (EditText) view.findViewById(R.id.pid_p_et);
		iEditText 			= (EditText) view.findViewById(R.id.pid_i_et);
		dEditText 			= (EditText) view.findViewById(R.id.pid_d_et);
		buzzerCntEditText 	= (EditText) view.findViewById(R.id.buzzer_cnt_et);
		buzzerTimeEditText = (EditText) view.findViewById(R.id.buzzer_time_et);

		encrypt1EditText = (EditText) view.findViewById(R.id.encrypt_et1);
		encrypt2EditText = (EditText) view.findViewById(R.id.encrypt_et2);

/*		leftSeekBar = (SeekBar)view.findViewById(R.id.left_wheel_pwm_sb);
		leftSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				leftPwmTextView.setText(getPwmByProgress(progress)+"%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				preLeftPwm = getPwmByProgress(seekBar.getProgress());
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int pwm = getPwmByProgress(seekBar.getProgress());
				if((preLeftPwm == pwm))
					return;
				else if(Math.abs(preLeftPwm - pwm) > MAX_DELTA_PWM){
					int delta = pwm > 0 ? MAX_DELTA_PWM : -MAX_DELTA_PWM;
					int out = preLeftPwm+delta;
					Toast.makeText(context, "set lpwm "+out, Toast.LENGTH_SHORT).show();
					MainActivity.bluetoothCommService.addPacket(new OutputPacket("rpwm="+out));
					seekBar.setProgress(out+100);
				}
				else
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("lpwm="+pwm));
			}

		});
		rightSeekBar = (SeekBar)view.findViewById(R.id.right_wheel_pwm_sb);
		rightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				rightPwmTextView.setText(getPwmByProgress(progress)+"%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				preRightPwm = getPwmByProgress(seekBar.getProgress());
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int pwm = getPwmByProgress(seekBar.getProgress());
				if((preRightPwm == pwm))
					return;
				else if(Math.abs(preRightPwm - pwm) > MAX_DELTA_PWM){
					int delta = pwm > 0 ? MAX_DELTA_PWM : -MAX_DELTA_PWM;
					int out = preRightPwm+delta;
					Toast.makeText(context, "set rpwm "+out, Toast.LENGTH_SHORT).show();
					MainActivity.bluetoothCommService.addPacket(new OutputPacket("rpwm="+out));
					seekBar.setProgress(out+100);
				}
				else
					MainActivity.bluetoothCommService.addPacket(new OutputPacket("rpwm="+pwm));
			}

		});*/
		((CheckBox)view.findViewById(R.id.buzzer_on_cb)).setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
					MainActivity.bluetoothCommService.addPacket(new OutputPacket("buzzer on"));
				else
					MainActivity.bluetoothCommService.addPacket(new OutputPacket("buzzer off"));
			}});
		String content = new SdcardUtils().getEncryption();
		if(content != null) {
			String[] encryption = content.trim().split(",");
			if(encryption.length == 2) {
				encrypt1EditText.setText(encryption[0]);
				encrypt2EditText.setText(encryption[1]);
			}
		}
		return view;
	}
	/*	private int getPwmByProgress(int progress){
            return progress - 100;
        }*/
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
				String log = new String(frame).trim();
				LogUtils.logString(log);
				if(log.contains("[INFO]") && log.contains("battery")){
					String[] strings = log.split("=");
					batteryTextView.setText(strings[1].trim());
				}
				else if(log.contains("p = ") && log.contains("i = ") && log.contains("d = ")){
					String[] strings = log.split(" = ");
//					String d = strings[3].substring(strings[3].length() - 2);
					String d = strings[3].substring(0, strings[3].length() - 3);
					LogUtils.logString(strings[3]);
					pEditText.setText(strings[1].trim().split(",")[0]);
					iEditText.setText(strings[2].trim().split(",")[0]);
					dEditText.setText(d.trim());
				}
				else if(log.contains("encryption key = ")){
					String[] strings = log.split(" = ");
					String[] encryption = strings[1].trim().split(",");
					LogUtils.logString(encryption[1]);
					String encryption_key0 = encryption[0];
					String encryption_key1 = encryption[1].substring(0, encryption[1].length() - 3);
					new SdcardUtils().saveEncryption(encryption_key1);
					encrypt1EditText.setText(encryption_key0);
					encrypt2EditText.setText(encryption_key1);
				}
				break;
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.buzzer_set_btn:
				String cnt = buzzerCntEditText.getText().toString();
				String time = buzzerTimeEditText.getText().toString();
				if(cnt.equals("") || time.equals("")){
					Toast.makeText(context, "参数为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if(Integer.valueOf(cnt) <= 0){
					Toast.makeText(context, "次数必须大于0", Toast.LENGTH_SHORT).show();
					return;
				}
				if(Integer.valueOf(time) < 50){
					Toast.makeText(context, "时间必须大于50ms", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(Integer.valueOf(time) > 60000){
					Toast.makeText(context, "时间必须小于60s", Toast.LENGTH_SHORT).show();
					return;
				}
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("buzzer "+cnt+" "+time));
				break;
			case R.id.get_pid_btn:
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("getpid"));
				break;
			case R.id.set_pid_btn:
				String p = pEditText.getText().toString();
				String i = iEditText.getText().toString();
				String d = dEditText.getText().toString();
				if(p.equals("") || i.equals("") || d.equals("")){
					Toast.makeText(context, "参数为空", Toast.LENGTH_SHORT).show();
					return;
				}
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("setpid "+p+" "+i+" "+d));
				break;
			case R.id.get_encrypt_btn:
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("getencrypt"));
				break;
			case R.id.set_encrypt_btn:
				String encrypt1 = encrypt1EditText.getText().toString();
				String encrypt2 = encrypt2EditText.getText().toString();
				if(encrypt1.equals("") || encrypt2.equals("") ){
					Toast.makeText(context, "参数为空", Toast.LENGTH_SHORT).show();
					return;
				}
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("setencrypt " + encrypt1 + " " + encrypt2));
				break;
			case R.id.mergency_stop_btn:
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("setrobot stop"));
				break;
/*		case R.id.left_pwm_reset_btn:
			leftSeekBar.setProgress(100);
			MainActivity.bluetoothCommService.addPacket(new OutputPacket("lpwm=0"));
			break;
		case R.id.right_pwm_reset_btn:
			rightSeekBar.setProgress(100);
			MainActivity.bluetoothCommService.addPacket(new OutputPacket("rpwm=0"));
			break;*/
			case R.id.set_forward_btn:
				String distance = forwardDistanceEditText.getText().toString().trim();
				String velocity = forwardVelEditText.getText().toString().trim();
				int vel = Integer.valueOf(velocity);
				if(Math.abs(vel) < 50) {
					Toast.makeText(context, "速度至少为50mm/s", Toast.LENGTH_SHORT).show();
					return;
				}
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("setvx "+velocity+" "+distance));
				break;
			case R.id.set_turn_btn:
				String angle = turnAngleEditText.getText().toString().trim();
				String angle_velocity = turnVelEditText.getText().toString().trim();
				int angle_vel = Integer.valueOf(angle_velocity);
				if(Math.abs(angle_vel) < 20) {
					Toast.makeText(context, "速度至少为20°/s", Toast.LENGTH_SHORT).show();
					return;
				}
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("setwz "+angle_velocity+" "+angle));
				break;
			case R.id.controller_reset_btn:
				MainActivity.bluetoothCommService.addPacket(new OutputPacket("reset"));
				break;
			case R.id.log_level_select_btn:
				final LogLevelSelectView contentView = new LogLevelSelectView(context);
				AlertDialog dialog = new AlertDialog.Builder(context).
						setTitle("选择").
						setView(contentView.view).
						setNegativeButton("取消", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}

						}).
						show();
				contentView.setDialog(dialog);
				dialog.setOnDismissListener(new OnDismissListener(){

					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						String level = contentView.getLogLevel();
						if(level == null || level.equals(""))
							return;
						logLevelTextView.setText(level);
						MainActivity.bluetoothCommService.addPacket(new OutputPacket("setloglv "+level));
					}
				});
				break;
		}
	}
}
