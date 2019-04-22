package com.taku.safe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.taku.safe.R;

import java.util.List;


public class ChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    enum UI_Type {
        LIST_HEARD, LIST_NORMAL
    }

    private Context mContext;
    private List<String> mList;

    private int mPosition = 1;  //保存上一次处于checked状态的控件位置

    private CallBack mCallBack;
    private CompoundButton checkedBtn;


    public interface CallBack {
        void selectItem(int postion, int index, String name, boolean isNext);
    }


    public ChoiceAdapter(Context context, CallBack callBack) {
        mContext = context;
        mCallBack = callBack;
    }

    public void setList(List<String> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {  //给Item划分类别
        int type;
        if (position == 0) {
            type = UI_Type.LIST_HEARD.ordinal();
        } else {
            type = UI_Type.LIST_NORMAL.ordinal();
        }
        return type;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder = null;

        switch (UI_Type.values()[viewType]) {   //根据Item类别不同，选择不同的Item布局
            case LIST_HEARD: {
                view = inflater.inflate(R.layout.item_choice_head, parent, false);
                holder = new TextViewHolder(view);
            }
            break;
            case LIST_NORMAL: {
                view = inflater.inflate(R.layout.item_choice, parent, false);
                holder = new TextViewHolder(view);
            }
            break;
        }
        return holder;
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final String item = mList.get(position);

        TextView tv_content = ((TextViewHolder) viewHolder).tv_content;
        tv_content.setText(item);


        CheckBox checkbox = ((TextViewHolder) viewHolder).checkbox;
        checkbox.setTag(position);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (checkedBtn != null) {
                        checkedBtn.setChecked(false);
                    }
                    checkedBtn = buttonView;
                    int index = (int) buttonView.getTag();
                    if (mCallBack != null) {
                        mCallBack.selectItem(mPosition, index, item, false);
                    }
                }
            }
        });
        if (position == 0) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }


        View view = ((TextViewHolder) viewHolder).view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition == 3) {
                    CheckBox checkbox = (CheckBox) v.findViewById(R.id.checkbox);
                    checkbox.setChecked(true);
                } else {
                    if (mCallBack != null) {
                        mCallBack.selectItem(mPosition, position, item, true);
                    }
                }
            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        View view;
        CheckBox checkbox;
        TextView tv_content;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);

        }

    }


}

