package com.virtualtec.kangutest;

/**
 * Created by Android on 2/16/18.
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.Datas.DataUser;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences prefs;
    String prefsIdUser, prefsUser, prefsPassword;

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Actividad Splash");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("Kangu",0);
        prefsIdUser=prefs.getString("idUser", "");

        if (prefsIdUser.isEmpty()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

}