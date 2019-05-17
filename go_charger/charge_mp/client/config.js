/**
 * 小程序配置文件
 */

// 此处主机域名修改成腾讯云解决方案分配的域名
//var host = 'http://192.168.0.66:8899';   
//var host = 'http://192.168.0.108:8899';
//var host = 'https://charge.huxin.biz:7654';
var host = 'https://s.yoomaa.cn';
//var host = 'http://192.168.0.42:7654';  


var config = {
  // 应用名称，地图key 等信息
  globalData: {
    mapKey: '6RCBZ-N2LW5-T22IO-QAQD6-ZBRZE-U3FZU',
    appName: '小蓝快充'
  },

  // 下面的地址配合云端 Demo 工作
  service: {
    host,

    // 登录地址，用于建立会话
    loginUrl: `${host}/api/login`,

    loginOutUrl: `${host}/api/open/logout`,
    // 手机号
    getPhoneNumberUrl: `${host}/api/open/regist`,
    // 获取充电桩
    getChargeDeviceUrl: `${host}/api/open/getPosition`,
    // 支付
    payUrl: `${host}/api/inner/charger`,
    // 扫码充电
    scanCodeUrl: `${host}/api/inner/scanCode`,
    // 充电状态
    chargerStateUrl: `${host}/api/inner/charging`,
    // 余额
    balanceUrl: `${host}/api/inner/balance`,
    // 充电与充值帐单
    recordUrl: `${host}/api/inner/accountRecord`,
    // 充电与充值帐单详情
    recordDetailUrl: `${host}/api/inner/userChargeRecord`,
    // 活动
    activityUrl: `${host}/api/open/getActivity`,
    activityDetailUrl: `${host}/api/open/actDetail`
  }
};

module.exports = config;