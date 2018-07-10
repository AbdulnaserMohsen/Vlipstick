package com.luxand.mirrorreality;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity
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

    TextView totalAllTextView;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id  = prefs.getInt("id",0);//"No name defined" is the default value.


        fa = this;

        totalAllTextView =(TextView) findViewById(R.id.TotalAll);

        favo_cart_item = new Favo_Cart();
        favo_cart_item.user_id = user_id;

        db = new DatabaseHandler(this);

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        calculateAll();


        ListView listView = (ListView) findViewById(R.id.list);

        Cart.CustomAdapter customAdapter = new Cart.CustomAdapter();

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
        setContentView(R.layout.activity_cart);

        totalAllTextView =(TextView) findViewById(R.id.TotalAll);

        favo_cart_item = new Favo_Cart();
        favo_cart_item.user_id = user_id;

        db = new DatabaseHandler(this);

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        calculateAll();


        ListView listView = (ListView) findViewById(R.id.list);

        Cart.CustomAdapter customAdapter = new Cart.CustomAdapter();

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
        return;
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

        Intent i=new Intent(getApplicationContext(),Favorites.class);
        startActivity(i);
        menuClick(view);
        finish();
    }



    public void DeleteFromCart(int prod_id)
    {
        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);

        db.DeleteFromCart(getApplicationContext(),favo_cart_item);

        Toast.makeText(getApplicationContext(),"deleted from cart",Toast.LENGTH_SHORT).show();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        renderAgain();

    }


    public void fav(ImageView imgggg , int prod_id)
    {
        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
        if(favo_cart_item.loved == 0)
        {
            favo_cart_item.prod_in_favo_cart_id = prod_id;
            favo_cart_item.user_id = user_id;

            imgggg.setImageResource(R.drawable.heart3);
            db.AddToFavo(getApplicationContext(), favo_cart_item);

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

    }

    void IncreaseQuantity(EditText e, int prod_id)
    {
        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
        favo_cart_item.quantity++;
        db.AddToCart(getApplicationContext(), favo_cart_item);

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
        e.setText(Integer.toString(favo_cart_item.quantity));

    }

    void DecreaseQuantity(EditText e, int prod_id)
    {
        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);

        if(favo_cart_item.quantity < 2)
        {
            favo_cart_item.quantity =1;
            db.AddToCart(getApplicationContext(), favo_cart_item);
            return;
        }

        favo_cart_item.quantity--;
        db.AddToCart(getApplicationContext(),favo_cart_item);

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
        e.setText(Integer.toString(favo_cart_item.quantity));

    }

    public void fromEdit(EditText e, int prod_id)
    {
        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);

        favo_cart_item.quantity = Integer.parseInt(e.getText().toString());

        if(favo_cart_item.quantity < 2)
        {
            favo_cart_item.quantity =1;
            db.AddToCart(getApplicationContext(),favo_cart_item);
            return;
        }

        db.AddToCart(getApplicationContext(),favo_cart_item);

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);

        e.setText(Integer.toString(favo_cart_item.quantity));

    }

    public void calculateTotaloneItem(TextView totalItem, int prod_id)
    {
        favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
        Glass g = db.getOneGlass(prod_id);
        int total = favo_cart_item.quantity * g.price;
        totalItem.setText(Integer.toString(total));
        calculateAll();
    }

    void calculateAll( )
    {

        int TotalAll=0;
        for(int j=0; j<cartList.size(); j++)
        {
            Favo_Cart f = db.getOneFavoCarted(user_id,cartList.get(j).prod_id);
            TotalAll += cartList.get(j).price * f.quantity;
        }

        totalAllTextView.setText(Integer.toString(TotalAll));
        //totalAllTextView.setText("0");
    }


    public void pay(View v)
    {
        if(totalAllTextView.getText().equals("0")) return;

        for(int j=0; j<cartList.size(); j++)
        {
            Favo_Cart f = db.getOneFavoCarted(user_id,cartList.get(j).prod_id);
            db.DeleteFromCart(getApplicationContext(),f);
        }

        Toast.makeText(getApplicationContext(),"Congratulation",Toast.LENGTH_SHORT).show();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        totalAllTextView.setText("0");

        renderAgain();

    }




    class  CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            //return IMAGES.length;
            return cartList.size();
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
//            view = getLayoutInflater().inflate(R.layout.list_item_cart,null);
//
//            de.hdodenhof.circleimageview.CircleImageView imageView = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.CartElementImageView);
//
//
//            final int prod_id = cartList.get(i).prod_id;
//            final ImageView imgheart=(ImageView) view.findViewById(R.id.CartElementFavBtn);
//            favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
//            if(favo_cart_item.loved != 0)
//                imgheart.setImageResource(R.drawable.heart3);
//            else
//                imgheart.setImageResource(R.drawable.heart5);
//
//
//            imgheart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view)
//                {
//                    fav(imgheart,prod_id);
//                }
//            });




