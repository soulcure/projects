// pages/charge/my/walllet.js

var qcloud = require('../../../../vendor/wafer2-client-sdk/index')
var config = require('../../../../config')
var util = require('../../../../utils/util.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    balence: 0
  },

  getBalance: function() {
    var that = this;
    var session = qcloud.Session.get();
    qcloud.request({
      url: `${config.service.balanceUrl}`,
      login: false,
      method: 'POST',
      data: {
        "openId": session.userinfo.userId //微信平台用户id
      },
      success(res) {
        console.log('getBalance', res.data.data);
        that.setData({
          balence: res.data.data.money
        })
      },
      fail(error) {
        console.log('getBalance', error);
      }
    });
  },

  goRecharge: function(res) {
    wx.navigateTo({
      url: './recharge/recharge',
    })
  },

  /* 充值明细  */
  chargeDetail: function(res) {
    wx.navigateTo({
      url: './rechargeOrder/rechargeOrder',
    })
  },

  /* 提现  */
  withdraw: function(res) {

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
    this.getBalance();
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

  formSubmit(e) {
    console.log('sumbitId：' + e.detail.formId)

    this.setData({
      scanSubmitId: e.detail.formId
    })
  },

  formReset() {
    console.log('form发生了reset事件')
  }
})