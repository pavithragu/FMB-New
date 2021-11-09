package com.android.findmybus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<AdModel> list;
    ProgressDialog dialog;
    CardView bus, stage, stages, help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.ad_recyclerview);
        list = new ArrayList<>();

        bus = findViewById(R.id.bus_cardView);
        stage = findViewById(R.id.stage_cardView);
        stages = findViewById(R.id.stages_cardView);
        help = findViewById(R.id.help_cardView);

        bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BusActivity.class));
            }
        });

        stage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StageActivity.class));
            }
        });

        stages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StagesActivity.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });

        if (isConnected()) {
            new prepareJson().execute();
            Log.e("connection", "Internet Connected!...");
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setIcon(R.drawable.bus);
            alertDialog.setTitle("Connection Error!");
            alertDialog.setMessage("Active Internet connection required...");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            alertDialog.show();
            Log.e("connection", "Internet Not Connected!...");
        }

    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    @SuppressLint("StaticFieldLeak")
    private class prepareJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            Log.e("connection", "No Error Occurred in Pre Execute");

        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder builder;

            try {
                URL url = new URL("https://raw.githubusercontent.com/pavithragu/Json/main/ads.json");
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
                JSONArray array = object.getJSONArray("ads");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.getJSONObject(i);
                    String imageUrl = object1.getString("adImageUrl");
                    String linkUrl = object1.getString("adLinkUrl");
                    list.add(new AdModel(imageUrl, linkUrl));
                }
                Log.e("connection", "No Error Occurred While Reading JSON");
                prepareAds();
            } catch (JSONException e) {
                Log.e("connection", "Error Occurred While Reading JSON");
                dialog.dismiss();
            }
        }
    }

    private void prepareAds() {
        AdAdapter myAdapter = new AdAdapter(list, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
        dialog.dismiss();
        Log.e("connection", "MainActivity Fully Read! No Error Discovered...");
    }
}