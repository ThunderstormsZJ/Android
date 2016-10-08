package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.mine.widget.ChooseAddressDia;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;
import cn.spinsoft.wdq.utils.StringUtils;

/**
 * Created by zhoujun on 2016-5-4.
 */
public class AddressChooseActivity extends BaseActivity implements View.OnClickListener, ChooseAddressDia.OnAddressChooseListener {
    private final static String TAG = AddressChooseActivity.class.getSimpleName();

    private TextView mAddressChooseTx;
    private EditText mDetailAddress;
    private ChooseAddressDia addressDia;
    private String addressInfo = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_org_address_input;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView baseTx = (TextView) findViewById(R.id.base_title_back);
        TextView titleTx = (TextView) findViewById(R.id.base_title_name);
        TextView otherTx = (TextView) findViewById(R.id.base_title_otherfunction);
        mAddressChooseTx = (TextView) findViewById(R.id.address_choose_container);
        mDetailAddress = (EditText) findViewById(R.id.address_choose_detailaddress);

        titleTx.setText("编辑地址");
        otherTx.setText("完成");

        Intent intent = getIntent();
        addressInfo = intent.getStringExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS);
        if (addressInfo == null || addressInfo.isEmpty()) {
            addressDia = new ChooseAddressDia(this, null, null, null);
        } else {
            addressDia = new ChooseAddressDia(this, addressInfo.split(" ")[0], addressInfo.split(" ")[1], addressInfo.split(" ")[2]);
        }

        baseTx.setOnClickListener(this);
        otherTx.setOnClickListener(this);
        mAddressChooseTx.setOnClickListener(this);
        addressDia.setOnAddressConfirmListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (addressInfo == null || addressInfo.isEmpty()) {
            mAddressChooseTx.setText("");
        } else {
            mAddressChooseTx.setText(StringUtils.getNoBlankString(addressInfo.substring(0, addressInfo.lastIndexOf(" "))));
        }
        mDetailAddress.setText(addressInfo.substring(addressInfo.lastIndexOf(" ") + 1, addressInfo.length()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.base_title_otherfunction://完成
                doComfirmOper();
                break;
            case R.id.address_choose_container:
                addressDia.show();
                break;
        }
    }

    public void doComfirmOper() {
        if (!TextUtils.isEmpty(mDetailAddress.getText().toString().trim())) {
            Intent intent = new Intent();
            if (mAddressChooseTx.getTag() == null) {
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_RESULT, addressInfo.substring(0, addressInfo.lastIndexOf(" ")) + " " + mDetailAddress.getText().toString().trim());
            } else {
                intent.putExtra(Constants.Strings.SIMPLE_INPUT_RESULT, mAddressChooseTx.getTag() + " " + mDetailAddress.getText());
            }
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "请输入详细地址", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnAddressComfirm(String chooseAddress) {
        mAddressChooseTx.setText(StringUtils.getNoBlankString(chooseAddress));
        mAddressChooseTx.setTag(chooseAddress);
    }


    @Override
    public void finish() {
        ContentResolverUtil.hideIMM(getCurrentFocus());
        super.finish();
    }
}
