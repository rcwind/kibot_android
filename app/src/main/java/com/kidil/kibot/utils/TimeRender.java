package com.kidil.kibot.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 
 * @author �����
 *
 */
public class TimeRender {
	@SuppressLint("SimpleDateFormat")
	public static String getDate(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}
	public static String getTime() {
			
			String dateStr =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			return dateStr;
	}
	public static String getDate() {
		
		String dateStr =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		return dateStr;
		/*
		Time time = new Time("GMT+8");    
        time.setToNow();   
        int year = time.year;   
        int month = time.month;   
        int day = time.monthDay;   
        int hour = time.hour; 
        int minute = time.minute; 
        int sec = time.second;  */ 
		/*
	    Calendar calendar = Calendar.getInstance();
		int year 	= calendar.get(Calendar.YEAR);
	    int month	= calendar.get(Calendar.MONTH)+1;
	    int day 	= calendar.get(Calendar.DAY_OF_MONTH);
	    int hour 	= calendar.get(Calendar.HOUR_OF_DAY);
	    int minute 	= calendar.get(Calendar.MINUTE);
	    int sec 	= calendar.get(Calendar.SECOND);

		return String.format("%d-%02d-%02d %02d:%02d:%02d",year,month,day,hour,minute,sec);
		*///getDate("MM-dd hh:mm:ss");
	}
}
