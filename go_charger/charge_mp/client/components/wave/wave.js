// components/wave/wave.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {

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
    /* 充电动画 */
    drawWave: function () {
      var canvas = {
        width: 250,
        height: 250,
      };
      var lines = [
        "#fff",
        "#0f8bfb"
      ];

      var ctx = wx.createCanvasContext('canvasWave',this);

      let requestAnimFrame = (function () {
        return function (callback) {
          setTimeout(callback, 1000 / 30);
        };
      })();

      var waveWidth = 500,
        offset = 0,
        waveHeight = 15,
        waveCount = 4,
        startX = -150,
        startY = 75,
        progress = 0,
        progressStep = 1,
        d2 = waveWidth / waveCount,
        d = d2 / 2,
        hd = d / 2;
      //img = new Image();

      var rad = 125;

      function loop() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        offset -= 5;
        progress += progressStep;

        if (progress > 50 || progress < 0) {
          progressStep *= -1;
        }

        if (-1 * offset === d2) {
          offset = 0;
        }
        ctx.save();
        ctx.beginPath();
        ctx.arc(canvas.width / 2, canvas.height / 2, 125, 0, 2 * Math.PI);
        ctx.setFillStyle('#fff');
        ctx.fill();
        ctx.clip();

        for (var j = 0; j < lines.length; j++) {
          ctx.beginPath(); //真机与 IDE表现不一样，真机需要加这句
          ctx.fillStyle = lines[j];
          var offsetY = startY;// - progress;   /* Y 轴移动 */
          ctx.moveTo(startX - offset, offsetY);

          for (var i = 0; i < waveCount; i++) {
            var dx = i * d2;

            var coverOffset = d * j;
            var offsetX = dx + startX - offset - coverOffset;

            ctx.quadraticCurveTo(offsetX + hd, offsetY + waveHeight, offsetX + d, offsetY);
            ctx.quadraticCurveTo(offsetX + hd + d, offsetY - waveHeight, offsetX + d2, offsetY);
          }

          ctx.lineTo(startX + waveWidth, 600);
          ctx.lineTo(startX, 600);
          ctx.fill();
        }

        // ctx.beginPath();
        // ctx.arc(canvas.width / 2, canvas.height / 2, 125, 0, 2 * Math.PI);
        // ctx.setStrokeStyle('#0f8bfb');
        // ctx.fill();

        ctx.restore();
        
  
        // ctx.beginPath();
        // ctx.arc(150, 150, 150 - rad, 0, 2 * Math.PI);
        // ctx.setFillStyle('white');
        // ctx.fill();

        // ctx.beginPath();
        // ctx.arc(150, 150, 170 - rad, 0, 2 * Math.PI);
        // ctx.setShadow(10, 50, 50, "rgba(15,137,247, 1)")
        // ctx.setFillStyle("rgba(15,137,247, 1)");
        // ctx.fill();

        // ctx.globalCompositeOperation = 'destination-atop';   // 模拟可以
     
        ctx.draw();
        requestAnimFrame(loop);
      }
      loop();
    }
  },

  
})
