package com.sample.xteamtest.rest;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by nitishmehrotra.
 */
public interface XTeamEmoticonApis {

    @GET("/api/search")
    public Observable<String> fetch(
            @Query("limit") String limit,
            @Query("skip") String skip,
            @Query("q") String searchFromTags,
            @Query("onlyInStock") String inStock
    );

}
