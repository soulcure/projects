package com.taku.safe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.activity.SignAnalyzeInClassActivity;
import com.taku.safe.activity.SignAnalyzeInCollegeActivity;
import com.taku.safe.activity.UnusualAnalysisActivity;
import com.taku.safe.protocol.respond.RespClassList;

import java.util.List;


public class CollegeAnalysisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespClassList.ClassListBean> mList;

    public CollegeAnalysisAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RespClassList.ClassListBean> list) {
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
        View view = inflater.inflate(R.layout.item_college, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespClassList.ClassListBean item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).view;
        TextView tv_rank = ((TextViewHolder) viewHolder).tv_rank;
        TextView tv_class = ((TextViewHolder) viewHolder).tv_class;
        TextView tv_info = ((TextViewHolder) viewHolder).tv_info;
        TextView tv_number = ((TextViewHolder) viewHolder).tv_number;


        tv_rank.setText(String.valueOf(position + 1));
        tv_class.setText(item.getClassName());

        String format = mContext.getResources().getString(R.string.person);
        String persons = String.format(format, item.getUnusualNum());
        tv_number.setText(persons);

        String students = "";
        int index = item.getStudentList().size() > 3 ? 3 : item.getStudentList().size();
        for (int i = 0; i < index; i++) {
            if (i == index - 1) {
                students += item.getStudentList().get(i).getStudentName();
            } else {
                students += (item.getStudentList().get(i).getStudentName() + "、");
            }
        }

        if (item.getStudentList().size() > 3) {
            students += "等人";
        }

        tv_info.setText(students);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SignAnalyzeInClassActivity.class);

                intent.putExtra(SignAnalyzeInClassActivity.CLASS_ID, item.getClassId());

                if (mContext instanceof SignAnalyzeInCollegeActivity) {
                    SignAnalyzeInCollegeActivity act = (SignAnalyzeInCollegeActivity) mContext;

                    String title;
                    if (act.getDataType() == UnusualAnalysisActivity.DATA_UNUSUAL) {
                        title = item.getClassName() + mContext.getString(R.string.unusual_title);
                    } else {
                        title = item.getClassName() + mContext.getString(R.string.no_sign_title);
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
        TextView tv_class;
        TextView tv_info;
        TextView tv_number;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_rank = (TextView) itemView.findViewById(R.id.tv_rank);
            tv_class = (TextView) itemView.findViewById(R.id.tv_class);
            tv_info = (TextView) itemView.findViewById(R.id.tv_info);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
        }

    }


}

