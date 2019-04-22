package com.applidium.nickelodeon.config;

import android.content.Context;

import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.DeviceUtils;
import com.applidium.nickelodeon.uitls.StringUtils;

public class AppConfig {
    /**
     * 是否打开日志
     * false 表示关闭debug日志，大部分日志将会关闭
     * true  表示打开debug日志
     * release版本请将debug日志关闭
     */
    public static boolean debug = false;

    /**
     * 是否连接测试服务器
     * false 表示连接现网服务器
     * true  表示连接测试网服务器
     */
    public static boolean TEST_HOST = true;

    /**
     * 是否打开现网测试 0.01元
     * true  表示打开现网 0.01元支付测试
     * false 表示关闭测试功能
     */
    public static boolean PAY_TEST_ONLINE = false;


    public static Context AppContext;  //app context

    public static final String MNJWSOPERATOR_ID = "smit";
    public static final String APP_ID = "ANDROID_MNJ_H7CoW3126Pl7F3YA"; //mnj app id

    /**
     * 服务器协议主域名
     */

    /*ivmall 主域名*/
    public static final String MAIN_HOST = "http://api.huhatv.com";


    /*mnj 法国服务器 主域名*/
    public static final String MNJ_HOST = "http://mnj-api.huhatv.com";  //作为登录法国服务器，获取profile


    public static final String REPORT_HOST = "http://report.mnj.huhatv.com/report"; //统计host url

    /**
     * 测试配置项
     * 测试服务器域名
     */

    public static final String HOST_FILE = "mnj_config.txt"; //此配置代码不要修改

    public static String MAIN_HOST_TEST = "http://192.168.40.22:28080";
    public static String REPORT_HOST_TEST = "http://report.mnj.huhatv.com/report"; //统计host url

    public static String CRASH_LOG_REPORT_HOST = "http://mobileinfo.ivmall.com/ivmallmobile/ivmupload"; //崩溃日志上传地址

    /**
     * 小米AppID
     * AppKey
     * 小米Oauth授权回调地址
     */
    public static final String XIAOMI_APPID = "2882303761517292029"; //小米AppID
    public static final String XIAOMI_APPKEY = "5221729281029"; //AppKey


    /**
     * 创维酷开TV ID
     */
    public static final String COOCAA_APPID = "5536"; //创维酷开AppID
    public static final String COOCAA_TYPE = "虚拟"; //创维酷开虚拟道具


    /**
     * TCL 欢网支付ID
     */
    //public static final String APP_PAYKEY = "999"; //欢网支付ID , for测试
    public static final String APP_PAYKEY = "pay20150130145715231";  //欢网支付ID , 正式

    /**
     * 百度播放器设置
     */
    /*public static final String AK = "asXW7E4FdwKVapULxMtickc7";
    public static final String SK = "3T1gs027Baz8kh73";*/

    /**
     * pref文件名定义
     */
    public static final String SHARED_PREFERENCES = "KidsMind";

    /**
     * 百度云推送API KEY
     */
    public static final String BAIDU_PUSH_API_KEY = "5OoUjC6TbxCQl9yBFyzjpMDZ";  //发布版
    //public static final String BAIDU_PUSH_API_KEY = "wmyFQTxRGX0SoGwN1NRKLq5A";  //自测版

    /**
     * 小尼克下载地址
     */
    public static final String DOWN_MNJCHAIN = "http://download.ivmall.com/install/app/mnjchina/mnjchina.apk";
    /**
     * 纷纷英语下载地址
     */
    public static final String DOWN_FNF = "http://download.huhatv.com/install/app/fnf/FnFprojectOfficial.apk";


    /***********************************************************
     * ******************以下为http post 协议地址*****************
     ***********************************************************/

    /**
     * mnj 登录法国服务器
     */
    public static final String MNJ_LOGIN = MNJ_HOST + "/mobile/login";  //mnj login

    /**
     * 以下是ivmall 协议地址
     */
    /*5.3.1.1应用安装,首次运行*/
    public static final String APP_FIRST_RUN = MAIN_HOST + "/app/install.action";

    /*5.3.1.2应用控制*/
    public static final String APP_CONTROL = MAIN_HOST + "/app/config.action"; //未使用

    /*5.3.1.3应用启动*/
    public static final String APP_LAUNCH = MAIN_HOST + "/app/launch.action";  //未使用

    /*5.3.1.4 获取促销活动信息*/
    public static final String APP_ACTION = MAIN_HOST + "/app/activityInfo.action";

    /*5.3.1.5 获取应用配置项*/
    public static final String APP_CONFIG = MAIN_HOST + "/app/configuration.action";

