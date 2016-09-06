package com.gaop.huthelper.net;


import android.content.Context;
import android.util.Log;

import com.gaop.huthelper.DB.DBHelper;
import com.gaop.huthelper.Model.DepInfo;
import com.gaop.huthelper.Model.Electric;
import com.gaop.huthelper.Model.Goods;
import com.gaop.huthelper.Model.GoodsListItem;

import com.gaop.huthelper.Model.HttpResult;
import com.gaop.huthelper.Model.MyGoodsItem;
import com.gaop.huthelper.Model.UpdateMsg;
import com.gaop.huthelper.jiekou.AllClassAPI;
import com.gaop.huthelper.jiekou.CourseTableAPI;
import com.gaop.huthelper.jiekou.DeleteGoodAPI;
import com.gaop.huthelper.jiekou.ElectricAPI;
import com.gaop.huthelper.jiekou.FeedBackAPI;
import com.gaop.huthelper.jiekou.FileUploadAPI;
import com.gaop.huthelper.jiekou.GoodListAPI;
import com.gaop.huthelper.jiekou.GoodsAPI;
import com.gaop.huthelper.jiekou.GradeAPI;
import com.gaop.huthelper.jiekou.MyGoodListAPI;
import com.gaop.huthelper.jiekou.UpdateAPI;
import com.gaop.huthelper.jiekou.UserDataAPI;
import com.gaop.huthelper.utils.PrefUtil;
import com.gaop.huthelperdao.CourseGrade;
import com.gaop.huthelperdao.Grade;
import com.gaop.huthelperdao.Lesson;
import com.gaop.huthelperdao.Trem;
import com.gaop.huthelperdao.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 高沛 on 2016/7/24.
 */
