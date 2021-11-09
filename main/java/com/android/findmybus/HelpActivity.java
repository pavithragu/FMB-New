package com.android.findmybus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity {

    RecyclerView view;
    ArrayList<HelpModel> list;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        view = findViewById(R.id.help_recyclerview);
        list = new ArrayList<>();

        if (isConnected()){
            new prepareJson().execute();
            Log.e("connection", "Internet Connected!...");
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HelpActivity.this);
            alertDialog.setIcon(R.drawable.bus);
            alertDialog.setTitle("Connection Error!");
            alertDialog.setMessage("Active Internet connection required...");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener(){
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
        ConnectivityManager manager = (ConnectivityManager) HelpActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    @SuppressLint("StaticFieldLeak")
    private class prepareJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HelpActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            Log.e("connection", "No Error Occurred in Pre Execute");
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder builder;

            try {
                URL url = new URL("https://raw.githubusercontent.com/pavithragu/Json/main/help.json");
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
                JSONArray array = object.getJSONArray("help");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.getJSONObject(i);
                    String d1 = object1.getString("name");
                    String d2 = object1.getString("number");
                    Log.e("connection", d1 + " " + d2);
                    list.add(new HelpModel(d1, d2));
                }
                Log.e("connection", "No Error Occurred While Reading JSON");
                prepareList();
            } catch (Exception e) {
                Log.e("connection", "Error Occurred While Reading JSON");
                dialog.dismiss();
            }
        }
    }

    private void prepareList() {
        HelpAdapter adapter = new HelpAdapter(list, HelpActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        view.setLayoutManager(layoutManager);
        view.setAdapter(adapter);
        dialog.dismiss();
        Log.e("connection", "MainActivity Fully Read! No Error Discovered...");
    }

}