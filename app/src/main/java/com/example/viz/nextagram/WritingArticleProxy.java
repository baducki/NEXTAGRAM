package com.example.viz.nextagram;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

public class WritingArticleProxy {

    private AsyncHttpClient client = new AsyncHttpClient();
    private SharedPreferences pref;
    private String serverIP;

    public WritingArticleProxy(Context context) {
        pref = context.getSharedPreferences(context.getString(R.string.pref_name), context.MODE_PRIVATE);
        serverIP = pref.getString(context.getString(R.string.server_ip), "");
    }

    public void uploadArticle(ArticleDTO article, String filePath, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("Title", article.getTitle());
        params.put("Writer", article.getWriterName());
        params.put("Id", article.getWriterId());
        params.put("Content", article.getContent());
        params.put("WriteDate", article.getWriteDate());
        params.put("ImgName", article.getImgName());

        try {
            params.put("uploadedfile", new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(serverIP + "/upload", params, responseHandler);
    }
}
