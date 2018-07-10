package com.androcode.sankalp.applicationcontext;

import android.app.Application;
import android.content.Context;

/**
 * Created by hp-u on 9/10/2017.
 */

public class ApplicationContextReference extends Application {
    static ApplicationContextReference applicationContextReference;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContextReference=this;
    }

    public static Context getAppContext(){
        return applicationContextReference.getApplicationContext();
    }
}
