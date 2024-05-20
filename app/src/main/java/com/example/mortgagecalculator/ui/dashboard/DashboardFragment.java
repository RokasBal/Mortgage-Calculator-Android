package com.example.mortgagecalculator.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mortgagecalculator.SharedViewModel;
import com.example.mortgagecalculator.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    int loanAmount;
    float interestRate;
    int loanTermYear;
    int loanTermMonth;
    String selectedType;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        System.out.println("TEST MESSAGE IN DASHBOARD ONCREATEVIEW");

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
    }

    public void fillTable() {

    }
}