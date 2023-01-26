package com.comsats.aistoryteller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Recomended extends AppCompatActivity {
    StoriesAdapter adapter;
    RecyclerView myrecyclerview;
    String slogin;
    String user;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_recomended);
        user = "";
        myrecyclerview = (RecyclerView) findViewById(R.id.myrecycleview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

// And set it to RecyclerView
        myrecyclerview.setLayoutManager(mLayoutManager);
        // myrecyclerview.setLayoutManager(new LinearLayoutManager(this).setReverseLayout(true));
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.show();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("stories");
        Query query = myRef.orderByChild("story_rating");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value = snapshot.getValue(String.class);
                //  Log.e("TAG", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Failed to read value.", error.toException());

            }
        });
        FirebaseRecyclerOptions<StoriesModel> options =
                new FirebaseRecyclerOptions.Builder<StoriesModel>()
                        .setQuery(query,StoriesModel.class)

                        .build();
        Log.e("TAG", "done");
        adapter = new StoriesAdapter(Recomended.this, options);

        myrecyclerview.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        dialog.dismiss();
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        dialog.dismiss();
        super.onStop();
        adapter.stopListening();
    }

}