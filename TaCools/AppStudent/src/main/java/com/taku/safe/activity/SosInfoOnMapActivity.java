
package com.taku.safe.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.adapter.VoiceAdapter;
import com.taku.safe.config.AppConfig;
import com.taku.safe.dialog.SosDialog;
import com.taku.safe.entity.VoiceItem;
import com.taku.safe.http.DownloadListener;
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespSosDetail;
import com.taku.safe.protocol.respond.RespSosLatest;
import com.taku.safe.protocol.respond.SosInfo;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.MediaPlayManager;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 圆形地理围栏
 *
 * @author hongming.wang
 * @since 3.2.0
 */
public class SosInfoOnMapActivity extends BasePermissionActivity implements
        LocationSource, AMapLocationListener, View.OnClickListener {
    private static final String TAG = "Amap";

    private static final String NOTIFY_ACTION = "com.taku.safe.Intent.FROM_NOTIFICATION";

    public static final int HANDLER_REFRESH_SOS = 1;

    public static final String SIGN_ID = "sign_id";
    public static final String SIGN_TYPE = "sign_type";

    private TextView tv_from;
    private TextView tv_address;
    private TextView tv_phone;

    private VoiceAdapter mAdapter;
    private Marker mMarker;

    /**
     * 用于显示当前的位置
     * <p>
     * 示例中是为了显示当前的位置，在实际使用中，单独的地理围栏可以不使用定位接口
     * </p>
     */
    private AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private MapView mMapView;
    private AMap mAMap;


    private UIHandler mHandler;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_info_on_map);
        mContext = this;
        mHandler = new UIHandler(this);

        initMap(savedInstanceState);

        initTitle();
        initView();

        int sosId = getIntent().getIntExtra("sosId", 0);
        SosInfo sosInfo = getIntent().getParcelableExtra("sosInfo");

        if (sosInfo != null) {
            String type = sosInfo.getPushType();
            if (type != null
                    && type.equals("sos_new")
                    && sosInfo.getSosId() != 0) {
                reqSosDetail(sosInfo.getSosId());
            }

            double latitude = sosInfo.getLat();
            double longitude = sosInfo.getLng();
            addSignMarker(latitude, longitude);

            boolean isFg = getIntent().getBooleanExtra("isFg", false);
            if (isFg) {
                SosDialog.Builder builder = new SosDialog.Builder();
                builder.context(this).latitude(latitude).longitude(longitude);
                builder.builder().show();
            }
        } else if (sosId != 0) {
            reqSosDetail(sosId);
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        SosInfo sosInfo = getIntent().getParcelableExtra("sosInfo");
        if (sosInfo != null) {
            String type = sosInfo.getPushType();
            if (type != null && type.equals("sos_stop")) {
                // TODO: 2017/8/5
                //sos 停止
                if (mHandler.hasMessages(HANDLER_REFRESH_SOS)) {
                    mHandler.removeMessages(HANDLER_REFRESH_SOS);
                }
            }
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
        MobclickAgent.onPause(this);

        MediaPlayManager.instance().pause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        if (mHandler.hasMessages(HANDLER_REFRESH_SOS)) {
            mHandler.removeMessages(HANDLER_REFRESH_SOS);
        }
    }


    @Override
    public void onBackPressed() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (shouldUpRecreateTask(this)) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            super.onBackPressed();
        }
    }


    private boolean shouldUpRecreateTask(Activity from) {
        String action = from.getIntent().getAction();
        return action != null && action.equals(NOTIFY_ACTION);
    }


    private void initMap(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(16));
            setUpMap();
        }
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.sos_detail);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                onBackPressed();
            }
        });

    }

    private void initView() {
        tv_from = (TextView) findViewById(R.id.tv_from);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_phone = (TextView) findViewById(R.id.tv_phone);

        RecyclerView recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new VoiceAdapter(this);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(mAdapter);
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        /*myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));*/
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(mContext, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 请求作息签到
     *
     * @param sosId SOS Id
     */
    private void reqSosDetail(final int sosId) {

        String url = AppConfig.TEACHER_SOS_DETAIL;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("sosId", sosId);

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespSosDetail bean = GsonUtil.parse(response, RespSosDetail.class);
                if (bean != null && bean.isSuccess()) {
                    double latitude = bean.getLatitude();
                    double longitude = bean.getLongitude();

                    String format = getString(R.string.sos_from);
                    tv_from.setText(String.format(format, bean.getStudentName()));
                    tv_address.setText(bean.getAddress());
                    tv_phone.setText(bean.getStudentPhone());

                    addSignMarker(latitude, longitude);
                    List<RespSosDetail.VoliceListBean> voliceList = bean.getVoliceList();
                    for (RespSosDetail.VoliceListBean item : voliceList) {
                        final int time = item.getDuration();
                        downLoadVoice(item.getVoiceUrl(), new DownloadListener() {
                            @Override
                            public void onProgress(int rate) {

                            }

                            @Override
                            public void onFail(String err) {

                            }

                            @Override
                            public void onSuccess(String path) {
                                VoiceItem voiceItem = new VoiceItem();
                                voiceItem.setAutoPlay(false);
                                voiceItem.setUrl(path);
                                voiceItem.setDuration(time);
                                mAdapter.setItem(voiceItem);
                            }
                        });
                    }

                    if (!mHandler.hasMessages(HANDLER_REFRESH_SOS)) {
                        Message msg = mHandler.obtainMessage(HANDLER_REFRESH_SOS);
                        msg.arg1 = sosId;
                        mHandler.sendMessageDelayed(msg, 5000);
                    }

                }
            }
        });
    }


    /**
     * 下载录音文件
     *
     * @param url
     */
    private void downLoadVoice(String url, DownloadListener downLoadListen) {
        if (!TextUtils.isEmpty(url)) {
            OkHttpConnector.httpDownload(url, null, downLoadListen);
        }
    }


    /**
     * 定时更新救援详情
     */
    private void reqSosLatest(final int sosId) {

        String url = AppConfig.TEACHER_SOS_LATEST;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("sosId", sosId);

        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespSosLatest bean = GsonUtil.parse(response, RespSosLatest.class);
                if (bean != null && bean.isSuccess()) {
                    double latitude = bean.getLatitude();
                    double longitude = bean.getLongitude();

                    addSignMarker(latitude, longitude);
                    String url = bean.getVoiceUrl();
                    downLoadVoice(url, new DownloadListener() {
                        @Override
                        public void onProgress(int rate) {

                        }

                        @Override
                        public void onFail(String err) {

                        }

                        @Override
                        public void onSuccess(String path) {
                            VoiceItem item = new VoiceItem();
                            item.setAutoPlay(true);
                            item.setUrl(path);
                            mAdapter.setItem(item);
                        }
                    });

                    if (!mHandler.hasMessages(HANDLER_REFRESH_SOS)) {
                        Message msg = mHandler.obtainMessage(HANDLER_REFRESH_SOS);
                        msg.arg1 = sosId;
                        mHandler.sendMessageDelayed(msg, 5000);
                    }
                }
            }
        });
    }


    private void addSignMarker(double latitude, double longitude) {
        LatLng curLatLng = new LatLng(latitude, longitude);
        if (mMarker == null) {
            MarkerOptions markerOption = new MarkerOptions()
                    .position(curLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location))
                    .draggable(false);
            mMarker = mAMap.addMarker(markerOption);
        } else {
            mMarker.setPosition(curLatLng);
        }

        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));
    }



    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听   //关闭定位
            //mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            //mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
            // 只是为了获取当前位置，所以设置为单次定位
            //mLocationOption.setOnceLocation(true);
            // 设置定位参数
            //mlocationClient.setLocationOption(mLocationOption);
            //mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_send:

                break;
            case R.id.btn_commit:

                break;
        }
    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<SosInfoOnMapActivity> mTarget;

        UIHandler(SosInfoOnMapActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            SosInfoOnMapActivity act = mTarget.get();
            if (act == null) {
                return;
            }
            switch (msg.what) {
                case HANDLER_REFRESH_SOS:
                    int sosId = msg.arg1;
                    act.reqSosLatest(sosId);
                    break;
                default:
                    break;
            }
        }
    }
}
