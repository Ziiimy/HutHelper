package com.gaop.huthelper.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.gaop.huthelper.DB.DBHelper;
import com.gaop.huthelperdao.Lesson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by gaop1 on 2016/7/15.
 */
public class CommUtil {

    /**
     * 获取日期周数
     */
    public static String getData() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        //return new StringBuilder().append(mYear).append(".").append(mMonth).append(".").append(mDay).append("  星期").append(mWay);
        return "第" + DateUtil.getNowWeek() + "周  星期" + mWay;
    }
    /**
     * 返回下一节课
     *
     * @param
     * @return
     */
    public static String getNextClass(Context context) {
        if(!PrefUtil.getBoolean(context,"isLoadCourseTable",false)){
            return "";
        }
        final Calendar c = Calendar.getInstance();
        int mWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (mWeek == 0) {
            mWeek = 7;
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int num;
        if(hour>=0&&hour<8)
            num=1;
        else if(hour>=8&&hour<10)
            num=3;
        else if(hour>=10&&hour<14)
            num=5;
        else if(hour>=14&&hour<16)
            num=7;
        else if(hour>=16&&hour<19)
            num=9;
        else
            return "今天没课了";

        List<Lesson> courseList = DBHelper.getLessonByWeek(String.valueOf(mWeek));

        HashMap<Integer,Lesson> LessonMap=new HashMap<>();
        for (Lesson l:courseList) {
            if(CommUtil.ifHaveCourse(l,DateUtil.getNowWeek())){
                if(l.getDjj().equals(num))
                    return "第"+num+(num+1)+"节"+l.getName()+" "+l.getRoom();
                else
                    LessonMap.put(l.getDjj(),l);
            }
        }
        do {
            num+=2;
            if(num>9)
                return "今天没课了";
            if(LessonMap.get(num)!=null){
                return "第"+num+(num+1)+"节"+LessonMap.get(num).getName()+"　"+LessonMap.get(num).getRoom();
            }
        }while (num<9);

        return "";
    }


    /**
     * 判断课程有无
     * @param lesson 课程
     * @param currWeek  查询的周
     * @return
     */

    public static boolean ifHaveCourse(Lesson lesson, int currWeek) {
        if(currWeek>=lesson.getQsz()&&currWeek<=lesson.getJsz()){
            if("单".equals(lesson.getDsz())&&currWeek%2==0){
                return false;
            }
            if("双".equals(lesson.getDsz())&&currWeek%2!=0){
                return false;
            }
            return true;
        }
        return false;
    }


    /***
     * MD5加码 生成32位md5码
     */
    public static String getMD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 隐藏软键盘
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideSoftInput(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 压缩图片
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>500) { //循环判断如果压缩后图片是否大于1M,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
