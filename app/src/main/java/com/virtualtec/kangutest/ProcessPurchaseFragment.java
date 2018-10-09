package com.virtualtec.kangutest;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.virtualtec.kangutest.Adapters.AddressAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.DatePickerFragment;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.ClassesCustom.TimePickerFragment;
import com.virtualtec.kangutest.Datas.DataAddress;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.Datas.DataUser;
import com.virtualtec.kangutest.RestServiceApi.RestService;
import com.virtualtec.kangutest.SqlLite.DataBaseManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

/**
 * Created by Android on 2/04/18.
 */

public class ProcessPurchaseFragment extends Fragment {

    private String globalIdOrder = "", strDataProducts, itemProducts, itemArticles, totalPrice, address, idList;
    private DataBaseManager DBmanager;
    private Cursor Cursor;
    private AwesomeValidation awesomeValidation;
    private View view;
    private Retrofit retrofit;
    private RestService service;
    private Call<DataResponse> dataResponse;
    private Call<ArrayList<DataAddress>> dataAddress;
    private ArrayList<DataAddress> myAddress;
    private ArrayList<DataUser> arrayUser;
    @BindView(R.id.loading_processPurchase) GifView gifLoading;
    @BindView(R.id.linear_loading_processPurchase) LinearLayout linearLoading;
    @BindView(R.id.linear_content_processPurchase) LinearLayout linearContent;
    @BindView(R.id.textview_itemProducts_processPurchase) TextView textViewItemProducts;
    @BindView(R.id.textview_itemArticles_processPurchase) TextView textViewItemArticles;
    @BindView(R.id.textview_total_processPurchase) TextView textViewTotal;
    @BindView(R.id.edit_text_schedule_processPurchase) EditText editTextSchedule;
    @BindView(R.id.edit_text_time_processPurchase) EditText editTextTime;
    @BindView(R.id.edit_text_address_processPurchase) EditText editTextAddress;
    @BindView(R.id.imagebtn_view_address_processPurchase) ImageButton btnViewAddress;
    @BindView(R.id.btn_process_purchase) Button btnProcessPurchase;
    @BindView(R.id.btn_pay_process_purchase) Button btnPay;
    @BindView(R.id.btn_pay_againstdelivery_process_purchase) Button btnPayAgainstdelivery;
    @BindView(R.id.text_methods_payments_processPurchase) TextView textViewMethods;
    @BindView(R.id.image_methods_payments_processPurchase) ImageView imageMethods;


    public static ProcessPurchaseFragment newInstance() {
        ProcessPurchaseFragment fragment = new ProcessPurchaseFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Procesar compra");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_process_purchase, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.process_purchase), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(2);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        DBmanager = new DataBaseManager(getActivity());
        Cursor = DBmanager.cursorKangu();

        gifLoading.loadGIFResource(R.drawable.cargando_verde);
        strDataProducts = this.getArguments().getString("strDataProducts");
        itemProducts = this.getArguments().getString("itemProducts");
        itemArticles = this.getArguments().getString("itemArticles");
        totalPrice = this.getArguments().getString("totalPrice");
        address = this.getArguments().getString("address");
        idList = this.getArguments().getString("idList");
        // Validation Form
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(editTextAddress, RegexTemplate.NOT_EMPTY, getResources().getString(R.string.addresserror));
        awesomeValidation.addValidation(editTextSchedule, RegexTemplate.NOT_EMPTY, getResources().getString(R.string.scheduleerror));
        awesomeValidation.addValidation(editTextTime, RegexTemplate.NOT_EMPTY, getResources().getString(R.string.timeerror));
        // Animations
        // btnPay.setVisibility(View.GONE);
        // btnPayAgainstdelivery.setVisibility(View.GONE);

        InitRetrofit();
        setData();
        setValidIdList();
        ((HomeActivity)getActivity()).validSesion();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        textViewItemProducts.setText(itemProducts);
        textViewItemArticles.setText(itemArticles);
        textViewTotal.setText("$ " + totalPrice);
    }

    private void setValidIdList() {
        // btnViewAddress.setVisibility(View.GONE);
        editTextAddress.setText(address);
    }

