package cn.spinsoft.wdq.mine.biz;

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

import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.biz.BrowseParser;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.enums.AgeRange;
import cn.spinsoft.wdq.enums.AttestState;
import cn.spinsoft.wdq.enums.BookingState;
import cn.spinsoft.wdq.enums.MoneyFlow;
import cn.spinsoft.wdq.enums.Sex;
import cn.spinsoft.wdq.enums.UserType;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.OkHttpClientManager.ResultCallback;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;
import cn.spinsoft.wdq.utils.StringUtils;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * Created by hushujun on 15/12/7.
 */
public class MineParser {
    private static final String TAG = MineParser.class.getSimpleName();

    public static UserInfoSimple getUserInfoSimple(int userId, int orgId) {
        if (userId < 0 && orgId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (userId >= 0) {
            builder.add("id", String.valueOf(userId));
        }
        if (orgId >= 0) {
            builder.add("trainId", String.valueOf(orgId));
        }
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.MINE_INFO_SIMPLE))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getUserInfoSimple:" + json);
            JSONObject object = new JSONObject(json);
            UserInfoSimple userInfo = new UserInfoSimple();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            userInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                JSONObject infoObj = object.optJSONObject("info");
                if (infoObj != null) {
                    userInfo.setSignature(infoObj.optString("signature"));
                    userInfo.setPhotoUrl(infoObj.optString("headurl"));
                    userInfo.setNickName(infoObj.optString("name"));
                    userInfo.setUserId(infoObj.optInt("userid"));
                    userInfo.setOrgId(infoObj.optInt("orgid"));
                    return userInfo;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserInfoDetail getUserInfoDetail(int userId, int orgId) {
        if (userId < 0 && orgId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (orgId > 0) {
            builder.add("trainId", String.valueOf(orgId));
        } else {
            builder.add("id", String.valueOf(userId));
        }
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.MINE_INFO_DETAIL))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getUserInfoDetail:" + json);
            JSONObject object = new JSONObject(json);
            UserInfoDetail userInfo = new UserInfoDetail();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            userInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                userInfo.setType(UserType.getEnum(object.optString("type")));

                JSONObject infoObj = object.optJSONObject("info");
                userInfo.setSignature(infoObj.optString("signature"));
                userInfo.setSex(Sex.getEnum(infoObj.optInt("sex")));
                userInfo.setPhotoUrl(infoObj.optString("headurl"));

                JSONArray danceArr = infoObj.optJSONArray("dancenames");
                List<SimpleItemData> danceList = new ArrayList<>();
                for (int i = 0; i < danceArr.length(); i++) {
                    JSONObject danceObj = danceArr.optJSONObject(i);
                    SimpleItemData itemData = new SimpleItemData();
                    itemData.setId(danceObj.optInt("danceid"));
                    itemData.setName(danceObj.optString("dancename"));
                    danceList.add(itemData);
                }
                userInfo.setDances(danceList);

                userInfo.setUserId(infoObj.optInt("userid"));
                userInfo.setTelephone(infoObj.optString("telphone"));
                userInfo.setNickName(infoObj.optString("name"));
                userInfo.setContactWay(infoObj.optString("contactway"));
                userInfo.setVisible(infoObj.optInt("iffind") == 1);
                userInfo.setAgeRange(AgeRange.getEnum(infoObj.optInt("agerange")));
                userInfo.setTall(infoObj.optString("height"));

                userInfo.setOrgIntro(infoObj.optString("intro"));
                userInfo.setOrgAddress(infoObj.optString("address"));
                userInfo.setOrgId(infoObj.optInt("orgid"));
                JSONArray attachArr = infoObj.optJSONArray("attachments");
                if (attachArr != null) {
                    List<String> attachList = new ArrayList<>();
                    for (int i = 0; i < attachArr.length(); i++) {
                        attachList.add(attachArr.optString(i));
                    }
                    userInfo.setAttachments(attachList);
                }
                return userInfo;
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

    public static SimpleResponse updateUserInfo(UserInfoDetail infoDetail) {
        if (infoDetail == null) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userId", String.valueOf(infoDetail.getUserId())),
                new OkHttpClientManager.Param("nickname", infoDetail.getNickName()),
                new OkHttpClientManager.Param("signature", StringUtils.getNoNullString(infoDetail.getSignature())),
                new OkHttpClientManager.Param("height", StringUtils.getNoNullString(infoDetail.getTall())),
                new OkHttpClientManager.Param("agerange", String.valueOf(infoDetail.getAgeRange().getValue())),
                new OkHttpClientManager.Param("sex", String.valueOf(infoDetail.getSex().getValue())),
                new OkHttpClientManager.Param("telphone", StringUtils.getNoNullString(infoDetail.getContactWay())),
                new OkHttpClientManager.Param("contactway", infoDetail.getContactWay()),
                new OkHttpClientManager.Param("danceid", StringUtils.getNoNullString(SimpleItemDataUtils.listIdToString(infoDetail.getDances()))),
                new OkHttpClientManager.Param("iffind", String.valueOf(infoDetail.isVisible() ? 1 : 0)),
                new OkHttpClientManager.Param("headurl", infoDetail.getPhotoUrl())
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_INFO_UPDATE), params);
            String json = response.body().string();
            LogUtil.w(TAG, "updateUserInfo:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse updateOrgInfo(UserInfoDetail detail) {
        if (detail == null) {
            return null;
        }
       /* String[] attachments = new String[3];
        List<String> strings = detail.getAttachments();
        if (strings != null && !strings.isEmpty()) {
            for (int i = 0; i < strings.size(); i++) {
                attachments[i] = strings.get(i);
            }
        }*/
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("trainId", String.valueOf(detail.getOrgId())),
                new OkHttpClientManager.Param("orgname", StringUtils.getNoNullString(detail.getNickName())),
                new OkHttpClientManager.Param("intro", StringUtils.getNoNullString(detail.getOrgIntro())),
                new OkHttpClientManager.Param("agegrades", String.valueOf(detail.getAgeRange().getValue())),
                new OkHttpClientManager.Param("address", String.valueOf(detail.getOrgAddress())),
                new OkHttpClientManager.Param("mobile", StringUtils.getNoNullString(detail.getContactWay())),
                new OkHttpClientManager.Param("danceid", StringUtils.getNoNullString(SimpleItemDataUtils.
                        listIdToString(detail.getDances()))),
                new OkHttpClientManager.Param("headurl", StringUtils.getNoNullString(detail.getPhotoUrl())),
                new OkHttpClientManager.Param("attachment1", StringUtils.getNoNullString(detail.getAttachments().get(0))),
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ORG_UPDATE), params);
            String json = response.body().string();
            LogUtil.w(TAG, "updateOrgInfo:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DiscoverListWithInfo getMineStartList(int watcherId, int pageIdx, int type) {
        if (watcherId < 0 || pageIdx < 1 || type < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userId", String.valueOf(watcherId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        builder.add("f_type", String.valueOf(type));
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.MINE_START))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getMineStartList:" + json);
            return BrowseParser.parseDiscoverList(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void publishInfos(PublishInfoBean infoBean, ResultCallback<String> callback) {
        if (infoBean == null) {
            return;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userid", String.valueOf(infoBean.getUserId())),
                new OkHttpClientManager.Param("title", StringUtils.getNoNullString(infoBean.getTitle())),
                new OkHttpClientManager.Param("content", StringUtils.getNoNullString(infoBean.getContent())),
                new OkHttpClientManager.Param("image", StringUtils.listContentToString(infoBean.getImages())),
                new OkHttpClientManager.Param("smallImg", StringUtils.listContentToString(infoBean.getSmallImg())),
                new OkHttpClientManager.Param("location", StringUtils.getNoNullString(infoBean.getLocation())),
                new OkHttpClientManager.Param("f_type", String.valueOf(infoBean.getType().getValue())),
                new OkHttpClientManager.Param("d_type", StringUtils.getNoNullString(infoBean.getDanceType())),
                new OkHttpClientManager.Param("payment", StringUtils.getNoNullString(infoBean.getSalary())),
                new OkHttpClientManager.Param("orgname", StringUtils.getNoNullString(infoBean.getOrgName())),
                new OkHttpClientManager.Param("orgintro", StringUtils.getNoNullString(infoBean.getOrgIntro())),
                new OkHttpClientManager.Param("appEndTime", StringUtils.getNoNullString(infoBean.getAppEndTime())),
                new OkHttpClientManager.Param("startTime", StringUtils.getNoNullString(infoBean.getStartTime())),
                new OkHttpClientManager.Param("endTime", StringUtils.getNoNullString(infoBean.getEndTime())),
                new OkHttpClientManager.Param("poBigImg", StringUtils.getNoNullString(infoBean.getPosBigImg())),
                new OkHttpClientManager.Param("poSmallImg", StringUtils.getNoNullString(infoBean.getPosSmallImg())),
                new OkHttpClientManager.Param("videoImg", StringUtils.getNoNullString(infoBean.getVideoPoster())),
                new OkHttpClientManager.Param("videoUrl", StringUtils.getNoNullString(infoBean.getVideoUri()))
        };
        OkHttpClientManager.getInstance().getPostDelegate()
                .postAsyn(UrlManager.getUrl(UrlManager.UrlName.MINE_PUBLISH), params, callback);
    }

    public static SimpleUserListWithInfo teacherAttest(String watcherId, String orgName, String pageIdx) {
        OkHttpClientManager.Param[] params;
        if (TextUtils.isEmpty(orgName)) {
            params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("pageNumber", pageIdx),
                    new OkHttpClientManager.Param("id", watcherId)
            };
        } else {
            params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("id", watcherId),
                    new OkHttpClientManager.Param("name", orgName),
                    new OkHttpClientManager.Param("pageNumber", pageIdx)
            };
        }
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_TEACHER_ATTEST), params);
            String json = response.body().string();
            return parseSimpleUser(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleUserListWithInfo teacherInvitation(String orgId, String orgName, String pageIdx) {
        OkHttpClientManager.Param[] params;
        if (TextUtils.isEmpty(orgName)) {
            params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("pageNumber", pageIdx),
                    new OkHttpClientManager.Param("trainId", orgId)
            };
        } else {
            params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("trainId", orgId),
                    new OkHttpClientManager.Param("name", orgName),
                    new OkHttpClientManager.Param("pageNumber", pageIdx)
            };
        }
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ORG_INVITE), params);
            String json = response.body().string();
            return parseSimpleUser(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleUserListWithInfo orgTeacherAttest(String orgId, String orgName, String pageIdx) {
        OkHttpClientManager.Param[] params;
        if (TextUtils.isEmpty(orgName)) {
            params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("pageNumber", pageIdx),
                    new OkHttpClientManager.Param("trainId", orgId)
            };
        } else {
            params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("trainId", orgId),
                    new OkHttpClientManager.Param("name", orgName),
                    new OkHttpClientManager.Param("pageNumber", pageIdx)
            };
        }
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ORG_ATTEST), params);
            String json = response.body().string();
            return parseSimpleUser(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SimpleUserListWithInfo parseSimpleUser(String json) throws JSONException {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        LogUtil.i(TAG, "teacherAttest:" + json);
        JSONObject object = new JSONObject(json);
        SimpleUserListWithInfo listWithInfo = new SimpleUserListWithInfo();
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setCode(object.optInt("code"));
        simpleResponse.setMessage(object.optString("msg"));
        listWithInfo.setResponse(simpleResponse);
        if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
            listWithInfo.setTotalPages(object.optInt("totalPage"));
            JSONArray orgArrs = object.optJSONArray("trains");
            if (orgArrs == null) {
                orgArrs = object.optJSONArray("teachers");
            }
            if (orgArrs == null) {
                orgArrs = object.optJSONArray("page");
            }
            if (orgArrs != null) {
                List<SimpleUserBean> infoSimples = new ArrayList<>();
                for (int i = 0; i < orgArrs.length(); i++) {
                    JSONObject infoObj = orgArrs.optJSONObject(i);
                    SimpleUserBean infoSimple = new SimpleUserBean();
                    infoSimple.setPhotoUrl(infoObj.optString("headurl"));
                    if (infoObj.has("orgid")) {
                        infoSimple.setOrgId(infoObj.optInt("orgid"));
                        infoSimple.setUserId(infoObj.optInt("userid"));
                    } else {
                        infoSimple.setUserId(infoObj.optInt("userid"));
                    }
                    if (infoObj.has("orgname")) {
                        infoSimple.setNickName(infoObj.optString("orgname"));
                    } else {
                        infoSimple.setNickName(infoObj.optString("nickname"));
                    }
                    infoSimple.setState(AttestState.getEnum(infoObj.optInt("state")));
                    infoSimple.setRecodeId(infoObj.optInt("id"));
                    infoSimples.add(infoSimple);
                }
                listWithInfo.setDataList(infoSimples);
            }
        }
        return listWithInfo;
    }


    public static SimpleResponse submitTeacherAttest(String watcherId, String orgIds) {
        if (TextUtils.isEmpty(orgIds)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("id", watcherId),
                new OkHttpClientManager.Param("trainIds", orgIds)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ATTEST_SUBMIT), params);
            String json = response.body().string();
            LogUtil.w(TAG, "submitTeacherAttest:" + json);
            SimpleResponse simpleResponse = new SimpleResponse();
            JSONObject object = new JSONObject(json);
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse submitOrgInvitation(String orgId, String userIds) {
        if (TextUtils.isEmpty(userIds)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("trainId", orgId),
                new OkHttpClientManager.Param("userId", userIds)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ORG_INVITE_SUBMIT), params);
            String json = response.body().string();
            LogUtil.w(TAG, "submitOrgInvitation:" + json);
            SimpleResponse simpleResponse = new SimpleResponse();
            JSONObject object = new JSONObject(json);
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage("邀请成功");
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse submitOrgAttest(String orgId, String userId, String optionType) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("trainId", orgId),
                new OkHttpClientManager.Param("id", userId),
                new OkHttpClientManager.Param("type", optionType)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ORG_ATTEST_SUBMIT), params);
            String json = response.body().string();
            LogUtil.w(TAG, "submitOrgAttest:" + json);
            SimpleResponse simpleResponse = new SimpleResponse();
            JSONObject object = new JSONObject(json);
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            simpleResponse.setContentInt(object.getInt("code"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BookingListWithInfo getMineBookingList(int watcherId, int state, int pageIdx) {
        if (watcherId <= 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userId", String.valueOf(watcherId)),
                new OkHttpClientManager.Param("pageNumber", String.valueOf(pageIdx)),
                new OkHttpClientManager.Param("type", String.valueOf(state))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_PERSON_BOOKING), params);
            String json = response.body().string();
            LogUtil.w(TAG, "getMineBookingList:" + json);

            BookingListWithInfo listWithInfo = new BookingListWithInfo();
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setTotalPages(object.optInt("totalPage"));
                JSONArray bookingArr = object.optJSONArray("page");
                if (bookingArr != null) {
                    List<BookingCourseBean> courseBeanList = new ArrayList<>();
                    for (int i = 0; i < bookingArr.length(); i++) {
                        JSONObject bookingObj = bookingArr.optJSONObject(i);
                        BookingCourseBean courseBean = new BookingCourseBean();
                        courseBean.setCourseId(bookingObj.optInt("courseid"));
                        courseBean.setCourseName(bookingObj.optString("coursetitle"));
                        courseBean.setBookId(bookingObj.optInt("id"));
                        courseBean.setOrderTime(bookingObj.optString("ordertime"));
                        courseBean.setOrgId(bookingObj.optInt("orgid"));
                        courseBean.setOrgName(bookingObj.optString("orgname"));
                        courseBean.setState(BookingState.getEnum(bookingObj.optInt("state")));
                        courseBean.setUserId(bookingObj.optInt("userid"));
                        courseBeanList.add(courseBean);
                    }
                    listWithInfo.setDataList(courseBeanList);
                }
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BookingListWithInfo getOrgBookingList(int orgId, int state, int pageIdx) {
        if (orgId <= 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("trainId", String.valueOf(orgId)),
                new OkHttpClientManager.Param("pageNumber", String.valueOf(pageIdx)),
                new OkHttpClientManager.Param("type", String.valueOf(state))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ORG_BOOKING), params);
            String json = response.body().string();
            LogUtil.w(TAG, "getOrgBookingList:" + json);

            BookingListWithInfo listWithInfo = new BookingListWithInfo();
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setTotalPages(object.optInt("totalPage"));
                JSONArray bookingArr = object.optJSONArray("page");
                if (bookingArr != null) {
                    List<BookingCourseBean> courseBeanList = new ArrayList<>();
                    for (int i = 0; i < bookingArr.length(); i++) {
                        JSONObject bookingObj = bookingArr.optJSONObject(i);
                        OrgOrderBean orderBean = new OrgOrderBean();
                        orderBean.setCourseId(bookingObj.optInt("courseid"));
                        orderBean.setCourseName(bookingObj.optString("coursetitle"));
                        orderBean.setBookId(bookingObj.optInt("id"));
                        orderBean.setOrderTime(bookingObj.optString("ordertime"));
                        orderBean.setOrgId(bookingObj.optInt("orgid"));
                        orderBean.setOrgName(bookingObj.optString("orgname"));
                        orderBean.setState(BookingState.getEnum(bookingObj.optInt("state")));
                        orderBean.setUserId(bookingObj.optInt("userid"));
                        orderBean.setCreateTime(bookingObj.optString("createtime"));
                        orderBean.setUserName(bookingObj.optString("name"));
                        orderBean.setUserPhone(bookingObj.optString("mobile"));
                        courseBeanList.add(orderBean);
                    }
                    listWithInfo.setDataList(courseBeanList);
                }
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse bookingOption(int bookingId, int optionState) {
        if (bookingId < 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("id", String.valueOf(bookingId)),
                new OkHttpClientManager.Param("type", String.valueOf(optionState))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_CANCEL_BOOKING), params);
            String json = response.body().string();
            LogUtil.w(TAG, "cancelBooking:" + json);
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

    public static RelatedListWithInfo getRelatedList(int watcherId, int pageIdx) {
        if (watcherId <= 0 || pageIdx < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userId", String.valueOf(watcherId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.MINE_RELATE_ME))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getRelatedList:" + json);
            JSONObject object = new JSONObject(json);
            RelatedListWithInfo listWithInfo = new RelatedListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setTotalPages(object.optInt("totalPage"));
                JSONArray relatedArr = object.optJSONArray("related");
                List<RelatedBean> relatedBeanList = new ArrayList<>();
                for (int i = 0; i < relatedArr.length(); i++) {
                    JSONObject relateObj = relatedArr.optJSONObject(i);
                    RelatedBean relatedBean = new RelatedBean();
                    relatedBean.setCreateTime(relateObj.optString("createtime"));
                    relatedBean.setComment(relateObj.optString("content"));
                    relatedBean.setAnotherPhoto(relateObj.optString("cuhu"));
                    relatedBean.setAnotherName(relateObj.optString("cuname"));
                    relatedBean.setImageUrl(relateObj.optString("image"));
                    relatedBean.setRecodeId(relateObj.optInt("id"));
                    relatedBean.setCurrName(relateObj.optString("nickname"));
                    relatedBean.setRelatedId(relateObj.optInt("objectid"));
                    relatedBean.setCurrUserId(relateObj.optInt("theuserid"));
                    relatedBean.setAnotherUserId(relateObj.optInt("userid"));
                    relatedBean.setCurrName(relateObj.optString("nickname"));
                    relatedBean.setType(relateObj.optInt("type"));
                    relatedBean.setDiscoverType(relateObj.optInt("f_type"));
                    relatedBean.setRelatedContent(relateObj.optString("objcontent"));
                    relatedBean.setRelatedTitle(relateObj.optString("objtitle"));
                    relatedBeanList.add(relatedBean);
                }
                listWithInfo.setDataList(relatedBeanList);
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WalletRecodeListWithInfo getWalletRecode(int watcherId, int pageIdx) {
        if (watcherId < 0) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userId", String.valueOf(watcherId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.MINE_WALLET))
                .post(builder.build()).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getWalletRecode:" + json);
            JSONObject object = new JSONObject(json);
            WalletRecodeListWithInfo listWithInfo = new WalletRecodeListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setTotalPages(object.optInt("totalPage"));
                listWithInfo.setBalance(object.optString("count"));
                JSONArray recodeArr = object.optJSONArray("page");
                List<WalletRecode> recodeList = new ArrayList<>();
                for (int i = 0; i < recodeArr.length(); i++) {
                    JSONObject recodeObj = recodeArr.optJSONObject(i);
                    WalletRecode recode = new WalletRecode();
                    recode.setCreateTime(recodeObj.optString("createtime"));
                    recode.setRemark(recodeObj.optString("remark"));
                    recode.setQuantity(recodeObj.optString("money"));
                    recode.setRecodeId(recodeObj.optInt("id"));
                    recode.setType(MoneyFlow.getEnum(recodeObj.optInt("type")));
                    recode.setTradeName(recodeObj.optString("nickname"));
                    recode.setUserId(recodeObj.optInt("userid"));
                    recodeList.add(recode);
                }
                listWithInfo.setDataList(recodeList);
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateMsgOuterListWithInfo getPrivateMsg(int watcherId, int pageIdx) {
        if (watcherId < 0 || pageIdx < 1) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("pageNumber", String.valueOf(pageIdx)),
                new OkHttpClientManager.Param("userId", String.valueOf(watcherId))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_PRIVATE_MSG), params);
            String json = response.body().string();
            LogUtil.w(TAG, "getPrivateMsg:" + json);
            JSONObject object = new JSONObject(json);
            PrivateMsgOuterListWithInfo listWithInfo = new PrivateMsgOuterListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setTotalPages(object.optInt("totalPage"));
                List<PrivateMsgOuterItem> outerItemList = new ArrayList<>();
                JSONArray msgArr = object.optJSONArray("messages");
                for (int i = 0; i < msgArr.length(); i++) {
                    JSONObject msgObj = msgArr.optJSONObject(i);
                    PrivateMsgOuterItem outerItem = new PrivateMsgOuterItem();
                    outerItem.setFromUserId(msgObj.optInt("fromuserid"));
                    outerItem.setToUserId(msgObj.optInt("touserid"));
                    outerItem.setHeadUrl(msgObj.optString("headurl"));
                    outerItem.setNickName(msgObj.optString("nickname"));
                    outerItem.setTime(msgObj.optString("time"));
                    outerItem.setUnReadCount(msgObj.optInt("unreadcount"));
                    outerItem.setContent(msgObj.optString("content"));
                    outerItemList.add(outerItem);
                }
                listWithInfo.setDataList(outerItemList);
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateMsgInnerListWithInfo getPrivateMsg(int fromUserId, int toUserId, int pageIdx) {
        if (fromUserId <= 0 || toUserId <= 0 || pageIdx < 1) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("pageNumber", String.valueOf(pageIdx)),
                new OkHttpClientManager.Param("toUserId", String.valueOf(toUserId)),
                new OkHttpClientManager.Param("fromUserId", String.valueOf(fromUserId))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_PRIVATE_MSG_DETAILS), params);
            String json = response.body().string();
            LogUtil.w(TAG, "getPrivateMsg:" + json);
            JSONObject object = new JSONObject(json);
            PrivateMsgInnerListWithInfo listWithInfo = new PrivateMsgInnerListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                List<PrivateMsgInnerItem> innerItemList = new ArrayList<>();
                JSONArray msgArr = object.optJSONArray("messages");
                for (int i = 0; i < msgArr.length(); i++) {
                    PrivateMsgInnerItem innerItem = new PrivateMsgInnerItem();
                    JSONObject msgObj = msgArr.optJSONObject(i);
                    innerItem.setCreateTime(msgObj.optString("time"));
                    innerItem.setHeadUrl(msgObj.optString("headurl"));
                    innerItem.setRecodeId(msgObj.optInt("id"));
                    innerItem.setState(msgObj.optInt("state"));
                    innerItem.setUserId(msgObj.optInt("userid"));
                    innerItem.setContent(msgObj.optString("content"));
                    innerItem.setSendBySelf(innerItem.getUserId() == toUserId);
                    innerItemList.add(innerItem);
                }
                listWithInfo.setDataList(innerItemList);
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse sendPrivateMsg(String fromUserId, String toUserId, String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("toUserId", String.valueOf(toUserId)),
                new OkHttpClientManager.Param("fromUserId", String.valueOf(fromUserId)),
                new OkHttpClientManager.Param("content", content)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_PRIVATE_MSG_SEND), params);
            String json = response.body().string();
            LogUtil.w(TAG, "sendPrivateMsg:" + json);
            JSONObject object = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SimpleResponse doWalletWithDraw(String userId, String openId, String money) {
        if (openId == null || userId == null) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userid", userId),
                new OkHttpClientManager.Param("openid", openId),
                new OkHttpClientManager.Param("money", money)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_WITHDRAW), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(jsonObject.optInt("code"));
            simpleResponse.setMessage(jsonObject.optString("errmsg"));
            simpleResponse.setContentString(jsonObject.optString("balance"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    //提交机构教师邀请
    public static SimpleResponse submitInviteConfirm(int relateId) {
        if (relateId <= 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("id", String.valueOf(relateId))
        };

        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_CONFIRM_INVITE), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObj = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(jsonObj.optInt("code"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
