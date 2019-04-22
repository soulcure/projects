package com.mykj.andr.logingift;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mykj.andr.logingift.LoginGiftManager.LoginSignItem;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.pay.ui.SinglePayDialog;
import com.mykj.andr.ui.tabactivity.MarketActivity;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.utils.AppConfig;

/**
 * 
 * @ClassName: PromptEditDialog
 * @Description: TODO(这里描述这个类的作用)
 * @author Administrator
 * @date 2012-7-30 上午11:34:52
 * 
 */
public class LoginGiftSuccessDialog extends AlertDialog implements
		android.view.View.OnClickListener {

	private Context ctx;
	private int day;
	private Button btnOk;
	private Button btnStart;
	private Dialog parent;
	public static boolean showBeVip = true;
	public LoginGiftSuccessDialog(Context context, int day) {
		super(context);
		ctx = context;
		this.day = day;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_gift_success_dialog);

		init();
		setCanceledOnTouchOutside(true);
	}

	void init() {
		String normalNum = "0";
		String gift = "";
		int vipNum = LoginGiftManager.getInstance().getLoginStatus().vipAward;
		LoginSignItem item = LoginGiftManager.getInstance().getLoginSignItem(
				day);
		if (item != null) {
			normalNum = ((item.num == null) ? "0" : item.num);
			gift = ((item.item == null) ? "" : item.item);
		}
		TextView text1 = (TextView) findViewById(R.id.text1);
		TextView text2 = (TextView) findViewById(R.id.text2);
		btnOk = (Button) findViewById(R.id.btn_ok);
		btnStart = (Button)findViewById(R.id.btn_start);
		boolean isVip = HallDataManager.getInstance().getUserMe().isVip();
		UserInfo user = HallDataManager.getInstance().getUserMe();
		int normalInt = 0;
		try{
			normalInt = Integer.parseInt(normalNum);
		}catch(Exception e){}
		if (isVip) {
			text1.setText("主人，签到奖励" + normalNum + "乐豆");
			text2.setText(HallDataManager.getInstance().getUserMe().getVipName()
					+ "给您加送" + vipNum + "乐豆");
			user.setBean(user.getBean() + normalInt + vipNum);
		} else {
			text1.setText("签到成功，奖励" + normalNum + "乐豆");
			text2.setText("主人，贵族每日签到最高加赠" + vipNum + "乐豆，好心动，咱们也加入吧~");
			btnOk.setText("成为贵族");
			user.setBean(user.getBean() + normalInt);
		}
		btnOk.setOnClickListener(this);
		btnStart.setOnClickListener(this);
		findViewById(R.id.btn_close).setOnClickListener(this);
		if(!showBeVip){
			btnOk.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		if (id == R.id.btn_ok) {
			Intent intent =  new Intent(ctx, MarketActivity.class);
			intent.putExtra("toPage", MarketActivity.TAB_VIP);
			ctx.startActivity(intent);
			dismiss();
			if(parent!=null && parent.isShowing()){
				parent.dismiss();
			}
		} else if (id == R.id.btn_close) {
			dismiss();
		}else if(id == R.id.btn_start){
			FiexedViewHelper.getInstance().sHandler.sendEmptyMessage(FiexedViewHelper.HANDLER_CHARGE_SUCCESS);
			dismiss();
			if(parent!=null && parent.isShowing()){
				parent.dismiss();
			}
			
		}
	}

	public void setParent(Dialog parent){
		this.parent = parent;
	}
}
