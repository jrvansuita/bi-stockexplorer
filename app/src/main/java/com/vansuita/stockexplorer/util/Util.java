package com.vansuita.stockexplorer.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jrvansuita on 18/10/17.
 */

public class Util {

    public static void clipboard(Context context, String s) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", s);
        clipboard.setPrimaryClip(clip);
    }


    public static Locale getLocal() {
        return new Locale("pt", "BR");
    }

    protected static NumberFormat createFormat() {
        NumberFormat f = NumberFormat.getNumberInstance(getLocal());
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        f.setGroupingUsed(true);
        return f;
    }

    public static String parseFloat(double value) {
        return createFormat().format(value);
    }


    public static String parseMonetaryMasked(double value) {
        return "R$ " + createFormat().format(value);
    }

    public static String formatDate(Date value) {
        if (value == null)
            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

        return sdf.format(value);
    }


    public static Date parseDate(String value) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.parse(value);

    }

    public static LinkedHashSet<String> getSiteImages(String data) {

        LinkedHashSet<String> set = new LinkedHashSet<>();

        int ini = data.indexOf("product-image-gallery");
        if (ini > 0) {
            int end = data.indexOf("</div>", ini);

            String part = data.substring(ini, end);

            String iniPattern = "data-zoom-image=\"";
            String endPattern = ".jpg\"";

            do {
                ini = part.indexOf(iniPattern) + iniPattern.length();
                end = part.indexOf(endPattern, ini) + endPattern.length() - 1;

                String u = part.substring(ini, end);
                set.add(u);

                part = part.substring(end, part.length());
            } while (part.indexOf(iniPattern) > -1);

            //Remove o primeiro da lista porque é repetido
            //links.splice(1, 1);
        }

        return set;
    }

    public static int getThemeAttributeDimensionSize(Context context, int attr)
    {
        TypedArray a = null;
        try{
            a = context.getTheme().obtainStyledAttributes(new int[] { attr });
            return a.getDimensionPixelSize(0, 0);
        }finally{
            if(a != null){
                a.recycle();
            }
        }
    }

    public static boolean isText(String s) {
        try {
            if (s == null || s.isEmpty())
                return false;

            NumberFormat.getInstance().parse(s);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    public static OkHttpClient getOkHttpClient(final HashMap<String, String> params, final HashMap<String, String> headers) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(60,TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl.Builder builder = originalHttpUrl.newBuilder();

                if (params != null)
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        builder.addQueryParameter(entry.getKey(), entry.getValue());
                    }

                HttpUrl url = builder.build();

                Request.Builder requestBuilder = original.newBuilder();

                if (headers != null)
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        requestBuilder.addHeader(entry.getKey(), entry.getValue());
                    }

                requestBuilder.url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }

    public static long daysBetween(Date last, Date now) {
        if (now == null)
            return 0;

        if (last == null)
            return 0;

        long nowLong = now.getTime();
        long lastLong = last.getTime();

        return TimeUnit.DAYS.convert(nowLong - lastLong, TimeUnit.MILLISECONDS);
    }

    public static long daysSince(Date last) {
        return daysBetween(last, new Date());
    }


    public static boolean isLong(String ean){
        try{
            Long.parseLong(ean);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean internet(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (con == null) {
            return false;
        }

        NetworkInfo inf = con.getActiveNetworkInfo();

        return inf != null && inf.isConnected();
    }

}
