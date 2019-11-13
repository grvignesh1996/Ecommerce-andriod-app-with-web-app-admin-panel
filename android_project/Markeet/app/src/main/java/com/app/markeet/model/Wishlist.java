package com.app.markeet.model;

import java.io.Serializable;

public class Wishlist implements Serializable {

    public Long product_id;
    public String name;
    public String image;
    public Long created_at = 0L;

    public Wishlist() {
    }

    public Wishlist(long product_id, String name, String image, Long created_at) {
        this.product_id = product_id;
        this.name = name;
        this.image = image;
        this.created_at = created_at;
    }
}
