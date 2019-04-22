package com.taku.safe.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.dialog.BottomGenderDialog;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespUserInfo;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.TimeUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by colin on 2017/6/8.
 */

public class UserInfoActivity extends BasePermissionActivity implements View.OnClickListener,
        OnDateSetListener, BottomGenderDialog.SexSelectCallback {

    private static final int CHANGE_MOBILE = 1;

    private RoundedImageView imgHeard;
    private TextView tv_birthday;
    private TextView tv_gender;
    private TextView tv_phone;

    private int gender;
    private String birthday;
    private String avatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initTitle();
        initView();

        RespUserInfo info = mTakuApp.getUserInfo();
        if (info != null) {
            refreshUserInfo(info);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTakuApp.reqUserInfo(null);
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.user_info);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_phone = (TextView) findViewById(R.id.tv_phone);

        imgHeard = (RoundedImageView) findViewById(R.id.cropImageView);
        imgHeard.setOnClickListener(this);

        imgHeard = (RoundedImageView) findViewById(R.id.cropImageView);

        findViewById(R.id.ll_gender).setOnClickListener(this);
        findViewById(R.id.ll_birthday).setOnClickListener(this);
        findViewById(R.id.ll_phone).setOnClickListener(this);

    }


    /**
     * 刷新用户信息
     *
     * @param info
     */
    private void refreshUserInfo(RespUserInfo info) {
        gender = info.getGender();
        if (gender == 1) {
            tv_gender.setText("男");
            imgHeard.setImageResource(R.mipmap.ic_male);
        } else {
            tv_gender.setText("女");

        }

        avatar = info.getAvataUrl();
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(mContext)
                    .load(avatar)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imgHeard);
        } else {
            if (gender == 1) {
                imgHeard.setImageResource(R.mipmap.ic_male);
            } else {
                imgHeard.setImageResource(R.mipmap.ic_female);
            }
        }

        birthday = info.getBirthday();
        tv_birthday.setText(birthday);

        String phone = info.getPhoneNo();
        tv_phone.setText(phone);
    }


    /**
     * 更新用户信息
     *
     * @param gender
     * @param birthday
     * @param avatar
     */
    private void updateUserInfo(int gender, String birthday, String avatar) {
        String url = AppConfig.STUDENT_USERINFO_UPDATE;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        Map<String, Object> params = new HashMap<>();
        params.put("gender", gender);

        if (!TextUtils.isEmpty(birthday)) {
            params.put("birthday", birthday);
        }

        if (!TextUtils.isEmpty(avatar)) {
            File file = new File(avatar);
            if (file.exists()) {
                params.put("avatar", file);
            }
        }

        OkHttpConnector.httpPostMultipart(url, header, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    mTakuApp.reqUserInfo(null);
                }
            }
        });
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        birthday = TimeUtils.getDate(millseconds);
        tv_birthday.setText(birthday);
        updateUserInfo(gender, birthday, null);
    }


    @Override
    public void onMale() {
        gender = 1;
        tv_gender.setText("男");
        updateUserInfo(gender, birthday, null);
    }

    @Override
    public void onFemale() {
        gender = 2;
        tv_gender.setText("女");
        updateUserInfo(gender, birthday, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.cropImageView:
                startCrop();
                break;
            case R.id.ll_gender:
                BottomGenderDialog dialog = new BottomGenderDialog(this);
                dialog.setOnSexSelectListener(this);
                dialog.show();
                break;
            case R.id.ll_birthday:
                datePickerDialog();
                break;
            case R.id.ll_phone:
                choiceReBindDialog();
                break;
        }
    }


    private void choiceReBindDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.change_mobile_msg)
                .setPositiveButton(R.string.btn_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(mContext, ChangeMobileActivity.class), CHANGE_MOBILE);
                            }
                        })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }


    private void datePickerDialog() {
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24;
        long twentyYears = 20L * 365 * 1000 * 60 * 60 * 24;
        long seventyYears = 70L * 365 * 1000 * 60 * 60 * 24;
        long curTime = System.currentTimeMillis();
        TimePickerDialog dialog = new TimePickerDialog.Builder()
                .setType(Type.YEAR_MONTH_DAY)
                .setCallBack(this)
                .setWheelItemTextSize(14)
                .setTitleStringId("请选择生日")
                .setCurrentMillseconds(curTime - twentyYears)
                .setMinMillseconds(curTime - seventyYears)
                .setMaxMillseconds(curTime - tenYears)
                .setThemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setWheelItemTextNormalColor(ContextCompat.getColor(this, R.color.color_gray))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(this, R.color.color_blue))
                .build();
        dialog.show(getSupportFragmentManager(), "year_month_day");

    }


    private void startCrop() {
        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                Glide.with(this)
                        .load(uri)
                        .apply(new RequestOptions()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imgHeard);

                String path = AppUtils.getPath(mContext, uri);
                updateUserInfo(gender, birthday, path);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        } else if (requestCode == CHANGE_MOBILE) {
            if (resultCode == RESULT_OK) {
                String newPhone = data.getStringExtra("new_phone");
                tv_phone.setText(newPhone);
            }

        }
    }
}
