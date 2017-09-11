package com.example.arvind.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Calculator extends AppCompatActivity {
    // IDs of all the numeric buttons
    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    // IDs of all the operator buttons
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};
    // TextView used to display the output
    private TextView txtScreen;
    // Represent whether the lastly pressed key is numeric or not
    private boolean lastNumeric;
    // Represent that current state is in error or not
    private boolean stateError;
    // If true, do not allow to add another DOT
    private boolean lastDot;
    // BackSpace Button
    private int backbtn = R.id.backspace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        // Find the TextView
        this.txtScreen = (TextView) findViewById(R.id.txtScreen);
        // Find and set OnClickListener to numeric buttons
        setNumericOnClickListener();
        // Find and set OnClickListener to operator buttons, equal button and decimal point button
        setOperatorOnClickListener();
        // Remove Char on BackSpace
        findViewById(backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence text = txtScreen.getText();
                if(text.length() != 0) {
                    text = text.subSequence(0, text.length() - 1);
                    txtScreen.setText(text);
                }
            }
        });
    }

    /**
     * Find and set OnClickListener to numeric buttons.
     */
    private void setNumericOnClickListener() {
        // Create a common OnClickListener
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just append/set the text of clicked button
                Button button = (Button) v;
                if (stateError) {
                    // If current state is Error, replace the error message
                    txtScreen.setText(button.getText());
                    stateError = false;
                } else {
                    // If not, already there is a valid expression so append to it
                    txtScreen.append(button.getText());
                }
                // Set the flag
                lastNumeric = true;
            }
        };
        // Assign the listener to all the numeric buttons
        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    /**
     * Find and set OnClickListener to operator buttons, equal button and decimal point button.
     */
    private void setOperatorOnClickListener() {
        // Create a common OnClickListener for operators
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the current state is Error do not append the operator
                // If the last input is number only, append the operator
                if (lastNumeric && !stateError) {
                    Button button = (Button) v;
                    txtScreen.append(button.getText());
                    lastNumeric = false;
                    lastDot = false;    // Reset the DOT flag
                }
            }
        };
        // Assign the listener to all the operator buttons
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(listener);
        }
        // Decimal point
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError && !lastDot) {
                    txtScreen.append(".");
                    lastNumeric = false;
                    lastDot = true;
                }
            }
        });
        // Clear button
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("");  // Clear the screen
                // Reset all the states and flags
                lastNumeric = false;
                stateError = false;
                lastDot = false;
            }
        });
        // Equal button
        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
            }
        });
    }

    /**
     * Logic to calculate the solution.
     */
    private void onEqual() {
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if (lastNumeric && !stateError) {
            // Read the expression
            String txt = txtScreen.getText().toString();
            // Create an Expression (A class from exp4j library)
            try {
                // Calculate the result and display
                double result = evaluate(txt);
                txtScreen.setText(Double.toString(result));
                lastDot = true; // Result contains a dot
            } catch (ArithmeticException ex) {
                // Display an error message
                txtScreen.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }

    private class Pair{
        double num;
        char character;
    }

    private double evaluate(String txt){
        double rv = 0.0;
        String currnum = "";
        ArrayList<Pair> al = new ArrayList<>();

        for(int i = 0; i < txt.length(); i++){
            char cc = txt.charAt(i);
            if(cc != '+' && cc != '-' && cc != '*' && cc != '/'){
                currnum += cc;
            }else{
                Pair p = new Pair();
                p.num = Double.parseDouble(currnum);
                p.character = cc;
                al.add(p);
                currnum = "";
            }
        }

        Pair p = new Pair();
        p.num = Double.parseDouble(currnum);
        p.character = '\0';
        al.add(p);

        // Solving for /
        for(int i = 0; i < al.size(); i++){
            if(al.get(i).character == '/'){
                Pair fn = al.remove(i);
                Pair sn = al.remove(i);
                double fnd = fn.num;
                double snd = sn.num;
                double ansd = (fnd * 1.0)/ snd;
                Pair ans = new Pair();
                ans.num = ansd;
                ans.character = sn.character;
                al.add(i, ans);
                i--;
            }
        }

        // Solving for *
        for(int i = 0; i < al.size(); i++){
            if(al.get(i).character == '*'){
                Pair fn = al.remove(i);
                Pair sn = al.remove(i);
                double fnd = fn.num;
                double snd = sn.num;
                double ansd = (fnd * 1.0) * snd;
                Pair ans = new Pair();
                ans.num = ansd;
                ans.character = sn.character;
                al.add(i, ans);
                i--;
            }
        }

        // Solving for -
        for(int i = 0; i < al.size(); i++){
            if(al.get(i).character == '-'){
                Pair fn = al.remove(i);
                Pair sn = al.remove(i);
                double fnd = fn.num;
                double snd = sn.num;
                double ansd = (fnd * 1.0) - snd;
                Pair ans = new Pair();
                ans.num = ansd;
                ans.character = sn.character;
                al.add(i, ans);
                i--;
            }
        }

        // Solving for +
        for(int i = 0; i < al.size(); i++){
            if(al.get(i).character == '+'){
                Pair fn = al.remove(i);
                Pair sn = al.remove(i);
                double fnd = fn.num;
                double snd = sn.num;
                double ansd = (fnd * 1.0) + snd;
                Pair ans = new Pair();
                ans.num = ansd;
                ans.character = sn.character;
                al.add(i, ans);
                i--;
            }
        }

        rv = al.get(0).num;

        return rv;
    }
}
