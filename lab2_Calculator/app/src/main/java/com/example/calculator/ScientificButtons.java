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
import java.util.Arrays;
import java.util.List;

public class ScientificButtons extends Fragment implements View.OnClickListener {

    private List<String> functions = Arrays.asList("sin", "cos", "tg", "ctg", "ln", "exp", "log2", "log10");

    public ScientificButtons() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scientific_buttons, container, false);

        ArrayList<View> buttons = view.findViewById(R.id.scientificButtonsGrid).getTouchables();

        for (View b : buttons) {
            b.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        String currentButtonText = ((Button) view).getText().toString();
        Activity currentActivity = getActivity();
        TextView numbersTextView;
        if (currentActivity != null)
            numbersTextView = currentActivity.findViewById(R.id.formulaTextView);
        else
            return;

        String expr = numbersTextView.getText().toString();
        String constants = "πe";
        char lastSymbol;
        if (!expr.equals("")) {
            lastSymbol = expr.charAt(expr.length() - 1);
            if (!currentButtonText.equals(")") && lastSymbol != '('
                    && (Character.isDigit(lastSymbol) || constants.indexOf(lastSymbol) != -1))
                numbersTextView.append("*" + currentButtonText);
            else
                numbersTextView.append(currentButtonText);
        }
        else
            numbersTextView.append(currentButtonText);

        if (functions.contains(currentButtonText))
            numbersTextView.append("(");

        numbersTextView.setText(numbersTextView.getText());
    }
}
