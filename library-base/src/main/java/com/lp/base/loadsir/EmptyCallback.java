package com.lp.base.loadsir;

import com.kingja.loadsir.callback.Callback;
import com.lp.base.R;

/**
 * Description:
 * <p>
 * 空页面
 *
 * @author: lp
 * @date: 2021/1/7
 * @version 1.0.0
 */
public class EmptyCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.base_layout_empty;
    }
}
