package com.taku.safe.ui.main;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;
import com.taku.safe.TakuApp;
import com.taku.safe.activity.MapActivity;
import com.taku.safe.activity.MessageActivity;
import com.taku.safe.activity.SignTrackActivity;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.HttpConnector;
import com.taku.safe.http.IGetListener;
import com.taku.safe.protocol.respond.RespNews;
import com.taku.safe.sos.SosInMapActivity;
import com.taku.safe.dialog.ToSoSDialog;
import com.taku.safe.protocol.respond.RespStudentInfo;
import com.taku.safe.sos.StartSosActivity;
import com.taku.safe.ui.mine.MineStudentActivity;
import com.taku.safe.utils.GsonUtil;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;


public class MainStudentFragment1 extends BasePermissionFragment implements View.OnClickListener {

    public final static String TAG = MainStudentFragment1.class.getSimpleName();

    private UIHandler mHandler;

    private RespStudentInfo mSignInfo;

    private QBadgeView badgeView;
    private Banner banner;

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;


    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new UIHandler(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_student1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        reqSignInfo();
        MobclickAgent.onPageStart(TAG);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }


    private void initView(View view) {
        String[] urls = getResources().getStringArray(R.array.url);
        String[] tips = getResources().getStringArray(R.array.title);

        List<String> images = Arrays.asList(urls);
        List<String> titles = Arrays.asList(tips);


        banner = (Banner) view.findViewById(R.id.banner);
        //本地图片数据（资源文件）

        banner.setImages(images)
                .setBannerTitles(titles)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {

                    }
                })
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .start();


        view.findViewById(R.id.tv_mine).setOnClickListener(this);
        view.findViewById(R.id.tv_sos).setOnClickListener(this);
        view.findViewById(R.id.tv_rest).setOnClickListener(this);
        view.findViewById(R.id.tv_internship).setOnClickListener(this);
        view.findViewById(R.id.tv_track).setOnClickListener(this);

        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setOnClickListener(this);

        badgeView = new QBadgeView(mContext);
        Badge badge = badgeView.bindTarget(tv_msg);
        badge.setBadgeTextSize(10, true);
        badge.setBadgeGravity(Gravity.TOP | Gravity.END);
        //badge.setBadgePadding(6, true);

        int offsetX = getResources().getDimensionPixelOffset(R.dimen.badge_offset_x);
        badge.setGravityOffset(offsetX, 5, true);

        badge.setBadgeTextColor(ContextCompat.getColor(getContext(), R.color.color_white));

        int count = mTakuApp.getMsgCount();
        if (count > 0) {
            badgeView.setVisibility(View.VISIBLE);
            badge.setBadgeText(count + "");
        } else {
            badgeView.setVisibility(View.GONE);
        }


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new NewsAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

        reqArticleList();
    }

    /**
     * 请求签到信息
     */
    private void reqSignInfo() {
        mTakuApp.reqSignInfo(new TakuApp.SignInfo() {
            @Override
            public void success(RespStudentInfo info) {
                if (isAdded()) { //网络回调需刷新UI,添加此判断
                    if (info != null
                            && info.isSuccess()
                            && info.getRestSignInfo() != null) {
                        mSignInfo = info;
                    }
                }
            }
        });
    }


    /**
     * 获取每日一文信息列表
     */
    private void reqArticleList() {
        String url = AppConfig.STUDENT_ARTICLE_LIST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        HttpConnector.httpGet(header, url, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespNews bean = GsonUtil.parse(response, RespNews.class);
                if (bean != null && bean.isSuccess()) {
                    List<RespNews.PageInfoBean.ArticleListBean> list = bean.getPageInfo().getArticleList();
                    mAdapter.setList(list);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_mine:
                startActivity(new Intent(mContext, MineStudentActivity.class));
                break;
            case R.id.tv_sos:
                startActivity(new Intent(mContext, StartSosActivity.class));
                break;
            case R.id.tv_rest:
                startSign(MapActivity.TYPE_REST);
                break;
            case R.id.tv_internship:
                startSign(MapActivity.TYPE_PRACTICE);
                break;
            case R.id.tv_track:
                startActivity(new Intent(mContext, SignTrackActivity.class));
                break;
            case R.id.tv_msg:
                if (badgeView.getVisibility() == View.VISIBLE) {
                    badgeView.setVisibility(View.GONE);
                    mTakuApp.setMsgCount(0);
                }
                startActivity(new Intent(mContext, MessageActivity.class));
                break;
        }

    }


    private static final int RC_ACCESS_FINE_LOCATION = 123;

    @AfterPermissionGranted(RC_ACCESS_FINE_LOCATION)
    private void startSos(boolean isTry) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            // Already have permission, do the thing
            // ...
            if (isTry) {
                Intent intent = new Intent(mContext, SosInMapActivity.class);
                intent.putExtra(SosInMapActivity.IS_TRY, true);
                startActivity(intent);
            } else {
                ToSoSDialog.CallBack callBack = new ToSoSDialog.CallBack() {
                    @Override
                    public void onEntry() {
                        if (isAdded()) {
                            Intent intent = new Intent(mContext, SosInMapActivity.class);
                            intent.putExtra(SosInMapActivity.IS_TRY, false);
                            startActivity(intent);
                        }
                    }
                };

                ToSoSDialog.Builder builder = new ToSoSDialog.Builder(getContext());
                builder.callBack(callBack);
                builder.builder().show();
            }

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "没有开启定位权限将无法使用",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @AfterPermissionGranted(RC_ACCESS_FINE_LOCATION)
    private void startSign(int signType) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            // Already have permission, do the thing
            // ...
            Intent intent = new Intent(mContext, MapActivity.class);
            intent.putExtra(MapActivity.SIGN_TYPE, signType);
            intent.putExtra(MapActivity.SIGN_INFO, mSignInfo);
            startActivity(intent);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "没有开启定位权限将无法使用",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<MainStudentFragment1> mTarget;

        UIHandler(MainStudentFragment1 target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }

}
