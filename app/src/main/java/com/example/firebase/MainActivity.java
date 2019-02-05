package com.example.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public static final String EMAIL = "email";
    public static final String USERS ="users";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth firebaseAuth;
    CallbackManager callbackManager;
    EditText etEmail, etPassword;
    TextView tvLogInResult;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();//Firebase
        callbackManager = CallbackManager.Factory.create();//Facebook

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();



        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvLogInResult = findViewById(R.id.tvLogInResult);
        loginButton = findViewById(R.id.login_button);

        // Firebase DB
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext()){
                    String user = iterator.next().getValue().toString();
                    User use = new Gson().fromJson(user, User.class);
                    Log.d("TAG", "onDataChange: " + use.getUserEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        saveUserToFirebaseDB(new User("hmmm", "email.email.com"));
    }

    private void saveUserToFirebaseDB(User user){
        String key = user.getKey() != null ? user.getKey() : myRef.getKey();
        user.setKey(key);
        myRef.child(key).setValue(user);
        myRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "onDataChange: " + dataSnapshot.getKey() +  " = " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            tvLogInResult.setText("USER: " + user.getEmail() + " is logged IN");
        } else {
            tvLogInResult.setText("USER: " + etEmail.getText().toString() + " FAILED TO LOGIN");
        }
    }

    public void onClick(View view) {
        String email = etEmail.getText() != null ? etEmail.getText().toString() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        switch (view.getId()){
            case R.id.btnSignIn:
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithEmail:success");
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    updateUI(null);
                                }
                            }
                        });
                break;
            case R.id.btnSignUp:
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    updateUI(null);
                                }
                            }
                        });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
