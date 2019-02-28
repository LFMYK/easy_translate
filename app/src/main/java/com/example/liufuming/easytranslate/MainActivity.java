package com.example.liufuming.easytranslate;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    //private MediaPlayer mediaPlayer; // 媒体播放器

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
/*
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            initMediaPlayer();
        }
*/
        /*
        try {
            mediaPlayer.setDataSource(MainActivity.this, Uri.parse("http://res.iciba.com/resource/amp3/oxford/0/e8/7d/e87dcdd7986213c8def8af06571439d9.mp3"));
        }catch (Exception e){
            e.printStackTrace();
        }
*/
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型

        spinnerFrom=findViewById(R.id.spinner_from);


        ArrayAdapter adapterFrom = ArrayAdapter.createFromResource(MainActivity.this,R.array.planets_from,android.R.layout.simple_spinner_dropdown_item );
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapterFrom);


        //System.out.println(spinnerFrom.getSelectedItem());

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

                //Log.d(TAG,"哈哈");
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
/*
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer();
                }else{
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
*/






    public void request() {

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        // 步骤5:创建 网络请求接口 的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        //对 发送请求 进行封装
        Call<Translation> call = request.getCall(this.stringSelectFrom,this.stringSelectTo,editTextOriginalText.getText().toString());//editTextOriginalText.getText().toString()

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
    }
}
