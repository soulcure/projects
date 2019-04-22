package com.ivmall.android.app.parent;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.ivmall.android.app.KidsMindApplication;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GlideRoundTransform;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.uitls.ZXingUtil;



public class OrderFragment extends Fragment {

    public static final String TAG = OrderFragment.class.getSimpleName();

    private Activity mAct;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.order_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView imgOrder = (ImageView) view.findViewById(R.id.img_order);

        String url = ((KidsMindApplication) mAct.getApplication())
                .getAppConfig("queryOrderURL");
        if (!StringUtils.isEmpty(url)) {
            try {
                int width = getResources().getDimensionPixelSize(R.dimen.qrcode_width);
                int height = getResources().getDimensionPixelSize(R.dimen.qrcode_height);
                Bitmap nestImage = BitmapFactory.decodeResource(
                        getResources(), R.drawable.app_icon);
                Bitmap bitmap = ZXingUtil.encode(url, width, height, nestImage);
                imgOrder.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }


}
