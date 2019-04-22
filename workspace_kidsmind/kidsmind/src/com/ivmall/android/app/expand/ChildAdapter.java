package com.ivmall.android.app.expand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.SelectItem;
import com.ivmall.android.app.entity.SerieItem;
import com.ivmall.android.app.views.SmoothCheckBox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by koen on 2016/4/8.
 */
public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    private List<SerieItem> childItem;
    private Context mContext;
    private ArrayList<Bean> mList;
    private int serieId;
    private List<SelectItem> selectedList;

    public void setChildItem(List<SerieItem> itemList, int id, List<SelectItem> selected) {
        childItem = itemList;
        serieId = id;
        selectedList = selected;
        notifyDataSetChanged();
        if (itemList != null) {
            if (mList.isEmpty() && itemList.size() != 0) {
                for (int i = 0; i < itemList.size(); i++) {
                    mList.add(new Bean());
                }
            } else if (!mList.isEmpty() && mList.size() < itemList.size()) {
                mList.clear();
                for (int i = 0; i < itemList.size(); i++) {
                    mList.add(new Bean());
                }
            } else if (itemList.size() == 0) {
                mList.clear();
            }
        }
    }

    public void setSelectedList(List<SelectItem> selected) {
        selectedList = selected;
        notifyDataSetChanged();
    }

    public ChildAdapter(Context context) {
        childItem = new ArrayList<>();
        mList = new ArrayList<>();
        mContext = context;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChildViewHolder holder = new ChildViewHolder(LayoutInflater
                .from(mContext).inflate(R.layout.child_item_layout, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ChildViewHolder holder, int position) {
        final SerieItem currentItem = childItem.get(position);
        if (currentItem != null) {
            String title = currentItem.getTitle();
            holder.textView.setText(title);
            if (selectedList != null && contain(currentItem)) {
                mList.get(position).isChecked = true;
            } else {
                mList.get(position).isChecked = false;
            }
            holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox smoothCheckBox, boolean checked) {
                    int index = holder.getAdapterPosition();
                    mOnItemCheckChangedLitener.onCheckedChanged(smoothCheckBox, checked, currentItem, serieId,
                            mList.get(index).isChecked);
                    mList.get(holder.getAdapterPosition()).isChecked = checked;
                }
            });
            holder.checkBox.setChecked(mList.get(position).isChecked, false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = holder.getAdapterPosition();
                    holder.checkBox.setChecked(!mList.get(index).isChecked, true);
                }
            });
        }
    }

    private boolean contain(SerieItem item) {
        for (SelectItem i : selectedList) {
            if (item.getEpisodeId() == i.getEpisodeId())
                return true;
        }
        return false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (childItem != null) {
            return childItem.size();
        } else {
            return 0;
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public SmoothCheckBox checkBox;

        public ChildViewHolder(View itemView) {
            super(itemView);
            checkBox = (SmoothCheckBox) itemView.findViewById(R.id.child_item_checkbtn);
            textView = (TextView) itemView.findViewById(R.id.child_item_title);
        }
    }

    private class Bean implements Serializable {
        boolean isChecked;
    }

    public interface OnItemCheckChangedLitener {

        void onCheckedChanged(SmoothCheckBox smoothCheckBox,
                              boolean checked, SerieItem item, int SerieId, boolean isSelected);
    }

    private OnItemCheckChangedLitener mOnItemCheckChangedLitener;

    public void setOnItemCheckChangedLitener(OnItemCheckChangedLitener litener) {
        mOnItemCheckChangedLitener = litener;
    }
}


