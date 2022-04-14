package com.vansuita.stockexplorer.eccosys.update;

import com.vansuita.stockexplorer.bean.User;

/**
 * Created by jrvansuita on 19/10/17.
 */

public class StockUpdate {

    User user;
    int stock;
    String sku;


    public StockUpdate(User user, int stock, String sku) {
        this.user = user;
        this.stock = stock;
        this.sku = sku;
    }
}
