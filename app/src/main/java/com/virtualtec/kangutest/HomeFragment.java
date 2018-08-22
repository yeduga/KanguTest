package com.virtualtec.kangutest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.virtualtec.kangutest.Adapters.CategoryAdapter;
import com.virtualtec.kangutest.Adapters.ListsAdapter;
import com.virtualtec.kangutest.Adapters.ProductsCategoryAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataProducts;
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
 * Created by Android on 21/02/18.
 */

public class HomeFragment extends Fragment {

    static private View view;
    private ProductsCategoryFragment fragmentProductCategory;
    private Retrofit retrofit;
    private RestService service;
    private Call<ArrayList<DataList>> dataLists;
    private Call<ArrayList<DataProducts>> dataProducts;
    private Call<ArrayList<DataCategory>> dataCategory;
    // Products
    @BindView(R.id.rv_products_home) RecyclerView rvProducts;
    @BindView(R.id.textview_rv_products_home) TextView textViewRvProducts;
    @BindView(R.id.loading_products_home) GifView gifLoadingProducts;
    @BindView(R.id.linear_loading_products_home) LinearLayout linearLoadingProducts;
    // Categories
    @BindView(R.id.rv_categories_home) RecyclerView rvCategories;
    @BindView(R.id.textview_rv_categories_home) TextView textViewRvCategories;
    @BindView(R.id.loading_categories_home) GifView gifLoadingCategories;
    @BindView(R.id.linear_loading_categories_home) LinearLayout linearLoadingCategories;
    // Lists
    @BindView(R.id.rv_lists_home) RecyclerView rvLists;
    @BindView(R.id.textview_rv_lists_home) TextView textViewRvLists;
    @BindView(R.id.loading_lists_home) GifView gifLoadingLists;
    @BindView(R.id.linear_loading_list_home) LinearLayout linearLoadingLists;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Inicio");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.item_home), 0, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(0);

        Init();
        return view;
    }

    private void Init() {
        gifLoadingProducts.loadGIFResource(R.drawable.cargando_verde);
        gifLoadingLists.loadGIFResource(R.drawable.cargando_verde);
        gifLoadingCategories.loadGIFResource(R.drawable.cargando_verde);

        InitRetrofit();
        LoadLists();
        LoadProducts();
        LoadCategories();
    }

    private void LoadLists() {
        dataLists = service.Lists("list", ((HomeActivity)getActivity()).prefsIdUser);
        dataLists.enqueue(new Callback<ArrayList<DataList>>() {
            @Override
            public void onResponse(Call<ArrayList<DataList>> call, Response<ArrayList<DataList>> response) {
                if (getActivity() == null) return; // Pregunta si esta asociada esta actividad
                linearLoadingLists.setVisibility(view.GONE);
                ArrayList<DataList> myLists = response.body();
                if (myLists.isEmpty()){
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

    private void LoadProducts() {
        dataProducts = service.ProductsCategory("list_products_category", "15");
        dataProducts.enqueue(new Callback<ArrayList<DataProducts>>() {
            @Override
            public void onResponse(Call<ArrayList<DataProducts>> call, Response<ArrayList<DataProducts>> response) {
                if (getActivity() == null) return;
                linearLoadingProducts.setVisibility(view.GONE);
                ArrayList<DataProducts> misProductos = response.body();
                if (misProductos.isEmpty()){
                    textViewRvProducts.setVisibility(View.VISIBLE);
                    textViewRvProducts.setText(getResources().getString(R.string.no_data));
                    return;
                }
                ProductsCategoryAdapter booksAdapter = new ProductsCategoryAdapter(view.getContext(), misProductos, 1);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                rvProducts.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                rvProducts.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataProducts>> call, Throwable t) {
                if (getActivity() == null) return;
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
                linearLoadingProducts.setVisibility(view.GONE);
                textViewRvProducts.setVisibility(View.VISIBLE);
                textViewRvProducts.setText(getResources().getString(R.string.error));
            }
        });
    }

    private void LoadCategories() {
        dataCategory = service.Categories("categories", "0");
        dataCategory.enqueue(new Callback<ArrayList<DataCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<DataCategory>> call, Response<ArrayList<DataCategory>> response) {
                if (getActivity() == null) return;
                linearLoadingCategories.setVisibility(view.GONE);
                ArrayList<DataCategory> myCategories = response.body();
                CategoryAdapter booksAdapter = new CategoryAdapter(fragmentProductCategory, getActivity(), myCategories, 1, 0);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                rvCategories.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                rvCategories.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataCategory>> call, Throwable t) {
                if (getActivity() == null) return;
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
                linearLoadingCategories.setVisibility(view.GONE);
                textViewRvCategories.setVisibility(View.VISIBLE);
                textViewRvCategories.setText(getResources().getString(R.string.error));
            }
        });
    }

    // Click's See More
    @OnClick(R.id.textview_seemorelists_home)
    public void seeMoreLists(){
        Fragment selectedFragment = ListsFragment.newInstance();
        ((HomeActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate();
        ((HomeActivity)getActivity()).FunctFragmentTransaction(selectedFragment);
    }
    @OnClick(R.id.textview_seemoreproducts_home)
    public void seeMoreProducts(){
        Fragment selectedFragment = ProductsFragment.newInstance();
        ((HomeActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate();
        ((HomeActivity)getActivity()).FunctFragmentTransaction(selectedFragment);
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

}
