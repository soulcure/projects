package com.taku.safe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.taku.safe.login.AppealFragment;
import com.taku.safe.login.BindFragment;
import com.taku.safe.login.ChangeDeviceFragment;
import com.taku.safe.login.ForgetFragment;
import com.taku.safe.login.LoginFragment;
import com.taku.safe.login.RegisterFragment;
import com.umeng.analytics.MobclickAgent;


public class AccountInfoActivity extends BasePermissionActivity {

    public static final int AUTHORIZE_FINISH = 2;//从授权页面返回

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        Bundle bundle = new Bundle();

        if (TextUtils.isEmpty(mTakuApp.getToken()) || mTakuApp.isExpire()) {
            skipToFragment(LoginFragment.TAG, bundle);
        } else {
            if (!mTakuApp.isBind()) {
                skipToFragment(BindFragment.TAG, null);
            }
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

    public boolean skipToFragment(String tag, Bundle bundle) {
        Fragment frag = null;
        if (tag.equals(RegisterFragment.TAG)) {
            frag = new RegisterFragment();
        } else if (tag.equals(LoginFragment.TAG)) {
            frag = new LoginFragment();
        } else if (tag.equals(BindFragment.TAG)) {
            frag = new BindFragment();
        } else if (tag.equals(ForgetFragment.TAG)) {
            frag = new ForgetFragment();
        } else if (tag.equals(AppealFragment.TAG)) {
            frag = new AppealFragment();
        } else if (tag.equals(ChangeDeviceFragment.TAG)) {
            frag = new ChangeDeviceFragment();
        }


        if (frag != null) {
            if (bundle != null) {
                frag.setArguments(bundle);
            }
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                ft.replace(R.id.content, frag, tag);
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.commitAllowingStateLoss();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag(RegisterFragment.TAG) != null
                || fragmentManager.findFragmentByTag(ForgetFragment.TAG) != null) {
            Bundle bundle = new Bundle();
            skipToFragment(LoginFragment.TAG, bundle);
        } else if (fragmentManager.findFragmentByTag(BindFragment.TAG) != null) {
            mTakuApp.setToken("");
            mTakuApp.setPhoneNum("");
            mTakuApp.setBind(false);
            mTakuApp.setExpire(0);
            mTakuApp.setTs(0);
            skipToFragment(LoginFragment.TAG, null);
        } else if (fragmentManager.findFragmentByTag(AppealFragment.TAG) != null) {
            skipToFragment(ForgetFragment.TAG, null);
        } else {
            finish();
        }
    }

}
