package com.chen.fy.patshow.home.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.util.ShowUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * 地图
 */
public class MapActivity extends AppCompatActivity {

    /// 初始坐标，象山景区
    private final LatLng rawLatLng = new LatLng(25.267571, 110.296565);
    /// 初始缩放大小
    private final int rawScaleSize = 17;

    /// 地图View
    private MapView mMapView;
    /// 地图控制器
    private AMap mMap;

    /// 定位
    private AMapLocationClient mLocationClient;
    /// 当前位置距离香山景区的距离
    private TextView tvDistance;

    /// 是否跳转高德导航底部弹窗
    private BottomSheetBehavior sheetBehavior;
    /// 蒙层
    private View mantleView;

    public static void start(Context context) {
        final Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShowUtils.changeStatusBarTextImgColor(this, true);
        setContentView(R.layout.map_layout);

        bindView(savedInstanceState);
        showMap();
        startLocation();
    }

    private void bindView(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.map);
        // 创建地图
        mMapView.onCreate(savedInstanceState);

        initMap();
        initBottomSheet();
        setListener();
    }

    private void initMap() {
        mMap = mMapView.getMap();
        initUiSettings();
    }

    /// 控件交互,手势交互
    private void initUiSettings() {
        //实现控件交互,手势交互等
        UiSettings settings = mMap.getUiSettings();

        //1 定位按钮
        settings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
        mMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置

        //2 手势设置
        settings.setRotateGesturesEnabled(false);   //旋转手势关闭
        settings.setTiltGesturesEnabled(false);     //倾斜手势关闭
    }

    private void initBottomSheet() {
        // MantleView
        mantleView = findViewById(R.id.mantle_map);
        // bottom sheet
        NestedScrollView sheet = findViewById(R.id.nsv_map_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheet);
        showBottomSheet(false);
    }

    private void setListener() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        // MantleView
        mantleView.setOnClickListener(v -> {
            showBottomSheet(false);
        });
        findViewById(R.id.tv_open_gd).setOnClickListener(v -> jumGD());
        findViewById(R.id.tv_cancel_map).setOnClickListener(v -> {
            showBottomSheet(false);
        });
    }

    /// 显示地图
    private void showMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rawLatLng, rawScaleSize));
        mMap.showIndoorMap(true);
    }

    /// 开始定位
    private void startLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(aMapLocation -> {
            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            // 获取当前位置距离象山景区的距离
            float distance_km = AMapUtils.calculateLineDistance(rawLatLng, latLng) / (1000 * 1000);
            String distance_str =  String.format("%.2f",distance_km);
            // 绘制象山景区Mark
            drawMarkers("距你"+distance_str+"km");
        });
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (mLocationClient != null) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

    }

    /// 绘制象山景区Marker
    public void drawMarkers(String distance) {
        ///象山景区Marker
        View view = getLayoutInflater().inflate(R.layout.map_customize_layout, null);
        tvDistance = view.findViewById(R.id.tv_distance);
        tvDistance.setText(distance);
        mMap.addMarker(new MarkerOptions().position(rawLatLng)
                .icon(BitmapDescriptorFactory.fromView(view)).visible(true));
        /// location circle
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.location_circle);
        mMap.addMarker(new MarkerOptions().position(rawLatLng)
                .icon(BitmapDescriptorFactory.fromView(imageView)).visible(true));

        mMap.setOnMarkerClickListener(marker -> {
            showBottomSheet(true);
            return true;
        });
    }

    /**
     * 跳转高德导航
     */
    private void jumGD() {
        // 终点的显示名称 必要参数
        String gbNavName = "象山景区";
        // dlat= 和 dlon= 后不写经纬度默认起始位置为当前设备所在定位位置
        Uri gdUri = Uri.parse("amapuri://route/plan/?dlat=" + rawLatLng.latitude
                + "&dlon=" + rawLatLng.longitude + "&dname=" + gbNavName + "&dev=0&t=2");
        Intent gdNav = new Intent("android.intent.action.VIEW", gdUri);
        gdNav.addCategory("android.intent.category.DEFAULT");
        startActivity(gdNav);
    }

    private void showBottomSheet(boolean isShow) {
        if (isShow) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mantleView.setVisibility(View.VISIBLE);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mantleView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMapView.onSaveInstanceState(outState);
    }
}
