package com.nil.treeclause;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nil.treeclause.adapter.CommonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DataFillActivity extends AppCompatActivity implements LocationListener {


    Spinner spinner_ward,spinner_tree,spinner_tree_type,spinner_tree_condn,spinner_tree_ownership;
    TextView txt_scientific,txt_gps,txt_capture;
    CommonClass commonClass;
    CommonAdapter commonAdapter;
    ArrayList<CommonClass>wardcommonClassArrayList;
    ArrayList<CommonClass>treecommonClassArrayList;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    TextView tvLatitude, tvLongitude, txt_send,txt_map;
    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    public static SharedPreferences sharedpreferences;
    String UserId="",TreeType="",Address="",Ownername="",Gher="",Height="",Heritage="",Tree="",
            Condition="",WardNo="",TreeName="",ScientificName="",Ownership="";
    AppCompatRadioButton radio_yes,radio_no,radio_tree_yes,radio_tree_no;
    EditText edt_hight,edt_gher,edt_owner,edt_address;
    GridView grid_item;
    private File file;
    Uri outPutfileUri;
    private int RESULT_CAPTURE_IMG=101;
    private String attachment;
    private File Attachmentfile;
    ProgressDialog progressDoalog;
    public Handler mainThreadHandler;
    ImageView img_photo;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree_details_add_lay);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        spinner_ward = findViewById(R.id.spinner_ward);
        spinner_tree = findViewById(R.id.spinner_tree);
        spinner_tree_type = findViewById(R.id.spinner_tree_type);
        spinner_tree_condn = findViewById(R.id.spinner_tree_condn);
        spinner_tree_ownership = findViewById(R.id.spinner_tree_ownership);
        spinner_tree_type = findViewById(R.id.spinner_tree_type);
        txt_scientific = findViewById(R.id.txt_scientific);
        txt_gps = findViewById(R.id.txt_gps);
        txt_map = findViewById(R.id.txt_map);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        radio_yes = findViewById(R.id.radio_yes);
        radio_no = findViewById(R.id.radio_no);
        radio_tree_yes = findViewById(R.id.radio_tree_yes);
        radio_tree_no = findViewById(R.id.radio_tree_no);
        edt_gher = findViewById(R.id.edt_gher);
        edt_hight = findViewById(R.id.edt_hight);
        edt_owner = findViewById(R.id.edt_owner);
        edt_address = findViewById(R.id.edt_address);
        txt_send = findViewById(R.id.txt_send);
        txt_capture = findViewById(R.id.txt_capture);
        img_photo = findViewById(R.id.img_photo);

        wardcommonClassArrayList = new ArrayList<>();
        treecommonClassArrayList = new ArrayList<>();

        sharedpreferences = getSharedPreferences("LoggingPrefs", MODE_PRIVATE);
        UserId=sharedpreferences.getString("user","");


        getWard();



        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        spinner_tree_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TreeType =adapterView.getSelectedItem().toString();
                getTree();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_ward.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                WardNo =wardcommonClassArrayList.get(i).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_tree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TreeName=treecommonClassArrayList.get(i).getName();
                ScientificName=treecommonClassArrayList.get(i).getCode();
                txt_scientific.setText(ScientificName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_tree_ownership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Ownership=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_tree_condn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Condition=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        txt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DataFillActivity.this,MapsActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        txt_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isGPS && !isNetwork) {
                    Log.d(TAG, "Connection off");
                    showSettingsAlert();
                    getLastLocation();
                } else {
                    Log.d(TAG, "Connection on");
                    // check permissions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (permissionsToRequest.size() > 0) {
                            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                                    ALL_PERMISSIONS_RESULT);
                            Log.d(TAG, "Permission requests");
                            canGetLocation = false;
                        }
                    }

                    // get location
                    getLocation();




                }

            }
        });

        txt_send.setOnClickListener(new View.OnClickListener() {
            private ProgressDialog progressDialog;

            @Override
            public void onClick(View view) {
                if (Attachmentfile==null){
                    Toast.makeText(DataFillActivity.this,"Please click image",Toast.LENGTH_LONG).show();
                }else {
                    Address = edt_address.getText().toString();
                    Ownername = edt_owner.getText().toString();
                    Gher = edt_gher.getText().toString();
                    Height = edt_hight.getText().toString();
                    postdata();
                }





            }

        });


        radio_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (radio_no.isChecked()){
                    Heritage="0";
                }
            }
        });
        radio_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radio_yes.isChecked()){
                    Heritage="1";
                }

            }
        });
        radio_tree_yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radio_tree_yes.isChecked()){
                    Tree="1";
                }

            }
        });
        radio_tree_no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (radio_tree_no.isChecked()){
                    Tree="0";
                }
            }
        });

        txt_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
            }
        });
    }

    private void postdata() {

        progressDoalog = new ProgressDialog(DataFillActivity.this);
        progressDoalog.setMessage("Please Wait....");
        progressDoalog.setTitle("Request sending ...");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("user_id",UserId)
                .addFormDataPart("ward_no",WardNo)
                .addFormDataPart("serial_no","")
                .addFormDataPart("tree_type",TreeType)
                .addFormDataPart("comman_name",TreeName)
                .addFormDataPart("scientific_name",ScientificName)
                .addFormDataPart("height",Height)
                .addFormDataPart("girth",Gher)
                .addFormDataPart("condition",Condition)
                .addFormDataPart("ownership",Ownership)
                .addFormDataPart("owner_name",Ownername)
                .addFormDataPart("address",Address)
                .addFormDataPart("heritage",Heritage)
                .addFormDataPart("rare",Tree)
                .addFormDataPart("lat",tvLatitude.getText().toString())
                .addFormDataPart("lng",tvLongitude.getText().toString())
                .addFormDataPart("image[]", Attachmentfile.getAbsolutePath(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(Attachmentfile.toString())))
                .build();
        Request request = new Request.Builder()
               // .url("http://44.203.141.165/Api/add_tree_data")
                .url("https://tree-census.in/Api/add_tree_data")
                .method("POST", body)
                .build();



           /* mainThreadHandler = new Handler(Looper.getMainLooper()){
                @Override public void handleMessage(Message msg)
                { progressDoalog = new ProgressDialog(DataFillActivity.this);
                    progressDoalog.setMessage("Its loading....");
                    progressDoalog.setTitle("ProgressDialog bar example");
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDoalog.show(); } };*/

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String Status = jsonObject.getString("status");
                        String Msg = jsonObject.getString("message");
                        if (Status.equalsIgnoreCase("true")) {
                            progressDoalog.dismiss();
                             runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(DataFillActivity.this,
                                            "Record saved successfully!", Toast.LENGTH_LONG).show();
                                    edt_address.setText("");
                                    edt_gher.setText("");
                                    edt_hight.setText("");
                                    edt_owner.setText("");
                                    tvLatitude.setText("");
                                    tvLongitude.setText("");
                                    Attachmentfile=null;
                                    radio_no.setChecked(false);
                                    radio_yes.setChecked(false);
                                    radio_tree_no.setChecked(false);
                                    radio_tree_yes.setChecked(false);
                                    img_photo.setVisibility(View.GONE);

                                }
                            });



                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(DataFillActivity.this, Msg, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            });

    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                    200);
        } else {
            //startCameraIntent();
            dispatchTakePictureIntent();
        }
    }
    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_CAPTURE_IMG);
    }

    private void getTree() {
        RestAdapter adapter = new RestAdapter.Builder()
               // .setEndpoint("http://44.203.141.165/Api") //Setting the Root URL
                .setEndpoint("https://tree-census.in/Api") //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        UploadAPIs api = adapter.create(UploadAPIs.class);

        //Defining the method insertuser of our interface
        api.gettree(

                //Passing the values by getting it from editTexts
              TreeType,
                (new Callback<Response>() {
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
                                treecommonClassArrayList.clear();
                                JSONObject jsonObject=new JSONObject(output);
                                String Status=jsonObject.getString("status");
                                String Msg=jsonObject.getString("message");
                                if (Status.equalsIgnoreCase("true")){
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                                    for (int i=0;i<jsonArray.length();i++){
                                        CommonClass commonClass=new CommonClass();
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                         commonClass.setId(jsonObject1.getString("id"));
                                         commonClass.setName(jsonObject1.getString("tree_name"));
                                         commonClass.setCode(jsonObject1.getString("scientific_name"));
                                         treecommonClassArrayList.add(commonClass);

                                    }

                                    commonAdapter=new CommonAdapter(DataFillActivity.this,treecommonClassArrayList);
                                    spinner_tree.setAdapter(commonAdapter);


                                }else {
                                    Toast.makeText(DataFillActivity.this, Msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //Displaying the output as a toast
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            //If any error occured displaying the error as toast
                            Toast.makeText(DataFillActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
        ));
    }

    private void getWard() {
        RestAdapter adapter = new RestAdapter.Builder()
               // .setEndpoint("http://44.203.141.165/Api") //Setting the Root URL
                .setEndpoint("https://tree-census.in/Api") //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        UploadAPIs api = adapter.create(UploadAPIs.class);

        //Defining the method insertuser of our interface
        api.getward(

                //Passing the values by getting it from editTexts
                UserId,
                (new Callback<Response>() {
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
                            wardcommonClassArrayList.clear();
                            JSONObject jsonObject=new JSONObject(output);
                            String Status=jsonObject.getString("status");
                            String Msg=jsonObject.getString("message");
                            if (Status.equalsIgnoreCase("true")){
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                                    for (int i=0;i<jsonArray.length();i++){
                                        CommonClass commonClass=new CommonClass();
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                        commonClass.setId(jsonObject1.getString("id"));
                                        commonClass.setName(jsonObject1.getString("ward_name"));
                                        commonClass.setCode(jsonObject1.getString("ward_no"));
                                        wardcommonClassArrayList.add(commonClass);

                                    }

                                    commonAdapter=new CommonAdapter(DataFillActivity.this,wardcommonClassArrayList);
                                    spinner_ward.setAdapter(commonAdapter);
                            }else {
                                Toast.makeText(DataFillActivity.this, Msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Displaying the output as a toast
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        Toast.makeText(DataFillActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
                ));

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DataFillActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        tvLatitude.setText(String.format("%.6f",loc.getLatitude()));
        tvLongitude.setText(String.format("%.6f",loc.getLongitude()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 101) {
                String uri = outPutfileUri.toString();
                Log.e("uri-:", uri);
                try {

                    /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri);
                    img_photo.setVisibility(View.VISIBLE);
                     img_photo.setImageBitmap(bitmap);*/

                    //  FileOutputStream out = new FileOutputStream(A);

                 //   bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);

                    if (outPutfileUri.toString().contains("content")) {
                        handleSendImage(outPutfileUri);
                    }else {
                        File file = new File(getRealPathFromUri(DataFillActivity.this,outPutfileUri));//create path from uri
                        attachment = file.getName();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            else if(requestCode==2)
            {
                double lat= data.getDoubleExtra("lat",0);
                double lng= data.getDoubleExtra("lng",0);
                tvLatitude.setText(String.format("%.6f",lat));
                tvLongitude.setText(String.format("%.6f",lng));
            }



    } catch (Exception e) {
          /*  Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();*/
        }


    }

    public void handleSendImage(Uri imageUri) throws IOException {
        //Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            File file = new File(getCacheDir(), "image");
            InputStream inputStream=getContentResolver().openInputStream(imageUri);
            try {

                OutputStream output = new FileOutputStream(file);
                try {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;

                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }

                    output.flush();
                } finally {
                    output.close();
                }
            } finally {
                inputStream.close();
                byte[] bytes =getFileFromPath(file);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmapToUriConverter(bitmap);
                //Upload Bytes.
            }
        }
    }

    public static byte[] getFileFromPath(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;


        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, w, h,
                    true);
            String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString();
            File file = new File(path1 + "/" + "Tree");
            if (!file.exists())
                file.mkdirs();
            File file1 = new File(file, "Tree-"+ new Random().nextInt() + ".jpg");
            if (file1.exists())
                file1.delete();
            FileOutputStream out = new FileOutputStream(file1);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
            Attachmentfile=file1;
            attachment = file1.getAbsolutePath();
            Bitmap myBitmap = BitmapFactory.decodeFile(Attachmentfile.getAbsolutePath());
            img_photo.setVisibility(View.VISIBLE);
            img_photo.setImageBitmap(myBitmap);
            File f = new File(attachment);
            Toast.makeText(DataFillActivity.this,"Image save successfully",Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static String getRealPathFromUri(Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }

        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    private File createImageFile() throws IOException {
        String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString();
        File file1 = new File(path1, "Image-"+ new Random().nextInt() + ".jpg");
        if (file1.exists())
            file1.delete();
        attachment = file1.getAbsolutePath();
        return file1;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                Attachmentfile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (Attachmentfile != null) {
                outPutfileUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        Attachmentfile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                startActivityForResult(takePictureIntent, RESULT_CAPTURE_IMG);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}