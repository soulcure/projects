package com.ivmall.android.app.player;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.debug.CrashHandler;
import com.ivmall.android.app.dialog.AnswerQuestionDialog;
import com.ivmall.android.app.dialog.BuyVipDialog;
import com.ivmall.android.app.dialog.DoYouLikeDialog;
import com.ivmall.android.app.dialog.PlayPauseDialog;
import com.ivmall.android.app.dialog.SeekbarTimeDialog;
import com.ivmall.android.app.dialog.TimeOverDialog;
import com.ivmall.android.app.entity.CancelFavRequest;
import com.ivmall.android.app.entity.HeartBeatRequest;
import com.ivmall.android.app.entity.HeartBeatResponse;
import com.ivmall.android.app.entity.PlayUrlInfo;
import com.ivmall.android.app.entity.PlayUrlRequest;
import com.ivmall.android.app.entity.PlayUrlResponse;
import com.ivmall.android.app.entity.RecommendItem;
import com.ivmall.android.app.entity.SmartRecommendRequest;
import com.ivmall.android.app.entity.SmartRecommendResponse;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.service.MediaPlayerService;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.CustomDigitalClock;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SmartPlayingActivity extends Activity implements OnClickListener, View.OnLongClickListener {

    public static final String TAG = SmartPlayingActivity.class.getSimpleName();

    private static final int UPDATE_CURRPOSITION_DELAY_TIME = 200;


    private Context mContext;
    /**
     * **UI控件***
     */

    private VideoView mVideoView = null;
    private BuyVipDialog mBuyVipDialog;
    private TextView mSpeed;
    private TextView tvEpisodeName;
    private LinearLayout mController = null;
    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;
    private CustomDigitalClock mPlaytime = null;
    
    private TextView btnLang; //语言
    private ImageButton mPlaybtn; //暂停
    private ImageButton btnCollect;  //收藏

    private LinearLayout linearMainTitle;
    private TextView tvCartoonName;
    private TextView tvPrefOne;
    private TextView tvPrefTwo;
    private TextView tvPrefThree;

    private TextView tvSerie;
    private TextView tvEpisode;

    private ImageButton btnLock;
    private ImageButton btnUnLock;
    private ImageButton btnBack;
    private TextView btnQuality;

    TimeOverDialog timeOverDialog;

    private boolean isDialogShow = false;

    private boolean isLocked = false;  //设置一个状态值，用于锁屏


    private List<RecommendItem> mSmartPlayList = new ArrayList<RecommendItem>();

    private int playListIndex = -1;


    private int mPlayingEpisodeId;
    private String mVideoSource = null;

    private int langIndex = 0;  //语言选项
    private int qualityIndex = 0;
    private int spareIndex = 0; //切换备用视频播放地址
    private PlayUrlInfo mPlayUrlInfo;


    private PlayerReportModel mReportModel;

    private GestureDetector mGestureDetector; //用于手势处理
    private final int FLING_MIN_DISTANCE = 1 * 150;  //判断滑动时，设置的最小滑动距离
    private final int FLING_MIN_VELOCITY = 2 * 100;  //判断滑动时，设置的最小滑动速度
    private final int PLAYER_SEEK_TIME = 5 * 1000; //设置播放器向前或向后跳5s

    /**
     * 记录播放位置
     */
    private int mLastPos = 0;   //最后播放的位置
    private int mHeadPos = 0;
    private boolean isFavorite;  //是否是收藏剧集


    private PlayUIHandler mUIHandler;


    private final int EVENT_PLAY = 0;
    private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
    private final int UI_EVENT_FULL_SCREEN = 2; // full screen
    private final int UI_EVENT_HALF_SCREEN = 3; // half screen
    private final int UI_EVENT_LOADING_PROGRESS = 4; // 更新缓存进度
    private final int UI_EVENT_LOADING_NEXT = 5; // 加载下一个节目
    private final int UI_EVENT_SHOW_PUPUP = 6; // show pup up
    private final int UI_EVENT_REPORT_PLAY = 7; // 播放汇报
    private final int UI_EVENT_HIDE_PUPUP = 8; // hide pup up
    private final int REPORT_EVENT_ADD_TOTAL_TIME = 9; // 统计总播放时长
    private final int UI_EVENT_SHOW_ADV = 10; // 显示广告
    private final int UI_EVENT_SHOW_DOWNLOAD_SPEED = 11; // 显示缓存速度

    private final int UI_EVENT_SHOW_NEXT = 20; // 加载下一个节目
    private final int UI_EVENT_HIDE_UNLOCK_BUTTON = 21;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_playing_activity);
        ((KidsMindApplication) getApplicationContext()).reqUserInfo();//刷新用户VIP信息

        String quality = AppUtils.getStringSharedPreferences(this, "Quality", PlayUrlInfo.HD);
        if (quality.equals(PlayUrlInfo.FHD)) {
            qualityIndex = 1;
        }

        mContext = this;
        mUIHandler = new PlayUIHandler(this);
        mReportModel = new PlayerReportModel(this);   //播放汇报

        initView();

        initAndroidPlayer();
        initPlayTimeControl();

        int profileId = ((KidsMindApplication) getApplication()).getProfileId();
        if (profileId == 0) {
            ((KidsMindApplication) getApplication()).reqProfile(new OnReqProfileResult());
        } else {
            String record = AppUtils.getStringSharedPreferences(mContext, "PLAY_RECORD", "");
            if (!StringUtils.isEmpty(record)) {
                String[] strs = record.split("#");
                if (strs != null && strs.length == 4) {
                    int episodeId = Integer.parseInt(strs[0]);
                    mLastPos = Integer.parseInt(strs[1]);
                    isFavorite = Boolean.parseBoolean(strs[2]);
                    langIndex = Integer.parseInt(strs[3]);
                    if (isFavorite) {
                        btnCollect.setImageResource(R.drawable.btn_free_favorited);
                    } else {
                        btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                    }
                    reqPlayUrl(episodeId);
                    reqSmartRecommend(true);
                }

            } else {
                reqSmartRecommend(false);
            }


        }


        //语言提示
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("media", MediaPlayerService.BEFORE);
        startService(intent);
    }

    private class OnReqProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            int episodeId = getIntent().getIntExtra("EpisodeId", 0);
            reqPlayUrl(episodeId);
        }


        @Override
        public void create() {

        }

        @Override
        public void fail() {

        }
    }


    @Override
    public void onResume() {
        super.onResume();

        hideBuyVipDialog();

        /**
         * 继续原来的播放
         */
        if (mBuyVipDialog != null && mBuyVipDialog.isShowing()) {
            if (mReportModel != null && !mReportModel.hasSession()) {
                mReportModel.genSession();
            }
        } else if (mLastPos != 0
                && !StringUtils.isEmpty(mVideoSource) && (timeOverDialog == null || !timeOverDialog.isShowing())) {

            playingStart();

            showControlBar();
            startTimer();
        }

        BaiduUtils.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        playingPause();

        stopService(new Intent(this, MediaPlayerService.class));

        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        stopPlayer(true);
        mReportModel.report();
        stopTimer();

        BaiduUtils.onPause(this);
    }


    @Override
    public void onStop() {
        super.onStop();

    }


    private void initPlayTimeControl() {

        mPlaytime = new CustomDigitalClock(this);
        mPlaytime.setTimeOutListener(new CustomDigitalClock.TimeOutListener() {
            @Override
            public void timeOut() {
                playingPause();
                timeOverDialog = new TimeOverDialog(mContext);
                try {
                    timeOverDialog.show();
                    isDialogShow = true;
                } catch (WindowManager.BadTokenException e) {

                }
                timeOverDialog.setOnDismissListener(
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                setNoLimit();
                            }
                        });
            }
        });

    }


    private void initAndroidPlayer() {

        /**
         * 注册listener
         */
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            /**
             * 准备播放就绪
             */
            @Override
            public void onPrepared(MediaPlayer mp) {

                if (mLastPos > 0) {
                    mVideoView.seekTo(mLastPos);
                    mLastPos = 0;
                } else {

                    if (mHeadPos > 0 && isSkip()) {
                        mVideoView.seekTo(mHeadPos * 1000);
                        mHeadPos = 0;
                        Toast.makeText(mContext, R.string.skip_head, Toast.LENGTH_SHORT).show();
                    }

                }
                mVideoView.start();  //开始播放
                mPlaytime.start(); //开始记时

                mReportModel.clear();
                mReportModel.playPrepared();


                if (!mUIHandler.hasMessages(REPORT_EVENT_ADD_TOTAL_TIME)) {
                    mUIHandler.sendEmptyMessageDelayed(
                            REPORT_EVENT_ADD_TOTAL_TIME, UPDATE_CURRPOSITION_DELAY_TIME);
                }

                if (linearMainTitle.getVisibility() == View.VISIBLE) {
                    linearMainTitle.setVisibility(View.GONE);
                }

                if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                    mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
                }

                if (!mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_FULL_SCREEN, 5000);
                }
                startTimer();

                stopSpeedTimer();

            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            /**
             * 播放完成
             */
            @Override
            public void onCompletion(MediaPlayer mp) {
                //mp.reset();  //酷开 android 5.0 不能自动下一集

                if (spareIndex > 0) {
                    spareIndex = 0;
                }

                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

                Message msg = mUIHandler.obtainMessage(UI_EVENT_LOADING_NEXT);
                mUIHandler.sendMessage(msg);

                //系统播放器只有在播放完成后才会回掉
                mReportModel.report();
                mUIHandler.removeMessages(REPORT_EVENT_ADD_TOTAL_TIME);

                stopTimer();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            /**
             * 播放出错
             */
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (extra == MediaPlayer.MEDIA_ERROR_IO
                        || extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {

                    String url = mPlayUrlInfo.getPlayUrl();//获取播放出错的视频地址
                    CrashHandler.getInstance().saveVideoSourceErrorToFile(url);  //保存播放出错的视频地址。汇报到服务器

                    if (spareIndex > 1) {
                        Toast.makeText(mContext, getString(R.string.playing_error), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    mLastPos = getCurrentPosition();
                    String source = mPlayUrlInfo.getSparePlayUrl();  //获取备用播放地址
                    playVideo(source, mPlayingEpisodeId);  //备用播放地址请求播放
                    spareIndex++;

                    Toast.makeText(mContext, getString(R.string.net_timeout), Toast.LENGTH_SHORT).show();
                } else if (extra == MediaPlayer.MEDIA_ERROR_UNKNOWN
                        || extra == MediaPlayer.MEDIA_ERROR_SERVER_DIED
                        || extra == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK
                        || extra == MediaPlayer.MEDIA_ERROR_MALFORMED
                        || extra == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {

                    String url = mPlayUrlInfo.getPlayUrl();
                    CrashHandler.getInstance().saveVideoSourceErrorToFile(url);  //保存不会出错的视频地址

                    Toast.makeText(mContext, getString(R.string.playing_error), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "MediaPlayer onError and what=" + what + "& extra=" + extra);
                }

                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= 17) {
            mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START: //开始缓冲
                            mReportModel.loadingStart();

                            if (mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                                mReportModel.pauseTimesByLoading();
                            }

                            startSpeedTimer();
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END: //结束缓冲
                            mReportModel.loadingEnd();
                            mReportModel.seekPauseEnd();
                            stopSpeedTimer();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }

    }


    /**
     * 判断是否跳过片头
     *
     * @return
     */
    private boolean isSkip() {
        boolean isSkip = false;
        if (mPlayUrlInfo != null) {
            isSkip = mPlayUrlInfo.isSkip();
        } else {
            isSkip = PlaySettingFragment.isSkipHead(mContext);
        }
        return isSkip;
    }


    /**
     * 保存播放状态
     *
     * @param isSavePosition
     */
    private void stopPlayer(boolean isSavePosition) {
        if (isSavePosition) {
            //记录当前播放的位置,以便以后可以续播
            mLastPos = getCurrentPosition();
            if (mPlayingEpisodeId > 0) {
                AppUtils.setStringSharedPreferences(mContext, "PLAY_RECORD",
                        mPlayingEpisodeId + "#" + mLastPos + "#" + isFavorite + "#" + langIndex);
            }
        }

    }


    private void callPlayVideo() {
        mVideoView.stopPlayback();
        /**
         * 设置播放url
         */
        mVideoView.setVideoPath(mVideoSource);
        startSpeedTimer();
        mReportModel.playStart();
    }


    private int getCurrentPosition() {

        return mVideoView.getCurrentPosition();

    }

    private int getDuration() {

        return mVideoView.getDuration();

    }


    /**
     * 如果在播放状态，暂停播放
     */
    private void playingPause() {
        if (mVideoView.isPlaying()) {

            mPlaybtn.setImageResource(R.drawable.btn_free_start);
            /**
             * 暂停播放
             */
            mVideoView.pause();
            if (mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
                mUIHandler.removeMessages(UI_EVENT_FULL_SCREEN);
            }

            mPlaytime.pause(); //暂停记时
            mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

        }

    }

    /**
     * 如果在暂停状态，播放
     */
    private void playingStart() {
        if (!mVideoView.isPlaying()) {

            mPlaybtn.setImageResource(R.drawable.btn_free_pause);
            /**
             * 继续播放
             */
            mVideoView.start();
            mPlaytime.start(); //开始记时

            if (mReportModel != null && !mReportModel.hasSession()) {
                mReportModel.genSession();
            }

            if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION))
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
        }


    }

    /**
     * 如果在播放->暂停 如果在暂停->播放
     */
    private void playingStartOrPause() {
        if (mVideoView.isPlaying()) {
            playingPause();
            mReportModel.pauseStart();
            mReportModel.userPauseTimes();
            BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_PAUSE, getString(R.string.smart_pause));
        } else {
            playingStart();
            mReportModel.pauseEnd();
            BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_PLAY, getString(R.string.smart_play));
        }


    }


    /**
     * 判断是否在播放
     *
     * @return
     */
    public boolean isPlaying() {
        boolean res = false;
        if (mVideoView != null && mVideoView.isPlaying()) {
            res = true;
        }
        return res;
    }

    /**
     * 判断是否在播放暂停状态
     *
     * @return
     */
    public boolean isPlayingPause() {
        boolean res = false;
        if (mVideoView.isActivated()
                && !isPlaying()) {
            res = true;
        }
        return res;
    }


    private void seekPos(int pos) {

        mVideoView.seekTo(pos);

        mReportModel.seekPauseStart();


        BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_SEEK, getString(R.string.smart_seek));
    }

    /**
     * 初始化控件
     */
    private void initView() {

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mSpeed = (TextView) findViewById(R.id.speed);
        tvEpisodeName = (TextView) findViewById(R.id.tv_episode_name);
        findViewById(R.id.btn_share).setVisibility(View.GONE);


        linearMainTitle = (LinearLayout) findViewById(R.id.linear_main_title);

        tvCartoonName = (TextView) linearMainTitle.findViewById(R.id.tv_cartoon_name);
        tvPrefOne = (TextView) linearMainTitle.findViewById(R.id.tv_pref_one);
        tvPrefTwo = (TextView) linearMainTitle.findViewById(R.id.tv_pref_two);
        tvPrefThree = (TextView) linearMainTitle.findViewById(R.id.tv_pref_three);
        btnLock = (ImageButton) findViewById(R.id.btn_play_lock);
        btnUnLock = (ImageButton) findViewById(R.id.btn_smartplay_unlock);
        btnBack = (ImageButton) findViewById(R.id.btn_play_return);

        btnLock.setOnClickListener(this);
        btnUnLock.setOnLongClickListener(this);
        btnBack.setOnClickListener(this);

        //findViewById(R.id.btn_home).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        btnQuality = (TextView) findViewById(R.id.btn_quality);
        btnQuality.setOnClickListener(this);
        if (qualityIndex == 1) {
            btnQuality.setText(R.string.text_chaoqing);
        }
        tvSerie = (TextView) findViewById(R.id.tv_serie);
        tvEpisode = (TextView) findViewById(R.id.tv_episode);

        // 收藏
        btnCollect = (ImageButton) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);

        mPlaybtn = (ImageButton) findViewById(R.id.btn_play);
        mPlaybtn.setOnClickListener(this);

        btnLang = (TextView) findViewById(R.id.btn_lang);
        btnLang.setOnClickListener(this);

        mController = (LinearLayout) findViewById(R.id.controlbar);
        mProgress = (SeekBar) findViewById(R.id.media_progress);
        mProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress.requestFocus();
            }
        }, 200);

        mDuration = (TextView) findViewById(R.id.time_total);
        mCurrPostion = (TextView) findViewById(R.id.time_current);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        registerCallbackForControl();

    }

    /**
     * 为控件注册回调处理函数
     */
    private void registerCallbackForControl() {
        mProgress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {

                        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

                        if (mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
                            mUIHandler.removeMessages(UI_EVENT_FULL_SCREEN);
                        }
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        int iseekPos = ((SeekBar) v).getProgress();

                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {  //用户倒退
                            mReportModel.userSeekBackward();
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            mReportModel.userSeekForward();
                        }

                        /**
                         * SeekBark完成seek时执行seekTo操作并更新界面
                         *
                         */
                        seekPos(iseekPos);
                        Log.v(TAG, "seek to " + iseekPos);
                        mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
                    }
                }
                return false;
            }
        });

        mProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (!fromUser) { //fromUser == false //表示为系统更新进度条
                    //5秒后，自动全屏
                    if (isBarShow()
                            && !mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
                        mUIHandler.sendEmptyMessageDelayed(UI_EVENT_FULL_SCREEN,
                                5000);
                    }
                    return;
                }

                int curPlayTime = getCurrentPosition();

                //SeekBark完成seek时,执行seekTo操作
                int iseekPos = seekBar.getProgress();
                Log.v(TAG, "seek to " + iseekPos);

                if (iseekPos < curPlayTime) {  //用户倒退
                    mReportModel.userSeekBackward();
                } else { //用户快进
                    mReportModel.userSeekForward();
                }

                seekPos(iseekPos);
                //SeekBark完成seek时, 更新界面
                updateTextViewWithTimeFormat(mCurrPostion, progress);


            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                /**
                 * SeekBar开始，seek时停止更新
                 */
                mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                /**
                 * SeekBar结束，seek开始更新
                 */
                mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
            }
        });

    }

    private void updateTextViewWithTimeFormat(TextView view, int second) {

        second /= 1000;

        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        view.setText(strTemp);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
            mUIHandler.removeMessages(UI_EVENT_FULL_SCREEN);
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isLocked) {
                //锁屏状态下，按后退键无效。
            } else if (mController.getVisibility() == View.VISIBLE) {
                mController.setVisibility(View.GONE);
                btnLock.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
            } else {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            playingStartOrPause();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mController.getVisibility() == View.GONE) {
                showControlBar();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
            mUIHandler.removeMessages(UI_EVENT_FULL_SCREEN);
        } else if (mUIHandler.hasMessages(UI_EVENT_HIDE_UNLOCK_BUTTON)) {
            mUIHandler.removeMessages(UI_EVENT_HIDE_UNLOCK_BUTTON);
        }
        return super.dispatchTouchEvent(ev);
    }

    private BuyVipDialog getmBuyVipDialog(BuyVipDialog.VIP_TYPE type) {
        if (mBuyVipDialog == null) {
            mBuyVipDialog = new BuyVipDialog(mContext, type);
        } else {
            mBuyVipDialog.setVipType(type);
        }
        return mBuyVipDialog;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_collect) {
            if (isFavorite) {
                reqRemoveFavorite(new int[]{mPlayingEpisodeId});
                BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_REMOVE_FAVORITE, getString(R.string.smart_remove_favorite));
            } else {
                reqAddFavorite(mPlayingEpisodeId);
                BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_ADD_FAVORITE, getString(R.string.smart_add_favorite));
            }

        } else if (id == R.id.btn_play) {
            playingStartOrPause();
            if (!AppUtils.isRepeatClick()) {
                //延迟发送 展示广告的信息
                Message msg = new Message();
                msg.what = UI_EVENT_SHOW_ADV;
                mUIHandler.sendMessage(msg);
            }
        } else if (id == R.id.tv_time) {

            setPlayTime();
            BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_SET_PLAYTIME, getString(R.string.smart_set_playtime));
        } else if (id == R.id.btn_next) {
            mVideoView.pause();
            int currPosition = getCurrentPosition();
            int duration = getDuration();
            showDoYouLike(currPosition, duration);
            BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_NEXT, getString(R.string.smart_next));
        } else if (id == R.id.btn_lang) {

            if (!((KidsMindApplication) getApplication()).isVip()) {
                getmBuyVipDialog(BuyVipDialog.VIP_TYPE.VIP_TRIAL).show();
                playingPause();
                mBuyVipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        boolean isVip = ((KidsMindApplication) getApplication()).isVip();
                        if (isVip) {
                            switchLang();
                        } else {
                            playingStart();
                        }
                    }
                });
                return;
            }
            if (!mPlayUrlInfo.hasEnglish()) {
                return;
            }

            switchLang();

        } else if (id == R.id.btn_play_lock) {
            isLocked = true;
            setUnLockButtonVisible();
            fullScreenPlaying();
        } else if (id == R.id.btn_play_return) {
            finish();
        } else if (id == R.id.btn_quality) {
            mLastPos = getCurrentPosition();  //记录播放位置
            mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
            qualityIndex++;
            setQuality(mPlayUrlInfo);

            String source = mPlayUrlInfo.getPlayUrl();
            playVideo(source, mPlayingEpisodeId);

            /*String lang = setLang(mPlayUrlInfo);  //切换清晰度，取消汇报
            int profileId = ((KidsMindApplication) getApplication()).getProfileId();
            mReportModel.init(profileId, mPlayingEpisodeId, lang, mPlayUrlInfo.getBehaviorPlayId());*/

            mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_smartplay_unlock) {
            isLocked = false;
            halfScreenPlaying();
            btnUnLock.setVisibility(View.GONE);
        }

        return false;
    }

    private void setUnLockButtonVisible() {
        btnUnLock.setVisibility(View.VISIBLE);
        mUIHandler.sendEmptyMessageDelayed(UI_EVENT_HIDE_UNLOCK_BUTTON, 4000); //4秒后自动隐藏
    }


    /**
     * 切换语言
     */
    private void switchLang() {
        langIndex++;
        String lang = setLang(mPlayUrlInfo);
        BaiduUtils.onEvent(getApplicationContext(), OnEventId.SMART_CHINESE, getString(R.string.smart_chinese));

        mLastPos = getCurrentPosition();  //记录播放位置
        String source = mPlayUrlInfo.getPlayUrl();
        playVideo(source, mPlayingEpisodeId);

        /*int profileId = ((KidsMindApplication) getApplication()).getProfileId();  //切换语言，取消汇报
        mReportModel.init(profileId, mPlayingEpisodeId, lang, mPlayUrlInfo.getBehaviorPlayId());*/

        mReportModel.setAudioLang(lang);

        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

    }

    private void showControlBar() {
        Animation scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        // 动画集
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        mController.setVisibility(View.VISIBLE);
        tvEpisodeName.setVisibility(View.VISIBLE);
        btnLock.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);

        // 设置动画时间 (作用到每个动画)
        set.setDuration(200);
        mController.startAnimation(set);
        mProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress.requestFocus();
            }
        }, 200);
    }


    /**
     * 全屏播放
     */
    private void fullScreenPlaying() {
        mController.setVisibility(View.GONE);
        tvEpisodeName.setVisibility(View.GONE);
        btnLock.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
    }

    private void halfScreenPlaying() {
        mController.setVisibility(View.VISIBLE);
        tvEpisodeName.setVisibility(View.VISIBLE);
        btnLock.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
    }

    /**
     * 播放控制条是否在显示状态 不在显示状态 表示为全屏
     *
     * @return
     */
    private boolean isBarShow() {
        boolean res = false;
        if (mController.getVisibility() == View.VISIBLE) {
            res = true;
        }
        return res;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 退出后台事件处理线程
         */
        //mUIHandler.removeMessages(UI_EVENT_REPORT_PLAY);
        mUIHandler.removeMessages(REPORT_EVENT_ADD_TOTAL_TIME);
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
        mUIHandler.removeMessages(UI_EVENT_SHOW_PUPUP);
        mUIHandler.removeMessages(UI_EVENT_HIDE_PUPUP);
        mUIHandler.removeMessages(UI_EVENT_SHOW_ADV);
        mUIHandler = null;

    }


    /**
     * 发起播放
     *
     * @param source    资源url
     * @param episodeId 保存正在播放的id
     */
    private void playVideo(String source, int episodeId) {
        if (mUIHandler == null) {
            return;
        }

        if (StringUtils.isEmpty(source) || mUIHandler == null) {
            Toast.makeText(mContext, R.string.play_url_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        mVideoSource = source;
        if (mPlayingEpisodeId != episodeId) {
            mPlayingEpisodeId = episodeId;
        }

        mReportModel.setPlayUrl(source);

        /**
         * 发起一次新的播放任务
         */
        if (mUIHandler.hasMessages(EVENT_PLAY))
            mUIHandler.removeMessages(EVENT_PLAY);
        mUIHandler.sendEmptyMessage(EVENT_PLAY);
    }


    /**
     * 提示帮助信息
     *
     * @param position
     * @param title
     */
    private void showPopupWindow(View position, String title) {
        if (mUIHandler == null || position.getVisibility() == View.GONE) return;


        int count = AppUtils.getIntSharedPreferences(mContext, "POPUP", 0);
        if (count > 100) {
            return;
        } else {
            count++;
            AppUtils.setIntSharedPreferences(mContext, "POPUP", count);
        }

        TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.popup_bg_info);
        textView.setGravity(Gravity.CENTER);
        textView.setText(title);
        textView.setTextColor(getResources().getColor(R.color.white));
        final PopupWindow popupWindow = new PopupWindow(position);
        int w = (int) ScreenUtils.dpToPx(mContext, 60);
        int h = (int) ScreenUtils.dpToPx(mContext, 40);
        popupWindow.setWidth(w);
        popupWindow.setHeight(h);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(textView);

        try {
            popupWindow.showAsDropDown(position, 0, 0);
        } catch (WindowManager.BadTokenException e) {

        }

        Message msg = mUIHandler.obtainMessage(UI_EVENT_HIDE_PUPUP);
        msg.obj = popupWindow;
        mUIHandler.sendMessageDelayed(msg, 1500);

    }


    /**
     * 设置播放时间
     */
    private void setPlayTime() {
        playingPause();
        AnswerQuestionDialog questionDialog = new AnswerQuestionDialog(mContext);
        questionDialog.show();
        questionDialog.setRightAnswerListener(new AnswerQuestionDialog.RightAnswerListener() {
            @Override
            public void doing() {
                SeekbarTimeDialog seekDialog = new SeekbarTimeDialog(mContext);
                seekDialog.show();
                seekDialog.setOnDismissListener(
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                playingStart();
                                String value = ((SeekbarTimeDialog) dialog).getSelectValue();

                                if (((SeekbarTimeDialog) dialog).isParentsSettingTime()) {
                                    int min = Integer.parseInt(value);
                                    if (min == 0) {
                                        mPlaytime.setLimited(false);
                                    } else {
                                        mPlaytime.setLimited(true);
                                    }

                                }
                            }
                        });


            }
        });

    }


    /**
     * 设置播放时间
     */
    private void setNoLimit() {

        AnswerQuestionDialog questionDialog = new AnswerQuestionDialog(mContext);
        questionDialog.setCanDismiss(false);
        questionDialog.setCanceledOnTouchOutside(false);
        questionDialog.setCancelable(false);
        questionDialog.show();
        isDialogShow = true;
        questionDialog.setRightAnswerListener(new AnswerQuestionDialog.RightAnswerListener() {
            @Override
            public void doing() {
                playingStart();
                mPlaytime.setLimited(false);
                isDialogShow = false;
                //PlaySettingFragment.setTimeSet(mContext, false);
            }
        });

    }

    /**
     * 进入家长中心
     */
    private void goToParent() {
        AnswerQuestionDialog questionDialog = new AnswerQuestionDialog(mContext);
        questionDialog.show();
        questionDialog.setRightAnswerListener(new AnswerQuestionDialog.RightAnswerListener() {
            @Override
            public void doing() {
                /*Intent intent = new Intent(mContext, ParentFragmentActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
            }
        });

    }


    private void showDoYouLike(int currPosition, int duration) {
        DoYouLikeDialog dialog = new DoYouLikeDialog(this);
        dialog.setCancelable(false);
        dialog.setEpisodeId(mPlayingEpisodeId);

        int percentage;

        if (currPosition == 0 || duration == 0) {
            percentage = 100;
        } else {
            percentage = currPosition * 100 / duration;
        }

        dialog.setPercentage(percentage);//for test
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                playListIndex++;
                if (mSmartPlayList != null && playListIndex < mSmartPlayList.size()) {
                    RecommendItem item = mSmartPlayList.get(playListIndex);
                    isFavorite = item.isFavorite();
                    if (isFavorite) {
                        btnCollect.setImageResource(R.drawable.btn_free_favorited);
                    } else {
                        btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                    }
                    reqPlayUrl(item.getEpisodeId());

                    mUIHandler.sendEmptyMessage(UI_EVENT_SHOW_NEXT);

                    if (playListIndex == mSmartPlayList.size() - 1) {
                        reqSmartRecommend(true);
                    }
                }

            }

        });
    }

    /**
     * 1.10 获取推荐内容（智能推荐）
     */
    private void reqSmartRecommend(final boolean isPlay) {
        String url = AppConfig.SMART_RECOMMEND_EPISODE;
        SmartRecommendRequest request = new SmartRecommendRequest();
        int profileId = ((KidsMindApplication) getApplication()).getProfileId();
        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        request.setCount(3);

        request.setProfileId(profileId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                SmartRecommendResponse resp = GsonUtil.parse(response,
                        SmartRecommendResponse.class);
                if (resp.isSucess()) {
                    List<RecommendItem> list = resp.getData().getRecommendation();
                    if (mSmartPlayList.isEmpty() && !isPlay) {
                        RecommendItem item = list.get(0);
                        isFavorite = item.isFavorite();
                        if (isFavorite) {
                            btnCollect.setImageResource(R.drawable.btn_free_favorited);
                        } else {
                            btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                        }
                        reqPlayUrl(item.getEpisodeId());
                        list.remove(0);
                    }
                    mSmartPlayList.addAll(list);
                    mUIHandler.sendEmptyMessage(UI_EVENT_SHOW_NEXT);

                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    /**
     * 1.11 获取播放地址
     *
     * @param episodeId
     */
    private void reqPlayUrl(final int episodeId) {
        mReportModel.report();

        String url = AppConfig.PLAY_URL;
        PlayUrlRequest request = new PlayUrlRequest();

        int profileId = ((KidsMindApplication) getApplication()).getProfileId();
        int behaviorId = ((KidsMindApplication) getApplication()).getBehaviorId();
        String token = ((KidsMindApplication) getApplication()).getToken();

        request.setToken(token);
        request.setProfileId(profileId);
        request.setEpisodeId(episodeId);
        request.setBehaviorId(behaviorId);

        String json = request.toJsonString();
        mReportModel.startReqUrlTime();

        final long reqTime = System.currentTimeMillis();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                final PlayUrlResponse resp = GsonUtil.parse(response,
                        PlayUrlResponse.class);
                if (resp.isSucess()) {

                    linearMainTitle.setVisibility(View.VISIBLE);

                    String name = resp.getData().getEpisodeName();
                    tvCartoonName.setText(name);
                    tvEpisodeName.setText(name);

                    List<String> preferences = resp.getData().getRates();
                    if (preferences != null && preferences.size() == 3) {
                        tvPrefOne.setText(preferences.get(0));
                        tvPrefTwo.setText(preferences.get(1));
                        tvPrefThree.setText(preferences.get(2));
                    }
                    mReportModel.setReqUrlTime();

                    if ((System.currentTimeMillis() - reqTime) < 30000) {
                        mUIHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                repPlayUrl(resp, episodeId);
                            }
                        }, 2000);
                    } else {
                        repPlayUrl(resp, episodeId);
                    }

                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }


    private void repPlayUrl(PlayUrlResponse resp, int episodeId) {

        mHeadPos = resp.getData().getPrologue();
        mPlayUrlInfo = resp.getData();

        String lang = setLang(mPlayUrlInfo);
        setQuality(mPlayUrlInfo);

        mPlaybtn.setImageResource(R.drawable.btn_free_pause);

        String source = mPlayUrlInfo.getPlayUrl();
        playVideo(source, episodeId);
        int profileId = ((KidsMindApplication) getApplication()).getProfileId();
        mReportModel.init(profileId, episodeId, lang, mPlayUrlInfo.getBehaviorPlayId());
    }

    private String setLang(PlayUrlInfo playUrlInfo) {
        String lang = PlayUrlInfo.CHINESE;
        int cur = langIndex % 2;
        if (cur == 1) {
            lang = PlayUrlInfo.ENGLISH;
        }
        if (lang.equals(PlayUrlInfo.CHINESE)) {
            btnLang.setText(R.string.zhong);
        } else {
            btnLang.setText(R.string.ying);
        }

        if (mPlayUrlInfo.hasEnglish()) {
            btnLang.setVisibility(View.VISIBLE);
        } else {
            btnLang.setVisibility(View.GONE);
        }
        playUrlInfo.setLang(lang);

        return lang;
    }

    private void setQuality(PlayUrlInfo playUrlInfo) {
        String quality;
        int index = qualityIndex % 2;
        if (index == 1) {
            quality = PlayUrlInfo.FHD;
        } else {
            quality = PlayUrlInfo.HD;
        }

        if (quality.equals(PlayUrlInfo.FHD)) {
            btnQuality.setText(R.string.text_chaoqing);
        } else {
            btnQuality.setText(R.string.text_gaoqing);
        }

        if (mPlayUrlInfo.hasHD()) {
            btnQuality.setVisibility(View.VISIBLE);
        } else {
            btnQuality.setVisibility(View.GONE);
        }

        playUrlInfo.setQuality(mContext, quality);
    }


    /**
     * 1.19 收藏剧集 收藏剧集公用了请求播放地址的协议数据
     */
    private void reqAddFavorite(int episodeId) {
        String url = AppConfig.ADD_FAVORITE;
        PlayUrlRequest request = new PlayUrlRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        request.setEpisodeId(episodeId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                PlayUrlResponse resp = GsonUtil.parse(response,
                        PlayUrlResponse.class);
                if (resp.isSucess() || resp.isCollect()) {
                    isFavorite = true;
                    btnCollect.setImageResource(R.drawable.btn_free_favorited);
                    Toast.makeText(mContext, "添加收藏成功！", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    /**
     * 1.20   取消收藏
     */
    private void reqRemoveFavorite(final int[] episodeId) {
        String url = AppConfig.REMOVE_FAVORITE;
        CancelFavRequest request = new CancelFavRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);

        request.setEpisodeId(episodeId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                PlayUrlResponse resp = GsonUtil.parse(response,
                        PlayUrlResponse.class);
                if (resp.isSucess()) {
                    isFavorite = false;
                    btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                }
                Toast.makeText(mContext, "取消收藏成功！", Toast.LENGTH_SHORT).show();
            }

        });
    }


    /**
     * 隐藏购买VIP提示框
     */
    public void hideBuyVipDialog() {
        boolean isVip = ((KidsMindApplication) getApplication()).isVip();
        if (mBuyVipDialog != null && mBuyVipDialog.isShowing() && isVip) {
            mBuyVipDialog.dismiss();
        }
    }


    /**
     * 1.31 非会员心跳
     */
    private void reqHeartBeat() {
        String url = AppConfig.HEART_BEAT;
        HeartBeatRequest request = new HeartBeatRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);


        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                HeartBeatResponse resp = GsonUtil.parse(response,
                        HeartBeatResponse.class);
                if (resp != null && resp.isNeedPayVip()) {
                    playingPause();
                    mBuyVipDialog = getmBuyVipDialog(BuyVipDialog.VIP_TYPE.VIP_HEART_BEAT);
                    mBuyVipDialog.show();
                    mUIHandler.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                if (mBuyVipDialog.isShowing()) {
                                    mBuyVipDialog.dismiss();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, 30 * 1000);
                }

            }

        });
    }

    private Timer mTimer;

    private void startTimer() {
        if (mTimer != null)
            return;
        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                reqHeartBeat();
            }
        }, 10 * 1000, 60 * 1000); // 每隔1分钟触发一次
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    private Timer mSpeedTimer;
    private long old_kb;

    private void startSpeedTimer() {
        if (mSpeedTimer != null)
            return;
        mSpeed.setVisibility(View.VISIBLE);
        mSpeed.setText("");

        old_kb = AppUtils.getUidRxBytes(mContext);
        mSpeedTimer = new Timer(true);

        mSpeedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long new_kb = AppUtils.getUidRxBytes(mContext);
                if (mUIHandler == null) {
                    return;
                }
                Message msg = mUIHandler.obtainMessage();
                msg.what = UI_EVENT_SHOW_DOWNLOAD_SPEED;
                msg.obj = new_kb - old_kb;
                mUIHandler.sendMessage(msg);

                old_kb = new_kb;
            }
        }, 1000, 1000); // 每隔1秒钟触发一次
    }

    private void stopSpeedTimer() {
        if (mSpeedTimer != null) {
            mSpeedTimer.cancel();
            mSpeedTimer = null;
        }
        mSpeed.setVisibility(View.GONE);
    }


    private class PlayUIHandler extends Handler {
        private final WeakReference<SmartPlayingActivity> mTarget;

        PlayUIHandler(SmartPlayingActivity target) {
            mTarget = new WeakReference<SmartPlayingActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_PLAY:
                    callPlayVideo();
                    break;
                /**
                 * 更新进度及时间
                 */
                case UI_EVENT_UPDATE_CURRPOSITION:
                    int currPosition = getCurrentPosition();
                    int duration = getDuration();

                    mReportModel.addPlayTime(UPDATE_CURRPOSITION_DELAY_TIME);

                    updateTextViewWithTimeFormat(mCurrPostion, currPosition);
                    updateTextViewWithTimeFormat(mDuration, duration);
                    mProgress.setMax(duration);
                    mProgress.setProgress(currPosition);

                    int percent = mVideoView.getBufferPercentage();
                    int second = (duration * percent) / 100;
                    mProgress.setSecondaryProgress(second);

                    if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                        mUIHandler.sendEmptyMessageDelayed(
                                UI_EVENT_UPDATE_CURRPOSITION, UPDATE_CURRPOSITION_DELAY_TIME);
                    }

                    break;
                case UI_EVENT_FULL_SCREEN:
                    if (isPlaying())
                        fullScreenPlaying();
                    break;
                case UI_EVENT_HALF_SCREEN:
                    showControlBar();
                    break;
                case UI_EVENT_LOADING_PROGRESS:
                    break;
                case UI_EVENT_LOADING_NEXT:
                    showDoYouLike(0, 0);
                    break;
                case UI_EVENT_SHOW_PUPUP:

                    break;
                case UI_EVENT_REPORT_PLAY:

                    break;
                case UI_EVENT_HIDE_PUPUP:
                    PopupWindow popupWindow = (PopupWindow) msg.obj;
                    if (popupWindow != null && popupWindow.isShowing()) {
                        try {
                            popupWindow.dismiss();
                        } catch (Exception e) {

                        }
                    }
                    break;
                case REPORT_EVENT_ADD_TOTAL_TIME:

                    mReportModel.addTotalTime(UPDATE_CURRPOSITION_DELAY_TIME);
                    mUIHandler.sendEmptyMessageDelayed(
                            REPORT_EVENT_ADD_TOTAL_TIME, UPDATE_CURRPOSITION_DELAY_TIME);
                    break;

                case UI_EVENT_HIDE_UNLOCK_BUTTON:
                    btnUnLock.setVisibility(View.GONE);
                    break;
                case UI_EVENT_SHOW_ADV:
                    if (!mVideoView.isPlaying()) {
                        //暂停状态状态请求广告
                        AppUtils.getInstance().reqAction(mBuyVipDialog, mTarget.get(), new PlayPauseDialog.onDismissListener() {

                            @Override
                            public void dismiss(boolean ifPlay) {
                                if (ifPlay) {
                                    playingStartOrPause();
                                }
                            }
                        });
                    }
                    break;
                case UI_EVENT_SHOW_DOWNLOAD_SPEED:
                    long kb = (Long) msg.obj;
                    if (kb < 100) {
                        mSpeed.setTextColor(getResources().getColor(R.color.red));
                    } else if (kb < 200) {
                        mSpeed.setTextColor(getResources().getColor(R.color.yellow));
                    } else {
                        mSpeed.setTextColor(getResources().getColor(R.color.green));
                    }
                    String text = getString(R.string.video_loading) + kb + getString(R.string.unit);

                    mSpeed.setText(text);
                    break;
                case UI_EVENT_SHOW_NEXT:
                    int next = playListIndex + 1;
                    if (mSmartPlayList != null && next < mSmartPlayList.size()) {
                        RecommendItem nextItem = mSmartPlayList.get(next);
                        tvSerie.setText(nextItem.getSerieName());
                        tvEpisode.setText(nextItem.getEpisodeName());
                    }

                    if (playListIndex >= 0 && mSmartPlayList.size() > 0) {
                        RecommendItem curItem = mSmartPlayList.get(playListIndex);
                        tvEpisodeName.setText(curItem.getEpisodeName());
                    }
                    break;
                default:
                    break;
            }
        }

    }

    class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {

            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            //用户单击屏幕时被调用
            if (isLocked) {
                if (btnUnLock.getVisibility() == View.GONE) {
                    setUnLockButtonVisible();
                } else {
                    btnUnLock.setVisibility(View.GONE);
                }
            } else if (isBarShow()) {
                fullScreenPlaying();
            } else {
                halfScreenPlaying();
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                    && Math.abs(vx) > FLING_MIN_VELOCITY) {
                //用户左滑时快退5s
                if (isPlaying() && !isLocked) {
                    seekPos(getCurrentPosition() - PLAYER_SEEK_TIME);
                }
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                    && Math.abs(vx) > FLING_MIN_VELOCITY) {
                //用户右滑时快进5s
                if (isPlaying() && !isLocked) {
                    seekPos(getCurrentPosition() + PLAYER_SEEK_TIME);
                }
            }
            return false;
        }
    }
}
