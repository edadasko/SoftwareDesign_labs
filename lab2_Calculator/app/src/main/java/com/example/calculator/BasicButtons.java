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
        String constants = "eπ";
        String currentButtonText = ((Button)view).getText().toString();
            Activity currentActivity = getActivity();
            TextView numbersTextView;
            if (currentActivity != null)
                numbersTextView = currentActivity.findViewById(R.id.formulaTextView);
            else
                return;
        String expr = numbersTextView.getText().toString();

            switch (currentButtonText) {
                case "c":
                    numbersTextView.setText("");
                    break;
                case "±":
                    if (expr.length() == 0)
                        return;
                    if (expr.charAt(0) != '-')
                        expr = "-(" + expr + ")";
                    else if (expr.length() > 2
                            && expr.substring(0, 2).equals("-(")
                            && expr.charAt(expr.length() - 1) == ')')
                        expr = expr.substring(2, expr.length() - 1);
                    else
                        expr = expr.substring(1);
                    numbersTextView.setText(expr);
                    break;
                case "=":
                    ((MainActivity)getActivity()).calculate();
                    return;
                default:
                    if (!expr.equals("")) {
                        char lastSymbol = expr.charAt(expr.length() - 1);
                        if ((lastSymbol == ')' || constants.indexOf(lastSymbol) != -1)
                                && Character.isDigit(currentButtonText.charAt(0))) {
                            numbersTextView.append("*" + currentButtonText);
                            break;
                        }
                    }
                    numbersTextView.append(currentButtonText);
                    break;
            }
        numbersTextView.setText(numbersTextView.getText());
    }

}
