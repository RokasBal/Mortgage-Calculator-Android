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
        int counter = 1;
        int term = loanTermYear * 12 + loanTermMonth;
        double remainingBalance = loanAmount;
        double monthlyInterestRate = interestRate / 12 / 100;
        double monthlyPayment;
        int postponeStart = 0;
        int postponeEnd = 0;
        double totalToPay = 0;

        if(selectedType.equals("Annuity")) {
            System.out.println("CALCULATING ANNUITY MORTGAGE");

            monthlyPayment = (loanAmount * monthlyInterestRate) / (1 - Math.pow((1 + monthlyInterestRate), -term));
            if (Double.isNaN(monthlyPayment)) {
                monthlyPayment = loanAmount / term;
            }

            counter = 1;
            remainingBalance = monthlyPayment * term;
            double principal = loanAmount;
            double percent;

            for (int i = 1; i <= term; i++) {
                remainingBalance -= monthlyPayment;

                if (i >= postponeStart && i < postponeEnd && postponeEnd < term) {
                    monthlyPayment = 0;
                    counter++;
                }

                if (postponeEnd == i && i > 1 && postponeEnd < term) {
                    remainingBalance += remainingBalance * counter * monthlyInterestRate;
                    monthlyPayment = remainingBalance / (term - i);
                    principal += principal * monthlyInterestRate;
                }

                if (principal <= 0) principal = 0;
                if (remainingBalance < 1) remainingBalance = 0;
                percent = principal * monthlyInterestRate;
                principal -= percent;

                double monthlyPaymentRounded = ModifyInput.roundInput(monthlyPayment);
                double interestPaymentRounded = ModifyInput.roundInput(percent);
                double remainingBalanceRounded = ModifyInput.roundInput(remainingBalance);

                System.out.println("!!!Monthly payment: " + monthlyPaymentRounded + " Interest payment: " + interestPaymentRounded + " Remaining balance: " + remainingBalanceRounded);

                TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f);
                TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

                TableRow tableRow = new TableRow(getContext());

                TextView monthTextView = new TextView(getContext());
                monthTextView.setText(String.valueOf(i));
                monthTextView.setLayoutParams(params1);
                tableRow.addView(monthTextView);

                TextView monthlyPaymentTextView = new TextView(getContext());
                monthlyPaymentTextView.setText(String.valueOf(monthlyPaymentRounded));
                monthlyPaymentTextView.setLayoutParams(params2);
                tableRow.addView(monthlyPaymentTextView);

                TextView interestPaymentTextView = new TextView(getContext());
                interestPaymentTextView.setText(String.valueOf(interestPaymentRounded));
                interestPaymentTextView.setLayoutParams(params2);
                tableRow.addView(interestPaymentTextView);

                TextView remainingBalanceTextView = new TextView(getContext());
                remainingBalanceTextView.setText(String.valueOf(remainingBalanceRounded));
                remainingBalanceTextView.setLayoutParams(params2);
                tableRow.addView(remainingBalanceTextView);
                tableLayout.addView(tableRow);
            }
        } else {
            System.out.println("CALCULATING LINEAR MORTGAGE");

            double monthlyReduction = loanAmount / term;
            for (int i = 1; i <= term; i++) {
                monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                if (i >= postponeStart && i < postponeEnd && postponeEnd < term) {
                    monthlyPayment = 0;
                    counter++;
                } else if (postponeEnd == i && i > 1 && postponeEnd < term) {
                    remainingBalance += remainingBalance * counter * monthlyInterestRate;
                    monthlyReduction = remainingBalance / term;
                    monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                } else {
                    remainingBalance -= monthlyReduction;
                }

                if (remainingBalance < 1) remainingBalance = 0;
                totalToPay += monthlyPayment;
            }

            double percentPay = totalToPay;
            monthlyReduction = loanAmount / term;
            remainingBalance = loanAmount;
            counter = 1;
            double percent;

            for (int i = 1; i <= term; i++) {
                monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                if (i >= postponeStart && i < postponeEnd && postponeEnd < term) {
                    monthlyPayment = 0;
                    counter++;
                    percent = remainingBalance * monthlyInterestRate;
                } else if (postponeEnd == i && i > 1 && postponeEnd < term) {
                    remainingBalance += remainingBalance * counter * monthlyInterestRate;
                    monthlyReduction = remainingBalance / term;
                    monthlyPayment = monthlyReduction + monthlyInterestRate * remainingBalance;
                    percent = remainingBalance * monthlyInterestRate;
                } else {
                    percent = remainingBalance * monthlyInterestRate;
                    remainingBalance -= monthlyReduction;
                }

                if (remainingBalance < 1) remainingBalance = 0;
                totalToPay -= monthlyPayment;

                double monthlyPaymentRounded = ModifyInput.roundInput(monthlyPayment);
                double interestPaymentRounded = ModifyInput.roundInput(percent);
                double remainingBalanceRounded = ModifyInput.roundInput(totalToPay);

                TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f);
                TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

                TableRow tableRow = new TableRow(getContext());

                TextView monthTextView = new TextView(getContext());
                monthTextView.setText(String.valueOf(i));
                monthTextView.setLayoutParams(params1);
                tableRow.addView(monthTextView);

                TextView monthlyPaymentTextView = new TextView(getContext());
                monthlyPaymentTextView.setText(String.valueOf(monthlyPaymentRounded));
                monthlyPaymentTextView.setLayoutParams(params2);
                tableRow.addView(monthlyPaymentTextView);

                TextView interestPaymentTextView = new TextView(getContext());
                interestPaymentTextView.setText(String.valueOf(interestPaymentRounded));
                interestPaymentTextView.setLayoutParams(params2);
                tableRow.addView(interestPaymentTextView);

                TextView remainingBalanceTextView = new TextView(getContext());
                remainingBalanceTextView.setText(String.valueOf(remainingBalanceRounded));
                remainingBalanceTextView.setLayoutParams(params2);
                tableRow.addView(remainingBalanceTextView);
                tableLayout.addView(tableRow);
            }
        }
    }
}