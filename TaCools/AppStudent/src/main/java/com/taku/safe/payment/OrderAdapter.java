package com.taku.safe.payment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.protocol.respond.RespAction;
import com.taku.safe.protocol.respond.RespOrder;

import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespOrder> mList;

    public OrderAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RespOrder> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_order, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespOrder item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).itemView;

        TextView tv_order_title = ((TextViewHolder) viewHolder).tv_order_title;
        TextView tv_pay_time = ((TextViewHolder) viewHolder).tv_pay_time;
        TextView tv_order_id = ((TextViewHolder) viewHolder).tv_order_id;
        TextView tv_price = ((TextViewHolder) viewHolder).tv_price;

        tv_order_title.setText(item.getTitle());
        tv_pay_time.setText(item.getDate());
        tv_order_id.setText(item.getOrderId());
        tv_price.setText(item.getPrice());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order_title;
        TextView tv_pay_time;
        TextView tv_order_id;
        TextView tv_price;


        private TextViewHolder(View itemView) {
            super(itemView);
            tv_order_title = (TextView) itemView.findViewById(R.id.tv_order_title);
            tv_pay_time = (TextView) itemView.findViewById(R.id.tv_pay_time);
            tv_order_id = (TextView) itemView.findViewById(R.id.tv_order_id);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
        }

    }


}

