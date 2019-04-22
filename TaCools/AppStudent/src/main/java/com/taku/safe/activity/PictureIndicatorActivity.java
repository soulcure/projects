package com.taku.safe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rd.PageIndicatorView;
import com.taku.safe.R;
import com.taku.safe.adapter.PagerIndicatorAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by colin on 2017/6/8.
 */

public class PictureIndicatorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_indicator);

        initTitle();
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

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("图片详细");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        String[] array = getIntent().getStringArrayExtra("image");
        int index = getIntent().getIntExtra("index", 0);
        List<String> list = Arrays.asList(array);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        List<View> listView = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        for (String url : list) {
            View view = mInflater.inflate(R.layout.item_picture, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_content);
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
            listView.add(view);
        }
        PagerIndicatorAdapter adapter = new PagerIndicatorAdapter(this, listView);

        viewPager.setAdapter(adapter);
        //instance of android.support.v4.view.PagerAdapter adapter

        PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(viewPager);

        viewPager.setCurrentItem(index);
        pageIndicatorView.setSelection(index);
    }


}
