
package com.taku.safe.ui.sign;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.makeramen.roundedimageview.RoundedImageView;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.adapter.SignTrackAdapter;
import com.taku.safe.config.AppConfig;
import com.taku.safe.entity.MonthChioce;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespSignList;
import com.taku.safe.protocol.respond.RespUserInfo;
import com.taku.safe.refresh.OnNextPageListener;
import com.taku.safe.refresh.RefreshRecyclerView;
import com.taku.safe.utils.GlideCircleTransform;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;

import java.util.List;

public class SignTrackFragment extends BasePermissionFragment {
    private static final String TAG = SignTrackFragment.class.getSimpleName();

    private RefreshRecyclerView mRecyclerView;
    private TextView tv_empty;

    private SignTrackAdapter mAdapter;

    private FloatingActionMenu menuGreen;

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private FloatingActionButton fab5;
    private FloatingActionButton fab6;

    private RoundedImageView imgHeard;
    private TextView tv_name;
    private TextView tv_phone;

    private TextView tv_normal;
    private TextView tv_unusual;

    private String mCurMonth = "";  //当前查询显示的月份

    // 是否刷新状态
    private boolean isPullRefresh = false;
    //private int curPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track_sign, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initTitle(view);
        initView(view);

        RespUserInfo userInfo = mTakuApp.getUserInfo();
        if (userInfo != null) {
            initData(userInfo);
        } else {
            mTakuApp.reqUserInfo(new TakuApp.UserInfo() {
                @Override
                public void success(RespUserInfo info) {
                    initData(info);
                }
            });  //请求用户信息
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurMonth = MonthChioce.instance().getMonthDate(0);

        reqPracticeList();

        //mRecyclerView.setRefreshing(true);  //开启下拉刷新动画
        //mRecyclerView.setLoadMoreEnable(false);  //关闭上拉刷新

        setRefreshEnable(false);
    }

    /**
     * 设置下拉刷新
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshing(boolean b) {
        mRecyclerView.setRefreshEnable(b); //是否关闭下拉刷新
        mRecyclerView.setRefreshing(b);  //是否关闭下拉刷新动画
    }


    /**
     * 是否刷新是否可用
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshEnable(boolean b) {
        mRecyclerView.setRefreshEnable(b); //是否关闭下拉刷新
        mRecyclerView.setLoadMoreEnable(b);  //是否关闭上拉刷新
    }

    /**
     * 关闭刷新动画
     */
    private void refreshComplete() {
        isPullRefresh = false;
        mRecyclerView.setRefreshing(false);  //关闭下拉刷新动画
        mRecyclerView.refreshComplete();//关闭上拉刷新动画
    }


