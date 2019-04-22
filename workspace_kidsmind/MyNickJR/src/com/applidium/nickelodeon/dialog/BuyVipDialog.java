package com.applidium.nickelodeon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.ActionActivity;
import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.VipInfoActivity;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.VipListItem;
import com.applidium.nickelodeon.entity.VipListRequest;
import com.applidium.nickelodeon.entity.VipListResponse;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;

import java.util.List;

/**
 * @author chenqy
 *         <p/>
 *         设置对话框
 */
public class BuyVipDialog extends Dialog {

    private static final String TAG = BuyVipDialog.class.getSimpleName();

    private Context mContext;

    private List<VipListItem> mList;

    private LinearLayout vipContainer;

    private VIP_TYPE mVipType;

    public enum VIP_TYPE {
        VIP_HEART_BEAT, VIP_TRIAL,
    }

    public BuyVipDialog(Context context, VIP_TYPE type) {
        super(context, R.style.full_dialog);
        mContext = context;
        mVipType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_vip_dialog);
        //语言提示
        /*Intent intent = new Intent(mContext, MediaPlayerService.class);
        intent.putExtra("media", MediaPlayerService.BUYVIP);
        mContext.startService(intent);*/
        initView();
    }

    private void initView() {
        vipContainer = (LinearLayout) findViewById(R.id.vip_container);
        findViewById(R.id.vip_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //更新VIP过期时间
        String time = ((MNJApplication) mContext.getApplicationContext()).getVipExpiresTime();
        if (!StringUtils.isEmpty(time)) {

            String content = mContext.getString(R.string.vip_end_time) + time;
            int color = mContext.getResources().getColor(R.color.yellow);
            int start = mContext.getString(R.string.vip_end_time).length();
            int end = content.length();
            CharSequence cs = AppUtils.setHighLightText(content, color, start, end);
        } else {
            ((MNJApplication) mContext.getApplicationContext()).reqUserInfo();//刷新用户VIP信息
        }

        mList = ((MNJApplication) mContext.getApplicationContext()).getVipListItems();
        if (mList != null) {
            setVipListInfo(mList);
        } else {
            reqVipList(); // 获取VIP列表
        }
        setVipInfo();
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        ((MNJApplication) mContext.getApplicationContext()).setRefresh(null);
    }


    private void setVipInfo() {
        if (mVipType == VIP_TYPE.VIP_HEART_BEAT) {
            SpannableString msp = new SpannableString(mContext.getString(R.string.recommend_heart));
            int color = mContext.getResources().getColor(R.color.yellow);

            msp.setSpan(new ForegroundColorSpan(color), 34, 38,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } else if (mVipType == VIP_TYPE.VIP_TRIAL) {
            SpannableString msp = new SpannableString(mContext.getString(R.string.recommend_trial));
            int color = mContext.getResources().getColor(R.color.yellow);

            msp.setSpan(new ForegroundColorSpan(color), 28, 32,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


    /**
     * 1.30 获取VIP列表
     */
    private void reqVipList() {
        String url = AppConfig.VIP_LIST;
        VipListRequest request = new VipListRequest();

        String token = ((MNJApplication) mContext.getApplicationContext()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                VipListResponse resp = GsonUtil.parse(response,
                        VipListResponse.class);
                if (resp != null && resp.isSucess()) {
                    mList = resp.getData().getList();

                    if (mList == null || mList.size() == 0) {
                        Toast.makeText(mContext, "暂时没有VIP列表信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((MNJApplication) mContext.getApplicationContext()).setVipListItems(mList);
                        setVipListInfo(mList);
                    }
                }
            }
        });
    }


    private void setVipListInfo(List<VipListItem> list) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0; i < list.size(); i++) {
            final VipListItem item = list.get(i);
            View view = inflater.inflate(R.layout.activity_vipinfo_item, vipContainer, false);
            if (i == 0) {
                //for android 较低版本电视无法获取焦点
                view.requestFocus();
            }
            TextView textPrice = (TextView) view.findViewById(R.id.textPrice);
            TextView textLastPrice = (TextView) view.findViewById(R.id.textLastPrice);
            TextView textTitle = (TextView) view.findViewById(R.id.textTitle);

            textPrice.setText(item.getPrice());
            textLastPrice.setText(item.getListprice());
            textTitle.setText(item.getVipTitle());

            textLastPrice.getPaint().setAntiAlias(true);//抗锯齿
            textLastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //中划线

            int height = ScreenUtils.getHeightPixels(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (height * 0.3), (int) (height * 0.34));
            if (ScreenUtils.isPhone(mContext)) {
                params.leftMargin = 15;
            } else {
                params.leftMargin = 30;
            }
            view.setLayoutParams(params);
            vipContainer.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        double price = item.getpriceDouble();
                        PaymentDialog.payment(mContext, price, item.getVipGuid(), item.getVipDescription(), item.getPartnerProductId());
                    } catch (NumberFormatException e) {

                    }
                }
            });
        }

        ViewGroup.LayoutParams params = vipContainer.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        vipContainer.setLayoutParams(params);
    }


}