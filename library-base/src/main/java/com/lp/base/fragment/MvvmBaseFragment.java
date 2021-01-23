package com.lp.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.lp.base.activity.IBaseView;
import com.lp.base.loadsir.ErrorCallback;
import com.lp.base.loadsir.LoadingCallback;
import com.lp.base.utils.ToastUtils;
import com.lp.base.viewmodel.IMvvmBaseViewModel;

/**
 * Description:
 * <p>
 * Fragment抽象基类
 *
 * @version 1.0.0
 * @author: lp
 * @date: 2021/1/7
 */
public abstract class MvvmBaseFragment<V extends ViewDataBinding, VM extends IMvvmBaseViewModel>
        extends Fragment implements IBaseView {
    private static final String TAG = MvvmBaseFragment.class.getSimpleName();

    protected Context mContext;
    protected VM mViewModel;
    protected V mViewDataBinding;
    private LoadService mLoadService;
    private boolean isShowedContent = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        initParameters();
        Log.d(TAG, "onAttach()... ");
    }

    /**
     * 初始化参数
     */
    protected void initParameters() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()... ");
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayouyId(), container, false);
        return mViewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()... ");
        //初始化ViewModel
        initViewModel();
        //实现DataBinding
        performDataBinding();
    }

    private void initViewModel() {
        mViewModel = getViewModel();
        if (mViewModel != null) {
            mViewModel.attachView(this);
        }
    }

    private void performDataBinding() {
        if (getDataBindingVariable() > 0) {
            mViewDataBinding.setVariable(getDataBindingVariable(), mViewModel);
            mViewDataBinding.executePendingBindings();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()... ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewModel != null && mViewModel.isViewAttach()) {
            mViewModel.detachView();
        }
        Log.d(TAG, "onDestroy()... ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()... ");
    }

    @Override
    public void showContent() {
        if (mLoadService != null) {
            isShowedContent = true;
            mLoadService.showSuccess();
        }
    }

    @Override
    public void showLoading() {
        if (mLoadService != null) {
            mLoadService.showCallback(LoadingCallback.class);
        }
    }

    @Override
    public void showEmpty() {
        if (mLoadService != null) {
            mLoadService.showCallback(LoadingCallback.class);
        }
    }

    @Override
    public void showFailure(String msg) {
        if (mLoadService != null) {
            if (!isShowedContent) {
                mLoadService.showCallback(ErrorCallback.class);
            } else {
                ToastUtils.showToast(mContext, msg, Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * 注册LoadSir
     *
     * @param view 替换视图
     */
    public void setLoadSir(View view) {
        if (mLoadService == null) {
            mLoadService = LoadSir.getDefault().register(view, (Callback.OnReloadListener) v -> onRetryBtnClick());
        }
    }

    /**
     * 获取ViewModel
     *
     * @return
     */
    protected abstract VM getViewModel();

    /**
     * 获取layout资源文件
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayouyId();

    /**
     * 获取参数Variable
     *
     * @return
     */
    protected abstract int getDataBindingVariable();

    /**
     * 失败重试，重新加载
     */
    protected abstract void onRetryBtnClick();

}
