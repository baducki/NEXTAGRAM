package com.example.nextagram;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class Proxy {
	
	String server_URL = "http://10.73.39.188/";
	public String getJSON(){
		
		try{
			URL url = new URL(server_URL + "loadData");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setConnectTimeout(10*1000);
			conn.setReadTimeout(10*1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			
			conn.setRequestProperty("Cache-Control","no-cache");
			conn.setRequestProperty("Accept", "application/json");
			
			conn.setDoInput(true);
			
			conn.connect();
			Log.i("test","loadData test");
			int status = conn.getResponseCode();
			Log.i("test","ProxyResponseCode: " + status);
			
			switch(status){
			
			case 200:
			case 201:
				
				BufferedReader br = new BufferedReader(new InputStreamReader( conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				
				while( (line = br.readLine()) != null){
					sb.append(line + "\n");
				}
				br.close();
				
				return sb.toString();
			}
			
		} catch(Exception e){
			e.printStackTrace();
			Log.i("test","NETWORK ERROR: " +e);
		}
		return null;  // null로처리하면 데이터를 못받으면 죽게됨.
	}
	
	public String getCommentJson(){
		
		try{
			
			URL url = new URL(server_URL + "loadComment");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setConnectTimeout(10*1000);
			conn.setReadTimeout(10*1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			
			conn.setRequestProperty("Cache-Control","no-cache");
			conn.setRequestProperty("Accept", "application/json");
			
			conn.setDoInput(true);
			
			conn.connect();
			Log.i("test",""+conn.getResponseCode());
			int status = conn.getResponseCode();
			Log.i("test","ProxyResponseCode: " + status);
			
			switch(status){
			
			case 200:
			case 201:
				
				BufferedReader br = new BufferedReader(new InputStreamReader( conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				
				while( (line = br.readLine()) != null){
					sb.append(line + "\n");
				}
				br.close();

				return sb.toString();
			}
			
		} catch(Exception e){
			e.printStackTrace();
			Log.i("test","NETWORK ERROR: " +e);
		}
		return null;  // null로처리하면 데이터를 못받으면 죽게됨.
		
	}
}