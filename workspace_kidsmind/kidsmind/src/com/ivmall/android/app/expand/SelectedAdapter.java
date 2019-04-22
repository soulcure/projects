package com.ivmall.android.app.expand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.SelectItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 2016/4/13.
 */
public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.SelectedViewHolder>{

    private List<SelectItem> selectItem;
    private Context mContext;

    public void setSelectItem(List<SelectItem> list) {
        selectItem = list;
        notifyDataSetChanged();
    }

    public void removeSelectItem(SelectItem item) {
        if (!selectItem.isEmpty()) {
            int rm = selectItem.indexOf(item);
            selectItem.remove(item);
            notifyItemRemoved(rm);
        }
    }

    public SelectedAdapter(Context context) {
        selectItem = new ArrayList<>();
        mContext = context;
    }

    @Override
    public SelectedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SelectedViewHolder holder = new SelectedViewHolder(LayoutInflater.from(mContext)
        .inflate(R.layout.selected_item_layout, parent, false));
        return  holder;
    }

    @Override
    public void onBindViewHolder(SelectedViewHolder holder, int position) {
        final SelectItem currentItem = selectItem.get(position);
        if (currentItem != null) {
            String title = currentItem.getEpisodeName();
            holder.textView.setText(title);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeSelectItem(currentItem);
                    mOnItemClickLitener.onItemClick(currentItem);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (selectItem != null) {
            return selectItem.size();
        } else {
            return 0;
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(SelectItem item);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener litener) {
        mOnItemClickLitener = litener;
    }

    public class SelectedViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public SelectedViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.selected_name);
        }
    }
}
