// pages/charge/my/wallet/rechare/rechare.js
var qcloud = require('../../../../../vendor/wafer2-client-sdk/index')
var config = require('../../../../../config')
var util = require('../../../../../utils/util.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    rechargeValueArr: [0.1, 100, 200, 500, 1000, 2000],
    currentIndex: 0,
    cash: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {

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

  selectValue: function(e) {
    this.setData({
      currentIndex: e.currentTarget.dataset.currentindex,
      cash: ''
    })
  },

  inputCash: function(e) {
    console.log(e.detail.value)
    this.setData({
      cash: e.detail.value
    })
  },

  inputFocus: function() {
    this.setData({
      currentIndex: -1
    })
  },

  /* 充值  */
  recharge: function(res) {
    var that = this;
    var value = (that.data.cash != '' ? that.data.cash : that.data.rechargeValueArr[that.data.currentIndex]) * 100;
    console.log('recharge--'+value)
    wx.login({
      success(res) {
        if (res.code) {
          that.payRequest(res.code, value);
        } else {
          console.log('登录失败！' + res.errMsg)
        }
      }
    });
  },

  payRequest: function(code, sum) {
    var that = this;
    var date = Date.parse(new Date);
    var timeStamp = date / 1000;
    qcloud.request({
      url: `${config.service.payUrl}`,
      login: false,
      method: 'POST',
      data: {
        js_code: code, //用户微信平台js_code
        total_fee: sum //充值金额 单位分
      },
      success(res) {
        console.log('payRequest', res);
        var data = res.data.data;
        wx.requestPayment({
          timeStamp: data.timeStamp,
          nonceStr: data.nonceStr,
          package: data.package,
          signType: data.signType,
          paySign: data.paySign,
          success(res) {
            console.log("requestPayment  success", res);
            util.showModel("提示", "充值成功！");
            wx.navigateBack({

            })
          },
          fail(res) {
            console.log("requestPayment  fail", res);
            util.showModel("提示", "充值失败，再试一下！");
          }
        })
      },
      fail(error) {
        console.log('payRequest', error);
      }
    });
  },

})