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
        public int postponeStartYear;
        public int postponeStartMonth;
        public int postponeEndYear;
        public int postponeEndMonth;

        public LoanData(int loanAmount, float interestRate, int loanTermYear, int loanTermMonth, String selectedType, int postponeStartYear, int postponeStartMonth, int postponeEndYear, int postponeEndMonth) {
            this.loanAmount = loanAmount;
            this.interestRate = interestRate;
            this.loanTermYear = loanTermYear;
            this.loanTermMonth = loanTermMonth;
            this.selectedType = selectedType;
            this.postponeStartYear = postponeStartYear;
            this.postponeStartMonth = postponeStartMonth;
            this.postponeEndYear = postponeEndYear;
            this.postponeEndMonth = postponeEndMonth;
        }
    }
}