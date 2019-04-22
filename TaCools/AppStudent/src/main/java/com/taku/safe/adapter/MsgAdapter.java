package com.taku.safe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.db.bean.NoticeMsg;
import com.taku.safe.utils.TimeUtils;

import java.util.List;


public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NoticeMsg> mList;

    public MsgAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<NoticeMsg> list) {
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
        View view = inflater.inflate(R.layout.item_msg, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NoticeMsg item = mList.get(position);

        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        TextView tv_content = ((TextViewHolder) viewHolder).tv_content;

        tv_date.setText(TimeUtils.getTime(item.getTime(), TimeUtils.DATE_FORMAT_MONTH_TEXT));
        tv_title.setText(item.getTitle());
        tv_content.setText(item.getContent());

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date;
        TextView tv_title;
        TextView tv_content;

        private TextViewHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }

    }


}

