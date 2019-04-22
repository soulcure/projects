package com.taku.safe.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taku.safe.AccountInfoActivity;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;

/**
 * Created by colin on 2017/6/8.
 */

public class AppealFragment extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = AppealFragment.class.getSimpleName();

    private String phoneNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appeal, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);
    }


    private void initTitle(View view) {
        view.findViewById(R.id.content_title)
                .setBackgroundColor(getResources().getColor(R.color.color_white));
        TextView tv_back = (TextView) view.findViewById(R.id.tv_back);
        tv_back.setVisibility(View.GONE);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setTextColor(getResources().getColor(R.color.color_title));
        tv_title.setText(R.string.login);

        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        tv_confirm.setBackgroundResource(R.drawable.bg_white_selector);
        Drawable drawableCorrect = getResources().getDrawable(R.mipmap.ic_close_black);
        tv_confirm.setCompoundDrawablesWithIntrinsicBounds(drawableCorrect, null, null, null);
        tv_confirm.setOnClickListener(this);

    }

    private void initView(View view) {
        phoneNum = getString(R.string.service_phone_number);
        String format = getString(R.string.appeal_info);


        TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
        tv_info.setText(String.format(format, phoneNum));

        view.findViewById(R.id.btn_call).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_confirm:
                ((AccountInfoActivity) getActivity()).skipToFragment(ForgetFragment.TAG, null);
                break;
            case R.id.btn_call:
                callDialog();
                break;
        }
    }


    private void call() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
        startActivity(intent);
    }


    private void callDialog() {
        String format = getString(R.string.call_dialog_title);

        new AlertDialog.Builder(getContext())
                .setMessage(String.format(format, phoneNum))
                .setPositiveButton(R.string.btn_confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                call();
                            }
                        })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
