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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class secretCodeFragment extends Fragment {

    private static final String TAG = "SecretCodeFragment";
    String MyPREFERENCES = "MyPrefs" ;
    private SharedPreferences sharedpreferences;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseUser user;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.secretcode_fragment, container,false);

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
            Secret secret = new Secret();

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
            String share1 = "";
            if (getLocalShare() != null) {
                share1 = getLocalShare();
            }else {
                Log.d(TAG, "getLocalShare returned null");
            }

            String share2 = getRemoteShare();

            secret.recombindSecret(share1, share2);
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

    private String getRemoteShare() {
        if (mAuth.getCurrentUser() != null) {
            // already signed in
            user = mAuth.getCurrentUser();
            storage = FirebaseStorage.getInstance();
            StorageReference remoteShareLoc = storage.getReference().child("user/" + user.getUid() + "/share2.txt");

            File file = new File.createTempFile(getContext().getFilesDir(), "share2");

            remoteShareLoc.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d(TAG, "getting remote share successful");
                    System.out.println("Download file successful");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d(TAG, "getting remote share unsuccessful");
                    exception.printStackTrace();
                }
            });

            BufferedReader br = null;
            String strLine = "";

            try {
                br = new BufferedReader(new FileReader(file.getPath()));
                while ((strLine = br.readLine()) != null) {
                    System.out.println("file = " + strLine);
                }
            } catch (FileNotFoundException e) {
                System.err.println("Unable to find the file: fileName");
            } catch (IOException e) {
                System.err.println("Unable to read the file: fileName");
            }

            return strLine;
        }
        return null;
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

            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            storage = FirebaseStorage.getInstance();
            StorageReference remoteShareLoc = storage.getReference().child("user/" + user.getUid() + "/share2.txt");

            UploadTask uploadTask = remoteShareLoc.putBytes(share2);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();

                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Uploading share failed!");
                    exception.printStackTrace();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    progressDialog.dismiss();

                    Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                    Log.d(TAG, "Uploading share successful!");
                    Log.d(TAG, "File Data: " + taskSnapshot.getMetadata().getContentType());
                    Log.d(TAG, "File Data: " + taskSnapshot.getMetadata().getName());
                    Log.d(TAG, "File Data: " + taskSnapshot.getMetadata().getPath());
                }
            });
        }
    }
}
