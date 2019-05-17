// pages/charge/map.js
var qcloud = require('../../vendor/wafer2-client-sdk/index')
var util = require('../../utils/util.js')
var config = require('../../config')

// 引入SDK核心类
var QQMapWX = require('../../qqmap-wx-jssdk.js');
var qqmapsdk;

const QUERY_PERIOD = 10 * 1000;

Page({

  /**
   * 页面的初始数据
   */
  data: {
    //用户个人信息
    userInfo: {
      avatarUrl: "", //用户头像
      nickName: "", //用户昵称
    },

    isLoged: false, // 是否登录

    currLongitude: 113.952942,
    currLatitude: 22.538498,

    markers: [],
    currentMarkerId: 0,
    isShowChagerLoc: false,
    scanSubmitId: '',
    finishSubmitId: '',

    stateInverval: '',
    timeout: 0,

    //充电状态
    chargingState: '正在充电',
    //充电时长
    chargingTime: {
      hour: 0,
      minute: 0
    },
    //充电费用
    chargingFee: '',
    //充电度数
    chargingWatt: '',
    //汽车电池电量
    chargingDegree: 30, // 0,
    //汽车充满时间
    chargingRemainTime: 0,
    //当前充电桩编号
    chargingCode: '',
    //充电状态
    isCharging: 0,

    isStopCharging: false,
    //充电动画进度值
    progressCount: 0,
    //充电动画
    progressOffset: 5,
    //充电动画定时器 setInterval
    chargeProgressTimer: '',

    // 活动列表
    hasActivities: 0,

    // 计时开始时间
    timeCounterStartTime: 0,

    // 计时时间
    timeCounter: '00:00:00',

    // 计时定时器变量
    timeCounterIntervalHandle: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    // 实例化API核心类
    qqmapsdk = new QQMapWX({
      key: config.globalData.mapKey
    });

    // 更新是否有活动在进行的变量
    this.updateHasActivities();

    this.startItervalState();

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
    console.log('onShow');
    var that = this;
    this.isLogin('map', function () {
      //第一次进入，自动获取附近充电桩
      that.getLocation();
    });
    this.timeCounterInterval();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {
    console.log('onHide');
    clearInterval(this.data.stateInverval);
    clearInterval(this.data.timeCounterIntervalHandle);
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {
    console.log('onUnload');
    clearInterval(this.data.stateInverval);
    clearInterval(this.data.timeCounterIntervalHandle);
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

  controltap: function(e) {
    console.log("controltap---" + e.controlId)
    var controlId = e.controlId;
    switch (controlId) {
      case 1:
        this.getLocation();
        break;
      case 2:
        var callback = this.goServiceCenter;
        this.checkLogin(callback);
        break;
    }
  },

  checkLogin: function(callback) {
    wx.checkSession({
      success() {
        callback(); // session_key 未过期，并且在本生命周期一直有效
        console.log("checkSession---success")
      },
      fail() {
        // session_key 已经失效，需要重新执行登录流程

        // 查看是否授权
        wx.getSetting({
          success(res) {
            if (res.authSetting['scope.userInfo']) {
              // 已经授权，可以直接调用 getUserInfo 获取头像昵称
              wx.getUserInfo({
                success(res) {
                  console.log(res.userInfo)
                }
              })
            }
          },
          fail(res) {
            console.log(res);
            bind
          }
        })
      }
    })
  },

  /* 获取现在位置附近充电桩 */
  getLocation: function() {
    var that = this;
    wx.getLocation({
      success: function(res) {
        console.log("getLocation", res);
        that.setLocationAndSearchCharger(res);
        var mks = [];
        mks.push({ // 获取返回结果，放到mks数组中
          title: "当前位置",
          id: 0,
          code: "",
          latitude: res.latitude,
          longitude: res.longitude,
          address: "",
          iconPath: "./resources/location.png", //图标路径
          width: 60,
          height: 60
        })
        that.setData({
          markers: mks
        })
      }
    })
  },

  /* 选择地图位置附近充电桩 */
  chooseLocation: function() {
    var that = this;
    wx.chooseLocation({
      success: function(res) {
        console.log(res);
        that.setLocationAndSearchCharger(res);
      },
      fail: function(res) {},
      complete: function(res) {},
    })
  },

  /* 获取提供经纬度附近充电桩 */
  setLocationAndSearchCharger: function(res) {
    var that = this;
    this.setData({
      currLongitude: res.longitude,
      currLatitude: res.latitude
    })

    qcloud.request({
      url: `${config.service.getChargeDeviceUrl}`,
      login: false,
      method: 'POST',
      data: {
        longitude: res.longitude, // 22.520168586, //res.longitude,
        latitude: res.latitude //144.0710449219 //res.latitude
      },
      success(res) {
        console.log('setLocationAndSearchCharger', res);
        if (res.data.data) {
          var data = res.data.data;
          var mks = that.data.markers;
          for (var i = 0; i < data.length; i++) {
            mks.push({ // 获取返回结果，放到mks数组中
              title: data[i].name,
              id: i+1,
              code: data[i].code,
              latitude: data[i].latitude,
              longitude: data[i].longitude,
              address: data[i].address,
              iconPath: "./resources/marker.png", //图标路径
              width: 40,
              height: 40
            })
          }
          that.setData({ //设置markers属性，将搜索结果显示在地图中
            markers: mks
          })
        }
        console.log("-----------------充电桩--------------------");
        console.log(that.data.markers);
      },
      fail(error) {
        console.log('setLocationAndSearchCharger', error);
      }
    });


    //搜索地图
    //var currLoc = this.data.currLatitude + ',' + this.data.currLongitude;
    //this.searchMap("充电桩", currLoc);
  },

  /* 获取提供经纬度附近充电桩 */
  searchMap: function(searchkey, currLoc) {
    var that = this;
    console.log(currLoc);
    qqmapsdk.search({
      keyword: searchkey,
      location: currLoc, //设置周边搜索中心点
      success: function(res) {
        //console.log(res);
        var mks = []
        for (var i = 0; i < res.data.length; i++) {
          mks.push({ // 获取返回结果，放到mks数组中
            title: res.data[i].title,
            id: res.data[i].id,
            latitude: res.data[i].location.lat,
            longitude: res.data[i].location.lng,
            address: res.data[i].address,
            iconPath: "./resources/marker.png", //图标路径
            width: 40,
            height: 40
          })
        }

        mks.push(that.data.markers);
        that.setData({ //设置markers属性，将搜索结果显示在地图中
          markers: mks
        })

        console.log("-----------------充电桩--------------------");
        console.log(that.data.markers);
      },
      fail: function(res) {
        console.log(res);
      },
      complete: function(res) {
        console.log(res);
      }
    });
  },

  /* 扫码 
    scancode   
     1: 超时    
     2: 充电桩不可用  
     3: 充电桩正在使用
     4：未插枪
  */
  scanCode: function() {
    var that = this;

    if (that.data.isCharging) {
      util.showModel("提示", "您已经在充电！");
      //return;
    }
    var scanfunc = function() {
      wx.scanCode({
        onlyFromCamera: true,
        success: function(res) {
          console.log(res);
          var codeArr = that.parseChargeCode(res.result);
          that.setData({
            chargingCode: codeArr
          })
          that.requestCharge(that.data.chargingCode);
        },
        fail: function(res) {
          if (res.errMsg != 'scanCode:fail cancel') {
            util.showModel("扫码", "扫码失败！");
          }
          console.log(res);
        },
        complete: function(res) {}
      });
    }

    //this.isLogin('', scanfunc);
    scanfunc();
  },

  requestCharge: function(code) {
    var that = this;
    var session = qcloud.Session.get();
    qcloud.request({
      url: `${config.service.scanCodeUrl}`,
      login: false,
      method: 'POST',
      data: {
        stationCode: code,
        code: code,
        userId: session.userinfo.userId,
        pointNum: 1,
        type: 0,
        timer: 0,
        strategy: 0,
        parameter: 0,
        scanSubmitId: that.data.scanSubmitId
      },
      success(res) {
        console.log('scanCode success', res);
        that.startItervalState();
        //wx.hideLoading();
        wx.showLoading({
          title: '正在启动中，可能需要2分钟...',
        })
      },
      fail(error) {
        console.log('scanCode fail', error);
        if (error.type == 4) {
          wx.hideLoading()
          wx.showModal({
            title: '提示',
            content: '请插入充电抢~~~',
            showCancel: true,
            cancelText: '不充了',
            cancelColor: '#ccc',
            confirmText: '已插入',
            confirmColor: '#90ee90',
            success(res) {
              if (res.confirm) {
                console.log('用户点击已插入')
                //that.requestCharge(that.data.chargingCode);
                wx.showLoading({
                  title: '准备设备...',
                })

                setTimeout(function() {
                  that.requestCharge(that.data.chargingCode)
                }, 1000 * 10);
              } else if (res.cancel) {
                console.log('用户点击取消')
              }
            }
          })
        } else {
          util.showModel("提示", error.message);
        }
      }
    });
  },

  chargerState: function() {
    var that = this;
    qcloud.request({
      url: `${config.service.chargerStateUrl}`,
      login: false,
      success(res) {
        console.log('chargerState success', res);
        var data = res.data.data;
        if (data.state == 1 || data.state == 2) {
          wx.hideLoading();
          that.updateChargeState(data);
          // that.showChargingUi();
          that.showWave();
        } else if (data.state == 3) {
          data.state = 0;
          that.updateChargeState(data);

          // that.showChargingUi();

          wx.hideLoading()
          clearInterval(that.data.stateInverval);
          clearInterval(that.data.timeCounterIntervalHandle);
          that.setData({
            isCharging: 0,
            stateInverval: '',
            timeout: 0,
            timeCounterIntervalHandle: null
          })
          wx.navigateTo({
            url:'./my/chargeOrderList/chargeOrderList'
          });
        } else {
          // 由于状态是通过轮询方式取得
          // 结束状态 3 很快变成空闲状态0 
          // 现在通话超过3次查询，来认为是结束充电，并进入空间状态。
          // 空间状态则移除轮询定时器
          if (that.data.timeout > 1) {
            wx.hideLoading();
            clearInterval(that.data.stateInverval);
            clearInterval(that.data.timeCounterInverval);
            that.setData({
              isCharging: 0,
              stateInverval: '',
              timeout: 0,
              timeCounterIntervalHandle: null
            })
          } else {
            that.setData({
              timeout: that.data.timeout + 1
            })
          }
        }
        // for test start 
        // that.setData({
        //   isCharging: 1
        // })
        // that.showChargingUi();
        // that.showWave();
        // for test end
      },
      fail(error) {
        console.log('chargerState fail', error);
      }
    })
  },

  updateChargeState: function(data){
    //carstate    0 未连接 1 交流连接 2 直流连接    
    var that = this;
    var stateInfo = '';
    if (data.state == 1){
      stateInfo = '充电桩启动中...';
    } else if (data.state == 2) {
      stateInfo = '正在充电...';
    } else if (data.state == 3) {
      if (data.carstate == 0) {
        stateInfo = '充电结束';
      } else{
        stateInfo = '已停止充电，请拨枪';
      }
    }

    var st = new Date().getTime() - data.time * 1000;
    this.setData({
      isCharging: data.state,
      chargingState: stateInfo,
      chargingTime: that.formartChargeTime(data.time),
      chargingFee: data.money,
      chargingWatt: data.electric,
      chargingDegree: data.soc,
      chargingRemainTime: data.remainTime,
      chargingCode: data.code,
      isStopCharging: true,
      timeout: 0,
      timeCounterStartTime: st 
    });
  },

  startItervalState: function() {
    var stateInverval = setInterval(this.chargerState, QUERY_PERIOD);
    this.setData({
      stateInverval: stateInverval
    })
    this.chargerState();
  },

  /* second to h.m  */
  formartChargeTime: function(oriTime) {
    console.log(oriTime);
    var sec = oriTime; // / 1000;
    var hour = parseInt(sec / 3600);
    var minute = parseInt((sec - hour * 3600) / 60);
    console.log(hour, minute);
    return {
      hour: hour,
      minute: minute
    }
  },

  goMy: function() {
    console.log("goMy");
    //this.isLogin("my", function() {
    wx.navigateTo({
      url: './my/my',
    })
    //});
  },

  goChargerList: function() {
    if (this.data.markers.length == 0) {
      util.showModel("提示", "未找到附近的充电桩，换个位置试一下哦！");
      return;
    }
    wx.navigateTo({
      url: './chargerList',
    })
  },

  goServiceCenter: function() {
    console.log("goServiceCenter");

    //this.isLogin("service", function() {
    wx.navigateTo({
      url: './service/serviceCenter',
    })
    //});
  },

  /* 用户登录
    page: 未登录，跳转的页面
    callback：已经登录，继续执行的函数
  */

  isLogin: function(page, callback) {
    // 查看是否授权
    wx.getSetting({
      success(res) {
        console.log(res);
        var session = qcloud.Session.get();
        //console.log(session);
        if (res.authSetting['scope.userInfo'] && session && session.userinfo && (session.userinfo.status == 1)) {
          // 已经授权userInfo
          console.log("已经授权userInfo");
          if (callback != null) {
            callback();
          }
        } else {
          // 未授权userInfo
          console.log("未授权userInfo");
          var url;
          if (page == '') {
            url = './login/login';
          } else {
            url = './login/login?page=' + page;
          }
          wx.redirectTo({
            url: url,
          })
        }
      },
      fail(res) {
        console.log(res);
      }
    })
  },

  parseChargeCode: function(res) {
    /*var arr = res.split("?");
    if (arr.length > 1) {
      arr = arr[1].split("&");
    }
    var chargeCode;
    var chargePort;
    if (arr.length > 0) {
      chargeCode = arr[0].split("=");
    }
    if (arr.length > 1) {
      chargePort = arr[1].split("=");
    }

    return [chargeCode[1], chargePort[1]];*/

    var arr = res.split("/");
    var length = arr.length;
    return arr[length - 1];
  },

  formSubmit(e) {
    console.log('sumbitId：' + e.detail.formId)

    this.setData({
      scanSubmitId: e.detail.formId
    })
  },

  formReset() {
    console.log('form发生了reset事件')
  },

  markertap(e) {
    console.log(e);
    this.setData({
      isShowChagerLoc: true,
      currentMarkerId: e.markerId
    })
  },

  naviTo: function(e) {
    var loc = this.data.markers[this.data.currentMarkerId];
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
  },

  maptap: function() {
    console.log("maptap");
    if (this.data.isShowChagerLoc) {
      this.setData({
        isShowChagerLoc: false
      })
    }
  },

  stopCharge: function() {
    var that = this;
    var session = qcloud.Session.get();
    qcloud.request({
      url: `${config.service.scanCodeUrl}`,
      login: false,
      method: 'POST',
      data: {
        stationCode: that.data.chargingCode,
        code: that.data.chargingCode,
        userId: session.userinfo.userId,
        pointNum: 0,
        type: 3,
        timer: 0,
        strategy: 0,
        parameter: 0,
        scanSubmitId: that.data.finishSubmitId
      },
      success(res) {
        console.log('stopCharge success', res);
        clearInterval(that.data.stateInverval);
        clearInterval(that.data.timeCounterIntervalHandle);
        that.setData({
          isStopCharging: false,
          chargingState: '正在停止...',
          timeCounterIntervalHandle: null
        })
        setTimeout(function() {
          that.startItervalState();
        }, 1000 * 20);
      },
      fail(error) {+
        console.log('stopCharge fail', error);
        util.showModel("提示", error.message);
      }
    });
  },

  showWave: function() {
    // 获得 wave 组件
    if (!this.wave) {
      console.log("showWave", "create wave")
      this.wave = this.selectComponent("#chargeWave");

      this.wave.drawWave()
    }
  },

  showChargingUi: function() {
    if (this.data.isCharging == 0) {
      return;
    }

    // // 获得 circle 组件
    if (!this.circle) {
      console.log("showChargingUi", "create circle")
      this.circle = this.selectComponent("#circleProgress");

      // 绘制背景圆环
      this.circle.drawCircleBg(100, 15)
      // 绘制彩色圆环 
      this.circle.drawCircle(100, 15, this.data.chargingDegree / 50);
    }
    if (this.data.isCharging == 2) {
      this.data.progressCount = this.data.chargingDegree - this.data.progressOffset < 0 ? 0 : this.data.chargingDegree - this.data.progressOffset;
      this.chargeProgressInterval();
    }
  },

  /* 进度条动画 */
  chargeProgressInterval: function() {
    if (this.data.chargeProgressTimer == '') {
      var countTimer = setInterval(() => {
        console.log("chargeProgressInterval", this.data.progressCount)
        if (this.data.progressCount < 100 && (this.data.progressCount < this.data.chargingDegree)) {
          // 绘制彩色圆环进度条
          this.circle.drawCircle(100, 15, this.data.progressCount / 50)
          this.data.progressCount = this.data.progressCount + 1;
        } else {
          //this.circle.clearCircle((this.data.progressCount - 10) / 50, this.data.progressCount / 50);
          if (this.data.isCharging == 3) {
            clearInterval(this.data.chargeProgressTimer);
            this.setData({
              chargeProgressTimer: ''
            })
          } else {
            this.data.progressCount = this.data.chargingDegree - this.data.progressOffset < 0 ? 0 : this.data.chargingDegree - this.data.progressOffset;
          }
        }
      }, 500)

      this.setData({
        chargeProgressTimer: countTimer
      })
    }
  },

  preventTouchMove: function() {},


  /* 跳转到活动列表 */
  toActivities: function () {
    console.log('jump to activities');
    wx.navigateTo({
      url: '/pages/charge/activity/activity'
    });
  },

  /* 更新活动按钮控制变量*/
  updateHasActivities: function() {
    var that = this;
    var url = `${config.service.activityUrl}`;
    console.log("接口请求：", url);
    qcloud.request({
      url: url,
      login: false,
      method: 'POST',
      data: {},
      success(res) {
        console.log(res);
        var tf = 0;
        var c = res.data.data.length;
        if (c > 0) {
          tf = 1;
        }
        that.setData({
          hasActivities: tf
        });
      },
      fail(error) {
        console.log('getActivities', error);
      }
    });
  },

  timeCounterInterval: function() {
    var that = this;
    var handle = setInterval(() => {
      console.log('timeCounter', that.data.timeCounterStartTime, util.getTimerString(that.data.timeCounterStartTime));
      that.setData({
        timeCounter: util.getTimerString(that.data.timeCounterStartTime)
      });
    }, 1000);
    this.setData({
      timeCounterIntervalHandle: handle
    });
  }

})