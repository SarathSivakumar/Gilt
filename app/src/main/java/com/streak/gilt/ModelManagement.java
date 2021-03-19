package com.streak.gilt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ModelManagement extends AppCompatActivity {
    ListView modellist;
    Button addModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_management);
        modellist=findViewById(R.id.modellist);
        addModel=findViewById(R.id.add_model);

        populateList();
    }
    public void populateList(){
        ArrayList<String> models=new ArrayList<String>();
        models.add("sarath");
        models.add("venki");
        models.add("sabari");
        ArrayAdapter<String> modelAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,models);
        modellist.setAdapter(modelAdapter);
    }
}