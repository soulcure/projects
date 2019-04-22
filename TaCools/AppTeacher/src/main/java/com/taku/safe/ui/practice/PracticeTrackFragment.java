package com.taku.safe.ui.practice;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.taku.safe.BasePermissionFragment;
import com.taku.safe.R;


/**
 * Created by colin on 2017/5/10.
 */
public class PracticeTrackFragment extends BasePermissionFragment {

    private MapView mMapView;
    private AMap mAMap;

    private TextView tv_time;
    private TextView tv_sign;
    private TextView tv_status;
    private TextView tv_location;


    private TextView tv_desc;
    private LinearLayout linear_pic;
    private EditText tv_follow;
    private Button btn_commit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice_track, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initMap(view, savedInstanceState);
        initView(view);

        double latitude = 22.564403;
        double longitude = 113.894129;
        drawMarker(latitude, longitude);
    }

    private void initView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_location = (TextView) view.findViewById(R.id.tv_location);

        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        linear_pic = (LinearLayout) view.findViewById(R.id.linear_pic);
        tv_follow = (EditText) view.findViewById(R.id.tv_follow);
        btn_commit = (Button) view.findViewById(R.id.btn_commit);
    }

    private void initMap(View view, Bundle savedInstanceState) {
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);


        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(16));
            setUpMap();
        }
    }


    private void drawMarker(double latitude, double longitude) {
        MarkerOptions markerOption = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point))
                .draggable(false);
        mAMap.addMarker(markerOption);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 16));

        mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {

            }
        });
    }


    /**
     * 设置一些amap的属性
     */

    private void setUpMap() {
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        //myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.img_location));
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
    }

}
