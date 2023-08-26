package com.example.dustmeter;

import static com.example.dustmeter.mainContent.urlvalue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Logout extends AppCompatActivity {

    SharedPreferences.Editor editor;

    SharedPreferences pref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        editor.remove("userid");
        editor.remove("name");
        editor.commit();
        Toast toast = Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);




    }


}
