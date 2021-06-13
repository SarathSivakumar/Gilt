package com.streak.gilt;

import android.widget.TextView;

public class OrdersRVModel {
    String customerName;
    String model;
    int orderID;
    String thumbnailEncoded;
    String date;
    String factoryName;

    public OrdersRVModel(int orderID,String model, String  customerName, String date,String factoryName) {
        this.customerName = customerName;
        this.model = model;
        this.orderID = orderID;
        this.date = date;
        this.factoryName=factoryName;
        //this.thumbnailEncoded=thumbnailEncoded;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getModel() {
        return model;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getDate() {
        return date;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public String getThumbnailEncoded(){
        return thumbnailEncoded;
    }
}
