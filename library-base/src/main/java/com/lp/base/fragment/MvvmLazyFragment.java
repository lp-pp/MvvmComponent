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
import androidx.fragment.app.FragmentManager;

import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.lp.base.activity.IBaseView;
import com.lp.base.loadsir.ErrorCallback;
import com.lp.base.loadsir.LoadingCallback;
import com.lp.base.utils.ToastUtils;
import com.lp.base.viewmodel.IMvvmBaseViewModel;

import java.util.List;

/**
 * Description:
 * <p>配置懒加载的fragment(支持fragment嵌套懒加载)
 *
 *
 * @author: lp
 * @date: 2021/1/14
 * @version 1.0.0
 * @Email: 1049345904@qq.com
 */
public abstract class MvvmLazyFragment <V extends ViewDataBinding, VM extends IMvvmBaseViewModel>
        extends Fragment implements IBaseView {
    private static final String TAG = MvvmLazyFragment.class.getSimpleName();

    protected Context mContext;
    protected VM mViewModel;
    protected V mViewDataBinding;
    private LoadService mLoadService;
    /**
     * 是否显示内容
     */
    private boolean isShowedContent = false;

    /**
     * Fragment生命周期 onAttach -> onCreate -> onCreatedView -> onActivityCreated
     * -> onStart -> onResume -> onPause -> onStop -> onDestroyView -> onDestroy-> onDetach
     *
     * 对于 ViewPager + Fragment 的实现我们需要关注的几个生命周期有：
     * onCreatedView + onActivityCreated + onResume + onPause + onDestroyView
     */

    protected View rootView = null;

    /**
     * 布局是否创建完成
     */
    protected boolean isViewCreated = false;

    /**
     * 当前可见状态
     */
    protected boolean currentVisibleState = false;

    /**
     * 是否第一次可见
     */
    protected boolean mIsFirstVisible = true;

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
        if (rootView == null) {
            mViewDataBinding = DataBindingUtil.inflate(inflater, getLayouyId(), container, false);
            rootView = mViewDataBinding.getRoot();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()... ");
        isViewCreated = true;
        //初始化ViewModel
        initViewModel();
        //实现DataBinding
        performDataBinding();

        if (!isHidden() && getUserVisibleHint()) {
            // 可见状态,进行事件分发
            dispatchUserVisibleHint(true);
        }
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

    /**
     * 统一处理用户可见事件分发
     * @param isVisible
     */
    private void dispatchUserVisibleHint(boolean isVisible) {
        Log.d(TAG, "dispatchUserVisibleHint(), isVisible: " + isVisible);
        // 首先考虑一下fragment嵌套fragment的情况(只考虑2层嵌套)
        if (isVisible && isParentInvisible()) {
            // 父Fragmnet此时不可见,直接return不做处理
            return;
        }
        // 为了代码严谨,如果当前状态与需要设置的状态本来就一致了,就不处理了
        if (currentVisibleState == isVisible) {
            return;
        }
        currentVisibleState = isVisible;
        if (isVisible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                // 第一次可见,进行全局初始化
                onFragmentFirstVisible();
            }
            onFragmentResume();
            // 分发事件给内嵌的Fragment
            dispatchChildVisibleState(true);
        } else {
            onFragmentPause();
            dispatchChildVisibleState(false);
        }
    }

    /**
     * 在双重ViewPager嵌套的情况下，第一次滑到Frgment 嵌套ViewPager(fragment)的场景的时候
     * 此时只会加载外层Fragment的数据，而不会加载内嵌viewPager中的fragment的数据，因此，我们
     * 需要在此增加一个当外层Fragment可见的时候，分发可见事件给自己内嵌的所有Fragment显示
     */
    private void dispatchChildVisibleState(boolean visible) {
        Log.d(TAG, "dispatchChildVisibleState, visible: " + visible);
        FragmentManager fragmentManager = getChildFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (null != fragments) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof MvvmLazyFragment && !fragment.isHidden()
                        && fragment.getUserVisibleHint()) {
                    ((MvvmLazyFragment) fragment).dispatchUserVisibleHint(visible);
                }
            }
        }

    }

    /**
     * 第一次可见,根据业务进行初始化操作
     */
    protected void onFragmentFirstVisible() {
        Log.d(TAG, "onFragmentFirstVisible() 第一次可见");
    }

    /**
     * Fragment真正的Pause,暂停一切网络耗时操作
     */
    protected void onFragmentPause() {
        Log.d(TAG, "onFragmentPause(), 真正的Pause 结束相关操作耗时 ");
    }

    /**
     * Fragment真正的Resume,开始处理网络加载等耗时操作
     */
    protected void onFragmentResume() {
        Log.d(TAG, "onFragmentResume(), 真正的Resume 开始相关操作耗时 ");
    }

    private boolean isParentInvisible() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof MvvmLazyFragment) {
            MvvmLazyFragment fragment = (MvvmLazyFragment) parentFragment;
            return !fragment.isSupportVisible();
        }
        return false;
    }

    private boolean isSupportVisible() {
        return currentVisibleState;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            dispatchUserVisibleHint(false);
        } else {
            dispatchUserVisibleHint(true);
        }
    }

    /**
     * 在滑动或者跳转的过程中，第一次创建fragment的时候均会调用onResume方法
     */
    @Override
    public void onResume() {
        super.onResume();
        // 如果不是第一次可见
        if (!mIsFirstVisible) {
            // 如果此时进行Activity跳转,会将所有的缓存的fragment进行onResume生命周期的重复
            // 只需要对可见的fragment进行加载,
            if (!isHidden() && !currentVisibleState && getUserVisibleHint()) {
                dispatchUserVisibleHint(true);
            }
        }
    }

    /**
     * 只有当当前页面由可见状态转变到不可见状态时,才需要调用dispatchUserVisibleHint.
     * currentVisibleState && getUserVisibleHint() 能够限定是当前可见的Fragment,
     * 当前Fragment包含子Fragment的时候， dispatchUserVisibleHint 内部本身就会通知子Fragment不可见,
     * 子fragment走到这里的时候自身又会调用一遍
     */
    @Override
    public void onPause() {
        super.onPause();
        if (currentVisibleState && getUserVisibleHint()) {
            dispatchUserVisibleHint(false);
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
