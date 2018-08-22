package com.virtualtec.kangutest;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualtec.kangutest.Adapters.ListsAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android on 20/03/18.
 */

public class ListsFragment extends Fragment{

    static private View view;
    private Retrofit retrofit;
    private RestService service;
    private Call<ArrayList<DataList>> dataLists;
    private Call<DataResponse> dataResponse;
    @BindView(R.id.loading_list) GifView gifLoading;
    @BindView(R.id.linear_loading_list) LinearLayout linearLoading;
    @BindView(R.id.rv_lists) RecyclerView recyclerView;
    @BindView(R.id.textview_rv_lists) TextView textViewRv;

    public static ListsFragment newInstance() {
        ListsFragment fragment = new ListsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Listas");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lists, container, false);
        ButterKnife.bind(this, view);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.my_lists), 0, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(3);

        Init();
        return view;
    }

    private void Init() {
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        InitRetrofit();
        LoadLists();
    }

    private void LoadLists() {
        dataLists = service.Lists("list", ((HomeActivity)getActivity()).prefsIdUser);
        dataLists.enqueue(new Callback<ArrayList<DataList>>() {
            @Override
            public void onResponse(Call<ArrayList<DataList>> call, Response<ArrayList<DataList>> response) {
                if (getActivity() == null) return; // Pregunta si esta asociada esta actividad
                linearLoading.setVisibility(View.GONE);
                ArrayList<DataList> myLists = response.body();
                if(myLists.isEmpty()){
                    textViewRv.setVisibility(View.VISIBLE);
                    textViewRv.setText(getResources().getString(R.string.no_exist_lists));
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                ListsAdapter booksAdapter = new ListsAdapter(getActivity(), myLists, 0);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                recyclerView.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataList>> call, Throwable t) {
                if (getActivity() == null) return;
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
                linearLoading.setVisibility(View.GONE);
                textViewRv.setVisibility(View.VISIBLE);
                textViewRv.setText(getResources().getString(R.string.error));
            }
        });
    }

    @OnClick(R.id.btn_add_list)
    public void functAddList(){
        // Crear lista
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_input, null);

        TextView textDialog = (TextView) alertLayout.findViewById(R.id.text_dialog_input);
        TextInputLayout layoutTextDialog = (TextInputLayout) alertLayout.findViewById(R.id.layout_text_dialog_input);
        final TextView editTextDialog = (EditText) alertLayout.findViewById(R.id.edit_text_dialog_input);

        textDialog.setText("Crear una nueva lista vacia.");
        layoutTextDialog.setHint("Nombre lista");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Crear lista");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editTextDialog.getText().toString().length() == 0){
                    Toast toast = Toast.makeText(getActivity(), "Nombre lista no debe estar vacio.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                // Peticion al API
                String jsonStr = "[]";
                String idUser = ((HomeActivity)getActivity()).prefsIdUser;
                String nameList = editTextDialog.getText().toString();
                Snackbar snackbar = Snackbar.make(((HomeActivity)getActivity()).coordinatorLayout, "Creando lista...", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                dataResponse = service.CreateListProducts("add_list_product", idUser, nameList, "", jsonStr);
                dataResponse.enqueue(new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        if(getActivity() == null) return;
                        DataResponse myResponse = response.body();
                        if (myResponse == null){
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.error));
                            return;
                        }
                        LoadLists();
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.done));
                    }
                    @Override
                    public void onFailure(Call<DataResponse> call, Throwable t) {
                        if(getActivity() == null) return;
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
                    }
                });
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }
}
