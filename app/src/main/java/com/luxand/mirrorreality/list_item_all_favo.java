package com.luxand.mirrorreality;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class list_item_all_favo extends AppCompatActivity
{

    boolean loved = false;
    ImageView img =(ImageView) findViewById(R.id.Fav);

    public void fav(View view)
    {
        if(!loved)
        {
            img.setImageResource(R.drawable.heart3);
            loved = true;
        }
        else
        {
            img.setImageResource(R.drawable.heart5);
            loved = false;
        }

    }


}
