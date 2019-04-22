package com.ivmall.android.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.entity.VipListItem;

import java.util.List;

/**
 * Created by koen on 2016/5/13.
 */
public class BuyVipAdapter extends RecyclerView.Adapter<BuyVipAdapter.BuyVipViewHolder>{

    private Context mContext;
    private boolean isPhone;
    private List<VipListItem> list;

    public BuyVipAdapter(Context context, boolean isPhone, List<VipListItem> l) {
        mContext = context;
        this.isPhone = isPhone;
        list = l;
    }


    @Override
    public BuyVipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BuyVipViewHolder holder;
        if(isPhone) {
            holder = new BuyVipViewHolder(LayoutInflater.from(
                    mContext).inflate(R.layout.goods_item_v, parent, false));
        } else {
            holder = new BuyVipViewHolder(LayoutInflater.from(
                    mContext).inflate(R.layout.goods_item_l, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BuyVipViewHolder holder, int p) {
        final int position = p;
        VipListItem item = list.get(position);
        holder.tvVipName.setText(item.getVipName());
        holder.tvCost.setText(item.getListPriceStr());
        holder.tvCost.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
        holder.tvDiscount.setText(item.getVipPriceStr());
        if (item.getPreferential() > 0) {
            if (isPhone) {
                holder.tvCost.setText("代金券 -" + item.getPreferentialStr());
                holder.tvCost.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);  // 去除横杠
            } else {
                holder.tvCoupon.setText("代金券 -" + item.getPreferentialStr());
            }
            holder.itemView.setTag(item.getPreferentialPrice());
        } else {
            if(!isPhone)
            holder.tvCoupon.setVisibility(View.GONE);
            holder.itemView.setTag(item.getVipPrice());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BuyVipViewHolder extends RecyclerView.ViewHolder {

        TextView tvVipName;
        TextView tvCost;
        TextView tvCoupon;
        TextView tvDiscount;

        public BuyVipViewHolder(View itemView) {
            super(itemView);
            tvVipName = (TextView) itemView.findViewById(R.id.tv_vipName);//商品名称
            tvCost = (TextView) itemView.findViewById(R.id.tv_cost);  //原价
            if(!isPhone)
            tvCoupon = (TextView) itemView.findViewById(R.id.tv_coupon);//优惠券
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);//现价
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener litener) {
        mOnItemClickLitener = litener;
    }
}
