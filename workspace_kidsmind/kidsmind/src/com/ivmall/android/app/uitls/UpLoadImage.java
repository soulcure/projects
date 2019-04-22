package com.ivmall.android.app.uitls;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.OOSRequest;
import com.ivmall.android.app.entity.OOSResponse;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by colin on 2016/4/8.
 */
public class UpLoadImage {
    //handler what
    private static final int HANDLER_CUT_PICTRUE = 0; //剪裁图片

    private static final int CUT_PICTURE_SIZE = 150;// 剪裁图片尺寸大小

    //start activity result
    public final static int FROM_CAMERA = 0;
    public final static int FROM_PHOTO = 1;
    public final static int IMAGE_CUT = 2;

    private Activity mAct;

    private Uri fileUri;


    private MainHandler mHandler;
    private OOSUtils.OOSListener mListener;


    public UpLoadImage(Activity activity) {
        mAct = activity;

        if (mHandler == null)
            mHandler = new MainHandler(activity);
        else
            mHandler.setTarget(activity);

    }


    /**
     * 相册中选择上传的图片
     */
    public void pickImage(OOSUtils.OOSListener listener, int positon) {
        mListener = listener;

        if (positon == FROM_CAMERA) {
            capturePicture();
        } else if (positon == FROM_PHOTO) {
            pickPhoto();
        }

    }


    public void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        mAct.startActivityForResult(intent, FROM_PHOTO);
    }


    public Uri getFileUri() {
        return fileUri;
    }

    public void capturePicture() {
        // 拍照事件
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = ImageUtils.getOutputMediaFile();
        fileUri = Uri.fromFile(file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        mAct.startActivityForResult(intent, FROM_CAMERA);

    }


    /**
     * 剪裁图片
     *
     * @param uri 图片资源地址
     * @return Intent
     */
    private Intent getImageClipIntent(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CUT_PICTURE_SIZE);// 输出图片大小
        intent.putExtra("outputY", CUT_PICTURE_SIZE);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);

        return intent;
    }

    /**
     * 1.93 获取上传文件到OSS的STS凭证
     */
    public void repOosInfo(final String path) {
        String url = AppConfig.ASSUME_ROLE;
        OOSRequest request = new OOSRequest();
        String token = ((KidsMindApplication) mAct.getApplication()).getToken();

        request.setToken(token);
        request.setAliyunApi(OOSRequest.ApiType.postObject.toString());
        request.setType(OOSRequest.ImageType.head_img.toString());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                OOSResponse res = GsonUtil.parse(response, OOSResponse.class);
                if (res != null && res.isSucess()) {
                    String endpoint = res.getData().getEndPoint();
                    String accessKeyId = res.getData().getAccessKeyId();
                    String accessKeySecret = res.getData().getAccessKeySecret();
                    String bucket = res.getData().getBucket();
                    String subObject = res.getData().getSubObject();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
                    int profileId = ((KidsMindApplication) mAct.getApplication()).getProfileId();
                    subObject += "/headImage/" + profileId + timeStamp + ".jpg";

                    OOSUtils oosUtils = new OOSUtils(mAct, endpoint, accessKeyId, accessKeySecret);
                    oosUtils.asyncPutObject(bucket, subObject, path, mListener);
                }
            }
        });
    }


    public void cutPictrue(Uri uri) {
        Intent in = getImageClipIntent(uri);
        Message msg = mHandler.obtainMessage(HANDLER_CUT_PICTRUE);
        msg.obj = in;
        mHandler.sendMessageDelayed(msg, 100);
    }


    private static class MainHandler extends Handler {
        private WeakReference<Activity> mTarget;

        MainHandler(Activity target) {
            mTarget = new WeakReference<Activity>(target);
        }

        public void setTarget(Activity target) {
            mTarget.clear();
            mTarget = new WeakReference<Activity>(target);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Activity activity = mTarget.get();
            switch (msg.what) {
                case HANDLER_CUT_PICTRUE:// 裁剪图片
                    Intent in = (Intent) msg.obj;
                    activity.startActivityForResult(in, IMAGE_CUT);
                    break;

                default:
                    break;
            }
        }

    }
}
