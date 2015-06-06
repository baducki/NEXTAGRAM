package com.example.viz.nextagram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class WritingArticleView extends Activity implements View.OnClickListener {

    private static final int REQUEST_PHOTO_ALBUM = 1;
    private EditText etWriter;
    private EditText etTitle;
    private EditText etContent;
    private ImageButton ibPhoto;
    private ProgressDialog progressDialog;
    private String filePath;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        etWriter = (EditText) findViewById(R.id.write_writer);
        etTitle = (EditText) findViewById(R.id.write_title);
        etContent = (EditText) findViewById(R.id.write_content);
        ibPhoto = (ImageButton) findViewById(R.id.write_image_button);
        Button buUpload = (Button) findViewById(R.id.write_upload_button);

        ibPhoto.setOnClickListener(this);
        buUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_image_button:
                Log.e("button clicked", "ok");

                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PHOTO_ALBUM);

                break;

            case R.id.write_upload_button:

                progressDialog = ProgressDialog.show(WritingArticleView.this, "", "업로드중입니다...");

                String ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                String DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(new Date());

                ArticleDTO article = new ArticleDTO(
                        2,
                        etTitle.getText().toString(),
                        etWriter.getText().toString(),
                        ID,
                        etContent.getText().toString(),
                        DATE,
                        fileName);

                WritingArticleProxy writingArticleProxy = new WritingArticleProxy(this);
                writingArticleProxy.uploadArticle(article, filePath,
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                Log.e("uploadArticle", "success: " + i);
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                Log.e("uploadArticle", "fail: " + i);
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();
                            }
                        });

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == REQUEST_PHOTO_ALBUM) {
                Uri uri = getRealPathUri(data.getData());
                filePath = uri.toString();
                fileName = uri.getLastPathSegment();

                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                Log.e("image original path: ", filePath);
                ibPhoto.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            Log.e("getImageFromLocal", "fail: " + e);
        }
    }

    private Uri getRealPathUri(Uri uri) {
        Log.e("content provider uri", uri.toString());
        Uri filePathUri = uri;
        if (uri.getScheme().compareTo("content") == 0) {
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePathUri = Uri.parse(cursor.getString(column_index));
            }
            cursor.close();
        }
        return filePathUri;
    }
}
