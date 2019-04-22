package com.mykj.andr.newsflash;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.Util;

/**
 * @author wanghj
 * 快讯对话框
 */
public class NewsFlashDialog extends AlertDialog implements
		android.view.View.OnClickListener {
	private ListView lvNoticeSystem;
	private Activity mContext;
	private List<NewsFlashData> datas;    //快讯数据
	private String title;                 //快讯标题

	public NewsFlashDialog(Activity context, String title,
			List<NewsFlashData> datas) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		this.datas = datas;
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.notice_activity_a);
		init();
	}

	private void init() {
//		if (datas != null && datas.size() > 0) {
//			LinearLayout contaner = (LinearLayout) findViewById(R.id.newsflash_main_contaner);  //容器
//			for (NewsFlashData data : datas) {
//				TextView tv = new TextView(mContext);        //快讯，动态添加
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.FILL_PARENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//				lp.bottomMargin=DensityConst.getPx(5);
//				tv.setLayoutParams(lp);
//				tv.setTextColor(0xffffffff);
//				tv.setTag(data);
//				tv.setTextSize(16);
//				tv.setClickable(true);
//				tv.setLongClickable(false);
//				String btnText;
//				if(data.type == 0){
//					btnText="";
//				}else
//				if(Util.isEmptyStr(data.btnText)){
//					btnText=" 详情>>";
//				}else{
//					btnText=" "+data.btnText+">>";
//				}
//				SpannableString spanableInfo = new SpannableString(
//						Html.fromHtml(data.content + btnText));          //特殊样式，用html格式，后面详情固定
//				if(data.type != 0){
//					int end = spanableInfo.length();
//					int start = end - btnText.length() + 1;
//					spanableInfo.setSpan(clickable, start, end,
//							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);       //将详情设置成可点击
//					spanableInfo.setSpan(new ForegroundColorSpan(0xff86ff35),
//							start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);        //详情颜色
//				}
//				tv.setText(spanableInfo);
//
//				tv.setMovementMethod(LinkMovementMethod.getInstance());       //必须加这个不然不能点击
//				contaner.addView(tv);
//			}
//
//		}
//		if (!Util.isEmptyStr(title)) {
//			((TextView) findViewById(R.id.newsflash_main_title)).setText(title);
//		}
//		findViewById(R.id.newsflash_main_btn).setOnClickListener(this);
//		findViewById(R.id.newsflash_main_cancel).setOnClickListener(this);
		
		findViewById(R.id.tvBack).setOnClickListener(this);
//		findViewById(R.id.ensure).setOnClickListener(this);
		
		// 从View中找寻ListView
		lvNoticeSystem = (ListView)findViewById(R.id.lvNoticeSystem);
		lvNoticeSystem.setAdapter(new NewsFlashAdapter());
	}

	ClickableSpan clickable = new ClickableSpan() {

		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
			dismiss();
			NewsFlashManager.getInstance().operate(mContext,
					(NewsFlashData) widget.getTag());             //详情操作
		}

	};

	@Override
	public void dismiss(){
		NewsFlashManager.getInstance().savedReaded();
		FiexedViewHelper.getInstance().sHandler.sendEmptyMessage(FiexedViewHelper.HANDLER_UPDATE_CARDZONE_MSG_TIP);
		super.dismiss();
	}
	
	public void show(){
//		try{
//			NewsFlashManager.getInstance().readed(datas.get(0).subTitle);
//		}catch (Exception e){}
		super.show();
	}
	
	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id==R.id.tvBack){
			dismiss();
		}
	}
	
	
	
	class NewsFlashAdapter extends BaseAdapter{
		ViewHolder holder;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas == null ? 0:datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return (datas==null || position >= datas.size())?null:datas.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				// 获得界面解析器
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.notice_system_item, null);
				holder = new ViewHolder();

				holder.titleArea = convertView.findViewById(R.id.lySTitle);
				holder.ivTitleRead=(ImageView) convertView.findViewById(R.id.ivSTitleRead);
				
				// 保留
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tvSTitle);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tvSContent);
				holder.btnExpand = (ImageView) convertView.findViewById(R.id.btn_expand);
				holder.expandArea = convertView.findViewById(R.id.msg_expand_area);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(datas!=null && datas.size() > position){
				NewsFlashData data = datas.get(position);
				
				holder.tvTitle.setText(data.subTitle == null?"":data.subTitle);
				
				String btnText;
				if(data.type == 0){
					btnText="";
				}else
				if(Util.isEmptyStr(data.btnText)){
					btnText=" 详情>>";
				}else{
					btnText=" "+data.btnText+">>";
				}
				SpannableString spanableInfo = new SpannableString(
						Html.fromHtml(data.content + btnText));          //特殊样式，用html格式，后面详情固定
				if(data.type != 0){
					int end = spanableInfo.length();
					int start = end - btnText.length() + 1;
					spanableInfo.setSpan(clickable, start, end,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);       //将详情设置成可点击
					spanableInfo.setSpan(new ForegroundColorSpan(0xff057300),
							start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);        //详情颜色
				}
				holder.tvContent.setTag(data);
				holder.tvContent.setText(spanableInfo);
				holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());       //必须加这个不然不能点击
			
				if(data.isExpanded()){
					holder.expandArea.setVisibility(View.VISIBLE);
					holder.btnExpand.setImageResource(R.drawable.btn_msg_close);
				}else{
					holder.expandArea.setVisibility(View.GONE);
					holder.btnExpand.setImageResource(R.drawable.btn_msg_extern);
				}
				holder.titleArea.setTag(data);
				holder.titleArea.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						NewsFlashData tag = (NewsFlashData )arg0.getTag();
						if(tag!=null){
							tag.setExpand(!tag.isExpanded());
							NewsFlashManager.getInstance().readed(tag.subTitle);
						}
						notifyDataSetChanged();
					}
				});
			
			}
			return convertView;
		}
		
		class ViewHolder {
			TextView tvTitle;
			TextView tvContent;
			ImageView ivTitleRead;
			ImageView btnExpand;
			View expandArea;
			View titleArea;
		}
		
	}
}