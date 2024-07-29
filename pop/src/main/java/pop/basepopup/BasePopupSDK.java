package pop.basepopup;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.Observer;

import java.lang.ref.WeakReference;

/**
 * Created by 大灯泡 on 2019/5/13
 * <p>
 * Description：
 */
public final class BasePopupSDK {

    private static volatile Application mApplicationContext;
    private WeakReference<Activity> mTopActivity;
    private FirstOpenActivityLiveData<Boolean> firstActivityOpenLiveData;

    private static class SingletonHolder {
        private static final BasePopupSDK INSTANCE = new BasePopupSDK();
    }

    void regFirstActivityOpen(Observer<Boolean> observer) {
        if (firstActivityOpenLiveData == null) {
            firstActivityOpenLiveData = new FirstOpenActivityLiveData<>();
        }
        firstActivityOpenLiveData.observeForever(observer);
    }

    private BasePopupSDK() {
    }

    synchronized void init(Context context) {
        if (mApplicationContext != null) return;
        mApplicationContext = (Application) context.getApplicationContext();
        regLifeCallback();
    }

    public Activity getTopActivity() {
        return mTopActivity == null ? null : mTopActivity.get();
    }

    private void regLifeCallback() {
        mApplicationContext.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                recordTopActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                recordTopActivity(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                recordTopActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private void recordTopActivity(Activity act) {
        if (mTopActivity != null) {
            if (mTopActivity.get() == act) {
                return;
            }
            mTopActivity.clear();
        }
        boolean isFirstActivityOpened = mTopActivity == null;
        mTopActivity = new WeakReference<>(act);
        if (isFirstActivityOpened && firstActivityOpenLiveData != null) {
            firstActivityOpenLiveData.setValue(true);
            firstActivityOpenLiveData.clear();
            firstActivityOpenLiveData = null;
        }
    }

    public static BasePopupSDK getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static Application getApplication() {
        return mApplicationContext;
    }
}
