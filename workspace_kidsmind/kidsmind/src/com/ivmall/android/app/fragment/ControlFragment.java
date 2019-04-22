package com.ivmall.android.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.PlaySkipRequest;
import com.ivmall.android.app.entity.PlaySkipResponse;
import com.ivmall.android.app.entity.PlayTimeResponse;
import com.ivmall.android.app.parent.PlaySettingFragment;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.Log;
import com.ivmall.android.app.views.SettingItemView;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


/**
 * Created by koen on 2016/3/16.
 */
public class ControlFragment extends Fragment implements SettingItemView.onSettingItemClickListener {


    //private static final int CLOSE_RESULT = 1;
    //private ControlHandler mHandler;

    private static final String ACTION_CONFIRM = "com.ivmall.android.app.action.Confirm";

    private SettingItemView setPlaytime;

    private SettingItemView setTvRoport;
    private SettingItemView setTvClose;

    private SettingItemView setSkiphead;
    private SettingItemView setProgramList;

    // -------------------------用于家长控制时间---------------------
    private int DEFAULT_PLAY_TIME;
    //private int mQueryCount = 9;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DEFAULT_PLAY_TIME = PlaySettingFragment.getPlayTime(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mHandler = new ControlHandler(this);
        getActivity().registerReceiver(mConnectionChangeReceiver,
                new IntentFilter(ACTION_CONFIRM));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.control_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            boolean isPhone = bundle.getBoolean("isPhone");
            if (isPhone) {
                TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);


                String content1 = getString(R.string.control_info_1);
                String content2 = getString(R.string.control_info_2);
                String content3 = getString(R.string.control_info_3);

