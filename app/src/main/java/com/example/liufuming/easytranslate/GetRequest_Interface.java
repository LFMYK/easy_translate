package com.example.liufuming.easytranslate;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetRequest_Interface {

    //http://fy.iciba.com/ajax.php?a=fy&f=auto&t=auto&w=hello

    //@GET("ajax.php?a=fy&f=auto&t=auto")
    //Call<Translation> getCall(@Query(value ="w", encoded = true) String w);

    @FormUrlEncoded
    @POST("ajax.php?a=fy")
    //Call<Translation> getCall(@Field("f") String f, @Field("t") String t, @Field("w") String w);
    Observable<Translation> getCall(@Field("f") String f, @Field("t") String t, @Field("w") String w);
}
