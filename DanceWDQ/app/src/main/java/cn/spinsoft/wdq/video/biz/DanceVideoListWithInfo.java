package cn.spinsoft.wdq.video.biz;

import java.util.List;

import cn.spinsoft.wdq.base.bean.ListDataWithInfo;
import cn.spinsoft.wdq.bean.SimpleItemData;

/**
 * Created by hushujun on 15/11/5.
 */
public class DanceVideoListWithInfo extends ListDataWithInfo<DanceVideoBean> {
    private List<SimpleItemData> typeList;

    public List<SimpleItemData> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<SimpleItemData> typeList) {
        this.typeList = typeList;
    }
}
