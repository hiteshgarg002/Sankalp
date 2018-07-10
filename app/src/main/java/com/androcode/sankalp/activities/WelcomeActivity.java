package com.androcode.sankalp.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androcode.sankalp.R;
import com.androcode.sankalp.networks.CallHttpRequest;

public class WelcomeActivity extends AppCompatActivity {
    private SharedPreferences pos;
    private Context ctx;
    private Handler H;
    private Intent intent;
    private Bundle bundle;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ctx=this;
        activity=this;

        pos=ctx.getSharedPreferences("userinfo",MODE_PRIVATE);
        H=new Handler();
        H.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(pos.getBoolean("loginstate",false)==true){
                    CallHttpRequest request=new CallHttpRequest(ctx,"Login",activity);
                    String url[]={"http://192.168.1.10/login.php?user="
                            + pos.getString("mobileno","")
                            + "&password=" + pos.getString("pwd","")};
                    request.execute(url);
                }else{
                    intent=new Intent(ctx,LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },2000);
    }
}
