package com.ivmall.android.app.uitls;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by colin on 2016/4/1.
 */
public class OOSUtils {
    public final static String TAG = OOSUtils.class.getSimpleName();

    private OSS oss;


    public OOSUtils(Context context, String endpoint, String accessKeyId, String accessKeySecret) {

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(context.getApplicationContext(), endpoint, credentialProvider, conf);
    }


    /**
     * 从本地文件上传，使用非阻塞的异步接口
     *
     * @param bucket
     * @param subObject
     * @param uploadFilePath
     */
    public void asyncPutObject(String bucket, final String subObject, String uploadFilePath, final OOSListener listener) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, subObject, uploadFilePath);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                android.util.Log.d(TAG, "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (listener != null)
                    listener.onSuccess(subObject);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (listener != null)
                    listener.onFailure();
            }
        });
        // task.cancel(); // 可以取消任务
        task.waitUntilFinished(); // 可以等待任务完成
    }


    /**
     * 下载文件
     *
     * @param bucket
     * @param subObject
     */
    public void asyncGetObject(String bucket, String subObject) {

        GetObjectRequest get = new GetObjectRequest(bucket, subObject);

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();

                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        Log.d("asyncGetObjectSample", "read length: " + len);
                    }
                    Log.d("asyncGetObjectSample", "download success.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

        // task.cancel(); // 可以取消任务
        task.waitUntilFinished(); // 可以等待任务完成
    }


    public interface OOSListener {
        void onSuccess(String path);

        void onFailure();
    }
}
