package com.virtualtec.kangutest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.virtualtec.kangutest.Adapters.CategoryAdapter;
import com.virtualtec.kangutest.Adapters.ProductsCategoryAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android on 21/02/18.
 */

public class ProductsFragment extends Fragment {

    static private View view;
    private ProductsCategoryFragment fragmentProductCategory;
    private Retrofit retrofit;
    private RestService service;
    private Call<ArrayList<DataCategory>> dataCategory;
    private String globalIdCategory;
    private ArrayList<DataCategory> arrayCategory;
    private ArrayList<DataProducts> arrayProducts;
    private Call<ArrayList<DataProducts>> dataProducts;
    @BindView(R.id.tv_loading_categories) TextView tvLoadingCategories;
    @BindView(R.id.tv_info_products) TextView tvInfoProducts;
    @BindView(R.id.rv_products) RecyclerView rvProducts;
    @BindView(R.id.rv_categories) RecyclerView rvCategories;
    @BindView(R.id.swipeRefreshLayout_products) SwipeRefreshLayout swipeRefreshLayoutProducts;
    @BindView(R.id.loading_products) GifView gifLoading;
    @BindView(R.id.linear_loading_products) LinearLayout linearLoadingProducts;

    public static ProductsFragment newInstance() {
        ProductsFragment fragment = new ProductsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Productos");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.item_products), 0, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(1);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        InitRetrofit();
        LoadCategories();

        swipeRefreshLayoutProducts.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        swipeRefreshLayoutProducts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuffleItems();
            }
        });
    }

    public void shuffleItems() {
        // Funcion cuando se hala el refrescador
        LoadProducts(globalIdCategory);
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

    private void LoadCategories() {
        arrayCategory = ((HomeActivity)getActivity()).ArrayCategories;
        if(arrayCategory == null){
            dataCategory = service.Categories("categories", "0");
            dataCategory.enqueue(new Callback<ArrayList<DataCategory>>() {
                @Override
                public void onResponse(Call<ArrayList<DataCategory>> call, Response<ArrayList<DataCategory>> response) {
                    if(getActivity() == null) return;
                    arrayCategory = response.body();
                    RecyclerViewCategories();
                }
                @Override
                public void onFailure(Call<ArrayList<DataCategory>> call, Throwable t) {
                    if(getActivity() == null) return;
                    ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
                    tvLoadingCategories.setText(getResources().getString(R.string.error));
                    tvInfoProducts.setText(getResources().getString(R.string.error));
                }
            });
        }else{
            RecyclerViewCategories();
        }
    }

    private void RecyclerViewCategories(){
        rvCategories.setVisibility(view.VISIBLE);
        tvLoadingCategories.setVisibility(view.GONE);

        CategoryAdapter booksAdapter = new CategoryAdapter(fragmentProductCategory, getActivity(), arrayCategory, 0, 0);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(mLayoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
        rvCategories.setAdapter(alphaAdapter);

        globalIdCategory = arrayCategory.get(0).getId();
        linearLoadingProducts.setVisibility(View.VISIBLE);

        arrayProducts = ((HomeActivity)getActivity()).ArrayProducts;
        linearLoadingProducts.setVisibility(View.VISIBLE);
        swipeRefreshLayoutProducts.setVisibility(View.VISIBLE);
        if(arrayProducts == null){
            LoadProducts(globalIdCategory);
        }else{
            RecyclerViewProducts(arrayProducts);
        }
    }

    // Cargar productos
    public void LoadProducts(String idCategory) {
        dataProducts = service.ProductsCategory("list_products", idCategory);
        dataProducts.enqueue(new Callback<ArrayList<DataProducts>>() {
            @Override
            public void onResponse(Call<ArrayList<DataProducts>> call, Response<ArrayList<DataProducts>> response) {
                ArrayList<DataProducts> misProductos = response.body();
                RecyclerViewProducts(misProductos);
            }
            @Override
            public void onFailure(Call<ArrayList<DataProducts>> call, Throwable t) {
                linearLoadingProducts.setVisibility(View.GONE);
                swipeRefreshLayoutProducts.setRefreshing(false);
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
            }
        });
    }

    private void RecyclerViewProducts(ArrayList<DataProducts> misProductos){
        linearLoadingProducts.setVisibility(View.GONE);
        swipeRefreshLayoutProducts.setRefreshing(false);
        if (misProductos.isEmpty()){
            swipeRefreshLayoutProducts.setVisibility(View.GONE);
            tvInfoProducts.setVisibility(View.VISIBLE);
            tvInfoProducts.setText(view.getResources().getString(R.string.no_exist_products));
            return;
        }
        ProductsCategoryAdapter booksAdapter = new ProductsCategoryAdapter(getActivity(), misProductos, 0);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        rvProducts.setLayoutManager(mLayoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
        rvProducts.setAdapter(alphaAdapter);
        // Seteo en principal
        ((HomeActivity)getActivity()).ArrayProducts = misProductos;
    }

    // Progress initial
    public void functLoading(int status){
        if (status == 0) swipeRefreshLayoutProducts.setRefreshing(true); else swipeRefreshLayoutProducts.setRefreshing(false); // cancelar la indicación visual de refrescar
    }
}
