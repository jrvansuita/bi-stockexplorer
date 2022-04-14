package com.vansuita.stockexplorer.eccosys.update;

import java.util.List;

/**
 * Created by jrvansuita on 19/10/17.
 */

public class ResponseUpdate {

    List<?>  success;
    List<?>  error;

    public List getSuccess() {
        return success;
    }

    public List getError() {
        return error;
    }
}
