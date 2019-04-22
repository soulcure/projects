
package com.taku.safe.location;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.utils.ScreenUtils;
import com.taku.safe.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 圆形地理围栏
 *
 * @author hongming.wang
 * @since 3.2.0
 */
public class SetActionActivity extends BasePermissionActivity implements LocationSource,
        AMapLocationListener, AMap.OnCameraChangeListener, PoiSearch.OnPoiSearchListener,
        GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener {

    private static final String TAG = "Amap";

    private MapView mMapView;
    private AMap mAMap;

    private AMapLocationClient mLocationClient;
    private OnLocationChangedListener mListener;

    //你编码对象
    private GeocodeSearch geocoderSearch;
    private AMapLocation amapLocation;

    private Marker marker;             //定位位置显示

    private AutoCompleteTextView et_search;
    private ImageView img_cancel;

    private TextView tv_address;
    private TextView tv_date;
    private TextView tv_time;

    private String city = "";
    private String newText;


    private boolean isFirstInput = true;
    private boolean isSearchSelect = false;

    Calendar calendar = Calendar.getInstance();

    Inputtips.InputtipsListener inputTipsListener = new Inputtips.InputtipsListener() {
        @Override
        public void onGetInputtips(List<Tip> list, int rCode) {
            if (rCode == 1000) {// 正确返回

                List<SearchTips> dataList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    SearchTips data = new SearchTips(list.get(i));
                    dataList.add(data);
                }

                LocationAdapter aAdapter = new LocationAdapter(mContext, R.layout.item_gaode_location_autotext, dataList);
                aAdapter.setKeyword(newText);

                et_search.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
                if (isFirstInput) {
                    isFirstInput = false;
                    et_search.showDropDown();
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_set);
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
        deactivate();
        MobclickAgent.onPause(this);
    }


    private void initMap(Bundle savedInstanceState) {

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);


        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(16f));
            setUpMap();
        }
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("设置活动");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initView() {
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);

        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);

        img_cancel = (ImageView) findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                img_cancel.setVisibility(View.GONE);
            }
        });

        et_search = (AutoCompleteTextView) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newText = s.toString().trim();
                if (newText.length() > 0) {
                    InputtipsQuery inputQuery = new InputtipsQuery(newText, city);
                    Inputtips inputTips = new Inputtips(mContext, inputQuery);
                    inputQuery.setCityLimit(true);
                    inputTips.setInputtipsListener(inputTipsListener);
                    inputTips.requestInputtipsAsyn();

                    img_cancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSearchSelect = true;
                SearchTips selected = (SearchTips) parent.getItemAtPosition(position);
                searchPoi(selected);

                tv_address.setText(selected.getAddress());

                et_search.setText("");
                img_cancel.setVisibility(View.GONE);

            }
        });
    }


    private void hideSoftKey(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void moveMarker(PoiItem poiItem) {
        if (marker != null) {
            //marker.setTitle(poiItem.getTitle());
            LatLng curLatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            marker.setPosition(curLatlng);
            marker.showInfoWindow();
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setOnCameraChangeListener(this);

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
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        this.amapLocation = amapLocation;
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                city = amapLocation.getCity();

                LatLng la = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                setMarket(la, amapLocation.getAddress());

                mLocationClient.stopLocation();

            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);

            // 只是为了获取当前位置，所以设置为单次定位
            locationOption.setOnceLocation(false);  //
            //locationOption.setOnceLocationLatest(true);

            locationOption.setInterval(5000);
            // 设置定位参数
            mLocationClient.setLocationOption(locationOption);
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }


    private void setMarket(LatLng latLng, String title) {
        if (marker != null) {
            marker.remove();
        }

        MarkerOptions markOptions = new MarkerOptions();
        markOptions.draggable(true);//设置Marker可拖动
        markOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location))).anchor(0.5f, 0.7f);
        //设置一个角标
        marker = mAMap.addMarker(markOptions);
        //设置marker在屏幕的像素坐标
        marker.setPosition(latLng);
        //marker.setTitle(title);

        //获取屏幕宽高
        int width = ScreenUtils.getWidthPixels(this) / 2;
        int height = ((ScreenUtils.getHeightPixels(this)) / 2) - 80;

        //设置像素坐标
        marker.setPositionByPixels(width, height);
        if (!TextUtils.isEmpty(title)) {
            marker.showInfoWindow();
        }
        mMapView.invalidate();
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if (marker != null) {
            marker.setPosition(cameraPosition.target);
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLonPoint latLonPoint = new LatLonPoint(
                cameraPosition.target.latitude, cameraPosition.target.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
        //searchPoi(latLonPoint);
    }


    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                if (marker != null) {
                    //marker.setTitle(result.getRegeocodeAddress().getFormatAddress());
                    marker.showInfoWindow();
                }

                tv_address.setText(result.getRegeocodeAddress().getFormatAddress());

                /*List<PoiItem> list = result.getRegeocodeAddress().getPois();
                adapter.setList(list);

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);*/

            }
        }
    }

    /**
     * POI查询
     *
     * @param result
     */
    private void searchPoi(SearchTips result) {
        LatLonPoint point = result.getPoint();
        if (point != null) {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
            if (marker != null) {
                //marker.setTitle(result.getName());
                marker.setPosition(latLng);
                marker.showInfoWindow();
            }
            hideSoftKey(et_search);
        }

    }


    /**
     * PoiSearched结果回调，为自定义模糊查询 (未使用)
     *
     * @param poiResult
     * @param i
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        final ArrayList<PoiItem> list = poiResult.getPois();
        /*adapter.setList(list);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    /**
     * 搜索附近 POI查询 ,为自定义模糊查询 (未使用)
     *
     * @param latLonPoint
     */
    private void searchPoi(LatLonPoint latLonPoint) {
        //progressBar.setVisibility(View.VISIBLE);
        //recyclerView.setVisibility(View.GONE);

        String code = amapLocation.getCityCode();
        PoiSearch.Query query = new PoiSearch.Query("", "商务住宅|道路附属设施|地名地址信息|公共设施|生活服务|餐饮服务|购物服务", code);

        // keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
        //共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）

        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查第一页
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 200));//设置周边搜索的中心点以及区域

        poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器

        poiSearch.searchPOIAsyn();//开始搜索

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_date:
                datePickerDialog();
                break;
            case R.id.tv_time:
                timePickerDialog();
                break;
        }

    }


    private void datePickerDialog() {
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_YEAR));
        long minTime = calendar.getTime().getTime();

        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        long maxTime = calendar.getTime().getTime();


        TimePickerDialog dialog = new TimePickerDialog.Builder()
                .setType(Type.YEAR_MONTH_DAY)
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        String birthday = TimeUtils.getDate(millseconds);
                        tv_date.setText(birthday);
                    }
                })
                //.setCyclic(false)
                .setWheelItemTextSize(14)
                .setTitleStringId("请选择日期")
                .setMinMillseconds(minTime)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(maxTime)
                .setThemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setWheelItemTextNormalColor(ContextCompat.getColor(this, R.color.color_gray))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(this, R.color.color_blue))
                .build();
        dialog.show(getSupportFragmentManager(), "year_month_day");

    }


    private void timePickerDialog() {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long minTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long maxTime = calendar.getTimeInMillis();

        TimePickerDialog dialog = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        String birthday = TimeUtils.getTime(millseconds, TimeUtils.DATE_FORMAT_HOUR);

                        tv_time.setText(birthday);
                    }
                })
                .setWheelItemTextSize(14)
                .setTitleStringId("请选择时间")
                .setHourText("")
                .setMinuteText("")
                //.setCyclic(false)
                .setMinMillseconds(minTime)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(maxTime)
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.HOURS_MINS)
                .setThemeColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setWheelItemTextNormalColor(ContextCompat.getColor(this, R.color.color_gray))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(this, R.color.color_blue))
                .build();
        dialog.show(getSupportFragmentManager(), "hour_minute");
    }

}
