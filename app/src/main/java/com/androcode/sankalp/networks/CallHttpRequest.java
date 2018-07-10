package com.androcode.sankalp.networks;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.androcode.sankalp.R;
import com.androcode.sankalp.activities.AdminActivity;
import com.androcode.sankalp.activities.LoginActivity;
import com.androcode.sankalp.activities.NewsActivity;
import com.androcode.sankalp.activities.ProfileActivity;
import com.androcode.sankalp.multipartutility.MultipartUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by hp-u on 11/28/2017.
 */

public class CallHttpRequest extends AsyncTask<String, Void, String> {
    private Context ctx;
    private Activity activityCtx;
    private Intent intent;
    private String activity;
    private File fileUpload;
    private Bundle bundle;
    private SharedPreferences pos;
    private SharedPreferences.Editor editor;

    public CallHttpRequest(Context ctx, String activity, Activity activityCtx) {
        this.ctx = ctx;
        this.activityCtx = activityCtx;
        this.activity = activity;
    }

    public CallHttpRequest(Context ctx, File fileUpload, String activity) {
        this.ctx = ctx;
        this.fileUpload = fileUpload;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... url) {
        if (activity.equals("FileUpload")) {
            String res = fileUpload(url[0], fileUpload);
            return res;
        } else {
            String res = callRequest(url[0]);
            return res;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (activity.equals("Login")) {
            if (s.equals("1")) {
                Toast.makeText(ctx, "Error on Server", Toast.LENGTH_SHORT).show();
                intent = new Intent(ctx, LoginActivity.class);
                ctx.startActivity(intent);
                activityCtx.finish();
            } else if (s.equals("failed")) {
                Toast.makeText(ctx, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                intent = new Intent(ctx, LoginActivity.class);
                ctx.startActivity(intent);
                activityCtx.finish();
            } else {
                pos = ctx.getSharedPreferences("userinfo", ctx.MODE_PRIVATE);
                editor = pos.edit();
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        editor.putString("name", jsonObject.optString("name"));
                        editor.putString("mobileno", jsonObject.optString("mobileno"));
                        editor.putString("emailid", jsonObject.optString("emailid"));
                        editor.putString("age", jsonObject.optString("age"));
                        editor.putString("bloodgroup", jsonObject.optString("bloodgroup"));
                        editor.putString("gender", jsonObject.optString("gender"));
                        editor.putString("tag", jsonObject.optString("tag"));
                        editor.putString("pwd",jsonObject.optString("password"));
                        // editor.putString("pwd",jsonObject.optString("password"));
                        editor.commit();

                        if (jsonObject.optString("tag").equals("user")) {
                            intent = new Intent(ctx, NewsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            bundle = ActivityOptions.makeCustomAnimation(ctx, R.anim.left_to_right, R.anim.right_to_left).toBundle();
                            ctx.startActivity(intent,bundle);
                            // Toast.makeText(ctx,s.toString(),Toast.LENGTH_LONG).show();
                            activityCtx.finish();
                        }
                        if (jsonObject.optString("tag").equals("admin")) {
                            intent = new Intent(ctx, AdminActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            bundle = ActivityOptions.makeCustomAnimation(ctx, R.anim.left_to_right, R.anim.right_to_left).toBundle();
                            ctx.startActivity(intent,bundle);
                            // Toast.makeText(ctx,s.toString(),Toast.LENGTH_LONG).show();
                            activityCtx.finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String callRequest(String Url) {
        try {
            URL url = new URL(Url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            DataInputStream in = new DataInputStream(con.getInputStream());

            StringBuffer output = new StringBuffer();
            String str;
            while ((str = in.readLine()) != null) {
                output.append(str);
            }
            in.close();
            return (output.toString());
        } catch (Exception e) {
        }
        return (null);
    }

    String fileUpload(String requestURL, File uploadFile) {
        String charset = "UTF-8";
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFilePart("image", uploadFile);
            List<String> response = multipart.finish();
            System.out.println("SERVER REPLIED:");
            for (String line : response) {
                System.out.println(line);
            }
            return ("Uploaded");
        } catch (IOException ex) {
            return ("Fail");
        }
    }
}




