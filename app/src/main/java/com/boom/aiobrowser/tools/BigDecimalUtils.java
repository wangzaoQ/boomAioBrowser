package com.boom.aiobrowser.tools;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @Package: com.cdsg.zgph.module_common.util
 * @ClassName: BigDecimalUtils
 * @Author: wr
 * @CreateDate: 2020/4/18 16:02
 */
public class BigDecimalUtils {

    private static long mOneHundredMillion = 10000_0000;
    private static long billion = 1000_000_000;
    private static long thousand = 1000;
    private static long mTenThousand = 10000;
    private static long million = 1000_000;
    static final int location = 10; //小数点后位数

    //加法 返回 num1+num2
    public static double add(double num1, double num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.add(b2).doubleValue();
    }

    //减法 返回 num1-num2
    public static double sub(double num1, double num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.subtract(b2).doubleValue();
    }
    //减法 返回 num1-num2
    public static double sub(String num1, String num2) {
        if (TextUtils.isEmpty(num1)){
            num1 = "0";
        }
        if (TextUtils.isEmpty(num2)){
            num2 = "0";
        }
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.subtract(b2).doubleValue();
    }

    //乘法 返回 num1*num2
    public static double mul(double num1, double num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        double v = b1.multiply(b2).doubleValue();
        return v;
    }

    //乘法 返回 num1*num2
    public static double mul(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        double v = b1.multiply(b2).doubleValue();
        return v;
    }


    //除法 返回 num1/num2
    public static double div(double num1, double num2) {
        return div(num1, num2, location);
    }
    //除法 返回 num1/num2
    public static double div(String num1, String num2) {
        return div(new BigDecimal(num1).doubleValue(), new BigDecimal(num2).doubleValue(), location);
    }

    //除法 返回num1/num2 自定义小数点后位数
    public static double div(double num1, double num2, int _location) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.divide(b2, _location, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     *
     * @param num
     * @param pattern 转换的格式
     * @return
     */
    public static String saveDecimal(double num, String pattern) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat(pattern);//格式化小数
            String format = decimalFormat.format(num);
            return format;
        } catch (Exception e) {
            return String.valueOf(num);
        }
    }
    /**
     *
     * @param num
     * @param pattern 转换的格式
     * @return
     */
    public static String saveDecimal(String num, String pattern) {
        try {
            if (TextUtils.isEmpty(num)){
                num = "0";
            }
            DecimalFormat decimalFormat = new DecimalFormat(pattern);//格式化小数
            String format = decimalFormat.format(num);
            return format;
        } catch (Exception e) {
            return String.valueOf(num);
        }
    }

    public static int getInt(double number){
        BigDecimal bd=new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return Integer.parseInt(bd.toString());
    }
    public static int getInt(String number){
        if (TextUtils.isEmpty(number) || number.equals("0")){
            return 0;
        }
        BigDecimal bd=new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP);
        return Integer.parseInt(bd.toString());
    }
}
