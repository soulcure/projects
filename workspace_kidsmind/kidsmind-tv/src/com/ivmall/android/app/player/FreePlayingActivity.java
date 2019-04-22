package com.ivmall.android.app.player;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.ParentFragmentActivity;
import com.ivmall.android.app.adapter.FavoriteAdapter;
import com.ivmall.android.app.adapter.FreePlayAdapter;
import com.ivmall.android.app.adapter.MusicPlayAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.debug.CrashHandler;
import com.ivmall.android.app.dialog.AnswerQuestionDialog;
import com.ivmall.android.app.dialog.BuyVipDialog;
import com.ivmall.android.app.dialog.FirstRecommDialog;
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
import com.ivmall.android.app.receiver.ControlHandlerReceiver;
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
import com.ivmall.android.app.views.VideoViewCustom;
import com.smit.android.ivmall.stb.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FreePlayingActivity extends Activity implements OnClickListener {

    public static final String TAG = FreePlayingActivity.class.getSimpleName();
    private static final int UPDATE_CURRPOSITION_DELAY_TIME = 200;

    public static final int FROM_NORMAL = 0; // 普通点播进入
    public static final int FROM_NARTOON_PEOPLE = 1; // 动画人物进入
    public static final int FROM_FAVORITE = 2; // 收藏进入
    public static final int FROM_UGC = 3; // UGC进入
    public static final int FROM_HOSTORY = 4; // 播放历史进入
    public static final int FROM_MUSIC_PLAY = 5; // 播放mp3儿歌
    public static final int FROM_TOPIC_PLAY = 6; // 播放专题
    public static final int FROM_REMOTE_PLAY = 7; // 远程定制列表

    private Context mContext;
    /**
     * **UI控件***
     */

    private VideoViewCustom mVideoView = null;


    private BuyVipDialog mBuyVipDialog;
    private TextView mAudiotitle;
    private RelativeLayout rel_music;

    private ImageView imgLoadingPlay;
    private TextView mSpeed;
    private TextView tvEpisodeName;

    private ImageButton mPlaybtn = null;

    private LinearLayout mController = null;
    private RecyclerView mListView = null;

    private FreePlayAdapter mFreePlayAdapter;
    private FavoriteAdapter mFavoriteAdapter;
    private MusicPlayAdapter mMusicPlayAdapter;

    private SeekBar mProgress = null;
    private TextView mDuration = null;
    private TextView mCurrPostion = null;

    private CustomDigitalClock mPlaytime = null;

    private ImageButton btnLang;
    private ImageButton btnParrents;
    private ImageButton btnCollect;
    private ImageButton btnCycle;
    private boolean isCycle;  //是否单曲循环播放
    private ImageButton btnQuality;
    private ImageView adView;

    private int mStartIndex; // 获取剧集条目的索引

    private int fromHistoryEpisodeId; // 从播放历史返回的播放id
    private int mPlayingEpisodeId;


    private String mVideoSource = null;

    private int langIndex = 0;
    private int qualityIndex = 0;
    private int spareIndex = 0; //切换备用视频播放地址

    private PlayUrlInfo mPlayUrlInfo;
    private CartoonInfo mCartoonInfo = new CartoonInfo();

    private long mTouchTime;
    private PlayerReportModel mReportModel;

    /**
     * 记录播放位置
     */
    private int mLastPos = 0;
    private int mHeadPos = 0;
    private boolean isFavorite;  //是否是收藏剧集


    private int mSerieId;

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

    private final int UI_EVENT_NEED_BUY_VIP = 12; // 提示购买VIP权限才能观看


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

        if (type != FROM_MUSIC_PLAY) {
            //语言提示
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.putExtra("media", MediaPlayerService.BEFORE);
            startService(intent);
        }

        ((KidsMindApplication) getApplication()).addActivity(this);
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
            mPlaybtn.setVisibility(View.GONE);
        } else if (mLastPos != 0
                && !StringUtils.isEmpty(mVideoSource)) {

            playingStart();

            showControlBar();

            //startTimer();
            mPlaybtn.setVisibility(View.GONE);


            if (rel_music.getVisibility() == View.VISIBLE
                    && rel_music.getAlpha() != 1.0F) {
                rel_music.setAlpha(1.0F);
            }

        } else if (mPlaybtn.getVisibility() == View.VISIBLE) {
            mPlaybtn.requestFocus();
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
        mPlaybtn.setVisibility(View.GONE);
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
            mController.setVisibility(View.VISIBLE);
            mPlaybtn.setVisibility(View.VISIBLE);
            tvEpisodeName.setVisibility(View.VISIBLE);
            mPlaybtn.requestFocus();
            mPlaybtn.setImageResource(R.drawable.btn_free_start);
            if (rel_music.getVisibility() == View.VISIBLE) {
                rel_music.setAlpha(0.2F);
            }
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
            mController.setVisibility(View.GONE);
            mPlaybtn.setVisibility(View.GONE);
            tvEpisodeName.setVisibility(View.GONE);
            if (mController.getVisibility() == View.VISIBLE) {
                mProgress.requestFocus();
            }
            mPlaybtn.setImageResource(R.drawable.btn_free_pause);
            if (rel_music.getVisibility() == View.VISIBLE) {
                rel_music.setAlpha(1.0F);
            }
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
            case FROM_MUSIC_PLAY://播放mp3儿歌
                reqMusicPlayList(serieId, mStartIndex, offset);
                break;
            case FROM_TOPIC_PLAY:// 播放专题
                reqTopicPlayList(serieId, mStartIndex, offset);
                break;
            case FROM_REMOTE_PLAY: //远程播放列表
                String romoteToken = getIntent().getStringExtra("token");
                reqRemoteList(romoteToken);
                break;
            default:
                break;
        }
    }


    /**
     * 初始化控件
     */
    private void initView() {

        mVideoView = (VideoViewCustom) findViewById(R.id.video_view);
        imgLoadingPlay = (ImageView) findViewById(R.id.img_loading);
        mSpeed = (TextView) findViewById(R.id.speed);
        tvEpisodeName = (TextView) findViewById(R.id.tv_episode_name);
        adView = (ImageView) findViewById(R.id.ad_bar);

        btnQuality = (ImageButton) findViewById(R.id.btn_quality);
        btnQuality.setOnClickListener(this);
        if (qualityIndex == 1) {
            btnQuality.setBackgroundResource(R.drawable.btn_free_fhd);
        }

        mListView = (RecyclerView) findViewById(R.id.listview);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(layoutManager);

        // 收藏
        btnCollect = (ImageButton) findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(this);

        btnCycle = (ImageButton) findViewById(R.id.btn_cycle);
        btnCycle.setOnClickListener(this);

        mPlaybtn = (ImageButton) findViewById(R.id.btn_play);
        mPlaybtn.setOnClickListener(this);

        btnLang = (ImageButton) findViewById(R.id.btn_lang);
        btnLang.setOnClickListener(this);


        btnParrents = (ImageButton) findViewById(R.id.btn_parrents);
        btnParrents.setOnClickListener(this);


        mController = (LinearLayout) findViewById(R.id.controlbar);
        mProgress = (SeekBar) findViewById(R.id.media_progress);
        mProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress.requestFocus();
                if (btnCollect.getVisibility() == View.VISIBLE) {
                    mProgress.setNextFocusDownId(R.id.btn_collect);
                }
            }
        }, 200);

        mDuration = (TextView) findViewById(R.id.time_total);
        mCurrPostion = (TextView) findViewById(R.id.time_current);

        mAudiotitle = (TextView) findViewById(R.id.tv_audio);
        rel_music = (RelativeLayout) findViewById(R.id.rel_music);
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
            if (mListView.getVisibility() == View.VISIBLE
                    || mController.getVisibility() == View.VISIBLE) {
                fullScreenPlaying();
            } else {
                if (((KidsMindApplication) getApplicationContext()).isFirst()) {
                    ((KidsMindApplication) getApplicationContext()).setFirst(false);

                    FirstRecommDialog dialog = new FirstRecommDialog(mContext, mSerieId);
                    dialog.show();
                    dialog.setCallBack(new FirstRecommDialog.OnClickView() {
                        @Override
                        public void onClick(int serieId) {
                            reloadSerie(serieId);
                        }

                        @Override
                        public void onCancel() {
                            finish();
                        }
                    });

                    mVideoView.pause();
                } else {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {

            playingStartOrPause();
            if (!AppUtils.isRepeatClick()) {
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
                showControlBar();
                mListView.setVisibility(View.GONE);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            mTouchTime = System.currentTimeMillis();
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            long time = System.currentTimeMillis() - mTouchTime;
            if (time < 400) {
                if (isBarShow()) {
                    fullScreenPlaying();
                } else {
                    showControlBar();
                }
            }
        }

        return true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mUIHandler.hasMessages(UI_EVENT_FULL_SCREEN)) {
            mUIHandler.removeMessages(UI_EVENT_FULL_SCREEN);
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

        } else if (id == R.id.btn_cycle) {
            if (isCycle) {
                isCycle = false;
                btnCycle.setImageResource(R.drawable.btn_free_no_cycle);
            } else {
                isCycle = true;
                btnCycle.setImageResource(R.drawable.btn_free_cycle);
            }

        } else if (id == R.id.btn_play) {
            if (!AppUtils.isRepeatClick()) {
                playingStartOrPause();
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

        } else if (id == R.id.btn_parrents) {
            if (!AppUtils.isRepeatClick()) {
                playingPause();
                goToParent();
                BaiduUtils.onEvent(getApplicationContext(), OnEventId.FREE_PARENT, getString(R.string.free_parent));
            }
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

    //显示播放条
    private void showControlBar() {
        Animation scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        // 动画集
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        mController.setVisibility(View.VISIBLE);

        if (!mVideoSource.contains(".mp3")) {
            tvEpisodeName.setVisibility(View.VISIBLE);
        }

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
        mListView.setVisibility(View.GONE);
        tvEpisodeName.setVisibility(View.GONE);
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
        ((KidsMindApplication) getApplication()).finishActivity(this);
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
        if (mVideoSource.contains(".mp3")) {
            rel_music.setVisibility(View.VISIBLE);
            btnCycle.setVisibility(View.VISIBLE);
            btnCollect.setVisibility(View.GONE);
            mProgress.setNextFocusDownId(R.id.btn_cycle);
        } else {
            tvEpisodeName.setVisibility(View.VISIBLE);
        }


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
        questionDialog.setRightAnswerListener(new AnswerQuestionDialog.RightAnswerListener() {
            @Override
            public void doing() {
                playingStart();
                mPlaytime.setLimited(false);
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
                Intent intent = new Intent(mContext, ParentFragmentActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                        mUIHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mPlaytime.pause();
                            }
                        }, 1500);


                        btnLang.setEnabled(false);
                        btnLang.setVisibility(View.GONE);
                        btnCollect.setEnabled(false);


                        Toast.makeText(mContext, getResources().getString(R.string.favorite_empty), Toast.LENGTH_SHORT).show();
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
                } else {
                    Toast.makeText(mContext, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }


    private void reqMusicPlayList(final int serieId, final int start, final int offset) {
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

                    List<SerieItem> lists = resp.getData().getList();
                    if (lists != null && lists.size() > 0) {
                        int episodeId = 0;
                        mLastPos = 0;
                        langIndex = 0;

                        SerieItem item = lists.get(0);

                        for (SerieItem serieItem : lists) {
                            int epid = serieItem.getEpisodeId();
                            if (epid == fromHistoryEpisodeId
                                    || epid == episodeId) {
                                item = serieItem;
                                break;
                            }
                        }

                        mMusicPlayAdapter = new MusicPlayAdapter(mContext, lists);
                        mListView.setAdapter(mMusicPlayAdapter);
                        int index = lists.indexOf(item);
                        mMusicPlayAdapter.setSelectItem(index);

                        isFavorite = item.isFavorite();
                        if (isFavorite) {
                            btnCollect.setImageResource(R.drawable.btn_free_favorited);
                        } else {
                            btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                        }

                        reqPlayUrl(item);


                    }

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
     * 家长定制界面列表
     *
     * @param token
     */
    private void reqRemoteList(String token) {
        String url = AppConfig.REMOTE_LIST;
        SerieInfoRequest request = new SerieInfoRequest();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                SerieInfoResponse resp = GsonUtil.parse(response,
                        SerieInfoResponse.class);
                if (resp.isSucess()) {

                    List<SerieItem> lists = resp.getData().getList();
                    if (lists != null && lists.size() > 0) {
                        SerieItem item = lists.get(0);

                        mFreePlayAdapter = new FreePlayAdapter(mContext, lists);
                        mListView.setAdapter(mFreePlayAdapter);
                        int index = lists.indexOf(item);

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
        int serieId = serieItem.getSerieId();
        if (serieId != 0) {
            mSerieId = serieId;
        }

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
                    String episodeName = item.getTitle();
                    if (StringUtils.isEmpty(episodeName)) {
                        episodeName = item.getEpisodeName();
                    }
                    tvEpisodeName.setText(episodeName);
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
            btnLang.setImageResource(R.drawable.btn_free_chinese);
        } else {
            btnLang.setImageResource(R.drawable.btn_free_english);
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
            btnQuality.setImageResource(R.drawable.btn_free_fhd);
        } else {
            btnQuality.setImageResource(R.drawable.btn_free_hd);
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
        List<Map<String, Integer>> adTimeInfo = mPlayUrlInfo.getAdTimeInfo();
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


    private void reloadSerie(int serieId) {
        mSerieId = serieId;
        initPlayList(0, mSerieId);
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
                    showControlBar();
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
                    } else if (adapter instanceof MusicPlayAdapter) {
                        List<SerieItem> list = ((MusicPlayAdapter) adapter).getLists();
                        if (ListUtils.isEmpty(list)) {
                            return;
                        }

                        int index = ((MusicPlayAdapter) adapter).getSelectItem();//获取当前选择项
                        if (!isCycle) {
                            index = (index + 1) % list.size(); //下一集
                        }

                        SerieItem item = list.get(index);

                        if (item.isFavorite()) {
                            btnCollect.setImageResource(R.drawable.btn_free_favorited);
                        } else {
                            btnCollect.setImageResource(R.drawable.btn_free_no_favorite);
                        }

                        /*if (!item.isTrial() && !isVip) {
                            showBuyVipDialog(item.getEpisodeId(), index);
                        } else {
                            reqPlayUrl(mProfileId, item.getEpisodeId());
                            ((FreePlayAdapter) adapter).setSelectItem(index);
                        }*/
                        mAudiotitle.setText(item.getTitle());
                        reqPlayUrl(item);
                        ((MusicPlayAdapter) adapter).setSelectItem(index);
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
                        final PlayPauseDialog.onDismissListener listener = new PlayPauseDialog.onDismissListener() {
                            @Override
                            public void dismiss(boolean ifPlay) {
                                if (ifPlay) {
                                    playingStartOrPause();
                                }
                            }
                        };

                        boolean b = mPlayUrlInfo.isHasShopping();
                        if (b) {
                            String url = mPlayUrlInfo.getPauseImgUrl();
                            Glide.with(mContext).load(url).asBitmap()
                                    .into(new SimpleTarget<Bitmap>(1227, 930) {
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

    class CartoonInfo {
        private String shareImgUrl;
        private String shareTitle;
        private boolean trial;
    }
}
