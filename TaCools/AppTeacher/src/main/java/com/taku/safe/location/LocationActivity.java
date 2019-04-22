package com.taku.safe.location;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.utils.ScreenUtils;


import java.util.ArrayList;
import java.util.List;


public class LocationActivity extends BasePermissionActivity implements LocationSource,
        AMapLocationListener, AMap.OnCameraChangeListener,
        OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener {
    // UI
    private AMap mAMap;
    private MapView mMapView;

    private AMapLocationClient mLocationClient;
    private OnLocationChangedListener mListener;

    private GeocodeSearch geocoderSearch;// 坐标转物理地址
    private AMapLocation amapLocation;

    // Logic
    private Marker marker;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    private AutoCompleteTextView et_search;
    private ImageView img_cancel;

    private String city = "";
    private String newText;

    private boolean isFirstInput = true;
    private boolean isSearchSelect = false;

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
        setContentView(R.layout.activity_location);

        initTitle();
        initView();

        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new SearchAdapter(mContext);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layout.getOrientation()));

        recyclerView.setAdapter(adapter);
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

                et_search.setText("");
                img_cancel.setVisibility(View.GONE);

            }
        });
    }

    /**
     * 设置amap属性
     */
    private void setUpMap() {
        mAMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
        //mAMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器

        mAMap.moveCamera(CameraUpdateFactory.zoomTo(16f));
        mAMap.setLocationSource(this);// 设置定位监听

        mAMap.getUiSettings().setScaleControlsEnabled(true);
        mAMap.getUiSettings().setZoomControlsEnabled(true);
        mAMap.getUiSettings().setZoomGesturesEnabled(true);
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.getUiSettings().setAllGesturesEnabled(true);


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


        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        activate(mListener);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 定位成功后回调函数
     */
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

    private void setMarket(LatLng latLng, String title) {

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);

        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

        markerOption.title(title);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_location));

        marker = mAMap.addMarker(markerOption);

        //获取屏幕宽高
        int width = ScreenUtils.getWidthPixels(this) / 2;
        int height = ((ScreenUtils.getHeightPixels(this)) / 2) - 80;

        //设置像素坐标
        marker.setPositionByPixels(width, height);
        marker.showInfoWindow();
    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this.mContext);

            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mLocationClient.setLocationOption(locationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
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
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                if (marker != null && !isSearchSelect) {
                    marker.setTitle(result.getRegeocodeAddress()
                            .getFormatAddress());
                    marker.showInfoWindow();
                }

                List<PoiItem> list = result.getRegeocodeAddress().getPois();
                adapter.setList(list);

                isSearchSelect = false;
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }
        }
    }


    /**
     * PoiSearched结果回调，为自定义模糊查询
     *
     * @param poiResult
     * @param i
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        final ArrayList<PoiItem> list = poiResult.getPois();
        adapter.setList(list);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
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
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

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
                marker.setTitle(result.getName());
                marker.setPosition(latLng);
                marker.showInfoWindow();
            }
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            hideSoftKey(et_search);
        }

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
            marker.setTitle(poiItem.getTitle());
            LatLng curLatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            marker.setPosition(curLatlng);
            marker.showInfoWindow();
        }
    }
}
