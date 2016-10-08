package cn.spinsoft.wdq.search.biz;

import android.os.Message;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseHandler;
import cn.spinsoft.wdq.browse.biz.BrowseParser;
import cn.spinsoft.wdq.enums.PageType;
import cn.spinsoft.wdq.org.biz.OrgParser;
import cn.spinsoft.wdq.org.biz.OrgSimpleListWithInfo;
import cn.spinsoft.wdq.service.LocationOnMain;
import cn.spinsoft.wdq.user.biz.DancerListWithInfo;
import cn.spinsoft.wdq.user.biz.UserParser;
import cn.spinsoft.wdq.utils.ThreadManager;
import cn.spinsoft.wdq.video.biz.DanceVideoListWithInfo;

/**
 * Created by hushujun on 15/11/18.
 */
public class SearchHandler extends BaseHandler {
    private static final String TAG = SearchHandler.class.getSimpleName();
    public static final int CHILD_HISTORY = 1 << 1;
    public static final int CHILD_RESULT = 1 << 2;

    private double[] location;

    private Runnable getVideoListRun() {
        return new Runnable() {
            @Override
            public void run() {
//                DanceVideoListWithInfo videoListWithInfo = BrowseParser.getVideoList(Status.pageIdx, Status.keyWord);
                DanceVideoListWithInfo videoListWithInfo = BrowseParser.searchVideoList(watcherUserId, Status.keyWord);
                Message msg = SearchHandler.this.obtainMessage(R.id.msg_search_video_list_got);
                msg.obj = videoListWithInfo;
                SearchHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getFriendListRun() {
        return new Runnable() {
            @Override
            public void run() {
                DancerListWithInfo listWithInfo = UserParser.searchFriendList(watcherUserId, Status.keyWord, location[0], location[1]);
                Message msg = SearchHandler.this.obtainMessage(R.id.msg_search_friend_list_got);
                msg.obj = listWithInfo;
                SearchHandler.this.sendMessage(msg);
            }
        };
    }

    private Runnable getOrgListRun() {
        return new Runnable() {
            @Override
            public void run() {
//                OrgSimpleListWithInfo listWithInfo = OrgParser.getOrgList(location[0], location[1],
//                        Status.keyWord, String.valueOf(Status.pageIdx));
                OrgSimpleListWithInfo listWithInfo = OrgParser.searchOrgList(watcherUserId, Status.keyWord,location[0],location[1]);
                Message msg = SearchHandler.this.obtainMessage(R.id.msg_search_org_list_got);
                msg.obj = listWithInfo;
                SearchHandler.this.sendMessage(msg);
            }
        };
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.msg_search_get_video_list:
                ThreadManager.exectueSingleTask(getVideoListRun());
                break;
            case R.id.msg_search_get_friend_list:
                location = LocationOnMain.getInstance().getLocation();
                ThreadManager.exectueSingleTask(getFriendListRun());
                break;
            case R.id.msg_search_get_org_list:
                location = LocationOnMain.getInstance().getLocation();
                ThreadManager.exectueSingleTask(getOrgListRun());
                break;
            case R.id.msg_search_org_list_got:
            case R.id.msg_search_friend_list_got:
            case R.id.msg_search_video_list_got:
                handleCallbackMessage(CHILD_RESULT, msg);
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
        public static int pageIdx = 1;
        public static PageType pageType = PageType.VIDEO;
        public static String keyWord = "";
        public static int userId = -1;

        private static void release() {
            pageIdx = 1;
            pageType = PageType.VIDEO;
            keyWord = "";
            userId = -1;
        }
    }
}
