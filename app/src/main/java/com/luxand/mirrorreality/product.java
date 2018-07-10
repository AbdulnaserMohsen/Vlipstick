package com.luxand.mirrorreality;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.luxand.mirrorreality.Glass;

public class product extends AppCompatActivity
{

    ImageView img1,img2,img3,img4,img5 ;
    boolean open =true;
    Animation animOpen,animClose,animRotate;


    List<Glass> glassList;
    List<Glass> favoList;
    List<Glass> cartList;

    List<Integer>server_prod_ids;

    int user_id =0;
    Favo_Cart favo_cart_item;

    DatabaseHandler db;


    int[] IMAGES = {R.drawable.img1,R.drawable.img2,
            R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,
            R.drawable.img7,R.drawable.img8,R.drawable.img9,R.drawable.img10,
            R.drawable.img11,R.drawable.img12,R.drawable.img13,R.drawable.img14,
            R.drawable.img15};
    String[] NAMES = {"Monesters part 1"};
    String[] DESC  = {"Company monsters cartoon"};
    float [] PRICES={10};


    public static final String MY_PREFS_NAME = "MyPrefsFile";




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id  = prefs.getInt("id",0);//"No name defined" is the default value.




        favo_cart_item = new Favo_Cart();
        favo_cart_item.user_id = user_id;

        db = new DatabaseHandler(this);



        //glassList = new ArrayList<Glass>(db.getAllGlasses());
        //favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        //cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));



        //db.Drop();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        getdatafromInternet();
        FavoCart();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        ListView listView = (ListView) findViewById(R.id.list);

        CustomAdapter customAdapter = new CustomAdapter();

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
        setContentView(R.layout.activity_product);

        favo_cart_item = new Favo_Cart();
        favo_cart_item.user_id = user_id;

        db = new DatabaseHandler(this);

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        //getdatafromInternet();
        //FavoCart();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));




        ListView listView = (ListView) findViewById(R.id.list);

        CustomAdapter customAdapter = new CustomAdapter();

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

    void getdatafromInternet()
    {

        final String url = "http://naserahmed1995.000webhostapp.com/glasses/get_lips.php";//volley


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
    }
    void  FavoCart()
    {

        final String url = "http://naserahmed1995.000webhostapp.com/glasses/get_favo_cart_lips.php";//volley


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext()); //volley

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
                                    JSONObject item = arr.getJSONObject(i);

                                    String ff;
                                    Favo_Cart f = new Favo_Cart();
                                    f.user_id   = item.getInt("user_id");
                                    f.prod_in_favo_cart_id = item.getInt("prod_id");
                                    f.loved = item.getInt("loved");
                                    f.carted = item.getInt("carted");
                                    f.quantity = item.getInt("quantity");


