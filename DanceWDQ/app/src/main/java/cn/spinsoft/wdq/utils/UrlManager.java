package cn.spinsoft.wdq.utils;

/**
 * Created by hushujun on 15/11/4.
 */
public class UrlManager {

    private enum UrlHost {
//        HOST("http://192.168.0.123:8080/front"), HOST2("http://192.168.0.168:8080/front"),
//        HOST3("http://192.168.0.254:8888/front"), HOST4("http://115.159.5.169:8080/front"), UNKNOWN("");

        HOST("http://115.159.5.169/front"), HOST2("http://app.wudaoquan.net/front"),
        HOST3("http://192.168.0.253:80/front"), HOST4("http://192.168.0.253:8888/front"), UNKNOWN("");

        private String host;

        UrlHost(String host) {
            this.host = host;
        }

        @Override
        public String toString() {
            return host;
        }
    }

    public enum UrlName {
        START_PAGE(UrlHost.HOST, "/registercontroller/getStartPage"),
        REGISTER(UrlHost.HOST, "/registercontroller/register"),
        REPLACE_PWD(UrlHost.HOST, "/registercontroller/updatePassword"),
        UPDATE_PWD(UrlHost.HOST, "/registercontroller/updatePassword2"),
        VERIFY_CODE(UrlHost.HOST, "/registercontroller/getVerificationCode"),
        VERIFY_CODE_UPDATE(UrlHost.HOST, "/registercontroller/getVCodeOfUpdatePassword"),
        LOGIN(UrlHost.HOST, "/registercontroller/login"),
        REPORT_LOCATION(UrlHost.HOST, "/registercontroller/updateUserLocation"),

        ATTENTION_DANCER(UrlHost.HOST, "/video/focusRec"),

        VIDEO_LIST(UrlHost.HOST, "/video/getVideoList"),
        VIDEO_ADS(UrlHost.HOST, "/video/advantisedImg"),
        VIDEO_DETAIL(UrlHost.HOST, "/video/toVideoDetail"),
        WATCH_COUNT(UrlHost.HOST, "/video/updatePageView"),
        VIDEO_COMMENT_SEND(UrlHost.HOST, "/video/sendCommon"),
        VIDEO_LIKE(UrlHost.HOST, "/video/addThumb"),
        VIDEO_FORWARD(UrlHost.HOST, "/video/relay"),
        VIDEO_TIPS(UrlHost.HOST, "/video/admire"),
        VIDEO_COMMENT(UrlHost.HOST, "/video/getCommon"),

        UPLOAD_SIGNATURE(UrlHost.HOST, "/video/getSignaure"),
        UPLOAD_IMAGE(UrlHost.HOST, "/video/uploadImg"),
        SAVE_VIDEO_INFO(UrlHost.HOST, "/video/saveVideoInfo"),
        VIDEO_WX_GOTO_PAGE(UrlHost.HOST2, "/video/details?videoid="),
        VIDEO_SEARCH(UrlHost.HOST, "/video/search"),

        FRIEND_LIST(UrlHost.HOST, "/danceFrinends/getDancefriendsByCondition"),
        FRIEND_SEARCH(UrlHost.HOST, "/danceFrinends/search"),
        DANCE_TYPES(UrlHost.HOST, "/danceFrinends/getDancenames"),

        DISCOVER_LIST(UrlHost.HOST, "/find/getFindList"),
        DISCOVER_DETAIL(UrlHost.HOST, "/find/getDetail"),
        DISCOVER_COMMENT(UrlHost.HOST, "/find/getCommentList"),
        DISCOVER_LIKE(UrlHost.HOST, "/find/addFocus"),
        DISCOVER_COMMENT_SEND(UrlHost.HOST, "/find/addComment"),
        DISCOVER_SIGN_IN(UrlHost.HOST, "/find/saveActApply"),
        DISCOVER_SHARE_GOTO_PAGE(UrlHost.HOST2, "/find/detailPage"),
        DISCOVER_ADD_SHARE(UrlHost.HOST, "/find/addShare"),
        DISCOVER_DELETE_BY_ID(UrlHost.HOST, "/find/deleContent"),
        DISCOVER_SIGN_PERSON(UrlHost.HOST, "/find/getApplyPerson"),

