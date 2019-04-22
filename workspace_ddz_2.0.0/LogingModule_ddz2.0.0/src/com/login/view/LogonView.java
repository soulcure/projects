package com.login.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.login.utils.DensityConst;
import com.mingyou.accountInfo.AccountItem;
import com.mingyou.accountInfo.LoginInfoManager;

public class LogonView extends RelativeLayout{

	public static final int HANDLER_CHOOSE_ACCOUNT=0;   //选择账号handler

	private Context mContext;
	private  Handler mPopHandler;
	private Drawable popupWindwPush=null;  //Drawable popup弹出指示 箭头向下
	private Drawable popupWindwPull=null;  //Drawable popup缩回指示 箭头向上

	private PopupWindow popupWindow;     //账号弹出pop框 
	private AccountsAdapter optionsAdapter; //账号适配器
	private ArrayList<AccountItem> mAccountPsw; //账号信息列表

	private EditText etAccoutInpunt;         //账号输入框
	private EditText etPassWordInput;        //密码输入框

	private ImageView imgLogo;        //移动棋牌图片logo  
	private Button btnGetPassWord;    //忘记密码
	private Button btnLogin;          //登录
	private ImageButton btnChoose;    //账号选择按钮
	private ImageButton btnServer;    //客服电话按钮

	private AccountList accountList;  //popup 弹出账号选择listview 控件

	private RelativeLayout input;     //中间整体UI布局
	private LinearLayout linearInput; //账号和密码框
	private TextView tvVersion;			//版本信息

	@SuppressLint("HandlerLeak")
	public LogonView(Context context) {
		super(context);
		mContext=context;

		// 用来处理选中或者删除下拉项消息
		mPopHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HANDLER_CHOOSE_ACCOUNT:
					int index=msg.arg1;
					String accout=mAccountPsw.get(index).getUsername();
					String password=mAccountPsw.get(index).getPassword();
					if (etAccoutInpunt==null) {
						return;
					}
					etAccoutInpunt.setText(accout);

					if(isEmptyString(password)||password.endsWith(AccountItem.NO_PASS)){
						etPassWordInput.setText("");
						etPassWordInput.setHint("无需密码");
					}else{
						etPassWordInput.setText(password);
					}
					popupWindow.dismiss();
					break;
				default:
					break;
				}

			}
		};


		RelativeLayout container = this;

		//container.setBackgroundResource(R.drawable.bg);  //设置整体背景，开发接口 外部设置

		imgLogo=new ImageView(context);
		imgLogo.setId(GlobalViewId.NORMAL_LOGO);
		//imgLogo.setImageResource(R.drawable.normal_logo);//设置logo图片，开发接口 外部设置

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); 
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lp.topMargin=DensityConst.getPx(10);   //设置上间距 10dp
		//container.addView(imgLogo, lp);



		input=new RelativeLayout(mContext);
		//input.setBackgroundResource(R.drawable.bg_content);
		RelativeLayout.LayoutParams inputLp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		inputLp.topMargin=DensityConst.getPx(-40);   //设置上间距 15dp
		inputLp.bottomMargin = DensityConst.getPx(40);
		inputLp.addRule(RelativeLayout.BELOW,GlobalViewId.NORMAL_LOGO);
//		inputLp.addRule(RelativeLayout.ABOVE, GlobalViewId.BOTTOM_LAYOUT);
		inputLp.addRule(RelativeLayout.CENTER_HORIZONTAL);

		linearInput=new LinearLayout(mContext);
		linearInput.setId(GlobalViewId.LINEAR_INPUT);

		//linearInput.setBackgroundResource(R.drawable.ll_account_psw);
		linearInput.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams inputParams2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,0,1); 
		inputParams2.gravity = Gravity.BOTTOM;
		linearInput.addView(getAccoutInput(mContext),inputParams2);
		linearInput.addView(getPassWordInput(mContext),inputParams2);
		RelativeLayout.LayoutParams inputParams = new RelativeLayout.LayoutParams(DensityConst.getPx(260),LayoutParams.WRAP_CONTENT);
		inputParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		inputParams.topMargin = DensityConst.getPx(40);
		input.addView(linearInput,inputParams);
		RelativeLayout.LayoutParams loginLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		loginLp.addRule(RelativeLayout.BELOW,GlobalViewId.LINEAR_INPUT);
		loginLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		loginLp.topMargin = DensityConst.getPx(5);
		input.addView(getLoginInput(mContext),loginLp);

		//客服电话
		btnServer=new ImageButton(mContext);
		btnServer.setVisibility(View.INVISIBLE);
		//imgButtomLogo.setBackgroundResource(R.drawable.common_chinamobile_logo);
		RelativeLayout.LayoutParams serverLp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT); 
		serverLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		serverLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		serverLp.rightMargin=DensityConst.getPx(20);
