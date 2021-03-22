package com.streak.gilt;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ViewOrderMoreBottomSheet extends BottomSheetDialogFragment {
    EditText etoldPassword,etnewPassword,etconfirmPassword;
    String op,np,cp;
    int userid;
    SessionManager sessionManager;
    Button changePassword;
    View view;
    public ViewOrderMoreBottomSheet() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.viewordermore_bottom_sheet_layout,container,false);
        etoldPassword=v.findViewById(R.id.et_oldpassword);
        etnewPassword=v.findViewById(R.id.et_newpassword);
        etconfirmPassword=v.findViewById(R.id.et_confirmpassword);
        changePassword=v.findViewById(R.id.btnChangePassword);

        changePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                op=etoldPassword.getText().toString();
                np=etnewPassword.getText().toString();
                cp=etconfirmPassword.getText().toString();
                sessionManager=new SessionManager(getContext());
                userid=sessionManager.getSession();
                view=v;
                if(TextUtils.isEmpty(op)||TextUtils.isEmpty(np)||TextUtils.isEmpty(cp)){
                    Toast.makeText(view.getContext(),"Please Fill all the values",Toast.LENGTH_SHORT).show();
                }
                else if(cp.length()<5){
                    Toast.makeText(view.getContext(),"Password length must be greater than 5",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!np.equals(cp))
                        Toast.makeText(view.getContext(),"New Password and Confirm Password not matched",Toast.LENGTH_SHORT).show();
                    else
                        updatePassword(op,cp);
                }
            }
        });
        return v;
    }
    public void updatePassword(String password, String newPassword){
            UserPasswordUpdate upu=new UserPasswordUpdate();
            upu.execute();
            etoldPassword.getText().clear();
            etconfirmPassword.getText().clear();
            etnewPassword.getText().clear();
            dismiss();
    }

    class UserPasswordUpdate extends AsyncTask<Void, Void, String> {

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
                    Toast.makeText(view.getContext(), "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            HashMap<String, String> params = new HashMap<>();
            params.put("newpassword", np);
            params.put("password", op);
            params.put("userid",""+userid);
            try{
                return requestHandler.sendPostRequest(Urls.URL_UPDATE_PASSWORD, params);
            }
            catch (Exception e){
                Toast.makeText(view.getContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }

}
