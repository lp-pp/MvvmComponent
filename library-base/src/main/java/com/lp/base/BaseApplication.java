package com.lp.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lp.base.manager.AppManager;

import java.util.List;

public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();

    private static BaseApplication mInstance;

    private static boolean mIsDebug;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
    }

    /**
     * 当宿主没有继承自该Application时,可以使用set方法进行初始化baseApplication
     *
     * @param application
     */
    private void setApplication(@NonNull BaseApplication application) {
        mInstance = application;
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                AppManager.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                AppManager.getInstance().removeActivity(activity);
            }
        });
    }

    /**
     * 获得当前app运行的Application
     *
     * @return
     */
    public static BaseApplication getInstance() {
        if (mInstance == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication...");
        }
        return mInstance;
    }

    public void setDebug(boolean isDebug) {
        mIsDebug = isDebug;
    }

    public boolean getDebug() {
        return mIsDebug;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public static String getCurrentProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }
}
