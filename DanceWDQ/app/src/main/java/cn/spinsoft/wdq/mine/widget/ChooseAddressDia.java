package cn.spinsoft.wdq.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.mine.biz.CityModel;
import cn.spinsoft.wdq.mine.biz.DistrictModel;
import cn.spinsoft.wdq.mine.biz.ProvinceModel;
import cn.spinsoft.wdq.mine.biz.XmlParserHandler;
import cn.spinsoft.wdq.widget.linkwidget.OnWheelChangedListener;
import cn.spinsoft.wdq.widget.linkwidget.WheelView;
import cn.spinsoft.wdq.widget.linkwidget.adapters.ArrayWheelAdapter;

/**
 * Created by zhoujun on 2016-5-4.
 */
public class ChooseAddressDia extends Dialog implements OnWheelChangedListener, View.OnClickListener {
    private final static String TAG = ChooseAddressDia.class.getSimpleName();


    protected String[] mProvinceDatas;
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
    protected String mCurrentProviceName = "";
    protected String mCurrentCityName = "";
    protected String mCurrentDistrictName = "";
    protected String mCurrentZipCode = "";
    private int mProviceIndex, mCityIndex, mDistriceIndex;
    private int mProviceTmpIndex, mCityTmpIndex, mDistriceTmpIndex;
    private TextView mCancel, mConfirm;

    private WheelView mProviceWv, mCityWv, mDistrictWv;

    private OnAddressChooseListener mAddressChooseListener;

    public ChooseAddressDia(Context context, String provice, String city, String district) {
        super(context, R.style.DialogWithTransparentBackground);
        if (provice != null && city != null && district != null) {
            this.mCurrentProviceName = provice;
            this.mCurrentCityName = city;
            this.mCurrentDistrictName = district;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHanlder.sendEmptyMessage(R.id.msg_get_address_datas);
            }
        }).start();
    }

    public void setOnAddressConfirmListener(OnAddressChooseListener addressChooseListener) {
        this.mAddressChooseListener = addressChooseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_address_choose);
        mProviceWv = (WheelView) findViewById(R.id.dia_address_choose_province);
        mCityWv = (WheelView) findViewById(R.id.dia_address_choose_city);
        mDistrictWv = (WheelView) findViewById(R.id.dia_address_choose_district);
        mCancel = (TextView) findViewById(R.id.dia_address_choose_cancel);
        mConfirm = (TextView) findViewById(R.id.dia_address_choose_comfirm);

        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mProviceWv.addChangingListener(this);
        mCityWv.addChangingListener(this);
        mDistrictWv.addChangingListener(this);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        mProviceWv.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), mProvinceDatas));
        mProviceWv.setVisibleItems(7);
        mCityWv.setVisibleItems(7);
        mDistrictWv.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mProviceWv.setCurrentItem(mProviceIndex);
        mCityWv.setCurrentItem(mCityIndex);
        mDistrictWv.setCurrentItem(mDistriceIndex);
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == R.id.msg_get_address_datas) {
                initProvinceDatas();
            }
        }
    };

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mProviceWv) {
            updateCities();
        } else if (wheel == mCityWv) {
            updateAreas();
        } else if (wheel == mDistrictWv) {
            mDistriceTmpIndex = newValue;
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
        }
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mProviceWv.getCurrentItem();
        mProviceTmpIndex = pCurrent;
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mCityWv.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), cities));
        mCityWv.setCurrentItem(0);
        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mCityWv.getCurrentItem();
        mCityTmpIndex = pCurrent;
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        mDistriceTmpIndex = 0;
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        mDistrictWv.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), areas));
        mDistrictWv.setCurrentItem(0);
    }

    private void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getContext().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();

            provinceList = handler.getDataList();
            if (mCurrentProviceName.isEmpty() && mCurrentCityName.isEmpty() && mCurrentDistrictName.isEmpty()) {
                if (provinceList != null && !provinceList.isEmpty()) {
                    mCurrentProviceName = provinceList.get(0).getName();
                    List<CityModel> cityList = provinceList.get(0).getCityList();
                    if (cityList != null && !cityList.isEmpty()) {
                        mCurrentCityName = cityList.get(0).getName();
                        List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                        mCurrentDistrictName = districtList.get(0).getName();
                        mCurrentZipCode = districtList.get(0).getZipcode();
                    }
                }
            }

            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                if (provinceList.get(i).getName().equals(mCurrentProviceName)) {
                    mProviceIndex = i;
                }
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    if (cityList.get(j).getName().equals(mCurrentCityName)) {
                        mCityIndex = j;
                    }
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        if (districtList.get(k).getName().equals(mCurrentDistrictName)) {
                            mDistriceIndex = k;
                        }
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dia_address_choose_cancel:
                dismiss();
                break;
            case R.id.dia_address_choose_comfirm:
//                LogUtil.w(TAG, mCurrentProviceName + " " + mCurrentCityName + " " + mCurrentDistrictName);
                mProviceIndex = mProviceTmpIndex;
                mCityIndex = mCityTmpIndex;
                mDistriceIndex = mDistriceTmpIndex;
                mAddressChooseListener.OnAddressComfirm(mCurrentProviceName + " " + mCurrentCityName + " " + mCurrentDistrictName);
                dismiss();
                break;
        }
    }

    public interface OnAddressChooseListener {
        void OnAddressComfirm(String chooseAddress);
    }
}
