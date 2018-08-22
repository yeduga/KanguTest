package com.virtualtec.kangutest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.Datas.DataUser;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

/**
 * Created by Android on 6/04/18.
 */

public class SettingsFragment extends Fragment {

    private View view;
    private String prefsIdUser;
    private Retrofit retrofit;
    private RestService service;
    private Call<DataUser> dataUser;
    private Call<DataResponse> dataResponse;
    private AwesomeValidation awesomeValidation;
    @BindView(R.id.linear_content_settings) LinearLayout linearLayoutContent;
    @BindView(R.id.linear_loading_settings) LinearLayout linearLayoutLoading;
    @BindView(R.id.loading_settings) GifView gifLoadingLists;
    // EditTexts
    @BindView(R.id.edit_text_user_settings) EditText editTextUser;
    @BindView(R.id.edit_text_name_settings) EditText editTextName;
    @BindView(R.id.edit_text_email_settings) EditText editTextEmail;
    @BindView(R.id.edit_text_phone_settings) EditText editTextPhone;
    @BindView(R.id.edit_text_address_settings) EditText editTextAddress;
    @BindView(R.id.edit_text_city_settings) EditText editTextCity;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Ajustes");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.settings), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(4);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        prefsIdUser = ((HomeActivity)getActivity()).prefsIdUser;
        gifLoadingLists.loadGIFResource(R.drawable.cargando_verde);
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(editTextUser, RegexTemplate.NOT_EMPTY, "Ingrese un usuario");
        awesomeValidation.addValidation(editTextEmail, Patterns.EMAIL_ADDRESS, getResources().getString(R.string.emailerror));
        awesomeValidation.addValidation(editTextPhone,
                Pattern.compile( // sdd = space, dot, or dash
                        "(\\+[0-9]+[\\- \\.]*)?"        // +<digits><sdd>*
                        + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                        + "([0-9][0-9\\- \\.]+[0-9])?"),
                getResources().getString(R.string.phoneerror));

        InitRetrofit();
        LoadData();
    }

    private void LoadData() {
        functLoading(0);
        dataUser = service.ViewCustomer("view_customer", prefsIdUser);
        dataUser.enqueue(new Callback<DataUser>() {
            @Override
            public void onResponse(Call<DataUser> call, Response<DataUser> response) {
                if (getActivity() == null) return;
                DataUser myUser = response.body();
                functLoading(1);
                if (myUser == null){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    ((HomeActivity)getActivity()).finish();
                }else{
                    SetDatas(myUser);
                }
            }

            @Override
            public void onFailure(Call<DataUser> call, Throwable t) {
                if (getActivity() == null) return;
                functLoading(1);
                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.no_connection));
            }
        });
    }

    @OnClick(R.id.btn_save_settings)
    public void UpdateUser(){
        if (awesomeValidation.validate()) {
            functLoading(0);
            dataResponse = service.UpdateCustomer("update_customer", prefsIdUser, editTextName.getText().toString(),
                    "", editTextAddress.getText().toString(), "", editTextCity.getText().toString(),
                    "", "", "", editTextPhone.getText().toString());
            dataResponse.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if(getActivity() == null) return;
                    DataResponse myResponse = response.body();
                    if (myResponse == null){
                        Toast.makeText(getActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ((HomeActivity)getActivity()).validSesion();
                    LoadData();
                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.done));
                }
                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    if(getActivity() == null) return;
                    functLoading(1);
                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
                }
            });
        }
    }

    @OnClick(R.id.btn_clear_search_settings)
    public void ClearSearch(){
        ((HomeActivity)getActivity()).searchClearAll();
        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.done));
    }

    private void SetDatas(DataUser myUser) {
        editTextUser.setText(myUser.getUsername());
        editTextName.setText(myUser.getFirst_name());
        editTextEmail.setText(myUser.getEmail());
        editTextPhone.setText(myUser.getBilling().getPhone());
        editTextAddress.setText(myUser.getBilling().getAddress_1());
        editTextCity.setText(myUser.getBilling().getCity());
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

    private void functLoading(int status) {
        if(status == 0){
            linearLayoutContent.setVisibility(View.GONE); linearLayoutLoading.setVisibility(View.VISIBLE);
        }else{
            linearLayoutContent.setVisibility(View.VISIBLE); linearLayoutLoading.setVisibility(View.GONE);
        }
    }
}
