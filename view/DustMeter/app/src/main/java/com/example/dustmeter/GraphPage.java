package com.example.dustmeter;

import static com.example.dustmeter.mainContent.urlvalue;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GraphPage extends AppCompatActivity {

    TextView textView;
    //선 그래프
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_page);

        textView = findViewById(R.id.textView);

        new JSONTask().execute(urlvalue+"/grp");




        ArrayList<BarEntry> entry_chart = new ArrayList<>(); // 데이터를 담을 Arraylist

        barChart = (BarChart) findViewById(R.id.chart);

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
            // 배열 두개 선언해서 날짜랑 데이터 따로따로 저장
            // 날짜는 일만 파싱하는 법 찾기

            try{
                JSONObject jsonObject = new JSONObject(s);

                JSONArray data = jsonObject.getJSONArray("data");
                String[] datelist = new String[data.length()];
                int[] valuelist = new int[data.length()];


                valuelist = new int[data.length()];



                ArrayList<BarEntry> entry_chart = new ArrayList<>();

                //파싱해서 배열에 저장
                for(int i = 0; i <data.length(); i++) {
                    JSONObject jsondata = data.getJSONObject(i);

                    valuelist[i] = jsondata.getInt("max_daily_value");

                }
                //그래프에 데이터 입력
                BarData barData = new BarData();
                for (int j = 0; j<data.length(); j++){
                    entry_chart.add(new BarEntry(j,valuelist[j]));
                }

                //데이터 출력 테스트
                for (int j = 0; j<data.length(); j++){
                    System.out.println(datelist[j]);
                    System.out.println(valuelist[j]);

                }

                BarDataSet barDataSet = new BarDataSet(entry_chart, "bardataset"); //어레이리스트 -> bardataset 변경
                barDataSet.setColor(Color.CYAN); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.

                barData.addDataSet(barDataSet); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

                barChart.setData(barData); // 차트에 위의 DataSet 을 넣는다.

                barChart.invalidate(); // 차트 업데이트
                barChart.setTouchEnabled(false);




            } catch (Throwable tx) {
                Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
            }


        };

    }
}
