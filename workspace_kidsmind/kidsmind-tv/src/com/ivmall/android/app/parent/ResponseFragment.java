package com.ivmall.android.app.parent;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.ActionRequest;
import com.ivmall.android.app.entity.ActionResponse;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.uitls.BaiduUtils;


public class ResponseFragment extends Fragment {

    public static final String TAG = ResponseFragment.class.getSimpleName();

    private Activity mAct;

    private ImageView mImgMobleDownLoad;

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

        return inflater.inflate(R.layout.mobile_response, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mImgMobleDownLoad = (ImageView) view.findViewById(R.id.img_mobile_download);
        //reqAction("index");
    }


    /**
     * 1.52 获取促销信息宣传图
     *//*
    private void reqAction(String location) {
        String url = AppConfig.REQ_ACTION;
        ActionRequest request = new ActionRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);
        request.setLocation(location);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                final ActionResponse resp = GsonUtil.parse(response,
                        ActionResponse.class);
                if (resp != null && resp.isSucess()) {
                    String url = resp.getImgUrl();

                    if (!StringUtils.isEmpty(url)) {
                        Glide.with(mAct)
                                .load(url)
                                .centerCrop()
                                .into(mImgMobleDownLoad);

                    }
                }
            }
        });
    }*/


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
