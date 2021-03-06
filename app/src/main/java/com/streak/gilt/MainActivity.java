package com.streak.gilt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.streak.gilt.DRVInterface.LoadMore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView orders_rv ;
    private RecyclerView.Adapter order_rv_adapter;
    private List<OrdersRVModel> ordersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orders_rv=(RecyclerView) findViewById(R.id.home_rv1);
        orders_rv.setHasFixedSize(true);
        orders_rv.setLayoutManager(new LinearLayoutManager(this));

        ordersList=new ArrayList<>();

        GetOrderList gl = new GetOrderList();
        gl.execute();
    }

    public void movetoaddorder(View v){
        Intent intent=new Intent(MainActivity.this,AddOrder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    class GetOrderList extends AsyncTask<Void, Void, String> {

        ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String customerName;
            String factoryName;
            int orderID;
            String modelName;
            //progressBar.setVisibility(View.GONE);
            try {
                JSONObject obj = new JSONObject(s);
                if (!obj.getBoolean("error")) {
                    JSONArray items = obj.getJSONArray("orderList");

                    for (int it = 0; it < items.length(); it++) {
                        JSONObject orderItem = items.getJSONObject(it);
                        customerName = orderItem.getString("customername");
                        orderID = Integer.parseInt(orderItem.getString("id"));
                        modelName = orderItem.getString("model");
                        factoryName=orderItem.getString("factoryname");
                        ordersList.add(new OrdersRVModel(orderID,modelName+" - "+factoryName,customerName,"12/12/2021"));
                    }
                    order_rv_adapter=new OrdersRVAdapter(ordersList,MainActivity.this);
                    orders_rv.setAdapter(order_rv_adapter);
                }else {
                    Toast.makeText(getApplicationContext(), "Invalid params called", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            HashMap<String, String> params = new HashMap<>();
            try{
                return requestHandler.sendPostRequest(Urls.URL_GET_ORDER_LIST,params);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }
}

