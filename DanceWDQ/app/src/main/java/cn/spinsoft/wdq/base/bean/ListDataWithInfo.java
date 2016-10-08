package cn.spinsoft.wdq.base.bean;

import java.util.List;

import cn.spinsoft.wdq.bean.SimpleResponse;

/**
 * Created by hushujun on 15/12/3.
 */
public abstract class ListDataWithInfo<T> {
    protected int pageNumber;
    protected int totalPages;
    protected List<T> dataList;
    protected SimpleResponse response;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public SimpleResponse getResponse() {
        return response;
    }

    public void setResponse(SimpleResponse response) {
        this.response = response;
    }
}
