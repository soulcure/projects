package com.taku.safe;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.taku.safe.config.AppConfig;
import com.taku.safe.config.Constant;
import com.taku.safe.db.bean.NoticeMsg;
import com.taku.safe.db.dao.DaoMaster;
import com.taku.safe.db.dao.DaoSession;
import com.taku.safe.db.dao.NoticeMsgDao;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespLogin;
import com.taku.safe.protocol.respond.RespSchoolList;
import com.taku.safe.protocol.respond.RespStudentInfo;
import com.taku.safe.protocol.respond.RespTeacherInfo;
import com.taku.safe.protocol.respond.RespUserInfo;
import com.taku.safe.protocol.respond.SosInfo;
import com.taku.safe.utils.AppUtils;
import com.taku.safe.utils.DeviceUtils;
import com.taku.safe.utils.GsonUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.greenrobot.greendao.database.Database;

import java.util.List;


public class TakuApp extends MultiDexApplication {
    private static final String TAG = TakuApp.class.getSimpleName();

    private Context mContext;
    private DaoSession daoSession;

    private RespUserInfo userInfo;

    private long expire;//过期时间
    private long ts;//注册时间戳

    private String mToken;  //for test
    private String mPhoneNum;  //for test


    private boolean isBind = false;


    private int msgCount = 0;  //接收的消息数目


