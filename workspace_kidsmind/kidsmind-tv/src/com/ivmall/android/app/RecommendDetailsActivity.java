package com.ivmall.android.app;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.BirthdayPickerDialog;
import com.ivmall.android.app.entity.FiveValue;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.entity.ProfileRequest;
import com.ivmall.android.app.entity.ProfileResponse;
import com.ivmall.android.app.entity.RecommendChildItem;
import com.ivmall.android.app.entity.SevenValue;
import com.ivmall.android.app.entity.SmartRecommendChildResponse;
import com.ivmall.android.app.entity.SmartRecommendRequest;
import com.ivmall.android.app.fragment.KidsInfoFragment;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.player.SmartPlayingActivity;
import com.ivmall.android.app.service.MediaPlayerService;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.ListUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.BabyAblityStarLayout;
import com.ivmall.android.app.views.FoucsRadioButton;
import com.smit.android.ivmall.stb.R;

import java.util.List;

/**
 * Created by smit on 2015/11/5.
 */
public class RecommendDetailsActivity extends Activity implements View.OnClickListener {

    private EditText eBabyName;
    private TextView tBrithday;
    private ImageView iBabyHead;
    private RadioGroup radioGroup;
    private FoucsRadioButton radioMale;
    private FoucsRadioButton radioFemale;
    private Button btnSave;
    private BabyAblityStarLayout rHealth, rScience, rSociety, rLanguage, rArt;

    private LinearLayout episodeLayout;

    private TextView episode;
    private TextView infoText;
    private Context mContext;

    private int mProfileId;
    public static final String ISFIRSTCREATE = "isFirstCreate"; // 是否第一次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.recommend_details_activity);
        mContext = this;
        initView();
        ProfileItem list = ((KidsMindApplication) getApplication()).getProfile();
        if (list != null) {
            initData(list); // 初始化数据
        } else {
            ((KidsMindApplication) getApplication()).reqProfile(new OnReqProfileResult());
        }
        mProfileId = ((KidsMindApplication) getApplication()).getProfileId();
        reqSmartRecommend();

        // 语言提示（第一次进入）
        boolean isFirstCreate = AppUtils.getBooleanSharedPreferences(this,
                ISFIRSTCREATE, false);

        if (!isFirstCreate) {
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.putExtra("media", MediaPlayerService.BABYINFO);
            this.startService(intent);
            AppUtils.setBooleanSharedPreferences(this, ISFIRSTCREATE, true);
        }

