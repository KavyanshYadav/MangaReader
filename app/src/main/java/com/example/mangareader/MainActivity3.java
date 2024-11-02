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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize adapter with empty list
        adapter = new ImageAdapter(this, new ArrayList<>(),new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Fetch chapters (including image URLs)
        new FetchChaptersTask().execute("https://manganato.com/");

    }
}
