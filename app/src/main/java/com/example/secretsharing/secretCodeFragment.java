package com.example.secretsharing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class secretCodeFragment extends Fragment {

    private static final String TAG = "SecretCodeFragment";

    String MyPREFERENCES = "MyPrefs" ;

    SharedPreferences sharedpreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.secretcode_fragment, container,false);

        final EditText secretCodeInput = (EditText)view.findViewById(R.id.secretCode);
        final Button showButton = (Button)view.findViewById(R.id.showButton);
        final TextView output = (TextView)view.findViewById(R.id.shareOutput);
        final Button hideButton = (Button)view.findViewById(R.id.hideButton);

        final Spinner mspin = (Spinner)view.findViewById(R.id.numberOfShares);

        Integer[] items = new Integer[]{2,3,4,5,6};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, items);
        mspin.setAdapter(adapter);


        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String share1 = sharedpreferences.getString("share1", "none");

        if (!share1.equals("none")) {
            toggleButton(view);
            output.setText(share1);
        }


        showButton.setEnabled(false);

        hideButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Secret s = new Secret();

                if (secretCodeInput.getText().toString().length() > 0) {
                    if (secretCodeInput.getText().toString().matches("[a-zA-Z0-9]*")) {
                        ArrayList<String> result = s.hideSecret(secretCodeInput.getText().toString());
                        if (result != null) {
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
        final Button showButton = (Button)view.findViewById(R.id.showButton);
        final Button hideButton = (Button)view.findViewById(R.id.hideButton);
        final Spinner mspin = (Spinner)view.findViewById(R.id.numberOfShares);


        secretCodeInput.setText("");
        secretCodeInput.setVisibility(View.INVISIBLE);
        secretCodeInput.setEnabled(false);
        hideButton.setEnabled(false);
        showButton.setEnabled(true);
        mspin.setEnabled(false);
        mspin.setVisibility(View.INVISIBLE);
    }
    public void shareExists() {

    }
}
