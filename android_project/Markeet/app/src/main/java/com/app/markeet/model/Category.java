package com.app.markeet.model;

import java.io.Serializable;

public class Category implements Serializable {

    public Long id;
    public String name;
    public String icon;
    public Integer draft;
    public String brief;
    public String color;
    public Long created_at;
    public Long last_update;

}
