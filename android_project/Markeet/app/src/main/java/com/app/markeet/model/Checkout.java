package com.app.markeet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Checkout implements Serializable {

    public ProductOrder product_order = new ProductOrder();
    public List<ProductOrderDetail> product_order_detail = new ArrayList<>();

}
