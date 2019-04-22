package com.applidium.nickelodeon.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.NmjLoginResponse;
import com.applidium.nickelodeon.entity.ProfileRequest;
import com.applidium.nickelodeon.entity.ProfileResponse;
import com.applidium.nickelodeon.entity.SevenValue;
import com.applidium.nickelodeon.impl.OnArticleSelectedListener;
import com.applidium.nickelodeon.impl.OnSucessListener;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.views.NumberPicker;
import com.applidium.nickelodeon.views.NumberPicker.OnValueChangeListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class KidsInfoFragment extends Fragment implements OnClickListener {
    public final static String TAG = KidsInfoFragment.class.getSimpleName();

    private Activity mAct;
    public OnArticleSelectedListener mOnArticleKidsMind;

    // ---------------------------UI控件--------------------------------------------------
    private NumberPicker agePicker;
    private NumberPicker sexPicker;

    private Gender mGender;
    private int mAgeValue;

    private boolean isUpdateProfile;

    private ImageView finger;

    public enum Gender {
        N, M, F
        //none, male, female
    }

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
        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdateProfile = bundle.getBoolean("update_profile", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kidsinfo_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.img_next).setOnClickListener(this);
        finger = (ImageView) view.findViewById(R.id.finger);
        if (ScreenUtils.isTv(mAct)) {
            finger.setVisibility(View.INVISIBLE);
        }

        agePicker = (NumberPicker) view.findViewById(R.id.age_picker);
        agePicker.setMinValue(2);
        agePicker.setMaxValue(8);
        agePicker.setFocusable(true);
        agePicker.setFocusableInTouchMode(true);
        agePicker.setOnValueChangedListener(mAgeChangedListener);
        agePicker.setValue(3);
        mAgeValue = 3;


        sexPicker = (NumberPicker) view.findViewById(R.id.sex_picker);
        sexPicker.setMaxValue(3);
        sexPicker.setMinValue(0);
        sexPicker.setFocusable(true);
        sexPicker.setFocusableInTouchMode(true);
        sexPicker.setOnValueChangedListener(mSexChangedListener);
        sexPicker.setValue(0);
        mGender = Gender.M;
        updateDateControl();

    }

    @Override
    public void onResume() {
        super.onResume();
        agePicker.requestFocus(); //解决小米盒子无法focus
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


    private void updateDateControl() {
        String[] displayValues = {"男", "女", "男", "女"};
        sexPicker.setDisplayedValues(displayValues);
        sexPicker.invalidate();
    }

    private NumberPicker.OnValueChangeListener mAgeChangedListener = new OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            // TODO Auto-generated method stub
            mAgeValue = picker.getValue();
        }
    };

    private NumberPicker.OnValueChangeListener mSexChangedListener = new OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            // TODO Auto-generated method stub
            int value = picker.getValue();
            if (value % 2 == 0) {
                mGender = Gender.M;
            } else {
                mGender = Gender.F;
            }
        }
    };

    /**
     * 获取宝宝生日
     */
    private String getBabyBirthday() {

        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.YEAR, date.get(Calendar.YEAR) - mAgeValue);

        return dft.format(date.getTime());

    }

    /**
     * 1.8 创建profile
     */
    private void createProfile() {
        String url = AppConfig.CREATE_PROFILE;
        ProfileRequest request = new ProfileRequest();

        String token = ((MNJApplication) mAct.getApplication()).getToken();
        request.setToken(token);
        request.setNickname(mAct.getResources().getString(R.string.baby_default_name));
        request.setBirthday(getBabyBirthday());
        request.setGender(mGender);
        SevenValue sevenValue = new SevenValue();
        sevenValue.setAgility(2);
        sevenValue.setBilingualComm(2);
        sevenValue.setImagCreativity(2);
        sevenValue.setNumMath(2);
        sevenValue.setOwnPerception(2);
        sevenValue.setReasoning(2);
        sevenValue.setWildlifeNature(2);

        request.setPreferences(sevenValue);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProfileResponse resp = GsonUtil.parse(response,
                        ProfileResponse.class);
                if (resp.isSucess()) {
                    ((MNJApplication) mAct.getApplication()).reqProfile(new OnReqProfileResult());
                }

            }

            @Override
            public void doError() {
                // createProfile 协议暂时未使用 出错情况也进入
                mOnArticleKidsMind.skipToFragment(MetroFragment.TAG, null);

            }
        });
    }


    private class OnReqProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            ((MNJApplication) mAct.getApplication()).reqUserInfo(); //请求用户信息
            mOnArticleKidsMind.skipToFragment(MetroFragment.TAG, null);
        }

        @Override
        public void create() {

        }

        @Override
        public void fail() {

        }
    }


    /**
     * 1.9 更新profile
     */
    private void updateProfile() {

        String url = AppConfig.MNJ_HOST + "/CogniKizzTV/profiles";

        ContentValues headers = new ContentValues();

        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "application/json");
        headers.put("X-HTTP-METHOD-OVERRIDE", "PUT");

        String xmlBody = "";
        HttpConnector.httpPost(url, xmlBody, headers, new IPostListener() {
            @Override
            public void httpReqResult(String response) {

                NmjLoginResponse resp = GsonUtil.parse(response,
                        NmjLoginResponse.class);

            }
        });


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_next) {
            if (isUpdateProfile) {
                updateProfile();
            } else {
                createProfile();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
