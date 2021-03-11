package com.streak.gilt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddOrder extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE=4655;
    private int PICK_IMAGE_REQUEST=1;
    private Uri filepath;
    private Bitmap bitmap;
    EditText et_customername,et_mobilenumber,et_wieght,et_size,et_options,et_advance;
    TextView image_hyperlink,txt_filepath;
    Spinner et_factorname,et_modelname;
    ConstraintLayout img_layout;
    ImageView thumbnail;
    String filename;
    protected void onCreate(Bundle savedInstanceState) {
        requestStoragePermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        et_customername=this.findViewById(R.id.et_username);
        et_factorname=this.findViewById(R.id.sp_factory);
        et_modelname=this.findViewById(R.id.sp_model);
        et_mobilenumber=this.findViewById(R.id.et_mobile);
        et_wieght=this.findViewById(R.id.et_weight);
        et_size=this.findViewById(R.id.et_size);
        et_options=this.findViewById(R.id.et_option);
        et_advance=this.findViewById(R.id.et_advance);
        image_hyperlink=this.findViewById(R.id.attachment_hyperlink);
        thumbnail=this.findViewById(R.id.thumbnail);
        txt_filepath=this.findViewById(R.id.filename);
        img_layout=this.findViewById(R.id.attachmentview);

        Spinner modelSpinner=findViewById(R.id.sp_model);
        Spinner factorySpinner=findViewById(R.id.sp_factory);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Thaali");
        arrayList.add("Mothiram");
        arrayList.add("Ear Ring");
        arrayList.add("Aaram");

        ArrayList<String> arrayList1 = new ArrayList<>();
        arrayList1.add("Gandhipuram");
        arrayList1.add("Ukkadam");
        arrayList1.add("Peelamedu");
        arrayList1.add("Chinniyampalayam");
        arrayList1.add("Saravanampatti");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(arrayAdapter);
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String modelsName = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Selected: " + modelsName,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrayList1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        factorySpinner.setAdapter(arrayAdapter);
        factorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String factoryNames=parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Selected: " + factoryNames,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addorder(View v){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RequestHandler requestHandler = new RequestHandler();
        String input_customername,input_option,input_advance,input_factoryname,input_modelname;
        String input_mobilenumber;
        String input_weight,input_size;
        String input_factoryid,input_modelid;
        String path = getPath(filepath);
        input_customername=et_customername.getText().toString();
        input_advance=et_advance.getText().toString();
        input_option=et_options.getText().toString();
        input_factoryname=et_factorname.getSelectedItem().toString();
        input_modelname=et_modelname.getSelectedItem().toString();
        input_mobilenumber=et_mobilenumber.getText().toString();
        input_weight=et_wieght.getText().toString();
        input_size=et_size.getText().toString();


        if(input_factoryname.equals("Gandhipuram")){
            input_factoryid="1";
        }else if(input_factoryname.equals("Ukkadam")){
            input_factoryid="2";
        }else if(input_factoryname.equals("Peelamedu")){
            input_factoryid="3";
        }else if(input_factoryname.equals("Chinniyampalayam")){
            input_factoryid="4";
        }else{
            input_factoryid="5";
        }


        if(input_modelname.equals("Thaali")){
            input_modelid="1";
        }else if(input_modelname.equals("Mothiram")){
            input_modelid="2";
        }else if(input_modelname.equals("Ear ring")){
            input_modelid="3";
        }else{
            input_modelid="4";
        }


        class AddOrderReq extends AsyncTask<Void, Void, String> {

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
                    //JSONObject obj = new JSONObject(s);
                    //if (!obj.getBoolean("error")) {
                       // Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                        Toast.makeText(getApplicationContext(), "Order added successfully", Toast.LENGTH_SHORT).show();
                        moveToMain();

                   // } else {
                    //    Toast.makeText(getApplicationContext(), "Issue while adding record", Toast.LENGTH_SHORT).show();
                   // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put("customername", input_customername);
                params.put("mobilenumber", input_mobilenumber);
                params.put("modelid", input_modelid);
                params.put("factoryid", input_factoryid);
                params.put("weight", input_weight);
                params.put("size1", input_size);
                params.put("option1", input_option);
                params.put("advance", input_advance);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                params.put("image",imageString);
                params.put("imagename",filename);
                params.put("username","1");
                try{
                    //System.out.println("Sarath -- "+params);
                    return requestHandler.sendPostRequest(Urls.URL_ADD_ORDER, params);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                    return  null;
                }

            }
        }

        AddOrderReq ao = new AddOrderReq();
        ao.execute();
    }
    public void moveToMain(){
        Intent intent=new Intent(AddOrder.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void ShowFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            filepath = data.getData();
            try {
                img_layout.setVisibility(View.VISIBLE);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                thumbnail.setImageBitmap(bitmap);
                filename=getPath(filepath).substring(getPath(filepath).lastIndexOf("/")+1);
                txt_filepath.setText(filename);
                //Toast.makeText(getApplicationContext(),getPath(filepath),Toast.LENGTH_LONG).show();
            } catch (Exception ex) {

            }
        }
    }
    private String getPath(Uri uri) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null
        );
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }
    public void selectImage(View view)
    {
        ShowFileChooser();
    }
    public void closeattachment(View view){
        img_layout.setVisibility(View.INVISIBLE);
    }
}