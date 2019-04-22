package com.ivmall.android.app.parent;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.BirthdayPickerDialog;
import com.ivmall.android.app.entity.FiveValue;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.entity.ProfileRequest;
import com.ivmall.android.app.entity.ProfileResponse;
import com.ivmall.android.app.entity.SevenValue;
import com.ivmall.android.app.fragment.KidsInfoFragment;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.service.MediaPlayerService;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.uitls.ZXingUtil;
import com.ivmall.android.app.views.FoucsRatingBar;
import com.smit.android.ivmall.stb.R;

public class BabyInfoFragment extends Fragment implements OnClickListener, OnRatingBarChangeListener {

    public static final String TAG = BabyInfoFragment.class.getSimpleName();

    public static final String ISFIRSTCREATE = "isFirstCreate"; // 是否第一次进入

    private Activity mAct;

    private EditText mNickName;
    private TextView mBirday;
    private ImageView babyHead;

    private FoucsRatingBar health;
    private FoucsRatingBar science;
    private FoucsRatingBar society;
    private FoucsRatingBar language;
    private FoucsRatingBar art;


    private ImageView imageQrcord;

    private Switch switchView;

    public enum ScanType {
        createProfile, login, feedback
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAct.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.babyinfo_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        babyHead = (ImageView) view.findViewById(R.id.img_baby_head);
        imageQrcord = (ImageView) view.findViewById(R.id.img_qrcode);
        view.findViewById(R.id.btn_save).setOnClickListener(this);

        health = (FoucsRatingBar) view.findViewById(R.id.health);
        health.setOnRatingBarChangeListener(this);

        science = (FoucsRatingBar) view.findViewById(R.id.science);
        science.setOnRatingBarChangeListener(this);

        society = (FoucsRatingBar) view.findViewById(R.id.society);
        society.setOnRatingBarChangeListener(this);

        language = (FoucsRatingBar) view.findViewById(R.id.language);
        language.setOnRatingBarChangeListener(this);

        art = (FoucsRatingBar) view.findViewById(R.id.art);
        art.setOnRatingBarChangeListener(this);


