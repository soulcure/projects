package com.taku.safe.action;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.protocol.respond.RespAction;
import com.taku.safe.protocol.respond.RespEvaluation;

import java.util.List;


public class ActionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespAction> mList;

    public ActionAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RespAction> list) {
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
        View view = inflater.inflate(R.layout.item_action, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespAction item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).itemView;

        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        TextView tv_teacher = ((TextViewHolder) viewHolder).tv_teacher;

        tv_title.setText(item.getTitle());
        tv_date.setText(item.getDate());
        tv_teacher.setText(item.getTeacher());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_date;
        TextView tv_teacher;


        private TextViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_teacher = (TextView) itemView.findViewById(R.id.tv_teacher);
        }

    }


}

