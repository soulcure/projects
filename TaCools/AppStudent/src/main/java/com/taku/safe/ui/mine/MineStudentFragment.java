package com.taku.safe.ui.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.AccountInfoActivity;
import com.taku.safe.R;
import com.taku.safe.TaKuStudentActivity;
import com.taku.safe.TakuApp;
import com.taku.safe.activity.AboutTakuActivity;
import com.taku.safe.activity.ChangePasswordActivity;
import com.taku.safe.activity.FeedBackActivity;
import com.taku.safe.activity.MapActivity;
import com.taku.safe.activity.MySchoolActivity;
import com.taku.safe.activity.UserInfoActivity;
import com.taku.safe.config.Constant;
import com.taku.safe.dialog.ChoiceDialog;
import com.taku.safe.entity.ChoiceDialogItem;
import com.taku.safe.protocol.respond.RespUserInfo;
import com.taku.safe.update.UpdateAppManager;
import com.taku.safe.utils.GlideCircleTransform;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MineStudentFragment extends BasePermissionFragment implements View.OnClickListener {

    private static final String TAG = MineStudentFragment.class.getSimpleName();


    private RoundedImageView imgHeard;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_app_update;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(getContext(), Constant.WECHAT_APPID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }


    @Override
    public void onResume() {
        super.onResume();
        RespUserInfo info = mTakuApp.getUserInfo();
        if (info != null) {
            refreshUserInfo(info);
        } else {
            mTakuApp.reqUserInfo(new TakuApp.UserInfo() {
                @Override
                public void success(RespUserInfo info) {
                    refreshUserInfo(info);
                }
            });
        }
        MobclickAgent.onPageStart(TAG);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }


    private void initView(View view) {
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_app_update = (TextView) view.findViewById(R.id.tv_app_update);
        tv_app_update.setOnClickListener(this);
        if (UpdateAppManager.instance().isHasUpdate()) {
            QBadgeView badgeView = new QBadgeView(mContext);
            Badge badge = badgeView.bindTarget(tv_app_update);
            badge.setBadgeTextSize(12, true);
            badge.setBadgeGravity(Gravity.CENTER | Gravity.START);
            badge.setBadgePadding(5, true);

            badge.setGravityOffset(100, 0, true);
            badge.setBadgeTextColor(ContextCompat.getColor(getContext(), R.color.color_red));
            badge.setBadgeText("NEW");
        }

        view.findViewById(R.id.tv_school).setOnClickListener(this);
        view.findViewById(R.id.tv_share).setOnClickListener(this);
        view.findViewById(R.id.tv_response).setOnClickListener(this);
        view.findViewById(R.id.tv_change_pw).setOnClickListener(this);
        view.findViewById(R.id.tv_about).setOnClickListener(this);
        view.findViewById(R.id.tv_exit).setOnClickListener(this);

        imgHeard = (RoundedImageView) view.findViewById(R.id.cropImageView);
        imgHeard.setOnClickListener(this);
    }


    /**
     * 刷新用户信息
     *
     * @param info
     */
    private void refreshUserInfo(RespUserInfo info) {
        tv_name.setText(info.getName());
        tv_phone.setText(info.getPhoneNo());

        String avatar = info.getAvataUrl();
        if (!TextUtils.isEmpty(avatar) && isAdded()) {
            Glide.with(this)
                    .load(avatar)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imgHeard);
        } else {
            int gender = info.getGender();
            if (gender == 1) {
                imgHeard.setImageResource(R.mipmap.ic_male);
            } else {
                imgHeard.setImageResource(R.mipmap.ic_female);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.cropImageView:
                startActivity(new Intent(mContext, UserInfoActivity.class));
                break;
            case R.id.tv_school:
                Intent intent = new Intent(mContext, MySchoolActivity.class);
                intent.putExtra(MapActivity.SIGN_INFO, mTakuApp.getSignInfo());
                startActivity(intent);
                break;
            case R.id.tv_share:
                shareWechatChoiceDialog();
                break;
            case R.id.tv_response:
                startActivity(new Intent(mContext, FeedBackActivity.class));
                break;
            case R.id.tv_change_pw:
                startActivity(new Intent(mContext, ChangePasswordActivity.class));
                break;
            case R.id.tv_app_update:
                if (UpdateAppManager.instance().isHasUpdate()) {
                    UpdateAppManager.instance().check(getContext(), 1);
                } else {
                    Toast.makeText(getContext(), "当前已经是最新版本", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_about:
                /*String title = getString(R.string.about_us);
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.WEB_TITLE, title);
                intent.putExtra(WebViewActivity.WEB_URL, AppConfig.ABOUT_US);
                startActivity(intent);*/
                startActivity(new Intent(mContext, AboutTakuActivity.class));
                break;
            case R.id.tv_exit:
                exitLogin();
                break;
        }
    }


    /**
     * 退出登录
     */
    private void exitLogin() {
        new AlertDialog.Builder(getContext())
                .setMessage("确定退出登录吗?")
                .setPositiveButton(R.string.btn_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTakuApp.clear();
                                Intent intent = new Intent(mContext, AccountInfoActivity.class);
                                getActivity().startActivityForResult(intent, TaKuStudentActivity.LOGIN_OUT);
                                //getActivity().finish();
                            }
                        })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }


    // WeChat SDK 接口
    private IWXAPI api;
    private static final int THUMB_SIZE = 150;
    private static final int WECHAT_TIMELINE = 0; //分享到朋友圈
    private static final int WECHAT_FRIEND = 1;   //分享到好友

    private void shareWechatChoiceDialog() {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(getContext(), "未安装微信!", Toast.LENGTH_SHORT).show();
            return;
        }

        final ArrayList<ChoiceDialogItem> list = new ArrayList<>();
        list.add(new ChoiceDialogItem("微信朋友圈", WECHAT_TIMELINE));
        list.add(new ChoiceDialogItem("微信好友", WECHAT_FRIEND));

        new ChoiceDialog.Builder(getContext())
                .setList(list)
                .callBack(new ChoiceDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int type = list.get(position).getId();
                        shareWebToWechat(type);
                    }
                })
                .builder()
                .show();
    }


    private void shareWebToWechat(int type) {
        WXWebpageObject webpage = new WXWebpageObject();

        webpage.webpageUrl = Constant.SHARE_URL;   //h5 url
        WXMediaMessage msg = new WXMediaMessage(webpage);

        if (type == WECHAT_TIMELINE) {
            msg.title = getString(R.string.about_title);
            msg.description = getString(R.string.about_title);
        } else {
            msg.title = getString(R.string.app_name);
            msg.description = getString(R.string.about_title);
        }

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("share_web_page");
        req.message = msg;
        if (type == WECHAT_TIMELINE) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }

        api.sendReq(req);
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
