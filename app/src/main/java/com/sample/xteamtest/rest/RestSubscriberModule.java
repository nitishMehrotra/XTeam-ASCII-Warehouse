package com.sample.xteamtest.rest;

import android.util.Log;

import com.sample.xteamtest.App;
import com.sample.xteamtest.events.NetworkCallFailureEvent;

import rx.Subscriber;

/**
 * Created by nitishmehrotra.
 */
public class RestSubscriberModule {
    public static final String TAG = RestSubscriberModule.class.getCanonicalName();

    public static Subscriber<String> provideSearchSubscriber() {
        return new Subscriber<String>() {
            @Override
            public void onCompleted() {
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
                App.getBus().post(new NetworkCallFailureEvent());
            }

            @Override
            public void onNext(String responseBody) {
                App.getBus().post(responseBody);
            }
        };
    }

}
