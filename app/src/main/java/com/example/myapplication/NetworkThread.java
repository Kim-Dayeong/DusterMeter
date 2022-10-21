package com.example.myapplication;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.Handler;

//public class NetworkThread extends Thread{
//
////
//  static String value;
////    JSONParser parser = new JSONParser();
//  //  JSONObject jsonObject = (JSONObject) parser.parse(value);
//
//    static JSONObject JsonValue;
//    @Override
//    public void run() {
//        // String to JSON String에서 JSON 으로 형변환
//        //String test = "[{'name':'홍길동','age':12,'address':'서울'}," + "{'name':'청길동','age':34,'address':'대전'}," + "{'name':'백길동','age':56,'address':'대구'}]";
//
//
//
//
//        try{
//            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/UlfptcaAlarmInqireSvc/getUlfptcaAlarmInfo"); /*URL*/
//            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=aeyWMR9bHz6tch8FAPiOiuPoI%2BYvGImSqJSjQx7kB7rguqnMMP7WhAfCxH5xMk1kujpECK530srXKfaAofH26A%3D%3D"); /*Service Key*/
//            urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
//            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
//            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
//            urlBuilder.append("&" + URLEncoder.encode("year","UTF-8") + "=" + URLEncoder.encode("2020", "UTF-8")); /*측정 연도*/
//            urlBuilder.append("&" + URLEncoder.encode("itemCode","UTF-8") + "=" + URLEncoder.encode("PM10", "UTF-8")); /*미세먼지 항목 구분(PM10, PM25), PM10/PM25 모두 조회할 경우 파라미터 생략*/
//            URL url = new URL(urlBuilder.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Content-type", "application/json");
//            System.out.println("Response code: " + conn.getResponseCode());
//            BufferedReader rd;
//            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//            }
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            rd.close();
//            conn.disconnect();
//            //System.out.println(sb.toString());
//           value = sb.toString();
//            System.out.println(value);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        try {
//            JSONObject jsonObject = new JSONObject(value);
//           // System.out.println(jsonObject);
//            JsonValue = jsonObject;
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        }
//
//
//
//
//
//    }
//
