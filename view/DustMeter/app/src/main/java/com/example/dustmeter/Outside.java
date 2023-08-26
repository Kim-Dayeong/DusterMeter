package com.example.dustmeter;


import static com.example.dustmeter.mainContent.urlvalue;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.navigation.NavigationView;

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
import java.util.List;

public class Outside extends AppCompatActivity {
    private static final String TAG = "Outside";

    private Context mContext = Outside.this;
    private NavigationView nav;


    //실외
    TextView put_outdate;
    TextView put_outpm;
    TextView put_outpm25;
    TextView put_number;

    //GPS
    TextView lon;
    TextView add;
    Geocoder geocoder;
    TextView tvData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside);

        tvData = (TextView)findViewById(R.id.tvData);

        //gps
        // 위치 관리자 객체 참조하기
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<Address> address = null;
        String city = null;

        geocoder = new Geocoder(this); //역지오코딩


       // Button button = (Button)findViewById(R.id.button2);
        
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("버튼 클릭");
//
//            }
//        });






        new JSONTask().execute(urlvalue+"/out"); //테스트

        put_outdate = findViewById(R.id.put_outdate);
        put_outpm = findViewById(R.id.put_outpm);
        put_outpm25 = findViewById(R.id.put_outpm25);
        put_number = findViewById(R.id.put_number);
        add = findViewById(R.id.add);


        //gps

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Outside.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            // 가장최근 위치정보 가져오기
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();


//                lon.setText("위치정보 : " + provider + "\n" +
//                        "위도 : " + longitude + "\n" +
//                        "경도 : " + latitude + "\n" +
//                        "고도  : " + altitude);
                System.out.println("위치 정보: " + provider+ "\n" +
                        "위도 : " + longitude + "\n" +
                        "경도 : " + latitude + "\n" +
                        "고도  : " + altitude);
                try {
                    address = geocoder.getFromLocation(latitude, longitude, 10);

                } catch (IOException e) {
                    e.printStackTrace();

                }
                if (address != null) {
                    if (address.size() == 0) {
                        //add.setText("주소 에러");
                        System.out.println("주소 에러");
                    } else {
                        city = address.get(0).getAdminArea();
                        //add.setText(city);
                        String txt = "충청남도";
                        add.setText(txt);
                        System.out.println(city);
                        System.out.println("도시명============="+city);


                    }

                }

                //JSONPost jsonPost = new JSONPost(longitude,latitude); //jsonpost 로 위도 경도 보내기
                //GpsData.JSONPost jsonPost = new GpsData.JSONPost(city);
                JSONPost jsonPost = new JSONPost(city);
                jsonPost.execute("http://10.0.6.194:7700/post");

                //new JSONPost(longitude,latitude).execute("http://192.168.219.100:7700/post");

            }
            // (gps)위치정보를 원하는 시간, 거리마다 갱신해준다.
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
        }//gps 코드 끝

    }

    public class JSONTask extends AsyncTask<String, String, String> {

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
                Log.d("time ", jsonObject.getString("time"));
                String time = jsonObject.getString("time");
                System.out.println(time);
                String pm10 = jsonObject.getString("pm10");
                String pm25 = jsonObject.getString("pm25");
                String num = jsonObject.getString("number");

                put_outdate.setText(time);
                put_outpm.setText(pm10);
                put_outpm25.setText(pm25);
                put_number.setText(num);


            } catch (Throwable tx) {
                Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
            }
        }

        ;


    }


    //gps
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다.
            String provider = location.getProvider();  // 위치정보
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도
//            lon.setText("위치정보 : " + provider + "\n" + "위도 : " + longitude + "\n" + "경도 : " + latitude + "\n" + "고도 : " + altitude);
            System.out.println("위치 정보: " + provider+ "\n" +
                    "위도 : " + longitude + "\n" +
                    "경도 : " + latitude + "\n" +
                    "고도  : " + altitude);

        } public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };

    public class JSONPost extends AsyncTask<String,String,String> {


        private String city;


        public JSONPost(String city){
            this.city = city;
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("longitude", longi);
//                jsonObject.accumulate("latitude", lati);
                jsonObject.accumulate("city", city);

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
            super.onPostExecute(result);
            //tvData.setText("=============================!!!!!!!!!!"+result);

        }
    }


}