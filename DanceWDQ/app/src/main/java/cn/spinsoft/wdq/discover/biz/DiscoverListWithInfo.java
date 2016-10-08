package cn.spinsoft.wdq.discover.biz;

import cn.spinsoft.wdq.base.bean.ListDataWithInfo;

/**
 * Created by hushujun on 15/11/24.
 */
public class DiscoverListWithInfo extends ListDataWithInfo<DiscoverItemBean> {
    private int totalRows;

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

}
