package com.virtualtec.kangutest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.Adapters.ListCheckBoxAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.Datas.SubDatas.DataImage;
import com.virtualtec.kangutest.RestServiceApi.RestService;
import com.virtualtec.kangutest.SqlLite.DataBaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android on 28/02/18.
 */

public class ProductDetailsFragment extends Fragment{

    private View view;
    private DataBaseManager DBmanager;
    private Retrofit retrofit;
    private RestService service;
    private DataProducts dataProduct;
    private Call<ArrayList<DataList>> dataLists;
    private ArrayList<DataList> myLists;
    private Call<DataResponse> dataResponse;
    @BindView(R.id.image_product) ImageView imageProduct;
    @BindView(R.id.textview_name_product) TextView textNameProduct;
    @BindView(R.id.textview_price_product) TextView textPriceProduct;
    @BindView(R.id.textview_descr_product) TextView textDescrProduct;
    @BindView(R.id.textview_qty_product_details) TextView textViewQty;
    @BindView(R.id.textview_und_product_details) TextView textViewUND;
    @BindView(R.id.spinner_maturity) Spinner spinnerMaturity;
    @BindView(R.id.linear_spinner_productDetails) LinearLayout linearSpinner;

    public static ProductDetailsFragment newInstance() {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Detalles del producto");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_details, container, false);
        ButterKnife.bind(this, view);

        // TODO-- title en action bar
        ((HomeActivity) getActivity()).setActionBarCustom("Detalles del producto", 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(1);

        Init();
        return view;
    }

    private void Init() {
        DBmanager = new DataBaseManager(getActivity());
        linearSpinner.setVisibility(View.GONE);
        dataProduct = (DataProducts) this.getArguments().getSerializable("arrayProduct");
        for ( int i = 0; i < dataProduct.getAttributes().size(); i++) {
            if (dataProduct.getAttributes().get(i).getName().contains("Madurez")) {
                linearSpinner.setVisibility(View.VISIBLE);
                spinnerMaturity.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dataProduct.getAttributes().get(i).getOptions()));
            }
            if (dataProduct.getAttributes().get(i).getName().contains("Unidad")) {
                textViewUND.setText(dataProduct.getAttributes().get(i).getOptions()[0]);
            }
        }

//        if (dataProduct.getAttributes().isEmpty()){
//            linearSpinner.setVisibility(View.GONE);
//        }else{
//            spinnerMaturity.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dataProduct.getAttributes().get(0).getOptions()));
//        }