//            final ImageView imgDeletCart=(ImageView) view.findViewById(R.id.CartElementDeleteCart);
//            imgDeletCart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view)
//                {
//                    DeleteFromCart(prod_id);
//                }
//            });
//
//            /*final ImageView imgeDeleteCart = (ImageView) findViewById(R.id.CartElementDeleteCart);
//            imgeDeleteCart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view)
//                {
//                    DeleteFromCart(prod_id);
//                }
//            });*/
//
//            final TextView totOneItm =(TextView) view.findViewById(R.id.ToalOneItem);
//            final EditText quantity = (EditText) view.findViewById(R.id.CartElementQunatity);
//            quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if(!hasFocus)
//                    {
//                        fromEdit(quantity,prod_id);
//                        calculateTotaloneItem(totOneItm,prod_id);
//                    }
//                }
//            });
//
//
//            favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
//            quantity.setText(Integer.toString(favo_cart_item.quantity));
//
//            final ImageView imgIncreaseQuantity =(ImageView) view.findViewById(R.id.CartElementBtnIncrease);
//            imgIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v)
//                {
//                    IncreaseQuantity(quantity,prod_id);
//                    calculateTotaloneItem(totOneItm,prod_id);
//                }
//            });
//
//            final ImageView imgDecreaseQuantity=(ImageView) view.findViewById(R.id.CartElementBtnDeacrease);
//            imgDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view)
//                {
//                    DecreaseQuantity(quantity,prod_id);
//                    calculateTotaloneItem(totOneItm,prod_id);
//                }
//            });
//            calculateTotaloneItem(totOneItm,prod_id);
//
//
//
//
//
//
//
//
//
//            TextView name  = (TextView)  view.findViewById(R.id.CartElementName);
//            TextView company = (TextView) view.findViewById(R.id.CartElementCompany);
//            TextView price   = (TextView) view.findViewById(R.id.CartElementPrice);
//            TextView color = (TextView) view.findViewById(R.id.CartElementColor);
//
//
//
//
//            //imageView.setImageResource(IMAGES[i]);
//            //name.setText(NAMES[i]);
//            //type.setText(DESC[i]);
//            //price.setText(Float.toString(PRICES[i]));
//
//
//            //Picasso.with(getApplicationContext()).load(glassList.get(i).image).into(imageView);
//
//
//
//            /*final String imagename = cartList.get(i).image;
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//            File myImageFile = new File(directory, "glass2.png");
//            //File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            //File folder = new File(sd, "/Picasso/");
//            //File myImageFile = new File(folder, imagename);
//            Picasso.with(getApplicationContext()).load(myImageFile).into(imageView);*/
//
//            int index=0;
//            for(int j=0;j<glassList.size(); j++)
//            {
//                if(prod_id == glassList.get(j).prod_id)
//                {
//                    index = j;
//                    break;
//                }
//            }
//            Picasso.with(getApplicationContext()).load(IMAGES[index]).into(imageView);
//
//            name.setText(cartList.get(i).name);
//            company.setText(cartList.get(i).company);
//            price.setText(Integer.toString(cartList.get(i).price)+" $");
//            color.setText(cartList.get(i).color);




            view = getLayoutInflater().inflate(R.layout.cart_items2,null);

            final int prod_id = cartList.get(i).prod_id;

            de.hdodenhof.circleimageview.CircleImageView imageView = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.imgofprod);

            int index=0;
            for(int j=0;j<glassList.size(); j++)
            {
                if(prod_id == glassList.get(j).prod_id)
                {
                    index = j;
                    break;
                }
            }
            Picasso.with(getApplicationContext()).load(IMAGES[index]).into(imageView);



            TextView price   = (TextView) view.findViewById(R.id.priceOfElem);
            price.setText(Integer.toString(cartList.get(i).price)+" $");


            final TextView totOneItm =(TextView) view.findViewById(R.id.totOFone);


            final EditText quantity = (EditText) view.findViewById(R.id.quantityOfElem);
            quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus)
                    {
                        fromEdit(quantity,prod_id);
                        calculateTotaloneItem(totOneItm,prod_id);
                    }
                }
            });

            favo_cart_item = db.getOneFavoCarted(user_id,prod_id);
            quantity.setText(Integer.toString(favo_cart_item.quantity));


            final ImageView imgIncreaseQuantity =(ImageView) view.findViewById(R.id.increaseQuantity);
            imgIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    IncreaseQuantity(quantity,prod_id);
                    calculateTotaloneItem(totOneItm,prod_id);
                }
            });

            final ImageView imgDecreaseQuantity=(ImageView) view.findViewById(R.id.decreaseQuantity);
            imgDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    DecreaseQuantity(quantity,prod_id);
                    calculateTotaloneItem(totOneItm,prod_id);
                }
            });
            calculateTotaloneItem(totOneItm,prod_id);


            final ImageView imgDeletCart=(ImageView) view.findViewById(R.id.deleteCartElem);
            imgDeletCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    DeleteFromCart(prod_id);
                }
            });


            /*final ImageView imgheart=(ImageView) view.findViewById(R.id.CartElementFavBtn);
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
            });*/






















            /*TextView name  = (TextView)  view.findViewById(R.id.CartElementName);
            TextView company = (TextView) view.findViewById(R.id.CartElementCompany);
            TextView price   = (TextView) view.findViewById(R.id.CartElementPrice);
            TextView color = (TextView) view.findViewById(R.id.CartElementColor);*/





            /*name.setText(cartList.get(i).name);
            company.setText(cartList.get(i).company);
            price.setText(Integer.toString(cartList.get(i).price)+" $");
            color.setText(cartList.get(i).color);*/




            return view;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.d, R.anim.d);
    }
}
