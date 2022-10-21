package com.example.myapplication;




import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Array;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> districtNameList;
    private ArrayList<String> dataDateList;
    private ArrayList<String> issueValList;
    private TextView txtResult;
    private JSONArray jsonArray;
    static String result;


    EditText editText; // url 주소를 입력받는 텍스트 박스
    TextView textView; // 입력받은 url 에서 가져온 정보를 표시하는 텍스트 박스

    String urlStr; // url 주소 받을 변수

    Handler handler = new Handler();// Thread에서 전달받은 값을 메인으로 가져오기 위한 Handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {// saveInstanceState -> 상태저장
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  editText = findViewById(R.id.editText); // 앱 내에서 위치 확인
        //textView = (TextView) findViewById(R.id.txtResult); // 앱 내에서 위치 확인
        //Button button = (Button) findViewById(R.id.button);


        districtNameList = new ArrayList<>();
        dataDateList = new ArrayList<>();
        issueValList = new ArrayList<>();

        RequestThread thread = new RequestThread(); // Thread 생성
        thread.start(); // Thread 시작

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                RequestThread thread = new RequestThread(); // Thread 생성
//                thread.start(); // Thread 시작
//            }
//        });


    }

    class RequestThread extends Thread { // DB를 불러올 때도 앱이 동작할 수 있게 하기 위해 Thread 생성
        @Override
        public void run() { // 이 쓰레드에서 실행 될 메인 코드
            try {

                StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/UlfptcaAlarmInqireSvc/getUlfptcaAlarmInfo"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=aeyWMR9bHz6tch8FAPiOiuPoI%2BYvGImSqJSjQx7kB7rguqnMMP7WhAfCxH5xMk1kujpECK530srXKfaAofH26A%3D%3D"); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
                urlBuilder.append("&" + URLEncoder.encode("year", "UTF-8") + "=" + URLEncoder.encode("2020", "UTF-8")); /*측정 연도*/
                urlBuilder.append("&" + URLEncoder.encode("itemCode", "UTF-8") + "=" + URLEncoder.encode("PM10", "UTF-8")); /*미세먼지 항목 구분(PM10, PM25), PM10/PM25 모두 조회할 경우 파라미터 생략*/
                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                BufferedReader rd;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                sb.toString();
                System.out.println(sb);
              result = sb.toString();
              //System.out.println("받아오기:"+result);
              //System.out.println(result.length());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                JSONObject jsobj = new JSONObject(result);
                //System.out.println("파싱파트에서 받아온 값:" +jsobj);
            JSONObject response = jsobj.getJSONObject("response"); //response 객체 깜 1
            System.out.println(response);

            JSONObject body = response.getJSONObject("body"); //body객체 깜 2
            System.out.println(body);
            //JSONObject totalCount = body.getJSONObject("totalCount"); //totalCount 객체 깜 3
            //System.out.println("totalcounty="+totalCount);
            JSONArray items = body.getJSONArray("items"); //items 배열 깜 1


            for(int i = 0; i <items.length(); i++){ //items 배열 루프 돌림
                JSONObject jsbJsobj = items.getJSONObject(i);
                String districtName = jsbJsobj.getString("districtName");
                String dataDate = jsbJsobj.getString("dataDate");
                String issueVal = jsbJsobj.getString("issueVal");
                districtNameList.add(districtName);
                dataDateList.add(dataDate);
                issueValList.add(issueVal);

                          }



            }catch (JSONException e){

                throw new RuntimeException(e);
            }

            //파싱 값 확인
//            int totalElements = districtNameList.size();// arrayList의 요소의 갯수를 구한다.
//            for (int index = 0; index < totalElements; index++) {
//                System.out.println(districtNameList.get(index));
//            }
//
//            int totalElements2 = districtNameList.size();// arrayList의 요소의 갯수를 구한다.
//            for (int index = 0; index < totalElements2; index++) {
//                System.out.println(dataDateList.get(index));
//            }
//
//
//            int totalElements3 = districtNameList.size();// arrayList의 요소의 갯수를 구한다.
//            for (int index = 0; index < totalElements3; index++) {
//                System.out.println(issueValList.get(index));
//            }

            // 한줄로 보여주기
            for (int i=0;i<districtNameList.toArray().length;i++){
                System.out.println("지역명:"+districtNameList.get(i)+"날짜"+dataDateList.get(i)+"미세먼지 지수"+issueValList.get(i));
            }




        }


    }





}



//                try {
//                   //jsonArray = new JSONArray(sb);
//                   JSONObject jsonobj = new JSONObject(sb);
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String districtName = jsonObject.optString("districtName");
//                        String dataDate = jsonObject.optString("dataDate");
//                        String issueVal = jsonObject.optString("issueVal");
//                        districtNameList.add(districtName);
//                        dataDateList.add(dataDate);
//                        issueValList.add(issueVal);
//
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                }

//
//                rd.close();
//                conn.disconnect();
//                //System.out.println(sb.toString());
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            txtResult.setText(districtNameList.toString() + "\n" + dataDateList.toString()+"\n" +issueValList.toString());
//
//        }
//
//










//    private TextView mTextView;
//
//
//    String mInfo = String.valueOf((CharSequence) JsonValue);
//    //  "[{'name':'홍길동','age':12,'address':'서울'}," + "{'name':'청길동','age':34,'address':'대전'}," + "{'name':'백길동','age':56,'address':'대구'}]";
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mTextView = (TextView) findViewById(R.id.textView);
//        // api 값 불러오기
//        NetworkThread thread = new NetworkThread();
//        thread.start();
//
//        mTextView.setText(value);
        //System.out.println(value);


//        TextView textView = (TextView) findViewById(R.id.textView);
//
//        mTextView.setText((CharSequence) JsonValue);
//
//
//        mTextView.setText(mInfo);
//
//
//            mTextView = findViewById(R.id.textView);
//            findViewById(R.id.btnData).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    JSONParse(mInfo);
//                }
//            });
//            mTextView.setText(mInfo);
//        }
//
//        void JSONParse(String jsonStr){
//            StringBuilder stringBuilder = new StringBuilder();
//
//            try {
//                JSONArray jsonArray = new JSONArray(jsonStr);
//                for(int i = 0; i < jsonArray.length(); i++){
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    String name = jsonObject.getString("name");
//                    int age = jsonObject.getInt("age");
//                    String address = jsonObject.getString("address");
//
//                    stringBuilder.append("이름 : ").append(name).append(" 나이 : ").append(age).append(" 주소 : ").append(address).append("\n");
//                }
//                mTextView.setText(stringBuilder);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//       }
//
//
//    }
//}
//
//


