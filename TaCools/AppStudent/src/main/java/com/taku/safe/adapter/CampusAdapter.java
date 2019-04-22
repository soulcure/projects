package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.taku.safe.R;
import com.taku.safe.activity.PictureIndicatorActivity;
import com.taku.safe.protocol.respond.RespReportList;
import com.taku.safe.utils.ListUtils;
import com.taku.safe.utils.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CampusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespReportList.PageInfoBean.ListBean> mList;

    public CampusAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<RespReportList.PageInfoBean.ListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }



    /*public void setList(List<RespReportList.ReportListBean> list) {
        mList = list;
        notifyDataSetChanged();
    }*/

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_campus, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespReportList.PageInfoBean.ListBean item = mList.get(position);

        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        tv_title.setText(item.getTitle());

        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        String strDate = item.getReportDate();
        try {
            Calendar calendar = TimeUtils.parseDate(strDate, TimeUtils.DEFAULT_DATE_FORMAT);
            Date date = calendar.getTime();

            String uiDate = TimeUtils.DATE_FORMAT_DATE.format(date);
            tv_date.setText(uiDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView tv_feedback = ((TextViewHolder) viewHolder).tv_feedback;
        tv_feedback.setText(item.getContent());

        TextView tv_type = ((TextViewHolder) viewHolder).tv_type;
        String[] types = mContext.getResources().getStringArray(R.array.response_type_str);
        int type = item.getIssueType();
        if (type <= types.length) {
            tv_type.setText(types[type]);
        } else {
            tv_type.setText(types[4]);
        }

        TextView tv_response_content = ((TextViewHolder) viewHolder).tv_response_content;
        String respContent = item.getResponseContent();
        if (!TextUtils.isEmpty(respContent)) {
            tv_response_content.setText(respContent);
        } else {
            LinearLayout linear_response_content = ((TextViewHolder) viewHolder).linear_response_content;
            linear_response_content.setVisibility(View.GONE);
        }


        HorizontalScrollView h_scrollview = ((TextViewHolder) viewHolder).h_scrollview;  //load picture
        LinearLayout h_linear = ((TextViewHolder) viewHolder).h_linear;  //load picture
        h_linear.removeAllViews();

        List<String> list = item.getImageList();
        if (ListUtils.isEmpty(list)) {
            h_scrollview.setVisibility(View.GONE);
        } else {
            final String[] array = list.toArray(new String[list.size()]);

            for (int i = 0; i < list.size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                final View view = inflater.inflate(R.layout.item_show_pic, null);

                ImageView imgContent = (ImageView) view.findViewById(R.id.img_content);
                Glide.with(mContext)
                        .load(list.get(i))
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imgContent);
                h_linear.addView(view);

                final int index = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PictureIndicatorActivity.class);
                        intent.putExtra("image", array);
                        intent.putExtra("index", index);
                        mContext.startActivity(intent);
                    }
                });
            }
        }

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_date;
        TextView tv_feedback;
        TextView tv_type;
        HorizontalScrollView h_scrollview;
        LinearLayout h_linear;
        LinearLayout linear_response_content;
        TextView tv_response_content;

        private TextViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_feedback = (TextView) itemView.findViewById(R.id.tv_feedback);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            h_scrollview = (HorizontalScrollView) itemView.findViewById(R.id.h_scrollview);
            h_linear = (LinearLayout) itemView.findViewById(R.id.h_linear);
            linear_response_content = (LinearLayout) itemView.findViewById(R.id.linear_response_content);
            tv_response_content = (TextView) itemView.findViewById(R.id.tv_response_content);
        }

    }


}

