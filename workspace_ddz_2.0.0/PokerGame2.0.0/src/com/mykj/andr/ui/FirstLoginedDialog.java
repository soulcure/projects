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

public class FirstLoginedDialog extends Dialog implements OnClickListener {
	private String mContenString;
	private Context mContext;
	public FirstLoginedDialog(Context context,String content) {
		super(context,R.style.dialog);
		mContext = context;
		mContenString = content;
	}
	
	@Override
	public void onClick(View v) {
		FiexedViewHelper.getInstance().playKeyClick();
		if(v.getId() == R.id.first_login_start_game_B){
//			FiexedViewHelper.getInstance().quickGame();
		}
		AnalyticsUtils.onClickEvent(mContext, "001");
		dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_first_logined);
		TextView contentTextView = (TextView) findViewById(R.id.first_login_content_TV);
		contentTextView.setText(Html.fromHtml(mContenString));
		Button startGameButton = (Button) findViewById(R.id.first_login_start_game_B);
		startGameButton.setOnClickListener(this);
		findViewById(R.id.close).setOnClickListener(this);
	}

	
}
