package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.Datas.SubDatas.DataImage;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.ProductsCategoryFragment;
import com.virtualtec.kangutest.ProductsFragment;
import com.virtualtec.kangutest.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 23/02/18.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    private ProductsCategoryFragment fragment;
    private Activity mContext;
    private ArrayList<DataCategory> categoryList;
    private int typeViewCategories, statusClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_category_grid) TextView textViewCategory;
        @BindView(R.id.image_category_grid) ImageView imageCategory;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public CategoryAdapter(ProductsCategoryFragment fragment, Activity mContext, ArrayList<DataCategory> categoryList, int typeViewCategories, int statusClick) {
        this.fragment = fragment;
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.typeViewCategories = typeViewCategories;
        this.statusClick = statusClick; // 1 = Click en ProductosCategoria
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(typeViewCategories == 0){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_categories, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_categories_cardview, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DataCategory category = categoryList.get(position);
        holder.textViewCategory.setText(category.getName());

        final DataImage image = category.getImage();
        if (image != null){
            Picasso.with(holder.imageCategory.getContext())
                    .load(image.getSrc())
                    .error( R.drawable.logo_circular )
                    .placeholder( R.drawable.logo_circular )
                    .into( holder.imageCategory );
        }else{
            Picasso.with(holder.imageCategory.getContext())
                    .load(R.drawable.logo_circular)
                    .error( R.drawable.logo_circular )
                    .placeholder( R.drawable.logo_circular )
                    .into( holder.imageCategory );
        }
        holder.imageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ProductsFragment.LoadProducts(category.getId());
                String imageCategory = null;
                if (image != null) imageCategory = image.getSrc();
                if(statusClick == 1){
                    fragment.LoadTexts(category.getId(), category.getName(), imageCategory);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString("idCategory", category.getId());
                    bundle.putString("nameCategory", category.getName());
                    bundle.putString("imageCategory", imageCategory);
                    ProductsCategoryFragment nextFrag= new ProductsCategoryFragment();
                    nextFrag.setArguments(bundle);
                    ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    public String currency_price(String price){
        String currency;

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA);
        currency = format.format(Integer.parseInt(price));
        currency = currency.replace(".00", "");

        return currency;
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
