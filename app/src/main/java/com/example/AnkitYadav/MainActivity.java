package com.example.AnkitYadav;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.memeshare.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    ImageView memeImageView;

    ProgressBar progressBar;

    String memeImageUrl;

    Button shareButton;

    Button nextButton;

    String nsfwValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar;
        actionBar = getSupportActionBar();

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#BD0101"));

        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);

        memeImageView = findViewById(R.id.memeImageView);

        progressBar = findViewById(R.id.progressBar);

        shareButton = findViewById(R.id.shareButton);

        nextButton = findViewById(R.id.nextButton);

        loadMeme();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey Checkout this Meme\n" + memeImageUrl);
                startActivity((Intent.createChooser(intent,"Share this meme using.....")));
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setting the ProgressBar to VISIBLE
                progressBar.setVisibility(View.VISIBLE);

                // Calling the loadMeme function to display a new meme on the screen
                loadMeme();
            }
        });

    }


    private void checkInternetConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            loadMeme();
        }
        else{
            progressBar.setVisibility(View.GONE);

            memeImageView.setVisibility(View.INVISIBLE);

            shareButton.setEnabled(false);

            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadMeme(){
        String MEME_URL = "https://meme-api.herokuapp.com/gimme";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MEME_URL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    memeImageView.setVisibility(View.VISIBLE);

                    shareButton.setEnabled(true);

                    memeImageUrl = response.getString("url");

                    nsfwValue = response.getString("nsfw");

                    Log.d("URL", memeImageUrl);

                    Glide.with(MainActivity.this).load(memeImageUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Setting the ProgressBar to Invisible/Gone when the meme image is ready to be displayed on the Screen
                            progressBar.setVisibility(View.GONE);
                            if (nsfwValue.equals("false")){

                            }
                            return false;
                        }
                    }).into(memeImageView);
                } catch (JSONException e) {
                    Log.d("Error", "Something Went Wrong While Fetching Data");

                    shareButton.setEnabled(false);

                    Toast.makeText(MainActivity.this, "Something Went Wrong While Fetching Data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkInternetConnection();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}