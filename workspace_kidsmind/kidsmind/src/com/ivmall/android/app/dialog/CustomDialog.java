package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.ivmall.android.app.R;

/**
 *
 */
public class CustomDialog extends Dialog {

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */
    public CustomDialog(Context context) {
        super(context, R.style.full_dialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            dismiss();
        } catch (Exception e) {

        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                dismiss();
            } catch (Exception e) {

            }
        }
        return true;
    }


    @Override
    public void show() {
        // TODO Auto-generated method stub
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
