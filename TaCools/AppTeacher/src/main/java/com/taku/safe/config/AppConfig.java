package com.taku.safe.config;


public class AppConfig {


    /**
     * toucool 服务器连接配置
     */
    private final static int LAUNCH_MODE = 0; //0 本地测试,开发版        1正式平台,正式版

    private final static String HTTP_HOST[] = new String[]{"http://safetest.tou-cool.com:8080", "http://studentsafe.tou-cool.com:8080"};


    private static String getHttpHost() {
        return HTTP_HOST[LAUNCH_MODE];
    }


    private static final String PROTOCOL_VERSION = "1.0";    //协议版本


    /* #######################################通用API####################################### */
    /**
     * 获取组织机构列表
     */
    public static final String COMMON_SCHOOL_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/common/school/list";

    /**
     * 上报友盟推送token和硬件信息（调用多次只存一个）
     */
    public static final String COMMON_DEVICE_INFO_UPLOAD = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/common/deviceinfo/upload";

    /**
     * 获取验证码(通用接口，绑定手机号和验证码)
     */
    public static final String GET_SMS_CODE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/common/getsmscode";

    /**
     * V1.2 新增
     * 根据imei更新mac信息和手机版本
     */
    public static final String COMMON_USERDEVICE_INFO_UPDATE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/common/usermacinfo/update";
    /**
     * V1.2 新增
     * app 版本升级接口
     */
    public static final String COMMON_APP_UPDATE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/common/appversion";

    /* ############################################ 学生相关API begin ##################################### */
    /**
     * 学生注册
     */
    public static final String STUD_REGISTER = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/register";

    /**
     * 学生登录
     */
    public static final String STUD_LOGIN = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/login";

    /**
     * 学生绑定
     */
    public static final String STUD_BIND = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/bind";

    /**
     * 学生忘记密码
     */
    public static final String STUD_RESETPWD = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/resetpwd";

    /**
     * 学生修改密码
     */
    public static final String STUDENT_CHANGEWD = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/changepwd";

    /**
     * 学生重新绑定手机
     */
    public static final String RE_BIND_DEVICES = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/changedevice";

    /**
     * 学生修改登录手机号
     */
    public static final String STUDENT_CHANGE_MOBILE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/changephone";

    /**
     * 学生获取作息签到信息
     */
    public static final String STUDENT_INFO = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/signinfo/detail";

    /**
     * 学生作息签到
     */
    public static final String SIGN_IN = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/rest/signin";

    /**
     * 学生按月获取作息签到列表
     */
    public static final String SIGN_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/rest/sign/list";

    /**
     * 学生作息签到详情
     */
    public static final String SIGN_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/rest/sign/detail";

    /**
     * 学生实习签到
     */
    public static final String PRACTICE_SIGN_IN = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/internship/signin";

    /**
     * 学生按月获取实习签到列表
     */
    public static final String PRACTICE_SIGN_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/internship/sign/list";

    /**
     * 实习签到详情
     */
    public static final String PRACTICE_SIGN_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/internship/sign/detail";

    /**
     * 学生sos发起求助
     */
    public static final String SOS_NEW = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/sos/new";

    /**
     * 学生sos救援位置定时上报
     */
    public static final String SOS_REPORT_LOC = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/sos/reportloc";

    /**
     * 学生sos救援语音定时上报
     */
    public static final String SOS_REPORT_VOICE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/sos/reportvoice";

    /**
     * 学生sos救援终止-我已安全
     */
    public static final String SOS_STOP = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/sos/stop";

    /**
     * 学生发送校长直通车
     */
    public static final String PRESIDENT_MSG_NEW = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/presidentmsg/new";

    /**
     * 学生获取校长直通车列表
     */
    public static final String PRESIDENT_MSG_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/presidentmsg/list";

    /**
     * 获取学生个人信息
     */
    public static final String STUDENT_USERINFO_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/userinfo/detail";

    /**
     * 学生设置个人信息
     */
    public static final String STUDENT_USERINFO_UPDATE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/userinfo/update";

    /**
     * 学生上报用户反馈
     */
    public static final String STUDENT_USER_FEEDBACK = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/stud/appissue/report";


    /* ################################################# 老师管理者API begin ############################################### */
    /**
     * 老师登陆
     */
    public static final String TEACHER_LOGIN = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/login";

    /**
     * 老师修改登录手机号
     */
    public static final String TEACHER_CHANGE_MOBILE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/changephone";

    /**
     * 老师修改密码
     */
    public static final String TEACHER_CHANGEWD = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/changepwd";

    /**
     * 学生忘记密码
     */
    public static final String TEACHER_RESETPWD = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/resetpwd";

    /**
     * 老师获取SOS列表
     */
    public static final String TEACHER_SOS_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/sos/list";

