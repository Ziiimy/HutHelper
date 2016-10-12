package com.gaop.huthelper.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gaop.huthelper.DB.DBHelper;
import com.gaop.huthelperdao.Lesson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * 公共工具
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
        if (!PrefUtil.getBoolean(context, "isLoadCourseTable", false)) {
            return "";
        }
        final Calendar c = Calendar.getInstance();
        int mWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (mWeek == 0) {
            mWeek = 7;
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int num;
        if (hour >= 0 && hour < 8)
            num = 1;
        else if (hour >= 8 && hour < 10)
            num = 3;
        else if (hour >= 10 && hour < 14)
            num = 5;
        else if (hour >= 14 && hour < 16)
            num = 7;
        else if (hour >= 16 && hour < 19)
            num = 9;
        else
            return "今天没课了";

        List<Lesson> courseList = DBHelper.getLessonByWeek(String.valueOf(mWeek));

        HashMap<Integer, Lesson> LessonMap = new HashMap<>();
        for (Lesson l : courseList) {
            if (CommUtil.ifHaveCourse(l, DateUtil.getNowWeek())) {
                if (l.getDjj().equals(num))
                    return "第" + num + "," + (num + 1) + "节" + l.getName() + " " + l.getRoom();
                else
                    LessonMap.put(l.getDjj(), l);
            }
        }
        do {
            num += 2;
            if (num > 9)
                return "今天没课了";
            if (LessonMap.get(num) != null) {
                return "第" + num + "," + (num + 1) + "节" + LessonMap.get(num).getName() + "　" + LessonMap.get(num).getRoom();
            }
        } while (num < 9);

        return "";
    }

    /**
     * 判断网络
     *
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }


    /**
     * 判断课程有无
     *
     * @param lesson   课程
     * @param currWeek 查询的周
     * @return
     */

    public static boolean ifHaveCourse(Lesson lesson, int currWeek) {
        String[] s = lesson.getIndex().split(" ");

        String curr = String.valueOf(currWeek);
        for (String w : s) {
            if (w.equals(curr)) {
                return true;
            }
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
     *
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


    public static int compressImage(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 90;
            while (baos.toByteArray().length / 1024 > 600) {
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
            baos.close();
            return options;
        } catch (IOException e) {
            e.printStackTrace();
            return 50;
        }
    }

    public static  File uri2File(Activity context,Uri uri) {
        File file = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = context.managedQuery(uri, proj, null,
                null, null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor
                .getString(actual_image_column_index);
        file = new File(img_path);
        return file;
    }

    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        Log.e("ea",filePath);
        return filePath;
    }


    /**
     * 得到指定路径图片的options，不加载内存
     *
     * @param srcPath 源图片路径
     * @return Options {@link BitmapFactory.Options}
     */
    public static BitmapFactory.Options getBitmapOptions(String srcPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        return options;
    }


    /**
     * 压缩指定路径图片，并将其保存在缓存目录中;<br>
     * 通过isDelSrc判定是否删除源文件，并获取到缓存后的图片路径;<br>
     * 图片过大可能OOM
     *
     * @param context
     * @param srcPath
     * @param rqsW
     * @param rqsH
     * @param isDelSrc
     * @return
     */
    public static String compressBitmap(Context context, String srcPath,
                                        Bitmap.CompressFormat format,
                                        int rqsW, int rqsH, boolean isDelSrc) {
        Bitmap bitmap = compressBitmap(srcPath, rqsW, rqsH);
        int num=compressImage(bitmap);
        File srcFile = new File(srcPath);
        String desPath = getImageCacheDir(context) + srcFile.getName();
        clearCropFile(desPath);
        try {
            File file = new File(desPath);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(format, num, fos);
            fos.close();
            if (isDelSrc) srcFile.deleteOnExit();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return desPath;
    }

    /**
     * 删除文件
     *
     * @param uri 文件Uri
     * @return 成功与否
     */
    public static boolean clearCropFile(Uri uri) {
        if (uri == null) {
            return false;
        }
        return clearCropFile(uri.getPath());
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return 成功与否
     */
    public static boolean clearCropFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        //有时文件明明存在  但 file.exists为false

        File file = new File(path);

        //System.out.println("工具判断:"+FileUtils.exists(file)+" 原始判断:"+file.exists()+" \npath:"+file.getPath());

        if (file.exists()) {
            boolean result = file.delete();
            if (result) {
                System.out.println("Cached crop file cleared.");
            } else {
                System.out.println("Failed to clear cached crop file.");
            }
            return result;
        } else {
            System.out.println("Trying to clear cached crop file but it does not exist.");
        }

        return false;
    }

    /**
     * 获取图片缓存路径
     *
     * @param context
     * @return
     */
    private static String getImageCacheDir(Context context) {
        String dir = context.getCacheDir()+ File.separator;
        File file = new File(dir);
        if (!file.exists()) file.mkdirs();
        return dir;
    }

    /**
     * 压缩指定路径的图片，并得到图片对象
     *
     * @param path bitmap source path
     * @return Bitmap {@link Bitmap}
     */
    public static Bitmap compressBitmap(String path, int rqsW, int rqsH) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, rqsW, rqsH);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * calculate the bitmap sampleSize
     *
     * @param options
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int rqsW, int rqsH) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (rqsW == 0 || rqsH == 0) return 1;
        if (height > rqsH || width > rqsW) {
            final int heightRatio = Math.round((float) height / (float) rqsH);
            final int widthRatio = Math.round((float) width / (float) rqsW);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * sha1加密
     *
     * @param decript
     * @return
     */
    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * BASE64 加密
     *
     * @param str
     * @return
     */
    public static String encryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 加密
            return new String(Base64.encode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 6.0以上权限
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

}
