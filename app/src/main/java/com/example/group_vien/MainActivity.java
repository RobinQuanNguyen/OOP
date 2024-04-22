package com.example.group_vien;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.group_vien.Utils.ApiService;
import com.example.group_vien.Utils.JsonQueryAdapter;
import com.tencent.mmkv.MMKV;


import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import javax.inject.Inject;
public class MainActivity extends AppCompatActivity {
    Retrofit retrofit;
    ApiService apiService ;
    JsonQueryAdapter jsonQueryAdapter;
    SearchView searchView;
    ListView listView;
    ArrayAdapter<String > adapter;
    List<String> areas = new ArrayList<>();
    Map<String, String> areasMap = new HashMap<String, String>();
    MMKV kv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView = (SearchView) findViewById(R.id.searchView);
        listView = (ListView) findViewById(R.id.lv1);
        jsonQueryAdapter = new JsonQueryAdapter();
        //fetch city list
        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
        networkExecutor.execute(() -> {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
            Call<ResponseBody> getAreaCall = apiService.getArea();
            try {
                Response<ResponseBody> selectorInfo = getAreaCall.execute();

                String message = selectorInfo.body().string();
                areas = jsonQueryAdapter.getAreas(message);
                Log.d("Area",areas.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,areas);
                        listView.setAdapter(adapter);
                    }
                });
                areasMap = jsonQueryAdapter.getNumbericArea(message);
                Log.d("Area Map", areasMap.toString());
            } catch (IOException e) {
                Log.d("selectorInfo", "Something Wrong!!");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(areas.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                kv = MMKV.defaultMMKV();
                kv.encode("areaCode", areasMap.get(parent.getAdapter().getItem(position).toString()));
                kv.encode("area", parent.getAdapter().getItem(position).toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }
}
