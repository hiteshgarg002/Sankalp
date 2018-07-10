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
import com.androcode.sankalp.models.NotificationList;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp-u on 11/27/2017.
 */

public class NotificationListAdapter extends ArrayAdapter<NotificationList> {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private NotificationList N;
    private ViewHolder holder;
    private String URL;

    public NotificationListAdapter(@NonNull Context context, int resource, @NonNull List<NotificationList> objects) {
        super(context, resource, objects);
        ctx = context;
        layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        TextView name, mobileno, location, date, time;
        CircleImageView dp;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.custom_row_notification_list, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.notification_name);
            holder.location = (TextView) convertView.findViewById(R.id.notification_location);
            holder.mobileno = (TextView) convertView.findViewById(R.id.notification_mobileno);
            holder.date = (TextView) convertView.findViewById(R.id.notification_date);
            holder.time = (TextView) convertView.findViewById(R.id.notification_time);
            holder.dp = (CircleImageView) convertView.findViewById(R.id.notification_dp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        N = getItem(position);
        holder.name.setText(N.getName());
        holder.mobileno.setText(N.getMobileno());
        holder.location.setText(N.getLocation());
        holder.date.setText(N.getDate());
        holder.time.setText(N.getTime());
        URL = "";
        //Glide.with(ctx).load(URL).into(holder.dp);

        return convertView;
    }
}
