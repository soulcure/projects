// pages/charge/my/chargeOrderList.js

var qcloud = require('../../../../vendor/wafer2-client-sdk/index')
var config = require('../../../../config')
var util = require('../../../../utils/util')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    pageNo: 0,
    orders: []
  },

  getOrders: function() {
    var that = this;
    var session = qcloud.Session.get();
    var pageNo = that.data.pageNo + 1;

    qcloud.request({
      url: `${config.service.recordUrl}`,
      login: false,
      method: 'POST',
      data: {
        remark: 0, //(0:查询消费记录,1:查询充值记录,-1或无参数查询所有记录)
        pageNo: pageNo,
        pageSize: 10
      },
      success(res) {
        console.log('getOrders', res.data.data);

        that.formatTime(res.data.data);

        //that.orders.push(res.data.data);
        var newData = res.data.data;
        if (newData.length>0) {
          that.setData({
            pageNo: pageNo,
            orders: that.data.orders.concat(newData)
          })
        } else {
          
        }
      },
      fail(error) {
        console.log('getOrders', error);
      }
    });
  },

  formatTime: function(res) {
    //console.log(res)
    for (var i = 0; i < res.length; i++) {
      res[i].time = util.formatTime(res[i].time * 1000, true);
    }
  },

  goToOrderDetail: function(e){
    console.log(e.currentTarget.dataset.index)

    var seq = this.data.orders[e.currentTarget.dataset.index].orderNum;

    wx.navigateTo({
      url: './chargeDetail/chargeDetail?seq='+ seq,
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    this.getOrders();
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
    console.log("onPullDownRefresh")
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {
    console.log("onReachBottom")
    this.getOrders();
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  }
})