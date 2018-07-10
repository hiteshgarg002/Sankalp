package com.androcode.sankalp.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.androcode.sankalp.R;
import com.androcode.sankalp.activities.MapsSosActivity;
import com.androcode.sankalp.activities.NewsActivity;
import com.androcode.sankalp.activities.ProfileActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by hp-u on 11/25/2017.
 */

public class BottomNavViewExHelper {
    public static void setupBottomNavView(BottomNavigationViewEx bottomNavigationViewEx){
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(false);
    }

    public static void enableBottomNavView(final Context context, BottomNavigationViewEx bottomNavigationViewEx, final Activity activity){
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        Intent intent1=new Intent(context, ProfileActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent1);
                        activity.finish();
                        break;

                    case R.id.nav_sos:
                        Intent intent2=new Intent(context, MapsSosActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent2);
                        activity.finish();
                        break;

                    case R.id.nav_news:
                        Intent intent3=new Intent(context, NewsActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent3);
                        activity.finish();
                        break;
                }
                return false;
            }
        });
    }
}
