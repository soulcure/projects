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


import com.bumptech.glide.Glide;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GlideRoundTransform;
import com.ivmall.android.app.uitls.OnEventId;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.entity.FavoriteItem;

import java.util.List;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context mContext;
    private List<FavoriteItem> mLists;
    private int mSelectItem;

    private TextView tv_tag;

    public FavoriteAdapter(Context context, List<FavoriteItem> list) {
        mContext = context;
        mLists = list;
    }

    public List<FavoriteItem> getLists() {
        return mLists;
    }

    public void setList(List<FavoriteItem> lists) {
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

    public int removeItem(FavoriteItem item) {
        int index = 0;
        if (mLists.contains(item)) {
            index = mLists.indexOf(item);
            mLists.remove(item);
            notifyDataSetChanged();
        }
        return index;
    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.free_play_item, null);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // 设置文本和图片，然后返回这个View，用于ListView的Item的展示

        final FavoriteItem item = mLists.get(position);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FreePlayingActivity) mContext).reqPlayUrl(item);
                setSelectItem(position);

                BaiduUtils.onEvent(mContext, OnEventId.FREE_CLICK_ITEM,
                        mContext.getString(R.string.free_click_item) + item.getEpisodeName());

            }
        });


        int roundPx = mContext.getResources().getDimensionPixelSize(
                R.dimen.image_round_size);
        Glide.with(mContext)
                .load(item.getImgUrl())
                .centerCrop()
                .bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                .placeholder(R.drawable.img_h_default)  //占位图片
                .error(R.drawable.img_h_default)        //下载失败
                .into(viewHolder.imgFrame);

        viewHolder.tvCollect.setText(item.getEpisodeName());

        boolean isVip = ((KidsMindApplication) mContext.getApplicationContext()).isVip();
        if (item.isTrial() && !isVip) {
            viewHolder.tvTag.setBackgroundResource(R.drawable.free);
        } else {
            viewHolder.tvTag.setText("");
            viewHolder.tvTag.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }


        if (position == mSelectItem) {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.skyblue));
            viewHolder.itemView.requestFocus();
        } else {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            if (position == 0) {
                viewHolder.itemView.requestFocus();
            }
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

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imgFrame = (ImageView) itemView.findViewById(R.id.img_frame);// 图片
            tvCollect = (TextView) itemView.findViewById(R.id.tv_collect);//描述
            tvTag = (TextView) itemView.findViewById(R.id.tv_tag);//是否免费

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
