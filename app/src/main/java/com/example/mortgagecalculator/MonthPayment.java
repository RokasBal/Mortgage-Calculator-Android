package com.example.mortgagecalculator;

public class MonthPayment {
    private int month;

    public void setMonth(int month) {
        this.month = month;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    private double monthlyPayment;

    public int getMonth() {
        return month;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public MonthPayment(int month, double monthlyPayment) {
        this.month = month;
        this.monthlyPayment = monthlyPayment;
    }
}
