package com.boom.aiobrowser.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {
//    private static final String TAG = "Logger";

    public static void writeLog(Context context, String message) {
        // 获取应用私有目录的日志文件路径
//        File logFile = new File(context.getExternalFilesDir(null), "app_log.txt");
//        File logFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "app_pay_log_temp.txt");

//        File logFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ad_time1.txt");
//
//        try {
//            // 如果文件不存在则创建它
//            if (!logFile.exists()) {
//                logFile.createNewFile();
//            }
//
//            // 打开文件流，向文件追加内容
//            FileOutputStream fos = new FileOutputStream(logFile, true);
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//
//            // 写入日志信息
//            osw.append(message);
//            osw.append("\n");
//
//            // 关闭流
//            osw.close();
//            fos.close();
//        } catch (IOException e) {
//            Log.e(TAG, "Error writing log to file", e);
//        }
    }
}
