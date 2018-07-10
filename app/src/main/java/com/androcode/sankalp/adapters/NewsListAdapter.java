package com.androcode.sankalp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.androcode.sankalp.R;
import com.androcode.sankalp.models.NewsList;

import java.util.List;

/**
 * Created by hp-u on 11/27/2017.
 */

public class NewsListAdapter extends ArrayAdapter<NewsList> {
    private Context ctx;
    private LayoutInflater layoutInflater;
    private NewsList N;
    private ViewHolder holder;

    public NewsListAdapter(@NonNull Context context, int resource, @NonNull List<NewsList> objects) {
        super(context, resource, objects);
        ctx = context;
        layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.custom_row_notification_list, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        N = getItem(position);

        return convertView;
    }
}
