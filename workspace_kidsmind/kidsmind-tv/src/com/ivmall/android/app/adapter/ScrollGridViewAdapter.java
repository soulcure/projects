package com.ivmall.android.app.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.uitls.GlideRoundTransform;
import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.entity.CartoonItem;
import com.ivmall.android.app.uitls.ScreenUtils;

public class ScrollGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<CartoonItem> mList;

    public ScrollGridViewAdapter(Context context, List<CartoonItem> list) {
        this.mContext = context;
        this.mList = list;
        setThreeTittleList(list);


    }

    private void setThreeTittleList(List<CartoonItem> list) {
        CartoonItem smartPlay = new CartoonItem();
        smartPlay.setTitle(mContext.getResources().getString(R.string.channel));
        smartPlay.setImgId(R.drawable.bg_smart);


        CartoonItem ugcPlay = new CartoonItem();
        ugcPlay.setTitle(mContext.getResources().getString(R.string.global_channel));
        ugcPlay.setImgId(R.drawable.bg_ugc);

        CartoonItem favoritePlay = new CartoonItem();
        favoritePlay.setTitle(mContext.getResources().getString(R.string.my_channel));
        favoritePlay.setImgId(R.drawable.bg_favorite);

        list.add(0, favoritePlay);
        list.add(0, ugcPlay);
        list.add(0, smartPlay);
    }


    public void setList(List<CartoonItem> list) {
        mList.addAll(list);

        notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        CartoonItem item = (CartoonItem) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gird_item, null);

            holder = new ViewHolder();
            holder.relContainer = (RelativeLayout) convertView.findViewById(R.id.rel_img);
            holder.imgPlayer = (ImageView) convertView.findViewById(R.id.img_player);
            holder.imgTag = (ImageView) convertView.findViewById(R.id.img_tag);
            holder.tvPlayer = (TextView) convertView.findViewById(R.id.tv_player);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String url = item.getImgUrl();

        if (StringUtils.isEmpty(url)) {
            holder.relContainer.setBackgroundResource(item.getImgId());
            holder.imgPlayer.setVisibility(View.GONE);
            holder.tvPlayer.setVisibility(View.GONE);
        } else {
            holder.relContainer.setBackgroundResource(R.drawable.bg_grid_item);
            holder.imgPlayer.setVisibility(View.VISIBLE);
            holder.tvPlayer.setVisibility(View.VISIBLE);

            int roundPx = (int) ScreenUtils.dpToPx(mContext, 15);
            Glide.with(mContext)
                    .load(item.getImgUrl())
                    .centerCrop()
                    .bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                    .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                    .error(R.drawable.cartoon_icon_default)        //下载失败
                    .into(holder.imgPlayer);

            holder.tvPlayer.setText(item.getTitle());
        }


        return convertView;
    }


    private class ViewHolder {
        RelativeLayout relContainer;
        ImageView imgPlayer;
        ImageView imgTag;
        TextView tvPlayer;

    }

}
