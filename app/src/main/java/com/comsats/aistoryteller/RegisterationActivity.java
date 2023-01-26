package com.comsats.aistoryteller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterationActivity extends AppCompatActivity {
    private TextInputLayout email_ed3;
    private TextInputLayout pass_ed4;
    private Button register;
    private Button btn_login;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_register);
        email_ed3 = (TextInputLayout) findViewById(R.id.ed3);
        pass_ed4 = (TextInputLayout) findViewById(R.id.ed4);
        register = (Button) findViewById(R.id.btn_reg);
        btn_login = (Button) findViewById(R.id.btn_sign_act);
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_str = email_ed3.getEditText().getText().toString();
                String password = pass_ed4.getEditText().getText().toString();
                if (email_str.isEmpty() || password.isEmpty()) {

                    Toast.makeText(RegisterationActivity.this, "Your Fields are Empty", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email_str, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
                                        final HashMap<String, Object> UserMap = new HashMap<>();
                                        UserMap.put("user_id", FirebaseAuth.getInstance().getUid());

                                        UserMap.put("user_email", email_str);
                                        UserMap.put("type", "user");

                                        userRef.child(FirebaseAuth.getInstance().getUid())

                                                .updateChildren(UserMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(RegisterationActivity.this, "Registeraton Completed", Toast.LENGTH_SHORT).show();
                                                            Intent intent1 = new Intent(RegisterationActivity.this, LoginActivity.class);
                                                            startActivity(intent1);

                                                        } else {
                                                            Toast.makeText(RegisterationActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();


                                                        }

                                                    }
                                                });

                                    } else {
                                        Toast.makeText(RegisterationActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    // Toast.makeText(RegisterationActivity.this, "your email & password is :  \n" + email_str + "\n" + password, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}