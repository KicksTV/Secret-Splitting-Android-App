package com.example.secretsharing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Login extends AppCompatActivity {

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;

    private static final String TAG = "Login";

    FirebaseAuth mAuth;
    GoogleSignInClient client;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            // already signed in
            startActivity(new Intent(Login.this, HomeScreen.class).putExtra("account", account.getDisplayName()));
            finish();
        } else {
            setContentView(R.layout.initial_setup);

            // not signed in

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            client = GoogleSignIn.getClient(this, gso);

            SignInButton signin = (SignInButton)findViewById(R.id.sign_in_button);

            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn(client);
                }
            });

        }
    }
    private void signIn(GoogleSignInClient c) {
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

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "signin success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            }else {
                Log.d("TAG", "signin failure", task.getException());
                Toast.makeText(this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getBaseContext(), HomeScreen.class);
            intent.putExtra("account", user.getDisplayName());
            startActivity(intent);
        }else {


        }

    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        client.signOut().addOnCompleteListener(this, task -> {
            updateUI(null);
        });

    }
}
