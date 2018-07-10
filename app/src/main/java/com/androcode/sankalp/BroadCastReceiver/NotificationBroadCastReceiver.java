package com.androcode.sankalp.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.androcode.sankalp.services.NotificationService;

/**
 * Created by hp-u on 11/30/2017.
 */

public class NotificationBroadCastReceiver extends BroadcastReceiver {
    private Intent intent;
    @Override
    public void onReceive(Context context, Intent intent) {
        intent=new Intent(context, NotificationService.class);
        context.startService(intent);
    }
}
