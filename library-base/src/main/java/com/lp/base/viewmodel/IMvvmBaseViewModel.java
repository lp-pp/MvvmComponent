package com.lp.base.viewmodel;

/**
 * Description: viewModel
 * <p>
 * 定义viewModel与V的关联
 *
 * @author: lp
 * @date: 2020/12/30 
 * @version 1.0.0
 */
public interface IMvvmBaseViewModel<V> {

    /**
     * 关联View
     * @param view
     */
    void attachView(V view);

    /**
     * 获取关联View
     * @return
     */
    V getView();

    /**
     * 是否已关联View
     * @return
     */
    boolean isViewAttach();

    /**
     * 解除关联
     */
    void detachView();

}
