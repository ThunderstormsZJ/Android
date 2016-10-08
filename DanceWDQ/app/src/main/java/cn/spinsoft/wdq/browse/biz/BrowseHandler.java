package cn.spinsoft.wdq.browse.biz;

import android.os.Message;

import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseHandler;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.discover.biz.DiscoverListWithInfo;
import cn.spinsoft.wdq.discover.biz.DiscoverParser;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.biz.UserInfoSimple;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.user.biz.DancerListWithInfo;
import cn.spinsoft.wdq.user.biz.UserParser;
import cn.spinsoft.wdq.utils.ThreadManager;
import cn.spinsoft.wdq.video.biz.AdvertisementInfo;
import cn.spinsoft.wdq.video.biz.DanceVideoListWithInfo;
import cn.spinsoft.wdq.video.biz.DetailParser;

/**
 * Created by zhoujun on 15/12/1.
 */
public class BrowseHandler extends BaseHandler {
    private static final String TAG = BrowseHandler.class.getSimpleName();
    public static final int CHILD_HOST = 1 << 0;
    public static final int CHILD_VIDEO = 1 << 1;
    public static final int CHILD_FRIEND = 1 << 2;
    public static final int CHILD_ORG = 1 << 3;
    public static final int CHILD_DISCOVER = 1 << 4;
    public static final int CHILD_PERSONAL = 1 << 5;

    private double[] location;

