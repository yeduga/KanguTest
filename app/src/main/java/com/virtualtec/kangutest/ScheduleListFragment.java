package com.virtualtec.kangutest;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.virtualtec.kangutest.Adapters.AddressAdapter;
import com.virtualtec.kangutest.App.MyApplication;
import com.virtualtec.kangutest.ClassesCustom.DatePickerFragment;
import com.virtualtec.kangutest.ClassesCustom.GifView;
import com.virtualtec.kangutest.Datas.DataAddress;
import com.virtualtec.kangutest.Datas.DataList;
import com.virtualtec.kangutest.Datas.DataResponse;
import com.virtualtec.kangutest.RestServiceApi.RestService;

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
 * Created by Android on 4/04/18.
 */

public class ScheduleListFragment extends Fragment {

    private View view;
    private AwesomeValidation aweValAddress;
    private Retrofit retrofit;
    private RestService service;
    private String scheduleGlobal;
    private DataList dataList;
    private Call<ArrayList<DataAddress>> dataAddress;
    private ArrayList<DataAddress> myAddress;
    private Call<DataResponse> dataResponse;
    @BindView(R.id.textview_loading_scheduleList) TextView textViewLoading;
    @BindView(R.id.textview_namelist_scheduleList) TextView textViewNameList;
    @BindView(R.id.edit_text_address_scheduleList) EditText editTextAddress;
    @BindView(R.id.edit_text_delivery_scheduleList) EditText editTextDelivery;
    @BindView(R.id.textview_dd_scheduleList) TextView textViewDD;
    @BindView(R.id.textview_mm_scheduleList) TextView textViewMM;
    @BindView(R.id.textview_aaaa_scheduleList) TextView textViewAAAA;
    @BindView(R.id.listview_address_scheduleList) ListView listViewAddress;
    @BindView(R.id.linear_scheduleList) LinearLayout linearLoading;
    @BindView(R.id.loading_scheduleList) GifView gifLoading;

    public static ScheduleListFragment newInstance() {
        ScheduleListFragment fragment = new ScheduleListFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Fragmento Programar lista");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        // Set title bar
        ((HomeActivity) getActivity()).setActionBarCustom(getResources().getString(R.string.schedule_list), 1, getActivity());
        ((HomeActivity) getActivity()).CheckedNavigationView(3);

        ButterKnife.bind(this, view);

        Init();
        return view;
    }

    private void Init() {
        gifLoading.loadGIFResource(R.drawable.cargando_verde);

        dataList = (DataList) this.getArguments().getSerializable("arrayList");
        // Validation Form Address
        aweValAddress = new AwesomeValidation(BASIC);
        aweValAddress.addValidation(editTextAddress, RegexTemplate.NOT_EMPTY, getResources().getString(R.string.addresserror));

        InitRetrofit();
        SetDatas(dataList.getAddress());
        LoadAddress();
        SetOnClickAddress();
    }

    private void shuffleItems() { LoadAddress(); }

