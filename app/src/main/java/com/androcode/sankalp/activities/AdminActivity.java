package com.androcode.sankalp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androcode.sankalp.R;
import com.androcode.sankalp.fragments.AllUsersFragment;
import com.androcode.sankalp.fragments.UserDeleleFragment;
import com.gigamole.navigationtabstrip.NavigationTabStrip;

public class AdminActivity extends AppCompatActivity {
    private SharedPreferences pos;
    private SharedPreferences.Editor editor;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ctx = this;

        pos = ctx.getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = pos.edit();
        editor.putBoolean("loginstate", true);
        editor.commit();

        setUpTab();
    }

    private void setUpTab() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment F = null;
                if (position == 0) {
                    F = new AllUsersFragment();
                }
                if (position == 1) {
                    F = new AllUsersFragment();
                }
                if (position == 2) {
                    F = new UserDeleleFragment();
                }
                return F;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts_top);
        navigationTabStrip.setTitles("All Users", "Add User", "Delete User");
        navigationTabStrip.setTitleSize(20);
        navigationTabStrip.setStripColor(Color.GREEN);
        navigationTabStrip.setStripWeight(6);
        navigationTabStrip.setStripFactor(2);
        navigationTabStrip.setStripType(NavigationTabStrip.StripType.POINT);
        navigationTabStrip.setStripGravity(NavigationTabStrip.StripGravity.BOTTOM);
        navigationTabStrip.setCornersRadius(3);
        navigationTabStrip.setAnimationDuration(300);
        navigationTabStrip.setInactiveColor(Color.GRAY);
        navigationTabStrip.setActiveColor(Color.WHITE);

        navigationTabStrip.setViewPager(viewPager, 0);
        navigationTabStrip.setTabIndex(0, true);
    }
}
