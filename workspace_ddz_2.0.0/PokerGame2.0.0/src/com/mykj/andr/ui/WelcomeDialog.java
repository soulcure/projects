package com.mykj.andr.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;

public class WelcomeDialog extends Dialog implements OnClickListener {
	private Context mContext;
	public WelcomeDialog(Context context) {
		super(context,R.style.dialog);
		mContext = context;
	}
	
	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		if(v.getId() == R.id.btnConfir){
		}
		AnalyticsUtils.onClickEvent(mContext, "001");
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_dialog);
		Button btn = (Button) findViewById(R.id.btnConfir);
		btn.setOnClickListener(this);
		btn.setText("下一步");
		((TextView)findViewById(R.id.tvMsg)).setText(Html.fromHtml("主人，终于等到您来啦！我是<font color=\"#ff5c03\">Baby</font>， 天天陪你斗地主哟。"));
	}

	
}
