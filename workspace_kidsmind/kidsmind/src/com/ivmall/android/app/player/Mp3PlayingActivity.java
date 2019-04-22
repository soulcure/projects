package com.ivmall.android.app.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.adapter.MusicPlayAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.SerieInfoRequest;
import com.ivmall.android.app.entity.SerieInfoResponse;
import com.ivmall.android.app.entity.SerieItem;
import com.ivmall.android.app.service.MediaPlayerService;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

import java.util.List;

/**
 * Created by Markey on 2015/10/19.
 * mp3 播放
 */
public class Mp3PlayingActivity extends Activity implements View.OnClickListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        SeekBar.OnSeekBarChangeListener ,View.OnTouchListener{

    private Context mContext;
    private int mStartIndex = 0;
    private int mSerieId;
    private ImageView imgLoadingPlay;
    private KidsMindApplication application;
    private RecyclerView mListView = null;
    private LinearLayout linear_bottom;
    private RelativeLayout relative_bg;
    private MusicPlayAdapter mMusicPlayAdapter;
    private SeekBar seekbar;
    private ImageButton btn_cycle;
    private ImageButton btn_psOrpy;
    private ImageButton btn_back;
    private TextView tv_audio;
    private TextView time_current;
    private TextView time_total;
    //更新bar
    private int UPDATE_PROGRESS = 1;
    //更新bottom
    private int UPDATE_BOTTOM = 2;
    //是否单曲循环
    private boolean islooping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp3play_activity);

        mContext = this;

        application = (KidsMindApplication) getApplication();
        application.getMpPlay().setOnCompletionListener(this);
        application.getMpPlay().setOnPreparedListener(this);

        mSerieId = getIntent().getIntExtra("serieId", 0);

        initView();

        initPlayList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新Button
        updatePauseBtn();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        imgLoadingPlay = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.loading_play, null);
        mListView = (RecyclerView) findViewById(R.id.listview);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(layoutManager);
        linear_bottom = (LinearLayout) findViewById(R.id.linear_bottom);

        relative_bg = (RelativeLayout) findViewById(R.id.relative_bg);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        relative_bg.addView(imgLoadingPlay, params);
        imgLoadingPlay.setVisibility(View.GONE);

        seekbar = (SeekBar) findViewById(R.id.seekbar);
        btn_cycle = (ImageButton) findViewById(R.id.btn_cycle);
        btn_psOrpy = (ImageButton) findViewById(R.id.btn_psOrpy);
        btn_back = (ImageButton) findViewById(R.id.btn_play_return);
        tv_audio = (TextView) findViewById(R.id.tv_audio);
        time_current = (TextView) findViewById(R.id.time_current);
        time_total = (TextView) findViewById(R.id.time_total);

        seekbar.setOnSeekBarChangeListener(this);
        relative_bg.setOnClickListener(this);
        btn_cycle.setOnClickListener(this);
        btn_psOrpy.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void initPlayList() {
        mStartIndex = 0;
        int offset = 1000;
        //播放mp3儿歌
        reqMusicPlayList(mStartIndex, offset);

        if (!application.getMpPlay().isPlaying()) {
            //语言提示
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.putExtra("media", MediaPlayerService.BEFORE);
            startService(intent);
        } else {
            seekbar.setMax(application.getMpPlay().getDuration());
            Message msg = handler.obtainMessage();
            handler.sendMessage(msg);
        }
    }

    /**
     * 请求MP3 播放剧集
     */
    private void reqMusicPlayList(int mStartIndex, int offset) {
        String url = AppConfig.SERIE_INFO;
        SerieInfoRequest request = new SerieInfoRequest();

        String token = ((KidsMindApplication) getApplication()).getToken();
        request.setToken(token);
        request.setSerieId(mSerieId);
        request.setStartIndex(mStartIndex);
        request.setOffset(offset);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                SerieInfoResponse resp = GsonUtil.parse(response,
                        SerieInfoResponse.class);
                if (resp.isSucess()) {
                    List<SerieItem> lists = resp.getData().getList();
                    mMusicPlayAdapter = new MusicPlayAdapter(mContext, lists);
                    mListView.setAdapter(mMusicPlayAdapter);

                    mListView.setVisibility(View.GONE);
                    linear_bottom.setVisibility(View.GONE);
                    relative_bg.performClick();

                    application.setLists(lists);
                    String history = AppUtils.getStringSharedPreferences(Mp3PlayingActivity.this,
                            application.MP3_HISTORY, "none");
                    if (history.equals("none")) {
                        readlyPlay(0);
                    } else {
                        String infos[] = history.split("#");
                        //进入的是相同的剧集
                        if (infos[0].equals(mSerieId + "")) {
                            for (int i = 0; i < lists.size(); i++) {
                                if (infos[1].equals(lists.get(i).getEpisodeId() + "")) {
                                    if (!application.getMpPlay().isPlaying()) {
                                        readlyPlay(i);

                                    }
                                }
                            }
                        } else {
                            readlyPlay(0);
                        }
                    }
                }
            }
        });
    }

    /**
     * 准备播放
     */
    public void readlyPlay(int i) {
        application.readlyPlay(i, mSerieId);

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

        // 设置动画时间 (作用到每个动画)
        set.setDuration(200);
        mListView.startAnimation(set);

        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.requestFocus();
            }
        }, 200);

        mMusicPlayAdapter.setSelectItem(application.getIndex());
        mListView.scrollToPosition(application.getIndex());
        mListView.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_previous:
