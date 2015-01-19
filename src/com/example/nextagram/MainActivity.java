package com.example.nextagram;

import java.util.ArrayList;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements OnClickListener, OnItemClickListener {

	private ListView mainListView1;
	private ArrayList<Article> articleList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button buWrite = (Button) findViewById(R.id.custom_write_button);
		Button buRefresh = (Button) findViewById(R.id.custom_refresh_button);
		
		buWrite.setOnClickListener(this);
		buRefresh.setOnClickListener(this);
		
		mainListView1 = (ListView)findViewById(R.id.main_listView1);
		
		refreshData();

	}
	
	private void listView() {
		Dao dao = new Dao( getApplicationContext() );
		articleList = dao.getArticleList();
		
		CustomAdapter customAdapter = new CustomAdapter(this, R.layout.custom_list_row, articleList);
		mainListView1.setAdapter(customAdapter);
		mainListView1.setOnItemClickListener(this);
	}
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	private void refreshData() {
		client.get("http://10.73.39.188:5009/loadData", new AsyncHttpResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				String jsonData = new String(response);
				Log.i("test", "jsonData:" + jsonData);
				
				Dao dao = new Dao(getApplicationContext());
				dao.insertJsonData(jsonData);
				
				listView();
			}

		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Intent intent = new Intent (this, ArticleViewer.class);
		
		intent.putExtra("ArticleNumber",  articleList.get(position).getArticleNumber() + "");
		
		startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.custom_write_button:
			Intent intent = new Intent(this, WriteArticle.class);
			startActivity(intent);
			break;
		case R.id.custom_refresh_button:
			refreshData();
			break;
		}
	}

}
