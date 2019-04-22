package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.activity.SignAnalyzeInCollegeActivity;
import com.taku.safe.activity.UnusualAnalysisActivity;
import com.taku.safe.protocol.respond.RespCollegeAnalysis;

import java.util.List;


public class SchoolAnalysisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespCollegeAnalysis.CollegeListBean> mList;

    public SchoolAnalysisAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RespCollegeAnalysis.CollegeListBean> list) {
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
        View view = inflater.inflate(R.layout.item_nosign, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespCollegeAnalysis.CollegeListBean item = mList.get(position);

        TextView tv_rank = ((TextViewHolder) viewHolder).tv_rank;
        TextView tv_college = ((TextViewHolder) viewHolder).tv_college;
        TextView tv_number = ((TextViewHolder) viewHolder).tv_number;


        tv_rank.setText(String.valueOf(position + 1));
        tv_college.setText(item.getCollegeName());
        tv_number.setText(String.valueOf(item.getUnusualNum()));

        View view = ((TextViewHolder) viewHolder).view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SignAnalyzeInCollegeActivity.class);

                intent.putExtra(SignAnalyzeInCollegeActivity.COLLEGE_ID, item.getCollegeId());

                if (mContext instanceof UnusualAnalysisActivity) {
                    UnusualAnalysisActivity act = (UnusualAnalysisActivity) mContext;

                    String title;
                    if (act.getDataType() == UnusualAnalysisActivity.DATA_UNUSUAL) {
                        title = item.getCollegeName() + mContext.getString(R.string.unusual_title);
                    } else {
                        title = item.getCollegeName() + mContext.getString(R.string.no_sign_title);
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
        TextView tv_college;
        TextView tv_number;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_rank = (TextView) itemView.findViewById(R.id.tv_rank);
            tv_college = (TextView) itemView.findViewById(R.id.tv_college);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
        }

    }


}

