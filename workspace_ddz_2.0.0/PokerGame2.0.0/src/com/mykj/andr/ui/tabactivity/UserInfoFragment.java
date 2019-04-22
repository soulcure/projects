package com.mykj.andr.ui.tabactivity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mingyou.accountInfo.AccountItem;
import com.mingyou.accountInfo.LoginInfoManager;
import com.mingyou.distributor.NetPrivateListener;
import com.mingyou.distributor.NetSocketPak;
import com.mykj.andr.headsys.HeadManager;
import com.mykj.andr.model.HallDataManager;
import com.mykj.andr.model.UserCenterData;
import com.mykj.andr.model.UserInfo;
import com.mykj.andr.net.NetSocketManager;
import com.mykj.andr.pay.PayUtils;
import com.mykj.andr.pay.model.FastBuyModel;
import com.mykj.andr.provider.UserCenterProvider;
import com.mykj.andr.ui.fragment.NodifyPasswordFragmentDialog;
import com.mykj.comm.io.TDataInputStream;
import com.mykj.comm.io.TDataOutputStream;
import com.mykj.game.FiexedViewHelper;
import com.mykj.game.ddz.R;
import com.mykj.game.ddz.api.AnalyticsUtils;
import com.mykj.game.utils.AppConfig;
import com.mykj.game.utils.CenterUrlHelper;
import com.mykj.game.utils.Log;
import com.mykj.game.utils.PostAsyncTask;
import com.mykj.game.utils.Util;
import com.mykj.game.utils.UtilHelper;

/**
 * 用户信息界面
 */
public class UserInfoFragment extends Fragment implements OnClickListener {
    private final static int FROM_CAMERA = 1;
    private final static int FROM_PHOTO = 2;
    private final static int IMAGE_CUT = 3;
    private final static String TAG = "UserInfoFragment";

    // -----------------------------------协议数据处理--------------------------------------------------
    private static final short LS_TRANSIT_LOGON = 18;
    /**
     * 子协议-个人中心信息请求
     */
    private static final short MSUB_USERCENTER_INFO_REQ = 115;
    /**
     * 子协议-个人中心信息返回
     */
    private static final short MSUB_USERCENTER_INFO_RESP = 116;
    /**
     * 子协议-个人中心信息读取失败
     */
    private static final short MSUB_USERCENTER_INFO_FAIL = 117;

    /**
     * 子协议-个人信息修改请求
     */
    private static final short MSUB_CMD_MODIFY_USERINFO = 1;
    /**
     * 子协议-个人信息修改结果
     */
    private static final short MSUB_CMD_MODIFY_RESULT = 2;

    /**
     * 手机道具主协议
     */
    private static final short LS_MDM_PROP = 17;
    /**
     * 上报头像结果监听
     */
    private static final short LSUB_CMD_UPLOAD_USER_HEAD_REQ = 890;


    // ---------------------定义消息发送标识，提供与handler------------------------

    private static final int HANDLER_USERCENTER_SUCCESS = 1;

    private static final int HANDLER_USERCENTER_FAIL = 2;

    /**
     * 修改用户信息成功
     */
    private static final int HANDLER_MODIFY_USERINFO_SUCCESS = 3;
    /**
     * 修改失败
     */
    private static final int HANDLER_MODIFY_USERINFO_FAILED = 4;


    private static final int HANDLER_FAIL = 5;

    /**
     * 更新头像
     */
    private static final int HANDLER_UPDATE_HEAD = 6;

    /**
     * 上传头像结果
     */
    public static final int HANDLER_UPLOAD_HEAD_RESULT = 7;

    /**
     * 剪裁图片
     */
    private static final int HANDLER_CUT_PICTRUE = 8;


    // --------------个人中心-----------------
    private Activity mAct;
    private TextView tvDouNo;// 乐豆数量
    private TextView tvCount;// 用户账号
//    private TextView tvSex;// 性别
    private TextView tvWinRat;// 胜率
    private TextView tvMasterScore;// 大师积分
//    private TextView tvTelCharge;// 话费卷
    private ImageView imgVipLevel;// vip等级
    private Button btnBindAccount;// 绑定账号按钮
    private Button btnModifyInfo;// 修改资料按钮
    private EditText etNickName;// 昵称
    private RadioGroup rgSelectSex;// 性别单选项
    private RadioButton rbMan, rbWoman;// 男 or女单选按钮
    private ImageView headImage;// 头像
    