        // ----------
        textNameProduct.setText(dataProduct.getName());
        if(dataProduct.getPrice().equals("")){ textPriceProduct.setText("$ 0.0"); }
        else{ textPriceProduct.setText("$ " + dataProduct.getPrice()); }
        textDescrProduct.setText(Html.fromHtml("<b>"+dataProduct.getName()+"</b>"+dataProduct.getShort_description()));
        ArrayList<DataImage> image = dataProduct.getImages();
        if (image.get(0).getSrc() != null){
            Picasso.with(getActivity())
                    .load(image.get(0).getSrc())
                    .error( R.drawable.imagen_not_found )
                    .placeholder( R.drawable.logo_horizontal )
                    .into( imageProduct );

        }
        InitRetrofit();
    }

    // TODO-- Buttons more & less - Qty
    @OnClick(R.id.image_btn_more_prod) public void functMoreQty(View v){
        int value = Integer.parseInt(textViewQty.getText().toString());
        value++;
        if(value > 1000){ value = 1000; }
        textViewQty.setText(Integer.toString(value));
    }
    @OnClick(R.id.image_btn_less_prod) public void functLessQty(View v){
        int value = Integer.parseInt(textViewQty.getText().toString());
        value--;
        if(value < 0){ value = 0; }
        textViewQty.setText(Integer.toString(value));
    }
    // TODO-- TextView Qty
    @OnClick(R.id.textview_qty_product_details) public void functTextViewQty(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cantidad");
        builder.setCancelable(false);

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                if(m_Text.isEmpty()){
                    textViewQty.setText("0");
                    return;
                }else if (m_Text.length() < 5){
                    int value = Integer.parseInt(m_Text);
                    if(value > 1001){
                        value = 1000;
                        ((HomeActivity) getActivity()).setSnackbar("Valor máximo: 1000");
                    }
                    textViewQty.setText(Integer.toString(value));
                    return;
                }
                ((HomeActivity) getActivity()).setSnackbar("Valor máximo: 1000");
                textViewQty.setText("1000");
            }
        });

        builder.show();

    }

    // Add to cart
    @OnClick(R.id.button_add_cart_product_details)
    public void functAddCartProduct(View view){
        String qty = textViewQty.getText().toString();
        ArrayList<DataImage> image = dataProduct.getImages();
        if (qty.equals("0")){
            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.different_qty_of_0));
        }else{
            // Capturar id de la variacion
            String variationName = spinnerMaturity.getSelectedItem().toString();
            String variationID = "";
            for ( int i = 0; i < dataProduct.getVariations().size(); i++) {
                if (dataProduct.getVariations().get(i).getAttributes().get(0).getOption().contains(variationName)) {
                    variationID = dataProduct.getVariations().get(i).getId();
                }
            }
            // Insert product to cart
            String[] mStrings = DBmanager.selectProductxID(dataProduct.getId(), variationID, qty);
            // Unity String
            String unity = "LBS";
            for ( int i = 0; i < dataProduct.getAttributes().size(); i++) {
                if (dataProduct.getAttributes().get(i).getName().contains("Unidad")) {
                    unity = dataProduct.getAttributes().get(i).getOptions()[0];
                }
            }

            if(mStrings[0].equals("1")){
                int new_qty = 0;
                new_qty = (Integer.parseInt(qty) + Integer.parseInt(mStrings[1]));
                DBmanager.updateProduct(dataProduct.getId(), variationID, dataProduct.getId(), dataProduct.getName(), dataProduct.getShort_description(), dataProduct.getPrice(), unity, "", Integer.toString(new_qty), image.get(0).getSrc());
                ((HomeActivity)getActivity()).setSnackbarAddCart(getResources().getString(R.string.updated_to_cart));
            }else{
                DBmanager.insertProduct(dataProduct.getId(), variationID, dataProduct.getId(), dataProduct.getName(), dataProduct.getShort_description(), dataProduct.getPrice(), unity, "", qty, image.get(0).getSrc());
                ((HomeActivity)getActivity()).setSnackbarAddCart(getResources().getString(R.string.added_to_cart));
            }
            textViewQty.setText("0");
        }
    }

    // Add to list
    @OnClick(R.id.button_add_list_product_details)
    public void functAddListProduct(View view){

        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_product_to_list);

        final RecyclerView rvListsDialog = (RecyclerView) dialog.findViewById(R.id.rv_lists_dialog_productList);
        final TextView textViewLoading = (TextView) dialog.findViewById(R.id.textview_loading_dialog_productList);
        final Button btnAdd = (Button) dialog.findViewById(R.id.btn_add_productList);
        btnAdd.setVisibility(View.GONE);
        dataLists = service.Lists("list", ((HomeActivity)getActivity()).prefsIdUser);
        dataLists.enqueue(new Callback<ArrayList<DataList>>() {
            @Override
            public void onResponse(Call<ArrayList<DataList>> call, Response<ArrayList<DataList>> response) {
                if (dialog == null) return; // Pregunta si esta asociada esta actividad
                myLists = response.body();
                if(myLists.isEmpty()){
                    textViewLoading.setText(getResources().getString(R.string.no_exist_lists));
                    return;
                }
                textViewLoading.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
                rvListsDialog.setVisibility(View.VISIBLE);
                btnAdd.setEnabled(true);
                ListCheckBoxAdapter booksAdapter = new ListCheckBoxAdapter(getContext(), myLists);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                rvListsDialog.setLayoutManager(mLayoutManager);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter (booksAdapter);
                ScaleInAnimationAdapter scaleAdapter=new ScaleInAnimationAdapter(alphaAdapter);
                rvListsDialog.setAdapter(scaleAdapter);
            }
            @Override
            public void onFailure(Call<ArrayList<DataList>> call, Throwable t) {
                if (dialog == null) return;
                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.no_connection));
            }
        });

        // set the custom dialog components - text, image and button
        final TextView textViewQtyDialog = (TextView) dialog.findViewById(R.id.textview_qty_dialog_productList);
        TextView titleDialog = (TextView) dialog.findViewById(R.id.title_dialog_productList);
        ImageView imageBtnLess = (ImageView) dialog.findViewById(R.id.image_btn_less_dialog_productList);
        ImageView imageBtnMore = (ImageView) dialog.findViewById(R.id.image_btn_more_dialog_productList);
        // Seteo
        titleDialog.setText("Agregar " + dataProduct.getName());
        textViewQtyDialog.setText("0");

        // Onclick's de QTY - Less More
        imageBtnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { functLessQtyDialog(textViewQtyDialog); }
        });
        imageBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { functMoreQtyDialog(textViewQtyDialog); }
        });
        // Onclick Save
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (textViewQtyDialog.getText().toString().equals("0")){
                    Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.different_qty_of_0), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    // Creacion JSON lists
                    int countTrue = 0;
                    JSONArray jsonArray = new JSONArray();
                    for (int k = 0; k < myLists.size(); k++){
                        if(myLists.get(k).isSelected() == true){
                            JSONObject listObject = new JSONObject();
                            try {
                                listObject.put("id_list", myLists.get(k).getId_list());
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            jsonArray.put(listObject);
                            countTrue++;
                        }
                    }
                    // Validacion seleccionando lista
                    if(countTrue == 0){
                        Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.select_a_list), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    // Peticion al API
                    String jsonStrLists = jsonArray.toString();
                    Log.d("Json Lists", jsonStrLists);
                    String qtyProduct = textViewQtyDialog.getText().toString();
                    Snackbar snackbar = Snackbar.make(((HomeActivity)getActivity()).coordinatorLayout, "Agregando producto...", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    dataResponse = service.AddProductLits("add_product_lists", dataProduct.getId(), qtyProduct, jsonStrLists);
                    dataResponse.enqueue(new Callback<DataResponse>() {
                        @Override
                        public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                            if(getActivity() == null) return;
                            DataResponse myResponse = response.body();
                            if (myResponse == null){
                                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.error));
                                return;
                            }
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.product_added_to_the_lists));
                        }
                        @Override
                        public void onFailure(Call<DataResponse> call, Throwable t) {
                            if(getActivity() == null) return;
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
                        }
                    });
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void functLessQtyDialog(TextView textViewQty) {
        int value = Integer.parseInt(textViewQty.getText().toString());
        value--;
        if(value < 0){ value = 0; }
        textViewQty.setText(Integer.toString(value));
    }

    private void functMoreQtyDialog(TextView textViewQty) {
        int value = Integer.parseInt(textViewQty.getText().toString());
        value++;
        if(value > 1000){ value = 1000; }
        textViewQty.setText(Integer.toString(value));
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }
}
