package com.sample.xteamtest.ui.controllers;

import com.sample.xteamtest.App;
import com.sample.xteamtest.events.SwipeRefreshEvent;
import com.sample.xteamtest.rest.RestClient;
import com.sample.xteamtest.rest.RestSubscriberModule;
import com.squareup.okhttp.Cache;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by nitishmehrotra.
 */
public class MainController {

    private Cache mCache;
    private RestClient mRestClient;

    public void init(Cache cache) {
        mCache = cache;
        mRestClient = new RestClient(mCache);


    }

    /**
     * This function is used to call get the data from the server.
     *
     * @param limit Max number of search results to return.
     * @param skip Number of results to skip before sending back search results.
     * @param searchFromTags Search query for tags which are separated by spaces
     * @param stockStatus When flag is set, only return products that are currently in stock.
     */

    public void getData(String limit, String skip, String searchFromTags, boolean stockStatus) {
        App.getBus().post(new SwipeRefreshEvent(true));
        if (mRestClient == null) {
            throw new NullPointerException("Rest Client has not been initialized.");
        }
        String inStock;
        if(stockStatus) {
            inStock = "1";
        } else {
            inStock = "0";
        }

        mRestClient.getServerAPI()
                .fetch(limit, skip, searchFromTags, inStock)
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RestSubscriberModule.provideSearchSubscriber());
    }
}
