package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.ParentFragmentActivity;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.JoinActionRequest;
import com.ivmall.android.app.entity.JoinActionResponse;
import com.ivmall.android.app.entity.PrizesInfo;
import com.ivmall.android.app.impl.OnfinshListener;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.views.LuckyPanView;
import com.smit.android.ivmall.stb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by koen on 2015/12/22.
 */
public class LotteryDialog extends Dialog implements View.OnClickListener, OnfinshListener {
    public static final String TAG = "LotteryDialog";
    PrizesInfo lotteryinfo;
    Context context;
    List<String> infos = new ArrayList<String>();
    ImageView startBtn;
    ImageView iamgeLeftCount;
    LuckyPanView luckyPanView;
    ListView mlistView;
    ImageView imgTicket;
    LinearLayout ticketLayout;
    Timer autoUpdateTimer;
    int listIndex;
    Button btn_vip_use;

    private int[] mImgs = new int[]{   //优惠券集合  //1:70  2:30  3:谢谢  4::vip  5:10  6:50
            R.drawable.coupons_70, R.drawable.coupons_30, R.drawable.coupons_thank,
            R.drawable.coupons_vip, R.drawable.coupons_10,
            R.drawable.coupons_50
    };


    public LotteryDialog(Context context, PrizesInfo p) {
        super(context, R.style.full_dialog);
        this.lotteryinfo = p;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.lottery_view);
        getData();
        initView();
        setLeftCoutImg();
    }

    private void getData() {
        if (lotteryinfo != null) {
            for (int i = 0; i < lotteryinfo.getSelectedCouponList().size(); i++) {
                infos.add(infos(i));
            }
        }
    }

    private String infos(int index) {
        String userName = lotteryinfo.getSelectedCouponList().get(index).getUserName();
        StringBuilder builder = new StringBuilder(userName);
        builder.replace(userName.length() - 10, userName.length() - 4, "******");
        return builder.toString() + "抽中" +
                lotteryinfo.getSelectedCouponList().get(index).getSelectedCoupon();
    }

    private void initView() {
        ticketLayout = (LinearLayout) findViewById(R.id.lottery_layout);
        imgTicket = (ImageView) findViewById(R.id.lottery_ticket);
        startBtn = (ImageView) findViewById(R.id.start_btn);
        iamgeLeftCount = (ImageView) findViewById(R.id.iamge_LeftCount);
        luckyPanView = (LuckyPanView) findViewById(R.id.luckyPan);
        luckyPanView.setFinshListener(this);
        btn_vip_use= (Button) findViewById(R.id.btn_vip_use);
        btn_vip_use.setOnClickListener(this);
        mlistView = (ListView) findViewById(R.id.lottery_info);
        turnView();
        List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
        for (String s : infos) {
            HashMap<String, String> map = new HashMap<String,String>();
            map.put("info", s);
            data.add(map);
        }
        mlistView.setAdapter(new SimpleAdapter
                (context, data,
                        R.layout.lottery_item, new String[]{"info"}
                        , new int[]{R.id.lottery_textview}));
        autoScroll();
        startBtn.requestFocus();
        startBtn.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (startBtn.isEnabled()) {
            startBtn.requestFocus();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 将获奖信息转化为int
     *
     * @param infoString
     * @return
     */
    private int getindex(String infoString) {

        int result;
        if (infoString.contains("10")) {
            result = 5;
        } else if (infoString.contains("30")) {
            result = 2;
        } else if (infoString.contains("50")) {
            result = 6;
        } else if (infoString.contains("70")) {
            result = 1;
        } else if (infoString.contains("免费")) {
            result = 4;
        } else {
            result = 3;
        }
        return result;
    }

    /**
     * 设置视图为抽奖视图
     */
    private void setLotteryView() {
        startBtn.setVisibility(View.VISIBLE);
        luckyPanView.setVisibility(View.VISIBLE);
        ticketLayout.setVisibility(View.GONE);
        imgTicket.setVisibility(View.GONE);
        btn_vip_use.setVisibility(View.GONE);
    }

    /**
     * 设置视图为完成视图
     */
    private void setFinishView() {
        startBtn.setVisibility(View.GONE);
        luckyPanView.setVisibility(View.GONE);
        ticketLayout.setVisibility(View.VISIBLE);
        imgTicket.setVisibility(View.VISIBLE);
        btn_vip_use.setVisibility(View.VISIBLE);
        btn_vip_use.requestFocus();
        if (lotteryinfo.getSelectedCoupon() != null) {
            changeView(getindex(lotteryinfo.getSelectedCoupon()));
        } else {
            changeView(getindex(lotteryinfo.getCoupon()));
        }
    }

    /**
     * 判断显示哪种视图
     */
    private void turnView() {
        if (null != lotteryinfo.getSelectedCoupon()) {
            setFinishView();
        } else {
            setLotteryView();
        }
    }

    private void setLeftCoutImg() {
        if (lotteryinfo.getLeftCount() == 0) {
            iamgeLeftCount.setImageResource(R.drawable.lottery_zero);
        } else if (lotteryinfo.getLeftCount() == 1) {
            iamgeLeftCount.setImageResource(R.drawable.lottery_one);
        }
    }

    // 根据index变化视图；
    private void changeView(int index) {
        if (index >= 1) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mImgs[index - 1]);
            imgTicket.setImageBitmap(bitmap);
        }
    }

    /**
     * listview自动滚动
     */
    private void autoScroll() {
        autoUpdateTimer = new Timer();
        autoUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                listIndex += 1;
                if (listIndex >= mlistView.getCount()) {
                    listIndex = 0;
                }
                mlistView.smoothScrollToPosition(listIndex);
            }
        }, 0, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                if (!luckyPanView.isStart()) {
                    startBtn.setImageResource(R.drawable.lottery_stop);
                    luckyPanView.luckyStart(getindex(lotteryinfo.getCoupon()));  //1:70  2:30  3:谢谢  4::vip  5:10  6:50
                } else {
                    if (!luckyPanView.isShouldEnd()) {
                        startBtn.setImageResource(R.drawable.lucky_btn_selector);
                        luckyPanView.luckyEnd();
                    }
                }
                break;
            case R.id.btn_vip_use:
                this.dismiss();
                Intent intent = new Intent(context, ParentFragmentActivity.class);
                context.startActivity(intent);
                ((FragmentActivity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                BaiduUtils.onEvent(context, OnEventId.PARENT_CENTER, context.getString(R.string.parent_center));
                break;
        }
    }

    /**
     * 1.53 活动信息登记接口
     */
    private void joinAction() {
        String url = AppConfig.JOIN_ACTION;
        JoinActionRequest request = new JoinActionRequest();
        String token = ((KidsMindApplication) context.getApplicationContext()).getToken();
        request.setToken(token);
        request.setTitle("20160101_乌吉送元旦礼物啦!");
        request.setCoupon(lotteryinfo.getCoupon());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                JoinActionResponse resp = GsonUtil.parse(response,
                        JoinActionResponse.class);
                if (resp.isSucess()) {
                    // 显示成功信息
                    lotteryinfo.setLeftCount(lotteryinfo.getLeftCount() - 1);
                    setFinishView();
                    setLeftCoutImg();
                } else {
                    Toast.makeText(context, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finshed() {
        //可以抽奖进行登记
        if (lotteryinfo.getLeftCount() > 0) {
            joinAction();
        }
    }
}
