package com.streak.gilt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ModelBottomSheet extends BottomSheetDialogFragment {
    public ModelBottomSheet(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.model_bottom_sheet_layout,container,false);
        Button btAddModel=v.findViewById(R.id.btnNewModelAdd);
        EditText etNewModelName=v.findViewById(R.id.etNewModelName);
        btAddModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modelName=etNewModelName.getText().toString();
                if (!modelName.isEmpty()){
                    dismiss();
                    etNewModelName.getText().clear();
                    ModelManagement activity = (ModelManagement) getActivity();
                    activity.onCloseDialog(modelName);
                }
                else {
                    Toast.makeText(getContext(),"Please enter the Model Name",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
}

