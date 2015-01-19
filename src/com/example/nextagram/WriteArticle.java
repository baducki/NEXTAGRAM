package com.example.nextagram;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class WriteArticle extends ActionBarActivity implements OnClickListener {
	
	private EditText etWriter;
	private EditText etTitle;
	private EditText etContent;
	private ImageButton ibPhoto;
	private Button bnUpload;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_writer);
		etWriter = (EditText)findViewById(R.id.write_article_editText_writer);
		etTitle = (EditText)findViewById(R.id.write_article_editText_title);
		etContent = (EditText)findViewById(R.id.write_article_editText_content);
		
		ibPhoto = (ImageButton)findViewById(R.id.write_article_imageButton_photo);
		ibPhoto.setOnClickListener(this);
		
		bnUpload = (Button)findViewById(R.id.write_article_button1);
		bnUpload.setOnClickListener(this);
	}
	
	private static final int REQUEST_PHOTO_ALBUM = 1;
	
	private String filePath;
	private String fileName;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if(requestCode == REQUEST_PHOTO_ALBUM) {
				Uri uri = getRealPathUri(data.getData());
				filePath = uri.toString();
				fileName = uri.getLastPathSegment();
				
				Bitmap bitmap = BitmapFactory.decodeFile(filePath);
				ibPhoto.setImageBitmap(bitmap);
			}
		} catch(Exception e) {
			Log.e("test", "onActivityResult ERROR:" + e);
		}
	}
	
	private Uri getRealPathUri(Uri uri) {
		Uri filePathUri = uri;
		if(uri.getScheme().toString().compareTo("content") == 0) {
			Cursor cursor = getApplicationContext().getContentResolver().query(uri,  null,  null, null, null);
			if(cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				filePathUri = Uri.parse(cursor.getString(column_index));
			}
		}
		return filePathUri;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.write_article_imageButton_photo:
			
			Intent intent = new Intent(Intent.ACTION_PICK);
			
			intent.setType(Images.Media.CONTENT_TYPE);
			intent.setType(Images.Media.CONTENT_TYPE);
			startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
			
			break;
			
		case R.id.write_article_button1:
			final Handler handler = new Handler();
			
			progressDialog = ProgressDialog.show(WriteArticle.this, "", "업로드 중입니다.");
			
			String ID = Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
			String DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(new Date());
			
			Article article = new Article(0,etTitle.getText().toString(),etWriter.getText().toString(),ID,etContent.getText().toString(),DATE,fileName);

			ProxyUP.uploadArticle(article, filePath,
					new AsyncHttpResponseHandler() {
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							Log.e("test", "up onFailure:" + statusCode);
							progressDialog.cancel();
							Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();
							finish();
						}
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							Log.i("test", "up onSuccess:" + statusCode);
							progressDialog.cancel();
							Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_SHORT).show();
							finish();
						} 
					});
			
			break;
		}
	}
}