//                //上一集
//                application.mpPrevious();
//                break;
            case R.id.btn_psOrpy:
                //暂停
                application.startOrPlay();
                //更新Button
                updatePauseBtn();
                break;
            case R.id.btn_play_return:
                onBackPressed();
                break;
//            case R.id.btn_next:
//                //下一集
//                application.mpNext();
//                break;
//            case R.id.btn_all:
//                //展开列表
//                showSerieList();
//                break;
            case R.id.btn_cycle:
                //单曲循环
                if (islooping) {
                    islooping = false;
                    application.getMpPlay().setLooping(false);
                    //btn_cycle.setImageResource(R.drawable.btn_free_no_cycle);
                } else {
                    islooping = true;
                    application.getMpPlay().setLooping(true);
                    //btn_cycle.setImageResource(R.drawable.btn_free_cycle);
                }
                break;
            case R.id.relative_bg:
                hideOrShow();
                break;

        }
    }

    /**
     * 更新暂停 播放Button 的UI
     */
    private void updatePauseBtn() {
        if (application.getMpPlay().isPlaying()) {
            btn_psOrpy.setImageResource(R.drawable.btn_free_pause);
        } else {
            btn_psOrpy.setImageResource(R.drawable.btn_free_start);
        }
    }

    private void hideOrShow(){
        if(linear_bottom.getVisibility()==View.VISIBLE){
            hideListBottom();
        }else{
            showListBottom();
        }
    }

    /**
     * 显示右侧的list
     * 和下方的bottom UI
     */
    private void showListBottom() {
        if(linear_bottom.getVisibility()==View.GONE){
            linear_bottom.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            showSerieList();
        }
        //关闭
        if (handler.hasMessages(UPDATE_BOTTOM)) {
            handler.removeMessages(UPDATE_BOTTOM);
        }
        Message msg = new Message();
        msg.what = UPDATE_BOTTOM;
        handler.sendMessageDelayed(msg, 5000);
    }

    /**
     * 隐藏
     */
    private void hideListBottom() {
        mListView.setVisibility(View.GONE);
        linear_bottom.setVisibility(View.GONE);
    }

    /**
     * 格式化时间
     */
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
    public void onCompletion(MediaPlayer mediaPlayer) {
        //下一集
        application.mpNext();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        seekbar.setMax(mediaPlayer.getDuration());
        Message msg = handler.obtainMessage();
        msg.what = UPDATE_PROGRESS;
        handler.sendMessage(msg);

        //更新Button
        updatePauseBtn();

        mediaPlayer.setLooping(islooping);
    }

    /**
     * UI更新操作
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_PROGRESS) {
                Message newMsg = new Message();
                newMsg.what = UPDATE_PROGRESS;
                seekbar.setProgress(application.getMpPlay().getCurrentPosition());
                handler.sendMessageDelayed(newMsg, 500);

                updateTextViewWithTimeFormat(time_current, application.getMpPlay().getCurrentPosition());
                updateTextViewWithTimeFormat(time_total, application.getMpPlay().getDuration());
                tv_audio.setText(application.getTitle());
            } else if (msg.what == UPDATE_BOTTOM) {
                //隐藏
                hideListBottom();
                handler.removeMessages(UPDATE_BOTTOM);
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            application.getMpPlay().seekTo(progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        application.closeMp3Play();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        showListBottom();
        return false;
    }
}