    private void initTitle(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.trace);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    private void initView(View view) {
        tv_normal = (TextView) view.findViewById(R.id.tv_normal);
        tv_unusual = (TextView) view.findViewById(R.id.tv_unusual);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        imgHeard = (RoundedImageView) view.findViewById(R.id.cropImageView);

        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        mRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mRecyclerView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;
                reqPracticeList();
            }
        });


        mAdapter = new SignTrackAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);


        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab1.setImageResource(MonthChioce.instance().getMonthItemRes(0));

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "fab1", Toast.LENGTH_SHORT).show();
                String month = MonthChioce.instance().getMonthDate(0);
                if (!month.equals(mCurMonth)) {
                    mCurMonth = month;
                    reqPracticeList();
                }

                menuGreen.close(true);
                menuGreen.getMenuIconView().setImageResource(MonthChioce.instance().getMonthSelectRes(0));
            }
        });

        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab2.setImageResource(MonthChioce.instance().getMonthItemRes(1));
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "fab2", Toast.LENGTH_SHORT).show();
                String month = MonthChioce.instance().getMonthDate(1);
                if (!month.equals(mCurMonth)) {
                    mCurMonth = month;
                    reqPracticeList();
                }
                menuGreen.close(true);
                menuGreen.getMenuIconView().setImageResource(MonthChioce.instance().getMonthSelectRes(1));
            }
        });

        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
        fab3.setImageResource(MonthChioce.instance().getMonthItemRes(2));
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "fab3", Toast.LENGTH_SHORT).show();
                String month = MonthChioce.instance().getMonthDate(2);
                if (!month.equals(mCurMonth)) {
                    mCurMonth = month;
                    reqPracticeList();
                }
                menuGreen.close(true);
                menuGreen.getMenuIconView().setImageResource(MonthChioce.instance().getMonthSelectRes(2));
            }
        });

        fab4 = (FloatingActionButton) view.findViewById(R.id.fab4);
        fab4.setImageResource(MonthChioce.instance().getMonthItemRes(3));
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "fab3", Toast.LENGTH_SHORT).show();
                String month = MonthChioce.instance().getMonthDate(3);
                if (!month.equals(mCurMonth)) {
                    mCurMonth = month;
                    reqPracticeList();
                }
                menuGreen.close(true);
                menuGreen.getMenuIconView().setImageResource(MonthChioce.instance().getMonthSelectRes(3));
            }
        });

        fab5 = (FloatingActionButton) view.findViewById(R.id.fab5);
        fab5.setImageResource(MonthChioce.instance().getMonthItemRes(4));
        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "fab3", Toast.LENGTH_SHORT).show();
                String month = MonthChioce.instance().getMonthDate(4);
                if (!month.equals(mCurMonth)) {
                    mCurMonth = month;
                    reqPracticeList();
                }
                menuGreen.close(true);
                menuGreen.getMenuIconView().setImageResource(MonthChioce.instance().getMonthSelectRes(4));
            }
        });

        fab6 = (FloatingActionButton) view.findViewById(R.id.fab6);
        fab6.setImageResource(MonthChioce.instance().getMonthItemRes(5));
        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "fab3", Toast.LENGTH_SHORT).show();
                String month = MonthChioce.instance().getMonthDate(5);
                if (!month.equals(mCurMonth)) {
                    mCurMonth = month;
                    reqPracticeList();
                }
                menuGreen.close(true);
                menuGreen.getMenuIconView().setImageResource(MonthChioce.instance().getMonthSelectRes(5));
            }
        });


        menuGreen = (FloatingActionMenu) view.findViewById(R.id.menu_green);
        //menuGreen.getMenuIconView().setImageResource(R.mipmap.ic_launcher);

        /*menuGreen.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                String text;
                if (opened) {
                    text = "Menu opened";
                } else {
                    text = "Menu closed";
                }
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });*/


        createCustomAnimation();

        menuGreen.getMenuIconView().setImageResource(MonthChioce.instance().getMonthSelectRes(0));

    }


    private void initData(RespUserInfo info) {
        tv_name.setText(info.getName());
        tv_phone.setText(info.getPhoneNo());
        int gender = info.getGender();
        String url = info.getAvataUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imgHeard);
        } else {
            if (gender == 1) {
                imgHeard.setImageResource(R.mipmap.ic_male);
            } else {
                imgHeard.setImageResource(R.mipmap.ic_female);
            }
        }
    }

    private void reqPracticeList() {
        String url = AppConfig.SIGN_LIST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("month", mCurMonth);
        //params.put("page", curPage);
        //params.put("limit", 10);


        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespSignList bean = GsonUtil.parse(response, RespSignList.class);
                if (bean != null && bean.isSuccess()) {
                    List<RespSignList.SignlistBean> list = bean.getSignlist();
                    if (ListUtils.isEmpty(list)) {
                        tv_empty.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    } else {
                        tv_empty.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }

                    tv_normal.setText(bean.signNormalCount());
                    tv_unusual.setText(bean.signUnusualCount());

                    mAdapter.setList(list);
                    //curPage++;

                    //todo
                    /*if (bean.getPageInfo().isLastPage()) {
                        mRecyclerView.setOnNextPageListener(null);
                    } else {
                        mRecyclerView.setLoadMoreEnable(true);  //开启上拉刷新
                    }
                    mRecyclerView.setRefreshEnable(false); //关闭下拉刷新
                    */
                }
                refreshComplete();
            }
        });

    }


    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleX", 1.0f, 0.9f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleY", 1.0f, 0.9f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleX", 0.9f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menuGreen.getMenuIconView(), "scaleY", 0.9f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        /*scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menuGreen.getMenuIconView().setImageResource(menuGreen.isOpened()
                        ? R.mipmap.ic_close : resId);
            }
        });*/

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuGreen.setIconToggleAnimatorSet(set);
    }

}
