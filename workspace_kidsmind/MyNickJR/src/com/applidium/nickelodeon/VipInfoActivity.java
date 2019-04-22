package com.applidium.nickelodeon;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.dialog.BindPhoneDialog;
import com.applidium.nickelodeon.dialog.ChangeUsernameDialog;
import com.applidium.nickelodeon.dialog.CodeOpenVipDialog;
import com.applidium.nickelodeon.dialog.PaymentDialog;
import com.applidium.nickelodeon.entity.VipListItem;
import com.applidium.nickelodeon.entity.VipListRequest;
import com.applidium.nickelodeon.entity.VipListResponse;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;

import java.util.List;

/**
 * Created by Markry on 2016/1/6.
 */
public class VipInfoActivity extends Activity implements View.OnClickListener, ChangeUsernameDialog.OnChangeSuccessListener {

    private android.widget.Button btnback;
    private android.widget.Button btnmodify;
    private android.widget.Button btnbind;
    private LinearLayout linearadd;
    private List<VipListItem> mList;
    private android.widget.TextView textUsname;
    private android.widget.TextView textPhone;
    private android.widget.TextView textTime;
    private MNJApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipinfo_layout);

        initView();

        initData();

        mList = application.getVipListItems();

        if (mList != null) {
            setVipListInfo(mList);
        } else {
            reqVipList();
        }
    }

    private void initView() {
        application = ((MNJApplication) getApplication());

        this.btnbind = (Button) findViewById(R.id.btn_bind);
        this.btnmodify = (Button) findViewById(R.id.btn_modify);
        this.btnback = (Button) findViewById(R.id.btn_back);
        this.linearadd = (LinearLayout) findViewById(R.id.linear_add);
        this.textTime = (TextView) findViewById(R.id.textTime);
        this.textPhone = (TextView) findViewById(R.id.textPhone);
        this.textUsname = (TextView) findViewById(R.id.textUsname);

        btnbind.setOnClickListener(this);
        btnmodify.setOnClickListener(this);
        btnback.setOnClickListener(this);

        if (!StringUtils.isEmpty(application.getMoblieNum())) {
            btnbind.setVisibility(View.GONE);
        }
        //设置VIP时间 刷新VIew
        application.setRefresh(textTime);
    }

    public void initData() {
        textPhone.setText(application.getMoblieNum());
        //如果用户名已经修改过了，则不能修改
        if (!StringUtils.isEmpty(application.getFirstModifiedTime())) {
            btnmodify.setVisibility(View.GONE);
        }
        if (application.getUserName().length() > 0) {
            textUsname.setText(application.getUserName());
        }
        //如果有手机号则代表已经绑定了，隐藏绑定按钮
        if (application.getMoblieNum().length() == 11) {
            btnbind.setVisibility(View.GONE);
            textPhone.setText(application.getMoblieNum());
        } else {
            textPhone.setText("未绑定");
        }
        textTime.setText(application.getVipExpiresTime());

        ((MNJApplication) getApplication()).reqUserInfo(); //新增每次进入刷新VIP时间

    }

    /**
     * 1.30 获取VIP列表
     */
    private void reqVipList() {
        String url = AppConfig.VIP_LIST;
        final VipListRequest request = new VipListRequest();

        String token = application.getToken();
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
                        Toast.makeText(VipInfoActivity.this, "暂时没有VIP列表信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        application.setVipListItems(mList);
                        setVipListInfo(mList);
                    }
                }
            }
        });
    }

    private void setVipListInfo(List<VipListItem> list) {
        linearadd.removeAllViews();
        for (int i = 0; i < list.size() + 1; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_vipinfo_item, null);
            TextView textPrice = (TextView) view.findViewById(R.id.textPrice);
            TextView textLastPrice = (TextView) view.findViewById(R.id.textLastPrice);
            TextView textTitle = (TextView) view.findViewById(R.id.textTitle);
            if (i == list.size()) {

                textLastPrice.setText("开通码");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CodeOpenVipDialog dialogVip = new CodeOpenVipDialog(VipInfoActivity.this);
                        dialogVip.setChangeSuccessListener(VipInfoActivity.this);
                        dialogVip.show();
                    }
                });
            } else {
                final VipListItem item = list.get(i);

                textPrice.setText(item.getPrice());
                textLastPrice.setText(item.getListprice());
                textTitle.setText(item.getVipTitle());

                textLastPrice.getPaint().setAntiAlias(true);//抗锯齿
                textLastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //中划线

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            double price = item.getpriceDouble();
                            PaymentDialog.payment(VipInfoActivity.this, price, item.getVipGuid(),
                                    item.getVipTitle(), item.getPartnerProductId());
                        } catch (NumberFormatException e) {

                        }
                    }
                });
            }

            view.setLayoutParams(new LinearLayout.LayoutParams((int)
                    (ScreenUtils.getHeightPixels(this) * 0.31), (int) (ScreenUtils.getHeightPixels(this) * 0.33)));
            TextView textBank = new TextView(this);
            textBank.setMinimumWidth((int) (ScreenUtils.getHeightPixels(this) * 0.02));
            textBank.setMinimumHeight(10);
            linearadd.addView(view);
            linearadd.addView(textBank);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_bind:
                BindPhoneDialog dilogBind = new BindPhoneDialog(this);
                dilogBind.setChangeSuccessListener(this);
                dilogBind.show();
                break;
            case R.id.btn_modify:
                ChangeUsernameDialog dilogUs = new ChangeUsernameDialog(this);
                dilogUs.setChangeSuccessListener(this);
                dilogUs.show();
                break;
        }
    }

    @Override
    public void onSuccess() {
        initData();
    }
}

