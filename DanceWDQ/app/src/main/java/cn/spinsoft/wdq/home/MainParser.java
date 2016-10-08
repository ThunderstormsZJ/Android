package cn.spinsoft.wdq.home;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * Created by hushujun on 15/11/27.
 */
public class MainParser {
    private static final String TAG = MainParser.class.getSimpleName();

    public static String getStartPage() {
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.START_PAGE)).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getStartPage:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                return object.optString("imageurl");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reportLocation(int userId, double longitude, double latitude) {
        if (userId <= 0) {
            return;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userid", String.valueOf(userId));
        builder.add("longitude", String.valueOf(longitude));
        builder.add("latitude", String.valueOf(latitude));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.REPORT_LOCATION)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "reportLocation:" + json);
//            JSONObject object = new JSONObject(json);
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }
}
