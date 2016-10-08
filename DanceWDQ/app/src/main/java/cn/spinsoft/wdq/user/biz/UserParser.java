package cn.spinsoft.wdq.user.biz;

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

import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.biz.DiscoverItemBean;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.enums.AgeRange;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * Created by hushujun on 15/12/2.
 */
public class UserParser {
    private static final String TAG = UserParser.class.getSimpleName();

    public static UserDetail getDetail(int userId, int watcherId) {
        if (userId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userid", String.valueOf(userId));
        builder.add("currentuserid", String.valueOf(watcherId));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.USER_DETAIL)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getUserDetail:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                UserDetail userDetail = new UserDetail();
                JSONObject userObj = object.optJSONObject("userinfo");
                userDetail.setAttention(Attention.getEnum(userObj.optInt("isAttention")));
                userDetail.setFansNum(userObj.optString("fansNum"));
                userDetail.setOrgName(userObj.optString("orgname"));
                userDetail.setSignature(userObj.optString("signature"));
                userDetail.setAttest(userObj.optString("renzhengName"));
                userDetail.setSex(Sex.getEnum(userObj.optInt("sex")));
                userDetail.setDynamicNum(userObj.optString("dongtaiNum"));
                userDetail.setNickName(userObj.optString("nickname"));
                userDetail.setPhotoUrl(userObj.optString("headurl"));
                userDetail.setAttentNum(userObj.optString("attentNum"));
                userDetail.setWorksNum(userObj.optString("videoNum"));
                userDetail.setUserId(userObj.optInt("userId"));
                userDetail.setOrgid(userObj.optInt("orgid"));

                return userDetail;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserVideoWithInfo getVideos(int userId, int pageIdx) {
        if (userId < 0 || pageIdx < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userid", String.valueOf(userId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        RequestBody body = builder.build();
        return getVideos(UrlManager.getUrl(UrlManager.UrlName.USER_VIDEOS), body);
    }

    public static UserVideoWithInfo getVideos(String url, RequestBody body) {
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getVideos:" + json);

            JSONObject object = new JSONObject(json);
            UserVideoWithInfo videoWithInfo = new UserVideoWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            videoWithInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                videoWithInfo.setPageNumber(object.optInt("pageNumber"));
                videoWithInfo.setTotalPages(object.optInt("totalPage"));
                videoWithInfo.setThumNum(object.optInt("thumbNum"));

                JSONArray listArr = object.optJSONArray("videos");
                if (listArr == null) {
                    listArr = object.optJSONArray("list");
                }
                List<UserVideo> videoList = new ArrayList<>();
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject videoObj = listArr.optJSONObject(i);
                    UserVideo userVideo = new UserVideo();
                    userVideo.setPlayUrl(videoObj.optString("address"));
                    userVideo.setCreateTime(videoObj.optString("createtime"));
                    userVideo.setDanceId(videoObj.optString("danceid"));
                    userVideo.setIntroduce(videoObj.optString("intro"));
                    userVideo.setPoster(videoObj.optString("bigimg"));
                    userVideo.setTitle(videoObj.optString("title"));
                    userVideo.setUserId(videoObj.optInt("userid"));
                    userVideo.setVideoId(videoObj.optInt("videoid"));
                    videoList.add(userVideo);
                }
                videoWithInfo.setDataList(videoList);
                return videoWithInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DiscoverListWithInfo getDynamic(int userId, int pageIdx,int attendid) {
        if (userId < 0 || pageIdx < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userid", String.valueOf(userId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        builder.add("attendid", String.valueOf(attendid));
        RequestBody body = builder.build();
        return getDynamic(UrlManager.getUrl(UrlManager.UrlName.USER_DYNAMIC), body);
    }

    public static DiscoverListWithInfo getDynamic(String url, RequestBody body) {
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getDynamic:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
//                JSONObject pagingObj = object.optJSONObject("paging");
                DiscoverListWithInfo listWithInfo = new DiscoverListWithInfo();
                listWithInfo.setPageNumber(object.optInt("pageNumber"));
                listWithInfo.setTotalPages(object.optInt("totalPage"));

                JSONArray listArr = object.optJSONArray("dynamics");
                if (listArr == null) {
                    listArr = object.optJSONArray("dongtailist");
                }
                List<DiscoverItemBean> itemBeanList = new ArrayList<>();
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject itemObj = listArr.optJSONObject(i);
                    DiscoverItemBean itemBean = new DiscoverItemBean();
                    itemBean.setPhotoUrl(itemObj.optString("headurl"));
                    itemBean.setNickName(itemObj.optString("nickname"));
                    itemBean.setTitle(itemObj.optString("title"));
                    itemBean.setContent(itemObj.optString("content"));
                    itemBean.setLocation(itemObj.optString("location"));
                    itemBean.setLikeCount(itemObj.optInt("thumbNum"));
                    itemBean.setIsLike(itemObj.optInt("isThumb") == 1);
                    itemBean.setCommentCount(itemObj.optInt("commentNum"));
                    itemBean.setForwardCount(itemObj.optInt("forwardNum"));
                    itemBean.setIsLike(itemObj.optInt("isThumb") == 1);
                    itemBean.setTime(itemObj.optString("timeDiff"));
                    itemBean.setType(DiscoverType.getEnum(itemObj.optInt("f_type")));
                    itemBean.setEventId(itemObj.optInt("typeId"));
                    itemBean.setUserId(itemObj.optInt("userid"));
//                    itemBean.setProgessState(itemObj.optInt("typeId"));

                    JSONArray smallImgArr = itemObj.optJSONArray("smallImgs");
                    List<String> smallUrlList = new ArrayList<>();
                    for (int j = 0; j < smallImgArr.length(); j++) {
                        smallUrlList.add(smallImgArr.optString(j));
                    }
                    itemBean.setSmallImgs(smallUrlList);

                    JSONArray imgArr = itemObj.optJSONArray("images");
                    List<String> urlList = new ArrayList<>();
                    for (int j = 0; j < imgArr.length(); j++) {
                        urlList.add(imgArr.optString(j));
                    }
                    itemBean.setImagesUrls(urlList);
                    itemBeanList.add(itemBean);
                }
                listWithInfo.setDataList(itemBeanList);
                return listWithInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DancerListWithInfo getAttentions(int userId, int pageIdx, int watchUserId) {
        return getDancerList(UrlManager.getUrl(UrlManager.UrlName.USER_ATTENTION), userId, pageIdx, watchUserId);
    }

    public static DancerListWithInfo getFans(int userId, int pageIdx, int watchUserId) {
        return getDancerList(UrlManager.getUrl(UrlManager.UrlName.USER_FANS), userId, pageIdx, watchUserId);
    }

    private static DancerListWithInfo getDancerList(String url, int userId, int pageIdx, int watchUserId) {
        if (userId < 0 || pageIdx < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userid", String.valueOf(userId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        builder.add("currentuserid", String.valueOf(watchUserId));
        RequestBody body = builder.build();
        return getDancerList(url, body);
    }

    public static DancerListWithInfo getDancerList(String url, RequestBody body) {
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getDancerList:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {

                DancerListWithInfo listWithInfo = new DancerListWithInfo();
                listWithInfo.setPageNumber(object.optInt("pageNumber"));
                listWithInfo.setTotalPages(object.optInt("totalPage"));

//                JSONObject usersObj = object.optJSONObject("userlist");
                JSONArray listArr = object.optJSONArray("list");
                if (listArr != null) {
                    List<DancerInfo> dancerInfoList = new ArrayList<>();
                    for (int i = 0; i < listArr.length(); i++) {
                        JSONObject dancerObj = listArr.optJSONObject(i);
                        DancerInfo dancerInfo = new DancerInfo();
                        dancerInfo.setAgeRange(AgeRange.getEnum(dancerObj.optInt("agerange")));
                        dancerInfo.setAttention(Attention.getEnum(dancerObj.optInt("isAttention")));
//                    dancerInfo.setDanceId(dancerObj.optInt("danceid"));
                        dancerInfo.setDistance(dancerObj.optString("distance"));
                        dancerInfo.setPhotoUrl(dancerObj.optString("headurl"));
//                    dancerInfo.setIsPublic(dancerObj.optInt("ifFind") == 1);
                        dancerInfo.setNickName(dancerObj.optString("nickname"));
//                    dancerInfo.setSex(Sex.getEnum(dancerObj.optInt("sex")));
                        dancerInfo.setSignature(dancerObj.optString("signature"));
                        dancerInfo.setUserId(dancerObj.optInt("userid"));
                        dancerInfo.setOrgId(dancerObj.optInt("orgid"));

//                    JSONArray labelArr = dancerObj.optJSONArray("dancename");
//                    if (labelArr != null && labelArr.length() > 0) {
//                        List<String> labelList = new ArrayList<>();
//                        for (int j = 0; j < labelArr.length(); j++) {
//                            labelList.add(labelArr.optString(j));
//                        }
//                        dancerInfo.setLabels(labelList);
//                    }

                        dancerInfoList.add(dancerInfo);
                    }
                    listWithInfo.setDataList(dancerInfoList);
                }
                return listWithInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DancerListWithInfo searchFriendList(int userId, String name, double longitude, double latitude) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userId", String.valueOf(userId)),
                new OkHttpClientManager.Param("name", name),
                new OkHttpClientManager.Param("longitude", String.valueOf(longitude)),
                new OkHttpClientManager.Param("latitude", String.valueOf(latitude))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate().post(UrlManager.getUrl(UrlManager.UrlName.FRIEND_SEARCH), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject object = new JSONObject(json);
            DancerListWithInfo listWithInfo = new DancerListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(Integer.valueOf(object.optString("code")));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                JSONArray listArr = object.optJSONArray("list");
                List<DancerInfo> dancerInfoList = new ArrayList<>();
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject dancerObj = listArr.optJSONObject(i);
                    DancerInfo dancerInfo = new DancerInfo();
                    dancerInfo.setAgeRange(AgeRange.getEnum(dancerObj.optInt("agerange")));
                    dancerInfo.setAttention(Attention.getEnum(dancerObj.optInt("isAttention")));
                    dancerInfo.setDanceId(dancerObj.optInt("danceid"));
                    dancerInfo.setAdmireCount(dancerObj.optInt("thumbNum"));
                    dancerInfo.setDistance(dancerObj.optString("distance"));
                    dancerInfo.setPhotoUrl(dancerObj.optString("headurl"));
                    dancerInfo.setIsPublic(dancerObj.optInt("ifFind") == 1);
                    dancerInfo.setNickName(dancerObj.optString("nickname"));
                    dancerInfo.setSex(Sex.getEnum(dancerObj.optInt("sex")));
                    dancerInfo.setSignature(dancerObj.optString("signature"));
                    dancerInfo.setUserId(dancerObj.optInt("userid"));

                    JSONArray labelArr = dancerObj.optJSONArray("dancenames");
                    if (labelArr != null && labelArr.length() > 0) {
                        List<String> labelList = new ArrayList<>();
                        for (int j = 0; j < labelArr.length(); j++) {
                            labelList.add(labelArr.optString(j));
                        }
                        dancerInfo.setLabels(labelList);
                    }

                    dancerInfoList.add(dancerInfo);
                }
                listWithInfo.setDataList(dancerInfoList);
                return listWithInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DancerListWithInfo getFriendList(int pageIdx, double longitude, double latitude, String nickName) {
        return getFriendList(pageIdx, longitude, latitude, Sex.UNLIMITED.getValue(), -1, -1, nickName, -1, -1);
    }

    public static DancerListWithInfo getFriendList(int pageIdx, double longitude, double latitude,
                                                   int sex, int userId, int ageRange, int danceId, int distance) {
        return getFriendList(pageIdx, longitude, latitude, sex, userId, ageRange, null, danceId, distance);
    }

    private static DancerListWithInfo getFriendList(int pageIdx, double longitude, double latitude,
                                                    int sex, int userId, int ageRange, String nickName, int danceId, int distance) {
        if (pageIdx < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("pageNumber", String.valueOf(pageIdx));
        builder.add("longitude", String.valueOf(longitude));
        builder.add("latitude", String.valueOf(latitude));
//        if (sex >= 0) {
        builder.add("sex", String.valueOf(sex));
//        }
        if (userId >= 0) {
            builder.add("userid", String.valueOf(userId));
        }
//        if (ageRange >= 0) {
//        builder.add("agerange", String.valueOf(ageRange));
//        }
       /* if (!TextUtils.isEmpty(StringUtils.getNoNullString(nickName))) {
            builder.add("nickname", nickName);
        }*/
//        if (danceId >= 0) {
//        builder.add("danceid", String.valueOf(danceId));
//        }
//        if (distance >= 0) {
//        builder.add("distance", String.valueOf(distance));
//        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.FRIEND_LIST)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getFriendList:" + json);
            JSONObject object = new JSONObject(json);
            DancerListWithInfo listWithInfo = new DancerListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setPageNumber(object.optInt("pageNumber"));
                listWithInfo.setTotalPages(object.optInt("totalPage"));

                JSONObject usersObj = object.optJSONObject("userlist");
                JSONArray listArr = usersObj.optJSONArray("list");
                List<DancerInfo> dancerInfoList = new ArrayList<>();
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject dancerObj = listArr.optJSONObject(i);
                    DancerInfo dancerInfo = new DancerInfo();
                    dancerInfo.setAgeRange(AgeRange.getEnum(dancerObj.optInt("agerange")));
                    dancerInfo.setAttention(Attention.getEnum(dancerObj.optInt("isAttention")));
                    dancerInfo.setDanceId(dancerObj.optInt("danceid"));
                    dancerInfo.setAdmireCount(dancerObj.optInt("thumbNum"));
                    dancerInfo.setDistance(dancerObj.optString("distance"));
                    dancerInfo.setPhotoUrl(dancerObj.optString("headurl"));
                    dancerInfo.setIsPublic(dancerObj.optInt("ifFind") == 1);
                    dancerInfo.setNickName(dancerObj.optString("nickname"));
                    dancerInfo.setSex(Sex.getEnum(dancerObj.optInt("sex")));
                    dancerInfo.setSignature(dancerObj.optString("signature"));
                    dancerInfo.setUserId(dancerObj.optInt("userid"));

                    JSONArray labelArr = dancerObj.optJSONArray("dancenames");
                    if (labelArr != null && labelArr.length() > 0) {
                        List<String> labelList = new ArrayList<>();
                        for (int j = 0; j < labelArr.length(); j++) {
                            labelList.add(labelArr.optString(j));
                        }
                        dancerInfo.setLabels(labelList);
                    }

                    dancerInfoList.add(dancerInfo);
                }
                listWithInfo.setDataList(dancerInfoList);
                return listWithInfo;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<SimpleItemData> getDanceTypes() {
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.DANCE_TYPES)).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getDanceTypes:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                List<SimpleItemData> typeBeanList = new ArrayList<>();
                JSONArray typeArr = object.optJSONArray("dancelist");
                for (int i = 0; i < typeArr.length(); i++) {
                    SimpleItemData typeBean = new SimpleItemData();
                    JSONObject typeObj = typeArr.optJSONObject(i);
                    typeBean.setId(typeObj.optInt("danceid"));
                    typeBean.setName(typeObj.optString("dancename"));
                    typeBeanList.add(typeBean);
                }
                return typeBeanList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
