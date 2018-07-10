package com.luxand.mirrorreality;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity
{

    ImageView img1,img2,img3,img4,img5 ;
    boolean open =true;
    Animation animOpen,animClose,animRotate;

    List<Glass> glassList;
    List<Glass> favoList;
    List<Glass> cartList;

    int user_id =0;
    Favo_Cart favo_cart_item;

    DatabaseHandler db;

    int[] IMAGES = {R.drawable.img1,R.drawable.img2,
            R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,
            R.drawable.img7,R.drawable.img8,R.drawable.img9,R.drawable.img10,
            R.drawable.img11,R.drawable.img12,R.drawable.img13,R.drawable.img14,
            R.drawable.img15};


    public static final String MY_PREFS_NAME = "MyPrefsFile";

    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id  = prefs.getInt("id",0);//"No name defined" is the default value.

        fa = this;


        favo_cart_item = new Favo_Cart();
        favo_cart_item.user_id = user_id;

        db = new DatabaseHandler(this);

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        //db.Drop();







        ListView listView = (ListView) findViewById(R.id.list);

        Favorites.CustomAdapter customAdapter = new Favorites.CustomAdapter();

        listView.setAdapter(customAdapter);


        img1=(ImageView) findViewById(R.id.Menu);
        img2=(ImageView) findViewById(R.id.Home);
        img3=(ImageView) findViewById(R.id.Card);
        img4=(ImageView) findViewById(R.id.Favo);
        img5=(ImageView) findViewById(R.id.Logout);
        img1.bringToFront();

        animOpen= AnimationUtils.loadAnimation(this,R.anim.trans1);
        animClose= AnimationUtils.loadAnimation(this,R.anim.trans2);
        animRotate= AnimationUtils.loadAnimation(this,R.anim.r);




    }

    void renderAgain()
    {

        setContentView(R.layout.activity_favorites);

        favo_cart_item = new Favo_Cart();
        favo_cart_item.user_id = user_id;

        db = new DatabaseHandler(this);


        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        ListView listView = (ListView) findViewById(R.id.list);

        Favorites.CustomAdapter customAdapter = new Favorites.CustomAdapter();

        listView.setAdapter(customAdapter);


        img1=(ImageView) findViewById(R.id.Menu);
        img2=(ImageView) findViewById(R.id.Home);
        img3=(ImageView) findViewById(R.id.Card);
        img4=(ImageView) findViewById(R.id.Favo);
        img5=(ImageView) findViewById(R.id.Logout);
        img1.bringToFront();

        animOpen= AnimationUtils.loadAnimation(this,R.anim.trans1);
        animClose= AnimationUtils.loadAnimation(this,R.anim.trans2);
        animRotate= AnimationUtils.loadAnimation(this,R.anim.r);
    }

    public void menuClick(View view)
    {
        if(open)
        {
            open=false;
            img1.startAnimation(animRotate);
            img2.startAnimation(animOpen);
            img3.startAnimation(animOpen);
            img4.startAnimation(animOpen);
            img5.startAnimation(animOpen);


            img2.setVisibility(View.VISIBLE);
            img3.setVisibility(View.VISIBLE);
            img4.setVisibility(View.VISIBLE);
            img5.setVisibility(View.VISIBLE);

        }
        else
        {
            open =true;
            img1.startAnimation(animRotate);
            img2.startAnimation(animClose);
            img3.startAnimation(animClose);
            img4.startAnimation(animClose);
            img5.startAnimation(animClose);

            img2.setVisibility(View.INVISIBLE);
            img3.setVisibility(View.INVISIBLE);
            img4.setVisibility(View.INVISIBLE);
            img5.setVisibility(View.INVISIBLE);
        }
    }


    public void homeClick (View view )
    {

        if(user_id == 0)
        {
            Toast.makeText(getApplicationContext(),"you are in guest mode not user"
                    ,Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i=new Intent(getApplicationContext(),profile1.class);
        startActivity(i);
        menuClick(view);
        finish();

    }

    public void cardClick (View view )
    {
        if(user_id == 0)
        {
            Toast.makeText(getApplicationContext(),"you are in guest mode not user"
                    ,Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i=new Intent(getApplicationContext(),Cart.class);
        startActivity(i);
        menuClick(view);
        finish();
    }

    public void outClick(View view )
    {
        if(user_id == 0)
        {
            Toast.makeText(getApplicationContext(),"you are in guest mode not user"
                    ,Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putInt("id",  0);
        editor.apply();

        Intent i=new Intent(getApplicationContext(),menu1.class);
        startActivity(i);
        menuClick(view);
        finish();

    }


    public void favClick (View view )
    {
        return;
    }


    public void AddToCart(int prod_id)
    {
        if(user_id == 0)
        {
            Toast.makeText(getApplicationContext(),"you are in guest mode not user"
                    ,Toast.LENGTH_SHORT).show();
            return;
        }

        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);

        favo_cart_item.prod_in_favo_cart_id = prod_id;
        favo_cart_item.user_id = user_id;
        favo_cart_item.quantity++;
        db.AddToCart(getApplicationContext(),favo_cart_item);

        Toast.makeText(getApplicationContext(),"added to cart",Toast.LENGTH_SHORT).show();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

    }

    public void fav(ImageView imgggg , int prod_id)
    {

        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);

        imgggg.setImageResource(R.drawable.heart5);
        db.DeleteFavo(getApplicationContext(),favo_cart_item);

        Toast.makeText(getApplicationContext(),"deleted from favorites",Toast.LENGTH_SHORT).show();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        renderAgain();

    }




    class  CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            //return IMAGES.length;
            return favoList.size();
        }

        @Override
        public Object getItem(int i)
        {
            return null;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            view = getLayoutInflater().inflate(R.layout.list_item_all_favo,null);

            de.hdodenhof.circleimageview.CircleImageView imageView = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.l2imageView);


            final int prod_id = favoList.get(i).prod_id;
            final ImageView imgheart=(ImageView) view.findViewById(R.id.Fav);
            imgheart.setImageResource(R.drawable.heart3);
            imgheart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fav(imgheart,prod_id);
                }
            });


            final android.support.v7.widget.AppCompatButton imgCart =(android.support.v7.widget.AppCompatButton) view.findViewById(R.id.ProdCart);
            favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
            imgCart.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AddToCart(prod_id);
                }
            });



            Button tryit = (Button) view.findViewById(R.id.tryIT);
            tryit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putInt("prod_id",  prod_id);
                    editor.apply();

                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                }
            });





            TextView name  = (TextView)  view.findViewById(R.id.l2name);
            TextView company = (TextView) view.findViewById(R.id.l2company);
            TextView price   = (TextView) view.findViewById(R.id.l2price);
            TextView color = (TextView) view.findViewById(R.id.l2color);


            //imageView.setImageResource(IMAGES[i]);
            //name.setText(NAMES[i]);
            //type.setText(DESC[i]);
            //price.setText(Float.toString(PRICES[i]));


            //Picasso.with(getApplicationContext()).load(glassList.get(i).image).into(imageView);



            /*final String imagename = glassList.get(i).image;
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File myImageFile = new File(directory, "glass2.png");
            //File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            //File folder = new File(sd, "/Picasso/");
            //File myImageFile = new File(folder, imagename);
            Picasso.with(Favorites.this).load(myImageFile).into(imageView);*/

            int index=0;
            for(int j=0;j<glassList.size(); j++)
            {
                if(prod_id == glassList.get(j).prod_id)
                {
                    index = j;
                    break;
                }
            }

            /*String[] parts =  glassList.get(i).image.split("\\.");
            String imgnam = "R.drawable."+parts[0];
            int k = Integer.parseInt(String.valueOf(imgnam));*/

            Picasso.with(getApplicationContext()).load(IMAGES[index]).into(imageView);





            name.setText(favoList.get(i).name);
            company.setText(favoList.get(i).company);
            price.setText(Integer.toString(favoList.get(i).price)+" $");
            color.setText(favoList.get(i).color);



            return view;

        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.d, R.anim.d);
    }


}
