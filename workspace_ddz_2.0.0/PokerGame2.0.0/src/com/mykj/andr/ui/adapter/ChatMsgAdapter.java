package com.mykj.andr.ui.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mykj.andr.model.ChatMsgEntity;
import com.mykj.game.ddz.R;

/**
 * *****************************************
 *
 * @文件名称 : ChatMsgAdapter.java
 * @文件描述 : 消息数据填充起
 * *****************************************
 */
public class ChatMsgAdapter extends BaseAdapter {

    private List<ChatMsgEntity> msgList;
    private LayoutInflater mInflater;
    private Context mContext;

    public ChatMsgAdapter(Context context) {
        msgList = new LinkedList<ChatMsgEntity>();
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    /**
     * 获得list列表
     *
     * @return
     */
    public List<ChatMsgEntity> getList() {
        return msgList;
    }

    /**
     * 设置list列表
     *
     * @param list
     */
    public void setList(List<ChatMsgEntity> list) {
        if (list == null) {
            return;
        }
        msgList.clear();
        msgList.addAll(list);
        notifyDataSetChanged();
    }


    public void addItem(ChatMsgEntity item) {
        msgList.add(item);
        notifyDataSetChanged();

    }

    public int getCount() {
        return msgList.size();
    }

    public Object getItem(int position) {
        return msgList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMsgEntity item = msgList.get(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.server_msg_item, null);
            viewHolder = new ViewHolder();

            viewHolder.tvGuid = (TextView) convertView
                    .findViewById(R.id.tvGuid);

            viewHolder.tvReferTime = (TextView) convertView
                    .findViewById(R.id.tvReferTime);

            viewHolder.tvContent = (TextView) convertView
                    .findViewById(R.id.tvContent);

            viewHolder.tvOperateTime = (TextView) convertView
                    .findViewById(R.id.tvOperateTime);

            viewHolder.tvOperator = (TextView) convertView
                    .findViewById(R.id.tvOperator);

            viewHolder.tvReply = (TextView) convertView
                    .findViewById(R.id.tvReply);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.tvGuid.setText(item.getUid());
        viewHolder.tvReferTime.setText(item.getReferTime());
        viewHolder.tvContent.setText(item.getContent());
        viewHolder.tvOperateTime.setText(item.getOperateTime());
        viewHolder.tvOperator.setText(item.getOperator());
        viewHolder.tvReply.setText(item.getReply());

        return convertView;
    }

    private class ViewHolder {
        public TextView tvGuid;
        public TextView tvReferTime;
        public TextView tvContent;
        public TextView tvOperateTime;
        public TextView tvOperator;
        public TextView tvReply;
    }

}
