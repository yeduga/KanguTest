package com.virtualtec.kangutest.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.BuildConfig;
import com.virtualtec.kangutest.CartFragment;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.Datas.SubDatas.DataImage;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.ListDetailsFragment;
import com.virtualtec.kangutest.ProductDetailsFragment;
import com.virtualtec.kangutest.R;
import com.virtualtec.kangutest.RestServiceApi.RestService;
import com.virtualtec.kangutest.SqlLite.DataBaseManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
 * Created by Android on 22/03/18.
 */

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.MyViewHolder>{

    private Context mContext;
    private List<DataProducts> productList;
    private DataBaseManager DBmanager;
    private ListDetailsFragment fragment;
    private Retrofit retrofit;
    private RestService service;
    private Call<DataResponse> dataResponse;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_productCart) TextView tvName;
        @BindView(R.id.descr_productCart) TextView tvDescr;
        @BindView(R.id.qty_productCart) TextView tvQty;
        @BindView(R.id.price_productCart) TextView tvPrice;
        @BindView(R.id.image_productCart) ImageView imageProduct;
        @BindView(R.id.overflow_product_cart) ImageView overflow;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ProductsListAdapter(Context mContext, ListDetailsFragment fragment, List<DataProducts> productList) {
        this.mContext = mContext;
        this.productList = productList;
        this.fragment = fragment;
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

    @Override
    public ProductsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart, parent, false);
        return new ProductsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductsListAdapter.MyViewHolder holder, final int position) {
        final DataProducts produc = productList.get(position);
        // Unity String
        String unity = "LBS";
        if (produc.getUnity() != null){
            unity = produc.getUnity();
        }
        holder.tvName.setText(produc.getName());
        holder.tvDescr.setText(produc.getLong_description());
        holder.tvPrice.setText("$ " + produc.getPrice());
        holder.tvQty.setText(produc.getStock_quantity() + " " + unity);
        final ArrayList<DataImage> image = produc.getImages();
        if (!image.get(0).getSrc().equals("")){
            Picasso.with(holder.imageProduct.getContext())
                    .load(image.get(0).getSrc())
                    .error( R.drawable.imagen_not_found )
                    .placeholder( R.drawable.logo_horizontal )
                    .into( holder.imageProduct );
        }
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options_product_cart, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        int position;

        public MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            final DataProducts produc = productList.get(position);
            switch (menuItem.getItemId()) {
                case R.id.action_editProductCart: // Modificar Producto
                    // custom dialog
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_edit_product_cart);

                    // set the custom dialog components - text, image and button
                    final TextView textViewQtyDialog = (TextView) dialog.findViewById(R.id.textview_qty_dialog_productCart);
                    TextView titleDialog = (TextView) dialog.findViewById(R.id.title_dialog_productCart);
                    TextView priceDialog = (TextView) dialog.findViewById(R.id.price_dialog_productCart);
                    ImageView imageBtnLess = (ImageView) dialog.findViewById(R.id.image_btn_less_dialog_productCart);
                    ImageView imageBtnMore = (ImageView) dialog.findViewById(R.id.image_btn_more_dialog_productCart);
                    Button btnSave = (Button) dialog.findViewById(R.id.button_save_edit_productCart);
                    // Seteo
                    textViewQtyDialog.setText(produc.getStock_quantity());
                    titleDialog.setText("Editar " + produc.getName());
                    priceDialog.setText("$ " + produc.getPrice());
                    btnSave.setText(mContext.getResources().getString(R.string.save_changes));

                    // Onclick's de QTY - Less More
                    imageBtnLess.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { functLessQty(textViewQtyDialog); }
                    });
                    imageBtnMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { functMoreQty(textViewQtyDialog); }
                    });
                    // Onclick Save
                    btnSave.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            if (textViewQtyDialog.getText().toString().equals("0")){
                                Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.different_qty_of_0), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }else{
                                // Modificacion
                                dialog.dismiss();
                                fragment.functLoading(0);
                                dataResponse = service.UpdateProductxList("update_product_list", produc.getId_detail_list(), textViewQtyDialog.getText().toString());
                                dataResponse.enqueue(new Callback<DataResponse>() {
                                    @Override
                                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                                        if(mContext == null) return;
                                        DataResponse myResponse = response.body();
                                        if (myResponse == null){
                                            return;
                                        }
                                        Toast.makeText(mContext, mContext.getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();
                                        fragment.LoadProducts();
                                    }
                                    @Override
                                    public void onFailure(Call<DataResponse> call, Throwable t) {
                                        if(mContext == null) return;
                                        fragment.functLoading(1);
                                        ((HomeActivity)mContext).setSnackbar(mContext.getResources().getString(R.string.unknown_error));
                                    }
                                });
                            }
                        }
                    });
                    dialog.show();
                    return true;
                case R.id.action_deleteProductCart: // Eliminar Producto..
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle("Borrar de la lista");
                    alertDialogBuilder
                            .setMessage(Html.fromHtml("¿Desea eliminar <b>" + produc.getName() + "</b> de la lista?"))
                            .setCancelable(false)
                            .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    fragment.functLoading(0);
                                    dataResponse = service.DeleteProductxList("delete_product_list", produc.getId_detail_list());
                                    dataResponse.enqueue(new Callback<DataResponse>() {
                                        @Override
                                        public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                                            if(mContext == null) return;
                                            fragment.functLoading(1);
                                            DataResponse myResponse = response.body();
                                            if (myResponse == null){
                                                return;
                                            }
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.product_removed), Toast.LENGTH_SHORT).show();
                                            removeAt(position);
                                            if(getItemCount() == 0){ fragment.messageListEmpty(); }
                                        }
                                        @Override
                                        public void onFailure(Call<DataResponse> call, Throwable t) {
                                            if(mContext == null) return;
                                            fragment.functLoading(1);
                                            ((HomeActivity)mContext).setSnackbar(mContext.getResources().getString(R.string.unknown_error));
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialogBuilder.create().show();
                    return true;
                default:
            }
            return false;
        }

        private void functMoreQty(TextView textViewQty){
            int value = Integer.parseInt(textViewQty.getText().toString());
            value++;
            if(value > 1000){ value = 1000; }
            textViewQty.setText(Integer.toString(value));
        }
        private void functLessQty(TextView textViewQty){
            int value = Integer.parseInt(textViewQty.getText().toString());
            value--;
            if(value < 0){ value = 0; }
            textViewQty.setText(Integer.toString(value));
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void removeAt(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productList.size());
    }

}
