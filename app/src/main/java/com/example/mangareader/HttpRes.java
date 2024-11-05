package com.example.mangareader;


import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Headers;

public class HttpRes {

    OkHttpClient client = new OkHttpClient();
    MangaKakrotScraper scraper = new MangaKakrotScraper();

    public String getHtml(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }

    public ArrayList<MangaKakrotScraper.LessinformationSchema> getRecentChapter(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();



        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Ensure a non-null return
            ArrayList<MangaKakrotScraper.LessinformationSchema> result = scraper.GetRecentChapters(response.body().string());
            if (result == null) {

            }
            return result != null ? result : new ArrayList<>();
        }catch (Exception e) {
            throw new IOException("Unexpected code " + e);
        }
    }

    public MangaKakrotScraper.MoreinformationSchema getMangaDetails(String MangaID) throws  IOException {
        Log.d("Scraper", "GettingManga: " + MangaID);
        Log.d("Scraper", "GettingManga: " + "https://manganato.com/manga-"+MangaID);
//        Request request = new Request.Builder()
//                .url("https://manganato.com/manga-"+MangaID)
//                .get()
//                .build();
        Request request = new Request.Builder()
                .url("https://chapmanganato.to/manga-"+MangaID)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Ensure a non-null return
           MangaKakrotScraper.MoreinformationSchema result = scraper.GetMangaInfo(response.body().string());
//           if (result ==null){
//               Log.d("Scraper", "Scrapping from Site 2");
//               Response response2 = client.newCall(request2).execute();
//               if (!response2.isSuccessful()) throw new IOException("Unexpected code " + response2);
//               result = scraper.GetMangaInfo(response2.body().string());
//           }
           Log.d("MangaReader1", "Result: " + result);
            return result;
        }catch (Exception e) {
            Log.d("MangaReader1", "failed to fetch ");

            throw new IOException("Unexpected code " + e);
        }
    }

    public ArrayList<String> getImageHtml(String ChapterUrl) throws IOException{
        Request request = new Request.Builder()
                .url(ChapterUrl)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            // Ensure a non-null return
            ArrayList<String> result = scraper.GetChapterImages(response.body().string());
//            Log.d("MangaReader1", "Result: " + result);
            return result ;
        }catch (Exception e) {
//            Log.d("MangaReader1", "failed to fetch ");

            throw new IOException("Unexpected code " + e);
        }
    }

    public ArrayList<MangaKakrotScraper.LessinformationSchema> getSearch(String Search) throws IOException, InterruptedException {
        String encodedSearch = Search.replace(" ", "_");
        System.out.println(encodedSearch);
        Log.d("MangaReader1","https://manganato.com/search/story/" + encodedSearch);
        Request request = new Request.Builder()
                .url("https://manganato.com/search/story/" + encodedSearch)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            // Ensure a non-null return
            ArrayList<MangaKakrotScraper.LessinformationSchema> retu = scraper.SearchManga(response.body().string());
            Log.d("MangaReader1", "Result: " + retu.get(0).Title);
            return retu ;
        }catch (Exception e) {
//            Log.d("MangaReader1", "failed to fetch ");

            throw new IOException("Unexpected code " + e);
        }

    }


    public byte[] getImage(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(new Headers.Builder()
                        .add("authority", "v12.com")
                        .add("method", "GET")
                        .add("path", "/img/tab_32/05/00/60/yi1001417/chapter_16/13-1722560353-o.jpg")
                        .add("scheme", "https")
                        .add("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                        .add("Accept-Encoding", "gzip, deflate, br, zstd")
                        .add("Accept-Language", "en-US,en;q=0.9,hi;q=0.8")
                        .add("Cache-Control", "no-cache")
                        .add("Pragma", "no-cache")
                        .add("Priority", "i")
                        .add("Referer", "https://chapmanganato.to/")
                        .add("Sec-Ch-Ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                        .add("Sec-Ch-Ua-Mobile", "20")
                        .add("Sec-Ch-Ua-Platform", "Windows")
                        .add("Sec-Fetch-Dest", "image")
                        .add("Sec-Fetch-Mode", "no-cors")
                        .add("Sec-Fetch-Site", "cross-site")
                        .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6446.75 Safari/537.36")
                        .build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().bytes();
        }
    }
}
