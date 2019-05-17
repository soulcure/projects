// pages/charge/my/chargeOrderList/chargeDetail/chargeDetail.js
var qcloud = require('../../../../../vendor/wafer2-client-sdk/index')
var config = require('../../../../../config')
var util = require('../../../../../utils/util')

Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  getOrderDetail: function (orderSeq) {
    var that = this;
    var session = qcloud.Session.get();
    var pageNo = that.data.pageNo + 1;

    qcloud.request({
      url: `${config.service.recordDetailUrl}`,
      login: false,
      method: 'POST',
      data: {
        seq: orderSeq, 
      },
      success(res) {
        console.log('getOrderDetail', res);

        //that.formatTime(res.data.data);

      },
      fail(error) {
        console.log('getOrderDetail', error);
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log('onLoad', options.seq);
    this.getOrderDetail(options.seq)
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})