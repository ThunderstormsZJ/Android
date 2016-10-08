package cn.spinsoft.wdq.org.biz;

import android.os.Message;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseHandler;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.biz.DiscoverParser;
import cn.spinsoft.wdq.utils.ThreadManager;
import cn.spinsoft.wdq.video.biz.DetailParser;

/**
 * Created by zhoujun on 16/1/25.
 */
public class OrgHandler extends BaseHandler {
    private static final String TAG = OrgHandler.class.getSimpleName();

    public static final int CHILD_HOST = 1 << 0;

    private Runnable getOrgDetails() {
        return new Runnable() {
            @Override
            public void run() {
                OrgInfoDetails orgDetails = OrgParser.getOrgDetails(Status.orgId, watcherUserId);
                Message msg = OrgHandler.this.obtainMessage(R.id.msg_org_detail_got);
                msg.obj = orgDetails;
                OrgHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable reportOrgAtten() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DetailParser.attention(watcherUserId, Status.userId);
                Message msg = OrgHandler.this.obtainMessage(R.id.msg_org_attention_reported);
                msg.obj = simpleResponse;
                OrgHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable addCourseRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = OrgParser.addCourse(Status.orgId, Status.course, "");
                Message msg = OrgHandler.this.obtainMessage(R.id.msg_org_course_added);
                msg.obj = simpleResponse;
                OrgHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable deleteDiscoverItemById() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = DiscoverParser.deleteDiscoverItemById(Status.eventId, Status.typeId);
                Message msg = OrgHandler.this.obtainMessage(R.id.msg_discover_by_id_delete);
                msg.obj = response;
                OrgHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverLikeRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DiscoverParser.like(Status.typeId, Status.eventId,
                        watcherUserId, Status.userId);
                Message msg = OrgHandler.this.obtainMessage(R.id.msg_like_reported_discover);
                msg.obj = success;
                OrgHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverAddShareRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DiscoverParser.discoverAddShare(Status.eventId, Status.typeId,
                        watcherUserId, Status.userId, Status.forwarWay);
                Message msg = OrgHandler.this.obtainMessage(R.id.msg_forward_reported_discover);
                msg.obj = simpleResponse;
                OrgHandler.this.sendMessage(msg);
            }
        };
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_org_get_detail:
                ThreadManager.exectueSingleTask(getOrgDetails());
                break;
            case R.id.msg_org_report_attention:
                ThreadManager.exectueSingleTask(reportOrgAtten());
                break;
            case R.id.msg_org_add_course:
                ThreadManager.exectueSingleTask(addCourseRun());
                break;
            case R.id.msg_discover_delete_by_id:
                ThreadManager.exectueSingleTask(deleteDiscoverItemById());
                break;
            case R.id.msg_report_like_discover:
                ThreadManager.exectueSingleTask(getDiscoverLikeRun());
                break;
            case R.id.msg_report_forward_discover:
                ThreadManager.exectueSingleTask(getDiscoverAddShareRun());
                break;
            case R.id.msg_org_detail_got:
            case R.id.msg_org_course_added:
            case R.id.msg_org_attention_reported:
                handleCallbackMessage(CHILD_HOST, msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void release() {
        super.release();
        Status.release();
    }

    public static class Status {
        public static int orgId = -1;
        public static int typeId = -1;
        public static int eventId = -1;
        public static int userId = -1;
        public static String course = "";
        public static String forwarWay = "";

        private static void release() {
            orgId = -1;
            typeId = -1;
            eventId = -1;
            userId = -1;
            course = "";
            forwarWay = "";
        }
    }
}
