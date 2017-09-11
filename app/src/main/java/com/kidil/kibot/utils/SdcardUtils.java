package com.kidil.kibot.utils;

import java.io.*;

import android.os.Environment;
import android.util.Log;

public class SdcardUtils {
	
	private static final String TAG = "SdcardUtils";
	private static final String LOG_DIR = "/log";
	
	private static String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/kibot";
	
	public boolean isFileExist(String filename){
		File file = new File(rootpath);
		if(!file.exists()){
			return false;
		}
		file = new File(rootpath, filename);
		if(file.exists()){
			return true;
		}
		return false;
	}
	public boolean isDirExist(String dir){
		File file = new File(dir);
		
		if(file.exists()){
			return true;
		}
		return false;
	}
	public void createDir(String dir){
		File file = new File(dir);
		if(!file.exists()){
			Log.d(TAG, "mkdir");
			file.mkdirs();
		}
	}

	public void deleteFile(String filename){
		File file = new File(rootpath, filename);
		if(file.exists())
			file.delete();
	}

	public void saveLog(String fileName, String content){

		if(fileName == null || fileName.equals("")){
			Log.e(TAG,"saveFileToSD fail,filename is null or ...");
			return;
		}
		if(!isDirExist(rootpath + LOG_DIR))
			createDir(rootpath + LOG_DIR);
		
		File file = new File(rootpath + LOG_DIR, fileName);	
		if(file.exists()){
			file.delete();
		}

		byte data[] = content.getBytes();
		file = new File(rootpath + LOG_DIR, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);	
			fos.write(data);
			fos.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void saveEncryption(String content){

		if(!isDirExist(rootpath))
			createDir(rootpath);

		File file = new File(rootpath, "encryption.txt");
		if(file.exists()){
			file.delete();
		}

		byte data[] = content.getBytes();
		file = new File(rootpath, "encryption.txt");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String  getEncryption(){
		File file = new File(rootpath);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(rootpath, "encryption.txt");
		if( !file.exists() ){
			Log.e(TAG, file.getAbsolutePath()+"文件不存在");
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			byte[] data = new byte[inputStream.available()];
			inputStream.read(data);
			return new String(data);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if( inputStream != null ){
					inputStream.close();
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
