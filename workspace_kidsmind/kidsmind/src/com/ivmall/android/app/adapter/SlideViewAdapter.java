package com.ivmall.android.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.R;

import java.util.List;


/**
 * Created by smit on 2015/11/3.
 */
public class SlideViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> imageUrlList;
    private int size;

    public SlideViewAdapter(Context context, List<String> list) {
        mContext = context;
        imageUrlList = list;
        if (imageUrlList != null) {
            size = imageUrlList.size();
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private int getPosition(int position) {
        return position % size;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.slide_view_item, null);
            holder.imageView = (ImageView) view.findViewById(R.id.slide_imageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        /*int roundPx = mContext.getResources().getDimensionPixelSize(
                R.dimen.image_round_size);*/
        Glide.with(mContext)
                .load(imageUrlList.get(getPosition(i)))
                .centerCrop()
                //.bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                .placeholder(R.drawable.icon_h_default)
                .error(R.drawable.icon_h_default)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    private class ViewHolder {

        ImageView imageView;
    }
}
