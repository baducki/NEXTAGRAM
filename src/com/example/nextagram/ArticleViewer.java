package com.example.nextagram;

import java.io.IOException;
import java.io.InputStream;

import android.support.v7.app.ActionBarActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleViewer extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_article);
	
		TextView tvTitle = (TextView)findViewById(R.id.view_article_textview_title);
		TextView tvWriter = (TextView)findViewById(R.id.view_article_textview_writer);
		TextView tvContent = (TextView)findViewById(R.id.view_article_textview_content);
		TextView tvWriteDate = (TextView)findViewById(R.id.view_article_textview_write_time);
		
		ImageView ivImage = (ImageView)findViewById(R.id.view_article_imageview_photo);
		
		String articleNumber = getIntent().getExtras().getString("ArticleNumber");
		
		Dao dao = new Dao( getApplicationContext() );
		
		Article article = dao.getArticleByArticleNumber( Integer.parseInt(articleNumber) );
		
		tvTitle.setText(article.getTitle());
		tvWriter.setText(article.getWriter());
		tvContent.setText(article.getContent());
		tvWriteDate.setText(article.getWriteDate());
		
		try {
			InputStream ims = getApplicationContext().getAssets().open(article.getImgName());
			Drawable d = Drawable.createFromStream(ims, null);
			ivImage.setImageDrawable(d);
		}
		catch(IOException e) {
			Log.e("ERROR", "ERROR:"  + e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_article, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.view_article) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
