package com.androcode.sankalp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androcode.sankalp.R;
import com.androcode.sankalp.helpers.BottomNavViewExHelper;
import com.androcode.sankalp.networks.VolleyConnect;
import com.androcode.sankalp.services.NotificationService;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.baoyz.widget.PullRefreshLayout;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Context ctx;
    private BottomNavigationViewEx viewEx;
    private Menu menu;
    private String URL, mobno, date;
    private MenuItem menuItem;
    private Intent intent;
    private CFAlertDialog.Builder builder;
    private Activity activity;
    private ImageView editProfile;
    private CircleImageView profilePhoto;
    private TextView userName, userAge, userBloodGroup, daysLeft, donatedOn;
    private Button donate;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    private PullRefreshLayout refreshLayout;
    private FloatingActionButton notificationBtn;
    private SharedPreferences pos;
    private SharedPreferences.Editor editor;
    private Calendar currentTime, futureTime;
    private StringRequest stringRequest;
    private LinearLayout buttonLayout, tagLayout;
    private Date futureDate;
    private RelativeLayout progress,profile;
    private int flag;
    private static final int ACTIVITY_NUMBER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ctx = this;
        activity = this;
        flag = getIntent().getFlags();

        if (flag == Intent.FLAG_ACTIVITY_NEW_TASK) {
            showProfileUpdatedSuccessDialog();
        }

        pos = ctx.getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = pos.edit();
        editor.putBoolean("loginstate", true);
        editor.commit();

        profile=(RelativeLayout)findViewById(R.id.rel_profile);
        progress=(RelativeLayout)findViewById(R.id.rel_progress_profile);
        mobno = pos.getString("mobileno", "");
        requestQueue = VolleyConnect.getInstance().getRequestQueue();
        refreshLayout = (PullRefreshLayout) findViewById(R.id.refresh_profile);
        profilePhoto = (CircleImageView) findViewById(R.id.profile_pic);
        userName = (TextView) findViewById(R.id.userName);
        userAge = (TextView) findViewById(R.id.userAge);
        userBloodGroup = (TextView) findViewById(R.id.userBloodGroup);
        donatedOn = (TextView) findViewById(R.id.donatedOn);
        donate = (Button) findViewById(R.id.donate);
        daysLeft = (TextView) findViewById(R.id.countDown);
        buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
        tagLayout = (LinearLayout) findViewById(R.id.tag_layout);
        notificationBtn = (FloatingActionButton) findViewById(R.id.notification_button);
        futureTime = Calendar.getInstance();

        editProfile = (ImageView) findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ctx, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ctx, NotificationListActivity.class);
                startActivity(intent);
            }
        });

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        setupBottomNavView();
        getDetails();
        createApplicationFolder();
        startService();
        getTimeStuff();
    }

    private void setupBottomNavView() {
        viewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_view_ex);
        BottomNavViewExHelper.setupBottomNavView(viewEx);
        BottomNavViewExHelper.enableBottomNavView(ctx, viewEx, activity);
        menu = viewEx.getMenu();
        menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void getDetails() {
        URL = "http://192.168.1.10/fetchprofile.php?mobileno=" + mobno;

        jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);
                    userName.setText(jsonObject.optString("name"));
                    //  gender.setText(jsonObject.optString("gender"));
                    userBloodGroup.setText(jsonObject.optString("bloodgroup"));
                    userAge.setText(jsonObject.optString("age"));
                    //  Toast.makeText(ctx, jsonObject.optString("age") + "", Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.GONE);
                    profile.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ctx, error + "", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void createApplicationFolder() {
        File folder = new File(android.os.Environment.getExternalStorageDirectory(), File.separator + "Pictures/Sankalp/");
        folder.mkdirs();
    }

    private void startService() {
        intent = new Intent(ctx, NotificationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }

    private void showDialog() {
        builder = new CFAlertDialog.Builder(ctx)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Confirm Blood Donation")
                .addButton("Comfirm :)", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    timeStuff();
                    dialog.dismiss();
                }).addButton("Cancel", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.show();
    }

    private void timeStuff() {
        currentTime = Calendar.getInstance();
        date = currentTime.get(Calendar.YEAR) + "-" + (currentTime.get(Calendar.MONTH) + 1) + "-" + currentTime.get(Calendar.DATE);
        URL = "http://192.168.1.10/donate.php?mobileno=" + mobno + "&timestamp=" + currentTime.getTimeInMillis() + "&date=" + date;
        stringRequest = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getTimeStuff();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void getTimeStuff() {
        URL = "http://192.168.1.10/donatestuff.php?mobileno=" + mobno;
        jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);
                    futureTime.setTime(new Date(Long.parseLong(jsonObject.optString("timestamp"))));
                    futureTime.add(Calendar.MONTH, 6);
                    futureDate = futureTime.getTime();
                    daysLeft.setText(getCountdownText(ctx, futureDate));
                    donatedOn.setText(jsonObject.optString("date"));

                    if (!(donatedOn.getText().equals(""))) {
                        buttonLayout.setVisibility(View.GONE);
                        tagLayout.setVisibility(View.VISIBLE);
                    } else {
                        buttonLayout.setVisibility(View.VISIBLE);
                        tagLayout.setVisibility(View.GONE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    //This function takes the future date and creates a counter till that
    public CharSequence getCountdownText(Context context, Date futureDate) {
        StringBuilder countdownText = new StringBuilder();

        // Calculate the time between now and the future date.
        long timeRemaining = futureDate.getTime() - new Date().getTime();

        // If there is no time between (ie. the date is now or in the past), do nothing
        if (timeRemaining > 0) {
            Resources resources = context.getResources();
            // Calculate the days/hours/minutes/seconds within the time difference.
            // It's important to subtract the value from the total time remaining after each is calculated.
            // For example, if we didn't do this and the time was 25 hours from now,
            // we would get `1 day, 25 hours`.
            int days = (int) TimeUnit.MILLISECONDS.toDays(timeRemaining);
            timeRemaining -= TimeUnit.DAYS.toMillis(days);
            int hours = (int) TimeUnit.MILLISECONDS.toHours(timeRemaining);
            timeRemaining -= TimeUnit.HOURS.toMillis(hours);
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
            timeRemaining -= TimeUnit.MINUTES.toMillis(minutes);
            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeRemaining);

            // For each time unit, add the quantity string to the output, with a space.
            if (days > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.days, days, days));
                countdownText.append(" ");
            }
            if (days > 0 || hours > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.hours, hours, hours));
                countdownText.append(" ");
            }
            if (days > 0 || hours > 0 || minutes > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.minutes, minutes, minutes));
                countdownText.append(" ");
            }
            if (days > 0 || hours > 0 || minutes > 0 || seconds > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.seconds, seconds, seconds));
                countdownText.append(" ");
            }
        }
        return countdownText.toString();
    }

    private void showProfileUpdatedSuccessDialog() {
        new AwesomeSuccessDialog(ctx)
                .setTitle("Profile")
                .setMessage("Your profile has been updated!")
                .setDialogBodyBackgroundColor(R.color.colorPrimary)
                .setPositiveButtonText(getString(R.string.dialog_ok_button))
                .setCancelable(false)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                    }
                })
                .show();
    }
}

