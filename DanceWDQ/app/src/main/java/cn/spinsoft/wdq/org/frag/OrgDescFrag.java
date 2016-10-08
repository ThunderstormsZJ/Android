package cn.spinsoft.wdq.org.frag;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Map;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseFragment;
import cn.spinsoft.wdq.org.biz.OrgHandler;
import cn.spinsoft.wdq.org.biz.OrgInfoDetails;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.video.VideoDetailsNewActivity;

/**
 * Created by hushujun on 16/1/7.
 */
public class OrgDescFrag extends BaseFragment {
    private static final String TAG = OrgDescFrag.class.getSimpleName();

    private TextView mOrgDescTx, mAddressTx, mPhoneTx;
    //    private ImageView mPhoneCallImg;
    private ViewStub mImageVs;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_org_desc;
    }

    @Override
    protected void initViewAndListener(View root, Bundle savedInstanceState) {
        mOrgDescTx = (TextView) root.findViewById(R.id.org_desc_introduce);
        mAddressTx = (TextView) root.findViewById(R.id.org_desc_address);
        mPhoneTx = (TextView) root.findViewById(R.id.org_desc_phone);
//        mPhoneCallImg = (ImageView) root.findViewById(R.id.org_desc_phone_call);
        mImageVs = (ViewStub) root.findViewById(R.id.org_desc_images_vs);
        mPhoneTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.Ints.PERMISSION_CALL_PHONE);
            }
        });

      /*  mPhoneCallImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {}*//*
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},Constants.Ints.PERMISSION_CALL_PHONE);
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.Ints.PERMISSION_CALL_PHONE) {
            for (int index = 0; index < permissions.length; index++) {
                switch (permissions[index]) {
                    case Manifest.permission.CALL_PHONE:
                        if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                            //Builder是对对话框进行设置
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setIcon(android.R.drawable.ic_dialog_alert);//设置图标
                            builder.setTitle("提示");
                            builder.setMessage("是否拨打客服电话  " + mPhoneTx.getText().toString())
                                    .setPositiveButton("取消", null)
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, //就是对话框控件
                                                            int which) {//表示正面,中性，负面
                                            Intent phoneIntent = new Intent("android.intent.action.CALL",
                                                    Uri.parse("tel:" + mPhoneTx.getText().toString()));
                                            //启动
                                            startActivity(phoneIntent);
                                        }
                                    });
                            builder.setCancelable(false);//点击对话框以外的区域，对话框不会关闭
                            AlertDialog d = builder.create();//创建对话框
                            d.show();
                        } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(getActivity(), "您拒绝了拨打电话权限，此功能不可用！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "应用没有拨打电话权限，此功能不可用！", Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncOrgDetails().execute(OrgHandler.Status.orgId,OrgHandler.watcherUserId);
    }

    private void loadDataToWidget(OrgInfoDetails details) {
        if (details == null) {
            return;
        }
        if (TextUtils.isEmpty(details.getIntroduce()) || details.getIntroduce().toLowerCase().equals("null")) {
            mOrgDescTx.setText("暂无简介");
        } else {
            mOrgDescTx.setText(details.getIntroduce());
        }
        if (TextUtils.isEmpty(details.getAddress()) || details.getAddress().toLowerCase().equals("null")) {
            mAddressTx.setText("不详");
        } else {
            mAddressTx.setText(details.getAddress());
        }
        if (TextUtils.isEmpty(details.getMobile()) || !TextUtils.isDigitsOnly(details.getMobile())) {
            mPhoneTx.setText("保密,试试大声喊");
//            mPhoneCallImg.setVisibility(View.GONE);
        } else {
            mPhoneTx.setText(details.getMobile());
        }
        Map<String, String> workUrls = details.getWorkUrls();
        if (workUrls == null || workUrls.isEmpty()) {
            return;
        }
        View view = mImageVs.inflate();
        SimpleDraweeView[] imageViews = new SimpleDraweeView[4];
        imageViews[0] = (SimpleDraweeView) view.findViewById(R.id.org_desc_image1);
        imageViews[1] = (SimpleDraweeView) view.findViewById(R.id.org_desc_image2);
        imageViews[2] = (SimpleDraweeView) view.findViewById(R.id.org_desc_image3);
        imageViews[3] = (SimpleDraweeView) view.findViewById(R.id.org_desc_image4);
        //获取屏幕宽度
        LinearLayout.LayoutParams lp = null;
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 20;
        width = width / 3;
        int size = workUrls.size() > 3 ? 3 : workUrls.size();
        int visible = Math.min(size, 3);
        for (int i = 0; i < imageViews.length; i++) {
            if (i < visible) {
                lp = new LinearLayout.LayoutParams(width, width);
                if (i > 0) {
                    lp.leftMargin = 5;
                }
                final String videoId = (String) workUrls.keySet().toArray()[i];
                imageViews[i].setVisibility(View.VISIBLE);
                imageViews[i].setLayoutParams(lp);
                imageViews[i].setImageURI(Uri.parse(workUrls.get(videoId)));
                imageViews[i].setTag(videoId);
                imageViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), VideoDetailsNewActivity.class);
                        intent.putExtra(Constants.Strings.VIDEO_ID, Integer.parseInt(videoId));
                        int orgId = getActivity().getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
                        intent.putExtra(Constants.Strings.OWNER_ID, orgId);
                        startActivity(intent);
                    }
                });
            } else {
                imageViews[i].setVisibility(View.GONE);
            }
        }
    }

    class AsyncOrgDetails extends AsyncTask<Integer, Integer, OrgInfoDetails> {

        @Override
        protected OrgInfoDetails doInBackground(Integer... params) {
            return OrgParser.getOrgDetails(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(OrgInfoDetails orgInfoDetails) {
            loadDataToWidget(orgInfoDetails);
        }
    }
}
