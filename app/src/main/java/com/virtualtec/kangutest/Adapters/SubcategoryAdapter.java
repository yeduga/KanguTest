package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.ProductsCategoryFragment;
import com.virtualtec.kangutest.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 4/04/18.
 */

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.MyViewHolder>{

    private Activity mContext;
    private ArrayList<DataCategory> dataCategory;
    private ProductsCategoryFragment fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_name_grid_subcategory) TextView btnName;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public SubcategoryAdapter(Activity mContext, ProductsCategoryFragment fragment, ArrayList<DataCategory> dataCategory) {
        this.mContext = mContext;
        this.dataCategory = dataCategory;
        this.fragment = fragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_subcategory_horizontal, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DataCategory category = dataCategory.get(position);
        holder.btnName.setText(category.getName());

        holder.btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.LoadProducts(category.getId());
            }
        });
    }

    @Override
    public int getItemCount() { return dataCategory.size(); }

}
