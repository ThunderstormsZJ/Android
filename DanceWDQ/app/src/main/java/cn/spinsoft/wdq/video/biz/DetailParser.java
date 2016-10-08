package cn.spinsoft.wdq.video.biz;

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

import cn.spinsoft.wdq.bean.CommentItem;
import cn.spinsoft.wdq.bean.CommentListWithInfo;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * Created by zhoujun on 15/11/9.
 */
public class DetailParser {
    private static final String TAG = DetailParser.class.getSimpleName();

    /**
     * @param videoId
     * @param watcherUserId 观看用户的ID,可以为空
     * @param ownerUserId   视频所有者的ID
     * @return
     */
    public static VideoDetailBean getVideoDetail(int videoId, int watcherUserId, int ownerUserId) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        if (watcherUserId > 0) {
            builder.add("userid", String.valueOf(watcherUserId));
        }
        if (ownerUserId > 0) {
            builder.add("attuserid", String.valueOf(ownerUserId));
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.VIDEO_DETAIL)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getVideoDetail:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                JSONObject resultObj = object.optJSONObject("result");
                VideoDetailBean detailBean = new VideoDetailBean();
                detailBean.setPlayUrl(resultObj.optString("address"));
                detailBean.setPosterUrl(resultObj.optString("bigimg"));
                detailBean.setDanceName(resultObj.optString("dancename"));
                detailBean.setTitle(resultObj.optString("title"));
                detailBean.setDesc(resultObj.optString("intro"));
                detailBean.setTimeDiff(resultObj.optString("timeDiff"));
                detailBean.setWatchCount(resultObj.optInt("pageview"));
                detailBean.setPhotoUrl(resultObj.optString("headurl"));
                detailBean.setNickName(resultObj.optString("nickname"));
                detailBean.setAttentioned(resultObj.optInt("isAttention") == 1);
                detailBean.setIsLike(resultObj.optInt("isThumb") == 1);
                detailBean.setIsOriginal(resultObj.optInt("isOriginal") == 1);
                detailBean.setLikeCount(resultObj.optInt("thumbNum"));
                detailBean.setForwardCount(resultObj.optInt("forwardNum"));
                detailBean.setCommentCount(resultObj.optInt("commentNum"));
                detailBean.setAdmireCount(resultObj.optInt("admireNum"));
                detailBean.setVideoId(resultObj.optInt("videoid"));
                detailBean.setUserId(resultObj.optInt("userid"));
                detailBean.setOrgId(resultObj.optInt("orgid"));
                JSONArray admireArray = resultObj.optJSONArray("admireList");
                List<SimpleItemData> itemDataList = new ArrayList<SimpleItemData>();
                for (int i = 0; i < admireArray.length(); i++) {
                    JSONObject admireUser = admireArray.getJSONObject(i);
                    SimpleItemData simpleItemData = new SimpleItemData(null, admireUser.optInt("userid"), admireUser.optString("headurl"));
                    simpleItemData.setSubId(admireUser.optInt("orgid"));
                    itemDataList.add(simpleItemData);
                }
                detailBean.setUserAdmire(itemDataList);
                return detailBean;
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