    public interface UserInfo {
        void success(RespUserInfo userInfo);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this, android.os.Process.myPid());
        Log.e(TAG, "processName:" + processName);
        if (processName != null) {
            boolean defaultProcess = processName.equals(getPackageName());
            if (defaultProcess) {
                initAppForMainProcess();
            }
        }
    }

    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }


    private void initAppForMainProcess() {
        mContext = this;
        RespBaseBean.setTakuApp(this);

        String channel = AppUtils.getChannel(this);

        initAccount();
        initIndex();
        initUmengSDK(channel);
        initUmengPush(channel);
        updateDeviceInfo();
        EmojiManager.install(new IosEmojiProvider()); // This line needs to be executed before any usage of EmojiTextView, EmojiEditText or EmojiButton.

        Stetho.initializeWithDefaults(this);
    }


    private void initAccount() {
        mToken = AppUtils.getStringSharedPreferences(mContext, Constant.TOKEN, "");
        expire = AppUtils.getLongSharedPreferences(mContext, Constant.EXPIRE, 0);
        ts = AppUtils.getLongSharedPreferences(mContext, Constant.TS, 0);
        isBind = AppUtils.getBooleanSharedPreferences(mContext, Constant.BIND, false);
    }


    private void initGreenDao() {
        if (!TextUtils.isEmpty(getPhoneNum())) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                    getPhoneNum() + Constant.DB_NAME);
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        }
    }


    public void addPushMsg(String title, String content) {
        if (daoSession == null) {
            initGreenDao();
        }
        if (daoSession == null) {
            return;
        }

        NoticeMsgDao msgDao = daoSession.getNoticeMsgDao();
        NoticeMsg note = new NoticeMsg();
        note.setTime(System.currentTimeMillis());
        note.setTitle(title);
        note.setContent(content);
        msgDao.insert(note);
    }

    public List<NoticeMsg> readPushMsg() {
        if (daoSession == null) {
            initGreenDao();
        }

        if (daoSession == null) {
            return null;
        }

        NoticeMsgDao msgDao = daoSession.getNoticeMsgDao();
        List<NoticeMsg> lists = msgDao.queryBuilder().build().list();
        return lists;
    }

    private void clearPushMsg() {
        if (daoSession != null) {
            NoticeMsgDao msgDao = daoSession.getNoticeMsgDao();
            if (msgDao != null) {
                msgDao.deleteAll();
            }
            daoSession = null;
        }
    }


    /**
     * 清除用户账号信息
     */
    public void clear() {
        setToken("");
        setPhoneNum("");
        setBind(false);
        setExpire(0);
        setTs(0);
        clearPushMsg();
        mSignInfo = null;
        mTeacherInfo = null;
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    public RespUserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 获取用户手机号
     *
     * @return
     */
    public String getPhoneNum() {
        if (TextUtils.isEmpty(mPhoneNum)) {
            mPhoneNum = AppUtils.getStringSharedPreferences(mContext, Constant.MOBILE, "");
        }
        return mPhoneNum;
    }

    /**
     * 设置用户手机号
     *
     * @param phoneNum
     */
    public void setPhoneNum(String phoneNum) {
        if (!TextUtils.isEmpty(phoneNum) && !mPhoneNum.equals(phoneNum)) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                    phoneNum + Constant.DB_NAME);
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
        }
        mPhoneNum = phoneNum;

        AppUtils.setStringSharedPreferences(mContext, Constant.MOBILE, phoneNum);
    }

    /**
     * 用户token是否过期
     *
     * @return
     */
    public boolean isExpire() {
        boolean res;

        if (expire == 0) {
            expire = AppUtils.getLongSharedPreferences(mContext, Constant.EXPIRE, 0);
        }
        long time = System.currentTimeMillis() - getTs();

        if (expire == 0) {
            res = true;
        } else {
            res = (time - expire * 1000) > 0;
        }

        //res = false;  //for test

        return res;
    }

    /**
     * 设置token过期时间（默认30天）
     *
     * @param expire
     */
    public void setExpire(long expire) {
        this.expire = expire;
        AppUtils.setLongSharedPreferences(mContext, Constant.EXPIRE, expire);
    }

    /**
     * 设置用户token生成时间戳
     *
     * @return
     */
    public long getTs() {
        if (ts == 0) {
            ts = AppUtils.getLongSharedPreferences(mContext, Constant.TS, 0);
        }
        return ts;
    }

    /**
     * 获取用户token生成时间戳
     *
     * @return
     */
    public void setTs(long ts) {
        this.ts = ts;
        AppUtils.setLongSharedPreferences(mContext, Constant.TS, ts);
    }

    /**
     * 获取用户token
     *
     * @return
     */
    public String getToken() {
        if (TextUtils.isEmpty(mToken)) {
            mToken = AppUtils.getStringSharedPreferences(mContext, Constant.TOKEN, "");
            Log.v(TAG, "getToken=" + mToken);
        }
        //mToken = "bc82ab6a-f36c-4603-9af8-92505ae00c84"; //for test
        return mToken;
    }


    /**
     * 设置用户token
     *
     * @param token
     */
    public void setToken(String token) {
        Log.v(TAG, "setToken=" + mToken);
        mToken = token;
        AppUtils.setStringSharedPreferences(mContext, Constant.TOKEN, token);
    }


    /**
     * 获取用户绑定状态
     *
     * @return
     */
    public boolean isBind() {
        if (!isBind) {
            isBind = AppUtils.getBooleanSharedPreferences(mContext, Constant.BIND, false);
        }
        //isBind = true; //for test
        return isBind;
    }

    /**
     * 设置用户绑定状态
     *
     * @param isBind
     */
    public void setBind(boolean isBind) {
        this.isBind = isBind;
        AppUtils.setBooleanSharedPreferences(mContext, Constant.BIND, isBind);
    }


    /**
     * http协议 返回code=500(需要用户重新登录)
     */
    public void reLoginByProtoCode() {
        setExpire(0);
        Intent intent = new Intent(this, AccountInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        //Toast.makeText(mContext, "http proto error code=401", Toast.LENGTH_SHORT).show();  //for test
    }


    /**
     * 根据imei更新mac信息和手机版本
     */
    private void updateDeviceInfo() {
        String url = AppConfig.COMMON_USERDEVICE_INFO_UPDATE;

        String mac = DeviceUtils.getMacAddress(mContext);
        String imei = DeviceUtils.getIMEI(mContext);
        String appVersion = AppUtils.getVersion(mContext);

        ContentValues params = new ContentValues();
        params.put("mac", mac);
        params.put("imei", imei);
        params.put("appVersion", appVersion);

        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespLogin respLogin = GsonUtil.parse(response, RespLogin.class);
                if (respLogin != null && respLogin.isSuccess()) {

                }
            }
        });
    }


    /**
     * 友盟统计初始化
     */
    private void initUmengSDK(String channel) {
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(this,
                Constant.UM_KEY, channel);
        MobclickAgent.startWithConfigure(config);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
    }


    /**
     * 友盟推送初始化
     *
     * @param channel
     */
    private void initUmengPush(String channel) {
        final PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setMessageChannel(channel);
        mPushAgent.setDebugMode(true);
        mPushAgent.setMuteDurationSeconds(60);

        //友盟自定义通知打开动作
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //友盟自定义消息
        mPushAgent.setMessageHandler(messageHandler);

        //注册推送服务，每次调用register方法都会回调该接口
        final IUmengRegisterCallback callback = new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.d(TAG, "deviceToken:" + deviceToken);

                String mac = DeviceUtils.getMacAddress(mContext);
                String imei = DeviceUtils.getIMEI(mContext);
                String platform = "android";
                String brand = DeviceUtils.getModel();
                deviceInfoUpload(deviceToken, mac, imei, platform, brand);

            }

            @Override
            public void onFailure(String code, String msg) {
                Log.e(TAG, "error:" + code + "  &msg:" + msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPushAgent.register(callback);
            }
        }).start();

    }


    /**
     * 友盟自定义通知打开动作
     */
    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            //自定义消息点击处理
            SosInfo sosInfo = GsonUtil.parse(msg.custom, SosInfo.class);
            if (sosInfo != null
                    && sosInfo.isInternshipSignRemind()) {
                Intent intent = new Intent(mContext, TaKuStudentActivity.class);
                intent.putExtra("tab_index", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else if (sosInfo != null
                    && sosInfo.isReportReply()) {
                Intent intent = new Intent(mContext, TaKuStudentActivity.class);
                intent.putExtra("tab_index", 2);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                this.launchApp(context, msg);  //default
            }
        }

        @Override
        public void openActivity(Context context, UMessage msg) {
            super.openActivity(context, msg);
            msgCount = 0;
            Log.d(TAG, "onClick notification message");
        }

        @Override
        public void launchApp(Context context, UMessage uMessage) {
            super.launchApp(context, uMessage);
            msgCount = 0;
            Log.d(TAG, "onClick notification message");
        }
    };

    /**
     * 友盟自定义消息,自定义消息的内容存放在UMessage.custom字段里
     */
    UmengMessageHandler messageHandler = new UmengMessageHandler() {
        @Override
        public void dealWithCustomMessage(final Context context, final UMessage msg) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // 对于自定义消息，PushSDK默认只统计送达。若开发者需要统计点击和忽略，则需手动调用统计方法。
                    UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                    //Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    // TODO: 2017/8/3 处理透传消息
                    String customMsg = msg.custom;
                    Log.d(TAG, "push 透传消息:" + customMsg);
                }
            });
        }

        @Override
        public Notification getNotification(Context context, UMessage msg) {
            String title = msg.title;
            String content = msg.text;
            String custom = msg.custom;  //自定义消息

            Log.d(TAG, "onReceive notification message && customMsg=" + msg.custom);

            boolean isFg = AppUtils.isAppOnForeground(mContext);
            Log.d(TAG, "isFg=" + isFg);

            if (!TextUtils.isEmpty(custom)) {
                SosInfo sosInfo = GsonUtil.parse(custom, SosInfo.class);
                if (sosInfo != null &&
                        (sosInfo.isRestSignRemind()
                                || sosInfo.isInternshipSignRemind()
                                || sosInfo.isNotice()
                                || sosInfo.isReportReply())) {
                    addPushMsg(title, content);
                    msgCount++;
                } else {
                    //for 隐藏不该收到的通知
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder.setVibrate(new long[]{0, 0, 0, 0, 0});
                    return builder.build();
                }


            }
            return super.getNotification(context, msg);
        }
    };


    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    /**
     * 上报友盟推送token和硬件信息（调用多次只存一个）
     *
     * @param token
     * @param mac
     * @param imei
     * @param platform
     * @param brand
     */
    private void deviceInfoUpload(String token, String mac,
                                  String imei, String platform, String brand) {

        String url = AppConfig.COMMON_DEVICE_INFO_UPLOAD;

        ContentValues params = new ContentValues();
        params.put("token", token);
        params.put("mac", mac);
        params.put("imei", imei);
        params.put("platform", platform);
        params.put("brand", brand);

        OkHttpConnector.httpPost(url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespLogin respLogin = GsonUtil.parse(response, RespLogin.class);
                if (respLogin != null && respLogin.isSuccess()) {

                }
            }
        });
    }


    /**
     * 获取用户信息
     */
    public void reqUserInfo(final UserInfo callback) {
        String url = AppConfig.STUDENT_USERINFO_DETAIL;

        String token = getToken();
        if (TextUtils.isEmpty(token)) {
            return;
        }

        ContentValues header = new ContentValues();
        header.put("token", getToken());

        OkHttpConnector.httpPost(header, url, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespUserInfo info = GsonUtil.parse(response, RespUserInfo.class);
                if (info != null && info.isSuccess()) {
                    userInfo = info;
                    if (callback != null) {
                        callback.success(info);
                    }
                }
            }
        });
    }


    /************************************** common api start ***********************************/
    private RespSchoolList mSchoolList;

    /**
     * 获取学校列表
     */
    public void reqSchoolList(final SchoolInfo callback) {
        String url = AppConfig.COMMON_SCHOOL_LIST;
        if (mSchoolList != null && mSchoolList.isSuccess()) {
            if (callback != null) {
                callback.success(mSchoolList);
                return;
            }
        }

        OkHttpConnector.httpGet(url, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                mSchoolList = GsonUtil.parse(response, RespSchoolList.class);
                if (callback != null)
                    callback.success(mSchoolList);
            }
        });

    }


    /************************************** common api start ***********************************/


    /************************************** student api start ***********************************/

    private RespStudentInfo mSignInfo; //学生签到信息

    /**
     * 学生签到信息回调接口
     */
    public interface SignInfo {
        void success(RespStudentInfo info);
    }

    public RespStudentInfo getSignInfo() {
        return mSignInfo;
    }

    /**
     * 获取学生签到信息
     *
     * @param callback
     */
    public void reqSignInfo(final SignInfo callback) {
        String url = AppConfig.STUDENT_INFO;

        ContentValues header = new ContentValues();
        header.put("token", getToken());

        OkHttpConnector.httpPost(header, url, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespStudentInfo signInfo = GsonUtil.parse(response, RespStudentInfo.class);
                if (signInfo != null && signInfo.isSuccess()) {
                    mSignInfo = signInfo;

                    if (callback != null) {
                        callback.success(signInfo);
                    }
                }
            }
        });
    }
    /************************************** student api end ***********************************/


    /************************************** teacher api start ***********************************/
    private RespTeacherInfo mTeacherInfo;

    private int role;
    private int privilegeLevel;  //原始作用域

    private int level;
    private int indexCollege;
    private int indexMarjor;
    private int indexClass;

    private String signDate;  //当前签到选择日期
    private String practiceDate;  //当前实习选择日期
    private int timeRange;

    public RespTeacherInfo getTeacherInfo() {
        return mTeacherInfo;
    }

    public void setTeacherInfo(RespTeacherInfo teacherInfo) {
        this.mTeacherInfo = teacherInfo;
    }

    /**
     * 1 决策者 other 管理者
     *
     * @return
     */
    public boolean isCommander() {
        return role == 1;
    }

    /**
     * 1 决策者 other 管理者
     *
     * @return
     */
    public boolean isManager() {
        return role != 1;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setIndexCollege(int indexCollege) {
        this.indexCollege = indexCollege;
        Log.d(TAG, "indexCollege=" + indexCollege);
    }


    public int getIndexCollege() {
        return indexCollege;
    }


    public void setIndexMarjor(int indexMarjor) {
        this.indexMarjor = indexMarjor;
        Log.d(TAG, "indexMarjor=" + indexMarjor);
    }

    public int getIndexMarjor() {
        return indexMarjor;
    }

    public void setIndexClass(int indexClass) {
        this.indexClass = indexClass;
        Log.d(TAG, "indexClass=" + indexClass);
    }

    public int getIndexClass() {
        return indexClass;
    }


    public void initIndex() {
        level = AppUtils.getIntSharedPreferences(mContext, Constant.LEVEL, 0);
        indexCollege = AppUtils.getIntSharedPreferences(mContext, Constant.INDEX_COLLEGE, 0);
        indexMarjor = AppUtils.getIntSharedPreferences(mContext, Constant.INDEX_MARJOR, 0);
        indexClass = AppUtils.getIntSharedPreferences(mContext, Constant.INDEX_CLASS, 0);
    }

    public void saveIndex() {
        AppUtils.setIntSharedPreferences(mContext, Constant.LEVEL, level);
        AppUtils.setIntSharedPreferences(mContext, Constant.INDEX_COLLEGE, indexCollege);
        AppUtils.setIntSharedPreferences(mContext, Constant.INDEX_MARJOR, indexMarjor);
        AppUtils.setIntSharedPreferences(mContext, Constant.INDEX_CLASS, indexClass);
    }


    /**
     * 获取用户原始作用域
     *
     * @return
     */
    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public int getLevel() {
        if (level == 0) {
            level = privilegeLevel;
        }
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        Log.d(TAG, "level=" + level);
    }


    public int getDeptId() {
        return mTeacherInfo.getDeptId(level, indexCollege, indexMarjor, indexClass);
    }

    public void resetDeptId() {
        level = privilegeLevel;
        indexCollege = 0;
        indexMarjor = 0;
        indexClass = 0;
        saveIndex();
    }

    public void revertDeptId(int level, int indexCollege, int indexMarjor, int indexClass) {
        setLevel(level);
        this.indexCollege = indexCollege;
        this.indexMarjor = indexMarjor;
        this.indexClass = indexClass;
    }

    public boolean isDecision() {
        return mTeacherInfo.getRole() == 1;
    }


    public List<String> parseSpinnerData() {
        return mTeacherInfo.parseSpinnerData(indexCollege, indexMarjor, indexClass);
    }

    public void setSpinnerData(int level, int index) {
        if (level == 1) {
            setLevel(2);
            indexCollege = index;
        } else if (level == 2) {
            setLevel(3);
            indexMarjor = index;
        } else if (level == 3) {
            setLevel(4);
            indexClass = index;
        }
    }

    public String getSchoolName() {
        if (mTeacherInfo != null && mTeacherInfo.isSuccess()) {
            return mTeacherInfo.getSchool().getSchoolName();
        }
        return null;
    }

    public String getDeptName() {
        if (mTeacherInfo != null && mTeacherInfo.isSuccess()) {
            return mTeacherInfo.getDeptName(level, indexCollege, indexMarjor, indexClass);
        }
        return null;
    }

    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate;
    }

    public String getPracticeDate() {
        return practiceDate;
    }

    public void setPracticeDate(String practiceDate) {
        this.practiceDate = practiceDate;
    }

    public int getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(int timeRange) {
        this.timeRange = timeRange;
    }

    public interface SchoolInfo {
        void success(RespSchoolList list);
    }

    public interface TeacherInfo {
        void success(RespSchoolList list, RespTeacherInfo info);
    }


    /**
     * 获取老师信息
     */
    public void reqTeacherInfo(final TeacherInfo callback) {
        if (mSchoolList != null && mSchoolList.isSuccess()
                && mTeacherInfo != null && mTeacherInfo.isSuccess()) {
            if (callback != null) {
                callback.success(mSchoolList, mTeacherInfo);
            }
            return;
        }

        String url = AppConfig.TEACHER_USERINFO;

        ContentValues header = new ContentValues();
        header.put("token", getToken());

        OkHttpConnector.httpGet(header, url, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespTeacherInfo teacherInfo = GsonUtil.parse(response, RespTeacherInfo.class);
                if (teacherInfo != null
                        && teacherInfo.isSuccess()
                        && teacherInfo.getSchool() != null) {

                    mTeacherInfo = teacherInfo;

                    //动态调整  //1 决策者 other 管理者
                    if (teacherInfo.getRole() != 1) {
                        try {
                            if (teacherInfo.getSchool().getCollegeList().size() == 1
                                    && teacherInfo.getPrivilegeLevel() == 1) {
                                teacherInfo.setPrivilegeLevel(2);

                                if (teacherInfo.getSchool().getCollegeList().get(0)
                                        .getMajorList().size() == 1) {
                                    teacherInfo.setPrivilegeLevel(3);

                                    if (teacherInfo.getSchool().getCollegeList().get(0)
                                            .getMajorList().get(0).getClassList().size() == 1) {
                                        teacherInfo.setPrivilegeLevel(4);
                                    }

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    privilegeLevel = teacherInfo.getPrivilegeLevel();

                    String school = GsonUtil.format(teacherInfo.getSchool());
                    String md5 = AppUtils.md5(school);
                    String saveMd5 = AppUtils.getStringSharedPreferences(mContext, Constant.TEACHER_INFO, "");

                    if (TextUtils.isEmpty(saveMd5)) {
                        AppUtils.setStringSharedPreferences(mContext, Constant.TEACHER_INFO, md5);
                    } else if (!saveMd5.equals(md5)) {
                        AppUtils.setStringSharedPreferences(mContext, Constant.TEACHER_INFO, md5);
                        resetDeptId();
                    }

                    if (callback != null)
                        callback.success(mSchoolList, mTeacherInfo);

                } else {
                    Toast.makeText(mContext, "获取老师信息失败，请联系管理员!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    /**
     * 查询学生的作息统计数据
     *
     * @param day 天
     */
    public void reqSignRestStatistics(String day, IGetListener listener) {
        if (mTeacherInfo != null
                && mTeacherInfo.isSuccess()
                && mTeacherInfo.getSchool() != null) {

            String url = AppConfig.TEACHER_REST_STATISTICS;

            ContentValues header = new ContentValues();
            header.put("token", getToken());

            ContentValues params = new ContentValues();
            params.put("level", getLevel());
            params.put("deptId", getDeptId());
            params.put("day", day);

            OkHttpConnector.httpGet(header, url, params, listener);

        } else {
            Toast.makeText(mContext, "获取老师信息失败，请联系管理员!", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 查询学生的实习统计数据
     *
     * @param day 天
     */
    public void reqSignPracticeStatistics(String day, IGetListener listener) {
        if (mTeacherInfo != null
                && mTeacherInfo.isSuccess()
                && mTeacherInfo.getSchool() != null) {

            String url = AppConfig.TEACHER_INTERNSHIP_STATISTICS;

            ContentValues header = new ContentValues();
            header.put("token", getToken());

            ContentValues params = new ContentValues();
            if (isDecision()) {
                params.put("level", getLevel());
                params.put("deptId", getDeptId());
            }
            params.put("day", day);

            OkHttpConnector.httpGet(header, url, params, listener);

        } else {
            Toast.makeText(mContext, "获取老师信息失败，请联系管理员!", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 获取所在学校名称
     *
     * @param list
     * @param schoolId
     * @return
     */
    public String parseSchool(RespSchoolList list, int schoolId) {
        String schoolName = null;
        List<RespSchoolList.SchoolListBean> schoolList = list.getSchoolList();
        for (RespSchoolList.SchoolListBean itemSchool : schoolList) {
            if (schoolId == itemSchool.getSchoolId()) {
                schoolName = itemSchool.getSchoolName();
                break;
            }
        }
        return schoolName;
    }

    /**
     * 获取拥有查看权限的学院列表（拥有查看所有学院的权限）
     *
     * @param list
     * @param schoolId
     * @return
     */
    public List<RespSchoolList.SchoolListBean.CollegeListBean> parseCollege(RespSchoolList list,
                                                                            int schoolId) {
        List<RespSchoolList.SchoolListBean.CollegeListBean> collegeList = null;
        List<RespSchoolList.SchoolListBean> schoolList = list.getSchoolList();
        for (RespSchoolList.SchoolListBean itemSchool : schoolList) {
            if (schoolId == itemSchool.getSchoolId()) {
                collegeList = itemSchool.getCollegeList();
                break;
            }
        }
        return collegeList;
    }


    /**
     * 获取拥有查看权限的专业列表（拥有查看所有专业的权限）
     *
     * @param list
     * @param schoolId
     * @param collegeId
     * @return
     */
    public List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean> parseMajor(
            RespSchoolList list, int schoolId, int collegeId) {
        List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean> majorList = null;
        List<RespSchoolList.SchoolListBean> schoolList = list.getSchoolList();
        for (RespSchoolList.SchoolListBean itemSchool : schoolList) {
            if (schoolId == itemSchool.getSchoolId()) {
                List<RespSchoolList.SchoolListBean.CollegeListBean> collegeList = itemSchool.getCollegeList();
                for (RespSchoolList.SchoolListBean.CollegeListBean itemCollege : collegeList) {
                    if (collegeId == itemCollege.getCollegeId()) {
                        majorList = itemCollege.getMajorList();
                        break;
                    }
                }
            }
        }
        return majorList;
    }


    /**
     * 获取拥有查看权限的班级列表（拥有查看所有班级的权限）
     *
     * @param list
     * @param schoolId
     * @param collegeId
     * @param majorId
     * @return
     */
    public List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean> parseClass(
            RespSchoolList list, int schoolId, int collegeId, int majorId) {
        List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean> classList = null;
        List<RespSchoolList.SchoolListBean> schoolList = list.getSchoolList();
        for (RespSchoolList.SchoolListBean itemSchool : schoolList) {
            if (schoolId == itemSchool.getSchoolId()) {
                List<RespSchoolList.SchoolListBean.CollegeListBean> collegeList = itemSchool.getCollegeList();
                for (RespSchoolList.SchoolListBean.CollegeListBean itemCollege : collegeList) {
                    if (collegeId == itemCollege.getCollegeId()) {
                        List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean> majorList = itemCollege.getMajorList();
                        for (RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean majorItem : majorList) {
                            if (majorId == majorItem.getMajorId()) {
                                classList = majorItem.getClassList();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return classList;
    }


    /**
     * 获取拥有查看权限的某个班级
     *
     * @param list
     * @param schoolId
     * @param collegeId
     * @param majorId
     * @param classId
     * @return
     */
    public String parseClass(RespSchoolList list, int schoolId, int collegeId, int majorId, int classId) {
        List<RespSchoolList.SchoolListBean> schoolList = list.getSchoolList();
        for (RespSchoolList.SchoolListBean itemSchool : schoolList) {
            if (schoolId == itemSchool.getSchoolId()) {
                List<RespSchoolList.SchoolListBean.CollegeListBean> collegeList = itemSchool.getCollegeList();
                for (RespSchoolList.SchoolListBean.CollegeListBean itemCollege : collegeList) {
                    if (collegeId == itemCollege.getCollegeId()) {
                        List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean> majorList = itemCollege.getMajorList();
                        for (RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean majorItem : majorList) {
                            if (majorId == majorItem.getMajorId()) {
                                List<RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean> classList = majorItem.getClassList();
                                for (RespSchoolList.SchoolListBean.CollegeListBean.MajorListBean.ClassListBean classItem : classList) {
                                    if (classItem.getClasseId() == classId) {
                                        return classItem.getClasseName();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    /************************************** teacher api end ***********************************/


}
