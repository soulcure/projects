// pages/login/login.js
var qcloud = require('../../../vendor/wafer2-client-sdk/index')
var config = require('../../../config')
var util = require('../../../utils/util.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    page: '',
    appname: config.globalData.appName,
    isAuthTrue: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    if (options.page) {
      this.setData({
        page: options.page
      })
    }
  },

  bindGetUserInfo: function(res) {
    console.log("bindGetUserInfo", res);
    var that = this;
    if (res.detail.errMsg == "getUserInfo:ok" && res.detail.encryptedData && res.detail.iv) {
      this.login({
        success(result) {
          console.log('bindGetUserInfo login success', result);

          wx.hideLoading();
          //检查手机号绑定
          that.setData({
            isAuthTrue: true
          })
        },
        fail(error) {
          console.log('bindGetUserInfo login fail', error);
          wx.hideLoading();
          util.showError('登录失败!');
        }
      });
    }
  },

  getPhoneNumber: function(res) {
    console.log("getPhoneNumber", res);
    if (res.detail.errMsg == "getPhoneNumber:ok" && res.detail.encryptedData && res.detail.iv) {
      this.postPhoneNumber(res.detail);
    }
  },


  redirectTo: function(toPage) {

    if (toPage == "map") {
      wx.redirectTo({
        url: '../map',
      })
    } else if (toPage == "service") {
      wx.redirectTo({
        url: '../service/serviceCenter',
      })
    } else if (toPage == "my") {
      wx.redirectTo({
        url: '../my/my',
      })
    } else {
      console.log('navigateBack')
      wx.navigateBack({
        success: res => {
          console.log("navigateBack", res)
        },
        fail: err => {
          console.error("navigateBack", err)
        }
      })
    }
  },

  /**
   * login
   */
  login: function(cb) {
    var that = this
    wx.showLoading({
      title: '登录中...',
    })
    //服务端暂无二次登录的逻辑
    //var session = qcloud.Session.get();
    //console.log(session)
    //if (session) { 
    if (false) {
      qcloud.loginWithCode({
        success: res => {
          cb.success(res);
          console.log("login", res)
        },
        fail: err => {
          cb.fail(err);
          console.error("login", err)
        }
      })
    } else {
      // 首次登录
      qcloud.login({
        success: res => {
          cb.success(res);
        },
        fail: err => {
          cb.fail(err);
        }
      })
    }
  },

  postPhoneNumber: function(requestdata) {
    var that = this
    console.log('postPhoneNumber start');
    //服务端要求的每次都给CODE
    wx.login({
      success(res) {
        if (res.code) {
          // 构造请求头，包含 code、encryptedData 和 iv
          const header = {
            [qcloud.WX_HEADER_CODE]: res.code,
            [qcloud.WX_HEADER_ENCRYPTED_DATA]: requestdata.encryptedData,
            [qcloud.WX_HEADER_IV]: requestdata.iv
          }
          qcloud.request({
            url: `${config.service.getPhoneNumberUrl}`,
            header: header,
            login: false,
            success(result) {
              if (result.data.code == 0) {
                //刷新登录态（已绑定）
                var session = qcloud.Session.get();
                session.userinfo.status = result.data.data.status;
                session.userinfo.phone = result.data.data.phone;
                session.userinfo.countryCode = result.data.data.countryCode;
                qcloud.Session.set(session);

                that.redirectTo(that.data.page);
              } else if (result.data.code == qcloud.BC_RE_REGIST) {
                //重复注册手机号码
                that.redirectTo(that.data.page);
              } else {
                util.showSuccess(result.data.msg);
              }
            },
            fail(error) {
              console.log('postPhoneNumber fail', error);
              util.showModel("提示", error.message + "请重新登录试一下！");
              // 去掉获取手机号窗口
              that.setData({
                isAuthTrue: false
              })
            }
          })
        } else {
          console.log('登录失败！' + res.errMsg)
        }
      },

      fail(err) {
        console.log('登录失败！' + err)
      }
    })
  },

  confirmEvent: function() {
    console.log("confirmEvent");
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  },

  goAgreement: function (res) {
    wx.navigateTo({
      url: './userAgreement',
    })
  }
})