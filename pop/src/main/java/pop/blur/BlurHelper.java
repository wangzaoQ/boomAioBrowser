package pop.blur;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.Toast;

import pop.util.PopupUiUtils;
import pop.util.log.PopupLog;

/**
 * Created by 大灯泡 on 2017/12/27.
 * <p>
 * 模糊处理类
 * <p>
 * warn:renderscript即将遗弃，后期将前移到Vulkan，使用GPU更快
 * https://developer.android.com/guide/topics/renderscript/compute?hl=zh-cn#additional-code-samples
 * https://developer.android.com/guide/topics/renderscript/migrate?hl=zh-cn
 */
public class BlurHelper {
    private static final String TAG = "BlurHelper";
    private static long startTime;
    private static volatile RenderScript SCRIPT_INSTANCE;

    static RenderScript getScriptInstance(Context context) {
        if (SCRIPT_INSTANCE == null) {
            synchronized (BlurHelper.class) {
                if (SCRIPT_INSTANCE == null) {
                    SCRIPT_INSTANCE = RenderScript.create(context.getApplicationContext());
                }
            }
        }
        return SCRIPT_INSTANCE;
    }

    public static boolean renderScriptSupported() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static Bitmap blur(Context context, View view, float scaledRatio, float radius) {
        return blur(context, view, scaledRatio, radius, true);
    }

    public static Bitmap blur(Context context, View view, float scaledRatio, float radius, boolean fullScreen) {
        return blur(context,
                view,
                scaledRatio,
                radius,
                fullScreen,
                0,
                0);
    }

    public static Bitmap blur(Context context, View view, float scaledRatio, float radius, boolean fullScreen, int cutoutX, int cutoutY) {
        return blur(context,
                getViewBitmap(view, scaledRatio, fullScreen, cutoutX, cutoutY),
                view.getWidth(),
                view.getHeight(),
                radius);
    }

    public static Bitmap blur(Context context, Bitmap origin, int resultWidth, int resultHeight, float radius) {
        startTime = System.currentTimeMillis();
        if (renderScriptSupported()) {
            PopupLog.i(TAG, "脚本模糊");
            return scriptBlur(context,
                    origin,
                    resultWidth,
                    resultHeight,
                    radius);
        } else {
            PopupLog.i(TAG, "快速模糊");
            return fastBlur(context,
                    origin,
                    resultWidth,
                    resultHeight,
                    radius);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap scriptBlur(Context context, Bitmap origin, int outWidth, int outHeight, float radius) {
        if (origin == null || origin.isRecycled()) return null;
        RenderScript renderScript = getScriptInstance(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, origin);
        Allocation blurOutput = Allocation.createTyped(renderScript, blurInput.getType());

        ScriptIntrinsicBlur blur = null;
        try {
            blur = ScriptIntrinsicBlur.create(renderScript, blurInput.getElement());
        } catch (RSIllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("Unsuported element type")) {
                blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            }
        }

        if (blur == null) {
            PopupLog.e(TAG, "脚本模糊失败，转fastBlur");
            blurInput.destroy();
            blurOutput.destroy();
            return fastBlur(context, origin, outWidth, outHeight, radius);
        }

        blur.setRadius(range(radius, 0, 20));
        blur.setInput(blurInput);
        blur.forEach(blurOutput);
        blurOutput.copyTo(origin);

        //释放
        blurInput.destroy();
        blurOutput.destroy();

        Bitmap result = Bitmap.createScaledBitmap(origin, outWidth, outHeight, true);
        origin.recycle();
        long time = (System.currentTimeMillis() - startTime);
        if (PopupLog.isOpenLog()) {
            toast(context, "模糊用时：【" + time + "ms】");
        }
        PopupLog.i(TAG, "模糊用时：【" + time + "ms】");
        return result;
    }

    public static Bitmap fastBlur(Context context, Bitmap origin, int outWidth, int outHeight, float radius) {
        if (origin == null || origin.isRecycled()) return null;
        origin = FastBlur.doBlur(origin, (int) range(radius, 0, 20), false);
        if (origin == null || origin.isRecycled()) return null;
        origin = Bitmap.createScaledBitmap(origin,
                outWidth,
                outHeight,
                true);
        long time = (System.currentTimeMillis() - startTime);
        if (PopupLog.isOpenLog()) {
            toast(context, "模糊用时：【" + time + "ms】");
        }
        PopupLog.i(TAG, "模糊用时：【" + time + "ms】");
        return origin;
    }

    public static Bitmap getViewBitmap(final View v, boolean fullScreen) {
        return getViewBitmap(v, 1.0f, fullScreen, 0, 0);
    }


    public static Bitmap getViewBitmap(final View v, float scaledRatio, boolean fullScreen, int cutoutX, int cutoutY) {
        if (v == null || v.getWidth() <= 0 || v.getHeight() <= 0) {
            PopupLog.e("getViewBitmap  >>  宽或者高为空");
            return null;
        }
        final int statusBarHeight = PopupUiUtils.getStatusBarHeight();
        Bitmap b;
        PopupLog.i("模糊原始图像分辨率 [" + v.getWidth() + " x " + v.getHeight() + "]");

        try {
            b = Bitmap.createBitmap((int) (v.getWidth() * scaledRatio), (int) (v.getHeight() * scaledRatio), Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            System.gc();
            return null;
        }

        Canvas c = new Canvas(b);
        Matrix matrix = new Matrix();
        matrix.preScale(scaledRatio, scaledRatio);
        c.setMatrix(matrix);
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable == null) {
            c.drawColor(Color.parseColor("#FAFAFA"));
        } else {
            bgDrawable.draw(c);
        }
        if (fullScreen) {
            if (statusBarHeight > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && v.getContext() instanceof Activity) {
                int statusBarColor = ((Activity) v.getContext()).getWindow().getStatusBarColor();
                Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
                p.setColor(statusBarColor);
                Rect rect = new Rect(0, 0, v.getWidth(), statusBarHeight);
                c.drawRect(rect, p);
            }
        }
        v.draw(c);
        PopupLog.i("模糊缩放图像分辨率 [" + b.getWidth() + " x " + b.getHeight() + "]");
        if (cutoutX > 0 || cutoutY > 0) {
            try {
                int cutLeft = (int) (cutoutX * scaledRatio);
                int cutTop = (int) (cutoutY * scaledRatio);
                int cutWidth = b.getWidth() - cutLeft;
                int cutHeight = b.getHeight() - cutTop;
                b = Bitmap.createBitmap(b, cutLeft, cutTop, cutWidth, cutHeight, null, false);
            } catch (Exception e) {
                System.gc();
            }
        }
        return b;
    }


    public static float range(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }


    private static void toast(final Context context, final String msg) {
        if (Looper.myLooper() == null || Looper.myLooper() != Looper.getMainLooper()) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast(context, msg);
                    }
                });
            }
        } else {
            Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

    }
}
