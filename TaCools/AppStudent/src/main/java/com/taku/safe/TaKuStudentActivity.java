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
import com.taku.safe.action.ActionFragment;
import com.taku.safe.activity.GuideActivity;
import com.taku.safe.activity.SosHistoryActivity;
import com.taku.safe.activity.WelcomeTakuActivity;
import com.taku.safe.evaluation.EvaluationFragment;
import com.taku.safe.campus.TabCampusFragment;
import com.taku.safe.ui.main.MainStudentFragment1;
import com.taku.safe.update.UpdateAppManager;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.ScreenUtils;
import com.umeng.analytics.MobclickAgent;


/**
 * app first activity
 * Created by colin on 2017/5/10.
 */
public class TaKuStudentActivity extends BasePermissionActivity {

    private static final String TAG = TaKuStudentActivity.class.getSimpleName();

    public static final int LOGIN = 0;
    public static final int LOGIN_OUT = 1;
    public static final int START_WELCOME = 2;

    private FragmentManager fragmentManager;

    private TextView tv_title;
    private TextView tv_join_action;

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
            overridePendingTransition(R.anim.activity_open, 0);
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

        tv_join_action = (TextView) findViewById(R.id.tv_confirm);
        tv_join_action.setText("加入活动");
        tv_join_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SosHistoryActivity.class));
            }
        });

    }


    private void initView() {
        int index = getIntent().getIntExtra("tab_index", 0);
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);

        if (TextUtils.isEmpty(mTakuApp.getToken())) {
            entryAccountActivity();
        } else {
            if (mTakuApp.isExpire()) {
                entryAccountActivity();
            } else {
                if (mTakuApp.isBind()) {
                    initNav(index);
                } else {
                    entryAccountActivity();
                }
            }
        }

    }

    private void tabIndex(int index) {
        navigation.inflateMenu(R.menu.navigation_student);
        navigation.setOnNavigationItemSelectedListener(new NavStudentListener());
        tv_join_action.setVisibility(View.INVISIBLE);

        navigation.enableShiftingMode(false); //取消切换动画
        navigation.enableItemShiftingMode(false); //取消文字
        navigation.enableAnimation(false);  //取消选中动画
        navigation.setCurrentItem(index);
    }


    private void entryAccountActivity() {
        startActivityForResult(new Intent(this, AccountInfoActivity.class), LOGIN);
        overridePendingTransition(R.anim.activity_open, 0);
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


    /********************** student api start **********************/


    /**
     * 学生端导航栏
     */
    private class NavStudentListener implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof MainStudentFragment1;
                    if (!isNeed) {
                        Fragment homeFragment = new MainStudentFragment1();
                        transaction.replace(R.id.content, homeFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.title_home);
                    tv_join_action.setVisibility(View.INVISIBLE);
                }
                return true;
                case R.id.navigation_practice: {
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof ActionFragment;
                    if (!isNeed) {
                        Fragment actionFragment = new ActionFragment();
                        transaction.replace(R.id.content, actionFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.action);
                    tv_join_action.setVisibility(View.VISIBLE);
                }
                return true;
                case R.id.navigation_school: {
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof TabCampusFragment;
                    if (!isNeed) {
                        Fragment campusFragment = new TabCampusFragment();
                        transaction.replace(R.id.content, campusFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.complaint);
                    tv_join_action.setVisibility(View.INVISIBLE);
                }

                return true;
                case R.id.navigation_mine: {
                    Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                    boolean isNeed = fragment instanceof EvaluationFragment;
                    if (!isNeed) {
                        Fragment evaluationFragment = new EvaluationFragment();
                        transaction.replace(R.id.content, evaluationFragment);
                        transaction.commit();
                    }
                    tv_title.setText(R.string.evaluation);
                    tv_join_action.setVisibility(View.INVISIBLE);
                }
                return true;
            }
            return false;
        }
    }
    /********************** student api end **********************/

}
