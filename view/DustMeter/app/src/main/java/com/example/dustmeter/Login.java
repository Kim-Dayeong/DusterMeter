package com.example.dustmeter;

import static com.example.dustmeter.mainContent.urlvalue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Login extends AppCompatActivity {

    EditText inputuserid;
    EditText password;

    Button button;

     SharedPreferences pref;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputuserid = (EditText) findViewById(R.id.userid);
       // password = (EditText) findViewById(R.id.password);
        password = (EditText) findViewById(R.id.inputpassword);
        button = findViewById(R.id.loginbutton);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //로그인 값 받아서 json 파싱 후 서버로 전송
                String userid = inputuserid.getText().toString();
                String pwd = password.getText().toString();

                Login.JSONPost jsonPost = new Login.JSONPost(userid,pwd);
                jsonPost.execute(urlvalue+"/login");
                //jsonPost.execute("http://192.168.219.113:7700/login");

            }
        });
    }

        public class JSONPost extends AsyncTask<String,String,String> {


        private String userid;

        private String pwd;

        public JSONPost(String userid,  String pwd){
            this.userid = userid;

            this.pwd = pwd;
        }
        @Override
        protected String doInBackground(String... urls) {
            try { //json으로 만들기
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("userid", userid);
                jsonObject.accumulate("pwd",pwd);



                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{


                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");

                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            try{
                JSONObject jsonObject = new JSONObject(result);

                String userid = jsonObject.getString("userid");
                JSONArray nameArr = jsonObject.getJSONArray("name");
                String name = nameArr.getString(0);
                editor.putString("userid",userid);
                editor.putString("name",name);
                editor.apply();
//                String test = pref.getString("userid",userid);
//                String nametest= pref.getString("name",name);
//                System.out.println("test값!!!!!!!!!!!!"+test+"name:!!!!"+nametest);

            } catch (Throwable tx){
                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
            }




           if(result.length() != 0){
               Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
               Toast toast = Toast.makeText(getApplicationContext(), "로그인성공", Toast.LENGTH_SHORT);
               toast.show();
           }
           else{
               Toast toast = Toast.makeText(getApplicationContext(), "로그인실패", Toast.LENGTH_SHORT);
               toast.show();
           }

        }
    }

}
