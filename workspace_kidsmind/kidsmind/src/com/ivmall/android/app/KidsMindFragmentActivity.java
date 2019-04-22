package com.ivmall.android.app;

import com.ivmall.android.app.fragment.AutoLoginFragment;
import com.ivmall.android.app.fragment.KidsInfoFragment;
import com.ivmall.android.app.impl.OnArticleSelectedListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.ScreenUtils;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class KidsMindFragmentActivity extends AppCompatActivity implements OnArticleSelectedListener {

    private static final String TAG = KidsMindFragmentActivity.class.getSimpleName();

    public static final int REQUEST_MODE_A = 2;
    public static final int RESULT_MODEL_A_BACK = 3;


    private static final int UITHREAD_INIT_COMPLETE = 0;
    private static final int HANDLERTHREAD_INIT_CONFIG_START = 1;

    private UIHandler mUIHandler;
    private ProcessHandler mProcessHandler;
    public FragmentManager fragmentManager = null; // fragment 管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ScreenUtils.isPhone(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_kids_mind);
        fragmentManager = getSupportFragmentManager();// fragment管理

        Bundle bundle = getIntent().getExtras();
        skipToFragment(AutoLoginFragment.TAG, bundle);

        initHandler();

        BaiduUtils.init(this);

        float sw = ScreenUtils.getSmallWidthDPI(this);
        float wd = ScreenUtils.getWidthDPI(this);

        android.util.Log.v(TAG, "this device res use sw-xxdp<=" + sw + "dp");
        android.util.Log.v(TAG, "this device res use w-xxdp<=" + wd + "dp");
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!AppUtils.isNetworkConnected(this)) {
            AppUtils.showNetworkError(this);
        }
    }

    /**
     * 主线程处理handler
     *
     * @author Administrator
     */
    private static class UIHandler extends Handler {
        private WeakReference<KidsMindFragmentActivity> mTarget;

        UIHandler(KidsMindFragmentActivity target) {
            mTarget = new WeakReference<KidsMindFragmentActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            KidsMindFragmentActivity activity = mTarget.get();
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
            mUIHandler = new UIHandler(this);
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
     * 跳转到指定fragment 若当前不在跳转中则可进行
     * 跳转，若当前已在跳转中则新跳转调用不成功
     *
     * @param tag    Fragment TAG
     * @param bundle 数据
     */
    @Override
    public boolean skipToFragment(String tag, Bundle bundle) {
        Fragment frag = null;
        if (tag.equals(KidsInfoFragment.TAG)) {
            frag = new KidsInfoFragment();
        } else if (tag.equals(AutoLoginFragment.TAG)) {
            frag = new AutoLoginFragment();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MODE_A && resultCode == RESULT_MODEL_A_BACK) {
            //skipToFragment(PlayListFragment.TAG, null);
            /*Intent intent = new Intent(this,
                    PlayListFragmentActivity.class);
            startActivity(intent);*/
            finish();
        }
    }

}
