package com.streak.gilt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersRVAdapter extends RecyclerView.Adapter<OrdersRVAdapter.ViewHolder>{

    List<OrdersRVModel> orders;
    Context context;

    public OrdersRVAdapter(List<OrdersRVModel> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_rv_item_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrdersRVModel order=orders.get(position);
        if(holder.orderid!=null){
            holder.orderid.setText(""+order.getOrderID());
            holder.customername.setText(order.getCustomerName());
            holder.model.setText(order.getModel());
        }
        holder.orderLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,""+order.getOrderID(),Toast.LENGTH_SHORT).show();
                if (context instanceof MainActivity) {
                    ((MainActivity)context).moveToViewOrder(order.getOrderID());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView orderid,customername,model;
        public ConstraintLayout orderLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderid=(TextView) itemView.findViewById(R.id.order_id);
            customername=(TextView) itemView.findViewById(R.id.customer_name);
            model=(TextView) itemView.findViewById(R.id.model_name);
            orderLayout=(ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
        }
    }


}