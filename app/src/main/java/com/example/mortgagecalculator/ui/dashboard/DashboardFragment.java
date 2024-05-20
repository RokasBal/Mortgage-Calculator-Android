package com.example.mortgagecalculator.ui.dashboard;

import static java.lang.Math.pow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mortgagecalculator.ModifyInput;
import com.example.mortgagecalculator.R;
import com.example.mortgagecalculator.SharedViewModel;
import com.example.mortgagecalculator.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    int loanAmount;
    float interestRate;
    int loanTermYear;
    int loanTermMonth;
    String selectedType;

    private FragmentDashboardBinding binding;
    private TableLayout tableLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        System.out.println("TEST MESSAGE IN DASHBOARD ONCREATEVIEW");

        tableLayout = root.findViewById(R.id.dataTable);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<SharedViewModel.LoanData>() {
            @Override
            public void onChanged(SharedViewModel.LoanData loanData) {
                getData(loanData.loanAmount, loanData.interestRate, loanData.loanTermYear, loanData.loanTermMonth, loanData.selectedType);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getData(int loanAmount, float interestRate, int loanTermYear, int loanTermMonth, String selectedType) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTermYear = loanTermYear;
        this.loanTermMonth = loanTermMonth;
        this.selectedType = selectedType;

        System.out.println("Received data: " + loanAmount + " " + interestRate + " " + loanTermYear + " " + loanTermMonth + " " + selectedType);

        fillTable();
    }

    public void fillTable() {
        int term = loanTermYear * 12 + loanTermMonth;
        int remainingBalance = loanAmount;
        float monthlyInterestRate = interestRate / 1200;
        float monthlyPayment;
        int totalDelay = 0;
        int delayStartMonth = 0;
        int delayEndMonth = 0;

        if(selectedType.equals("Annuity")) {
            monthlyPayment = (float) ((loanAmount * monthlyInterestRate) / (1 - pow(1 + monthlyInterestRate, -(term - totalDelay))));
        } else {
            monthlyPayment = (loanAmount / term) + (loanAmount * monthlyInterestRate);
        }

        for (int i = 0; i < term; i++) {
            double interestPayment = remainingBalance * monthlyInterestRate;
            double principalPayment = 0;

            float interestPaymentRounded;
            float remainingBalanceRounded;
            float monthlyPaymentRounded;

            // Checking if current month is inside a postpone period.
            if(i >= delayStartMonth && i < delayEndMonth) {
                interestPayment = 0;
                monthlyPaymentRounded = 0;
            } else {
                if(selectedType.equals("Linear")) {
                    principalPayment = loanAmount / (term - totalDelay);
                } else {
                    principalPayment = monthlyPayment;
                }

                if(selectedType.equals("Linear")) {
                    monthlyPaymentRounded = ModifyInput.roundInput(principalPayment + interestPayment);
                } else monthlyPaymentRounded = ModifyInput.roundInput(monthlyPayment);

                remainingBalance -= principalPayment;

                System.out.println("Monthly payment: " + monthlyPaymentRounded + ", Remaining Balance: " + remainingBalance);
            }

            if(remainingBalance < 1) remainingBalance = 0;

            interestPaymentRounded = ModifyInput.roundInput(interestPayment);
            remainingBalanceRounded = ModifyInput.roundInput(remainingBalance);

            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);


            TableRow tableRow = new TableRow(getContext());

            TextView monthTextView = new TextView(getContext());
            monthTextView.setText(String.valueOf(i + 1));
            monthTextView.setLayoutParams(params);
            tableRow.addView(monthTextView);

            TextView monthlyPaymentTextView = new TextView(getContext());
            monthlyPaymentTextView.setText(String.valueOf(monthlyPaymentRounded));
            monthlyPaymentTextView.setLayoutParams(params);
            tableRow.addView(monthlyPaymentTextView);

            TextView interestPaymentTextView = new TextView(getContext());
            interestPaymentTextView.setText(String.valueOf(interestPaymentRounded));
            interestPaymentTextView.setLayoutParams(params);
            tableRow.addView(interestPaymentTextView);

            TextView remainingBalanceTextView = new TextView(getContext());
            remainingBalanceTextView.setText(String.valueOf(remainingBalanceRounded));
            remainingBalanceTextView.setLayoutParams(params);
            tableRow.addView(remainingBalanceTextView);

            tableLayout.addView(tableRow);
        }
    }
}