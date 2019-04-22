package com.applidium.nickelodeon.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.entity.LoginIvmallRequest;
import com.applidium.nickelodeon.entity.LoginResponse;
import com.applidium.nickelodeon.impl.OnArticleSelectedListener;
import com.applidium.nickelodeon.impl.OnSucessListener;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.ApplidiumTextView;


public class HasRegisterFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = HasRegisterFragment.class.getSimpleName();

    public static final String MOBILE_NUM = "mobile_num";
    public static final String USER_NAME = "user_name";


    public OnArticleSelectedListener mOnArticleKidsMind;
    private Activity mAct;

    public ApplidiumTextView tv_mobile;
    public ApplidiumTextView tv_username;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
        try {
            mOnArticleKidsMind = (OnArticleSelectedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.has_register_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initViews(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            initData(bundle);
        }
    }


    private void initViews(View v) {

        tv_mobile = (ApplidiumTextView) v.findViewById(R.id.tv_mobile);
        tv_username = (ApplidiumTextView) v.findViewById(R.id.tv_username);

        v.findViewById(R.id.btn_back).setOnClickListener(this);
        v.findViewById(R.id.btn_back).requestFocus();
    }


    private void initData(Bundle bundle) {
        String mobileNum = bundle.getString(MOBILE_NUM);
        String useName = bundle.getString(USER_NAME);

        tv_mobile.setText(mobileNum);

        tv_username.setText(useName);
    }


    @Override
    public void onClick(View v) {

        MediaPlayerService.playSound(mAct, MediaPlayerService.ONCLICK);
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                Bundle bundle = getArguments();
                mOnArticleKidsMind.skipToFragment(LoginIvmallFragment.TAG, bundle);
                break;
            default:
                break;
        }


    }


}