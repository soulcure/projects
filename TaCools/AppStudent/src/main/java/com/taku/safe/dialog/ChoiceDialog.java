package com.taku.safe.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.taku.safe.R;
import com.taku.safe.adapter.SelectAdapter;
import com.taku.safe.entity.ChoiceDialogItem;

import java.util.List;


/**
 * Created by Administrator on 2017/5/27.
 * 单选对话框
 */

public class ChoiceDialog extends Dialog implements View.OnClickListener {


    private RecyclerView recycler_view;

    private List<ChoiceDialogItem> mChoiceList;

    private OnItemClickListener mCallBack;

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class Builder {
        private Context context;
        private List<ChoiceDialogItem> choiceList;

        private OnItemClickListener callBack;

        public ChoiceDialog builder() {
            return new ChoiceDialog(this);
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setList(List<ChoiceDialogItem> choiceList) {
            this.choiceList = choiceList;
            return this;
        }


        public Builder callBack(OnItemClickListener callBack) {
            this.callBack = callBack;
            return this;
        }
    }


    private ChoiceDialog(Builder builder) {
        super(builder.context, R.style.custom_dialog);
        setCanceledOnTouchOutside(true);

        mCallBack = builder.callBack;
        mChoiceList = builder.choiceList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice);
        setDialogFeature();
        init();
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


    private void init() {
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        SelectAdapter adapter = new SelectAdapter(this, mChoiceList);
        if (mCallBack != null) {
            adapter.setOnItemClickListener(mCallBack);
        }

        recycler_view.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:
                cancel();
                break;
        }
    }
}
