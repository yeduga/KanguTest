package com.virtualtec.kangutest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.Adapters.CategoryAdapter;
import com.virtualtec.kangutest.Adapters.ProductsCategoryAdapter;
import com.virtualtec.kangutest.Adapters.SubcategoryAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android on 21/02/18.
 */

public class ProductsCategoryFragment extends Fragment {

    private int contLoading = 0;
    private ProductsCategoryFragment fragment;
    private View view;
    private String idCategory, nameCategory, imageCategory, globalIdCategory;
    private Retrofit retrofit;
    private RestService service;
    private ArrayList<DataCategory> arrayCategory;
    private Call<ArrayList<DataProducts>> dataProducts;
    private Call<ArrayList<DataCategory>> dataCategory;
    @BindView(R.id.textview_category) TextView textViewCategory;
    @BindView(R.id.tv_info_products_category) TextView textViewInfo;
    @BindView(R.id.rv_productsCategory) RecyclerView rvProductsCategory;
    @BindView(R.id.image_category) ImageView imageViewCategory;
    @BindView(R.id.swipeRefreshLayout_productsCategory) SwipeRefreshLayout srlProductsCategory;
    @BindView(R.id.linear_loading_products_category) LinearLayout linearLoading;
    @BindView(R.id.loading_products_category) GifView gifLoading;
    // Categorias
    @BindView(R.id.rv_categories_productsCategory) RecyclerView rvCategories;
    @BindView(R.id.tv_loading_categories_productsCategory) TextView tvLoadingCategories;
    // Subcategories
    @BindView(R.id.rv_subcategories_category) RecyclerView rvSubcategories;
    @BindView(R.id.tv_info_subcategories_category) TextView textViewInfoSubcategories;

    public static ProductsCategoryFragment newInstance() {
        ProductsCategoryFragment fragment = new ProductsCategoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Categorías");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products_category, container, false);
        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        idCategory = this.getArguments().getString("idCategory");
        nameCategory = this.getArguments().getString("nameCategory");
        imageCategory = this.getArguments().getString("imageCategory");
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        InitRetrofit();
        LoadCategories();
        LoadTexts(idCategory, nameCategory, imageCategory);

        // Titulo Action bar
        ((HomeActivity) getActivity()).setActionBarCustom("Categoría", 1, getActivity());

        srlProductsCategory.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        srlProductsCategory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuffleItems();
            }
        });
    }

    public void shuffleItems() {
        // Funcion cuando se hala el refrescador
        LoadProducts(idCategory);
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
                    textViewInfo.setText(getResources().getString(R.string.error));
                }
            });
        }else{
            RecyclerViewCategories();
        }
    }

    private void RecyclerViewCategories() {
        rvCategories.setVisibility(view.VISIBLE);
        tvLoadingCategories.setVisibility(view.GONE);

        CategoryAdapter booksAdapter = new CategoryAdapter(ProductsCategoryFragment.this, getActivity(), arrayCategory, 0, 1);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(mLayoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
        rvCategories.setAdapter(alphaAdapter);

        globalIdCategory = arrayCategory.get(0).getId();
        // linearLoadingProducts.setVisibility(View.VISIBLE);
    }

    public void LoadTexts(String idCategory, String nameCategory, String imageCategory) {
        // Textos e Imagenes XML
        textViewCategory.setText(nameCategory);
        Picasso.with(getActivity())
                .load(imageCategory)
                .error( R.drawable.logo_circular )
                .placeholder( R.drawable.logo_circular )
                .into( imageViewCategory );
        LoadSubcategory(idCategory);
        LoadProducts(idCategory);
    }

    public void LoadSubcategory(String idCategory) {
        dataCategory = service.Categories("categories", idCategory);
        dataCategory.enqueue(new Callback<ArrayList<DataCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<DataCategory>> call, Response<ArrayList<DataCategory>> response) {
                if(getActivity() == null) return;
                ArrayList<DataCategory> arraySubcategory = response.body();

                rvSubcategories.setVisibility(View.VISIBLE);
                textViewInfoSubcategories.setVisibility(View.GONE);

                SubcategoryAdapter booksAdapter = new SubcategoryAdapter(getActivity(), ProductsCategoryFragment.this, arraySubcategory);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                rvSubcategories.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                rvSubcategories.setAdapter(alphaAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataCategory>> call, Throwable t) {
                if(getActivity() == null) return;
                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.no_connection));
                textViewInfoSubcategories.setText(getResources().getString(R.string.error));
            }
        });
    }

    public void LoadProducts(String idCategory) {
        functLoading(0);
        textViewInfo.setVisibility(View.GONE);
        dataProducts = service.ProductsCategory("list_products_category", idCategory);
        dataProducts.enqueue(new Callback<ArrayList<DataProducts>>() {
            @Override
            public void onResponse(Call<ArrayList<DataProducts>> call, Response<ArrayList<DataProducts>> response) {
                if (getActivity() == null) return;
                functLoading(1);
                ArrayList<DataProducts> misProductos = response.body();
                if (misProductos.isEmpty()){
                    functLoading(1);
                    textViewInfo.setVisibility(View.VISIBLE);
                    textViewInfo.setText(getResources().getString(R.string.no_exist_products));
                }
                ProductsCategoryAdapter booksAdapter = new ProductsCategoryAdapter(getActivity(), misProductos, 0);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                rvProductsCategory.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                rvProductsCategory.setAdapter(alphaAdapter);

            }
            @Override
            public void onFailure(Call<ArrayList<DataProducts>> call, Throwable t) {
                if (getActivity() == null) return;
                Log.d("Error", t.getMessage());
                functLoading(1);
                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.no_connection));
            }
        });
    }

    @OnClick(R.id.image_category)
    public void LoadProductsCategory(){
        LoadProducts(idCategory);
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

    // Progress initial
    public void functLoading(int status){
        if (status == 0){
            textViewInfo.setVisibility(View.GONE);
            if (contLoading == 1) srlProductsCategory.setRefreshing(true);
        }else{
            linearLoading.setVisibility(View.GONE);
            srlProductsCategory.setRefreshing(false); // cancelar la indicación visual de refrescar
        }
        contLoading = 1;
    }
}
