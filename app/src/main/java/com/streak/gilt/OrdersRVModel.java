package com.streak.gilt;

import android.widget.TextView;

public class OrdersRVModel {
    String customerName;
    String model;
    int orderID;
    String date;

    public OrdersRVModel(int orderID,String model, String  customerName, String date) {
        this.customerName = customerName;
        this.model = model;
        this.orderID = orderID;
        this.date = date;

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
}
