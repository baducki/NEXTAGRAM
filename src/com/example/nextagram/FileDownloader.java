package com.example.nextagram;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.util.Log;

public class FileDownloader {
	private final Context context;
	
	public FileDownloader(Context context) {
		this.context = context;
	}

	public void downFile(String fileUrl, String fileName) {
		File filePath = new File(context.getFilesDir().getPath() + "/" + fileName);
		
		if (!filePath.exists()) {
			try {
				URL url = new URL(fileUrl);
				
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				
				conn.setConnectTimeout(10 * 1000);
				conn.setReadTimeout(10 * 1000);
				
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Accep-Charser", "UTF-8");
				
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setRequestProperty("Accept", "*/*");
				
				conn.setDoInput(true);
				
				conn.connect();
				
				int status = conn.getResponseCode();
				Log.i("test", "ProxyResponseCode:" + status);
				
				switch (status) {
				case 200:
				case 201:
					InputStream is = conn.getInputStream();
					
					BufferedInputStream bis = new BufferedInputStream(is);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					
					int current = 0;
					
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}
					
					FileOutputStream fos = context.openFileOutput(fileName, 0);
					fos.write(baf.toByteArray());
					fos.close();
					
					bis.close();
					is.close();
				}
				
			} catch (Exception e) {
				Log.e("test", " File download Error:" + e);
			}
			
		}
	}
}
