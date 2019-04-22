package com.ivmall.android.app.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivmall.android.app.KidsMindApplication;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.entity.SerieItem;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.OnEventId;

import java.util.List;


public class MusicPlayAdapter extends RecyclerView.Adapter<MusicPlayAdapter.ViewHolder> {

    private Context mContext;
    private List<SerieItem> mLists;
    private int mSelectItem;

    private TextView tvTag;

    public MusicPlayAdapter(Context context, List<SerieItem> list) {
        mContext = context;
        mLists = list;
    }

    public List<SerieItem> getLists() {
        return mLists;
    }

    public void setList(List<SerieItem> lists) {
        mLists.addAll(lists);
        notifyDataSetChanged();
    }

    public int getSelectItem() {
        return mSelectItem;
    }

    public void setSelectItem(int selectItem) {
        mSelectItem = selectItem;
        notifyDataSetChanged();
    }


    @Override
    public MusicPlayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.music_play_item, null);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // 设置文本和图片，然后返回这个View，用于ListView的Item的展示

        final SerieItem item = mLists.get(position);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectItem(position);
                ((FreePlayingActivity) mContext).reqPlayUrl(item);
                BaiduUtils.onEvent(mContext, OnEventId.FREE_CLICK_ITEM,
                        mContext.getString(R.string.free_click_item) + item.getTitle());

            }
        });


        viewHolder.tvSequence.setText(item.getSequence());
        viewHolder.tvCollect.setText(item.getTitle());

        boolean isVip = ((KidsMindApplication) mContext.getApplicationContext()).isVip();
        if (item.isTrial() && !isVip) {
            viewHolder.tvTag.setBackgroundResource(R.drawable.free);
        } else {
            viewHolder.tvTag.setText("");
            viewHolder.tvTag.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }

        //viewHolder.tvPlayTime.setText("5:30");  //设置播放时间
        if (position == mSelectItem) {
            viewHolder.imgFrame.setImageResource(R.drawable.bg_audio_orange);
        } else {
            viewHolder.imgFrame.setImageResource(R.drawable.bg_audio_green);
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


    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnFocusChangeListener {

        View itemView;
        ImageView imgFrame;
        TextView tvCollect;
        TextView tvPlayTime;
        TextView tvTag;
        TextView tvSequence;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imgFrame = (ImageView) itemView.findViewById(R.id.img_frame);// 图片
            tvCollect = (TextView) itemView.findViewById(R.id.tv_collect);//描述
            tvPlayTime = (TextView) itemView.findViewById(R.id.tv_play_time);//时间
            tvTag = (TextView) itemView.findViewById(R.id.tv_tag);//是否免费
            tvSequence = (TextView) itemView.findViewById(R.id.tv_sequence);//第几集

            itemView.setOnFocusChangeListener(this);
        }


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                        new float[]{1.0F, 1.1F}).setDuration(200);
                ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                        new float[]{1.0F, 1.1F}).setDuration(200);
                AnimatorSet scaleAnimator = new AnimatorSet();
                scaleAnimator.playTogether(new Animator[]{animX, animY});
                scaleAnimator.start();
            } else {
                ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                        new float[]{1.1F, 1.0F}).setDuration(200);
                ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                        new float[]{1.1F, 1.0F}).setDuration(200);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{animX, animY}); //设置两个动画一起执行
                animatorSet.start();
            }
        }


    }

}
