package com.taku.safe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.dialog.ChoiceDialog;
import com.taku.safe.entity.ChoiceDialogItem;

import java.util.List;


public class SelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ChoiceDialog mDialog;
    private List<ChoiceDialogItem> mList;

    private ChoiceDialog.OnItemClickListener mOnClickListener;


    public SelectAdapter(ChoiceDialog dialog, List<ChoiceDialogItem> list) {
        mDialog = dialog;
        mList = list;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setOnItemClickListener(ChoiceDialog.OnItemClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mDialog.getContext());
        View view = inflater.inflate(R.layout.item_select, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onItemClick(v, (int) v.getTag());
                }
                mDialog.dismiss();
            }
        });
        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ChoiceDialogItem item = mList.get(position);

        viewHolder.itemView.setTag(position);

        TextView tv_menu = ((TextViewHolder) viewHolder).tv_menu;
        tv_menu.setText(item.getTitle());

        if (mList.size() > 1) {
            if (position == 0) {
                tv_menu.setBackgroundResource(R.drawable.shape_rectangle_white_corner_up);
            } else if (position == (mList.size() - 1)) {
                tv_menu.setBackgroundResource(R.drawable.shape_rectangle_white_corner_down);
            } else {
                tv_menu.setBackgroundResource(R.drawable.shape_rectangle_white_no_corner);
            }
        } else {
            tv_menu.setBackgroundResource(R.drawable.shape_rectangle_white);
        }

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        TextView tv_menu;

        private TextViewHolder(View itemView) {
            super(itemView);
            tv_menu = (TextView) itemView.findViewById(R.id.tv_menu);
        }

    }


}

