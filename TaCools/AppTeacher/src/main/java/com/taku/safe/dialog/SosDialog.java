package com.taku.safe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.taku.safe.R;


/**
 * Created by Administrator on 2017/2/27.
 */

public class SosDialog extends Dialog implements View.OnClickListener {


    private Button btn_entry;
    private Context mContext;

    private int sosId;
    private double latitude;
    private double longitude;


    private SosDialog(Builder builder) {
        super(builder.context, R.style.custom_dialog);
        setCanceledOnTouchOutside(false);
        mContext = builder.context;

        sosId = builder.sosId;
        latitude = builder.latitude;
        longitude = builder.longitude;
    }

    public static class Builder {
        private Context context;

        private int sosId;
        private double latitude;
        private double longitude;

        public SosDialog builder() {
            return new SosDialog(this);
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder sosId(int sosId) {
            this.sosId = sosId;
            return this;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sos);
        setDialogFeature();
        initView();
    }


    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.TOP;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }

    private void initView() {
        btn_entry = (Button) findViewById(R.id.btn_entry);
        btn_entry.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_entry:
                /*Intent intent = new Intent(mContext, SosInfoOnMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);*/
                dismiss();
                break;
        }
    }
}
