package com.mykj.andr.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.Util;

public class MeinvDialog extends Dialog implements OnClickListener {
	public static final int MEINV_TYPE_HAPPY = 1;
	public static final int MEINV_TYPE_KAWAYI = 2;
	public static final int MEINV_TYPE_CRY = 3;
	
	private Context mContext;
	private String mInfo;
	private String btnStr;
	private int type;
	private android.view.View.OnClickListener mListener = null;
	
	/**
	 * @param context
	 * @param info  要显示的信息
	 * @param btnStr 按钮文字
	 * @param meinvType 美女类型，参考MEINV_TYPE_HAPPY， MEINV_TYPE_KAWAYI， MEINV_TYPE_CRY
	 */
	public MeinvDialog(Context context,String info, String btnStr, int meinvType) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		mContext = context;
		mInfo = info;
		this.btnStr = btnStr;
		type = meinvType;
	}
	
	
	/**
	 * 设置确定按钮的响应
	 * @param lis
	 */
	public void setConfirmCallBack(android.view.View.OnClickListener lis){
		mListener = lis;
	}
	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		if(v.getId() == R.id.btnConfir){
			if(mListener!=null){
				mListener.onClick(v);
			}
		}
		AnalyticsUtils.onClickEvent(mContext, "001");
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_dialog);
		
		if(mInfo!=null){
			((TextView)findViewById(R.id.tvMsg)).setText(Html.fromHtml(mInfo));
		}
		ImageView img = (ImageView)findViewById(R.id.mm_img);
		if(type == MEINV_TYPE_HAPPY){
			img.setImageResource(R.drawable.meinv_happy);
		}else if(type == MEINV_TYPE_KAWAYI){
			img.setImageResource(R.drawable.meinv_kawayi);
		}else if(type == MEINV_TYPE_CRY){
			img.setImageResource(R.drawable.meinv_cry);
		}
		Button btn = (Button)findViewById(R.id.btnConfir);
		if(!Util.isEmptyStr(btnStr)){
			btn.setText(btnStr);
		}
		btn.setOnClickListener(this);
	}

	
}
