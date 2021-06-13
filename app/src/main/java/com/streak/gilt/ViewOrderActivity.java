package com.streak.gilt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
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
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.os.Environment.getExternalStoragePublicDirectory;

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
    byte[] decodedAudioString;
    Uri audiouri,deleteUri;

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
        decodedAudioString=new byte[0];
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
                                //saveImage(decodedBitmap,""+orderID);
                                saveBitmap(getApplicationContext(),decodedBitmap, Bitmap.CompressFormat.JPEG,"image/jpeg","orderImage "+orderID);
                                Toast.makeText(getApplicationContext(),"Image Downloaded",Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(),"Error while saving Image",Toast.LENGTH_SHORT).show();
                                System.out.println("Sarath error "+e.getMessage());
                            }
                        }
                        else if(item.getTitle().equals("Export Order")){
                            String content="Order :"+orderID+"\nModel : "+modelName+"\nWeight : "+weight+"\nHeight : "+size+"\nOptions :"+option;
                            try {
                                Uri uri;
                                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                                    uri=saveBitmap(getApplicationContext(),decodedBitmap, Bitmap.CompressFormat.JPEG,"image/jpeg","orderImage "+orderID);
                                    sendMessage(v,content,uri);
                                }
                                else{
                                        uri=savebitmapOld(decodedBitmap,orderID+"");
                                        sendMessage(v,content,uri);
                                }
                            }
                            catch (IOException ex){

                            }
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
                            decodedAudioString = Base64.decode(audioencoded, Base64.DEFAULT);
                            isAudioPresent=true;
                        }
                        else
                            isAudioPresent=false;

                    byte[] decodedString = Base64.decode(imageencoded, Base64.DEFAULT);
                    decodedBitmap= BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);

                    orderImage.setImageBitmap(decodedBitmap);


                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(decodedBitmap, 20, 30, true));
                    orderImage.setBackground(d);

                    try {
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                            audioPath=saveAudio(decodedAudioString,orderID+"");
                        }
                        else{
                            audioPath=saveAudioOld(decodedAudioString,orderID+"");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
    public void deleteOrder(int orderID){//de.execute();
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

    public static Uri savebitmapOld(Bitmap bmp, String orderID) throws IOException {

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

        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "gilt"+File.separator +orderID+File.separator +"orderImage"+orderID+".jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        System.out.println(f.getAbsolutePath());
        return Uri.parse(f.getAbsolutePath());


    }

    @NonNull
    public Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                          @NonNull final Bitmap.CompressFormat format,
                          @NonNull final String mimeType,
                          @NonNull final String displayName) throws IOException {

        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+ File.separator + "Gilt");

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");
            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");
                if (!bitmap.compress(format, 95, stream))
                    throw new IOException("Failed to save bitmap.");
            }
            return uri;
        }
        catch (IOException e) {
            if (uri != null) {
                resolver.delete(uri, null, null);
            }
            throw e;
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

    public void sendMessage(View v, String message, Uri uri1) throws FileNotFoundException {
        String whatsAppMessage = message;
        Uri uri = uri1;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("image/jpeg");
        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }

    public String saveAudio(byte[] decodedAudioString,String orderid) throws IOException {
        Context context=getApplicationContext();
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "giltRecording"+orderid+".mp3");
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music");

        deleteUri=getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        String path=getRealPathFromURI(getApplicationContext(),deleteUri);


        if(path.contains(" (1)")){
            path=path.replace(" (1)","");
        }
        System.out.println("Delete Path - "+path);

        if(!path.isEmpty()){
            File fdelete =new File(path);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    System.out.println("file Deleted :" );
                } else {
                    System.out.println("file not Deleted :");
                }
            }
        }

        File destination = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "giltRecording"+orderid+".mp3");
        audiouri =getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(audiouri,"wrt");
        FileDescriptor fileDescriptor = descriptor.getFileDescriptor();

        //InputStream dataInputStream = openFileInput(getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath());

        OutputStream output = new FileOutputStream(fileDescriptor);
        byte[] buf = decodedAudioString;
        int bytesRead;
        output.write(buf, 0,buf.length);
        output.close();
        return destination.getAbsolutePath();
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public  String saveAudioOld(byte[] decodedAudioString,String orderID){
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
                        + File.separator + "gilt"+File.separator +orderID+File.separator +"record"+orderID+".mp3");
                f.createNewFile();
                audioPath=f.getPath();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(decodedAudioString);
                fo.close();
            } else {

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return audioPath;
    }
}