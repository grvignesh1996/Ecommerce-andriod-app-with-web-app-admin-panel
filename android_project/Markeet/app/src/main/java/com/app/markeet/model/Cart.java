package com.app.markeet.model;

public class Cart {

    public Long id;
    public Long order_id = -1L;
    public Long product_id;
    public String product_name;
    public String image;
    public Integer amount = 0;
    public Long stock = 0L;
    public Double price_item;
    public Long created_at = 0L;

    public Cart() {
    }

    public Cart(Long product_id, String product_name, String image, Integer amount, Long stock, Double price_item, Long created_at) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.image = image;
        this.amount = amount;
        this.stock = stock;
        this.price_item = price_item;
        this.created_at = created_at;
    }
}
