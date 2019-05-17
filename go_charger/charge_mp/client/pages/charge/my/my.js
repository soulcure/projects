// pages/charge/my.js
var qcloud = require('../../../vendor/wafer2-client-sdk/index')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    phoneNumber: '',
    nickname: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var session = qcloud.Session.get();

    //隐藏手机号中间4位(例如:12300102020,隐藏后为132****2020)
    var oriPhone = session.userinfo.phone;
    oriPhone = oriPhone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    
    this.setData({
      phoneNumber: oriPhone
    })
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

  /* 钱包页面 */
  goWallet: function() {
    wx.navigateTo({
      url: './wallet/wallet',
    })
  },

  /* 订单记录 */
  goChargeOrderList: function() {
    wx.navigateTo({
      url: './chargeOrderList/chargeOrderList',
    })
  }
})