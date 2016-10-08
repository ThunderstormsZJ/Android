package cn.spinsoft.wdq.login.frag;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.utils.StringUtils;

/**
 * Created by zhoujun on 2016-3-22.
 */
public class ProtocolFrag extends BaseFragment{
    private TextView mReadContentTx;
    @Override
    protected int getLayoutId() {
        return R.layout.frag_register_protocol;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mReadContentTx = (TextView) root.findViewById(R.id.register_user_read_content);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ReadFileAsync().execute();
    }

    class ReadFileAsync extends AsyncTask<String ,String ,String>{
        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = getResources().openRawResource(R.raw.user_protocol);
            return StringUtils.getStringFromText(inputStream);
        }

        @Override
        protected void onPostExecute(String str) {
            if(str!=null){
                mReadContentTx.setText(str);
            }else {
                Toast.makeText(getContext(),"协议获取失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
