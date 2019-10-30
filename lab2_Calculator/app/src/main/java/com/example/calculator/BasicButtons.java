package com.example.calculator;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
        EditText numbersEditText;
        if (currentActivity != null)
            numbersEditText = currentActivity.findViewById(R.id.numbersEditText);
        else
            return;
        String expr = numbersEditText.getText().toString();

            switch (currentButtonText) {
                case "c":
                    discharge(numbersEditText);
                    break;
                case "±":
                    changeSign(expr, numbersEditText);
                    break;
                case "=":
                    ((MainActivity)getActivity()).calculate();
                    return;
                default:
                    append(expr, currentButtonText, numbersEditText);
                    break;
            }
    }

    private void discharge(EditText numbersEditText) {
        numbersEditText.setText("");
    }

    private void changeSign(String expr, EditText numbersEditText) {
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

        numbersEditText.setText(expr);

    }

    private boolean isStringPositiveNumber(String str) {
        for (int i = 0; i < str.length(); i++)
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '.') return false;
        return true;
    }


    private void append(String expr, String currentButtonText, EditText numbersEditText) {
        if (!expr.equals("")) {
            int select = numbersEditText.getSelectionStart();
            if (select == 0) {
                if (Character.isLetter(expr.charAt(0)))
                    currentButtonText += "*";
                expr = currentButtonText + expr;
                numbersEditText.setText(expr);
                numbersEditText.setSelection(currentButtonText.length());
                return;
            }
            if (select == expr.length()) {
                char lastSymbol = expr.charAt(select - 1);
                if ((lastSymbol == ')' || RPNSolver.constants.contains(String.valueOf(lastSymbol)))
                        && Character.isDigit(currentButtonText.charAt(0))) {
                    expr += "*" + currentButtonText;
                    numbersEditText.setText(expr);
                    numbersEditText.setSelection(expr.length());
                    return;
                }
            }
            expr = expr.substring(0, select) + currentButtonText + expr.substring(select);
            numbersEditText.setText(expr);
            numbersEditText.setSelection(select + currentButtonText.length());
            return;

        }
        numbersEditText.append(currentButtonText);
        numbersEditText.requestFocus();
        numbersEditText.setSelection(currentButtonText.length());
    }

}
