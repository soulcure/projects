package com.mykj.andr.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mykj.game.ddz.R;

import java.util.List;


public class RankAwardAdapter extends BaseAdapter {

    private Activity mAct;
    private List<String> mLists;

    public RankAwardAdapter(Activity act, List<String> list) {
        mAct = act;
        mLists = list;
    }


    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        final String info = (String) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = mAct.getLayoutInflater();
            convertView = inflater.inflate(R.layout.rank_award_item, null);
            holder = new ViewHolder();

            holder.imgRank = (ImageView) convertView.findViewById(R.id.imgRank);
            holder.tvRankAward = (TextView) convertView.findViewById(R.id.tvRankAward);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvRankAward.setText(info);
        switch (position) {
            case 0:
                holder.imgRank.setVisibility(View.VISIBLE);
                holder.imgRank.setImageResource(R.drawable.img_rank1);
                break;
            case 1:
                holder.imgRank.setVisibility(View.VISIBLE);
                holder.imgRank.setImageResource(R.drawable.img_rank2);
                break;
            case 2:
                holder.imgRank.setVisibility(View.VISIBLE);
                holder.imgRank.setImageResource(R.drawable.img_rank3);
                break;
            default:
                holder.imgRank.setVisibility(View.INVISIBLE);
                break;
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView imgRank;
        TextView tvRankAward;
    }

}