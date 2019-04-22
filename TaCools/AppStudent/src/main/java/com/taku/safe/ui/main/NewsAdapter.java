package com.taku.safe.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taku.safe.R;
import com.taku.safe.activity.WebViewActivity;
import com.taku.safe.protocol.respond.RespNews;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RespNews.PageInfoBean.ArticleListBean> mList;

    public NewsAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RespNews.PageInfoBean.ArticleListBean> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_news, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RespNews.PageInfoBean.ArticleListBean item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).itemView;

        ImageView img_news = ((TextViewHolder) viewHolder).img_news;
        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        TextView tv_number = ((TextViewHolder) viewHolder).tv_number;

        tv_title.setText(item.getTitle());
        tv_time.setText(item.getCreateTime());
        tv_number.setText(item.getReadNum());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.ARTICLE_ID, item.getArticleId());
                mContext.startActivity(intent);
            }
        });

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        ImageView img_news;
        TextView tv_title;
        TextView tv_time;
        TextView tv_number;


        private TextViewHolder(View itemView) {
            super(itemView);
            img_news = (ImageView) itemView.findViewById(R.id.img_news);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
        }

    }


}

