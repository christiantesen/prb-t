package com.cine.complete.purchase;

import java.math.BigDecimal;

public class CandyProduct {

    private final int id;
    private final String name;
    private final String description;
    private final BigDecimal price;

    public CandyProduct(int id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
