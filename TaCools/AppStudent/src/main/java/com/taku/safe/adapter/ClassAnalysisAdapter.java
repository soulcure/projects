package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.activity.SignAnalyzeInClassActivity;
import com.taku.safe.activity.SignInfoOnCalendarActivity;
import com.taku.safe.activity.UnusualAnalysisActivity;
import com.taku.safe.protocol.respond.RespStudentList;
import com.taku.safe.utils.ListUtils;
import com.taku.safe.utils.TimeUtils;

import java.text.ParseException;
import java.util.List;


public class ClassAnalysisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespStudentList.StudentListBean> mList;

    public ClassAnalysisAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RespStudentList.StudentListBean> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_class, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespStudentList.StudentListBean item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).view;
        TextView tv_rank = ((TextViewHolder) viewHolder).tv_rank;
        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_info = ((TextViewHolder) viewHolder).tv_info;
        TextView tv_number = ((TextViewHolder) viewHolder).tv_number;


        tv_rank.setText(String.valueOf(position + 1));
        tv_name.setText(item.getStudentName());

        String format = mContext.getString(R.string.sum_times);
        tv_number.setText(String.format(format, item.getUnusualNum()));

        StringBuilder sb = new StringBuilder();
        List<String> days = item.getUnusualDays();
        if (!ListUtils.isEmpty(days)) {
            try {
                int index = days.size() > 3 ? 3 : days.size();
                for (int i = 0; i < index; i++) {
                    if (i == index - 1) {
                        sb.append(TimeUtils.parseDate(days.get(i)));
                    } else {
                        sb.append(TimeUtils.parseDate(days.get(i))).append("、");
                    }
                }

                if (days.size() > 3) {
                    sb.append("...");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mContext instanceof SignAnalyzeInClassActivity) {
                SignAnalyzeInClassActivity act = (SignAnalyzeInClassActivity) mContext;
                if (act.getDataType() == 0) {
                    sb.append("异常");
                } else {
                    sb.append("未签到");
                }
            }

        }
        tv_info.setText(sb.toString());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SignInfoOnCalendarActivity.class);

                intent.putExtra(SignInfoOnCalendarActivity.STUDENT_ID, item.getStudentId());

                List<String> list = item.getUnusualDays();
                String[] array = list.toArray(new String[list.size()]);
                intent.putExtra(SignInfoOnCalendarActivity.UNUSUAL_DAYS, array);

                if (mContext instanceof SignAnalyzeInClassActivity) {
                    SignAnalyzeInClassActivity act = (SignAnalyzeInClassActivity) mContext;

                    String title;
                    if (act.getDataType() == UnusualAnalysisActivity.DATA_UNUSUAL) {
                        title = item.getStudentName() + mContext.getString(R.string.unusual_title);
                    } else {
                        title = item.getStudentName() + mContext.getString(R.string.no_sign_title);
                    }

                    intent.putExtra("name", title);
                    intent.putExtra(UnusualAnalysisActivity.DATA_TYPE, act.getDataType());
                    intent.putExtra(UnusualAnalysisActivity.SIGN_TYPE, act.getSignType());

                    intent.putExtra(UnusualAnalysisActivity.CUR_DATE, act.getCurDate());
                    intent.putExtra(UnusualAnalysisActivity.TITLE_DATE, act.getTitleDate());
                    intent.putExtra(UnusualAnalysisActivity.TIME_RANGE, act.getTimeRange());

                    intent.putExtra(UnusualAnalysisActivity.START_DAY, act.getStartDay());
                    intent.putExtra(UnusualAnalysisActivity.END_DAY, act.getEndDay());
                }

                mContext.startActivity(intent);

            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_rank;
        TextView tv_name;
        TextView tv_info;
        TextView tv_number;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_rank = (TextView) itemView.findViewById(R.id.tv_rank);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
        }

    }


}

