package com.example.mortgagecalculator.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;

import android.graphics.Rect;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mortgagecalculator.ModifyInput;
import com.example.mortgagecalculator.MonthPayment;
import com.example.mortgagecalculator.R;
import com.example.mortgagecalculator.SharedPaymentModel;
import com.example.mortgagecalculator.SharedTableModel;
import com.example.mortgagecalculator.SharedViewModel;
import com.example.mortgagecalculator.databinding.FragmentHomeBinding;
import com.example.mortgagecalculator.ui.dashboard.DashboardFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ScrollView scrollView;
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private boolean hasNavigatedToDashboard = false;

    int loanAmount;
    float interestRate;
    int loanTermYear;
    int loanTermMonth;
    String selectedType;
    int postponeStartYear;
    int postponeStartMonth;
    int postponeEndYear;
    int postponeEndMonth;

    List<TableRow> tableRows = new ArrayList<>();
    List<MonthPayment> monthlyPaymentList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCalculateButtonClick();
            }
        });

        scrollView = (ScrollView) root.findViewById(R.id.scrollView);
        final View activityRootView = scrollView.getChildAt(0);
        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
                    View focusedView = getActivity().getCurrentFocus();
                    if (focusedView != null) {
                        int[] location = new int[2];
                        focusedView.getLocationInWindow(location);
                        int scrollHeight = (location[1] + focusedView.getHeight()) - r.bottom;
                        scrollView.smoothScrollBy(0, scrollHeight);
                    }
                }
            }
        };
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);

        System.out.println("TEST MESSAGE IN HOME ONCREATEVIEW");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scrollView.getViewTreeObserver().isAlive()) {
            scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
        binding = null;
    }

    private void handleCalculateButtonClick() {
        System.out.println("Calculate button clicked");

        String loanAmountString = binding.loanAmountInput.getText().toString();
        String interestRateString = binding.interestRateInput.getText().toString();
        String loanTermYearString = binding.termYearsInput.getText().toString();
        String loanTermMonthString = binding.termMonthsInput.getText().toString();

        String postponeStartYearString = binding.postponeStartYearInput.getText().toString();
        String postponeStartMonthString = binding.postponeStartMonthInput.getText().toString();
        String postponeEndYearString = binding.postponeEndYearInput.getText().toString();
        String postponeEndMonthString = binding.postponeEndMonthInput.getText().toString();

        int selectedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedRadioButtonId);
        selectedType = selectedRadioButton.getText().toString();

        loanAmount = Integer.parseInt(loanAmountString);
        interestRate = Float.parseFloat(interestRateString);
        loanTermYear = Integer.parseInt(loanTermYearString);
        loanTermMonth = Integer.parseInt(loanTermMonthString);

        if (TextUtils.isEmpty(postponeStartYearString)) {
            postponeStartYearString = "0";
        }
        if (TextUtils.isEmpty(postponeStartMonthString)) {
            postponeStartMonthString = "0";
        }
        if (TextUtils.isEmpty(postponeEndYearString)) {
            postponeEndYearString = "0";
        }
        if (TextUtils.isEmpty(postponeEndMonthString)) {
            postponeEndMonthString = "0";
        }

        postponeStartYear = Integer.parseInt(postponeStartYearString);
        postponeStartMonth = Integer.parseInt(postponeStartMonthString);
        postponeEndYear = Integer.parseInt(postponeEndYearString);
        postponeEndMonth = Integer.parseInt(postponeEndMonthString);

        System.out.println("Loan amount: " + loanAmount + " Interest rate: " + interestRate + " Loan term year: " + loanTermYear + " Loan term month: " + loanTermMonth + " Loan type: " + selectedType);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        SharedViewModel.LoanData loanData = new SharedViewModel.LoanData(loanAmount, interestRate, loanTermYear, loanTermMonth, selectedType, postponeStartYear, postponeStartMonth, postponeEndYear, postponeEndMonth);
        sharedViewModel.select(loanData);

        calculate();

        System.out.println("TEST MESSAGE IN HOME HANDLECALCULATEBUTTONCLICK");

//        hasNavigatedToDashboard = false;
    }

    public void calculate() {
        int counter = 1;
        int term = loanTermYear * 12 + loanTermMonth;
        double remainingBalance = loanAmount;
        double monthlyInterestRate = interestRate / 12 / 100;
        double monthlyPayment;
        int postponeStart = postponeStartYear * 12 + postponeStartMonth;
        int postponeEnd = postponeEndYear * 12 + postponeEndMonth;
        double totalToPay = 0;

        tableRows.clear();
        monthlyPaymentList.clear();

        if (selectedType.equals("Annuity")) {
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

                monthlyPaymentList.add(new MonthPayment(i, monthlyPaymentRounded));

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

                tableRows.add(tableRow);
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

                if (totalToPay < 1) totalToPay = 0;
                totalToPay -= monthlyPayment;

                double monthlyPaymentRounded = ModifyInput.roundInput(monthlyPayment);
                double interestPaymentRounded = ModifyInput.roundInput(percent);
                double remainingBalanceRounded = ModifyInput.roundInput(totalToPay);

                monthlyPaymentList.add(new MonthPayment(i, monthlyPaymentRounded));

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

                tableRows.add(tableRow);
            }
        }

        SharedTableModel sharedTableModel = new ViewModelProvider(requireActivity()).get(SharedTableModel.class);
        sharedTableModel.setMonthlyPaymentList(tableRows);

        SharedPaymentModel sharedPaymentModel = new ViewModelProvider(requireActivity()).get(SharedPaymentModel.class);
        sharedPaymentModel.setMonthlyPaymentList(monthlyPaymentList);
    }
}