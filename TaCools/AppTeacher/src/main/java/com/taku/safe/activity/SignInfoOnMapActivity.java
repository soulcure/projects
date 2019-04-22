
package com.taku.safe.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespSignDetail;
import com.taku.safe.protocol.respond.RespSignRestDataList;
import com.taku.safe.protocol.respond.RespUserInfo;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;

/**
 * 圆形地理围栏
 *
 * @author hongming.wang
 * @since 3.2.0
 */
public class SignInfoOnMapActivity extends BasePermissionActivity implements
        LocationSource, AMapLocationListener, View.OnClickListener {
    private static final String TAG = "Amap";

    public static final String SIGN_ID = "sign_id";
    public static final String SIGN_TYPE = "sign_type";
    public static final String IS_CHANGE = "is_change";
    public static final String CHANGE_TIME = "change_time";

    private TextView tv_time;
    private TextView tv_status;
    private TextView tv_address;
    private TextView tv_pic_info;
    private TextView tv_desc;
    private HorizontalScrollView h_scrollview;
    private LinearLayout h_linear;


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
        setContentView(R.layout.activity_sign_info_on_map);
        mContext = this;
        mHandler = new UIHandler(this);

        initMap(savedInstanceState);

        initTitle();
        initView();

        int signType = getIntent().getIntExtra(SIGN_TYPE, 0);
        int signId = getIntent().getIntExtra(SIGN_ID, 0);
        if (signId != 0) {
            reqSignDetail(signType, signId);
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
        tv_title.setText(R.string.sign_info);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {
        LinearLayout linear_change = (LinearLayout) findViewById(R.id.linear_change);
        TextView tv_change_time = (TextView) findViewById(R.id.tv_change_time);

        String changeTime = getIntent().getStringExtra(CHANGE_TIME);
        boolean isChange = getIntent().getBooleanExtra(IS_CHANGE, false);

        if (isChange) {
            linear_change.setVisibility(View.VISIBLE);
            try {
                changeTime = TimeUtils.parseDateFormat(changeTime, TimeUtils.SECOND_DATE_FORMAT);
                tv_change_time.setText(String.format("%s将状态改为正常签到", changeTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        tv_pic_info = (TextView) findViewById(R.id.tv_pic_info);
        h_scrollview = (HorizontalScrollView) findViewById(R.id.h_scrollview);
        h_linear = (LinearLayout) findViewById(R.id.h_linear);

        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        RespUserInfo userInfo = mTakuApp.getUserInfo();
        if (userInfo != null) {
            tv_name.setText(userInfo.getName());
        }
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
     * @param signType 是否是作息签到,0 作息签到, 1 实习签到
     * @param signId   签到Id
     */

    private void reqSignDetail(int signType, int signId) {
        String url;
        if (signType == 0) {
            url = AppConfig.SIGN_DETAIL;
        } else {
            url = AppConfig.PRACTICE_SIGN_DETAIL;
        }

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("signId", signId);


        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespSignDetail baseBean = GsonUtil.parse(response, RespSignDetail.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    double latitude = baseBean.getLatitude();
                    double longitude = baseBean.getLongitude();

                    addSignMarker(latitude, longitude);

                    tv_time.setText(baseBean.getSignDate());

                    int signValid = baseBean.getSignValid();
                    if (signValid == 1) {
                        tv_status.setText(R.string.sign_normal);
                    } else if (signValid == -1) {
                        tv_status.setText(R.string.no_sign);
                        tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_red));
                    } else {
                        tv_status.setText(R.string.sign_unusual);
                        tv_status.setTextColor(ContextCompat.getColor(mContext, R.color.color_orange));
                    }

                    tv_address.setText(baseBean.getLocation());

                    String note = baseBean.getNote();
                    if (!TextUtils.isEmpty(note)) {
                        tv_desc.setText(note);
                    } else {
                        tv_desc.setText("无");
                    }

                    List<String> list = baseBean.getImageList();
                    if (ListUtils.isEmpty(list)) {
                        h_scrollview.setVisibility(View.GONE);
                        tv_pic_info.setText("签到图片：无");
                    } else {
                        final String[] array = list.toArray(new String[list.size()]);

                        for (int i = 0; i < list.size(); i++) {
                            LayoutInflater inflater = LayoutInflater.from(mContext);
                            final View view = inflater.inflate(R.layout.item_show_pic, null);

                            ImageView imgContent = (ImageView) view.findViewById(R.id.img_content);
                            Glide.with(mContext)
                                    .load(list.get(i))
                                    .apply(new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(imgContent);
                            h_linear.addView(view);

                            final int index = i;
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, PictureIndicatorActivity.class);
                                    intent.putExtra("image", array);
                                    intent.putExtra("index", index);
                                    mContext.startActivity(intent);
                                }
                            });

                        }
                    }
                }
            }
        });
    }


    private void addSignMarker(double latitude, double longitude) {
        MarkerOptions markerOption = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location))
                .draggable(false);
        mAMap.addMarker(markerOption);

        LatLng curLatLng = new LatLng(latitude, longitude);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));
    }


    /**
     * 获取学生单次打卡详情
     */
    private void reqRestSignDetail() {
        String url = AppConfig.TEACHER_SIGN_DETAIL;
        int signId = 1;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("signId", signId);


        OkHttpConnector.httpPost(header, url, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespSignRestDataList list = GsonUtil.parse(response, RespSignRestDataList.class);
                if (list != null && list.isSuccess()) {

                }
            }
        });

    }


    /**
     * 获取学生单次打卡详情
     */
    private void handleRestSignDetail() {
        String url = AppConfig.TEACHER_SIGN_APPROVE;
        int signId = 1;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("signId", signId);


        OkHttpConnector.httpPost(header, url, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespSignRestDataList list = GsonUtil.parse(response, RespSignRestDataList.class);
                if (list != null && list.isSuccess()) {

                }
            }
        });

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
        private final WeakReference<SignInfoOnMapActivity> mTarget;

        UIHandler(SignInfoOnMapActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            SignInfoOnMapActivity act = mTarget.get();
            switch (msg.what) {
                default:
                    break;
            }
        }
    }
}
