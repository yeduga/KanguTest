package com.virtualtec.kangutest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualtec.kangutest.Adapters.AddressAdapter;
import com.virtualtec.kangutest.Adapters.ListsAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataAddress;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.Datas.DataResponseDelivery;
import com.virtualtec.kangutest.Datas.DataUser;
import com.virtualtec.kangutest.RestServiceApi.RestService;

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
 * Created by Android on 27/03/18.
 */

public class ProfileFragment extends Fragment {

    private View view;
    private String prefsUser, prefsPassword;
    private Retrofit retrofit;
    private RestService service;
    private Call<DataResponse> dataResponse;
    private Call<DataResponseDelivery> dataResponseDelivery;
    private Call<ArrayList<DataList>> dataLists;
    private Call<ArrayList<DataAddress>> dataAddress;
    private ArrayList<DataAddress> myAddress;
    private Call<ArrayList<DataUser>> dataUser;
    private ArrayList<DataUser> arrayUser;
    private DataResponseDelivery myResponseDelivery;
    @BindView(R.id.scrollview_profile) ScrollView scrollView;

    @BindView(R.id.textview_username_profile) TextView textViewUsername;
    @BindView(R.id.textview_email_profile) TextView textViewEmail;
    // Next Delivery
    @BindView(R.id.textview_month_nextDelivery_profile) TextView textViewMonth;
    @BindView(R.id.textview_day_nextDelivery_profile) TextView textViewDay;
    @BindView(R.id.linear_nextDelivery_profile) LinearLayout linearNextDelivery;
    @BindView(R.id.linear_loading_nextDelivery_profile) LinearLayout linearLoadingNextDelivery;
    @BindView(R.id.loading_nextDelivery_profile) GifView gifLoadingNextDelivery;
    // Lists
    @BindView(R.id.rv_lists_profile) RecyclerView rvLists;
    @BindView(R.id.textview_rv_lists_profile) TextView textViewRvLists;
    @BindView(R.id.loading_lists_profile) GifView gifLoadingLists;
    @BindView(R.id.linear_loading_list_profile) LinearLayout linearLoadingLists;
    // Addresses
    @BindView(R.id.textview_loading_profile) TextView textViewLoading;
    @BindView(R.id.listview_address_profile) ListView lvAddress;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Perfil");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.options_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                SettingsFragment nextFrag= new SettingsFragment();
                ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.action_logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(getResources().getString(R.string.logout));
                alertDialogBuilder
                        .setMessage(Html.fromHtml("¿Desea cerrar la sesión?"))
                        .setCancelable(false)
                        .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                ((HomeActivity)getActivity()).LogoutSesion();
                            }
                        })
                        .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alertDialogBuilder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.my_profile), 0, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(4);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        gifLoadingLists.loadGIFResource(R.drawable.cargando_verde);
        gifLoadingNextDelivery.loadGIFResource(R.drawable.cargando_verde);

        InitRetrofit();
        LoadUser();
        LoadNextDelivery();
        LoadLists();
        LoadAddress();
    }

    private void LoadUser() {
        arrayUser = ((HomeActivity)getActivity()).ArrayUser;
        if(arrayUser == null){
            prefsUser = ((HomeActivity)getActivity()).prefsUser;
            prefsPassword = ((HomeActivity)getActivity()).prefsPassword;
            dataUser = service.Access("login", prefsUser, prefsPassword);
            dataUser.enqueue(new Callback<ArrayList<DataUser>>() {
                @Override
                public void onResponse(Call<ArrayList<DataUser>> call, Response<ArrayList<DataUser>> response) {
                    if (getActivity() == null) return;
                    arrayUser = response.body();
                    if (arrayUser == null){
                        textViewUsername.setText(getResources().getString(R.string.error));
                        textViewEmail.setText(getResources().getString(R.string.error));
                    }else{
                        if (arrayUser.size() > 0){
                            if(arrayUser.get(0).getFirst_name().equals("")) {
                                textViewUsername.setText(arrayUser.get(0).getUsername());
                            }else{
                                textViewUsername.setText(arrayUser.get(0).getFirst_name());
                            }
                            textViewEmail.setText(arrayUser.get(0).getEmail());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<DataUser>> call, Throwable t) {
                    if (getActivity() == null) return;
                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.no_connection));
                }
            });
        }else{
            if (arrayUser.size() > 0){
                if(arrayUser.get(0).getFirst_name().equals("")) {
                    textViewUsername.setText(arrayUser.get(0).getUsername());
                }else{
                    textViewUsername.setText(arrayUser.get(0).getFirst_name());
                }
                textViewEmail.setText(arrayUser.get(0).getEmail());
            }
        }
    }

    private void LoadNextDelivery() {
        LoadingDelivery(0);
        dataResponseDelivery = service.NextDelivery("next_delivery", ((HomeActivity)getActivity()).prefsIdUser);
        dataResponseDelivery.enqueue(new Callback<DataResponseDelivery>() {
            @Override
            public void onResponse(Call<DataResponseDelivery> call, Response<DataResponseDelivery> response) {
                if(getActivity() == null) return; LoadingDelivery(1);
                myResponseDelivery = response.body();
                if (myResponseDelivery.getResponse().equals("1")){
                    String date = myResponseDelivery.getDate_delivery();
                    String [] parts = date.split("-");
                    textViewMonth.setText(parts[0]);
                    textViewDay.setText(parts[1]);
                }else{
                    textViewMonth.setText(getResources().getString(R.string.there_is_not));
                    textViewDay.setText("00");
                }
            }
            @Override
            public void onFailure(Call<DataResponseDelivery> call, Throwable t) {
                if(getActivity() == null) return; LoadingDelivery(0);
                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
            }
        });
    }

    private void LoadLists() {
        dataLists = service.Lists("list", ((HomeActivity)getActivity()).prefsIdUser);
        dataLists.enqueue(new Callback<ArrayList<DataList>>() {
            @Override
            public void onResponse(Call<ArrayList<DataList>> call, Response<ArrayList<DataList>> response) {
                if (getActivity() == null) return; // Pregunta si esta asociada esta actividad
                linearLoadingLists.setVisibility(view.GONE);
                ArrayList<DataList> myLists = response.body();
                if (myLists.isEmpty()) {
                    textViewRvLists.setVisibility(View.VISIBLE);
                    textViewRvLists.setText(getResources().getString(R.string.no_exist_lists));
                    return;
                }
                ListsAdapter booksAdapter = new ListsAdapter(getActivity(), myLists, 1);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                rvLists.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                rvLists.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataList>> call, Throwable t) {
                if (getActivity() == null) return;
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
                linearLoadingLists.setVisibility(view.GONE);
                textViewRvLists.setVisibility(View.VISIBLE);
                textViewRvLists.setText(getResources().getString(R.string.error));
            }
        });
    }

    private void LoadAddress() {
        textViewLoading.setText(getResources().getString(R.string.loading));
        textViewLoading.setVisibility(View.VISIBLE);
        dataAddress = service.AddressxUser("view_address", ((HomeActivity)getActivity()).prefsIdUser);
        dataAddress.enqueue(new Callback<ArrayList<DataAddress>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<ArrayList<DataAddress>> call, Response<ArrayList<DataAddress>> response) {
                if (getActivity() == null) return;
                textViewLoading.setVisibility(View.GONE);
                myAddress = response.body();
                if (myAddress.size() == 0){
                    textViewLoading.setText(getResources().getString(R.string.no_saved_addresses));
                    textViewLoading.setVisibility(View.VISIBLE);
                }
                AddressAdapter listAdapter = new AddressAdapter(getActivity(), myAddress, 1, ProfileFragment.this);
                lvAddress.setAdapter(listAdapter);
                RefreshHeightList(listAdapter, lvAddress);
            }

            @Override
            public void onFailure(Call<ArrayList<DataAddress>> call, Throwable t) {
                if (getActivity() == null) return;
                textViewLoading.setVisibility(View.GONE);
                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.no_connection));
            }
        });
    }

    @OnClick(R.id.btn_calendar_history_profile)
    public void OpenCalendar(){
        CalendarListFragment nextFrag= new CalendarListFragment();
        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.btn_orders_history_profile)
    public void OpenOrdersHistory(){
        OrdersHistoryFragment nextFrag= new OrdersHistoryFragment();
        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.linear_nextDelivery_profile)
    public void OpenListNextDelivery(){
        final DataList list = new DataList(myResponseDelivery.getId_list(), myResponseDelivery.getName_list(), myResponseDelivery.getAddress(), "", myResponseDelivery.getDate_delivery(), myResponseDelivery.getAddress());
        Bundle bundle = new Bundle();
        bundle.putSerializable("arrayList", list);
        ListDetailsFragment nextFrag= new ListDetailsFragment();
        nextFrag.setArguments(bundle);
        ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.imagebtn_add_address_profile)
    public void AddAddress(){
        // Crear Direccion
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_input, null);

        TextView textDialog = (TextView) alertLayout.findViewById(R.id.text_dialog_input);
        TextInputLayout layoutTextDialog = (TextInputLayout) alertLayout.findViewById(R.id.layout_text_dialog_input);
        final TextView editTextDialog = (EditText) alertLayout.findViewById(R.id.edit_text_dialog_input);

        textDialog.setText(getResources().getString(R.string.create_a_new_address));
        layoutTextDialog.setHint("Dirección completa");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Guardar una dirección");
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
                    Toast toast = Toast.makeText(getActivity(), "Por favor, ingrese una dirección.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    AddAddress();
                    return;
                }
                // Peticion al API
                String idUser = ((HomeActivity)getActivity()).prefsIdUser;
                String strAddress = editTextDialog.getText().toString();
                functLoading(0, getResources().getString(R.string.loading_adding));
                dataResponse = service.CreateAddressxUser("add_address", idUser, strAddress);
                dataResponse.enqueue(new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        if(getActivity() == null) return;
                        DataResponse myResponse = response.body();
                        if (myResponse == null){
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.error));
                            return;
                        }
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.done));
                        LoadAddress();
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

    // Editar direccion
    public void EditAddressSelected(final DataAddress dir){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_input, null);

        TextView textDialog = (TextView) alertLayout.findViewById(R.id.text_dialog_input);
        TextInputLayout layoutTextDialog = (TextInputLayout) alertLayout.findViewById(R.id.layout_text_dialog_input);
        final TextView editTextDialog = (EditText) alertLayout.findViewById(R.id.edit_text_dialog_input);

        editTextDialog.setText(dir.getAddress());
        textDialog.setVisibility(View.GONE);
        layoutTextDialog.setHint("Dirección completa");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Modificar dirección");
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
                    Toast toast = Toast.makeText(getActivity(), "Por favor, ingrese la dirección.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    EditAddressSelected(dir);
                    return;
                }
                // Peticion al API
                String strAddress = editTextDialog.getText().toString();
                functLoading(0, getResources().getString(R.string.loading));
                dataResponse = service.UpdateAddressxUser("update_address", dir.getId_list_address(), strAddress);
                dataResponse.enqueue(new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        if(getActivity() == null) return;
                        DataResponse myResponse = response.body();
                        if (myResponse == null){
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.error));
                            return;
                        }
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.done));
                        LoadAddress();
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

    // Eliminar direccion
    public void RemoveAddressSelected(final DataAddress dir) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Borrar de la lista");
        alertDialogBuilder
                .setMessage(Html.fromHtml("¿Desea eliminar la dirección <b>" + dir.getAddress() + "</b>?"))
                .setCancelable(false)
                .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        functLoading(0, getResources().getString(R.string.loading_delete)+"...");
                        dataResponse = service.DeleteAddress("delete_address", dir.getId_list_address());
                        dataResponse.enqueue(new Callback<DataResponse>() {
                            @Override
                            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                                if(getActivity() == null) return;
                                DataResponse myResponse = response.body();
                                if (myResponse == null){
                                    return;
                                }
                                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.deleted_address));
                                LoadAddress();
                            }
                            @Override
                            public void onFailure(Call<DataResponse> call, Throwable t) {
                                if(getActivity() == null) return;
                                ((HomeActivity)getActivity()).setSnackbar(getActivity().getResources().getString(R.string.unknown_error));
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.create().show();
    }

    // Refrescar tamaño del ListView
    private void RefreshHeightList(AddressAdapter listAdapter, ListView listViewAddress) {
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listViewAddress);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listViewAddress.getLayoutParams();
        params.height = totalHeight + (listViewAddress.getDividerHeight() * (listAdapter.getCount() - 1));
        listViewAddress.setLayoutParams(params);
        listViewAddress.requestLayout();

        scrollView.smoothScrollTo(0,0);
    }

    private void functLoading(int i, String text) {
        Snackbar snackbar;
        if (i == 0){
            snackbar = Snackbar.make(((HomeActivity)getActivity()).coordinatorLayout, text, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }

    private void LoadingDelivery(int status) {
        if (status == 0){
            linearLoadingNextDelivery.setVisibility(View.VISIBLE);
            linearNextDelivery.setVisibility(View.GONE);
        }else{
            linearLoadingNextDelivery.setVisibility(View.GONE);
            linearNextDelivery.setVisibility(View.VISIBLE);
        }
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }
}
