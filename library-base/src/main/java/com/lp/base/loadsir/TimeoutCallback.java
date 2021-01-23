package com.lp.base.loadsir;

import com.kingja.loadsir.callback.Callback;
import com.lp.base.R;

/**
 * Description:
 * <p>
 * 网络超时
 *
 * @version 1.0.0
 * @author: lp
 * @date: 2021/1/7
 */
public class TimeoutCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.base_layout_timeout;
    }

}
