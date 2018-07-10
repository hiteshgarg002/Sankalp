package com.androcode.sankalp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androcode.sankalp.R;
import com.androcode.sankalp.adapters.AllUsersListAdapter;
import com.androcode.sankalp.models.AllUsersList;
import com.androcode.sankalp.networks.VolleyConnect;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp-u on 12/2/2017.
 */

public class UserDeleleFragment extends Fragment {
    private ListView listView;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    private StringRequest stringRequest;
    private List<AllUsersList> list;
    private AllUsersListAdapter adapter;
    private PullRefreshLayout refreshLayout;
    private String URL;
    private Context ctx;
    private AllUsersList usersList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_users, container, false);
        ctx = getActivity();

        requestQueue = VolleyConnect.getInstance().getRequestQueue();
        getDetails();
        refreshLayout = (PullRefreshLayout) v.findViewById(R.id.refreshAllUsers);

        listView = (ListView) v.findViewById(R.id.all_users_list);
        list = new ArrayList<AllUsersList>();

        adapter = new AllUsersListAdapter(ctx, R.layout.custom_row_all_users, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDetails();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usersList = list.get(i);
                showDialog(usersList.getMobileNo());
            }
        });
        return v;
    }

    private void getDetails() {
        URL = "http://192.168.1.10/alluserslist.php";

        jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                list.clear();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = response.optJSONObject(i);
                    AllUsersList A = new AllUsersList();

                    A.setName(jsonObject.optString("name"));
                    A.setMobileNo(jsonObject.optString("mobileno"));
                    A.setJoinDate(jsonObject.optString("joindate"));
                    //  Toast.makeText(ctx,jsonObject.optString("mobileno")+"",Toast.LENGTH_LONG).show();
                    list.add(A);

                    refreshLayout.setRefreshing(false);
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

    private void showDialog(String mobileno) {
        new AwesomeWarningDialog(ctx)
                .setTitle("Delete User")
                .setMessage("Are you sure want to delete this user?")
                .setDialogBodyBackgroundColor(R.color.colorPrimary)
                .setButtonText(getString(R.string.dialog_ok_button))
                .setCancelable(true)
                .setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        deleteUser(mobileno);
                    }
                })
                .show();
    }

    private void deleteUser(String mobileno){
        URL="http://192.168.1.10/deleteuser.php?mobileno="+mobileno;

        stringRequest=new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
}

