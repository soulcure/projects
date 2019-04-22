package com.ivmall.android.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.debug.CrashHandler;
import com.ivmall.android.app.dialog.ExchangeDialog;
import com.ivmall.android.app.dialog.GenCodeDialog;
import com.ivmall.android.app.dialog.LoginDialog;
import com.ivmall.android.app.dialog.LotteryDialog;
import com.ivmall.android.app.entity.ActionInfoRequest;
import com.ivmall.android.app.entity.BehaviorResponse;
import com.ivmall.android.app.entity.CategoryItem;
import com.ivmall.android.app.entity.CategoryResponse;
import com.ivmall.android.app.entity.PrizesInfo;
import com.ivmall.android.app.entity.PrizesInfoRequest;
import com.ivmall.android.app.entity.PrizesInfoResponse;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.entity.ProtocolRequest;
import com.ivmall.android.app.fragment.KidsInfoFragment;
import com.ivmall.android.app.fragment.MetroFragment;
import com.ivmall.android.app.fragment.PlayListFragment;
import com.ivmall.android.app.impl.OnLoginSuccessListener;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.ListUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.MirrorItemView;
import com.ivmall.android.app.views.RecommendCardView;
import com.ivmall.android.app.views.SlidingTabLayout;
import com.jauker.widget.BadgeView;
import com.smit.android.ivmall.stb.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainFragmentActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = MainFragmentActivity.class.getSimpleName();

    private Context mContext;
    /*保存返回键被按下的时间*/
    private long mPressedTime;

    private TextView babyName;
    private TextView babyAge;
    private ImageView babyHeadImg;
    private ImageView imgVip;

    private ViewPager mViewPager;
    private SlidingTabLayout mTabLayout;

    private View babyInfoLayoutView;
    private ImageView imgLogin;
    private ImageButton lotteryBtn;
    private ImageButton imgAction;
    private BadgeView badgeView;


    private static String SHOWACTION = "showAction";
    private static String TIPSKEY = "tipsKey";

    private static String GEN_CODE = "promo_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.playlist_activity);
        initBaiduPush(); //初始化百度云推送的相关代码
        mContext = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String code = bundle.getString(GEN_CODE);
            if (!StringUtils.isEmpty(code)) {
                reqGenCode(code);//do something
            }
        }


        PlaySettingFragment.playSetting(this);
        initView();
        showAction();

        CrashHandler.getInstance().sendCrashLogToServer(this); //汇报崩溃日志汇报

        initAllCategorys();

        ((KidsMindApplication) getApplication()).addActivity(this);

        openApp(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (((KidsMindApplication) getApplication()).getLoginType()
                == KidsMindApplication.LoginType.mobileLogin) {
            getBabyInfo();
        } else {
            setLoginTips();
        }

        if (!AppUtils.isNetworkConnected(this)) {
            AppUtils.showNetworkError(this);
        }

        if (((KidsMindApplication) getApplication()).isInvited()) {
            findViewById(R.id.imgbtn_ecge).setVisibility(View.GONE);
        } else {
            findViewById(R.id.imgbtn_ecge).setVisibility(View.VISIBLE);
        }


    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((KidsMindApplication) getApplication()).finishActivity(this);
        leaveApp(this);
    }

    /**
     * 初始化tabview
     */
    private void initView() {
        babyName = (TextView) findViewById(R.id.tv_name);
        babyAge = (TextView) findViewById(R.id.tv_age);
        babyHeadImg = (ImageView) findViewById(R.id.img_baby);
        imgVip = (ImageView) findViewById(R.id.img_vip);
        imgLogin = (ImageView) findViewById(R.id.btn_login);

        imgAction = (ImageButton) findViewById(R.id.imgbtn_action);
        imgAction.setOnClickListener(this);

        imgAction.setBackgroundResource(R.drawable.bg_image_action);
        AnimationDrawable drawable = (AnimationDrawable) imgAction.getBackground();
        drawable.start();

        setTips();
        findViewById(R.id.imgbtn_set).setOnClickListener(this);
        findViewById(R.id.imgbtn_ecge).setOnClickListener(this);
        lotteryBtn = (ImageButton) findViewById(R.id.imgbtn_awards);
        lotteryBtn.setOnClickListener(this);

        mTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setCustomTabView(R.layout.tab_layout, R.id.tv_tab);    //设置标题textview样式
        mTabLayout.requestFocus();//默认title 获取焦点

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        babyInfoLayoutView = findViewById(R.id.baby_info_layout);
        babyInfoLayoutView.setOnClickListener(this);

        ((KidsMindApplication) getApplication()).setVisible(findViewById(R.id.imgbtn_ecge), imgVip);

        //获取抽奖信息
        reqPrizeDraw(true);
    }


    /**
     * 设置登录提示
     */
    private void setLoginTips() {
        babyName.setVisibility(View.GONE);
        babyAge.setVisibility(View.GONE);
        imgLogin.setVisibility(View.VISIBLE);
        if (((KidsMindApplication) getApplication()).isVip()) {
            imgVip.setVisibility(View.VISIBLE);
        } else {
            imgVip.setVisibility(View.INVISIBLE);
        }
    }

    private void setTips() {
        boolean isBeTips = AppUtils.getBooleanSharedPreferences(mContext, TIPSKEY, false);
        if (!isBeTips) {
            badgeView = new BadgeView(mContext);
            badgeView.setTargetView(imgAction);
            badgeView.setText("1");
            badgeView.setTextSize(getResources().getDimensionPixelSize(R.dimen.action_button_tips));
            badgeView.setBadgeGravity(Gravity.BOTTOM | Gravity.RIGHT);
            AppUtils.setBooleanSharedPreferences(mContext, TIPSKEY, true);
        }
    }

    /**
     * 获取宝宝信息
     */
    private void getBabyInfo() {

        ProfileItem list = ((KidsMindApplication) getApplication()).getProfile();
        if (list != null) {
            setBabyInfo(list); // 初始化数据
        } else {
            ((KidsMindApplication) getApplication()).reqProfile(new OnReqProfileResult());
        }
    }


    private class OnReqProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            ProfileItem list = ((KidsMindApplication) getApplication()).getProfile();
            if (list != null) {
                setBabyInfo(list); // 初始化数据
            }
        }

        @Override
        public void create() {

        }

        @Override
        public void fail() {

        }
    }

    /**
     * 用于初始化百度云推送
     */
    private void initBaiduPush() {
        PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
                AppConfig.BAIDU_PUSH_API_KEY);
    }


    /**
     * 设置宝宝信息
     */
    private void setBabyInfo(ProfileItem list) {
        babyAge.setVisibility(View.VISIBLE);
        babyName.setVisibility(View.VISIBLE);
        imgLogin.setVisibility(View.GONE);
        babyName.setText(list.getNickname());
        String time = list.getBirthday();
        String yearTime = time.substring(0, time.indexOf("-"));
        Calendar c = Calendar.getInstance();// 时间
        int currentYear = c.get(Calendar.YEAR);
        int birthYear = Integer.parseInt(yearTime);
        int age = currentYear - birthYear;
        babyAge.setVisibility(View.VISIBLE);
        babyAge.setText(age + "岁");
        if (list.getGender() == KidsInfoFragment.Gender.male) {
            babyHeadImg.setImageResource(R.drawable.head_boy);
        } else {
            babyHeadImg.setImageResource(R.drawable.head_girl);
        }

        if (((KidsMindApplication) getApplication()).isVip()) {
            imgVip.setVisibility(View.VISIBLE);
        } else {
            imgVip.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = false;

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    View rowOne = null;
                    View view = this.getCurrentFocus();

                    int screenX = (ScreenUtils.getWidthPixels(this) * 2) / 3; //屏幕宽2/3
                    int screenY = ScreenUtils.getHeightPixels(this) / 2;//屏幕高1/2

                    if (view instanceof RecommendCardView) {
                        if (((RecommendCardView) view).getRow() == 0) {
                            rowOne = view;
                        }
                    } else if (view instanceof MirrorItemView) {
                        rowOne = view;
                    }
                    if (rowOne != null) {
                        Rect r = new Rect();
                        boolean b = view.getGlobalVisibleRect(r);
                        if (b && r.left < screenX && r.top < screenY) {
                            int index = mViewPager.getCurrentItem();
                            mTabLayout.foucsChild(index);
                            handled = true;
                        }
                    }
                    break;
            }
        }

        if (handled) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.imgbtn_action) {
            Intent intent = new Intent(this, ActionActivity.class);
            startActivity(intent);
            if (badgeView != null && badgeView.isShown()) {
                badgeView.setVisibility(View.GONE);
            }
            BaiduUtils.onEvent(this, OnEventId.WEB_ACTION, getString(R.string.web_action));
        } else if (id == R.id.imgbtn_set) {
            Intent intent = new Intent(this, ParentFragmentActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            BaiduUtils.onEvent(this, OnEventId.PARENT_CENTER, getString(R.string.parent_center));
        } else if (id == R.id.imgbtn_ecge) {
            new ExchangeDialog(MainFragmentActivity.this).show();
        } else if (id == R.id.baby_info_layout) {
            if (((KidsMindApplication) getApplication()).getLoginType() ==
                    KidsMindApplication.LoginType.defaultLogin) {
                LoginDialog dialog = new LoginDialog(mContext, (KidsMindApplication) getApplication(), new OnLoginSuccessListener() {
                    @Override
                    public void onSuccess() {
                        getBabyInfo();
                    }
                });
                dialog.show();
            } else {
                Intent intent = new Intent(mContext, RecommendDetailsActivity.class);
                startActivity(intent);
                BaiduUtils.onEvent(mContext, OnEventId.SMART_PLAY_SESSION, getString(R.string.smart_play_session));
            }
        } else if (id == R.id.imgbtn_awards) {
            reqPrizeDraw(false);
        }
    }


    private void showAction() {
        if (AppUtils.getBooleanSharedPreferences(mContext, SHOWACTION, true)) {
            Intent intent = new Intent(this, ActionActivity.class);
            startActivity(intent);
            BaiduUtils.onEvent(this, OnEventId.WEB_ACTION, getString(R.string.web_action));
            AppUtils.setBooleanSharedPreferences(mContext, SHOWACTION, false);
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
            //android.os.Process.killProcess(android.os.Process.myPid());//for bug 天猫魔盒退出后 application不重新创建
        } else {
            mPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出教学", Toast.LENGTH_SHORT).show();
        }
    }


    static class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mTitles = new ArrayList<>();

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }


    /**
     * 抽奖信息
     */
    private void reqPrizeDraw(final boolean from) {
        if (AppUtils.isRepeatClick()) {
            return;
        }
        String url = AppConfig.PRIZES_DRAW_INFO;
        PrizesInfoRequest request = new PrizesInfoRequest();
        String token = ((KidsMindApplication) (getApplication())).getToken();
        request.setToken(token);
        request.setActivityTitle("20160101_乌吉送元旦礼物啦!");

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

                    @Override
                    public void httpReqResult(String response) {
                        PrizesInfoResponse resp = GsonUtil.parse(response,
                                PrizesInfoResponse.class);
                        if (resp != null && resp.isSucess()) {
                            PrizesInfo info = resp.getData();
                            lotteryBtn.setVisibility(View.VISIBLE);
                            if (from) {  //自启动
                                if (info.getLeftCount() == 0 && info.getSelectedCoupon() == null) {
                                    return;  //已经使用之后，就不主动弹出
                                } else {
                                    new LotteryDialog(mContext, info).show();
                                }
                            } else {  //按键启动
                                new LotteryDialog(mContext, info).show();
                            }
                        } else {
                            lotteryBtn.setVisibility(View.GONE);
                            AppUtils.getInstance().reqAction(AppUtils.INDEX,
                                    MainFragmentActivity.this);
                        }
                    }
                }
        );
    }


    /**
     * 活动兑换码
     */
    private void reqGenCode(String code) {
        String url = AppConfig.ACTION_CODE;
        PrizesInfoRequest request = new PrizesInfoRequest();
        String token = ((KidsMindApplication) (getApplication())).getToken();
        request.setToken(token);
        request.setActivationCode(code);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

                    @Override
                    public void httpReqResult(String response) {
                        PrizesInfoResponse resp = GsonUtil.parse(response,
                                PrizesInfoResponse.class);
                        if (resp != null)
                            new GenCodeDialog(mContext, resp.isSucess(), resp.getMessage()).show();
                    }
                }
        );
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
     * 获取TAB内容，带有application缓存
     */
    private void initAllCategorys() {
        String allCategoryStr = ((KidsMindApplication) mContext.getApplicationContext())
                .getAllCategoryStr();
        if (StringUtils.isEmpty(allCategoryStr)) {
            reqAllCategorys();
        } else {
            CategoryResponse resp = GsonUtil.parse(allCategoryStr,
                    CategoryResponse.class);
            if (resp != null && resp.isSucess()) {

                List<CategoryItem> list = resp.getData().getList();
                if (!ListUtils.isEmpty(list)) {
                    TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
                    for (int i = 0; i < list.size(); i++) {
                        String tabName = list.get(i).getCategory();
                        String title = list.get(i).getName();

                        if (i == 0) {
                            adapter.addFragment(MetroFragment.newInstance(tabName), title);
                        } else {
                            adapter.addFragment(PlayListFragment.newInstance(tabName), title);
                        }
                        //   mTabLayout.addTab(mTabLayout.newTab().setText(title));

                    }

                    mViewPager.setAdapter(adapter);
                    mTabLayout.setViewPager(mViewPager);

                }


            } else {
                reqAllCategorys();
            }

        }
    }


    /**
     * 1.78 获所有的分类
     */
    private void reqAllCategorys() {
        String url = AppConfig.ALL_CATEGORYS;
        ProtocolRequest request = new ProtocolRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                CategoryResponse resp = GsonUtil.parse(response,
                        CategoryResponse.class);
                if (resp != null && resp.isSucess()) {

                    ((KidsMindApplication) mContext.getApplicationContext())
                            .setAllCategoryStr(response);

                    List<CategoryItem> list = resp.getData().getList();
                    if (!ListUtils.isEmpty(list)) {
                        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
                        for (int i = 0; i < list.size(); i++) {
                            String tabName = list.get(i).getCategory();
                            String title = list.get(i).getName();

                            if (i == 0) {
                                adapter.addFragment(MetroFragment.newInstance(tabName), title);
                            } else {
                                adapter.addFragment(PlayListFragment.newInstance(tabName), title);
                            }
                            //mTabLayout.addTab(mTabLayout.newTab().setText(title));
                        }

                        mViewPager.setAdapter(adapter);
                        mTabLayout.setViewPager(mViewPager);
                    }
                }
            }
        });
    }

}