package com.nil.treeclause;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.nil.treeclause.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public class MainActivity extends AppCompatActivity {


    EditText edt_email,et_pass;
    TextView txt_send;
    String Email="",Password="",UserId="";
    public static SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_email=findViewById(R.id.edt_email);
        et_pass=findViewById(R.id.et_pass);
        txt_send=findViewById(R.id.txt_send);

        sharedpreferences = getSharedPreferences("LoggingPrefs", MODE_PRIVATE);
        UserId=sharedpreferences.getString("user","");

        if (UserId.equalsIgnoreCase("")){

        }else {
            startActivity(new Intent(MainActivity.this,DataFillActivity.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }



        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email=edt_email.getText().toString();
                Password=et_pass.getText().toString();
                Validatelogin();
            }
        });

    }
    private void Validatelogin(){
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter
        RestAdapter adapter = new RestAdapter.Builder()
                //.setEndpoint("http://44.203.141.165/Api") //Setting the Root URL
                .setEndpoint("https://tree-census.in/Api") //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        UploadAPIs api = adapter.create(UploadAPIs.class);

        //Defining the method insertuser of our interface
        api.Login(

                //Passing the values by getting it from editTexts
                edt_email.getText().toString(),
                et_pass.getText().toString(),

                   new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        BufferedReader reader = null;

                        //An string to store output from the server
                        String output = "";

                        try {
                            //Initializing buffered reader
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            //Reading the output in the string
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jsonObject=new JSONObject(output);
                            String Status=jsonObject.getString("status");
                            String Msg=jsonObject.getString("message");
                            if (Status.equalsIgnoreCase("true")){
                                String UserId=jsonObject.getString("user_id");
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("user",UserId);
                                editor.commit();
                                Toast.makeText(MainActivity.this, "Login successfully!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this,DataFillActivity.class).
                                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();

                            }else {
                                Toast.makeText(MainActivity.this, Msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Displaying the output as a toast
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        Toast.makeText(MainActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}