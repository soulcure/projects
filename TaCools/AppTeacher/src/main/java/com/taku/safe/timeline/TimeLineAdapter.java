package com.taku.safe.timeline;

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

import com.github.vipulasri.timelineview.TimelineView;
import com.taku.safe.R;
import com.taku.safe.activity.SignInfoOnMapActivity;
import com.taku.safe.protocol.respond.RespSignList;
import com.taku.safe.utils.TimeUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by colin on 2017/5/10.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public enum SignStatus {
        NO_SIGN,
        SIGNED_NORMAL,
        SIGNED_UNUSUAL
    }


    private List<RespSignList.SignlistBean> mFeedList;
    private Context mContext;


    public TimeLineAdapter(Context context) {
        mContext = context;
    }


    public void setList(List<RespSignList.SignlistBean> list) {
        mFeedList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_timeline, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final RespSignList.SignlistBean timeLineModel = mFeedList.get(position);

        if (holder instanceof TimeLineViewHolder) {

            //0 异常签到 1 有效签到 -1未签到
            if (timeLineModel.getSignValid() == -1) {
                ((TimeLineViewHolder) holder).mTimelineView.setMarker(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_marker),
                        ContextCompat.getColor(mContext, R.color.color_red));

                ((TimeLineViewHolder) holder).tv_status.setBackgroundResource(
                        R.drawable.shape_rectangle_red);
                ((TimeLineViewHolder) holder).tv_status.setText(R.string.no_sign);

                ((TimeLineViewHolder) holder).img_next.setVisibility(View.INVISIBLE);

            } else if (timeLineModel.getSignValid() == 1) {
                ((TimeLineViewHolder) holder).mTimelineView.setMarker(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_marker),
                        ContextCompat.getColor(mContext, R.color.color_gray));

                ((TimeLineViewHolder) holder).tv_status.setBackgroundResource(
                        R.drawable.shape_rectangle_blue);
                ((TimeLineViewHolder) holder).tv_status.setText(R.string.sign_normal);

                ((TimeLineViewHolder) holder).img_next.setVisibility(View.VISIBLE);
            } else {
                ((TimeLineViewHolder) holder).mTimelineView.setMarker(VectorDrawableUtils.
                        getDrawable(mContext, R.drawable.ic_marker_active, R.color.color_gray));

                ((TimeLineViewHolder) holder).tv_status.setBackgroundResource(
                        R.drawable.shape_rectangle_orange);

                ((TimeLineViewHolder) holder).tv_status.setText(R.string.sign_unusual);

                ((TimeLineViewHolder) holder).img_next.setVisibility(View.VISIBLE);
            }


            String strDate = timeLineModel.getSignDate();
            try {
                Calendar calendar = TimeUtils.parseDate(strDate, TimeUtils.DEFAULT_DATE_FORMAT);
                Date date = calendar.getTime();

                String uiDate = TimeUtils.DATE_FORMAT_MONTH_TEXT.format(date);
                ((TimeLineViewHolder) holder).tv_time.setText(uiDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView tv_approve = ((TimeLineViewHolder) holder).tv_approve;

            if (timeLineModel.isChangeStatus()) {
                tv_approve.setVisibility(View.VISIBLE);
            } else {
                tv_approve.setVisibility(View.INVISIBLE);
            }

            String address = timeLineModel.getLocation();
            if (!TextUtils.isEmpty(address)) {
                ((TimeLineViewHolder) holder).tv_address.setVisibility(View.VISIBLE);
                ((TimeLineViewHolder) holder).tv_address.setText(address);
            } else {
                ((TimeLineViewHolder) holder).tv_address.setVisibility(View.GONE);
            }


            ((TimeLineViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timeLineModel.getSignValid() != -1) {  //未签到,不能查看详情
                        Intent intent = new Intent(mContext, SignInfoOnMapActivity.class);
                        intent.putExtra(SignInfoOnMapActivity.SIGN_TYPE, 1);
                        intent.putExtra(SignInfoOnMapActivity.SIGN_ID, timeLineModel.getSignId());
                        mContext.startActivity(intent);
                    }
                }
            });

        }


    }


    class TimeLineViewHolder extends RecyclerView.ViewHolder {
        View view;
        TimelineView mTimelineView;
        TextView tv_time;
        TextView tv_address;
        TextView tv_status;
        TextView tv_approve;
        ImageView img_next;

        TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            view = itemView;
            mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            tv_approve = (TextView) itemView.findViewById(R.id.tv_approve);
            img_next = (ImageView) itemView.findViewById(R.id.img_next);

            mTimelineView.initLine(viewType);
        }
    }
}
