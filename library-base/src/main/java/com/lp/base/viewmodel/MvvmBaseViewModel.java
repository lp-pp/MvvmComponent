package com.lp.base.viewmodel;

import androidx.lifecycle.ViewModel;

import com.lp.base.model.SuperBaseModel;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Description: viewModel
 * <p>
 * 管理V M
 *
 * @author: lp
 * @date: 2020/12/30
 * @version 1.0.0
 */
public abstract class MvvmBaseViewModel<V, M extends SuperBaseModel> extends ViewModel implements IMvvmBaseViewModel<V> {

    private Reference<V> mViewRef;
    protected M mMode;

    @Override
    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view);
    }

    @Override
    public V getView() {
        if (mViewRef == null) return null;
        if (mViewRef.get() != null) {
            return mViewRef.get();
        }
        return null;
    }

    @Override
    public boolean isViewAttach() {
        return mViewRef != null && mViewRef.get() != null;
    }

    @Override
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        if (mMode != null) {
            mMode.cancel();
        }
    }

    protected abstract void initModel();
}
