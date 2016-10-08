package cn.spinsoft.wdq.discover.biz;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.bean.CommentListWithInfo;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.enums.ContestProgessState;
import cn.spinsoft.wdq.enums.DiscoverType;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.UrlManager;
import cn.spinsoft.wdq.video.biz.DetailParser;

/**
 * Created by hushujun on 15/12/9.
 */
public class DiscoverParser {
    private static final String TAG = DiscoverParser.class.getSimpleName();

    public static DiscoverDetail getDiscoverDetail(int typeId, int eventId, int watcherUserId) {
        if (typeId < 0 || eventId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("f_type", String.valueOf(typeId));
        builder.add("typeId", String.valueOf(eventId));
        if (watcherUserId > 0) {
            builder.add("userid", String.valueOf(watcherUserId));
        }
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_DETAIL))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getDiscoverDetail:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                JSONObject resultObj = object.optJSONObject("result");

                DiscoverDetail detail = new DiscoverDetail();
                detail.setCommentCount(resultObj.optInt("commentNum"));
                detail.setContent(resultObj.optString("content"));
                detail.setPhotoUrl(resultObj.optString("headurl"));
                detail.setType(DiscoverType.getEnum(resultObj.optInt("f_type")));

                JSONArray imgArr = resultObj.optJSONArray("images");
                List<String> imgUrls = new ArrayList<>();
                for (int i = 0; i < imgArr.length(); i++) {
                    imgUrls.add(imgArr.optString(i));
                }
                detail.setImageUrls(imgUrls);

                JSONArray smallArr = resultObj.optJSONArray("smallImgs");
                List<String> smallImgUrls = new ArrayList<String>();
                for (int i = 0; i < smallArr.length(); i++) {
                    smallImgUrls.add(smallArr.optString(i));
                }
                detail.setSmallImageUrls(smallImgUrls);

                detail.setNickName(resultObj.optString("nickname"));
                detail.setPublishTime(resultObj.optString("timeDiff"));
                detail.setPublishEndTime(resultObj.optString("appendtime"));
                detail.setTitle(resultObj.optString("title"));
                detail.setIsLike(resultObj.optInt("isThumb") == 1);
                detail.setLikeCount(resultObj.optInt("thumbNum"));

                JSONArray userArr = resultObj.optJSONArray("thumblist");
                List<SimpleItemData> users = new ArrayList<>();
                for (int j = 0; j < userArr.length(); j++) {
                    JSONObject userObj = userArr.optJSONObject(j);
                    SimpleItemData itemData = new SimpleItemData();
                    itemData.setName(userObj.optString("headurl"));
                    itemData.setId(userObj.optInt("userid"));
                    itemData.setSubId(userObj.optInt("orgid"));
                    users.add(itemData);
                }
                detail.setLikeUsers(users);

                detail.setStartTime(resultObj.optString("starttime"));
                detail.setEndTime(resultObj.optString("endtime"));
                detail.setLocation(resultObj.optString("location"));
                detail.setDanceName(resultObj.optString("dancename"));
                detail.setForwardNum(resultObj.optInt("forwardNum"));
                detail.setOrgName(resultObj.optString("orgname"));
                detail.setUserId(resultObj.optInt("userid"));
                detail.setSalary(resultObj.optString("payment"));
                detail.setOrgIntro(resultObj.optString("orgintro"));
                detail.setPosSmallImg(resultObj.optString("poSmallImg"));
                detail.setPosBigImg(resultObj.optString("poBigImg"));
                detail.setVideoImg(resultObj.optString("videoimg"));
                detail.setVideoUrl(resultObj.optString("videoUrl"));
                detail.setCity(resultObj.optString("city"));
                detail.setProgessState(ContestProgessState.getEnum(resultObj.optInt("state")));
                detail.setPeopleNum(resultObj.optInt("personnum"));
                detail.setOrgId(resultObj.optInt("orgid"));
                return detail;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CommentListWithInfo getDiscoverComment(int typeId, int eventId, int pageNum) {
        if (typeId < 0 || eventId < 0 || pageNum < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("f_type", String.valueOf(typeId));
        builder.add("typeId", String.valueOf(eventId));
        builder.add("pageNumber", String.valueOf(pageNum));
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_COMMENT))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getDiscoverComment:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                JSONObject dataObj = object.optJSONObject("commentlist");
                CommentListWithInfo listWithInfo = new CommentListWithInfo();
                listWithInfo.setTotalPages(dataObj.optInt("totalPage"));
                listWithInfo.setPageNumber(dataObj.optInt("pageNumber"));

