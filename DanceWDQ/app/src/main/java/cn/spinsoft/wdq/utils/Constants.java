package cn.spinsoft.wdq.utils;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by hushujun on 15/11/9.
 */
public interface Constants {
    interface Strings {
        String VIDEO_BEAN = "video_bean";
        String VIDEO_ID = "video_id";
        String OWNER_ID = "ownerId";

        String USER_ID = "user_id";
        String USER_OPENID = "user_openid";
        String USER_PHOTO = "user_photo";
        String USER_BALANCE = "user_balance";
        String USER_NAME = "user_name";
        String USER_PHONE = "user_phone";
        String USER_SIGN = "user_signature";
        String USER_ATTEN = "user_attention";
        String USER_MOBILE = "user_mobile";
        String USER_LOGIN = "user_login";
        String USER_SEX = "user_sex";
        String USER_TYPE = "user_type";


        String ORG_ID = "orgid";
        String ORG_NAME = "org_name";
        String ORG_LOGO = "org_logo";
        String ORG_SIGN = "org_signature";
        String ORG_COURSE_ID = "course_id";
        String ORG_ATTACH = "org_attach";

        String FROM_USERID = "from_userId";
        String FROM_WHO = "from_who";
        String TO_USERID = "to_userId";

        String PICTURE_URLS = "picture_urls";
        String PICTURE_URL = "picture_url";
        String PICTURE_DESC = "picture_desc";
        String PICTURE_POSITION = "picture_position";

        String PAGE_TYPE = "page_type";
        String TYPE_VIDEO = "视频";
        String TYPE_FRIEND = "舞友";
        String TYPE_ORGANIZATION = "培训";
        String TYPE_DISCOVER = "发现";

        String VIDEO_PATH = "video_path";
        String VIDEO_INFO = "video_info";

        String DISCOVER_TYPE = "discover_type";

        String PUBLISH_INFO = "publish_info";
        String PUBLISH_VIDEO = "publish_video";
        String LOCATION_PARAM = "location_param";

        String DISCOVER_TYPE_ID = "discover_typeId";
        String DISCOVER_EVENT_ID = "discover_eventId";

        String APPID_QCLOUND = "10009733";
        String QCLOUD_BUCKET = "wudaoquan";

        String GAODE_KEY = "e6538d7bb13528f49e66c2673e805854";
        String YM_APP_KEY = "56d7d18d67e58e95fc000d51";
        //QQ
        String QQ_APP_ID = "1105051506";
        String QQ_APP_KEY = "DhM3xNJQKZIPLnnO";
        //微信
        String WX_APP_ID = "wxbcc2a84def34b7d3";
        String WX_APP_SECRET = "6b610c18f1a082b966c27c167b286e30";
        String WX_APP_KEY = "wudaoqwudaoqwudaoqwudaoqwudaoq15";
        String WX_APP_PARTENERID = "1300748201";
        //即时聊天NIM
        String IM_APP_KEY = "d98543098a06f9c4c5d03b97b339df93";
        String IM_APP_SECRET = "ed9e4b04b5aa";


        String SIMPLE_INPUT_TILE = "simple_input_title";
        String SIMPLE_INPUT_RESULT = "simple_input_result";
        String SIMPLE_INPUT_LIMIT = "simple_input_limit";
        String SIMPLE_INPUT_LINE = "simple_input_line";
        String SIMPLE_INPUT_TYPE = "simple_input_type";
        String SIMPLE_INPUT_PREVIOUS = "simple_input_previous";

        String SIMPLE_LIST_TYPE = "simple_list_type";

        //打赏方式
        String REWARD_WAY_DICE = "reward_way_dice";
        String REWARD_WAY_WX = "reward_way_wx";

        //列表的模式
        String DELETE_MODE = "delete_mode";//删除
        String NORMAL_MODE = "normal_mode";
        String EDIT_MODE = "edit_mode";//编辑

        //选择的图片类型
        String CHOOSE_IMG_NORMAL = "choose_img_normal";
        String CHOOSE_IMG_POS = "choose_img_pos";

        String SEARCH_ADAPTER = "search_adapter";
        String NORMAL_ADAPTER = "normal_adapter";

        String UPDATE_URI_YYB = "http://a.app.qq.com/o/simple.jsp?pkgname=cn.spinsoft.wdq";
    }

    interface Ints {
        int REQUEST_CODE_VIDEO_CAPTURE = 0;
        int REQUEST_CODE_VIDEO_CHOOSE = 1;
        //        int REQUEST_CODE_VIDEO_INFO = 2;
        int REQUEST_CODE_ITEM_STATUS = 3;
        int REQUEST_CODE_MINE_EDITED = 4;
        int REQUEST_CODE_IMAGE_CHOOSE = 5;
        int REQUEST_CODE_IMAGE_NOCLIP_CHOOSE = 17;
        int REQUEST_CODE_IMAGE_CAPTURE = 6;
        int REQUEST_CODE_IMAGE_CLIPPED = 7;
        int REQUEST_CODE_BOOK_SUCCESS = 8;

        int REQUEST_CODE_NICKNAME = 9;
        int REQUEST_CODE_SIGNATURE = 10;
        int REQUEST_CODE_CONTACT = 11;
        int REQUEST_CODE_ORGNAME = 12;
        int REQUEST_CODE_RECRUIT = 13;
        int REQUEST_CODE_DESC = 14;
        int REQUEST_CODE_ATTACH = 15;
        int REQUEST_CODE_BALANCE = 16;
        int REQUEST_CODE_ADDRESS = 18;
        int REQUEST_CODE_COURSE = 19;
        int REQUEST_CODE_USER_DETAIL = 30;
        int REQUEST_CODE_WORK_DESC = 31;
        int REQUEST_CODE_IMAGE_NOCLIP_CAPTURE = 32;
        //定位时间间隔
        int LOCATION_INTERVAL_SHORT = 100000;//10秒
        int LOCATION_INTERVAL = 30000;//30秒
        int LOCATION_INTERVAL_LONG = 10 * 60 * 1000;//10分钟

        int PERMISSION_CAMERA = 0;
        int PERMISSION_LOCATION = 1;
        int PERMISSION_CALL_PHONE = 2;

        int RELATETYPE_VIDE_MIN = 18;
        int RELATETYPE_VIDE_MAX = 21;
    }

    interface Arrays {
        SHARE_MEDIA[] SHARDBOARD = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE};//分享的平台
    }
}
