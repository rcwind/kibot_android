package com.kidil.kibot.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017\9\27 0027.
 */

public class LogUtils {

	public static void logString(String string){

		StringBuilder builder = new StringBuilder();
		byte[] bytes = string.getBytes();
		for(int i = 0; i < bytes.length; i++){
			builder.append(String.format("0x%02x ", bytes[i]));
		}
		Log.d("Log String", "string: [" + string + "] string hex: [" + builder.toString() + "]");
	}
}
