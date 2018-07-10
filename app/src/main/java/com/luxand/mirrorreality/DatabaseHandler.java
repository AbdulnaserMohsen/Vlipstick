package com.luxand.mirrorreality;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper
{
    // Database Version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    private static final String DATABASE_NAME = "productdb";

    // tables name
    private static final String TABLE_Glasses = "glasses";

    private static final String TABLE_FAVO_CART = "favo_cart";


    // Glasses Table Columns names
    private static final String KEY_glass_ID = "id";
    private static final String KEY_glass_NAME = "name";
    private static final String KEY_glass_COMPANY = "company";
    private static final String KEY_glass_PRICE = "price";
    private static final String KEY_glass_IMG = "image";
    private static final String KEY_glass_COLOR = "color";


    //private static final String KEY_favo_ID = "id";
    private static final String KEY_user_ID = "user_id";
    private static final String KEY_prod_ID_FAVO_CART = "prod_id";
    private static final String KEY_LOVED = "loved";
    private static final String KEY_CARTED = "carted";
    private static final String KEY_QUANTITY = "quantity";




    public  DatabaseHandler(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE_PRODUCTS="CREATE TABLE " + TABLE_Glasses + "("
                + KEY_glass_ID +" INTEGER PRIMARY KEY,"
                + KEY_glass_NAME +" TEXT,"
                + KEY_glass_COMPANY +" TEXT,"
                + KEY_glass_PRICE +" INTEGER,"
                + KEY_glass_IMG +" TEXT,"
                + KEY_glass_COLOR  +" TEXT " + ")";
        db.execSQL(CREATE_TABLE_PRODUCTS);

        String CREATE_TABLE_FAVO_CART="CREATE TABLE " + TABLE_FAVO_CART + "("
                + KEY_user_ID +" INTEGER DEFAULT 0,"
                + KEY_prod_ID_FAVO_CART +" INTEGER,"
                + KEY_LOVED +" INTEGER DEFAULT 0,"
                + KEY_CARTED +" INTEGER DEFAULT 0,"
                + KEY_QUANTITY  +" INTEGER DEFAULT 0,"
                +"FOREIGN KEY ("+KEY_prod_ID_FAVO_CART+") REFERENCES "+TABLE_Glasses+"("+KEY_glass_ID+"),"
                +"PRIMARY KEY("+KEY_user_ID+","+KEY_prod_ID_FAVO_CART+") )";

        db.execSQL(CREATE_TABLE_FAVO_CART);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Glasses);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVO_CART);

        // Create tables again
        onCreate(db);
    }


    //adding

    public void AddGlass(Glass g)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        values.put(KEY_glass_ID,g.prod_id);
        values.put(KEY_glass_NAME, g.name);
        values.put(KEY_glass_COMPANY, g.company );
        values.put(KEY_glass_PRICE, g.price);
        values.put(KEY_glass_IMG, g.image);
        values.put(KEY_glass_COLOR, g.color);

        db.insert(TABLE_Glasses, null, values);
        db.close();

        /*String query = "INSERT INTO glasses (id,name,company,type,price,image,color)" +
                "values ("+g.id+",'"+ g.name + "','"+g.company + "','"+g.type +"','"+g.price
                +"','"+ g.image + "','"+g.color +"')";
        db.execSQL(query);
        db.close();*/


    }


    public void AddToFavoCart(Context context, Favo_Cart f)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        Favo_Cart exists = getOneFavoCarted(f.user_id,f.prod_in_favo_cart_id);

        if(f.prod_in_favo_cart_id != exists.prod_in_favo_cart_id)
        {
            //values.put(KEY_favo_ID, f.favo_cart_id );
            values.put(KEY_user_ID, f.user_id);
            values.put(KEY_prod_ID_FAVO_CART,f.prod_in_favo_cart_id);
            values.put(KEY_LOVED,f.loved);
            values.put(KEY_CARTED,f.carted);
            values.put(KEY_QUANTITY,f.quantity);

            db.insert(TABLE_FAVO_CART, null, values);
            db.close();
        }
        else
        {
            values.put(KEY_LOVED,f.loved);
            values.put(KEY_CARTED,f.carted);
            values.put(KEY_QUANTITY,f.quantity);

            db.update(TABLE_FAVO_CART, values,KEY_user_ID+" = "+f.user_id+" AND "
                    +KEY_prod_ID_FAVO_CART +" = "+f.prod_in_favo_cart_id,null);
            db.close();
        }
        if(f.user_id != 0)
            post(context,f);
    }


    public void AddToFavo(Context context, Favo_Cart f)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        Favo_Cart exists = getOneFavoCarted(f.user_id,f.prod_in_favo_cart_id);

        if(f.prod_in_favo_cart_id != exists.prod_in_favo_cart_id)
        {
            //values.put(KEY_favo_ID, f.favo_cart_id );
            values.put(KEY_user_ID, f.user_id);
            values.put(KEY_prod_ID_FAVO_CART,f.prod_in_favo_cart_id);
            values.put(KEY_LOVED,1);


            db.insert(TABLE_FAVO_CART, null, values);
            db.close();
        }
        else
        {
            values.put(KEY_LOVED,1);


            db.update(TABLE_FAVO_CART, values,KEY_user_ID+" = "+f.user_id+" AND "
                    +KEY_prod_ID_FAVO_CART +" = "+f.prod_in_favo_cart_id,null);
            db.close();
        }

        if(f.user_id != 0)
            post(context,f);
    }

    public void AddToCart(Context context, Favo_Cart f)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        Favo_Cart exist = getOneFavoCarted(f.user_id,f.prod_in_favo_cart_id);

        if(f.prod_in_favo_cart_id != exist.prod_in_favo_cart_id)
        {
            //values.put(KEY_favo_ID, f.favo_cart_id );
            values.put(KEY_user_ID, f.user_id);
            values.put(KEY_prod_ID_FAVO_CART,f.prod_in_favo_cart_id);
            values.put(KEY_CARTED,1);
            values.put(KEY_QUANTITY,f.quantity);

            db.insert(TABLE_FAVO_CART, null, values);
            db.close();
        }
        else
        {
            values.put(KEY_CARTED,1);
            values.put(KEY_QUANTITY,f.quantity);

            db.update(TABLE_FAVO_CART, values,KEY_user_ID+" = "+f.user_id+" AND "
                    +KEY_prod_ID_FAVO_CART +" = "+f.prod_in_favo_cart_id,null);
            db.close();
        }
        if(f.user_id != 0)
            post(context,f);

    }


    public List<Glass> getAllGlasses()
    {

        List<Glass> glassList = new ArrayList<Glass>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_Glasses;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                Glass g = new Glass();
                g.prod_id = Integer.parseInt(cursor.getString(0));
                g.name = cursor.getString(1);
                g.company = cursor.getString(2);
                g.price = Integer.parseInt(cursor.getString(3));
                g.image = cursor.getString(4);
                g.color = cursor.getString(5);

                // Adding contact to list
                glassList.add(g);

            } while (cursor.moveToNext());
        }

        return glassList;


        /*List<product.Glass> glassList = new ArrayList<product.Glass>();

        String query = "select * from glasses";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do
            {
                product.Glass g = new product.Glass();
                g.id = cursor.getInt(0);
                g.name = cursor.getString(1);
                g.company = cursor.getString(2);
                g.type = cursor.getString(3);
                g.price = cursor.getInt(4);
                g.image = cursor.getString(5);
                g.color = cursor.getString(6);


            }while (cursor.moveToNext());

        }


        return  glassList;*/


    }


    public  Glass getOneGlass(int glassID)
    {
        Glass g = new Glass();

        String selectQuery = "SELECT  * FROM " + TABLE_Glasses +" WHERE "+KEY_glass_ID+" = "+glassID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                g.prod_id = Integer.parseInt(cursor.getString(0));
                g.name = cursor.getString(1);
                g.company = cursor.getString(2);
                g.price = Integer.parseInt(cursor.getString(3));
                g.image = cursor.getString(4);
                g.color = cursor.getString(5);


            } while (cursor.moveToNext());
        }

        return g;

    }


    public List<Favo_Cart> getAllFavoCarted(int user_id)
    {

        List<Favo_Cart> favoCartedList = new ArrayList<Favo_Cart>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVO_CART +" WHERE "
                +KEY_user_ID +" != 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                Favo_Cart f = new Favo_Cart();

                //f.favo_cart_id = Integer.parseInt(cursor.getString(0));
                f.user_id = Integer.parseInt(cursor.getString(0));
                f.prod_in_favo_cart_id = Integer.parseInt(cursor.getString(1));
                f.loved = Integer.parseInt(cursor.getString(2));
                f.carted = Integer.parseInt(cursor.getString(3));
                f.quantity = Integer.parseInt(cursor.getString(4));

                favoCartedList.add(f);

            } while (cursor.moveToNext());
        }

        return favoCartedList;
    }

    public List<Favo_Cart> getFavorits(int user_id)
    {

        List<Favo_Cart> favoList = new ArrayList<Favo_Cart>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVO_CART +" WHERE "+ KEY_LOVED +" = 1 AND "
                +KEY_user_ID +" = "+user_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                Favo_Cart f = new Favo_Cart();

                //f.favo_cart_id = Integer.parseInt(cursor.getString(0));
                f.user_id = Integer.parseInt(cursor.getString(0));
                f.prod_in_favo_cart_id = Integer.parseInt(cursor.getString(1));
                f.loved = Integer.parseInt(cursor.getString(2));
                f.carted = Integer.parseInt(cursor.getString(3));
                f.quantity = Integer.parseInt(cursor.getString(4));

                favoList.add(f);

            } while (cursor.moveToNext());
        }

        return favoList;
    }

    public List<Favo_Cart> getCarted(int user_id)
    {

        List<Favo_Cart> favoList = new ArrayList<Favo_Cart>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVO_CART +" WHERE "+ KEY_CARTED +" = 1 AND "
                +KEY_user_ID +" = "+user_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                Favo_Cart f = new Favo_Cart();

                //f.favo_cart_id = Integer.parseInt(cursor.getString(0));
                f.user_id = Integer.parseInt(cursor.getString(0));
                f.prod_in_favo_cart_id = Integer.parseInt(cursor.getString(1));
                f.loved = Integer.parseInt(cursor.getString(2));
                f.carted = Integer.parseInt(cursor.getString(3));
                f.quantity = Integer.parseInt(cursor.getString(4));

                favoList.add(f);

            } while (cursor.moveToNext());
        }

        return favoList;
    }

    public Favo_Cart getOneFavoCarted(int user_id , int prod_id)
    {

        Favo_Cart favoItem = new Favo_Cart();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVO_CART +" WHERE "+KEY_user_ID +" = "
                +user_id+" AND "+KEY_prod_ID_FAVO_CART+" = "+prod_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                Favo_Cart f = new Favo_Cart();

                //f.favo_cart_id = Integer.parseInt(cursor.getString(0));
                f.user_id = Integer.parseInt(cursor.getString(0));
                f.prod_in_favo_cart_id = Integer.parseInt(cursor.getString(1));
                f.loved = Integer.parseInt(cursor.getString(2));
                f.carted = Integer.parseInt(cursor.getString(3));
                f.quantity = Integer.parseInt(cursor.getString(4));

                favoItem = f;

            } while (cursor.moveToNext());
        }

        return favoItem;
    }



    public List<Glass> getFavoGlasses(int user_id )
    {
        List<Favo_Cart> favo_list = new ArrayList<Favo_Cart>();

        favo_list = getFavorits(user_id);

        List<Glass> glassList = new ArrayList<Glass>();

        // Select All Query


        String selectQuery = "SELECT * FROM " + TABLE_Glasses +" g, "+TABLE_FAVO_CART+
                " fg WHERE fg."+ KEY_LOVED +" = 1 AND fg." +KEY_user_ID +
                " = "+user_id+" AND g."+ KEY_glass_ID+" = fg." +KEY_prod_ID_FAVO_CART;

        /*String selectQuery = "SELECT  * FROM " + TABLE_Glasses +
                " WHERE "+ KEY_glass_ID+" =( SELECT "+KEY_prod_ID_FAVO_CART+ " FROM "+
                TABLE_FAVO_CART +" WHERE "+KEY_LOVED +" = 1 AND " +KEY_user_ID +
                " = "+user_id+")";*/

        /*String selectQuery =
                "SELECT  * FROM " + TABLE_Glasses +" WHERE "+ KEY_glass_ID+" = "KR;*/

        /*String selectQuery1 = "SELECT * FROM " + TABLE_Glasses +" fg " +
                "WHERE fg."+ KEY_glass_ID+" = " +favo_list.get(1).prod_in_favo_cart_id;*/

        /*for (Favo_Cart favo : favo_list)
        {
            favo.prod_in_favo_cart_id++;
            String selectQuery = "SELECT * FROM " + TABLE_Glasses +" fg " +
                    "WHERE fg."+ KEY_glass_ID+" = " +favo.prod_in_favo_cart_id;


        }*/

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                Glass g = new Glass();
                g.prod_id = Integer.parseInt(cursor.getString(0));
                g.name = cursor.getString(1);
                g.company = cursor.getString(2);
                g.price = Integer.parseInt(cursor.getString(3));
                g.image = cursor.getString(4);
                g.color = cursor.getString(5);

                glassList.add(g);

            } while (cursor.moveToNext());
        }



        // return contact list
        return glassList;
    }


    public List<Glass> getCartGlasses(int user_id)
    {
        List<Glass> glassList = new ArrayList<Glass>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_Glasses +" , "+TABLE_FAVO_CART+
                " WHERE "+ KEY_CARTED +" = 1 AND " +KEY_user_ID +
                " = "+user_id+" AND "+ KEY_glass_ID+" = " +KEY_prod_ID_FAVO_CART;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {

                Glass g = new Glass();
                g.prod_id = Integer.parseInt(cursor.getString(0));
                g.name = cursor.getString(1);
                g.company = cursor.getString(2);
                g.price = Integer.parseInt(cursor.getString(3));
                g.image = cursor.getString(4);
                g.color = cursor.getString(5);

                glassList.add(g);

            } while (cursor.moveToNext());
        }

        // return contact list
        return glassList;
    }




    //don't forget cart
    public void DeleteGlass(int id)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Glasses, KEY_glass_ID + " = ?",
                new String[] { String.valueOf(id) });

        db.delete(TABLE_FAVO_CART, KEY_prod_ID_FAVO_CART + " = ?",
                new String[] { String.valueOf(id) });

        db.close();

       /* SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM glasses WHERE id = "+id;
        db.execSQL(query);*/
    }

    public void DeleteFavo(Context context, Favo_Cart f)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        values.put(KEY_LOVED,0);

        db.update(TABLE_FAVO_CART, values,KEY_user_ID+" = "+f.user_id
                +" AND "+KEY_prod_ID_FAVO_CART+" = "+f.prod_in_favo_cart_id,null);
        db.close();

        f.loved =0;
        if(f.user_id != 0)
            post(context,f);
    }

    public void DeleteFromCart(Context context, Favo_Cart f)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        values.put(KEY_CARTED,0);
        values.put(KEY_QUANTITY,0);

        db.update(TABLE_FAVO_CART, values,KEY_user_ID+" = "+f.user_id
                +" AND "+KEY_prod_ID_FAVO_CART+" = "+f.prod_in_favo_cart_id,null);
        db.close();

        f.carted=0;
        f.quantity=0;

        if(f.user_id != 0)
            post(context,f);
    }

    public  int getcountglasses()
    {
        String query = "SELECT * FROM "+ TABLE_Glasses;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        return  cursor.getCount();
    }

    public void Drop()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop glasses
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Glasses);

        //drop favorites
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVO_CART);

        // Create tables again
        onCreate(db);
    }


    void post(final Context context , final Favo_Cart item)
    {


        final String url = "http://naserahmed1995.000webhostapp.com/glasses/post_favo_cart.php";//volley

        //final String url = "http://195.246.49.58/section/films.php";


        RequestQueue queue = Volley.newRequestQueue(context); //volley


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
                                Toast.makeText(context, "updated successfully",
                                        Toast.LENGTH_LONG).show();
                            }
                            else if(jsonObject.getString("result").contains("error"))
                            {
                                Toast.makeText(context, "can not updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context, " Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(context, "No internet or server maybe sleep ",
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
                        Toast toast = Toast.makeText(context, "Connection error ", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("prod_id", String.valueOf(item.prod_in_favo_cart_id));
                params.put("user_id",String.valueOf(item.user_id));
                params.put("loved",String.valueOf(item.loved));
                params.put("carted",String.valueOf(item.carted));
                params.put("quantity",String.valueOf(item.quantity));

                return params;
            }
        };
        queue.add(postRequest);

    }

}

