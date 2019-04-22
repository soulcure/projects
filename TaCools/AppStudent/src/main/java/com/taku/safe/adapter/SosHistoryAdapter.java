package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.activity.SosInfoOnMapActivity;
import com.taku.safe.protocol.respond.RespSosList;

import java.util.ArrayList;
import java.util.List;


public class SosHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespSosList.PageInfoBean.ListBean> mList;

    public SosHistoryAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<RespSosList.PageInfoBean.ListBean> list) {
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
        View view = inflater.inflate(R.layout.item_sos_info, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespSosList.PageInfoBean.ListBean item = mList.get(position);


        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_class = ((TextViewHolder) viewHolder).tv_class;
        TextView tv_phone = ((TextViewHolder) viewHolder).tv_phone;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        TextView tv_status = ((TextViewHolder) viewHolder).tv_status;

        View view = ((TextViewHolder) viewHolder).view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SosInfoOnMapActivity.class);
                intent.putExtra("sosId", item.getSosId());
                mContext.startActivity(intent);
            }
        });

        tv_name.setText(item.getStudentName());
        tv_class.setText(item.getClassName());
        //tv_phone.setText(item.getPhone());
        tv_time.setText(item.getSosDate());

        int status = item.getSosStatus();
        if (status == 0) {
            tv_status.setText("已安全");
            tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_blue));
        } else if (status == 1) {
            tv_status.setText("救援中");
            tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_red));
        } else if (status == -1) {
            tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_orange));
            tv_status.setText("超时结束");
        }
    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_name;
        TextView tv_class;
        TextView tv_phone;
        TextView tv_time;
        TextView tv_status;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_class = (TextView) itemView.findViewById(R.id.tv_class);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
        }

    }


}

