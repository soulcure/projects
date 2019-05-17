// pages/charge/chargerList.js

Page({

  /**
   * 页面的初始数据
   */
  data: {
    markers: [
      // {
      //   title: 'dddddddd',
      //   address: 'dddddddddddddddd'
      // }
    ]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var pages = getCurrentPages();
    var prevPage = pages[pages.length - 2];

    if (prevPage.data && prevPage.data.markers) {
      this.setData({
        markers: prevPage.data.markers
      });
    }
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

  naviTo: function(e) {
    console.log(e.currentTarget.dataset.index);
    var index = e.currentTarget.dataset.index;
    if (index >= 0 && index < this.data.markers.length) {
      var loc = this.data.markers[index];
      wx.openLocation({
        latitude: loc.latitude,
        longitude: loc.longitude,
        scale: 18,
        name: loc.title,
        address: loc.address,
        success(res) {
          console.log("openLocation" + res);
        },
        fail(res) {
          console.log("openLocation fail");
        },
        complete(res) {
          console.log("openLocation complete");
        }
      })
    }
  }
})