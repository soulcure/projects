package com.applidium.nickelodeon.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.nickelodeon.ActionActivity;
import com.applidium.nickelodeon.BabyInfoActivity;
import com.applidium.nickelodeon.MNJApplication;
import com.applidium.nickelodeon.NickelFragmentActivity;
import com.applidium.nickelodeon.ParentsCenterActivity;
import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.VipInfoActivity;
import com.applidium.nickelodeon.adapter.HomeRecyclerAdapter;
import com.applidium.nickelodeon.config.AppConfig;
import com.applidium.nickelodeon.debug.CrashHandler;
import com.applidium.nickelodeon.dialog.GenCodeDialog;
import com.applidium.nickelodeon.entity.PrizesInfoRequest;
import com.applidium.nickelodeon.entity.PrizesInfoResponse;
import com.applidium.nickelodeon.entity.ProfileItem;
import com.applidium.nickelodeon.entity.SerieItemInfo;
import com.applidium.nickelodeon.entity.SeriesListRequest;
import com.applidium.nickelodeon.entity.SeriesListResponse;
import com.applidium.nickelodeon.service.MediaPlayerService;
import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.GsonUtil;
import com.applidium.nickelodeon.uitls.HttpConnector;
import com.applidium.nickelodeon.uitls.IPostListener;
import com.applidium.nickelodeon.uitls.ScreenUtils;
import com.applidium.nickelodeon.uitls.StringUtils;
import com.applidium.nickelodeon.views.FoucsLinearLayout;
import com.applidium.nickelodeon.views.MetroLayout;
import com.applidium.nickelodeon.views.RecommendCardView;
import com.jauker.widget.BadgeView;

import java.util.List;


