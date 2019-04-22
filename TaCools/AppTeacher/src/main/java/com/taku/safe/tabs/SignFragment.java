package com.taku.safe.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.activity.MapActivity;

import java.lang.ref.WeakReference;


public class SignFragment extends BasePermissionFragment implements View.OnClickListener {
    public final static String TAG = SignFragment.class.getSimpleName();

    private UIHandler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button btn_sign = (Button) view.findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MapActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<SignFragment> mTarget;

        UIHandler(SignFragment target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

}
