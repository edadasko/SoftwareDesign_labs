package com.example.calculator;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class BasicButtons extends Fragment implements View.OnClickListener {

    public BasicButtons() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_basic_buttons, container, false);

        ArrayList<View> buttons = view.findViewById(R.id.basicButtonsGrid).getTouchables();

        for (View b : buttons) {
            b.setOnClickListener(this);
        }

        return view;

    }

    @Override
    public void onClick(View view) {
        String currentButton = ((Button)view).getText().toString();
        Activity currentActivity = getActivity();
        TextView numbersTextView;
        if (currentActivity != null)
            numbersTextView = currentActivity.findViewById(R.id.formulaTextView);
        else
            return;

        switch (currentButton) {
            default:
                numbersTextView.setText(currentButton);
                break;
        }
    }

}
