package com.taku.safe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.entity.VoiceItem;
import com.taku.safe.utils.MediaPlayManager;

import java.util.ArrayList;
import java.util.List;


public class VoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<VoiceItem> mList = new ArrayList<>();

    public VoiceAdapter(Context context) {
        mContext = context;
    }

    public void setItem(VoiceItem item) {
        boolean isAdd = true;
        for (VoiceItem voiceItem : mList) {
            if (voiceItem.getUrl().equals(item.getUrl())) {
                isAdd = false;
                break;
            }
        }

        if (isAdd) {
            mList.add(item);
            notifyDataSetChanged();
        }

    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_voice, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final VoiceItem voiceItem = mList.get(position);

        TextView tv_length = ((TextViewHolder) viewHolder).tv_length;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        final ImageView img_voice = ((TextViewHolder) viewHolder).img_voice;

        boolean isAutoPlay = voiceItem.isAutoPlay();

        int duration;
        if (voiceItem.getDuration() > 0) {
            duration = voiceItem.getDuration();
        } else {
            duration = MediaPlayManager.instance().voiceTime(voiceItem.getUrl(), isAutoPlay,
                    img_voice);//获取音频的时间
            duration = duration / 1000;//转化为秒
        }

        Log.d("voice", "duration: " + duration);

        if (duration >= 59) {//设置声音条不同的长度
            tv_length.setEms(6);
        } else {
            tv_length.setEms(3);
        }
        String format = mContext.getString(R.string.sos_voice_time);
        String time = String.format(format, duration);
        tv_time.setText(time);

        LinearLayout linear_voice = ((TextViewHolder) viewHolder).linear_voice;
        linear_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayManager.instance().playSound(voiceItem.getUrl(), img_voice);
            }
        });
    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linear_voice;
        ImageView img_voice;
        TextView tv_length;
        TextView tv_time;

        private TextViewHolder(View itemView) {
            super(itemView);
            linear_voice = (LinearLayout) itemView.findViewById(R.id.linear_voice);
            img_voice = (ImageView) itemView.findViewById(R.id.img_voice);
            tv_length = (TextView) itemView.findViewById(R.id.tv_length);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }

    }


}

