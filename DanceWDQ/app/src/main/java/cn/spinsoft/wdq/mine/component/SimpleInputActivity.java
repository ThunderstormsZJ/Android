package cn.spinsoft.wdq.mine.component;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.ContentResolverUtil;

/**
 * Created by zhoujun on 16/1/13.
 */
public class SimpleInputActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private static final String TAG = SimpleInputActivity.class.getSimpleName();

    private TextView mInputTitleTx;
    private EditText mInputEt;
    //    private TextView mLimitTx, mLeftTx;
    private ImageButton mClearIBtn;
    private RelativeLayout mContainerRl;

    private int maxLength = 15;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_input;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        mInputTitleTx = (TextView) findViewById(R.id.base_title_name);
        TextView confirmTx = (TextView) findViewById(R.id.base_title_otherfunction);
        confirmTx.setText(R.string.action_confirm);
        mInputEt = (EditText) findViewById(R.id.simple_input_et);
        mClearIBtn = (ImageButton) findViewById(R.id.simple_input_clear);
        mContainerRl = (RelativeLayout) findViewById(R.id.simple_input_container);

        mInputEt.addTextChangedListener(this);
        mClearIBtn.setOnClickListener(this);
        backTx.setOnClickListener(this);
        confirmTx.setOnClickListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Intent intent = getIntent();
        String title = intent.getStringExtra(Constants.Strings.SIMPLE_INPUT_TILE);
        if (!TextUtils.isEmpty(title)) {
            mInputTitleTx.setText(/*"输入" +*/ title);
        }
        maxLength = intent.getIntExtra(Constants.Strings.SIMPLE_INPUT_LIMIT, 15);
        mInputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        int inputType = intent.getIntExtra(Constants.Strings.SIMPLE_INPUT_TYPE, InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mInputEt.setInputType(inputType);
        int inputLine = intent.getIntExtra(Constants.Strings.SIMPLE_INPUT_LINE, 1);
        if (inputLine == 1) {
            mInputEt.setSingleLine(true);
        } else {
            mInputEt.setSingleLine(false);
            mClearIBtn.setVisibility(View.GONE);
            mContainerRl.setPadding(10, 0, 10, 0);
        }
        mInputEt.setLines(inputLine);
        mInputEt.setHorizontallyScrolling(false);
        mInputEt.setText(intent.getStringExtra(Constants.Strings.SIMPLE_INPUT_PREVIOUS));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.base_title_otherfunction:
                String content = mInputEt.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.Strings.SIMPLE_INPUT_RESULT, content);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(this, "你还未输入任何内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.simple_input_clear:
                mInputEt.setText("");
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

    }

    @Override
    public void afterTextChanged(Editable s) {
        int length = s.length();
//        mLeftTx.setText(String.format("还剩 %d 个字", maxLength - length));
    }

    @Override
    public void finish() {
        ContentResolverUtil.hideIMM(getCurrentFocus());
        super.finish();
    }
}
