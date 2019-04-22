package com.login.view;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mingyou.accountInfo.AccountItem;

// 自定义适配器Adapter
public class AccountsAdapter extends BaseAdapter {

	private List<AccountItem> mList;

	private Context mContext;
	
	private Handler mHandler;
	/**
	 * 自定义构造方法
	 * 
	 * @param activity
	 * @param handler
	 * @param list
	 */
	public AccountsAdapter(Context context, Handler handler,List<AccountItem> list) {
		mContext = context;
		mHandler=handler;
		mList = list;
	}

	@Override
	public int getCount() {
		if (mList != null) {
			return mList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (mList != null) {
			return mList.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    TextView tvAccout=new TextView(mContext);
	    tvAccout.setSingleLine();
	    tvAccout.setClickable(true);
	    tvAccout.setGravity(Gravity.CENTER_VERTICAL);
	    tvAccout.setPadding(10, 2, 10, 2);
	    tvAccout.setTextSize(18);
	    tvAccout.setTextColor(Color.WHITE);
	    tvAccout.setText(mList.get(position).getUsername());
	    tvAccout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = mHandler.obtainMessage();
				msg.what = LogonView.HANDLER_CHOOSE_ACCOUNT;
				msg.arg1=position;
				// 发出消息
				mHandler.sendMessage(msg);
				
			}
		});
		return tvAccout;
	}

}
