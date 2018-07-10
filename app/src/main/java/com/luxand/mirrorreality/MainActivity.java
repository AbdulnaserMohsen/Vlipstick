package com.luxand.mirrorreality;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = "MainActivity";

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;

    //private ImageView mCameraButton;
    //private ImageView mPhotoButton;
    //private ImageView mInfoButton;

    private android.support.v7.widget.AppCompatImageView mPhotoButton;

    private RelativeLayout mBottomLayout;
    private RelativeLayout mMainLayout;
    //private TextView mFpsTextView;
    private MainView mMainView;

    private PowerManager.WakeLock mWakeLock;


    //public TextView fpsTextView() { return mFpsTextView; }


    private ArrayList<Integer> mImageUrls = new ArrayList<>();

    List<Glass> glassList;
    List<Glass> favoList;
    List<Glass> cartList;
    DatabaseHandler db;
    Favo_Cart favo_cartItem;

    int user_id = 0;
    int prod_id = 1;
    int requiredIndex=0;

    ImageView img;
    int curr=1;


    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        user_id  = prefs.getInt("id",0);//"No name defined" is the default value.
        prod_id  = prefs.getInt("prod_id",1);//"No name defined" is the default value.


        db = new DatabaseHandler(this);

        favo_cartItem = new Favo_Cart();
        favo_cartItem.user_id = user_id;

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));



        requiredIndex=0;
        for(int j=0;j<glassList.size(); j++)
        {
            if(prod_id == glassList.get(j).prod_id)
            {
                requiredIndex = j;
                break;
            }
        }




        // full screen & full brightness
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mWakeLock = ((PowerManager)getSystemService ( Context.POWER_SERVICE )).newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
        mWakeLock.acquire();

        setContentView(R.layout.activity_main);
        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        //mFpsTextView = (TextView) findViewById(R.id.fps_text_view);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);

        //mCameraButton = (ImageView) findViewById(R.id.camera_button);
        //mPhotoButton = (ImageView) findViewById(R.id.photo_button);
        //mInfoButton = (ImageView) findViewById(R.id.info_button);

        mPhotoButton = (android.support.v7.widget.AppCompatImageView) findViewById(R.id.appCompatImageView);

        //mCameraButton.setOnClickListener(this);
        mPhotoButton.setOnClickListener(this);
        //mInfoButton.setOnClickListener(this);

        checkCameraPermissionsAndOpenCamera();




        favo_cartItem = db.getOneFavoCarted(user_id,prod_id);
        img=(ImageView) findViewById(R.id.Fav);
        if(favo_cartItem.loved != 0)
        {
            img.setImageResource(R.drawable.heart3);
        }
        else
        {
            img.setImageResource(R.drawable.heart5);
        }


        getImages();



    }



    private void getImages()
    {



        mImageUrls.add(R.drawable.img1);
        mImageUrls.add(R.drawable.img2);
        mImageUrls.add(R.drawable.img3);
        mImageUrls.add(R.drawable.img4);
        mImageUrls.add(R.drawable.img5);
        mImageUrls.add(R.drawable.img6);
        mImageUrls.add(R.drawable.img7);
        mImageUrls.add(R.drawable.img8);
        mImageUrls.add(R.drawable.img9);
        mImageUrls.add(R.drawable.img10);
        mImageUrls.add(R.drawable.img11);
        mImageUrls.add(R.drawable.img12);
        mImageUrls.add(R.drawable.img13);
        mImageUrls.add(R.drawable.img14);
        mImageUrls.add(R.drawable.img15);


        //   Log.d(TAG, "initImageBitmaps: preparing bitmaps.");


        /*mImageUrls.add(R.drawable.e1);

        mImageUrls.add(R.drawable.e2);

        mImageUrls.add(R.drawable.e3);

        mImageUrls.add(R.drawable.e4);
        mImageUrls.add(R.drawable.e5);*/
        initRecyclerView();

    }

    private void initRecyclerView()
    {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,  mImageUrls);
        recyclerView.setAdapter(adapter);
    }



    public void fav(View view)
    {
        favo_cartItem = db.getOneFavoCarted(user_id,prod_id);
        if(favo_cartItem.loved == 0)
        {
            favo_cartItem.prod_in_favo_cart_id = prod_id;
            favo_cartItem.user_id = user_id;

            img.setImageResource(R.drawable.heart3);

            db.AddToFavo(getApplicationContext(),favo_cartItem);

            Toast.makeText(getApplicationContext(),"added to favorites",Toast.LENGTH_SHORT).show();

            glassList = new ArrayList<Glass>(db.getAllGlasses());
            favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
            cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));


        }
        else
        {
            favo_cartItem.prod_in_favo_cart_id = prod_id;
            favo_cartItem.user_id = user_id;

            img.setImageResource(R.drawable.heart5);

            db.DeleteFavo(getApplicationContext(),favo_cartItem);

            Toast.makeText(getApplicationContext(),"deleted from favorites",Toast.LENGTH_SHORT).show();

            glassList = new ArrayList<Glass>(db.getAllGlasses());
            favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
            cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

        }

    }

    public  void  addToCart(View view)
    {
        if(user_id == 0)
        {
            Toast.makeText(getApplicationContext(),"you are in guest mode not user"
                    ,Toast.LENGTH_SHORT).show();
            return;
        }

        favo_cartItem = db.getOneFavoCarted(user_id,prod_id);

        favo_cartItem.prod_in_favo_cart_id = prod_id;
        favo_cartItem.user_id = user_id;

        favo_cartItem.quantity++;
        db.AddToCart(getApplicationContext(), favo_cartItem);

        Toast.makeText(getApplicationContext(),"added to cart",Toast.LENGTH_SHORT).show();

        glassList = new ArrayList<Glass>(db.getAllGlasses());
        favoList =  new ArrayList<Glass>(db.getFavoGlasses(user_id));
        cartList =  new ArrayList<Glass>(db.getCartGlasses(user_id));

    }


    void renderagain()
    {
        favo_cartItem = db.getOneFavoCarted(user_id,prod_id);
        img=(ImageView) findViewById(R.id.Fav);
        if(favo_cartItem.loved != 0)
        {
            img.setImageResource(R.drawable.heart3);
        }
        else
        {

            img.setImageResource(R.drawable.heart5);
        }

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            /*case R.id.info_button:
                alert(this, null, "Immerse yourself in amazing augmented reality! Youmask applies live filters to your face in real time as you look into the front camera. Swipe left and right to change the filter. Youmask makes you look older, younger, turns you into a zombie and applies a bunch of special masks to your face. An SDK is available for mobile developers at luxand.com/mirror-reality.");
                break;*/
            case R.id.appCompatImageView:
                checkSavingPermissionAndTakeSnapshot();
                break;
            /*case R.id.camera_button:
                Log.d(TAG, "not implemented");
                break;*/
            default:
                break;
        }
    }

    @Override
    protected void onPause()
    {
        if (mWakeLock != null && mWakeLock.isHeld())
        {
            mWakeLock.release();
        }
        if (mMainView != null)
        {
            mMainView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        if (mMainView != null)
        {
            mMainView.pause();
        }
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mMainView != null)
        {
            mMainView.onResume();
        }
        if(mWakeLock != null && !mWakeLock.isHeld())
        {
            mWakeLock.acquire();
        }
    }

    public static void alert(Context context, final Runnable callback, String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        if (callback != null)
        {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    callback.run();
                }
            });
        }
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    openCamera();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    takeSnapshot();
                }
                break;
            default:
                break;
        }
    }

    private void openCamera()
    {
        mMainView = new MainView(this);

        /*
        mMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainView.renderer().nextMask();
            }
        });
        */

        mMainView.renderer().selectMask(requiredIndex);

        mMainLayout.addView(mMainView, new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        //mFpsTextView.bringToFront();
        mBottomLayout.bringToFront();
    }

    private void checkCameraPermissionsAndOpenCamera()
    {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {

                final Runnable onCloseAlert = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[] {Manifest.permission.CAMERA},
                                CAMERA_PERMISSION_REQUEST_CODE);
                    }
                };

                alert(this, onCloseAlert, "The application applies filters to the live feed from camera.");
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            openCamera();
        }
    }

    private void takeSnapshot()
    {
        if (mMainView != null)
        {
            mMainView.renderer().snapshot();
        }
    }

    private void checkSavingPermissionAndTakeSnapshot()
    {
        if (mMainView == null) return; // nothing to do without camera
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {

                final Runnable onCloseAlert = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
                    }
                };

                alert(this, onCloseAlert, "Permissions are needed to save image.");
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            takeSnapshot();
        }
    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
    {

        private static final String TAG = "RecyclerViewAdapter";



        private ArrayList<Integer> mImageUrls = new ArrayList<>();
        private Context mContext;





        public RecyclerViewAdapter(Context context, ArrayList<Integer> imageUrls)
        {

            mImageUrls = imageUrls;
            mContext = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position)
        {
            Log.d(TAG, "onBindViewHolder: called.");

            Glide.with(mContext)
                    .asBitmap()
                    .load(mImageUrls.get(position))
                    .into(holder.image);

            //  holder.name.setText(mNames.get(position));

            holder.image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // Log.d(TAG, "onClick: clicked on an image: " + mNames.get(position));
                    prod_id = glassList.get(position).prod_id;
                    renderagain();
                    mMainView.renderer().selectMask(position);
                    Toast.makeText(mContext, Integer.toString(position) , Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return mImageUrls.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {

            CircleImageView image;


            public ViewHolder(View itemView)
            {
                super(itemView);
                image = itemView.findViewById(R.id.image_view);
            }
        }
    }

}
