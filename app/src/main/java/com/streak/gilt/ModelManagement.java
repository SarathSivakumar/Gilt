package com.streak.gilt;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelManagement extends AppCompatActivity {
    ListView modellist;
    Button addModel;
    ModelBottomSheet modelBottomSheet=new ModelBottomSheet();
    ArrayAdapter<String> modelAdapter;
    ArrayList<String> models=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_management);
        modellist=findViewById(R.id.modellist);
        addModel=findViewById(R.id.addmodelbutton);
        addModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelBottomSheet.show(getSupportFragmentManager(),"TAG");
            }
        });

        populateList();
    }
    public void populateList(){
       GetModelList gml=new GetModelList();
       gml.execute();
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
                    }
                    modelAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,models);
                    modellist.setAdapter(modelAdapter);
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

    public void onCloseDialog(String modelname){
        RequestHandler requestHandler = new RequestHandler();
        class AddModel extends AsyncTask<Void, Void, String> {
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
                try {
                    Toast.makeText(getApplicationContext(), "Model added successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> params = new HashMap<>();
                params.put("modelname", modelname);
                try{
                    System.out.println("Sarath Log print -- "+params);
                    return requestHandler.sendPostRequest(Urls.URL_ADD_MODEL, params);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                    return  null;
                }

            }
        }

        AddModel am = new AddModel();
        am.execute();
        populateList();
    }
}
