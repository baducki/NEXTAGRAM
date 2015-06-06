package com.example.viz.nextagram;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class SyncDataService extends Service {

    private static final String TAG = SyncDataService.class.getSimpleName();

    private Timer mTimer;
    private TimerTask mTask;
    private Proxy proxy;
    private ProviderDao dao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        proxy = new Proxy(getApplicationContext());
        dao = new ProviderDao(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        mTask = new TimerTask() {
            @Override
            public void run() {

                if (isOnline()) {

                    String jsonData = proxy.getJSON();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray == null || jsonArray.length() == 0) {
                        Log.i(TAG, "nothing to update!");
                    } else {
                        dao.insertJsonData(jsonArray);
                    }
                } else {
                    Log.e("Network State", "not connected to internet");
                }
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 1000 * 3);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private boolean isOnline() {
        try {
            ConnectivityManager conMan = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }
}