    /**
     * @param videoId
     * @param pageIdx
     * @return
     */
    public static CommentListWithInfo getCommentList(int videoId, int pageIdx) {
        if (pageIdx < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        builder.add("pageSize", String.valueOf(30));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.VIDEO_COMMENT)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getCommentList:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                CommentListWithInfo commentWithInfo = new CommentListWithInfo();
                JSONObject commentlistObj = object.optJSONObject("commentlist");
                JSONObject pagingObj = commentlistObj.optJSONObject("paging");

                commentWithInfo.setPageNumber(pagingObj.optInt("pageNumber"));
                commentWithInfo.setTotalPages(pagingObj.optInt("totalPage"));
                commentWithInfo.setTotalRows(pagingObj.optInt("totalRow"));

                JSONArray listArray = pagingObj.optJSONArray("list");
                commentWithInfo.setDataList(getCommentList(listArray));
                return commentWithInfo;
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

    public static List<CommentItem> getCommentList(JSONArray listArray) {
        List<CommentItem> commentList = new ArrayList<>();
        for (int i = 0; i < listArray.length(); i++) {
            JSONObject commentObj = listArray.optJSONObject(i);
            CommentItem comment = new CommentItem();
            comment.setNickName(commentObj.optString("nickname"));
            comment.setCommentCount(commentObj.optInt("commnetrec"));
            comment.setContent(commentObj.optString("content"));
            comment.setDays(commentObj.optString("days"));
            comment.setPhotoUrl(commentObj.optString("headurl"));
            comment.setTime(commentObj.optString("times"));
            comment.setUserId(commentObj.optInt("userid"));
            comment.setOrgId(commentObj.optInt("orgid"));
            commentList.add(comment);
        }
        return commentList;
    }

    /**
     * @param videoId
     * @param currCount
     */
    public static int updateWatchCount(int videoId, int currCount) {
        if (videoId < 0) {
            return currCount;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        builder.add("currpageView", String.valueOf(currCount));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.WATCH_COUNT)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "updateWatchCount:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                return object.optInt("pageView");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currCount;
    }

    /**
     * @param watcherUserId
     * @param ownerUserId
     */
    public static SimpleResponse attention(int watcherUserId, int ownerUserId) {
        if (watcherUserId < 0 || ownerUserId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userid", String.valueOf(watcherUserId));
        builder.add("attuserid", String.valueOf(ownerUserId));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.ATTENTION_DANCER)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "attention:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param videoId
     * @param watcherUserId
     * @param ownerUserId
     * @return 带有消息的返回类型
     */
    public static SimpleResponse likeWithMsg(int videoId, int watcherUserId, int ownerUserId) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        if (watcherUserId > 0) {
            builder.add("userid", String.valueOf(watcherUserId));
        }
        builder.add("attuserid", String.valueOf(ownerUserId));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.VIDEO_LIKE)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            SimpleResponse simpleResponse = new SimpleResponse();
            LogUtil.w(TAG, "like:" + json);
            JSONObject object = new JSONObject(json);
            simpleResponse.setContentInt(object.optInt("thumbNum"));
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param videoId
     * @param watcherUserId
     */
    public static int like(int videoId, int watcherUserId, int ownerUserId) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        if (watcherUserId > 0) {
            builder.add("userid", String.valueOf(watcherUserId));
        }
        builder.add("attuserid", String.valueOf(ownerUserId));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.VIDEO_LIKE)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "like:" + json);
            JSONObject object = new JSONObject(json);
            if (object.optString("code").equals("0")) {
                return object.optInt("thumbNum");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param videoId
     * @param watcherUserId
     * @param cause
     */
    public static void forward(int videoId, int watcherUserId, int ownerUserId, String cause) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        if (watcherUserId > 0) {
            builder.add("userid", String.valueOf(watcherUserId));
        }
        builder.add("attuserid", String.valueOf(ownerUserId));
        builder.add("cause", cause);
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.VIDEO_FORWARD)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "forward:" + json);
            JSONObject object = new JSONObject(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param videoId
     * @param watcherUserId
     * @param content
     */
    public static boolean sendComment(int videoId, int watcherUserId, int ownerUserId, String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        if (watcherUserId > 0) {
            builder.add("userid", String.valueOf(watcherUserId));
        }
        builder.add("attuserid", String.valueOf(ownerUserId));
        builder.add("content", content);
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.VIDEO_COMMENT_SEND)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "sendComment:" + json);
            JSONObject object = new JSONObject(json);
            return object.optString("code").equals("0");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param videoId
     * @param watcherUserId
     * @param quantity
     */
    public static void tips(int videoId, int watcherUserId, int ownerUserId, float quantity) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("videoid", String.valueOf(videoId));
        if (watcherUserId > 0) {
            builder.add("userid", String.valueOf(watcherUserId));
        }
        builder.add("attuserid", String.valueOf(ownerUserId));
        builder.add("money", String.format("%.2f", quantity));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(
                UrlManager.UrlName.VIDEO_TIPS)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "tips:" + json);
            JSONObject object = new JSONObject(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加举报信息
     *
     * @param watcherUserId
     * @param ownerUserId
     * @param contentType
     * @param contentId
     * @return
     */
    public static SimpleResponse addReport(int watcherUserId, int ownerUserId, int contentType, int contentId) {
        if (contentType < 0 && contentId < 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("reportid", String.valueOf(watcherUserId)),
                new OkHttpClientManager.Param("reportedid", String.valueOf(ownerUserId)),
                new OkHttpClientManager.Param("contentType", String.valueOf(contentType)),
                new OkHttpClientManager.Param("contentid", String.valueOf(contentId))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.REPORT_ADDREPORT), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(jsonObject.optInt("code"));
            simpleResponse.setMessage(jsonObject.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除视频
     *
     * @param videoId
     * @return
     */
    public static SimpleResponse deleteVideoWork(int videoId) {
        if (videoId < 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("videoid", String.valueOf(videoId))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_DELETE_VIDEO_WORK), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(jsonObject.optInt("code"));
            simpleResponse.setMessage(jsonObject.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
