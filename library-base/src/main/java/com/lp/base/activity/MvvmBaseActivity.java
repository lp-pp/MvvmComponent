package com.lp.base.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.lp.base.loadsir.ErrorCallback;
import com.lp.base.loadsir.LoadingCallback;
import com.lp.base.utils.ToastUtils;
import com.lp.base.viewmodel.IMvvmBaseViewModel;

/**
 * Description:
 * <p>
 * Activity抽象基类
 *
 * @version 1.0.0
 * @author: lp
 * @date: 2021/1/7
 */
public abstract class MvvmBaseActivity<V extends ViewDataBinding, VM extends IMvvmBaseViewModel>
        extends AppCompatActivity implements IBaseView {
    private static final String TAG = MvvmBaseActivity.class.getSimpleName();

    protected VM mViewModel;
    protected V mViewDataBinding;
    private LoadService mLoadService;
    private boolean isShowedContent = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayouyId());
        if (mViewModel == null) {
            mViewModel = getViewModel();
        }
        if (getDataBindingVariable() > 0) {
            mViewDataBinding.setVariable(getDataBindingVariable(), mViewModel);
        }
        mViewDataBinding.executePendingBindings();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewModel != null && mViewModel.isViewAttach()) {
            mViewModel.detachView();
        }
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
                ToastUtils.showToast(this, msg, Toast.LENGTH_LONG);
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
