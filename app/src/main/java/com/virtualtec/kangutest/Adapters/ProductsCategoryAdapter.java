package com.virtualtec.kangutest.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.BuildConfig;
import com.virtualtec.kangutest.CartFragment;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.Datas.SubDatas.DataImage;
import com.virtualtec.kangutest.HomeActivity;
import com.virtualtec.kangutest.ListsFragment;
import com.virtualtec.kangutest.ProductDetailsFragment;
import com.virtualtec.kangutest.R;
import com.virtualtec.kangutest.RestServiceApi.RestService;
import com.virtualtec.kangutest.SqlLite.DataBaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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
 * Created by Android on 22/02/18.
 */

public class ProductsCategoryAdapter extends RecyclerView.Adapter<ProductsCategoryAdapter.MyViewHolder>{

    private Context mContext;
    private List<DataProducts> productList;
    private DataBaseManager DBmanager;
    private int typeViewProducts;
    private RestService service;
    private Retrofit retrofit;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.badge_notification_producto_grid) TextView badgeNotification;
        @BindView(R.id.nombre_grid) TextView title;
        @BindView(R.id.text_precio) TextView precio;
        @BindView(R.id.text_precio_antiguo) TextView text_precio_ant;
        @BindView(R.id.image_producto_grid) ImageView imageProducto;
        @BindView(R.id.btn_add_cart_producto_grid) Button btnAddCart;
        @BindView(R.id.btn_add_list_producto_grid) Button btnAddList;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ProductsCategoryAdapter(Context mContext, List<DataProducts> productList, int typeViewProducts) {
        this.mContext = mContext;
        this.productList = productList;
        // 0 = vertical, 1 = horizontal
        this.typeViewProducts = typeViewProducts;
        InitRetrofit();
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(typeViewProducts == 0){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_products, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_products_horizontal, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final DataProducts produc = productList.get(position);
        DBmanager = new DataBaseManager(mContext);
        int qtyCart = Integer.parseInt(DBmanager.selectProductxIDQty(produc.getId()));
        holder.badgeNotification.setText(""+qtyCart);
        if (qtyCart == 0) holder.badgeNotification.setVisibility(View.GONE); else holder.badgeNotification.setVisibility(View.VISIBLE);

        // Unity
        String unity = "LBS";
        for ( int i = 0; i < produc.getAttributes().size(); i++) {
            if (produc.getAttributes().get(i).getName().contains("Unidad")) {
                unity = produc.getAttributes().get(i).getOptions()[0];
            }
        }
        holder.title.setText(produc.getName());
        holder.precio.setText("$ " + produc.getPrice() + "/" + unity);

        if(produc.getPrice().equals(produc.getRegular_price()) || produc.getType().equals("variable")){
            // No tiene descuento
            holder.precio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.text_precio_ant.setVisibility(View.GONE);
        }else{
            holder.precio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_precio_ant.setVisibility(View.VISIBLE);
            holder.text_precio_ant.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            holder.text_precio_ant.setPaintFlags(holder.text_precio_ant.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.text_precio_ant.setText("$ " + produc.getRegular_price() + "/" + unity);
        }

        final ArrayList<DataImage> image = produc.getImages();
        if (image.get(0).getSrc() != null){
            Picasso.with(holder.imageProducto.getContext())
                    .load(image.get(0).getSrc())
                    .error( R.drawable.imagen_not_found )
                    .placeholder( R.drawable.logo_horizontal )
                    .into( holder.imageProducto );

        }
        holder.imageProducto.setOnClickListener(new View.OnClickListener() {
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

        // Agregar al carrito
        holder.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NumberFormat format = NumberFormat.getCurrencyInstance();

                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_edit_product_cart);

                // set the custom dialog components - text, image and button
                final TextView textViewQtyDialog = (TextView) dialog.findViewById(R.id.textview_qty_dialog_productCart);
                TextView titleDialog = (TextView) dialog.findViewById(R.id.title_dialog_productCart);
                TextView priceDialog = (TextView) dialog.findViewById(R.id.price_dialog_productCart);
                TextView undDialog = (TextView) dialog.findViewById(R.id.textview_und_productCart);
                ImageView imageBtnLess = (ImageView) dialog.findViewById(R.id.image_btn_less_dialog_productCart);
                ImageView imageBtnMore = (ImageView) dialog.findViewById(R.id.image_btn_more_dialog_productCart);
                Button btnSave = (Button) dialog.findViewById(R.id.button_save_edit_productCart);
                // Seteo
                titleDialog.setText("Agregar " + produc.getName());
                textViewQtyDialog.setText("0");
                for ( int i = 0; i < produc.getAttributes().size(); i++) {
                    if (produc.getAttributes().get(i).getName().contains("Unidad")) {
                        undDialog.setText(produc.getAttributes().get(i).getOptions()[0]);
                    }
                }
                if (produc.getPrice().equals("")){
                    priceDialog.setText(format.format(0));
                }else{
                    priceDialog.setText(format.format(Integer.parseInt(produc.getPrice())));
                }
                btnSave.setText(mContext.getResources().getString(R.string.add_to_cart));

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
                            // Insert product to cart
                            String qty = textViewQtyDialog.getText().toString();
                            String[] mStrings = DBmanager.selectProductxID(produc.getId(), qty);
                            // Unity String
                            String unity = "LBS";
                            for ( int i = 0; i < produc.getAttributes().size(); i++) {
                                if (produc.getAttributes().get(i).getName().contains("Unidad")) {
                                    unity = produc.getAttributes().get(i).getOptions()[0];
                                }
                            }
                            if(mStrings[0].equals("1")){
                                int new_qty;
                                new_qty = (Integer.parseInt(qty) + Integer.parseInt(mStrings[1]));
                                DBmanager.updateProduct(produc.getId(), produc.getId(), produc.getName(), produc.getShort_description(), produc.getPrice(), unity, "", Integer.toString(new_qty), image.get(0).getSrc());
                                ((HomeActivity)mContext).setSnackbarAddCart(mContext.getResources().getString(R.string.updated_to_cart));
                            }else{
                                DBmanager.insertProduct(produc.getId(), produc.getId(), produc.getName(), produc.getShort_description(), produc.getPrice(), unity, "", qty, image.get(0).getSrc());
                                ((HomeActivity)mContext).setSnackbarAddCart(mContext.getResources().getString(R.string.added_to_cart));
                            }
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();

            }

            private void functMoreQty(TextView textViewQty) {
                int value = Integer.parseInt(textViewQty.getText().toString());
                value++;
                if(value > 1000){ value = 1000; }
                textViewQty.setText(Integer.toString(value));
            }

            private void functLessQty(TextView textViewQty) {
                int value = Integer.parseInt(textViewQty.getText().toString());
                value--;
                if(value < 0){ value = 0; }
                textViewQty.setText(Integer.toString(value));
            }
        });

        // Agregar a listas
        holder.btnAddList.setOnClickListener(new View.OnClickListener() {
            private Call<ArrayList<DataList>> dataLists;
            private ArrayList<DataList> myLists;
            private Call<DataResponse> dataResponse;

            @Override
            public void onClick(View view) {

                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_add_product_to_list);

                final RecyclerView rvListsDialog = (RecyclerView) dialog.findViewById(R.id.rv_lists_dialog_productList);
                final TextView textViewLoading = (TextView) dialog.findViewById(R.id.textview_loading_dialog_productList);
                final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add_productList);
                btnAdd.setVisibility(View.GONE);
                dataLists = service.Lists("list", ((HomeActivity)mContext).prefsIdUser);
                dataLists.enqueue(new Callback<ArrayList<DataList>>() {
                    @Override
                    public void onResponse(Call<ArrayList<DataList>> call, Response<ArrayList<DataList>> response) {
                        if (dialog == null) return; // Pregunta si esta asociada esta actividad
                        myLists = response.body();
                        if(myLists.isEmpty()){
                            textViewLoading.setText(mContext.getResources().getString(R.string.no_exist_lists));
                            return;
                        }
                        textViewLoading.setVisibility(View.GONE);
                        btnAdd.setVisibility(View.VISIBLE);
                        rvListsDialog.setVisibility(View.VISIBLE);
                        btnAdd.setEnabled(true);
                        ListCheckBoxAdapter booksAdapter = new ListCheckBoxAdapter(mContext, myLists);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        rvListsDialog.setLayoutManager(mLayoutManager);
                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                        ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                        rvListsDialog.setAdapter(scaleAdapter);
                    }
                    @Override
                    public void onFailure(Call<ArrayList<DataList>> call, Throwable t) {
                        if (dialog == null) return;
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                });

                // set the custom dialog components - text, image and button
                final TextView textViewQtyDialog = (TextView) dialog.findViewById(R.id.textview_qty_dialog_productList);
                TextView titleDialog = (TextView) dialog.findViewById(R.id.title_dialog_productList);
                TextView undDialog = (TextView) dialog.findViewById(R.id.textview_und_dialog_productList);
                ImageView imageBtnLess = (ImageView) dialog.findViewById(R.id.image_btn_less_dialog_productList);
                ImageView imageBtnMore = (ImageView) dialog.findViewById(R.id.image_btn_more_dialog_productList);
                // Seteo
                titleDialog.setText("Agregar " + produc.getName());
                textViewQtyDialog.setText("0");
                for ( int i = 0; i < produc.getAttributes().size(); i++) {
                    if (produc.getAttributes().get(i).getName().contains("Unidad")) {
                        undDialog.setText(produc.getAttributes().get(i).getOptions()[0]);
                    }
                }

                // Onclick's de QTY - Less More
                imageBtnLess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { functLessQtyDialog(textViewQtyDialog); }

                    private void functLessQtyDialog(TextView textViewQty) {
                        int value = Integer.parseInt(textViewQty.getText().toString());
                        value--;
                        if(value < 0){ value = 0; }
                        textViewQty.setText(Integer.toString(value));
                    }
                });
                imageBtnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { functMoreQtyDialog(textViewQtyDialog); }

                    private void functMoreQtyDialog(TextView textViewQty) {
                        int value = Integer.parseInt(textViewQty.getText().toString());
                        value++;
                        if(value > 1000){ value = 1000; }
                        textViewQty.setText(Integer.toString(value));
                    }
                });
                // Onclick Save
                btnAdd.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if (textViewQtyDialog.getText().toString().equals("0")){
                            Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.different_qty_of_0), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }else{
                            // Creacion JSON lists
                            int countTrue = 0;
                            JSONArray jsonArray = new JSONArray();
                            for (int k = 0; k < myLists.size(); k++){
                                if(myLists.get(k).isSelected() == true){
                                    countTrue++;
                                    JSONObject listObject = new JSONObject();
                                    try {
                                        listObject.put("id_list", myLists.get(k).getId_list());
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    jsonArray.put(listObject);
                                }
                            }
                            // Validacion seleccionando lista
                            if(countTrue == 0){
                                Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.select_a_list), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return;
                            }
                            // Peticion al API
                            String jsonStrLists = jsonArray.toString();
                            Log.d("Json Lists", jsonStrLists);
                            String qtyProduct = textViewQtyDialog.getText().toString();
                            Snackbar snackbar = Snackbar.make(((HomeActivity)mContext).coordinatorLayout, "Agregando producto...", Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            dataResponse = service.AddProductLits("add_product_lists", produc.getId(), qtyProduct, jsonStrLists);
                            dataResponse.enqueue(new Callback<DataResponse>() {
                                @Override
                                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                                    if(mContext == null) return;
                                    DataResponse myResponse = response.body();
                                    if (myResponse == null){
                                        ((HomeActivity)mContext).setSnackbar(mContext.getResources().getString(R.string.error));
                                        return;
                                    }
                                    Snackbar.make(((HomeActivity)mContext).coordinatorLayout, mContext.getResources().getString(R.string.products_added), Snackbar.LENGTH_LONG)
                                            .setActionTextColor(mContext.getResources().getColor(R.color.colorSnackbarAction))
                                            .setAction(mContext.getResources().getString(R.string.item_list), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    ((HomeActivity)mContext).getSupportFragmentManager().popBackStackImmediate();
                                                    ((HomeActivity)mContext).FunctFragmentTransaction(ListsFragment.newInstance());
                                                }
                                            })
                                            .show();
                                }
                                @Override
                                public void onFailure(Call<DataResponse> call, Throwable t) {
                                    if(mContext == null) return;
                                    ((HomeActivity)mContext).setSnackbar(mContext.getResources().getString(R.string.unknown_error));
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}