    /**
     * 这个东西不显示，但是有些手机，如华为P7，在退出的时候会改图片大小，没这个就改头像的，所以加这个使头像不改变大小
     */
    private ImageView testImg;
    
    private TextView tvGuid;

    private File mFile; // 照相机拍照图片的存储路径

    /**
     * 是否编辑用户资料， true：编辑资料 ， false：不在编辑状态
     */
//    private boolean isEditUserInfo = false;
    private String modifyNickname;
    private byte modifySexId;
    private int mCheckedId;
    private String mPreNickName;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
//        try {
//            mListener = (OnRankArticleSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnRankArticleSelectedListener");
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册头像监听
        listenUploadHeaderImage();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.usercenter_userinfo_tab, container, false);
        // 初始化视图
        initView(rootView);
        // 初始化监听器
        initListener(rootView);

        UserCenterData userData = UserCenterProvider.getInstance()
                .getUserCenterData();
        if (userData != null) {
            updateView(userData);
        } else {
            int userId = FiexedViewHelper.getInstance().getUserId();
            getUserCenterInfo(userId, AppConfig.clientID);
        }
        return rootView;
    }

    /**
     * 控件初始化
     */
    private void initView(View view) {
        tvDouNo = (TextView) view.findViewById(R.id.usercenter_dou_number);
        tvCount = (TextView) view.findViewById(R.id.tvCount);
        etNickName = (EditText) view.findViewById(R.id.etNickName);
        tvWinRat = (TextView) view.findViewById(R.id.usercenter_winrate);
        tvMasterScore = (TextView) view.findViewById(R.id.usercenter_masterscore);
//        tvTelCharge = (TextView) view.findViewById(R.id.usercenter_telcharge);
        headImage = (ImageView) view.findViewById(R.id.usercenter_head);
        testImg = (ImageView)view.findViewById(R.id.testImg);
        imgVipLevel = (ImageView) view.findViewById(R.id.imgVip);
        btnBindAccount = (Button) view.findViewById(R.id.usercenter_bindaccount);
        rgSelectSex = (RadioGroup) view.findViewById(R.id.usercenter_select_sex);
        rbMan = (RadioButton) view.findViewById(R.id.usercenter_man);
        rbWoman = (RadioButton) view.findViewById(R.id.usercenter_woman);
        btnModifyInfo = (Button) view.findViewById(R.id.usercenter_modify_userinfo);
        tvGuid = (TextView) view.findViewById(R.id.usercenter_id);
    }


    /**
     * 注册监听
     */
    private void initListener(View view) {
        view.findViewById(R.id.usercenter_getdou).setOnClickListener(this);
        btnBindAccount.setOnClickListener(this);
//        view.findViewById(R.id.usercenter_gettelcharge).setOnClickListener(this);
        btnModifyInfo.setOnClickListener(this);
        headImage.setOnClickListener(this);
        etNickName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				changeBtnState(String.valueOf(s));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
        rgSelectSex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (HallDataManager.getInstance().getUserMe().faceID==0) {
					if (checkedId==R.id.usercenter_man) {
						headImage.setImageResource(R.drawable.ic_male_face);
					}else {
						headImage.setImageResource(R.drawable.ic_female_face);
					}
				}
				changeBtnState(checkedId);
			}
		});
    }
    
    private void changeBtnState(int checkedId) {
    	if (checkedId==mCheckedId) {
    		String name = etNickName.getText().toString();
    		UserCenterData data = UserCenterProvider.getInstance()
                    .getUserCenterData();
			if (!data.nickName.equals(name)) {
				btnModifyInfo.setVisibility(View.VISIBLE);
			}else {
				btnModifyInfo.setVisibility(View.INVISIBLE);
			}
		}else {
			btnModifyInfo.setVisibility(View.VISIBLE);
		}
    }
    
    private void changeBtnState(String name) {
    	UserCenterData data = UserCenterProvider.getInstance()
                .getUserCenterData();
    	if (data.nickName.equals(name)) {
    		int checkId = rgSelectSex.getCheckedRadioButtonId();
			if (checkId==mCheckedId) {
				btnModifyInfo.setVisibility(View.INVISIBLE);
			}else {
				btnModifyInfo.setVisibility(View.VISIBLE);
			}
		}else {
			btnModifyInfo.setVisibility(View.VISIBLE);
		}
    }

    @Override
    public void onClick(View v) {
    	FiexedViewHelper.getInstance().playKeyClick();
        switch (v.getId()) {
            case R.id.usercenter_getdou:
                // 获取乐豆
                // 快捷购买
                PayUtils.showBuyDialog(mAct, FastBuyModel.propId,
                        FastBuyModel.isFastBuyConfirm,"","");
                AnalyticsUtils.onClickEvent(mAct, "025");
                break;
            case R.id.usercenter_head:
                // 修改头像
                openCamera(1);
                AnalyticsUtils.onClickEvent(mAct, "022");
                break;
            case R.id.usercenter_bindaccount:
                // 绑定账号
                NodifyPasswordFragmentDialog dialog = new NodifyPasswordFragmentDialog(mAct, false);

                dialog.setBindSucessListener(new NodifyPasswordFragmentDialog.BindSucessListener() {
                    @Override
                    public void bindSucess(String count) {
                        UserCenterData userData = UserCenterProvider.getInstance()
                                .getUserCenterData();
                        if (userData != null) {
                            tvDouNo.setText(userData.getLeDou() + "");
                        }
                        UserCenterProvider.getInstance().setUserCount(count);
                        tvCount.setText(count);
                        btnBindAccount.setVisibility(View.GONE);
                        tvCount.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 3.0f));
                    }
                });
                dialog.show();
                AnalyticsUtils.onClickEvent(mAct, "023");
                break;
