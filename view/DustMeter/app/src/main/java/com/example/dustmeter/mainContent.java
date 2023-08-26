package com.example.dustmeter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class mainContent extends AppCompatActivity { //실내 - activity_main_content.xml

    //실내
    TextView put_indate;
    TextView input_temper;
    TextView input_humi;
    TextView input_pm25;
    TextView put_pm15;
    TextView name;

    Button btn_json;
    Button button;

    TextView textView3;
    TextView scrolltext;

    SharedPreferences pref;
    SharedPreferences.Editor editor;



    static String urlvalue = "http://10.0.13.26:7700";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);
        new JSONTask2().execute(urlvalue+"/in");
        System.out.println("test!!!!!!!!!!!!!!!!!!!!!1");
        //new Userinfo.JSONTask5().execute(urlvalue+"/user");

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        put_indate = findViewById(R.id.put_indate);
        input_temper = findViewById(R.id.input_temper);
        input_humi = findViewById(R.id.input_humi);
        input_pm25 = findViewById(R.id.input_pm25);
        put_pm15 = findViewById(R.id.put_pm15);
        name = findViewById(R.id.name);

        //scrolltext = (TextView)findViewById(R.id.textView);
        //scrolltext.setMovementMethod(new ScrollingMovementMethod());
       // String text = "";

           // scrolltext.setText(text);
        String prefValue = pref.getString("name",null);
            if (prefValue == null){
                name.setText("");
            }else{
        name.setText(pref.getString("name", String.valueOf(name)));
    }



    }


    public class JSONTask2 extends AsyncTask<String,String,String> { //실내, /in

        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection con = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);

                con = (HttpURLConnection) url.openConnection();
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
            } finally {
                con.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.d("s", jsonObject.toString());


                String data =  jsonObject.getString("data");
                JSONObject jsondata = new JSONObject(data);

               // JSONObject datapar = jsonObject.getJSONObject("data");
                Log.d("jsondata", jsondata.toString());

                String date = jsondata.getString("date");

                System.out.println(date);

                int pm1 = jsondata.getInt("pm1");
                int pm15 = jsondata.getInt("pm15");
                int pm20 = jsondata.getInt("pm20");
                int humi = jsondata.getInt("humi");
                int temper = jsondata.getInt("temper");

                put_indate.setText(date);
                put_pm15.setText(String.valueOf(pm15));
                input_humi.setText(String.valueOf(humi));
                input_pm25.setText(String.valueOf(pm20));
                input_temper.setText(String.valueOf(temper));





            } catch (Throwable tx) {
               tx.printStackTrace();
               put_indate.setText(tx.getMessage());
            }


        }



    }











}
