package com.streak.gilt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelManagement extends AppCompatActivity {
    ListView modellist;
    Button addModel;
    ModelBottomSheet modelBottomSheet=new ModelBottomSheet();
    ArrayAdapter<String> modelAdapter;
    ArrayList<String> models=new ArrayList<String>();
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_management);
        db = new DatabaseHelper(this);
        modellist=findViewById(R.id.modellist);
        addModel=findViewById(R.id.addmodelbutton);

        modelAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,models);
        modellist.setAdapter(modelAdapter);

        addModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelBottomSheet.show(getSupportFragmentManager(),"TAG");
            }
        });
        loadModels();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadModels();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.gilt.modelsaved"));
    }
    private void loadModels() {
        models.clear();
        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
                models.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
            } while (cursor.moveToNext());
        }
        modelAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,models);
        modellist.setAdapter(modelAdapter);
    }
    private void refreshList() {
        modelAdapter.notifyDataSetChanged();
    }
    private void saveNameToServer(String model) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Name...");
        progressDialog.show();

        final String name = model;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_ADD_MODEL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                saveNameToLocalStorage(name, 1);
                            } else {
                                saveNameToLocalStorage(name, 0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        saveNameToLocalStorage(name, 0);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("modelname", name);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void saveNameToLocalStorage(String name, int status) {
        db.addModel(name, status);
        models.add(name);
        refreshList();
    }
    public void onCloseDialog(String modelname){
        saveNameToServer(modelname);
    }

}
