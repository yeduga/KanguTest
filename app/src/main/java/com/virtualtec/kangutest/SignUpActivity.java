package com.virtualtec.kangutest;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataUser;
import com.virtualtec.kangutest.RestServiceApi.RestService;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignUpActivity extends AppCompatActivity {

    private AwesomeValidation awesomeValidation;
    private Retrofit retrofit;
    private RestService service;
    private Call<DataUser> dataUser;
    @BindView(R.id.coordinator_signup) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.relative_body_signup) RelativeLayout relativeBody;
    @BindView(R.id.linear_loading_signup) LinearLayout linearLoading;
    @BindView(R.id.edit_text_user) TextInputEditText editTextUser;
    @BindView(R.id.edit_text_email) TextInputEditText editTextEmail;
    @BindView(R.id.edit_text_password) TextInputEditText editTextPass;
    @BindView(R.id.edit_text_password_confirm) TextInputEditText editTextPassConfirm;
    @BindView(R.id.loading_signup) GifView gifLoading;
    @BindView(R.id.button_back_signup) Button btnBack;
    @BindView(R.id.button_signup) Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);
        Init();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Actividad Registrarse");
    }

    private void Init() {
        getSupportActionBar().hide();
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        // Metodo
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.edit_text_user, "^[A-Za-z0-9_-]{3,15}$", R.string.usercharacterserror);
        awesomeValidation.addValidation(this, R.id.edit_text_user, ".{3,15}$", R.string.limitusererror);
        awesomeValidation.addValidation(this, R.id.edit_text_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.edit_text_password, ".{8,}$", R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.edit_text_password_confirm, R.id.edit_text_password, R.string.passworconfirmderror);
    }

    private void submitForm() {
        // Primero validar formulario y luego avanzar
        // Si es true es porque significa que la validacion es exitosa
        if (awesomeValidation.validate()) {
            functSignUp();
        }
    }

    private void functSignUp(){
        Loading(0);
        dataUser = service.SignUp("create_customer", "", "", editTextUser.getText().toString(), editTextPass.getText().toString(), "", "", "", "", "", editTextEmail.getText().toString(), "", "");
        dataUser.enqueue(new Callback<DataUser>() {
            @Override
            public void onResponse(Call<DataUser> call, Response<DataUser> response) {
                DataUser user = response.body();
                if (user.getResponse() != null){
                    Loading(1);
                    String error = user.getResponse();
                    switch (error){
                        case "1": Snackbar.make(coordinatorLayout, Html.fromHtml("El correo <b>"+editTextEmail.getText().toString()+"</b> ya existe en nuestra plataforma."), Snackbar.LENGTH_LONG).show();
                            break;
                        case "2": Snackbar.make(coordinatorLayout, Html.fromHtml("El usuario <b>"+editTextUser.getText().toString()+"</b> ya existe en nuestra plataforma."), Snackbar.LENGTH_LONG).show();
                            break;
                        case "3": Snackbar.make(coordinatorLayout, getResources().getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Usuario "+editTextUser.getText().toString()+" registrado exitosamente ", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DataUser> call, Throwable t) {
                Loading(1);
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void Loading(int status) {
        if (status == 0){
            relativeBody.animate().alpha(0.0f);
            // Imagen Gif cargando
            linearLoading.animate().alpha(1.0f);
            // input Disabled
            editTextUser.setEnabled(false); editTextEmail.setEnabled(false); editTextPass.setEnabled(false); editTextPassConfirm.setEnabled(false);
            // Buttons Disabled
            btnSignUp.setEnabled(false); btnBack.setEnabled(false);
        }else{
            relativeBody.animate().alpha(1.0f);
            linearLoading.animate().alpha(0.0f);
            // input Disabled
            editTextUser.setEnabled(true); editTextEmail.setEnabled(true); editTextPass.setEnabled(true); editTextPassConfirm.setEnabled(true);
            // Buttons Disabled
            btnSignUp.setEnabled(true); btnBack.setEnabled(true);
        }
    }

    //Submit
    @OnClick(R.id.button_signup)
    public void Submit(){
        submitForm();
    }

    // Atras
    @OnClick(R.id.button_back_signup)
    public void Back(){
        finish();
    }
}
