package com.vansuita.stockexplorer.retrofit;

import android.content.Context;

import com.vansuita.stockexplorer.BuildConfig;
import com.vansuita.stockexplorer.R;
import com.vansuita.stockexplorer.bean.User;
import com.vansuita.stockexplorer.capsule.Product;
import com.vansuita.stockexplorer.capsule.ProductFixes;
import com.vansuita.stockexplorer.eccosys.update.LocalUpdate;
import com.vansuita.stockexplorer.eccosys.update.ResponseUpdate;

import com.vansuita.stockexplorer.eccosys.update.StockUpdate;
import com.vansuita.stockexplorer.capsule.Credentials;
import com.vansuita.stockexplorer.shared.Prefs;
import com.vansuita.stockexplorer.util.Util;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import retrofit2.http.Query;

/**
 * Created by jrvansuita on 17/10/17.
 */






public interface HawkCalls {



    class Retro {



        public static Retrofit get(Context c) {

            User loggerUser = Prefs.get(c).getUser();

            HashMap<String, String> headers = new HashMap();

            headers.put("device", "Mobile");

            if (loggerUser != null) {
                headers.put("access", loggerUser.getAccess());
                headers.put("pass", loggerUser.getPass());
            }

            headers.put("appkey","SEFXS0FQUC1UWHBSZWs1RVZUVk5SRTB5VGtSVlBRPT0tUlc=");

            String url = c.getString(R.string.hawk_url);

            if(BuildConfig.DEBUG) {
               url = "http://192.168.0.50:3000/";
            }

            return new Retrofit.Builder()
                    .client(Util.getOkHttpClient(null, headers))
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }


    @POST("login")
    Call<ResponseBody> login(@Body Credentials credentials);

    @GET("api/product")
    Call<Product> getProductByEan(@Query("ean") String ean);

    @GET("api/product")
    Call<Product> getProductBySku(@Query("sku") String sku);

    @POST("api/product/stock")
    Call<ResponseUpdate> updateStock(@Body StockUpdate update);

    @POST("api/product/local")
    Call<ResponseUpdate> updateLocal(@Body LocalUpdate update);

    @GET("api/diagnostics/fixes")
    Call<ProductFixes> getProductFixes(@Query("sku") String sku, @Query("grouped") Boolean grouped);

    @POST("api/diagnostics/check")
    Call<ProductFixes> productFixResync(@Body ProductFixes.ProductFixResync resync);

}