    /**
     * 老师获取救援详情
     */
    public static final String TEACHER_SOS_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/sos/detail";

    /**
     * 老师定时更新救援详情
     */
    public static final String TEACHER_SOS_LATEST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/sos/latest";

    /**
     * 获取老师个人信息
     */
    public static final String TEACHER_USERINFO_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/userinfo/detail";

    /**
     * 设置老师个人信息
     */
    public static final String TEACHER_USERINFO_UPDATE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/userinfo/update";

    /**
     * 老师上报用户反馈
     */
    public static final String TEACHER_USER_FEEDBACK = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/appissue/report";

    /**
     * 获取老师组织机构信息
     */
    public static final String TEACHER_USERINFO = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/userinfo";

    /**
     * 老师查询 作息统计数据--(按天和机构查询）
     */
    public static final String TEACHER_REST_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/statistics";

    /**
     * 老师查询 作息打卡信息--(按机构 状态 时间段查询)
     */
    public static final String TEACHER_REST_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/singin/list";

    /**
     * 获取学生单次作息打卡详情
     */
    public static final String TEACHER_SIGN_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/sign/detail";

    /**
     * 老师批注学生作息打卡信息
     */
    public static final String TEACHER_SIGN_APPROVE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/sign/approve";

    /**
     * 老师查询 实习统计数据--(按天和机构查询）
     */
    public static final String TEACHER_INTERNSHIP_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/statistics";

    /**
     * 老师查询 实习打卡信息--(按机构 状态 时间段查询)
     */
    public static final String TEACHER_INTERNSHIP_SINGIN_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/singin/list";

    /**
     * 获取学生单次实习打卡详情
     */
    public static final String TEACHER_INTERNSHIP_SINGIN_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/sign/detail";

    /**
     * 老师批注学生实习打卡信息
     */
    public static final String TEACHER_INTERNSHIP_SINGIN_APPROVE = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/sign/approve";

    /* ########################################################### 老师管理者API end ######################################################## */


    /* ########################################################### 硬件分析统计 begin ######################################################## */

    /**
     * 获取学生失联统计信息
     */
    public static final String DEVICE_STATS_NO_CONNECT_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/devicestats/losscontact/list";

    /**
     * 获取超级宅统计信息
     */
    public static final String DEVICE_STATS_INDOOR_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/devicestats/indoor/list";

    /**
     * 获取异常出入详细信息
     */
    public static final String DEVICE_STATS_UNUSUAL_OUT_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/devicestats/unusualout/list";

    /**
     * 获取异常出入详细信息
     */
    public static final String DEVICE_STATS_UNUSUAL_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/devicestats/unusualout/detail";


    /**
     * 获取严重逃课统计信息
     */
    public static final String DEVICE_STATS_NO_CLASS_LIST = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/devicestats/absentschool/list";

    /**
     * 获取超级宅详细信息
     */
    public static final String DEVICE_STATS_INDOOR_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/devicestats/indoor/detail";

    /**
     * 获取严重逃课详细信息
     */
    public static final String DEVICE_STATS_NO_CLASS_DETAIL = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/devicestats/absentschool/detail";


    /* ########################################################## 硬件分析统计API end ######################################################## */



    /* ########################################################## 老师决策者API begin ######################################################## */
    /**
     * 作息异常统计分析-- 按(school id)
     */
    public static final String TEACHER_UNUSUAL_COLLEGE_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/unusual/college/statistics";

    /**
     * 作息异常统计分析--按(college id)
     */
    public static final String TEACHER_REST_ANALYSIS_CLASS_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/unusual/class/statistics";

    /**
     * 作息异常统计分析--按(class id)
     */
    public static final String TEACHER_REST_ANALYSIS_STUDENT_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/unusual/class/ranklist";

    /**
     * 作息异常统计分析--按(student id)
     */
    public static final String TEACHER_REST_ANALYSIS_STUDENT_DAYS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/rest/unusual/student/days";

    /**
     * 实习异常统计分析-- 按(school id)
     */
    public static final String TEACHER_INTERNSHIP_COLLEGE_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/unusual/college/statistics";

    /**
     * 实习异常统计分析--按(college id)
     */
    public static final String TEACHER_INTERNSHIP_ANALYSIS_CLASS_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/unusual/class/statistics";

    /**
     * 实习异常统计分析--按(class id)
     */
    public static final String TEACHER_INTERNSHIP_ANALYSIS_STUDENT_STATISTICS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/unusual/class/ranklist";

    /**
     * 实习异常统计分析--按(student id)
     */
    public static final String TEACHER_INTERNSHIP_ANALYSIS_STUDENT_DAYS = getHttpHost() + "/api/" + PROTOCOL_VERSION + "/teacher/internship/unusual/student/days";

    /* ######################################################### 老师决策者API end ######################################################### */
}
