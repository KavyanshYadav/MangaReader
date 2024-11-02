package com.example.mangareader;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {





    private List<String> imageUrls;
    private Context context;
    private List<String> imageID;

    public ImageAdapter(Context context, List<String> imageUrls,List<String> imageIDs) {
        this.context = context;
        this.imageUrls = new ArrayList<>(imageUrls);
        this.imageID = new ArrayList<>(imageIDs);
    }

    public void updateImageUrls(List<String> newImageUrls,List<String> newImageID) {
        this.imageUrls.clear();
        this.imageUrls.addAll(newImageUrls);
        this.imageID.clear();
        this.imageID.addAll(newImageID);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        String currentImageID = imageID.get(position);  // Renamed to avoid conflict

        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(holder.imageView);

        // Set up the click listener with shared element transition
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MangaDetail.class);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("imageID", currentImageID);  // Pass image ID
            // Pass image URL

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    (MainActivity3) context,  // Cast to MainActivity3 if necessary
                    holder.imageView,
                    "shared"
            );
            context.startActivity(intent, options.toBundle());
        });
    }


    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
        }
    }
}
