package com.taku.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.taku.safe.R;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by colin on 2017/6/8.
 */

public class WelcomeTakuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_taku);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void initView() {
        ImageView img_gif = (ImageView) findViewById(R.id.img_gif);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.mipmap.welcome);
            gifDrawable.setLoopCount(1);
            gifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    onBackPressed();
                }
            });
            img_gif.setImageDrawable(gifDrawable);

        } catch (IOException e) {
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
