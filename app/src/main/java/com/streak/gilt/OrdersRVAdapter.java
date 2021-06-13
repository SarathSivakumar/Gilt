package com.streak.gilt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class OrdersRVAdapter extends RecyclerView.Adapter<OrdersRVAdapter.ViewHolder>{

    List<OrdersRVModel> orders;
    Context context;
    Bitmap decodedBitmap;
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
            holder.creationdate.setText(order.getDate());
            setThumbnailImage(order.getOrderID(),holder);
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
        public TextView orderid,customername,model,creationdate;
        public ImageView thumbnail;
        public ConstraintLayout orderLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderid=(TextView) itemView.findViewById(R.id.order_id);
            customername=(TextView) itemView.findViewById(R.id.customer_name);
            model=(TextView) itemView.findViewById(R.id.model_name);
            creationdate=(TextView) itemView.findViewById(R.id.creation_date);
            thumbnail=(ImageView) itemView.findViewById(R.id.img_thumbnail);
            orderLayout=(ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
        }
    }

    public void setThumbnailImage(int orderID,ViewHolder holder){
        class GetOrderImage extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String thumbnailEncoded;
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONObject orderItem = obj.getJSONObject("order");
                        thumbnailEncoded=orderItem.getString("image");
                        byte[] decodedString = Base64.decode(thumbnailEncoded, Base64.DEFAULT);
                        decodedBitmap= BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
                        holder.thumbnail.setImageBitmap(decodedBitmap);
                    }else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("orderid", ""+orderID);
                try{
                    return requestHandler.sendPostRequest(Urls.URL_GET_ORDER_IMAGE,params);
                }
                catch (Exception e){
                    return  null;
                }

            }
        }
        GetOrderImage gl = new GetOrderImage();
        gl.execute();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}