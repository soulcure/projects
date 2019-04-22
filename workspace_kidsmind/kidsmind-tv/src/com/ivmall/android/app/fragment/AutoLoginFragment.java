package com.ivmall.android.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.KidsMindFragmentActivity;
import com.ivmall.android.app.MainFragmentActivity;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.entity.AdvLogoResponse;
import com.ivmall.android.app.entity.ProtocolRequest;
import com.ivmall.android.app.entity.ProtocolResponse;
import com.ivmall.android.app.entity.SetupResponse;
import com.ivmall.android.app.impl.OnArticleSelectedListener;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.player.SmartPlayingActivity;
import com.ivmall.android.app.service.MediaPlayerService;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;


public class AutoLoginFragment extends Fragment {
    public final static String TAG = AutoLoginFragment.class.getSimpleName();

    private Activity mAct;
    public OnArticleSelectedListener mOnArticleKidsMind;
    private ImageView imageLogo;


    // --------------------handler what end--------------------

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
        try {
            mOnArticleKidsMind = (OnArticleSelectedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_anim_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        String promoter = ((KidsMindApplication) mAct.getApplication()).getPromoter();
        //小米TV渠道要求添加
        if (promoter.equals(PaymentDialog.MITV_CHANNEL)) {
            view.findViewById(R.id.logo_channel).setVisibility(View.VISIBLE);
        }

        imageLogo = (ImageView) view.findViewById(R.id.logo_img);

        reqAdvLogo();
    }


    @Override
    public void onResume() {
        super.onResume();
        BaiduUtils.onResume(mAct);

        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(2000);
        anim.setFillAfter(true);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Intent intent = new Intent(getActivity(), MediaPlayerService.class);
                intent.putExtra("media", MediaPlayerService.WELCOME);
                getActivity().startService(intent);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //mOnArticleKidsMind.skipToFragment(KidsInfoFragment.TAG, null);//for test
                // reqAppSetUp(); //for test
                autoLogin();
            }
        });
        imageLogo.startAnimation(anim);
    }


    @Override
    public void onPause() {
        super.onPause();
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        if (StringUtils.isEmpty(token)) {
            String url = AppConfig.AUTO_LOGIN;
            ProtocolRequest request = new ProtocolRequest();
            String json = request.toJsonString();
            HttpConnector.httpPost(url, json, new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    // TODO Auto-generated method stub
                    ProtocolResponse resp = GsonUtil.parse(response,
                            ProtocolResponse.class);
                    if (resp != null && resp.isSucess()) {
                        String token = resp.getToken();
                        ((KidsMindApplication) mAct.getApplication())
                                .setToken(token, true); // bug 没有初始化token导致下面协议无法进行


                        String mobile = resp.getMobile();
                        ((KidsMindApplication) mAct.getApplication())
                                .setMoblieNum(mobile);
                        //登录成功直接请求profile
                        ((KidsMindApplication) mAct.getApplication()).reqProfile(new OnCreateProfileResult());
                    } else {
                        Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } else {
            ((KidsMindApplication) mAct.getApplication()).reqProfile(new OnCreateProfileResult());
        }
    }


    private class OnCreateProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            ((KidsMindApplication) mAct.getApplication()).reqUserInfo(); //请求用户信息

            //原需求，已经删除
            /*boolean isFirst = ((KidsMindApplication) mAct.getApplication()).isFirstSetUp();
            if (isFirst) {
                reqAppSetUp();
            } else {
                boolean b = ((KidsMindApplication) mAct.getApplication()).isModelA();
                if (b) {
                    Intent intent = new Intent(mAct,
                            SmartPlayingActivity.class);
                    mAct.startActivityForResult(intent,
                            KidsMindFragmentActivity.REQUEST_MODE_A);
                } else {
                    //mOnArticleKidsMind.skipToFragment(PlayListFragment.TAG, null);
                    Intent intent = new Intent(mAct,
                            MainFragmentActivity.class);
                    startActivity(intent);
                    mAct.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    mAct.finish();
                }

            }*/

            Intent intent = new Intent(mAct,
                    MainFragmentActivity.class);
            mAct.startActivity(intent);
            mAct.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        @Override
        public void create() {
            mOnArticleKidsMind.skipToFragment(KidsInfoFragment.TAG, null);// profile
        }


        @Override
        public void fail() {
            ((KidsMindApplication) mAct.getApplication()).clearToken();
            autoLogin();
        }
    }


    /**
     * 1.23 应用安装
     */
    private void reqAppSetUp() {
        String url = AppConfig.APP_FIRST_RUN;
        ProtocolRequest request = new ProtocolRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                SetupResponse resp = GsonUtil.parse(response,
                        SetupResponse.class);
                if (resp.isSucess()) {
                    if (resp.isModelA()) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("model_a", true);
                        AppUtils.setBooleanSharedPreferences(mAct, KidsMindApplication.MOBILE_MODEL, true);

                        boolean b = ((KidsMindApplication) mAct.getApplication()).getProfile().isAutoInitial();
                        if (b) {
                            bundle.putBoolean("update_profile", true);
                            mOnArticleKidsMind.skipToFragment(KidsInfoFragment.TAG, bundle);
                        } else {
                            Intent intent = new Intent(mAct, SmartPlayingActivity.class);
                            mAct.startActivityForResult(intent,
                                    KidsMindFragmentActivity.REQUEST_MODE_A);
                        }
                    } else if (resp.isModelB()) {
                        //mOnArticleKidsMind.skipToFragment(PlayListFragment.TAG, null);
                        Intent intent = new Intent(mAct,
                                MainFragmentActivity.class);
                        startActivity(intent);
                    }
                } else {
                    //服务器异常也允许进入
                    //mOnArticleKidsMind.skipToFragment(PlayListFragment.TAG, null);
                    if (AppUtils.isNetworkConnected(mAct)) {
                        Intent intent = new Intent(mAct,
                                MainFragmentActivity.class);
                        startActivity(intent);
                    }
                }

            }

        });
    }

    /**
     * 1.24 应用启动 程序中根据渠道作区分，返回不同渠道的图片
     */
    private void reqAdvLogo() {
        String url = AppConfig.APP_LAUNCH_ADV;
        ProtocolRequest request = new ProtocolRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                AdvLogoResponse resp = GsonUtil.parse(response,
                        AdvLogoResponse.class);
                if (null != resp && resp.isSucess()) {
                    String url = resp.getData().getLaunchImg();
                    Glide.with(mAct)
                            .load(url)
                            .centerCrop()
                            .placeholder(R.drawable.pic_loading)  //占位图片
                            .error(R.drawable.pic_loading)        //下载失败
                            .into(imageLogo);

                }

            }

        });
    }


}
