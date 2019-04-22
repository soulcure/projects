package com.ivmall.android.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.uitls.AppUtils;


public class UpdateDialog extends AlertDialog {

    /**
     * 升级提示描述
     */
    private TextView tvUpdateContent;

    private TextView tvVersion;

    private TextView tvApkSize;
    /**
     * 确认升级按钮
     */
    private Button btnConfir;
    /**
     * 取消升级按钮
     */
    private Button btnCancel;

    private Context mContext;

    private LinearLayout system_pop_ll_left;

    private int rate;

    private View.OnClickListener cancelListener;
    /**
     * 进度说明
     */
    private TextView tvRate;

    /**
     * 进度条
     */
    private ProgressBar prgRate;


    public int getRate() {
        return rate;
    }


    public UpdateDialog(Context context) {
        super(context);
        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_dialog);
        initView();
    }

    private void initView() {
        tvUpdateContent = (TextView) this.findViewById(R.id.tvUpdateContent);

        tvVersion = (TextView) this.findViewById(R.id.tvVersion);
        tvApkSize = (TextView) this.findViewById(R.id.tvApkSize);

        btnCancel = (Button) this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dismiss();
                } catch (Exception e) {

                }
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });


        btnConfir = (Button) this.findViewById(R.id.btnConfir);
        btnConfir.requestFocus();

        system_pop_ll_left = (LinearLayout) this.findViewById(R.id.system_pop_ll_left);

        tvRate = (TextView) this.findViewById(R.id.tvRate);
        prgRate = (ProgressBar) this.findViewById(R.id.prgRate);


    }


    /**
     * 设置更新描述
     *
     * @param desc 更新描述内容
     */
    public void setUpgradeDesc(String desc) {
        tvUpdateContent.setText(desc);
    }

    public void setVersion(String version) {
        tvVersion.setText(tvVersion.getText() + version);
    }

    public void setApkSize(String size) {
        int apkSize;
        try {
            apkSize = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            apkSize = 0;
        }
        tvApkSize.setText(tvApkSize.getText() + AppUtils.bytes2mb(apkSize));
    }


    /**
     * 确定更新所做的操作
     */
    public void setOnEnsureUpgradeListener(View.OnClickListener listener) {
        btnConfir.setOnClickListener(listener);
    }

    /**
     * 取消更新所做的操作
     */
    public void setOnCancelUpgradeListener(View.OnClickListener listener) {
        cancelListener = listener;
    }


    public void setOnCancelUpgradeListener(boolean isMustUpdate, View.OnClickListener listener) {
        if (isMustUpdate) {
            system_pop_ll_left.setVisibility(View.GONE);
            btnConfir.setText("确定");
            this.setCancelable(false);
        }
        btnCancel.setOnClickListener(listener);
    }

    public void setProgressBar(int rate) {
        this.rate = rate;
        if (prgRate != null) {
            prgRate.setProgress(rate);
        }
    }

    public void setRateText(int rate) {
        StringBuilder sb = new StringBuilder();
        sb.append(rate);
        sb.append('%');
        sb.append(' ');


        if (tvRate != null) {
            tvRate.setText(sb.toString());
        }
    }

    public void showInstall() {
        prgRate.setVisibility(View.GONE);
        tvRate.setText("wifi后台已自动为您准备好了安装包");

        system_pop_ll_left.setVisibility(View.GONE);
        btnConfir.setText(mContext.getString(R.string.update_Install));
    }

}