                JSONArray listArr = dataObj.optJSONArray("list");
                listWithInfo.setDataList(DetailParser.getCommentList(listArr));
                return listWithInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse like(int typeId, int eventId, int watcherId, int ownerId) {
        if (typeId < 0 || eventId < 0 || watcherId < 0 || ownerId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("f_type", String.valueOf(typeId));
        builder.add("typeId", String.valueOf(eventId));
        builder.add("userid", String.valueOf(watcherId));
        builder.add("attuserid", String.valueOf(ownerId));
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_LIKE))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "like:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("errmsg"));
            return simpleResponse;
//            return object.optInt("isThumb") == 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse sendComment(int typeId, int eventId, int watcherId, int ownerId, String comment) {
        if (typeId < 0 || eventId < 0 || watcherId < 0 || ownerId < 0 || TextUtils.isEmpty(comment)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("f_type", String.valueOf(typeId)),
                new OkHttpClientManager.Param("typeId", String.valueOf(eventId)),
                new OkHttpClientManager.Param("userid", String.valueOf(watcherId)),
                new OkHttpClientManager.Param("attuserid", String.valueOf(ownerId)),
                new OkHttpClientManager.Param("content", comment)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_COMMENT_SEND), params);
            String json = response.body().string();
            LogUtil.w(TAG, "sendComment:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("errmsg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse discoverSignIn(int typeId, int eventId, int watcherId, int ownerId, String[] signParams) {
        if (typeId < 0 || eventId < 0 || watcherId < 0 || ownerId < 0 || signParams == null) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("f_type", String.valueOf(typeId)),
                new OkHttpClientManager.Param("typeId", String.valueOf(eventId)),
                new OkHttpClientManager.Param("userid", String.valueOf(watcherId)),
                new OkHttpClientManager.Param("attuserid", String.valueOf(ownerId)),
                new OkHttpClientManager.Param("name", signParams[0]),
                new OkHttpClientManager.Param("telephone", signParams[1]),
                new OkHttpClientManager.Param("number", signParams[2]),
                new OkHttpClientManager.Param("remark", signParams[3])
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_SIGN_IN), params);
            String json = response.body().string();
            LogUtil.w(TAG, "discoverSignIn:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("errmsg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //增加转发数量
    public static SimpleResponse discoverAddShare(int typeId, int fType, int userId, int attUserId, String forwardWay) {
        if (userId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("typeId", String.valueOf(typeId));
        builder.add("f_type", String.valueOf(fType));
        if (userId > 0) {
            builder.add("userid", String.valueOf(userId));
        }
        builder.add("attuserid", String.valueOf(attUserId));
        builder.add("way", String.valueOf(forwardWay));
        Request request = new Request.Builder()
                .url(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_ADD_SHARE))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
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

    //删除发现动态
    public static SimpleResponse deleteDiscoverItemById(int discoverId, int discoverType) {
        if (discoverId < 1) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("typeId", String.valueOf(discoverId)),
                new OkHttpClientManager.Param("f_type", String.valueOf(discoverType))
        };
        try {
            Response response = OkHttpClientManager.getInstance()
                    .getPostDelegate().post(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_DELETE_BY_ID), params);
            String json = response.body().string();
            LogUtil.w(TAG, "deleteDiscover:" + json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
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


    /**
     * 获取报名人数
     *
     * @param eventId
     * @param typeId
     * @param pageNum
     * @return
     */
    public static SignListWithInfo getSignPerson(int eventId, int typeId, int pageNum) {
        if (eventId < 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("typeId", String.valueOf(eventId)),
                new OkHttpClientManager.Param("f_type", String.valueOf(typeId)),
                new OkHttpClientManager.Param("pageNumber", String.valueOf(pageNum))
        };
        try {
            Response response = OkHttpClientManager.getInstance()
                    .getPostDelegate().post(UrlManager.getUrl(UrlManager.UrlName.DISCOVER_SIGN_PERSON), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObject = new JSONObject(json);
            SignListWithInfo signList = null;
            if (jsonObject.optString("code").equals("0")) {
                signList = new SignListWithInfo();
                SimpleResponse simpleResponse = new SimpleResponse();
                simpleResponse.setCode(jsonObject.optInt("code"));
                simpleResponse.setMessage(jsonObject.optString("errmsg"));
                signList.setResponse(simpleResponse);

                JSONObject pageContent = jsonObject.optJSONObject("paging");
                signList.setPageNumber(pageContent.optInt("pageNumber"));
                signList.setTotalPages(pageContent.optInt("totalPage"));

                JSONArray jsonArray = pageContent.optJSONArray("list");
                SignInfo signInfo = null;
                List<SignInfo> signInfos = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    signInfo = new SignInfo();
                    JSONObject jsonSign = jsonArray.optJSONObject(i);
                    signInfo.setName(jsonSign.optString("name"));
                    signInfo.setPersonNum(jsonSign.optString("number"));
                    signInfo.setTelphone(jsonSign.optString("telphone"));
                    signInfo.setRemark(jsonSign.optString("remark"));
                    signInfos.add(signInfo);
                }
                signList.setDataList(signInfos);
            }
            return signList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
