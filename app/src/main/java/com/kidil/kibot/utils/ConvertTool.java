package com.kidil.kibot.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

	/**
	 * Bitmap��DrawAble��byte[]��InputStream֮���ת��������
	 * @author Administrator
	 *
	 */
public class ConvertTool {
		private static ConvertTool tools = new ConvertTool();

		public static ConvertTool getInstance() {
			if (tools == null) {
				tools = new ConvertTool();
				return tools;
			}
			return tools;
		}
	    /** 
	     * �ļ�ת�ֽ� 
	     *  
	     * @param file 
	     * @return 
	     * @throws IOException 
	     */ 
	    public byte[] getFileBytes(File file) throws IOException {  
	        BufferedInputStream bis = null;  
	        try {  
	            bis = new BufferedInputStream(new FileInputStream(file));  
	            int bytes = (int) file.length();  
	            byte[] buffer = new byte[bytes];  
	            int readBytes = bis.read(buffer);  
	            if (readBytes != buffer.length) {  
	                throw new IOException("Entire file not read");  
	            }  
	            return buffer;  
	        } finally {  
	            if (bis != null) {  
	                bis.close();  
	            }  
	        }  
	    }  
		// ��byte[]ת����InputStream
		public InputStream Byte2InputStream(byte[] b) {
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			return bais;
		}

		// ��InputStreamת����byte[]
		public byte[] InputStream2Bytes(InputStream is) {
			String str = "";
			byte[] readByte = new byte[1024];
			//int readCount = -1;
			try {
				while (is.read(readByte, 0, 1024) != -1) {
					str += new String(readByte).trim();
				}
				return str.getBytes();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		// ��Bitmapת����InputStream
		public InputStream Bitmap2InputStream(Bitmap bm) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			return is;
		}

		// ��Bitmapת����InputStream
		public InputStream Bitmap2InputStream(Bitmap bm, int quality) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			return is;
		}

		// ��InputStreamת����Bitmap
		public Bitmap InputStream2Bitmap(InputStream is) {
			return BitmapFactory.decodeStream(is);
		}

		// Drawableת����InputStream
		public InputStream Drawable2InputStream(Drawable d) {
			Bitmap bitmap = this.drawable2Bitmap(d);
			return this.Bitmap2InputStream(bitmap);
		}

		// InputStreamת����Drawable
		public Drawable InputStream2Drawable(InputStream is) {
			Bitmap bitmap = this.InputStream2Bitmap(is);
			return this.bitmap2Drawable(bitmap);
		}

		// Drawableת����byte[]
		public byte[] Drawable2Bytes(Drawable d) {
			Bitmap bitmap = this.drawable2Bitmap(d);
			return this.Bitmap2Bytes(bitmap);
		}

		// byte[]ת����Drawable
		public Drawable Bytes2Drawable(byte[] b) {
			Bitmap bitmap = this.Bytes2Bitmap(b);
			return this.bitmap2Drawable(bitmap);
		}

		// Bitmapת����byte[]
		public byte[] Bitmap2Bytes(Bitmap bm) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}

		// byte[]ת����Bitmap
		public Bitmap Bytes2Bitmap(byte[] b) {
			if (b.length != 0) {
				return BitmapFactory.decodeByteArray(b, 0, b.length);
			}
			return null;
		}

		// Drawableת����Bitmap
		public Bitmap drawable2Bitmap(Drawable drawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		}

		// Bitmapת����Drawable
		public Drawable bitmap2Drawable(Bitmap bitmap) {
			@SuppressWarnings("deprecation")
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			Drawable d = (Drawable) bd;
			return d;
		}
		//public byte[] long2Bytes(long data){
			
		//}

}
