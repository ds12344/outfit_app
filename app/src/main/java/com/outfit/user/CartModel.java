package com.outfit.user;

import java.util.ArrayList;

public class CartModel {

    String sellerId, shippingPrice;
    ArrayList<ProductModel> productList = new ArrayList<>();

    public CartModel(String sellerId, String shippingPrice, ArrayList<ProductModel> productList) {
        this.sellerId = sellerId;
        this.shippingPrice = shippingPrice;
        this.productList = productList;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(String shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public ArrayList<ProductModel> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<ProductModel> productList) {
        this.productList = productList;
    }
}
