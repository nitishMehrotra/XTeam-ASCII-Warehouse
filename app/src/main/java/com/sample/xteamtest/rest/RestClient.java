package com.sample.xteamtest.rest;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by nitishmehrotra.
 */
public class RestClient {

    private static final String BASE_URL = "http://74.50.59.155:5000";
    private static long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB

    private XTeamEmoticonApis mXTeamEmoticonApis = null;


    public RestClient(Cache cache) {

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(cache);
        okHttpClient.networkInterceptors().add(mCacheControlInterceptor);

        // Create Executor
        Executor executor = Executors.newCachedThreadPool();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setExecutors(executor, executor)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setConverter(new NDJSONConverter())
                .build();

        mXTeamEmoticonApis = restAdapter.create(XTeamEmoticonApis.class);
    }

    public XTeamEmoticonApis getServerAPI() {
        if (mXTeamEmoticonApis == null) {
            throw new NullPointerException("Rest API not initialized");
        }
        return mXTeamEmoticonApis;
    }

    private static final Interceptor mCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (request.method().equals("GET")) {
                request.newBuilder()
                        .header("Cache-Control", "only-if-cached")
                        .build();
            }

            Response response = chain.proceed(request);

            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=86400") // 1 day
                    .build();
        }
    };

    private static class NDJSONConverter implements Converter {

        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {
            String text = null;
            try {
                text = fromStream(body.in());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;

        }

        @Override
        public TypedOutput toBody(Object object) {
            return null;
        }

        // Custom method to convert stream from request to string
        public static String fromStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("\n");

            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(newLine);
            }
            return out.toString();
        }
    }

}
