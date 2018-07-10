package com.androcode.sankalp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.androcode.sankalp.R;
import com.androcode.sankalp.applicationcontext.ApplicationContextReference;
import com.androcode.sankalp.networks.VolleyConnect;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hp-u on 11/30/2017.
 */

public class NotificationService extends Service {
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private Handler handler;
    private Runnable runnable;
    private JsonArrayRequest jsonArrayRequest;
    private SharedPreferences pos, preferences;
    private SharedPreferences.Editor editor;
    private String URL;
    private Context ctx;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;


    final class ServiceThread implements Runnable {
        private int startId;

        public ServiceThread(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {
            synchronized (this) {
                runnable = new Runnable() {
                    public void run() {
                        if (isConnected()) {
                            setNotifications();
                        }
                        handler.postDelayed(runnable, 1000);
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();

        handler = new Handler(Looper.getMainLooper());
        requestQueue = VolleyConnect.getInstance().getRequestQueue();
        pos = ApplicationContextReference.getAppContext().getSharedPreferences("notification", MODE_MULTI_PROCESS);
        preferences = ApplicationContextReference.getAppContext().getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = pos.edit();
        URL = "http://192.168.1.10/notification.php";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new ServiceThread(startId));
        thread.start();
        return START_STICKY;
    }

    private void setNotifications() {
        //Toast.makeText(getApplicationContext(), "Running", Toast.LENGTH_SHORT).show();
        jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);
                    if (jsonObject.optInt("id") > 0) {
                        if (pos.getInt("notificationId", 0) < jsonObject.optInt("id") && jsonObject.optInt("id") != pos.getInt("notificationId", 0)) {
                            editor.clear();
                            editor.commit();
                            editor.putInt("notificationId", jsonObject.optInt("id")).apply();
                            editor.commit();
                            if (!(jsonObject.optString("mobileno")
                                    .equals(preferences.getString("mobileno", "")))) {
                                getNotification(jsonObject.optString("name") + "");
                            }
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  Toast.makeText(getApplicationContext(), error + "", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void getNotification(String name) {
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.sankalp_notification)
                .setContentTitle("Help needed!")
                .setContentText(name + " need your help!")
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(false);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
