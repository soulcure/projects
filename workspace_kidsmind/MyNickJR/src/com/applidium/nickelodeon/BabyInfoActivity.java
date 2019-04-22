package com.applidium.nickelodeon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.views.FoucsRatingBar;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.dialog.BirthdayPickerDialog;
import com.applidium.nickelodeon.entity.FiveValue;
import com.applidium.nickelodeon.entity.ProfileItem;
import com.applidium.nickelodeon.entity.ProfileRequest;
import com.applidium.nickelodeon.entity.ProfileResponse;
import com.applidium.nickelodeon.entity.SevenValue;
import com.applidium.nickelodeon.fragment.KidsInfoFragment;
import com.applidium.nickelodeon.impl.OnSucessListener;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.OnEventId;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.FoucsRadioButton;
import com.applidium.nickelodeon.views.FoucsTextView;


/**
 * Created by smit on 2015/11/5.
 */
public class BabyInfoActivity extends Activity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private EditText eBabyName;
    private FoucsTextView tBrithday;
    private ImageView iBabyHead;
    private RadioGroup radioGroup;
    private FoucsRadioButton radioMale;
    private FoucsRadioButton radioFemale;
    private Button btnSave;
    private FoucsRatingBar rHealth, rScience, rSociety, rLanguage, rArt;

    private Context mContext;

    private int mProfileId;
    public static final String ISFIRSTCREATE = "isFirstCreate"; // 是否第一次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.baby_info_details_layout);
        mContext = this;
        initView();
        ProfileItem list = ((MNJApplication) getApplication()).getProfile();
        if (list != null) {
            initData(list); // 初始化数据
        } else {
            ((MNJApplication) getApplication()).reqProfile(new OnReqProfileResult());
        }
        mProfileId = ((MNJApplication) getApplication()).getProfileId();

        // 语言提示（第一次进入）
        boolean isFirstCreate = AppUtils.getBooleanSharedPreferences(this,
                ISFIRSTCREATE, false);

        if (!isFirstCreate) {
            MediaPlayerService.playSound(mContext, MediaPlayerService.BABYINFO);
            AppUtils.setBooleanSharedPreferences(this, ISFIRSTCREATE, true);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        eBabyName = (EditText) findViewById(R.id.re_baby_name_edit);
        tBrithday = (FoucsTextView) findViewById(R.id.re_baby_birday_text);
        tBrithday.setOnClickListener(this);

        iBabyHead = (ImageView) findViewById(R.id.recommend_baby_head);
        radioGroup = (RadioGroup) findViewById(R.id.sex_radiogroup);
        radioMale = (FoucsRadioButton) findViewById(R.id.sex_male);
        radioFemale = (FoucsRadioButton) findViewById(R.id.sex_female);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if (checkedId == radioMale.getId()) {
                    iBabyHead.setImageResource(R.drawable.head_boy);
                } else if (checkedId == radioFemale.getId()) {
                    iBabyHead.setImageResource(R.drawable.head_girl);
                }

            }
        });
        btnSave = (Button) findViewById(R.id.re_btn_save);
        btnSave.setOnClickListener(this);
        rHealth = (FoucsRatingBar) findViewById(R.id.re_health);
        rHealth.setOnRatingBarChangeListener(this);
        rScience = (FoucsRatingBar) findViewById(R.id.re_science);
        rScience.setOnRatingBarChangeListener(this);
        rSociety = (FoucsRatingBar) findViewById(R.id.re_society);
        rSociety.setOnRatingBarChangeListener(this);
        rLanguage = (FoucsRatingBar) findViewById(R.id.re_language);
        rLanguage.setOnRatingBarChangeListener(this);
        rArt = (FoucsRatingBar) findViewById(R.id.re_art);
        rArt.setOnRatingBarChangeListener(this);
        findViewById(R.id.btn_play_return).setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData(ProfileItem list) {
        String text = list.getNickname();
        eBabyName.setText(text);
        eBabyName.setSelection(text.length());

        tBrithday.setText(list.getBirthday());
        if (list.getGender().
                compareTo(KidsInfoFragment.Gender.M) == 0) {
            iBabyHead.setImageResource(R.drawable.head_boy);
            radioMale.setChecked(true);
        } else {
            iBabyHead.setImageResource(R.drawable.head_girl);
            radioFemale.setChecked(true);
        }
        rHealth.setRating(list.getRates().getHealth());
        rScience.setRating(list.getRates().getScience());
        rSociety.setRating(list.getRates().getSocial());
        rLanguage.setRating(list.getRates().getLanguage());
        rArt.setRating(list.getRates().getArt());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_btn_save:
                String babyName = eBabyName.getText().toString();
                if (StringUtils.isEmpty(babyName)) {
                    Toast.makeText(this, "宝宝名字不能为空!", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile();
                }
                break;

            case R.id.re_baby_birday_text:
                BirthdayPickerDialog dialog;
                // 如果是电视，且SDK小于 android 4.3
                if (ScreenUtils.isTv(this) && Build.VERSION.SDK_INT < 18) {
                    dialog = new BirthdayPickerDialog(this, R.style.magicbox_pro_theme);
                } else {
                    dialog = new BirthdayPickerDialog(this);
                }
                dialog.show();
                dialog.setDateLinster(new BirthdayPickerDialog.OnclickmDateLinster() {
                    @Override
                    public void choseDate(String date) {
                        tBrithday.setText(date);
                    }
                });
                break;
            case R.id.btn_play_return:
                finish();
                break;

        }
    }

    private void updateProfile() {
        String url = AppConfig.UPDATE_PROFILE;
        ProfileRequest request = new ProfileRequest();
        String token = ((MNJApplication) getApplication()).getToken();
        request.setToken(token);
        int profileId = ((MNJApplication) getApplication()).getProfileId();
        request.setProfileId(profileId + "");

        final String babyName = eBabyName.getText().toString();
        final String birthDay = tBrithday.getText().toString();
        final KidsInfoFragment.Gender sex;
        if (radioMale.isChecked()) {
            sex = KidsInfoFragment.Gender.M;
        } else {
            sex = KidsInfoFragment.Gender.F;
        }
        final int healthRating = (int) rHealth.getRating();
        final int scienceRating = (int) rScience.getRating();
        final int societyRating = (int) rSociety.getRating();
        final int languageRating = (int) rLanguage.getRating();
        final int artRating = (int) rArt.getRating();

        request.setNickname(babyName);
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
                ProfileResponse resp = GsonUtil.parse(response,
                        ProfileResponse.class);
                if (resp.isSucess()) {
                    //更新本地信息
                    ProfileItem profile = ((MNJApplication) getApplication())
                            .getProfile();

                    profile.setNickname(babyName);
                    profile.setBirthday(birthDay);
                    profile.setGender(sex);

                    FiveValue fiveValue = new FiveValue();
                    fiveValue.setHealth(healthRating);
                    fiveValue.setScience(scienceRating);
                    fiveValue.setSocial(societyRating);
                    fiveValue.setLanguage(languageRating);
                    fiveValue.setArt(artRating);
                    profile.setRates(fiveValue);
                    Toast.makeText(mContext, "更新宝宝信息成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        if (fromUser) {

        }
    }

    private class OnReqProfileResult implements OnSucessListener {

        @Override
        public void sucess() {
            ProfileItem list = ((MNJApplication) getApplication()).getProfile();
            if (list != null) {
                initData(list);
            }
        }

        @Override
        public void create() {
        }

        @Override
        public void fail() {
        }
    }

}
