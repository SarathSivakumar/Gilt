package com.streak.gilt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    SessionManager sessionManager;
    int userid;
    String userRole;
    ImageView addOrder,profile;
    ViewOrderMoreBottomSheet viewOrderMoreBottomSheet=new ViewOrderMoreBottomSheet();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager=new SessionManager(MainActivity.this);
        userid=sessionManager.getSession();
        userRole=sessionManager.getRole();
        addOrder=(ImageView) findViewById(R.id.add_order);

        profile=(ImageView) findViewById(R.id.icon_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, profile);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.account_menu, popup.getMenu());
                if(!(userRole.equals("admin")||userRole.equals("office"))){
                   popup.getMenu().findItem(R.id.add_model).setVisible(false);
                }
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Logout")){
                            logout();
                        }
                        else if(item.getTitle().equals("Add Model")){
                            moveToAddModel();
                        }
                        else if(item.getTitle().equals("Change Password")){
                            viewOrderMoreBottomSheet.show(getSupportFragmentManager(),"TAG");
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });
        //Toast.makeText(MainActivity.this,userRole,Toast.LENGTH_LONG).show();
        if(!(userRole.equals("admin")||userRole.equals("office"))){
            addOrder.setVisibility(View.INVISIBLE);
        }
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
            String creationdate;
            //progressBar.setVisibility(View.GONE);
            try {
                JSONObject obj = new JSONObject(s);
                if (!obj.getBoolean("error")) {
                    JSONArray items = obj.getJSONArray("orderlist");

                    for (int it = 0; it < items.length(); it++) {
                        JSONObject orderItem = items.getJSONObject(it);
                        customerName = orderItem.getString("customername");
                        orderID = Integer.parseInt(orderItem.getString("id"));
                        modelName = orderItem.getString("model");
                        factoryName=orderItem.getString("factoryname");
                        creationdate=orderItem.getString("creationdate");
                        ordersList.add(new OrdersRVModel(orderID,modelName+" - "+factoryName,customerName,creationdate));
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
            params.put("userid", ""+userid);
            try{
                return requestHandler.sendPostRequest(Urls.URL_GET_ORDER_LIST,params);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }
    public void moveToViewOrder(int orderId){
        Intent intent=new Intent(MainActivity.this,ViewOrderActivity.class);
        Bundle b=new Bundle();
        b.putInt("orderID",orderId);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void logout(){
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        sessionManager.logout();
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void moveToAddModel(){
        Intent intent=new Intent(MainActivity.this,ModelManagement.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

