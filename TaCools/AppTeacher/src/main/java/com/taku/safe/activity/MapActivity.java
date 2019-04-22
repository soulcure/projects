
package com.taku.safe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.config.AppConfig;
import com.taku.safe.dialog.ChoiceDialog;
import com.taku.safe.entity.ChoiceDialogItem;
import com.taku.safe.http.IPostListener;
import com.taku.safe.http.OkHttpConnector;
import com.taku.safe.protocol.respond.RespBaseBean;
import com.taku.safe.protocol.respond.RespStudentInfo;
import com.taku.safe.protocol.respond.RespUserInfo;
import com.taku.safe.sos.SosInMapActivity;
import com.taku.safe.utils.CompressImage;
import com.taku.safe.utils.Const;
import com.taku.safe.utils.GsonUtil;
import com.taku.safe.utils.TimeUtils;
import com.taku.safe.views.CountTimeView;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import droidninja.filepicker.FilePickerBuilder;
//import droidninja.filepicker.FilePickerConst;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 圆形地理围栏
 *
 * @author hongming.wang
 * @since 3.2.0
 */
public class MapActivity extends BasePermissionActivity implements
        GeoFenceListener, LocationSource, AMapLocationListener, View.OnClickListener {
    private static final String TAG = "Amap";

    public static final String SIGN_INFO = "signInfo";
    public static final String SIGN_TYPE = "signType";


    public static final int TYPE_REST = 0;  //作息
    public static final int TYPE_PRACTICE = 1; //实习


    private static final int PIC_NUM = 4; //最多4张图片

    // 地理围栏的广播action
    private static final String GEOFENCE_BROADCAST_ACTION = "com.taku.geofence.round";

    private static final int HANDLER_GEOFENCE_SUCCESS = 0;
    private static final int HANDLER_GEOFENCE_FAIL = 1;
    private static final int HANDLER_GEOFENCE_RESULT = 2;

    private ArrayList<String> photoOriginPaths = new ArrayList<>();
    private ArrayList<String> photoCompressPaths = new ArrayList<>();

    private Object lock = new Object();

    private boolean isOutOfRange = false;
    private TextView tv_name;
    private TextView tv_address;
    private TextView tv_range;

    private EditText ed_desc;

    private LinearLayout h_linear;

    private Button btn_commit;

    private CountTimeView tv_count_time;

    private static final int FROM_CAMERA = 50;// 拍照
    private static final int REQUEST_IMAGE = 51;// 仿微信图片选择器
    private String mCurrentPhotoPath;

    private int signType;   //签到类型

    private double mLatitude;  //签到当前纬度
    private double mLongitude; //签到当前经度
    private String mAddress;   //签到当前地址
    private int mLocateMode;   //签到当前定位类型

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

    // 当前的坐标点集合，主要用于进行地图的可视区域的缩放
    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

    // 地理围栏客户端
    private GeoFenceClient fenceClient = null;


    // 记录已经添加成功的围栏
    private HashMap<String, GeoFence> fenceMap = new HashMap<>();
    private List<GeoFence> fenceList = new ArrayList<>();

    private UIHandler mHandler;
    private Context mContext;

    private RespStudentInfo signInfo;

    /**
     * 接收触发围栏后的广播,当添加围栏成功之后，会立即对所有围栏状态进行一次侦测，如果当前状态与用户设置的触发行为相符将会立即触发一次围栏广播；
     * 只有当触发围栏之后才会收到广播,对于同一触发行为只会发送一次广播不会重复发送，除非位置和围栏的关系再次发生了改变。
     */
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                String customId = bundle
                        .getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                //status标识的是当前的围栏状态，不是围栏行为
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                StringBuilder sb = new StringBuilder();
                switch (status) {
                    case GeoFence.STATUS_LOCFAIL:
                        sb.append("定位失败");
                        break;
                    case GeoFence.STATUS_IN:
                        sb.append("进入围栏 ");
                        break;
                    case GeoFence.STATUS_OUT:
                        sb.append("离开围栏 ");
                        break;
                    case GeoFence.STATUS_STAYED:
                        sb.append("停留在围栏内 ");
                        break;
                    default:
                        break;
                }
                if (status != GeoFence.STATUS_LOCFAIL) {
                    if (!TextUtils.isEmpty(customId)) {
                        sb.append(" customId: " + customId);
                    }
                    sb.append(" fenceId: " + fenceId);
                }
                String str = sb.toString();
                Message msg = Message.obtain();
                msg.obj = str;
                msg.what = HANDLER_GEOFENCE_RESULT;
                mHandler.sendMessage(msg);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mContext = this;
        mHandler = new UIHandler(this);

        signInfo = getIntent().getParcelableExtra(SIGN_INFO);
        signType = getIntent().getIntExtra(SIGN_TYPE, 0);

        initMap(savedInstanceState);
        initTitle();
        initView();

        processSignInfo(signInfo);
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


    private void initMap(Bundle savedInstanceState) {
        fenceClient = new GeoFenceClient(this);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);


        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(16));
            setUpMap();
        }

        IntentFilter fliter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        fliter.addAction(GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, fliter);

        /*
         * 创建pendingIntent
         */
        fenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        fenceClient.setGeoFenceListener(this);

        /*
         * 设置地理围栏的触发行为,默认为进入
         */
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN
                | GeoFenceClient.GEOFENCE_OUT);
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
        h_linear = (LinearLayout) findViewById(R.id.h_linear);
        tv_name = (TextView) findViewById(R.id.tv_name);
        RespUserInfo userInfo = mTakuApp.getUserInfo();
        if (userInfo != null) {
            tv_name.setText(userInfo.getName());
        }

        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_range = (TextView) findViewById(R.id.tv_range);
        ed_desc = (EditText) findViewById(R.id.ed_desc);
        tv_count_time = (CountTimeView) findViewById(R.id.tv_count_time);

        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);

        findViewById(R.id.img_send).setOnClickListener(this);
    }


    /**
     * 处理签到信息
     *
     * @param signInfo
     */
    private void processSignInfo(RespStudentInfo signInfo) {
        boolean isEnable = false;

        if (signInfo == null || signInfo.getRestSignInfo() == null) {
            Toast.makeText(mContext, "学生签到信息获取错误！", Toast.LENGTH_SHORT).show();
            return;
        }

        int sosId = signInfo.getActiveSosId();
        int needSign = signInfo.getRestSignInfo().getNeedSign();      //0 不需要签到 1 需要签到
        int signStatus = signInfo.getRestSignInfo().getSignStatus();  //0 未签到 1 已签到
        int signValid = signInfo.getRestSignInfo().getSignValid();    // 0 异常签到 1 有效签到 -1未签到

        if (sosId > 0) {
            Intent intent = new Intent(mContext, SosInMapActivity.class);
            intent.putExtra(SosInMapActivity.IS_TRY, false);
            intent.putExtra(SosInMapActivity.SOS_ID, sosId);
            startActivity(intent);
        }

        if (needSign == 1) { //今天需要签到（上课天）
            if (signStatus == 1) { //今天已经签到
                if (signValid == -1) { //未签到
                    isEnable = true;
                } else if (signValid == 0) { //异常签到
                    //已经正常，但位置异常，可再此正常签到
                    isEnable = false;
                } else if (signValid == 1) { //有效签到
                    //已经正常签到（提示今天已经正常签到）
                    isEnable = false;
                }

            } else {
                //今天还没有签到
                isEnable = true;
            }
        }

        if (isEnable) {
            String curTime = TimeUtils.getTime(System.currentTimeMillis(),
                    TimeUtils.DATE_FORMAT_HOUR);
            String startTime = signInfo.getRestSignInfo().getStartTime(); //有效开始时间
            String endTime = signInfo.getRestSignInfo().getEndTime(); //有效结束时间

            try {
                Calendar startDate = TimeUtils.parseDate(startTime, TimeUtils.DATE_FORMAT_HOUR);
                int startHour = startDate.get(Calendar.HOUR_OF_DAY);
                int startMin = startDate.get(Calendar.MINUTE);

                Calendar curDate = TimeUtils.parseDate(curTime, TimeUtils.DATE_FORMAT_HOUR);
                int curHour = curDate.get(Calendar.HOUR_OF_DAY);
                int curMin = curDate.get(Calendar.MINUTE);
                int curSecond = curDate.get(Calendar.SECOND);

                Calendar endDate = TimeUtils.parseDate(endTime, TimeUtils.DATE_FORMAT_HOUR);
                int endHour = endDate.get(Calendar.HOUR_OF_DAY);
                int endMin = endDate.get(Calendar.MINUTE);
                int endSecond = endDate.get(Calendar.SECOND);

                if (curTime.compareTo(startTime) >= 0
                        && curTime.compareTo(endTime) <= 0) {
                    //显示签到结束倒计时
                    //long time = ((endHour - curHour) * 60 + (endMin - curMin)) * 60 * 1000;
                    long timeSecond = (endHour - curHour) * 60 * 60     //小时->秒
                            + (endMin - curMin) * 60                    //分钟->秒
                            + (endSecond - curSecond);                  //秒->秒
                    tv_count_time.setCountTime(timeSecond * 1000, 0);            //秒->毫秒
                    btn_commit.setEnabled(true);
                } else if (curTime.compareTo(startTime) < 0) {
                    //显示 签到开始倒计时
                    //显示 签到开始倒计时
                    long time = ((startHour - curHour) * 60 + (startMin - curMin)) * 60 * 1000;
                    tv_count_time.setCountTime(time, 1);
                    btn_commit.setEnabled(false);
                } else if (curTime.compareTo(endTime) > 0) {  //签到时间已过
                    //超过 签到结束时间
                    btn_commit.setEnabled(false);
                    btn_commit.setTextColor(ContextCompat.getColor(this, R.color.color_red));
                    btn_commit.setText(R.string.sign_out_time);
                    tv_count_time.setVisibility(View.GONE);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            btn_commit.setText("无需签到");
            btn_commit.setEnabled(false);
            tv_count_time.setVisibility(View.GONE);
        }
    }


    public Intent dispatchTakePictureIntent(Context context) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File newFile = createImageFile();
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", newFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
            }
            return takePictureIntent;
        }
        return null;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                Log.e("TAG", "Throwing Errors....");
                throw new IOException();
            }
        }

        File image = new File(storageDir, imageFileName);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * 相机拍照
     */
    public void fromCamera() {
        try {
            Intent intent = dispatchTakePictureIntent(this);
            if (intent != null)
                startActivityForResult(intent, FROM_CAMERA);
            else
                Toast.makeText(this, "没有相机的应用程序", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pickerImage() {
        /*FilePickerBuilder.getInstance().setMaxCount(PIC_NUM)
                .setSelectedFiles(photoOriginPaths)
                //.setActivityTheme(R.style.FilePickerTheme)
                .enableCameraSupport(true)
                .showFolderView(false)
                .pickPhoto(this);*/

        MultiImageSelector.create()
                .showCamera(false) // show camera or not. true by default
                .count(PIC_NUM) // max select image size, 9 by default. used width #.multi()
                .multi() // multi mode, default mode;
                .origin(photoOriginPaths) // original select data set, used width #.multi()
                .start(this, REQUEST_IMAGE);
    }


    private void showImageDialog() {
        final ArrayList<ChoiceDialogItem> list = new ArrayList<>();

        list.add(new ChoiceDialogItem("相机", 0));
        list.add(new ChoiceDialogItem("相册", 1));

        new ChoiceDialog.Builder(this)
                .setList(list)
                .callBack(new ChoiceDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 0) {
                            fromCamera();
                        } else {
                            pickerImage();
                        }

                    }
                })
                .builder()
                .show();
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
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


    private void drawRoundFence(double latitude, double longitude,
                                float fenceRadius, String id) {
        MarkerOptions markerOption = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location))
                .draggable(false);
        mAMap.addMarker(markerOption);

        DPoint centerPoint = new DPoint(latitude, longitude);
        fenceClient.addGeoFence(centerPoint, fenceRadius, id);
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
        try {
            unregisterReceiver(mGeoFenceReceiver);
        } catch (Throwable e) {
        }

        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }


    private void drawFence(GeoFence fence) {
        switch (fence.getType()) {
            case GeoFence.TYPE_ROUND:
            case GeoFence.TYPE_AMAPPOI:
                drawCircle(fence);
                break;
            case GeoFence.TYPE_POLYGON:
            case GeoFence.TYPE_DISTRICT:
                drawPolygon(fence);
                break;
            default:
                break;
        }
    }

    private void drawCircle(GeoFence fence) {
        LatLng center = new LatLng(fence.getCenter().getLatitude(),
                fence.getCenter().getLongitude());
        // 绘制一个圆形
        mAMap.addCircle(new CircleOptions().center(center)
                .radius(fence.getRadius()).strokeColor(Const.STROKE_COLOR)
                .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
        boundsBuilder.include(center);
    }

    private void drawPolygon(GeoFence fence) {
        final List<List<DPoint>> pointList = fence.getPointList();
        if (null == pointList || pointList.isEmpty()) {
            return;
        }
        for (List<DPoint> subList : pointList) {
            List<LatLng> lst = new ArrayList<>();

            PolygonOptions polygonOption = new PolygonOptions();
            for (DPoint point : subList) {
                lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
                boundsBuilder.include(
                        new LatLng(point.getLatitude(), point.getLongitude()));
            }
            polygonOption.addAll(lst);

            polygonOption.strokeColor(Const.STROKE_COLOR)
                    .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH);
            mAMap.addPolygon(polygonOption);
        }
    }


    private void drawFence2Map() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (null == fenceList || fenceList.isEmpty()) {
                            return;
                        }
                        for (GeoFence fence : fenceList) {
                            if (fenceMap.containsKey(fence.getFenceId())) {
                                continue;
                            }
                            drawFence(fence);
                            fenceMap.put(fence.getFenceId(), fence);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public void onGeoFenceCreateFinished(final List<GeoFence> geoFenceList,
                                         int errorCode, String customId) {
        Message msg = mHandler.obtainMessage();
        if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
            fenceList = geoFenceList;
            msg.obj = customId;
            msg.what = HANDLER_GEOFENCE_SUCCESS;
        } else {
            msg.arg1 = errorCode;
            msg.what = HANDLER_GEOFENCE_FAIL;
        }
        mHandler.sendMessage(msg);
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

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(mContext, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void locationInfo(AMapLocation amapLocation) {
        double latitude;
        double longitude;
        float fenceRadius;

        if (signType == TYPE_REST) {
            latitude = signInfo.getRestSignInfo().getLatitude();
            longitude = signInfo.getRestSignInfo().getLongitude();
        } else {
            latitude = signInfo.getInternshipInfo().getLatitude();
            longitude = signInfo.getInternshipInfo().getLongitude();
        }

        LatLng centerLatLng = new LatLng(latitude, longitude);

        int locationType = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        if (locationType == AMapLocation.LOCATION_TYPE_CELL) {//基站定位结果,定位精度在500米-5000米之间
            mLocateMode = 1;
            if (signType == TYPE_REST) {
                fenceRadius = (float) signInfo.getRestSignInfo().getValidDistanceBase();
            } else {
                fenceRadius = (float) signInfo.getInternshipInfo().getValidDistanceBase();
            }


        } else {
            mLocateMode = 0;
            if (signType == TYPE_REST) {
                fenceRadius = (float) signInfo.getRestSignInfo().getValidDistanceGps();
            } else {
                fenceRadius = (float) signInfo.getInternshipInfo().getValidDistanceGps();
            }
        }

        drawRoundFence(latitude, longitude, fenceRadius, "0");  //绘制签到围栏

        mLatitude = amapLocation.getLatitude();//获取纬度
        mLongitude = amapLocation.getLongitude();//获取经度

        LatLng curLatLng = new LatLng(mLatitude, mLongitude);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));

        String province = amapLocation.getProvince();//省信息
        String city = amapLocation.getCity();//城市信息
        String district = amapLocation.getDistrict();//城区信息
        String street = amapLocation.getStreet();//街道信息
        String streetNum = amapLocation.getStreetNum();//街道门牌号信息
        String aoiName = amapLocation.getAoiName();//获取当前定位点的AOI信息

        mAddress = province + city + district + street + streetNum + aoiName;
        //mAddress = amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。

        tv_address.setText(mAddress);

        float distance = AMapUtils.calculateLineDistance(centerLatLng, curLatLng);//距离判断
        if (distance <= fenceRadius) {
            isOutOfRange = false;
            tv_range.setText("位置正常");
            tv_range.setTextColor(ContextCompat.getColor(this, R.color.color_blue));
            //可以签到//// TODO: 2017/7/12
        } else {
            isOutOfRange = true;
            tv_range.setText("位置异常");
            tv_range.setTextColor(ContextCompat.getColor(this, R.color.color_red));
            //不可以签到 //// TODO: 2017/7/12
        }


    }


    private void reqStudentSign() {
        String url;
        if (signType == TYPE_REST) {
            url = AppConfig.SIGN_IN;
        } else {
            url = AppConfig.PRACTICE_SIGN_IN;
        }

        String note = ed_desc.getText().toString();

        ContentValues header = new ContentValues();
        header.put("token", mTakuApp.getToken());

        Map<String, Object> params = new HashMap<>();
        params.put("latitude", mLatitude);
        params.put("longitude", mLongitude);
        params.put("address", mAddress);
        params.put("mapType", 0);  //0 高德
        params.put("locateMode", mLocateMode);
        params.put("note", note);

        for (int i = 0; i < photoCompressPaths.size(); i++) {
            File file = new File(photoCompressPaths.get(i));
            if (file.exists()) {
                params.put("image" + (i + 1), file);
            }
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在签到，请稍后...");
        dialog.show();
        OkHttpConnector.httpPostMultipart(url, header, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, "签到成功", Toast.LENGTH_SHORT).show();
                    if (signType == TYPE_REST) {
                        //跳转到足迹页面
                        Intent intent = new Intent(mContext, SignTrackActivity.class);
                        startActivity(intent);
                    } else {
                        setResult(Activity.RESULT_OK);
                    }
                    finish();
                } else {
                    if (baseBean != null && baseBean.getMsg() != null) {
                        Toast.makeText(mContext, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    ArrayList<String> addPaths = new ArrayList<>();
                    for (String item : paths) {
                        if (!photoOriginPaths.contains(item)) {
                            photoOriginPaths.add(item);
                            addPaths.add(item);
                        }
                    }
                    new CompressAsyncTask().execute(addPaths.toArray(new String[addPaths.size()]));
                }
                break;*/
            case FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK /*&& data != null*/) {
                    photoOriginPaths.add(mCurrentPhotoPath);
                    new CompressAsyncTask().execute(new String[]{mCurrentPhotoPath});
                }
                break;
            case REQUEST_IMAGE:
                if (resultCode == Activity.RESULT_OK /*&& data != null*/) {
                    // Get the result list of select image paths
                    ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    ArrayList<String> addPaths = new ArrayList<>();
                    for (String item : paths) {
                        if (!photoOriginPaths.contains(item)) {
                            photoOriginPaths.add(item);
                            addPaths.add(item);
                        }
                    }
                    new CompressAsyncTask().execute(addPaths.toArray(new String[addPaths.size()]));
                }
                break;
        }

    }

    private void checkSignRange() {
        boolean isPhoto = h_linear.getChildCount() > 1;
        String note = ed_desc.getText().toString();

        if (isOutOfRange) {
            if (isPhoto && !TextUtils.isEmpty(note)) {
                reqStudentSign();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("异常签到提醒!")
                        .setMessage("不在签到范围,需要填写备注信息并拍照才能进行签到")
                        .setPositiveButton(R.string.btn_confirm, null)
                        //.setNegativeButton(R.string.btn_cancel, null)
                        .show();
            }

        } else {
            reqStudentSign();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_send:
                if (photoOriginPaths.size() >= PIC_NUM) {
                    Toast.makeText(this, String.format(getString(R.string.pic_num), PIC_NUM), Toast.LENGTH_SHORT).show();
                } else {
                    //showImageDialog();  //只能拍照
                    fromCamera();
                }
                break;
            case R.id.btn_commit:
                checkSignRange();
                break;
        }
    }

    /**
     * 压缩图片
     */
    private class CompressAsyncTask extends AsyncTask<String[], Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String[]... params) {

            ArrayList<String> res = new ArrayList<>();
            for (String item : params[0]) {
                res.add(CompressImage.compressImageEditDate(mContext, item));
            }

            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<String> res) {
            for (String item : res) {
                if (!photoCompressPaths.contains(item)) {
                    photoCompressPaths.add(item);
                    Bitmap bitmap = BitmapFactory.decodeFile(item);

                    /*int height = mContext.getResources().getDimensionPixelOffset(R.dimen.h_scrollview_height);
                    int width = (height * bitmap.getWidth()) / bitmap.getHeight();*/

                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View view = inflater.inflate(R.layout.item_select_pic, null);

                    /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                    view.setLayoutParams(params);*/

                    ImageView imgContent = (ImageView) view.findViewById(R.id.img_content);
                    imgContent.setImageBitmap(bitmap);

                    ImageView imgClose = (ImageView) view.findViewById(R.id.img_close);
                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = h_linear.indexOfChild(view);
                            if (index >= 0) {
                                h_linear.removeViewAt(index);

                                int size = photoOriginPaths.size();
                                int position = size - 1 - index;

                                photoOriginPaths.remove(position);
                                photoCompressPaths.remove(position);
                            }
                        }
                    });

                    h_linear.addView(view, 0);

                }
            }
        }
    }

    /**
     * service handler
     */
    public static class UIHandler extends Handler {
        private final WeakReference<MapActivity> mTarget;

        UIHandler(MapActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            MapActivity act = mTarget.get();
            switch (msg.what) {
                case HANDLER_GEOFENCE_SUCCESS:
                    StringBuilder sb = new StringBuilder();
                    sb.append("添加围栏成功");
                    String customId = (String) msg.obj;
                    if (!TextUtils.isEmpty(customId)) {
                        sb.append("customId: ").append(customId);
                    }
                    /*Toast.makeText(act.mContext, sb.toString(),
                            Toast.LENGTH_SHORT).show();*/
                    Log.d(TAG, sb.toString());
                    act.drawFence2Map();
                    break;
                case HANDLER_GEOFENCE_FAIL:
                    int errorCode = msg.arg1;

                    Log.e(TAG, "添加围栏失败 " + errorCode);
                    /*Toast.makeText(act.mContext,
                            "添加围栏失败 " + errorCode, Toast.LENGTH_SHORT).show();*/
                    break;
                case HANDLER_GEOFENCE_RESULT:
                    String statusStr = (String) msg.obj;
                    Log.v(TAG, statusStr);
                    /*Toast.makeText(act.mContext, statusStr, Toast.LENGTH_SHORT).show();*/
                    break;
                default:
                    break;
            }
        }
    }
}
