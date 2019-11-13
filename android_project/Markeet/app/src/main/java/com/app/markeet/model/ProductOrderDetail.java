package com.app.markeet.model;

import java.io.Serializable;

public class ProductOrderDetail implements Serializable {

    public Long product_id;
    public String product_name;
    public Integer amount;
    public Double price_item;
    public Long created_at = System.currentTimeMillis();
    public Long last_update = System.currentTimeMillis();

    public ProductOrderDetail() {
    }

    public ProductOrderDetail(Long product_id, String product_name, Integer amount, Double price_item) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.amount = amount;
        this.price_item = price_item;
    }
}
