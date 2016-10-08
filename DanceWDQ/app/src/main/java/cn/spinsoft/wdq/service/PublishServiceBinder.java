package cn.spinsoft.wdq.service;

import android.os.Bundle;
import android.os.RemoteException;

import cn.spinsoft.wdq.IPublishAidlCallback;
import cn.spinsoft.wdq.IPublishAidlService;

/**
 * Created by hushujun on 15/12/21.
 */
public class PublishServiceBinder extends IPublishAidlService.Stub {
    private static final String TAG = PublishServiceBinder.class.getSimpleName();

    private PublishService mService;

    public PublishServiceBinder(PublishService mService) {
        this.mService = mService;
    }

    @Override
    public void registerCallback(IPublishAidlCallback callback) throws RemoteException {
        mService.registerCallback(callback);
    }

    @Override
    public void prepareToPublish(Bundle bundle) throws RemoteException {
        mService.prepareToPublish(bundle);
    }

    @Override
    public void unRegisterCallback(IPublishAidlCallback callback) throws RemoteException {
        mService.unRegisterCallback(callback);
    }
}
