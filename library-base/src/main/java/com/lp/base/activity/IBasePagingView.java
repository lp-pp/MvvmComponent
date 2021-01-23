package com.lp.base.activity;

public interface IBasePagingView extends IBaseView {

    /**
     * 加载更多失败信息
     * @param msg 失败信息
     */
    void onLoadMoreFailure(String msg);

    /**
     * 加载更多空白页面
     */
    void onLoadMoreEmpty();
}
