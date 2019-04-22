package com.ivmall.android.app.dialog;

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

import com.ivmall.android.app.ActionActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.VipListItem;
import com.ivmall.android.app.entity.VipListRequest;
import com.ivmall.android.app.entity.VipListResponse;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;

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


    private TextView tvVipTime;
    private TextView tvVipBuyInfo;
    private ImageView imgTitle;

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

        tvVipBuyInfo = (TextView) findViewById(R.id.tvVipBuyInfo);
        tvVipTime = (TextView) findViewById(R.id.tvVipTime);
        imgTitle = (ImageView) findViewById(R.id.img_title);

        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActionActivity.class);
                mContext.startActivity(intent);
            }
        });

        if (mVipType == VIP_TYPE.VIP_HEART_BEAT) {
            imgTitle.setImageResource(R.drawable.vip_title_heart);
        } else if (mVipType == VIP_TYPE.VIP_TRIAL) {
            imgTitle.setImageResource(R.drawable.vip_title_trial);
        }

        ((KidsMindApplication) mContext.getApplicationContext()).setRefresh(tvVipTime);
        //更新VIP过期时间
        String time = ((KidsMindApplication) mContext.getApplicationContext()).getVipExpiresTime();
        if (!StringUtils.isEmpty(time)) {

            String content = mContext.getString(R.string.vip_end_time) + time;
            int color = mContext.getResources().getColor(R.color.yellow);
            int start = mContext.getString(R.string.vip_end_time).length();
            int end = content.length();
            CharSequence cs = AppUtils.setHighLightText(content, color, start, end);

            tvVipTime.setText(cs);
        } else {
            ((KidsMindApplication) mContext.getApplicationContext()).reqUserInfo();//刷新用户VIP信息
        }


        reqVipList(); // 获取VIP列表
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
        ((KidsMindApplication) mContext.getApplicationContext()).setRefresh(null);
    }

    public void setVipType(VIP_TYPE type) {
        mVipType = type;
        if (mVipType == VIP_TYPE.VIP_HEART_BEAT) {
            imgTitle.setImageResource(R.drawable.vip_title_heart);
        } else if (mVipType == VIP_TYPE.VIP_TRIAL) {
            imgTitle.setImageResource(R.drawable.vip_title_trial);
        }
        //setVipInfo();
    }

    private void setVipInfo() {
        if (mVipType == VIP_TYPE.VIP_HEART_BEAT) {
            SpannableString msp = new SpannableString(mContext.getString(R.string.recommend_heart));
            int color = mContext.getResources().getColor(R.color.yellow);

            msp.setSpan(new ForegroundColorSpan(color), 34, 38,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvVipBuyInfo.setText(msp);

        } else if (mVipType == VIP_TYPE.VIP_TRIAL) {
            SpannableString msp = new SpannableString(mContext.getString(R.string.recommend_trial));
            int color = mContext.getResources().getColor(R.color.yellow);

            msp.setSpan(new ForegroundColorSpan(color), 28, 32,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvVipBuyInfo.setText(msp);

        }


    }


    /**
     * 1.30 获取VIP列表
     */
    private void reqVipList() {
        String url = AppConfig.VIP_LIST;
        VipListRequest request = new VipListRequest();

        String token = ((KidsMindApplication) mContext.getApplicationContext()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                VipListResponse resp = GsonUtil.parse(response,
                        VipListResponse.class);
                if (resp.isSucess()) {
                    mList = resp.getData().getList();

                    if (mList == null || mList.size() == 0) {
                        Toast.makeText(mContext, "暂时没有VIP列表信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
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
            View view = inflater.inflate(R.layout.goods_item, vipContainer, false);
            vipContainer.addView(view);
            if (i == 0) {
                //for android 较低版本电视无法获取焦点
                view.requestFocus();
            }
            TextView tvVipName = (TextView) view.findViewById(R.id.tv_vipName);//商品名称

            String vipName = item.getVipName();
            tvVipName.setText(vipName);
            Drawable drawable;
            if (vipName.contains(mContext.getString(R.string.price_name))) {
                drawable = mContext.getResources().getDrawable(R.drawable.vip_experience);
            } else {
                drawable = mContext.getResources().getDrawable(R.drawable.vip_suit);
            }


            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, w, h);
            tvVipName.setCompoundDrawables(drawable, null, null, null);


            TextView tvCost = (TextView) view.findViewById(R.id.tv_cost);  //原价
            tvCost.setText(item.getListPriceStr());
            tvCost.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰


            TextView tvCoupon = (TextView) view.findViewById(R.id.tv_coupon);//优惠券
            TextView tvDiscount = (TextView) view.findViewById(R.id.tv_discount);//现价
            tvDiscount.setText(item.getVipPriceStr());
            if (item.getPreferential() > 0) {
                tvCoupon.setText("代金券 -" + item.getPreferentialStr());
                view.setTag(item.getPreferentialPrice());
            } else {
                tvCoupon.setVisibility(View.GONE);
                view.setTag(item.getVipPrice());
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double price = (Double) v.getTag();
                    if (price > 0) {
                        PaymentDialog.payment(mContext, price, item.getVipGuid(), item.getVipDesc(), item.getPartnerProductId());
                    }
                }
            });

        }

        ViewGroup.LayoutParams params = vipContainer.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        vipContainer.setLayoutParams(params);
    }


}