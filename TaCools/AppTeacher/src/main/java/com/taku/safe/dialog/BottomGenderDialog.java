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


public class BottomGenderDialog extends Dialog implements View.OnClickListener {

    public interface SexSelectCallback {
        void onMale();

        void onFemale();
    }


    private Button btn_save_male;
    private Button btn_save_female;
    private Button btnCancel;

    private Context mContext;

    private SexSelectCallback mCallback;


    public BottomGenderDialog(Context context) {
        super(context, R.style.custom_dialog);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_gender);
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
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }


    public void initView() {
        btn_save_male = (Button) findViewById(R.id.btn_save_male);
        btn_save_female = (Button) findViewById(R.id.btn_save_female);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        btn_save_male.setOnClickListener(this);
        btn_save_female.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }


    public void setOnSexSelectListener(SexSelectCallback callback) {
        this.mCallback = callback;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_save_male) {//选择性别男
            if (mCallback != null) {
                mCallback.onMale();
            }
            dismiss();
        } else if (id == R.id.btn_save_female) { //选择性别女
            if (mCallback != null) {
                mCallback.onFemale();
            }
            dismiss();
        } else if (id == R.id.btn_cancel) {
            dismiss();
        }
    }


}
