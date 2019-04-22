package com.ivmall.android.app.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ivmall.android.app.KidsMindApplication;
import com.ivmall.android.app.config.AppConfig;
import com.ivmall.android.app.dialog.UpdateDialog;
import com.ivmall.android.app.entity.AppUpdateResponse;
import com.ivmall.android.app.entity.CartoonItem;
import com.ivmall.android.app.entity.CartoonRoleResponse;
import com.ivmall.android.app.entity.MainPlayListRequest;
import com.ivmall.android.app.entity.ProtocolRequest;
import com.ivmall.android.app.entity.TopicItem;
import com.ivmall.android.app.entity.TopicRequest;
import com.ivmall.android.app.entity.TopicResponse;
import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.BaiduUtils;
import com.ivmall.android.app.uitls.FileAsyncTaskDownload;
import com.ivmall.android.app.uitls.GsonUtil;
import com.ivmall.android.app.uitls.HttpConnector;
import com.ivmall.android.app.uitls.IPostListener;
import com.ivmall.android.app.views.MetroLayout;
import com.ivmall.android.app.views.RecommendCardView;
import com.smit.android.ivmall.stb.R;

import java.util.List;


public class MetroFragment extends Fragment {
    private static final String TAG = MetroFragment.class.getSimpleName();


    private MetroLayout metroLayout;
    private Activity mAct;
    private boolean isShowUpdateDialog; //是否提示版本升级


    private String mInfo;

    public static MetroFragment newInstance(String info) {
        Bundle args = new Bundle();
        MetroFragment fragment = new MetroFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reqAppUpdate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInfo = getArguments().getString("info");
        return inflater.inflate(R.layout.metro_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        metroLayout = (MetroLayout) view.findViewById(R.id.metrolayout);

        RecommendCardView smartPlay = new RecommendCardView(mAct, MetroLayout.Vertical, 0, RecommendCardView.SMART_PLAY);
        metroLayout.addItemView(smartPlay);

        RecommendCardView ugc = new RecommendCardView(mAct, MetroLayout.Normal, 1, RecommendCardView.UGC_PLAY);
        metroLayout.addItemView(ugc);

        RecommendCardView favorite = new RecommendCardView(mAct, MetroLayout.Normal, 1, RecommendCardView.FAVORITE_PLAY);
        metroLayout.addItemView(favorite);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reqTopic(1);//默认取1个
        reqMainList(0, 1000);
    }


    @Override
    public void onResume() {
        super.onResume();
        isShowUpdateDialog = true;
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

    @Override
    public void onStop() {
        super.onStop();
        isShowUpdateDialog = false;
    }

    /**
     * 1.17 获取首页剧集列表
     */
    /*private void reqMainList(int count) {
        String url = AppConfig.SMART_RECOMMEND_SERIE;
        SmartRecommendRequest request = new SmartRecommendRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        int profileId = ((KidsMindApplication) mAct.getApplication()).getProfileId();

        request.setToken(token);
        request.setCount(count);
        request.setProfileId(profileId);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                SmartRecommendResponse resp = GsonUtil.parse(response,
                        SmartRecommendResponse.class);
                if (resp.isSucess()) {
                    List<RecommendItem> list = resp.getData().getRecommendation();

                    for (int i = 0; i < list.size(); i++) {
                        int row = 0;

                        if (i < 3) {  //第二行缺少3个，优先补齐第二行的3个
                            row = 1;
                        } else if (i % 2 == 0) {
                            row = 1;
                        }

                        RecommendCardView item = new RecommendCardView(mAct, MetroLayout.Normal, row, list.get(i), -1);
                        metroLayout.addItemView(item);
                    }
                } else {
                    Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }*/

    /**
     * 1.17 获取首页剧集列表
     */
    private void reqMainList(final int start, final int offset) {
        String url = AppConfig.MAIN_PLAYLIST_V2;
        MainPlayListRequest request = new MainPlayListRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();
        request.setToken(token);
        request.setStartIndex(start);
        request.setOffset(offset);
        request.setCategory(mInfo);

        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {

            @Override
            public void httpReqResult(String response) {
                // TODO Auto-generated method stub
                CartoonRoleResponse resp = GsonUtil.parse(response,
                        CartoonRoleResponse.class);
                if (resp.isSucess()) {

                    List<CartoonItem> list = resp.getData().getList();
                    for (int i = 0; i < list.size(); i++) {
                        int row = i % 2;

                        RecommendCardView item = new RecommendCardView(mAct, MetroLayout.Normal, row, list.get(i), -1);
                        metroLayout.addItemView(item);
                    }
                } else {
                    Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }


    /**
     * 1.57 获取专题列表
     */
    private void reqTopic(int count) {
        String url = AppConfig.PLAY_TOPIC;
        TopicRequest request = new TopicRequest();

        String token = ((KidsMindApplication) mAct.getApplication()).getToken();

        request.setToken(token);
        request.setRowCount(count);


        String json = request.toJsonString();
        HttpConnector.httpPost(url, json, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                TopicResponse resp = GsonUtil.parse(response,
                        TopicResponse.class);

                if (resp.isSucess()) {
                    TopicItem item = resp.getData().getTopicFirstItem();
                    RecommendCardView action = new RecommendCardView(mAct, MetroLayout.Horizontal, 0, item, RecommendCardView.TOPIC_PLAY);
                    metroLayout.addItemView(action);
                } else {
                    Toast.makeText(mAct, resp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
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
                if (null != resp && resp.isSucess()) {
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
}
