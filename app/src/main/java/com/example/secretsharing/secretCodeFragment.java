package com.example.secretsharing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class secretCodeFragment extends Fragment {

    private static final String TAG = "SecretCodeFragment";
    String MyPREFERENCES = "MyPrefs" ;
    private SharedPreferences sharedpreferences;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String localShare = "";
    private View view;

    private EditText secretInput;
    private TextView output;
    private Spinner mspin;
    private ImageView lockIcon;
    private TextView message;
    private Button btn_newShare;
    private ProgressDialog progressDialog;
    private boolean doesSecretExist;
    private Secret secret;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.secretcode_fragment, container,false);

        // Getting activity components
        secretInput = view.findViewById(R.id.inp_secretCode);
        output = view.findViewById(R.id.txt_shareOutput);
        mspin = view.findViewById(R.id.spn_numberOfShares);
        lockIcon = view.findViewById(R.id.img_lock);
        message = view.findViewById(R.id.txt_message);
        btn_newShare = view.findViewById(R.id.btn_newShare);

        secret = new Secret();

        // Setting up spinner
        Integer[] items = new Integer[]{2};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, items);
        mspin.setAdapter(adapter);



        mAuth = FirebaseAuth.getInstance();

        if (doesShareExist()) {
            System.out.println("Checking for existing share");
            Picasso.get().load(R.drawable.ic_lock_png).into(lockIcon, new Callback() {

                @Override
                public void onSuccess() {
                    //System.out.println("Lock image loaded successfully");
                }

                @Override
                public void onError(Exception e) {
                    //System.out.println("Lock image loaded unsuccessfully");
                    e.printStackTrace();
                }
            });
            lockIcon.setTag(R.drawable.ic_lock_png);
            doesSecretExist = true;
            updateUI( true);
        }else {
            lockIcon.setTag(R.drawable.ic_unlock_png);
            doesSecretExist = false;
        }


        lockIcon.setOnClickListener(v -> {
            System.out.println("button press event");
//            System.out.println("LockIcon: " + lockIcon.getTag());
//            System.out.println("image: " + R.drawable.ic_unlock_png);

            if ((Integer)lockIcon.getTag() == R.drawable.ic_unlock_png) {
                System.out.println("unlock image clicked");
                if (secretInput.getText().toString().length() > 0) {
                    System.out.println("secret has been inputted");
                    if (secretInput.getText().toString().matches("[a-zA-Z0-9]*")) {
                        System.out.println("secret matches regex");
                        ArrayList<String> result = new ArrayList<>();
                        result = secret.hideSecret(secretInput.getText().toString());
                        if (result != null) {

                            //System.out.println("result does not return null");

                            // Sets the textView to the local share
                            // output.setText(result.get(0));

                            // Changes the ui
                            updateUI(true);

                            // Stores the local share using Shared Preference
                            storeLocalShare(result.get(0));
                            storeRemoteShare(result.get(1));
                            doesSecretExist = true;
                        }
                    }else {
                        Toast.makeText(getActivity(), "Please remove special characters!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (doesSecretExist == true) {
                    System.out.println("Secret Exists");
                    updateUI( true);
                }else {
                    Toast.makeText(getActivity(), "Field cannot be left empty!", Toast.LENGTH_SHORT).show();
                }

            }else {
                System.out.println("unlocking secret");
                updateUI(false);
            }
        });


        btn_newShare.setOnClickListener(view -> {
            doesSecretExist = false;
            restartUI();
        });



        return view;
    }


    public void updateUI(boolean lock) {

        if (lock) {
            secretInput.setText("");
            secretInput.setVisibility(View.INVISIBLE);
            secretInput.setEnabled(false);
            mspin.setEnabled(false);
            mspin.setVisibility(View.INVISIBLE);
            message.setText("Secret has been locked!");
            btn_newShare.setVisibility(View.INVISIBLE);
            output.setText("");
            output.setVisibility(View.INVISIBLE);

            Picasso.get().load(R.drawable.ic_lock_png).into(lockIcon, new Callback() {

                @Override
                public void onSuccess() {
                    //System.out.println("Lock image loaded successfully");
                }

                @Override
                public void onError(Exception e) {
                    //System.out.println("Lock image loaded unsuccessfully");
                    e.printStackTrace();
                }
            });
            lockIcon.setTag(R.drawable.ic_lock_png);

        }else {
            btn_newShare.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.ic_unlock_png).into(lockIcon, new Callback() {

                @Override
                public void onSuccess() {
                    //System.out.println("Lock image loaded successfully");
                }

                @Override
                public void onError(Exception e) {
                    //System.out.println("Lock image loaded unsuccessfully");
                    e.printStackTrace();
                }
            });
            lockIcon.setTag(R.drawable.ic_unlock_png);

            message.setText("Secret has been revealed");
            if (getLocalShare() != null) {
                localShare = getLocalShare();
            }else {
                Log.d(TAG, "getLocalShare returned null");
            }

            output.setVisibility(View.VISIBLE);

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Retrieving Secret");
            progressDialog.show();

            getRemoteShare();

        }
    }

    public void restartUI() {
        message.setText("Enter a Secret..");
        output.setVisibility(View.INVISIBLE);
        secretInput.setVisibility(View.VISIBLE);
        mspin.setVisibility(View.VISIBLE);
        secretInput.setEnabled(true);
        mspin.setEnabled(true);

        Picasso.get().load(R.drawable.ic_unlock_png).into(lockIcon, new Callback() {

            @Override
            public void onSuccess() {
                //System.out.println("Lock image loaded successfully");
            }

            @Override
            public void onError(Exception e) {
                //System.out.println("Lock image loaded unsuccessfully");
                e.printStackTrace();
            }
        });

    }
    public boolean doesShareExist() {
        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String share1 = sharedpreferences.getString("share1", "none");

        // Checking if share exists
        if (!share1.equals("none")) {
            return true;
        }
        return false;
    }

    private String getLocalShare() {
        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String share1 = sharedpreferences.getString("share1", "none");

        return share1;
    }

    private void getRemoteShare() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

        final String[] result = new String[1];

        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI


                for (DataSnapshot sp : dataSnapshot.getChildren()) {
                    final String share = sp.getValue(String.class);
//                    System.out.println(share);

                    result[0] = share;
                }
                System.out.println("getRemoteShare");
                String s = secret.recombindSecret(localShare, result[0]);

                showSecret(s);
                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        ref.addListenerForSingleValueEvent(postListener);
    }
<<<<<<< HEAD

=======
>>>>>>> a5e31524ee7922ee45f21e793ad918c80752df0e


    public void storeLocalShare(String localShare) {
        System.out.println("Storing Local Share");

        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("share1", localShare);
        editor.commit();

        System.out.println(sharedpreferences.getString("share1", "none"));
    }

    public void storeRemoteShare(String remoteShare) {

        System.out.println("Storing Remote Share");
        System.out.println(remoteShare);

        // Checks if user is signed in
        if (mAuth.getCurrentUser() != null) {
            // already signed in
            user = mAuth.getCurrentUser();

<<<<<<< HEAD


=======
>>>>>>> a5e31524ee7922ee45f21e793ad918c80752df0e
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

            //Log.i("seeThisUri", remoteShare);// This is the one you should store

<<<<<<< HEAD
            ref.child("shareURL").setValue(remoteShare);

=======
            ref.child("shareURL").setValue(remoteShare)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //System.out.println("Remote Share Stored Successful");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //System.out.println("Remote Share Stored Unsuccessful");
                }
            });
>>>>>>> a5e31524ee7922ee45f21e793ad918c80752df0e
        }
    }
    private void showSecret(String s) {
        output.setText(s);
    }
}
