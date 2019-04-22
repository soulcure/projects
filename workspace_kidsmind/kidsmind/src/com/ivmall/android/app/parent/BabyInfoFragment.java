package com.ivmall.android.app.parent;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.entity.FiveValue;
import com.ivmall.android.app.impl.OnUpPrefileListener;
import com.ivmall.android.app.uitls.GlideCircleTransform;
import com.ivmall.android.app.uitls.OOSUtils;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.entity.ProfileRequest;
import com.ivmall.android.app.entity.ProfileResponse;
import com.ivmall.android.app.entity.SevenValue;
import com.ivmall.android.app.fragment.KidsInfoFragment;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.service.MediaPlayerService;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.uitls.UpLoadImage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class BabyInfoFragment extends Fragment implements OnClickListener, OnRatingBarChangeListener,
        DatePickerDialog.OnDateSetListener, View.OnFocusChangeListener {

    public static final String TAG = BabyInfoFragment.class.getSimpleName();

    public static final String ISFIRSTCREATE = "isFirstCreate"; // 是否第一次进入

    private Activity mAct;

    private EditText mNickName;
    private EditText mBirday;

    private AppCompatRatingBar health;
    private AppCompatRatingBar science;
    private AppCompatRatingBar society;
    private AppCompatRatingBar language;
    private AppCompatRatingBar art;

    private ImageView imgHead;
    private TextView tvHead;

    private RadioButton boyRadioBtn, girlRadioBtn;


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

        // 语言提示（第一次进入）
        boolean isFirstCreate = AppUtils.getBooleanSharedPreferences(mAct,
                ISFIRSTCREATE, false);

        if (!isFirstCreate) {
            Intent intent = new Intent(mAct, MediaPlayerService.class);
            intent.putExtra("media", MediaPlayerService.BABYINFO);
            mAct.startService(intent);
            AppUtils.setBooleanSharedPreferences(mAct, ISFIRSTCREATE, true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.babyinfo_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        view.findViewById(R.id.btn_save).setOnClickListener(this);

        health = (AppCompatRatingBar) view.findViewById(R.id.health);
        health.setOnRatingBarChangeListener(this);

        science = (AppCompatRatingBar) view.findViewById(R.id.science);
        science.setOnRatingBarChangeListener(this);

        society = (AppCompatRatingBar) view.findViewById(R.id.society);
        society.setOnRatingBarChangeListener(this);

        language = (AppCompatRatingBar) view.findViewById(R.id.language);
        language.setOnRatingBarChangeListener(this);

        art = (AppCompatRatingBar) view.findViewById(R.id.art);
        art.setOnRatingBarChangeListener(this);


        mNickName = (EditText) view.findViewById(R.id.et_name);

        boyRadioBtn = (RadioButton) view.findViewById(R.id.rd_male);
        girlRadioBtn = (RadioButton) view.findViewById(R.id.rd_female);

        mBirday = (EditText) view.findViewById(R.id.et_birday);
        mBirday.setOnFocusChangeListener(this);
        mBirday.setOnClickListener(this);
        mBirday.setKeyListener(null);

        view.findViewById(R.id.rel_head).setOnClickListener(this);
        imgHead = (ImageView) view.findViewById(R.id.img_head);
        tvHead = (TextView) view.findViewById(R.id.tv_head);
        ProfileItem profileItem = ((KidsMindApplication) getActivity().getApplication()).getProfile();
        String imgUrl = profileItem.getImgUrl();
        if (!StringUtils.isEmpty(imgUrl)) {
            tvHead.setText(R.string.babyinfo_update_head);
            /*Glide.with(getActivity())
                    .load(imgUrl)
                    .centerCrop()
                    .bitmapTransform(new GlideCircleTransform(getActivity())) //设置图片圆角
                    .placeholder(R.drawable.person_icon_add_head)  //占位图片
                    .error(R.drawable.person_icon_add_head)        //下载失败
                    .into(imgHead);*/
        }

    }


    private class OnUpProfileResult implements OnUpPrefileListener {

        @Override
        public void sucess(String path) {
            tvHead.setText("");
            Glide.with(getActivity())
                    .load(path)
                    .centerCrop()
                    .bitmapTransform(new GlideCircleTransform(getActivity())) //设置图片圆角
                    .placeholder(R.drawable.icon_login_image)  //占位图片
                    .error(R.drawable.icon_login_image)        //下载失败
                    .into(imgHead);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        ProfileItem list = ((KidsMindApplication) mAct.getApplication()).getProfile();
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
        View view = mAct.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = ((InputMethodManager) mAct.getSystemService(Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        mAct.stopService(new Intent(mAct, MediaPlayerService.class));

    }


    /**
     * 初始化数据
     */
    private void initData(ProfileItem list) {
        mNickName.setText(list.getNickname());
        mBirday.setText(list.getBirthday());
        if (list.getGender().compareTo(KidsInfoFragment.Gender.male) == 0) {
            //babyHead.setImageResource(R.drawable.head_boy);
            boyRadioBtn.setChecked(true);
        } else {
            // babyHead.setImageResource(R.drawable.head_girl);
            girlRadioBtn.setChecked(true);
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
            ProfileItem list = ((KidsMindApplication) mAct.getApplication()).getProfile();
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
        ((BaseActivity) mAct).startProgress();
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
        if (boyRadioBtn.isChecked()) {
            sex = KidsInfoFragment.Gender.male;
        } else {
            sex = KidsInfoFragment.Gender.female;
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
                    ((BaseActivity) mAct).stopProgress();
                } else {
                    Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    ((BaseActivity) mAct).stopProgress();
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

        } else if (id == R.id.et_birday) {
            showBirthdayPicker();
        }
        if (id == R.id.rel_head) {

            showPictureDialog(getActivity());
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.et_birday) {
            if (hasFocus) {
                showBirthdayPicker();
            }
        }
    }

    private void showBirthdayPicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                BabyInfoFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.vibrate(false);
        dpd.dismissOnPause(false);
        dpd.showYearPickerFirst(true);
        dpd.setAccentColor("#fabf01");
        dpd.show(mAct.getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        String date = i + "-" + ++i1 + "-" + i2;
        mBirday.setText(date);
        BaiduUtils.onEvent(mAct, OnEventId.CLICK_BABY_BIRTHDAY, getString(R.string.click_baby_birthday));
    }


    private void pickImage(int position) {


        OOSUtils.OOSListener listener = new OOSUtils.OOSListener() {
            @Override
            public void onSuccess(String path) {
                ((KidsMindApplication) getActivity().getApplication()).updateProfile(path, new OnUpProfileResult());
            }

            @Override
            public void onFailure() {

            }
        };

        ((BaseActivity) getActivity()).pickImage(listener, position);
    }


    /**
     * 显示图片获取提示框
     *
     * @param context
     */
    private void showPictureDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.pic_title))
                .setMessage(context.getString(R.string.pic_content));

        builder.setPositiveButton(context.getString(R.string.camera),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        pickImage(UpLoadImage.FROM_CAMERA);
                        arg0.dismiss();

                    }
                });

        builder.setNegativeButton(context.getString(R.string.photo),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        pickImage(UpLoadImage.FROM_PHOTO);
                        arg0.dismiss();
                    }
                });
        builder.show();
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
