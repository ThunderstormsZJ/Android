package cn.spinsoft.wdq.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

import cn.spinsoft.wdq.ILocationAidlCallback;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;

/**
 * Created by hushujun on 15/11/18.
 */
public class LocationService extends Service implements AMapLocationListener {
    private static final String TAG = LocationService.class.getSimpleName();

    private LocationServiceBinder mServiceBinder = new LocationServiceBinder(this);

    //    private ArrayDeque<ILocationAidlCallback> mLocationCallbacks = null;
    private ILocationAidlCallback mLocationCallback = null;

    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate");
//        mLocationCallbacks = new ArrayDeque<>();
        try {
            startLocation();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        LogUtil.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, i, i1);
    }

    public void registerCallback(ILocationAidlCallback callback) throws RemoteException {
//        mLocationCallbacks.addLast(callback);
        mLocationCallback = callback;
    }

    private void initLocation() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getApplicationContext());

            mLocationOption = new AMapLocationClientOption();
            //初始时高进度,确保初次定位准确
            mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
            //是否需要返回地址
            mLocationOption.setNeedAddress(true);
            //是否只定位一次
            mLocationOption.setOnceLocation(false);
            //是否前置刷新WIFI
            mLocationOption.setWifiActiveScan(true);
            //是否允许模拟位置
            mLocationOption.setMockEnable(false);
            //定位时间间隔,ms
            mLocationOption.setInterval(Constants.Ints.LOCATION_INTERVAL_SHORT);
            mLocationClient.setLocationOption(mLocationOption);

            mLocationClient.setLocationListener(this);
        }
    }

    public void startLocation() throws RemoteException {
        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.startLocation();
        } else {
            initLocation();
            mLocationClient.startLocation();
        }
    }

    public void stopLocation() throws RemoteException {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stopLocation();
        }
    }

    public void changLocationModel(int model, long interval) throws RemoteException {
//        if (mLocationClient != null && mLocationOption != null) {
//            AMapLocationMode locationMode;
//            if (model == AMapLocationMode.Hight_Accuracy.getValue()) {
//                locationMode = AMapLocationMode.Hight_Accuracy;
//            } else if (model == AMapLocationMode.Device_Sensors.getValue()) {
//                locationMode = AMapLocationMode.Device_Sensors;
//            } else {
//                locationMode = AMapLocationMode.Battery_Saving;
//            }
//            mLocationOption.setInterval(interval);
//            mLocationOption.setLocationMode(locationMode);
//            mLocationClient.setLocationOption(mLocationOption);
//        }
    }

    public void unRegisterCallback(ILocationAidlCallback callback) throws RemoteException {
//        mLocationCallbacks.remove(callback);
        mLocationCallback = null;
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) {
                mLocationClient.stopLocation();
            }
            mLocationClient.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            LogUtil.e(TAG, "<<===== onLocationChanged:aMapLocation==null =====>>");
            return;
        }
        try {
           /* GeocodeSearch geocoderSearch = new GeocodeSearch(this);//传入context
            LatLonPoint latLonPoint = new LatLonPoint(39.9, 116.3);
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    if(i==0){
                        LogUtil.w(TAG,regeocodeResult.getRegeocodeAddress().getFormatAddress());
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }
            });
            geocoderSearch.getFromLocationAsyn(query);*/

            if (aMapLocation.getErrorCode() == 0) {
                LogUtil.w(TAG, "onLocationChanged==>address:" + aMapLocation.getAddress());
                if (mLocationCallback != null) {
                    mLocationCallback.locationGot(aMapLocation.getLongitude(),
                            aMapLocation.getLatitude(), aMapLocation.getCity(), aMapLocation.getAddress());
                }
            } else {
                if (mLocationCallback != null) {
                    mLocationCallback.locationFailed(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