        ((KidsMindApplication) getApplication()).addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((KidsMindApplication) getApplication()).finishActivity(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        eBabyName = (EditText) findViewById(R.id.re_baby_name_edit);
        tBrithday = (TextView) findViewById(R.id.re_baby_birday_text);
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
        rHealth = (BabyAblityStarLayout) findViewById(R.id.re_health);
        rScience = (BabyAblityStarLayout) findViewById(R.id.re_science);
        rSociety = (BabyAblityStarLayout) findViewById(R.id.re_society);
        rLanguage = (BabyAblityStarLayout) findViewById(R.id.re_language);
        rArt = (BabyAblityStarLayout) findViewById(R.id.re_art);

        episodeLayout = (LinearLayout) findViewById(R.id.episode_view);

        infoText = (TextView) findViewById(R.id.recommend_info_text);
        episode = (TextView) findViewById(R.id.recommend_episode);
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
                compareTo(KidsInfoFragment.Gender.male) == 0) {
            iBabyHead.setImageResource(R.drawable.head_boy);
            radioMale.setChecked(true);
        } else {
            iBabyHead.setImageResource(R.drawable.head_girl);
            radioFemale.setChecked(true);
        }
        rHealth.setStarSize(list.getRates().getHealth());
        rScience.setStarSize(list.getRates().getScience());
        rSociety.setStarSize(list.getRates().getSocial());
        rLanguage.setStarSize(list.getRates().getLanguage());
        rArt.setStarSize(list.getRates().getArt());
    }

    /**
     * 加载智能推荐图片和介绍
     */
    private void setImgInfo(List<RecommendChildItem> list) {
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < list.size(); i++) {
            final RecommendChildItem item = list.get(i);
            final View view = inflater.inflate(R.layout.recommend_item, episodeLayout, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ImageView episodeImg = (ImageView) view.findViewById(R.id.recommend_image);
            TextView titleText = (TextView) view.findViewById(R.id.recommend_title_text);

            params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.recommend_item_padding);
            episodeLayout.addView(view, params);

            if (i == 0) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.requestFocus();
                    }
                }, 1000);
            }

            Glide.with(mContext)
                    .load(item.getImgUrl())
                    .centerCrop()
                    .placeholder(R.drawable.cartoon_icon_default)
                    .error(R.drawable.cartoon_icon_default)
                    .into(episodeImg);

            titleText.setText(item.getEpisodeName());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SmartPlayingActivity.class);
                    intent.putExtra("EpisodeId", item.getEpisodeId());
                    intent.putExtra("EpisodeName", item.getEpisodeName());
                    startActivity(intent);
                    BaiduUtils.onEvent(mContext, OnEventId.SMART_PLAY_SESSION, getString(R.string.smart_play_session));
                }
            });

            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                                new float[]{1.0F, 1.05F}).setDuration(200);
                        ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                                new float[]{1.0F, 1.05F}).setDuration(200);
                        AnimatorSet scaleAnimator = new AnimatorSet();
                        scaleAnimator.playTogether(new Animator[]{animX, animY});
                        scaleAnimator.start();

                        infoText.setText(item.getEpisodeDesc());
                        episode.setText("《" + item.getSerieName() + "》");
                    } else {
                        ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                                new float[]{1.05F, 1.0F}).setDuration(200);
                        ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                                new float[]{1.05F, 1.0F}).setDuration(200);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{animX, animY}); //设置两个动画一起执行
                        animatorSet.start();
                    }
                }
            });
        }
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
                    BaiduUtils.onEvent(this, OnEventId.BABY_INFO_SAVE, getString(R.string.baby_info_save));
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
                        BaiduUtils.onEvent(mContext, OnEventId.CLICK_BABY_BIRTHDAY, getString(R.string.click_baby_birthday));
                    }
                });
                break;
        }
    }

    private void updateProfile() {
        String url = AppConfig.UPDATE_PROFILE;
        ProfileRequest request = new ProfileRequest();
        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        int profileId = ((KidsMindApplication) getApplication()).getProfileId();
        request.setProfileId(profileId + "");

        final String babyName = eBabyName.getText().toString();
        final String birthDay = tBrithday.getText().toString();
        final KidsInfoFragment.Gender sex;
        if (radioMale.isChecked()) {
            sex = KidsInfoFragment.Gender.male;
        } else {
            sex = KidsInfoFragment.Gender.female;
        }
        final int healthRating = rHealth.getStarSize();
        final int scienceRating = rScience.getStarSize();
        final int societyRating = rSociety.getStarSize();
        final int languageRating = rLanguage.getStarSize();
        final int artRating = rArt.getStarSize();

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
                    ProfileItem profile = ((KidsMindApplication) getApplication())
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


                    reqSmartRecommend();
                    Toast.makeText(mContext, "更新宝宝信息成功", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }


    private class OnReqProfileResult implements OnSucessListener {

        @Override
        public void sucess() {
            ProfileItem list = ((KidsMindApplication) getApplication()).getProfile();
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

    /**
     * 获取推荐内容
     */
    private void reqSmartRecommend() {
        String url = AppConfig.SMART_RECOMMEND_EPISODE;
        SmartRecommendRequest request = new SmartRecommendRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        request.setCount(3);
        request.setProfileId(mProfileId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                SmartRecommendChildResponse resp = GsonUtil.parse(response,
                        SmartRecommendChildResponse.class);
                if (resp.isSucess()) {
                    List<RecommendChildItem> list = resp.getData().getRecommendation();
                    if (!ListUtils.isEmpty(list)) {
                        episodeLayout.removeAllViews();
                        if (this != null) {
                            setImgInfo(list);
                        }
                    }

                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            if (rHealth.hasFocus()) {
                return rHealth.onKeyDown(keyCode, event);
            } else if (rScience.hasFocus()) {
                return rScience.onKeyDown(keyCode, event);
            } else if (rSociety.hasFocus()) {
                return rSociety.onKeyDown(keyCode, event);
            } else if (rLanguage.hasFocus()) {
                return rLanguage.onKeyDown(keyCode, event);
            } else if (rArt.hasFocus()) {
                return rArt.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
