package com.luxand.mirrorreality;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class profile1 extends AppCompatActivity
{


    EditText e1,e2,e3;
    TextView t1,t2,t3,t4,t5,t6;
    Animation anim,anim1;
    Button but;


    //boolean open =true;
    boolean open =false;


    public static final String MY_PREFS_NAME = "MyPrefsFile";
    int user_id=0;
    String name,phone,email;

    String temp4,temp5,temp6;

    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile1);

        fa = this;

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id  = prefs.getInt("id",0);//"No name defined" is the default value.
        name  = prefs.getString("name","");//"No name defined" is the default value.
        phone  = prefs.getString("phone","");//"No name defined" is the default value.
        email  = prefs.getString("username","");//"No name defined" is the default value.


        e1=(EditText)findViewById(R.id.Ename);
        e2=(EditText)findViewById(R.id.EEmail);
        e3=(EditText)findViewById(R.id.EPhone);
        t1= (TextView)findViewById(R.id.textview1);
        t2= (TextView)findViewById(R.id.textview2);
        t3= (TextView)findViewById(R.id.textview3);
        t4= (TextView)findViewById(R.id.Tname);
        t5= (TextView)findViewById(R.id.TEmail);
        t6= (TextView)findViewById(R.id.TPhone);
        but=(Button)findViewById(R.id.but1);
        anim= AnimationUtils.loadAnimation(this,R.anim.l);
        anim1= AnimationUtils.loadAnimation(this,R.anim.r);
        t1.setAnimation(anim);
        t2.setAnimation(anim);
        t3.setAnimation(anim);

        temp4 = t4.getText().toString();
        t4.setText(temp4 +" "+ name);

        temp5 = t5.getText().toString();
        t5.setText(temp5 +" "+ email);

        temp6 = t6.getText().toString();
        t6.setText(temp6 +" "+ phone);


    }


    void  change()
    {
        getagain();

        open=false;

        e1.setVisibility(View.INVISIBLE);
        e2.setVisibility(View.INVISIBLE);
        e3.setVisibility(View.INVISIBLE);

        t4.setVisibility(View.VISIBLE);
        t5.setVisibility(View.VISIBLE);
        t6.setVisibility(View.VISIBLE);

    }

    public void update(View view)
    {
        if(open)
        {
            post();

        }
        else
        {
            open=true;

            t4.setVisibility(View.INVISIBLE);
            t5.setVisibility(View.INVISIBLE);
            t6.setVisibility(View.INVISIBLE);

            e1.setVisibility(View.VISIBLE);
            e2.setVisibility(View.VISIBLE);
            e3.setVisibility(View.VISIBLE);

            /*e1.setEnabled(true);
            e2.setEnabled(true);
            e3.setEnabled(true);*/

        }


    }

    void post()
    {

        final String name = e1.getText().toString();
        final String email = e2.getText().toString();
        final String phone = e3.getText().toString();

        name.trim(); email.trim(); phone.trim();
        if(name.isEmpty() || email.isEmpty() || phone.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"one or more required field is empty",Toast.LENGTH_SHORT).show();
            return;
        }


        final String url = "http://naserahmed1995.000webhostapp.com/glasses/update_user1.php";//volley

        //final String url = "http://195.246.49.58/section/films.php";


        RequestQueue queue = Volley.newRequestQueue(this); //volley


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
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("name", name);
                                editor.putString("username", email);
                                editor.putString("phone", phone);
                                but.startAnimation(anim1);
                                editor.apply();

                                change();

                                Toast.makeText(getApplicationContext(), "updated successfully",
                                        Toast.LENGTH_LONG).show();
                                e1.setText("");
                                e2.setText("");
                                e3.setText("");
                            }
                            else if(jsonObject.getString("result").contains("false"))
                            {
                                Toast.makeText(getApplicationContext(), "this user may be deleted",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else if(jsonObject.getString("result").contains("error"))
                            {
                                Toast.makeText(getApplicationContext(), "can not updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), " Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "No internet or server maybe sleep ",
                                    Toast.LENGTH_LONG).show();
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

                params.put("id", String.valueOf(user_id));
                params.put("name",name);
                params.put("email",email);
                params.put("phone",phone);

                return params;
            }
        };
        queue.add(postRequest);
    }




    void getagain()
    {

        RequestQueue queue = Volley.newRequestQueue(this); //volley

        final String url = "http://naserahmed1995.000webhostapp.com/glasses/post_user1.php";//volley

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
                                    JSONObject userInfo = arr.getJSONObject(i);

                                    t4.setText(temp4 +" "+ userInfo.getString("name"));
                                    t5.setText(temp5 +" "+ userInfo.getString("email"));
                                    t6.setText(temp6 +" "+ userInfo.getString("phone"));

                                }


                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), " No data found",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "No internet connection or server maybe sleep ",
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

                params.put("id",Integer.toString(user_id) );

                return params;
            }
        };
        queue.add(postRequest);
    }




    public void change(View view)
    {
        Intent in = new Intent(getApplicationContext(), profile2.class);
        startActivity(in);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.d, R.anim.d);
    }





}
