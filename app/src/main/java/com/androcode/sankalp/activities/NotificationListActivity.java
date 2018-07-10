package com.androcode.sankalp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.androcode.sankalp.R;
import com.androcode.sankalp.adapters.NotificationListAdapter;
import com.androcode.sankalp.models.NotificationList;
import com.androcode.sankalp.networks.VolleyConnect;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.crowdfire.cfalertdialog.CFAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationListActivity extends AppCompatActivity {
    private ImageView back;
    private Context ctx;
    private List<NotificationList> list;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    private String URL;
    private CFAlertDialog.Builder builder;
    private NotificationListAdapter adapter;
    private ListView listView;
    private NotificationList N;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        ctx=this;

        requestQueue= VolleyConnect.getInstance().getRequestQueue();
        getData();
        listView=(ListView)findViewById(R.id.notfication_list);
        list=new ArrayList<NotificationList>();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter=new NotificationListAdapter(ctx,R.layout.custom_row_notification_list,list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                N=list.get(i);
                showDialog(N.getMobileno(),N.getName());
            }
        });
    }

    private void getData(){
        URL="http://192.168.1.10/notificationlist.php";

        jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                list.clear();
                for(int i=0;i<response.length();i++){
                    JSONObject jsonObject=response.optJSONObject(i);
                    NotificationList N=new NotificationList();
                    N.setName(jsonObject.optString("name"));
                    N.setMobileno(jsonObject.optString("mobileno"));
                    N.setLocation(jsonObject.optString("location"));
                    N.setDate(jsonObject.optString("date"));
                    N.setTime(jsonObject.optString("time"));
                  //  Toast.makeText(ctx,jsonObject.optString("mobileno")+"",Toast.LENGTH_LONG).show();
                    list.add(N);
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void showDialog(String mobileno,String name) {
        builder = new CFAlertDialog.Builder(ctx)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Help "+name)
                .addButton("Call :)", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    makeCall(mobileno);
                    dialog.dismiss();
                }).addButton("Cancel", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                    dialog.dismiss();
                });
        builder.show();
    }

    private void makeCall(String mobileno){
        intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+mobileno));
        startActivity(intent);
    }
}
