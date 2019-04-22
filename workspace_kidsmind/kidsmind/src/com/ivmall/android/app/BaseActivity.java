package com.ivmall.android.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ivmall.android.app.expand.MakeListFragment;
import com.ivmall.android.app.expand.PostTvFragment;
import com.ivmall.android.app.fragment.BbsWebFragment;
import com.ivmall.android.app.fragment.UserInfoFragment;
import com.ivmall.android.app.parent.AboutFragment;
import com.ivmall.android.app.parent.BabyInfoFragment;
import com.ivmall.android.app.parent.BugVipFragment;
import com.ivmall.android.app.parent.LoginFragment;
import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.parent.RecordPlayFragment;
import com.ivmall.android.app.parent.ResponseFragment;
import com.ivmall.android.app.parent.TvRoportFragment;
import com.ivmall.android.app.uitls.ImageUtils;
import com.ivmall.android.app.uitls.OOSUtils;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.UpLoadImage;

import java.io.File;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by koen on 2016/3/23.
 * 是所有简单窗口的基础activity
 */
public class BaseActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    // 定义传递过来的参数
    public static final int BUGVIP = 1;    //购买VIP
    public static final int LOGIN = 2;     //登录
    public static final int RECORD = 3;    //播放记录
    public static final int BABYINFO = 4;  //宝宝信息
    public static final int RESPONSE = 5;  //用户反馈
    public static final int ABOUT_US = 6;  //关于我们
    public static final int PLAY_SET = 7;  //设置播放时长
    public static final int PLAY_LIST = 8;  //定制节目列表
    public static final int TV_REPORT = 9;  //TV操作汇报
    public static final int MY_BBS = 10;  //我的帖子
    public static final int BBS_INFO = 11; //我的贴子详细页面
    public static final int USER_INFO = 12; //用户信息

    public static final String NAME = "name";
    public static final String WEBKEY = "web";

    private int currentIndex = 0;

    private UpLoadImage mUpLoadImage;

    private MaterialProgressBar progressBar;

    private View content_area;

    private int webNoteId = 0;

    public boolean isPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ScreenUtils.isPhone(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isPhone  = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isPhone = false;
        }

        setContentView(R.layout.base_activity);
        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        currentIndex = intent.getIntExtra(NAME, 0);
        webNoteId = intent.getIntExtra(WEBKEY, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (MaterialProgressBar) findViewById(R.id.progress_bar);
        content_area = findViewById(R.id.content_area);
        toolbar.setNavigationIcon(R.drawable.icon_back_btn);
        initTopBar(toolbar, currentIndex);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initFragment(currentIndex);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        switch (currentIndex) {
            case PLAY_LIST:
                getMenuInflater().inflate(R.menu.menu_main, menu);
                break;
            default:
                break;

        }

        return true;
    }*/


    /**
     * pick image and upload
     *
     * @param listener 监听回调
     */
    public void pickImage(OOSUtils.OOSListener listener, int position) {
        if (mUpLoadImage == null)
            mUpLoadImage = new UpLoadImage(this);
        mUpLoadImage.pickImage(listener, position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UpLoadImage.FROM_CAMERA
                && resultCode == Activity.RESULT_OK) {

            Uri uri = mUpLoadImage.getFileUri();
            mUpLoadImage.cutPictrue(uri);

        } else if (requestCode == UpLoadImage.FROM_PHOTO
                && resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();
            mUpLoadImage.cutPictrue(uri);

        } else if (requestCode == UpLoadImage.IMAGE_CUT
                && resultCode == Activity.RESULT_OK) {

            Bitmap bm = data.getParcelableExtra("data");
            if (bm == null) { // 用户剪裁操作取消
                return;
            }
            // post image
            File file = ImageUtils.saveBitmapToJpg(bm);
            if (file.exists())
                mUpLoadImage.repOosInfo(file.getPath());
        }
    }


    /**
     * 设置menu按键监听
     */
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_edit:

                    //break;
                case R.id.action_share:
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content_area);
                    if (fragment instanceof MakeListFragment) {
                        ((MakeListFragment) fragment).sentEpisodeList();
                    }
                    break;
            }

            return true;
        }
    };


    /**
     * 初始化标题
     *
     * @param toolbar 控件
     * @param index   索引
     */
    private void initTopBar(Toolbar toolbar, int index) {
        int id = 0;
        switch (index) {
            case 0:
                return;
            case BUGVIP:
                id = R.string.open_vip;
                break;
            case LOGIN:
                id = R.string.login;
                break;
            case RECORD:
                id = R.string.record;
                break;
            case BABYINFO:
                id = R.string.modify_baby_info;
                break;
            case RESPONSE:
                id = R.string.response;
                break;
            case ABOUT_US:
                id = R.string.p_about_us;
                break;
            case PLAY_SET:
                id = R.string.set_playback_duration;
                break;
            case PLAY_LIST:
                id = R.string.custom_program_listing;
                break;
            case TV_REPORT:
                id = R.string.tv_report;
                break;
            case MY_BBS:
                id = R.string.my_post;
                break;

            case BBS_INFO:
                id = R.string.my_post_detail;
                break;

            case USER_INFO:
                id = R.string.user_info;
                break;
        }
        if (id != 0)
            toolbar.setTitle(id);
    }


    /**
     * 初始化 fragment
     *
     * @param index 索引
     */
    private void initFragment(int index) {
        switch (index) {
            case BUGVIP:
                BugVipFragment bugVipFragment = new BugVipFragment();
                commintFragment(bugVipFragment);
                break;
            case LOGIN:
                LoginFragment loginFragment = new LoginFragment();
                commintFragment(loginFragment);
                break;
            case RECORD:
                RecordPlayFragment recordPlayFragment = new RecordPlayFragment();
                commintFragment(recordPlayFragment);
                break;
            case BABYINFO:
                BabyInfoFragment babyInfoFragment = new BabyInfoFragment();
                commintFragment(babyInfoFragment);
                break;
            case RESPONSE:
                ResponseFragment responseFragment = new ResponseFragment();
                commintFragment(responseFragment);
                break;
            case ABOUT_US:
                AboutFragment aboutFragment = new AboutFragment();
                commintFragment(aboutFragment);
                break;
            case PLAY_SET:
                PlaySettingFragment settingFragment = new PlaySettingFragment();
                commintFragment(settingFragment);
                break;
            case PLAY_LIST:
                PostTvFragment postTvFragment = new PostTvFragment();
                commintFragment(postTvFragment);
                break;
            case TV_REPORT:
                TvRoportFragment tvRoportFragment = new TvRoportFragment();
                commintFragment(tvRoportFragment);
                break;
            case MY_BBS:
                BBSFragments bbsFragment = new BBSFragments();
                commintFragment(bbsFragment);
                break;

            case BBS_INFO:
                BbsWebFragment bbsWebFragment = new BbsWebFragment();
                Bundle webBundle = new Bundle();
                webBundle.putInt(WEBKEY, webNoteId);
                bbsWebFragment.setArguments(webBundle);
                commintFragment(bbsWebFragment);
                break;

            case USER_INFO:
                UserInfoFragment userInfoFragment = new UserInfoFragment();
                commintFragment(userInfoFragment);
                break;
        }
    }

    /**
     * commit
     *
     * @param fragment 碎片
     */
    private void commintFragment(Fragment fragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.content_area, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    /*
     *控制progressBar
     */
    public void startProgress() {
        if (progressBar.getVisibility() == View.INVISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    public void stopProgress() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void showTips(int id) {
        Snackbar snack = Snackbar.make(content_area, id, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.primary));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snack.show();
    }

    public void showTips(String st) {
        Snackbar snack = Snackbar.make(content_area, st, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.primary));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        snack.show();
    }


}
