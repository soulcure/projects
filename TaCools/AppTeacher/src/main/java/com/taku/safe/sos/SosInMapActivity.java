
package com.taku.safe.sos;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
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
import com.taku.safe.config.AppConfig;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespNewSos;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.VoiceUtils;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 圆形地理围栏
 */
public class SosInMapActivity extends BasePermissionActivity implements
        LocationSource, AMapLocationListener, View.OnClickListener {

    private static final String TAG = "Amap";

    public static final String SOS_ID = "sosId";
    public static final String IS_TRY = "isTry";

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
    private TextView tv_record;


    private int mSosId;
    private boolean mSosStop = false;
    private boolean isTry;

    private Marker marker;
    private VoiceUtils mVoiceRecord;

    private class MarkerInfoWindow implements AMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.sos_marker, null);
            /*TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
            tv_info.setText(marker.getTitle());*/

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        initMap(savedInstanceState);
        initTitle();
        initView();
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
        //deactivate();//sos灭屏不停止定位
        MobclickAgent.onPause(this);
    }


    @Override
    public void onBackPressed() {
        if (isTry || mSosStop || mSosId == 0) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("确定退出SOS模式，退出将表示我已安全")
                    .setPositiveButton("我已安全",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                    reportSosStop();
                                }
                            })
                    .setNegativeButton("我再想想", null)
                    .show();
        }
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
        tv_title.setText(R.string.sos_title);

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
        tv_record = (TextView) findViewById(R.id.tv_record);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        isTry = getIntent().getBooleanExtra(IS_TRY, true);
        mSosId = getIntent().getIntExtra(SOS_ID, 0);

        if (mSosId != 0) {
            mVoiceRecord = new VoiceUtils(mContext, mTakuApp.getToken(), mSosId);
            mVoiceRecord.startRecord();//开始录音上传
            mVoiceRecord.setStop(false);
        }

        if (isTry) {
            tv_record.setText("SOS体验模式!");
        }
    }


    private void showInfoMarker(double latitude, double longitude) {
        if (marker == null) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.title("录音及定位实时上传中...");

            //markerOption.draggable(true);//设置Marker可拖动
            markerOption.position(new LatLng(latitude, longitude));
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_location)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(false);//设置marker平贴地图效果
            //markerOption.zIndex(0.5f);

            marker = mAMap.addMarker(markerOption);
        } else {
            marker.setPosition(new LatLng(latitude, longitude));
        }

        marker.showInfoWindow();
    }


    private void reqNewSos(double latitude, double longitude, String address, int locateMode) {
        String url = AppConfig.SOS_NEW;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("address", address);
        params.put("mapType", 0);
        params.put("locateMode", locateMode);

        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespNewSos bean = GsonUtil.parse(response, RespNewSos.class);
                if (bean != null && bean.isSuccess()) {
                    mSosId = bean.getSosId();
                    mVoiceRecord = new VoiceUtils(mContext, mTakuApp.getToken(), mSosId);
                    mVoiceRecord.startRecord();//开始录音上传
                    mVoiceRecord.setStop(false);
                }
            }
        });
    }


    private void reportSosLocation(double latitude, double longitude, String address, int locateMode) {
        String url = AppConfig.SOS_REPORT_LOC;

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("sosId", mSosId);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("address", address);
        params.put("mapType", 0);
        params.put("locateMode", locateMode);

        OkHttpConnector.httpPost(header, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean bean = GsonUtil.parse(response, RespBaseBean.class);
                if (bean != null && bean.isSuccess()) {

                }
            }
        });
    }


    /**
     * 我已安全
     */
    private void reportSosStop() {

        if (isTry) {
            Toast.makeText(this, "救援模式下，按此按键表示我无需救援", Toast.LENGTH_LONG).show();
            return;
        }

        if (mSosStop) {
            Toast.makeText(this, "已经停止救援，如需救援请重新进入", Toast.LENGTH_LONG).show();
            return;
        }

        if (mSosId == 0) {
            Toast.makeText(this, "未正确请求到救援ID", Toast.LENGTH_LONG).show();
            return;
        }

        mVoiceRecord.stopRecord(true, 0); //停止自动录音,最后一次录用上传
        mVoiceRecord.setStop(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.SOS_STOP;

                ContentValues header = new ContentValues();
                header.put("token", mTakuApp.getToken());

                ContentValues params = new ContentValues();
                params.put("sosId", mSosId);

                OkHttpConnector.httpPost(header, url, params, new IPostListener() {
                    @Override
                    public void httpReqResult(String response) {
                        RespBaseBean bean = GsonUtil.parse(response, RespBaseBean.class);
                        if (bean != null && bean.isSuccess()) {
                            if (mVoiceRecord != null) {
                                mSosStop = true;
                            }
                            finish();
                        }
                    }
                });
            }
        }, 50);
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.showMyLocation(false);  ////设置是否显示定位小蓝点
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

        mAMap.setInfoWindowAdapter(new MarkerInfoWindow());//AMap类中

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
        deactivate();
        mMapView.onDestroy();

        if (mVoiceRecord != null) {
            mVoiceRecord.stopRecord(false, 0); //停止自动录音
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
                locationInfo(amapLocation);
                // float distance = AMapUtils.calculateLineDistance(centerLatLng, curLatLng);//距离判断
                Log.v(TAG, "Location Success");
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e(TAG, errText);
                Toast.makeText(mContext, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void locationInfo(AMapLocation amapLocation) {
        int locateMode;
        int locationType = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        if (locationType == AMapLocation.LOCATION_TYPE_CELL) {//基站定位结果,定位精度在500米-5000米之间
            locateMode = 1;
        } else {
            locateMode = 0;
        }

        double latitude = amapLocation.getLatitude();//获取纬度
        double longitude = amapLocation.getLongitude();//获取经度

        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 16));

        showInfoMarker(latitude, longitude);

        String province = amapLocation.getProvince();//省信息
        String city = amapLocation.getCity();//城市信息
        String district = amapLocation.getDistrict();//城区信息
        String street = amapLocation.getStreet();//街道信息
        String streetNum = amapLocation.getStreetNum();//街道门牌号信息
        String aoiName = amapLocation.getAoiName();//获取当前定位点的AOI信息

        String address = province + city + district + street + streetNum + aoiName;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //获取定位时间
        Date date = new Date(amapLocation.getTime());
        df.format(date);

        if (!mSosStop && !isTry) {
            if (!isReqSosId && mSosId == 0) {
                isReqSosId = true;
                reqNewSos(latitude, longitude, address, locateMode);
            } else {
                if (mSosId != 0) {
                    reportSosLocation(latitude, longitude, address, locateMode);
                }
            }
        }
    }


    boolean isReqSosId = false;

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);

            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption.setOnceLocation(false);  //
            //mLocationOption.setOnceLocationLatest(true);

            mLocationOption.setInterval(5000);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
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
            case R.id.btn_cancel:
                reportSosStop();
                break;
        }
    }

}
