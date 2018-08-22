package com.virtualtec.kangutest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualtec.kangutest.Adapters.ProductsListAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.DataResponse;
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
 * Created by Android on 21/03/18.
 */

public class ListDetailsFragment extends Fragment{

    static private View view;
    private Retrofit retrofit;
    private RestService service;
    private DataList dataList;
    private Call<ArrayList<DataProducts>> dataProducts;
    private Call<DataResponse> dataResponse;
    private ArrayList<DataProducts> misProductos = null;
    @BindView(R.id.btn_purchase_listDetails) Button btnPurchase;
    @BindView(R.id.textview_name_listDetails) TextView textViewName;
    @BindView(R.id.textview_rv_products_listDetails) TextView textViewRecyclerView;
    @BindView(R.id.loading_listDetails) GifView gifLoading;
    @BindView(R.id.linear_loading_listDetails) LinearLayout linearLoading;
    @BindView(R.id.rv_products_listDetails) RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout_products_listDetails) SwipeRefreshLayout swipeRefreshLayout;

    public static ListsFragment newInstance() {
        ListsFragment fragment = new ListsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Detalles de lista");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_details, container, false);
        ButterKnife.bind(this, view);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.details_list), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(3);

        Init();
        return view;
    }

    private void Init() {
        gifLoading.loadGIFResource(R.drawable.cargando_verde);
        dataList = (DataList) this.getArguments().getSerializable("arrayList");

        SetNameList(dataList.getName_list());
        InitRetrofit();
        LoadProducts();

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuffleItems();
            }
        });
    }

    private void shuffleItems() {
        LoadProducts();
    }

    public void LoadProducts() {
        functLoading(0);
        textViewRecyclerView.setVisibility(View.GONE);
        dataProducts = service.ProductsList("detail_list", dataList.getId_list());
        dataProducts.enqueue(new Callback<ArrayList<DataProducts>>() {
            @Override
            public void onResponse(Call<ArrayList<DataProducts>> call, Response<ArrayList<DataProducts>> response) {
                if(getActivity() == null) return;
                functLoading(1);
                linearLoading.setVisibility(View.GONE);
                misProductos = response.body();
                if (misProductos.isEmpty()){
                    messageListEmpty();
                    return;
                }
                ProductsListAdapter booksAdapter = new ProductsListAdapter(view.getContext(), ListDetailsFragment.this, misProductos);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                recyclerView.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataProducts>> call, Throwable t) {
                if(getActivity() == null) return;
                functLoading(1);
                Log.d("ErrorList", t.getMessage());
                linearLoading.setVisibility(View.GONE);
                textViewRecyclerView.setVisibility(View.VISIBLE);
                textViewRecyclerView.setText(view.getResources().getString(R.string.unknown_error));
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
            }
        });
    }

    // Editar Nombre de la lista
    @OnClick(R.id.imagebtn_edit_listDetails)
    public void functEditList(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_input, null);
        TextView textDialog = (TextView) alertLayout.findViewById(R.id.text_dialog_input);
        TextInputLayout layoutTextDialog = (TextInputLayout) alertLayout.findViewById(R.id.layout_text_dialog_input);
        final TextView editTextDialog = (EditText) alertLayout.findViewById(R.id.edit_text_dialog_input);
        textDialog.setVisibility(View.GONE);
        layoutTextDialog.setHint("Nombre lista");
        editTextDialog.setText(textViewName.getText().toString());
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Editar nombre");
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
                    Toast.makeText(getActivity(), "Nombre lista no puede estar vacio.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Cambiar nombre lista API
                functLoading(0);
                dataResponse = service.EditListName("update_list", dataList.getId_list(), editTextDialog.getText().toString(), dataList.getAddress());
                dataResponse.enqueue(new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        if(getActivity() == null) return;
                        functLoading(1);
                        DataResponse myResponse = response.body();
                        if (myResponse == null){
                            return;
                        }
                        SetNameList(editTextDialog.getText().toString());
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
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    // Eliminar lista
    @OnClick(R.id.btn_delete_listDetails)
    public void functDeleteList(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Borrar lista");
        alertDialogBuilder
                .setMessage(Html.fromHtml("¿Desea eliminar la lista <b>" + dataList.getName_list() + "</b>?"))
                .setCancelable(false)
                .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        functLoading(0);
                        dataResponse = service.DeleteList("delete_list", dataList.getId_list());
                        dataResponse.enqueue(new Callback<DataResponse>() {
                            @Override
                            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                                if(getActivity() == null) return;
                                functLoading(1);
                                DataResponse myResponse = response.body();
                                if (myResponse == null){
                                    return;
                                }
                                Toast.makeText(getActivity(), getResources().getString(R.string.list_removed), Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                            @Override
                            public void onFailure(Call<DataResponse> call, Throwable t) {
                                if(getActivity() == null) return;
                                functLoading(1);
                                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
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

    // Generar orden
    @OnClick(R.id.btn_purchase_listDetails)
    public void functPruchaseList(){
        if (misProductos == null){
            ((HomeActivity)getActivity()).ToastMessageCenter(getResources().getString(R.string.please_add_product_list));
        }else if(misProductos.isEmpty()){
            ((HomeActivity)getActivity()).ToastMessageCenter(getResources().getString(R.string.please_add_product_list));
        }else{
            String strDataProducts = "";
            int totalprice = 0;
            int itemArticles = 0;
            for (int i = 0; i < misProductos.size(); i++){
                strDataProducts += misProductos.get(i).getId() + "$" + misProductos.get(i).getStock_quantity() + "$-";
                itemArticles = itemArticles + Integer.parseInt(misProductos.get(i).getStock_quantity());
                int subtotal = Integer.parseInt(misProductos.get(i).getStock_quantity()) * Integer.parseInt(misProductos.get(i).getPrice());
                totalprice = totalprice + subtotal;
            }
            Bundle bundle = new Bundle();
            bundle.putString("strDataProducts", strDataProducts);
            bundle.putString("itemProducts", String.valueOf(misProductos.size()));
            bundle.putString("itemArticles", String.valueOf(itemArticles));
            bundle.putString("totalPrice", String.valueOf(totalprice));
            bundle.putString("address", dataList.getAddress());
            bundle.putString("idList", dataList.getId_list());
            ProcessPurchaseFragment nextFrag= new ProcessPurchaseFragment();
            nextFrag.setArguments(bundle);
            ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void messageListEmpty(){
        textViewRecyclerView.setVisibility(View.VISIBLE);
        textViewRecyclerView.setText(view.getResources().getString(R.string.no_exist_products_list));
    }

    private void SetNameList(String text) { textViewName.setText(text); }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

    // Loadigng
    public void functLoading(int status){
        if (status == 0){
            swipeRefreshLayout.setRefreshing(true);
        }else{
            swipeRefreshLayout.setRefreshing(false); // cancelar la indicación visual de refrescar
        }
    }
}
