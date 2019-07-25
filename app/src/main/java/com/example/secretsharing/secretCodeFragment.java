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
                            updateUI(view, true);

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
                updateUI(view, false);
            }
        });

        return view;
    }


    public void updateUI(View view, boolean lock) {
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
//
//    private void getRemoteShare(String url) {
//        if (mAuth.getCurrentUser() != null) {
//            // already signed in
//            user = mAuth.getCurrentUser();
//
//            System.out.println(url);
//
//            storage = FirebaseStorage.getInstance();
//
//            StorageReference remoteShareLoc = storage.getReference();
//
//
//
//            remoteShareLoc.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri ur) {
//                    // Local temp file has been created
//                    Log.d(TAG, "getting remote share successful");
//                    System.out.println("Download file successful");
//                    System.out.println(ur.toString());
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });
//
//            BufferedReader br = null;
//            String strLine = "";

//            try {
//                br = new BufferedReader(new FileReader(file.getPath()));
//                while ((strLine = br.readLine()) != null) {
//                    System.out.println("file = " + strLine);
//                }
//            } catch (FileNotFoundException e) {
//                System.err.println("Unable to find the file: fileName");
//            } catch (IOException e) {
//                System.err.println("Unable to read the file: fileName");
//            }
//
//            return strLine;
//        }
//
//    }


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

            //displaying a progress dialog while upload is going on
//            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setTitle("Uploading");
//            progressDialog.show();


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

            //Log.i("seeThisUri", remoteShare);// This is the one you should store

            ref.child("shareURL").setValue(remoteShare);

//            storage = FirebaseStorage.getInstance();
//            StorageReference remoteShareLoc = storage.getReference().child("user/" + user.getUid() + "/share2.txt");

//            UploadTask uploadTask = remoteShareLoc.putBytes(share2);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    progressDialog.dismiss();
//
//                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "Uploading share failed!");
//                    exception.printStackTrace();
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    //calculating progress percentage
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//
//                    //displaying percentage in progress dialog
//                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
//                }
//            })
//            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//
//
//                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//                @Override
//                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
//                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//
//                    progressDialog.dismiss();
//
//                    Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
//
//                    Log.d(TAG, "Uploading share successful!");
//                    Log.d(TAG, "File Data: " + taskSnapshot.getMetadata().getContentType());
//                    Log.d(TAG, "File Data: " + taskSnapshot.getMetadata().getName());
//                    Log.d(TAG, "File Data: " + taskSnapshot.getMetadata().getPath());
//
//
//                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                        @Override
//                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if (!task.isSuccessful()) {
//                                Log.i("problem", task.getException().toString());
//                            }
//
//                            return remoteShareLoc.getDownloadUrl();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if (task.isSuccessful()) {
//                                Uri downloadUri = task.getResult();
//
//
//
//
//                            } else {
//                                Log.i("wentWrong", "downloadUri failure");
//                            }
//                        }
//                    });
//                }
//            });
        }
    }
    private void showSecret(String s) {
        final TextView output = (TextView)view.findViewById(R.id.txt_shareOutput);
        output.setText(s);
    }
}
