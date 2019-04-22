package com.ivmall.android.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.ExchangeDialog;
import com.ivmall.android.app.dialog.UpdateDialog;
import com.ivmall.android.app.entity.AppUpdateResponse;
import com.ivmall.android.app.entity.CategoryItem;
import com.ivmall.android.app.entity.CategoryResponse;
import com.ivmall.android.app.entity.ProfileItem;
import com.ivmall.android.app.entity.ProtocolRequest;
import com.ivmall.android.app.entity.TopicItem;
import com.ivmall.android.app.entity.TopicResponse;
import com.ivmall.android.app.fragment.PlayListFragment;
import com.ivmall.android.app.impl.OnSucessListener;
import com.ivmall.android.app.player.FreePlayingActivity;
import com.ivmall.android.app.player.SmartPlayingActivity;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.FileAsyncTaskDownload;
import com.ivmall.android.app.uitls.GlideCircleTransform;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.uitls.OnEventId;
import com.ivmall.android.app.uitls.StringUtils;
import com.ivmall.android.app.views.GalleryBar.GalleryAdapter;
import com.ivmall.android.app.views.GalleryBar.GalleryView;

import java.util.ArrayList;
import java.util.List;


public class CartoonFragments extends Fragment implements View.OnClickListener {
    public final static String TAG = CartoonFragments.class.getSimpleName();

    private Activity mAct;
    private boolean isPhone = true;
    private boolean isShowUpdateDialog; //是否提示版本升级

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SliderLayout mSlider;
    private GalleryView mGalleryView;

    private GalleryAdapter mGaAdapter;

    private TextView tvInfo;
    private ImageView imgHead;


