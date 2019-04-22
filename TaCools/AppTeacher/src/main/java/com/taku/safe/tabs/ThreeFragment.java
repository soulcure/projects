package com.taku.safe.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
//import com.youth.banner.Banner;
//import com.youth.banner.BannerConfig;
//import com.youth.banner.listener.OnBannerListener;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;


public class ThreeFragment extends BasePermissionFragment implements View.OnClickListener{
    public final static String TAG = ThreeFragment.class.getSimpleName();

    private UIHandler mHandler;
    //private Banner banner;
    private List<String> images;
    private List<String> titles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);

        String[] urls = getResources().getStringArray(R.array.url);
        String[] tips = getResources().getStringArray(R.array.title);

        images = Arrays.asList(urls);
        titles = Arrays.asList(tips);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        /*banner = (Banner) view.findViewById(R.id.banner);
        //本地图片数据（资源文件）

        banner.setImages(images)
                .setBannerTitles(titles)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerListener(this)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .start();*/


    }

    /*@Override
    public void OnBannerClick(int position) {
        Toast.makeText(mContext, "position:" + position, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<ThreeFragment> mTarget;

        UIHandler(ThreeFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

    /*public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .crossFade()
                    .into(imageView);
        }
    }*/

}
