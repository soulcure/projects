package com.ivmall.android.app.parent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.FileAsyncTaskDownload;
import com.ivmall.android.app.views.DownProgressView;


public class AboutFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = AboutFragment.class.getSimpleName();

    /**
     * 标志是否正在进行下载小尼克
     */
    private boolean ifDwoningNj = false;
    /**
     * 标志是否正在进行下载纷纷英语
     */
    private boolean ifDwoningFnf = false;

    private Activity mAct;
    private DownProgressView njView;
    private DownProgressView fnfView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.about_us, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView textView = (TextView) view.findViewById(R.id.version);
        String ver = getResources().getText(R.string.version_number)
                + AppUtils.getVersion(mAct);
        textView.setText(ver);//设置当前版本号

        Button btn_downNj = (Button) view.findViewById(R.id.btn_downNj);
        Button btn_downFnf = (Button) view.findViewById(R.id.btn_downFnf);
        btn_downNj.setOnClickListener(this);
        btn_downFnf.setOnClickListener(this);

        njView = (DownProgressView) view.findViewById(R.id.image_icon_dora);
        fnfView = (DownProgressView) view.findViewById(R.id.image_icon_en);
    }


    @Override
    public void onResume() {
        super.onResume();
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


    @Override
    public void onClick(View v) {
        String url = "";
        switch (v.getId()) {
            case R.id.btn_downNj:
                url = AppConfig.DOWN_MNJCHAIN;
                if (!ifDwoningNj) {
                    ifDwoningNj = true;
                    downLoadApk(url, njView);
                }
                break;
            case R.id.btn_downFnf:
                url = AppConfig.DOWN_FNF;
                if (!ifDwoningFnf) {
                    ifDwoningFnf = true;
                    downLoadApk(url, fnfView);
                }
                break;
        }
    }

    /**
     * 下载apk
     */
    private void downLoadApk(String url, final DownProgressView downProgressView) {
        if (AppUtils.isNetworkConnected(mAct)) {
            FileAsyncTaskDownload.DownLoadingListener listener = new FileAsyncTaskDownload.DownLoadingListener() {
                @Override
                public void onProgress(int rate) {
                    downProgressView.updateProgress((int) ((rate / 100.0f) * 360.0f));
                }

                @Override
                public void downloadFail(String err) {
                    unLockDown();
                }

                @Override
                public void downloadSuccess(String path) {
                    unLockDown();
                    downProgressView.clearProgress();
                    AppUtils.installApk(mAct, path);
                }
            };

            new FileAsyncTaskDownload(listener).execute(url);

        } else {
            AppUtils.showNetworkError(mAct);
        }
    }

    /**
     * 下载解锁
     */
    private void unLockDown() {
        if (ifDwoningNj) {
            ifDwoningNj = false;
        }
        if (ifDwoningFnf) {
            ifDwoningFnf = false;
        }
    }
}
