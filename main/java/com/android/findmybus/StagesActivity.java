package com.android.findmybus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

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

public class StagesActivity extends AppCompatActivity {

    ProgressDialog dialog;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stages);

        if (isConnected()) {
            new StagesActivity.prepareJson().execute();
            Log.e("connection", "Internet Connected!...");
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(StagesActivity.this);
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
        ConnectivityManager manager = (ConnectivityManager) StagesActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }

    @SuppressLint("StaticFieldLeak")
    private class prepareJson extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(StagesActivity.this);
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
            TreeSet<String> treeSet = new TreeSet<>();
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                JSONArray array = object.getJSONArray("busList");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.getJSONObject(i);
                    JSONArray schedule = object1.getJSONArray("busSchedule");
                    for (int j=0; j<schedule.length(); j++){
                        String stageName = schedule.getString(j).substring(6);
                        treeSet.add(stageName);
                    }
                }
                Log.e("connection", "No Error Occurred While Reading JSON");
                dialog.dismiss();
            } catch (JSONException e) {
                Log.e("connection", "Error Occurred While Reading JSON");
                dialog.dismiss();
            }

            AutoCompleteTextView from = (AutoCompleteTextView) findViewById(R.id.fromACTV);
            AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.toACTV);
            Button submit = (Button) findViewById(R.id.button_stagesActivity);

            ArrayList<String> array = new ArrayList<>(treeSet);
            Log.e("connection", array.toString());

            adapter = new ArrayAdapter<>(StagesActivity.this, android.R.layout.simple_list_item_1, array);
            from.setAdapter(adapter);
            to.setAdapter(adapter);
            dialog.dismiss();

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stage1 = from.getText().toString();
                    String stage2 = to.getText().toString();
                    if (array.contains(stage1)){
                        if (array.contains(stage2)){
                            if (!stage1.equals(stage2)){
                                Intent intent = new Intent(StagesActivity.this, StagesInfoActivity.class);
                                intent.putExtra("one", stage1);
                                intent.putExtra("two", stage2);
                                startActivity(intent);
                            } else {
                                Toast.makeText(StagesActivity.this, "Choose Different Stages!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(StagesActivity.this, "Invalid Destination!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StagesActivity.this, "Invalid Origin!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}