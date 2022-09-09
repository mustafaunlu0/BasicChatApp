package com.mustafaunlu.basicchatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mustafaunlu.basicchatapp.databinding.ActivitySignBinding;

public class SignActivity extends AppCompatActivity {

    private ActivitySignBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser user=mAuth.getCurrentUser();

        if(user != null){
            Intent intent=new Intent(SignActivity.this,ChatActivity.class);
            startActivity(intent);
        }




    }

    public void signUp(View view){

        mAuth.createUserWithEmailAndPassword(binding.emailEditText.getText().toString(),binding.passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(getApplicationContext(),"Sign Up",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(SignActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

            }
        });


    }

    public void signIn(View view){

        mAuth.signInWithEmailAndPassword(binding.emailEditText.getText().toString(),binding.passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(SignActivity.this,ChatActivity.class);
                    startActivity(intent);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding=null;
    }



}