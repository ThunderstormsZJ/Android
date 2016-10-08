package cn.spinsoft.wdq.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

import cn.spinsoft.wdq.ILocationAidlCallback;
import cn.spinsoft.wdq.ILocationAidlService;
import cn.spinsoft.wdq.home.MainParser;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.utils.LogUtil;

/**
 * Created by zhoujun on 15/11/27.
 */
public class LocationOnMain {
    private static final String TAG = LocationOnMain.class.getSimpleName();

    private double mLongitude;
    private double mLatitude;
    private String mCity;
    private String mAddress;

    private UserLogin mUserLogin;

    private ILocationAidlService mLocationService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocationService = ILocationAidlService.Stub.asInterface(service);
            try {
                mLocationService.registerCallback(locationCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationService = null;
        }
    };

    private ILocationAidlCallback.Stub locationCallback = new ILocationAidlCallback.Stub() {

        @Override
        public void locationGot(double longitude, double latitude, String city, String address) throws RemoteException {
            LogUtil.w(TAG, "locationGot:经度=" + longitude + ",纬度=" + latitude + ",城市=" + city);
            mLongitude = longitude;
            mLatitude = latitude;
            mCity = city;
            mAddress = address;
            new AsyncUpdateLocation().execute(mLongitude, mLatitude);
        }

        @Override
        public void locationFailed(int errCode, String errMsg) throws RemoteException {
            LogUtil.w(TAG, "locationFailed:errCode=" + errCode + ",errMsg=" + errMsg);
        }
    };

    private LocationOnMain() {

    }

    private static final class MAIN_HOLDER {
        private static final LocationOnMain HOLDER = new LocationOnMain();
    }

    public static LocationOnMain getInstance() {
        return MAIN_HOLDER.HOLDER;
    }

    public void changLocationModel(int model, long interval) {
        try {
            mLocationService.changLocationModel(model, interval);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopLocation() {
        try {
            mLocationService.stopLocation();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startLocation() {
        try {
            mLocationService.startLocation();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public double[] getLocation() {
        return new double[]{mLongitude, mLatitude};
    }

    public String getCity() {
        return mCity;
    }

    public String getAddress() {
        return mAddress;
    }

    public ServiceConnection getServiceConnection() {
        return mServiceConnection;
    }

    public void unRegisterCallback() {
        try {
            mLocationService.unRegisterCallback(locationCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setUserLogin(UserLogin userLogin) {
        mUserLogin = userLogin;
    }

    class AsyncUpdateLocation extends AsyncTask<Double, Integer, String> {

        @Override
        protected String doInBackground(Double... params) {
            if (mUserLogin != null) {
                MainParser.reportLocation(mUserLogin.getUserId(), params[0], params[1]);
            } else {
                LogUtil.e(TAG, "用户未登录,不能上报位置信息");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
