// circleProgress.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    bg: String,
    draw: String
  },

  /**
   * 组件的初始数据
   */
  data: {

  },

  /**
   * 组件的方法列表
   */
  methods: {
    /* id : canvas 组件的唯一标识符 canvas-id ，x : canvas 绘制圆形的半径， w : canvas 绘制圆环的宽度  */
    drawCircleBg: function(x, w) { 
      // 设置圆环外面盒子大小 宽高都等于圆环直径
      console.log(this.data.bg, this.data.draw)
      this.setData({
        size: 2 * x
      });
      // 使用 wx.createContext 获取绘图上下文 ctx  绘制背景圆环
      var ctx = wx.createCanvasContext(this.data.bg, this)
      ctx.setLineWidth(w);
      ctx.setStrokeStyle('#ffffff');
      ctx.setLineCap('round')
      ctx.beginPath(); //开始一个新的路径
      //设置一个原点 (x,y)，半径为 r 的圆的路径到当前路径 此处 x=y=r
      ctx.arc(x, x, x - w, 0, 2 * Math.PI, false);
      ctx.stroke(); //对当前路径进行描边
      ctx.draw();
    },
    drawCircle: function(x, w, step) {
      // 使用 wx.createContext 获取绘图上下文 context  绘制彩色进度条圆环
      var context = wx.createCanvasContext(this.data.draw, this);
      // 设置渐变
      var gradient = context.createLinearGradient(2 * x, x, 0);
      gradient.addColorStop("0", "#2661DD");
      gradient.addColorStop("0.5", "#40ED94");
      gradient.addColorStop("1.0", "#5956CC");
      context.setLineWidth(w);
      context.setStrokeStyle(gradient);
      context.setLineCap('round')
      context.beginPath(); //开始一个新的路径
      // step 从 0 到 2 为一周
      context.arc(x, x, x - w, -Math.PI / 2, step * Math.PI - Math.PI / 2, false);
      context.stroke(); //对当前路径进行描边
      context.draw()
    },
    clearCircle: function(arcFrom, arcTo) {
      var r = this.data.size / 2;
      var x = 0;
      var y = 0;
      var w = r;
      var h = r;
      console.log("clearCircle", arcFrom, arcTo)
      if (arcFrom && arcTo) {

        var a = arcFrom * Math.PI - Math.PI / 2;
        var b = arcTo * Math.PI - Math.PI / 2;
        var x1 = r + r * Math.cos(a);
        var y1 = r + r * Math.sin(a);

        var x2 = r + r * Math.cos(b);
        var y2 = r + r * Math.sin(b);
        console.log("clearCircle", x1, y1, x2, y2)
        x = (x1 > x2) ? x2 : x1;
        y = (y1 > y2) ? y2 : y1;
        w = Math.abs(x1 - x2)
        h = Math.abs(y1 - y2)
      }
      console.log(x, y, w, h)
      var context = wx.createCanvasContext(this.data.draw, this);
      //context.clearRect(x, y, w, h);
      context.setStrokeStyle('red')
      context.strokeRect(x, y, w, h)
      context.draw();
    },
    _runEvent() {
      //触发自定义组件事件
      this.triggerEvent("runEvent")
      console.log('_runEvent')
    }
  }
})