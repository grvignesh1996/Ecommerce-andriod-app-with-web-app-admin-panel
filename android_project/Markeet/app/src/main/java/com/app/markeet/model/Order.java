package com.app.markeet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    public Long id;
    public String code;
    public String total_fees;
    public String status = "";
    public Long created_at = System.currentTimeMillis();
    public List<Cart> cart_list = new ArrayList<>();

    public Order() {
    }

    public Order(Long id, String code, String total_fees) {
        this.id = id;
        this.code = code;
        this.total_fees = total_fees;
    }
}