//		container.addView(btnServer, serverLp);

		setLastAccout();

		//版本信息
		tvVersion = new TextView(mContext);
		tvVersion.setId(GlobalViewId.TV_VERSION);
		tvVersion.setTextColor(0xffffffff);
		tvVersion.setTextSize(20);
		RelativeLayout.LayoutParams mLp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mLp.leftMargin=DensityConst.getPx(20);
		RelativeLayout bottomLayout = new RelativeLayout(context);
		bottomLayout.setId(GlobalViewId.BOTTOM_LAYOUT);
		bottomLayout.addView(tvVersion, mLp);
		bottomLayout.addView(btnServer, serverLp);
		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bottomParams.bottomMargin = DensityConst.getPx(5);
		container.addView(bottomLayout,bottomParams);
		container.addView(input, inputLp);
		container.addView(imgLogo, lp);
	}



	private boolean isEmptyString(String s) {
		if (s==null||s.equals("")) {
			return true;
		}
		return false;
	}



	/**
	 * 获取账号输入框账号
	 * @return
	 */
	public String getAccoutInput(){
		String accout="";
		if(etAccoutInpunt!=null){
			accout=etAccoutInpunt.getText().toString();
		}
		return accout;
	}



	/**
	 * 获取密码输入框密码
	 * @return
	 */
	public String getPassWordInput(){
		String password="";
		if(etPassWordInput!=null){
			password=etPassWordInput.getText().toString();
		}
		return password;
	}


	/**
	 * 设置背景
	 * @param resId
	 */
	public void setBackgroundRes(int resId){
		this.setBackgroundResource(resId);
	}


	/**
	 * 设置logo图片
	 * @param resId
	 */
	public void setImgLogo(int resId){
		if(imgLogo!=null){
			imgLogo.setImageResource(resId);
		}
	}


	/**
	 * 设置中间输入部分整体控件背景
	 * @param resId
	 */
	public void setBackgroudInput(int resId){
		if(input!=null){
			input.setBackgroundResource(resId);
		}
	}


	/**
	 * 设置账号和密码输入控件背景
	 * @param resId
	 */
	public void setBackgroudLinearInput(int resId){
		if(linearInput!=null){
			linearInput.setBackgroundResource(resId);
		}
	}


	/**
	 * 设置popup弹出按钮控件背景
	 * @param resId
	 */
	public void setBackgroudBtnChoose(int resId){
		if(btnChoose!=null){
			btnChoose.setBackgroundResource(resId);
		}
	}

	/**
	 * 设置忘记密码控件背景
	 * @param resId
	 */
	public void setBackgroundBtnGetPassWord(int resId){
		if(btnGetPassWord!=null){
			btnGetPassWord.setBackgroundResource(resId);
		}
	}

	/**
	 * 设置忘记密码控件按键响应背景
	 * @param resId
	 */
	public void setBackgroundBtnGetPassWord(Drawable drawable){
		if(btnGetPassWord!=null){
			btnGetPassWord.setBackgroundDrawable(drawable);
		}
	}


	/**
	 * 设置忘记密码按键监听器
	 * @param callback
	 */
	public void setBtnGetPassWordOnClickCallBack(View.OnClickListener callback){
		if(btnGetPassWord!=null){
			btnGetPassWord.setOnClickListener(callback);
		}
	}


	/**
	 * 设置登录按键控件背景
	 * @param drawable
	 */
	public void setBackgroundBtnLogin(Drawable drawable){
		if(btnLogin!=null){
			btnLogin.setBackgroundDrawable(drawable);
		}
	}


	/**
	 * 设置登录按键监听器
	 * @param callback
	 */
	public void setBtnLoginOnClickCallBack(View.OnClickListener callback){
		if(btnLogin!=null){
			btnLogin.setOnClickListener(callback);
		}
	}


	/**
	 * 设置客服按键控件背景
	 * @param drawable
	 */
	public void setBackgroundBtnServer(Drawable drawable){
		if(btnServer!=null){
			if(btnServer.getVisibility()==View.INVISIBLE){
				btnServer.setVisibility(View.VISIBLE);
			}
			btnServer.setBackgroundDrawable(drawable);
		}
	}


	/**
	 * 设置客服按键监听器
	 * @param callback
	 */
	public void setBtnServerOnClickCallBack(View.OnClickListener callback){
		if(btnServer!=null){
			btnServer.setOnClickListener(callback);
		}
	}


	/**
	 * 隐藏客服按钮
	 */
	public void hideBtnServer(){
		if(btnServer!=null){
			int vi=btnServer.getVisibility();
			if(vi==View.VISIBLE){
				btnServer.setVisibility(View.INVISIBLE);
			}
		}
	}


	/**
	 * 显示客服按钮
	 */
	public void showBtnServer(){
		if(btnServer!=null){
			int vi=btnServer.getVisibility();
			if(vi==View.INVISIBLE){
				btnServer.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 设置popup弹出框 listview控件背景
	 * @param resId
	 */
	public void setAccountListBackGround(int resId){
		if(accountList!=null){
			accountList.setBackgroundResource(resId);
		}
	}

	/**
	 * 设置popup弹出框 listview控件分隔线
	 * @param resId
	 */
	public void setAccountListDivider(int resId){
		if(accountList!=null){
			accountList.setDivider(resId);
		}
	}



	/**
	 *  popup弹出指示 箭头向下
	 */
	public void setPopupWindwPush(Drawable drawable){
		popupWindwPush=drawable;
		if(btnChoose!=null){
			btnChoose.setBackgroundDrawable(drawable);
		}
	}


	/**
	 *  popup缩回指示 箭头向上
	 */
	public void setPopupWindwPull(Drawable drawable){
		popupWindwPull=drawable;
	}




	/**
	 * 获取账号输入部分控件
	 * @param context
	 * @return
	 */
	private LinearLayout getAccoutInput(Context context){
		LinearLayout linearAccout=new LinearLayout(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams btnlayoutParams = new LinearLayout.LayoutParams(
				0,
				LinearLayout.LayoutParams.WRAP_CONTENT,1);
		btnlayoutParams.leftMargin = DensityConst.getPx(10);
		btnlayoutParams.gravity = Gravity.CENTER_VERTICAL;
		linearAccout.setLayoutParams(layoutParams);
		linearAccout.setOrientation(LinearLayout.HORIZONTAL);


		TextView tvAccout=new TextView(context);
		tvAccout.setText("账号");
		tvAccout.setTextSize(16);
		tvAccout.setTextColor(Color.WHITE);
		linearAccout.addView(tvAccout,btnlayoutParams);

		etAccoutInpunt=new EditText(context);
		etAccoutInpunt.setHint("请输入账号");
		etAccoutInpunt.setSingleLine();
		etAccoutInpunt.setTextColor(Color.WHITE);
		etAccoutInpunt.setTextSize(16);

		//linearAccout.setLayoutParams(layoutParams);

		etAccoutInpunt.setBackgroundDrawable(null);
		LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
				0,LinearLayout.LayoutParams.WRAP_CONTENT,4);
		editParams.gravity = Gravity.CENTER_VERTICAL;
		linearAccout.addView(etAccoutInpunt,editParams);

		btnChoose=new ImageButton(context);
		//btnChoose.setBackgroundResource(R.drawable.btn_account_pull);
		initPopuWindow();
		btnChoose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(popupWindow.isShowing()){
					popupWindow.dismiss();
				}else{
					popupWindwShowing(v);
				}
			}

		});
		
		RelativeLayout.LayoutParams ivlayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		ivlayoutParams.addRule(ALIGN_PARENT_RIGHT);
		ivlayoutParams.addRule(CENTER_VERTICAL);
		ivlayoutParams.rightMargin = DensityConst.getPx(5);
		RelativeLayout relativeLayout = new RelativeLayout(context);
		relativeLayout.addView(btnChoose,ivlayoutParams);
		linearAccout.addView(relativeLayout,btnlayoutParams);
		return linearAccout;
	}


	/**
	 * 获取密码输入部分控件
	 * @param context
	 * @return
	 */
	private LinearLayout getPassWordInput(Context context){
		LinearLayout linearAccout=new LinearLayout(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
//		linearAccout.setPadding(13, 6, 13, 6);
		LinearLayout.LayoutParams btnlayoutParams = new LinearLayout.LayoutParams(
				0,
				LinearLayout.LayoutParams.WRAP_CONTENT,1);
		btnlayoutParams.leftMargin = DensityConst.getPx(10);
		btnlayoutParams.gravity = Gravity.CENTER_VERTICAL;
		linearAccout.setOrientation(LinearLayout.HORIZONTAL);
		linearAccout.setLayoutParams(layoutParams);
		TextView tvPassWord=new TextView(context);
		tvPassWord.setText("密码");
		tvPassWord.setTextSize(16);
		tvPassWord.setTextColor(Color.WHITE);
		linearAccout.addView(tvPassWord,btnlayoutParams);

		etPassWordInput=new EditText(context);
		etPassWordInput.setHint("请输入密码");
		etPassWordInput.setSingleLine();
		etPassWordInput.setTextColor(Color.WHITE);
		etPassWordInput.setTextSize(16);
		etPassWordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		etPassWordInput.setBackgroundDrawable(null);
		
		LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
				0,LinearLayout.LayoutParams.WRAP_CONTENT,5);
		editParams.gravity = Gravity.CENTER_VERTICAL;
		linearAccout.addView(etPassWordInput,editParams);

		return linearAccout;
	}


	/**
	 * 获取忘记密码 登录按钮部分控件
	 * @param context
	 * @return
	 */
	private LinearLayout getLoginInput(Context context){
		LinearLayout linearAccout=new LinearLayout(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				DensityConst.getPx(140),
				DensityConst.getPx(50));
		linearAccout.setOrientation(LinearLayout.VERTICAL);
		linearAccout.setLayoutParams(layoutParams);


		btnGetPassWord=new Button(context);
		btnGetPassWord.setVisibility(View.GONE);
		//btnGetPassWord.setText("忘记密码");
		//btnGetPassWord.setBackgroundResource(R.drawable.btn_login_modify_unuse);
		linearAccout.addView(btnGetPassWord);

		btnLogin=new Button(context);
		//btnLogin.setBackgroundDrawable(UtilDrawableStateList.newSelector(context,R.drawable.btn_login_login_normal,R.drawable.btn_login_login_press));
		linearAccout.addView(btnLogin,layoutParams);

		return linearAccout;
	}


	/**
	 * 初始化popup
	 */
	@SuppressWarnings("deprecation")
	private void initPopuWindow() {
		accountList=new AccountList(mContext);

		ListView pwLoginList = accountList.getAccoutList();

		mAccountPsw = LoginInfoManager.getInstance().getAccountInfo();
		optionsAdapter = new AccountsAdapter(mContext,mPopHandler,mAccountPsw);
		pwLoginList.setAdapter(optionsAdapter);
		popupWindow=new PopupWindow(mContext);
		popupWindow.setContentView(accountList);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setWidth(DensityConst.getPx(200));
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}








	private void popupWindwShowing(final View v) {
		hideInputKeyBoard();
		popupWindow.showAsDropDown(etAccoutInpunt,0,0);
		if(popupWindwPull!=null){
			((ImageView) v).setBackgroundDrawable(popupWindwPull);
		}
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if(popupWindwPush!=null){
					((ImageView) v).setBackgroundDrawable(popupWindwPush);
				}
			}
		});
	}


	/**
	 * 隐藏输入框
	 */
	public void hideInputKeyBoard(){
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			if (imm.hideSoftInputFromWindow(getWindowToken(), 0)) {
				return;
			}
		}
	}


	/**
	 * @param ver 版本信息
	 */
	public void setVersion(String ver){
		if(tvVersion != null){
			tvVersion.setText(ver);
		}
	}


	/**
	 * 自动填充最后一次账号登录信息
	 */
	private void setLastAccout(){
		final AccountItem lastLoginAcc = LoginInfoManager.getInstance().getLastLoginAcc();
		if (lastLoginAcc != null ) {
			final String accout=lastLoginAcc.getUsername();
			final String password=lastLoginAcc.getPassword();

			final String username= LoginInfoManager.getInstance().getUserName();  //暂存的登录账户信息
			final String userpassword= LoginInfoManager.getInstance().getUserPassWord();//暂存的登录密码信息
			if(username!=null){
				if(!username.equals(accout)){  //登录密码错误分支
					etAccoutInpunt.setText(username);
					etPassWordInput.setText(userpassword);
					return;
				}
			}


			etAccoutInpunt.setText(accout);

			if(password!=null && password.endsWith(AccountItem.NO_PASS)){
				etPassWordInput.setText("");
				etPassWordInput.setHint("无需密码");
			}else{
				etPassWordInput.setText(password);
			}


			etAccoutInpunt.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s,
						int start, int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					etPassWordInput.setHint("请输入密码");
					etPassWordInput.setText("");
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}  

			});

		}
	}


}