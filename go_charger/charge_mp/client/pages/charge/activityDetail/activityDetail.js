// pages/charge/activityDetail/activityDetail.js
var qcloud = require('../../../vendor/wafer2-client-sdk/index')
var config = require('../../../config')
var util = require('../../../utils/util')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    activity: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    console.log(options);
    this.getActivityDetail(options.id);

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

  /**
   * 获取活动详细信息
   */
  getActivityDetail: function(p) {
    var that = this;
    var data = {
      id: parseInt(p)
    };
    var url = `${config.service.activityDetailUrl}`;
    console.log("接口请求：", url, data);
    qcloud.request({
      url: url,
      login: false,
      method: 'POST',
      data: data,
      success(res) {
        console.log('getActivityDetail', res);
        var activity = res.data.data;
        activity.startTime = util.formatTime(activity.startTime * 1000, false);
        activity.endTime = util.formatTime(activity.endTime * 1000, false);
        that.setData({
          activity:activity
        });
      },
      fail(error) {
        console.log('getActivities', error);
      }
    });
  },

  goCharge: function() {
      wx.navigateTo({
        url: "../my/wallet/recharge/recharge"
      });
  }
})