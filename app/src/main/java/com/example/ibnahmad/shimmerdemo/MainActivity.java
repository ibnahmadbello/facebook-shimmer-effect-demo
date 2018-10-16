package com.example.ibnahmad.shimmerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private List<Recipe> cartList;
    private RecipeListAdapter mAdapter;
    MyApplication application;
    private ShimmerFrameLayout mShimmerViewContainer;

    // URL to fetch menu json
    // this endpoint takes 2 sec before giving the response to add
    // some delay to test the Shimmer effect
    private static final String URL = "https://api.androidhive.info/json/shimmer/menu.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        mRecyclerView = findViewById(R.id.recycler_view);
        cartList = new ArrayList<>();
        mAdapter = new RecipeListAdapter(this, cartList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        application = MyApplication.getInstance();

        // making http call and fetching menu json
        fetchRecipes();
    }

    /**
     * method make volley network call and parses json
     */
    private void fetchRecipes(){
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Please try again", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Recipe> recipes = new Gson().fromJson(response.toString(), new TypeToken<List<Recipe>>() {

                }.getType());

                cartList.clear();
                cartList.addAll(recipes);

                mAdapter.notifyDataSetChanged();

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        try {
            application.addToRequestQueue(request);
        } catch (Exception e){
            Log.e(TAG, "Error message: " + e.getMessage());
        }

//        MyApplication.getInstance().addToRequestQueue(request);
//        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}
