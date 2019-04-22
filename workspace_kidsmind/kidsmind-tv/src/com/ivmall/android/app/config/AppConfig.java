package com.ivmall.android.app.config;

import android.content.Context;

import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.DeviceUtils;
import com.ivmall.android.app.uitls.StringUtils;

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
    public static boolean TEST_HOST = false;


    public static Context AppContext;  //app context

    /**
     * 服务器协议主域名
     */
    public static final String MAIN_HOST = "http://api.ikids.huhatv.com";
    public static final String REPORT_HOST = "http://report.ikidsmind.com/report"; //统计host url


    public static final String HOST_FILE = "kidsmind_config.txt"; //此配置代码不要修改

    public static String MAIN_HOST_TEST = "http://api-test.ikids.huhatv.com";
    //public static String REPORT_HOST_TEST = "http://report-test.ikidsmind.com/report"; //统计host url
    public static String REPORT_HOST_TEST = "http://report.ikidsmind.com/report"; //统计host url


    /**
     * 小米AppID
     * AppKey
     * 小米Oauth授权回调地址
     */
    public static final String XIAOMI_APPID = "2882303761517215652"; //小米AppID
    public static final String XIAOMI_APPKEY = "5311721513652"; //AppKey


    /**
     * 创维酷开TV ID
     */
    public static final String COOCAA_APPID = "2536"; //创维酷开AppID
    public static final String COOCAA_TYPE = "虚拟"; //创维酷开虚拟道具


    /**
     * 联通支付ID
     */

    public static final String UNICOM_APPID = "tlktyj";  //联通APPID
    public static final String UNICOM_APPKEY = "txwljf";  //联通APPKEY
    public static final String UNICOM_PRODUCTID = "tlktyjby18";  //联通productId
    public static final String UNICOM_CONTENTID = "tlktyjby18";  //联通contentId
    /**
     * 欢网支付ID
     */
    //public static final String HUAN_PAYKEY = "999"; //欢网支付ID , for测试
    public static final String HUAN_PAYKEY = "pay20150804085811807";  //欢网支付ID , 正式

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
    public static final String BAIDU_PUSH_API_KEY = "lmPN9AwmXPuInauqgQUmGXIC";  //发布版
    //public static final String BAIDU_PUSH_API_KEY = "zO1OkES6tkAY2dmU8Iiu8ehP";  //自测版

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
    /* 1.1 一键注册(自动分配用户名)*/
    public static final String AUTO_LOGIN = MAIN_HOST + "/user/autoRegister.do";

    /* 1.2 用户登录（随机密码登陆）*/
    public static final String MOBILE_LOGIN = MAIN_HOST + "/user/login.do";

    /* 1.3 手机号是否已注册*/
    public static final String MOBILE_NUM_REGISTERED = MAIN_HOST + "/user/mobile.do";

    /*1.4 获取短信验证码*/
    public static final String SMS_CODE = MAIN_HOST + "/user/sms.do";

    /*1.5 绑定手机号*/
    public static final String MOBILE_BIND = MAIN_HOST + "/user/bindMobile.do";

    /*1.6 注销登录*/
    public static final String LOGIN_OUT = MAIN_HOST + "/user/logout.do";

    /*1.7 获取profile信息列表*/
    public static final String PROFILE_LIST = MAIN_HOST + "/user/profile/list.do";

    /*1.8 创建profile*/
    public static final String CREATE_PROFILE = MAIN_HOST + "/user/profile/create.do";

    /*1.9 更新profile*/
    public static final String UPDATE_PROFILE = MAIN_HOST + "/user/profile/update.do";

    /*1.10 获取推荐内容（智能推荐）*/
    public static final String SMART_RECOMMEND = MAIN_HOST + "/user/recommendation.do";


    /*1.11 获取播放地址*/
    public static final String PLAY_URL = MAIN_HOST + "/user/play.do";


    /*1.12 剧集投票（喜欢或不喜欢）*/
    public static final String DO_YOU_LIKE = MAIN_HOST + "/user/rating.do";

    /*1.13 session/start*/
    public static final String SESSION_START = MAIN_HOST + "/user/start.do";  //暂未使用

    /*1.14 session/stop*/
    public static final String SESSION_STOP = MAIN_HOST + "/user/stop.do";   //暂未使用

    /*1.15 session/pulse*/
    public static final String SESSION_PAUSE = MAIN_HOST + "/user/pulse.do"; //暂未使用

    /*1.16 获取首页卡通人物*/
    public static final String CARTOON_ROLE_ = MAIN_HOST + "/index/interest.do";

    /*1.17 获取首页剧集列表*/
    public static final String MAIN_PLAYLIST = MAIN_HOST + "/index/series.do";

    /*1.18 获取总剧集详细内容*/
    public static final String SERIE_INFO = MAIN_HOST + "/index/serie.do";

    /*1.19 收藏剧集*/
    public static final String ADD_FAVORITE = MAIN_HOST + "/favorite/add.do";

    /*1.20 取消收藏*/
    public static final String REMOVE_FAVORITE = MAIN_HOST + "/favorite/remove.do";

    /*1.21 获取收藏列表*/
    public static final String FAVORITE_LIST = MAIN_HOST + "/favorite/list.do";

    /*1.22 获取历史播放记录*/
    public static final String WATCH_HISTORY = MAIN_HOST + "/history/watch.do";

    /*1.23 应用安装,首次运行*/
    public static final String APP_FIRST_RUN = MAIN_HOST + "/app/setup.do";

    /*1.24 应用启动 广告展示*/
    public static final String APP_LAUNCH_ADV = MAIN_HOST + "/app/launch.do";

    /*1.25 应用自升级*/
    public static final String APP_UPDATE = MAIN_HOST + "/app/upgrade.do";

    /*1.26 是否启用节目订制(收藏)*/
    public static final String FAVORITE_ENABLE = MAIN_HOST + "/favorite/enable.do"; //暂未使用

    /*1.27 获取应用配置项*/
    public static final String APP_CONFIG = MAIN_HOST + "/app/config.do";

    /*1.28 获取用户信息*/
    public static final String USER_INFO = MAIN_HOST + "/user/userinfo.do";

    /*1.29 获取历史播放记录*/
    public static final String USER_FEEDBACK = MAIN_HOST + "/user/feedback.do";

    /*1.30 获取VIP列表*/
    public static final String VIP_LIST = MAIN_HOST + "/vip/list.do";

    /*1.31 非会员心跳*/
    public static final String HEART_BEAT = MAIN_HOST + "/user/heartbeat.do";

    /*1.32 支付宝预支付*/
    public static final String PAY_ALIPAY_PREPARE = MAIN_HOST + "/payment/alipay/preparePay.do";

    /*1.33 支付宝订单查询*/
    public static final String PAY_ALIPAY_QUERY = MAIN_HOST + "/payment/alipay/tradeQuery.do";

    /*1.34 支付宝二维码扫描*/
    public static final String PAY_QR_ALIPAY_NOTIFY = MAIN_HOST + "/payment/alipay/QRCode.do";

    /*1.35 支付宝二维码扫描订单查询*/
    public static final String PAY_QR_ALIPAY_QUERY = MAIN_HOST + "/payment/alipay/tradeQRCodeQuery.do";

    /*1.36 微信预二维码扫描*/
    public static final String PAY_WX_PREPARE = MAIN_HOST + "/payment/tenpay/publicNativePay.do";

    /*1.37 微信二维码扫描订单查询*/
    public static final String PAY_WX_QUERY = MAIN_HOST + "/payment/tenpay/publicPayTradeQuery.do";

    /*1.38 微信预支付*/
    public static final String PAY_QR_WX_NOTIFY = MAIN_HOST + "";

    /*1.39 微信订单查询*/
    public static final String PAY_QR_WX_QUERY = MAIN_HOST + "";

    /*1.40 阿里云TV预支付*/
    public static final String PAY_ALIYUN_TV_PREPARE = MAIN_HOST + "/payment/alipay/prepareYunPay.do";

    /*1.41 阿里云TV订单查询*/
    public static final String PAY_ALIYUN_TV_QUERY = MAIN_HOST + "/payment/alipay/tradeQuery.do";

    /*1.42 小米TV预支付*/
    public static final String PAY_MI_TV_PREPARE = MAIN_HOST + "/payment/mipay/preparePay.do";

    /*1.43 小米TV订单查询*/
    public static final String PAY_MI_TV_QUERY = MAIN_HOST + "/payment/mipay/tradeQuery.do";

    /*1.44 创维酷开TV预支付*/
    public static final String PAY_COOCAA_TV_PREPARE = MAIN_HOST + "/payment/coocaa/preparePay.do";

    /*1.45 创维酷开TV订单查询*/
    public static final String PAY_COOCAA_TV_QUERY = MAIN_HOST + "/payment/coocaa/tradeQuery.do";

    /*1.46 大麦TV预支付*/
    public static final String PAY_DOMY_TV_PREPARE = MAIN_HOST + "/payment/domy/preparePay.do";

    /*1.47 大麦TV订单查询*/
    public static final String PAY_DOMY_TV_QUERY = MAIN_HOST + "/payment/domy/tradeQuery.do";

    /*1.48 TCL 欢网预支付*/
    public static final String PAY_HUANPAY_PREPARE = MAIN_HOST + "/payment/huan/preparePay.do";

    /*1.49 TCL 欢网订单查询*/
    public static final String PAY_HUANPAY_QUERY = MAIN_HOST + "/payment/huan/tradeQuery.do";


    /*1.51 获取首页剧集列表_V2*/
    public static final String MAIN_PLAYLIST_V2 = MAIN_HOST + "/index/v2/series.do";


    /*1.52 获取促销信息宣传图*/
    public static final String REQ_ACTION = MAIN_HOST + "/vip/salesinfo.do";


    /*1.53 活动信息登记接口*/
    public static final String JOIN_ACTION = MAIN_HOST + "/user/activity.do";


    /*1.54 智能推荐V2（返回总剧集）*/
    public static final String SMART_RECOMMEND_SERIE = MAIN_HOST + "/user/recommendation/serie.do";


    /*1.55 智能推荐V2（返回子剧集）*/
    public static final String SMART_RECOMMEND_EPISODE = MAIN_HOST + "/user/recommendation/episode.do";

    /*1.56 百度云推送（设备登记）*/
    public static final String BAIDU_PUSH_REGISTER = MAIN_HOST + "/app/push/initial.do";

    /*1.57 获取专题列表*/
    public static final String PLAY_TOPIC = MAIN_HOST + "/index/topic.do";


    /*1.58 获取专题列表详细内容*/
    public static final String PLAY_TOPIC_INFO = MAIN_HOST + "/index/topics.do";

    /*1.59 获取邀请码*/
    public static final String USER_CODE = MAIN_HOST + "/user/code.do";

    /*1.60 使用邀请码*/
    public static final String USE_INVATE_CODE = MAIN_HOST + "/user/code/invite.do";

    /*1.61 LeTV预支付*/
    public static final String PAY_LE_TV_PREPARE = MAIN_HOST + "/payment/letv/preparePay.do";

    /*1.62 LeTV订单查询*/
    public static final String PAY_LE_TV_QUERY = MAIN_HOST + "/payment/letv/tradeQuery.do";

    /*1.63 获取抽奖信息*/
    public static final String PRIZES_DRAW_INFO = MAIN_HOST + "/user/activity/prizesinfo.do";


    /*1.64 联通TV预支付*/
    public static final String PAY_UNICOM_TV_PREPARE = MAIN_HOST + "/payment/unicom/preparePay.do";

    /*1.65 联通TV订单查询*/
    public static final String PAY_UNICOM_TV_QUERY = MAIN_HOST + "/payment/unicom/tradeQuery.do";

    /*1.71 上报用户点击视频广告事件*/
    public static final String REPORT_CLICK_AD = MAIN_HOST + "/user/reportClickAd.do";

    /*1.72 相关推荐接口*/
    public static String FIRST_RECOMM = MAIN_HOST + "/user/relate/recommendation.do";

    /*1.73 错误日志上报*/
    public static String CRASH_LOG_REPORT_HOST = MAIN_HOST + "/report/errorLog.do";


    /*1.74 用户使用激活码*/
    public static final String ACTION_CODE = MAIN_HOST + "/user/activationCode/use.do";

    /*1.74 获取视频微信分享信息*/
    public static String SHARE_INFO = MAIN_HOST + "/episode/shareInfo.do";

    /*1.75 打开应用通知（用户行为统计）*/
    public static String OPEN_APP = MAIN_HOST + "/user/action/openApp.do";

    /*1.76 视频播放汇报（用户行为统计）*/
    public static String PLAY_REPORT = MAIN_HOST + "/user/action/playReport.do";

    /*1.77 离开应用通知（用户行为统计）*/
    public static String LEAVE_APP = MAIN_HOST + "/user/action/leaveApp.do";

    /*1.78 获所有的分类*/
    public static String ALL_CATEGORYS = MAIN_HOST + "/index/allCategorys.do";

    /*1.79 获取广告、专题和收藏信息*/
    public static String CONTENT_INFO = MAIN_HOST + "/index/noContentInfo.do";

    /*1.80 获取TV端播放汇报*/
    public static String TV_REPORT = MAIN_HOST + "/play/report.do";

    /*1.81 查询TV端是否登录接口*/
    public static String TV_ISLOGIN = MAIN_HOST + "/TVterminal/isLogin.do";

    /*1.82 关闭远程应用接口*/
    public static String TV_CLOSE = MAIN_HOST + "/remoteDevice/close.do";

    /*1.83 远程应用关闭确认接口*/
    public static String TV_CLOSE_CONFIRM = MAIN_HOST + "/remoteDevice/close/confirm.do";

    /*1.84 设置跳过片头接口*/
    public static String PLAY_SKIP = MAIN_HOST + "/setup/playSkip.do";

    /*1.85 设置播放时长接口*/
    public static String PLAY_DURATION = MAIN_HOST + "/setup/playDuration.do";

    /*1.86 播放时长心跳接口-计算剩余时间*/
    public static String LEFT_PLAY_DURATION = MAIN_HOST + "/user/heartbeat/calculateLeftPlayDuration.do";

    /*1.87 发送定制节目列表接口*/
    public static String EPISODE_LIST = MAIN_HOST + "/user/remoteSend/episodeList.do";

    /*1.88 获取播放时长是否生效和是否跳过片头接口*/
    public static String GET_SETTING = MAIN_HOST + "/play/getSetting.do";

    /*1.89 用户昵称是否修改接口*/
    public static String IS_UPDATE_NAME = MAIN_HOST + "/user/isUpdateName.do";

    /*1.90 修改用户信息接口*/
    public static String UPDATE_USER_INFO = MAIN_HOST + "/user/updateUserInfo.do";

    /*1.91 按分类获取帖子列表(社区)*/
    public static String NOTELIST_OF_CATEGORY = MAIN_HOST + "/bbs/noteListOfCategory.do";

    /*1.92 帖子详情(社区)*/
    public static String NOTE_DETAIL = MAIN_HOST + "/bbs/noteDetail.do";

    /*1.93 获取上传文件到OSS的STS凭证*/
    public static String ASSUME_ROLE = MAIN_HOST + "/oss/assumeRole.do";

    /*1.94 用户举报帖子或评论（社区）*/
    public static String REPORT_PROBLEM = MAIN_HOST + "/bbs/reportProblem.do";

    /*1.95 删除帖子或评论*/
    public static String BBS_DELETE = MAIN_HOST + "/bbs/delete.do";


    /*1.99 我发布的帖子（社区）*/
    public static String MY_NOTE = MAIN_HOST + "/bbs/myNote.do";

    /*2.0 我发布的帖子评论（社区）*/
    public static String NOTE_COMMENT = MAIN_HOST + "/bbs/myNoteComment.do";

    /*2.02 获取定制播放列表*/
    public static String REMOTE_LIST = MAIN_HOST + "/remoteSend/list.do";


    /***********************************************************
     * ******************以上为http post 协议地址*****************
     ***********************************************************/


    public enum Client {  //客户端类型,不在枚举范围内时默认为web  ,Enum("android","ios","web","stb")
        android, ios, web, stb
    }

    public enum DeviceType {  //控制端类型
        move, tv
    }

    public static final String LANG = "zh-cn";  //语言，默认“zh-cn”

    public static final String PARTNER = "kidsmind"; //  合作商，分配给第三方集成的唯一标志, 可传kidsmind


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
