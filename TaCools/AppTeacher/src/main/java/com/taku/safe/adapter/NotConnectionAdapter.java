package com.taku.safe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.taku.safe.R;
import com.taku.safe.protocol.respond.RespNoConnectList;

import java.util.ArrayList;
import java.util.List;


public class NotConnectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespNoConnectList.PageInfoBean.ListBean> mList;

    public NotConnectionAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<RespNoConnectList.PageInfoBean.ListBean> list) {
        if (list != null)
            mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_not_connection, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespNoConnectList.PageInfoBean.ListBean item = mList.get(position);

        RoundedImageView imgHeard = ((TextViewHolder) viewHolder).imgHeard;
        String avatar = item.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(mContext)
                    .load(avatar)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imgHeard);
        } else {
            imgHeard.setImageResource(R.mipmap.ic_male);
        }

        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_phone = ((TextViewHolder) viewHolder).tv_phone;
        TextView tv_class = ((TextViewHolder) viewHolder).tv_class;
        TextView tv_last_time = ((TextViewHolder) viewHolder).tv_last_time;
        TextView tv_last_place = ((TextViewHolder) viewHolder).tv_last_place;

        tv_name.setText(item.getName());
        tv_phone.setText(item.getPhoneNo());
        tv_class.setText(item.getClassName());

        tv_last_time.setText(String.format(mContext.getResources().getString(R.string.last_time),
                item.getLastSignTime()));

        tv_last_place.setText(String.format(mContext.getResources().getString(R.string.last_place),
                item.getLastShowTime(), item.getLastShowAddr()));

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imgHeard;
        TextView tv_name;
        TextView tv_phone;
        TextView tv_class;
        TextView tv_last_time;
        TextView tv_last_place;

        private TextViewHolder(View itemView) {
            super(itemView);
            imgHeard = (RoundedImageView) itemView.findViewById(R.id.cropImageView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_class = (TextView) itemView.findViewById(R.id.tv_class);
            tv_last_time = (TextView) itemView.findViewById(R.id.tv_last_time);
            tv_last_place = (TextView) itemView.findViewById(R.id.tv_last_place);
        }

    }


}

