package com.example.secretsharing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class secretCodeFragment extends Fragment {

    private static final String TAG = "SecretCodeFragment";
    String MyPREFERENCES = "MyPrefs" ;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.secretcode_fragment, container,false);

        // Getting activity components
        final EditText secretCodeInput = (EditText)view.findViewById(R.id.secretCode);
        final TextView output = (TextView)view.findViewById(R.id.shareOutput);
        final Spinner mspin= (Spinner)view.findViewById(R.id.numberOfShares);
        final ImageView lockIcon = (ImageView)view.findViewById(R.id.img_lock);

        // Setting up spinner
        Integer[] items = new Integer[]{2,3,4,5,6};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, items);
        mspin.setAdapter(adapter);


        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String share1 = sharedpreferences.getString("share1", "none");


        Picasso.get().load(R.drawable.ic_padlock).into(lockIcon, new Callback() {

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

        // Checking if share exists
        if (!share1.equals("none")) {

            toggleButton(view);
            output.setText(share1);
        }



        lockIcon.setOnClickListener(v -> {

            Secret s = new Secret();

            if (secretCodeInput.getText().toString().length() > 0) {
                if (secretCodeInput.getText().toString().matches("[a-zA-Z0-9]*")) {
                    ArrayList<String> result = s.hideSecret(secretCodeInput.getText().toString());
                    if (result != null) {

                        Picasso.get().load(R.drawable.ic_padlock_red).into(lockIcon);
                        output.setText(result.get(0));
                        toggleButton(view);
                        storeLocalShare(result.get(0));
                    }
                }else {
                    Toast.makeText(getActivity(), "Please remove special characters!", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getActivity(), "Field cannot be left empty!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void storeLocalShare(String localShare) {


        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("share1", localShare);
        editor.commit();
    }

    public void storeRemoteShare() {

    }

    public void toggleButton(View view) {
        final EditText secretCodeInput = (EditText)view.findViewById(R.id.secretCode);
        final Spinner mspin = (Spinner)view.findViewById(R.id.numberOfShares);


        secretCodeInput.setText("");
        secretCodeInput.setVisibility(View.INVISIBLE);
        secretCodeInput.setEnabled(false);
        mspin.setEnabled(false);
        mspin.setVisibility(View.INVISIBLE);
    }
}
