package cn.spinsoft.wdq.utils.session.viewholder;


import com.netease.nim.uikit.session.viewholder.MsgViewHolderText;

import cn.spinsoft.wdq.utils.session.extension.DefaultCustomAttachment;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderDefCustom extends MsgViewHolderText {

    @Override
    protected String getDisplayText() {
        DefaultCustomAttachment attachment = (DefaultCustomAttachment) message.getAttachment();

        return "type: " + attachment.getType() + ", data: " + attachment.getContent();
    }
}
