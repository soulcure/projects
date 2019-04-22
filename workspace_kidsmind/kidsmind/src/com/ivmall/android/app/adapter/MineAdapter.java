package com.ivmall.android.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.MineItem;

import java.util.ArrayList;
import java.util.List;


public class MineAdapter extends RecyclerView.Adapter<MineAdapter.ViewHolder> {

    private Context mContext;
    private List<MineItem> mList;


    public MineAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<MineItem>();
    }


    public MineAdapter(Context context, List<MineItem> list) {
        this.mContext = context;
        this.mList = list;
    }


    public void setList(List<MineItem> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public MineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.mine_item, null);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final MineItem item = mList.get(position);
        viewHolder.tvInfo.setText(item.getStringId());
        viewHolder.imgTab.setImageResource(item.getDrawableId());

        viewHolder.imgTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, BaseActivity.class);
                intent.putExtra(BaseActivity.NAME, item.getClickId());
                mContext.startActivity(intent);

            }
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTab;
        TextView tvInfo;

        public ViewHolder(View itemView) {
            super(itemView);

            imgTab = (ImageView) itemView.findViewById(R.id.img_tab);
            tvInfo = (TextView) itemView.findViewById(R.id.img_info);

        }

    }


}
