package com.streak.gilt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddOrder extends AppCompatActivity {
    EditText et_customername,et_mobilenumber,et_wieght,et_size,et_options,et_advance;
    Spinner et_factorname,et_modelname;
    protected void onCreate(Bundle savedInstanceState) {
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
        RequestHandler requestHandler = new RequestHandler();
        String input_customername,input_option,input_advance,input_factoryname,input_modelname;
        String input_mobilenumber;
        String input_weight,input_size;
        String input_factoryid,input_modelid;

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

                try{
                    System.out.println("Sarath -- "+params);
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
}