        USER_DETAIL(UrlHost.HOST, "/danceFrinends/getUserInfoByUserid"),
        USER_VIDEOS(UrlHost.HOST, "/homepage/getVideos"),
        USER_DYNAMIC(UrlHost.HOST, "/homepage/getDynamics"),
        USER_ATTENTION(UrlHost.HOST, "/homepage/getAttentions"),
        USER_FANS(UrlHost.HOST, "/homepage/getFans"),

        MINE_INFO_SIMPLE(UrlHost.HOST, "/center"),
        MINE_INFO_DETAIL(UrlHost.HOST, "/center/info"),
        MINE_INFO_UPLOAD_PHOTO(UrlHost.HOST, "/center/uploadHeadImg"),
        MINE_INFO_UPDATE(UrlHost.HOST, "/center/userInfoUpdate"),
        MINE_START(UrlHost.HOST, "/center/myStart"),
        MINE_PUBLISH(UrlHost.HOST, "/relase/relaseMSG"),
        MINE_TEACHER_ATTEST(UrlHost.HOST, "/center/certification"),
        MINE_ATTEST_SUBMIT(UrlHost.HOST, "/center/certificationResult"),
        MINE_PERSON_BOOKING(UrlHost.HOST, "/center/myBooking"),
        MINE_ORG_BOOKING(UrlHost.HOST, "/center/bookingOrder"),
        MINE_CANCEL_BOOKING(UrlHost.HOST, "/center/bookingOperation"),
        MINE_RELATE_ME(UrlHost.HOST, "/center/relatedToMe"),
        MINE_WALLET(UrlHost.HOST, "/center/wallet"),
        MINE_ORG_UPDATE(UrlHost.HOST, "/center/trainInfoUpdate"),
        MINE_ORG_INVITE(UrlHost.HOST, "/center/invitation"),
        MINE_ORG_ATTEST(UrlHost.HOST, "/center/teacherAuth"),
        MINE_ORG_INVITE_SUBMIT(UrlHost.HOST, "/center/startInvitation"),
        MINE_ORG_ATTEST_SUBMIT(UrlHost.HOST, "/center/authResult"),
        MINE_DELETE_VIDEO_WORK(UrlHost.HOST, "/center/removeWork"),
        MINE_ORG_FIRE_TEACHER(UrlHost.HOST, "/center/fire"),
        MINE_CONFIRM_INVITE(UrlHost.HOST, "/center/confirmInvitation"),

        MINE_PRIVATE_MSG(UrlHost.HOST, "/pm"),
        MINE_PRIVATE_MSG_DETAILS(UrlHost.HOST, "/pm/details"),
        MINE_PRIVATE_MSG_SEND(UrlHost.HOST, "/pm/send"),
        MINE_BALANCE(UrlHost.HOST, "/wxPay/getBalance"),
        MINE_WITHDRAW(UrlHost.HOST, "/wxPay/wxKiting"),

        ORG_LIST(UrlHost.HOST, "/train"),
        ORG_DETAILS(UrlHost.HOST, "/train/details"),
        ORG_WORKS(UrlHost.HOST, "/train/works"),
        ORG_COURSE(UrlHost.HOST, "/train/course"),
        ORG_DYNAMIC(UrlHost.HOST, "/train/dynamic"),
        ORG_TEACHER(UrlHost.HOST, "/train/teacher"),
        ORG_COURSE_BOOK_VERIFY(UrlHost.HOST, "/train/getVerificationCode"),
        ORG_COURSE_BOOK_CONFIRM(UrlHost.HOST, "/train/registerOrder"),
        ORG_COURSE_DELETE(UrlHost.HOST, "/train/removeCourse"),
        ORG_COURSE_ADD(UrlHost.HOST, "/train/addCourse"),
        ORG_SEARCH(UrlHost.HOST, "/train/search"),

        WX_ORDER(UrlHost.HOST, "/wxPay/addOrder"),
        DICE_ORDER(UrlHost.HOST, "/wxPay/payByBalance"),
        LOGIN_BY_THIRD(UrlHost.HOST, "/registercontroller/loginByThirdParty"),

        REPORT_ADDREPORT(UrlHost.HOST, "/report/addReport"),

        APP_VERSION(UrlHost.HOST,"/version?type=1"),

        UNKNOWN(UrlHost.UNKNOWN, "");
        private UrlHost host;
        private String path;

        UrlName(UrlHost host, String path) {
            this.host = host;
            this.path = path;
        }

        @Override
        public String toString() {
            return host.toString() + path;
        }
    }

    public static String getUrl(UrlName urlName) {
        return urlName.toString();
    }
}
