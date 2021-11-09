package com.android.findmybus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeSet;

public class StageInfoActivity extends AppCompatActivity {

    ArrayList<StageInfoModel> list = new ArrayList<>();
    TreeSet<String> treeSet = new TreeSet<>();
    ProgressDialog dialog;
    RecyclerView recyclerView;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_info);

        recyclerView = findViewById(R.id.recyclerView_stageInfoActivity);

        key = getIntent().getStringExtra("stageName");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(key);

        if (isConnected()) {
            new StageInfoActivity.prepareJson().execute();
            Log.e("connection", "Internet Connected!...");
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(StageInfoActivity.this);
            alertDialog.setIcon(R.drawable.bus);
            alertDialog.setTitle("Connection Error!");
            alertDialog.setMessage("Active Internet connection required...");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Close", (dialog, which) -> finishAffinity());
            alertDialog.show();
            Log.e("connection", "Internet Not Connected!...");
        }
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) StageInfoActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    @SuppressLint("StaticFieldLeak")
    private class prepareJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(StageInfoActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            Log.e("connection", "No Error Occurred in Pre Execute");

        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder builder;

            try {
                URL url = new URL("https://raw.githubusercontent.com/pavithragu/Json/main/schedule.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                Log.e("connection", "No Error Occurred While getting JSON");
                Log.e("connection", builder.toString());
                return (builder.toString());
            } catch (IOException e) {
                Log.e("connection", "Error Occurred While getting JSON");
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                JSONArray array = object.getJSONArray("busList");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.getJSONObject(i);
                    String busName = object1.getString("busName");
                    JSONArray schedule = object1.getJSONArray("busSchedule");
                    for (int j=0; j<schedule.length(); j++){
                        String time = schedule.getString(j).substring(0, 5);
                        String stage = schedule.getString(j).substring(6);
                        if (stage.equals(key)){
                            treeSet.add(time + " " + busName);
                        }
                    }
                }
                for (String i: treeSet){
                    list.add(new StageInfoModel(i));
                }
                Log.e("connection", "No Error Occurred While Reading JSON");
                prepareBusList();
            } catch (JSONException e) {
                Log.e("connection", "Error Occurred While Reading JSON");
                dialog.dismiss();
            }
        }
    }

    private void prepareBusList() {
        StageInfoAdapter adapter = new StageInfoAdapter(list, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        dialog.dismiss();
        Log.e("connection", "MainActivity Fully Read! No Error Discovered...");
    }
}