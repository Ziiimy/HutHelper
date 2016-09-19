package com.gaop.huthelper.jiekou;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by gaop1 on 2016/8/2.
 */
public interface FeedBackAPI {
    @FormUrlEncoded
    @POST("home/msg/0")
    Observable<String> feed(@Field("email") String email,
                                      @Field("content") String content);
}
