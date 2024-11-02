package com.example.mangareader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

public class MangaReadingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MangaImageAdapter mangaImageAdapter;
    public class CustomImageModel {
        private String url;

        public CustomImageModel(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
//    public class CustomImageModelLoader implements ModelLoader<CustomImageModel, InputStream> {
//        private Context context;
//
//        public CustomImageModelLoader(Context context) {
//            this.context = context;
//        }
//        @Override
//        public LoadData<InputStream> buildLoadData(CustomImageModel model, int width, int height, Options options) {
//            // Create a GlideUrl with the required headers
//            GlideUrl glideUrl = new GlideUrl(model.getUrl(), new LazyHeaders.Builder()
//                    .addHeader("authority", "v12.com")
//                    .addHeader("method", "GET")
//                    .addHeader("path", "/img/tab_32/05/00/60/yi1001417/chapter_16/13-1722560353-o.jpg")
//                    .addHeader("scheme", "https")
//                    .addHeader("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
//                    .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
//                    .addHeader("Accept-Language", "en-US,en;q=0.9,hi;q=0.8")
//                    .addHeader("Cache-Control", "no-cache")
//                    .addHeader("Pragma", "no-cache")
//                    .addHeader("Priority", "i")
//                    .addHeader("Referer", "https://chapmanganato.to/")
//                    .addHeader("Sec-Ch-Ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
//                    .addHeader("Sec-Ch-Ua-Mobile", "20")
//                    .addHeader("Sec-Ch-Ua-Platform", "Windows")
//                    .addHeader("Sec-Fetch-Dest", "image")
//                    .addHeader("Sec-Fetch-Mode", "no-cors")
//                    .addHeader("Sec-Fetch-Site", "cross-site")
//                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6446.75 Safari/537.36")
//                    .build());
//
//            return new LoadData<>(glideUrl, new DataFetcher<InputStream>() {
//                @Override
//                public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
//                    // Use Glide to fetch the image
//                    Glide.with(context)
//                            .asBitmap()
//                            .load(glideUrl)
//                            .into(new CustomI(callback));
//                }
//
//                @Override
//                public void cleanup() {}
//
//                @Override
//                public void cancel() {}
//
//                @Override
//                public Class<InputStream> getDataClass() {
//                    return InputStream.class;
//                }
//
//                @Override
//                public DataSource getDataSource() {
//                    return DataSource.REMOTE;
//                }
//            });
//        }
//
//        @Override
//        public boolean handles(CustomImageModel model) {
//            return true; // Indicate that this loader can handle CustomImageModel
//        }
//    }



    public class MangaImageAdapter extends RecyclerView.Adapter<MangaImageAdapter.ViewHolder> {

        private List<String> imageUrls;
        private Context context;

        public MangaImageAdapter(Context context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chapter_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String imageUrl = imageUrls.get(position);
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.mangaImageView);
        }

        @Override
        public int getItemCount() {
            return imageUrls.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mangaImageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mangaImageView = itemView.findViewById(R.id.fullscreenImageView);
            }
        }
    }

    private class FetchChapterInformation extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... Chapterurl) {
            HttpRes httpRes = new HttpRes();
            try {
                Log.d("ChapterReader", "GettingChapter: " + Chapterurl[0]);
                return httpRes.getImageHtml(Chapterurl[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null) {
                // Update your UI with the fetched data
                mangaImageAdapter = new MangaImageAdapter(MangaReadingActivity.this, result);
                recyclerView.setAdapter(mangaImageAdapter);
                Log.d("ChapterReader", "Fetched images: " + result);
                // For example, update a RecyclerView or ImageView with the result
            } else {
                Toast.makeText(MangaReadingActivity.this, "Failed to fetch chapter information", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manga_reading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//         Register the custom request handler with Picasso


        String chapterUrl = getIntent().getStringExtra("ChapterUrl");
        if (chapterUrl != null){
            new FetchChapterInformation().execute(chapterUrl); // Execute AsyncTask only if URL is not nul

        }else {
            Toast.makeText(MangaReadingActivity.this, "Failed to fetch chapter information", Toast.LENGTH_SHORT).show();
        }

    }
}