    /*5.3.2.1 用户注册*/
    public static final String USER_REGISTER = MAIN_HOST + "/mnj/user/register.action";

    /*5.3.2.2 用户登录(ivmall)*/
    public static final String USER_LOGIN_IVMALL = MAIN_HOST + "/mnj/user/login.action";

    /*5.3.2.3 用户登出*/
    public static final String LOGIN_OUT = MAIN_HOST + "/mnj/user/logout.action";

    /*5.3.2.4 用户详情信息*/
    public static final String USER_INFO = MAIN_HOST + "/mnj/user/detail.action";

    /*5.3.2.5 获取短信验证码(确认使用6位码)*/
    public static final String SMS_CODE = MAIN_HOST + "/mnj/user/sms.action";

    /*5.3.2.6 用户修改个人资料*/
    public static final String UPDATE_INFO = MAIN_HOST + "/mnj/user/update.action";

    /*5.3.2.7 用户忘记密码*/
    public static final String FORGET_PASSWORD = MAIN_HOST + "/mnj/user/forgetPassword.action";

    /*5.3.2.8 用户修改密码*/
    public static final String UPDATE_PASSWORD = MAIN_HOST + "/mnj/user/updatePassword.action";

    /*5.3.2.9 手机号码判断(用户注册调用，可判断是否已注册)*/
    public static final String MOBILE_IS_REGISTERED = MAIN_HOST + "/mnj/user/mobile.action";

    /*5.3.2.10 服务器分配用户名密码(快速注册)*/
    public static final String AUTO_LOGIN = MAIN_HOST + "/mnj/user/autoRegister.action";

    /*5.3.2.11 修改用户名*/
    public static final String UPDATE_USERNAME = MAIN_HOST + "/mnj/user/updateUsername.action";

    /*5.3.2.12 绑定手机号*/
    public static final String MOBILE_BIND = MAIN_HOST + "/mnj/user/bindMobile.action";

    /*5.3.2.13 查询和帐号绑定的微信号*/
    public static final String QUERY_BIND_MM = MAIN_HOST + "/mnj/user/queryBindMM.action";

    /*5.3.2.14 取绑定帐号的二维码信息*/
    public static final String QR_CODEGEN = MAIN_HOST + "/mnj/user/qrCodeGen.action";

    /*5.3.2.15 非会员用户播放心跳*/
    public static final String HEART_BEAT = MAIN_HOST + "/mnj/user/heartbeat.action";

    /*5.3.2.16 用户使用激活码*/
    public static final String ACTION_CODE = MAIN_HOST + "/user/activationCode/use.action";

    /*5.3.3.1 已购买产品列表*/
    public static final String GOOD_LIST = MAIN_HOST + "/mnj/buy/list.action";

    /*5.3.3.2 订阅VIP资格*/
    public static final String VIP_SUBSCRIBE = MAIN_HOST + "/mnj/buy/vipSubscribe.action";

    /*5.3.3.3 VIP资格列表*/
    public static final String VIP_LIST = MAIN_HOST + "/mnj/buy/vipList.action";

    /*5.3.3.4 获取促销信息*/
    public static final String SALES_INFO_ACTION = MAIN_HOST + "/mnj/buy/salesInfo.action";

    /*5.3.3.5 获取抽奖信息*/
    public static final String PRIZES_INFO_ACTION = MAIN_HOST + "/mnj/buy/prizesInfo.action";

    /*5.3.4.1 获取视频相关信息*/
    public static final String SERIE_INFO = MAIN_HOST + "/mnj/content/info.action";

    /*5.3.4.2 获取剧集系列列表*/
    public static final String SERIES_SERIES_LIST = MAIN_HOST + "/mnj/content/seriesList.action";  //未使用

    /*5.3.4.3 获取剧集系列内容列表*/
    public static final String SERIES_CONTENT_LIST = MAIN_HOST + "/mnj/content/seriesContentList.action";

    /*5.3.4.4 获取所有季剧集列表*/
    public static final String SERIES_LIST = MAIN_HOST + "/mnj/content/subSeriesList.action";

    /*5.3.4.5 获取指定子剧集的内容*/
    public static final String CONTENT_LIST = MAIN_HOST + "/mnj/content/contentList.action";

    /*5.3.4.6 前端通知后台播放视频*/
    public static final String PLAY_NOTICE = MAIN_HOST + "/mnj/content/playNotice.action";

    /*5.3.4.7 获取播放地址*/
    public static final String PLAY_URL = MAIN_HOST + "/mnj/content/play.action";

    /*5.3.4.8 剧集投票*/
    public static final String DO_YOU_LIKE = MAIN_HOST + "/mnj/profile/rating.action";