public class MetroFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = MetroFragment.class.getSimpleName();


    private MetroLayout metroLayout;
    private Activity mAct;
    private Button btnAction;
    private Button btnVip;
    private Button btnParent;
    private ImageView imgHead;

    private FoucsLinearLayout babyInfo;
    private TextView tvName;
    private TextView tvAge;


    private HomeRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean isPhone = false;  // 记录是移动端还是TV端，用于载入不同的页面。
    private List<SerieItemInfo> mList;

    private static String tipsKey = "oneTips";
    private static String GEN_CODE = "promo_code";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashHandler.getInstance().sendCrashLogToServer(mAct); //汇报崩溃日志汇报
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (ScreenUtils.isTv(mAct)) {
            isPhone = false;
            return inflater.inflate(R.layout.metro_fragment_tv, container, false);

        } else {
            isPhone = true;
            return inflater.inflate(R.layout.metro_fragment_phone, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        babyInfo = (FoucsLinearLayout) view.findViewById(R.id.baby_info_layout);
        babyInfo.requestFocus();
        babyInfo.setOnClickListener(this);

        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvAge = (TextView) view.findViewById(R.id.tv_age);

        imgHead = (ImageView) view.findViewById(R.id.img_baby);

        btnAction = (Button) view.findViewById(R.id.btn_action);
        btnAction.setOnClickListener(this);
        setTips();

        btnVip = (Button) view.findViewById(R.id.btn_vip);
        btnVip.setOnClickListener(this);

        btnParent = (Button) view.findViewById(R.id.btn_parent);
        btnParent.setOnClickListener(this);

        if (isPhone) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerview);

            int count = 4;  //列数目

            /**动态设置 RecyclerView 宽度*/
            int item_w = getResources().getDimensionPixelSize(R.dimen.ITEM_NORMAL_SIZE_WIDTH); //列宽度
            int spacing_w = getResources().getDimensionPixelSize(R.dimen.ITEM_DIVIDE_SIZE);  //列间距
            int width = item_w * count + spacing_w * (count - 1);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
            params.width = width;
            mRecyclerView.setLayoutParams(params);


            mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacing_w)); //设置间距

            GridLayoutManager layoutManager = new GridLayoutManager(mAct, count);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    if (i == 0) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            });
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            metroLayout = (MetroLayout) view.findViewById(R.id.metrolayout);
            RecommendCardView smartPlay = new RecommendCardView(mAct, MetroLayout.Horizontal, 0, RecommendCardView.SMART_PLAY);
            metroLayout.addItemView(smartPlay);
        }
        reqMainList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String code = bundle.getString(GEN_CODE);
            if (!StringUtils.isEmpty(code)) {
                reqGenCode(code);//do something
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        ProfileItem profile = ((MNJApplication) mAct.getApplication()).getProfile();
        if (profile != null) {
            tvName.setText(profile.getNickname());
            tvAge.setText(profile.getAge(mAct));
            if (profile.getGender() == KidsInfoFragment.Gender.N) {
                imgHead.setImageResource(R.drawable.head_normal);
            } else if (profile.getGender() == KidsInfoFragment.Gender.F) {
                imgHead.setImageResource(R.drawable.head_girl);
            } else {
                imgHead.setImageResource(R.drawable.head_boy);
            }

        }


    }

    @Override
    public void onClick(View v) {

        MediaPlayerService.playSound(mAct, MediaPlayerService.ONCLICK);
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.btn_action:
                AppUtils.setBooleanSharedPreferences(mAct, tipsKey, true);
                intent.setClass(getActivity(), ActionActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.btn_vip:
                intent.setClass(getActivity(), VipInfoActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.btn_parent:
                intent.setClass(getActivity(), ParentsCenterActivity.class);
                getActivity().startActivityForResult(intent, NickelFragmentActivity.REQUEST_VIP);
                break;
            case R.id.baby_info_layout:
                intent.setClass(getActivity(), BabyInfoActivity.class);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 活动兑换码
     */
    private void reqGenCode(String code) {
        String url = AppConfig.ACTION_CODE;
        PrizesInfoRequest request = new PrizesInfoRequest();
        String token = ((MNJApplication) (getActivity().getApplication())).getToken();
        request.setToken(token);
        request.setActivationCode(code);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

                    @Override
                    public void httpReqResult(String response) {
                        PrizesInfoResponse resp = GsonUtil.parse(response,
                                PrizesInfoResponse.class);
                        if (resp != null)
                            new GenCodeDialog(mAct, resp.isSucess(), resp.getMessage()).show();

                    }
                }
        );
    }


    /**
     * 1.17 获取首页剧集列表
     */
    private void reqMainList() {
        String url = AppConfig.SERIES_LIST;
        SeriesListRequest request = new SeriesListRequest();

        String token = ((MNJApplication) mAct.getApplication()).getToken();

        request.setToken(token);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                SeriesListResponse resp = GsonUtil.parse(response,
                        SeriesListResponse.class);
                if (resp != null && resp.isSucess()) {

                    mList = resp.getData().getList();

                    if (isPhone) {
                        // phone代码块
                        mAdapter = new HomeRecyclerAdapter(mAct, mList);
                        mRecyclerView.setAdapter(mAdapter);

                    } else {
                        // tv 代码块
                        for (int i = 0; i < mList.size(); i++) {
                            int row = 0;
                            if (i < 2) {  //第二行缺少2个，优先补齐第二行的2个
                                row = 1;
                            } else if (i % 2 == 1) {
                                row = 1;
                            }
                            RecommendCardView item = new RecommendCardView(mAct, MetroLayout.Normal, row, mList.get(i), -1);
                            metroLayout.addItemView(item);
                        }
                    }

                } else {
                    Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    /**
     * setTips
     */
    private void setTips() {
        boolean isBeTips = AppUtils.getBooleanSharedPreferences(mAct, tipsKey, false);
        if (!isBeTips) {
            BadgeView badgeView = new BadgeView(mAct);
            badgeView.setTargetView(btnAction);
            badgeView.setText("new");
            badgeView.setTextSize(getResources().getDimensionPixelSize(R.dimen.action_button_tips));
            badgeView.setBadgeGravity(Gravity.BOTTOM | Gravity.RIGHT);
        }
    }


    /**
     * 设置 RecyclerView 间距
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
        }
    }


}
