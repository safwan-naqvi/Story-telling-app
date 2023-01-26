package com.comsats.aistoryteller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class StoriesActivity extends AppCompatActivity {
    String type;
    RecyclerView myrecyclerview;
    ProgressDialog loadingdialogue;
    StoriesAdapter storiesAdapter;
    TextView title;

    FirebaseRecyclerOptions<StoriesModel> options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        type = getIntent().getStringExtra("type");
        myrecyclerview = (RecyclerView) findViewById(R.id.myrecycleview);
        myrecyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        title = findViewById(R.id.title);
        title.setText(type.toUpperCase(Locale.ROOT));
        loadingdialogue = new ProgressDialog(this);
        loadingdialogue.setTitle("Loading");
        loadingdialogue.setMessage("PLease wait while data is loading");
        loadingdialogue.setCanceledOnTouchOutside(false);
        loadingdialogue.show();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("stories");
        if (type.equals("all")) {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value = snapshot.getValue(String.class);
//                Log.e("TAG", "Value is: " + value);
                    loadingdialogue.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TAG", "Failed to read value.", error.toException());
                    loadingdialogue.dismiss();

                }
            });
            options =
                    new FirebaseRecyclerOptions.Builder<StoriesModel>()
                            .setQuery(myRef, StoriesModel.class)

                            .build();
        }
        else
        {

            Query query = myRef.orderByChild("story_type").equalTo(type);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value = snapshot.getValue(String.class);
//                Log.e("TAG", "Value is: " + value);
                    loadingdialogue.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TAG", "Failed to read value.", error.toException());
                    loadingdialogue.dismiss();

                }
            });
            options =
                    new FirebaseRecyclerOptions.Builder<StoriesModel>()
                            .setQuery(query, StoriesModel.class)

                            .build();
        }

        Log.e("TAG", "done");
        storiesAdapter = new StoriesAdapter(StoriesActivity.this, options);

        myrecyclerview.setAdapter(storiesAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        storiesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        loadingdialogue.dismiss();
        super.onStop();
        storiesAdapter.stopListening();
    }
}