package com.example.mangareader;// SeachItemFragment.java
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SeachItemFragment extends Fragment {
    private static final String ARG_IMAGE_URLS = "imageUrls";
    private static final String ARG_SEARCH_TEXT = "one piece";
    private HttpRes httpRes = new HttpRes();
    private ArrayList<String> imageUrls;
    public List<String> imageIDs;
    public   List<String> imageurl;
    private static String SearchText;
    private ImageAdapter imageApter ;

    private class FetchSearchmMangas extends AsyncTask<String, Void, ArrayList<MangaKakrotScraper.LessinformationSchema>> {
        @Override
        protected ArrayList<MangaKakrotScraper.LessinformationSchema> doInBackground(String... urls) {
            try {
                if (urls[0].isEmpty()) {
                    urls[0] = "one piece";
                }
                return httpRes.getSearch(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (InterruptedException e) {
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
                imageApter.updateImageUrls(imageUrls,imageID);
            }

        }
    }

    public static SeachItemFragment newInstance(String imageUrls) {
        SeachItemFragment fragment = new SeachItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TEXT, imageUrls);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrls = getArguments().getStringArrayList(ARG_IMAGE_URLS);
        }
        Log.d("name", "onCreate: "+getArguments().getString(ARG_SEARCH_TEXT));
        new FetchSearchmMangas().execute(getArguments().getString(ARG_SEARCH_TEXT));


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seach_item, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        imageApter = new ImageAdapter(getContext(),getActivity(), imageurl,imageIDs);
        recyclerView.setAdapter(imageApter);
        return view;
    }
}
