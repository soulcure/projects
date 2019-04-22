package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ivmall.android.app.views.FoucsActionView;
import com.smit.android.ivmall.stb.R;

/**
 *
 */
public class PlayPauseDialog extends Dialog implements View.OnClickListener {


    private Context mContext;
    private ImageView img_action;
    private Bitmap mBitmap;

    private EditText et_phone_number;
    private Button btn_commit;
    private ImageButton btn_cacel;
    private FoucsActionView rel_foucs;

    private onDismissListener listener;

    /**
     * Description:构造函数
     *
     * @param context 当前上下文
     */
    public PlayPauseDialog(Context context, Bitmap bitmap) {
        super(context, R.style.full_dialog);
        mContext = context;
        mBitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.action_dialog);
        initView();

    }


    private void initView() {
        btn_cacel = (ImageButton) findViewById(R.id.btn_cacel);
        img_action = (ImageView) findViewById(R.id.img_action);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        rel_foucs = (FoucsActionView) findViewById(R.id.rel_foucs);

        rel_foucs.setClickable(true);
        rel_foucs.setFocusable(true);

        rel_foucs.setOnClickListener(this);
        rel_foucs.requestFocus();


        btn_cacel.setOnClickListener(this);
        btn_commit.setOnClickListener(this);

        img_action.setImageBitmap(mBitmap);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cacel) {
            listener.dismiss(false);
            dismiss();
        } else if (id == R.id.rel_foucs) {
            listener.dismiss(true);
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }


    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭监听
     */
    public void setOnDismissListener(onDismissListener listener) {
        this.listener = listener;
    }

    public interface onDismissListener {
        void dismiss(boolean ifPlay);
    }
}
