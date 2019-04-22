package com.ivmall.android.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.RecordItem;

import java.util.List;

public class RecordPlayAdapter extends RecyclerView.Adapter<RecordPlayAdapter.ViewHolder> {

    private Context mContext;
    private List<RecordItem> mLists;

    private TextView tv_tag;

    public RecordPlayAdapter(Context context, List<RecordItem> list) {
        this.mContext = context;
        this.mLists = list;
    }


    @Override
    public RecordPlayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.record_item, null);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // 设置文本和图片，然后返回这个View，用于ListView的Item的展示

        final RecordItem item = mLists.get(position);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FreePlayingActivity.class);
                intent.putExtra("type", FreePlayingActivity.FROM_HOSTORY);
                intent.putExtra("serieId", item.getSerieId());
                intent.putExtra("episodeId", item.getEpisodeId());
                mContext.startActivity(intent);
                BaiduUtils.onEvent(mContext, OnEventId.PLAY_HISTORY_ITEM,
                        mContext.getResources().getString(R.string.play_history_item) + item.getEpisodeName());
            }
        });


        viewHolder.tvEpisodeName.setText(item.getEpisodeName());

        String[] preferences = item.getRates();
        if (preferences != null && preferences.length == 3) {
            viewHolder.tvPreferences1.setText(preferences[0]);
            viewHolder.tvPreferences2.setText(preferences[1]);
            viewHolder.tvPreferences3.setText(preferences[2]);
        } else {
            viewHolder.tvPreferences1.setText(R.string.thinking);
            viewHolder.tvPreferences2.setText(R.string.hanlde);
            viewHolder.tvPreferences3.setText(R.string.talking);
        }


        viewHolder.tvPlayTime.setText(item.getPlayTime());

        int roundPx = mContext.getResources().getDimensionPixelSize(
                R.dimen.image_round_size);

        Glide.with(mContext)
                .load(item.getImgUrl())
                .centerCrop()
                //.bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                .placeholder(R.drawable.img_h_default)  //占位图片
                .error(R.drawable.img_h_default)        //下载失败
                .into(viewHolder.mImageView);


        viewHolder.tvSequence.setText(item.getSequence());

        boolean isVip = ((KidsMindApplication) mContext.getApplicationContext()).isVip();
        if (item.isTrial() && !isVip) {
            viewHolder.tvTag.setBackgroundResource(R.drawable.free);
        } else {
            viewHolder.tvTag.setText("");
            viewHolder.tvTag.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView tvPreferences1;
        TextView tvPreferences2;
        TextView tvPreferences3;
        TextView tvEpisodeName;
        TextView tvPlayTime;
        ImageView mImageView;
        TextView tvTag;
        TextView tvSequence;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvPreferences1 = (TextView) itemView
                    .findViewById(R.id.preferences1);
            tvPreferences2 = (TextView) itemView
                    .findViewById(R.id.preferences2);
            tvPreferences3 = (TextView) itemView
                    .findViewById(R.id.preferences3);
            tvEpisodeName = (TextView) itemView
                    .findViewById(R.id.name);
            tvPlayTime = (TextView) itemView
                    .findViewById(R.id.istime);
            mImageView = (ImageView) itemView
                    .findViewById(R.id.image);
            tvTag = (TextView) itemView.findViewById(R.id.tv_tag);//是否免费
            tvSequence = (TextView) itemView.findViewById(R.id.tv_sequence);//第几集
        }
    }
}
