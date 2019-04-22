package com.ivmall.android.app.parent;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.adapter.ResponseAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.FeedBackRequest;
import com.ivmall.android.app.entity.FeedBackResponse;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.uitls.StringUtils;

public class ResponseFragment extends Fragment {

    public static final String TAG = ResponseFragment.class.getSimpleName();

    private Activity mAct;

    private EditText etContext;
    private Button btnCommit;

    private PopupWindow popupWindow;

    public enum what {
        createProfile, login, feedback
    }

    private boolean flag = false;

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

        return inflater.inflate(R.layout.response_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        etContext = (EditText) view.findViewById(R.id.et_context);
        etContext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    BaiduUtils.onEvent(mAct, OnEventId.USER_RESPONSE_INPUT, getString(R.string.user_response_input));
            }
        });


        btnCommit = (Button) view.findViewById(R.id.btn_commit);
        btnCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = etContext.getText().toString();
                if (!StringUtils.isEmpty(content)) {
                    reqFeedBack(content);
                    BaiduUtils.onEvent(mAct, OnEventId.USER_RESPONSE_COMMIT, getString(R.string.user_response_commit));
                } else {
                    Toast.makeText(mAct, "请输入您要反馈的信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        View view = mAct.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = ((InputMethodManager) mAct.getSystemService(Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        BaiduUtils.onPause(mAct);
    }


    private void showWindow(View position) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new PaintDrawable(android.R.color.transparent));
            popupWindow.setFocusable(true);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }


        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {

            View view = LayoutInflater.from(mAct).inflate(R.layout.pop_dropdown, null);
            ListView listView = (ListView) view.findViewById(R.id.listView);
            String[] items = getResources().getStringArray(R.array.what_context);
            List<String> list = new ArrayList<String>();
            for (String item : items) {
                list.add(item);
            }
            ResponseAdapter adapter = new ResponseAdapter(mAct, list);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String context = (String) parent.getAdapter().getItem(position);
                    String text = etContext.getText().toString();
                    if (!StringUtils.isEmpty(text)) {
                        if (!text.contains(context)) {
                            text = text + "\n" + context;
                            BaiduUtils.onEvent(mAct, OnEventId.USER_RESPONSE_CHIOCE, getString(R.string.user_response_chioce) + context);
                        }
                    } else {
                        text = context;
                    }
                    etContext.setText(text);
                    popupWindow.dismiss();

                }
            });

            popupWindow.setContentView(view);
            int offset = (int) ScreenUtils.dpToPx(mAct, 225);
            popupWindow.showAsDropDown(position, -offset, 0);

        }


    }

    /**
     * 1.29 用户反馈
     */
    private void reqFeedBack(String content) {
        String url = AppConfig.USER_FEEDBACK;
        FeedBackRequest request = new FeedBackRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token); // 参数1

        request.setContent(content); // 参数2

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                FeedBackResponse resp = GsonUtil.parse(response,
                        FeedBackResponse.class);

                if (resp.isSucess()) {
                    Toast.makeText(getActivity(), "感谢您的意见，您的反馈是我们前进的动力", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_SHORT)
                            .show();
                }

            }

        });
    }


}
