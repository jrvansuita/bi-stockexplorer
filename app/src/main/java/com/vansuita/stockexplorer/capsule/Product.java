package com.vansuita.stockexplorer.capsule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.vansuita.stockexplorer.eccosys.Attr;
import com.vansuita.stockexplorer.lists.StockRows;
import com.vansuita.stockexplorer.util.Util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jrvansuita on 16/10/17.
 */

public class Product {
    private String codigo;
    private String nome;
    private String descricaoEcommerce;
    private float preco;
    private float precoDe;
    private float precoCusto;
    private String obs;

    private String gtin;
    private String localizacao;
    private boolean showing = false;
    private String urlEcommerce;
    private Stock _Estoque;
    private List<Attr> _Atributos;
    private StockRows stockRows;
    private String situacao;
    private String error;



    @SerializedName("img")
    @Expose
    private ProductFeed feed;

    public List<String> getSkus() {
        return getFeed()!= null ? Arrays.asList(getFeed().getAssociates().split(",")) : null;
    }

    public String getDescricaoEcommerce() {
        return descricaoEcommerce;
    }

    public String getSku() {
        return codigo;
    }

    public boolean isVariant() {

        if(codigo.contains("-")){
            return this.getSkus().size() > 0;
        }else{
            return this.getSkus().get(0).isEmpty();
        }
    }


    public String getSkuPai() {
        int i = codigo.lastIndexOf('-');

        return i > 0 ? codigo.substring(0, i) : codigo;
    }

    public String getDescription() {
        return nome;
    }

    public float getPrice() {
        return preco;
    }


    public float getFromPrice() {
        return precoDe;
    }

    public float getCostPrice() {
        return precoCusto;
    }

    public float getMarkup() {
        return getPrice() / getCostPrice();
    }


    public String getStoredLocal() {
        return localizacao == null ? "" : localizacao;
    }

    public Stock getStock() {
        return _Estoque;
    }

    public float getFisicStock() {
        return _Estoque.getEstoqueReal();
    }

    public float getAvailableStock() {
        return _Estoque.getEstoqueDisponivel();
    }

    public float getReservedStock() {
        return getFisicStock() - getAvailableStock();
    }

    public String getEan() {
        return gtin;
    }


    public String getBrand() {
        String brand = getAttrValue(Attr.MARCA);

        if (!Util.isText(brand) && getDescricaoEcommerce().contains("-")) {
            brand = getDescricaoEcommerce().split("-")[1];
        }

        return brand;
    }

    public String getSize() {
        String size = getAttrValue(Attr.TAMANHO);

        if (size.trim().contentEquals("0")){
            return "";
        }else {
            return size;
        }
    }

    public String getColor() {
        String color = getAttrValue(Attr.COR);

        if (color.trim().contentEquals("0")){
            return "";
        }else {
            return color;
        }
    }

    public String getAttrValue(String desc) {
        String val = "";

        for (Attr attr : _Atributos) {
            if (attr.getDescricao().equalsIgnoreCase(desc)) {
                if (attr.getValor() != null){
                    val = attr.getValor();
                }
                break;
            }
        }

        return val;
    }


    public boolean isShowing() {
        if (getReservedStock() > 0) {
            return true;
        } else if (getFeed().getVisible()) {
            return true;
        } else {
            return showing;
        }
    }

    public boolean isActive() {
        return situacao.equalsIgnoreCase("A");
    }


    public StockRows getStockRows() {
        return stockRows;
    }

    public void setStockRows(StockRows stockRows) {
        this.stockRows = stockRows;
    }

    public boolean longTimeNoSell() {
        if (stockRows == null || stockRows.getLastBuy() == null)
            return true;

        return Util.daysSince(stockRows.getLastBuy()) > 10;
    }

    public String getObs() {
        return obs;
    }

    public ProductFeed getFeed() {
        return feed;
    }

    public Boolean isOk(){
        return feed != null && isActive() && isShowing();
    }

    public String getError() {
        return error;
    }
}


