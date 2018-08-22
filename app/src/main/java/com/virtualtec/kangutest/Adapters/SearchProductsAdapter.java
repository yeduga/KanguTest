package com.virtualtec.kangutest.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.HeaderSearch;
import com.virtualtec.kangutest.Datas.SubDatas.DataImage;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.ProductDetailsFragment;
import com.virtualtec.kangutest.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Android on 17/04/18.
 */

public class SearchProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Activity mContext;
    private HeaderSearch headerSearch;
    private List<DataProducts> productList;
    NumberFormat format = NumberFormat.getInstance(Locale.US);

    public SearchProductsAdapter(Activity mContext, HeaderSearch headerSearch, List<DataProducts> productList) {
        this.mContext = mContext;
        this.headerSearch = headerSearch;
        this.productList = productList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_search_products, parent, false);
            return new VHHeader(v);
        }else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart, parent, false);
            return new VHItem(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    private DataProducts getItem(int position) { return productList.get(position); }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHHeader) {
            VHHeader VHheader = (VHHeader)holder;
            VHheader.txtTitle.setText(headerSearch.getHeader());
            VHheader.txtTitle2.setText(headerSearch.getHeader2());
        }else if(holder instanceof VHItem) {
            final DataProducts produc = getItem(position-1);
            // Unity
            String unity = "LBS";
            for ( int i = 0; i < produc.getAttributes().size(); i++) {
                if (produc.getAttributes().get(i).getName().contains("Unidad")) {
                    unity = produc.getAttributes().get(i).getOptions()[0];
                }
            }
            VHItem VHitem = (VHItem)holder;
            VHitem.overflow.setVisibility(View.GONE);
            VHitem.tvName.setText(produc.getName());
            VHitem.tvDescr.setText(Html.fromHtml(produc.getShort_description()));
            VHitem.tvPrice.setText("$ " + produc.getPrice() + "/" + unity);
            VHitem.tvQty.setText("");
            final ArrayList<DataImage> image = produc.getImages();
            if (image.get(0).getSrc() != null){
                Picasso.with(VHitem.imageProduct.getContext())
                        .load(image.get(0).getSrc())
                        .error( R.drawable.imagen_not_found )
                        .placeholder( R.drawable.logo_horizontal )
                        .into( VHitem.imageProduct );
            }

            VHitem.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("arrayProduct", produc);
                    ProductDetailsFragment nextFrag= new ProductDetailsFragment();
                    nextFrag.setArguments(bundle);
                    ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position)) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    public void clear() {
        final int size = productList.size();
        productList.clear();
        notifyItemRangeRemoved(0, size);
    }

    //increasing getItemcount to 1. This will be the row of header.
    @Override
    public int getItemCount() {
        return productList.size()+1;
    }

    class VHHeader extends RecyclerView.ViewHolder{
        @BindView(R.id.textview1_headerSearchProducts) TextView txtTitle;
        @BindView(R.id.textview2_headerSearchProducts) TextView txtTitle2;

        public VHHeader(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class VHItem extends RecyclerView.ViewHolder{
        @BindView(R.id.name_productCart) TextView tvName;
        @BindView(R.id.descr_productCart) TextView tvDescr;
        @BindView(R.id.qty_productCart) TextView tvQty;
        @BindView(R.id.price_productCart) TextView tvPrice;
        @BindView(R.id.image_productCart) ImageView imageProduct;
        @BindView(R.id.overflow_product_cart) ImageView overflow;
        @BindView(R.id.cv_productCart) CardView cardView;

        public VHItem(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
