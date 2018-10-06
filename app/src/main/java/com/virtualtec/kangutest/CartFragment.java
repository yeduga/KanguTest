package com.virtualtec.kangutest;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.RestServiceApi.RestService;
import com.virtualtec.kangutest.SqlLite.DataBaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android on 27/02/18.
 */

public class  CartFragment extends Fragment {

    String data = "";
    private DataBaseManager DBmanager;
    private Cursor Cursor;
    private int totalCart, columnIndex, itemArticles;
    private Retrofit retrofit;
    private RestService service;
    private Call<DataResponse> dataResponse;
    ListAdapter listAdapter;
    @BindView(R.id.relative_body_cart) RelativeLayout relativeLayout;
    @BindView(R.id.listview_cart) ListView listViewCart;
    @BindView(R.id.textview_total_cart) TextView textviewTotal;
    @BindView(R.id.tv_info_cart) TextView textViewInfo;

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Carrito");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.options_cart, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_deleteCart){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Borrar carro");
            alertDialogBuilder
                    .setMessage(Html.fromHtml("¿Desea vaciar el carrito de compra?"))
                    .setCancelable(false)
                    .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            DBmanager.deleteAll();
                            Init();
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.done));
                        }
                    })
                    .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.item_cart), 0, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(2);

        Init();
        InitRetrofit();
        return view;
    }

    private void Init(){
        listAdapter = new ListAdapter(getActivity());

        DBmanager = new DataBaseManager(getActivity());
        Cursor = DBmanager.cursorKangu();
        listViewCart.setAdapter(listAdapter);
        totalCart = 0;
        itemArticles = 0;
        if (Cursor.moveToFirst()) {
            do {
                String tem_id_prod = Cursor.getString(Cursor.getColumnIndexOrThrow("id_product"));
                String tem_qty = Cursor.getString(Cursor.getColumnIndexOrThrow("qty"));
                String tem_unity = Cursor.getString(Cursor.getColumnIndexOrThrow("unity"));
                String tem_variation = Cursor.getString(Cursor.getColumnIndexOrThrow("id_variation"));
                itemArticles = itemArticles + Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("qty")));
                int subtotal = Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("price"))) * Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("qty")));
                totalCart = totalCart + subtotal;
                // do what ever you want here
                data += tem_id_prod + "$" + tem_qty + "$" + tem_unity + "$" + tem_variation + "$-";
            } while (Cursor.moveToNext());
        }

        if (itemArticles != 0) textViewInfo.setVisibility(View.GONE);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        textviewTotal.setText(format.format(totalCart));
    }

    @OnClick(R.id.btn_purchase_cart)
    public void functPurchase(){
        if(listAdapter.getCount() == 0){
            Toast.makeText(getActivity(), getResources().getString(R.string.no_products_cart), Toast.LENGTH_LONG).show();
        }else{
            DBmanager = new DataBaseManager(getActivity());
            Cursor = DBmanager.cursorKangu();
            listViewCart.setAdapter(listAdapter);
            totalCart = 0;
            itemArticles = 0;
            data = "";
            if (Cursor.moveToFirst()) {
                do {
                    String tem_id_prod = Cursor.getString(Cursor.getColumnIndexOrThrow("id_product"));
                    String tem_qty = Cursor.getString(Cursor.getColumnIndexOrThrow("qty"));
                    String tem_unity = Cursor.getString(Cursor.getColumnIndexOrThrow("unity"));
                    String tem_variation = Cursor.getString(Cursor.getColumnIndexOrThrow("id_variation"));
                    itemArticles = itemArticles + Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("qty")));
                    int subtotal = Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("price"))) * Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("qty")));
                    totalCart = totalCart + subtotal;
                    // do what ever you want here
                    data += tem_id_prod + "$" + tem_qty + "$" + tem_unity + "$" + tem_variation + "$-";
                } while (Cursor.moveToNext());
            }

            Bundle bundle = new Bundle();
            bundle.putString("strDataProducts", data);
            bundle.putString("itemProducts", String.valueOf(Cursor.getCount()));
            bundle.putString("itemArticles", String.valueOf(itemArticles));
            bundle.putString("totalPrice", String.valueOf(totalCart));
            bundle.putString("address", "");
            bundle.putString("idList", "");
            ProcessPurchaseFragment nextFrag= new ProcessPurchaseFragment();
            nextFrag.setArguments(bundle);
            ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    @OnClick(R.id.btn_createList_cart)
    public void functCreateList(){
        // Crear lista
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_input, null);

        TextView textDialog = (TextView) alertLayout.findViewById(R.id.text_dialog_input);
        TextInputLayout layoutTextDialog = (TextInputLayout) alertLayout.findViewById(R.id.layout_text_dialog_input);
        final TextView editTextDialog = (EditText) alertLayout.findViewById(R.id.edit_text_dialog_input);

        textDialog.setText("Crear una nueva lista con estos productos.");
        layoutTextDialog.setHint("Nombre lista");

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Crear lista");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editTextDialog.getText().toString().length() == 0){
                    Toast toast = Toast.makeText(getActivity(), "Nombre lista no debe estar vacio.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                // Creacion Json de Productos
                JSONArray jsonArray = new JSONArray();
                if (Cursor.moveToFirst()) {
                    do {
                        JSONObject product = new JSONObject();
                        try {
                            product.put("id_product", Cursor.getString(Cursor.getColumnIndexOrThrow("id_product")));
                            product.put("qty", Cursor.getString(Cursor.getColumnIndexOrThrow("qty")));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        jsonArray.put(product);
                    } while (Cursor.moveToNext());
                }
                // Peticion al API
                String jsonStr = jsonArray.toString();
                String idUser = ((HomeActivity)getActivity()).prefsIdUser;
                String nameList = editTextDialog.getText().toString();
                Snackbar snackbar = Snackbar.make(((HomeActivity)getActivity()).coordinatorLayout, "Creando lista...", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                dataResponse = service.CreateListProducts("add_list_product", idUser, nameList, "", jsonStr);
                dataResponse.enqueue(new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        if(getActivity() == null) return;
                        DataResponse myResponse = response.body();
                        if (myResponse == null){
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.error));
                            return;
                        }
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.done));
                    }
                    @Override
                    public void onFailure(Call<DataResponse> call, Throwable t) {
                        if(getActivity() == null) return;
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
                    }
                });
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    // Listado Cart Productos
    private class ListAdapter extends BaseAdapter {
        private Context context;

        public ListAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
            return Cursor.getCount();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                holder.tv_qty = new TextView(context);
                holder.tv_name = new TextView(context);
                holder.tv_price = new TextView(context);
                holder.tv_descr = new TextView(context);
                holder.image_prod = new ImageView(context);
                convertView = getActivity().getLayoutInflater().inflate(R.layout.custom_cart, parent, false);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Cursor.moveToPosition(position);
            int imageID = Cursor.getInt(columnIndex);

            //In Uri "" + imageID is to convert int into String as it only take String Parameter and imageID is in Integer format.
            //You can use String.valueOf(imageID) instead.
            Uri uri = Uri.withAppendedPath(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);

            //Setting Image to View Holder Image View.
            holder.tv_descr = (TextView) convertView.findViewById(R.id.descr_productCart);
            holder.tv_descr.setText(Html.fromHtml(Cursor.getString(Cursor.getColumnIndexOrThrow("descr"))));

            NumberFormat format = NumberFormat.getCurrencyInstance();
            holder.tv_price = (TextView) convertView.findViewById(R.id.price_productCart);
            holder.tv_price.setText(format.format(Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("price")))));

            holder.tv_name = (TextView) convertView.findViewById(R.id.name_productCart);
            holder.tv_name.setText(Cursor.getString(Cursor.getColumnIndexOrThrow("name")));

            holder.tv_qty = (TextView) convertView.findViewById(R.id.qty_productCart);
            holder.tv_qty.setText(Cursor.getString(Cursor.getColumnIndexOrThrow("qty")) + " " + Cursor.getString(Cursor.getColumnIndexOrThrow("unity")));

            holder.image_prod = (ImageView) convertView.findViewById(R.id.image_productCart);
            Picasso.with(holder.image_prod.getContext())
                    .load(Cursor.getString(Cursor.getColumnIndexOrThrow("image")))
                    .error(R.drawable.imagen_not_found)
                    .placeholder(R.drawable.logo_horizontal)
                    .into(holder.image_prod);

            final ViewHolder finalHolder = holder;
            holder.overflow = (ImageView) convertView.findViewById(R.id.overflow_product_cart);
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(finalHolder.overflow, position);
                }
            });

            return convertView;
        }

        // View Holder pattern used for Smooth Scrolling. As View Holder pattern recycle the findViewById() object.
        class ViewHolder {
            private TextView tv_price;
            private TextView tv_qty;
            private TextView tv_name;
            private TextView tv_descr;
            private ImageView image_prod;
            private ImageView overflow;
        }

        /**
         * Showing popup menu when tapping on 3 dots
         */
        private void showPopupMenu(View view, int position) {
            // inflate menu
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.options_product_cart, popup.getMenu());
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
            popup.show();
        }

        /**
         * Click listener for popup menu items
         **/
        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

            int position;
            int columnIndex;

            public MyMenuItemClickListener(int position) {
                this.position = position;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Cursor.moveToPosition(position);
                switch (menuItem.getItemId()) {
                    case R.id.action_editProductCart:
                        NumberFormat format = NumberFormat.getCurrencyInstance();

                        // custom dialog
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_edit_product_cart);

                        // set the custom dialog components - text, image and button
                        final TextView textViewQtyDialog = (TextView) dialog.findViewById(R.id.textview_qty_dialog_productCart);
                        TextView titleDialog = (TextView) dialog.findViewById(R.id.title_dialog_productCart);
                        TextView priceDialog = (TextView) dialog.findViewById(R.id.price_dialog_productCart);
                        TextView undDialog = (TextView) dialog.findViewById(R.id.textview_und_productCart);
                        ImageView imageBtnLess = (ImageView) dialog.findViewById(R.id.image_btn_less_dialog_productCart);
                        ImageView imageBtnMore = (ImageView) dialog.findViewById(R.id.image_btn_more_dialog_productCart);
                        Button btnSave = (Button) dialog.findViewById(R.id.button_save_edit_productCart);
                        // Setteo
                        textViewQtyDialog.setText(Cursor.getString(Cursor.getColumnIndexOrThrow("qty")));
                        titleDialog.setText("Editar " + Cursor.getString(Cursor.getColumnIndexOrThrow("name")));
                        undDialog.setText(Cursor.getString(Cursor.getColumnIndexOrThrow("unity")));
                        priceDialog.setText(format.format(Integer.parseInt(Cursor.getString(Cursor.getColumnIndexOrThrow("price")))));
                        btnSave.setText(getResources().getString(R.string.save_changes));

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
                                    Toast toast = Toast.makeText(context, getResources().getString(R.string.different_qty_of_0), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }else{
                                    DBmanager.updateProduct(Cursor.getString(Cursor.getColumnIndexOrThrow("id_product")), Cursor.getString(Cursor.getColumnIndexOrThrow("id_variation")), Cursor.getString(Cursor.getColumnIndexOrThrow("id_product")), Cursor.getString(Cursor.getColumnIndexOrThrow("name")), Cursor.getString(Cursor.getColumnIndexOrThrow("descr")), Cursor.getString(Cursor.getColumnIndexOrThrow("price")), Cursor.getString(Cursor.getColumnIndexOrThrow("unity")), "", textViewQtyDialog.getText().toString(), Cursor.getString(Cursor.getColumnIndexOrThrow("image")));
                                    dialog.dismiss();
                                    ((HomeActivity)getActivity()).setSnackbar("Hecho");
                                    Init();
                                }
                            }
                        });
                        dialog.show();

                        return true;
                    case R.id.action_deleteProductCart:
                        DBmanager.deleteProduct(Cursor.getString(Cursor.getColumnIndexOrThrow("id_product")), Cursor.getString(Cursor.getColumnIndexOrThrow("id_variation")));
                        final Cursor CursorRedo = Cursor;
                        Snackbar.make(relativeLayout, Cursor.getString(Cursor.getColumnIndexOrThrow("name")) + " borrado", Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.colorSnackbarAction))
                                .setAction(getResources().getString(R.string.redo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DBmanager.insertProduct(CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("id_product")),
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("id_variation")),
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("id_product")),
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("name")),
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("descr")),
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("price")),
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("unity")),
                                                "",
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("qty")),
                                                CursorRedo.getString(CursorRedo.getColumnIndexOrThrow("image")));
                                        ((HomeActivity)getActivity()).setSnackbar("Hecho");
                                        Init();
                                    }
                                })
                                .show();
                        Init();
                        return true;
                    default:
                }
                return false;
            }

            public void functMoreQty(TextView textViewQty){
                int value = Integer.parseInt(textViewQty.getText().toString());
                value++;
                if(value > 1000){ value = 1000; }
                textViewQty.setText(Integer.toString(value));
            }
            public void functLessQty(TextView textViewQty){
                int value = Integer.parseInt(textViewQty.getText().toString());
                value--;
                if(value < 0){ value = 0; }
                textViewQty.setText(Integer.toString(value));
            }
        }
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

}
