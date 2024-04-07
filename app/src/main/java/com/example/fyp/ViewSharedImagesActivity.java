package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewSharedImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SharedImagesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shared_images);

        recyclerView = findViewById(R.id.shared_images_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // SharedImagesAdapter is a custom adapter for displaying shared images
        adapter = new SharedImagesAdapter();
        recyclerView.setAdapter(adapter);

        loadSharedImages();
    }

    private void loadSharedImages() {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("shared_images")
                .orderByChild("toUser")
                .equalTo(currentUserEmail);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SharedImage> sharedImages = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SharedImage sharedImage = snapshot.getValue(SharedImage.class);
                    if (sharedImage != null) {
                        sharedImages.add(sharedImage);
                    }
                }

                if (sharedImages.isEmpty()) {
                    Toast.makeText(ViewSharedImagesActivity.this, "No images have been shared with you.", Toast.LENGTH_LONG).show();
                } else {
                    adapter.updateSharedImages(sharedImages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewSharedImagesActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

}
