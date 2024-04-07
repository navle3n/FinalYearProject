package com.example.fyp;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SharedImagesAdapter extends RecyclerView.Adapter<SharedImagesAdapter.SharedImageViewHolder> {

    private List<SharedImage> sharedImages = new ArrayList<>();

    public void setSharedImages(List<SharedImage> sharedImages) {
        this.sharedImages = sharedImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SharedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shared_image, parent, false);
        return new SharedImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedImageViewHolder holder, int position) {
        SharedImage sharedImage = sharedImages.get(position);
        Picasso.get().load(sharedImage.getImageUrl()).into(holder.imageView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(Long.parseLong(sharedImage.getTimestamp())));
        holder.timestampTextView.setText(String.format("When: %s", formattedDate));


        holder.downloadButton.setOnClickListener(view -> {
            DownloadManager downloadManager = (DownloadManager) holder.itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(sharedImage.getImageUrl());
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setTitle("Download Image")
                    .setDescription("Downloading " + sharedImage.getImageUrl())
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(sharedImage.getImageUrl()).getLastPathSegment());

            downloadManager.enqueue(request);
        });

    }

    @Override
    public int getItemCount() {
        return sharedImages.size();
    }

    static class SharedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView fromUserTextView;
        TextView timestampTextView;
        Button downloadButton;

        public SharedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.shared_image_view);
            fromUserTextView = itemView.findViewById(R.id.from_user_textview);
            timestampTextView = itemView.findViewById(R.id.timestamp_textview);
            downloadButton = itemView.findViewById(R.id.download_image_btn);
        }
    }
    public void updateSharedImages(List<SharedImage> newSharedImages) {
        sharedImages.clear();
        sharedImages.addAll(newSharedImages);
        notifyDataSetChanged(); // Refresh the RecyclerView with the new data
    }

}
