package com.sample.xteamtest.rest.model;

import java.util.List;

/**
 * Created by nitishmehrotra.
 */
public class Face {
    private String id;
    private String type;
    private int size;
    private int price;
    private String face;
    private int stock;
    private List<String> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString()
    {
        return "id: " + id + " type: " + type + " size: " + size + " price: " + price + " face: " + face + " stock: " + stock;
    }
}
