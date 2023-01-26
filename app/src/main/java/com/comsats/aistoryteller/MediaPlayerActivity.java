package com.comsats.aistoryteller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.github.ybq.android.spinkit.style.Wave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MediaPlayerActivity extends AppCompatActivity {
    String story_name, story_image, story_audio, story_id;
    ImageView story_image_view;
    Button btnplay, btnpause;
    MediaPlayer mediaPlayer;
    ProgressBar pBar;
    TextView story_Title, story_text;
    RecommendedStoryAdapter adapter;
    ArrayList<RecommendedStoryModel> model;
    RecyclerView recyclerView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_media_player);
        story_image = getIntent().getStringExtra("image");
        story_audio = getIntent().getStringExtra("story");
        story_id = getIntent().getStringExtra("key");
        story_name = getIntent().getStringExtra("name");
        //  Toast.makeText(this, "StoryImage" + story_image, Toast.LENGTH_LONG).show();
        recyclerView = findViewById(R.id.related_stories_rv);
        model = new ArrayList<>();

        model.add(new RecommendedStoryModel("CINDERALLA","https://firebasestorage.googleapis.com/v0/b/ai-story-teller-d46f0.appspot.com/o/story_banners%2Fg_cinderella1950_03_17805_4c9a7fe6_d92c24f2.webp?alt=media&token=f0d2575d-f44e-40b9-b24b-127c5bf1fbb6"));
        model.add(new RecommendedStoryModel("ELEPHANT AND FRIENDS","https://firebasestorage.googleapis.com/v0/b/ai-story-teller-d46f0.appspot.com/o/story_banners%2FCONTROLLING%20ANGER.jpg?alt=media&token=68538f4c-8950-4b5b-af89-76b83ee0138e"));
        model.add(new RecommendedStoryModel("SNOW WHITE AND THE SEVEN DWARFS STORY","https://firebasestorage.googleapis.com/v0/b/ai-story-teller-d46f0.appspot.com/o/story_banners%2FSNOW%20WHITE%20AND%20THE%20SEVEN%20DWARFS%20STORY.jpg?alt=media&token=b573027f-3d84-47a0-adc8-5c0337a036c2"));
        model.add(new RecommendedStoryModel("The Fox and the Grapes","https://firebasestorage.googleapis.com/v0/b/ai-story-teller.appspot.com/o/THE%20FOX%20AND%20THE%20GRAPES.jpg?alt=media&token=5a3b7f84-5e3b-44f6-bbcf-43d29124abb9"));

        adapter = new RecommendedStoryAdapter(this, model);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        story_image_view = findViewById(R.id.story_image);
        story_text = findViewById(R.id.story_text);

        story_text.setMovementMethod(new ScrollingMovementMethod());

        story_Title = findViewById(R.id.storyTitle);
        story_Title.setText(story_name.toUpperCase(Locale.ROOT));
        Log.e("onCreate: ", story_image);
        btnpause = findViewById(R.id.idBtnPause);
        btnplay = findViewById(R.id.idBtnPlay);
        pBar = findViewById(R.id.idProgreb);

        Sprite doubleBounce = new Wave();
        pBar.setIndeterminateDrawable(doubleBounce);
        btnplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                pBar.setVisibility(View.VISIBLE);
                btnplay.setVisibility(View.GONE);

                return false;
            }
        });
        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PLayAudio();
            }
        });
        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    // pausing the media player if media player
                    // is playing we are calling below line to
                    // stop our media player.
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();

                    // below line is to display a message
                    // when media player is paused.
                    btnplay.setVisibility(View.VISIBLE);
                    btnpause.setVisibility(View.GONE);
                    Toast.makeText(MediaPlayerActivity.this, "Audio has been paused", Toast.LENGTH_SHORT).show();
                } else {
                    // this method is called when media
                    // player is not playing.
                    Toast.makeText(MediaPlayerActivity.this, "Audio has not played", Toast.LENGTH_SHORT).show();
                }
            }

        });
        Glide.with(story_image_view.getContext()).load(story_image).placeholder(R.drawable.banner).dontAnimate().into(story_image_view);

    }

    private void PLayAudio() {
        pBar.setVisibility(View.VISIBLE);


        //     String audioUrl = "https://firebasestorage.googleapis.com/v0/b/ai-story-teller.appspot.com/o/audios%2FA%20glass%20of%20milk.mp3?alt=media&token=15d327ab-d81f-4d89-9866-fcc3d5d6c1f2";

        // initializing media player
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(story_audio);
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();
            pBar.setVisibility(View.GONE);


        } catch (IOException e) {
            Toast.makeText(this, "Audio Cannot be Played", Toast.LENGTH_SHORT).show();
            btnplay.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }
        btnpause.setVisibility(View.VISIBLE);
        // below line is use to display a toast message.
        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onStop() {
        mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {


        super.onPause();
    }

    public void ratestory(View view) {

        Intent intent = new Intent(MediaPlayerActivity.this, GiveFeedback.class);
        intent.putExtra("key", story_id);
        startActivity(intent);

    }
}