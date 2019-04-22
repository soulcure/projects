package com.ivmall.android.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.InviteRequest;
import com.ivmall.android.app.entity.InviteResponse;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

import java.lang.ref.WeakReference;


/**
 * Created by Markry on 2015/11/5.
 */
public class ExchangeDialog extends AlertDialog implements View.OnClickListener {
    private static final int DIG_DISMISS = 0;

    private Context mContext;
    private EditText editText;

    private MainHandler mHandler;

    private KidsMindApplication application;


    public ExchangeDialog(Context context) {
        //super(context, R.style.full_dialog);
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//重新显示软键盘

        setContentView(R.layout.exchange_layout);

        if (mHandler == null)
            mHandler = new MainHandler(this);
        else
            mHandler.setTarget(this);

        application = ((KidsMindApplication) mContext.getApplicationContext());
        editText = (EditText) findViewById(R.id.edit_exchange_num);

        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_do_exg).setOnClickListener(this);

    }

    /**
     * 兑换
     */
    private void doExChange() {
        String code = editText.getText().toString();
        if (code.length() > 0) {
            String token = application.getToken();
            String url = AppConfig.USE_INVATE_CODE;
            InviteRequest request = new InviteRequest();
            request.setToken(token);
            request.setCode(code);

            HttpConnector.httpPost(url, request.toJsonString(), new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    InviteResponse resp = GsonUtil.parse(response,
                            InviteResponse.class);
                    if (resp != null && resp.isSucess()) {
                        findViewById(R.id.relative_layout).setVisibility(View.GONE);
                        ImageButton imageSuccess = (ImageButton) findViewById(R.id.image_exchange_succes);
                        imageSuccess.setVisibility(View.VISIBLE);
                        imageSuccess.requestLayout();
                        imageSuccess.setOnClickListener(ExchangeDialog.this);
                        Message msg = new Message();
                        msg.what = DIG_DISMISS;
                        mHandler.sendMessageDelayed(msg, 3000);

                        application.reqUserInfo();
                    } else {
                        Toast.makeText(mContext, resp.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.text_input_interNum),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_do_exg:
                doExChange();
                break;
            case R.id.image_exchange_succes:
                if (mHandler.hasMessages(DIG_DISMISS)) {
                    mHandler.removeMessages(DIG_DISMISS);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mHandler.hasMessages(DIG_DISMISS)) {
            mHandler.removeMessages(DIG_DISMISS);
        }
    }


    private static class MainHandler extends Handler {
        private WeakReference<ExchangeDialog> mTarget;

        MainHandler(ExchangeDialog target) {
            mTarget = new WeakReference<ExchangeDialog>(target);
        }

        public void setTarget(ExchangeDialog target) {
            mTarget.clear();
            mTarget = new WeakReference<ExchangeDialog>(target);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            ExchangeDialog dialog = mTarget.get();
            switch (msg.what) {
                case DIG_DISMISS:// 裁剪图片
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }

    }
}
