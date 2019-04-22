package com.ivmall.android.app.parent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ivmall.android.app.ActionActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.entity.VipListItem;
import com.ivmall.android.app.entity.VipListRequest;
import com.ivmall.android.app.entity.VipListResponse;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;

import java.util.List;

public class BugVipFragment extends Fragment {

    public static final String TAG = BugVipFragment.class.getSimpleName();

    public static final String VIP_REFRESH = "VIP_REFRESH";
    private Activity mAct;

    private List<VipListItem> mList;

    private LinearLayout vipContainer;


    private TextView tvVipTime;
    private TextView tvVipBuyInfo;
    private RefreshReciver refreshReciver;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.buy_vip_fragment, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        vipContainer = (LinearLayout) view.findViewById(R.id.vip_container);

        tvVipBuyInfo = (TextView) view.findViewById(R.id.tvVipBuyInfo);
        tvVipTime = (TextView) view.findViewById(R.id.tvVipTime);

        view.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mAct, ActionActivity.class);
                startActivity(intent);
            }
        });


        setVipInfo();

        refreshReciver = new RefreshReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(VIP_REFRESH);
        mAct.registerReceiver(refreshReciver, filter);
    }


    @Override
    public void onResume() {
        super.onResume();
        reqVipList(); // 获取VIP列表
        ((KidsMindApplication) mAct.getApplication()).setRefresh(tvVipTime);
        //更新VIP过期时间
        String time = ((KidsMindApplication) mAct.getApplication()).getVipExpiresTime();
        if (!StringUtils.isEmpty(time)) {

            String content = getString(R.string.vip_end_time) + time;
            int color = mAct.getResources().getColor(R.color.yellow);
            int start = getString(R.string.vip_end_time).length();
            int end = content.length();
            CharSequence cs = AppUtils.setHighLightText(content, color, start, end);

            tvVipTime.setText(cs);
        } else {
            ((KidsMindApplication) mAct.getApplication()).reqUserInfo();//刷新用户VIP信息
        }
        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((KidsMindApplication) mAct.getApplication()).setRefresh(null);
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }


    private void setVipInfo() {
        String content = getString(R.string.recommend_parents);
        int color = mAct.getResources().getColor(R.color.yellow);
        CharSequence cs = AppUtils.setHighLightText(content, color, 30, 34);
        tvVipBuyInfo.setText(cs);
    }


    /**
     * 1.30 获取VIP列表
     */
    private void reqVipList() {
        String url = AppConfig.VIP_LIST;
        VipListRequest request = new VipListRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                VipListResponse resp = GsonUtil.parse(response,
                        VipListResponse.class);
                if (resp != null && resp.isSucess() && isAdded()) {
                    mList = resp.getData().getList();

                    if (mList == null || mList.size() == 0) {
                        Toast.makeText(getActivity(), "暂时没有VIP列表信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        setVipListInfo(mList);
                    }
                }
            }
        });
    }


    private void setVipListInfo(List<VipListItem> list) {
        LayoutInflater inflater = mAct.getLayoutInflater();
        vipContainer.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            final VipListItem item = list.get(i);
            final View view = inflater.inflate(R.layout.goods_item, vipContainer, false);
            vipContainer.addView(view);

            TextView tvVipName = (TextView) view.findViewById(R.id.tv_vipName);//商品名称

            String vipName = item.getVipName();
            tvVipName.setText(vipName);
            Drawable drawable;
            if (vipName.contains(getString(R.string.price_name))) {
                drawable = getResources().getDrawable(R.drawable.vip_experience);
            } else {
                drawable = getResources().getDrawable(R.drawable.vip_suit);
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
                        PaymentDialog.payment(mAct, price, item.getVipGuid(), item.getVipDesc(), item.getPartnerProductId());
                    }
                }
            });

        }
        if (list.size() > 1) {
            ViewGroup.LayoutParams params = vipContainer.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            vipContainer.setLayoutParams(params);
        }

    }

    /**
     * 刷新
     */
    private class RefreshReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            reqVipList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mAct.unregisterReceiver(refreshReciver);
        } catch (IllegalArgumentException e) {

        }
    }

}
