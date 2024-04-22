package com.example.group_vien;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.group_vien.Utils.ApiService;
import com.example.group_vien.Utils.JsonQueryAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tencent.mmkv.MMKV;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Frag_Dashboard extends Fragment {

    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    TextView text_location, text_temp, text_humidity, text_wind_speed, text_population, text_workplace_ss, text_employRate;
    Retrofit retrofit_weather_api;
    ApiService weather_api;
    Retrofit retrofit_px_api;
    ApiService px_api;
    JsonQueryAdapter jsonQueryAdapter;
    MMKV kv;
    List<Integer> populations = new ArrayList<Integer>();
    String[] years = {"1990","1991","1992","1993","1994",
                    "1995","1996","1997","1998","1999",
                    "2000","2001","2002","2003","2004",
                    "2005","2006","2007","2008","2009",
                    "2010","2011","2012","2013","2014",
                    "2015","2016","2017","2018","2019",
                    "2020","2021","2022"};
    BarChart barChart;
    List<BarEntry> barEntries;
    public Frag_Dashboard(){}
    public static Frag_Dashboard newInstance() {
        Frag_Dashboard fragment = new Frag_Dashboard();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_dashboard, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text_location = view.findViewById(R.id.tv_location);
        text_temp = view.findViewById(R.id.tv_temp);
        text_humidity = view.findViewById(R.id.tv_humidity);
        text_wind_speed = view.findViewById(R.id.tv_windspeed);
        text_population = view.findViewById(R.id.tv_population);
        text_workplace_ss = view.findViewById(R.id.tv_self_sulficiency);
        text_employRate= view.findViewById(R.id.tv_employment);
        barChart = view.findViewById(R.id.chart1);
        jsonQueryAdapter = new JsonQueryAdapter();
        barEntries = new ArrayList<>();
        kv = MMKV.defaultMMKV();
        String areaCode = kv.decodeString("areaCode");
        String area = kv.decodeString("area");
        String weather_api_key = "24b3b9b08cf4b30637005951aead8dc0";

        //fetch weather api
        ExecutorService weatherApiExcutor = Executors.newSingleThreadExecutor();
        weatherApiExcutor.execute(() -> {
            retrofit_weather_api =  new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            weather_api = retrofit_weather_api.create(ApiService.class);
            Call<ResponseBody> getGeoCall = weather_api.getAreaLocation(area, "1", weather_api_key);
            try{

                //get latitude and longitude of the city
                Response<ResponseBody> geoResponse = getGeoCall.execute();
                String geoMessage = geoResponse.body().string();
                String[] geoValues = jsonQueryAdapter.getGeoValues(geoMessage);

                //after get geoValues, we will call weather api
                Call<ResponseBody> getWeatherCall = weather_api.getWeatherData(geoValues[0], geoValues[1], weather_api_key);
                Response<ResponseBody> weatherResponse = getWeatherCall.execute();
                String weatherMessage = weatherResponse.body().string();
                String[] weatherValues = jsonQueryAdapter.getWeatherValues(weatherMessage);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_temp.setText(weatherValues[0]+"°K");
                        text_humidity.setText(weatherValues[1]+"%");
                        text_wind_speed.setText(weatherValues[2]+"m/s");
                        text_location.setText(area);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        //fetch px api.
        ExecutorService pxApiExcutor = Executors.newSingleThreadExecutor();
        pxApiExcutor.execute(() -> {
            retrofit_px_api = new Retrofit.Builder()
                    .baseUrl("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            px_api = retrofit_px_api.create(ApiService.class);

            String populationQuery;
            String workplaceQuery;
            String employmentRateQuery;
            RequestBody jsonQuery;
            try {
                //Population call
                populationQuery = jsonQueryAdapter.getPopulationQuery(areaCode);
                jsonQuery = RequestBody.create(populationQuery, JSON);
                Call<ResponseBody> getPopulationCall = px_api.getPopulation(jsonQuery);
                Response<ResponseBody> populationResponse = getPopulationCall.execute();
                String populationMessage = populationResponse.body().string();
                populations = jsonQueryAdapter.getPopulation(populationMessage);

                //Workplace self-sufficiency call
                workplaceQuery = jsonQueryAdapter.getWorkplaceQuery(areaCode);
                jsonQuery = RequestBody.create(workplaceQuery, JSON);
                Call<ResponseBody> getWorkplaceSsCall = px_api.getWorkplaceSs(jsonQuery);
                Response<ResponseBody> workplaceSsResponse = getWorkplaceSsCall.execute();
                String workplaceSsMessage = workplaceSsResponse.body().string();
                String workplaceSs = jsonQueryAdapter.getWorkplaceSs(workplaceSsMessage);

                //Employment rate call
                employmentRateQuery = jsonQueryAdapter.getEmploymentRateQuery(areaCode);
                jsonQuery = RequestBody.create(employmentRateQuery, JSON);
                Call<ResponseBody> getEmploymentRateCall = px_api.getEmploymentRate(jsonQuery);
                Response<ResponseBody> employmentRateResponse = getEmploymentRateCall.execute();
                String employmentRateMessage = employmentRateResponse.body().string();
                String employmentRate = jsonQueryAdapter.getEmploymentRate(employmentRateMessage);
                Log.d("Area",area);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_population.setText(populations.get(populations.size()-1).toString());
                        text_workplace_ss.setText(workplaceSs + "%");
                        text_employRate.setText(employmentRate + "%");
                    }
                });
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        ExecutorService chartExecutor = Executors.newSingleThreadExecutor();
        chartExecutor.execute(() -> {
            while(populations.size()==0){}
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //set barchart data
                    for(int i=0; i < years.length; i++){
                        barEntries.add(new BarEntry(i, populations.get(i)));
                    }
                    BarDataSet barDataSet = new BarDataSet(barEntries, "Yearly Populations");
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    Description description = new Description();
                    description.setText("Years");
                    barChart.setDescription(description);

                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(years));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setLabelRotationAngle(270);
                    barChart.animateY(2000);
                    barChart.invalidate();
                }
            });
        });
    }


}