//            case R.id.usercenter_gettelcharge:
//                // 获取话费卷
//                AnalyticsUtils.onClickEvent(mAct, "024");
//            {
//
//                int index = NewCardZoneProvider.getInstance()
//                        .getMatchIndex();
//                if (index >= 0) {
//                    mAct.finish();
//                    FiexedViewHelper.getInstance().jumpToMatchList(index);
//                }
//            }
//            break;
            case R.id.usercenter_modify_userinfo:
                // 修改资料
//                if (isEditUserInfo) {
                    modifyNickname = etNickName.getText().toString();
                    modifySexId = (byte) (rgSelectSex.getCheckedRadioButtonId() == rbMan
                            .getId() ? 1 : 0);
                    UserCenterData data = UserCenterProvider.getInstance()
                            .getUserCenterData();
                    if (data.getNickName().equals(modifyNickname)
                            && data.getSex() == modifySexId) {
                        com.mykj.game.utils.Toast.makeText(mAct, "您没有修改内容!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //开始修改
                        modifyUserInfo(modifyNickname, modifySexId);
                    }
//                }
//                isEditUserInfo = !isEditUserInfo;
//                switchEditUserInfo(isEditUserInfo);
                AnalyticsUtils.onClickEvent(mAct, "026");
                break;

            default:
                break;
        }
    }


    private void updateView(UserCenterData data) {
        setHeadImage(HallDataManager.getInstance().getUserMe().faceID);
        tvDouNo.setText(data.getLeDou() + "");
        tvCount.setText(String.valueOf(data.account));
        mPreNickName = data.getNickName();
        etNickName.setText(mPreNickName);
//        tvSex.setText(data.getSex() == 1 ? "男" : "女");
        if (data.getSex()==1) {
        	mCheckedId = R.id.usercenter_man;
        	rbMan.setChecked(true);
        	rbWoman.setChecked(false);
		}else {
			mCheckedId = R.id.usercenter_woman;
			rbMan.setChecked(false);
        	rbWoman.setChecked(true);
		}
        tvWinRat.setText(data.winBout + "胜" + data.loseBout + "败" + "("
                + data.winRatio + "%)");
//        tvTelCharge.setText(data.huafeiquan + "");
        tvMasterScore.setText(data.masterScore + "");
        tvGuid.setText(data.ID + "");


        UserInfo userInfo = HallDataManager.getInstance().getUserMe();
        // 更新绑定账号按钮
        updateBindAccountBtn();
        // 更新vip
        UtilHelper.setVipView(imgVipLevel, userInfo.getVipLevel(), !userInfo.isVip());

    }


    private void updateBindAccountBtn() {
        UserInfo userInfo = HallDataManager.getInstance().getUserMe();
        byte userLoginType = userInfo.loginType;
        if (userLoginType == AccountItem.ACC_TYPE_TEMP
                && !LoginInfoManager.getInstance().isBind()) { // 是游客
            btnBindAccount.setVisibility(View.VISIBLE);
        } else {
            btnBindAccount.setVisibility(View.GONE);
        }
    }

    /**
     * 切换编辑状态
     *
     * @param isEdit
     */
