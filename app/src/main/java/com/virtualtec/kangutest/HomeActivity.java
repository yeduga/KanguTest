package com.virtualtec.kangutest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.virtualtec.kangutest.Datas.DataCategory;
import com.virtualtec.kangutest.Datas.DataProducts;
import com.virtualtec.kangutest.Datas.DataUser;
import com.virtualtec.kangutest.RestServiceApi.RestService;

import java.util.ArrayList;

import br.com.mauker.materialsearchview.MaterialSearchView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    Retrofit retrofit;
    RestService service;
    Call<ArrayList<DataUser>> dataUser;
    SharedPreferences prefs;
    public String prefsIdUser, prefsUser, prefsPassword;
    ArrayList<DataUser> ArrayUser = null;
    ArrayList<DataCategory> ArrayCategories = null;
    ArrayList<DataProducts> ArrayProducts = null;
    private Call<ArrayList<DataCategory>> dataCategory;
    private Call<ArrayList<DataProducts>> dataProducts;
    @BindView(R.id.button_fab_cart) FloatingActionButton buttonFabCart;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.coordinator_home) public CoordinatorLayout coordinatorLayout;
    @BindView(R.id.search_view) MaterialSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = this.getSharedPreferences("Kangu",0);
        prefsIdUser = prefs.getString("idUser", "");

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Init();
    }

    private void InitSearch() {
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (currentFragment instanceof ResultsFragment){
                    ResultsFragment fragment = (ResultsFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                    fragment.LoadResults(query);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString("Query", query);
                    ResultsFragment nextFrag = new ResultsFragment();
                    nextFrag.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, nextFrag,"findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {
                // Do something once the view is open.
            }

            @Override
            public void onSearchViewClosed() {
                // Do something once the view is closed.
            }
        });

        final Context context = this;

        mSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSearchView.setQuery(mSearchView.getSuggestionAtPosition(i), true);
            }
        });

        mSearchView.setTintAlpha(200);
        mSearchView.adjustTintAlpha(0.2f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        getMenuInflater().inflate(R.menu.options_activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                // Open the search view on the menu item click.
                mSearchView.openSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isOpen()) {
            // Close the search on the back button press.
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    public void Init(){
        InitSearch();
        InitRetrofit();
        validSesion();
        LoadCategories();

        final BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
        bottomNavigationView.setTextVisibility(false);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.setIconSize(26, 26); // Tamano iconos en dp

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                        getSupportFragmentManager().popBackStackImmediate();
                        switch (item.getItemId()) {
                            case R.id.action_item_home:
                                 if (!(currentFragment instanceof HomeFragment)){
                                    selectedFragment = HomeFragment.newInstance();
                                    FunctFragmentTransaction(selectedFragment);
                                 }
                                break;
                            case R.id.action_item_products:
                                if(!(currentFragment instanceof ProductsFragment)){
                                    selectedFragment = ProductsFragment.newInstance();
                                    FunctFragmentTransaction(selectedFragment);
                                }
                                break;
                            case R.id.action_item_cart:
                                if(!(currentFragment instanceof CartFragment)) {
                                    selectedFragment = CartFragment.newInstance();
                                    FunctFragmentTransaction(selectedFragment);
                                }
                                break;
                            case R.id.action_item_list:
                                if(!(currentFragment instanceof ListsFragment)) {
                                    selectedFragment = ListsFragment.newInstance();
                                    FunctFragmentTransaction(selectedFragment);
                                }
                                break;
                            case R.id.action_item_profile:
                                if(!(currentFragment instanceof ProfileFragment)) {
                                    selectedFragment = ProfileFragment.newInstance();
                                    FunctFragmentTransaction(selectedFragment);
                                }
                                break;
                        }
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FunctFragmentTransaction(HomeFragment.newInstance());

        // Boton Centro click listener
        buttonFabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().popBackStackImmediate();
                //Used to select an item programmatically
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                FunctFragmentTransaction(CartFragment.newInstance());
            }
        });
    }

    // Transaction en fragment
    public void FunctFragmentTransaction(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void InitRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RestService.class);
    }

    // Comprobar usuario, sesion
    public void validSesion (){
        prefsUser=prefs.getString("user", "");
        prefsPassword=prefs.getString("password", "");

        dataUser = service.Access("login", prefsUser, prefsPassword);
        dataUser.enqueue(new Callback<ArrayList<DataUser>>() {
            @Override
            public void onResponse(Call<ArrayList<DataUser>> call, Response<ArrayList<DataUser>> response) {
                ArrayUser = response.body();
                if (ArrayUser == null || ArrayUser.size() == 0){
                    LogoutSesion();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DataUser>> call, Throwable t) {
                setSnackbar(getResources().getString(R.string.no_connection));
            }
        });
    }

    // Logout
    public void LogoutSesion(){
        Toast.makeText(HomeActivity.this, getResources().getString(R.string.coming_out), Toast.LENGTH_SHORT).show();
        prefs.edit().remove("idUser").commit();
        prefs.edit().remove("user").commit();
        prefs.edit().remove("password").commit();
        Intent autoactivity = new Intent(HomeActivity.this, LoginActivity.class);
        autoactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(autoactivity);
        finish();
    }

    public void setSnackbar(String text){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    public void setSnackbarAddCart(String text){
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.colorSnackbarAction))
                .setAction(getResources().getString(R.string.item_cart), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSupportFragmentManager().popBackStackImmediate();
                        FunctFragmentTransaction(CartFragment.newInstance());
                    }
                })
                .show();
    }

    // Funciones //
    // Titulo Action Bar
    public void setActionBarCustom(String title, int status, final Activity activity) {
        // Status 0: sin boton en action bar, Status 1: con boton y fragmento anterior
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setTitle(title);
        if (status == 1){
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }else{
            toolbar.setNavigationIcon(null);
        }
    }

    // Checked Navigation
    public void CheckedNavigationView(int position) {
        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
        bottomNavigationView.getMenu().getItem(position).setChecked(true);
    }

    private void LoadCategories() {
        dataCategory = service.Categories("categories", "0");
        dataCategory.enqueue(new Callback<ArrayList<DataCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<DataCategory>> call, Response<ArrayList<DataCategory>> response) {
                if(HomeActivity.this == null) return;
                ArrayCategories = response.body();
                String globalIdCategory = ArrayCategories.get(0).getId();
                LoadProducts(globalIdCategory);
            }
            @Override
            public void onFailure(Call<ArrayList<DataCategory>> call, Throwable t) {

            }
        });
    }

    private void LoadProducts(String idCategory) {
        dataProducts = service.ProductsCategory("list_products", idCategory);
        dataProducts.enqueue(new Callback<ArrayList<DataProducts>>() {
            @Override
            public void onResponse(Call<ArrayList<DataProducts>> call, Response<ArrayList<DataProducts>> response) {
                if(HomeActivity.this == null) return;
                ArrayProducts = response.body();
            }
            @Override
            public void onFailure(Call<ArrayList<DataProducts>> call, Throwable t) {

            }
        });
    }

    // Toast Center
    public void ToastMessageCenter(String text) {
        Toast toast = Toast.makeText(HomeActivity.this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void searchClearAll() {
        mSearchView.clearAll();
    }
}