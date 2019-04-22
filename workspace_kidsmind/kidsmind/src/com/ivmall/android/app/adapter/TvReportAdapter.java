package com.ivmall.android.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.TvReportItem;
import com.ivmall.android.app.uitls.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class TvReportAdapter extends RecyclerView.Adapter<TvReportAdapter.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;


    private Context mContext;
    private List<TvReportItem> mLists = new ArrayList<TvReportItem>();
    private View mHeaderView;

    public TvReportAdapter(Context context) {
        mContext = context;
    }


    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }


    public void setList(List<TvReportItem> lists) {
        mLists.addAll(lists);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public TvReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new ViewHolder(mHeaderView);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.tv_report_item, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;


        final int pos = getRealPosition(viewHolder);
        TvReportItem item = mLists.get(pos);

        if (viewHolder instanceof ViewHolder) {

            viewHolder.tv_time.setText(item.getTime());

            String operate = item.getOperation() + mContext.getResources().getString(R.string.tv_format);
            viewHolder.tv_operate.setText(operate);

            viewHolder.tv_episode.setText(item.getEpisodeName());

            String duration = item.getWatchDuration();
            if (!StringUtils.isEmpty(duration)) {
                int buttom = mContext.getResources().getDimensionPixelOffset(R.dimen.tv_report_item_padding_buttom);
                viewHolder.tv_duration.setPadding(0, 0, 0, buttom);
            } else {
                viewHolder.tv_duration.setPadding(0, 0, 0, 0);
            }
            viewHolder.tv_duration.setText(duration);

        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return mHeaderView == null ? mLists.size() : mLists.size() + 1;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_time;
        TextView tv_operate;
        TextView tv_episode;
        TextView tv_duration;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_operate = (TextView) itemView.findViewById(R.id.tv_operate);
            tv_episode = (TextView) itemView.findViewById(R.id.tv_episode);
            tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);
        }


    }

}
