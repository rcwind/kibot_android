package com.kidil.kibot.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.kidil.kibot.view.CustomProgressbar;
import com.kidil.kibot.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class UpdateManager {
	private String strInfo;
	private static final int HAZNOT_NEW_SOFTWARE = 4;
	private static final int HAZ_NEW_SOFTWARE = 3;
	//������...
	private static final int DOWNLOAD = 1;
	//�������
	private static final int DOWNLOAD_FINISH = 2;
	//���������XML��Ϣ
	HashMap<String , String> mHashMap;
	//���ر���·��
	private String mSavePath;
	//��¼����������
	private int progress;
	//�Ƿ�ȡ������
	private boolean cancelUpdate = false;
	//�����Ķ���
	private Context mContext;
	//���½������ĶԻ���
	//private Dialog mDownloadDialog;
	private CustomProgressbar mMyProgressbar;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
			//�����С�����
			case DOWNLOAD:
				//���½�����
				Log.i("progress",Integer.toString(progress));
				mMyProgressbar.setProgress(progress);
				break;
			//�������
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
				installApk();
				break;
			case HAZ_NEW_SOFTWARE:
					showNoticeDialog();//��ʾ��ʾ�Ի���
					break;
			case HAZNOT_NEW_SOFTWARE:
					if(!strInfo.equals(""))
						CustomToast.makeTextShort(mContext, strInfo);
				break;	
			}
		};
	};


	public UpdateManager(Context context,String strInfo) {
		super();
		this.mContext = context;
		this.strInfo=strInfo;
	}
	
	
	/**
	 * ����������
	 */
	public void checkUpdate() {
		Thread checkUpdateRunnable=new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				checkNewSoftware();
			}		
		});
		checkUpdateRunnable.start();
	}
	
	private void showNoticeDialog() {
		// TODO Auto-generated method stub
		//����Ի���
		new AlertDialog.Builder(mContext).
		setTitle(mContext.getResources().getString(R.string.soft_update_title)).
		setMessage(mContext.getResources().getString(R.string.soft_update_info)).
		setPositiveButton(mContext.getResources().getString(R.string.soft_update_updatebtn), new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// ��ʾ���ضԻ���
				showDownloadDialog();
				dialog.dismiss();
			}

		}).
        setNegativeButton(mContext.getResources().getString(R.string.soft_update_later), new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		}).show();
		/*AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
				// TODO Auto-generated method stub
				
		//����
		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// ��ʾ���ضԻ���
				showDownloadDialog();
			}
		});
		// �Ժ����
		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();*/
	}
	private void showDownloadDialog() {
		// ����������ضԻ���
		/*AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.software_update_progress, null);
		mProgressBar = (ProgressBar) view.findViewById(R.id.update_progress);
		builder.setView(view);
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// ����ȡ��״̬
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		*/
		mMyProgressbar = new CustomProgressbar(mContext);
		mMyProgressbar.setTitle(mContext.getString(R.string.soft_updating));
		mMyProgressbar.setNegativeButton(mContext.getString(R.string.soft_update_cancel), new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMyProgressbar.dismiss();
				// ����ȡ��״̬
				cancelUpdate = true;
			}
		});

		//�����ļ�
		downloadApk();
	}
	public void dismissDialog(){
		mMyProgressbar.dismiss();
	}
	
	/**
	 * ����APK�ļ�
	 */
	private void downloadApk() {
		// TODO Auto-generated method stub
		// �������߳��������
		new DownloadApkThread().start();
	}


	/**
	 * �������Ƿ��и��°汾
	 * @return
	 */
	public void checkNewSoftware() {
		// ��ȡ��ǰ����汾
		int versionCode = getVersionCode(mContext);
		try{
				// ��version.xml�ŵ������ϣ�Ȼ���ȡ�ļ���Ϣ
				URL url = null;//new URL(mContext.getString(R.string.versionXml));
			   //1.ֱ��ʹ��URL��ȡ������
			   //InputStream inputStream = url.openStream();
			   //2.���URLConnection
			   //URLConnection mURLConnection = url.openConnection();
			   //InputStream inputStream = mURLConnection.getInputStream();
			   //3.�����httpЭ�����ʹ��HttpURLConnection
			   HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
		       /*mHttpURLConnection.setDoInput(true);
		       mHttpURLConnection.connect();*/
			   /*mHttpURLConnection.setReadTimeout(5 * 1000);
			   mHttpURLConnection.setRequestMethod("GET");// ����Ҫ��д
			   mHttpURLConnection.connect();*/
			   //InputStream inputStream = ParseXml.class.getClassLoader().getResourceAsStream("version.xml");
			   InputStream inputStream = mHttpURLConnection.getInputStream(); 
			   //����XML�ļ��� ����XML�ļ��Ƚ�С�����ʹ��DOM��ʽ���н���
			   ParseXml service = new ParseXml();
			   mHashMap = service.parseXml(inputStream);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(null != mHashMap) {
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
			//�汾�ж�
			if(serviceCode > versionCode) {
				mHandler.sendEmptyMessage(HAZ_NEW_SOFTWARE);
			}
			else mHandler.sendEmptyMessage(HAZNOT_NEW_SOFTWARE);
		}
		
	}

	/**
	 * ��ȡ����汾��
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		// TODO Auto-generated method stub
		int versionCode = 0;

		// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
		try {
			versionCode = context.getPackageManager().getPackageInfo("com.rcwind.quadcopter", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}
	
	/**
	 * �����ļ��߳�
	 * @author Administrator
	 *
	 */
	private class DownloadApkThread extends Thread {
		@Override
		public void run() {
			try
			{
				//�ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// ��ȡSDCard��·��
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// ����ļ������ڣ��½�Ŀ¼
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do
					{
						int numread = is.read(buf);
						count += numread;
						// �����������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);//���ȡ����ֹͣ����
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			mMyProgressbar.dismiss();
		}
	}
	
	/**
	 * ��װAPK�ļ�
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists())
		{
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
