package com.example.viz.nextagram;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;


public class ArticleView extends Activity {

    private ImageView ivImage;
    private WeakReference<ImageView> imageViewReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        TextView tvTitle = (TextView) findViewById(R.id.viewer_title);
        TextView tvWriter = (TextView) findViewById(R.id.viewer_writer);
        TextView tvContent = (TextView) findViewById(R.id.viewer_content);
        TextView tvWriteTime = (TextView) findViewById(R.id.viewer_write_time);

        ivImage = (ImageView) findViewById(R.id.viewer_image_view);
        imageViewReference = new WeakReference<ImageView>(ivImage);

        int articleNumber = getIntent().getExtras().getInt("ArticleNumber");

        ProviderDao dao = new ProviderDao(getApplicationContext());
        ArticleDTO article = dao.getArticleByArticleNumber(articleNumber);

        tvTitle.setText(article.getTitle());
        tvWriter.setText(article.getWriterName());
        tvContent.setText(article.getContent());
        tvWriteTime.setText(article.getWriteDate());

        try {

            String imgPath = getFilesDir().getPath() + "/" + article.getImgName();

            Bitmap bitmap = ImageLoader.getInstance().get(imgPath);
            if (bitmap != null) {
                Log.e("result", "getCache");
                imageViewReference.get().setImageBitmap(bitmap);
            } else {
                Log.e("result", "putCache");
                File imgLoadPath = new File(imgPath);

                if (imgLoadPath.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    options.inPurgeable = true;
                    bitmap = BitmapFactory.decodeFile(imgPath, options);
                    ImageLoader.getInstance().put(imgPath, bitmap);

                    imageViewReference.get().setImageBitmap(bitmap);
                } else {
                    Log.e("viewImage", "fail: file not found");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
