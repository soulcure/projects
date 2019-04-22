package com.taku.safe;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.taku.safe.activity.GuideActivity;
import com.taku.safe.activity.SosHistoryActivity;
import com.taku.safe.activity.WelcomeTakuActivity;
import com.taku.safe.ui.Analysis.AnalysisFragment;
import com.taku.safe.ui.main.MainTeacherFragment;
import com.taku.safe.ui.mine.MineTeacherFragment;
import com.taku.safe.ui.practice.PracticeAnalysisFragment;
import com.taku.safe.update.UpdateAppManager;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.ScreenUtils;
import com.umeng.analytics.MobclickAgent;


/**
 * app first activity
 * Created by colin on 2017/5/10.
 */
public class TaKuTeacherActivity extends BasePermissionActivity {

    private static final String TAG = TaKuTeacherActivity.class.getSimpleName();

    public static final int LOGIN = 0;
    public static final int LOGIN_OUT = 1;
    public static final int START_WELCOME = 2;

    private FragmentManager fragmentManager;

    private TextView tv_title;
    private TextView tv_sos_history;

    private BottomNavigationViewEx navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taku_student);
        mContext = this;
        fragmentManager = getSupportFragmentManager();

        initTitle();
        initView();

        float sw = ScreenUtils.getSmallWidthDPI(this);
        Log.v(TAG, "SmallWidthDPI:" + sw);

        if (AppUtils.getBooleanSharedPreferences(mContext, "welcome", true)) {
            AppUtils.setBooleanSharedPreferences(mContext, "welcome", false);
            Intent intent = new Intent(mContext, GuideActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, WelcomeTakuActivity.class);
            startActivityForResult(intent, START_WELCOME);
        }
    }


    /**
     * 检查手机网络
     */
    private void checkNetwork() {
        boolean hasNet = AppUtils.isNetworkConnected(this);
        if (!hasNet) {
            new AlertDialog.Builder(this)
                    .setMessage("检查到您的设备无网络连接")
                    .setPositiveButton("设置",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkNetwork();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void initTitle() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.app_name);

        TextView tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.INVISIBLE);

        tv_sos_history = (TextView) findViewById(R.id.tv_confirm);
        tv_sos_history.setBackgroundResource(R.drawable.bg_blue_selector);
        tv_sos_history.setText(R.string.sos_history);
        tv_sos_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SosHistoryActivity.class));
            }
        });

    }


    private void initView() {
        int index = getIntent().getIntExtra("tab_index", 0);
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);

        Bundle bundle = new Bundle();


        if (TextUtils.isEmpty(mTakuApp.getToken())) {
            entryAccountActivity();
        } else {
            if (mTakuApp.isExpire()) {
                entryAccountActivity();
            } else {

                initNav(index);

            }
        }

    }

    private void tabIndex(int index) {
        navigation.inflateMenu(R.menu.navigation_teacher);
        navigation.setOnNavigationItemSelectedListener(new NavTeacherListener());

        navigation.enableShiftingMode(false); //取消切换动画
        navigation.enableItemShiftingMode(false); //取消文字
        navigation.enableAnimation(false);  //取消选中动画
        navigation.setCurrentItem(index);
    }


    private void entryAccountActivity() {
        startActivityForResult(new Intent(this, AccountInfoActivity.class), LOGIN);
    }


    private void initNav(int index) {
        mTakuApp.reqUserInfo(null);  //请求用户信息

        Menu bottomMenu = navigation.getMenu();
        if (bottomMenu.size() > 0) {
            bottomMenu.clear();
        }

        tabIndex(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LOGIN) {
                initNav(0);
            } else if (requestCode == LOGIN_OUT) {
                initNav(0);
                navigation.setSelectedItemId(R.id.navigation_home);//for setCurrentItem(index) bugs
            } else if (requestCode == START_WELCOME) {
                UpdateAppManager.instance().check(this, 1);
            }
        } else {
            if (requestCode == LOGIN || requestCode == LOGIN_OUT) {
                finish();
            } else {
                //do nothing
            }

        }

    }


    /********************** teacher api start **********************/


    /**
     * 老师端导航栏
     */
    private class NavTeacherListener implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    Fragment homeFragment = new MainTeacherFragment();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof MainTeacherFragment;
                    if (!isNeed) {
                        transaction.replace(R.id.content, homeFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.app_name);
                    tv_sos_history.setVisibility(View.VISIBLE);
                }
                return true;
                case R.id.navigation_practice: {
                    Fragment practiceFragment = new PracticeAnalysisFragment();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof PracticeAnalysisFragment;
                    if (!isNeed) {
                        transaction.replace(R.id.content, practiceFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.title_practice);
                    tv_sos_history.setVisibility(View.INVISIBLE);
                }
                return true;
                case R.id.navigation_analyse: {
                    Fragment tabFragment = new AnalysisFragment();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof AnalysisFragment;
                    if (!isNeed) {
                        transaction.replace(R.id.content, tabFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.title_analyse);
                    tv_sos_history.setVisibility(View.INVISIBLE);
                }

                return true;
                case R.id.navigation_mine: {
                    Fragment mineFragment = new MineTeacherFragment();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof MineTeacherFragment;
                    if (!isNeed) {
                        transaction.replace(R.id.content, mineFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.title_mine);
                    tv_sos_history.setVisibility(View.INVISIBLE);
                }
                return true;
            }
            return false;
        }

    }

    /********************** teacher api end **********************/

}
