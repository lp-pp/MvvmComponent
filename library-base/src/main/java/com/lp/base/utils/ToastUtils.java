package com.lp.base.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.royole.widgetsdk.widget.RyToast;

/**
 * Description:  Toast工具类
 * <p>
 *
 * @version 1.0.0
 * @author: lp
 * @date: 2020/12/30
 */
public class ToastUtils {
    private static final String TAG = ToastUtils.class.getSimpleName();

    private static Toast sToast;

    public static void showToast(Context context, int strResid, int duration) {
        if (context == null || strResid <= 0) {
            Log.e(TAG, "showToast() strResid = " + strResid);
            return;
        }
        showToast(context, context.getString(strResid), duration);
    }

    public static void showToast(Context context, String str, int duration) {
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            if (sToast != null) {
                sToast.cancel();
            }
            if (duration <= Toast.LENGTH_SHORT) {
                sToast = RyToast.makeText(context.getApplicationContext(), str, Toast.LENGTH_SHORT);
            } else {
                sToast = RyToast.makeText(context.getApplicationContext(), str, Toast.LENGTH_LONG);
            }
            sToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
