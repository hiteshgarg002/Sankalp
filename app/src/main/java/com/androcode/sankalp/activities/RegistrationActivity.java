package com.androcode.sankalp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.androcode.sankalp.R;

public class RegistrationActivity extends AppCompatActivity {
    private TextView login;
    private Context ctx;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ctx=this;

        login=(TextView)findViewById(R.id.loginTag);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(ctx,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
