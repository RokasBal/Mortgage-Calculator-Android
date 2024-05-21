package com.example.mortgagecalculator;

import android.widget.TableRow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedTableModel extends ViewModel {
    private final MutableLiveData<List<TableRow>> tableRows = new MutableLiveData<>();

    public void setMonthlyPaymentList(List<TableRow> list) {
        tableRows.setValue(list);
    }

    public LiveData<List<TableRow>> getMonthlyPaymentList() {
        return tableRows;
    }
}