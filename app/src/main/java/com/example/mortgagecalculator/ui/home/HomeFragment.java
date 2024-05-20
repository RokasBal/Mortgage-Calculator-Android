package com.example.mortgagecalculator.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mortgagecalculator.R;
import com.example.mortgagecalculator.SharedViewModel;
import com.example.mortgagecalculator.databinding.FragmentHomeBinding;
import com.example.mortgagecalculator.ui.dashboard.DashboardFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

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

        System.out.println("TEST MESSAGE IN HOME ONCREATEVIEW");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleCalculateButtonClick() {
        System.out.println("Calculate button clicked");

        String loanAmountString = binding.loanAmountInput.getText().toString();
        String interestRateString = binding.interestRateInput.getText().toString();
        String loanTermYearString = binding.termYearsInput.getText().toString();
        String loanTermMonthString = binding.termMonthsInput.getText().toString();

        int selectedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = binding.getRoot().findViewById(selectedRadioButtonId);
        String selectedType = selectedRadioButton.getText().toString();

        int loanAmount = Integer.parseInt(loanAmountString);
        float interestRate = Float.parseFloat(interestRateString);
        int loanTermYear = Integer.parseInt(loanTermYearString);
        int loanTermMonth = Integer.parseInt(loanTermMonthString);

        System.out.println("Loan amount: " + loanAmount + " Interest rate: " + interestRate + " Loan term year: " + loanTermYear + " Loan term month: " + loanTermMonth + " Loan type: " + selectedType);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        SharedViewModel.LoanData loanData = new SharedViewModel.LoanData(loanAmount, interestRate, loanTermYear, loanTermMonth, selectedType);
        sharedViewModel.select(loanData);

        System.out.println("TEST MESSAGE IN HOME HANDLECALCULATEBUTTONCLICK");
    }
}