    @OnClick(R.id.btn_process_purchase)
    public void functProcessPurchase(){
//        if (awesomeValidation.validate()){
//            functLoading(0);
//            arrayUser = ((HomeActivity)getActivity()).ArrayUser;
//            String scheduleText = editTextSchedule.getText().toString() + " " + editTextTime.getText().toString();
//            dataResponse = service.CreateOrder("create_order", arrayUser.get(0).getId(),
//                    arrayUser.get(0).getFirst_name(), "", editTextAddress.getText().toString(),
//                    "", arrayUser.get(0).getBilling().getCity(), "", "", "",
//                    arrayUser.get(0).getBilling().getPhone(), arrayUser.get(0).getEmail(), strDataProducts,
//                    scheduleText);
//            dataResponse.enqueue(new Callback<DataResponse>() {
//                @Override
//                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
//                    if(getActivity() == null) return;
//                    functLoading(1);
//                    DataResponse myResponse = response.body();
//                    if (myResponse == null){
//                        Toast.makeText(getActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    globalIdOrder = myResponse.getId();
//                    // Disabled
//                    imageMethods.setVisibility(View.GONE);
//                    textViewMethods.setVisibility(View.GONE);
//                    btnProcessPurchase.setVisibility(View.GONE);
//                    editTextAddress.setEnabled(false);
//                    editTextSchedule.setEnabled(false);
//                    // Animations
//                    btnPay.setVisibility(View.VISIBLE);
//                    btnPayAgainstdelivery.setVisibility(View.VISIBLE);
//                    // Delete All Cart
//                    DBmanager.deleteAll();
//                    // CONFIRMACION
//                    Toast.makeText(getActivity(), "Orden generada, por favor procesa con el pago.", Toast.LENGTH_LONG).show();
//                }
//                @Override
//                public void onFailure(Call<DataResponse> call, Throwable t) {
//                    if(getActivity() == null) return;
//                    functLoading(1);
//                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
//                }
//            });
//        }
    }

