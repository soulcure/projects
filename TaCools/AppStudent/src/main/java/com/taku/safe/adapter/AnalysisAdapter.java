package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.entity.AnalysisItem;

import java.util.List;


public class AnalysisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<AnalysisItem> mList;

    public AnalysisAdapter(Context context, List<AnalysisItem> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_analysis, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final AnalysisItem item = mList.get(position);

        TextView name = ((TextViewHolder) viewHolder).name;
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, item.mClass);
                intent.putExtra("name", item.name);
                intent.putExtra("resId", item.resId);
                mContext.startActivity(intent);
            }
        });

        name.setText(item.name);

        Drawable drawable = ContextCompat.getDrawable(mContext, item.resId);
        name.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        private TextViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
        }

    }


}

