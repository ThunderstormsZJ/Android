package cn.spinsoft.wdq.browse.biz;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.biz.DiscoverItemBean;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.enums.ContestProgessState;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.mine.biz.PublishVideoBean;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.video.biz.AdvertisementInfo;
import cn.spinsoft.wdq.video.biz.DanceVideoBean;
import cn.spinsoft.wdq.video.biz.DanceVideoListWithInfo;

/**
 * Created by zhoujun on 15/11/5.
 */
public class BrowseParser {
    private static final String TAG = BrowseParser.class.getSimpleName();

    public static DanceVideoListWithInfo searchVideoList(int userId, String title) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userId", String.valueOf(userId)),
                new OkHttpClientManager.Param("title", title)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate().post(UrlManager.getUrl(UrlManager.UrlName.VIDEO_SEARCH), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(jsonObject.optInt("code"));
            simpleResponse.setMessage(jsonObject.optString("msg"));
            JSONArray videosJson = jsonObject.optJSONArray("list");
            DanceVideoListWithInfo danceVideoList = new DanceVideoListWithInfo();
            danceVideoList.setResponse(simpleResponse);
            DanceVideoBean videoBean = null;
            List<DanceVideoBean> videoBeanList = new ArrayList<>();
            for (int i = 0; i < videosJson.length(); i++) {
                JSONObject videoJson = videosJson.optJSONObject(i);
                videoBean = new DanceVideoBean();
                videoBean.setPhotoUrl(videoJson.optString("headurl"));
                videoBean.setNickName(videoJson.optString("nickname"));
                videoBean.setUserId(videoJson.optInt("userid"));
                videoBean.setVideoId(videoJson.optInt("videoid"));
                videoBean.setAttention(Attention.getEnum(videoJson.optInt("isAttention")));
                videoBean.setPosterUrl(videoJson.optString("bigimg"));
                videoBean.setPlayUrl(videoJson.optString("address"));
                videoBean.setDanceName(videoJson.optString("dancename"));
                videoBean.setTitle(videoJson.optString("title"));
                videoBean.setTimeDiff(videoJson.optString("timeDiff"));
                videoBean.setLikeCount(videoJson.optInt("thumbNum"));
                videoBean.setForwardCount(videoJson.optInt("forwardNum"));
                videoBean.setCommentCount(videoJson.optInt("commentNum"));
                videoBean.setAdmireCount(videoJson.optInt("admireNum"));
                videoBean.setLiked(videoJson.optInt("isThumb") == 1);
                videoBean.setIsOriginal(videoJson.optInt("isOriginal") == 1);
                videoBean.setOrgId(videoJson.optInt("orgid"));
                videoBeanList.add(videoBean);
            }
            danceVideoList.setDataList(videoBeanList);
            return danceVideoList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DanceVideoListWithInfo getVideoList(int pageNum, String keyWords) {
        return getVideoList(pageNum, -1, -1, 0, 0, keyWords);//need default type 1
    }

    public static DanceVideoListWithInfo getVideoList(int pageNum, int userid, int type, int rank, int attention) {
        return getVideoList(pageNum, userid, type, rank, attention, null);
    }

    private static DanceVideoListWithInfo getVideoList(int pageNum, int userId, int type, int rank, int attention, String name) {
        if (pageNum < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("pageNumber", String.valueOf(pageNum));
        builder.add("attention", String.valueOf(attention));
        if (userId > 0) {
            builder.add("userid", String.valueOf(userId));
        }
//        if (type > 0) {
        builder.add("type", String.valueOf(type));
//        }
        if (rank >= 0) {
            builder.add("rank", String.valueOf(rank));
        }
        if (!TextUtils.isEmpty(name)) {
            builder.add("name", name);
        } else {
            builder.add("name", "");
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.VIDEO_LIST)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getVideoList:" + json);
            return parseVideoList(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static DanceVideoListWithInfo parseVideoList(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        DanceVideoListWithInfo listWithInfo = new DanceVideoListWithInfo();
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setCode(object.optInt("code"));
        simpleResponse.setMessage(object.optString("msg"));
        listWithInfo.setResponse(simpleResponse);

        if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
            JSONObject videoListObj = object.getJSONArray("videolist").optJSONObject(0);

            JSONObject pagingObj = videoListObj.getJSONObject("paging");

            listWithInfo.setPageNumber(pagingObj.optInt("pageNumber"));
            listWithInfo.setTotalPages(pagingObj.optInt("totalPage"));

            JSONArray listArray = pagingObj.getJSONArray("list");
            List<DanceVideoBean> videoBeanList = new ArrayList<>();

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject videoObj = listArray.optJSONObject(i);
                DanceVideoBean videoBean = new DanceVideoBean();
                videoBean.setPhotoUrl(videoObj.optString("headurl"));
                videoBean.setNickName(videoObj.optString("nickname"));
                videoBean.setUserId(videoObj.optInt("userid"));
                videoBean.setVideoId(videoObj.optInt("videoid"));
                videoBean.setAttention(Attention.getEnum(videoObj.optInt("isAttention")));
                videoBean.setPosterUrl(videoObj.optString("bigimg"));
                videoBean.setPlayUrl(videoObj.optString("address"));
                videoBean.setDanceName(videoObj.optString("dancename"));
                videoBean.setTitle(videoObj.optString("title"));
                videoBean.setTimeDiff(videoObj.optString("timeDiff"));
                videoBean.setLikeCount(videoObj.optInt("thumbNum"));
                videoBean.setForwardCount(videoObj.optInt("forwardNum"));
                videoBean.setCommentCount(videoObj.optInt("commentNum"));
                videoBean.setAdmireCount(videoObj.optInt("admireNum"));
                videoBean.setLiked(videoObj.optInt("isThumb") == 1);
                videoBean.setIsOriginal(videoObj.optInt("isOriginal") == 1);
                videoBean.setOrgId(videoObj.optInt("orgid"));
                videoBeanList.add(videoBean);
            }
            listWithInfo.setDataList(videoBeanList);

//                JSONArray danceArray = videoListObj.getJSONArray("dance");
//                List<SimpleItemData> typeBeanList = new ArrayList<>();
//
//                for (int j = 0; j < danceArray.length(); j++) {
//                    JSONObject typeObj = danceArray.optJSONObject(j);
//                    SimpleItemData typeBean = new SimpleItemData();
//                    typeBean.setNickName(typeObj.optString("dancename"));
//                    typeBean.setId(typeObj.optInt("danceid"));
//                    typeBeanList.add(typeBean);
//                }
//                listWithInfo.setTypeList(typeBeanList);
            return listWithInfo;
        }
        return null;
    }

    public static List<AdvertisementInfo> getAdsList() {
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.VIDEO_ADS)).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getAdsList:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                List<AdvertisementInfo> adsList = new ArrayList<>();
                JSONArray adsArray = object.getJSONArray("advantiseImg");
                for (int i = 0; i < adsArray.length(); i++) {
                    JSONObject adsObj = adsArray.optJSONObject(i);
                    AdvertisementInfo info = new AdvertisementInfo();
                    info.setImgUrl(adsObj.optString("averurl"));
                    info.setType(adsObj.optInt("type"));
                    info.setDiscoverId(adsObj.optInt("typeId"));
                    info.setUserId(adsObj.optInt("userid"));
                    info.setSkipType(DiscoverType.getEnum(adsObj.optInt("f_type")));
                    info.setSkipUrl(adsObj.optString("adurl"));
                    adsList.add(info);
                }
                return adsList;
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

    public static DiscoverListWithInfo getDiscoverList(int pageIdx, int type, int danceId, int sort, int userId) {
        return getDiscoverList(pageIdx, type, danceId, sort, userId, -1);
    }

    public static DiscoverListWithInfo getDiscoverList(int pageIdx, int type, int danceId, int sort, int userId, int attendId) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("pageNumber", String.valueOf(pageIdx));
        builder.add("f_type", String.valueOf(type));
        builder.add("d_type", String.valueOf(danceId));
        if (sort >= 0) {
            builder.add("rank", String.valueOf(sort));
        }
        if (userId >= 0) {
            builder.add("userid", String.valueOf(userId));
        }
        if (attendId > 0) {
            builder.add("attendid", String.valueOf(attendId));
        }
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_LIST))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getDiscoverList:" + json);
            return parseDiscoverList(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DiscoverListWithInfo parseDiscoverList(String json, int type) {
        try {
            JSONObject object = new JSONObject(json);
            DiscoverListWithInfo listWithInfo = new DiscoverListWithInfo();
            SimpleResponse response = new SimpleResponse();
            response.setCode(object.optInt("code"));
            response.setMessage(object.optString("msg"));
            listWithInfo.setResponse(response);

            if (response.getCode() == SimpleResponse.SUCCESS) {
                JSONObject pagingObj = object.optJSONObject("paging");
                JSONArray listArr = null;
                if (pagingObj != null) {
                    listWithInfo.setTotalRows(pagingObj.optInt("totalRow"));
                    listWithInfo.setPageNumber(pagingObj.optInt("pageNumber"));
                    listWithInfo.setTotalPages(pagingObj.optInt("totalPage"));
                    listArr = pagingObj.optJSONArray("list");
                } else {
                    listArr = new JSONArray();
                }

                List<DiscoverItemBean> itemBeanList = new ArrayList<>();
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject itemObj = listArr.optJSONObject(i);
                    DiscoverItemBean itemBean = new DiscoverItemBean();
                    itemBean.setPhotoUrl(itemObj.optString("headurl"));
                    itemBean.setNickName(itemObj.optString("nickname"));
                    itemBean.setUserId(itemObj.optInt("userid"));
                    itemBean.setOrgId(itemObj.optInt("orgid"));
                    itemBean.setTitle(itemObj.optString("title"));
                    itemBean.setContent(itemObj.optString("content"));
                    itemBean.setLocation(itemObj.optString("location"));
                    itemBean.setLikeCount(itemObj.optInt("thumbNum"));
                    itemBean.setCommentCount(itemObj.optInt("commentNum"));
                    itemBean.setForwardCount(itemObj.optInt("forwardNum"));
                    itemBean.setTime(itemObj.optString("timeDiff"));
                    itemBean.setEventId(itemObj.optInt("typeId"));
                    itemBean.setForwardId(itemObj.optInt("typeId"));
                    itemBean.setForwarType(itemObj.optInt("f_type"));
                    itemBean.setType(DiscoverType.getEnum(type));
                    itemBean.setIsLike(itemObj.optInt("isThumb") == 1);
                    itemBean.setPosSmallImg(itemObj.optString("poSmallImg"));
                    itemBean.setPosBigImg(itemObj.optString("poBigImg"));
                    itemBean.setProgessState(ContestProgessState.getEnum(itemObj.optInt("state")));
                    itemBean.setPersonNum(itemObj.optInt("personnum"));
                    itemBean.setCity(itemObj.optString("city"));
                    itemBean.setSalary(itemObj.optString("payment"));

                    JSONArray imgArr = itemObj.optJSONArray("images");
                    List<String> urlList = new ArrayList<>();
                    for (int j = 0; j < imgArr.length(); j++) {
                        urlList.add(imgArr.optString(j));
                    }
                    itemBean.setImagesUrls(urlList);
                    JSONArray smallImgArr = itemObj.optJSONArray("smallImgs");
                    List<String> smallImgList = new ArrayList<String>();
                    for (int j = 0; j < smallImgArr.length(); j++) {
                        smallImgList.add(smallImgArr.optString(j));
                    }
                    itemBean.setSmallImgs(smallImgList);
                    itemBeanList.add(itemBean);
                }
                listWithInfo.setDataList(itemBeanList);
                return listWithInfo;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void publishVideo(PublishVideoBean videoBean, OkHttpClientManager.ResultCallback<String> callback) {
        if (videoBean == null) {
            return;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("title", StringUtils.getNoNullString(videoBean.getTitle())),
                new OkHttpClientManager.Param("path", StringUtils.getNoNullString(videoBean.getVideoUrl())),
                new OkHttpClientManager.Param("bigimg", StringUtils.getNoNullString(videoBean.getPosterUrl())),
                new OkHttpClientManager.Param("desc", StringUtils.getNoNullString(videoBean.getDesc())),
                new OkHttpClientManager.Param("type", String.valueOf(videoBean.getDanceType())),
                new OkHttpClientManager.Param("isOriginal", String.valueOf(videoBean.getOriginal())),
                new OkHttpClientManager.Param("userid", String.valueOf(videoBean.getUserId())),
                new OkHttpClientManager.Param("region", StringUtils.getNoNullString(videoBean.getLocation())),
                new OkHttpClientManager.Param("longitude", String.valueOf(videoBean.getLongitude())),
                new OkHttpClientManager.Param("latitude", String.valueOf(videoBean.getLatitude()))
        };

        OkHttpClientManager.getInstance().getPostDelegate().postAsyn(UrlManager.getUrl(UrlManager.UrlName.SAVE_VIDEO_INFO), params, callback);
    }

    public static String getDeviceNetIpAddress() {
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post("http://ip.chinaz.com/getip.aspx", (OkHttpClientManager.Param[]) null);
            String json = response.body().string();
            LogUtil.w(TAG, "getDeviceNetIpAddress:" + json);
            JSONObject object = new JSONObject(json);
            return object.optString("ip");
//            URL url = new URL("http://www.ip38.com");
//            URLConnection connection = url.openConnection();
//            HttpURLConnection httpConnection = (HttpURLConnection) connection;
//            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                InputStream inputStream = httpConnection.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GB2312"));
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    if (line.contains("您的本机IP地址")) {
//                        break;
//                    }
//                }
//                inputStream.close();
//                reader.close();
//                if (!TextUtils.isEmpty(line)) {
//                    int start = line.indexOf("[");
//                    int end = line.indexOf("]", start + 1);
//                    line = line.substring(start + 1, end);
//                    if (line.contains("font")) {
//                        start = line.indexOf(">");
//                        end = line.indexOf("</", start + 1);
//                        line = line.substring(start + 1, end);
//                    }
//                    LogUtil.w(TAG, "getDeviceNetIpAddress:" + line);
//                    return line;
//                }
//                return null;
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WechatPrepay wxOrder(int videoId, int userId, int attUserId, String devicesIp, String orderDesc, String price) {
        if (TextUtils.isEmpty(devicesIp)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("videoid", String.valueOf(videoId)),
                new OkHttpClientManager.Param("userid", String.valueOf(userId)),
                new OkHttpClientManager.Param("attuserid", String.valueOf(attUserId)),
                new OkHttpClientManager.Param("spbill_create_ip", devicesIp),
                new OkHttpClientManager.Param("body", orderDesc),
                new OkHttpClientManager.Param("price", price)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.WX_ORDER), params);
            String json = response.body().string();
            LogUtil.w(TAG, "wxOrder:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("return_code").equals("SUCCESS")) {
                WechatPrepay prepay = new WechatPrepay();
                prepay.setAppId(object.optString("appid"));
                prepay.setNonceStr(object.optString("nonce_str"));
                prepay.setPartnerId(object.optString("mch_id"));
                prepay.setSign(object.optString("sign"));
                prepay.setTradeType(object.optString("trade_type"));
                prepay.setResultCode(object.optString("result_code"));
                prepay.setReturnCode(object.optString("return_code"));
                prepay.setReturnMsg(object.optString("return_msg"));
                prepay.setPrepayId(object.optString("prepay_id"));
                return prepay;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //余额支付
    public static SimpleResponse diceOrder(int videoId, int userId, int attUserId, String price) {
        if (videoId <= 0 && userId <= 0 && attUserId <= 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("videoid", String.valueOf(videoId)),
                new OkHttpClientManager.Param("userid", String.valueOf(userId)),
                new OkHttpClientManager.Param("attuserid", String.valueOf(attUserId)),
                new OkHttpClientManager.Param("money", price)
        };

        try {
            Response response = OkHttpClientManager.getInstance()
                    .getPostDelegate().post(UrlManager.getUrl(UrlManager.UrlName.DICE_ORDER), params);
            String json = response.body().string();
            LogUtil.w(TAG, "diceOrder:" + json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setContentString(jsonObject.optString("balance"));
            simpleResponse.setCode(jsonObject.optInt("code"));
            simpleResponse.setMessage(jsonObject.optString("errmsg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    //app版本获取
    public static SimpleResponse getAppVersion() {
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(UrlManager.getUrl(UrlManager.UrlName.APP_VERSION));
            String json = response.body().string();
            LogUtil.w(TAG, json);
            SimpleResponse simpleResponse = new SimpleResponse();
            JSONObject jsonObject = new JSONObject(json);
            simpleResponse.setCode(jsonObject.optInt("code"));
            JSONObject versionJson = jsonObject.optJSONObject("version");
            simpleResponse.setContentString(versionJson.optString("versionCode"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
