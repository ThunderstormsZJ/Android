package cn.spinsoft.wdq.org.biz;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.enums.Attention;
import cn.spinsoft.wdq.login.biz.LoginParser;
import cn.spinsoft.wdq.org.frag.OrgListFrag;
import cn.spinsoft.wdq.user.biz.DancerListWithInfo;
import cn.spinsoft.wdq.user.biz.UserParser;
import cn.spinsoft.wdq.user.biz.UserVideoWithInfo;
import cn.spinsoft.wdq.utils.LogUtil;
import cn.spinsoft.wdq.utils.OkHttpClientManager;
import cn.spinsoft.wdq.utils.UrlManager;

/**
 * Created by hushujun on 16/1/6.
 */
public class OrgParser {
    private static final String TAG = OrgListFrag.class.getSimpleName();

    public static OrgSimpleListWithInfo searchOrgList(int userId, String name, double longitude, double latitude) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userId", String.valueOf(userId)),
                new OkHttpClientManager.Param("name", name),
                new OkHttpClientManager.Param("longitude", String.valueOf(longitude)),
                new OkHttpClientManager.Param("latitude", String.valueOf(latitude))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate().post(UrlManager.getUrl(UrlManager.UrlName.ORG_SEARCH), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject object = new JSONObject(json);
            OrgSimpleListWithInfo listWithInfo = new OrgSimpleListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                JSONArray listArr = object.optJSONArray("list");
                List<OrgInfo> orgInfoList = new ArrayList<>();
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject infoObj = listArr.optJSONObject(i);
                    OrgInfo simple = new OrgInfo();
                    simple.setAddress(infoObj.optString("address"));
                    simple.setOrgName(infoObj.optString("orgname"));
                    simple.setDistance(infoObj.optDouble("distance"));
                    simple.setPhotoUrl(infoObj.optString("headurl"));
                    simple.setPageViews(infoObj.optInt("pageview"));
                    simple.setOrgId(infoObj.optInt("orgid"));
                    simple.setUserId(infoObj.optInt("userid"));
//                    simple.setTeacherCount(infoObj.getInt("teachercount"));

                    JSONArray labelArr = infoObj.optJSONArray("dancenames");
                    if (labelArr != null && labelArr.length() > 0) {
                        List<String> labelList = new ArrayList<>();
                        for (int j = 0; j < labelArr.length(); j++) {
                            labelList.add(labelArr.optString(j));
                        }
                        simple.setLabels(labelList);
                    }

                    orgInfoList.add(simple);
                }
                listWithInfo.setDataList(orgInfoList);
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static OrgSimpleListWithInfo getOrgList(double longitude, double latitude, String name, String pageIdx) {
        return getOrgList(longitude, latitude, -1, pageIdx, 0, 0, name);
    }

    public static OrgSimpleListWithInfo getOrgList(double longitude, double latitude, double danceType, double pageIdx,
                                                   double popular, double distance) {
        return getOrgList(longitude, latitude, danceType, String.valueOf((int) pageIdx), popular, distance, null);
    }