public class HttpMethods {
    public static final String BASE_URL = "http://218.75.197.121:8888/";

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

    }


    /**
     * 在访问HttpMethods时创建单例
     */
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    /**
     * 获取单例
     */
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取电费数据
     *
     * @param subscriber
     * @param lou        宿舍楼号码
     * @param hao        寝室号码
     */
    public void getElecticData(Subscriber<Electric> subscriber, String lou, String hao) {
        ElectricAPI electricAPI;
        electricAPI = retrofit.create(ElectricAPI.class);
        electricAPI.electricData(lou, hao)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 分页获取商品列表
     *
     * @param subscriber
     * @param pagenum    页码
     */
    public void getGoodsList(Subscriber<GoodsListItem[]> subscriber, int pagenum) {

        GoodListAPI goodListAPI = retrofit.create(GoodListAPI.class);
        goodListAPI.getGoodsList(pagenum)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 获取我的商品
     * @param subscriber
     * @param num
     * @param rember_code
     */
    public void getMyGoodsList(Subscriber<HttpResult<List<MyGoodsItem>>> subscriber,String num,String rember_code){
        MyGoodListAPI goodListAPI = retrofit.create(MyGoodListAPI.class);
        goodListAPI.getMyGoodsList(num,rember_code)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 删除商品
     * @param subscriber
     * @param num
     * @param rember_code
     * @param id
     */

    public void deleteGoods(Subscriber<HttpResult> subscriber,String num,String rember_code,String id){
        DeleteGoodAPI goodListAPI = retrofit.create( DeleteGoodAPI.class);
        goodListAPI.deleteGoods(num,rember_code,id)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取商品详情
     * @param subscriber
     * @param num
     * @param rember_code
     * @param id
     */
    public void getGoodsContent(Subscriber<HttpResult<Goods>> subscriber,String num,String rember_code,String id){

        GoodsAPI goodsAPI=retrofit.create(GoodsAPI.class);
        goodsAPI.getGoodsContent(num,rember_code,id).
        subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 获取成绩数据
     *
     * @param subscriber
     * @param num        学号
     * @param pass
     */
    public void getGradeData(final Context context, Subscriber<String> subscriber, final String num, final String pass) {
        GradeAPI gradeAPI = retrofit.create(GradeAPI.class);
        gradeAPI.getGrade(num, pass)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<HttpResult<List<CourseGrade>>, String>() {
                    @Override
                    public String call(HttpResult<List<CourseGrade>> studentGrade) {

                        if (studentGrade.getMsg().equals("ok")) {

                            Grade Allgrade = new Grade();
                            float allsf = 0;//所选学分
                            float getsf = 0;//获取学分
                            float cxsf = 0;//重修学分
                            float nopassxf = 0;//未通过学分
                            float allScore = 0;//总分数
                            int noPassNum = 0;//未通过科目
                            float allJd = 0;//总学分绩点  学分绩点=学分*绩点
                            int allNum = 0;//总数目
                            Set<Trem> trim = new HashSet<Trem>();
                            //处理成绩数据并存入数据库
                            for (CourseGrade grade : studentGrade.getData()) {
                                Trem trem = new Trem();
                                trem.setContent(grade.getXN() + "学年第" + grade.getXQ() + "学期");
                                trem.setXN(grade.getXN());
                                trem.setXQ(grade.getXQ());
                                trim.add(trem);
                                if(grade.getCXBJ()==null)
                                    grade.setCXBJ("0");
                                if ("0".equals(grade.getCXBJ())) {
                                    allsf = allsf + Float.valueOf(grade.getXF());
                                    allNum++;
                                }

                                allJd+=Float.valueOf(grade.getXF())*Float.valueOf(grade.getJD());

                                Float g;
                                if (grade.getBKCJ() != null) {
                                    g = Math.max(Float.valueOf(grade.getZSCJ()), Float.valueOf(grade.getBKCJ()));
                                } else {
                                    g = Float.valueOf(grade.getZSCJ());
                                }
                                if (g >= 60) {
                                    getsf = getsf + Float.valueOf(grade.getXF());
                                    //allJd += Float.valueOf(grade.getJD());
                                    allScore += g;
                                    if ("1".equals(grade.getCXBJ())) {
                                        cxsf = cxsf + Float.valueOf(grade.getXF());
                                        nopassxf -= Float.valueOf(grade.getXF());
                                        --noPassNum;
                                    }
                                } else {
                                    if ("0".equals(grade.getCXBJ())) {
                                        ++noPassNum;
                                        nopassxf += Float.valueOf(grade.getXF());
                                    }
                                }
                            }

                            Allgrade.setAllsf(allsf);
                            Allgrade.setNoPassNum(noPassNum);
                            Allgrade.setGetsf(getsf);
                            Allgrade.setCxsf(cxsf);
                            Allgrade.setNopassxf(nopassxf);
                            Allgrade.setAllNum(allNum);
                            Allgrade.setAvgScore(allScore / (studentGrade.getData().size() - noPassNum));
                            Allgrade.setAvgJd(allJd / allsf);

                            DBHelper.deleteAllCourseGrade();
                            DBHelper.deleteAllTrem();
                            DBHelper.deleteAllGrade();
                            DBHelper.insertGradeDao(Allgrade);
                            DBHelper.insertCourseGradeList(studentGrade.getData());
                            DBHelper.insertTremList(new ArrayList(trim));
                            PrefUtil.setBoolean(context, "isLoadGrade", true);

                        }
                        return studentGrade.getMsg();
                    }
                })
                .subscribe(subscriber);
    }

    /**
     * 获取课程表
     *
     * @param subscriber
     * @param num        学号
     */

    public void getCourseTable(final Context context, Subscriber<String> subscriber, String num, String code) {
        CourseTableAPI courseTableAPI = retrofit.create(CourseTableAPI.class);
        courseTableAPI.getCourseTable(num, code)
                .map(new Func1<HttpResult<List<Lesson>>, String>() {
                    @Override
                    public String call(HttpResult<List<Lesson>> listHttpResult) {
                        if ("ok".equals(listHttpResult.getMsg())) {
                            DBHelper.deleteAllLesson();
                            DBHelper.insertListLessonDao(listHttpResult.getData());
                            PrefUtil.setBoolean(context, "isLoadCourseTable", true);
                        }
                        return listHttpResult.getMsg();
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 反馈意见
     */
    public void feedBack(Subscriber<String> subscriber, String tel, String content) {
        FeedBackAPI feedBackAPI = retrofit.create(FeedBackAPI.class);
        feedBackAPI.feed(tel, content)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 检查更新
     */
    public void checkUpdate(Subscriber<HttpResult<UpdateMsg>> subscriber) {
        UpdateAPI updateAPI = retrofit.create(UpdateAPI.class);
        updateAPI.getUpdateData()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 获取学生信息
     *
     * @param subscriber
     * @param num        学号
     */
    public void getUserData(final Context context, Subscriber<String> subscriber, String num, String pass) {
        final UserDataAPI userDataAPI = retrofit.create(UserDataAPI.class);
        userDataAPI.getUserData(num, pass)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<HttpResult<User>, String>() {
                    @Override
                    public String call(HttpResult<User> userHttpResult) {
                        if ("ok".equals(userHttpResult.getMsg())) {
                            userHttpResult.getData().setRember_code(userHttpResult.getRemember_code_app());
                            DBHelper.deleteAllUser();
                            DBHelper.insertUserDao(userHttpResult.getData());
                            PrefUtil.setBoolean(context, "isLoadUser", true);
                            PrefUtil.setBoolean(context, "isLoadCourseTable", false);
                            PrefUtil.setBoolean(context, "isLoadGrade", false);
                        }
                        return userHttpResult.getMsg();
                    }
                })
                .subscribe(subscriber);
    }

    /**
     * 获取所有班级信息
     *
     * @param subscriber
     */
    public void getAllClass(Subscriber<List<DepInfo>> subscriber) {
        AllClassAPI allClassAPI = retrofit.create(AllClassAPI.class);
        allClassAPI.getAllClass()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    public void UploadFile(String des,Map<String, RequestBody> bodyMap){
       // FileUploadAPI fileUploadService=retrofit.create(FileUploadAPI.class);
        //fileUploadService.uploadImage(des,bodyMap).
    }
    public static void uploadMany(ArrayList<String> paths){
        Map<String,RequestBody> photos = new HashMap<>();
        if (paths.size()>0) {
            for (int i=0;i<paths.size();i++) {
                photos.put("photos"+i+"\"; filename=\"icon.png",  RequestBody.create(MediaType.parse("multipart/form-data"), new File(paths.get(i))));
            }
        }
//        Call<String> stringCall = apiManager.uploadImage(desp, photos);
//        stringCall.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Log.d(TAG, "onResponse() called with: " + "call = [" + call + "], response = [" + response + "]");
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.d(TAG, "onFailure() called with: " + "call = [" + call + "], t = [" + t + "]");
//            }
//        });
    }



}
