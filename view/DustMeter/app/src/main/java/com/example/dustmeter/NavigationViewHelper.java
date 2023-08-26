package com.example.dustmeter;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

public class NavigationViewHelper {
    public static void enableNavigation(final Context context, NavigationView view){


        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu1:
                    Intent intent1=new Intent(context, mainContent.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent1);
                    return true;

                case R.id.menu2:
                    Intent intent2=new Intent(context, GraphPage.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent2);
                    return true;
                case R.id.menu3:
                    Intent intent3 = new Intent(context,Outside.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent3);
                    return true;

                case R.id.menu4:
                    Intent intent4 = new Intent(context,Join.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent4);
                    return true;

                case R.id.menu5:
                    Intent intent5 = new Intent(context,Login.class);
                    intent5.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent5);
                    return true;

                case R.id.menu6:
                    Intent intent6 = new Intent(context,Userinfo.class);
                    intent6.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent6);
                    return true;

                case R.id.menu7:
                    Intent intent7 = new Intent(context,Logout.class);
                    intent7.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent7);
                    return true;

        }
        return false;

        }
        });



    }
}
