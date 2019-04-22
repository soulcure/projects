package com.taku.safe.campus;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.taku.safe.R;
import com.taku.safe.protocol.respond.RespReportList;
import com.taku.safe.utils.SpacesGridItemDecoration;
import com.taku.safe.utils.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TucaoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespReportList.PageInfoBean.ListBean> mList;

    public TucaoAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<RespReportList.PageInfoBean.ListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }



    /*public void setList(List<RespReportList.ReportListBean> list) {
        mList = list;
        notifyDataSetChanged();
    }*/

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_tucao, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespReportList.PageInfoBean.ListBean item = mList.get(position);

        RoundedImageView cropImageView = ((TextViewHolder) viewHolder).cropImageView;
        TextView tv_nick = ((TextViewHolder) viewHolder).tv_nick;

        LinearLayout linear_response1 = ((TextViewHolder) viewHolder).linear_response1;
        TextView tv_name1 = ((TextViewHolder) viewHolder).tv_name1;
        TextView tv_response1 = ((TextViewHolder) viewHolder).tv_response1;
        LinearLayout linear_response2 = ((TextViewHolder) viewHolder).linear_response2;
        TextView tv_name2 = ((TextViewHolder) viewHolder).tv_name2;
        TextView tv_response2 = ((TextViewHolder) viewHolder).tv_response2;

        LikeButton star_button = ((TextViewHolder) viewHolder).star_button;

        star_button.setLiked(true);

        star_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(mContext, "点赞了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });


        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        String strDate = item.getReportDate();
        try {
            Calendar calendar = TimeUtils.parseDate(strDate, TimeUtils.DEFAULT_DATE_FORMAT);
            Date date = calendar.getTime();

            String uiDate = TimeUtils.DATE_FORMAT_DATE.format(date);
            tv_date.setText(uiDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView tv_feedback = ((TextViewHolder) viewHolder).tv_feedback;
        tv_feedback.setText(item.getContent());

        TextView tv_type = ((TextViewHolder) viewHolder).tv_type;
        String[] types = mContext.getResources().getStringArray(R.array.response_type_str);
        int type = item.getIssueType();
        if (type <= types.length) {
            tv_type.setText(types[type]);
        } else {
            tv_type.setText(types[4]);
        }

        List<String> url = new ArrayList<>();
        RecyclerView recyclerView = ((TextViewHolder) viewHolder).mRecyclerView;  //load picture
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        ImageAdapter adapter = new ImageAdapter(mContext, url);

        recyclerView.addItemDecoration(new SpacesGridItemDecoration(5));
        recyclerView.setAdapter(adapter);


    }


    class TextViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView cropImageView;
        TextView tv_nick;
        TextView tv_date;
        TextView tv_feedback;
        RecyclerView mRecyclerView;

        TextView tv_type;
        LinearLayout linear_response1;
        TextView tv_name1;
        TextView tv_response1;
        LinearLayout linear_response2;
        TextView tv_name2;
        TextView tv_response2;

        LikeButton star_button;


        TextViewHolder(View itemView) {
            super(itemView);
            cropImageView = itemView.findViewById(R.id.cropImageView);
            tv_nick = itemView.findViewById(R.id.tv_nick);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_feedback = itemView.findViewById(R.id.tv_feedback);
            tv_type = itemView.findViewById(R.id.tv_type);

            mRecyclerView = itemView.findViewById(R.id.recycler_view);


            linear_response1 = itemView.findViewById(R.id.linear_response1);
            tv_name1 = itemView.findViewById(R.id.tv_name1);
            tv_response1 = itemView.findViewById(R.id.tv_response1);

            linear_response2 = itemView.findViewById(R.id.linear_response2);
            tv_name2 = itemView.findViewById(R.id.tv_name2);
            tv_response2 = itemView.findViewById(R.id.tv_response2);

            star_button = itemView.findViewById(R.id.star_button);
        }


    }


    class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private Context mContext;
        private List<String> urls;


        ImageAdapter(Context context, List<String> urls) {
            mContext = context;
            this.urls = urls;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View view = new ImageView(parent.getContext());
            view.setTag("pic");
            return new ImageAdapter.ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder viewHolder, final int position) {
            final String url = urls.get(position);
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(viewHolder.imageView);

        }

        @Override
        public int getItemCount() {
            return urls.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;

            ImageViewHolder(final View view) {
                super(view);
                imageView = view.findViewWithTag("pic");
            }
        }
    }
}

