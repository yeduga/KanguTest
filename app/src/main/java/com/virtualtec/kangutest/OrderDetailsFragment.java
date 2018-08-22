package com.virtualtec.kangutest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.virtualtec.kangutest.Adapters.ProductsOrderAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.Datas.DataOrders;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by Android on 11/04/18.
 */

public class OrderDetailsFragment extends Fragment {

    private View view;
    private String globalIdOrder = "";
    private DataOrders dataOrders;
    @BindView(R.id.textview_order_ordersDetails) TextView textViewOrder;
    @BindView(R.id.textview_total_ordersDetails) TextView textViewTotal;
    @BindView(R.id.textview_status_ordersDetails) TextView textViewStatus;
    @BindView(R.id.rv_ordersDetails) RecyclerView recyclerView;
    @BindView(R.id.btn_pay_process_ordersDetails) Button btnPay;

    public static OrdersHistoryFragment newInstance() {
        OrdersHistoryFragment fragment = new OrdersHistoryFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Detalles del pedido");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.options_order, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_infoOrder){
            Bundle bundle = new Bundle();
            bundle.putSerializable("arrayOrder", dataOrders);
            InfoOrderFragment nextFrag= new InfoOrderFragment();
            nextFrag.setArguments(bundle);
            ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_details, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.order_details), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(4);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        dataOrders = (DataOrders) this.getArguments().getSerializable("arrayOrder");
        // Setter
        if(dataOrders.getStatus().equals("Pago pendiente")) btnPay.setVisibility(View.VISIBLE); else btnPay.setVisibility(View.GONE);
        textViewOrder.setText("Pedido # " + dataOrders.getId());
        textViewTotal.setText("$ " + dataOrders.getTotal());
        textViewStatus.setText(dataOrders.getStatus());

        btnPay.setVisibility(View.GONE);
        globalIdOrder = dataOrders.getId();
        LoadProducts();
    }

    private void LoadProducts() {
        ProductsOrderAdapter booksAdapter = new ProductsOrderAdapter(getActivity(), dataOrders.getLine_items());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
        ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
        recyclerView.setAdapter(scaleAdapter);
    }

    @OnClick(R.id.btn_pay_process_ordersDetails)
    public void OpenPay(){
        btnPay.animate().translationY(btnPay.getHeight()).alpha(0.0f).setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        btnPay.setVisibility(View.GONE);
                    }
                });
        Intent intent = new Intent(getActivity(), PaymentsActivity.class);
        intent.putExtra("idp", globalIdOrder);
        startActivity(intent);
    }

}
