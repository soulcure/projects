package com.ivmall.android.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.UserUpdateRequest;
import com.ivmall.android.app.entity.UserUpdateResponse;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

/**
 * Created by smit on 2016/4/27.
 */
public class UserInfoFragment extends Fragment{

    private Context mContext;
    private EditText infoNum;
    private EditText infoName;
    private Button infoSave;
    BaseActivity act;
    KidsMindApplication application;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        act = (BaseActivity)activity;
        application = (KidsMindApplication) mContext.getApplicationContext();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_info_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        infoNum = (EditText) view.findViewById(R.id.info_phone_num);
        infoName = (EditText) view.findViewById(R.id.info_name);
        infoNum.setText(application.getPhoneNum());
        infoName.setText(application.getUserName());
        infoSave = (Button) view.findViewById(R.id.info_save);
        infoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInputMethod();
                updateUserInfo();
            }
        });

    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(infoName.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    // 修改用户信息
    private void updateUserInfo() {
        act.startProgress();
        infoSave.setEnabled(false);
        String name = infoName.getText().toString();
        if (name.length() != 0) {
            String url = AppConfig.UPDATE_USER_INFO;
            UserUpdateRequest request = new UserUpdateRequest();
            String token = ((KidsMindApplication) getActivity().getApplication()).getToken();

            request.setToken(token);
            request.setName(name);

            String json = request.toJsonString();
            HttpConnector.httpPost(url, json, new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    UserUpdateResponse res = GsonUtil.parse(response, UserUpdateResponse.class);
                    if (res.isSuccess()) {
                        ((KidsMindApplication) mContext.getApplicationContext()).reqUserInfo();//刷新用户VIP信息
                        act.stopProgress();
                        act.showTips(R.string.save_success);
                    }
                    infoSave.setEnabled(true);
                }
            });

        }
    }
}
