package com.app.markeet.connection.callbacks;

import com.app.markeet.model.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackProduct implements Serializable {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Product> products = new ArrayList<>();

}
