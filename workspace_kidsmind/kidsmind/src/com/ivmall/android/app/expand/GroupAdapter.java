package com.ivmall.android.app.expand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.CartoonItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 2016/4/8.
 */
 public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    private int mCurSelectPosion = 0;
    private List<CartoonItem> checkItems;
    private Context mContext;

    public GroupAdapter(Context context) {
        checkItems = new ArrayList<>();
        mContext = context;
    }

    public List<CartoonItem> getCheckItems() {
        return checkItems;
    }

    public void setCheckItems(List<CartoonItem> items) {
        checkItems = items;
        notifyDataSetChanged();
    }

    public int getmCurSelectPosion() {
        return mCurSelectPosion;
    }

    public void setmCurSelectPosion(int i) {
        mCurSelectPosion = i;
        notifyDataSetChanged();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GroupViewHolder holder = new GroupViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.group_item_layout, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, final int position) {
        if (position == mCurSelectPosion) {
            holder.itemView.setBackgroundResource(R.color.white);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundResource(R.color.window_background);
            holder.imageView.setVisibility(View.INVISIBLE);
        }

        holder.textView.setText(checkItems.get(position).getTitle());
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position, checkItems.get(position).getSerieId());
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return checkItems.size();
    }


    public class GroupViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        public GroupViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.group_item_title);
            imageView = (ImageView) itemView.findViewById(R.id.group_item_img_tag);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, int serieId);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener litener) {
        mOnItemClickLitener = litener;
    }

}
