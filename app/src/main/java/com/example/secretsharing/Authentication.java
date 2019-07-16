package com.example.secretsharing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;


public class Authentication extends AppCompatActivity {

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;

    private static final String TAG = "Authentication";

    SignInButton signin;
    Button btn_logout, btn_continue;
    TextView userName;
    TextView userEmail;
    ImageView userImage;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    GoogleSignInClient client;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_screen);

        signin = findViewById(R.id.sign_in_button);
        btn_logout = findViewById(R.id.btn_logout);
        btn_continue = findViewById(R.id.btn_continue);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userImage = findViewById(R.id.userImage);
        progressBar = findViewById(R.id.progress_circular);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Authentication.this, HomeScreen.class));
                finish();
            }
        });

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            // already signed in

            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        } else {
            // not signed in

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            client = GoogleSignIn.getClient(this, gso);



            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn(client);
                }
            });

        }
    }
    private void signIn(GoogleSignInClient c) {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = c.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("TAG", "signin success");

                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("TAG", "signin failure", task.getException());
                    Toast.makeText(Authentication.this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String img = String.valueOf(user.getPhotoUrl());


            signin.setVisibility(View.INVISIBLE);
            btn_logout.setVisibility(View.VISIBLE);
            btn_continue.setVisibility(View.VISIBLE);
            userName.setText(name);
            userEmail.setText(email);
            userEmail.setVisibility(View.VISIBLE);
            Picasso.get().load(img).into(userImage);

        }else {
            btn_logout.setVisibility(View.INVISIBLE);
            btn_continue.setVisibility(View.INVISIBLE);
            userEmail.setVisibility(View.INVISIBLE);
            signin.setVisibility(View.VISIBLE);

            userName.setText("Google Login");
            Picasso.get().load(R.drawable.ic_google_logo).into(userImage);
        }
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        client.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Authentication.this.updateUI(null);
            }
        });

    }
    @Override
    public void onBackPressed() {
        return;
    }
}