                                    db.AddToFavoCart(getApplicationContext(),f);
                                }

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), " no favo items",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "bad response from server"+e,
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
                params.put("userID", String.valueOf(user_id));
                return params;
            }
        };
        queue.add(postRequest);
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        renderAgain();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        renderAgain();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        renderAgain();
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

    }

    public void favClick (View view )
    {
        Intent i=new Intent(getApplicationContext(),Favorites.class);
        startActivity(i);
        menuClick(view);

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
        favo_cart_item.carted=1;
        db.AddToCart(getApplicationContext(), favo_cart_item);

        Toast.makeText(getApplicationContext(),"added to cart",Toast.LENGTH_SHORT).show();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

    }

    public void fav(ImageView imgggg , int prod_id)
    {
        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
        if(favo_cart_item.loved == 0)
        {
            favo_cart_item.prod_in_favo_cart_id = prod_id;
            favo_cart_item.user_id = user_id;
            favo_cart_item.loved=1;
            imgggg.setImageResource(R.drawable.heart3);
            db.AddToFavo(getApplicationContext(),favo_cart_item);

            Toast.makeText(getApplicationContext(),"added to favorites",Toast.LENGTH_SHORT).show();

            glassList = new ArrayList<Glass>(db.getAllGlasses());
            favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
            cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        }
        else
        {
            favo_cart_item.prod_in_favo_cart_id = prod_id;
            favo_cart_item.user_id = user_id;
            imgggg.setImageResource(R.drawable.heart5);
            db.DeleteFavo(getApplicationContext(),favo_cart_item);

            Toast.makeText(getApplicationContext(),"deleted from favorites",Toast.LENGTH_SHORT).show();

            glassList = new ArrayList<Glass>(db.getAllGlasses());
            favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
            cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));
        }


        /*if(glassList.get(id).loved ==0)
        {
            imgggg.setImageResource(R.drawable.heart3);
            //++id;
            db.AddToFavo(glassList.get(id));
            Toast.makeText(getApplicationContext(),"added to favorites",Toast.LENGTH_SHORT).show();

            glassList = db.getAllGlasses();
        }
        else
        {
            imgggg.setImageResource(R.drawable.heart5);
            db.DeleteFavo(glassList.get(id));
            Toast.makeText(getApplicationContext(),"deleted from favorites",Toast.LENGTH_SHORT).show();

            glassList = db.getAllGlasses();
        }*/

    }


    private Target picassoImageTarget(Context context, final String imageDir, final String imageName)
    {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target()
        {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        /*final File myImageFile = new File(directory, imageName); // Create image file
                        if (!myImageFile.exists())
                        {
                            try
                            {
                                myImageFile.createNewFile();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            try
                            {
                                FileOutputStream outputStream = new FileOutputStream(String.valueOf(myImageFile));
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();

                            }
                            catch (FileNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }*/


                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try
                        {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        finally
                        {
                            try
                            {
                                fos.close();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) { }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {
                if (placeHolderDrawable != null) {}
            }
        };

    }



    //target to save
    private static Target getTarget(final String url)
    {
        Target target = new Target()
        {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) { }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        };
        return target;
    }


    private Target target = new Target()
    {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from)
        {

            new Thread(new Runnable()
            {
                @Override
                public void run() {

                    File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File folder = new File(sd, "/Picasso/");
                    if (!folder.exists()) {
                        if (!folder.mkdir()) {
                            Log.e("ERROR", "Cannot create a directory!");
                        } else {
                            folder.mkdir();
                        }
                    }

                    File[] fileName = {new File(folder, "one.jpg"), new File(folder, "two.jpg")};

                    for (int i=0; i<fileName.length; i++) {

                        if (!fileName[i].exists()) {
                            try {
                                fileName[i].createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {

                            try {
                                FileOutputStream outputStream = new FileOutputStream(String.valueOf(fileName[i]));
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                outputStream.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }).start();

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) { }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) { }
    };





    class  CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            //return IMAGES.length;
            return glassList.size();
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


            final int prod_id = glassList.get(i).prod_id;
            final ImageView imgheart=(ImageView) view.findViewById(R.id.Fav);
            favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
            if(favo_cart_item.loved != 0)
                imgheart.setImageResource(R.drawable.heart3);
            else
                imgheart.setImageResource(R.drawable.heart5);

            imgheart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    fav(imgheart,prod_id);
                }
            });



            final android.support.v7.widget.AppCompatButton imgCart =(android.support.v7.widget.AppCompatButton) view.findViewById(R.id.ProdCart);
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
            Picasso.with(product.this).load(myImageFile).into(imageView);*/



            int index=0;
            for(int j=0;j<glassList.size(); j++)
            {
                if(prod_id == glassList.get(j).prod_id)
                {
                    index = j;
                    break;
                }
            }
            Picasso.with(product.this).load(IMAGES[index]).into(imageView);


            name.setText(glassList.get(i).name);
            company.setText(glassList.get(i).company);
            price.setText(Integer.toString(glassList.get(i).price)+" $");
            color.setText(glassList.get(i).color);

            return view;

        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.d, R.anim.d);
    }

}
