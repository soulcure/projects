package com.taku.safe.utils;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.taku.safe.config.AppConfig;
import com.taku.safe.config.FileConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 录音
 */
public class VoiceUtils {

    private static final int POST_AUDIO = 0;
    private static final int START_RECORD = 1;

    private static final int RECORD_TIME = 60;//默认录音时长，单位秒

    //private static final String AUDIO_FORMAT = ".amr";
    private static final String AUDIO_FORMAT = ".m4a";

    private String audioPath;

    private Context mContext;
    private String mToken;
    private int mSosId;

    private boolean isStop = false;

    private WorkHandler mHandler;
    private MediaRecorder mMediaRecorder;

    public VoiceUtils(Context context, String token, int sosId) {
        mContext = context;
        mToken = token;
        mSosId = sosId;
        mHandler = new WorkHandler(this);
        mMediaRecorder = new MediaRecorder();
    }


    public void setStop(boolean stop) {
        isStop = stop;
    }

    //使用okhttp提交多表单
    private void reportSosVoice(String path, int duration) {
        String url = AppConfig.SOS_REPORT_VOICE;
        ContentValues header = new ContentValues();
        header.put("token", mToken);

        Map<String, Object> params = new HashMap<>();
        params.put("sosId", mSosId);

        File file = new File(path);
        if (file.exists()) {
            params.put("voice", file);
            if (duration == 0) {
                MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(path));
                duration = mp.getDuration() / 1000;
            }
            params.put("duration", duration);
        } else {
            return;
        }

        OkHttpConnector.httpPostMultipart(url, header, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean bean = GsonUtil.parse(response, RespBaseBean.class);
                if (bean != null && bean.isSuccess()) {
                    Toast.makeText(mContext, "实时上传录音成功", Toast.LENGTH_SHORT).show();

                    if (!mHandler.hasMessages(START_RECORD) && !isStop) {
                        mHandler.sendEmptyMessageDelayed(START_RECORD, 500);
                    }
                }
            }
        });
    }


    /**
     * 开始录音
     * AMR_NB AMR NB file format
     * AMR_WB AMR WB file format
     * MPEG_4 MPEG4 media file format
     * THREE_GPP 3GPP media file format
     */
    public void startRecord() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        audioPath = getVoicePath();
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //.m4a

            mMediaRecorder.setOutputFile(audioPath);
            mMediaRecorder.setMaxDuration(RECORD_TIME * 1000);  //最长录音60秒
            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        stopRecord(true, RECORD_TIME);
                    }
                }
            });

            mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    stopRecord(false, 0);
                }
            });


            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 结束录音
     */
    public void stopRecord(boolean isSend, int time) {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isSend) {
            if (!mHandler.hasMessages(POST_AUDIO)) {
                Message msg = mHandler.obtainMessage(POST_AUDIO);
                msg.obj = audioPath;
                msg.arg1 = time;
                mHandler.sendMessage(msg);
            }
        }

    }

    /**
     * 创建录音文件名
     *
     * @return 文件路径
     */
    private String getVoicePath() {
        return FileConfig.getAudioDownLoadPath() + "/" + System.currentTimeMillis() / 1000 + AUDIO_FORMAT;
    }


    private static class WorkHandler extends Handler {

        private final WeakReference<VoiceUtils> mTarget;

        WorkHandler(VoiceUtils target) {
            mTarget = new WeakReference<>(target);
        }


        @Override
        public void handleMessage(Message msg) {
            VoiceUtils voice = mTarget.get();
            switch (msg.what) {
                case POST_AUDIO:
                    String path = (String) msg.obj;
                    int time = msg.arg1;
                    if (!TextUtils.isEmpty(path)) {
                        voice.reportSosVoice(path, time);
                    }
                    break;
                case START_RECORD:
                    voice.startRecord();
                    break;

            }
        }
    }
}
