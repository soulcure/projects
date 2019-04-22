package com.taku.safe.http;


public interface DownloadListener {

    void onProgress(int rate);

    void onFail(String err);

    void onSuccess(String path);

}