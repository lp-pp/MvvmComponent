package com.lp.base.manager;

import android.app.Activity;

import java.util.Stack;

/**
 * Description:
 * <p>
 * Activity的堆栈管理者
 *
 * @version 1.0.0
 * @author: lp
 * @date: 2020/10/15
 */

public class AppManager {

    private static Stack<Activity> activityStack;

    private static volatile AppManager mAppManager;

    private AppManager() {
    }

    /**
     * 双锁方式获取单例对象
     *
     * @return 返回初始化后的单例对象
     */
    public static AppManager getInstance() {
        if (mAppManager == null) {
            synchronized (AppManager.class) {
                if (mAppManager == null) {
                    mAppManager = new AppManager();
                }
            }
        }
        return mAppManager;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 是否有activity
     */
    public boolean isActivity() {
        if (activityStack != null) {
            return !activityStack.isEmpty();
        }
        return false;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                finishActivity(activityStack.get(i));
            }
        }
        activityStack.clear();
    }

    /**
     * 获取指定的Activity
     *
     * @author 返回指定的Activity对象
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void exitApp() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            activityStack.clear();
            e.printStackTrace();
        }
    }
}
