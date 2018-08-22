package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataOrders;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.OrderDetailsFragment;
import com.virtualtec.kangutest.R;
import com.virtualtec.kangutest.ScheduleListFragment;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 11/04/18.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder>{

    private Activity mContext;
    private ArrayList<DataOrders> dataOrders;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_order_gridOrders) TextView textViewOrder;
        @BindView(R.id.textview_status_gridOrders) TextView textViewStatus;
        @BindView(R.id.relative_body_gridOrders) RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public OrdersAdapter(Activity mContext, ArrayList<DataOrders> dataOrders) {
        this.mContext = mContext;
        this.dataOrders = dataOrders;
    }

    @Override
    public OrdersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_orders, parent, false);
        return new OrdersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrdersAdapter.MyViewHolder holder, int position) {
        final DataOrders order = dataOrders.get(position);
        holder.textViewOrder.setText(mContext.getResources().getString(R.string.order) + " # " + order.getId());
        holder.textViewStatus.setText(mContext.getResources().getString(R.string.status) + " " + order.getStatus());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("arrayOrder", order);
                OrderDetailsFragment nextFrag= new OrderDetailsFragment();
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
        return dataOrders.size();
    }
}