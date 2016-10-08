package cn.spinsoft.wdq.bean;

import cn.spinsoft.wdq.base.bean.ListDataWithInfo;

/**
 * Created by hushujun on 15/11/10.
 */
public class CommentListWithInfo extends ListDataWithInfo<CommentItem> {
    private int totalRows;

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
