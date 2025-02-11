package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.os.Bundle;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.InputMismatchException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    boolean isInScientificMode = false;
    EditText numbersEditText;
    Fragment scientificButtonsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dontShowKeyboard();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isInScientificMode = false;
            hideScientificButtons();
        } else {
            isInScientificMode = true;
        }

        numbersEditText = findViewById(R.id.numbersEditText);
        restoreState(savedInstanceState);
    }

    public void changeMode(View view) {
        if (isInScientificMode)
            hideScientificButtons();
        else
            showScientificButtons();
    }

    private void hideScientificButtons() {
        isInScientificMode = false;
        scientificButtonsFragment = getSupportFragmentManager().findFragmentById(R.id.scientificButtonsFragment);
        if (scientificButtonsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(scientificButtonsFragment)
                    .commit();
            findViewById(R.id.basicButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    2));
        }
    }

    private void showScientificButtons() {
        isInScientificMode = true;
        scientificButtonsFragment = getSupportFragmentManager().findFragmentById(R.id.scientificButtonsFragment);
        if (scientificButtonsFragment != null) {
            findViewById(R.id.scientificButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    1));
            findViewById(R.id.basicButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    1));
        }

        getSupportFragmentManager().beginTransaction()
                .show(scientificButtonsFragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("currentFormula", numbersEditText.getText().toString());
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState);
            String savedFormula = savedInstanceState.getString("currentFormula");
            if (savedFormula != null)
                numbersEditText.setText(savedFormula);
        }
    }

    public void removeSymbols(View view) {
        String text = numbersEditText.getText().toString();
        if (text.length() == 0)
            return;
        int selectStart = numbersEditText.getSelectionStart();
        int selectEnd = numbersEditText.getSelectionEnd();
        if (selectEnd == 0)
            return;
        if (selectStart == selectEnd) {
            text = text.substring(0, selectStart - 1) + text.substring(selectStart);
            numbersEditText.setText(text);
            numbersEditText.setSelection(selectStart - 1);
            return;
        }

        text = text.substring(0, selectStart) + text.substring(selectEnd);
        numbersEditText.setText(text);
        numbersEditText.setSelection(selectStart);
    }

    public void calculate() {
        String formula = numbersEditText.getText().toString();
        RPNSolver solver = new RPNSolver();
        double answer;
        try {
            answer = solver.calculate(formula);
        }
        catch (InputMismatchException exp) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "The expression is incorrect", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        setNumber(answer);
        numbersEditText.setSelection(numbersEditText.getText().length());
    }

    private void setNumber(Double num) {
        String strNum;
        if (num.isNaN() || num.isInfinite()) {
            numbersEditText.setText(String.format("%s", num));
            return;
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        strNum = new DecimalFormat("#.########", symbols).format(num);

        if(strNum.equals("-0"))
            strNum = "0";
        numbersEditText.setText(strNum);
    }

    private void dontShowKeyboard() {
        EditText editText = findViewById(R.id.numbersEditText);
        editText.setShowSoftInputOnFocus(false);
    }
}