    private void SetDatas(String address) {
        editTextAddress.setText(address);
        textViewNameList.setText(dataList.getName_list());
        // Seteo Horario Global
        if (dataList.getReminder().equals("0")) editTextDelivery.setText(""); else editTextDelivery.setText(dataList.getReminder());
        if(!dataList.getDate_delivery().equals("0000-00-00")){
            scheduleGlobal = dataList.getDate_delivery();
            String[] parts = scheduleGlobal.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];
            SetDateTextView(day, month, year);
        }else{
            scheduleGlobal = "";
            SetDateTextView("DD","MM","AAAA");
        }
    }

    private void LoadAddress() {
        textViewLoading.setVisibility(View.GONE);
        final Snackbar snackbar = Snackbar.make(((HomeActivity)getActivity()).coordinatorLayout, getResources().getString(R.string.loading), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        dataAddress = service.AddressxUser("view_address", ((HomeActivity)getActivity()).prefsIdUser);
        dataAddress.enqueue(new Callback<ArrayList<DataAddress>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<ArrayList<DataAddress>> call, Response<ArrayList<DataAddress>> response) {
                myAddress = response.body();
                snackbar.dismiss();
                if (myAddress.size() == 0){
                    textViewLoading.setText("No tiene direcciones guardadas.");
                    textViewLoading.setVisibility(View.VISIBLE);
                }
                AddressAdapter listAdapter = new AddressAdapter(getActivity(), myAddress, 0, ProfileFragment.newInstance());
                listViewAddress.setAdapter(listAdapter);
                RefreshHeightList(listAdapter, listViewAddress);
            }

            @Override
            public void onFailure(Call<ArrayList<DataAddress>> call, Throwable t) {
                ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.no_connection));
            }
        });
    }

    private void SetOnClickAddress() {
        // Click en direccion lista
        listViewAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                final DataAddress address = myAddress.get(position);
                //CODIGO AQUI
                SetDatas(address.getAddress());
            }
        });
    }

    // Seteo Horario
    private void SetDateTextView(String day, String month, String year) {
        textViewDD.setText(day);
        textViewMM.setText(month);
        textViewAAAA.setText(year);
    }

    @OnClick(R.id.btn_view_scheduleList)
    public void OpenViewList(){

    }

    @OnClick(R.id.linear_text_schedule_scheduleList)
    public void functDatePicker(){ showDatePickerDialog(); }

    // Suscribir lista
    @OnClick(R.id.btn_program_scheduleList)
    public void functProgramList(){
        if (aweValAddress.validate()){
            if (scheduleGlobal.equals("")){
                ((HomeActivity)getActivity()).ToastMessageCenter("Por favor, seleccione una fecha.");
            }else{
                functLoading(0, "Procesando...");
                String strAddress = editTextAddress.getText().toString();
                String reminder = "0";
                if (editTextDelivery.getText().toString().equals("")) reminder = "0";
                else reminder = editTextDelivery.getText().toString();
                dataResponse = service.AddDelivery("add_delivery", dataList.getId_list(), strAddress, reminder, scheduleGlobal);
                dataResponse.enqueue(new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        if(getActivity() == null) return;
                        DataResponse myResponse = response.body();
                        if (myResponse == null){
                            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.error));
                            return;
                        }
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.list_programmed_correctly));
                    }
                    @Override
                    public void onFailure(Call<DataResponse> call, Throwable t) {
                        if(getActivity() == null) return;
                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
                    }
                });
            }
        }

    }

    // Cancelar suscripcion
    @OnClick(R.id.btn_cancel_scheduleList)
    public void functCancelSusb(){
        if (scheduleGlobal.equals("")){
            ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.list_does_not_have_subscription));
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(getResources().getString(R.string.cancel_subscription));
            alertDialogBuilder
                    .setMessage(Html.fromHtml(getResources().getString(R.string.cancel_subscription_question)))
                    .setCancelable(false)
                    .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            functLoading(0, getResources().getString(R.string.loading_cancel)+"...");
                            // Metodo tambien para cancelar suscripction (DeleteList)
                            dataResponse = service.DeleteList("cancel_subscription", dataList.getId_list());
                            dataResponse.enqueue(new Callback<DataResponse>() {
                                @Override
                                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                                    if(getActivity() == null) return;
                                    DataResponse myResponse = response.body();
                                    if (myResponse == null){
                                        ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.error));
                                        return;
                                    }
                                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.canceled_subscription));
                                    dataList.setDate_delivery("0000-00-00");
                                    dataList.setReminder("0");
                                    SetDatas("");
                                }
                                @Override
                                public void onFailure(Call<DataResponse> call, Throwable t) {
                                    if(getActivity() == null) return;
                                    ((HomeActivity)getActivity()).setSnackbar(getResources().getString(R.string.unknown_error));
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
        }
    }

    private void showDatePickerDialog() {
        final DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                String Day = twoDigits(day);
                String Month = twoDigits((month+1));
                String Year = ""+year;
                scheduleGlobal = Year + "-" + Month + "-" + Day;
                SetDateTextView(Day, Month, Year);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    // Refrescar tamano del ListView
    private void RefreshHeightList(AddressAdapter listAdapter, ListView listViewAddress) {
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listViewAddress);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listViewAddress.getLayoutParams();
        params.height = totalHeight + (listViewAddress.getDividerHeight() * (listAdapter.getCount() - 1));
        listViewAddress.setLayoutParams(params);
        listViewAddress.requestLayout();
    }

    private void functLoading(int i, String text) {
        Snackbar snackbar;
        if (i == 0){
            snackbar = Snackbar.make(((HomeActivity)getActivity()).coordinatorLayout, text, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
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
