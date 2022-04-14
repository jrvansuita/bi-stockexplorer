package com.vansuita.stockexplorer.capsule;

import com.vansuita.stockexplorer.util.Util;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by jrvansuita on 17/10/17.
 */

public class StockRow {
    int idOrigem;
    float quantidade;
    String tipoEntrada;
    String data;
    String es;


    public int getIdOrigem() {
        return idOrigem;
    }

    public float getQuantidade() {
        return quantidade;
    }

    public String getTipoEntrada() {
        return tipoEntrada;
    }

    public boolean isSaida(){
        return es.equalsIgnoreCase("S");
    }

    public boolean isEntrada(){
        return es.equalsIgnoreCase("E");
    }

    public Date getDataBuy() throws ParseException {
        return Util.parseDate(data);
    }
}
