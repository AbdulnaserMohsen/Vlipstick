package com.luxand.mirrorreality;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity
{
    protected int t=3000;
    Button but;
    Animation anim;
    Animation anim1;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        but=(Button)findViewById(R.id.but1);
        anim= AnimationUtils.loadAnimation(this,R.anim.r);
        t1=(TextView)findViewById(R.id.te1);
        anim1= AnimationUtils.loadAnimation(this,R.anim.t5);
        t1.setAnimation(anim1);
    }



    public void reg_click()
    {



        but.startAnimation(anim);


        TimerTask task =new TimerTask() {
            @Override
            public void run() {
                Intent i=new Intent(Register.this,login.class);
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
    public void register(View view)
    {

        if(pressed) return;;

        final String url = "http://naserahmed1995.000webhostapp.com/glasses/register.php";//volley


        EditText e_name = (EditText) findViewById(R.id.name);
        EditText e_phone = (EditText) findViewById(R.id.phone);
        EditText e_email = (EditText) findViewById(R.id.email);
        EditText e_pass = (EditText) findViewById(R.id.password);
        EditText e_repass = (EditText) findViewById(R.id.RePassword);

        RequestQueue queue = Volley.newRequestQueue(this); //volley


        final String name = e_name.getText().toString();
        final String phone = e_phone.getText().toString();
        final String email = e_email.getText().toString();
        final String pass = e_pass.getText().toString();
        final String repass = e_repass.getText().toString();

        name.trim(); phone.trim(); email.trim(); pass.trim(); repass.trim();
        if(name.isEmpty() || phone.isEmpty() || email.isEmpty() || pass.isEmpty() || repass.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"one or more required field is empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if(repass.contentEquals(pass))
        {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);
                                if(response.toString().contains("true"))
                                {

                                    Toast toast =Toast.makeText(getApplicationContext(),"Regitered Successfully",Toast.LENGTH_SHORT);
                                    toast.show();
                                    reg_click();
                                    pressed = true;
                                }
                                else if(jsonObject.getString("result").contentEquals("false"))
                                {

                                    Toast toast =Toast.makeText(getApplicationContext(),"email is already exists",Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                                else if(jsonObject.getString("result").contentEquals("error"))
                                {

                                    Toast toast =Toast.makeText(getApplicationContext(),"Regitere faild",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();

                                Toast toast =Toast.makeText(getApplicationContext(),"No internet or Server maybe sleep ",Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }

                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast toast = Toast.makeText(getApplicationContext(),"connection error ",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

            )
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("name", name);
                    params.put("email", email);
                    params.put("phone", phone);
                    params.put("password", pass);


                    return params;
                }
            };
            queue.add(postRequest);

        }

        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Password and repassword must be equal",Toast.LENGTH_LONG);
            toast.show();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.d, R.anim.d);
    }

}
