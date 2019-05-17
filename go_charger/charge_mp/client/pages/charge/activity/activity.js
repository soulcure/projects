// pages/charge/activity/activity.js
var qcloud = require('../../../vendor/wafer2-client-sdk/index')
var config = require('../../../config')
var util = require('../../../utils/util')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    rs:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getActivities();
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

  },

  /**
   * 获取活动列表
   */
  getActivities: function() {
    var that = this;
    var url = `${config.service.activityUrl}`;
    console.log("接口请求：",url);
    qcloud.request({
      url: url,
      login: false,
      method: 'POST',
      data: {},
      success(res) {
        that.formatTime(res.data.data);
        var newData = res.data.data;
        if (newData.length > 0) {
          that.setData({
            rs: that.data.rs.concat(newData)
          });
        } else {

        }
      },
      fail(error) {
        console.log('getActivities', error);
      }
    });
  },

  // 格式化时间
  formatTime: function (res) {
    //console.log(res)
    for (var i = 0; i < res.length; i++) {
      res[i].startTime = util.formatTime(res[i].startTime * 1000, false);
      res[i].endTime = util.formatTime(res[i].endTime * 1000, false);
    }
  },

  // 跳转到详情页
  showDetail: function (p) {
    wx.navigateTo({
      url:"../activityDetail/activityDetail?id="+p.currentTarget.dataset.id
    });
  }
})