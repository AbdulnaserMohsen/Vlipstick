package com.luxand.mirrorreality;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class login extends AppCompatActivity
{

    // CircularProgressButton circularProgressButton;
    EditText etUserName, etPassword;
    Button but;
    Animation anim,anim1;
    TextView t1,t2,t3;

    final String url = "http://naserahmed1995.000webhostapp.com/glasses/login.php";//volley

    public static final String MY_PREFS_NAME = "MyPrefsFile";


    String username ="", password =""; // for shared




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);




            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            but=(Button)findViewById(R.id.b);
            anim= AnimationUtils.loadAnimation(this,R.anim.r);

        t1= (TextView)findViewById(R.id.textview1);
        t2= (TextView)findViewById(R.id.textview2);
        t3= (TextView)findViewById(R.id.textview3);

        anim1= AnimationUtils.loadAnimation(this,R.anim.l);
        t1.setAnimation(anim1);
        t2.setAnimation(anim1);
        t3.setAnimation(anim1);

      /*  circularProgressButton = (CircularProgressButton ) findViewById(R.id.cpbLogin);
        etUserName = (EditText)findViewById(R.id.tEmail);
        etPassword= (EditText)findViewById(R.id.tPass);

        circularProgressButton.setIndeterminateProgressMode(true);

        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(circularProgressButton.getProgress()==0)
                {
                    circularProgressButton.setProgress(30);
                }
                else if (circularProgressButton.getProgress()==-1)
                {
                    circularProgressButton.setProgress(0);
                }
                else if (circularProgressButton.getProgress()==100)
                {
                    startActivity(new Intent(login.this,l.class));
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(etUserName.getText().toString().equals("aml") && etPassword.getText().toString().equals("123"))
                        {
                            circularProgressButton.setProgress(100);
                        }
                        else
                        {
                            circularProgressButton.setProgress(-1);
                        }
                    }
                },2000);
            }
        });*/


    }


    public void sign()
    {
        but.startAnimation(anim);


        TimerTask task =new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(login.this,product.class);
                startActivity(i);
                menu1.fa.finish();
                finshes();
            }
        };
        Timer tt=new Timer();
        tt.schedule(task,3000);



    }

    public void finshes(){
        this.finish();
    }


    boolean pressed = false;
    public void login(View view)
    {
        if(pressed) return;


        EditText e_email = (EditText) findViewById(R.id.logEmail);
        EditText e_pass = (EditText) findViewById(R.id.logPass);

        final String email = e_email.getText().toString();
        final String pass = e_pass.getText().toString();

        email.trim(); pass.trim();
        if(email.isEmpty()||pass.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"one or more required field is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.contains(";") || pass.contains(";"))
        {
            Toast.makeText(getApplicationContext(),"not good email or password",Toast.LENGTH_SHORT).show();
            return;
        }

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

                            if(jsonObject.getString("result").contains("true"))
                            {

                                Toast toast =Toast.makeText(getApplicationContext(),"Loged in Successfully",Toast.LENGTH_SHORT);
                                toast.show();

                                int id = jsonObject.getInt("id");
                                String user_name = jsonObject.getString("name");
                                String phone = jsonObject.getString("phone");

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("name", user_name);
                                editor.putString("username", email);
                                editor.putString("phone", phone);
                                editor.putString("password", pass);
                                editor.putInt("id",  id);
                                editor.apply();

                                sign();

                                pressed = true;
                                //Intent i=new Intent(login.this,Products.class);
                                //startActivity(i);
                                //finshes();

                            }
                            else if(jsonObject.getString("result").contains("false"))
                            {
                                Toast toast =Toast.makeText(getApplicationContext(),"Incorrect email or password",Toast.LENGTH_SHORT);
                                toast.show();

                            }
                            else
                            {

                                Toast toast =Toast.makeText(getApplicationContext(),"maybe data send wrong",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "No internet or Server maybe sleep ",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),"Connection error ",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", pass);


                return params;
            }
        };
        queue.add(postRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.d, R.anim.d);
    }


}
