package cn.spinsoft.wdq.service;

import android.os.RemoteException;

import cn.spinsoft.wdq.ILocationAidlCallback;
import cn.spinsoft.wdq.ILocationAidlService;

/**
 * Created by hushujun on 15/11/19.
 */
public class LocationServiceBinder extends ILocationAidlService.Stub {
    private static final String TAG = LocationServiceBinder.class.getSimpleName();

    private LocationService mService;

    public LocationServiceBinder(LocationService mService) {
        this.mService = mService;
    }

    @Override
    public void registerCallback(ILocationAidlCallback callback) throws RemoteException {
        mService.registerCallback(callback);
    }

    @Override
    public void startLocation() throws RemoteException {
        mService.startLocation();
    }

    @Override
    public void stopLocation() throws RemoteException {
        mService.stopLocation();
    }

    @Override
    public void changLocationModel(int model, long interval) throws RemoteException {
        mService.changLocationModel(model, interval);
    }

    @Override
    public void unRegisterCallback(ILocationAidlCallback callback) throws RemoteException {
        mService.unRegisterCallback(callback);
    }
}
