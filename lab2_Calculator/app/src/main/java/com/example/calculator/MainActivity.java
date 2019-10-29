package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    boolean isInScientificMode = false;
    TextView formulaTextView;
    Fragment scientificButtonsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isInScientificMode = false;
            hideScientificButtons();
        } else {
            isInScientificMode = true;
        }

        formulaTextView = findViewById(R.id.formulaTextView);
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
                    5));
        }
    }

    private void showScientificButtons() {
        isInScientificMode = true;
        scientificButtonsFragment = getSupportFragmentManager().findFragmentById(R.id.scientificButtonsFragment);
        if (scientificButtonsFragment != null) {
            findViewById(R.id.scientificButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    2));
            findViewById(R.id.basicButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    3));
        }

        getSupportFragmentManager().beginTransaction()
                .show(scientificButtonsFragment)
                .commit();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("currentFormula", formulaTextView.getText().toString());
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState);
            String savedFormula = savedInstanceState.getString("currentFormula");
            if (savedFormula != null)
                formulaTextView.setText(savedFormula);
        }
    }

    public void removeSymbol(View view) {
        String formula = formulaTextView.getText().toString();
        if (formula.length() == 0)
            return;
        char lastSymbol = formula.charAt(formula.length() - 1);
        if (Character.isLetter(lastSymbol))
            while (Character.isLetter(lastSymbol)) {
                formula = formula.substring(0, formula.length() - 1);
                if (formula.length() == 0)
                    break;
                lastSymbol = formula.charAt(formula.length() - 1);
            }
        else
            formula = formula.substring(0, formula.length() - 1);
        formulaTextView.setText(formula);
    }

    public void calculate() {
        String formula = formulaTextView.getText().toString();
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
    }

    private void setNumber(Double num) {
        String strNum;
        if (num.isNaN() || num.isInfinite()) {
            formulaTextView.setText(String.format("%s", num));
            return;
        }
        if (num % 1 == 0)
            strNum = new DecimalFormat("#").format(num);
        else {
            String[] div = String.format("%s", num).split("\\.");
            if (div[1].length() > 7)
                strNum = String.format(Locale.US, "%.7f", num);
            else
                strNum = String.format("%s", num);
        }
        formulaTextView.setText(strNum);
    }
}
