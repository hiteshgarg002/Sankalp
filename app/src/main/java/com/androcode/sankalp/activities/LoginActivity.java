package com.androcode.sankalp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androcode.sankalp.R;
import com.androcode.sankalp.networks.CallHttpRequest;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {
    private TextView signup;
    private Context ctx;
    private Intent intent;
    private AutoCompleteTextView userId;
    private EditText password;
    private Button login;
    private Activity activity;
    private boolean permissionGranted;
    private SharedPreferences pos;
    private SharedPreferences.Editor editor;
    private static final int requestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = this;
        activity = this;

        pos = ctx.getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = pos.edit();
        userId = (AutoCompleteTextView) findViewById(R.id.mobno_login);
        password = (EditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.email_sign_in_button);

        signup = (TextView) findViewById(R.id.signupTag);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ctx, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCredentialNull(userId.getText().toString()) || isCredentialNull(password.getText().toString())) {
                    Snackbar.make(view, "Field(s) cannot be empty!", Snackbar.LENGTH_LONG).show();
                } else {
                    if (pos.getBoolean("perCheck", false) != true) {
                        requestPermission();
                    } else {
                        if (isConnected()) {
                            intent = new Intent(ctx, LoginLoadingActivity.class);
                            intent.putExtra("userid", userId.getText().toString());
                            intent.putExtra("pwd", password.getText().toString());
                            startActivity(intent);
                            finish();
                        }else{
                            Snackbar.make(view,"No Connection!",Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        requestPermission();
    }

    private boolean isCredentialNull(String string) {
        if (string.equals("")) {
            return true;
        }
        return false;
    }

    private void requestPermission() {
        if (pos.getBoolean("perCheck", false) != true) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                    {
                            ACCESS_COARSE_LOCATION,
                            ACCESS_FINE_LOCATION,
                            CAMERA,
                            INTERNET,
                            READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE,
                            CALL_PHONE
                    }, requestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case requestPermissionCode:
                if (grantResults.length > 0) {

                    boolean COARSE_LOCATION = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean FINE_LOCATION = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean CAMERA = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean INTERNET = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean READ_EXTERNAL = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean CALL_PHONE=grantResults[6]==PackageManager.PERMISSION_GRANTED;

                    if (COARSE_LOCATION && FINE_LOCATION && CAMERA && INTERNET
                            && READ_EXTERNAL && WRITE_EXTERNAL && CALL_PHONE) {
                        editor.putBoolean("perCheck", true);
                        editor.commit();
                        // Snackbar.make(findViewById(R.id.login_page), "Permissions Granted!", Snackbar.LENGTH_LONG).show();
                    } else {
                        //Snackbar.make(findViewById(R.id.login_page), "Permissions Denied!", Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
        }
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
