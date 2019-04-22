package com.applidium.nickelodeon.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.dialog.PaymentDialog;
import com.applidium.nickelodeon.entity.FiveValue;
import com.applidium.nickelodeon.entity.LoginResponse;
import com.applidium.nickelodeon.entity.ProfileRequest;
import com.applidium.nickelodeon.entity.ProfileResponse;
import com.applidium.nickelodeon.entity.ProtocolRequest;
import com.applidium.nickelodeon.impl.OnArticleSelectedListener;
import com.applidium.nickelodeon.impl.OnSucessListener;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        String promoter = ((MNJApplication) mAct.getApplication()).getPromoter();
        //小米TV渠道要求添加
        if (promoter.equals(PaymentDialog.MITV_CHANNEL)) {
            view.findViewById(R.id.logo_channel).setVisibility(View.VISIBLE);
        }

        imageLogo = (ImageView) view.findViewById(R.id.logo_img);
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
        String token = ((MNJApplication) mAct.getApplication()).getToken();
        if (StringUtils.isEmpty(token)) {
            String url = AppConfig.AUTO_LOGIN;
            ProtocolRequest request = new ProtocolRequest();
            String json = request.toJsonString();

            HttpConnector.httpPost(url, json, new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    // TODO Auto-generated method stub
                    LoginResponse resp = GsonUtil.parse(response,
                            LoginResponse.class);
                    if (resp != null) {
                        if (resp.isSucess()) {
                            String userId = resp.getData().getAccountId();
                            String token = resp.getData().getToken();
                            String userName = resp.getData().getUsername();
                            String mobileNum = resp.getData().getMobile();
                            String pwd = resp.getData().getPassword();
                            String firstMtime = resp.getData().getFirstModifiedTime();

                            if (StringUtils.isEmpty(token)) { //token为空表示用户已经修改了账号密码

                                Bundle bundle = new Bundle();
                                bundle.putString(LoginIvmallFragment.MOBILE_NUM, mobileNum);
                                bundle.putString(LoginIvmallFragment.USER_NAME, userName);

                                mOnArticleKidsMind.skipToFragment(HasRegisterFragment.TAG, bundle);
                            } else {
                                ((MNJApplication) getActivity().getApplication()).setUserId(userId);
                                ((MNJApplication) getActivity().getApplication()).setToken(token);
                                ((MNJApplication) getActivity().getApplication()).setUserName(userName);
                                ((MNJApplication) getActivity().getApplication()).setMoblieNum(mobileNum);
                                ((MNJApplication) getActivity().getApplication()).setPassWord(pwd);
                                ((MNJApplication) getActivity().getApplication()).setFirstModifiedTime(firstMtime);
                                ((MNJApplication) getActivity().getApplication()).setVipExpiresTime(resp.getData().getVipExpiryTime());

                                ((MNJApplication) mAct.getApplication()).reqProfile(new OnCreateProfileResult());
                            }
                        } else {
                            Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mAct, R.string.connect_error, Toast.LENGTH_SHORT).show();
                        mAct.finish();
                    }
                }
            });
        } else {
            ((MNJApplication) mAct.getApplication()).reqProfile(new OnCreateProfileResult());
        }

    }


    private class OnCreateProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            //登录法国服务器
            /*String userId = ((MNJApplication) mAct.getApplication()).getUserId();
            String token = ((MNJApplication) mAct.getApplication()).getToken();
            ((MNJApplication) mAct.getApplication()).loginMnj(userId, token);*/

            ((MNJApplication) mAct.getApplication()).reqUserInfo(); //请求用户信息

            Bundle bundle = getArguments();

            mOnArticleKidsMind.skipToFragment(MetroFragment.TAG, bundle);
        }

        @Override
        public void create() {
            mOnArticleKidsMind.skipToFragment(KidsInfoFragment.TAG, null);// profile
        }


        @Override
        public void fail() {
            ((MNJApplication) mAct.getApplication()).clearToken();
            mOnArticleKidsMind.skipToFragment(LoginIvmallFragment.TAG, null);
        }
    }


    private void createProfile() {
        String url = AppConfig.CREATE_PROFILE;
        ProfileRequest request = new ProfileRequest();

        String token = ((MNJApplication) mAct.getApplication()).getToken();
        request.setToken(token);
        request.setNickname(mAct.getString(R.string.baby_default_name));
        request.setBirthday(getBabyBirthday());
        request.setGender(KidsInfoFragment.Gender.F);

        FiveValue fiveValue = new FiveValue();
        fiveValue.setArt(2);
        fiveValue.setHealth(2);
        fiveValue.setLanguage(2);
        fiveValue.setScience(2);
        fiveValue.setSocial(2);
        request.setRates(fiveValue);


        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProfileResponse resp = GsonUtil.parse(response,
                        ProfileResponse.class);
                if (resp.isSucess()) {

                }

            }

            @Override
            public void doError() {
            }
        });
    }

    /**
     * 获取宝宝生日
     */
    private String getBabyBirthday() {

        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.YEAR, date.get(Calendar.YEAR) - 3);

        return dft.format(date.getTime());

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
