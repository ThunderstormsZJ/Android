package cn.spinsoft.wdq.video.biz;

import android.os.Message;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseHandler;
import cn.spinsoft.wdq.bean.CommentListWithInfo;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.utils.ThreadManager;

/**
 * Created by hushujun on 15/11/9.
 */
public class DetailHandler extends BaseHandler {
    private static final String TAG = DetailHandler.class.getSimpleName();

    public static final int CHILD_TOP = 1 << 0;
    public static final int CHILD_BOTTOM = 1 << 1;
    public static final int MAIN_ACTIVITY = 1 << 2;

    private Runnable getDetailRun() {
        return new Runnable() {
            @Override
            public void run() {
                VideoDetailBean detailBean = DetailParser.getVideoDetail(Status.videoId,
                        watcherUserId, Status.ownerUserId);
                Message msg = DetailHandler.this.obtainMessage(R.id.msg_video_detail_got);
                msg.obj = detailBean;
                DetailHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getCommentListRun() {
        return new Runnable() {
            @Override
            public void run() {
                CommentListWithInfo commentWithInfo = DetailParser.getCommentList(Status.videoId,
                        Status.commentPageIdx);
                Message msg = DetailHandler.this.obtainMessage(R.id.msg_video_comment_list_got);
                msg.obj = commentWithInfo;
                DetailHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getWatchCountRun() {
        return new Runnable() {
            @Override
            public void run() {
                Status.watchCount = DetailParser.updateWatchCount(Status.videoId, Status.watchCount);
                DetailHandler.this.sendEmptyMessage(R.id.msg_video_watch_count_reported);
            }
        };
    }

    private Runnable getSendCommentRun() {
        return new Runnable() {
            @Override
            public void run() {
                boolean success = DetailParser.sendComment(Status.videoId,
                        watcherUserId, Status.ownerUserId, Status.commentContent);
                Message msg = DetailHandler.this.obtainMessage(R.id.msg_comment_sent_video);
                msg.obj = success;
                DetailHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getAttentionRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DetailParser.attention(watcherUserId, Status.ownerUserId);
                Message msg = DetailHandler.this.obtainMessage(R.id.msg_attention_reported_video);
                msg.obj = success;
                DetailHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getLikeRun() {
        return new Runnable() {
            @Override
            public void run() {
                int likeCount = DetailParser.like(Status.videoId, watcherUserId, Status.ownerUserId);
                Message msg = DetailHandler.this.obtainMessage(R.id.msg_like_reported_video);
                msg.arg1 = likeCount;
                DetailHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getLikeWithMsgRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DetailParser.likeWithMsg(Status.videoId, watcherUserId, Status.ownerUserId);
                Message msg = DetailHandler.this.obtainMessage(R.id.msg_like_reported_withMsg_video);
                msg.obj = simpleResponse;
                DetailHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getForwardRun() {
        return new Runnable() {
            @Override
            public void run() {
                DetailParser.forward(Status.videoId, watcherUserId, Status.ownerUserId, Status.forwardCase);
                DetailHandler.this.sendEmptyMessage(R.id.msg_forward_reported_video);
            }
        };
    }

    private Runnable getTipsRun() {
        return new Runnable() {
            @Override
            public void run() {
                DetailParser.tips(Status.videoId, watcherUserId, Status.ownerUserId, Status.tipsQuantity);
                DetailHandler.this.sendEmptyMessage(R.id.msg_video_tips_reported);
            }
        };
    }

    private Runnable addReportRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DetailParser.addReport(watcherUserId, Status.ownerUserId, 6, Status.videoId);
                Message msg = DetailHandler.this.obtainMessage(R.id.msg_video_report_added);
                msg.obj = simpleResponse;
                DetailHandler.this.sendMessage(msg);
            }
        };
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case R.id.msg_video_get_detail:
                ThreadManager.exectueSingleTask(getDetailRun());
                break;
            case R.id.msg_video_get_comment_list:
                ThreadManager.exectueSingleTask(getCommentListRun());
                break;
            case R.id.msg_video_report_watch_count:
                ThreadManager.exectueSingleTask(getWatchCountRun());
                break;
            case R.id.msg_send_comment_video:
                ThreadManager.exectueSingleTask(getSendCommentRun());
                break;
            case R.id.msg_report_attention_video:
                ThreadManager.exectueSingleTask(getAttentionRun());
                break;
            case R.id.msg_report_like_video:
                ThreadManager.exectueSingleTask(getLikeRun());
                break;
            case R.id.msg_report_like_withMsg_video:
                ThreadManager.exectueSingleTask(getLikeWithMsgRun());
                break;
            case R.id.msg_report_forward_video:
                ThreadManager.exectueSingleTask(getForwardRun());
                break;
            case R.id.msg_video_report_tips:
                ThreadManager.exectueSingleTask(getTipsRun());
                break;
            case R.id.msg_video_add_report:
                ThreadManager.exectueSingleTask(addReportRun());
                break;
            case R.id.msg_comment_sent_video:
            case R.id.msg_video_tips_reported:
            case R.id.msg_forward_reported_video:
            case R.id.msg_like_reported_video:
            case R.id.msg_like_reported_withMsg_video:
            case R.id.msg_attention_reported_video:
            case R.id.msg_video_comment_list_got:
            case R.id.msg_video_watch_count_reported:
            case R.id.msg_video_detail_got:
            case R.id.msg_video_report_added:
                handleCallbackMessage(MAIN_ACTIVITY, msg);
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
        public static int videoId = -1;
        public static int ownerUserId = -1;

        public static float tipsQuantity = 0;
        public static String forwardCase = "";
        public static String commentContent = "";
        public static int commentPageIdx = 1;
        public static int watchCount = 0;

        private static void release() {
            videoId = -1;
            ownerUserId = -1;
            tipsQuantity = 0;
            forwardCase = "";
            commentContent = "";
            commentPageIdx = 1;
            watchCount = 0;
        }
    }
}
