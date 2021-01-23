package com.lp.base.loadsir;

import android.content.Context;
import android.view.View;

import com.kingja.loadsir.callback.Callback;
import com.lp.base.R;

/**
 * Description:
 * <p>
 * 骨架屏
 *
 * @version 1.0.0
 * @author: lp
 * @date: 2021/1/7
 */
public class ShimmerCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.base_layout_placeholder;
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        return true;
    }
}
