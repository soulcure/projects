package com.ivmall.android.app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bumptech.glide.Glide;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.debug.CrashHandler;
import com.ivmall.android.app.dialog.ExchangeDialog;
import com.ivmall.android.app.entity.ActionInfoRequest;
import com.ivmall.android.app.entity.BehaviorResponse;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.fragment.CommunityFragments;
import com.ivmall.android.app.fragment.ControlFragment;
import com.ivmall.android.app.fragment.MineFragment;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GlideCircleTransform;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.LoginUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;

import java.util.ArrayList;


public class MainFragmentActivity extends AppCompatActivity {
    public final static String TAG = MainFragmentActivity.class.getSimpleName();

    private Context mContext;

    private FragmentManager fragmentManager;

    private ImageView imgHead;
    private TextView tvInfo;
    private DrawerLayout mDrawerLayout;
    private ImageButton BtnImgExit;

    private long mPressedTime;

    private AHBottomNavigation bottomNavigation;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

    public boolean isPhone = true;


    // --------------------配置数据 end--------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ScreenUtils.isPhone(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isPhone = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isPhone = false;
        }

        setContentView(R.layout.main_activity);
        initBaiduPush(); //初始化百度云推送的相关代码
        mContext = this;

        registerReceiver(mConnectionChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        ControlFragment.playSetting(this);

        initView();
        CrashHandler.getInstance().sendCrashLogToServer(this); //汇报崩溃日志汇报

        openApp(mContext);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }


    /**
     * 用于初始化百度云推送
     */
    private void initBaiduPush() {
        PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
                AppConfig.BAIDU_PUSH_API_KEY);
    }


    /**
     * 初始化tabview
     */
    private void initView() {
        fragmentManager = getSupportFragmentManager();

        if (isPhone) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nv_main_navigation);
            if (navigationView != null) {
                setupDrawerContent(navigationView);
            }
        }

        ///////////////------分割线---------------------///

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.kids_fun,
                R.drawable.btn_tongle, R.color.mian_radio_text_selector);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.community,
                R.drawable.btn_community, R.color.mian_radio_text_selector);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.control,
                R.drawable.btn_control, R.color.mian_radio_text_selector);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.mine,
                R.drawable.btn_mine, R.color.mian_radio_text_selector);

        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigationItems.add(item4);
        bottomNavigation.addItems(bottomNavigationItems);
        bottomNavigation.setForceTitlesDisplay(true);
        bottomNavigation.setAccentColor(this.getResources().getColor(R.color.mian_yellow));

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new CartoonFragments();
        transaction.replace(R.id.content, fragment);
        transaction.commit();

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out);

                switch (position) {
                    case 0: {
                        Fragment homeFragment = new CartoonFragments();

                        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                        boolean isNeed = fragment instanceof CartoonFragments;
                        if (!isNeed) {
                            transaction.replace(R.id.content, homeFragment);
                            transaction.commit();
                        }
                    }
                    break;
                    case 1: {
                        Fragment communityFragments = new CommunityFragments();

                        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                        boolean isNeed = fragment instanceof CommunityFragments;
                        if (!isNeed) {
                            transaction.replace(R.id.content, communityFragments);
                            transaction.commit();
                        }
                    }
                    break;
                    case 2: {
                        Fragment personFragment = new ControlFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isPhone", isPhone);
                        personFragment.setArguments(bundle);

                        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                        boolean isNeed = fragment instanceof ControlFragment;
                        if (!isNeed) {
                            transaction.replace(R.id.content, personFragment);
                            transaction.commit();
                        }

                    }
                    break;
                    case 3: {
                        Fragment mineFragment = new MineFragment();

                        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
                        boolean isNeed = fragment instanceof MineFragment;
                        if (!isNeed) {
                            transaction.replace(R.id.content, mineFragment);
                            transaction.commit();
                        }

                    }
                    break;
                }
            }
        });
    }

    /**
     * DrawerLayout onClick
     *
     * @param navigationView 抽屉菜单控件
     */
    private void setupDrawerContent(NavigationView navigationView) {
        View headerView = navigationView.inflateHeaderView(R.layout.navigation_header);
        tvInfo = (TextView) headerView.findViewById(R.id.tv_info);
        imgHead = (ImageView) headerView.findViewById(R.id.img_head);
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((KidsMindApplication) getApplication()).isLogin()) {
                    Intent intent = new Intent(mContext, BaseActivity.class);
                    intent.putExtra(BaseActivity.NAME, BaseActivity.LOGIN);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, BaseActivity.class);
                    intent.putExtra(BaseActivity.NAME, BaseActivity.USER_INFO);
                    mContext.startActivity(intent);
                }
            }
        });
        BtnImgExit = (ImageButton) headerView.findViewById(R.id.img_exit);
        if (!((KidsMindApplication) getApplication()).isLogin()) {
            BtnImgExit.setVisibility(View.GONE);
        }


        BtnImgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuitDialog(mContext);
            }
        });


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_exchange:
                                new ExchangeDialog(mContext).show();
                                break;
                            case R.id.nav_action: {
                                Intent intent = new Intent(mContext, ActionActivity.class);
                                startActivity(intent);
                                BaiduUtils.onEvent(mContext, OnEventId.WEB_ACTION, getString(R.string.web_action));
                                break;
                            }
                            case R.id.nav_mall: {
                                final String url = ((KidsMindApplication) getApplication())
                                        .getAppConfig("shoppingURL");

                                if (!StringUtils.isEmpty(url)) {
                                    /*Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse(url);
                                    intent.setData(content_url);*/

                                    Intent intent = new Intent(mContext, MallActivity.class);
                                    intent.putExtra(MallActivity.URL_KEY, url);
                                    startActivity(intent);
                                    BaiduUtils.onEvent(mContext, OnEventId.ENTRY_TOY_MALL,
                                            getString(R.string.entry_toy_mall));
                                }
                                break;
                            }
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPhone) {
            initHeadImage();
        }
        if (!AppUtils.isNetworkConnected(this)) {
            AppUtils.showNetworkError(this);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mConnectionChangeReceiver);
        leaveApp(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //File upload related
        if (requestCode == CommunityFragments.FILE_CHOOSER_RESULT_CODE
                && (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)) {

            Fragment fragment = fragmentManager.findFragmentById(R.id.content);
            if (fragment != null && fragment instanceof CommunityFragments) {
                CommunityFragments currentFragment = ((CommunityFragments) fragment);
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }

        }
    }


    /**
     * 获取用户头像
     */
    public void initHeadImage() {
        ProfileItem profileItem = ((KidsMindApplication) getApplication()).getProfile();
        if (profileItem != null) {
            initHead(profileItem);
        } else {
            ((KidsMindApplication) getApplication()).reqProfile(new OnReqProfileResult());
        }
    }


    private void initHead(ProfileItem profileItem) {
        String imgUrl = profileItem.getImgUrl();
        if (!StringUtils.isEmpty(imgUrl)) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .centerCrop()
                    .bitmapTransform(new GlideCircleTransform(mContext)) //设置图片圆角
                    .placeholder(R.drawable.icon_login_image)  //占位图片
                    .error(R.drawable.icon_login_image)        //下载失败
                    .into(imgHead);

        }
        if (((KidsMindApplication) getApplication()).isLogin()) {
            tvInfo.setText(((KidsMindApplication) getApplication()).getUserName());
        } else {
            tvInfo.setText(R.string.please_login);
        }
    }

    private class OnReqProfileResult implements OnSucessListener {

        @Override
        public void sucess() {
            ProfileItem list = ((KidsMindApplication) getApplication()).getProfile();
            boolean isDestroy = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroy = isDestroyed();
            }

            if (list != null && !isDestroy) {
                initHead(list);
            }
        }

        @Override
        public void create() {
        }

        @Override
        public void fail() {
        }

    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mPressedTime) < 2000) {
            leaveApp(mContext);

            if (((KidsMindApplication) getApplication()).isFirstSetUp()) {

                String moblieNum = ((KidsMindApplication) getApplication()).getMoblieNum();
                if (StringUtils.isEmpty(moblieNum)) {
                    //清除前面版本保存的token数据
                    ((KidsMindApplication) getApplication()).clearToken();
                    //清除前面版本保存的deviceDRMId数据
                    AppUtils.setStringSharedPreferences(this, "device_id", "");
                }

            }

            ((KidsMindApplication) getApplication()).clearTopicStr();
            finish();
        } else {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(KidsMindApplication.MP3_NOFA_ID);
            mPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出教学", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 1.75 打开应用通知（用户行为统计）
     */
    public static void openApp(final Context context) {
        String url = AppConfig.OPEN_APP;
        ActionInfoRequest request = new ActionInfoRequest();
        String token = ((KidsMindApplication) context.getApplicationContext()).getToken();

        request.setToken(token);
        request.setActivityTime(System.currentTimeMillis());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                BehaviorResponse res = GsonUtil.parse(response, BehaviorResponse.class);
                if (res != null && res.isSucess()) {
                    int behaviorId = res.getData().getBehaviorId();
                    ((KidsMindApplication) context.getApplicationContext()).setBehaviorId(behaviorId);
                }
            }
        });

    }


    /**
     * 1.77 离开应用通知（用户行为统计）
     */
    public static void leaveApp(Context context) {
        String url = AppConfig.LEAVE_APP;
        ActionInfoRequest request = new ActionInfoRequest();
        String token = ((KidsMindApplication) context.getApplicationContext()).getToken();

        int behaviorId = ((KidsMindApplication) context.getApplicationContext()).getBehaviorId();
        request.setBehaviorId(behaviorId);
        request.setActivityTime(System.currentTimeMillis());
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                Log.v(TAG, response);
            }
        });
    }


    /**
     * 1.6 注销登录
     */
    private void loginOut() {
        LoginUtils loginUtils = new LoginUtils(mContext);

        loginUtils.setLoginOutSuccess(new LoginUtils.LoginOutListener() {
            @Override
            public void onSuccess() {
                tvInfo.setText(R.string.please_login);
                BtnImgExit.setVisibility(View.GONE);

                Snackbar snack = Snackbar.make(imgHead, R.string.login_out_sucess, Snackbar.LENGTH_LONG);
                View view = snack.getView();
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(getResources().getColor(R.color.primary));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                snack.show();

            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
        loginUtils.loginOut();
    }


    private void showQuitDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.login_out))
                .setMessage(context.getString(R.string.login_out_msg));

        builder.setPositiveButton(context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        loginOut();
                        arg0.dismiss();

                    }
                });

        builder.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }

    /**
     * 网络状态监听器
     */
    private BroadcastReceiver mConnectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivity = (ConnectivityManager) mContext.
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null) {
                    int apn = info.getType();
                    if (apn == ConnectivityManager.TYPE_WIFI) {
                        int level = getWifiLevel();
                        if (level <= SIGNAL_STRENGTH_POOR) {
                            Toast.makeText(mContext, R.string.wifi_strength_poor, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        }
    };

    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;

    /**
     * 获取wifi信号水平
     *
     * @return
     */
    private int getWifiLevel() {
        int level;
        int rssi;
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        rssi = wifiInfo.getRssi();

        if (rssi < -100 || rssi > 0) level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        else if (rssi >= -50) level = SIGNAL_STRENGTH_GREAT;
        else if (rssi >= -70) level = SIGNAL_STRENGTH_GOOD;
        else if (rssi >= -90) level = SIGNAL_STRENGTH_MODERATE;
        else level = SIGNAL_STRENGTH_POOR;

        return level;
    }


}