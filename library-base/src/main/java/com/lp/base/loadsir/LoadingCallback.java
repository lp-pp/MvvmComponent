package com.lp.base.loadsir;

import android.content.Context;
import android.view.View;

import com.kingja.loadsir.callback.Callback;
import com.lp.base.R;

/**
 * Description:
 * <p>
 * 加载提示页面
 *
 * @author: lp
 * @date: 2021/1/7
 * @version 1.0.0
 */
public class LoadingCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.base_layout_loading;
    }

    @Override
    public boolean getSuccessVisible() {
        return super.getSuccessVisible();
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        return true;
    }
}
