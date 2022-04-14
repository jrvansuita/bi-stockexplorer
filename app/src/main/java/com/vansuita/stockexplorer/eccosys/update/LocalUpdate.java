package com.vansuita.stockexplorer.eccosys.update;

import com.vansuita.stockexplorer.bean.User;

/**
 * Created by jrvansuita on 19/10/17.
 */

public class LocalUpdate {

    User user;
    String local;
    String sku;


    public LocalUpdate(User user, String local, String sku) {
        this.user = user;
        this.local = local;
        this.sku = sku;
    }
}