                String content = content1 + content2 + content3;
                int color = getResources().getColor(R.color.yellow);
                int start = content1.length();
                int end = start + content2.length();
                CharSequence cs = AppUtils.setHighLightText(content, color, start, end);
                tvInfo.setText(cs);
            }

        }
        setTvRoport = (SettingItemView) view.findViewById(R.id.setting_tv_roport);
        setTvRoport.setOnSettingItemClickListener(this);

        setTvClose = (SettingItemView) view.findViewById(R.id.setting_tv_close);
        setTvClose.setOnSettingItemClickListener(this);
        setTvClose.setOnSettingItemButtonClickListener(new SettingItemView.onSettingItemButtonClickListener() {
            @Override
            public void onButtonClick(View buttonView) {
                if (judgeLogin()) {
                    showCloseDialog(getActivity());
                }
            }
        });


        setSkiphead = (SettingItemView) view.findViewById(R.id.setting_skiphead);
        setSkiphead.setChecked(PlaySettingFragment.isSkipHead(getActivity()));
        setSkiphead.setOnSettingItemCheckChangeListener(new SettingItemView.onSettingItemCheckChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                playSkip(isChecked);
            }
        });

        setProgramList = (SettingItemView) view.findViewById(R.id.setting_program_list);
        setProgramList.setOnSettingItemClickListener(this);


        setPlaytime = (SettingItemView) view.findViewById(R.id.setting_playtime);
        setPlaytime.setChecked(PlaySettingFragment.isTimeSet(getActivity()));
        setPlaytime.setOnSettingItemClickListener(this);
        setPlaytime.setOnSettingItemCheckChangeListener(new SettingItemView.onSettingItemCheckChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                playDuration(isChecked, DEFAULT_PLAY_TIME);
                if (isChecked) {
                    showSetPlayTimeDialog();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mConnectionChangeReceiver);
    }

    /**
     * 显示设置播放时长对话框
     */
    private void showSetPlayTimeDialog() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(new myTimeSetListener(),
                1, DEFAULT_PLAY_TIME, false);
        tpd.setAccentColor("#fabf01");
        tpd.vibrate(false);
        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.setting_tv_roport: {
                if (judgeLogin()) {
                    Intent intent = new Intent(getActivity(), BaseActivity.class);
                    intent.putExtra(BaseActivity.NAME, BaseActivity.TV_REPORT);
                    startActivity(intent);
                }
                break;
            }
            case R.id.setting_tv_close: {
                if (judgeLogin()) {
                    showCloseDialog(getActivity());
                }
                break;
            }
            case R.id.setting_program_list: {
                if (judgeLogin()) {
                    Intent intent = new Intent(getActivity(), BaseActivity.class);
                    intent.putExtra(BaseActivity.NAME, BaseActivity.PLAY_LIST);
                    startActivity(intent);
                }
                break;
            }
            case R.id.setting_playtime: {
                showSetPlayTimeDialog();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 判断用户是否登录
     * 如果没有登录则提示登录
     *
     * @return
     */
    private boolean judgeLogin() {
        boolean isLogin = ((KidsMindApplication) getActivity().getApplication()).isLogin();
        if (!isLogin) {
            showLoginDialog(getActivity());
        }
        return isLogin;
    }


    /**
     * 进入登录界面
     */
    private void login() {
        Intent intent = new Intent(getActivity(), BaseActivity.class);
        intent.putExtra(BaseActivity.NAME, BaseActivity.LOGIN);
        startActivity(intent);
    }

    /**
     * 显示登录提示框
     *
     * @param context
     */
    private void showLoginDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.login_title))
                .setMessage(context.getString(R.string.login_content));

        builder.setPositiveButton(context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        login();
                        arg0.dismiss();

                    }
                });

        builder.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }

    /**
     * 显示关闭TV框
     *
     * @param context
     */
    private void showCloseDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.close_title))
                .setMessage(context.getString(R.string.close_content));

        builder.setPositiveButton(context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        tvIslogin();
                        arg0.dismiss();

                    }
                });

        builder.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }


    /**
     * 1.81 查询TV端是否登录接口
     */
    private void tvIslogin() {
        String url = AppConfig.TV_ISLOGIN;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();

        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                PlaySkipResponse res = GsonUtil.parse(response, PlaySkipResponse.class);
                if (res != null && res.isSuccess()) {
                    tvClose(true);
                } else {
                    showMsgDialog(getActivity(), getString(R.string.tv_no_login));
                }

            }
        });
    }


    /**
     * 1.82 关闭远程应用接口
     *
     * @param close
     */
    private void tvClose(boolean close) {
        String url = AppConfig.TV_CLOSE;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();

        request.setToken(token);
        request.setClose(close);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                Log.v(TAG, response);
                /*PlaySkipResponse res = GsonUtil.parse(response, PlaySkipResponse.class);
                if (res != null && res.isSuccess()) {

                }*/
            }
        });
    }


    /**
     * 1.83 远程应用关闭确认接口
     */
    /*private void tvCloseConfirm() {
        String url = AppConfig.TV_CLOSE_CONFIRM;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();

        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                PlaySkipResponse res = GsonUtil.parse(response, PlaySkipResponse.class);
                if (res != null && res.isSuccess()) {
                    showMsgDialog(getActivity(), getResources().getString(R.string.close_tv_sucess));
                } else {
                    if (mQueryCount > 0) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = CLOSE_RESULT;
                        mHandler.sendMessageDelayed(msg, 1000);
                        mQueryCount--;
                    }
                }
            }
        });
    }*/


    /**
     * 1.84 设置跳过片头接口
     *
     * @param isSkip
     */
    private void playSkip(final boolean isSkip) {
        String url = AppConfig.PLAY_SKIP;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();

        request.setToken(token);
        request.setSkip(isSkip);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                PlaySkipResponse res = GsonUtil.parse(response, PlaySkipResponse.class);
                if (res.isSuccess()) {
                    PlaySettingFragment.setSkipHead(getActivity(), isSkip);
                }
            }
        });
    }


    /**
     * 1.85 设置播放时长接口
     *
     * @param effective
     * @param playDuration
     */
    private void playDuration(final boolean effective, final int playDuration) {
        String url = AppConfig.PLAY_DURATION;
        PlaySkipRequest request = new PlaySkipRequest();
        String token = ((KidsMindApplication) getActivity().getApplication()).getToken();

        request.setToken(token);
        request.setEffective(effective);
        request.setPlayDuration(playDuration);
        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                PlaySkipResponse res = GsonUtil.parse(response, PlaySkipResponse.class);
                if (res.isSuccess()) {
                    DEFAULT_PLAY_TIME = playDuration;
                    PlaySettingFragment.setTimeSet(getActivity(), effective);
                    PlaySettingFragment.setPlayTime(getActivity(), playDuration);
                }
            }
        });
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
                    PlaySettingFragment.setSkipHead(context, isSkip);

                    boolean isEff = res.getData().isEffective();
                    PlaySettingFragment.setTimeSet(context, isEff);

                    int playDur = res.getData().getPlayDuration();
                    PlaySettingFragment.setPlayTime(context, playDur);
                }
            }
        });
    }

    /**
     * 显示关闭TV框
     *
     * @param context
     */
    private void showMsgDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.prompt))
                .setMessage(msg);

        builder.setPositiveButton(context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        arg0.dismiss();

                    }
                });

        builder.show();
    }


    private class myTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int var1, int var2, int var3) {
            playDuration(true, var2);
        }
    }

    /*private static class ControlHandler extends Handler {
        private final WeakReference<ControlFragment> mTarget;

        ControlHandler(ControlFragment target) {
            mTarget = new WeakReference<ControlFragment>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            ControlFragment fragment = mTarget.get();
            switch (msg.what) {
                case CLOSE_RESULT:
                    // 查询状态
                    fragment.tvCloseConfirm();
                    break;
                default:
                    break;
            }
        }

    }*/


    /**
     * 网络状态监听器
     */
    private BroadcastReceiver mConnectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CONFIRM) && isAdded()) {
                showMsgDialog(getActivity(), getResources().getString(R.string.close_tv_sucess));
            }
        }
    };

}
