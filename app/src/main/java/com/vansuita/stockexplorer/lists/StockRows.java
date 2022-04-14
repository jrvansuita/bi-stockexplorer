package com.vansuita.stockexplorer.lists;

import com.vansuita.stockexplorer.capsule.StockRow;
import com.vansuita.stockexplorer.util.Util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jrvansuita on 17/10/17.
 */

public class StockRows extends ArrayList<StockRow> {


    private float initialValue = 0;
    private Date lastBuy;


    public void init() throws ParseException {


        for (StockRow row : this){
            if (row.getIdOrigem() == 0)
                initialValue += row.getQuantidade();


            if (row.isSaida()) {
                Date actual = row.getDataBuy();

                if (lastBuy == null || actual.getTime() > lastBuy.getTime())
                    lastBuy = row.getDataBuy();
            }
        }
    }


    public String getLastBuyStr() {
            return Util.formatDate(lastBuy);

    }
    public Date getLastBuy() {
        return lastBuy;
    }

    public float getInitialValue() {
        return initialValue;
    }




}
