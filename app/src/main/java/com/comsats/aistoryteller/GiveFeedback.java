package com.comsats.aistoryteller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GiveFeedback extends AppCompatActivity {

    EditText comment;
    RatingBar ratingb;
    Button submitbtn;
    Double oldrating, rating_s, average;

    String comment_s, key, username, userid;
    float getrating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_feedback);
        comment = findViewById(R.id.comment);
        ratingb = findViewById(R.id.m_rating);
        submitbtn = findViewById(R.id.submitbtn);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        key = getIntent().getStringExtra("key");
        DatabaseReference fbd = FirebaseDatabase.getInstance().getReference("users").child(userid);
        fbd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = (String) snapshot.child("user_email").getValue();
            //    Toast.makeText(GiveFeedback.this, "username" + username, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference fbdrating = FirebaseDatabase.getInstance().getReference("stories").child(key);
        fbdrating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                oldrating = (Double) snapshot.child("story_rating").getValue();
              //  Log.e( "onDataChange: ",key+"rating:"+ String.valueOf(oldrating));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e( "onDataChangeError: ", ""+error);

            }
        });

    }

    public void submitreview(View view) {
        comment_s = comment.getText().toString();
        getrating = ratingb.getRating();
        rating_s = Double.valueOf(getrating);
//        oldrating = (oldrating + rating_s) / 2;
        if (comment_s.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();


            return;
        }
     //   Toast.makeText(this, "Value" + comment_s + rating_s, Toast.LENGTH_SHORT).show();
        submittofirebase();

    }

    private void submittofirebase() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference root = db.getReference("review");
        reviewModel obj = new reviewModel(username, "", String.valueOf(getrating), comment_s);
        //  root.push().setValue(obj);
        root.child(key).child(userid).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                comment.setText("");
                ratingb.setRating(0.0F);
                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("stories");
                dbref.child(key).child("story_rating").setValue(rating_s);
              Toast.makeText(GiveFeedback.this, "Submitted"+oldrating, Toast.LENGTH_SHORT).show();
            }
        });
    }
}