package com.ivmall.android.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.CartoonItem;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.player.Mp3PlayingActivity;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private Context mContext;
    private List<CartoonItem> mList;

    private String mInfo;

    public GridViewAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<CartoonItem>();
    }


    public GridViewAdapter(Context context, List<CartoonItem> list) {
        this.mContext = context;
        this.mList = list;
    }


    public void setList(List<CartoonItem> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setInfo(String info) {
        this.mInfo = info;
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
    public GridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.gird_item, null);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final CartoonItem item = mList.get(position);

        /*String tab = item.getTag();  //高清标签取消掉
        if (!StringUtils.isEmpty(tab)) {
            if (tab.equals("hd")) {
                viewHolder.tv_tag.setText(R.string.text_gaoqing);
                viewHolder.tv_tag.setBackgroundResource(R.drawable.gaoqing);
            } else if (tab.equals("sd")) {
                viewHolder.tv_tag.setText(R.string.text_biaoqing);
                viewHolder.tv_tag.setBackgroundResource(R.drawable.biaoqing);
            } else if (tab.equals("audio")) {
                viewHolder.tv_tag.setText("");
                viewHolder.tv_tag.setBackgroundResource(R.drawable.ting);
            }
        } else {
            viewHolder.tv_tag.setText("");
            viewHolder.tv_tag.setBackgroundColor(Color.TRANSPARENT);
        }*/

        if (!item.isEnd()) {
            viewHolder.tv_info.setText(R.string.text_new);
            viewHolder.tv_info.setBackgroundResource(R.drawable.tip_info);
        } else {
            viewHolder.tv_info.setText("");
            viewHolder.tv_info.setBackgroundColor(Color.TRANSPARENT);
        }

        /*int roundPx = mContext.getResources().getDimensionPixelSize(
                R.dimen.image_round_size);*/
        Glide.with(mContext)
                .load(item.getImgUrl())
                .centerCrop()
                //.bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                .error(R.drawable.cartoon_icon_default)        //下载失败
                .into(viewHolder.imgPlayer);

        viewHolder.tvPlayer.setText(item.getTitle());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                if (item.getTag().equals("audio")) {
                    intent = new Intent(mContext, Mp3PlayingActivity.class);
                } else {
                    intent = new Intent(mContext, FreePlayingActivity.class);
                }

                intent.putExtra("type", FreePlayingActivity.FROM_NORMAL);
                intent.putExtra("serieId", item.getSerieId());

                intent.putExtra("info", mInfo);
                mContext.startActivity(intent);
                BaiduUtils.onEvent(mContext, OnEventId.FREE_PLAY_ITEM, mContext.getString(R.string.free_play_item));
            }
        });


    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        ImageView imgPlayer;
        TextView tvPlayer;
        TextView tv_tag;
        TextView tv_info;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imgPlayer = (ImageView) itemView.findViewById(R.id.img_player);
            tvPlayer = (TextView) itemView.findViewById(R.id.tv_player);
            tv_tag = (TextView) itemView.findViewById(R.id.tv_tag);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);

        }

    }


}
