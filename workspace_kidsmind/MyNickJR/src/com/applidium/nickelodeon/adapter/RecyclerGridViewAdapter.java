package com.applidium.nickelodeon.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.dialog.BuyVipDialog;
import com.applidium.nickelodeon.entity.ContentItem;
import com.applidium.nickelodeon.player.FreePlayingActivity;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.views.SubSeriesView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {

    private static final String TAG = RecyclerGridViewAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<ContentItem> mLists;
    private int mSerieId;
    private ViewFocusListener viewFocus;

    public interface ViewFocusListener {
        void onFocusChange(int postion);
    }

    public RecyclerGridViewAdapter(Context context, ArrayList<ContentItem> list, int serieId) {
        this.mContext = context;
        this.mLists = list;
        this.mSerieId = serieId;
    }

    public ArrayList<ContentItem> getList() {
        return mLists;
    }

    public void setList(List<ContentItem> list) {
        mLists.addAll(list);

        notifyDataSetChanged();
    }

    public void setViewFocusListener(ViewFocusListener viewFocus) {
        this.viewFocus = viewFocus;
    }

    @Override
    public RecyclerGridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.gird_item, null);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // 设置文本和图片，然后返回这个View，用于ListView的Item的展示
        final ContentItem item = mLists.get(position);
        viewHolder.rel_img.setTag(position);
        viewHolder.rel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MediaPlayerService.playSound(mContext, MediaPlayerService.ONCLICK);
                boolean isVip = ((MNJApplication) mContext.getApplicationContext()).isVip();
                if (!item.isTrial() && !isVip) {
                    BuyVipDialog dialog = new BuyVipDialog(mContext, BuyVipDialog.VIP_TYPE.VIP_TRIAL);
                    dialog.show();
                } else {
                    Intent intent = new Intent(mContext, FreePlayingActivity.class);
                    intent.putExtra("serieId", mSerieId);
                    intent.putExtra("position", position);
                    intent.putParcelableArrayListExtra("series", mLists);
                    mContext.startActivity(intent);
                }

            }
        });

        Glide.with(mContext)
                .load(item.getContentImg())
                .fitCenter()
                .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                .error(R.drawable.cartoon_icon_default)        //下载失败
                .into(viewHolder.imgPlayer);


        viewHolder.tv_info.setText(item.getSequence());
        viewHolder.tvPlayer.setText(item.getContentName());


        boolean isVip = ((MNJApplication) mContext.getApplicationContext()).isVip();
        if (item.isTrial() && !isVip) {
            viewHolder.tv_tag.setText(R.string.free);
            viewHolder.tv_tag.setBackgroundResource(R.drawable.free);
        } else {
            viewHolder.tv_tag.setText("");
            viewHolder.tv_tag.setBackgroundColor(Color.TRANSPARENT);
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
        ImageView imgPlayer;
        TextView tvPlayer;
        TextView tv_tag;
        TextView tv_info;
        SubSeriesView rel_img;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            imgPlayer = (ImageView) itemView.findViewById(R.id.img_player);
            tvPlayer = (TextView) itemView.findViewById(R.id.tv_player);
            tv_tag = (TextView) itemView.findViewById(R.id.tv_tag);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
            rel_img = (SubSeriesView) itemView.findViewById(R.id.rel_img);

            rel_img.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {

                ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                        new float[]{1.0F, 1.05F}).setDuration(200);
                ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                        new float[]{1.0F, 1.05F}).setDuration(200);
                AnimatorSet scaleAnimator = new AnimatorSet();
                scaleAnimator.playTogether(new Animator[]{animX, animY});
                scaleAnimator.start();

            } else {
                ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                        new float[]{1.05F, 1.0F}).setDuration(200);
                ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                        new float[]{1.05F, 1.0F}).setDuration(200);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{animX, animY}); //设置两个动画一起执行
                animatorSet.start();
            }
        }
    }

}
