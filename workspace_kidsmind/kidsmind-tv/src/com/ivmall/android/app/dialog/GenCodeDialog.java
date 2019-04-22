package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smit.android.ivmall.stb.R;


/**
 * Created by Markry on 2015/11/5.
 */
public class GenCodeDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private boolean result;
    private String msg;


    public GenCodeDialog(Context context, boolean b, String info) {
        super(context, R.style.full_dialog);
        mContext = context;
        result = b;
        if (b) {
            msg = context.getResources().getString(R.string.code_sucess);
        } else {
            msg = info;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gen_code_layout);

        TextView tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_msg.setText(msg);

        ImageView imgResult = (ImageView) findViewById(R.id.img_result);
        if (result) {
            imgResult.setImageResource(R.drawable.code_success);
        } else {
            imgResult.setImageResource(R.drawable.code_failure);
        }

        Button btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        btn_close.requestFocus();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_close:
                dismiss();
                break;
            default:
                break;
        }
    }
}
