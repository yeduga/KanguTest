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

import com.virtualtec.kangutest.Adapters.OrdersAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataOrders;
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
 * Created by Android on 10/04/18.
 */

public class OrdersHistoryFragment extends Fragment {

    private View view;
    private Retrofit retrofit;
    private RestService service;
    private Call<ArrayList<DataOrders>> dataOrders;
    @BindView(R.id.loading_ordersHistory) GifView gifLoading;
    @BindView(R.id.rv_ordersHistory) RecyclerView recyclerView;
    @BindView(R.id.textview_rv_ordersHistory) TextView textViewRv;
    @BindView(R.id.linear_loading_ordersHistory) LinearLayout linearLoading;

    public static OrdersHistoryFragment newInstance() {
        OrdersHistoryFragment fragment = new OrdersHistoryFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Historial de pedidos");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders_history, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.orders_history), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(4);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        InitRetrofit();
        LoadOrders();
    }

    private void LoadOrders() {
        dataOrders = service.ListOrders("list_orders",((HomeActivity)getActivity()).prefsIdUser);
        dataOrders.enqueue(new Callback<ArrayList<DataOrders>>() {
            @Override
            public void onResponse(Call<ArrayList<DataOrders>> call, Response<ArrayList<DataOrders>> response) {
                if (getActivity() == null) return; // Pregunta si esta asociada esta actividad
                linearLoading.setVisibility(View.GONE);
                ArrayList<DataOrders> myOrder = response.body();
                if(myOrder.isEmpty()){
                    textViewRv.setVisibility(View.VISIBLE);
                    textViewRv.setText(getResources().getString(R.string.no_exist_lists));
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                OrdersAdapter booksAdapter = new OrdersAdapter(getActivity(), myOrder);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                recyclerView.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataOrders>> call, Throwable t) {
                if (getActivity() == null) return;
                ((HomeActivity)view.getContext()).setSnackbar(view.getResources().getString(R.string.no_connection));
                linearLoading.setVisibility(View.GONE);
                textViewRv.setVisibility(View.VISIBLE);
                textViewRv.setText(getResources().getString(R.string.error));
            }
        });
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }
}
