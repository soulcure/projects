package com.ivmall.android.app.player;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.KidsMindFragmentActivity;
import com.ivmall.android.app.R;
import com.ivmall.android.app.adapter.FavoriteAdapter;
import com.ivmall.android.app.adapter.FreePlayAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.debug.CrashHandler;
import com.ivmall.android.app.dialog.AnswerQuestionDialog;
import com.ivmall.android.app.dialog.BuyVipDialog;
import com.ivmall.android.app.dialog.CustomDialog;
import com.ivmall.android.app.dialog.PlayPauseDialog;
import com.ivmall.android.app.dialog.SeekbarTimeDialog;
import com.ivmall.android.app.dialog.TimeOverDialog;
import com.ivmall.android.app.entity.CancelFavRequest;
import com.ivmall.android.app.entity.FavoriteItem;
import com.ivmall.android.app.entity.FavoriteRequest;
import com.ivmall.android.app.entity.FavoriteResponse;
import com.ivmall.android.app.entity.PlayUrlInfo;
import com.ivmall.android.app.entity.PlayUrlRequest;
import com.ivmall.android.app.entity.PlayUrlResponse;
import com.ivmall.android.app.entity.SerieInfo;
import com.ivmall.android.app.entity.SerieInfoRequest;
import com.ivmall.android.app.entity.SerieInfoResponse;
import com.ivmall.android.app.entity.SerieItem;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.service.MediaPlayerService;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.CustomDigitalClock;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.ListUtils;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FreePlayingActivity extends Activity implements OnClickListener, View.OnLongClickListener {

    public static final String TAG = FreePlayingActivity.class.getSimpleName();
    private static final int UPDATE_CURRPOSITION_DELAY_TIME = 200;

    public static final int FROM_NORMAL = 0; // 普通点播进入
    public static final int FROM_NARTOON_PEOPLE = 1; // 动画人物进入
    public static final int FROM_FAVORITE = 2; // 收藏进入
    public static final int FROM_UGC = 3; // UGC进入
    public static final int FROM_HOSTORY = 4; // 播放历史进入
    public static final int FROM_TOPIC_PLAY = 6; // 播放专题

    private Context mContext;
    /**
     * **UI控件***
     */

    private VideoView mVideoView = null;
    private BuyVipDialog mBuyVipDialog;
    private ImageView imgLoadingPlay;
    private TextView mSpeed;
    private TextView tvEpisodeName;

    private ImageButton mPlaybtn = null;
    private LinearLayout mController = null;
    private RecyclerView mListView = null;
    private FreePlayAdapter mFreePlayAdapter;
    private FavoriteAdapter mFavoriteAdapter;
    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;
    private CustomDigitalClock mPlaytime = null;
    private TextView btnLang;
    private ImageButton btnCollect;
    private ImageButton btnLock;
    private ImageButton btnShare;
    private ImageButton btnUnLock;
    private ImageButton btnBack;
    private TextView btnQuality;
    private ImageView adView;


    private boolean isLocked = false;  //设置一个状态值，用于锁屏

    private int mStartIndex; // 获取剧集条目的索引

    private int fromHistoryEpisodeId; // 从播放历史返回的播放id
    private int pushId; //推送过来直接播放的id
    private int mPlayingEpisodeId;
    private String mVideoSource = null;

    private int langIndex = 0;
    private int qualityIndex = 0;
    private int spareIndex = 0; //切换备用视频播放地址


    private int mSerieId;
    private PlayUrlInfo mPlayUrlInfo;
    private CartoonInfo mCartoonInfo = new CartoonInfo();


    private PlayerReportModel mReportModel;

    private GestureDetector mGestureDetector; //用于手势处理
    private final int FLING_MIN_DISTANCE = 1 * 150;  //判断滑动时，设置的最小滑动距离
    private final int FLING_MIN_VELOCITY = 2 * 100;  //判断滑动时，设置的最小滑动速度
    private final int PLAYER_SEEK_TIME = 5 * 1000; //设置播放器向前或向后跳5s

    /**
     * 记录播放位置
     */
    private int mLastPos = 0;
    private int mHeadPos = 0;
    private boolean isFavorite;  //是否是收藏剧集


    private PlayUIHandler mUIHandler;


    private boolean isDialogShow = false;

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
        ((KidsMindApplication) getApplicationContext()).reqUserInfo();//刷新用户VIP信息

        String quality = AppUtils.getStringSharedPreferences(this, "Quality", PlayUrlInfo.HD);
        if (quality.equals(PlayUrlInfo.FHD)) {
            qualityIndex = 1;
        }

        mContext = this;
        mUIHandler = new PlayUIHandler(this);
        mReportModel = new PlayerReportModel(this);   //播放汇报

        String info = getIntent().getStringExtra("info");
        mReportModel.setInfo(info);

        mSerieId = getIntent().getIntExtra("serieId", 0);
        fromHistoryEpisodeId = getIntent().getIntExtra("episodeId", 0);
        pushId = getIntent().getIntExtra("sequence", 0);

        initView();
        initAndroidPlayer();
        initPlayTimeControl();

        int type = getIntent().getIntExtra("type", 0);

        int profileId = ((KidsMindApplication) getApplication()).getProfileId();
        if (profileId == 0) {
            ((KidsMindApplication) getApplication()).reqProfile(new OnReqProfileResult());
        } else {
            initPlayList(type, mSerieId);
        }


        //语言提示
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("media", MediaPlayerService.BEFORE);
        startService(intent);
    }


    private class OnReqProfileResult implements OnSucessListener {
        @Override
        public void sucess() {
            int type = getIntent().getIntExtra("type", 0);
            initPlayList(type, mSerieId);
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
                && !StringUtils.isEmpty(mVideoSource) && !isDialogShow) {

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
        mReportModel.report();

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
            if (mSerieId > 0 && mPlayingEpisodeId > 0) {
                AppUtils.setStringSharedPreferences(mContext, "PLAY_RECORD" + mSerieId,
                        mPlayingEpisodeId + "#" + mLastPos + "#" + isFavorite + "#" + langIndex);
                AppUtils.saveSmartPlayStatus(mContext, mPlayingEpisodeId, isFavorite);
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
            BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_PAUSE, getString(R.string.free_pause));
        } else {
            playingStart();
            mReportModel.pauseEnd();
            BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_PLAY, getString(R.string.free_play));
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


        BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_SEEK, getString(R.string.free_seek));
    }


    private void initPlayList(int type, int serieId) {
        mStartIndex = 0;
        int offset = 1000;

        switch (type) {
            case FROM_NORMAL: // 普通点播进入
                reqPlayListInfo(serieId, mStartIndex, offset);
                break;
            case FROM_NARTOON_PEOPLE: // 动画人物进入
                reqPlayListInfo(serieId, mStartIndex, offset);
                break;
            case FROM_FAVORITE: // 收藏进入
                reqFavoriteList(mStartIndex, offset);
                break;
            case FROM_UGC: // UGC进入
                reqPlayListInfo(serieId, mStartIndex, offset);
                break;
            case FROM_HOSTORY: // 播放历史进入
                reqPlayListInfo(serieId, mStartIndex, offset);
                break;
            case FROM_TOPIC_PLAY:// 播放专题
                reqTopicPlayList(serieId, mStartIndex, offset);
                break;
            default:
                break;
        }
    }


    /**
     * 初始化控件
     */
    private void initView() {

        mVideoView = (VideoView) findViewById(R.id.video_view);
        imgLoadingPlay = (ImageView) findViewById(R.id.img_loading);
        mSpeed = (TextView) findViewById(R.id.speed);
        tvEpisodeName = (TextView) findViewById(R.id.tv_episode_name);
        btnLock = (ImageButton) findViewById(R.id.btn_play_lock);
        btnShare = (ImageButton) findViewById(R.id.btn_share);
        btnUnLock = (ImageButton) findViewById(R.id.btn_play_unlock);
        btnBack = (ImageButton) findViewById(R.id.btn_play_return);
        adView = (ImageView) findViewById(R.id.ad_bar);
        adView.setOnClickListener(this);
        btnUnLock.setOnLongClickListener(this);
        btnLock.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        btnQuality = (TextView) findViewById(R.id.btn_quality);
        btnQuality.setOnClickListener(this);
        if (qualityIndex == 1) {
            btnQuality.setText(R.string.text_chaoqing);
        }

        mListView = (RecyclerView) findViewById(R.id.listview);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(layoutManager);

        // 收藏
        btnCollect = (ImageButton) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);

        mPlaybtn = (ImageButton) findViewById(R.id.btn_play);
        mPlaybtn.setOnClickListener(this);

        btnLang = (TextView) findViewById(R.id.btn_lang);
        btnLang.setOnClickListener(this);

        mController = (LinearLayout) findViewById(R.id.controlbar);
        mProgress = (SeekBar) findViewById(R.id.media_progress);

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
            } else if (mListView.getVisibility() == View.VISIBLE
                    || mController.getVisibility() == View.VISIBLE) {
                mListView.setVisibility(View.GONE);
                mController.setVisibility(View.GONE);
                btnLock.setVisibility(View.GONE);
                btnShare.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
            } else {
                setResult(KidsMindFragmentActivity.RESULT_MODEL_A_BACK);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return mGestureDetector.onTouchEvent(event);

    }

    private void setUnLockButtonVisible() {
        btnUnLock.setVisibility(View.VISIBLE);
        mUIHandler.sendEmptyMessageDelayed(UI_EVENT_HIDE_UNLOCK_BUTTON, 4000); //4秒后自动隐藏
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
        if (id == R.id.btn_collect) {
            if (isFavorite) {
                reqRemoveFavorite(new int[]{mPlayingEpisodeId});
                BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_REMOVE_FAVORITE, getString(R.string.free_remove_favorite));
            } else {
                reqAddFavorite(mPlayingEpisodeId);
                BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_ADD_FAVORITE, getString(R.string.free_add_favorite));
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
            BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_SET_PLAYTIME, getString(R.string.free_set_playtime));

        } else if (id == R.id.btn_lang) {

            if (!((KidsMindApplication) getApplication()).isVip()) {
                getmBuyVipDialog().show();
                playingPause();
                mBuyVipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        boolean isVip = ((KidsMindApplication) getApplication()).isVip();
                        if (isVip) {
                            switchLang();
                        } else {
                            if (!mCartoonInfo.trial
                                    && mUIHandler.hasMessages(UI_EVENT_NEED_BUY_VIP))
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

        }
        /*else if (id == R.id.btn_parrents) {
            if (!AppUtils.isRepeatClick()) {
                playingPause();
                goToParent();
                BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_PARENT, getString(R.string.free_parent));
            }
        }*/
        else if (id == R.id.btn_play_lock) {
            isLocked = true;
            setUnLockButtonVisible();
            fullScreenPlaying();
        } else if (id == R.id.btn_play_return) {
            setResult(KidsMindFragmentActivity.RESULT_MODEL_A_BACK);
            finish();
        } else if (id == R.id.btn_quality) {
            if (null == mPlayUrlInfo) return;
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

        } else if (id == R.id.ad_bar) {

            openUrl();
            BaiduUtils.onEvent(mContext, OnEventId.CLICK_HOVER_TOY,
                    getString(R.string.click_hover_toy));

        } else if (id == R.id.btn_share) {
            final String shareUrl = mPlayUrlInfo.getShareUrl();

            if (shareUrl != null
                    && mCartoonInfo.shareImgUrl != null
                    && mCartoonInfo.shareTitle != null) {
                IWXAPI api = WXAPIFactory.createWXAPI(mContext, WXEntryActivity.WX_APP_ID, false);
                if (!api.isWXAppInstalled()) {
                    Toast.makeText(mContext, "未发现有安装微信客户端", Toast.LENGTH_SHORT).show();
                    return;
                }
                Glide.with(this).load(mCartoonInfo.shareImgUrl).asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                    glideAnimation) {
                                Intent intent = new Intent(mContext,
                                        WXEntryActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("wx_url", shareUrl);
                                bundle.putString("wx_title", mCartoonInfo.shareTitle + "|童乐幼教动画");
                                bundle.putParcelable("wx_bitmap", resource);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
            }
        }
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
     * 切换语言
     */
    private void switchLang() {
        langIndex++;
        String lang = setLang(mPlayUrlInfo);
        BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_CHINESE, getString(R.string.free_chinese));

        mLastPos = getCurrentPosition();  //记录播放位置
        String source = mPlayUrlInfo.getPlayUrl();

        playVideo(source, mPlayingEpisodeId);

        /*int profileId = ((KidsMindApplication) getApplication()).getProfileId();  //切换语言，取消汇报
        mReportModel.init(profileId, mPlayingEpisodeId, lang, mPlayUrlInfo.getBehaviorPlayId());*/

        mReportModel.setAudioLang(lang);

        mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
    }

    /**
     * 显示剧集列表
     */
    private void showSerieList() {
        Animation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        // 动画集
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);

        mListView.setVisibility(View.VISIBLE);

        // 设置动画时间 (作用到每个动画)
        set.setDuration(200);
        mListView.startAnimation(set);

    }

    //显示播放条
    private void showControlBar() {
        Animation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        // 动画集
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);

        mController.setVisibility(View.VISIBLE);

        // 设置动画时间 (作用到每个动画)
        set.setDuration(200);
        mController.startAnimation(set);
    }

    // 显示上面的部分
    private void showTopBar() {
        Animation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        // 动画集
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);

        tvEpisodeName.setVisibility(View.VISIBLE);
        btnLock.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        set.setDuration(200);
        tvEpisodeName.startAnimation(set);
        btnLock.startAnimation(set);
        btnBack.startAnimation(set);
        // 设置动画时间 (作用到每个动画)
        if (mPlayUrlInfo != null && mPlayUrlInfo.isCanShare()) {
            btnShare.setVisibility(View.VISIBLE);
            btnShare.startAnimation(set);
        }
    }

    private void halfScreenPlaying() {
        showControlBar();
        showSerieList();
        showTopBar();
    }

    /**
     * 全屏播放
     */
    private void fullScreenPlaying() {
        mController.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        tvEpisodeName.setVisibility(View.GONE);
        btnLock.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);
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


    /**
     * 1.21 获取收藏列表
     *
     * @param
     * @param start
     * @param offset
     */
    private void reqFavoriteList(final int start, final int offset) {
        String url = AppConfig.FAVORITE_LIST;
        FavoriteRequest request = new FavoriteRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        request.setStartIndex(start);
        request.setOffset(offset);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                FavoriteResponse resp = GsonUtil.parse(response,
                        FavoriteResponse.class);
                if (resp.isSucess()) {

                    int count = resp.getCounts();

                    if (count == 0) {
                        CustomDialog customDialog = new CustomDialog(mContext);
                        customDialog.show();
                        customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                        Toast.makeText(mContext, R.string.favorite_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }


                    List<FavoriteItem> lists = resp.getList();

                    if (lists != null && lists.size() > 0) {
                        int episodeId = 0;
                        String record = AppUtils.getStringSharedPreferences(mContext, "PLAY_RECORD" + mSerieId, "");
                        if (!StringUtils.isEmpty(record)) {
                            String[] strs = record.split("#");
                            if (strs != null && strs.length == 4) {
                                episodeId = Integer.parseInt(strs[0]);
                                mLastPos = Integer.parseInt(strs[1]);
                                //isFavorite = Boolean.parseBoolean(strs[2]);
                                langIndex = Integer.parseInt(strs[3]);
                            }
                        } else {
                            episodeId = 0;
                            mLastPos = 0;
                            //isFavorite = Boolean.parseBoolean(strs[2]);
                            langIndex = 0;
                        }


                        FavoriteItem item = lists.get(0);

                        for (FavoriteItem favItem : lists) {
                            if (favItem.getEpisodeId() == episodeId) {
                                item = favItem;
                                break;
                            }
                        }

                        mFavoriteAdapter = new FavoriteAdapter(mContext, lists);
                        mListView.setAdapter(mFavoriteAdapter);
                        int index = lists.indexOf(item);

                        isFavorite = true;
                        btnCollect.setImageResource(R.drawable.btn_free_favorited);

                        reqPlayUrl(item);
                        mFavoriteAdapter.setSelectItem(index);


                    }
                }


            }

        });
    }

    /**
     * 1.18 获取总剧集详细内容
     *
     * @param serieId
     * @param start
     * @param offset
     */
    private void reqPlayListInfo(final int serieId, final int start, final int offset) {
        String url = AppConfig.SERIE_INFO;
        SerieInfoRequest request = new SerieInfoRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        request.setSerieId(serieId);
        request.setStartIndex(start);
        request.setOffset(offset);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                SerieInfoResponse resp = GsonUtil.parse(response,
                        SerieInfoResponse.class);
                if (resp.isSucess()) {

                    SerieInfo sInfo = resp.getData();
                    List<SerieItem> lists = sInfo.getList();

                    if (lists != null && lists.size() > 0) {

                        int episodeId = 0;
                        String record = AppUtils.getStringSharedPreferences(mContext, "PLAY_RECORD" + mSerieId, "");
                        if (!StringUtils.isEmpty(record)) {
                            String[] strs = record.split("#");
                            if (strs != null && strs.length == 4) {
                                episodeId = Integer.parseInt(strs[0]);
                                mLastPos = Integer.parseInt(strs[1]);
                                //isFavorite = Boolean.parseBoolean(strs[2]);
                                langIndex = Integer.parseInt(strs[3]);

                            }
                        } else {
                            episodeId = 0;
                            mLastPos = 0;
                            //isFavorite = Boolean.parseBoolean(strs[2]);
                            langIndex = 0;
                        }


                        SerieItem item = lists.get(0);
                        if (pushId == 0) {
                            for (SerieItem serieItem : lists) {
                                int epid = serieItem.getEpisodeId();
                                if (fromHistoryEpisodeId != 0) {
                                    if (epid == fromHistoryEpisodeId) {
                                        item = serieItem;
                                        break;
                                    }
                                } else {
                                    if (epid == episodeId) {
                                        item = serieItem;
                                        break;
                                    }
                                }
                            }
                        } else {
                            item = lists.get(pushId - 1);
                        }

                        mFreePlayAdapter = new FreePlayAdapter(mContext, lists, sInfo.isEnd());
                        mListView.setAdapter(mFreePlayAdapter);
                        int index = lists.indexOf(item);

                        isFavorite = item.isFavorite();
                        if (isFavorite) {
                            btnCollect.setImageResource(R.drawable.btn_free_favorited);
                        } else {
                            btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                        }

                        reqPlayUrl(item);
                        mFreePlayAdapter.setSelectItem(index);

                    }
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    private void reqTopicPlayList(final int topicId, final int start, final int offset) {
        String url = AppConfig.PLAY_TOPIC_INFO;
        SerieInfoRequest request = new SerieInfoRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        request.setTopicId(topicId);
        request.setStartIndex(start);
        request.setOffset(offset);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                SerieInfoResponse resp = GsonUtil.parse(response,
                        SerieInfoResponse.class);
                if (resp.isSucess()) {

                    List<SerieItem> lists = resp.getData().getList();
                    if (lists != null && lists.size() > 0) {

                        int episodeId = 0;
                        String record = AppUtils.getStringSharedPreferences(mContext, "PLAY_RECORD" + mSerieId, "");
                        if (!StringUtils.isEmpty(record)) {
                            String[] strs = record.split("#");
                            if (strs != null && strs.length == 4) {
                                episodeId = Integer.parseInt(strs[0]);
                                mLastPos = Integer.parseInt(strs[1]);
                                //isFavorite = Boolean.parseBoolean(strs[2]);
                                langIndex = Integer.parseInt(strs[3]);

                            }
                        } else {
                            episodeId = 0;
                            mLastPos = 0;
                            //isFavorite = Boolean.parseBoolean(strs[2]);
                            langIndex = 0;
                        }


                        SerieItem item = lists.get(0);

                        for (SerieItem serieItem : lists) {
                            int epid = serieItem.getEpisodeId();
                            if (epid == fromHistoryEpisodeId
                                    || epid == episodeId) {
                                item = serieItem;
                                break;
                            }
                        }

                        mFreePlayAdapter = new FreePlayAdapter(mContext, lists);
                        mListView.setAdapter(mFreePlayAdapter);
                        int index = lists.indexOf(item);

                        isFavorite = item.isFavorite();
                        if (isFavorite) {
                            btnCollect.setImageResource(R.drawable.btn_free_favorited);
                        } else {
                            btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                        }

                        reqPlayUrl(item);
                        mFreePlayAdapter.setSelectItem(index);

                    }

                }

            }

        });
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

        boolean isVip = ((KidsMindApplication) mContext.getApplicationContext()).isVip();
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

    public void reqPlayUrl(FavoriteItem favoriteItem) {

        mReportModel.report();

        mCartoonInfo.trial = favoriteItem.isTrial();
        mCartoonInfo.shareImgUrl = favoriteItem.getImgUrl();
        mCartoonInfo.shareTitle = favoriteItem.getSerieName();

        setVipInfo(mCartoonInfo.trial);
        int episodeId = favoriteItem.getEpisodeId();
        btnCollect.setImageResource(R.drawable.btn_free_favorited);

        reqPlayUrl(episodeId);
    }

    public void reqPlayUrl(SerieItem serieItem) {

        mReportModel.report();

        mCartoonInfo.trial = serieItem.isTrial();
        mCartoonInfo.shareImgUrl = serieItem.getImgUrl();
        mCartoonInfo.shareTitle = serieItem.getTitle();

        setVipInfo(mCartoonInfo.trial);

        int episodeId = serieItem.getEpisodeId();

        isFavorite = serieItem.isFavorite();
        if (isFavorite) {
            btnCollect.setImageResource(R.drawable.btn_free_favorited);
        } else {
            btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
        }
        reqPlayUrl(episodeId);
    }

    private void setEpisodeName(int episodeId) {
        RecyclerView.Adapter adapter = mListView.getAdapter();
        if (adapter instanceof FreePlayAdapter) {
            List<SerieItem> list = ((FreePlayAdapter) adapter).getLists();
            for (SerieItem item : list) {
                if (item.getEpisodeId() == episodeId) {
                    tvEpisodeName.setText(item.getTitle());
                }
            }

        } else if (adapter instanceof FavoriteAdapter) {
            List<FavoriteItem> list = ((FavoriteAdapter) adapter).getLists();
            for (FavoriteItem item : list) {
                if (item.getEpisodeId() == episodeId) {
                    tvEpisodeName.setText(item.getEpisodeName());
                }
            }
        }
    }


    /**
     * 1.11 获取播放地址
     *
     * @param episodeId
     */
    private void reqPlayUrl(final int episodeId) {
        String url = AppConfig.PLAY_URL;
        PlayUrlRequest request = new PlayUrlRequest();
        final int profileId = ((KidsMindApplication) getApplication()).getProfileId();
        int behaviorId = ((KidsMindApplication) getApplication()).getBehaviorId();
        String token = ((KidsMindApplication) getApplication()).getToken();

        request.setToken(token);
        request.setProfileId(profileId);
        request.setEpisodeId(episodeId);
        request.setBehaviorId(behaviorId);

        String json = request.toJsonString();
        mReportModel.startReqUrlTime();
        setEpisodeName(episodeId);
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                PlayUrlResponse resp = GsonUtil.parse(response,
                        PlayUrlResponse.class);
                if (resp.isSucess()) {
                    imgLoadingPlay.setVisibility(View.VISIBLE);
                    AnimationDrawable anim = (AnimationDrawable) (imgLoadingPlay.getBackground());
                    anim.start();
                    mHeadPos = resp.getData().getPrologue();
                    mPlayUrlInfo = resp.getData();

                    String lang = setLang(mPlayUrlInfo);
                    setQuality(mPlayUrlInfo);

                    String source = mPlayUrlInfo.getPlayUrl();
                    playVideo(source, episodeId);

                    mReportModel.init(profileId, episodeId, lang, mPlayUrlInfo.getBehaviorPlayId());
                    mReportModel.setReqUrlTime();


                    if (mPlayUrlInfo.isHasShopping()) {
                        setAdInfo(mPlayUrlInfo);
                    }
                    if (mPlayUrlInfo.isCanShare()) {
                        btnShare.setVisibility(View.VISIBLE);
                    } else {
                        btnShare.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    private void setAdInfo(PlayUrlInfo info) {
        String adImgUrl = info.getAdImgUrl();

        if (adImgUrl != null) {
            Glide.with(this).load(adImgUrl)
                    .centerCrop()
                    .into(adView);
        }
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
    private void reqAddFavorite(final int episodeId) {
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
                    RecyclerView.Adapter adapter = mListView.getAdapter();
                    if (adapter instanceof FreePlayAdapter) {
                        ((FreePlayAdapter) adapter).setFavoritedByEpisodeId(episodeId, true);
                    }
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

                    RecyclerView.Adapter adapter = mListView.getAdapter();
                    if (adapter instanceof FavoriteAdapter) {

                        showSerieList();

                        List<FavoriteItem> list = ((FavoriteAdapter) adapter).getLists();

                        List<FavoriteItem> saveList = new ArrayList<FavoriteItem>();
                        saveList.addAll(list);

                        for (int i = 0; i < episodeId.length; i++) {
                            int id = episodeId[i];
                            for (FavoriteItem item : saveList) {
                                if (item.getEpisodeId() == id) {
                                    mFavoriteAdapter.removeItem(item);
                                    mFavoriteAdapter.setSelectItem(-1);  //用户remove，将取消select
                                    continue;
                                }
                            }
                        }

                    }

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


    private void judgeAdBar(int time) {
        List<Map<String, Integer>> adTimeInfo = null;
        if (null != mPlayUrlInfo) {
            adTimeInfo = mPlayUrlInfo.getAdTimeInfo();
        }
        if (adTimeInfo != null) {
            int currentTime = time / 1000;
            boolean isBelongTime = false;
            for (Map<String, Integer> showTime : adTimeInfo) {
                int startTime = showTime.get("start");
                int duration = showTime.get("duration");
                int endTime = startTime + duration;
                if (startTime < currentTime && currentTime < endTime) {
                    //先选中范围
                    isBelongTime = true;
                }
            }
            showAdBar(isBelongTime);
        }
    }


    private void showAdBar(boolean isBelong) {
        if (isBelong && !isBarShow() && adView.getVisibility() != View.VISIBLE) {
            TranslateAnimation translateAnimation =
                    new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f);
            translateAnimation.setDuration(200);
            translateAnimation.setInterpolator(new OvershootInterpolator());
            adView.setVisibility(View.VISIBLE);
            adView.startAnimation(translateAnimation);
        } else if (!isBelong || isBarShow()) {
            adView.setVisibility(View.GONE);
        }
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

                    judgeAdBar(currPosition);

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
                        List<SerieItem> list = ((FreePlayAdapter) adapter).getLists();
                        if (ListUtils.isEmpty(list)) {
                            return;
                        }

                        int index = ((FreePlayAdapter) adapter).getSelectItem();//获取当前选择项

                        index = (index + 1) % list.size(); //下一集

                        SerieItem item = list.get(index);

                        if (item.isFavorite()) {
                            btnCollect.setImageResource(R.drawable.btn_free_favorited);
                        } else {
                            btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                        }

                        reqPlayUrl(item);
                        ((FreePlayAdapter) adapter).setSelectItem(index);

                    } else if (adapter instanceof FavoriteAdapter) {
                        List<FavoriteItem> list = ((FavoriteAdapter) adapter).getLists();
                        if (ListUtils.isEmpty(list)) {
                            mPlaytime.pause();
                            return;
                        }

                        int index = ((FavoriteAdapter) adapter).getSelectItem();//获取当前选择项

                        index = (index + 1) % list.size(); //下一集

                        FavoriteItem item = list.get(index);

                        btnCollect.setImageResource(R.drawable.btn_free_favorited);  //我的收藏全部为收藏属性

                        reqPlayUrl(item);
                        ((FavoriteAdapter) adapter).setSelectItem(index);
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
                case UI_EVENT_HIDE_UNLOCK_BUTTON:
                    btnUnLock.setVisibility(View.GONE);
                    break;
                case UI_EVENT_SHOW_ADV:
                    //暂停状态状态请求广告
                    if (!mVideoView.isPlaying()) {
                        final PlayPauseDialog.onDismissListener listener = new PlayPauseDialog.onDismissListener() {
                            @Override
                            public void dismiss(boolean ifPlay) {
                                if (ifPlay) {
                                    // playingStartOrPause();
                                    openUrl();
                                    BaiduUtils.onEvent(mContext, OnEventId.CLICK_PAUSE_TOY,
                                            getString(R.string.click_pause_toy));

                                }
                            }
                        };
                        boolean b = null == mPlayUrlInfo ? false : mPlayUrlInfo.isHasShopping();
                        if (b) {
                            String url = mPlayUrlInfo.getPauseImgUrl();
                            Glide.with(mContext).load(url).asBitmap()
                                    .into(new SimpleTarget<Bitmap>(984, 610) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                                                glideAnimation) {
                                            PlayPauseDialog playDialog = new PlayPauseDialog(mContext, resource);
                                            playDialog.setOnDismissListener(listener);
                                            playDialog.show();
                                        }
                                    });

                        } else {
                            AppUtils.getInstance().reqAction(mBuyVipDialog, mTarget.get(), listener);
                        }


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
                case UI_EVENT_NEED_BUY_VIP:
                    getmBuyVipDialog().show();
                    playingPause();
                    mBuyVipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            boolean isVip = ((KidsMindApplication) getApplication()).isVip();
                            if (isVip) {
                                mPlaybtn.setOnClickListener(FreePlayingActivity.this);
                                playingStart();
                            } else {
                                mPlaybtn.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!mUIHandler.hasMessages(UI_EVENT_NEED_BUY_VIP))
                                            mUIHandler.sendEmptyMessageDelayed(UI_EVENT_NEED_BUY_VIP, 500);
                                    }
                                });

                                mUIHandler.sendEmptyMessageDelayed(UI_EVENT_NEED_BUY_VIP, 5 * 1000);
                            }
                        }
                    });


                    break;
                default:
                    break;
            }
        }

    }

    private void openUrl() {
        String shoppingUrl = mPlayUrlInfo.getShoppingUrl();
        if (shoppingUrl != null) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(shoppingUrl);
            intent.setData(content_url);
            startActivity(intent);

            AppUtils.ADClick(mContext, mPlayingEpisodeId);  //汇报
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
