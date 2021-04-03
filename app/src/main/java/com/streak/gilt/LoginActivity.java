package com.streak.gilt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername,etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername=(EditText)findViewById(R.id.txt_username);
        etPassword=(EditText)findViewById(R.id.txt_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionManager sessionManager=new SessionManager(LoginActivity.this);
        int userID=sessionManager.getSession();
        if(userID!=-1){
            moveToMain();
        }
        else{

        }
    }

    public void login(View v){

        String input_username=etUsername.getText().toString();
        String input_password=etPassword.getText().toString();

        User user=new User(1,input_username,"admin");

        if (TextUtils.isEmpty(input_username)) {
            etUsername.setError("Please enter your username");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(input_password)) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            return;
        }

        class UserLogin extends AsyncTask<Void, Void, String> {

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
                    if (!obj.getBoolean("error")) {
                        //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("user");
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("role")
                        );
                        SessionManager sessionManager=new SessionManager(LoginActivity.this);
                        sessionManager.saveSession(user);
                        finish();
                        moveToMain();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("username", input_username);
                params.put("password", input_password);

                try{
                    return requestHandler.sendPostRequest(Urls.URL_LOGIN, params);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                    return  null;
                }

            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    public void moveToMain(){
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}