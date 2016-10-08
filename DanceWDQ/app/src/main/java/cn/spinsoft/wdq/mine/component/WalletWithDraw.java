package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.widget.ShareBoardDialog;

/**
 * Created by zhoujun on 2016-4-1.
 */
public class WalletWithDraw extends BaseActivity implements View.OnClickListener, TextWatcher {
    private final static String TAG = WalletWithDraw.class.getSimpleName();

    private SimpleDraweeView mPhotoImg;
    private TextView mNickNameTx, mMobileTx, mSurplusTx;
    private Button mWithDrawAllBtn, mConfimBtn;
    private EditText mInputNumEt;

    private String userBanlance, openId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_withdraw;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView titleName = (TextView) findViewById(R.id.base_title_name);
        titleName.setText("提现到微信账户");
        TextView titleBack = (TextView) findViewById(R.id.base_title_back);
        titleBack.setText("返回");

        mPhotoImg = (SimpleDraweeView) findViewById(R.id.wallet_withdraw_headphoto);
        mNickNameTx = (TextView) findViewById(R.id.wallet_withdraw_nickname);
        mMobileTx = (TextView) findViewById(R.id.wallet_withdraw_mobile);
        mSurplusTx = (TextView) findViewById(R.id.wallet_withdraw_surplus_num);
        mWithDrawAllBtn = (Button) findViewById(R.id.wallet_withdraw_all);
        mInputNumEt = (EditText) findViewById(R.id.wallet_withdraw_input_num);
        mConfimBtn = (Button) findViewById(R.id.wallet_withdraw_confirm);

        mInputNumEt.addTextChangedListener(this);
        mWithDrawAllBtn.setOnClickListener(this);
        titleBack.setOnClickListener(this);
        mConfimBtn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDataToWidet();
    }

    private void loadDataToWidet() {
        Intent intent = getIntent();
        openId = intent.getStringExtra(Constants.Strings.USER_OPENID);
        String userName = intent.getStringExtra(Constants.Strings.USER_NAME);
        String userPhoto = intent.getStringExtra(Constants.Strings.USER_PHOTO);
        String userMobile = intent.getStringExtra(Constants.Strings.USER_MOBILE);
        userBanlance = intent.getStringExtra(Constants.Strings.USER_BALANCE);
        userMobile = userMobile.substring(0, 3) + "****" + userMobile.substring(7, userMobile.length());

        mPhotoImg.setImageURI(Uri.parse(userPhoto));
        mNickNameTx.setText(userName);
        mMobileTx.setText(userMobile);
        mSurplusTx.setText("零钱金额￥" + userBanlance);
        mConfimBtn.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.wallet_withdraw_all:
                mInputNumEt.setText(userBanlance);
                break;
            case R.id.wallet_withdraw_confirm:
                UserLogin loginUser = SharedPreferencesUtil.getInstance(this).getLoginUser();
                new AsyncWithDraw().execute(String.valueOf(loginUser.getUserId()), openId, mInputNumEt.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().indexOf("0") != 0 && s.toString().indexOf(".")!=0 && s.length() > 0) {
            Double money = Double.valueOf(s.toString());
            if (money <= Double.valueOf(userBanlance) && money >= 100) {
                mConfimBtn.setEnabled(true);
            } else {
                mConfimBtn.setEnabled(false);
            }
        } else {
            mConfimBtn.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().contains(".")) {
            if (s.length() - s.toString().indexOf(".") - 1 > 2) {
                s.delete(s.toString().indexOf(".") + 3, s.length());
            }
        }
        if (s.toString().indexOf("0") == 0 || s.toString().indexOf(".")==0) {
            s.clear();
        }
    }


    class AsyncWithDraw extends AsyncTask<String, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return MineParser.doWalletWithDraw(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                    userBanlance = simpleResponse.getContentString();
                    Intent intent = new Intent();
                    intent.putExtra(Constants.Strings.USER_BALANCE, userBanlance);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
    }
}
