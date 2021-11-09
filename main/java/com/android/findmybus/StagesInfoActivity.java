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

public class StagesInfoActivity extends AppCompatActivity {

    ArrayList<StageInfoModel> list = new ArrayList<>();
    TreeSet<String> treeSet = new TreeSet<>();
    ProgressDialog dialog;
    RecyclerView recyclerView;
    String stage1, stage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stages_info);

        recyclerView = findViewById(R.id.recyclerView_stagesInfoActivity);

        stage1 = getIntent().getStringExtra("one");
        stage2 = getIntent().getStringExtra("two");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(stage1 + " - " + stage2);

        if (isConnected()) {
            new StagesInfoActivity.prepareJson().execute();
            Log.e("connection", "Internet Connected!...");
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(StagesInfoActivity.this);
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
        ConnectivityManager manager = (ConnectivityManager) StagesInfoActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    @SuppressLint("StaticFieldLeak")
    private class prepareJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(StagesInfoActivity.this);
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
                    String x = null;
                    for (int j=0; j<schedule.length(); j++){
                        String time = schedule.getString(j).substring(0, 5);
                        String stage = schedule.getString(j).substring(6);
                        if (stage.equals(stage1)){
                            x = time + " " + busName;
                        }
                        if (stage.equals(stage2)){
                            if (x != null){
                                treeSet.add(x);
                                x= null;
                            }
                        }
                    }
                }
                for (String i: treeSet){
                    list.add(new StageInfoModel(i));
                }
            } catch (JSONException e) {
                Log.e("connection", "Error Occurred While Reading JSON");
                dialog.dismiss();
            }
            StageInfoAdapter adapter = new StageInfoAdapter(list, StagesInfoActivity.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(StagesInfoActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            dialog.dismiss();
            Log.e("connection", "MainActivity Fully Read! No Error Discovered...");
        }
    }
}