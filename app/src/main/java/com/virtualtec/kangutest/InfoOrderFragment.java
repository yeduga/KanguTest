package com.virtualtec.kangutest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.Datas.DataOrders;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 11/04/18.
 */

public class InfoOrderFragment extends Fragment {

    private View view;
    private DataOrders dataOrders;
    @BindView(R.id.textview_order_infoOrder) TextView textViewOrder;
    @BindView(R.id.textview_status_infoOrder) TextView textViewStatus;
    @BindView(R.id.textview_creation_date_infoOrder) TextView textViewCreationDate;
    @BindView(R.id.textview_delivery_date_infoOrder) TextView textViewDeliveryDate;
    @BindView(R.id.textview_products_infoOrder) TextView textViewProducts;
    @BindView(R.id.textview_articles_infoOrder) TextView textViewArticles;
    @BindView(R.id.textview_address_infoOrder) TextView textViewAddress;
    @BindView(R.id.textview_city_infoOrder) TextView textViewCity;
    @BindView(R.id.textview_total_ordersDetails) TextView textViewTotal;

    public static InfoOrderFragment newInstance() {
        InfoOrderFragment fragment = new InfoOrderFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Información de pedido");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info_order, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.info_order), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(4);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        dataOrders = (DataOrders) this.getArguments().getSerializable("arrayOrder");

        SetData();
    }

    private void SetData() {
        textViewOrder.setText("Pedido # " + dataOrders.getId());

        textViewStatus.setText("Estado: " + dataOrders.getStatus());
        textViewCreationDate.setText("Fecha de creación: " + dataOrders.getDate_created());
        textViewDeliveryDate.setText("Fecha de entrega: " );
        textViewProducts.setText("Productos: " );
        textViewArticles.setText("# de Artículos: " );

        textViewAddress.setText("Dirección: " + dataOrders.getBilling().getAddress_1());
        textViewCity.setText("Ciudad: " + dataOrders.getBilling().getCity());

        textViewTotal.setText("$ " + dataOrders.getTotal());
    }
}
