package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.virtualtec.kangutest.Datas.SubDatas.DataLineItems;
import com.virtualtec.kangutest.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 11/04/18.
 */

public class ProductsOrderAdapter extends RecyclerView.Adapter<ProductsOrderAdapter.MyViewHolder>{

    private Activity mContext;
    private ArrayList<DataLineItems> dataLineItems;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_name_product_gridProductsOrder) TextView textViewName;
        @BindView(R.id.textview_qty_gridProductsOrder) TextView textViewQty;
        @BindView(R.id.textview_price_gridProductsOrder) TextView textViewPrice;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ProductsOrderAdapter(Activity mContext, ArrayList<DataLineItems> dataLineItems) {
        this.mContext = mContext;
        this.dataLineItems = dataLineItems;
    }

    @Override
    public ProductsOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_products_order, parent, false);
        return new ProductsOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductsOrderAdapter.MyViewHolder holder, int position) {
        final DataLineItems lineItems = dataLineItems.get(position);
        // Unity String
        String unity = "LBS";
        if (lineItems.getUnity() != null){
            unity = lineItems.getUnity();
        }
        String Qty = lineItems.getQuantity() + " " + unity;
        String Price = "$ " + lineItems.getPrice();
        holder.textViewName.setText(lineItems.getName());
        holder.textViewQty.setText(Qty);
        holder.textViewPrice.setText(Price);
    }

    @Override
    public int getItemCount() {
        return dataLineItems.size();
    }
}