package com.example.weathernow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRV;
    private ProgressBar loadingPB;
    private TextView cityNameTV, conditionTV;
    private TextInputEditText enterCityNameTIET;
    private RecyclerView weatherRV;
    private ImageView backgroundIV, iconIV, searchIV;
    private ArrayList<WeatherObject> weatherObjectArrayList;
    private WeatherAdapter weatherAdapter;
    private int PERMISSION_CODE = 1;
    private String cityNameString = "Bucuresti";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        homeRV = findViewById(R.id.idHomeRV);
        loadingPB = findViewById(R.id.idLoadingPB);
        cityNameTV = findViewById(R.id.idCityNameTV);
        enterCityNameTIET = findViewById(R.id.idEnterCityNameTIET);
        conditionTV = findViewById(R.id.idConditionTV);
        weatherRV = findViewById(R.id.idWeatherRV);
        backgroundIV = findViewById(R.id.idBackgroundIV);
        iconIV = findViewById(R.id.idIconIV);
        searchIV = findViewById(R.id.idSearchIV);
        weatherObjectArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherObjectArrayList);
        weatherRV.setAdapter(weatherAdapter);

        getWeatherInfo(cityNameString);
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = enterCityNameTIET.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(cityNameString);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getWeatherInfo(String cityNameString) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=eda075218e1141a5806142435222605&q=" + cityNameString + "&days=1&aqi=no&alerts=no";
        System.out.println(url);
        cityNameTV.setText(cityNameString);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingPB.setVisibility(View.GONE);
                        homeRV.setVisibility(View.VISIBLE);
                        weatherObjectArrayList.clear();
                        try {
                            String temperatureString = response.getJSONObject("current").getString("temp_c");

                            int isDay = response.getJSONObject("current").getInt("is_day");

                            String conditionString = response.getJSONObject("current").getJSONObject("condition").getString("text");
                            String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                            Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                            conditionTV.setText(temperatureString + "°C     " + conditionString);

                            if (isDay == 1) {
                                Picasso.get().load("https://wallpaperaccess.com/full/6133030.jpg").into(backgroundIV);
                            } else {
                                Picasso.get().load("https://images.fineartamerica.com/images-medium-large-5/starry-night-gilbert-rondilla-photography.jpg").into(backgroundIV);
                            }

                            JSONObject forecastJSON = response.getJSONObject("forecast");
                            JSONObject forecast = forecastJSON.getJSONArray("forecastday").getJSONObject(0);
                            JSONArray hourArray = forecast.getJSONArray("hour");

                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hour = hourArray.getJSONObject(i);
                                String timeString = hour.getString("time");
                                String temperatureString2 = hour.getString("temp_c");
                                String iconString = hour.getJSONObject("condition").getString("icon");
                                String windSpeedString = hour.getString("wind_kph");
                                weatherObjectArrayList.add(new WeatherObject(timeString, temperatureString2, iconString, windSpeedString));
                            }

                            weatherAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter Valid City", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
