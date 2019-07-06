package com.example.secretsharing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class secretCodeFragment extends Fragment {

    private static final String TAG = "SecretCodeFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.secretcode_fragment, container,false);

        final EditText secretCodeInput = (EditText)view.findViewById(R.id.secretCode);
        final EditText shareOutput = (EditText)view.findViewById(R.id.shareOutput);
        final Button hideButton = (Button)view.findViewById(R.id.hideButton);
        final Button showButton = (Button)view.findViewById(R.id.showButton);


        showButton.setEnabled(false);

        hideButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Secret s = new Secret();

                if (secretCodeInput.getText().toString().length() > 0) {
                    if (secretCodeInput.getText().toString().matches("[a-zA-Z0-9]*")) {
                        String result = s.hideSecret(secretCodeInput.getText().toString());
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
}