    @OnClick(R.id.btn_pay_process_purchase)
    public void OpenPay(){
        if (awesomeValidation.validate()){
            functLoading(0);
            arrayUser = ((HomeActivity)getActivity()).ArrayUser;
            String scheduleText = editTextSchedule.getText().toString() + " " + editTextTime.getText().toString();
            dataResponse = service.CreateOrder("create_order", arrayUser.get(0).getId(),
                    arrayUser.get(0).getFirst_name(), "", editTextAddress.getText().toString(),
                    "", arrayUser.get(0).getBilling().getCity(), "", "", "",
                    arrayUser.get(0).getBilling().getPhone(), arrayUser.get(0).getEmail(), strDataProducts,
                    scheduleText);
            dataResponse.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if(getActivity() == null) return;
                    functLoading(1);
                    DataResponse myResponse = response.body();
                    if (myResponse == null){
                        Toast.makeText(getActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    globalIdOrder = myResponse.getId();
                    // Disabled
                    imageMethods.setVisibility(View.GONE);
                    textViewMethods.setVisibility(View.GONE);
                    btnProcessPurchase.setVisibility(View.GONE);
                    editTextAddress.setEnabled(false);
                    editTextSchedule.setEnabled(false);
                    editTextTime.setEnabled(false);
                    // Delete All Cart
                    DBmanager.deleteAll();
                    // Envio a pagar
                    btnPay.animate().translationY(btnPay.getHeight()).alpha(0.0f).setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    btnPay.setVisibility(View.GONE);
                                    btnPayAgainstdelivery.setVisibility(View.GONE);
                                }
                            });
                    Intent intent = new Intent(getActivity(), PaymentsActivity.class);
                    intent.putExtra("idp", globalIdOrder);
                    startActivity(intent);
                    // CONFIRMACION
                    Toast.makeText(getActivity(), "Orden generada, por favor procesa con el pago.", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    if(getActivity() == null) return;
                    functLoading(1);
                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
                }
            });
        }
    }

    @OnClick(R.id.btn_pay_againstdelivery_process_purchase)
    public void OpenPayAgainstdelivery(){
        if (awesomeValidation.validate()){
            functLoading(0);
            arrayUser = ((HomeActivity)getActivity()).ArrayUser;
            String scheduleText = editTextSchedule.getText().toString() + " " + editTextTime.getText().toString();
            dataResponse = service.CreateOrder("create_order", arrayUser.get(0).getId(),
                    arrayUser.get(0).getFirst_name(), "", editTextAddress.getText().toString(),
                    "", arrayUser.get(0).getBilling().getCity(), "", "", "",
                    arrayUser.get(0).getBilling().getPhone(), arrayUser.get(0).getEmail(), strDataProducts,
                    scheduleText);
            dataResponse.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if(getActivity() == null) return;
                    functLoading(1);
                    DataResponse myResponse = response.body();
                    if (myResponse == null){
                        Toast.makeText(getActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    globalIdOrder = myResponse.getId();
                    // Disabled
                    imageMethods.setVisibility(View.GONE);
                    textViewMethods.setVisibility(View.GONE);
                    btnProcessPurchase.setVisibility(View.GONE);
                    editTextAddress.setEnabled(false);
                    editTextSchedule.setEnabled(false);
                    editTextTime.setEnabled(false);
                    // Delete All Cart
                    DBmanager.deleteAll();
                    // Proceso pago contra entrega
                    btnPayAgainstdelivery.animate().translationY(btnPayAgainstdelivery.getHeight()).alpha(0.0f).setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    btnPay.setVisibility(View.GONE);
                                    btnPayAgainstdelivery.setVisibility(View.GONE);
                                }
                            });
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Pago Contra Entrega");
                    alertDialog.setMessage("Recuerde tener el dinero a la mano cuando llegue el domiciliario a la dirección de envío.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.order_made));
                                }
                            });
                    alertDialog.show();
                    Toast.makeText(getActivity(), "Orden generada correctamente.", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    if(getActivity() == null) return;
                    functLoading(1);
                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
                }
            });
        }
    }

    @OnClick(R.id.imagebtn_view_address_processPurchase)
    public void functViewAddres(){
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_address_purchase);

        final ListView lvAddressDialog = (ListView) dialog.findViewById(R.id.listview_address_dialog_AddAddressPurchase);
        final TextView textViewLoading = (TextView) dialog.findViewById(R.id.textview_loading_dialog_AddAddressPurchase);
        dataAddress = service.AddressxUser("view_address", ((HomeActivity)getActivity()).prefsIdUser);
        dataAddress.enqueue(new Callback<ArrayList<DataAddress>>() {
            @Override
            public void onResponse(Call<ArrayList<DataAddress>> call, Response<ArrayList<DataAddress>> response) {
                if (dialog == null) return;
                myAddress = response.body();
                if (myAddress.isEmpty()) {
                    dialog.cancel();
                    ((HomeActivity)getActivity()).ToastMessageCenter(getResources().getString(R.string.no_saved_addresses));
                }else{
                    textViewLoading.setVisibility(View.GONE);
                    lvAddressDialog.setVisibility(View.VISIBLE);
                    AddressAdapter adapter = new AddressAdapter(getActivity(), myAddress, 0, ProfileFragment.newInstance());
                    lvAddressDialog.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DataAddress>> call, Throwable t) {
                if (dialog == null) return;
                ((HomeActivity)getActivity()).ToastMessageCenter(getResources().getString(R.string.no_connection));
            }
        });
        // Clic en una direccion
        lvAddressDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                final DataAddress address = myAddress.get(position);
                editTextAddress.setText(address.getAddress());
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.edit_text_schedule_processPurchase)
    public void functDatePicker(){
        showDatePickerDialog();
    }

    @OnClick(R.id.edit_text_time_processPurchase)
    public void functTimePicker(){
        showTimePickerDialog();
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = twoDigits(day) + "-" + twoDigits((month+1)) + "-" + year;
                editTextSchedule.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final String selectedDate = twoDigits(hourOfDay) + ":" + twoDigits(minute);
                editTextTime.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    private void functLoading(int status) {
        if (status == 0){
            linearLoading.setVisibility(View.VISIBLE); linearContent.setVisibility(View.GONE);
        }else{
            linearLoading.setVisibility(View.GONE); linearContent.setVisibility(View.VISIBLE);
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