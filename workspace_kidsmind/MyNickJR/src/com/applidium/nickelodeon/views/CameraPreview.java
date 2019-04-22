package com.applidium.nickelodeon.views;


import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


/**
 * Created by colin on 2015/7/1.
 */
public class CameraPreview extends SurfaceView implements Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context) {
        super(context);
        // 安装一个SurfaceHolder.Callback
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT);//for SurfaceView 首次创建会黑屏

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 把预览画面的位置通知摄像头
        try {
            int index = findFrontCamera();
            if (index != -1) {
                mCamera = Camera.open(index);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }

        } catch (Exception e) {
            if (mCamera != null) {
                mCamera.release();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null) {
            // 预览surface不存在
            return;
        }

        // 更改时停止预览
        try {
            mCamera.stopPreview();
        } catch (Exception e) {

        }

        // 在此进行缩放、旋转和重新组织格式
        // 以新的设置启动预览
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    /**
     * 获取前置摄像头
     *
     * @return
     */
    private int findFrontCamera() {
        int cameraCount = Camera.getNumberOfCameras(); // get cameras number
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int index = 0; index < cameraCount; index++) {
            Camera.getCameraInfo(index, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return index;
            }
        }
        return -1;
    }


}