package com.example.mangareader;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class MainActivity3 extends AppCompatActivity {

    private FirebaseAuth auth;
    private HttpRes httpRes = new HttpRes();
    private ImageAdapter adapter;
    private RecyclerView recyclerView;

    private void goback() {
        Intent intent = new Intent(MainActivity3.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class FetchChaptersTask extends AsyncTask<String, Void, ArrayList<MangaKakrotScraper.LessinformationSchema>> {
        @Override
        protected ArrayList<MangaKakrotScraper.LessinformationSchema> doInBackground(String... urls) {
            HttpRes httpRes = new HttpRes();
            try {
                return httpRes.getRecentChapter(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MangaKakrotScraper.LessinformationSchema> result) {
            if (result != null && !result.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                List<String> imageID = new ArrayList<>();

                for (MangaKakrotScraper.LessinformationSchema schema : result) {
                    imageUrls.add(schema.ImageUrl);
                    imageID.add(schema.ID);
                    // assuming `getImageUrl()` exists
                }

                // Update the RecyclerView adapter with the new URLs

                adapter.updateImageUrls(imageUrls,imageID);
            } else {
                Log.d("MangaReader1", "No recent chapters found or failed to fetch.");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            goback();
            return;
        } else {
            Toast.makeText(this, "Logged in as " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_main3);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new homescreen())
                    .commit();
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_home) {
                selectedFragment = new homescreen();
            } else if (id==R.id.nav_search) {
                selectedFragment = new Profile();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
        Button SearchBtn = findViewById(R.id.SearchBtn);
        TextView SearchText = findViewById(R.id.SearchText);
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = SearchText.getText().toString();
                if (!searchText.isEmpty()) {
                    Intent goToSearch = new Intent(MainActivity3.this,SearchActivity.class);
                    goToSearch.putExtra("searchText",searchText);
                    startActivity(goToSearch);
                }else {
                    Toast.makeText(MainActivity3.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                }
//                Intent intent = new Intent(MainActivity3.this, SearchActivity.class);
//                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize adapter with empty list
        adapter = new ImageAdapter(this, this, new ArrayList<>(),new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the LayoutManager is a LinearLayoutManager (or GridLayoutManager, which extends it)
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    // Get the total number of items in the RecyclerView
                    int totalItemCount = layoutManager.getItemCount();

                    // Get the position of the first and last visible items
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    // Calculate the middle position of the currently visible items
                    int middlePosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2;

                    // Calculate the percentage of the user's scroll progress
                    float scrollPercentage = ((float) middlePosition / totalItemCount) * 100;

                    // Log or use the scroll percentage as needed
                    Log.d("ScrollPercentage", "User is viewing " + scrollPercentage + "% of the list");
                }
            }
        });


        // Fetch chapters (including image URLs)
        new FetchChaptersTask().execute("https://manganato.com/");

    }
}
