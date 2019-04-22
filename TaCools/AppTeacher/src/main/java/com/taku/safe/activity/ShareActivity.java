package com.taku.safe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.taku.safe.R;
import com.taku.safe.config.Constant;
//import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//import com.tencent.tauth.IUiListener;
//import com.tencent.tauth.Tencent;
//import com.tencent.tauth.UiError;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ShareActivity extends Activity implements OnClickListener {

    public final static String TAG = ShareActivity.class.getSimpleName();

    // WeChat SDK 接口
    private IWXAPI api;

    private int mTargetScene = SendMessageToWX.Req.WXSceneTimeline;
    private static final int THUMB_SIZE = 150;

    // QQ SDK 接口
    //private Tencent mTencent;
    // weibo SDK 接口
    // private WbShareHandler shareHandler;

    private CheckBox mTextCheckbox; //是否分享文本
    private CheckBox mImageCheckbox; //否分享图片

    private EditText et_title, et_text, et_action;
    private EditText et_web_title, et_web_desc, et_web_action, et_web_default;
    private ImageView img_pic;


    private Context mContext;

    /*private class WeboShareCallback implements WbShareCallback {
        @Override
        public void onWbShareSuccess() {
            Toast.makeText(mContext, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWbShareCancel() {
            Toast.makeText(mContext, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWbShareFail() {
            Toast.makeText(mContext, getString(R.string.weibosdk_demo_toast_share_failed), Toast.LENGTH_SHORT).show();
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mContext = this;

        initViews();

        /*shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();*/

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //shareHandler.doResultIntent(intent, new WeboShareCallback());
    }


    /**
     * 用户点击分享按钮，唤起微博客户端进行分享。
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_qq:
                //shareToQzone();
                break;
            case R.id.tv_wechat:
                shareToWechatPic();
                //shareToWechatText();
                break;
            case R.id.tv_weibo:
                /*shareToWeibo(mTextCheckbox.isChecked(),
                        mImageCheckbox.isChecked());*/
                break;
        }
    }


    /**
     * 初始化界面。
     */
    private void initViews() {
        findViewById(R.id.tv_qq).setOnClickListener(this);
        findViewById(R.id.tv_wechat).setOnClickListener(this);
        findViewById(R.id.tv_weibo).setOnClickListener(this);

        mTextCheckbox = (CheckBox) findViewById(R.id.share_text_checkbox);
        mImageCheckbox = (CheckBox) findViewById(R.id.shared_image_checkbox);

        et_title = (EditText) findViewById(R.id.et_title);
        et_text = (EditText) findViewById(R.id.et_text);
        et_action = (EditText) findViewById(R.id.et_action);

        et_web_title = (EditText) findViewById(R.id.et_web_title);
        et_web_desc = (EditText) findViewById(R.id.et_web_desc);
        et_web_action = (EditText) findViewById(R.id.et_web_action);
        et_web_default = (EditText) findViewById(R.id.et_web_default);

        img_pic = (ImageView) findViewById(R.id.img_pic);
    }


    private void shareToWechatText() {
        WXTextObject textObj = new WXTextObject();
        textObj.text = et_title.getText().toString();

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = et_text.getText().toString();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = mTargetScene;

        api.sendReq(req);
    }

    private void shareToWechatPic() {
        api = WXAPIFactory.createWXAPI(this, Constant.WECHAT_APPID);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_share);
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);
    }


    /**
     * 分享到QQ空间
     */
    /*private void shareToQzone() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Constant.QQ_APPID, mContext);
        }
        final Bundle params = new Bundle();

        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, et_title.getText().toString());//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, et_text.getText().toString());//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, et_action.getText().toString());//必填

        ArrayList<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            imageUrls.add("");
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        IUiListener qZoneShareListener = new IUiListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onError(UiError e) {
            }

            @Override
            public void onComplete(Object response) {
                Toast.makeText(mContext, "onComplete: " + response.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        // QZone分享要在主线程做
        mTencent.shareToQzone(this, params, qZoneShareListener);
    }*/


    /**
     * 分享到微博
     */
    /*private void shareToWeibo(boolean hasText, boolean hasImage) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }
        weiboMessage.mediaObject = getWebpageObj();
        shareHandler.shareMessage(weiboMessage, true);

    }*/


    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    /*private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.title = et_title.getText().toString();
        textObject.text = et_text.getText().toString();
        textObject.actionUrl = et_action.getText().toString();
        return textObject;
    }*/


    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    /*private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap bitmap = drawableToBitmap(img_pic.getDrawable());

        imageObject.setImageObject(bitmap);
        return imageObject;
    }*/

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */

    /*private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = et_web_title.getText().toString();
        mediaObject.description = et_web_desc.getText().toString();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。

        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = et_web_action.getText().toString();
        mediaObject.defaultText = et_web_default.getText().toString();
        return mediaObject;
    }*/


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