    private Runnable getVideoListRun() {
        return new Runnable() {
            @Override
            public void run() {
                DanceVideoListWithInfo listWithInfo = BrowseParser.getVideoList(Status.Video.pageNum, watcherUserId,
                        Status.Video.danceType, Status.Video.listRank, Status.Video.attention);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_videos_video_list_got);
                msg.obj = listWithInfo;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getAdsRun() {
        return new Runnable() {
            @Override
            public void run() {
                List<AdvertisementInfo> adsList = BrowseParser.getAdsList();
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_videos_ads_got);
                msg.obj = adsList;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getVideoAttentionRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DetailParser.attention(watcherUserId, Status.Video.ownerUserId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_attention_reported_video);
                msg.obj = success;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getFriendAttRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DetailParser.attention(watcherUserId, Status.Friend.ownerUserId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_friend_att_reported);
                msg.obj = success;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getVideoLikeRun() {
        return new Runnable() {
            @Override
            public void run() {
                int likeCount = DetailParser.like(Status.Video.videoId, watcherUserId, Status.Video.ownerUserId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_like_reported_video);
                msg.arg1 = likeCount;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getVideoForwardRun() {
        return new Runnable() {
            @Override
            public void run() {
                DetailParser.forward(Status.Video.videoId, watcherUserId, Status.Video.ownerUserId,
                        Status.Video.forwardCase);
                BrowseHandler.this.sendEmptyMessage(R.id.msg_forward_reported_video);
            }
        };
    }

    private Runnable getVideoTipsRun() {
        return new Runnable() {
            @Override
            public void run() {
                DetailParser.tips(Status.Video.videoId, watcherUserId, Status.Video.ownerUserId,
                        Status.Video.tipsQuantity);
                BrowseHandler.this.sendEmptyMessage(R.id.msg_video_tips_reported);
            }
        };
    }


    private Runnable getDiscoverListRun() {
        return new Runnable() {
            @Override
            public void run() {
                DiscoverListWithInfo listWithInfo = BrowseParser.getDiscoverList(Status.Discover.pageNum,
                        Status.Discover.typeId, Status.Discover.danceType, Status.Discover.sort, watcherUserId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_discover_list_got);
                msg.obj = listWithInfo;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverByAttRun() {
        return new Runnable() {
            @Override
            public void run() {
                DiscoverListWithInfo listWithInfo = BrowseParser.getDiscoverList(Status.Discover.pageNum,
                        Status.Discover.typeId, Status.Discover.danceType, Status.Discover.sort, watcherUserId, watcherUserId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_discover_att_list_got);
                msg.obj = listWithInfo;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverLikeRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse success = DiscoverParser.like(Status.Discover.typeId, Status.Discover.eventId,
                        watcherUserId, Status.Discover.ownerId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_like_reported_discover);
                msg.obj = success;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDanceTypeRun() {
        return new Runnable() {
            @Override
            public void run() {
                List<SimpleItemData> danceTypeBeanList = UserParser.getDanceTypes();
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_dance_type_got);
                msg.obj = danceTypeBeanList;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getMineInfoSimpleRun() {
        return new Runnable() {
            @Override
            public void run() {
                UserInfoSimple userInfo = MineParser.getUserInfoSimple(watcherUserId, Status.orgId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_mine_info_simple_got);
                msg.obj = userInfo;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getFriendListRun() {
        return new Runnable() {
            @Override
            public void run() {
                DancerListWithInfo listWithInfo = UserParser.getFriendList(Status.Friend.pageIdx,
                        location[0], location[1], Status.Friend.sex, watcherUserId,
                        Status.Friend.ageRange, Status.Friend.danceId, Status.Friend.distance);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_friend_list_got);
                msg.obj = listWithInfo;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getDiscoverAddShareRun() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse simpleResponse = DiscoverParser.discoverAddShare(Status.Discover.eventId, Status.Discover.typeId,
                        watcherUserId, Status.Discover.ownerId, Status.Discover.forwarWay);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_forward_reported_discover);
                msg.obj = simpleResponse;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable deleteDiscoverItemById() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = DiscoverParser.deleteDiscoverItemById(Status.Discover.eventId, Status.Discover.typeId);
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_discover_by_id_delete);
                msg.obj = response;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getAppVersion() {
        return new Runnable() {
            @Override
            public void run() {
                SimpleResponse response = BrowseParser.getAppVersion();
                Message msg = BrowseHandler.this.obtainMessage(R.id.msg_app_version_got);
                msg.obj = response;
                BrowseHandler.this.sendMessage(msg);
            }
        };
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_discover_get_list:
                ThreadManager.exectueSingleTask(getDiscoverListRun());
                break;
            case R.id.msg_discover_att_get_list:
                ThreadManager.exectueSingleTask(getDiscoverByAttRun());
                break;
            case R.id.msg_get_dance_type:
                ThreadManager.exectueSingleTask(getDanceTypeRun());
                break;
            case R.id.msg_mine_get_info_simple:
                ThreadManager.exectueSingleTask(getMineInfoSimpleRun());
                break;
            case R.id.msg_videos_get_video_list:
                ThreadManager.exectueSingleTask(getVideoListRun());
                break;
            case R.id.msg_videos_get_ads:
                ThreadManager.exectueSingleTask(getAdsRun());
                break;
            case R.id.msg_report_attention_video:
                ThreadManager.exectueSingleTask(getVideoAttentionRun());
                break;
            case R.id.msg_report_like_video:
                ThreadManager.exectueSingleTask(getVideoLikeRun());
                break;
            case R.id.msg_report_forward_video:
                ThreadManager.exectueSingleTask(getVideoForwardRun());
                break;
            case R.id.msg_video_report_tips:
                ThreadManager.exectueSingleTask(getVideoTipsRun());
                break;
            case R.id.msg_friend_get_list:
                location = LocationOnMain.getInstance().getLocation();
                ThreadManager.exectueSingleTask(getFriendListRun());
                break;
            case R.id.msg_report_like_discover:
                ThreadManager.exectueSingleTask(getDiscoverLikeRun());
                break;
            case R.id.msg_report_forward_discover:
                ThreadManager.exectueSingleTask(getDiscoverAddShareRun());
                break;
            case R.id.msg_report_friend_att:
                ThreadManager.exectueSingleTask(getFriendAttRun());
                break;
            case R.id.msg_discover_delete_by_id:
                ThreadManager.exectueSingleTask(deleteDiscoverItemById());
                break;
            case R.id.msg_get_app_version:
                ThreadManager.exectueSingleTask(getAppVersion());
                break;
            case R.id.msg_app_version_got:
                handleCallbackMessage(CHILD_HOST, msg);
                break;
            case R.id.msg_attention_reported_video:
            case R.id.msg_forward_reported_video:
            case R.id.msg_like_reported_video:
            case R.id.msg_video_tips_reported:
            case R.id.msg_videos_ads_got:
            case R.id.msg_videos_video_list_got:
                handleCallbackMessage(CHILD_VIDEO, msg);
                break;
            case R.id.msg_mine_info_simple_got:
                handleCallbackMessage(CHILD_PERSONAL, msg);
                break;
            case R.id.msg_friend_list_got:
            case R.id.msg_friend_att_reported:
            case R.id.msg_dance_type_got:
                handleCallbackMessage(CHILD_FRIEND, msg);
            case R.id.msg_forward_reported_discover:
            case R.id.msg_like_reported_discover:
            case R.id.msg_discover_list_got:
            case R.id.msg_discover_att_list_got:
            case R.id.msg_discover_by_id_delete:
                handleCallbackMessage(CHILD_DISCOVER, msg);
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

        public static void release() {
            orgId = -1;
            Video.release();
            Friend.release();
            Discover.release();
        }

        public static class Video {
            public static int videoId = -1;
            public static int ownerUserId = -1;
            public static int attention = 0;

            public static float tipsQuantity = 0;
            public static String forwardCase = "";

            public static int pageNum = 1;
            public static int maxPage = 1;
            public static int danceType = -1;
            public static int listRank = 0;

            private static void release() {
                videoId = -1;
                ownerUserId = -1;
                attention = 0;
                tipsQuantity = 0;
                forwardCase = "";
                pageNum = 1;
                maxPage = 1;
                danceType = -1;
                listRank = 0;
            }
        }

        public static class Friend {
            public static int pageIdx = 1;
            public static int sex = 0;
            public static int ownerUserId = -1;
            public static int ageRange = -1;
            public static int danceId = -1;
            public static int distance = -1;

            private static void release() {
                pageIdx = 1;
                sex = 0;
                ownerUserId = -1;
                ageRange = -1;
                danceId = -1;
                distance = -1;
            }
        }

        public static class Discover {
            public static int pageNum = 1;
            public static int danceType = -1;
            public static int sort = 0;
            public static int typeId = 1;
            public static int eventId = -1;
            public static int ownerId = -1;
            public static String forwarWay = "";

            private static void release() {
                pageNum = 1;
                danceType = -1;
                sort = 0;
                typeId = 1;
                eventId = -1;
                ownerId = -1;
                forwarWay = "";
            }
        }
    }
}
