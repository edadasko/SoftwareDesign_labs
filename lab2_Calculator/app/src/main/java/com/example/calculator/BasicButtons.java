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

    public BasicButtons() { }

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
                    discharge(numbersTextView);
                    break;
                case "±":
                    changeSign(expr, numbersTextView);
                    break;
                case "=":
                    ((MainActivity)getActivity()).calculate();
                    return;
                default:
                    append(expr, currentButtonText, numbersTextView);
                    break;
            }
        numbersTextView.setText(numbersTextView.getText());
    }

    private void discharge(TextView numbersTextView) {
        numbersTextView.setText("");
    }

    private void changeSign(String expr, TextView numbersTextView) {
        if (expr.length() == 0)
            return;
        if (isStringPositiveNumber(expr) || RPNSolver.constants.contains(expr))
            expr = "-" + expr;
        else if (expr.charAt(0) == '-') {
            if (isStringPositiveNumber(expr.substring(1)) || RPNSolver.constants.contains(expr.substring(1)))
                expr = expr.substring(1);
            else if (expr.length() > 2
                    && expr.substring(0, 2).equals("-(")
                    && expr.charAt(expr.length() - 1) == ')')
                expr = expr.substring(2, expr.length() - 1);
            else
                expr = "-(" + expr + ")";
        }
        else
            expr = "-(" + expr + ")";

        numbersTextView.setText(expr);

    }

    private boolean isStringPositiveNumber(String str) {
        for (int i = 0; i < str.length(); i++)
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '.') return false;
        return true;
    }


    private void append(String expr, String currentButtonText, TextView numbersTextView) {
        if (!expr.equals("")) {
            char lastSymbol = expr.charAt(expr.length() - 1);
            if ((lastSymbol == ')' || RPNSolver.constants.contains(String.valueOf(lastSymbol)))
                    && Character.isDigit(currentButtonText.charAt(0))) {
                numbersTextView.append("*" + currentButtonText);
                return;
            }
        }
        numbersTextView.append(currentButtonText);
    }

}
