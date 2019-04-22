package com.ivmall.android.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.BbsWebActivity;
import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.BBSItem;

import java.util.ArrayList;
import java.util.List;


public class BbsFromMeAdapter extends RecyclerView.Adapter<BbsFromMeAdapter.ViewHolder> {

    private Context mContext;
    private List<BBSItem> mList;

    public BbsFromMeAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<BBSItem>();
    }


    public BbsFromMeAdapter(Context context, List<BBSItem> list) {
        this.mContext = context;
        this.mList = list;
    }


    public void setList(List<BBSItem> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clean() {
        if (mList.size() != 0) {
            mList.clear();
            notifyDataSetChanged();
        }
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
    public BbsFromMeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.bbs_item, null);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final BBSItem item = mList.get(position);

        viewHolder.tv_title.setText(item.getSummary());
        viewHolder.tv_time.setText(item.getPublishTime());
        viewHolder.tv_count.setText(item.getCommentCount() + "");
        viewHolder.tv_user.setText(item.getUserName());

        Glide.with(mContext)
                .load(item.getImgUrl())
                .centerCrop()
                //.bitmapTransform(new GlideRoundTransform(mContext, roundPx)) //设置图片圆角
                .into(viewHolder.img_head);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BbsWebActivity.class);
                intent.putExtra(BaseActivity.WEBKEY, item.getNoteId());
                mContext.startActivity(intent);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_time;
        TextView tv_user;
        ImageView img_head;
        TextView tv_count;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_user = (TextView) itemView.findViewById(R.id.tv_user);
            img_head = (ImageView) itemView.findViewById(R.id.img_head);
            tv_count = (TextView) itemView.findViewById(R.id.tv_count);

        }

    }

}
