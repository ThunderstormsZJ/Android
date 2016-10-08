package cn.spinsoft.wdq.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;

import cn.spinsoft.wdq.IPublishAidlCallback;
import cn.spinsoft.wdq.IPublishAidlService;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;

/**
 * Created by hushujun on 15/12/21.
 */
public class PublishOnMain {
    private static final String TAG = PublishOnMain.class.getSimpleName();

    private IPublishAidlService mPublishService = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPublishService = IPublishAidlService.Stub.asInterface(service);
            try {
                mPublishService.registerCallback(publishCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPublishService = null;
        }
    };

    private IPublishAidlCallback publishCallback = new IPublishAidlCallback.Stub() {

        @Override
        public void srcUpLoadResult(boolean success, String srcPath, String srcUrl) throws RemoteException {
            LogUtil.d(TAG, "srcUpLoadResult:" + success + ",srcPath=" + srcPath + ",srcUrl=" + srcUrl);
        }

        @Override
        public void publishResult(int code, String message) throws RemoteException {
            LogUtil.e(TAG, "publishResult:code=" + code + ",message=" + message);
        }

        @Override
        public void savePublishUnComplete(Bundle bundle) throws RemoteException {
            LogUtil.i(TAG, "savePublishUnComplete");
//            if (bundle != null) {
//                Parcelable parcelable = bundle.getParcelable(Constants.Strings.PUBLISH_INFO);
//            }
        }
    };

    private PublishOnMain() {

    }

    private static final class MAIN_HOLDER {
        private static final PublishOnMain HOLDER = new PublishOnMain();
    }

    public static PublishOnMain getInstance() {
        return MAIN_HOLDER.HOLDER;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void prepareToPublish(Bundle bundle) {
        try {
            mPublishService.prepareToPublish(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unRegisterCallback() {
        try {
            mPublishService.unRegisterCallback(publishCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
