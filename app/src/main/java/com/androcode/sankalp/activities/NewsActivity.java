package com.androcode.sankalp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.androcode.sankalp.BroadCastReceiver.NotificationBroadCastReceiver;
import com.androcode.sankalp.R;
import com.androcode.sankalp.helpers.BottomNavViewExHelper;
import com.androcode.sankalp.networks.VolleyConnect;
import com.androcode.sankalp.services.NotificationService;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.Calendar;

public class NewsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context ctx;
    private String URL, date, time, mobno, name;
    private static final int requestPermissionCode = 1;
    private BottomNavigationViewEx viewEx;
    private Menu menu;
    private Activity activity;
    private PullRefreshLayout refreshLayout;
    private MenuItem menuItem;
    private Intent intent;
    private int flag;
    private LocationSettingsRequest.Builder locationSettingsRequest;
    private GoogleApiClient mGoogleApiClient;
    private PendingResult<LocationSettingsResult> pendingResult;
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private Calendar currentDateTime;
    private SharedPreferences pos;
    private static final int REQUEST_LOCATION = 001;
    private static final int ACTIVITY_NUMBER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ctx = this;
        activity = this;
        flag = getIntent().getFlags();

        pos = ctx.getSharedPreferences("userinfo", MODE_PRIVATE);
        mobno = pos.getString("mobileno", "");
        name = pos.getString("name", "");
        requestQueue = VolleyConnect.getInstance().getRequestQueue();
        refreshLayout = (PullRefreshLayout) findViewById(R.id.refresh_news);
        setupBottomNavView();
        if (isConnected()) {
            startService();
        }

        if (flag == Intent.FLAG_ACTIVITY_NEW_TASK) {
            //Toast.makeText(ctx,"YO",Toast.LENGTH_LONG).show();
            buildGoogleApiClient();
            mLocationSetting();
            gpsPrompt();
        }
    }

    private void setupBottomNavView() {
        viewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_view_ex);
        BottomNavViewExHelper.setupBottomNavView(viewEx);
        BottomNavViewExHelper.enableBottomNavView(ctx, viewEx, activity);
        menu = viewEx.getMenu();
        menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void startService() {
        intent = new Intent(ctx, NotificationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
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

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(NewsActivity.this)
                .addOnConnectionFailedListener(NewsActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void gpsPrompt() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // Toast.makeText(ctx,"Success",Toast.LENGTH_LONG).show();
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(NewsActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void mLocationSetting() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationSetting();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            sendLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void sendLocation(double latitude, double longitude) {
        currentDateTime = Calendar.getInstance();
        time = currentDateTime.get(Calendar.HOUR) + ":" + currentDateTime.get(Calendar.MINUTE) + ":" + currentDateTime.get(Calendar.SECOND);
        date = currentDateTime.get(Calendar.YEAR) + "-" + (currentDateTime.get(Calendar.MONTH) + 1) + "-" + currentDateTime.get(Calendar.DATE);
        URL = "http://192.168.1.10/storelocation.php?mobileno=" + mobno + "&time=" + time + "&date=" + date + "&latitude=" + latitude + "&longitude=" + longitude + "&name=" + name;
        stringRequest = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(ctx,response+"",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  Toast.makeText(ctx,error+"",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}
