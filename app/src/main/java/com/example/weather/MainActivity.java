package com.example.weather;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.android.volley.Request.Method;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ModelLoader;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout parentLayout;
    private LinearLayout llt;
    private ProgressBar progressBar;
     TextView cityname,temperature,weatherstatus;
    private EditText editTextCity;
    private ImageView weathericon;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<Model> dataList;
    private LocationManager locationManager;

    private int PERMISSION_CODE=1;
    private ImageView search;
    private String currentCityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //for displaying the application in full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        parentLayout=findViewById(R.id.parentLayout);
        llt=findViewById(R.id.linearLayouttap);
        progressBar=findViewById(R.id.progressBar);
        cityname=findViewById(R.id.textView);
        temperature=findViewById(R.id.textView2);
        weatherstatus=findViewById(R.id.textView3);
        recyclerView=findViewById(R.id.recycler);
        search=findViewById(R.id.search);
        weathericon=findViewById(R.id.weathericon);
        editTextCity=findViewById(R.id.edittxt_cityname);
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        progressBar.setVisibility(View.INVISIBLE);
        dataList=new ArrayList<>();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        adapter=new RecyclerAdapter(dataList,MainActivity.this);
        recyclerView.setAdapter(adapter);
        currentCityName="Indore";
        getWeatherInfo(currentCityName);
     /*   Location location=null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // if permission is not granted then request for permission
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if(location!=null)
        { currentCityName=getCityNameFromCoord(location.getLongitude(),location.getLatitude());
        getWeatherInfo(currentCityName);}
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }*/
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(editTextCity.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
                else {
                    cityname.setText(editTextCity.getText().toString());
                    getWeatherInfo(editTextCity.getText().toString());
                }
            }
        });
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }*/

    private String getCityNameFromCoord(double longitude, double latitude) // with help of Geocoder
    {
        String cityName="Not found";
        Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addressList=gcd.getFromLocation(latitude,longitude,10);
            for (Address temp : addressList){
                if(temp!=null)
                {
                    String city=temp.getLocality();
                    if(city!=null && !city.isEmpty())
                    {
                        cityName=city;
                    }
                    else {
                        Log.d("LOCATION","City not found");
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "User's City not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityname)
    {
        cityname=Character.toUpperCase(cityname.charAt(0))+cityname.substring(1);
        String url="https://api.weatherapi.com/v1/forecast.json?key=00d1296c075f44c8a7961921232210&q="+cityname+"&days=1&aqi=yes&alerts=yes";
        this.cityname.setText(cityname);
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                if (response!=null)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    JSONObject jsonObject=response;
                    dataList.clear();
                    try {
                        String temp=response.getJSONObject("current").getString("temp_c");
                        temperature.setText(temp+"°C");;
                        int isDay=response.getJSONObject("current").getInt("is_day");
                        if(isDay==1)
                        {
                            parentLayout.setBackgroundColor(getResources().getColor(R.color.purple_200));
                        }
                        else {
                            parentLayout.setBackgroundColor(getResources().getColor(R.color.black));
                        }
                        String weatherstat=response.getJSONObject("current").getJSONObject("condition").getString("text");
                        weatherstatus.setText(weatherstat);

                        String weatherIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");

                        Log.d("ICON",weatherIcon);

                        JSONObject forecast=response.getJSONObject("forecast");
                        JSONObject forecast0=forecast.getJSONArray("forecastday").getJSONObject(0);
                        JSONArray hourlyforecast=forecast0.getJSONArray("hour");
                        for(int i=0;i<hourlyforecast.length();i++)
                        {
                            JSONObject hourlyData=hourlyforecast.getJSONObject(i);
                            String timeh=hourlyData.getString("time");
                            String temph=hourlyData.getString("temp_c");
                            String imgh=hourlyData.getJSONObject("condition").getString("icon");
                            Log.d("IMAGE",imgh);
                            String windh=hourlyData.getString("wind_kph");
                            dataList.add(new Model(timeh,temph,imgh,windh));
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                Toast.makeText(MainActivity.this, "Please enter a valid city name", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}