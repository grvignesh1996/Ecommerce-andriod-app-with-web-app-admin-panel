package com.app.markeet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    public Long id;
    public String name;
    public String image;
    public Double price;
    public Double price_discount;
    public Long stock;
    public Integer draft;
    public String description;
    public String status;
    public Long created_at;
    public Long last_update;

    public List<Category> categories = new ArrayList<>();
    public List<ProductImage> product_images = new ArrayList<>();

}
