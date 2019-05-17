const formatTime = (date,full) => {
  var finalDate = date;
  if (typeof date == 'number'){
    finalDate = new Date(date);
  }
  const year = finalDate.getFullYear()
  const month = finalDate.getMonth() + 1
  const day = finalDate.getDate()
  const hour = finalDate.getHours()
  const minute = finalDate.getMinutes()
  const second = finalDate.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ((full)?(' ' + [hour, minute, second].map(formatNumber).join(':')):'')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}


// 显示繁忙提示
var showBusy = text => wx.showToast({
  title: text,
  icon: 'loading',
  duration: 10000
})

// 显示成功提示
var showSuccess = text => wx.showToast({
  title: text,
  icon: 'success'
})

// 显示错误提示
var showError = text => wx.showToast({
  title: text,
  mask: true,
  icon: 'none',
  duration: 2000
})

// 显示失败提示
var showModel = (title, content) => {
  wx.hideToast();

  wx.showModal({
    title,
    content: content,
    showCancel: false
  })
}

var showCanIUse = () => {
  // 如果希望用户在最新版本的客户端上体验您的小程序，可以这样子提示
  wx.showModal({
    title: '提示',
    content: '当前微信版本过低，无法使用该功能，请升级到最新微信版本后重试。'
  })
}

/**
 * @param number st 开始时间戳(毫秒)
 */

var getTimerString = st => {
  var now = new Date().getTime();
  var t = now - st;
  var hour = Math.floor(t/(1000*60*60));
  var minute = Math.floor((t-hour*1000*60*60)/(1000*60));
  var second = Math.floor((t - hour * 1000 * 60 * 60 - minute*1000*60)/1000);
  // console.log(now, st, t, hour, minute, second, [hour, minute, second].map(formatNumber).join(":"));
  return [hour,minute,second].map(formatNumber).join(":");
}

module.exports = {
  formatTime,
  showBusy,
  showSuccess,
  showError,
  showModel,
  showCanIUse,
  getTimerString
}