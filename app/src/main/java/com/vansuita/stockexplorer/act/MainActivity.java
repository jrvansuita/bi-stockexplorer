package com.vansuita.stockexplorer.act;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.Result;
import com.squareup.picasso.Picasso;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.vansuita.stockexplorer.R;
import com.vansuita.stockexplorer.anim.Animate;
import com.vansuita.stockexplorer.bean.User;
import com.vansuita.stockexplorer.capsule.ProductFixes;
import com.vansuita.stockexplorer.dialog.FixDialog;
import com.vansuita.stockexplorer.dialog.InputDialog;
import com.vansuita.stockexplorer.capsule.Product;
import com.vansuita.stockexplorer.eccosys.update.LocalUpdate;
import com.vansuita.stockexplorer.eccosys.update.ResponseUpdate;
import com.vansuita.stockexplorer.eccosys.update.StockUpdate;

import com.vansuita.stockexplorer.retro.Retro;
import com.vansuita.stockexplorer.retrofit.HawkCalls;
import com.vansuita.stockexplorer.shared.Prefs;
import com.vansuita.stockexplorer.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import retrofit2.Call;


public class MainActivity extends AppCompatActivity implements Retro.IOnRequestError, ZXingScannerView.ResultHandler {

    @BindView(R.id.scanner)
    ZXingScannerView scannerView;
    @BindView(R.id.sku)
    TextView tvSku;
    @BindView(R.id.stored_local)
    TextView tvStoredLocal;
    @BindView(R.id.fisic_stock)
    TextView tvFisicStock;
    @BindView(R.id.description)
    TextView tvDescription;
    @BindView(R.id.ean)
    TextView tvEan;
    @BindView(R.id.brand)
    TextView tvBrand;
    @BindView(R.id.available_stock)
    TextView tvAvailableStock;
    @BindView(R.id.reserved_stock)
    TextView tvReservedStock;
    @BindView(R.id.color)
    TextView tvColor;
    @BindView(R.id.size)
    TextView tvSize;
    @BindView(R.id.status)
    ImageView ivStatus;

    @BindView(R.id.family_view)
    ViewGroup vFamilyView;

    @BindView(R.id.image)
    ImageView ivThumb;

    @BindView(R.id.price_cost)
    TextView tvPriceCost;
    @BindView(R.id.from_price)
    TextView tvFromPrice;
    @BindView(R.id.to_price)
    TextView tvToPrice;
    @BindView(R.id.markup)
    TextView tvMarkup;

    @BindView(R.id.info_holder)
    View vInfo;
    @BindView(R.id.message_holder)
    View vMessage;

    @BindView(R.id.message)
    TextView tvMessage;
    @BindView(R.id.progress)
    ProgressBar pbProgress;
    @BindView(R.id.icon)
    View vIcon;

    @BindView(R.id.color_effect)
    View vColorEffect;

    @BindView(R.id.stored_local_holder)
    View vStoredLocalHolder;

    Product product;
    ProductFixes productFixes;

    private Picasso picasso;
    private User loggerUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        requestPermission();

        Init();

        vInfo.setVisibility(View.GONE);
        tvMessage.setText(R.string.cons);

        getSupportActionBar().setLogo(R.drawable.logo_actionbar);

