package com.taku.safe.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.protocol.respond.RespIndoorDetail;
import com.taku.safe.protocol.respond.RespUnusualOutDetail;

import java.util.List;


public class IndoorDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    enum UI_Type {
        LIST_HEARD, LIST_NORMAL
    }

    private Context mContext;
    private List<RespIndoorDetail.EventListBean> mList;


    public IndoorDetailAdapter(Context context, List<RespIndoorDetail.EventListBean> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {  //给Item划分类别
        int type;
        if (position == 0) {
            type = UI_Type.LIST_HEARD.ordinal();
        } else {
            type = UI_Type.LIST_NORMAL.ordinal();
        }
        return type;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_hardware_not_class_detail, parent, false);
        RecyclerView.ViewHolder holder = new TextViewHolder(view);

        int gray = ContextCompat.getColor(mContext, R.color.text_gray_30);
        int black = ContextCompat.getColor(mContext, R.color.text_gray_87);

        switch (UI_Type.values()[viewType]) {   //根据Item类别不同，选择不同的Item布局
            case LIST_HEARD: {
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(gray);
                ((TextView) view.findViewById(R.id.tv_days)).setTextColor(gray);
                ((TextView) view.findViewById(R.id.tv_content)).setTextColor(gray);
            }
            break;
            case LIST_NORMAL: {
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(black);
                ((TextView) view.findViewById(R.id.tv_days)).setTextColor(black);
                ((TextView) view.findViewById(R.id.tv_content)).setTextColor(black);
            }
            break;
        }
        return holder;
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final RespIndoorDetail.EventListBean item = mList.get(position);

        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        TextView tv_days = ((TextViewHolder) viewHolder).tv_days;
        TextView tv_content = ((TextViewHolder) viewHolder).tv_content;


        tv_title.setText(item.getRangeDay());
        int days = item.getDays();
        if (days == -1) {
            tv_days.setText(R.string.not_class_days);
        } else {
            tv_days.setText(String.format(mContext.getString(R.string.days), days));
        }
        tv_content.setText(item.getLastShowDate());
    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_days;
        TextView tv_content;

        private TextViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_days = (TextView) itemView.findViewById(R.id.tv_days);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }

    }


}

