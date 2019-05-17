var constants = require('./constants');
var utils = require('./utils');
var Session = require('./session');
var loginLib = require('./login');

var noop = function noop() {};

var buildAuthHeader = function buildAuthHeader(session) {
  var header = {};

  if (session) {
    header[constants.WX_HEADER_SKEY] = session;
  }

  return header;
};

/***
 * @class
 * 表示请求过程中发生的异常
 */
var RequestError = (function() {
  function RequestError(type, message) {
    Error.call(this, message);
    this.type = type;
    this.message = message;
  }

  RequestError.prototype = new Error();
  RequestError.prototype.constructor = RequestError;

  return RequestError;
})();

function request(options) {
  if (typeof options !== 'object') {
    var message = '请求传参应为 object 类型，但实际传了 ' + (typeof options) + ' 类型';
    throw new RequestError(constants.ERR_INVALID_PARAMS, message);
  }

  var requireLogin = options.login;
  var success = options.success || noop;
  var fail = options.fail || noop;
  var complete = options.complete || noop;
  var originHeader = options.header || {};

  // 成功回调
  var callSuccess = function() {
    success.apply(null, arguments);
    complete.apply(null, arguments);
  };

  // 失败回调
  var callFail = function(error) {
    fail.call(null, error);
    complete.call(null, error);
  };

  // 是否已经进行过重试
  var hasRetried = false;

  if (requireLogin) {
    doRequestWithLogin();
  } else {
    doRequest();
  }

  // 登录后再请求
  function doRequestWithLogin() {
    loginLib.loginWithCode({
      success: doRequest,
      fail: callFail
    });
  }

  // 实际进行请求的方法
  function doRequest() {
    var authHeader = {}

    var session = Session.get();

    if (session) {
      authHeader = buildAuthHeader(session.skey);
    }

    wx.request(utils.extend({}, options, {
      header: utils.extend({}, originHeader, authHeader),

      success: function(response) {
        if (response.statusCode == 200) {
          var data = response.data;
          console.log("request", response)
          var error, message;
          if (data && (data.code == constants.BC_SUCCESS || data.code == constants.BC_RE_REGIST)) {
            callSuccess.apply(null, arguments);
          } else {

            if (data.code == constants.BC_TOKEN_ERR /*token失效*/ || data.code == constants.BC_NO_LOGIN /*未登录*/ ) {
              Session.clear();
            }
            /* 目前接口不支持此种登录方式 */
            //如果是登录态无效，并且还没重试过，会尝试登录后刷新凭据重新请求
            // if (!hasRetried) {
            //     hasRetried = true;
            //     doRequestWithLogin();
            //     return;
            // }

            message = data.msg + '(' + data.code + ')'; //;
            error = new RequestError(data.code, message);

            callFail(error);
            return;
          }
        }else{
          message = '服务异常' + '(' + response.statusCode + ')'; //服务异常返回异常
          error = new RequestError(response.statusCode, message);

          callFail(error);
        }
      },

      fail: callFail,
      complete: noop,
    }));
  };

};

module.exports = {
  RequestError: RequestError,
  request: request,
};