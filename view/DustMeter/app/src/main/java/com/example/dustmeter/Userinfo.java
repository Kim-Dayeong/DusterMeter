package com.example.dustmeter;

import static com.example.dustmeter.mainContent.urlvalue;
import static com.example.dustmeter.text.asthma;
import static com.example.dustmeter.text.blood;
import static com.example.dustmeter.text.breath;
import static com.example.dustmeter.text.eye;
import static com.example.dustmeter.text.nose;
import static com.example.dustmeter.text.old;
import static com.example.dustmeter.text.skin;
import static com.example.dustmeter.text.young;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
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

public class Userinfo extends AppCompatActivity {

    static TextView textView;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    static String name ="";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
      //  setContentView(R.layout.activity_main_content);
        new JSONTask5().execute(urlvalue+"/user");

        //textView = findViewById(R.id.textView13);
        textView = findViewById(R.id.textView13);

         pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        name = pref.getString("name", String.valueOf(name));



    }




    public static class JSONTask5 extends AsyncTask<String,String,String> {




        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection con = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0] + "?name=" + name);

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
                System.out.println("유저정보"+s);
              JSONObject jsonObject = new JSONObject(s);
              Log.d("s", jsonObject.toString());
                JSONArray dataArray = jsonObject.getJSONArray("data");

                //배열에 저장
              int[] data = new int[10];
              for (int i=0; i <=dataArray.length(); i++){
                  data[i] = dataArray.isNull(i) ? 0: dataArray.getInt(i);
              }


              ArrayList<String> Textdata = new ArrayList<String>();

                if (data[0] == 1) {
                    Textdata.add(blood);
                }
                if (data[1] == 1) {
                    Textdata.add(breath);
                }
                if (data[2] == 1) {
                    Textdata.add(asthma);
                }
                if (data[3] == 1) {
                    Textdata.add(young);
                }
                if (data[4] == 1) {
                    Textdata.add(old);
                }
                if (data[5] == 1) {
                    Textdata.add(skin);
                }
                if (data[6] == 1) {
                    Textdata.add(nose);
                }
                if (data[7] == 1) {
                    Textdata.add(eye);
                }


                StringBuilder stringBuilder = new StringBuilder();
                for(String i:Textdata){
                    stringBuilder.append(i).append("\n");
                }

                textView.setText(stringBuilder.toString());




            } catch (Throwable tx) {
                tx.printStackTrace();
//                put_indate.setText(tx.getMessage());
            }


        }
    }



}
