package com.lp.base.activity;

/**
 * Description:
 * <p>
 * 界面UI显示切换
 *
 * @author: lp
 * @date: 2021/1/7
 * @version 1.0.0
 */
public interface IBaseView {

    /**
     * 显示内容
     */
    void showContent();

    /**
     * 显示加载提示
     */
    void showLoading();

    /**
     * 显示空白页面
     */
    void showEmpty();

    /**
     * 显示失败提示
     * @param msg 失败信息
     */
    void showFailure(String msg);
}
