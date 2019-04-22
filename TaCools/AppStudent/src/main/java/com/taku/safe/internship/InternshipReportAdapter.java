package com.taku.safe.internship;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taku.safe.R;
import com.taku.safe.activity.SignInfoOnMapActivity;
import com.taku.safe.protocol.respond.RespSignList;
import com.taku.safe.utils.ListUtils;
import com.taku.safe.utils.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class InternshipReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespSignList.SignlistBean> mList;

    public InternshipReportAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<RespSignList.SignlistBean> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        int res = 0;
        if (!ListUtils.isEmpty(mList)) {
            res = mList.size();
        }

        return res;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_new_report, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespSignList.SignlistBean item = mList.get(position);

        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        TextView tv_status = ((TextViewHolder) viewHolder).tv_status;
        TextView tv_address = ((TextViewHolder) viewHolder).tv_address;
        TextView tv_approve = ((TextViewHolder) viewHolder).tv_approve;
        ImageView img_next = ((TextViewHolder) viewHolder).img_next;

        String strDate = item.getSignDate();
        try {
            Calendar calendar = TimeUtils.parseDate(strDate, TimeUtils.DEFAULT_DATE_FORMAT);
            Date date = calendar.getTime();

            String uiDate = TimeUtils.DATE_FORMAT_MONTH_TEXT.format(date);
            tv_date.setText(uiDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int signValid = item.getSignValid();
        if (signValid == 1) {
            tv_status.setText(R.string.sign_normal);
            tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray_87));
            img_next.setVisibility(View.VISIBLE);
        } else if (signValid == -1) {
            tv_status.setText(R.string.no_sign);
            tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_red));
            img_next.setVisibility(View.INVISIBLE);
        } else {
            tv_status.setText(R.string.sign_unusual);
            tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_orange));
            img_next.setVisibility(View.VISIBLE);
        }

        if (item.isChangeStatus()) {
            tv_approve.setVisibility(View.VISIBLE);
        } else {
            tv_approve.setVisibility(View.INVISIBLE);
        }

        String address = item.getLocation();
        if (!TextUtils.isEmpty(address)) {
            tv_address.setText(address);
            tv_address.setVisibility(View.VISIBLE);
        } else {
            tv_address.setVisibility(View.GONE);
        }


        ((TextViewHolder) viewHolder).view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int signValid = item.getSignValid();
                if (signValid == -1) {
                    Toast.makeText(mContext, "这天没有签到信息哦", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(mContext, SignInfoOnMapActivity.class);
                intent.putExtra(SignInfoOnMapActivity.SIGN_TYPE, 0);
                intent.putExtra(SignInfoOnMapActivity.SIGN_ID, item.getSignId());
                intent.putExtra(SignInfoOnMapActivity.CHANGE_TIME, item.getChangeTime());
                intent.putExtra(SignInfoOnMapActivity.IS_CHANGE, item.isChangeStatus());
                mContext.startActivity(intent);
            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_date;
        TextView tv_status;
        TextView tv_address;
        TextView tv_approve;
        ImageView img_next;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_approve = (TextView) itemView.findViewById(R.id.tv_approve);
            img_next = (ImageView) itemView.findViewById(R.id.img_next);
        }

    }


}

