package com.example.mortgagecalculator;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<LoanData> selected = new MutableLiveData<>();

    public void select(LoanData item) {
        selected.setValue(item);
    }

    public MutableLiveData<LoanData> getSelected() {
        return selected;
    }

    public static class LoanData {
        public int loanAmount;
        public float interestRate;
        public int loanTermYear;
        public int loanTermMonth;
        public String selectedType;

        public LoanData(int loanAmount, float interestRate, int loanTermYear, int loanTermMonth, String selectedType) {
            this.loanAmount = loanAmount;
            this.interestRate = interestRate;
            this.loanTermYear = loanTermYear;
            this.loanTermMonth = loanTermMonth;
            this.selectedType = selectedType;
        }
    }
}