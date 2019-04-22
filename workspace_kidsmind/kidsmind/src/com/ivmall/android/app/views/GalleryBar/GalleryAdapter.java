package com.ivmall.android.app.views.GalleryBar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.TopicItem;

import java.util.List;

/**
 * Created by koen on 2016/5/9.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private LayoutInflater mInflater;
    private List<TopicItem> mDatas;
    private Context mContext;


    public GalleryAdapter(Context context, List<TopicItem> datats)
    {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.gallery_item_view,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.imgView = (ImageView) view.findViewById(R.id.paid_image_view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mDatas.get(position % mDatas.size()).getImgUrl())
                .centerCrop()
                .placeholder(R.drawable.img_h_default)  //占位图片
                .error(R.drawable.img_h_default)        //下载失败
                .into(holder.imgView);
        if (onItemClickLitener != null) {
            holder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = holder.getAdapterPosition() - 1;
                    int i = index % mDatas.size();
                    onItemClickLitener.onItemClick(mDatas.get(index % mDatas.size()), index);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mDatas != null)
            return mDatas.size() * 500;
        //return Integer.MAX_VALUE;
        else
            return 0;
    }


    public interface  OnItemClickLitener {
        void onItemClick(TopicItem item, int position);
    }

    private OnItemClickLitener onItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener litener) {
        this.onItemClickLitener = litener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
