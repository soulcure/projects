package com.taku.safe.evaluation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.activity.WebViewActivity;
import com.taku.safe.protocol.respond.RespEvaluation;

import java.util.List;


public class EvaluationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespEvaluation.TestListBean> mList;

    public EvaluationAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RespEvaluation.TestListBean> list) {
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
        View view = inflater.inflate(R.layout.item_evaluation, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespEvaluation.TestListBean item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).itemView;

        ImageView img_title = ((TextViewHolder) viewHolder).img_title;
        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        TextView tv_number = ((TextViewHolder) viewHolder).tv_number;

        tv_title.setText(item.getTitle());
        tv_number.setText(item.getTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.TEST_ID, item.getTestId());
                mContext.startActivity(intent);
            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        ImageView img_title;
        TextView tv_title;
        TextView tv_number;


        private TextViewHolder(View itemView) {
            super(itemView);
            img_title = (ImageView) itemView.findViewById(R.id.img_title);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
        }

    }


}