        mNickName = (EditText) view.findViewById(R.id.et_name);
        mNickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    BaiduUtils.onEvent(mAct, OnEventId.CLICK_BABY_NAME, getString(R.string.click_baby_name));
            }
        });


        mBirday = (TextView) view.findViewById(R.id.tv_birday);
        mBirday.setOnClickListener(this);

        switchView = (Switch) view.findViewById(R.id.sex_switch);
        switchView.setSwitchTextAppearance(mAct, R.style.baby_sex);
        switchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaiduUtils.onEvent(mAct, OnEventId.CLICK_BABY_SEX, getString(R.string.click_baby_sex));
            }
        });

        switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    babyHead.setImageResource(R.drawable.head_girl);
                } else {
                    babyHead.setImageResource(R.drawable.head_boy);
                }

            }
        });


        // 设置二维码扫码登录
        String URL = ((KidsMindApplication) mAct.getApplication())
                .getAppConfig("webControlURL");
        String deviceDRMId = AppConfig.getDeviceDRMId(mAct);
        String token = ((KidsMindApplication) mAct.getApplication())
                .getToken();
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append('?');
        sb.append('&').append("func").append('=').append(ScanType.createProfile);
        sb.append('&').append("token").append('=').append(token);
        sb.append('&').append("deviceDRMId").append('=').append(deviceDRMId);

        initQrcodeCreateProfile(sb.toString());

    }

    @Override
    public void onResume() {
        super.onResume();


        ProfileItem list = ((KidsMindApplication) mAct.getApplication())
                .getProfile();
        if (list != null) {
            initData(list); // 初始化数据
        } else {
            ((KidsMindApplication) mAct.getApplication()).reqProfile(new OnReqProfileResult());
        }


        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);

        mAct.stopService(new Intent(mAct, MediaPlayerService.class));

    }


    /**
     * 设置二维码扫码手机填写信息
     *
     * @param url
     */
    private void initQrcodeCreateProfile(String url) {
        try {
            Bitmap nestImage = BitmapFactory.decodeResource(getResources(),
                    R.drawable.app_icon);
            int size = (int) ScreenUtils.dpToPx(mAct, 200);
            Bitmap bitmap = ZXingUtil.encode(url, size, size, nestImage);

            imageQrcord.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化数据
     */
    private void initData(ProfileItem list) {
        mNickName.setText(list.getNickname());
        mBirday.setText(list.getBirthday());
        if (list.getGender().compareTo(KidsInfoFragment.Gender.male) == 0) {
            babyHead.setImageResource(R.drawable.head_boy);
            switchView.setChecked(false);
        } else {
            babyHead.setImageResource(R.drawable.head_girl);
            switchView.setChecked(true);
        }

        health.setRating(list.getRates().getHealth());
        science.setRating(list.getRates().getScience());
        society.setRating(list.getRates().getSocial());
        language.setRating(list.getRates().getLanguage());
        art.setRating(list.getRates().getArt());


    }


    private class OnReqProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            ProfileItem list = ((KidsMindApplication) mAct.getApplication())
                    .getProfile();
            if (list != null) {
                initData(list); // 初始化数据
            }
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
        String url = AppConfig.UPDATE_PROFILE;
        ProfileRequest request = new ProfileRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);

        int profileId = ((KidsMindApplication) mAct.getApplication())
                .getProfileId();
        request.setProfileId(profileId + "");


        final String nickName = mNickName.getText().toString();
        final String birthDay = mBirday.getText().toString();

        final KidsInfoFragment.Gender sex;
        if (switchView.isChecked()) {
            sex = KidsInfoFragment.Gender.female;
        } else {
            sex = KidsInfoFragment.Gender.male;
        }


        final int healthRating = (int) health.getRating();
        final int scienceRating = (int) science.getRating();
        final int societyRating = (int) society.getRating();
        final int languageRating = (int) language.getRating();
        final int artRating = (int) art.getRating();


        request.setNickname(nickName);
        request.setBirthday(birthDay);
        request.setGender(sex);

        request.setPreferences(new SevenValue());

        FiveValue fiveValue = new FiveValue();
        fiveValue.setHealth(healthRating);
        fiveValue.setScience(scienceRating);
        fiveValue.setSocial(societyRating);
        fiveValue.setLanguage(languageRating);
        fiveValue.setArt(artRating);

        request.setRates(fiveValue);


        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProfileResponse resp = GsonUtil.parse(response,
                        ProfileResponse.class);
                if (resp.isSucess()) {

                    ProfileItem profile = ((KidsMindApplication) mAct.getApplication())
                            .getProfile();

                    profile.setNickname(nickName);
                    profile.setBirthday(birthDay);
                    profile.setGender(sex);


                    FiveValue fiveValue = new FiveValue();
                    fiveValue.setHealth(healthRating);
                    fiveValue.setScience(scienceRating);
                    fiveValue.setSocial(societyRating);
                    fiveValue.setLanguage(languageRating);
                    fiveValue.setArt(artRating);
                    profile.setRates(fiveValue);

                    Toast.makeText(mAct, "更新宝宝信息成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }

            }

        });
    }

    /**
     * 保存修改
     */
    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        int id = view.getId();
        if (id == R.id.btn_save) {
            String nickName = mNickName.getText().toString();

            if (StringUtils.isEmpty(nickName)) {
                Toast.makeText(mAct, "宝宝名字不能为空!", Toast.LENGTH_SHORT).show();
            } else {

                updateProfile();
                BaiduUtils.onEvent(mAct, OnEventId.BABY_INFO_SAVE, getString(R.string.baby_info_save));
            }

        } else if (id == R.id.tv_birday) {

            BirthdayPickerDialog dialog;

            // 如果设备是电视 且 sdk 小于 andriod 4.3
            if (ScreenUtils.isTv(mAct) && Build.VERSION.SDK_INT < 18) {
                dialog = new BirthdayPickerDialog(mAct, R.style.magicbox_pro_theme);
            } else {
                dialog = new BirthdayPickerDialog(mAct);
            }
            dialog.show();

            dialog.setDateLinster(new BirthdayPickerDialog.OnclickmDateLinster() {
                @Override
                public void choseDate(String date) {
                    mBirday.setText(date);
                    BaiduUtils.onEvent(mAct, OnEventId.CLICK_BABY_BIRTHDAY, getString(R.string.click_baby_birthday));
                }
            });
        }

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating,
                                boolean fromUser) {
        // TODO Auto-generated method stub
        if (fromUser) {
            BaiduUtils.onEvent(mAct, OnEventId.BABY_SEVEN_ABILITY, getString(R.string.baby_seven_ability) + ratingBar.getTag());
        }

    }

}
