package com.virtualtec.kangutest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualtec.kangutest.Adapters.SearchProductsAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.HeaderSearch;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android on 16/04/18.
 */

public class ResultsFragment extends Fragment {

    private View view;
    private String Query;
    private Retrofit retrofit;
    private RestService service;
    private Call<ArrayList<DataProducts>> dataProducts;
    private ArrayList<DataProducts> misProductos = null;
    @BindView(R.id.textview_info_searchResults) TextView textViewInfo;
    @BindView(R.id.rv_searchResults) RecyclerView recyclerView;
    @BindView(R.id.linear_loading_searchResults) LinearLayout linearLoading;
    @BindView(R.id.loading_searchResults) GifView gifLoading;


    public static ResultsFragment newInstance() {
        ResultsFragment fragment = new ResultsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Resultados de búsqueda");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_results, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.results_search), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(1);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        Query = this.getArguments().getString("Query");
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        InitRetrofit();
        LoadResults(Query);
    }

    public void LoadResults(String textQuery) {
        Toast.makeText(getActivity(), "Buscando " + textQuery + "...", Toast.LENGTH_SHORT).show();
        Query = textQuery;
        functLoading(0);
        textViewInfo.setVisibility(View.GONE);
        dataProducts = service.SearchProducts("search_products", Query);
        dataProducts.enqueue(new Callback<ArrayList<DataProducts>>() {
            @Override
            public void onResponse(Call<ArrayList<DataProducts>> call, Response<ArrayList<DataProducts>> response) {
                if(getActivity() == null) return;
                functLoading(1);
                misProductos = response.body();
                if (misProductos.isEmpty()){
                    messageListEmpty();
                    return;
                }
                SearchProductsAdapter booksAdapter = new SearchProductsAdapter(getActivity(), getHeader(Query, misProductos.size()), misProductos);
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
                linearLoading.setVisibility(View.GONE);
                textViewInfo.setVisibility(View.VISIBLE);
                textViewInfo.setText(view.getResources().getString(R.string.unknown_error));
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
            }
        });
    }

    public void messageListEmpty(){
        textViewInfo.setVisibility(View.VISIBLE);
        textViewInfo.setText("No se han encontrado resultados para \""+ Query+"\".");
    }

    public HeaderSearch getHeader(String query, int size) {
        HeaderSearch headerSearch = new HeaderSearch();
        headerSearch.setHeader( "Búsqueda \"" + query + "\".", size + " resultado(s) encontrados.");
        return headerSearch;
    }

    private void functLoading(int status) {
        if (status == 0){
            recyclerView.setAdapter(null);
            linearLoading.setVisibility(View.VISIBLE); recyclerView.setVisibility(View.GONE);
        }else{
            linearLoading.setVisibility(View.GONE); recyclerView.setVisibility(View.VISIBLE);
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
