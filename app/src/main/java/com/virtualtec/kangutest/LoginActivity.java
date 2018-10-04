package com.virtualtec.kangutest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataUser;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ProgressDialog mDialog;
    @BindView(R.id.button_login_facebook) LoginButton loginButtonFB;

    private AwesomeValidation awesomeValidation;
    SharedPreferences prefs;
    Retrofit retrofit;
    RestService service;
    Call<ArrayList<DataUser>> dataUser;
    @BindView(R.id.coordinator_login) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.edit_text_user) TextInputEditText editTextUser;
    @BindView(R.id.edit_text_password) TextInputEditText editTextPassword;
    @BindView(R.id.loading_login) GifView gifLoading;
    @BindView(R.id.button_login) Button buttonLogin;
    @BindView(R.id.button_signup) Button buttonSignup;
    @BindView(R.id.linear_loading_login) LinearLayout linearLoading;
    @BindView(R.id.relative_body) RelativeLayout relativeBody;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Actividad Inicio de sesión");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        prefs = getSharedPreferences("Kangu", Context.MODE_PRIVATE);

        LoginManager.getInstance().logOut(); // logout default
        Init();
    }

    public void Init() {
        callbackManager = CallbackManager.Factory.create();
        loginButtonFB.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("Recibiendo datos...");
                mDialog.show();

                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        Log.d("Object Facebook", object.toString());
                        getData(object);
                    }
                });

                // Request Graph API
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        if (AccessToken.getCurrentAccessToken() != null){
            Toast.makeText(this, AccessToken.getCurrentAccessToken().getUserId(), Toast.LENGTH_LONG).show();
        }

        getSupportActionBar().hide();
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        // Metodo
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
        // Validation Form
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.edit_text_user, RegexTemplate.NOT_EMPTY, R.string.usererror);
        awesomeValidation.addValidation(this, R.id.edit_text_password, RegexTemplate.NOT_EMPTY, R.string.passwordloginerror);
    }

    private void getData(JSONObject object){
        try{
            URL profile_picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=720&height=720");
            ClickLoginFacebook(object.getString("email"), object.getString("name"), object.getString("id"));
            // Picasso.with(this).load(profile_picture.toString()).into(imgAvatar);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Button Login
    @OnClick(R.id.button_login)
    public void ClickLogin(View view) {
        if (awesomeValidation.validate()){
            Loading(0);
            dataUser = service.Access("login", editTextUser.getText().toString(), editTextPassword.getText().toString());
            dataUser.enqueue(new Callback<ArrayList<DataUser>>() {
                @Override
                public void onResponse(Call<ArrayList<DataUser>> call, Response<ArrayList<DataUser>> response) {
                    ArrayList<DataUser> user = response.body();
                    if (user.isEmpty()){
                        Loading(1);
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.invalid_user), Snackbar.LENGTH_LONG).show();
                        return;
                    }else{
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.welcome), Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("idUser", user.get(0).getId());
                        editor.putString("user", editTextUser.getText().toString());
                        editor.putString("password", editTextPassword.getText().toString());
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<DataUser>> call, Throwable t) {
                    Loading(1);
                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    // Recuperar clave
    @OnClick(R.id.textview_lostpassword_login)
    public void ClickLostPassword(){
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://kangu.com.co/mi-cuenta/lost-password/"));
        startActivity(viewIntent);
    }

    // Validacion Datos e inicio con Facebook
    private void ClickLoginFacebook(final String textEmail, final String textName, final String textPassword){
        Loading(0);
        dataUser = service.AccessFacebook("login_facebook", textEmail, textName, textPassword);
        dataUser.enqueue(new Callback<ArrayList<DataUser>>() {
            @Override
            public void onResponse(Call<ArrayList<DataUser>> call, Response<ArrayList<DataUser>> response) {
                ArrayList<DataUser> user = response.body();
                if (user.get(0).getResponse() != null){
                    Loading(1);
                    String error = user.get(0).getResponse();
                    switch (error){
                        case "1": Snackbar.make(coordinatorLayout, Html.fromHtml("El correo <b>" + textEmail +"</b> ya existe en nuestra plataforma."), Snackbar.LENGTH_LONG).show();
                            break;
                        case "2": Snackbar.make(coordinatorLayout, Html.fromHtml("El usuario <b>"+editTextUser.getText().toString()+"</b> ya existe en nuestra plataforma."), Snackbar.LENGTH_LONG).show();
                            break;
                        case "3": Snackbar.make(coordinatorLayout, getResources().getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }else{
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.welcome), Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("idUser", user.get(0).getId());
                    editor.putString("user", textEmail);
                    editor.putString("password", textPassword);
                    editor.commit();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DataUser>> call, Throwable t) {
                Loading(1);
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Boton registro
    @OnClick(R.id.button_signup)
    public void ClickSignUp(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    // Key Facebook
    private void PrintKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.virtualtec.kangutest", PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Loading
    private void Loading(int status){
        if (status == 0){
            relativeBody.animate().alpha(0.0f);
            // Imagen Gif cargando
            linearLoading.animate().alpha(1.0f);
            // Input Disabled
            editTextUser.setEnabled(false); editTextPassword.setEnabled(false);
            // Buttons Disabled
            buttonLogin.setEnabled(false); buttonSignup.setEnabled(false);
        }else{
            relativeBody.animate().alpha(1.0f);
            linearLoading.animate().alpha(0.0f);
            // Input Disabled
            editTextUser.setEnabled(true); editTextPassword.setEnabled(true);
            // Buttons Disabled
            buttonLogin.setEnabled(true); buttonSignup.setEnabled(true);
        }
    }
}
