package com.applidium.nickelodeon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.dialog.BindPhoneDialog;
import com.applidium.nickelodeon.dialog.ChangePasswordDialog;
import com.applidium.nickelodeon.dialog.ChangeUsernameDialog;
import com.applidium.nickelodeon.entity.CodeOpenVipRequest;
import com.applidium.nickelodeon.entity.CodeOpenVipResponse;
import com.applidium.nickelodeon.fragment.KidsInfoFragment;
import com.applidium.nickelodeon.switchbutton.SwitchButton;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.CustomDigitalClock;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.percent.PercentLinearLayout;
import com.applidium.nickelodeon.views.seekbar.ComboSeekBar;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Markry on 2016/1/7.
 */
public class ParentsCenterActivity extends Activity implements View.OnClickListener, ChangeUsernameDialog.OnChangeSuccessListener {
    public static final String SKIP_HEAD = "SKIP_HEAD";
    public static final String TIME_SET = "TIME_SET";
    public static final String TIME_POS = "TIME_POS";

    private Button btnback;
    private ImageView imagehead;
    private Button btnexit;
    private Button btnmodifyus;
    private Button btnbind;
    private Button btnmodifyps;
    private PercentLinearLayout linearinfo;
    private SwitchButton timeswitch;
    private TextView tvVipInfo;
    private ComboSeekBar mSeekbar;
    private static int mSettingCount = 0;
    private static final List<String> seekbarlist =
            Arrays.asList("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60");
    private Context mAct;
    private MNJApplication application;

