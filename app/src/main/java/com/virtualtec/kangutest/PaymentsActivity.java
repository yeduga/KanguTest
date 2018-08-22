package com.virtualtec.kangutest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.virtualtec.kangutest.ClassesCustom.GifView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsActivity extends AppCompatActivity {

    String url, idOrder;
    @BindView(R.id.toolbar_payments) Toolbar toolbar;
    @BindView(R.id.webview_payments) WebView webView;
    @BindView(R.id.linear_loading_payments) LinearLayout linearLoading;
    @BindView(R.id.loading_payments) GifView gifLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Init();
    }

    private void Init() {
        idOrder = getIntent().getStringExtra("idp");
        gifLoading.loadGIFResource(R.drawable.cargando_verde);
        url = "http://173.199.148.4/tiendas_api/kangu/payu/payment.php?idp=" + idOrder;

        webView.setWebViewClient(new MyWebViewClient(linearLoading));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(url);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setTitle("Pagos con PayU");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {

        private LinearLayout linearLoading;

        public MyWebViewClient(LinearLayout linearLoading) {
            this.linearLoading=linearLoading;
            linearLoading.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            linearLoading.setVisibility(View.GONE);
        }
    }
}