    /*5.3.4.9 获取profile信息列表*/
    public static final String PROFILE_LIST = MAIN_HOST + "/mnj/profile/list.action";

    /*5.3.4.10 创建宝宝profile*/
    public static final String CREATE_PROFILE = MAIN_HOST + "/mnj/profile/create.action";

    /*5.3.4.11 更新profile信息*/
    public static final String UPDATE_PROFILE = MAIN_HOST + "/mnj/profile/update.action";

    /*5.3.4.12 智能推荐内容*/
    public static final String RECOMMEND_PLAY = MAIN_HOST + "/mnj/content/recommendation/episode.action";


    /**
     * 以下为静态页面
     */
    /*用户条款*/
    public static final String USER_CLAUSE = "http://mnj.huhatv.com/ivmall-terms.htm";


    /*1.32 支付宝预支付*/
    public static final String PAY_ALIPAY_PREPARE = MAIN_HOST + "/alipay/prepareSecurePay.action";

    /*1.33 支付宝订单查询*/
    public static final String PAY_ALIPAY_QUERY = MAIN_HOST + "/alipay/singleTradeQuery.action";

    /*1.34 支付宝二维码扫描*/
    public static final String PAY_QR_ALIPAY_NOTIFY = MAIN_HOST + "/alipay/qrCodeGen.action";

    /*1.35 支付宝二维码扫描订单查询*/
    public static final String PAY_QR_ALIPAY_QUERY = MAIN_HOST + "/alipay/singleTradeQuery.action";

    /*1.36 微信预二维码扫描*/
    public static final String PAY_WX_PREPARE = MAIN_HOST + "/tenpay/publicNativePay.action";

    /*1.37 微信二维码扫描订单查询*/
    public static final String PAY_WX_QUERY = MAIN_HOST + "/tenpay/publicPayTradeQuery.action";

    /*1.38 微信预支付*/
    public static final String PAY_QR_WX_NOTIFY = MAIN_HOST + "";

    /*1.39 微信订单查询*/
    public static final String PAY_QR_WX_QUERY = MAIN_HOST + "";

    /*1.40 阿里云TV预支付*/
    public static final String PAY_ALIYUN_TV_PREPARE = MAIN_HOST + "/alipay/prepareYunPay.action";

    /*1.41 阿里云TV订单查询*/
    public static final String PAY_ALIYUN_TV_QUERY = MAIN_HOST + "/alipay/tradeResult.action";

    /*1.42 小米TV预支付*/
    public static final String PAY_MI_TV_PREPARE = MAIN_HOST + "/mipay/prepareMiPay.action";

    /*1.43 小米TV订单查询*/
    public static final String PAY_MI_TV_QUERY = MAIN_HOST + "/mipay/singleTradeQuery.action";

    /*1.44 创维酷开TV预支付*/
    public static final String PAY_COOCAA_TV_PREPARE = MAIN_HOST + "/coocaa/preparePay.action";

    /*1.45 创维酷开TV订单查询*/
    public static final String PAY_COOCAA_TV_QUERY = MAIN_HOST + "/coocaa/singleTradeQuery.action";

    /*1.46 大麦TV预支付*/
    public static final String PAY_DOMY_TV_PREPARE = MAIN_HOST + "/domy/preparePay.action";

    /*1.47 大麦TV订单查询*/
    public static final String PAY_DOMY_TV_QUERY = MAIN_HOST + "/domy/singleTradeQuery.action";

    /*1.48 TCL 欢网预支付*/
    public static final String PAY_HUANPAY_PREPARE = MAIN_HOST + "/huan/preparePay.action";

    /*1.49 TCL 欢网订单查询*/
    public static final String PAY_HUANPAY_QUERY = MAIN_HOST + "/huan/singleTradeQuery.action";


    /***********************************************************
     * ******************以上为http post 协议地址*****************
     ***********************************************************/


    public enum Client {  //客户端类型,不在枚举范围内时默认为web  ,Enum("android","ios","web","stb")
        android, ios, web, stb
    }

    public static final String LANG = "zh-cn";  //语言，默认“zh-cn”

    public static final String PARTNER = "mnj"; //  合作商，分配给第三方集成的唯一标志, 可传kidsmind


    public static String deviceDRMId;  //设备编号

    public static String getDeviceDRMId(Context context) {

        if (StringUtils.isEmpty(deviceDRMId)) {
            deviceDRMId = AppUtils.getStringSharedPreferences(context, "device_id", "");
            if (StringUtils.isEmpty(deviceDRMId)) {
                deviceDRMId = DeviceUtils.getDeviceDRMId(context);
                AppUtils.setStringSharedPreferences(context, "device_id", deviceDRMId);
            }

        }
        return deviceDRMId;
    }


}