        toggleBluetoothScan();
        InternetAvailabilityChecker.init(this);

    }

    private void Init() {
        picasso = Picasso.with(this);

        loggerUser = Prefs.get(MainActivity.this).getUser();
    }


    @OnClick(R.id.scanner)
    public void onResumeClicked() {
        if (!isBluetoothActive) {
            scannerView.resumeCameraPreview(MainActivity.this);
        }
    }


    @OnLongClick(R.id.scanner)
    public boolean onScanLongClicked() {
        toggleBluetoothScan();

        return true;
    }


    private boolean isBluetoothActive = false;


    public void toggleBluetoothScan() {
        android.support.v7.app.ActionBar bar = getSupportActionBar();

        isBluetoothActive = !isBluetoothActive;

        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, isBluetoothActive ? R.color.blue : android.R.color.transparent)));
        bar.setDisplayShowTitleEnabled(!isBluetoothActive);  // required to force redraw, without, gray color
        bar.setDisplayShowTitleEnabled(isBluetoothActive);
        getSupportActionBar().setElevation(isBluetoothActive ? 10 : 0);

        if (isBluetoothActive) {
            stopCameraScan();
            vInfo.setPadding(vInfo.getPaddingLeft(), Util.getThemeAttributeDimensionSize(this, android.R.attr.actionBarSize), vInfo.getPaddingRight(), vInfo.getPaddingBottom());
        } else {
            startCameraScan();
            vInfo.setPadding(vInfo.getPaddingLeft(), vInfo.getPaddingBottom(), vInfo.getPaddingRight(), vInfo.getPaddingBottom());
        }

        ActivityCompat.invalidateOptionsMenu(this);
        vColorEffect.setVisibility(isBluetoothActive ? View.GONE : View.INVISIBLE);

        getSupportActionBar().setDisplayShowHomeEnabled(isBluetoothActive);
        getSupportActionBar().setDisplayUseLogoEnabled(isBluetoothActive);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCameraScan();
    }

    private void startCameraScan() {
        if (!isBluetoothActive) {
            scannerView.setAutoFocus(true);
            scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            scannerView.startCamera();          // Start camera on resume
        }
    }

    private void stopCameraScan() {
        scannerView.stopCamera();           // Stop camera on pause
        scannerView.stopCameraPreview();
    }

    @Override
    public void handleResult(Result rawResult) {
        searchByEan(rawResult.getText());
    }


    private void clear() {
        vFamilyView.removeAllViews();

        imageLoaded = false;
        ivThumb.setImageDrawable(null);
        ivStatus.setImageBitmap(null);
    }

    private void bindProduct(Product p) {
        this.product = p;
        Log.i("produtoe", product.toString());

        if(p.getError() != null){
            onRequestError(p.getError(), null);
            return;
        }


        if (!product.isVariant()) {
            onRequestError(getString(R.string.cant_variant, product.getSku()), null);
            return;
        }

        findProductFixes(product.getSku().split("-")[0]);

        vInfo.setVisibility(View.VISIBLE);

        tvSku.setText(product.getSku());

        String active = "";

        if (!product.isActive()){
            active = "[Inativo] ";
            tvDescription.setBackgroundColor(Color.RED);
        }else{
            tvDescription.setBackgroundColor(Color.WHITE);
        }

        tvDescription.setText(active + product.getDescription().split(" -")[0]);
        tvEan.setText(product.getEan());
        tvStoredLocal.setText(product.getStoredLocal());
        //vStoredLocalHolder.setBackgroundColor(ContextCompat.getColor(MainActivity.this, product.getStoredLocal().isEmpty() ? R.color.red_alpha : android.R.color.transparent));

        tvFisicStock.setText(String.valueOf((int) product.getFisicStock()));
        tvBrand.setText(product.getBrand());



        tvAvailableStock.setText(String.valueOf((int) product.getAvailableStock()));
        tvReservedStock.setText(String.valueOf((int) product.getReservedStock()));

        tvColor.setText(product.getColor());
        tvSize.setText(product.getSize());


        tvPriceCost.setText(Util.parseMonetaryMasked(product.getCostPrice()));
        tvFromPrice.setText(Util.parseMonetaryMasked(product.getFromPrice()));
        tvToPrice.setText(Util.parseMonetaryMasked(product.getPrice()));
        tvMarkup.setText(Util.parseFloat(product.getMarkup()));

        if (product.getFeed() != null) {
            putImageThumb(product.getFeed().getImage());
            buildFamilyView();
        }else{
            picasso.load(product.getFeed().getImage()).into(ivThumb);
        }
    }

    private void checkProductStatus() {
        ivStatus.setImageResource(productFixes.isEmpty() ? R.mipmap.ic_check : R.mipmap.ic_alert);
    }

    private boolean imageLoaded;

    private void putImageThumb(String url) {
        if (url == null || url.isEmpty()) {
            imageLoaded = false;
            picasso.load(R.mipmap.ic_warning).into(ivThumb);
            splashColor(R.color.red_alpha);
        } else {
            imageLoaded = true;
            picasso.load(url).placeholder(R.drawable.progress).error(R.mipmap.ic_warning).into(ivThumb);
            splashColor(R.color.green_alpha);
        }
    }



    private void buildFamilyView() {
        vFamilyView.removeAllViews();

        if (product.isVariant()) {
            for (final String sku : product.getSkus()) {
                View v = LayoutInflater.from(this).inflate(R.layout.sku_view, null, false);
                ((TextView) v.findViewById(R.id.sku_label)).setText(sku);
                vFamilyView.addView(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchBySku(sku);
                    }
                });
            }
        }


    }


    @OnClick(R.id.status)
    public void OnStatusClick() {
        showFixDialog(productFixes);
    }

    @OnClick(R.id.image)
    public void onThumbClick(View v) {
        if (product.getFeed() != null && product.getFeed().getUrl() != null && !product.getFeed().getUrl().isEmpty() ){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(product.getFeed().getUrl())));
        }
    }



    @OnLongClick({R.id.sku, R.id.ean, R.id.stored_local})
    public boolean onClipboard(View v) {
        if (v instanceof TextView)
            Util.clipboard(this, ((TextView) v).getText().toString());

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.cam_scan).setVisible(isBluetoothActive);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                InputDialog.build(this, new InputDialog.IOnProductSearch() {
                    @Override
                    public void onSearch(String value) {
                        if (Util.isLong(value)) {
                            searchByEan(value);
                        } else {
                            searchBySku(value);
                        }
                    }
                })
                        .setHint(R.string.product_code).setTitle(R.string.search_product)
                        .setDefault(tvSku.getText().toString())
                       // .setDefault("22645im-8")
                        .show();
                break;
            case R.id.cam_scan:
                toggleBluetoothScan();
                break;

        }

        return true;
    }


    //-------- bluetooth scan -------//

    private String inputedCodeScan = "";
    private Handler inputScanHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Util.isLong(inputedCodeScan)) {
                searchByEan(inputedCodeScan);
            }

            inputedCodeScan = "";
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (Util.isLong(String.valueOf(event.getNumber()))) {

            System.out.println("Passed Code: " + event.getNumber());
            inputScanHandler.removeCallbacks(runnable);

            inputedCodeScan += event.getNumber();

            inputScanHandler.postDelayed(runnable, 100);


            return true;
        } else if (!inputedCodeScan.isEmpty()) {
            System.out.println("Not Passed: " + event.getNumber());
            return false;
        } else {
            System.out.println("Normal: " + event.getNumber());
            return super.onKeyDown(keyCode, event);
        }

    }


    //-------- -------- -------//

    private void searchBySku(String sku) {
        messageToggle(null, true);

        HawkCalls.Retro.get(this).create(HawkCalls.class).getProductBySku(sku).enqueue(onProductSearchResult);
    }

    private void searchByEan(String ean) {
        messageToggle(null, true);
        HawkCalls.Retro.get(this).create(HawkCalls.class).getProductByEan(ean).enqueue(onProductSearchResult);
    }


    private Retro<Product> onProductSearchResult = (new Retro<Product>() {
        @Override
        public void onResponse(Product product) {
            bindProduct(product);
        }

        @Override
        public void onFailure(Call<Product> call, Throwable t) {
            if (t.getMessage().contains("Auth")) {
                onRequestError(t.getMessage(), null);
            } else {
                if (!Util.internet(MainActivity.this)) {
                    onRequestError(getString(R.string.no_internet), null);
                } else {
                    onRequestError(getString(R.string.invalid_search, getSearchTerm(call)), null);
                }
            }
        }
    });

    private static final int PERMISSIONS_REQUEST_CAMERA = 99;

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scannerView.startCamera();
            }
        }
    }

    @Override
    public void onRequestError(String msg, Throwable error) {
        messageToggle(msg, false);
        splashColor(R.color.red_alpha);
    }

    private void splashColor(int color) {

        if (!isBluetoothActive) {
            vColorEffect.setBackgroundColor(ContextCompat.getColor(this, color));

            Animate.builder(vColorEffect, android.R.anim.fade_in).visible().start();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animate.builder(vColorEffect, android.R.anim.fade_out).invisible().start();
                }
            }, 1000);
        }
    }

    private void messageToggle(String message, boolean progress) {

        if (progress) {
            clear();
        }

        vInfo.setVisibility(View.GONE);
        vIcon.setVisibility(progress ? View.GONE : View.VISIBLE);
        pbProgress.setVisibility(progress ? View.VISIBLE : View.GONE);
        tvMessage.setText(progress && (message==null || message.isEmpty()) ? getString(R.string.searching_product) : message);


    }

    @OnClick(R.id.stored_local)
    public void onStoredLocalClick() {
        if (product != null && product.isActive())


            InputDialog.build(this, new InputDialog.IOnProductSearch() {
                @Override
                public void onSearch(String localizacao) {
                    updateLocal(product.getSku(), localizacao);
                }
            }).setHint(R.string.stored_local)
                    .setDefault(tvStoredLocal.getText().toString())
                    .setTitle(R.string.new_stored_local)
                    .show();
    }



    @OnClick(R.id.fisic_stock)
    public void onFisicStockClick() {
        if (product != null && product.isActive())
            InputDialog.build(this, new InputDialog.IOnProductSearch() {
                @Override
                public void onSearch(String value) {

                    updateStock(product.getSku(), Integer.valueOf(value));



                }
            }).setHint(R.string.fisic_stock)
                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                    .setDefault(String.valueOf(1))
                    .setTitle(R.string.new_stock_row)
                    .setPositiveLabel(R.string.update)
                    .show();

    }


    private void updateStock(String sku, Integer stock){
        messageToggle("Atualizando produto: " + product.getSku(), true);
        HawkCalls.Retro.get(this).create(HawkCalls.class).updateStock(new StockUpdate(loggerUser, stock, sku)).enqueue(this.onProductUpdateResult());
    }


    private void updateLocal(String sku, String local){
        messageToggle("Atualizando produto: " + product.getSku(), true);
        HawkCalls.Retro.get(this).create(HawkCalls.class).updateLocal(new LocalUpdate(loggerUser, local, sku)).enqueue(this.onProductUpdateResult());
    }

    private Retro<ResponseUpdate> onProductUpdateResult(){
        return new Retro<ResponseUpdate>() {

            @Override
            public void onFailure(Call<ResponseUpdate> call, Throwable t) {
                Log.i("s", t.getMessage());
                onRequestError(getString(R.string.cant_update, product.getSku()), t);
            }

            @Override
            public void onResponse(ResponseUpdate result) {
                    searchBySku(product.getSku());
            }
        };
    }


    private void findProductFixes(final String sku){
        ivStatus.setImageResource(R.drawable.progress);

        HawkCalls.Retro.get(this).create(HawkCalls.class).getProductFixes(sku, true).enqueue(new Retro<ProductFixes>() {
            @Override
            public void onResponse(ProductFixes result) {
                    if(result.isEmpty()){
                        resyncProductFix(sku);
                    }else{
                        productFixes = result;
                        checkProductStatus();
                    }
            }

            @Override
            public void onFailure(Call<ProductFixes> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void resyncProductFix(String sku){
        HawkCalls.Retro.get(this).create(HawkCalls.class).productFixResync(new ProductFixes.ProductFixResync(sku, true, true)).enqueue(new Retro<ProductFixes>() {
            @Override
            public void onResponse(ProductFixes result) {
                productFixes = result;
                checkProductStatus();
            }

            @Override
            public void onFailure(Call<ProductFixes> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void showFixDialog(ProductFixes productFixes){
        FixDialog.build(this, productFixes).show();
    }
}

