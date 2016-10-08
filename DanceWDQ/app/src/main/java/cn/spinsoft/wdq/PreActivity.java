package cn.spinsoft.wdq;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.browse.BrowseNewActivity;
import cn.spinsoft.wdq.home.MainParser;
import cn.spinsoft.wdq.login.LoginNewActivity;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.user.biz.UserParser;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;

/**
 * Created by zhoujun on 15/11/2.
 */
public class PreActivity extends Activity implements Target {
    private static final String TAG = PreActivity.class.getSimpleName();
    private View mRootView;
    private String mImageUrl;
    private static boolean firstEnter = true; // 是否首次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre);
        mRootView = findViewById(R.id.pre_root);

        if (savedInstanceState != null) {
            setIntent(new Intent());// 从堆栈恢复，不再重复解析之前的intent
        }

        autoLogin();
        if (!firstEnter) {
            onIntent();
        } else {
            showSplashView();
        }
    }

    //自动登录
    private void autoLogin() {
        UserLogin loginUser = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (loginUser != null) {
            NIMClient.getService(AuthService.class).
                    login(new LoginInfo(String.valueOf(loginUser.getUserAccount()), loginUser.getImToken()));
            NimUIKit.setAccount(String.valueOf(loginUser.getUserAccount()));
            cn.spinsoft.wdq.utils.LogUtil.w("LoginStatus:", NIMClient.getStatus().toString());
        }
    }

    //显示主要的界面
    private void showSplashView() {
        mImageUrl = SharedPreferencesUtil.getInstance(this).getStartPage();
        if (!TextUtils.isEmpty(mImageUrl)) {
            Picasso.with(this).load(mImageUrl).into(this);
        }
        new AsyncUpdateStartPage().execute((Void[]) null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstEnter) {
            firstEnter = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRootView.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            mRootView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /**
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        onIntent();
    }

    //处理收到的Intent
    private void onIntent() {
        LogUtil.w(TAG, "onIntent...");
        if (SharedPreferencesUtil.getInstance(this).getLoginUser() != null) {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
                }
            }
        } else {
            //跳转登录界面
            startActivity(new Intent(this, LoginNewActivity.class));
            finish();
        }
    }

    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
            BrowseNewActivity.start(PreActivity.this, null);
        } else {
            BrowseNewActivity.start(PreActivity.this, new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
            finish();
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        // TODO: 15/11/27 do nothing here
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        // TODO: 15/11/27 do nothing here
    }

    class AsyncUpdateStartPage extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            return MainParser.getStartPage();
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (!TextUtils.isEmpty(imageUrl)) {
                if (mImageUrl == null || !mImageUrl.equals(imageUrl)) {
                    SharedPreferencesUtil.getInstance(PreActivity.this).saveStartPage(imageUrl);
                    mImageUrl = imageUrl;
                    Picasso.with(PreActivity.this).load(imageUrl).into(PreActivity.this);
                }
            }
            new AsyncGetDanceType().execute((Void[]) null);
        }
    }

    class AsyncGetDanceType extends AsyncTask<Void, Integer, List<SimpleItemData>> {

        @Override
        protected List<SimpleItemData> doInBackground(Void... params) {
            return UserParser.getDanceTypes();
        }

        @Override
        protected void onPostExecute(List<SimpleItemData> simpleItemDatas) {
            if (simpleItemDatas == null || simpleItemDatas.isEmpty()) {
                //减少获取不到舞蹈类型而卡在启动界面的几率
                simpleItemDatas = SharedPreferencesUtil.getInstance(PreActivity.this).getDanceTypes();
                if (simpleItemDatas == null || simpleItemDatas.isEmpty()) {
                    new AsyncGetDanceType().execute((Void[]) null);
                } else {
                    Intent intent = new Intent(PreActivity.this, BrowseNewActivity.class);
                    startActivity(intent);
                    PreActivity.this.finish();
                }
            } else {
                SharedPreferencesUtil.getInstance(PreActivity.this).saveDaceTypes(simpleItemDatas);

                Intent intent = new Intent(PreActivity.this, BrowseNewActivity.class);
                startActivity(intent);
                PreActivity.this.finish();
            }
        }
    }
}
