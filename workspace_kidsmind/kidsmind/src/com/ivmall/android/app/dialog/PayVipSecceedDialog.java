package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.parent.BugVipFragment;

/**
 *
 */
public class PayVipSecceedDialog extends Dialog {

    private String getVipName;

    private String getVipTime;

    private Context context;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */


    public PayVipSecceedDialog(Context context, String vipName, String vipTime) {
        super(context, R.style.full_dialog);
        this.context = context;
        getVipName = vipName;
        getVipTime = vipTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_secceed);
        initView();
    }


    private void initView() {
        Button btnKnow = (Button) findViewById(R.id.btn_know);
        TextView vipName = (TextView) findViewById(R.id.vip_name);
        TextView vipTime = (TextView) findViewById(R.id.vip_time);

        vipName.setText(getVipName);

        vipTime.setText(getVipTime);

        btnKnow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
        Intent intent = new Intent();
        intent.setAction(BugVipFragment.VIP_REFRESH);
        context.sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
