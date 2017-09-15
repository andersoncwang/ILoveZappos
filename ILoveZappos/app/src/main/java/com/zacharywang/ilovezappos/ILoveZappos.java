package com.zacharywang.ilovezappos;

import android.app.Application;
import android.content.Context;

/**
 * Created by david on 9/11/17.
 */

public class ILoveZappos extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ILoveZappos.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ILoveZappos.context;
    }
}
