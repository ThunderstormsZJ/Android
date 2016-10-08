package cn.spinsoft.wdq.discover.biz;

import android.os.Message;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseHandler;
import cn.spinsoft.wdq.bean.CommentListWithInfo;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.utils.ThreadManager;
import cn.spinsoft.wdq.video.biz.DetailParser;

/**
 * Created by zhoujun on 15/12/9.
 */
public class DiscoverHandler extends BaseHandler {
    private static final String TAG = DiscoverHandler.class.getSimpleName();
    public static final int CHILD_HOST = 1;

    private Runnable getDiscoverDetailRun() {
        return new Runnable() {
            @Override
            public void run() {
                DiscoverDetail detail = DiscoverParser.getDiscoverDetail(Status.typeId, Status.eventId, watcherUserId);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_discover_detail_got);
                msg.obj = detail;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverCommentsRun() {
        return new Runnable() {
            @Override
            public void run() {
                CommentListWithInfo withInfo = DiscoverParser.getDiscoverComment(Status.typeId,
                        Status.eventId, Status.pageIdx);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_discover_comments_got);
                msg.obj = withInfo;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverLikeRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DiscoverParser.like(Status.typeId, Status.eventId, watcherUserId, Status.ownerId);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_like_reported_discover);
                msg.obj = success;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable senDiscoverCommentRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = DiscoverParser.sendComment(Status.typeId, Status.eventId,
                        watcherUserId, Status.ownerId, Status.comment);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_comment_sent_discover);
                msg.obj = response;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable discoverSignInRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = DiscoverParser.discoverSignIn(Status.typeId, Status.eventId,
                        watcherUserId, Status.ownerId, Status.signParams);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_discover_sign_in_reported);
                msg.obj = response;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable deleteDiscoverItemById() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = DiscoverParser.deleteDiscoverItemById(Status.eventId, Status.typeId);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_discover_by_id_delete);
                msg.obj = response;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverAddShareRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DiscoverParser.discoverAddShare(Status.eventId, Status.typeId,
                        watcherUserId, Status.ownerId, Status.forwarWay);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_forward_reported_discover);
                msg.obj = simpleResponse;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable addReport() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DetailParser.addReport(watcherUserId,Status.ownerId,Status.typeId,Status.eventId);
                Message msg = DiscoverHandler.this.obtainMessage(R.id.msg_discover_report_added);
                msg.obj = simpleResponse;
                DiscoverHandler.this.sendMessage(msg);
            }
        };
    }



    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_discover_get_detail:
                ThreadManager.exectueSingleTask(getDiscoverDetailRun());
                break;
            case R.id.msg_discover_get_comments:
                ThreadManager.exectueSingleTask(getDiscoverCommentsRun());
                break;
            case R.id.msg_report_like_discover:
                ThreadManager.exectueSingleTask(getDiscoverLikeRun());
                break;
            case R.id.msg_send_comment_discover:
                ThreadManager.exectueSingleTask(senDiscoverCommentRun());
                break;
            case R.id.msg_discover_report_sign_in:
                ThreadManager.exectueSingleTask(discoverSignInRun());
                break;
            case R.id.msg_discover_delete_by_id:
                ThreadManager.exectueSingleTask(deleteDiscoverItemById());
                break;
            case R.id.msg_report_forward_discover:
                ThreadManager.exectueSingleTask(getDiscoverAddShareRun());
                break;
            case R.id.msg_discover_add_report:
                ThreadManager.exectueSingleTask(addReport());
                break;
            case R.id.msg_forward_reported_discover:
            case R.id.msg_discover_sign_in_reported:
            case R.id.msg_comment_sent_discover:
            case R.id.msg_like_reported_discover:
            case R.id.msg_discover_comments_got:
            case R.id.msg_discover_detail_got:
            case R.id.msg_discover_by_id_delete:
            case R.id.msg_discover_report_added:
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
        public static int typeId = -1;
        public static int eventId = -1;
        public static int ownerId = -1;
        public static int pageIdx = 1;
        public static String comment = null;
        public static String[] signParams = null;
        public static String forwarWay = null;

        private static void release() {
            typeId = -1;
            eventId = -1;
            ownerId = -1;
            pageIdx = 1;
            comment = null;
            signParams = null;
            forwarWay = null;
        }
    }
}
