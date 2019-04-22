package com.taku.safe.utils;

import android.os.AsyncTask;


import java.util.ArrayList;

/**
 * Created by soulcure on 2017/5/31.
 */

public class CompressAsyncTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {


    @Override
    protected ArrayList<String> doInBackground(ArrayList<String>... params) {

        ArrayList<String> res = new ArrayList<>();
        for (String item : params[0]) {
            res.add(CompressImage.compressImage(item));
        }

        return res;
    }

    protected void onPostExecute(ArrayList<String> res) {


    }

}