//    private void switchEditUserInfo(boolean isEdit) {
//        if (isEdit) {
//            etNickName.setEnabled(true);
//            rgSelectSex.setVisibility(View.VISIBLE);
////            tvSex.setVisibility(View.GONE);
//            btnModifyInfo.setText("保存资料");
//            if (tvSex.getText().toString().contains("男")) {
//                rbMan.setChecked(true);
//                rbWoman.setChecked(false);
//            } else {
//                rbMan.setChecked(false);
//                rbWoman.setChecked(true);
//            }
//
//        } else {
//            etNickName.setEnabled(false);
//            rgSelectSex.setVisibility(View.GONE);
//            tvSex.setVisibility(View.VISIBLE);
//            btnModifyInfo.setText("修改资料");
//            if (rbMan.isChecked()) {
//                tvSex.setText("男");
//            } else {
//                tvSex.setText("女");
//            }
//        }
//    }


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            int userId = FiexedViewHelper.getInstance().getUserId();
            switch (msg.what) {
                case HANDLER_USERCENTER_SUCCESS: // 个人中心数据下发成功
                    UserCenterData userData = UserCenterProvider.getInstance()
                            .getUserCenterData();
                    if (userData != null) {
                        updateView(userData);
                    }
                    break;
                case HANDLER_USERCENTER_FAIL:// 接受错误数据
                    Toast.makeText(mAct, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_MODIFY_USERINFO_SUCCESS:// 修改个人信息成功，包括昵称，城市，性别
//                    Toast.makeText(mAct, msg.obj.toString(),
//                            Toast.LENGTH_SHORT).show();
                	
                	Util.displayCenterToast(tvMasterScore,msg.obj.toString());
                    UserInfo userInfo = HallDataManager.getInstance().getUserMe();
                    userInfo.gender = modifySexId;
                    userInfo.nickName = modifyNickname;

                    UserCenterData data = UserCenterProvider.getInstance()
                            .getUserCenterData();
                    if (data != null) {
                        data.setNickName(modifyNickname);
                        data.setSex(modifySexId);
                    }
                    //修改成功，更新界面
                    updateView(data);
                    // 改为不可编辑状态
                    btnModifyInfo.setVisibility(View.INVISIBLE);
                    break;
                case HANDLER_MODIFY_USERINFO_FAILED:
                    String errMsg = msg.obj.toString();
                    if (!Util.isEmptyStr(errMsg)) {
                        Toast.makeText(mAct, errMsg, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case HANDLER_FAIL: // 网络解析数据出现Error
                    Toast.makeText(mAct,
                            getResources().getString(R.string.info_obtain_failed),
                            Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_UPDATE_HEAD:    //更新头像

                    setHeadImage(HallDataManager.getInstance().getUserMe().faceID);

                    break;
                case HANDLER_UPLOAD_HEAD_RESULT:
                    String uploadMsg = (String) msg.obj;
                    int res = msg.arg1;
                    int faceId = msg.arg2;
                    if (res==0) {
                    	Util.displayCenterToast(tvMasterScore ,"post文件成功");
                    	HallDataManager.getInstance().getUserMe().setFaceId(faceId);
					}else {
						Util.displayCenterToast(tvMasterScore ,"post文件失败");
					}

                    if (res == 0 && faceId != 0) {
                    /*String path = HeadManager.getInstance().getImgFullFileName(faceId, HeadManager.SELFHEAD);   //先查分区图片
                    if(!Util.isEmptyStr(path)){
						File file=new File(path);
						if(file.exists()){
							file.delete();
						}
					}*/
                        setHeadImage(faceId);

                    }

//                    if (!Util.isEmptyStr(uploadMsg)) {
//                        Toast.makeText(mAct, uploadMsg, Toast.LENGTH_SHORT).show();
//                    }

                    break;
                case HANDLER_CUT_PICTRUE:// 裁剪图片
                    Intent in = (Intent) msg.obj;
                    startActivityForResult(in, IMAGE_CUT);
                    break;

                default:
                    break;
            }
        }
    };


    private void setHeadImage(int faceId) {
        HeadManager.getInstance().setUpdateHanler(mHandler, HANDLER_UPDATE_HEAD);

        int userId = HallDataManager.getInstance().getUserMe().userID;
        Drawable head = HeadManager.getInstance().getZoneHead(mAct, userId, faceId);
        /*Drawable d = getResources().getDrawable(R.drawable.img_head_bg);
        Bitmap bmp = Util.drawabletoBitmap(head);
        Bitmap scaleBmap = Util.imageScale(toRoundCorner(bmp, 5), d.getIntrinsicWidth(), d.getIntrinsicHeight());
        headImage.setImageBitmap(scaleBmap);*/
        headImage.setImageDrawable(head);
        testImg.setImageDrawable(head);
    }


    private Bitmap toRoundCorner(Bitmap bitmap, int round) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float r = round;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void getUserCenterInfo(int userId, int clentId) {
        // 创建发送的数据包
        TDataOutputStream tdos = new TDataOutputStream(false);
        tdos.writeInt(userId);
        // tdos.writeInt(8080);
        tdos.writeInt(clentId);

        NetSocketPak centerInfo = new NetSocketPak(LS_TRANSIT_LOGON,
                MSUB_USERCENTER_INFO_REQ, tdos);
        // 定义接受数据的协议
        short[][] parseProtocol = {
                {LS_TRANSIT_LOGON, MSUB_USERCENTER_INFO_RESP},
                {LS_TRANSIT_LOGON, MSUB_USERCENTER_INFO_FAIL}};
        // 创建协议解析器
        NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
            public boolean doReceive(NetSocketPak netSocketPak) {
                // 解析接受到的网络数据
                try {
                    short sub_gr = netSocketPak.getSub_gr();
                    if (sub_gr == MSUB_USERCENTER_INFO_RESP) {
                        TDataInputStream tdis = netSocketPak
                                .getDataInputStream();
                        UserCenterData userData = new UserCenterData(tdis);
                        UserCenterProvider.getInstance().setUserCenterData(
                                userData);
                        Message msg = mHandler
                                .obtainMessage(HANDLER_USERCENTER_SUCCESS);
                        mHandler.sendMessage(msg);

                    } else if (sub_gr == MSUB_USERCENTER_INFO_FAIL) {
                        TDataInputStream tdis = netSocketPak
                                .getDataInputStream();
                        tdis.readByte(); // byte errorCode ,未使用
                        String errMsg = tdis.readUTFByte();
                        Log.e(TAG, errMsg);
                        mHandler.sendMessage(mHandler.obtainMessage(
                                HANDLER_USERCENTER_FAIL, errMsg));
                    }

                } catch (Exception e) {
                    mHandler.sendMessage(mHandler.obtainMessage(HANDLER_FAIL));
                    e.printStackTrace();
                }
                // 数据处理完成，终止继续解析
                return true;
            }
        };
        // 注册协议解析器到网络数据分发器中
        NetSocketManager.getInstance().addPrivateListener(nPListener);
        // 发送协议
        NetSocketManager.getInstance().sendData(centerInfo);
        // 清理协议对象
        centerInfo.free();
    }

    private void modifyUserInfo(String nickName, byte sexId) {
        // 创建发送的数据包
        int userId = FiexedViewHelper.getInstance().getUserId();
        TDataOutputStream tdos = new TDataOutputStream(false);
        tdos.writeInt(userId);
        tdos.writeByte(sexId);
        tdos.writeUTFByte(nickName);

        NetSocketPak userInfo = new NetSocketPak(LS_TRANSIT_LOGON,
                MSUB_CMD_MODIFY_USERINFO, tdos);
        // 定义接受数据的协议
        short[][] parseProtocol = {{LS_TRANSIT_LOGON, MSUB_CMD_MODIFY_RESULT}};
        // 创建协议解析器
        NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
            public boolean doReceive(NetSocketPak netSocketPak) {
                // 解析接受到的网络数据
                try {
                    TDataInputStream tdis = netSocketPak.getDataInputStream();
                    byte result = tdis.readByte();
                    String errMsg = tdis.readUTFByte();
                    if (result == 1) {// 成功
                        mHandler.sendMessage(mHandler.obtainMessage(
                                HANDLER_MODIFY_USERINFO_SUCCESS, errMsg));
                    } else if (result == 0) {// 失败

                        mHandler.sendMessage(mHandler.obtainMessage(
                                HANDLER_MODIFY_USERINFO_FAILED, errMsg));
                    }
                    //
                } catch (Exception e) {
                    //
                    mHandler.sendMessage(mHandler.obtainMessage(HANDLER_FAIL));
                    e.printStackTrace();
                }
                // 数据处理完成，终止继续解析
                return true;
            }
        };
        // 注册协议解析器到网络数据分发器中
        NetSocketManager.getInstance().addPrivateListener(nPListener);
        nPListener.setOnlyRun(false);
        // 发送协议
        NetSocketManager.getInstance().sendData(userInfo);
    }

    public void openCamera(int position) {

        if (position == 0) {
            // 拍照事件
            // AnalyticsUtils.onClickEvent(this, UC.EVENT_223);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mFile = Util.getOutputMediaFile();
            Uri uri = Uri.fromFile(mFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, FROM_CAMERA);
        } else if (position == 1) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            startActivityForResult(intent, FROM_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_CAMERA && resultCode == Activity.RESULT_OK) {

            Intent in = getImageClipIntent(Uri.fromFile(mFile));

            Message msg = mHandler.obtainMessage(HANDLER_CUT_PICTRUE);
            msg.obj = in;
            mHandler.sendMessageDelayed(msg, 100);
            // startActivityForResult(in, IMAGE_CUT);

        } else if (requestCode == FROM_PHOTO
                && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Intent in = getImageClipIntent(uri);

            Message msg = mHandler.obtainMessage(HANDLER_CUT_PICTRUE);
            msg.obj = in;
            mHandler.sendMessageDelayed(msg, 100);
            // startActivityForResult(in, IMAGE_CUT);
        } else if (requestCode == IMAGE_CUT && data != null) {
            Bitmap bm = data.getParcelableExtra("data");
            if (bm == null) { // 用户剪裁操作取消
                return;
            }

            String para = CenterUrlHelper.getUploadParam();

            StringBuilder sb = new StringBuilder();
            sb.append(AppConfig.UPLOAD_HOST).append('/');
            sb.append(AppConfig.plat_id).append('/');
            sb.append(AppConfig.gameId).append('/');
            sb.append("member/avatar").append('?');
            sb.append("api").append('=').append(para);
            String url = sb.toString();

            new PostAsyncTask(mAct, bm).execute(url);
            // 设置头像
            headImage.setImageBitmap(bm);
            // AnalyticsUtils.onClickEvent(this, UC.EVENT_224);

        }
    }

    private Intent getImageClipIntent(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);// 输出图片大小
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);

        return intent;
    }


    private void listenUploadHeaderImage() {

        // 定义接受数据的协议
        short[][] parseProtocol = {{LS_MDM_PROP,
                LSUB_CMD_UPLOAD_USER_HEAD_REQ}};

        NetPrivateListener nPListener = new NetPrivateListener(parseProtocol) {
            public boolean doReceive(NetSocketPak netSocketPak) {
                TDataInputStream tdis = netSocketPak.getDataInputStream();
                tdis.setFront(false);

                int res = tdis.readInt();  //结果
                Log.v("test", "listenUploadHeaderImage (协议 17 890) res=" + res);

                short len = tdis.readShort();
                String msgStr = tdis.readUTF(len);
                int faceId = tdis.readInt();
                byte n = tdis.readByte();

                if (res == 0) {
                    UserInfo userInfo = HallDataManager.getInstance().getUserMe();
                    userInfo.faceID = faceId;
                    int userId = userInfo.userID;

                    Log.v("test", "listenUploadHeaderImage userId=" + userId + " @faceId=" + faceId + " @faceIdValue=" + n);
//					HeadManager.getInstance().setSelfImageByValue(UserCenterActivity.this,
//							userId, faceId, n);
                }

                Message msg = mHandler.obtainMessage(HANDLER_UPLOAD_HEAD_RESULT);
                msg.arg1 = res;
                msg.arg2 = faceId;
                msg.obj = msgStr;
                mHandler.sendMessage(msg);
                return true;
            }
        };
        // 注册协议解析器到网络数据分发器中
        NetSocketManager.getInstance().addPrivateListener(nPListener);
        nPListener.setOnlyRun(false);

    }


}
