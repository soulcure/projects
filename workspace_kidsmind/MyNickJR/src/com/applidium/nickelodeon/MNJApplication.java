package com.applidium.nickelodeon;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.debug.CrashHandler;
import com.applidium.nickelodeon.dialog.BindPhoneDialog;
import com.applidium.nickelodeon.dialog.PayVipSecceedDialog;
import com.applidium.nickelodeon.entity.ConfigItem;
import com.applidium.nickelodeon.entity.ConfigRequest;
import com.applidium.nickelodeon.entity.ConfigResponse;
import com.applidium.nickelodeon.entity.NmjLoginResponse;
import com.applidium.nickelodeon.entity.NmjProfileInfo;
import com.applidium.nickelodeon.entity.ProfileItem;
import com.applidium.nickelodeon.entity.ProfileRequest;
import com.applidium.nickelodeon.entity.ProfileResponse;
import com.applidium.nickelodeon.entity.ProtocolRequest;
import com.applidium.nickelodeon.entity.SetupResponse;
import com.applidium.nickelodeon.entity.UserInfo;
import com.applidium.nickelodeon.entity.UserInfoRequest;
import com.applidium.nickelodeon.entity.UserInfoResponse;
import com.applidium.nickelodeon.entity.VipListItem;
import com.applidium.nickelodeon.impl.OnSucessListener;
import com.applidium.nickelodeon.pay.payment.DomyPayActivity;
import com.applidium.nickelodeon.pay.payment.QrcodePayActivity;
import com.applidium.nickelodeon.player.FreePlayingActivity;
import com.applidium.nickelodeon.player.SmartPlayingActivity;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IGetListener;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.Log;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.uitls.XmlUtil;
import com.xiaomi.mistatistic.sdk.MiStatInterface;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: MNJApplication
 * @Description: 应用程序全局类，
 */
public class MNJApplication extends Application {

    private static final String TAG = MNJApplication.class.getSimpleName();

    private Context mContext;

    public static final String IVMALL_TOKEN = "ivmall_token";  //ivmall账号token
    public static final String USER_NAME = "user_name";  //用户名
    public static final String USER_ID = "user_id";  //用户ID
    public static final String MOBILE_NUM = "mobile_num";  //手机号码
    public static final String PASS_WORD = "pass_word";  //手机号码
    public static final String FIRST_MODIFIED_TIME = "firstModifiedTime";  //手机号码
    public static final String SETUP = "SET_UP";  //应用是否首次安装

    private Map<String, String> mAppConfigs;

    private List<VipListItem> vipListItems;  // 因为vip价格变的比较慢，不需要每次都去请求。

    // 读取配置文件
    private static final String ASSETS_CONFIG_FILE = "config.properties";

    private Properties mConfigProps = null;

    private LoginType mLoginType = LoginType.defaultLogin;//用户登录类型
    private String ivmallToken;   //用户登录TOKEN
    private String mnjToken;

    private String mUserName; //用户名
    private String moblieNum; //电话号码
    private String passWord;//密码
    private String firstModifiedTime;//
    private String vipExpiryTime;

    private ProfileItem mProfile;
    private String mUserId; //用户ID
    private UserInfo mUserInfo;    //用户等级 device=匿名,register=注册,paid=付费


    public UserInfo getUserInfo() {
        return mUserInfo;
    }

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

        CrashHandler.getInstance().init(this);  //初始化崩溃日志汇报

        if (isFirstSetUp()) {
            Log.v(TAG, "app is first setup");
            //转移前面版本的保存数据
            initVersionData(this);

            reqAppSetUp();
        }


        //小米统计添加
        MiStatInterface.initialize(this, AppConfig.XIAOMI_APPID, AppConfig.XIAOMI_APPKEY, "xiaomi_tv_store"); //need change
        MiStatInterface.setUploadPolicy(MiStatInterface.UPLOAD_POLICY_REALTIME, 0);


        readHostFormConfig();  //初始化测试配置

