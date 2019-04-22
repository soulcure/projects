package com.applidium.nickelodeon.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.applidium.nickelodeon.entity.ContentItem;
import com.bumptech.glide.Glide;
import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.entity.SerieItem;
import com.applidium.nickelodeon.player.FreePlayingActivity;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.GlideRoundTransform;
import com.applidium.nickelodeon.uitls.OnEventId;

import java.util.ArrayList;
import java.util.List;


public class FreePlayAdapter extends RecyclerView.Adapter<FreePlayAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ContentItem> mLists;
    private int mSelectItem;

    private TextView tv_tag;

    public FreePlayAdapter(Context context, ArrayList<ContentItem> list) {
        mContext = context;
        mLists = list;
    }

    public ArrayList<ContentItem> getLists() {
        return mLists;
    }

    public void setList(List<ContentItem> lists) {
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
    public FreePlayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.free_play_item, null);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // 设置文本和图片，然后返回这个View，用于ListView的Item的展示

        final ContentItem item = mLists.get(position);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FreePlayingActivity) mContext).reqPlayUrl(item);
                setSelectItem(position);
            }
        });


        int roundPx = mContext.getResources().getDimensionPixelSize(
                R.dimen.image_round_size);
        Glide.with(mContext)
                .load(item.getContentImg())
                .centerCrop()
                .bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                .placeholder(R.drawable.ic_launcher)  //占位图片
                .error(R.drawable.ic_launcher)        //下载失败
                .into(viewHolder.imgFrame);


        viewHolder.tvSequence.setText(item.getSequence());
        viewHolder.tvCollect.setText(item.getContentName());

        boolean isVip = ((MNJApplication) mContext.getApplicationContext()).isVip();
        if (item.isTrial() && !isVip) {
            viewHolder.tvTag.setText(R.string.free);
            viewHolder.tvTag.setBackgroundResource(R.drawable.free);
        } else {
            viewHolder.tvTag.setText("");
            viewHolder.tvTag.setBackgroundColor(Color.TRANSPARENT);
        }


        if (position == mSelectItem) {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.skyblue));
        } else {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
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
        TextView tvTag;
        TextView tvSequence;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imgFrame = (ImageView) itemView.findViewById(R.id.img_frame);// 图片
            tvCollect = (TextView) itemView.findViewById(R.id.tv_collect);//描述
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
