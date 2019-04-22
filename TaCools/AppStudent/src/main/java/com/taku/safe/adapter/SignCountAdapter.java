package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.activity.SignCountActivity;
import com.taku.safe.activity.TeacherApproveOnMapActivity;
import com.taku.safe.protocol.respond.RespSignRestDataList;
import com.taku.safe.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;


public class SignCountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespSignRestDataList.PageInfoBean.ListBean> mList;

    private int signStatus;     //1 正常 2 异常 3 未签到 4 未激活
    private int signType;

    public SignCountAdapter(Context context, int signType, int signStatus) {
        mContext = context;
        mList = new ArrayList<>();
        this.signType = signType;
        this.signStatus = signStatus;
    }

    public void setList(List<RespSignRestDataList.PageInfoBean.ListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_sign_count, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespSignRestDataList.PageInfoBean.ListBean item = mList.get(position);

        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_class = ((TextViewHolder) viewHolder).tv_class;
        TextView tv_phone = ((TextViewHolder) viewHolder).tv_phone;

        final String name = item.getName();

        if (signStatus == SignCountActivity.NOSIGN
                || signStatus == SignCountActivity.NOREGEDIT) {
            tv_phone.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            View view = ((TextViewHolder) viewHolder).view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, TeacherApproveOnMapActivity.class);
                    intent.putExtra(TeacherApproveOnMapActivity.SIGN_TYPE, signType);
                    intent.putExtra(TeacherApproveOnMapActivity.SIGN_ID, item.getSignId());
                    intent.putExtra(TeacherApproveOnMapActivity.SIGN_STATUS, signStatus);
                    intent.putExtra(TeacherApproveOnMapActivity.SIGN_NAME, name);
                    if (mContext instanceof SignCountActivity) {
                        SignCountActivity act = (SignCountActivity) mContext;
                        act.startActivityForResult(intent, SignCountActivity.PI_ZHU);
                    }
                }
            });
        }

        try {
            final String phoneNum = item.getPhoneNo();
            tv_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(phoneNum) && AppUtils.isMobileNum(phoneNum)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
                        mContext.startActivity(intent);
                    }
                }
            });

            tv_name.setText(name);
            tv_class.setText(item.getClassName());
            tv_phone.setText(phoneNum);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tv_name;
        TextView tv_class;
        TextView tv_phone;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_class = (TextView) itemView.findViewById(R.id.tv_class);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
        }

    }


}

