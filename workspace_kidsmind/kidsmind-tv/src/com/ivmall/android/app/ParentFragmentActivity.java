package com.ivmall.android.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.parent.AboutFragment;
import com.ivmall.android.app.parent.BugVipFragment;
import com.ivmall.android.app.parent.LoginFragment;
import com.ivmall.android.app.parent.OrderFragment;
import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.parent.RecordPlayFragment;
import com.ivmall.android.app.parent.ReportStudyFragment;
import com.ivmall.android.app.parent.ResponseFragment;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.smit.android.ivmall.stb.R;

public class ParentFragmentActivity extends FragmentActivity implements View.OnFocusChangeListener {

    private static final String TAG = ParentFragmentActivity.class.getSimpleName();

    private FragmentManager fragmentManager;
    private ImageButton btnBack;
    private RadioGroup radioGroupTab;
    private RadioButton rbtnVip;
    //private RadioButton rbtnBabyInfo;
    private RadioButton rbtnPlaySetting;
    private RadioButton rbtnPlayRecord;
    private RadioButton rbtnLearnReport;
    private RadioButton rbtnResponse;
    private RadioButton rbtnAboutUs;
    private RadioButton rbtnLogin;
    private RadioButton rbtnOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_parents);
        fragmentManager = getSupportFragmentManager();
        initView();
        setOnListener();

        ((KidsMindApplication) getApplication()).addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((KidsMindApplication) getApplication()).finishActivity(this);
    }

    /**
     * 跳到购买页面
     */
    public void goToBuyVip() {
        rbtnVip.requestFocus();
        rbtnVip.setChecked(true);
    }

    public void setLoginText(boolean isLogin) {
        if (isLogin) {
            rbtnLogin.setText(R.string.login_out);
        } else {
            rbtnLogin.setText(R.string.login_in);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //for TV bug
        rbtnVip.setOnFocusChangeListener(null);
        //rbtnBabyInfo.setOnFocusChangeListener(null);
        rbtnPlayRecord.setOnFocusChangeListener(null);
        rbtnPlaySetting.setOnFocusChangeListener(null);
        rbtnLearnReport.setOnFocusChangeListener(null);
        rbtnResponse.setOnFocusChangeListener(null);
        rbtnAboutUs.setOnFocusChangeListener(null);
        rbtnLogin.setOnFocusChangeListener(null);
        rbtnOrder.setOnFocusChangeListener(null);
        finish();
    }

    /**
     * 初始化tabview
     */
    private void initView() {
        btnBack = (ImageButton) findViewById(R.id.btnBack);

        radioGroupTab = (RadioGroup) findViewById(R.id.main_radiogroup);

        rbtnLogin = (RadioButton) findViewById(R.id.tab_user_login);
        rbtnVip = (RadioButton) findViewById(R.id.tab_kt_vip);
        rbtnPlaySetting = (RadioButton) findViewById(R.id.tab_play_setting);
        rbtnPlayRecord = (RadioButton) findViewById(R.id.tab_play_record);
        //rbtnBabyInfo = (RadioButton) findViewById(R.id.tab_baby_message);
        rbtnLearnReport = (RadioButton) findViewById(R.id.tab_lean_result);
        rbtnResponse = (RadioButton) findViewById(R.id.tab_response);
        rbtnAboutUs = (RadioButton) findViewById(R.id.tab_about_us);
        rbtnOrder = (RadioButton) findViewById(R.id.tab_order);

        String promoter = ((KidsMindApplication) getApplication()).getPromoter();
        //小米渠道屏蔽“我要吐槽”
        if (promoter.equals(PaymentDialog.MITV_CHANNEL)) {
            rbtnResponse.setVisibility(View.GONE);
            rbtnOrder.setVisibility(View.GONE);
        }


        //强行到宝宝信息
        KidsMindApplication.LoginType type = ((KidsMindApplication) getApplication())
                .getLoginType();
        if (type == KidsMindApplication.LoginType.mobileLogin) {
            radioGroupTab.removeView(rbtnLogin);
            int count = radioGroupTab.getChildCount();
            rbtnLogin.setText(R.string.login_out);
            radioGroupTab.addView(rbtnLogin, count);
        }

        rbtnVip.requestFocus();
        rbtnVip.setChecked(true);
        fragmentSelection(R.id.tab_kt_vip);
    }

    /**
     * 设置按键监听
     */
    private void setOnListener() {

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rbtnVip.setOnFocusChangeListener(this);
        //rbtnBabyInfo.setOnFocusChangeListener(this);
        rbtnPlaySetting.setOnFocusChangeListener(this);
        rbtnPlayRecord.setOnFocusChangeListener(this);
        rbtnLearnReport.setOnFocusChangeListener(this);
        rbtnResponse.setOnFocusChangeListener(this);
        rbtnAboutUs.setOnFocusChangeListener(this);
        rbtnLogin.setOnFocusChangeListener(this);
        rbtnOrder.setOnFocusChangeListener(this);

        if (!ScreenUtils.isTv(this)) {
            radioGroupTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    fragmentSelection(checkedId);
                }
            });
        }

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (hasFocus) {
            ((RadioButton) v).setChecked(true);
            fragmentSelection(id);
        }
    }


    private void fragmentSelection(int id) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (id) {
            case R.id.tab_kt_vip:
                BugVipFragment bugVipFragment = new BugVipFragment();
                transaction.replace(R.id.frame_container, bugVipFragment);

                BaiduUtils.onEvent(getApplicationContext(), OnEventId.GOTO_VIP,
                        getString(R.string.goto_vip));
                break;
            case R.id.tab_play_setting:

                PlaySettingFragment playSettingFragment = new PlaySettingFragment();
                transaction.replace(R.id.frame_container, playSettingFragment);

                BaiduUtils.onEvent(getApplicationContext(), OnEventId.GOTO_PLAY_HISTORY,
                        getString(R.string.goto_play_setting));
                break;
            case R.id.tab_play_record:

                RecordPlayFragment recordPlayFragment = new RecordPlayFragment();
                transaction.replace(R.id.frame_container, recordPlayFragment);

                BaiduUtils.onEvent(getApplicationContext(), OnEventId.GOTO_PLAY_HISTORY,
                        getString(R.string.goto_play_history));
                break;
            case R.id.tab_lean_result:

                ReportStudyFragment reportStudyFragment = new ReportStudyFragment();
                transaction.replace(R.id.frame_container, reportStudyFragment);

                BaiduUtils.onEvent(getApplicationContext(), OnEventId.GOTO_STUDY_REPORT,
                        getString(R.string.goto_study_report));
                break;
            case R.id.tab_response:

                ResponseFragment responseFragment = new ResponseFragment();
                transaction.replace(R.id.frame_container, responseFragment);

                BaiduUtils.onEvent(getApplicationContext(), OnEventId.GOTO_USER_RESPONSE,
                        getString(R.string.goto_user_response));
                break;
            case R.id.tab_about_us:

                AboutFragment aboutFragment = new AboutFragment();
                transaction.replace(R.id.frame_container, aboutFragment);

                BaiduUtils.onEvent(getApplicationContext(), OnEventId.GOTO_ABOUT_US,
                        getString(R.string.goto_about_us));
                break;
            case R.id.tab_user_login:

                LoginFragment loginFragment = new LoginFragment();
                transaction.replace(R.id.frame_container, loginFragment);

                BaiduUtils.onEvent(getApplicationContext(), OnEventId.GOTO_LOGIN,
                        getString(R.string.goto_login));
                break;
            case R.id.tab_order:
                OrderFragment orderFragment = new OrderFragment();
                transaction.replace(R.id.frame_container, orderFragment);
                break;
        }
        transaction.commit();
    }
}
