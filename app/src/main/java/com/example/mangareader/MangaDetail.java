package com.example.mangareader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MangaDetail extends AppCompatActivity {
    private RecyclerView recyclerViewChapters;
    private ChapterViewAdapter chapterViewAdapter;
    private List<MangaKakrotScraper.ChapterInfo> chapterss = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);

        // Apply window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView and Adapter
        recyclerViewChapters = findViewById(R.id.recyclerView);
        recyclerViewChapters.setLayoutManager(new LinearLayoutManager(this));
        chapterViewAdapter = new ChapterViewAdapter(this, chapterss);
        recyclerViewChapters.setAdapter(chapterViewAdapter);

        // Load and display the manga cover image
        ImageView imageView = findViewById(R.id.imageView);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String imageId = getIntent().getStringExtra("imageID");
        Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(imageView);

        // Fetch chapter information using AsyncTask
        new FetchChapterInformation().execute(imageId);
    }

    ObjectMapper JSON = new ObjectMapper();

    // Fetch data asynchronously
    private class FetchChapterInformation extends AsyncTask<String, Void, MangaKakrotScraper.MoreinformationSchema> {
        @Override
        protected MangaKakrotScraper.MoreinformationSchema doInBackground(String... MangaID) {
            HttpRes httpRes = new HttpRes();
            try {
                Log.d("MangaReader1", "GetMangaId: " + MangaID[0]);
                return httpRes.getMangaDetails(MangaID[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MangaKakrotScraper.MoreinformationSchema result) {
            TextView MangaName = findViewById(R.id.MangaName);
            if (result != null) {
                MangaName.setText(result.Title);

                // Update the adapter’s data and refresh RecyclerView
                chapterViewAdapter.SetChapter(result.chapters);
                chapterViewAdapter.notifyDataSetChanged();

                try {
                    Log.d("MangaReader1", "resultID: " + JSON.writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.d("MangaReader1", "No recent chapters found or failed to fetch.");
            }
        }
    }

    // Adapter class for RecyclerView
    public class ChapterViewAdapter extends RecyclerView.Adapter<ChapterViewAdapter.ViewHolder> {
        private List<MangaKakrotScraper.ChapterInfo> chapters;
        private Context context;

        public ChapterViewAdapter(Context context, List<MangaKakrotScraper.ChapterInfo> chapters) {
            this.context = context;
            this.chapters = chapters;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
            return new ViewHolder(view);
        }

        public void SetChapter(List<MangaKakrotScraper.ChapterInfo> chapters) {
            this.chapters = chapters;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MangaKakrotScraper.ChapterInfo chapter = chapters.get(position);
            holder.chapterTitle.setText(chapter.ChapterTitle);
            holder.chapterTime.setText(chapter.Time);
            holder.chapterViews.setText("Views: " + chapter.View);

            holder.itemView.setOnClickListener(v -> {
                Log.d("MangaReader1", "ChapterUrl: " + chapter.href);
                Intent intent = new Intent(context, MangaReadingActivity.class);
                intent.putExtra("ChapterUrl", chapter.href);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return chapters.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView chapterTitle, chapterTime, chapterViews;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                chapterTitle = itemView.findViewById(R.id.chapterTitle);
                chapterTime = itemView.findViewById(R.id.chapterTime);
                chapterViews = itemView.findViewById(R.id.chapterViews);
            }
        }
    }
}
