package com.streak.gilt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewOrderActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private static final int STORAGE_PERMISSION_CODE=4655;
    private OrderDetailsFragment orderDetailsFragment;
    private ActivitiesFragment activitiesFragment;
    private CustomerInfoFragment customerInfoFragment;
    ImageView viewmoreorder,editIcon;
    public int orderID;
    TextView orderidtxt;
    ImageView orderImage;
    Bitmap decodedBitmap,bitmap;
    String userRole;
    int userid;
    SessionManager sessionManager;
    View orderdetailsview;
    ConstraintLayout vieworder;
    String customerName;
    String factoryName;
    Bitmap img;
    String imageencoded, audioencoded,audioPath;
    Boolean isAudioPresent;
    String modelName,size,weight,seal,option,mobilenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestStoragePermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        Bundle b = getIntent().getExtras();
        orderidtxt=findViewById(R.id.orderdetails_orderid);
        orderImage=findViewById(R.id.imageView);
        viewmoreorder=findViewById(R.id.img_viewmoreorder);
        vieworder=findViewById(R.id.vieworderlayout);
        orderdetailsview=findViewById(R.id.view_pager);
        orderdetailsview.setDrawingCacheEnabled(true);
        orderdetailsview.buildDrawingCache();
        sessionManager=new SessionManager(ViewOrderActivity.this);
        userRole=sessionManager.getRole();
        editIcon=findViewById(R.id.editOrder);
        userid=sessionManager.getSession();
        if(b != null) {
            orderID = b.getInt("orderID");
        }

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder();
            }
        });
        if(!userRole.equals("admin")){
            editIcon.setVisibility(View.GONE);
        }

        viewmoreorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu=new PopupMenu(ViewOrderActivity.this,viewmoreorder);
                popupMenu.getMenuInflater().inflate(R.menu.order_menu,popupMenu.getMenu());
                if(!userRole.equals("admin")){
                    popupMenu.getMenu().findItem(R.id.delete_order).setVisible(false);

                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Download Image")){
                            requestStoragePermission();
                            try {
                                savebitmap(decodedBitmap,""+orderID);
                                Toast.makeText(getApplicationContext(),"Image Downloaded",Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(),"Error while saving Image",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(item.getTitle().equals("Export Order")){
                            //exportOrder();
                        }
                        else if(item.getTitle().equals("Delete Order")){
                            deleteOrder(orderID);
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        orderidtxt.setText(""+orderID);
        viewPager=findViewById(R.id.view_pager);
        tabLayout=findViewById(R.id.tabLayout);
        GetOrder gl = new GetOrder();
        gl.execute();

    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    public void showImage(View view) {
        Dialog builder = new Dialog(this,R.style.DialogTheme);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ZoomableImageView touch = new ZoomableImageView(this,null);
        touch.setImageBitmap(decodedBitmap);

        //ImageView imageView = new ImageView(this);
        //imageView.setImageBitmap(decodedBitmap);
        builder.addContentView(touch, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
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
                        mobilenumber=orderItem.getString("mobilenumber");
                        imageencoded=orderItem.getString("image");
                        if(orderItem.has("audio")){
                            audioencoded=orderItem.getString("audio");
                            byte[] decodedAudioString = Base64.decode(audioencoded, Base64.DEFAULT);
                            try
                            {
                                boolean success = true;
                                File folder = new File(Environment.getExternalStorageDirectory()
                                        + File.separator + "gilt"+File.separator +orderID);
                                if (!folder.exists()) {
                                    success = folder.mkdirs();
                                }
                                if (success) {
                                    File f = new File(Environment.getExternalStorageDirectory()
                                            + File.separator + "gilt"+File.separator +orderID+File.separator +"record"+orderID+".3gp");
                                    f.createNewFile();
                                    audioPath=f.getPath();
                                    FileOutputStream fo = new FileOutputStream(f);
                                    fo.write(decodedAudioString);
                                    fo.close();
                                } else {

                                }

                                System.out.println("Audio recorded");
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            isAudioPresent=true;
                        }
                        else
                            isAudioPresent=false;

                    byte[] decodedString = Base64.decode(imageencoded, Base64.DEFAULT);
                    decodedBitmap= BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);

                    orderImage.setImageBitmap(decodedBitmap);


                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(decodedBitmap, 20, 30, true));
                    orderImage.setBackground(d);

                    orderDetailsFragment=new OrderDetailsFragment(modelName,size,weight,factoryName,option,seal,isAudioPresent,audioPath);
                    activitiesFragment=new ActivitiesFragment();
                    customerInfoFragment=new CustomerInfoFragment();
                    tabLayout.setupWithViewPager(viewPager);

                    ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager(),0);
                    viewPagerAdapter.addFragment(orderDetailsFragment,"Order");
                    viewPagerAdapter.addFragment(customerInfoFragment,"Customer");
                    viewPagerAdapter.addFragment(activitiesFragment,"Activities");

                    viewPager.setAdapter(viewPagerAdapter);
                }else {
                    //System.out.println("Sarath -- Invalid params");
                    Toast.makeText(getApplicationContext(), "Invalid params called", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public int getDominantColor(Bitmap bitmap) {
            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
            final int color = newBitmap.getPixel(0, 0);
            newBitmap.recycle();
            return color;
        }
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            HashMap<String, String> params = new HashMap<>();
            params.put("orderid",""+orderID);
            try{
                return requestHandler.sendPostRequest(Urls.URL_GET_ORDER_DETAILS,params);
            }
            catch (Exception e){

                Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }
    public void deleteOrder(int orderID){

       //de.execute();
        new AlertDialog.Builder(this,R.style.AlertDialogCustom)
                .setTitle("Delete confirmation")
                .setMessage("Do you really want to Delete? This might lead to data loss")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DeleteOrder de=new DeleteOrder();
                        de.execute();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
    class DeleteOrder extends AsyncTask<Void, Void, String> {

        ProgressBar progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //progressBar.setVisibility(View.GONE);
            try {
                JSONObject obj = new JSONObject(s);
                if (!obj.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), "Order Deleted Successfully", Toast.LENGTH_SHORT).show();
                    moveToMain();
                } else {
                   Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
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
            params.put("userid", ""+userid);
            try{
                return requestHandler.sendPostRequest(Urls.URL_DELETE_ORDER, params);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }
    public static void savebitmap(Bitmap bmp, String orderID) throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File folder = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "gilt"+File.separator +orderID);
        File giltFolder = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "gilt");
        boolean success = true;
        if (!giltFolder.exists()) {
            System.out.println("sarath debug - "+giltFolder.getAbsolutePath());
            giltFolder.mkdir();
        }
        else {
            System.out.println("sarath debug - folder present "+giltFolder.getAbsolutePath());
        }
        if (!folder.exists()) {
                success = folder.mkdirs();
        }
        if (success) {
                File f = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "gilt"+File.separator +orderID+File.separator +"orderImage"+orderID+".jpg");
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
        } else {

        }
    }
    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    public void moveToMain(){
        Intent intent=new Intent(ViewOrderActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void updateOrder(){
        Intent intent=new Intent(ViewOrderActivity.this,AddOrder.class);
        Bundle b=new Bundle();

        b.putInt("orderID",orderID);
        b.putString("customername",customerName);
        b.putString("modelname",modelName);
        b.putString("factoryname",factoryName);
        b.putString("size",size);
        b.putString("weight",weight);
        b.putString("mobilenumber",mobilenumber);
        b.putString("option",option);
        b.putString("seal",seal);
        b.putString("imgstr",convertBitmaptoString(getResizedBitmap(decodedBitmap,30)));
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String convertBitmaptoString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}