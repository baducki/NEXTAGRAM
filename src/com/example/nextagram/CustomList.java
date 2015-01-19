package com.example.nextagram;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CustomList extends ActionBarActivity implements OnClickListener {
	
	private Button mButtonViewArticle;
	private Button mButtonWriteArticle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButtonViewArticle = (Button) findViewById(R.id.custom_refresh_button);
		mButtonWriteArticle = (Button) findViewById(R.id.custom_write_button);
		
		mButtonViewArticle.setOnClickListener(this);
		mButtonWriteArticle.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.custom_refresh_button:
			break;
		case R.id.custom_write_button:
			break;
		}
	}

}
