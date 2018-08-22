package com.virtualtec.kangutest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.virtualtec.kangutest.Adapters.OrdersAdapter;
import com.virtualtec.kangutest.Adapters.OrdersDeliveryAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataOrders;
import com.virtualtec.kangutest.Datas.DataResponseDelivery;
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
 * Created by Android on 23/04/18.
 */

public class CalendarListFragment extends Fragment{

    private View view;
    private Retrofit retrofit;
    private RestService service;
    private Call<ArrayList<DataResponseDelivery>> dataResponseDelivery;
    @BindView(R.id.loading_calendarList) GifView gifLoading;
    @BindView(R.id.rv_calendarList) RecyclerView recyclerView;
    @BindView(R.id.textview_rv_calendarList) TextView textViewRv;
    @BindView(R.id.linear_loading_calendarList) LinearLayout linearLoading;

    public static OrdersHistoryFragment newInstance() {
        OrdersHistoryFragment fragment = new OrdersHistoryFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Calendario");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar_list, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.calendar), 1, getActivity());
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
        dataResponseDelivery = service.ListDelivery("delivery",((HomeActivity)getActivity()).prefsIdUser);
        dataResponseDelivery.enqueue(new Callback<ArrayList<DataResponseDelivery>>() {
            @Override
            public void onResponse(Call<ArrayList<DataResponseDelivery>> call, Response<ArrayList<DataResponseDelivery>> response) {
                if (getActivity() == null) return; // Pregunta si esta asociada esta actividad
                linearLoading.setVisibility(View.GONE);
                ArrayList<DataResponseDelivery> myDelivery = response.body();
                if(myDelivery.isEmpty()){
                    textViewRv.setVisibility(View.VISIBLE);
                    textViewRv.setText(getResources().getString(R.string.no_exist_lists));
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                OrdersDeliveryAdapter booksAdapter = new OrdersDeliveryAdapter(getActivity(), myDelivery);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                recyclerView.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataResponseDelivery>> call, Throwable t) {
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
