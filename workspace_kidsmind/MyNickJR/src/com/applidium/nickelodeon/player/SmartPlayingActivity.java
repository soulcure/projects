package com.applidium.nickelodeon.player;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.ParentsCenterActivity;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.debug.CrashHandler;
import com.applidium.nickelodeon.dialog.AnswerQuestionDialog;
import com.applidium.nickelodeon.dialog.BuyVipDialog;
import com.applidium.nickelodeon.dialog.DoYouLikeDialog;
import com.applidium.nickelodeon.dialog.PaymentDialog;
import com.applidium.nickelodeon.dialog.PlayPauseDialog;
import com.applidium.nickelodeon.dialog.SeekbarTimeDialog;
import com.applidium.nickelodeon.dialog.TimeOverDialog;
import com.applidium.nickelodeon.entity.HeartBeatRequest;
import com.applidium.nickelodeon.entity.HeartBeatResponse;
import com.applidium.nickelodeon.entity.PlayNoticeRequest;
import com.applidium.nickelodeon.entity.PlayUrlInfo;
import com.applidium.nickelodeon.entity.PlayUrlRequest;
import com.applidium.nickelodeon.entity.PlayUrlResponse;
import com.applidium.nickelodeon.entity.RecommendItem;
import com.applidium.nickelodeon.entity.SmartRecommendRequest;
import com.applidium.nickelodeon.entity.SmartRecommendResponse;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.CustomDigitalClock;
import com.applidium.nickelodeon.uitls.GlideCircleTransform;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.VideoViewCustom;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SmartPlayingActivity extends Activity implements OnClickListener,
        View.OnLongClickListener {

    public static final String TAG = SmartPlayingActivity.class.getSimpleName();

    private static final int UPDATE_CURRPOSITION_DELAY_TIME = 200;


    private Context mContext;
    /**
     * **UI控件***
     */

    private VideoViewCustom mVideoView = null;


    private BuyVipDialog mBuyVipDialog;
    private TextView mSpeed;
    private TextView tvEpisodeName;
    private ImageButton mPlaybtn = null;

    private LinearLayout mController = null;

    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;
    private CustomDigitalClock mPlaytime = null;

    private ImageButton btnLang;

    private ImageButton btnBack;
    private ImageButton btnLock;

    private ImageButton btnUnLock;

    private boolean isLocked = false;  //设置一个状态值，用于锁屏

    private LinearLayout linearMainTitle;
    private ImageView imageCover;
    private TextView tvCartoonName;
    private TextView tvEpdNname;
    private TextView tvPrefOne;
    private TextView tvPrefTwo;
    private TextView tvPrefThree;


    private List<RecommendItem> mSmartPlayList = new ArrayList<RecommendItem>();

    private int playListIndex = -1;


    private int mPlayingEpisodeId;
    private String mVideoSource = null;

    private int langIndex = 0;  //语言选项
    private int spareIndex = 0; //切换备用视频播放地址
    private PlayUrlInfo mPlayUrlInfo;

    private PlayerReportModel mReportModel;
    /**
     * 记录播放位置
     */
    private int mLastPos = 0;   //最后播放的位置
    private int mHeadPos = 0;


    private PlayUIHandler mUIHandler;

    private GestureDetector mGestureDetector; //用于手势处理
    private final int FLING_MIN_DISTANCE = 1 * 150;  //判断滑动时，设置的最小滑动距离
    private final int FLING_MIN_VELOCITY = 2 * 100;  //判断滑动时，设置的最小滑动速度
    private final int PLAYER_SEEK_TIME = 5 * 1000; //设置播放器向前或向后跳5s

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
    private final int UI_EVENT_SHOW_NEXT = 19; // 加载下一个节目
    private final int UI_EVENT_HIDE_UNLOCK_BUTTON = 20;  //自动隐藏unLock button

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_playing_activity);
        ((MNJApplication) getApplicationContext()).reqUserInfo();//刷新用户VIP信息


        mContext = this;
        mUIHandler = new PlayUIHandler(this);
        mReportModel = new PlayerReportModel(this);   //播放汇报

        initView();

        initAndroidPlayer();
        initPlayTimeControl();


        //语言提示
        MediaPlayerService.playSound(this, MediaPlayerService.BEFORE);

        String record = AppUtils.getStringSharedPreferences(mContext, "SMART_RECORD", "");
        if (!StringUtils.isEmpty(record)) {
            String[] strs = record.split("#");
            if (strs != null && strs.length == 3) {
                int episodeId = Integer.parseInt(strs[0]);
                if (episodeId > 0) {
                    mLastPos = Integer.parseInt(strs[1]);
                    langIndex = Integer.parseInt(strs[2]);
                    reqPlayUrl(episodeId);
                    return;
                }

            }

        } else {
            langIndex = AppUtils.getIntSharedPreferences(mContext, "LANG", 0);
        }

        reqSmartRecommend();

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
                && !StringUtils.isEmpty(mVideoSource)) {

            playingStart();

            showControlBar();
            startTimer();
        }
        BaiduUtils.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //playingPause();
        mPlaytime.pause();//暂停计时

        stopService(new Intent(this, MediaPlayerService.class));

        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        stopPlayer(true);
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
                TimeOverDialog timeOverDialog = new TimeOverDialog(mContext);
                try {
                    timeOverDialog.show();
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
                    /*if (PlaySettingFragment.isSkipHead(mContext) && mHeadPos > 0) {
                        mVideoView.seekTo(mHeadPos * 1000);
                        mHeadPos = 0;
                        Toast.makeText(mContext, R.string.skip_head, Toast.LENGTH_SHORT).show();
                    }*/

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
                if (extra == MediaPlayer.MEDIA_ERROR_UNKNOWN
                        || extra == MediaPlayer.MEDIA_ERROR_SERVER_DIED
                        || extra == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK
                        || extra == MediaPlayer.MEDIA_ERROR_IO
                        || extra == MediaPlayer.MEDIA_ERROR_MALFORMED
                        || extra == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {

                    String url = mPlayUrlInfo.getPlayUrl(langIndex);  //获取播放出错的地址
                    CrashHandler.getInstance().saveVideoSourceErrorToFile(url);  //保存播放出错的视频地址

                    if (spareIndex > 1) {
                        Toast.makeText(mContext, getString(R.string.playing_error), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    mLastPos = getCurrentPosition();
                    String source = mPlayUrlInfo.getSparePlayUrl(langIndex);  //获取备用播放地址
                    playVideo(source, mPlayingEpisodeId);  //备用播放地址请求播放
                    spareIndex++;

                    Toast.makeText(mContext, getString(R.string.net_timeout), Toast.LENGTH_SHORT).show();

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
     * 保存播放状态
     *
     * @param isSavePosition
     */
    private void stopPlayer(boolean isSavePosition) {
        if (isSavePosition) {
            //记录当前播放的位置,以便以后可以续播
            mLastPos = getCurrentPosition();
            if (mPlayingEpisodeId > 0) {
                AppUtils.setStringSharedPreferences(mContext, "SMART_RECORD",
                        mPlayingEpisodeId + "#" + mLastPos + "#" + langIndex);
            }
        }
        mReportModel.report();
    }


    private void callPlayVideo() {
        mVideoView.stopPlayback();
        /**
         * 设置播放url
         */
        mVideoView.setVideoPath(mVideoSource);
        startSpeedTimer();
        mReportModel.playStart();
        mPlaybtn.setImageResource(R.drawable.btn_free_pause);

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
        } else {
            playingStart();
            mReportModel.pauseEnd();
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

    }


    /**
     * 初始化控件
     */
    private void initView() {

        mVideoView = (VideoViewCustom) findViewById(R.id.video_view);
        /*int width = ScreenUtils.getWidthPixels(mContext);
        int height = ScreenUtils.getHeightPixels(mContext);
        mVideoView.setDimensions(width, height);*/

        mSpeed = (TextView) findViewById(R.id.speed);
        tvEpisodeName = (TextView) findViewById(R.id.tv_episode_name);


        linearMainTitle = (LinearLayout) findViewById(R.id.linear_main_title);

        imageCover = (ImageView) linearMainTitle.findViewById(R.id.image_cover);
        tvEpdNname = (TextView) linearMainTitle.findViewById(R.id.tv_epd_name);
        tvCartoonName = (TextView) linearMainTitle.findViewById(R.id.tv_cartoon_name);
        tvPrefOne = (TextView) linearMainTitle.findViewById(R.id.tv_pref_one);
        tvPrefTwo = (TextView) linearMainTitle.findViewById(R.id.tv_pref_two);
        tvPrefThree = (TextView) linearMainTitle.findViewById(R.id.tv_pref_three);

        findViewById(R.id.btn_next).setOnClickListener(this);


        mPlaybtn = (ImageButton) findViewById(R.id.btn_play);
        mPlaybtn.setOnClickListener(this);

        btnLang = (ImageButton) findViewById(R.id.btn_lang);
        btnLang.setOnClickListener(this);


        mController = (LinearLayout) findViewById(R.id.controlbar);

        mProgress = (SeekBar) findViewById(R.id.media_progress);
        mDuration = (TextView) findViewById(R.id.time_total);
        mCurrPostion = (TextView) findViewById(R.id.time_current);

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnLock = (ImageButton) findViewById(R.id.btn_play_lock);
        btnLock.setOnClickListener(this);

        //如果是手机和平板 显示返回 和锁定
        if (!ScreenUtils.isTv(mContext)) {
            btnBack.setVisibility(View.VISIBLE);
            btnLock.setVisibility(View.VISIBLE);
        }

        btnUnLock = (ImageButton) findViewById(R.id.btn_play_unlock);
        btnUnLock.setOnLongClickListener(this);

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
            if (mController.getVisibility() == View.VISIBLE) {
                fullScreenPlaying();
            } else {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

            playingStartOrPause();
            //延时设为300毫秒
            if (!AppUtils.isRepeatClick(300)) {
                //延迟发送 展示广告的信息
                Message msg = new Message();
                msg.what = UI_EVENT_SHOW_ADV;
                mUIHandler.sendMessage(msg);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

            if (mController.getVisibility() == View.GONE) {
                showControlBar();
                return true;
            }

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

    private BuyVipDialog getmBuyVipDialog() {
        if (mBuyVipDialog == null) {
            mBuyVipDialog = new BuyVipDialog(mContext, BuyVipDialog.VIP_TYPE.VIP_TRIAL);
        }
        return mBuyVipDialog;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_play) {
            //延时设为300毫秒
            if (!AppUtils.isRepeatClick(300)) {
                playingStartOrPause();
            }
        } else if (id == R.id.btn_next) {
            mVideoView.pause();
            int currPosition = getCurrentPosition();
            int duration = getDuration();
            showDoYouLike(currPosition, duration);
        } else if (id == R.id.btn_lang) {

            if (!((MNJApplication) getApplication()).isVip()) {
                getmBuyVipDialog().show();
                playingPause();
                mBuyVipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        boolean isVip = ((MNJApplication) getApplication()).isVip();
                        if (isVip) {
                            switchLang();
                        } else {
                            if (PaymentDialog.isPaymentOtherActivity(mContext)) {
                                callPlayVideo();
                            } else {
                                playingStart();
                            }
                        }
                    }
                });
                return;
            }

            switchLang();

        } else if (id == R.id.btn_back) {
            finish();
        } else if (id == R.id.btn_play_lock) {
            isLocked = true;
            setUnLockButtonVisible();
            fullScreenPlaying();
        }
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
        AppUtils.setIntSharedPreferences(mContext, "LANG", langIndex);

        int cur = langIndex % 2;
        //中文-->英文
        if (cur == 1) {
            btnLang.setImageResource(R.drawable.btn_free_english);
        } else {//英文-->中文
            btnLang.setImageResource(R.drawable.btn_free_chinese);
        }

        mLastPos = getCurrentPosition();  //记录播放位置
        String source = mPlayUrlInfo.getPlayUrl(langIndex);
        playVideo(source, mPlayingEpisodeId);

        int profileId = ((MNJApplication) getApplication()).getProfileId();
        mReportModel.init(profileId, mPlayingEpisodeId, langIndex);
        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
    }

    /*
 *  长按事件
 */
    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_play_unlock) {
            isLocked = false;
            showControlBar();
            btnUnLock.setVisibility(View.GONE);
        }

        return false;
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

        //如果是手机和平板 显示返回 和锁定
        if (!ScreenUtils.isTv(mContext)) {
            btnBack.setVisibility(View.VISIBLE);
            btnLock.setVisibility(View.VISIBLE);
        }

        mProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress.requestFocus();
            }
        }, 200);

        // 设置动画时间 (作用到每个动画)
        set.setDuration(200);
        mController.startAnimation(set);
    }


    /**
     * 全屏播放
     */
    private void fullScreenPlaying() {
        mController.setVisibility(View.GONE);
        tvEpisodeName.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        btnLock.setVisibility(View.GONE);
        btnUnLock.setVisibility(View.GONE);
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
        mUIHandler.removeMessages(UI_EVENT_REPORT_PLAY);
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
        if (StringUtils.isEmpty(source) || mUIHandler == null) {
            Toast.makeText(mContext, R.string.play_url_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        stopPlayer(false);

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


        playNoticeToServer(episodeId);
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
                                    mPlaytime.setCountMillSecond(min * 60 * 1000);
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
        questionDialog.setRightAnswerListener(new AnswerQuestionDialog.RightAnswerListener() {
            @Override
            public void doing() {
                playingStart();
                mPlaytime.setLimited(false);
                ParentsCenterActivity.setTimeSet(mContext, false);
            }
        });

    }


    private void showDoYouLike(int currPosition, int duration) {
        langIndex = AppUtils.getIntSharedPreferences(mContext, "LANG", 0);

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

                    reqPlayUrl(item.getContentId());
                } else {
                    reqSmartRecommend();
                }

            }

        });
    }

    /**
     * 1.10 获取推荐内容（智能推荐）
     */
    private void reqSmartRecommend() {
        String url = AppConfig.RECOMMEND_PLAY;
        SmartRecommendRequest request = new SmartRecommendRequest();
        int profileId = ((MNJApplication) getApplication()).getProfileId();
        String token = ((MNJApplication) getApplication()).getToken();
        request.setToken(token);
        request.setCount(7);

        request.setProfileId(profileId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                SmartRecommendResponse resp = GsonUtil.parse(response,
                        SmartRecommendResponse.class);
                if (resp.isSucess()) {
                    playListIndex = 0;
                    mSmartPlayList = resp.getData().getRecommendation();
                    RecommendItem item = mSmartPlayList.get(playListIndex);

                    reqPlayUrl(item.getContentId());

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
        String url = AppConfig.PLAY_URL;
        PlayUrlRequest request = new PlayUrlRequest();

        String token = ((MNJApplication) getApplication()).getToken();
        request.setToken(token);
        request.setContentId(episodeId);

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

                    tvCartoonName.setText(resp.getData().getSubName());
                    tvEpdNname.setText(resp.getData().getContentName());
                    Glide.with(SmartPlayingActivity.this)
                            .load(resp.getData().getSeriesImg())
                            .centerCrop()
                            .transform(new GlideCircleTransform(SmartPlayingActivity.this))
                            //.placeholder(R.drawable.ic_launcher)  //占位图片
                            .error(R.drawable.ic_launcher)        //下载失败
                            .into(imageCover);

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


    /**
     * 默认播放中文动画
     *
     * @param resp
     * @param episodeId
     */
    private void repPlayUrl(PlayUrlResponse resp, int episodeId) {
        mPlayUrlInfo = resp.getData();

        if (!mPlayUrlInfo.isLangSwitch()) {
            btnLang.setVisibility(View.GONE);
        } else {
            btnLang.setVisibility(View.VISIBLE);
        }

        int cur = langIndex % 2;
        //中文-->英文
        if (cur == 1) {
            btnLang.setImageResource(R.drawable.btn_free_english);
        } else {//英文-->中文
            btnLang.setImageResource(R.drawable.btn_free_chinese);
        }


        mPlaybtn.setImageResource(R.drawable.btn_free_pause);
        tvEpisodeName.setText(mPlayUrlInfo.getContentName());

        String source = mPlayUrlInfo.getPlayUrl(langIndex);
        playVideo(source, episodeId);
        int profileId = ((MNJApplication) getApplication()).getProfileId();
        mReportModel.init(profileId, episodeId, langIndex);
    }


    /**
     * 隐藏购买VIP提示框
     */
    public void hideBuyVipDialog() {
        boolean isVip = ((MNJApplication) getApplication()).isVip();
        if (mBuyVipDialog != null && mBuyVipDialog.isShowing() && isVip) {
            mBuyVipDialog.dismiss();
        }
    }

    /**
     * 前端通知后台播放视频
     */
    private void playNoticeToServer(int contentId) {
        String url = AppConfig.PLAY_NOTICE;
        PlayNoticeRequest request = new PlayNoticeRequest();

        String token = ((MNJApplication) getApplication()).getToken();
        int profileId = ((MNJApplication) getApplication()).getProfileId();

        request.setToken(token);
        request.setContentId(contentId);
        request.setProfileId(profileId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {

            }

        });
    }


    /**
     * 1.31 非会员心跳
     */
    private void reqHeartBeat() {
        String url = AppConfig.HEART_BEAT;
        HeartBeatRequest request = new HeartBeatRequest();

        String token = ((MNJApplication) getApplication()).getToken();
        request.setToken(token);


        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                HeartBeatResponse resp = GsonUtil.parse(response,
                        HeartBeatResponse.class);
                if (resp.isNeedPayVip()) {
                    playingPause();
                    mBuyVipDialog = getmBuyVipDialog();
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
                showControlBar();
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
