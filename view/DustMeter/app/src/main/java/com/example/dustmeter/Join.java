package com.example.dustmeter;

import static com.example.dustmeter.mainContent.urlvalue;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Join extends AppCompatActivity {

    EditText inputId;
    EditText inputPwd;
    EditText inputName;
    TextView test;
    Button button;

    CheckBox bloodbox;
    CheckBox breathbox;
    CheckBox asthmabox;
    CheckBox youngbox;
    CheckBox oldbox;
    CheckBox skinbox;
    CheckBox nosebox;
    CheckBox eyebox;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputId = (EditText) findViewById(R.id.inputId);
        inputPwd = findViewById(R.id.inputPwd);
        inputName = findViewById(R.id.inputName);
        test = findViewById(R.id.test);
        button = findViewById(R.id.button);

        bloodbox = (CheckBox) findViewById(R.id.blood);
        breathbox = (CheckBox) findViewById(R.id.breath);
        asthmabox = (CheckBox) findViewById(R.id.asthma);
        youngbox = (CheckBox) findViewById(R.id.young);
        oldbox = (CheckBox) findViewById(R.id.old);
        skinbox = (CheckBox) findViewById(R.id.skin);
        nosebox = (CheckBox) findViewById(R.id.nose);
        eyebox = (CheckBox) findViewById(R.id.eye);




        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String userid = inputId.getText().toString();
                String pwd = inputPwd.getText().toString();
                String name = inputName.getText().toString();

                int blood;int breath; int asthma; int young;
                int old; int skin; int nose; int eye;

                if(bloodbox.isChecked()){
                    blood = 1;
                    //System.out.println("블러드 체크");
                }else{
                    blood = 0;
                }

                if(breathbox.isChecked()){
                    breath = 1;
                }else{
                    breath = 0;
                }

                if(asthmabox.isChecked()){
                    asthma = 1;
                }else{
                    asthma = 0;
                }

                if(youngbox.isChecked()){
                    young = 1;
                }else{
                    young =0;
                }

                if(oldbox.isChecked()){
                    old = 1;
                }else{
                    old = 0;
                }

                if(skinbox.isChecked()){
                    skin = 1;
                }else{
                    skin = 0;
                }

                if(nosebox.isChecked()){
                    nose = 1;
                }else{
                    nose = 0;
                }

                if(eyebox.isChecked()){
                    eye = 1;
                }else{
                    eye = 0;
                }







                Join.JSONPost jsonPost = new Join.JSONPost(userid,name,pwd,blood,breath,asthma,young,old,skin,
                        nose,eye); //순서 중요

//                Join.JSONPost jsonPost = new Join.JSONPost(userid,name,pwd); //순서 중요
                jsonPost.execute(urlvalue+"/signup");

                //System.out.println(userid);

            }


        });


    }

    public class JSONPost extends AsyncTask<String,String,String> {


        private String userid;
        private String name;
        private String pwd;

        private int blood;
        private int breath;
        private  int asthma;
        private int young;
        private int old;
        private int skin;
        private int nose;
        private int eye;


        public JSONPost(String userid, String name, String pwd,
                        int blood,int breath, int asthma, int young,
                        int old, int skin, int nose, int eye){

//                public JSONPost(String userid, String name, String pwd
//                   ){
            this.userid = userid;
            this.name = name;
            this.pwd = pwd;

            this.blood = blood;
            this.breath = breath;
            this.asthma = asthma;
            this.young = young;
            this.old = old;
            this.skin = skin;
            this.nose = nose;
            this.eye = eye;
        }



        @Override
        protected String doInBackground(String... urls) {
            try { //json으로 만들기
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("userid", userid);
                jsonObject.accumulate("pwd",pwd);
                jsonObject.accumulate("name", name);

                jsonObject.accumulate("blood",blood);
                jsonObject.accumulate("breath",breath);
                jsonObject.accumulate("asthma",asthma);
                jsonObject.accumulate("young",young);
                jsonObject.accumulate("old",old);
                jsonObject.accumulate("skin",skin);
                jsonObject.accumulate("nose",nose);
                jsonObject.accumulate("eye",eye);


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{

                    //URL url = new URL("http://192.168.25.16:3000/users");
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
            //tvData.setText("=============================!!!!!!!!!!"+result);
            Toast toast = Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }

}
