package com.androcode.sankalp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androcode.sankalp.R;
import com.androcode.sankalp.helpers.BottomNavViewExHelper;
import com.androcode.sankalp.models.User;
import com.androcode.sankalp.networks.VolleyConnect;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsSosActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private Context ctx;
    private String URL,name,mobno;
    private List<User> list;
    private BottomNavigationViewEx viewEx;
    private Menu menu;
    private Activity activity;
    private FloatingActionButton sos, myLoc;
    private MenuItem menuItem;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest.Builder locationSettingsRequest;
    private PendingResult<LocationSettingsResult> pendingResult;
    private double myLatitude, myLongitude;
    private RequestQueue requestQueue = null;
    private JsonArrayRequest jsonArrayRequest;
    private StringRequest stringRequest;
    private MarkerOptions markerOptions;
    private SharedPreferences pos;
    private static final int REQUEST_LOCATION = 001;
    private static final int ACTIVITY_NUMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_sos);
        ctx = this;
        activity = this;

        pos=ctx.getSharedPreferences("userinfo",MODE_PRIVATE);
        name=pos.getString("name","");
        mobno=pos.getString("mobileno","");
        name=pos.getString("name","");
        requestQueue = VolleyConnect.getInstance().getRequestQueue();
        list = new ArrayList<User>();

        sos = (FloatingActionButton) findViewById(R.id.sos_button);
        myLoc = (FloatingActionButton) findViewById(R.id.my_location_button);

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSOS(myLatitude,myLongitude);
            }
        });

        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllUsersLocation();
                if (isLocationServicesEnabled()) {
                    if (markerOptions == null && myLatitude != 0 && myLongitude != 0) {
                        markerOptions = new MarkerOptions().position(new LatLng(myLatitude, myLongitude)).title("My Current Location");
                        mMap.addMarker(markerOptions);
                        sos.setVisibility(View.VISIBLE);
                    }
                    if (myLatitude != 0 && myLongitude != 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude, myLongitude), 15.0f));
                    }
                } else {
                    gpsPrompt();
                }
            }
        });

        setupBottomNavView();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        mLocationSetting();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(myLatitude,myLongitude);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
        // if(isLocationServicesEnabled()){
        //    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude,myLongitude), 15.0f));
        //    mMap.addMarker(new MarkerOptions().position(new LatLng(myLatitude,myLongitude)).title("My Current Location"));
        //  }else{
        gpsPrompt();
        //  }
    }

    private void setupBottomNavView() {
        viewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_view_ex);
        BottomNavViewExHelper.setupBottomNavView(viewEx);
        BottomNavViewExHelper.enableBottomNavView(ctx, viewEx, activity);
        menu = viewEx.getMenu();
        menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationSetting();

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(MapsSosActivity.this)
                .addOnConnectionFailedListener(MapsSosActivity.this)
                .addApi(LocationServices.API)
                .build();
    }

    private void mLocationSetting() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
    }

    private boolean isLocationServicesEnabled() {
        LocationManager lm = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        return false;
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
                            status.startResolutionForResult(MapsSosActivity.this, REQUEST_LOCATION);
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

    private void getAllUsersLocation() {
        URL = "http://192.168.1.10/tracking.php";
        jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                list.clear();
                // Toast.makeText(ctx,response.toString(),Toast.LENGTH_SHORT).show();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);
                    User user = new User();
                    user.setUserMobileNo(jsonObject.optString("mobileno"));
                    user.setUserEmailId(jsonObject.optString("emailid"));
                    user.setUserAge(jsonObject.optString("age"));
                    user.setUserBloodGroup(jsonObject.optString("bloodgroup"));
                    user.setUserName(jsonObject.optString("name"));
                    user.setUserLatitude(jsonObject.optString("latitude"));
                    user.setUserLongitude(jsonObject.optString("longitude"));
                    user.setLocationDate(jsonObject.optString("date"));
                    user.setLocationTime(jsonObject.optString("time"));
                    //Toast.makeText(ctx,user.getUserLatitude(),Toast.LENGTH_SHORT).show();
                    markerOptions = new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(user.getUserLatitude()), Double.parseDouble(user.getUserLongitude())))
                            .title("Test");
                    mMap.addMarker(markerOptions);
                    list.add(user);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, error.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void sendSOS(double lat, double lng) {
        try {
            Geocoder GC = new Geocoder(ctx, Locale.getDefault());
            List<Address> A = GC.getFromLocation(lat, lng, 1);

            StringBuffer sb = new StringBuffer();
            if (A.size() > 0) {
                Address add = A.get(0);
                for (int i = 0; i <= add.getMaxAddressLineIndex(); i++) {
                    sb.append(add.getAddressLine(i) + " ");
                }
                String address = sb.toString().replace(" ", "+");
                Calendar C = Calendar.getInstance();
                String cd = C.get(Calendar.YEAR) + "-" + (C.get(Calendar.MONTH) + 1) + "-" + C.get(Calendar.DATE);
                String ct = C.get(Calendar.HOUR) + ":" + C.get(Calendar.MINUTE) + ":" + C.get(Calendar.SECOND);

                sendNotification(mobno,name,ct,cd,address);
            }
        } catch (Exception e) {
            Toast.makeText(ctx, e + "", Toast.LENGTH_LONG).show();
        }
    }

    private void sendNotification(String mobno,String name,String time,String date,String location){
        URL="http://192.168.1.10/sos.php?mobileno="+mobno+"&name="+name+"&location="+location+"&date="+date+"&time="+time;

        stringRequest=new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success")){
                    Toast.makeText(ctx,"SOS Sent!",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx,"Error while sending SOS!",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(stringRequest);
    }
}
