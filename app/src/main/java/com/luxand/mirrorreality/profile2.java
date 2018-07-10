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

public class profile2 extends AppCompatActivity
{

    Animation anim1;
    Button but;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    int user_id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id  = prefs.getInt("id",0);//"No name defined" is the default value.
        but=(Button)findViewById(R.id.changPass);
        anim1= AnimationUtils.loadAnimation(this,R.anim.r);

    }

    public void change_click(View view)
    {

        post();

    }

    void Done()
    {

        //this.finishActivity(1);
        profile1.fa.finish();

        Intent in = new Intent(getApplicationContext(), profile1.class);
        startActivity(in);
        finish();
    }

    void post()
    {
        EditText e1 = (EditText) findViewById(R.id.CurrentPass);
        EditText e2 = (EditText) findViewById(R.id.newPass);
        EditText e3 = (EditText) findViewById(R.id.ConfrmPass);

        final String CurrentPass = e1.getText().toString();
        final String NewPass = e2.getText().toString();
        final String ConfrmPass = e3.getText().toString();

        CurrentPass.trim(); NewPass.trim(); ConfrmPass.trim();
        if(CurrentPass.isEmpty() || NewPass.isEmpty() || ConfrmPass.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"one or more required field is empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!NewPass.equals(ConfrmPass))
        {
            Toast.makeText(getApplicationContext(),"new password not matched",Toast.LENGTH_SHORT).show();
            return;
        }

        final String url = "http://naserahmed1995.000webhostapp.com/glasses/update_password.php";//volley



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

                                but.startAnimation(anim1);
                                Toast.makeText(getBaseContext(), "updated successfully",
                                        Toast.LENGTH_SHORT).show();
                                Done();
                            }
                            else if(jsonObject.getString("result").contains("false"))
                            {
                                Toast.makeText(getBaseContext(), "this user may be deleted",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else if(jsonObject.getString("result").contains("error"))
                            {
                                Toast.makeText(getBaseContext(), "can not updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else if(jsonObject.getString("result").contains("not"))
                            {
                                Toast.makeText(getBaseContext(), "not correct password",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(), " Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), "No internet or Server maybe sleep",
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
                        Toast toast = Toast.makeText(getApplicationContext(), "Connection error ",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", String.valueOf(user_id));
                params.put("password",CurrentPass);
                params.put("newpass",ConfrmPass);

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
