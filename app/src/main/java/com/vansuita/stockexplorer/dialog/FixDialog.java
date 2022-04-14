package com.vansuita.stockexplorer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.stockexplorer.R;
import com.vansuita.stockexplorer.capsule.ProductFixes;

public class FixDialog {

    Context context;
    ProductFixes productFixes;
    View view;


    FixDialog(Context context, ProductFixes productFixes){
        this.productFixes = productFixes;
        this.context = context;
    }

    public static FixDialog build(Context context, ProductFixes productFixes){
        return new FixDialog(context, productFixes);
    }

    private View getView(){
        view = LayoutInflater.from(context).inflate(R.layout.fix_dialog, null, false);
        return view;
    }

    private void buildItems(){
        GridLayout mainGrid = view.findViewById(R.id.gridFix);
        if(!productFixes.isEmpty()){
            for (ProductFixes.ProductFix f : productFixes) {
                View vFix = LayoutInflater.from(context).inflate(R.layout.fix_view, null, false);

                ((TextView) vFix.findViewById(R.id.fixSku)).setText(f.getSku());
                GridLayout gridInfos = vFix.findViewById(R.id.gridFixInfos);

                for(ProductFixes.Data a : f.getData()){

                    View vInfo = LayoutInflater.from(context).inflate(R.layout.fix_info, null, false);
                    ((TextView) vInfo.findViewById(R.id.fixDescription)).setText(a.getName());
                    gridInfos.addView(vInfo);
                    int image = context.getResources().getIdentifier(context.getPackageName() + ":mipmap/ic_" + a.getIcon().toLowerCase().replace("-","_"), null, null);
                    ((ImageView) vInfo.findViewById(R.id.fixImg)).setImageResource(image);
                }
                mainGrid.addView(vFix);
            }
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.fix_view, null, false);
            ((TextView) v.findViewById(R.id.fixSku)).setText(R.string.no_fix);
            mainGrid.addView(v);
        }
    }

    public void show(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Diagnóstico")
                .setView(getView()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

        buildItems();
        builder.show();
    }



}
