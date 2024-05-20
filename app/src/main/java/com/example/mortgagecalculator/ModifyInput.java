package com.example.mortgagecalculator;

import java.text.DecimalFormat;

public class ModifyInput {
    public static float roundInput(double input) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String inputRoundedString = decimalFormat.format(input);
        float inputRounded = Float.parseFloat(inputRoundedString);
        return inputRounded;
    }
}
