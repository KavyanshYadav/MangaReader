package com.example.mangareader;

import android.app.Application;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.Request;
public class MangareaderApp extends  Application{
    @Override
    public void onCreate(){
        super.onCreate();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(client));
        builder.addRequestHandler(new CustomrequestHandler(this,client));
        Picasso.setSingletonInstance(builder.build());
    }
}
