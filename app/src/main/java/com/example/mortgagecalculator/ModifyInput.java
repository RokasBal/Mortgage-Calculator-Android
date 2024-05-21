package com.example.mortgagecalculator;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ModifyInput {
    public static double roundInput(double input) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String inputRoundedString = decimalFormat.format(input);
        double inputRounded = Double.parseDouble(inputRoundedString);
        return inputRounded;
    }
}
