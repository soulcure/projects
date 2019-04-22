package com.applidium.nickelodeon.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.dialog.BuyVipDialog;
import com.applidium.nickelodeon.entity.ContentItem;
import com.applidium.nickelodeon.player.FreePlayingActivity;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.GlideRoundTransform;
import com.applidium.nickelodeon.uitls.OnEventId;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;

import static android.view.View.OnClickListener;

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ContentItem> mList;
    private int mSerieId;


    public GridViewAdapter(Context context, ArrayList<ContentItem> list, int serieId) {
        this.mContext = context;
        this.mList = list;
        this.mSerieId = serieId;
    }


    public void setList(List<ContentItem> list) {
        mList.addAll(list);

        notifyDataSetChanged();
    }


    public ArrayList<ContentItem> getList() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ContentItem item = (ContentItem) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gird_item, null);
            holder = new ViewHolder();
            holder.imgPlayer = (ImageView) convertView.findViewById(R.id.img_player);
            holder.tvPlayer = (TextView) convertView.findViewById(R.id.tv_player);
            holder.tv_tag = (TextView) convertView.findViewById(R.id.tv_tag);
            holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        boolean tab = item.isTrial();
        if (tab) {
            holder.tv_tag.setText(mContext.getString(R.string.free));
            holder.tv_tag.setBackgroundResource(R.drawable.grid_item_shape);
        } else {
            holder.tv_tag.setText("");
            holder.tv_tag.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.tv_info.setText(item.getSequence());

        Glide.with(mContext)
                .load(item.getContentImg())
                .centerCrop()
                .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                .error(R.drawable.cartoon_icon_default)        //下载失败
                .into(holder.imgPlayer);

        holder.tvPlayer.setText(item.getContentName());

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isVip = ((MNJApplication) mContext.getApplicationContext()).isVip();
                if (!item.isTrial() && !isVip) {
                    BuyVipDialog dialog = new BuyVipDialog(mContext, BuyVipDialog.VIP_TYPE.VIP_TRIAL);
                    dialog.show();
                } else {
                    Intent intent = new Intent(mContext, FreePlayingActivity.class);
                    intent.putExtra("serieId", mSerieId);
                    intent.putExtra("position", position);
                    intent.putParcelableArrayListExtra("series", mList);
                    mContext.startActivity(intent);
                }
            }
        });
        convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });
        return convertView;
    }


    private class ViewHolder {
        ImageView imgPlayer;
        TextView tvPlayer;
        TextView tv_tag;
        TextView tv_info;
    }

}
