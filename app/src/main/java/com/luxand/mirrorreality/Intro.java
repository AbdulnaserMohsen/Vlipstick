package com.luxand.mirrorreality;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Dialog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*must put them */
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifImageView;

public class Intro extends AppCompatActivity
{

    protected int t=10000;
    protected boolean a=true;
    TextView t1;
    ImageView i1;
    Animation anim,anim1,anim2,anim3;

    GifImageView i2;
    /*ÇäãÔä á ÏæÑÇä ãÔ ÚÇÑÝÉ åíÔÊÛá æáÇ ÚáÔÇä ãÇÌÑÈÊåæÔ Úáí layout */

    RelativeLayout rl;

    Dialog myDialog;



    final String url = "http://naserahmed1995.000webhostapp.com/glasses/login.php";//volley

    public static final String MY_PREFS_NAME = "MyPrefsFile";


    String username ="", password =""; // for shared

    DatabaseHandler db;

    int user_id =0;


    //    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        db = new DatabaseHandler(getApplicationContext());

        login();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        t1=(TextView)findViewById(R.id.tex);
        anim= AnimationUtils.loadAnimation(this,R.anim.t);
        t1.setAnimation(anim);

        i1=(ImageView)findViewById(R.id.img1);
        anim1= AnimationUtils.loadAnimation(this,R.anim.lr);
        i1.setAnimation(anim1);


        i2=(GifImageView)findViewById(R.id.img2);
        anim2= AnimationUtils.loadAnimation(this,R.anim.t);
        i2.setAnimation(anim2);



        rl=(RelativeLayout)findViewById(R.id.layout1);
        anim3= AnimationUtils.loadAnimation(this,R.anim.r);


        // myDialog = new Dialog(this);

        /*TimerTask task1 =new TimerTask() {
            @Override
            public void run() {
                rl.startAnimation(anim3);

            }
        };
        Timer tt1=new Timer();
        tt1.schedule(task1,6000);*/







        TimerTask task =new TimerTask() {
            @Override
            public void run() {

                if(user_id != 0)
                {
                    Intent i=new Intent(getApplicationContext(),product.class);
                    startActivity(i);
                }
                else
                {
                    Intent i=new Intent(Intro.this,menu1.class);
                    startActivity(i);
                }


                overridePendingTransition(R.anim.u, R.anim.d);

                finish();


                //  finshes();
            }
        };
        Timer tt=new Timer();
        tt.schedule(task,6000);



    }
/*    public void finshes(){
        this.finish();
    }*/



    void login()
    {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        username = prefs.getString("username", "");//"No name defined" is the default value.
        password = prefs.getString("password", ""); //0 is the default value.
        user_id  = prefs.getInt("id",0);//"No name defined" is the default value.

        RequestQueue queue = Volley.newRequestQueue(this); //volley

        final String user = username;
        final String pass = password;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").contains("true"))
                            {
                                //Toast.makeText(login.this, " successfully login ",
                                // Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(getApplicationContext(),product.class);
                                //startActivity(intent);
                                //finshes();
                                user_id = jsonObject.getInt("id");
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putInt("id",  user_id);
                                editor.apply();
                                //FavoCart();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), " Failed to login",
                                        Toast.LENGTH_SHORT).show();
                                user_id = 0;
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putInt("id",  user_id);
                                editor.apply();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "bad response",
                                    Toast.LENGTH_SHORT).show();
                            user_id = 0;
                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putInt("id",  user_id);
                            editor.apply();
                        }
                    }
                }
                ,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "Connection error ", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", user);
                params.put("password", pass);


                return params;
            }
        };
        queue.add(postRequest);
    }




    /*void getdatafromInternet()
    {

        final String url = "http://naserahmed1995.000webhostapp.com/glasses/product.php";//volley


        RequestQueue queue = Volley.newRequestQueue(this); //volley

        final int[] numberOfnewItems = {0};

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getJSONArray("result").length() > 0)
                            {
                                JSONArray arr = jsonObject.getJSONArray("result");
                                for(int i=0; i<arr.length(); i++)
                                {
                                    JSONObject film = arr.getJSONObject(i);

                                    String ff;
                                    Glass g = new Glass();
                                    g.prod_id   = film.getInt("id");
                                    g.name = film.getString("name");
                                    g.company = film.getString("company");
                                    g.price = film.getInt("price");
                                    g.image = film.getString("image");
                                    g.color = film.getString("color");

                                    String ImgUrl = g.image;
                                    String[] parts = g.image.split("/");
                                    int index = parts.length-1;
                                    final String imgename = parts[index];
                                    g.image = imgename;

                                    //Picasso.with(product.this).load(ImgUrl).into(picassoImageTarget(getApplicationContext(), "imageDir",imgename));
                                    //Picasso.with(getApplicationContext()).load(ImgUrl).into(getTarget(imgename));

                                    //glassList.add(g);
                                    glassList = new ArrayList<Glass>(db.getAllGlasses());
                                    if(glassList.size() == 0)
                                    {
                                        db.AddGlass(g);
                                        ++numberOfnewItems[0];
                                        Toast.makeText(product.this, "successfully added "
                                                        + numberOfnewItems[0] +" item",
                                                Toast.LENGTH_SHORT).show();
                                        Picasso.with(product.this).load(ImgUrl).into(picassoImageTarget(getApplicationContext(), "imageDir","maaaa2.png"));
                                    }
                                    else
                                    {
                                        boolean exists = false;
                                        for(Glass glass : glassList)
                                        {
                                            if(glass.prod_id == g.prod_id)
                                            {
                                                exists = true;
                                                break;
                                            }
                                        }
                                        if(!exists)
                                        {
                                            db.AddGlass(g);
                                            ++numberOfnewItems[0];
                                            Toast.makeText(product.this, "successfully added "
                                                            + numberOfnewItems[0] +" item",
                                                    Toast.LENGTH_SHORT).show();
                                            Picasso.with(product.this).load(ImgUrl).into(picassoImageTarget(getApplicationContext(), "imageDir","maaaa2.png"));
                                        }
                                    }


                                }

                                glassList = new ArrayList<Glass>(db.getAllGlasses());
                            }
                            else
                            {
                                Toast.makeText(product.this, " no items",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(product.this, "No internet or Server maybe sleep",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                ,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "Connection error ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        queue.add(postRequest);
    }*/


}
