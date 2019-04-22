package com.ivmall.android.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.debug.CrashHandler;
import com.ivmall.android.app.dialog.BindPhoneDialog;
import com.ivmall.android.app.dialog.PayVipSecceedDialog;
import com.ivmall.android.app.entity.ConfigItem;
import com.ivmall.android.app.entity.ConfigRequest;
import com.ivmall.android.app.entity.ConfigResponse;
import com.ivmall.android.app.entity.HeartBeatResponse;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.entity.ProfileRequest;
import com.ivmall.android.app.entity.ProfileResponse;
import com.ivmall.android.app.entity.UserInfo;
import com.ivmall.android.app.entity.UserInfoRequest;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.impl.OnUpPrefileListener;
import com.ivmall.android.app.pay.payment.DomyPayActivity;
import com.ivmall.android.app.pay.payment.QrcodePayActivity;
import com.ivmall.android.app.pay.payment.UnicomPayActivity;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.player.SmartPlayingActivity;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.Log;
import com.ivmall.android.app.uitls.LoginUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

/**
 * KidsMindApplication
 * 应用程序全局类，
 */
public class KidsMindApplication extends Application {

    private static final String TAG = KidsMindApplication.class.getSimpleName();

    private Context mContext;
    private static Stack<Activity> mActivityStack;

    public static final String DEFAULT_TOKEN = "default_token";  //匿名账号token
    public static final String MOBILE_TOKEN = "mobile_token";  //手机号账号token
    public static final String MOBILE_NUM = "mobile_num";  //手机号码
    public static final String MOBILE_MODEL = "mobile_model";  //播放模式

    public static final String SETUP = "SET_UP";  //应用是否首次安装
    public static final String TAG_DEVICE = "device";
    public static final String TAG_REGISTER = "register";
    public static final String TAG_PAID = "paid";

    private Map<String, String> mAppConfigs;


    // 读取配置文件
    private static final String ASSETS_CONFIG_FILE = "config.properties";

    private Properties mConfigProps = null;

    private LoginType mLoginType = LoginType.defaultLogin;//用户登录类型
    private String saveToken;   //用户登录TOKEN
    private String moblieNum;
    //private int mProfileId;
    private ProfileItem mProfile;


    private String phoneNum;
    private String userName;

    private int mUserId; //用户ID
    private UserInfo.VipType vipLevel;    //用户等级 device=匿名,register=注册,paid=付费
    private String vipExpiresTime;  //Vip过期时间  yyyy-MM-dd HH:mm:ss

    private String topicStr;
    private String allCategoryStr;

    private int behaviorId;

    private LoginUtils loginUtils;  //用于管理登录

    private boolean isFirst = true;

    /**
     * 登录类型枚举
     */
    public enum LoginType {
        defaultLogin, mobileLogin
    }

    /**
     * 版本升级的严重度
     */
    public enum Severity {  //严重度，blocker级别需要强制升级应用后使用
        blocker, major, normal
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        AppConfig.AppContext = this;
        mAppConfigs = new HashMap<String, String>();
        loginUtils = new LoginUtils(this);

        CrashHandler.getInstance().init(this);  //初始化崩溃日志汇报

        //小米统计添加
        MiStatInterface.initialize(this, AppConfig.XIAOMI_APPID, AppConfig.XIAOMI_APPKEY, "xiaomi_tv_store");
        MiStatInterface.setUploadPolicy(MiStatInterface.UPLOAD_POLICY_REALTIME, 0);


        readHostFormConfig();  //初始化测试配置
        initTokenMobileNum();
        reqAppConfig();

        if (AppConfig.TEST_HOST)
            Toast.makeText(this, R.string.test_host_info, Toast.LENGTH_SHORT).show();
    }

    public void initTokenMobileNum() {
        String mobileToken = AppUtils.getStringSharedPreferences(this, MOBILE_TOKEN, "");
        if (!StringUtils.isEmpty(mobileToken)) {
            saveToken = mobileToken;
            mLoginType = LoginType.mobileLogin;
        } else {
            String defaultToken = AppUtils.getStringSharedPreferences(this, DEFAULT_TOKEN, "");
            if (!StringUtils.isEmpty(defaultToken)) {
                saveToken = defaultToken;
                mLoginType = LoginType.defaultLogin;
            }
        }

        String mobileNum = AppUtils.getStringSharedPreferences(this, MOBILE_NUM, "");
        if (!StringUtils.isEmpty(mobileNum)) {
            moblieNum = mobileNum;
        }

    }


