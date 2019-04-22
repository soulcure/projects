package com.applidium.nickelodeon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.applidium.nickelodeon.fragment.AutoLoginFragment;
import com.applidium.nickelodeon.fragment.ForgetPasswordFragment;
import com.applidium.nickelodeon.fragment.HasRegisterFragment;
import com.applidium.nickelodeon.fragment.KidsInfoFragment;
import com.applidium.nickelodeon.fragment.LoginIvmallFragment;
import com.applidium.nickelodeon.fragment.MetroFragment;
import com.applidium.nickelodeon.fragment.RegisterFragment;
import com.applidium.nickelodeon.impl.OnArticleSelectedListener;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

public class NickelFragmentActivity extends FragmentActivity implements OnArticleSelectedListener {

    private static final String TAG = NickelFragmentActivity.class.getSimpleName();

    public static final int REQUEST_MODE_A = 0;
    public static final int RESULT_MODEL_A_BACK = 1;

    public static final int REQUEST_VIP = 2;
    public static final int RESULT_VIP_BACK = 3;

    private static final int UITHREAD_INIT_COMPLETE = 0;
    private static final int HANDLERTHREAD_INIT_CONFIG_START = 1;

    private UIHandler mUIHandler;
    private ProcessHandler mProcessHandler;
    public FragmentManager fragmentManager = null; // fragment 管理

    /**
     * 重复按键时间设为2s
     */
    private static final int showTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_mind);
        fragmentManager = getSupportFragmentManager();// fragment管理

        Bundle bundle = getIntent().getExtras();
        String token = ((MNJApplication) getApplication()).getToken();
        if (StringUtils.isEmpty(token)) {
            skipToFragment(LoginIvmallFragment.TAG, bundle);
        } else {
            skipToFragment(AutoLoginFragment.TAG, bundle);
        }

        initHandler();

        BaiduUtils.init(this);

        ScreenUtils.printfDeviceInfo(this);

        //语言提示
        MediaPlayerService.playSound(this, MediaPlayerService.WELCOME);
    }


    @Override
    public void onResume() {
        super.onResume();

        MiStatInterface.recordPageStart(this, TAG);

        if (!AppUtils.isNetworkConnected(this)) {
            AppUtils.showNetworkError(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    /**
     * 主线程处理handler
     *
     * @author Administrator
     */
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UITHREAD_INIT_COMPLETE:
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 子线程handler,looper
     *
     * @author Administrator
     */
    private class ProcessHandler extends Handler {
        private Handler mHandler;

        public ProcessHandler(Looper looper, Handler restoreHandler) {
            super(looper);
            this.mHandler = restoreHandler;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLERTHREAD_INIT_CONFIG_START:
                    mHandler.sendEmptyMessage(UITHREAD_INIT_COMPLETE); // 通知UI线程
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 线程初始化
     */
    private void initHandler() {
        if (mUIHandler == null) {
            mUIHandler = new UIHandler();
        }
        if (mProcessHandler == null) {
            HandlerThread handlerThread = new HandlerThread(
                    "handler looper Thread");
            handlerThread.start();
            mProcessHandler = new ProcessHandler(handlerThread.getLooper(),
                    mUIHandler);
        }
    }


    /**
     * 跳转到指定fragment 若当前不在跳转中则可进行跳转，若当前已在跳转中则新跳转调用不成功
     *
     * @param tag
     */
    @Override
    public boolean skipToFragment(String tag, Bundle bundle) {
        Fragment frag = null;
        if (tag.equals(RegisterFragment.TAG)) {
            frag = new RegisterFragment();
        } else if (tag.equals(KidsInfoFragment.TAG)) {
            frag = new KidsInfoFragment();
        } else if (tag.equals(AutoLoginFragment.TAG)) {
            frag = new AutoLoginFragment();
        } else if (tag.equals(MetroFragment.TAG)) {
            frag = new MetroFragment();
        } else if (tag.equals(ForgetPasswordFragment.TAG)) {
            frag = new ForgetPasswordFragment();
        } else if (tag.equals(LoginIvmallFragment.TAG)) {
            frag = new LoginIvmallFragment();
        } else if (tag.equals(HasRegisterFragment.TAG)) {
            frag = new HasRegisterFragment();
        }


        if (bundle != null) {
            frag.setArguments(bundle);
        }

        if (frag != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (fragmentManager.findFragmentByTag(tag) == null) {
                ft.replace(R.id.fragment, frag, tag);
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.commitAllowingStateLoss();
            }

            return true;
        }
        return false;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MODE_A && resultCode == RESULT_MODEL_A_BACK) {
            skipToFragment(MetroFragment.TAG, null);
            /*Intent intent = new Intent(this,
                    PlayListFragmentActivity.class);
            startActivity(intent);*/
        } else if (requestCode == REQUEST_VIP && resultCode == RESULT_VIP_BACK) {
            Bundle bundle = new Bundle();
            String mobileNum = ((MNJApplication) getApplication()).getMoblieNum();
            String userName = ((MNJApplication) getApplication()).getUserName();

            bundle.putString(LoginIvmallFragment.MOBILE_NUM, mobileNum);
            bundle.putString(LoginIvmallFragment.USER_NAME, userName);

            skipToFragment(LoginIvmallFragment.TAG, bundle);
        }
    }


    @Override
    public void onBackPressed() {
        if (AppUtils.isRepeatClick(showTime)) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());//for bug 天猫魔盒退出后 application不重新创建
        } else {
            Toast.makeText(this, R.string.quit_msg, Toast.LENGTH_SHORT).show();
        }
    }


}
