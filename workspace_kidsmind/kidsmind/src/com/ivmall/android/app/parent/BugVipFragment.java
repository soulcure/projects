package com.ivmall.android.app.parent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ivmall.android.app.BaseActivity;
import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.R;
import com.ivmall.android.app.adapter.BuyVipAdapter;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.PaymentDialog;
import com.ivmall.android.app.entity.VipListItem;
import com.ivmall.android.app.entity.VipListRequest;
import com.ivmall.android.app.entity.VipListResponse;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;

import java.util.List;

public class BugVipFragment extends Fragment {

    public static final String TAG = BugVipFragment.class.getSimpleName();

    public static final String VIP_REFRESH = "VIP_REFRESH";
    private Activity mAct;

    private List<VipListItem> mList;

    private TextView tvVipTime;
    private RefreshReciver refreshReciver;

    private boolean isPhone = true;

    private RecyclerView vipRecylerView;

    private BuyVipAdapter vipAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
        isPhone = ((BaseActivity)mAct).isPhone;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.buy_vip_fragment, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        vipRecylerView = (RecyclerView) view.findViewById(R.id.vip_list);
        if (!isPhone) {
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.vip_item_space);
            vipRecylerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        }
        tvVipTime = (TextView) view.findViewById(R.id.tvVipTime);

        refreshReciver = new RefreshReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(VIP_REFRESH);
        mAct.registerReceiver(refreshReciver, filter);
    }


    @Override
    public void onResume() {
        super.onResume();
        reqVipList(); // 获取VIP列表
        ((KidsMindApplication) mAct.getApplication()).setRefresh(tvVipTime);
        ((KidsMindApplication) mAct.getApplication()).reqUserInfo();//刷新用户VIP信息

        /**
         * Fragment页面起始 (注意： 如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onResume(mAct);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((KidsMindApplication) mAct.getApplication()).setRefresh(null);
        /**
         *Fragment 页面结束（注意：如果有继承的父Fragment中已经添加了该调用，那么子Fragment中务必不能添加）
         */
        BaiduUtils.onPause(mAct);
    }

    /**
     * 1.30 获取VIP列表
     */
    private void reqVipList() {
        String url = AppConfig.VIP_LIST;
        VipListRequest request = new VipListRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                VipListResponse resp = GsonUtil.parse(response,
                        VipListResponse.class);
                if (resp != null && resp.isSucess()) {
                    mList = resp.getData().getList();

                    if (mList == null || mList.size() == 0) {
                        Toast.makeText(getActivity(), "暂时没有VIP列表信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (isAdded()) {  // for bug not attached to Activity
                            setVipListInfo(mList, isPhone);
                        }
                    }
                }
            }
        });
    }


    private void setVipListInfo(List<VipListItem> list, boolean isPhone) {
        final List<VipListItem> ls = list;
        LinearLayoutManager layoutManager = new LinearLayoutManager(mAct);
        if (isPhone) {
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        } else {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        }

        vipRecylerView.setLayoutManager(layoutManager);
        vipAdapter = new BuyVipAdapter(mAct, isPhone, list);
        vipRecylerView.setAdapter(vipAdapter);
        vipAdapter.setOnItemClickLitener(new BuyVipAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                double price = (Double) view.getTag();
                if (!judgeLogin()) {
                    login();
                    return;
                }

                if (price > 0) {
                    PaymentDialog dialog = new PaymentDialog(mAct, price, ls.get(position).getVipGuid(), ls.get(position).getVipDesc());
                    dialog.setPartnerProductId(ls.get(position).getPartnerProductId());
                    dialog.show();
                }
            }
        });

    }


    /**
     * 判断用户是否登录
     * 如果没有登录则提示登录
     *
     * @return
     */
    private boolean judgeLogin() {
        boolean isLogin = ((KidsMindApplication) getActivity().getApplication()).isLogin();
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
     * 刷新
     */
    private class RefreshReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            reqVipList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mAct.unregisterReceiver(refreshReciver);
        } catch (IllegalArgumentException e) {

        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

                outRect.right = space;
        }
    }
}
