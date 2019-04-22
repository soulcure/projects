package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.taku.safe.activity.HardwareAnalysisDetailActivity;
import com.taku.safe.protocol.respond.RespUnusualOutList;

import java.util.ArrayList;
import java.util.List;


public class UnusualOutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespUnusualOutList.PageInfoBean.StudentlistBean> mList;

    public UnusualOutAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<RespUnusualOutList.PageInfoBean.StudentlistBean> list) {
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
        View view = inflater.inflate(R.layout.item_hardware_analysis, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespUnusualOutList.PageInfoBean.StudentlistBean item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).view;
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
        TextView tv_info = ((TextViewHolder) viewHolder).tv_info;
        TextView tv_days = ((TextViewHolder) viewHolder).tv_days;

        String format = mContext.getResources().getString(R.string.times);

        tv_name.setText(item.getStudentName());
        tv_phone.setText(item.getPhoneNo());
        tv_class.setText(item.getClassName());
        tv_info.setText(R.string.unusual_out_times);
        tv_days.setText(String.format(format, item.getTimes()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HardwareAnalysisDetailActivity.class);
                intent.putExtra(HardwareAnalysisDetailActivity.STUDENT_ID, item.getStudentId());
                intent.putExtra(HardwareAnalysisDetailActivity.DETAIL_TYPE, HardwareAnalysisDetailActivity.UNUSUAL_DETAIL);
                mContext.startActivity(intent);
            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        View view;
        RoundedImageView imgHeard;
        TextView tv_name;
        TextView tv_phone;
        TextView tv_class;
        TextView tv_info;
        TextView tv_days;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imgHeard = (RoundedImageView) itemView.findViewById(R.id.cropImageView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_class = (TextView) itemView.findViewById(R.id.tv_class);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
            tv_days = (TextView) itemView.findViewById(R.id.tv_days);
        }

    }


}

