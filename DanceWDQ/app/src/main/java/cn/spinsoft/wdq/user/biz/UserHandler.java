package cn.spinsoft.wdq.user.biz;

import android.os.Message;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseHandler;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.discover.biz.DiscoverParser;
import cn.spinsoft.wdq.utils.ThreadManager;
import cn.spinsoft.wdq.video.biz.DetailParser;

/**
 * Created by hushujun on 15/12/2.
 */
public class UserHandler extends BaseHandler {
    private static final String TAG = UserHandler.class.getSimpleName();

    public static final int CHILD_HOST = 1 << 0;
    public static final int CHILD_WORKS = 1 << 1;
    public static final int CHILD_DYNAMIC = 1 << 2;
    public static final int CHILD_ATTENTION = 1 << 3;
    public static final int CHILD_FANS = 1 << 4;

    private Runnable getUserDetailRun() {
        return new Runnable() {
            @Override
            public void run() {
                UserDetail userDetail = UserParser.getDetail(Status.userId, watcherUserId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_user_detail_got);
                msg.obj = userDetail;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getUserVideosRun() {
        return new Runnable() {
            @Override
            public void run() {
                UserVideoWithInfo videoWithInfo = UserParser.getVideos(Status.userId, Status.pageIdx_works);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_user_videos_got);
                msg.obj = videoWithInfo;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getUserDynamicRun() {
        return new Runnable() {
            @Override
            public void run() {
                DiscoverListWithInfo dynamicWithInfo = UserParser.getDynamic(Status.userId, Status.pageIdx_dynamic,watcherUserId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_user_dynamic_got);
                msg.obj = dynamicWithInfo;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getUserAttentionRun() {
        return new Runnable() {
            @Override
            public void run() {
                DancerListWithInfo listWithInfo = UserParser.getAttentions(Status.userId, Status.pageIdx_att, watcherUserId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_user_attention_got);
                msg.obj = listWithInfo;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getUserFansRun() {
        return new Runnable() {
            @Override
            public void run() {
                DancerListWithInfo listWithInfo = UserParser.getFans(Status.userId, Status.pageIdx_fans, watcherUserId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_user_fans_got);
                msg.obj = listWithInfo;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable reportAttention() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DetailParser.attention(watcherUserId, Status.userId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_attention_reported_video);
                msg.obj = success;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable deleteDiscoverItemById() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = DiscoverParser.deleteDiscoverItemById(Status.eventId, Status.typeId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_discover_by_id_delete);
                msg.obj = response;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable deleteVideoWorkById() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = DetailParser.deleteVideoWork(Status.videoId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_user_video_deleted);
                msg.obj = response;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverLikeRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DiscoverParser.like(Status.typeId, Status.eventId,
                        watcherUserId, Status.userId);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_like_reported_discover);
                msg.obj = success;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverAddShareRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DiscoverParser.discoverAddShare(Status.eventId, Status.typeId,
                        watcherUserId, Status.userId, Status.forwarWay);
                Message msg = UserHandler.this.obtainMessage(R.id.msg_forward_reported_discover);
                msg.obj = simpleResponse;
                UserHandler.this.sendMessage(msg);
            }
        };
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_user_get_detail:
                ThreadManager.exectueSingleTask(getUserDetailRun());
                break;
            case R.id.msg_user_get_videos:
                ThreadManager.exectueSingleTask(getUserVideosRun());
                break;
            case R.id.msg_user_get_dynamic:
                ThreadManager.exectueSingleTask(getUserDynamicRun());
                break;
            case R.id.msg_user_get_attention:
                ThreadManager.exectueSingleTask(getUserAttentionRun());
                break;
            case R.id.msg_user_get_fans:
                ThreadManager.exectueSingleTask(getUserFansRun());
                break;
            case R.id.msg_report_attention_video:
                ThreadManager.exectueSingleTask(reportAttention());
                break;
            case R.id.msg_discover_delete_by_id:
                ThreadManager.exectueSingleTask(deleteDiscoverItemById());
                break;
            case R.id.msg_user_delete_video:
                ThreadManager.exectueSingleTask(deleteVideoWorkById());
                break;
            case R.id.msg_report_like_discover:
                ThreadManager.exectueSingleTask(getDiscoverLikeRun());
                break;
            case R.id.msg_report_forward_discover:
                ThreadManager.exectueSingleTask(getDiscoverAddShareRun());
                break;
            case R.id.msg_user_fans_got:
                handleCallbackMessage(CHILD_FANS, msg);
                break;
            case R.id.msg_user_attention_got:
                handleCallbackMessage(CHILD_ATTENTION, msg);
                break;
            case R.id.msg_user_dynamic_got:
            case R.id.msg_like_reported_discover:
            case R.id.msg_forward_reported_discover:
                handleCallbackMessage(CHILD_DYNAMIC, msg);
                break;
            case R.id.msg_user_videos_got:
            case R.id.msg_user_video_deleted:
                handleCallbackMessage(CHILD_WORKS, msg);
                break;
            case R.id.msg_attention_reported_video:
            case R.id.msg_user_detail_got:
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
        public static int userId = -1;
        public static int typeId = -1;
        public static int eventId = -1;
        public static int videoId = -1;
        public static int pageIdx_works = 1;
        public static int pageIdx_dynamic = 1;
        public static int pageIdx_att = 1;
        public static int pageIdx_fans = 1;
        public static String forwarWay = "";

        private static void release() {
            userId = -1;
            typeId = -1;
            eventId = -1;
            pageIdx_works = 1;
            pageIdx_dynamic = 1;
            pageIdx_att = 1;
            pageIdx_fans = 1;
            videoId = -1;
            forwarWay = "";
        }
    }
}
