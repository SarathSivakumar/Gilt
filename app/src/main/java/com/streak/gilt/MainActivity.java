package com.streak.gilt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.streak.gilt.DRVInterface.LoadMore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    private RecyclerView orders_rv ;
    private RecyclerView.Adapter order_rv_adapter;
    private List<OrdersRVModel> ordersList;
    private List<String> factoryList;
    SessionManager sessionManager;
    int userid;
    String userRole;
    ImageView addOrder,profile;
    ViewOrderMoreBottomSheet viewOrderMoreBottomSheet=new ViewOrderMoreBottomSheet();
    TextView factoryName;
    ConstraintLayout factoryDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        factoryList=new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager=new SessionManager(MainActivity.this);
        userid=sessionManager.getSession();
        factoryName=(TextView)findViewById(R.id.factory_name);
        userRole=sessionManager.getRole();
        addOrder=(ImageView) findViewById(R.id.add_order);
        factoryDropdown=(ConstraintLayout)findViewById(R.id.factory_drpdwn);
        profile=(ImageView) findViewById(R.id.icon_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, profile);
                popup.getMenuInflater().inflate(R.menu.account_menu, popup.getMenu());
                if(!(userRole.equals("admin")||userRole.equals("office"))){
                   popup.getMenu().findItem(R.id.add_model).setVisible(false);
                }
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

        factoryDropdown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, factoryDropdown);
                for (String element : factoryList) {
                    popup.getMenu().add(element);
                }
                popup.getMenu().add("All Factory");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        factoryName.setText(item.getTitle().toString());
                        filterMainMenu(item.getTitle().toString());
                        return false;
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
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        orders_rv.setLayoutManager(gridLayoutManager);
        ordersList=new ArrayList<>();
        GetFactoryList getFactoryList=new GetFactoryList();
        getFactoryList.execute();
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
            progressBar = (ProgressBar) findViewById(R.id.progressBarHome);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String customerName;
            String factoryName;
            int orderID;
            String modelName;
            String thumbnailEncoded;
            String creationdate;
            progressBar.setVisibility(View.GONE);
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
                        //thumbnailEncoded=orderItem.getString("thumbnail");
                        creationdate=orderItem.getString("creationdate");
                        creationdate=timeAgo(creationdate);
                        ordersList.add(new OrdersRVModel(orderID,modelName+" - "+factoryName,customerName,creationdate,factoryName));

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

    class GetFactoryList extends AsyncTask<Void, Void, String> {
        ProgressBar progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                if (!obj.getBoolean("error")) {
                    JSONArray items = obj.getJSONArray("factorylist");
                    factoryList.clear();
                    for (int it = 0; it < items.length(); it++) {
                        JSONObject orderItem = items.getJSONObject(it);
                        factoryList.add(orderItem.getString("factoryname"));
                    }
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
                return requestHandler.sendPostRequest(Urls.URL_GET_FACTORY_LIST,params);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            minimizeApp();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    public String timeAgo(String timeString) {
        long time_ago;
        Timestamp ts2 = java.sql.Timestamp.valueOf(timeString);
        time_ago = ts2.getTime()/1000;

        long cur_time = (Calendar.getInstance().getTimeInMillis()) / 1000;
        long time_elapsed = cur_time - time_ago;
        long seconds = time_elapsed;
        int minutes = Math.round(time_elapsed / 60);
        int hours = Math.round(time_elapsed / 3600);
        int days = Math.round(time_elapsed / 86400);
        int weeks = Math.round(time_elapsed / 604800);
        int months = Math.round(time_elapsed / 2600640);
        int years = Math.round(time_elapsed / 31207680);

        Timestamp ts=new Timestamp(time_ago*1000);
        Date date=new Date(ts.getTime());
        // Seconds
        if (seconds <= 60) {
            return "just now";
        }
        //Minutes
        else if (minutes <= 60) {
            if (minutes == 1) {
                return "one minute ago";
            } else {
                return minutes + " minutes ago";
            }
        }
        //Hours
        else if (hours <= 24) {
            if (hours == 1) {
                return "an hour ago";
            } else {
                return hours + " hrs ago";
            }
        }
        //Days
        else if (days <= 7) {
            if (days == 1) {
                return "yesterday";
            } else {
                return days + " days ago";
            }
        }
        //Weeks
        else if (weeks <= 4.3) {
            SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd");
            return(dt1.format(date));
        }
        //Months
        else if (months <= 11) {
            SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd");
            return(dt1.format(date));
        }
        //Years
        else {
            SimpleDateFormat dt1 = new SimpleDateFormat("dd MMM YYYY");
            return(dt1.format(date));
        }
    }

    public void filterMainMenu(String filterFactory){
        List<OrdersRVModel> ordersListFiltered=new ArrayList<>();
        if(filterFactory.equals("All Factory")){
            ordersListFiltered=ordersList;
        }
        else{
            for(OrdersRVModel order:ordersList){
                if(order.getFactoryName().equals(filterFactory)){
                    ordersListFiltered.add(order);
                }
            }
        }
        order_rv_adapter=new OrdersRVAdapter(ordersListFiltered,MainActivity.this);
        orders_rv.setAdapter(order_rv_adapter);
    }
}