        initTokenMobileNum();
        reqAppConfig();


    }


    /**
     * 转移前面版本的保存数据
     *
     * @param context
     */
    public void initVersionData(Context context) {

        //转移 deviceDRMId 数据
        SharedPreferences sharedPref = context.getSharedPreferences("MNJ_PREF_UNIQUE_ID",
                Context.MODE_PRIVATE);
        String deviceDRMId = sharedPref.getString("MNJ_PREF_UNIQUE_ID", "");
        if (!StringUtils.isEmpty(deviceDRMId)) {
            Log.v(TAG, "app is first setup save device_id=" + deviceDRMId);
            AppUtils.setStringSharedPreferences(context, "device_id", deviceDRMId);
        }

        //转移 ivmall token
        SharedPreferences shareToken = context.getSharedPreferences("Nickelodeon_Android",
                Context.MODE_PRIVATE);
        String token = shareToken.getString("MonNickelodeonJuniorivmallTokenKey", "");
        if (!StringUtils.isEmpty(token)) {
            Log.v(TAG, "app is first setup save token=" + token);
            AppUtils.setStringSharedPreferences(this, IVMALL_TOKEN, token);
        }

    }

    public void initTokenMobileNum() {

        String mobileToken = AppUtils.getStringSharedPreferences(this, IVMALL_TOKEN, "");
        if (!StringUtils.isEmpty(mobileToken)) {
            ivmallToken = mobileToken;
        }

        String mobileNum = AppUtils.getStringSharedPreferences(this, MOBILE_NUM, "");
        if (!StringUtils.isEmpty(mobileNum)) {
            moblieNum = mobileNum;
        }

    }


    /**
     * 获取登录类型
     *
     * @return
     */
    public LoginType getLoginType() {
        return mLoginType;
    }

    /**
     * 设置登录类型
     *
     * @return
     */
    public void setLoginType(boolean isDefault) {
        if (isDefault) {
            mLoginType = LoginType.defaultLogin;
        } else {
            mLoginType = LoginType.mobileLogin;
        }
    }

    public String getUserName() {
        if (StringUtils.isEmpty(mUserName)) {
            mUserName = AppUtils.getStringSharedPreferences(this, USER_NAME, "");
        }
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
        AppUtils.setStringSharedPreferences(this, USER_NAME, userName);
    }

    public String getMoblieNum() {
        if (StringUtils.isEmpty(moblieNum)) {
            moblieNum = AppUtils.getStringSharedPreferences(this, MOBILE_NUM, "");
        }
        return moblieNum;
    }

    public void setMoblieNum(String moblieNum) {
        this.moblieNum = moblieNum;
        AppUtils.setStringSharedPreferences(this, MOBILE_NUM, moblieNum);
    }

    public String getPassWord() {
        if (StringUtils.isEmpty(passWord)) {
            passWord = AppUtils.getStringSharedPreferences(this, PASS_WORD, "");
        }
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
        AppUtils.setStringSharedPreferences(this, PASS_WORD, passWord);
    }

    public String getFirstModifiedTime() {
        if (StringUtils.isEmpty(firstModifiedTime)) {
            firstModifiedTime = AppUtils.getStringSharedPreferences(this, FIRST_MODIFIED_TIME, "");
        }
        return firstModifiedTime;
    }

    /**
     * firstModifiedTime不为空 代表用户已经修改了账号
     */
    public void setFirstModifiedTime(String firstModifiedTime) {
        this.firstModifiedTime = firstModifiedTime;
        AppUtils.setStringSharedPreferences(this, FIRST_MODIFIED_TIME, firstModifiedTime);
    }


    public int getProfileId() {
        int id = 0;
        if (mProfile != null) {
            id = mProfile.getId();
        }
        return id;
    }


    public ProfileItem getProfile() {
        return mProfile;
    }

    public void setProfile(ProfileItem profile) {
        mProfile = profile;
    }


    /**
     * 设置用户登录token
     *
     * @param token
     */
    public void setToken(String token) {
        ivmallToken = token;
        AppUtils.setStringSharedPreferences(this, IVMALL_TOKEN, token);

    }


    /**
     * 获取用户登录token
     *
     * @return
     */
    public String getToken() {
        return ivmallToken;
    }


    /**
     * 获取法国mnj token
     *
     * @return
     */
    public String getMnjToken() {
        return mnjToken;
    }

    /**
     * 设置法国mnj token
     *
     * @param mnjToken
     */
    public void setMnjToken(String mnjToken) {
        this.mnjToken = mnjToken;
    }

    /**
     * 注销登录，清楚手机token
     */
    public void clearToken() {
        AppUtils.setStringSharedPreferences(this, IVMALL_TOKEN, "");
        ivmallToken = "";
    }


    /**
     * 获取渠道号
     *
     * @return
     */
    public String getPromoter() {
        String promoter = getProperty("ChannelNo");
        if (StringUtils.isEmpty(promoter) || promoter.equals("false")) {
            promoter = "mnjmarket";
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
     * @return ture 表示首次创建
     */
    private boolean isFirstSetUp() {
        boolean res = false;
        boolean isSave = AppUtils.getBooleanSharedPreferences(this, SETUP, false);
        if (!isSave) {
            AppUtils.setBooleanSharedPreferences(this, SETUP, true);
            res = true;
        }
        return res;
    }

    public void setUserId(String id) {
        mUserId = id;
        AppUtils.setStringSharedPreferences(this, USER_ID, id);
    }


    public String getUserId() {
        if (StringUtils.isEmpty(mUserId)) {
            mUserId = AppUtils.getStringSharedPreferences(this, USER_ID, "");
        }
        return mUserId;
    }


    public String getVipExpiresTime() {
        if (vipExpiryTime.contains("T")) {
            vipExpiryTime = vipExpiryTime.substring(0, vipExpiryTime.indexOf("T"));
        }
        return vipExpiryTime;
    }

    public void setVipExpiresTime(String vipExpiryTime) {
        this.vipExpiryTime = vipExpiryTime;
    }

    public boolean isVip() {
        boolean res = false;
        if (mUserInfo != null) {
            res = mUserInfo.isVip();
        }
        return res;
    }


    /**
     * 从配置文件读取host
     * 是否链接测试服务器
     */
    public void readHostFormConfig() {
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

                String pay_test = pro.getProperty("pay_test_online");
                if (!StringUtils.isEmpty(test_host)) {
                    AppConfig.PAY_TEST_ONLINE = Boolean.parseBoolean(pay_test);
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

        if (AppConfig.TEST_HOST && AppConfig.PAY_TEST_ONLINE) {
            Toast.makeText(mContext, "不要同时使用：测试服务器环境和现网服务器0.01元环境",
                    Toast.LENGTH_SHORT).show();
        } else if (AppConfig.TEST_HOST && !AppConfig.PAY_TEST_ONLINE) {
            Toast.makeText(mContext, "正在使用测试服务器环境", Toast.LENGTH_SHORT).show();
        } else if (!AppConfig.TEST_HOST && AppConfig.PAY_TEST_ONLINE) {
            Toast.makeText(mContext, "正在使用现网服务器0.01元环境", Toast.LENGTH_SHORT).show();
        } else if (!AppConfig.TEST_HOST && !AppConfig.PAY_TEST_ONLINE) {
            android.util.Log.v(TAG, "release版本，工程配置检查正确...");
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
     * 1.23 应用安装
     */
    private void reqAppSetUp() {
        String url = AppConfig.APP_FIRST_RUN;
        ProtocolRequest request = new ProtocolRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                SetupResponse resp = GsonUtil.parse(response,
                        SetupResponse.class);
                if (resp != null && resp.isSucess()) {

                }

            }

        });
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

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ProfileResponse resp = GsonUtil.parse(response,
                        ProfileResponse.class);
                if (resp.isSucess()) {
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
     * 1.27 获取应用配置项
     */
    private void reqAppConfig() {
        String url = AppConfig.APP_CONFIG;
        ConfigRequest request = new ConfigRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                ConfigResponse resp = GsonUtil.parse(response,
                        ConfigResponse.class);
                if (resp != null && resp.isSucess()) {
                    List<ConfigItem> list = resp.getList();

                    for (ConfigItem item : list) {
                        mAppConfigs.put(item.getUniqueKey(), item.getUniqueValue());
                    }
                }

            }

        });
    }


    /**
     * 设置一个需要动态刷新的TextView
     *
     * @param textView
     */
    private TextView mTextView;

    public void setRefresh(TextView textView) {
        mTextView = textView;
    }

    /**
     * 1.28 获取用户信息
     */
    public void reqUserInfo() {
        reqUserInfo(null, null);
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
                UserInfoResponse resp = GsonUtil.parse(response, UserInfoResponse.class);
                if (resp.success()) {
                    mUserInfo = resp.getData();

                    String mobile = mUserInfo.getMobile();
                    String userName = mUserInfo.getUsername();
                    String vipExpiresTime = mUserInfo.getVipExpiryTime();
                    String firstMtime = mUserInfo.getFirstModifiedTime();

                    setMoblieNum(mobile);
                    setUserName(userName);
                    setVipExpiresTime(vipExpiresTime);
                    setFirstModifiedTime(firstMtime);

                    if (mTextView != null) {
                        String str = getVipExpiresTime();
                        mTextView.setText(str);
                    }

                    if (act != null && !StringUtils.isEmpty(vipName)) {
                        showPaySucessDialog(act, vipName);
                    }
                }

            }

        });
    }

    /**
     * 提示支付成功dialog
     *
     * @param act
     * @param vipName
     */
    private void showPaySucessDialog(final Activity act, final String vipName) {
        //提示用户
        PayVipSecceedDialog dialog = new PayVipSecceedDialog(act, vipName, getVipExpiresTime());
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
                            if (act instanceof VipInfoActivity) {
                                VipInfoActivity vipAct = (VipInfoActivity) act;
                                vipAct.initData();
                            }
                            dialogDismiss(act);
                        }
                    });
                } else {
                    dialogDismiss(act);
                }

            }
        });
    }

    public void setVipListItems(List<VipListItem> lists) {
        this.vipListItems = lists;
    }

    public List<VipListItem> getVipListItems() {
        return this.vipListItems;
    }


    /**
     * 关闭播放页面
     *
     * @param act
     */
    private void dialogDismiss(Activity act) {
        if (act instanceof QrcodePayActivity
                || act instanceof DomyPayActivity) {
            act.finish();
        } else if (act instanceof FreePlayingActivity) {
            ((FreePlayingActivity) act).hideBuyVipDialog();
        } else if (act instanceof SmartPlayingActivity) {
            ((SmartPlayingActivity) act).hideBuyVipDialog();
        }
    }


    /**
     * http post
     * mnj 登录法国服务器 ,登录成功会返回 宝宝profile信息
     */
    public void loginMnj(final String accountId, final String token) {

        String app_id = AppConfig.APP_ID;

        /*ContentValues bodys=new ContentValues();

        bodys.put("tokenType", "null");
        bodys.put("token", token);
        bodys.put("app_id", app_id);
        bodys.put("accountId", accountId);
        bodys.put("password", accountId);

        String xmlBody = XmlUtil.xmlGen("login", bodys);*/
        String xmlBody = XmlUtil.xmlLogin(token, app_id, accountId + "", accountId + "");

        try {
            xmlBody = URLEncoder.encode(xmlBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        xmlBody = "xmldata=" + xmlBody;

        String url = AppConfig.MNJ_LOGIN;

        ContentValues headers = new ContentValues();
        headers.put("Accept", "application/json");
        headers.put("Accept-Encoding", "gzip");
        headers.put("x-operator-id", AppConfig.MNJWSOPERATOR_ID);


        HttpConnector.httpPost(url, xmlBody, headers, new IPostListener() {
            @Override
            public void httpReqResult(String response) {

                NmjLoginResponse resp = GsonUtil.parse(response,
                        NmjLoginResponse.class);

                String mnj_token = resp.getToken();
                setMnjToken(mnj_token);

                //登录成功,请求mnj profile
                reqMnjProfile();
            }
        });


    }

    /**
     * http get
     * mnj 单独从法国服务器 ,请求宝宝profile信息
     */
    private void reqMnjProfile() {
        String userId = getUserId();

        String url = AppConfig.MNJ_HOST + "/accounts/" + userId + "/profiles";

        ContentValues headers = new ContentValues();

        headers.put("Accept", "application/json");
        headers.put("Accept-Encoding", "gzip");
        headers.put("x-operator-id", AppConfig.MNJWSOPERATOR_ID);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        String token = getMnjToken();
        headers.put("x-app-token", token);

        HttpConnector.httpGet(url, headers, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                NmjProfileInfo resp = GsonUtil.parse(response,
                        NmjProfileInfo.class);
            }
        });
    }
}
