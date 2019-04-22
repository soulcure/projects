package com.ivmall.android.app.parent;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.smit.android.ivmall.stb.R;
import com.ivmall.android.app.adapter.RecordPlayAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.entity.RecordItem;
import com.ivmall.android.app.entity.RecordRequest;
import com.ivmall.android.app.entity.RecordResponse;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

public class RecordPlayFragment extends Fragment {

    public static final String TAG = RecordPlayFragment.class.getSimpleName();

    private Activity mAct;

    private RecordPlayAdapter mAdapter;

    private RecyclerView mListView;


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

        return inflater.inflate(R.layout.record_play_fragment, container,
                false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mListView = (RecyclerView) view.findViewById(R.id.history_list);

        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(mAct);
        mListView.setLayoutManager(layoutManager);

        int offset = 1000;
        reqPlayRecord(0, offset); // 获取播放记录

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
        BaiduUtils.onPause(mAct);
    }


    /**
     * 1.22 获取播放记录
     */
    private void reqPlayRecord(final int start, final int offset) {
        String url = AppConfig.WATCH_HISTORY;
        RecordRequest request = new RecordRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token); // 参数1

        int profileId = ((KidsMindApplication) mAct.getApplication())
                .getProfileId();
        request.setProfileId(profileId); // 参数2

        request.setStartIndex(start); // 参数3
        request.setOffset(offset);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                RecordResponse resp = GsonUtil.parse(response,
                        RecordResponse.class);

                if (resp != null && resp.isSucess() && isAdded()) {
                    List<RecordItem> list = resp.getData().getList();
                    if (list == null || list.size() == 0) {
                        Toast.makeText(getActivity(), "暂时没有播放记录", Toast.LENGTH_SHORT).show();
                    } else {
                        mAdapter = new RecordPlayAdapter(mAct, list);
                        mListView.setAdapter(mAdapter);
                    }
                }
            }
        });
    }


}
