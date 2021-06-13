package com.streak.gilt;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextSwitcher;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddOrder extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE=4655;
    private int PICK_IMAGE_REQUEST=1;
    private Uri filepath;
    private Bitmap bitmap;
    EditText et_customername,et_mobilenumber,et_wieght,et_size,et_options,et_seal;
    TextView image_hyperlink,txt_filepath,txt_addorderheader,close_recording;
    Spinner et_factorname,et_modelname;
    TextSwitcher recordText;
    ConstraintLayout img_layout;
    ImageView thumbnail,record_btn;
    int orderid;
    public TextView timerTextView;
    private long startHTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    String filename,customfilepath,pathsave;
    Spinner modelSpinner,factorySpinner;
    ArrayList<String> models;
    ArrayList<String> modelid;
    Button addOrderBtn;
    Bitmap decodedBitmap;
    ParcelFileDescriptor file;
    Boolean isAudioRecorded;
    ArrayList<String> factories;
    ArrayList<String> factoryID;
    Bundle b;
    Uri audiouri,deleteUri;
    int btnStatus=1;
    ScrollView scrollView;
    MediaRecorder mediaRecorder;
    final int REQUEST_PERMISSION_RECORD_CODE=1000;
    Intent intent;
    String action,type;

    protected void onCreate(Bundle savedInstanceState) {
        requestStoragePermission();
        super.onCreate(savedInstanceState);
        isAudioRecorded=false;
        intent= getIntent();
       action = intent.getAction();
        type = intent.getType();

        setContentView(R.layout.activity_add_order);
        addOrderBtn=this.findViewById(R.id.add_order_btn);
        et_customername=this.findViewById(R.id.et_username);
        et_factorname=this.findViewById(R.id.sp_factory);
        et_modelname=this.findViewById(R.id.sp_model);
        et_mobilenumber=this.findViewById(R.id.et_mobile);
        et_wieght=this.findViewById(R.id.et_weight);
        et_size=this.findViewById(R.id.et_size);
        et_options=this.findViewById(R.id.et_option);
        et_seal=this.findViewById(R.id.et_seal);
        image_hyperlink=this.findViewById(R.id.attachment_hyperlink);
        thumbnail=this.findViewById(R.id.thumbnail);
        txt_filepath=this.findViewById(R.id.filename);
        img_layout=this.findViewById(R.id.attachmentview);
        txt_addorderheader=this.findViewById(R.id.txt_addorderheader);
        scrollView=findViewById(R.id.add_order_scrollview);
        modelSpinner=findViewById(R.id.sp_model);
        factorySpinner=findViewById(R.id.sp_factory);
        models = new ArrayList<>();
        modelid=new ArrayList<>();
        record_btn=findViewById(R.id.recordBtn);
        recordText=findViewById(R.id.recordTxtSwitcher);
        close_recording=findViewById(R.id.closerecording);
        factories=new ArrayList<>();
        factoryID=new ArrayList<>();

        GetModelList gml=new GetModelList();
        gml.execute();

        GetFactoryList gfl=new GetFactoryList();
        gfl.execute();
        b = getIntent().getExtras();

        recordText.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        recordText.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
        recordText.setCurrentText("Tap Mic to Speak");

        File folder = Environment.getExternalStorageDirectory();
        String fileName = folder.getPath() + "/gilt/temprecord.3gp";

        File myFile = new File(fileName);
        if(myFile.exists())
            myFile.delete();

        if(b != null) {
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    Uri image_received = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (image_received != null) {
                        try {
                            System.out.println(image_received);
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_received);
                            thumbnail.setImageBitmap(bitmap);
                            txt_filepath.setText(image_received.toString().substring(image_received.toString().lastIndexOf("%")));
                            //customfilepath=getPath(image_received);
                            filename="sarath";
                            img_layout.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                           Toast.makeText(getApplicationContext(),"Problem while attaching image",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else if(b.containsKey("orderID")){
                orderid = b.getInt("orderID");
                txt_addorderheader.setText("Edit Order - " + orderid);
                et_customername.setText(b.getString("customername"));
                et_mobilenumber.setText(b.getString("mobilenumber"));
                txt_filepath.setText(b.getString("filename"));
                et_wieght.setText(b.getString("weight"));
                et_size.setText(b.getString("size"));
                et_options.setText(b.getString("option"));
                et_seal.setText(b.getString("seal"));
                img_layout.setVisibility(View.VISIBLE);
                customfilepath = "customfilepathsarath";
                txt_filepath.setText("Image.jgp");
                byte[] decodedString = Base64.decode(b.getString("imgstr"), Base64.DEFAULT);
                decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                thumbnail.setImageBitmap(decodedBitmap);
                addOrderBtn.setText("Update");
            }
        }
        addOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addOrderBtn.getText().equals("Add")){
                    addorder();
                }
                else if (addOrderBtn.getText().equals("Update")){
                    updateorderdetail();
                }
            }
        });

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
        scrollView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    scrollView.scrollBy(0, 150);
            }
        });

        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    recordingProcess();
                }
                else {
                    recordingProcessOld();
                }
            }
        });

        close_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordText.setText("Tap Mic to Speak");
                btnStatus=1;
                isAudioRecorded=false;
                close_recording.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void recordingProcess(){
        File folder = new File(Environment.getExternalStorageDirectory()
                + File.separator + "gilt"+File.separator );
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            pathsave= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Gilt/"+"temprecord.3gp";
        } else {

        }
        if(checkPermissionForRecord()){
            if(btnStatus==1){
                recordingProcessStart();
            }
            else if(btnStatus==2){
                recordingProcessStop();
            }
        }
        else{
            requestRecordPermission();
        }
    }
    public void recordingProcessStart(){
        if(btnStatus==1){

            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.playtopause);
            record_btn.setImageDrawable(drawable);
            drawable.start();
            startHTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            btnStatus=2;
            recordText.setText("Recording...");
            setupMediaRecorder();
            try{
                ContentValues values = new ContentValues(4);
                values.put(MediaStore.Audio.Media.DISPLAY_NAME,"temp.mp3");
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");
                getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,MediaStore.Audio.Media.DISPLAY_NAME+" in ('temp.mp3')",null);
                deleteUri=getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                deleteAudioTemp(deleteUri);

                audiouri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                file = getContentResolver().openFileDescriptor(audiouri, "wr");
                if (file != null) {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setOutputFile(file.getFileDescriptor());
                    mediaRecorder.setAudioChannels(1);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                }
                startHTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void recordingProcessStop(){
        if(btnStatus==2) {
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.pausetoplay);
            record_btn.setImageDrawable(drawable);
            drawable.start();
            mediaRecorder.stop();
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            //recordText.setText("Audio Recorded"+recordText.toString());
            TextView tv = (TextView) recordText.getCurrentView();
            if (tv.getText().toString().length() > 0) {
                tv.setText("Audio Recorded - " + tv.getText().toString());
                isAudioRecorded = true;
            }
            isAudioRecorded = true;
            btnStatus = 3;
            close_recording.setVisibility(View.VISIBLE);
        }
    }
    public void requestRecordPermission(){
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        },REQUEST_PERMISSION_RECORD_CODE);
    }
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            if (recordText != null)
                recordText.setText("" + String.format("%02d", mins) + ":"
                        + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

    public void setupMediaRecorder(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathsave);
    }
    public boolean checkPermissionForRecord(){
        int write_external_storage_result=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_permission=ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result==PackageManager.PERMISSION_GRANTED && record_permission==PackageManager.PERMISSION_GRANTED;
    }
    public void addorder(){
        recordingProcessStop();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RequestHandler requestHandler = new RequestHandler();
        String input_customername,input_option,input_seal,input_factoryname,input_modelname;
        String input_mobilenumber;
        String input_weight,input_size;
        String input_factoryid,input_modelid;
        //String path = getPath(filepath);
        input_customername=et_customername.getText().toString();
        input_seal=et_seal.getText().toString();
        input_option=et_options.getText().toString();
        input_factoryname=et_factorname.getSelectedItem().toString();
        input_modelname=et_modelname.getSelectedItem().toString();
        input_mobilenumber=et_mobilenumber.getText().toString();
        input_weight=et_wieght.getText().toString();
        input_size=et_size.getText().toString();
        input_factoryid=factoryID.get(factories.indexOf(input_factoryname));
        input_modelid=modelid.get(models.indexOf(input_modelname));

        class AddOrderReq extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;
            Bitmap thumbnail;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);


                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        // Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                        Toast.makeText(getApplicationContext(), "Order added successfully", Toast.LENGTH_SHORT).show();
                        moveToMain();
                    } else {
                        System.out.println(obj.get("response"));
                        Toast.makeText(getApplicationContext(), "Issue while adding record", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Issue while adding order, Please check connection", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {

                String audioString="sarath";
                HashMap<String, String> params = new HashMap<>();
                params.put("customername", input_customername);
                params.put("mobilenumber", input_mobilenumber);
                params.put("modelid", input_modelid);
                params.put("factoryid", input_factoryid);
                params.put("weight", input_weight);
                params.put("size1", input_size);
                params.put("option1", input_option);
                params.put("seal", input_seal);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                baos.reset();
                Float width = new Float(bitmap.getWidth());
                Float height = new Float(bitmap.getHeight());
                Float ratio = width/height;
                thumbnail=Bitmap.createScaledBitmap(bitmap, (int)(100 * ratio), 100, false);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbnailBytes = baos.toByteArray();
                String thumbnailImageString=Base64.encodeToString(thumbnailBytes, Base64.DEFAULT);

                params.put("image",imageString);
                params.put("thumbnail",thumbnailImageString);
                params.put("imagename",filename);
                params.put("username","1");
                if(isAudioRecorded){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        File file = new File(getRealPathFromURI(getApplicationContext(),audiouri));
                        try{
                            FileInputStream in=new FileInputStream(file);
                            byte fileContent[] = new byte[(int)file.length()];
                            in.read(fileContent,0,fileContent.length);
                            audioString = Base64.encodeToString(fileContent,0);
                        }
                        catch (Exception e){
                            System.out.println("Sarath - "+e.getMessage());
                        }
                        //String audioString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        params.put("audio",audioString);
                        params.put("audioname","record.mp3");
                    }
                    else{
                        File file = new File(Environment.getExternalStorageDirectory() + "/Gilt/temprecord.mp3");
                        try{
                            FileInputStream in=new FileInputStream(file);
                            byte fileContent[] = new byte[(int)file.length()];
                            in.read(fileContent,0,fileContent.length);
                            audioString = Base64.encodeToString(fileContent,0);
                        }
                        catch (Exception e){
                            System.out.println("Sarath - "+e.getMessage());
                        }
                        params.put("audio",audioString);
                        params.put("audioname","record.mp3");
                    }
                }
                try{
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
        Intent intent = new Intent(Intent.ACTION_EDIT);
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
                customfilepath=getPath(filepath);
                filename=getPath(filepath).substring(getPath(filepath).lastIndexOf("/")+1);
                txt_filepath.setText(filename);
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

    class GetModelList extends AsyncTask<Void, Void, String> {

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

            try {
                JSONObject obj = new JSONObject(s);
                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                if (!obj.getBoolean("error")) {
                    JSONArray items = obj.getJSONArray("modellist");
                    models.clear();
                    for (int it = 0; it < items.length(); it++) {
                        JSONObject orderItem = items.getJSONObject(it);
                        models.add(orderItem.getString("modelname"));
                        modelid.add(orderItem.getString("modelid"));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, models);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    modelSpinner.setAdapter(arrayAdapter);
                    if(b != null) {
                        if(b.containsKey("orderID")){
                            modelSpinner.setSelection(models.indexOf(b.get("modelname").toString()));
                        }
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
                return requestHandler.sendPostRequest(Urls.URL_GET_MODEL_LIST,params);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }

    class GetFactoryList extends AsyncTask<Void, Void, String> {
        ProgressBar progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject obj = new JSONObject(s);
                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                if (!obj.getBoolean("error")) {
                    JSONArray items = obj.getJSONArray("factorylist");
                    factories.clear();
                    for (int it = 0; it < items.length(); it++) {
                        JSONObject orderItem = items.getJSONObject(it);
                        factories.add(orderItem.getString("factoryname"));
                        factoryID.add(orderItem.getString("factoryid"));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, factories);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    factorySpinner.setAdapter(arrayAdapter);
                    if(b != null) {
                        if(b.containsKey("orderID")){
                            factorySpinner.setSelection(factories.indexOf(b.get("factoryname").toString()));
                        }

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

    public void updateorderdetail(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RequestHandler requestHandler = new RequestHandler();
        String input_customername,input_option,input_seal,input_factoryname,input_modelname;
        String input_mobilenumber;
        String input_weight,input_size;
        String input_factoryid,input_modelid;

        String path ="";
        input_customername=et_customername.getText().toString();
        input_seal=et_seal.getText().toString();
        input_option=et_options.getText().toString();
        input_factoryname=et_factorname.getSelectedItem().toString();
        input_modelname=et_modelname.getSelectedItem().toString();
        input_mobilenumber=et_mobilenumber.getText().toString();
        input_weight=et_wieght.getText().toString();
        input_size=et_size.getText().toString();
        input_factoryid=factoryID.get(factories.indexOf(input_factoryname));
        input_modelid=modelid.get(models.indexOf(input_modelname));

        class UpdateOrderReq extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;
            String audioString;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                        moveToMain();
                    } else {
                        Toast.makeText(getApplicationContext(), "Issue while updating order", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put("customername", input_customername);
                params.put("orderid",""+orderid);
                params.put("mobilenumber", input_mobilenumber);
                params.put("modelid", input_modelid);
                params.put("factoryid", input_factoryid);
                params.put("weight", input_weight);
                params.put("size1", input_size);
                params.put("option1", input_option);
                params.put("seal", input_seal);
                params.put("username","1");

                if(isAudioRecorded){
                    File file = new File(Environment.getExternalStorageDirectory() + "/Gilt/temprecord.3gp");
                    try{
                        FileInputStream in=new FileInputStream(file);
                        byte fileContent[] = new byte[(int)file.length()];
                        in.read(fileContent,0,fileContent.length);
                        audioString = Base64.encodeToString(fileContent,0);
                    }
                    catch (Exception e){
                        System.out.println("Sarath - "+e.getMessage());
                    }
                    //String audioString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    params.put("audio",audioString);
                    params.put("audioname","record.3gp");
                }

                if(customfilepath.equals("customfilepathsarath")){
                    params.put("imagemodified","unmodified");
                    params.put("image","");
                    params.put("imagename","");
                }
                else{
                    params.put("imagemodified","modified");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    params.put("image",imageString);
                    params.put("imagename",filename);
                }


                try{
                    //System.out.println("Sarath -- "+params);
                    return requestHandler.sendPostRequest(Urls.URL_UPDATE_ORDER, params);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                    return  null;
                }

            }
        }

        UpdateOrderReq uor = new UpdateOrderReq();
        uor.execute();
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

    public void deleteAudioTemp(Uri deleteUri){
        String path=getRealPathFromURI(getApplicationContext(),deleteUri);
        System.out.println("delete audio path - "+path);
        if(path.contains(" (1)")){
            path=path.replace(" (1)","");
        }
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
    }

    //Pre Android Q
    public void recordingProcessOld(){
        File folder = new File(Environment.getExternalStorageDirectory()
                + File.separator + "gilt"+File.separator );
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            pathsave= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Gilt/"+"temprecord.mp3";
        } else {

        }
        if(checkPermissionForRecord()){
            if(btnStatus==1){
                recordingProcessOldStart();
            }
            else if(btnStatus==2){
                recordingProcessOldStop();
            }
        }
        else{
            requestRecordPermission();
        }
    }
    public void recordingProcessOldStart(){
        if(btnStatus==1){
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.playtopause);
            record_btn.setImageDrawable(drawable);
            drawable.start();
            startHTime = 0L;
            timeInMilliseconds = 0L;
            timeSwapBuff = 0L;
            updatedTime = 0L;
            btnStatus=2;
            recordText.setText("Recording...");
            setupMediaRecorder();
            try{
                mediaRecorder.prepare();
                mediaRecorder.start();
                startHTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void recordingProcessOldStop(){
        if(btnStatus==2) {
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.pausetoplay);
            record_btn.setImageDrawable(drawable);
            drawable.start();
            mediaRecorder.stop();
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            //recordText.setText("Audio Recorded"+recordText.toString());
            TextView tv = (TextView) recordText.getCurrentView();
            if (tv.getText().toString().length() > 0) {
                tv.setText("Audio Recorded - " + tv.getText().toString());
                isAudioRecorded = true;
            }
            isAudioRecorded = true;
            btnStatus = 3;
            close_recording.setVisibility(View.VISIBLE);
        }
    }
}