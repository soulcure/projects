package com.applidium.nickelodeon.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.StringUtils;


public class LoadingDialog extends AlertDialog {


    private Context mContext;
    private String loadingMsg;

    private ImageView imgLoading;
    private TextView tvMsg;


    public LoadingDialog(Context context) {
        super(context);
        mContext = context;
    }


    public LoadingDialog(Context context, String msg) {
        super(context);
        mContext = context;
        loadingMsg = msg;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loading_dialog);
        initView();
    }

    private void initView() {
        imgLoading = (ImageView) findViewById(R.id.img_loading);

        AnimationDrawable anim = (AnimationDrawable) imgLoading.getBackground();
        anim.start();

        tvMsg = (TextView) findViewById(R.id.tv_msg);
        if (!StringUtils.isEmpty(loadingMsg)) {
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(loadingMsg);
        }

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
