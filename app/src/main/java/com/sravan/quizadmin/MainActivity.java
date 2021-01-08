package com.sravan.quizadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText email, pass;
    private Button login;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.loginB);

        firebaseAuth = firebaseAuth.getInstance();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(email.getText().toString().isEmpty()) {
                    email.setError("Enter Email ID");
                    return;
                }
                else
                {
                    email.setError(null);
                }

                if(pass.getText().toString().isEmpty()) {
                    pass.setError("Enter Password");
                    return;
                }
                else
                {
                    pass.setError(null);
                }

                firebaseLogin();


            }
        });

        if(firebaseAuth.getCurrentUser() != null)
        {
         //Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
            //startActivity(intent);
        }
    }

    private void firebaseLogin()
    {
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

                         Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                         startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}