    // --------------------配置数据 end--------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
        this.isPhone = ((MainFragmentActivity) activity).isPhone;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reqAppUpdate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tongle_fragments, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);


        if (isPhone) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.app_bar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            mSlider = (SliderLayout) view.findViewById(R.id.slider);
            DrawerLayout drawerLayout = ((MainFragmentActivity) getActivity()).getDrawerLayout();
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.close);
            drawerLayout.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        } else {
            mGalleryView = (GalleryView) view.findViewById(R.id.galleryview);
            //initPadInfo(view);

            tvInfo = (TextView) view.findViewById(R.id.tv_info);
            imgHead = (ImageView) view.findViewById(R.id.img_head);
            imgHead.setOnClickListener(this);
            view.findViewById(R.id.user_area).setOnClickListener(this);
            view.findViewById(R.id.img_exchange).setOnClickListener(this);
            view.findViewById(R.id.img_action).setOnClickListener(this);
            view.findViewById(R.id.img_mall).setOnClickListener(this);
        }

        initTopic();
        initAllCategorys();
    }


    /**
     * 获取专题，带有application缓存
     */
    private void initTopic() {
        String topicStr = ((KidsMindApplication) mAct.getApplication())
                .getTopicStr();
        if (StringUtils.isEmpty(topicStr)) {
            reqTopic();  //取一个专题
        } else {
            parseTopic(topicStr);
        }

    }

    /**
     * 获取TAB内容，带有application缓存
     */
    private void initAllCategorys() {
        String allCategoryStr = ((KidsMindApplication) mAct.getApplication())
                .getAllCategoryStr();
        if (StringUtils.isEmpty(allCategoryStr)) {
            reqAllCategorys();
        } else {
            parseCategorys(allCategoryStr);
        }
    }

    /**
     * 解析专题数据
     *
     * @param response
     */
    private void parseTopic(String response) {

        TopicResponse resp = GsonUtil.parse(response,
                TopicResponse.class);
        if (resp != null && resp.isSucess()) {

            ((KidsMindApplication) mAct.getApplication())
                    .setTopicStr(response);

            List<TopicItem> lists = resp.getData().getList();

            if (isPhone) {
                for (TopicItem item : lists) {
                    initSlider(item);
                }
            } else {
                initViewBar(lists);
            }
        }
    }


    /**
     * 解析节目列表数据
     *
     * @param response
     */
    private void parseCategorys(String response) {

        CategoryResponse resp = GsonUtil.parse(response,
                CategoryResponse.class);

        if (resp != null && resp.isSucess()) {
            ((KidsMindApplication) mAct.getApplication())
                    .setAllCategoryStr(response);

            List<CategoryItem> list = resp.getData().getList();
            TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager());

            for (CategoryItem item : list) {
                String tabName = item.getCategory();
                String title = item.getName();
                adapter.addFragment(PlayListFragment.newInstance(tabName), title);
                mTabLayout.addTab(mTabLayout.newTab().setText(title));
            }

            mViewPager.setAdapter(adapter);
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        isShowUpdateDialog = true;
        if (!isPhone) {
            initHeadImage();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        isShowUpdateDialog = false;

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.img_exchange:
                new ExchangeDialog(mAct).show();
                break;

            case R.id.img_action:
                Intent intent = new Intent(mAct, ActionActivity.class);
                startActivity(intent);
                BaiduUtils.onEvent(mAct, OnEventId.WEB_ACTION, getString(R.string.web_action));
                break;

            case R.id.img_mall:
                final String url = ((KidsMindApplication) mAct.getApplication())
                        .getAppConfig("shoppingURL");

                if (!StringUtils.isEmpty(url)) {

                    Intent intent1 = new Intent(mAct, MallActivity.class);
                    intent1.putExtra(MallActivity.URL_KEY, url);
                    startActivity(intent1);
                    BaiduUtils.onEvent(mAct, OnEventId.ENTRY_TOY_MALL,
                            getString(R.string.entry_toy_mall));
                }
                break;

            case R.id.user_area:
            case R.id.img_head:

                if (!((KidsMindApplication) mAct.getApplication()).isLogin()) {
                    Intent intent2 = new Intent(mAct, BaseActivity.class);
                    intent2.putExtra(BaseActivity.NAME, BaseActivity.LOGIN);
                    mAct.startActivity(intent2);
                } else {
                    Intent intent2 = new Intent(mAct, BaseActivity.class);
                    intent2.putExtra(BaseActivity.NAME, BaseActivity.USER_INFO);
                    mAct.startActivity(intent2);
                }
                break;
        }


    }

    /**
     * 获取用户头像
     */
    private void initHeadImage() {
        ProfileItem profileItem = ((KidsMindApplication) mAct.getApplication()).getProfile();
        if (profileItem != null) {
            initHead(profileItem);
        } else {
            ((KidsMindApplication) mAct.getApplication()).reqProfile(new OnReqProfileResult());
        }
    }

    private void initHead(ProfileItem profileItem) {
        String imgUrl = profileItem.getImgUrl();
        if (!StringUtils.isEmpty(imgUrl)) {
            Glide.with(mAct)
                    .load(imgUrl)
                    .centerCrop()
                    .bitmapTransform(new GlideCircleTransform(mAct)) //设置图片圆角
                    .placeholder(R.drawable.icon_login_image)  //占位图片
                    .error(R.drawable.icon_login_image)        //下载失败
                    .into(imgHead);

        }
        if (((KidsMindApplication) mAct.getApplication()).isLogin()) {
            tvInfo.setText(((KidsMindApplication) mAct.getApplication()).getUserName());
        } else {
            tvInfo.setText(R.string.please_login);
        }
    }

    private class OnReqProfileResult implements OnSucessListener {

        @Override
        public void sucess() {
            ProfileItem list = ((KidsMindApplication) mAct.getApplication()).getProfile();
            boolean isDestroy = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroy = mAct.isDestroyed();
            }

            if (list != null && !isDestroy) {
                initHead(list);
            }
        }

        @Override
        public void create() {
        }

        @Override
        public void fail() {
        }

    }

    /**
     * 1.78 获所有的分类
     */
    private void reqAllCategorys() {
        String url = AppConfig.ALL_CATEGORYS;
        ProtocolRequest request = new ProtocolRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                parseCategorys(response);
            }
        });
    }


    /**
     * 1.79 获取广告、专题和收藏信息
     */
    private void reqTopic() {
        String url = AppConfig.CONTENT_INFO;
        ProtocolRequest request = new ProtocolRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                parseTopic(response);
            }
        });
    }


    private void initViewBar(List<TopicItem> lists) {
        mGaAdapter = new GalleryAdapter(mAct, lists);
        mGalleryView.setAdapter(mGaAdapter);
        // 点击事件
        mGaAdapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(TopicItem item, int position) {
                clickImgItem(item);
            }
        });

    }


    /**
     * 初始化banner
     *
     * @param item 专题数据
     */
    private void initSlider(final TopicItem item) {

        TextSliderView defaultSliderView = new TextSliderView(mAct);
        defaultSliderView.description(item.getTitle())
                .image(item.getImgUrl())
                .setScaleType(BaseSliderView.ScaleType.Fit)
                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                    @Override
                    public void onSliderClick(BaseSliderView baseSliderView) {
                        clickImgItem(item);
                    }
                });
        mSlider.addSlider(defaultSliderView);

        mSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);   //滚动时间间隔
    }

    private void clickImgItem(TopicItem item) {
        String type = item.getType();
        if (type.equals(TopicType.babyshow.toString())) {
            Intent intent = new Intent(mAct, FreePlayingActivity.class);
            intent.putExtra("type", FreePlayingActivity.FROM_UGC);

            String ugc = ((KidsMindApplication) mAct.getApplicationContext())
                    .getAppConfig("UGCSerieId");
            try {
                int serieId = Integer.parseInt(ugc);
                intent.putExtra("serieId", serieId);
                mAct.startActivity(intent);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            BaiduUtils.onEvent(mAct, OnEventId.GOLBAL_KIDS,
                    mAct.getString(R.string.golbal_kids));
        } else if (type.equals(TopicType.favorite.toString())) {
            Intent intent = new Intent(mAct, FreePlayingActivity.class);
            intent.putExtra("type", FreePlayingActivity.FROM_FAVORITE);
            mAct.startActivity(intent);
            BaiduUtils.onEvent(mAct, OnEventId.FAVORITE_CHANNEL, mAct.getString(R.string.favorite_channel));
        } else if (type.equals(TopicType.subject.toString())) {
            Intent intent = new Intent(mAct, FreePlayingActivity.class);
            intent.putExtra("type", FreePlayingActivity.FROM_TOPIC_PLAY);
            try {
                int serieId = Integer.parseInt(item.getContentid());
                intent.putExtra("serieId", serieId);
            } catch (NumberFormatException e) {
            }
            mAct.startActivity(intent);
        } else if (type.equals(TopicType.earlyeducation.toString())) {
            Intent intent = new Intent(mAct, SmartPlayingActivity.class);
            startActivity(intent);
            BaiduUtils.onEvent(mAct, OnEventId.SMART_PLAY_SESSION, getString(R.string.smart_play_session));
        } else if (type.equals(TopicType.ad.toString())) {
            Intent intent = new Intent(mAct, MallActivity.class);
            intent.putExtra(MallActivity.URL_KEY, item.getContentid());
            startActivity(intent);
        } else if (type.equals(TopicType.activity.toString())) {
            Intent intent = new Intent(mAct, ActionActivity.class);
            //intent.putExtra(MallActivity.URL_KEY, item.getContentid());
            startActivity(intent);
        }
    }


    /**
     * 1.25 应用自升级
     */
    private void reqAppUpdate() {
        String url = AppConfig.APP_UPDATE;
        ProtocolRequest request = new ProtocolRequest();

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                final AppUpdateResponse resp = GsonUtil.parse(response,
                        AppUpdateResponse.class);
                if (resp != null && resp.isSucess()) {
                    final String url = resp.getUpgradeUrl();
                    final String md5 = resp.getChecksum();
                    final boolean isWifi = AppUtils.isWifi(mAct);

                    if (isWifi) {
                        FileAsyncTaskDownload.DownLoadingListener listener =
                                new FileAsyncTaskDownload.DownLoadingListener() {
                                    @Override
                                    public void onProgress(int rate) {
                                    }

                                    @Override
                                    public void downloadFail(String err) {
                                    }

                                    @Override
                                    public void downloadSuccess(final String path) {
                                        if (isShowUpdateDialog) {
                                            final UpdateDialog dialog = new UpdateDialog(mAct);
                                            dialog.show();
                                            dialog.setUpgradeDesc(resp.getDescription());
                                            dialog.setVersion(resp.getAppVersion());

                                            dialog.showInstall();
                                            dialog.setOnEnsureUpgradeListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    AppUtils.installApk(mAct, path);
                                                }
                                            });
                                        }
                                    }
                                };
                        new FileAsyncTaskDownload(listener).execute(url, md5);
                    } else {
                        if (isShowUpdateDialog) {
                            final UpdateDialog dialog = new UpdateDialog(mAct);
                            dialog.show();
                            dialog.setUpgradeDesc(resp.getDescription());
                            dialog.setVersion(resp.getAppVersion());
                            dialog.setOnEnsureUpgradeListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FileAsyncTaskDownload.DownLoadingListener listener =
                                            new FileAsyncTaskDownload.DownLoadingListener() {
                                                @Override
                                                public void onProgress(int rate) {
                                                    dialog.setRateText(rate);
                                                }

                                                @Override
                                                public void downloadFail(String err) {
                                                    Toast.makeText(mAct, getString(R.string.download_error), Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void downloadSuccess(String path) {
                                                    dialog.dismiss();
                                                    AppUtils.installApk(mAct, path);
                                                }
                                            };
                                    new FileAsyncTaskDownload(listener).execute(url, md5);
                                }
                            });
                        }
                    }
                }
            }

        });
    }


    static class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mTitles = new ArrayList<>();

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }


    public enum TopicType {
        babyshow,
        favorite,
        subject,
        earlyeducation,
        ad,
        activity
    }
}