    /**
     * 是否手机号登录
     *
     * @return
     */
    public boolean isLogin() {
        boolean res = false;
        KidsMindApplication.LoginType type = getLoginType();
        if (type == KidsMindApplication.LoginType.mobileLogin) {
            res = true;
        }
        return res;
    }


    /**
     * 获取登录类型
     *
     * @return
     */
    public LoginType getLoginType() {
        return mLoginType;
    }


    public String getMoblieNum() {
        return moblieNum;
    }

    public void setMoblieNum(String moblieNum) {
        this.moblieNum = moblieNum;
        AppUtils.setStringSharedPreferences(this, MOBILE_NUM, moblieNum);
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public int getProfileId() {
        int id = 0;
        if (mProfile != null) {
            id = mProfile.getId();
        }
        return id;
    }

    public boolean isModelA() {
        //20150828 取消A模式
        return false;//AppUtils.getBooleanSharedPreferences(this, MOBILE_MODEL, false);
    }


    public ProfileItem getProfile() {
        return mProfile;
    }

    public void setProfile(ProfileItem profile) {
        mProfile = profile;
    }


    public int getBehaviorId() {
        return behaviorId;
    }

    public void setBehaviorId(int behaviorId) {
        this.behaviorId = behaviorId;
    }

    /**
     * 设置用户登录token
     *
     * @param token
     */
    public void setToken(String token, boolean isDefault) {
        saveToken = token;
        if (isDefault) {
            mLoginType = LoginType.defaultLogin;
            AppUtils.setStringSharedPreferences(this, DEFAULT_TOKEN, token);
        } else {
            mLoginType = LoginType.mobileLogin;
            AppUtils.setStringSharedPreferences(this, MOBILE_TOKEN, token);
        }

    }


    /**
     * 设置注册用户登录token
     *
     * @param token
     */
    public void setToken(String token) {
        this.setToken(token, false);
    }


    /**
     * 获取用户登录token
     *
     * @return
     */
    public String getToken() {
        return saveToken;
    }

    /**
     * 注销登录，清楚手机token
     */
    public void clearToken() {
        AppUtils.setStringSharedPreferences(this, MOBILE_TOKEN, "");
        AppUtils.setStringSharedPreferences(this, DEFAULT_TOKEN, "");
        saveToken = "";
    }


    /**
     * 注销登录，清楚手机token
     */
    public void clearMobileToken() {
        AppUtils.setStringSharedPreferences(this, MOBILE_TOKEN, "");

        saveToken = AppUtils.getStringSharedPreferences(this, DEFAULT_TOKEN, "");
        mLoginType = LoginType.defaultLogin;

    }

    /**
     * 获取渠道号
     *
     * @return
     */
    public String getPromoter() {
        String promoter = getProperty("ChannelNo");
        if (StringUtils.isEmpty(promoter) || promoter.equals("false")) {
            promoter = "kidsmindTV";
        }
        return promoter;
    }


    /**
     * 获取配置信息
     *
     * @param key
     * @return boolean
     */
    public boolean getBooleanProperty(String key) {
        boolean value = false;

        if (mConfigProps == null) {
            initConfig();
        }
        if ("true".equals(mConfigProps.getProperty(key, "false"))) {
            value = true;
        }
        return value;
    }

    /**
     * 获取配置信息
     *
     * @param key
     * @return string
     */
    public String getProperty(String key) {
        if (mConfigProps == null) {
            initConfig();
        }
        return mConfigProps.getProperty(key, "false");
    }

    /**
     * 判断应用是否首次启动
     *
     * @return
     */
    public boolean isFirstSetUp() {
        boolean res = false;
        boolean isSave = AppUtils.getBooleanSharedPreferences(this, SETUP, false);
        if (!isSave) {
            AppUtils.setBooleanSharedPreferences(this, SETUP, true);
            res = true;
        }
        return res;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setVipExpiresTime(String vipTime) {
        vipExpiresTime = vipTime;
    }

    public String getVipExpiresTime() {
        return vipExpiresTime;
    }


    public void setVipLevel(UserInfo.VipType vipLevel) {
        this.vipLevel = vipLevel;
    }

    public boolean isVip() {
        boolean res = false;
        if (vipLevel == UserInfo.VipType.paid) {
            res = true;
        }
        return res;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public String getTopicStr() {
        return topicStr;
    }

    public void setTopicStr(String topicStr) {
        this.topicStr = topicStr;
    }

    public void clearTopicStr() {
        this.topicStr = null;
    }

    public String getAllCategoryStr() {
        return allCategoryStr;
    }

    public void setAllCategoryStr(String allCategoryStr) {
        this.allCategoryStr = allCategoryStr;
    }


    /**
     * 从配置文件读取host
     * 是否链接测试服务器
     */
    public static void readHostFormConfig() {
        String filePath = AppUtils.getSdcardPath();
        File file = new File(filePath, AppConfig.HOST_FILE);
        if (file.exists()) {
            try {
                Properties pro = new Properties();
                InputStream is = new FileInputStream(file);
                pro.load(is);
                String debug = pro.getProperty("debug");
                if (!StringUtils.isEmpty(debug)) {
                    AppConfig.debug = Boolean.parseBoolean(debug);
                }

                String test_host = pro.getProperty("test_host");
                if (!StringUtils.isEmpty(test_host)) {
                    AppConfig.TEST_HOST = Boolean.parseBoolean(test_host);
                }

                String main_host = pro.getProperty("main_host");
                if (!StringUtils.isEmpty(main_host)) {
                    AppConfig.MAIN_HOST_TEST = main_host;
                }

                String report_host = pro.getProperty("report_host");
                if (!StringUtils.isEmpty(report_host)) {
                    AppConfig.REPORT_HOST_TEST = report_host;
                }
                is.close();
            } catch (Exception e) {

            }
        }

    }


    private void initConfig() {
        AssetManager am = getAssets();
        mConfigProps = new Properties();
        InputStream is = null;
        try {
            is = am.open(ASSETS_CONFIG_FILE);
            mConfigProps.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 获取配置项数据
     *
     * @param key
     * @return
     */
    public String getAppConfig(String key) {
        return mAppConfigs.get(key);
    }


    /**
     * 1.7 获取profile信息列表
     * 用于注销登陆后的匿名用户再登陆
     */
    public void reqProfile(final OnSucessListener listener) {
        String url = AppConfig.PROFILE_LIST;
        ProfileRequest request = new ProfileRequest();
        String token = getToken();
        request.setToken(token);
        request.setProfileId("");  //默认传空

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProfileResponse resp = GsonUtil.parse(response,
                        ProfileResponse.class);
                if (resp != null && resp.isSucess()) {
                    List<ProfileItem> list = resp.getData().getList();
                    if (list != null && list.size() > 0) {
                        ProfileItem profile = list.get(0);
                        setProfile(profile);
                        if (listener != null) {
                            listener.sucess();
                        }
                    } else {
                        if (listener != null) {
                            listener.create();
                        }
                    }

                } else if (resp.isFail()) {
                    if (listener != null) {
                        listener.fail();
                    }
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    /**
     * 1.9 更新profile
     */
    public void updateProfile(String headimg, final OnUpPrefileListener listener) {
        String url = AppConfig.UPDATE_PROFILE;
        ProfileRequest request = new ProfileRequest();

        String token = getToken();
        request.setToken(token);

        int profileId = getProfileId();
        request.setProfileId(profileId + "");

        request.setHeadimg(headimg);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProfileResponse resp = GsonUtil.parse(response,
                        ProfileResponse.class);
                if (resp.isSucess()) {
                    String url = resp.getData().getHeadimg();
                    mProfile.setImgUrl(url);
                    if (listener != null) {
                        listener.sucess(url);
                    }
                }

            }

        });
    }

    /**
     * 1.27 获取应用配置项
     */
    private void reqAppConfig() {
        String url = AppConfig.APP_CONFIG;
        ConfigRequest request = new ConfigRequest();

        request.setUniqueKey("");

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ConfigResponse resp = GsonUtil.parse(response,
                        ConfigResponse.class);
                if (null != resp && resp.isSucess()) {
                    List<ConfigItem> list = resp.getList();

                    for (ConfigItem item : list) {
                        mAppConfigs.put(item.getUniqueKey(), item.getUniqueValue());
                    }
                }

            }

        });
    }

    private boolean invited;
    private View exChangeView;
    private ImageView imgVip;
    private ImageButton btn_exchange;


    public boolean isInvited() {
        return invited;
    }

    public void setVisible(View view, ImageView imgVip) {
        this.exChangeView = view;
        this.imgVip = imgVip;
    }


    public void setExChangeBtn(ImageButton btn_exchange, ImageView imgVip) {
        this.btn_exchange = btn_exchange;
        this.imgVip = imgVip;
    }

    /**
     * 1.28 获取用户信息
     */
    public void reqUserInfo() {
        String url = AppConfig.USER_INFO;
        UserInfoRequest request = new UserInfoRequest();

        String token = getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                HeartBeatResponse resp = GsonUtil.parse(response,
                        HeartBeatResponse.class);
                if (resp.success()) {
                    mUserId = resp.getUserId();
                    vipLevel = resp.getVipLevel();
                    vipExpiresTime = resp.getVipExpiresTime();
                    invited = resp.isInvited();
                    phoneNum = resp.getMobile();
                    userName = resp.getName();

                    //判断兑换按钮是否出现
                    if (null != btn_exchange) {
                        if (resp.isInvited()) {
                            btn_exchange.setVisibility(View.GONE);
                        } else {
                            btn_exchange.setVisibility(View.VISIBLE);
                        }
                    }
                    //判断vip标志是否出现
                    if (null != imgVip) {
                        if (isVip()) {
                            imgVip.setVisibility(View.VISIBLE);
                        } else {
                            imgVip.setVisibility(View.GONE);
                        }
                    }
                    if (mVipInfo != null) {
                        String content = getString(R.string.vip_end_time) + vipExpiresTime;
                        int color = getResources().getColor(R.color.orange_text);
                        int start = getString(R.string.vip_end_time).length();
                        int end = content.length();
                        CharSequence cs = AppUtils.setHighLightText(content, color, start, end);
                        mVipInfo.setText(cs);
                    }
                }

            }

        });
    }

    /**
     * 设置一个需要动态刷新VIP过期时间
     *
     * @param textView
     */
    private TextView mVipInfo; //VIP过期时间

    public void setRefresh(TextView textView) {
        mVipInfo = textView;
    }


    /**
     * 1.28 获取用户信息
     * 用户购买成功后刷新用户信息
     */
    public void reqUserInfo(final Activity act, final String vipName) {
        String url = AppConfig.USER_INFO;
        UserInfoRequest request = new UserInfoRequest();

        String token = getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                HeartBeatResponse resp = GsonUtil.parse(response, HeartBeatResponse.class);
                if (resp.success()) {
                    String vipTime = resp.getVipExpiresTime();
                    //保存到全局信息中
                    mUserId = resp.getUserId();
                    vipLevel = resp.getVipLevel();
                    vipExpiresTime = vipTime;

                    if (mVipInfo != null) {
                        String content = getString(R.string.vip_end_time) + vipExpiresTime;
                        int color = getResources().getColor(R.color.orange_text);
                        int start = getString(R.string.vip_end_time).length();
                        int end = content.length();
                        CharSequence cs = AppUtils.setHighLightText(content, color, start, end);
                        mVipInfo.setText(cs);

                    }

                    //提示用户
                    PayVipSecceedDialog dialog = new PayVipSecceedDialog(act, vipName, vipTime);
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            if (StringUtils.isEmpty(getMoblieNum())) {//为空就绑定手机
                                BindPhoneDialog bindPhoneDialog = new BindPhoneDialog(act);
                                bindPhoneDialog.show();
                                bindPhoneDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        dialogDismiss(act);
                                    }
                                });
                            } else {
                                dialogDismiss(act);
                            }

                        }
                    });
                }

            }

        });
    }


    private void dialogDismiss(Activity act) {
        if (act instanceof QrcodePayActivity
                || act instanceof DomyPayActivity
                || act instanceof UnicomPayActivity) {
            act.finish();
        } else if (act instanceof FreePlayingActivity) {
            ((FreePlayingActivity) act).hideBuyVipDialog();
        } else if (act instanceof SmartPlayingActivity) {
            ((SmartPlayingActivity) act).hideBuyVipDialog();
        }
    }


    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack<Activity>();
        }
        mActivityStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = mActivityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
