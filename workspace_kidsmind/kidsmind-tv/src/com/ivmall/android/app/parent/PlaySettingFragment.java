package com.ivmall.android.app.parent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.PlaySkipRequest;
import com.ivmall.android.app.entity.PlayTimeResponse;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.views.seekbar.ComboSeekBar;
import com.ivmall.android.app.views.switchbutton.SwitchButton;


import java.util.Arrays;
import java.util.List;

public class PlaySettingFragment extends Fragment {

    public static final String TAG = PlaySettingFragment.class.getSimpleName();

    public static final String SKIP_HEAD = "SKIP_HEAD";
    public static final String TIME_SET = "TIME_SET";
    public static final String TIME_POS = "TIME_POS";

    private static final List<String> seekbarlist = Arrays.asList("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60");

    private static final int CURSOR = 5;

    private static int mSettingCount = 0;

    private SwitchButton headSwitch;
    private SwitchButton timeSwitch;
    private ComboSeekBar mSeekbar;

    private Activity mAct;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.play_setting_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSeekbar = (ComboSeekBar) view.findViewById(R.id.seekbar);

        mSeekbar.setAdapter(seekbarlist);
        mSeekbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mSettingCount++;
                int time = position * CURSOR;
                setPlayTime(mAct, time);
            }
        });

        headSwitch = (SwitchButton) view.findViewById(R.id.head_switch);
        headSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSkipHead(mAct, true);
                } else {
                    setSkipHead(mAct, false);
                }

            }
        });
        if (isSkipHead(mAct)) {
            headSwitch.setChecked(true);
        } else {
            headSwitch.setChecked(false);
        }

        timeSwitch = (SwitchButton) view.findViewById(R.id.time_switch);
        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setTimeSet(mAct, true);
                    int position = getSeekBarPosition(mAct);
                    mSeekbar.setSelection(position);
                } else {
                    setTimeSet(mAct, false);
                    mSeekbar.setSelection(0);
                }

            }
        });
        if (isTimeSet(mAct)) {
            timeSwitch.setChecked(true);
        } else {
            timeSwitch.setChecked(false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    /**
     * 判断是否用户有设置
     *
     * @return
     */
    public static boolean isParentsSettingTime() {
        boolean res = false;
        if (mSettingCount > 0) {
            res = true;
        }
        return res;
    }


    /**
     * 是否跳过片头
     *
     * @param context
     * @return true表示跳过
     */
    public static boolean isSkipHead(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, PlaySettingFragment.SKIP_HEAD, false);
    }


    /**
     * 设置是否跳过片头
     *
     * @param context
     * @param b       true表示跳过
     */
    public static void setSkipHead(Context context, boolean b) {
        AppUtils.setBooleanSharedPreferences(context, PlaySettingFragment.SKIP_HEAD, b);
    }


    /**
     * 是否控制播放时长
     *
     * @param context
     * @return true 表示控制播放时长
     */
    public static boolean isTimeSet(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, PlaySettingFragment.TIME_SET, false);
    }


    /**
     * 设置是否控制播放时长
     *
     * @param context
     * @param b       true 表示控制播放时长
     */
    public static void setTimeSet(Context context, boolean b) {
        AppUtils.setBooleanSharedPreferences(context, PlaySettingFragment.TIME_SET, b);
    }


    /**
     * 获取设置的播放时长
     *
     * @param context
     * @return
     */
    public static int getPlayTime(Context context) {
        return AppUtils.getIntSharedPreferences(context, TIME_POS, 30);
    }


    /**
     * 保存设置的播放时长
     *
     * @param context
     * @param time
     */
    public static void setPlayTime(Context context, int time) {
        AppUtils.setIntSharedPreferences(context, TIME_POS, time);
    }


    public static long getLimitTime(Context context) {
        return getPlayTime(context) * 60 * 1000;
    }


    private int getSeekBarPosition(Context context) {
        return getPlayTime(context) / CURSOR;
    }


    @Override
    public void onPause() {
        super.onPause();
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }


    /**
     * 1.88 获取播放时长是否生效和是否跳过片头接口
     */
    public static void playSetting(final Context context) {
        String url = AppConfig.GET_SETTING;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) context.getApplicationContext()).getToken();

        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                PlayTimeResponse res = GsonUtil.parse(response, PlayTimeResponse.class);
                if (res != null && res.getData() != null && res.isSuccess()) {
                    boolean isSkip = res.getData().isSkip();
                    setSkipHead(context, isSkip);

                    boolean isEff = res.getData().isEffective();
                    setTimeSet(context, isEff);

                    int playDur = res.getData().getPlayDuration();
                    setPlayTime(context, playDur);
                }
            }
        });
    }

}
