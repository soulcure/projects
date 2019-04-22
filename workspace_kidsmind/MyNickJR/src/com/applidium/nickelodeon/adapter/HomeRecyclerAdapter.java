package com.applidium.nickelodeon.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.SubSeriesActivity;
import com.applidium.nickelodeon.entity.SerieItemInfo;
import com.applidium.nickelodeon.impl.OnNoDoubleClickListener;
import com.applidium.nickelodeon.player.SmartPlayingActivity;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.GlideRoundTransform;
import com.applidium.nickelodeon.uitls.ImageUtils;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by koen on 2016/1/13.
 */
public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SerieItemInfo> mList;

    public HomeRecyclerAdapter(Context context, List<SerieItemInfo> list) {
        this.context = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (viewtype == 1) {
            // 第一个按钮
            view = LayoutInflater.from(viewGroup.getContext()).inflate
                    (R.layout.metro_horizontal_item, null);
            holder = new FirstViewHolder(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate
                    (R.layout.metro_normal_item, null);
            holder = new MyViewHolder(view);
        }
        return holder;
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (position == 0) {
            FirstViewHolder holder = (FirstViewHolder) viewHolder;
            holder.img_serie.setOnClickListener(new OnNoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    MediaPlayerService.playSound(context, MediaPlayerService.ONCLICK);
                    Intent intent = new Intent(context, SmartPlayingActivity.class);
                    context.startActivity(intent);
                }
            });
        } else {
            final SerieItemInfo item = mList.get(position - 1);
            MyViewHolder holder = (MyViewHolder) viewHolder;
            String url = item.getSubImg();
            if (StringUtils.isEmpty(url)) {
                holder.imgPlayer.setVisibility(View.GONE);
                holder.tvPlayer.setVisibility(View.GONE);
            } else {
                holder.imgPlayer.setVisibility(View.VISIBLE);
                holder.tvPlayer.setVisibility(View.VISIBLE);

                int roundPx = (int) ScreenUtils.dpToPx(context, 15);
                Glide.with(context)
                        .load(item.getSubImg())
                        .centerCrop()
                        .bitmapTransform(new GlideRoundTransform(context, roundPx)) //设置图片圆角
                        .placeholder(R.drawable.cartoon_icon_default)  //占位图片
                        .error(R.drawable.cartoon_icon_default)        //下载失败
                        .into(holder.imgPlayer);

                holder.tvPlayer.setText(item.getSubName() + "");
                holder.imgPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MediaPlayerService.playSound(context, MediaPlayerService.ONCLICK);
                        Intent intent = new Intent(context, SubSeriesActivity.class);
                        intent.putExtra("subId", item.getSubId());
                        intent.putExtra("SubName", item.getSubName());
                        intent.putExtra("subDescription", item.getSubDescription());
                        intent.putExtra("subImg", item.getSubImg());

                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relContainer;
        ImageView imgPlayer;
        TextView tvPlayer;
        TextView tvInfo;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imgPlayer = (ImageView) itemView.findViewById(R.id.img_serie);
            this.tvPlayer = (TextView) itemView.findViewById(R.id.tv_serie);
        }
    }

    public class FirstViewHolder extends RecyclerView.ViewHolder {
        ImageView img_serie;
        TextView tv_serie;

        public FirstViewHolder(View itemView) {
            super(itemView);
            this.img_serie = (ImageView) itemView.findViewById(R.id.img_serie);
            this.tv_serie = (TextView) itemView.findViewById(R.id.tv_serie);
            int roundPx = context.getResources().getDimensionPixelSize(
                    R.dimen.image_round_size);
            Bitmap bitmap = ImageUtils.ResourceToBitmap(context, R.drawable.icon_h_default);
            img_serie.setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, roundPx));
            tv_serie.setText(R.string.channel);
        }
    }
}
