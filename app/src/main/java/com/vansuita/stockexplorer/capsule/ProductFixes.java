package com.vansuita.stockexplorer.capsule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductFixes extends ArrayList<ProductFixes.ProductFix> {


    public class ProductFix {
        @SerializedName("fixes")
        @Expose
        private ArrayList<Data> data;

        private String type;
        private String name;
        private String sku;
        private String icon;

        public ArrayList<Data> getData() {
            return data;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getSku() { return sku; }
    }



    public class Data {
        private String name;
        private String icon;

        public String getName() {
            return name;
        }

        public String getIcon() { return icon; }
    }

    public static class ProductFixResync{
        private String sku;
        private Boolean groupped;
        private Boolean forceFather;

        public ProductFixResync(String sku, Boolean groupped, Boolean forceFather) {
            this.sku = sku;
            this.groupped = groupped;
            this.forceFather = forceFather;
        }
    }

}


