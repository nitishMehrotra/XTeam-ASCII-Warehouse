package com.sample.xteamtest;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Created by nitishmehrotra.
 */
public class App extends Application {

    private static Bus mBusInstance;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Bus getBus() {
        if (mBusInstance == null)
            mBusInstance = new Bus();
        return mBusInstance;
    }
}
