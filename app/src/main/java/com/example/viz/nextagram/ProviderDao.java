package com.example.viz.nextagram;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProviderDao {
    private Context context;
    private SQLiteDatabase database;
    private SharedPreferences pref;
    private String serverIP;

    public ProviderDao(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(context.getString(R.string.pref_name), context.MODE_PRIVATE);
        serverIP = pref.getString(context.getString(R.string.server_ip), "");
    }

    public void insertJsonData(JSONArray jsonArray) {

        int articleNumber = 0;
        String title;
        String writerName;
        String writerId;
        String content;
        String writeDate;
        String imgName;

        try {

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jObj = jsonArray.getJSONObject(i);

                articleNumber = jObj.getInt("ArticleNumber");
                title = jObj.getString("Title");
                writerName = jObj.getString("Writer");
                writerId = jObj.getString("Id");
                content = jObj.getString("Content");
                writeDate = jObj.getString("WriteDate");
                imgName = jObj.getString("ImgName");

                ContentValues values = new ContentValues();

                values.put("_id", articleNumber);
                values.put("Title", title);
                values.put("WriterName", writerName);
                values.put("WriterID", writerId);
                values.put("Content", content);
                values.put("WriteDate", writeDate);
                values.put("ImgName", imgName);

                FileDownloader.downFile(context, serverIP + "/image/" + imgName, imgName);

                context.getContentResolver().insert(NextagramContract.Article.CONTENT_URI, values);
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(context.getString(R.string.last_update_article_number_key), articleNumber);
            editor.apply();

        } catch (JSONException e) {
            Log.e("test", "JSON ERROR! - " + e);
            e.printStackTrace();
        }
    }

    public ArrayList<ArticleDTO> getArticleList() {

        ArrayList<ArticleDTO> articleList = new ArrayList<ArticleDTO>();

        int articleNumber;
        String title;
        String writerName;
        String writerId;
        String content;
        String writeDate;
        String imgName;

        Cursor cursor = context.getContentResolver().query(
                NextagramContract.Article.CONTENT_URI,
                NextagramContract.Article.PROJECTION_ALL, null, null,
                NextagramContract.Article.SORT_ORDER_DEFAULT
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                articleNumber = cursor.getInt(0);
                title = cursor.getString(1);
                writerName = cursor.getString(2);
                writerId = cursor.getString(3);
                content = cursor.getString(4);
                writeDate = cursor.getString(5);
                imgName = cursor.getString(6);

                ArticleDTO article = new ArticleDTO(articleNumber, title, writerName, writerId, content, writeDate, imgName);
                articleList.add(article);
            }

            cursor.close();
        }

        return articleList;
    }

    public ArticleDTO getArticleByArticleNumber(int articleNumber) {

        ArticleDTO article = null;

        String title;
        String writerName;
        String writerId;
        String content;
        String writeDate;
        String imgName;

        String where = "_id = " + articleNumber;

        Cursor cursor = context.getContentResolver().query(NextagramContract.Article.CONTENT_URI,
                NextagramContract.Article.PROJECTION_ALL, where, null,
                NextagramContract.Article.SORT_ORDER_DEFAULT);

        if (cursor != null) {
            cursor.moveToFirst();

            articleNumber = cursor.getInt(0);
            title = cursor.getString(1);
            writerName = cursor.getString(2);
            writerId = cursor.getString(3);
            content = cursor.getString(4);
            writeDate = cursor.getString(5);
            imgName = cursor.getString(6);

            article = new ArticleDTO(articleNumber, title, writerName, writerId, content, writeDate, imgName);

            cursor.close();
        }

        return article;
    }
}
