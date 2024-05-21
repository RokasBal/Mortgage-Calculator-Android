package com.example.mortgagecalculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedPaymentModel extends ViewModel {
    private final MutableLiveData<List<MonthPayment>> monthlyPaymentList = new MutableLiveData<>();

    public void setMonthlyPaymentList(List<MonthPayment> list) {
        monthlyPaymentList.setValue(list);
    }

    public LiveData<List<MonthPayment>> getMonthlyPaymentList() {
        return monthlyPaymentList;
    }
}