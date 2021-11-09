package com.android.findmybus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

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

public class BusInfo extends AppCompatActivity {

    private final ArrayList<BusInfoModel> list = new ArrayList<>();
    private ProgressDialog dialog;
    RecyclerView recyclerView;
    TextView busName, busNumber, busType, busCapacity;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_info);

        recyclerView = findViewById(R.id.recyclerView_busInfoActivity);
        busName = findViewById(R.id.busName_busInfoActivity);
        busNumber = findViewById(R.id.busNumber_busInfoActivity);
        busType = findViewById(R.id.busType_busInfoActivity);
        busCapacity = findViewById(R.id.busCapacity_busInfoActivity);

        key = getIntent().getStringExtra("busName");

        if (isConnected()) {
            new prepareJson().execute();
            Log.e("connection", "Internet Connected!...");
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(BusInfo.this);
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
        ConnectivityManager manager = (ConnectivityManager) BusInfo.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    @SuppressLint("StaticFieldLeak")
    private class prepareJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(BusInfo.this);
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
                    String name = object1.getString("busName");

                    if (name.equals(key)) {
                        String number = object1.getString("busNumber");
                        String type = object1.getString("busType");
                        String capacity = object1.getString("busCapacity");

                        busName.setText(name);
                        busNumber.setText(number);
                        busType.setText(type);
                        busCapacity.setText(capacity);

                        JSONArray jsonArray = object1.getJSONArray("busSchedule");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            String time = jsonArray.getString(j);
                            Log.e("connection", "Time, Stage: " + time);
                            list.add(new BusInfoModel(time.substring(0, 5), time.substring(6)));
                        }
                        break;
                    }
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
        BusInfoAdapter adapter = new BusInfoAdapter(list, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        dialog.dismiss();
        Log.e("connection", "MainActivity Fully Read! No Error Discovered...");
    }
}