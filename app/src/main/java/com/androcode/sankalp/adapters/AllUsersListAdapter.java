package com.androcode.sankalp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androcode.sankalp.R;
import com.androcode.sankalp.models.AllUsersList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp-u on 12/2/2017.
 */

public class AllUsersListAdapter extends ArrayAdapter<AllUsersList> {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private AllUsersList usersList;
    private ViewHolder holder;
    private String URL;

    public AllUsersListAdapter(@NonNull Context context, int resource, @NonNull List<AllUsersList> objects) {
        super(context, resource, objects);
        ctx = context;
        layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        TextView name, mobileno, joindate;
        CircleImageView dp;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.custom_row_all_users, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.all_users_name);
            holder.mobileno = (TextView) convertView.findViewById(R.id.all_users_mobileno);
            holder.joindate = (TextView) convertView.findViewById(R.id.all_users_join_date);
            holder.dp = (CircleImageView) convertView.findViewById(R.id.notification_dp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        usersList = getItem(position);
        holder.name.setText(usersList.getName());
        holder.mobileno.setText(usersList.getMobileNo());
        holder.joindate.setText(usersList.getJoinDate());
        URL = "";
        //Glide.with(ctx).load(URL).into(holder.dp);

        return convertView;
    }
}
