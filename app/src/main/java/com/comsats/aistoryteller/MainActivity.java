package com.comsats.aistoryteller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout open, btnall, funny, sci_fi, horror, adventure, fantasy, roman;
    DrawerLayout drawerLayout;
    androidx.appcompat.widget.Toolbar toolbar;
    private FirebaseAuth mAuth;
    NavigationView navigationView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        open = findViewById(R.id.btn_upload);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_view);
        btnall = findViewById(R.id.btn_all);
        toolbar = findViewById(R.id.toolbar);
        funny = findViewById(R.id.funny);
        sci_fi = findViewById(R.id.sci_fi);
        horror = findViewById(R.id.horror);
        adventure = findViewById(R.id.adventure);
        fantasy = findViewById(R.id.fantasy);
        roman = findViewById(R.id.roman);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.setVisibility(View.VISIBLE);

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        btnall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, StoriesActivity.class).putExtra("type", "all"));

                // PyObject pyob = pyobj.callAttr("main", "My Name is Arsalan");


            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TextExtractor.class));
            }
        });
        roman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StoriesActivity.class);
                intent.putExtra("type", "roman");
                Toast.makeText(MainActivity.this, "Roman", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        funny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StoriesActivity.class);
                intent.putExtra("type", "funny");
                Toast.makeText(MainActivity.this, "Funny", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        sci_fi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StoriesActivity.class);
                intent.putExtra("type", "Sci-Fi");
                Toast.makeText(MainActivity.this, "Sci-fi", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        adventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StoriesActivity.class);
                intent.putExtra("type", "adventure");
                Toast.makeText(MainActivity.this, "adventure", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        fantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StoriesActivity.class);
                intent.putExtra("type", "fantasy");
              //  Toast.makeText(MainActivity.this, "Fantasy", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        horror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StoriesActivity.class);
                intent.putExtra("type", "horror");
                Toast.makeText(MainActivity.this, "horror", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.recomendation:
                Intent i1 = new Intent(this, Recomended.class);
                startActivity(i1);
                break;
            case R.id.all_stories:
                Intent i3 = new Intent(this, StoriesActivity.class);
                i3.putExtra("type", "all");
                startActivity(i3);
                break;
            case R.id.logout:
//                if (! Python.isStarted()) {
//                    Python.start(new AndroidPlatform(MainActivity.this));
//                }
//                callpython();
                /*Intent intent = new Intent(this, RegisterationActivity.class);
                startActivity(intent);*/
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.Languages:
                Intent intent1 = new Intent(this, AmazonPolly.class);
                startActivity(intent1);
                break;

        }
        return true;
    }

    private void callpython() {
        Python py = Python.getInstance();
        PyObject pyFile = py.getModule("predict");
        PyObject obj=pyFile.callAttr("");
       // return pyFile.callAttr("forecast", location, type, marla).toJava(float[].class);


    }

}