    private TextView text_username;
    private TextView text_phone;
    private TextView text_psd;
    private TextView textVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_center_layout);

        initView();

        initData();
    }

    private void initView() {
        application = ((MNJApplication) getApplication());
        mAct = this;
        this.mSeekbar = (ComboSeekBar) findViewById(R.id.seekbar);
        this.tvVipInfo = (TextView) findViewById(R.id.tvVipInfo);
        this.timeswitch = (SwitchButton) findViewById(R.id.time_switch);
        this.linearinfo = (PercentLinearLayout) findViewById(R.id.linear_info);
        this.btnmodifyps = (Button) findViewById(R.id.btn_modify_ps);
        this.btnbind = (Button) findViewById(R.id.btn_bind);
        this.btnmodifyus = (Button) findViewById(R.id.btn_modify_us);
        this.btnexit = (Button) findViewById(R.id.btn_exit);
        this.imagehead = (ImageView) findViewById(R.id.image_head);
        this.btnback = (Button) findViewById(R.id.btn_back);
        this.text_username = (TextView) findViewById(R.id.text_username);
        this.text_phone = (TextView) findViewById(R.id.text_phone);
        this.text_psd = (TextView) findViewById(R.id.text_psd);
        this.textVersion = (TextView) findViewById(R.id.textVersion);

        this.btnback.setOnClickListener(this);
        this.btnmodifyus.setOnClickListener(this);
        this.btnbind.setOnClickListener(this);
        this.btnmodifyps.setOnClickListener(this);
        this.btnexit.setOnClickListener(this);


        mSeekbar.setAdapter(seekbarlist);
        mSeekbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mSettingCount++;
                AppUtils.setIntSharedPreferences(mAct, TIME_POS, position);
            }
        });

        timeswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setTimeSet(mAct, true);
                    mSeekbar.setVisibility(View.VISIBLE);
                    int position = AppUtils.getIntSharedPreferences(mAct, TIME_POS, 6);
                    mSeekbar.setSelection(position);
                } else {
                    setTimeSet(mAct, false);
                    mSeekbar.setVisibility(View.GONE);
                    mSeekbar.setSelection(0);
                }

            }
        });

        if (isTimeSet(mAct)) {
            timeswitch.setChecked(true);
        } else {
            timeswitch.setChecked(false);
        }
    }

    private void initData() {
        textVersion.setText(getResources().getString(R.string.version_number)
                + AppUtils.getVersion(this));

        //如果用户名已经修改过了，则不能修改
        if (!StringUtils.isEmpty(application.getFirstModifiedTime())) {
            btnmodifyus.setVisibility(View.GONE);
        }
        if (application.getUserName().length() > 0) {
            text_username.setText(application.getUserName());
        }

        //如果有手机号则代表已经绑定了，隐藏绑定按钮
        if (application.getMoblieNum().length() == 11) {
            btnbind.setVisibility(View.GONE);
            text_phone.setText(application.getMoblieNum());
        } else {
            text_phone.setText("未绑定");
        }
//        //无密码则未默认初始密码
//        if (application.getPassWord().length() == 0) {
//            application.setPassWord("000000");
//        }
        //如果密码为000000 的初始默认密码，则显示提示修改密码
        if (application.getPassWord().equals("000000")) {
            text_psd.setText("000000");
            findViewById(R.id.text_prompt).setVisibility(View.VISIBLE);
        } else {
            text_psd.setText("******");
            findViewById(R.id.text_prompt).setVisibility(View.GONE);
        }
        //判断用户性别图像
        if (application.getProfile().getGender() == KidsInfoFragment.Gender.M) {
            imagehead.setBackgroundResource(R.drawable.head_boy);
        } else if (application.getProfile().getGender() == KidsInfoFragment.Gender.F) {
            imagehead.setBackgroundResource(R.drawable.head_girl);
        } else {
            imagehead.setBackgroundResource(R.drawable.head_normal);
        }
    }


    /**
     * 获取控制的播放时长
     *
     * @param context
     * @return
     */
    public static long getLimitTime(Context context) {
        long res = 0;
        try {
            int position = AppUtils.getIntSharedPreferences(context, TIME_POS, 6);
            String selectValue = seekbarlist.get(position);
            int time = Integer.parseInt(selectValue);
            if (time > 0) {
                res = time * 60 * 1000;
            }
        } catch (Exception e) {
            res = 0;
        }
        return res;
    }

    /**
     * 登出
     */
    private void logOut() {
        String url = AppConfig.LOGIN_OUT;
        CodeOpenVipRequest request = new CodeOpenVipRequest();
        request.setToken(application.getToken());

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                CodeOpenVipResponse resp = GsonUtil.parse(response,
                        CodeOpenVipResponse.class);

                if (resp.isSucess()) {
                    application.setVipExpiresTime(resp.getData().getVipExpiryTime());
                    application.reqUserInfo();
                }
            }

        });
    }

    /**
     * 是否控制播放时长
     *
     * @param context
     * @return true 表示控制播放时长
     */
    private boolean isTimeSet(Context context) {
        return CustomDigitalClock.isTimeSet(context);
    }

    /**
     * 设置是否控制播放时长
     *
     * @param context
     * @param b       true 表示控制播放时长
     */
    public static void setTimeSet(Context context, boolean b) {
        CustomDigitalClock.setTimeSet(context, b);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_exit:
                Dialog alertDialog = new AlertDialog.Builder(this).
                        setTitle("退出").
                        setMessage("您确定退出登录吗？").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                application.clearToken();
                                logOut();
                                setResult(NickelFragmentActivity.RESULT_VIP_BACK);
                                finish();
                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        create();
                alertDialog.show();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_modify_us:
                ChangeUsernameDialog dialogUn = new ChangeUsernameDialog(mAct);
                dialogUn.setChangeSuccessListener(this);
                dialogUn.show();
                break;
            case R.id.btn_bind:
                BindPhoneDialog dilogBind = new BindPhoneDialog(this);
                dilogBind.setChangeSuccessListener(this);
                dilogBind.show();
                break;
            case R.id.btn_modify_ps:
                if (text_phone.getText().toString().length() == 11) {
                    ChangePasswordDialog dilogPw = new ChangePasswordDialog(mAct);
                    dilogPw.setChangeSuccessListener(this);
                    dilogPw.show();
                } else {
                    Toast.makeText(this, "请先绑定手机号", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onSuccess() {
        initData();
    }
}
