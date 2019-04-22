package com.applidium.nickelodeon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.uitls.AppUtils;

/**
 *
 */
public class PayVipSecceedDialog extends Dialog {

    private String mVipName;
    private String mVipTime;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */


    public PayVipSecceedDialog(Context context, String vipName, String vipTime) {
        super(context, R.style.full_dialog);
        mVipName = vipName;
        mVipTime = vipTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_secceed);
        initView();
    }


    private void initView() {
        TextView vipTime = (TextView) findViewById(R.id.vip_time);
        vipTime.setText(mVipTime);

        /** start vipName */
        TextView vipName = (TextView) findViewById(R.id.vip_name);

        StringBuilder sb = new StringBuilder();
        String have_pay = getContext().getResources().getString(R.string.you_have_pay);
        String vip = getContext().getResources().getString(R.string.menber_server);
        sb.append(have_pay);
        sb.append(mVipName);
        sb.append(vip);
        String content = sb.toString();

        int color = getContext().getResources().getColor(R.color.orange_text);
        int start = have_pay.length();
        int end = content.length() - vip.length();
        CharSequence cs = AppUtils.setHighLightText(content, color, start, end);

        vipName.setText(cs);
        /** end vipName */

        Button btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dismiss();
                } catch (Exception e) {

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                try {
                    dismiss();
                } catch (Exception e) {

                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
