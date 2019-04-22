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
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GlideRoundTransform;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.SerieItem;

import java.util.List;


public class FreePlayAdapter extends RecyclerView.Adapter<FreePlayAdapter.ViewHolder> {

    private Context mContext;
    private List<SerieItem> mLists;
    private int mSelectItem;
    private boolean isEnd;

    private TextView tv_tag;

    public FreePlayAdapter(Context context, List<SerieItem> list) {
        mContext = context;
        mLists = list;
    }

    public FreePlayAdapter(Context context, List<SerieItem> list, boolean isEnd) {
        mContext = context;
        mLists = list;
        this.isEnd = isEnd;
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

    public void setFavoritedByEpisodeId(int episodeId, boolean favorite) {
        for (SerieItem item : mLists) {
            if (item.getEpisodeId() == episodeId) {
                item.setFavorite(favorite);
            }
        }
    }


    @Override
    public FreePlayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.free_play_item, null);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        // 设置文本和图片，然后返回这个View，用于ListView的Item的展示

        final SerieItem item = mLists.get(position);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FreePlayingActivity) mContext).reqPlayUrl(item);
                setSelectItem(viewHolder.getAdapterPosition());

                BaiduUtils.onEvent(mContext, OnEventId.FREE_CLICK_ITEM,
                        mContext.getString(R.string.free_click_item) + item.getTitle());

            }
        });


        int roundPx = mContext.getResources().getDimensionPixelSize(R.dimen.image_round_size) + 7;
        Glide.with(mContext)
                .load(item.getImgUrl())
                .centerCrop()
                .bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                .placeholder(R.drawable.img_h_default)  //占位图片
                .error(R.drawable.img_h_default)        //下载失败
                .into(viewHolder.imgFrame);


        viewHolder.tvSequence.setText(item.getSequence());
        viewHolder.tvCollect.setText(item.getTitle());

        boolean isVip = ((KidsMindApplication) mContext.getApplicationContext()).isVip();
        if (item.isTrial() && !isVip) {
            viewHolder.tvTag.setBackgroundResource(R.drawable.free);
        } else {
            viewHolder.tvTag.setText("");
            viewHolder.tvTag.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }


        if (position == mSelectItem) {
            viewHolder.tvRelativeBg.setBackgroundResource(R.drawable.bg_playlist_item);
        } else {
            viewHolder.tvRelativeBg.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
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
        RelativeLayout tvRelativeBg;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imgFrame = (ImageView) itemView.findViewById(R.id.img_frame);// 图片
            tvCollect = (TextView) itemView.findViewById(R.id.tv_collect);//描述
            tvTag = (TextView) itemView.findViewById(R.id.tv_tag);//是否免费
            tvSequence = (TextView) itemView.findViewById(R.id.tv_sequence);//第几集
            tvRelativeBg = (RelativeLayout) itemView.findViewById(R.id.relative_bg);

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
