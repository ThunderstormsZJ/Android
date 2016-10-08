package cn.spinsoft.wdq.utils.session.viewholder;


import com.netease.nim.uikit.session.viewholder.MsgViewHolderText;

import cn.spinsoft.wdq.utils.session.extension.GuessAttachment;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderGuess extends MsgViewHolderText {

    @Override
    protected String getDisplayText() {
        GuessAttachment attachment = (GuessAttachment) message.getAttachment();

        return attachment.getValue().getDesc() + "!";
    }
}
