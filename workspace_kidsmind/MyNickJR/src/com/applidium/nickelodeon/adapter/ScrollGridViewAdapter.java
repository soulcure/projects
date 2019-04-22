package com.applidium.nickelodeon.adapter;

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

import com.applidium.nickelodeon.entity.SerieItemInfo;
import com.bumptech.glide.Glide;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.entity.CartoonItem;
import com.applidium.nickelodeon.uitls.GlideRoundTransform;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;

public class ScrollGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<SerieItemInfo> mList;

    public ScrollGridViewAdapter(Context context, List<SerieItemInfo> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setList(List<SerieItemInfo> list) {
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
        SerieItemInfo item = (SerieItemInfo) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            /*if (position == 0) {
                // 加载我的小尼克频道
                //convertView = LayoutInflater.from(mContext).inflate();
            } else {*/
                convertView = LayoutInflater.from(mContext).inflate(R.layout.metro_normal_item, null);
                holder = new ViewHolder();
                holder.imgPlayer = (ImageView) convertView.findViewById(R.id.img_serie);
                holder.tvPlayer = (TextView) convertView.findViewById(R.id.tv_serie);
                convertView.setTag(holder);
          //  }

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String url = item.getSubImg();
        if (StringUtils.isEmpty(url)) {
            holder.imgPlayer.setVisibility(View.GONE);
            holder.tvPlayer.setVisibility(View.GONE);
        } else {
            holder.imgPlayer.setVisibility(View.VISIBLE);
            holder.tvPlayer.setVisibility(View.VISIBLE);

            int roundPx = (int) ScreenUtils.dpToPx(mContext, 15);
            Glide.with(mContext)
                    .load(item.getSubImg())
                    .centerCrop()
                    .bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                    .placeholder(R.drawable.ic_launcher)  //占位图片
                    .error(R.drawable.ic_launcher)        //下载失败
                    .into(holder.imgPlayer);

            holder.tvPlayer.setText(item.getSubName()+"");
        }
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout relContainer;
        ImageView imgPlayer;
        TextView tvPlayer;
        TextView tvInfo;
    }
}
