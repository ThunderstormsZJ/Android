package cn.spinsoft.wdq.org;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.login.biz.UserLogin;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.widget.VerifyButton;

/**
 * Created by zhoujun on 16/1/11.
 */
public class CourseBookingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = CourseBookingActivity.class.getSimpleName();

    private EditText mNameEt, mDateEt, mPhoneEt, mVerifyEt;
    private VerifyButton verifyBtn;

    private int watcherId = -1;
    private int courseId = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_booking;
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView backTx = (TextView) findViewById(R.id.base_title_back);
        TextView titleNameTx = (TextView) findViewById(R.id.base_title_name);
        titleNameTx.setText("登记预约");
        mNameEt = (EditText) findViewById(R.id.course_booking_name);
        mDateEt = (EditText) findViewById(R.id.course_booking_date);
        mPhoneEt = (EditText) findViewById(R.id.course_booking_phone);
        mVerifyEt = (EditText) findViewById(R.id.course_booking_verify);

        verifyBtn = (VerifyButton) findViewById(R.id.course_booking_get_verify);
        Button bookingBtn = (Button) findViewById(R.id.course_booking_confirm);
        backTx.setOnClickListener(this);
        verifyBtn.setOnClickListener(this);
        bookingBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        courseId = getIntent().getIntExtra(Constants.Strings.ORG_COURSE_ID, -1);
        UserLogin userLogin = SharedPreferencesUtil.getInstance(this).getLoginUser();
        if (userLogin != null) {
            watcherId = userLogin.getUserId();
            mPhoneEt.setText(userLogin.getMobile());
        }
        mDateEt.setText(StringUtils.formatTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_title_back:
                finish();
                break;
            case R.id.course_booking_get_verify:
                String phone = mPhoneEt.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, "请先输入手机号码", Toast.LENGTH_SHORT).show();
                } else {
                    if (phone.length() != 11) {
                        Toast.makeText(this, "手机号码格式错误,长度为11位", Toast.LENGTH_SHORT).show();
                    } else {
                        new AsyncVerify().execute(phone);
                        verifyBtn.start();
                        verifyBtn.setEnabled(false);
                    }
                }
                break;
            case R.id.course_booking_confirm:
                String name = mNameEt.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String verifyCode = mVerifyEt.getText().toString();
                    if (TextUtils.isEmpty(verifyCode)) {
                        Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        String date = mDateEt.getText().toString();
                        String mobile = mPhoneEt.getText().toString();
                        new AsyncBookingConfirm().execute(name, date, mobile, verifyCode);
                    }
                }
                break;
            default:
                break;
        }
    }

    class AsyncVerify extends AsyncTask<String, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return OrgParser.getVerify(params[0]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(CourseBookingActivity.this, "验证码发送成功,请注意查看手机短信", Toast.LENGTH_SHORT).show();
                } else {
                    verifyBtn.setEnabled(true);
                    Toast.makeText(CourseBookingActivity.this, simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                verifyBtn.setEnabled(true);
                Toast.makeText(CourseBookingActivity.this, "验证码发送失败,请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AsyncBookingConfirm extends AsyncTask<String, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return OrgParser.bookingConfirm(watcherId, courseId, params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(CourseBookingActivity.this, "登记预约成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(CourseBookingActivity.this, simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CourseBookingActivity.this, "登记预约失败,请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
