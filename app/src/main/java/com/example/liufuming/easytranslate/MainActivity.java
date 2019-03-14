package com.example.liufuming.easytranslate;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private EditText editTextOriginalText;
    private EditText editTextTranslationResult;
    private Button buttonTranslate;

    private String phEnMp3Url="";
    private String phAmMp3Url="";
    private Button buttonPhEnMp3;
    private Button buttonPhAmMp3;


    private Spinner spinnerFrom;
    public String stringSelectFrom="auto";
    private Spinner spinner_to;
    public String stringSelectTo="auto";
    protected TextView textViewSoundmarkEn;
    protected TextView textViewSoundmarkAm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextOriginalText=findViewById(R.id.editText_original_text);
        editTextTranslationResult=findViewById(R.id.editText_translation_result);
        buttonTranslate=findViewById(R.id.button_translate);

        buttonPhEnMp3=findViewById(R.id.button_phEnMp3);
        buttonPhAmMp3=findViewById(R.id.button_phAmMp3);

        textViewSoundmarkEn=findViewById(R.id.textView_soundmark_en);
        textViewSoundmarkAm=findViewById(R.id.textView_soundmark_am);

        spinnerFrom=findViewById(R.id.spinner_from);

        ArrayAdapter adapterFrom = ArrayAdapter.createFromResource(MainActivity.this,R.array.planets_from,android.R.layout.simple_spinner_dropdown_item );
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapterFrom);


        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id) {
                stringSelectFrom=getCode((String)spinnerFrom.getSelectedItem());
                Log.d(TAG,"原文语言编码："+stringSelectFrom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        spinner_to=findViewById(R.id.spinner_to);
        ArrayAdapter adapterTo = ArrayAdapter.createFromResource(MainActivity.this,R.array.planets_to,android.R.layout.simple_spinner_dropdown_item );
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_to.setAdapter(adapterTo);

        spinner_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id) {
                stringSelectTo=getCode((String)spinner_to.getSelectedItem());
                Log.d(TAG,"翻译后语言编码："+stringSelectTo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //加载按钮背景图
        Glide.with(this).load(R.mipmap.sound)
        .into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                buttonPhEnMp3.setBackground(resource);
                buttonPhAmMp3.setBackground(resource);
            }
        });

        //加载文本背景图
        Glide.with(this).load(R.mipmap.pointing_right)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        findViewById(R.id.textView).setBackground(resource);
                    }
                });


        buttonPhEnMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (phEnMp3Url!=null && !"".equals(phEnMp3Url)) {
                        MediaPlayer mediaPlayerTemp = new MediaPlayer();
                        mediaPlayerTemp.setDataSource(phEnMp3Url);
                        mediaPlayerTemp.prepare();
                        mediaPlayerTemp.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


        buttonPhAmMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (phAmMp3Url!=null && !"".equals(phAmMp3Url)) {
                        MediaPlayer mediaPlayerTemp = new MediaPlayer();
                        mediaPlayerTemp.setDataSource(phAmMp3Url);
                        mediaPlayerTemp.prepare();
                        mediaPlayerTemp.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

    }

    protected String getCode(String stringValue) {
        switch(stringValue){
            case "自动":return "auto";
            case "中文":return "zh";
            case "英语":return"en";
            case "日语":return "ja";
            case "韩语":return "ko";
            case "德语":return "de";
            case "西班牙语":return "es";
            case "法语":return "fr";
            default:return "auto";
        }
    }







    public void request() {

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();

        // 步骤5:创建 网络请求接口 的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        //对 发送请求 进行封装
        //Call<Translation> call = request.getCall(this.stringSelectFrom,this.stringSelectTo,editTextOriginalText.getText().toString());
        Observable<Translation> observableCall = request.getCall(this.stringSelectFrom,this.stringSelectTo,editTextOriginalText.getText().toString());


        //步骤6:发送网络请求(异步)
        observableCall.subscribeOn(Schedulers.io())               // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  // 切换回到主线程 处理请求结果
                .subscribe(new Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Translation translation) {
                        // 接收服务器返回的数据
                        // 步骤7：处理返回的数据结果

                        final String string=translation.getContent().getTranslationText();
                        final String phEn=translation.getContent().getPhEn();
                        final String phAm=translation.getContent().getPhAm();

                        phAmMp3Url=translation.getContent().getPhAmMp3();
                        phEnMp3Url=translation.getContent().getPhEnMp3();


                        if (string != null) {
                            editTextTranslationResult.setText(string);

                            if (phEn != null && !"".equals(phEn)) {
                                textViewSoundmarkEn.setText("英[" + phEn + "]");
                                textViewSoundmarkEn.setVisibility(View.VISIBLE);
                                buttonPhEnMp3.setVisibility(View.VISIBLE);


                            }else{
                                textViewSoundmarkEn.setVisibility(View.GONE);
                                buttonPhEnMp3.setVisibility(View.GONE);
                            }


                            if (phAm != null && !"".equals(phAm)) {
                                textViewSoundmarkAm.setText("美[" + phAm + "]");
                                textViewSoundmarkAm.setVisibility(View.VISIBLE);
                                buttonPhAmMp3.setVisibility(View.VISIBLE);
                            }else{
                                textViewSoundmarkAm.setVisibility(View.GONE);
                                buttonPhAmMp3.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,  e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}




/*先屏蔽掉
        Log.d(TAG,"查看URL：");
        Log.d(TAG,call.request().url().toString());



        Log.d(TAG,"查看方法：");
        Log.d(TAG,call.request().method());

        StringBuilder sb = new StringBuilder();
        if (call.request().body() instanceof FormBody) {
            FormBody body = (FormBody) call.request().body();
            for (int i = 0; i < body.size(); i++) {
                sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
            }
            sb.delete(sb.length() - 1, sb.length());
            Log.d(TAG, "RequestParams:{"+sb.toString()+"}");
        }

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<Translation>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                // 步骤7：处理返回的数据结果

                final String string=response.body().getContent().getTranslationText();
                final String phEn=response.body().getContent().getPhEn();
                final String phAm=response.body().getContent().getPhAm();

                phAmMp3Url=response.body().getContent().getPhAmMp3();
                phEnMp3Url=response.body().getContent().getPhEnMp3();
//System.out.println(response.body().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (string != null) {
                            editTextTranslationResult.setText(string);

                            if (phEn != null && !"".equals(phEn)) {
                                textViewSoundmarkEn.setText("英[" + phEn + "]");
                                textViewSoundmarkEn.setVisibility(View.VISIBLE);
                                buttonPhEnMp3.setVisibility(View.VISIBLE);


                            }else{
                                textViewSoundmarkEn.setVisibility(View.GONE);
                                buttonPhEnMp3.setVisibility(View.GONE);
                            }


                            if (phAm != null && !"".equals(phAm)) {
                                textViewSoundmarkAm.setText("美[" + phAm + "]");
                                textViewSoundmarkAm.setVisibility(View.VISIBLE);
                                buttonPhAmMp3.setVisibility(View.VISIBLE);
                            }else{
                                textViewSoundmarkAm.setVisibility(View.GONE);
                                buttonPhAmMp3.setVisibility(View.GONE);
                            }

                        }

                    }
                });
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<Translation> call, Throwable throwable) {
                Log.d(TAG,"连接失败");
            }
        });
先屏蔽掉*/