    private static OrgSimpleListWithInfo getOrgList(double longitude, double latitude, double danceType, String pageIdx,
                                                    double popular, double distance, String name) {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("longitude", String.valueOf(longitude)),
                new OkHttpClientManager.Param("latitude", String.valueOf(latitude)),
                new OkHttpClientManager.Param("type", String.valueOf((int) danceType)),
                new OkHttpClientManager.Param("name", TextUtils.isEmpty(name) ? "" : name),
                new OkHttpClientManager.Param("pageNumber", pageIdx),
                new OkHttpClientManager.Param("popular", String.valueOf((int) popular)),
                new OkHttpClientManager.Param("distance", String.valueOf((int) distance))
        };
        try {
            LogUtil.i(TAG, Arrays.toString(params));
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.ORG_LIST), params);
            String json = response.body().string();
            LogUtil.w(TAG, "getOrgList:" + json);
            JSONObject object = new JSONObject(json);
            OrgSimpleListWithInfo listWithInfo = new OrgSimpleListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);

            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setPageNumber(object.optInt("pageNumber"));
                listWithInfo.setTotalPages(object.optInt("totalPage"));
                JSONArray listArr = object.optJSONArray("list");
                List<OrgInfo> orgInfoList = new ArrayList<>();
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject infoObj = listArr.optJSONObject(i);
                    OrgInfo simple = new OrgInfo();
                    simple.setAddress(infoObj.optString("address"));
                    simple.setOrgName(infoObj.optString("orgname"));
                    simple.setDistance(infoObj.optDouble("distance"));
                    simple.setPhotoUrl(infoObj.optString("headurl"));
                    simple.setPageViews(infoObj.optInt("pageview"));
                    simple.setOrgId(infoObj.optInt("orgid"));
                    simple.setUserId(infoObj.optInt("userid"));
                    simple.setTeacherCount(infoObj.getInt("teachercount"));

                    JSONArray labelArr = infoObj.optJSONArray("dancenames");
                    if (labelArr != null && labelArr.length() > 0) {
                        List<String> labelList = new ArrayList<>();
                        for (int j = 0; j < labelArr.length(); j++) {
                            labelList.add(labelArr.optString(j));
                        }
                        simple.setLabels(labelList);
                    }

                    orgInfoList.add(simple);
                }
                listWithInfo.setDataList(orgInfoList);
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static OrgInfoDetails getOrgDetails(int orgId, int userId) {
        if (orgId < 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("trainId", String.valueOf(orgId)),
                new OkHttpClientManager.Param("userId", String.valueOf(userId))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.ORG_DETAILS), params);
            String json = response.body().string();
            LogUtil.w(TAG, "getOrgDetails:" + json);
            JSONObject object = new JSONObject(json);
            OrgInfoDetails details = new OrgInfoDetails();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            details.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                JSONObject trainObj = object.optJSONObject("train");
                details.setIsAttention(Attention.getEnum(trainObj.optInt("isAttention")));
                details.setWorkCount(trainObj.optInt("workcount"));
                details.setCourseCount(trainObj.optInt("coursecount"));
                details.setDynamicCount(trainObj.optInt("dynamiccount"));
                details.setTeacherCount(trainObj.optInt("teachercount"));
                details.setAddress(trainObj.optString("address"));
                details.setOrgName(trainObj.optString("orgname"));
                details.setSignature(trainObj.optString("signature"));
                details.setIntroduce(trainObj.optString("intro"));
                details.setHeadurl(trainObj.optString("headurl"));
                details.setMobile(trainObj.optString("mobile"));
                details.setOrgid(trainObj.optInt("orgid"));
                details.setUserId(trainObj.optInt("userid"));

                JSONArray imgArr = trainObj.optJSONArray("images");
                Map<String, String> workUrlMap = new HashMap<String, String>();
                for (int i = 0; i < imgArr.length(); i++) {
                    JSONObject imgObj = imgArr.optJSONObject(i);
                    workUrlMap.put(imgObj.optString("videoid"), imgObj.optString("bigimg"));
                }
                details.setWorkUrls(workUrlMap);
            }
            return details;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserVideoWithInfo getVideos(int orgId, int pageIdx) {
        if (orgId < 0 || pageIdx < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("trainId", String.valueOf(orgId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        RequestBody body = builder.build();
        return UserParser.getVideos(UrlManager.getUrl(UrlManager.UrlName.ORG_WORKS), body);
    }

    public static CourseListWithInfo getCourseList(int orgId, int watcherId, int pageIdx) {
        if (orgId < 0 /*|| watcherId < 0*/ || pageIdx < 1) {
            return null;
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("trainId", String.valueOf(orgId));
        builder.add("id", String.valueOf(watcherId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(UrlManager.getUrl(UrlManager.UrlName.ORG_COURSE)).post(body).build();
        try {
            Response response = OkHttpClientManager.getInstance().getGetDelegate().get(request);
            String json = response.body().string();
            LogUtil.w(TAG, "getCourseList:" + json);
            JSONObject object = new JSONObject(json);
            CourseListWithInfo listWithInfo = new CourseListWithInfo();
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(object.optInt("code"));
            simpleResponse.setMessage(object.optString("msg"));
            listWithInfo.setResponse(simpleResponse);
            if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                listWithInfo.setTotalPages(object.optInt("totalPage"));
                JSONArray courseArr = object.optJSONArray("courses");
                List<CourseBean> courseBeanList = new ArrayList<>();
                for (int i = 0; i < courseArr.length(); i++) {
                    JSONObject courseObj = courseArr.optJSONObject(i);
                    CourseBean courseBean = new CourseBean();
                    courseBean.setState(courseObj.optInt("state"));
                    courseBean.setCourseId(courseObj.optInt("courseid"));
                    courseBean.setCourseName(courseObj.optString("coursetitle"));
                    courseBean.setOrgId(courseObj.optInt("orgid"));
                    courseBeanList.add(courseBean);
                }
                listWithInfo.setDataList(courseBeanList);
            }
            return listWithInfo;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DiscoverListWithInfo getDynamic(int orgId, int pageIdx) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("trainId", String.valueOf(orgId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        RequestBody body = builder.build();
        return UserParser.getDynamic(UrlManager.getUrl(UrlManager.UrlName.ORG_DYNAMIC), body);
    }

    public static DancerListWithInfo getTeacherList(int orgId, int pageIdx) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("trainId", String.valueOf(orgId));
        builder.add("pageNumber", String.valueOf(pageIdx));
        RequestBody body = builder.build();
        return UserParser.getDancerList(UrlManager.getUrl(UrlManager.UrlName.ORG_TEACHER), body);
    }

    public static SimpleResponse getVerify(String phone) {
        return LoginParser.getVerifyCode(UrlManager.getUrl(UrlManager.UrlName.ORG_COURSE_BOOK_VERIFY), phone);
    }

    public static SimpleResponse bookingConfirm(int watcherId, int courseId, String name, String date,
                                                String phone, String verify) {
        if (watcherId < 0 || courseId < 0 || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(verify)) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userId", String.valueOf(watcherId)),
                new OkHttpClientManager.Param("courseId", String.valueOf(courseId)),
                new OkHttpClientManager.Param("name", name),
                new OkHttpClientManager.Param("date", date),
                new OkHttpClientManager.Param("telphone", phone),
                new OkHttpClientManager.Param("code", verify)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.ORG_COURSE_BOOK_CONFIRM), params);
            String json = response.body().string();
            LogUtil.w(TAG, "bookingConfirm:" + json);
            try {
                JSONObject object = new JSONObject(json);
                SimpleResponse simpleResponse = new SimpleResponse();
                simpleResponse.setCode(object.optInt("code"));
                simpleResponse.setMessage(object.optString("msg"));
                return simpleResponse;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //删除课程
    public static SimpleResponse deleteCourse(int courseId) {
        if (courseId <= 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("id", String.valueOf(courseId))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.ORG_COURSE_DELETE), params);
            String json = response.body().string();
            LogUtil.w(TAG, json);
            JSONObject jsonObject = new JSONObject(json);
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setCode(jsonObject.optInt("code"));
            return simpleResponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //添加课程
    public static SimpleResponse addCourse(int orgId, String title, String detail) {
        if (orgId <= 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("trainId", String.valueOf(orgId)),
                new OkHttpClientManager.Param("title", title),
                new OkHttpClientManager.Param("details", detail)
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.ORG_COURSE_ADD), params);
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
     * 解聘教师
     *
     * @param userId
     * @param orgId
     * @return
     */
    public static SimpleResponse fireTeacher(int userId, int orgId) {
        if (userId < 0) {
            return null;
        }
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("userId", String.valueOf(userId)),
                new OkHttpClientManager.Param("trainId", String.valueOf(orgId))
        };
        try {
            Response response = OkHttpClientManager.getInstance().getPostDelegate()
                    .post(UrlManager.getUrl(UrlManager.UrlName.MINE_ORG_FIRE_TEACHER), params);
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
