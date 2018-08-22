package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataResponseDelivery;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.ListDetailsFragment;
import com.virtualtec.kangutest.OrderDetailsFragment;
import com.virtualtec.kangutest.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 23/04/18.
 */

public class OrdersDeliveryAdapter extends RecyclerView.Adapter<OrdersDeliveryAdapter.MyViewHolder>{

    private Activity mContext;
    private ArrayList<DataResponseDelivery> dataResponseDelivery;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_year_gridOrdersDelivery) TextView textViewYear;
        @BindView(R.id.textview_month_gridOrdersDelivery) TextView textViewMonth;
        @BindView(R.id.textview_day_gridOrdersDelivery) TextView textViewDay;
        @BindView(R.id.textview_name_gridOrdersDelivery) TextView textViewName;
        @BindView(R.id.textview_status_gridOrdersDelivery) TextView textViewStatus;
        @BindView(R.id.relative_body_gridOrdersDelivery) RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public OrdersDeliveryAdapter(Activity mContext, ArrayList<DataResponseDelivery> dataResponseDelivery) {
        this.mContext = mContext;
        this.dataResponseDelivery = dataResponseDelivery;
    }

    @Override
    public OrdersDeliveryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_orders_delivery, parent, false);
        return new OrdersDeliveryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrdersDeliveryAdapter.MyViewHolder holder, int position) {
        final DataResponseDelivery delivery = dataResponseDelivery.get(position);
        holder.textViewYear.setText(delivery.getYear());
        holder.textViewMonth.setText(delivery.getMonth());
        holder.textViewDay.setText(delivery.getDay());
        holder.textViewName.setText(delivery.getName_list());
        holder.textViewStatus.setText(delivery.getName_list());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DataList list = new DataList(delivery.getId_list(), delivery.getName_list(), delivery.getAddress(), delivery.getDate_creation(), delivery.getDate_delivery(), delivery.getReminder());
                Bundle bundle = new Bundle();
                bundle.putSerializable("arrayList", list);
                ListDetailsFragment nextFrag= new ListDetailsFragment();
                nextFrag.setArguments(bundle);
                ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataResponseDelivery.size();
    }
}
