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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.adapter.FreePlayAdapter;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.debug.CrashHandler;
import com.applidium.nickelodeon.dialog.AnswerQuestionDialog;
import com.applidium.nickelodeon.dialog.BuyVipDialog;
import com.applidium.nickelodeon.dialog.PaymentDialog;
import com.applidium.nickelodeon.dialog.PlayPauseDialog;
import com.applidium.nickelodeon.dialog.SeekbarTimeDialog;
import com.applidium.nickelodeon.dialog.TimeOverDialog;
import com.applidium.nickelodeon.entity.ContentItem;
import com.applidium.nickelodeon.entity.PlayNoticeRequest;
import com.applidium.nickelodeon.entity.PlayUrlInfo;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.BaiduUtils;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ListUtils;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.uitls.CustomDigitalClock;
import com.applidium.nickelodeon.views.VideoViewCustom;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FreePlayingActivity extends Activity implements OnClickListener,
        View.OnLongClickListener {

    public static final String TAG = FreePlayingActivity.class.getSimpleName();
    private static final int UPDATE_CURRPOSITION_DELAY_TIME = 200;

    private Context mContext;
    /**
     * **UI控件***
     */

    private VideoViewCustom mVideoView = null;


    private BuyVipDialog mBuyVipDialog;

    private ImageView imgLoadingPlay;
    private TextView mSpeed;
    private TextView tvEpisodeName;

    private ImageButton mPlaybtn = null;

    private LinearLayout mController = null;
    private RecyclerView mListView = null;

    private FreePlayAdapter mFreePlayAdapter;


    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;

    private CustomDigitalClock mPlaytime = null;

    private ImageButton btnLang;

    private ImageButton btnBack;
    private ImageButton btnLock;

    private ImageButton btnUnLock;

    private boolean isLocked = false;  //设置一个状态值，用于锁屏

    private int mPlayingEpisodeId;
    private ContentItem mContentItem;


    private String mVideoSource = null;

    private int langIndex = 0;  //语言选项
    private int spareIndex = 0; //切换备用视频播放地址
    private CartoonInfo mCartoonInfo = new CartoonInfo();

    private PlayerReportModel mReportModel;


    ArrayList<ContentItem> mSeriesLists;  //所有播放剧集信息
    /**
     * 记录播放位置
     */
    private int mLastPos = 0;
    private int mHeadPos = 0;


    private int mSerieId;

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

    private final int UI_EVENT_NEED_BUY_VIP = 12; // 提示购买VIP权限才能观看
    private final int UI_EVENT_HIDE_UNLOCK_BUTTON = 20;  //自动隐藏unLock button

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.free_playing_activity);
        ((MNJApplication) getApplicationContext()).reqUserInfo();//刷新用户VIP信息

        mContext = this;
        mUIHandler = new PlayUIHandler(this);
        mReportModel = new PlayerReportModel(this);   //播放汇报

        initView();
        initAndroidPlayer();
        initPlayTimeControl();

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        mSeriesLists = getIntent().getParcelableArrayListExtra("series");

        mLastPos = intent.getIntExtra("lastPos", 0);  //播放历史位置
        langIndex = intent.getIntExtra("langIndex", 0); //播放语言类别
        if (langIndex == 0) {
            langIndex = AppUtils.getIntSharedPreferences(mContext, "LANG", 0);
        }
        mSerieId = intent.getIntExtra("serieId", 0); //总剧集ID

        mFreePlayAdapter = new FreePlayAdapter(this, mSeriesLists);
        mListView.setAdapter(mFreePlayAdapter);
        mFreePlayAdapter.setSelectItem(position);
        ContentItem serie = mSeriesLists.get(position);
        reqPlayUrl(serie);

        //语言提示
        MediaPlayerService.playSound(this, MediaPlayerService.BEFORE);
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

            halfScreenPlaying();

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


        BaiduUtils.onPause(this);
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

                if (imgLoadingPlay.getVisibility() == View.VISIBLE) {
                    imgLoadingPlay.setVisibility(View.GONE);
                }

                if (!mUIHandler.hasMessages(UI_EVENT_UPDATE_CURRPOSITION)) {
                    mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
                }

                if (!mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
                    mUIHandler.sendEmptyMessageDelayed(UI_EVENT_FULL_SCREEN, 5000);
                }
                //startTimer();

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


                //stopTimer();
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

                    String url = mContentItem.getPlayUrl(langIndex);  //获取播放出错的地址
                    CrashHandler.getInstance().saveVideoSourceErrorToFile(url);  //保存播放出错的视频地址

                    if (spareIndex > 1) {
                        Toast.makeText(mContext, getString(R.string.playing_error), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    mLastPos = getCurrentPosition();
                    String source = mContentItem.getSparePlayUrl(langIndex);  //获取备用播放地址
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
            if (mSerieId > 0 && mPlayingEpisodeId > 0) {
                AppUtils.setStringSharedPreferences(mContext, "PLAY_RECORD" + mSerieId,
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
        mPlaybtn.setImageResource(R.drawable.btn_free_pause);
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

        imgLoadingPlay = (ImageView) findViewById(R.id.img_loading);
        mListView = (RecyclerView) findViewById(R.id.listview);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(layoutManager);


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
            if (isLocked) {
                //锁屏状态下，按后退键无效。
            } else if (mListView.getVisibility() == View.VISIBLE
                    || mController.getVisibility() == View.VISIBLE) {
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

            if (mController.getVisibility() == View.GONE
                    && mListView.getVisibility() == View.GONE) {
                showSerieList();
                return true;
            }

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mController.getVisibility() == View.GONE) {
                showControlBar(false);
                mListView.setVisibility(View.GONE);
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
        String source = mContentItem.getPlayUrl(langIndex);

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
            halfScreenPlaying();
            btnUnLock.setVisibility(View.GONE);
        }

        return false;
    }


    /**
     * 显示剧集列表
     */
    private void showSerieList() {
        Animation scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        // 动画集
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);

        mListView.setVisibility(View.VISIBLE);

        if (!mVideoSource.contains(".mp3")) {
            tvEpisodeName.setVisibility(View.VISIBLE);
        }

        // 设置动画时间 (作用到每个动画)
        set.setDuration(200);
        mListView.startAnimation(set);

        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.requestFocus();
            }
        }, 200);
    }

    /**
     * 显示控制栏
     */
    private void showControlBar(boolean isShowSerieList) {

        mController.setVisibility(View.VISIBLE);

        if (isShowSerieList) {
            int screen_width = ScreenUtils.getWidthPixels(mContext);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mController.getLayoutParams();
            if (params.width == screen_width) {
                int list_w = getResources().getDimensionPixelOffset(R.dimen.playlist_width);
                int width = screen_width - list_w;
                params.width = width;
                params.leftMargin = list_w / 2;
                params.rightMargin = list_w / 2;
                mController.setLayoutParams(params);
            }

        }

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
    }


    /**
     * 非全屏状态
     */
    private void halfScreenPlaying() {
        showControlBar(true);
        showSerieList();
    }

    /**
     * 全屏播放
     */
    private void fullScreenPlaying() {
        mController.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
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
        if (mController.getVisibility() == View.VISIBLE
                || mListView.getVisibility() == View.VISIBLE) {
            res = true;
        }
        return res;
    }


    @Override
    public void onStop() {
        super.onStop();
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
                //ParentsCenterActivity.setTimeSet(mContext, false);
            }
        });

    }


    public void reqPlayUrl(int positon) {
        if (mSeriesLists != null && mSeriesLists.size() > positon) {
            ContentItem item = mSeriesLists.get(positon);
            reqPlayUrl(item);
        }

    }


    /**
     * 默认播放中文动画
     *
     * @param item
     */
    public void reqPlayUrl(ContentItem item) {
        mContentItem = item;

        if (!item.isLangSwitch()) {
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

        playVideo(item.getPlayUrl(langIndex), item.getContentId());
        tvEpisodeName.setText(item.getContentName());

        int profileId = ((MNJApplication) getApplication()).getProfileId();
        mReportModel.init(profileId, item.getContentId(), langIndex);

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
     * 设置2分钟延时后是否显示VIP购买
     *
     * @param isTrial
     */
    private void setVipInfo(boolean isTrial) {
        mPlaybtn.setOnClickListener(this);
        if (mUIHandler != null
                && mUIHandler.hasMessages(UI_EVENT_NEED_BUY_VIP)) {
            mUIHandler.removeMessages(UI_EVENT_NEED_BUY_VIP);
        }

        boolean isVip = ((MNJApplication) mContext.getApplicationContext()).isVip();
        if (!isTrial && !isVip) {
            final TextView textView = (TextView) findViewById(R.id.tv_free_time);
            textView.setVisibility(View.VISIBLE);
            textView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setVisibility(View.GONE);
                }
            }, 10 * 1000);

            mUIHandler.sendEmptyMessageDelayed(UI_EVENT_NEED_BUY_VIP, 1000 * 60 * 2);
            mLastPos = 0; //设置默认从头播放
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
        private final WeakReference<FreePlayingActivity> mTarget;

        PlayUIHandler(FreePlayingActivity target) {
            mTarget = new WeakReference<FreePlayingActivity>(target);
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
                    halfScreenPlaying();
                    break;
                case UI_EVENT_LOADING_PROGRESS:
                    break;
                case UI_EVENT_LOADING_NEXT:
                    RecyclerView.Adapter adapter = mListView.getAdapter();

                    if (adapter instanceof FreePlayAdapter) {
                        List<ContentItem> list = ((FreePlayAdapter) adapter).getLists();
                        if (ListUtils.isEmpty(list)) {
                            return;
                        }

                        int index = ((FreePlayAdapter) adapter).getSelectItem();//获取当前选择项

                        index = (index + 1) % list.size(); //下一集

                        ContentItem item = list.get(index);

                        reqPlayUrl(item);
                        ((FreePlayAdapter) adapter).setSelectItem(index);
                    }

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
                    //暂停状态状态请求广告
                    if (!mVideoView.isPlaying()) {
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
                case UI_EVENT_HIDE_UNLOCK_BUTTON:
                    btnUnLock.setVisibility(View.GONE);
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

    class CartoonInfo {
        private String shareImgUrl;
        private String shareTitle;
        private boolean trial;
    }
}
