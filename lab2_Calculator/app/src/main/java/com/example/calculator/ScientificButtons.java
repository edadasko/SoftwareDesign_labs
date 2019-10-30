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

public class ScientificButtons extends Fragment implements View.OnClickListener {

    public ScientificButtons() { }

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
        EditText numbersEditText;
        if (currentActivity != null)
            numbersEditText = currentActivity.findViewById(R.id.numbersEditText);
        else
            return;

        String expr = numbersEditText.getText().toString();
        append(expr, currentButtonText, numbersEditText);
    }

    private void append(String expr, String currentButtonText, EditText numbersEditText) {
        char lastSymbol;
        if (!expr.equals("")) {
            int select = numbersEditText.getSelectionStart();
            if (select == 0) {
                if (RPNSolver.functions.contains(currentButtonText))
                    currentButtonText += "(";
                else if (RPNSolver.constants.contains(currentButtonText))
                    currentButtonText += "*";
                expr = currentButtonText + expr;
                numbersEditText.setText(expr);
                numbersEditText.setSelection(currentButtonText.length());
                return;
            }
            if (select == expr.length()) {
                lastSymbol = expr.charAt(select - 1);
                if (!currentButtonText.equals(")") && lastSymbol != '('
                        && (Character.isDigit(lastSymbol) || lastSymbol == ')' ||
                        RPNSolver.constants.contains(String.valueOf(lastSymbol)))) {
                    if (RPNSolver.functions.contains(currentButtonText))
                        currentButtonText += "(";
                    expr += "*" + currentButtonText;
                }
                else if (RPNSolver.functions.contains(currentButtonText))
                    expr += currentButtonText + "(";
                else
                    expr += currentButtonText;
                numbersEditText.setText(expr);
                numbersEditText.setSelection(expr.length());
                return;
            }
            if (RPNSolver.functions.contains(currentButtonText))
                currentButtonText += "(";
            expr = expr.substring(0, select) + currentButtonText + expr.substring(select);
            numbersEditText.setText(expr);
            numbersEditText.setSelection(select + currentButtonText.length());
        }
        else {
            if (RPNSolver.functions.contains(currentButtonText))
                currentButtonText += "(";
            numbersEditText.append(currentButtonText);
            numbersEditText.requestFocus();
            numbersEditText.setSelection(currentButtonText.length());
        }
    }

}
