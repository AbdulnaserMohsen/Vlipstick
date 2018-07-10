package com.luxand.mirrorreality;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class menu1 extends AppCompatActivity
{

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    LinearLayout lay;
    Animation anim,anim1;
    ImageView i1;

    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);



        i1=(ImageView)findViewById(R.id.p2);
        anim1= AnimationUtils.loadAnimation(this,R.anim.uu);
        i1.setAnimation(anim1);


        fa = this;

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);

        //setSupportActionBar(toolbar);
        lay=(LinearLayout)findViewById(R.id.layout2);
        lay.setVisibility(View.VISIBLE);
        anim= AnimationUtils.loadAnimation(this,R.anim.dd);
    }


    public void LogActivity(View view)
    {



        Toast.makeText(menu1.this,"login",Toast.LENGTH_SHORT).show();
       /* Intent i=new Intent(menu1.this,login.class);
        startActivity(i);
*/


        lay.startAnimation(anim);


        TimerTask task =new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(menu1.this,login.class);
                startActivity(i);
              //  overridePendingTransition(R.anim.u, R.anim.d);
                //lay.setVisibility(View.INVISIBLE);
                //finish();

            }
        };
        Timer tt=new Timer();
        tt.schedule(task,600);
    }

    public void RegActivity(View view)
    {


        Toast.makeText(menu1.this,"register",Toast.LENGTH_SHORT).show();
       /* Intent i=new Intent(menu1.this,Register.class);
        startActivity(i);*/


        lay.startAnimation(anim);


        TimerTask task =new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(menu1.this,Register.class);
                startActivity(i);
                //overridePendingTransition(R.anim.u, R.anim.d);
                //lay.setVisibility(View.INVISIBLE);

                //finish();

            }
        };
        Timer tt=new Timer();
        tt.schedule(task,600);


    }

    public void GueActivity(View view)
    {

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("id",  0);
        editor.apply();

        Toast.makeText(menu1.this,"guest",Toast.LENGTH_SHORT).show();


        lay.startAnimation(anim);


        TimerTask task =new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(menu1.this,product.class);
                startActivity(i);
                //overridePendingTransition(R.anim.u, R.anim.d);
             //   lay.setVisibility(View.INVISIBLE);

            }
        };
        Timer tt=new Timer();
        tt.schedule(task,600);


      /*  Intent i=new Intent(menu1.this,product.class);
        startActivity(i);
*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.d, R.anim.d);
    }


    public void animation()
    {



    }
}
