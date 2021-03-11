package com.streak.gilt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewOrderActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private OrderDetailsFragment orderDetailsFragment;
    private ActivitiesFragment activitiesFragment;
    private CustomerInfoFragment customerInfoFragment;

    private int orderID;
    TextView orderidtxt;
    ImageView orderImage;
    Bitmap decodedBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        Bundle b = getIntent().getExtras();
        orderidtxt=findViewById(R.id.orderdetails_orderid);
        orderImage=findViewById(R.id.imageView);
        if(b != null)
            orderID = b.getInt("orderID");
        orderidtxt.setText(""+orderID);
        viewPager=findViewById(R.id.view_pager);
        tabLayout=findViewById(R.id.tabLayout);
        GetOrder gl = new GetOrder();
        gl.execute();

       // orderDetailsFragment=new OrderDetailsFragment("sarath","12.34");



    }

    public void showImage(View view) {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(decodedBitmap);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment>fragments=new ArrayList<>();
        private List<String> fragmentTitle=new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }


    class GetOrder extends AsyncTask<Void, Void, String> {

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
            Bitmap img;
            String modelName,size,weight,seal,option;
            System.out.println("Sarath -- Reached here");
            String imageencoded;
            //progressBar.setVisibility(View.GONE);
            try {
                JSONObject obj = new JSONObject(s);

                if (!obj.getBoolean("error")) {
                    //JSONArray items = obj.getJSONArray("order");
                        JSONObject orderItem = obj.getJSONObject("order");
                        customerName = orderItem.getString("customername");
                        orderID = Integer.parseInt(orderItem.getString("id"));
                        modelName = orderItem.getString("model");
                        factoryName=orderItem.getString("factoryname");
                        size = orderItem.getString("size1");
                        weight=orderItem.getString("weight");
                        seal = orderItem.getString("seal");
                        option=orderItem.getString("option1");
                        imageencoded=orderItem.getString("image");
                    byte[] decodedString = Base64.decode(imageencoded, Base64.DEFAULT);
                    decodedBitmap= BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);

                        orderImage.setImageBitmap(decodedBitmap);
                    orderDetailsFragment=new OrderDetailsFragment(modelName,size,weight,factoryName,option,seal);
                    activitiesFragment=new ActivitiesFragment();
                    customerInfoFragment=new CustomerInfoFragment();
                    tabLayout.setupWithViewPager(viewPager);


                    ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager(),0);
                    viewPagerAdapter.addFragment(orderDetailsFragment,"Order");
                    viewPagerAdapter.addFragment(customerInfoFragment,"Customer");
                    viewPagerAdapter.addFragment(activitiesFragment,"Activities");

                    viewPager.setAdapter(viewPagerAdapter);
                }else {
                    System.out.println("Sarath -- Invalid params");
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
            params.put("orderid",""+orderID);
            System.out.println("Sarath -- Reached call");
            try{
                return requestHandler.sendPostRequest(Urls.URL_GET_ORDER_DETAILS,params);
            }
            catch (Exception e){

                Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }
}