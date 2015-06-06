package com.example.viz.nextagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;

import java.io.IOException;


public class HomeView extends Activity implements OnClickListener {

    private Button button1;
    private Button button2;
    private SharedPreferences pref;
    private final static String TAG = HomeView.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String PROPERTY_REG_ID = "registration_id";

    private String SENDER_ID = "567041356777";
    private GoogleCloudMessaging gcm;
    private String regid;
    Context context;

    private static SyncHttpClient client = new SyncHttpClient();

    private void sendRegistrationToServer(String regid) {
        RequestParams params = new RequestParams();
        params.put("reg_id", regid);
        client.post("http://192.168.0.3:5009/gcm", params, new JsonHttpResponseHandler() {

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.server_ip), getString(R.string.server_ip_value));
        editor.apply();

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        Intent intentSync = new Intent(this, SyncDataService.class);
        startService(intentSync);

        listView();

        context = getApplicationContext();

        if (checkGooglePlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

            regid = getRegistrationId(context);

            // if (regid.isEmpty()) {
                registerInBackground();
            // }
        }
    }

    private boolean checkGooglePlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "이 장비는 Google Play Service를 지원하지 않습니다.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        String registrationId = pref.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        Log.i(TAG, "Registration ID = " + registrationId);
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    Log.i(TAG, "Registration ID=" + regid);

                    sendRegistrationToServer(regid);
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    Log.e(TAG, "Error :" + ex.getMessage());
                }
                return null;
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regid) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PROPERTY_REG_ID, regid);
        editor.commit();
    }

    public void listView() {
        ListView listView = (ListView) findViewById(R.id.customlist_listview);

        Cursor cursor = getContentResolver().query(
                NextagramContract.Article.CONTENT_URI,
                NextagramContract.Article.PROJECTION_ALL, null, null,
                NextagramContract.Article.SORT_ORDER_DEFAULT
        );

        HomeViewAdapter homeViewAdapter = new HomeViewAdapter(this, cursor, R.layout.custom_list_row);

        listView.setAdapter(homeViewAdapter);
        listView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.button1:
                Intent intentWrite = new Intent(arg0.getContext(), WritingArticleView.class);
                startActivity(intentWrite);
                break;
            case R.id.button2:
                try {
                    new ProviderDao(this).insertJsonData(new JSONArray(new Proxy(this).getJSON()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(".ArticleView");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // TODO: 뭘까?
            intent.putExtra("ArticleNumber",
                    ((HomeViewAdapter.ViewHolderItem) view.getTag()).articleNumber);

            startActivity(intent);
        }
    };

}