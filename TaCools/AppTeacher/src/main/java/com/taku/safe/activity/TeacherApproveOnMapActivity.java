
package com.taku.safe.activity;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.taku.safe.http.IGetListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespSignDetail;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.ListUtils;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * 圆形地理围栏
 *
 * @author hongming.wang
 * @since 3.2.0
 */
public class TeacherApproveOnMapActivity extends BasePermissionActivity implements
        LocationSource, AMapLocationListener, View.OnClickListener {

    private static final String TAG = "Amap";

    public static final String SIGN_ID = "sign_id";
    public static final String SIGN_TYPE = "sign_type";
    public static final String SIGN_STATUS = "sign_status";
    public static final String SIGN_NAME = "sign_name";

    private TextView tv_time;
    private TextView tv_status;
    private TextView tv_address;
    private TextView tv_desc;
    private HorizontalScrollView h_scrollview;
    private LinearLayout h_linear;

    private LinearLayout linear_status;
    private CheckBox cb_box;
    private TextView tv_approve_time;

    private String mApproveNote;
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

    private EditText ed_approve;
    private Button btn_commit;


    private UIHandler mHandler;
    private Context mContext;

    private int signType;
    private int signId;
    private int signStatus;

    private boolean isReset;//是否批注

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_approve_on_map);
        mContext = this;
        mHandler = new UIHandler(this);

        initMap(savedInstanceState);

        initTitle();
        initView();

        signType = getIntent().getIntExtra(SIGN_TYPE, 0);
        signId = getIntent().getIntExtra(SIGN_ID, 0);
        signStatus = getIntent().getIntExtra(SIGN_STATUS, SignCountActivity.NOSIGN);

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
                onBackPressed();
            }
        });

    }

    private void initView() {
        String name = getIntent().getStringExtra(SIGN_NAME);
        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(name);

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        h_scrollview = (HorizontalScrollView) findViewById(R.id.h_scrollview);
        h_linear = (LinearLayout) findViewById(R.id.h_linear);

        linear_status = (LinearLayout) findViewById(R.id.linear_status);
        cb_box = (CheckBox) findViewById(R.id.cb_box);
        tv_approve_time = (TextView) findViewById(R.id.tv_approve_time);

        ed_approve = (EditText) findViewById(R.id.ed_approve);
        btn_commit = (Button) findViewById(R.id.btn_commit);

        btn_commit.setOnClickListener(this);

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
            url = AppConfig.TEACHER_SIGN_DETAIL;
        } else {
            url = AppConfig.TEACHER_INTERNSHIP_SINGIN_DETAIL;
        }

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("signId", signId);


        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
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

                    mApproveNote = baseBean.getApproveNote();
                    if (!TextUtils.isEmpty(mApproveNote)) {
                        ed_approve.setText(mApproveNote);

                    }

                    //签到已经修改过了
                    if (baseBean.isChanged()) {
                        ed_approve.setEnabled(false);
                        btn_commit.setEnabled(false);
                        cb_box.setEnabled(false);
                        cb_box.setChecked(true);
                        linear_status.setVisibility(View.VISIBLE);
                        tv_approve_time.setVisibility(View.VISIBLE);

                        try {
                            String changeTime = TimeUtils.parseDateFormat(baseBean.getChangedTime(), TimeUtils.SECOND_DATE_FORMAT);
                            tv_approve_time.setText(String.format("修改时间: \n %s", changeTime));
                            tv_approve_time.setTextColor(ContextCompat.getColor(mContext, R.color.text_red));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        if (mTakuApp.isManager()) {
                            String signDate = baseBean.getSignDate();
                            try {
                                Calendar changeCal = TimeUtils.parseDate(signDate, TimeUtils.DEFAULT_DATE_FORMAT);

                                Calendar curCal = Calendar.getInstance();
                                curCal.set(Calendar.DAY_OF_YEAR, -5);
                                if (curCal.before(changeCal)) {
                                    linear_status.setVisibility(View.VISIBLE);
                                    tv_approve_time.setVisibility(View.GONE);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    List<String> list = baseBean.getImageList();
                    if (ListUtils.isEmpty(list)) {
                        h_scrollview.setVisibility(View.GONE);
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


    /**
     * 老师录入作息打卡签到跟进信息
     */
    private void handleRestSignDetail(int signType, int signId,
                                      final String approveNote, final int isNormal) {
        String url;
        if (signType == 0) {
            url = AppConfig.TEACHER_SIGN_APPROVE;
        } else {
            url = AppConfig.TEACHER_INTERNSHIP_SINGIN_APPROVE;
        }

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        ContentValues params = new ContentValues();
        params.put("signId", signId);
        params.put("approveNote", approveNote);
        params.put("setNormal", isNormal);

        btn_commit.setEnabled(false);
        OkHttpConnector.httpGet(header, url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean respBaseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (respBaseBean != null && respBaseBean.isSuccess()) {
                    if (isNormal == 1) {
                        isReset = true;
                    }
                    mApproveNote = approveNote;
                    Toast.makeText(mContext, "跟进签到批注信息成功", Toast.LENGTH_SHORT).show();
                } else {
                    btn_commit.setEnabled(true);
                    if (!TextUtils.isEmpty(respBaseBean.getMsg())) {
                        Toast.makeText(mContext, respBaseBean.getMsg(), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
        if (isReset) {
            setResult(Activity.RESULT_OK);
        }
        finish();
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
                String approve = ed_approve.getText().toString();
                if (TextUtils.isEmpty(approve)) {
                    Toast.makeText(mContext, "请输入签到跟进内容！", Toast.LENGTH_SHORT).show();
                    return;
                }

                int isNormal = 0;
                if (cb_box.isChecked()) {
                    isNormal = 1;
                }
                handleRestSignDetail(signType, signId, approve, isNormal);

                break;
        }
    }


    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<TeacherApproveOnMapActivity> mTarget;

        UIHandler(TeacherApproveOnMapActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            TeacherApproveOnMapActivity act = mTarget.get();
            switch (msg.what) {
                default:
                    break;
            }
        }
    }
}
