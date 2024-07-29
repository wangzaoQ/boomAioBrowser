package pop.basepopup;

import android.os.Message;

/**
 * Created by 大灯泡 on 2019/8/16
 * <p>
 * Description：popup事件
 */
class BasePopupEvent {
    public static final int EVENT_SHOW = 1;
    public static final int EVENT_DISMISS = 2;
    public static final int EVENT_ALIGN_KEYBOARD = 3;


    static Message getMessage(int event) {
        Message msg = Message.obtain();
        msg.what = event;
        return msg;
    }

    public interface EventObserver {
        void onEvent(Message msg);
    }
}
