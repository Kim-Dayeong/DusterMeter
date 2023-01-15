package com.example.dustmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Gson gson = new Gson();

    //실외
    TextView put_outdate;
    TextView put_outpm;
    TextView put_outpm25;
    //실내
    TextView put_indate;
    Button Grp_btn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //실외
        put_outdate = findViewById(R.id.put_outdate);
        put_outpm = findViewById(R.id.put_outpm);
        put_outpm25 = findViewById(R.id.put_outpm25);

        //실내
        put_indate = findViewById(R.id.put_indate);

        //그래프
        Grp_btn = (Button)findViewById(R.id.Grp_btn);

        Grp_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), GraphPage.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute("http://192.168.219.100:7700/");
                new JSONTask2().execute("http://192.168.219.100:7700/in");
            }
        });

    }
    public class JSONTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection con = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);

                con = (HttpURLConnection)url.openConnection();
                con.connect();

                InputStream stream = con.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) buffer.append(line);

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                con.disconnect();
                try {
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {

            try{
                JSONObject jsonObject = new JSONObject(s);
                Log.d("s", jsonObject.toString());
                Log.d("time ", jsonObject.getString("time"));
                String time =  jsonObject.getString("time");
                String pm10 =  jsonObject.getString("pm10");
                String pm25 = jsonObject.getString("pm25");

                put_outdate.setText(time);
                put_outpm.setText(pm10);
                put_outpm25.setText(pm25);

            } catch (Throwable tx) {
                Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
            }
            };

        }
    public class JSONTask2 extends AsyncTask<String,String,String> { //실내, /in

        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection con = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);

                con = (HttpURLConnection)url.openConnection();
                con.connect();

                InputStream stream = con.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) buffer.append(line);

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                con.disconnect();
                try {
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            try{
                JSONObject jsonObject = new JSONObject(s);
                Log.d("s", jsonObject.toString());
                Log.d("data ", jsonObject.getString("data"));

                //String data =  jsonObject.getString("data");
                JSONObject data = jsonObject.getJSONObject("data");
                String date =  jsonObject.getString("date");
                System.out.println("날짜"+ date);
                put_indate.setText(date);

            } catch (Throwable tx) {
                Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
            }
        };


    }



    }

