package com.androcode.sankalp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.androcode.sankalp.R;
import com.androcode.sankalp.networks.CallHttpRequest;

public class LoginLoadingActivity extends AppCompatActivity {
    private Intent intent;
    private Context ctx;
    private String user,pwd;
    private Handler H;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_loading);
        ctx=this;
        activity=this;

        intent=getIntent();
        user=intent.getStringExtra("userid");
        pwd=intent.getStringExtra("pwd");
        H=new Handler();
        doLogin();
    }

    private void doLogin(){
        H.postDelayed(new Runnable() {
            @Override
            public void run() {
                CallHttpRequest request=new CallHttpRequest(ctx,"Login",activity);
                String url[]={"http://192.168.1.10/login.php?user=" + user + "&password=" + pwd};
                request.execute(url);
            }
        },3000);
    }
}
