package com.example.secretsharing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.secretcode_fragment, container,false);

        // Getting activity components
        final EditText secretCodeInput = (EditText)view.findViewById(R.id.secretCode);
        final TextView output = (TextView)view.findViewById(R.id.txt_message);
        final Spinner mspin= (Spinner)view.findViewById(R.id.numberOfShares);
        final ImageView lockIcon = (ImageView)view.findViewById(R.id.img_lock);



        // Setting up spinner
        Integer[] items = new Integer[]{2,3,4,5,6};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, items);
        mspin.setAdapter(adapter);



        mAuth = FirebaseAuth.getInstance();


//        if (doesShareExist()) {
//            Picasso.get().load(R.drawable.ic_lock_png).into(lockIcon, new Callback() {
//
//                @Override
//                public void onSuccess() {
//                    System.out.println("Lock image loaded successfully");
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    System.out.println("Lock image loaded unsuccessfully");
//                    e.printStackTrace();
//                }
//            });
//            updateUI(view, true);
//        }



        lockIcon.setOnClickListener(v -> {

            if (lockIcon.getDrawable().getConstantState() == getResources().getDrawable( R.drawable.ic_unlock_png).getConstantState()) {
                Secret s = new Secret();
                if (secretCodeInput.getText().toString().length() > 0) {
                    if (secretCodeInput.getText().toString().matches("[a-zA-Z0-9]*")) {
                        ArrayList<String> result = s.hideSecret(secretCodeInput.getText().toString());
                        if (result != null) {


                            // Changes imageView to locked padlock image
                            Picasso.get().load(R.drawable.ic_padlock_red).into(lockIcon);

                            // Sets the textView to the local share
                            output.setText(result.get(0));

                            // Changes the ui
                            updateUI(true);

                            // Stores the local share using Shared Preference
                            storeLocalShare(result.get(0));
                            storeRemoteShare(result.get(1));

                        }
                    }else {
                        Toast.makeText(getActivity(), "Please remove special characters!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Field cannot be left empty!", Toast.LENGTH_SHORT).show();
                }

            }else {
                updateUI(false);
            }
        });

        return view;
    }


    public void updateUI(boolean lock) {
        final EditText secretCodeInput = (EditText)view.findViewById(R.id.secretCode);
        final Spinner mspin = (Spinner)view.findViewById(R.id.numberOfShares);
        final ImageView lockIcon = (ImageView)view.findViewById(R.id.img_lock);
        final TextView message = view.findViewById(R.id.txt_message);
        final TextView output = (TextView)view.findViewById(R.id.txt_message);

        if (lock) {
            secretCodeInput.setText("");
            secretCodeInput.setVisibility(View.INVISIBLE);
            secretCodeInput.setEnabled(false);
            mspin.setEnabled(false);
            mspin.setVisibility(View.INVISIBLE);
            message.setText("Secret has been locked!");

            Picasso.get().load(R.drawable.ic_lock_png).into(lockIcon, new Callback() {

                @Override
                public void onSuccess() {
                    System.out.println("Lock image loaded successfully");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("Lock image loaded unsuccessfully");
                    e.printStackTrace();
                }
            });

        }else {

            Picasso.get().load(R.drawable.ic_unlock_png).into(lockIcon, new Callback() {

                @Override
                public void onSuccess() {
                    System.out.println("Lock image loaded successfully");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("Lock image loaded unsuccessfully");
                    e.printStackTrace();
                }
            });
            message.setText("Secret has been revealed");
            if (getLocalShare() != null) {
                localShare = getLocalShare();
            }else {
                Log.d(TAG, "getLocalShare returned null");
            }

            getRemoteShare();

        }
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

                Secret secret = new Secret();

                for (DataSnapshot sp : dataSnapshot.getChildren()) {
                    final String share = sp.getValue(String.class);
                    System.out.println(share);

                    result[0] = share;
                }

                String s = secret.recombindSecret(localShare, result[0]);

                showSecret(s);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        ref.addValueEventListener(postListener);
    }



    public void storeLocalShare(String localShare) {


        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("share1", localShare);
        editor.commit();
    }

    public void storeRemoteShare(String remoteShare) {
        // Gets the remote share and converts into byte array
        byte[] share2 = remoteShare.getBytes();

        // Checks if user is signed in
        if (mAuth.getCurrentUser() != null) {
            // already signed in
            user = mAuth.getCurrentUser();



            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

            //Log.i("seeThisUri", remoteShare);// This is the one you should store

            ref.child("shareURL").setValue(remoteShare);

        }
    }
    private void showSecret(String s) {
        final TextView output = (TextView)view.findViewById(R.id.txt_shareOutput);
        output.setText(s);
    }
}
