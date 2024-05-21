package com.example.mortgagecalculator.ui.dashboard;

import static java.lang.Math.pow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mortgagecalculator.ModifyInput;
import com.example.mortgagecalculator.MonthPayment;
import com.example.mortgagecalculator.R;
import com.example.mortgagecalculator.SharedPaymentModel;
import com.example.mortgagecalculator.SharedTableModel;
import com.example.mortgagecalculator.SharedViewModel;
import com.example.mortgagecalculator.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    int loanAmount;
    float interestRate;
    int loanTermYear;
    int loanTermMonth;
    String selectedType;

    int postponeStartYear;
    int postponeStartMonth;
    int postponeEndYear;
    int postponeEndMonth;

    int filterStart;
    int filterEnd;

    private FragmentDashboardBinding binding;
    private TableLayout tableLayout;

    SeekBar filterStartSlider;
    SeekBar filterEndSlider;

    List<TableRow> tableRows = new ArrayList<>();
    List<MonthPayment> monthlyPaymentList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        filterStartSlider = root.findViewById(R.id.filterStartSlider);
        filterEndSlider = root.findViewById(R.id.filterEndSlider);

        System.out.println("TEST MESSAGE IN DASHBOARD ONCREATEVIEW");

        tableLayout = root.findViewById(R.id.dataTable);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<SharedViewModel.LoanData>() {
            @Override
            public void onChanged(SharedViewModel.LoanData loanData) {
                getData(loanData.loanAmount, loanData.interestRate, loanData.loanTermYear, loanData.loanTermMonth, loanData.selectedType, loanData.postponeStartYear, loanData.postponeStartMonth, loanData.postponeEndYear, loanData.postponeEndMonth);

                int term = loanTermYear * 12 + loanTermMonth;

                filterStartSlider.setMax(term);
                filterEndSlider.setMax(term);

                filterEndSlider.setProgress(term);
                filterStartSlider.setProgress(0);
            }
        });

        SharedTableModel sharedTableModel = new ViewModelProvider(requireActivity()).get(SharedTableModel.class);
        sharedTableModel.getMonthlyPaymentList().observe(getViewLifecycleOwner(), new Observer<List<TableRow>>() {
            @Override
            public void onChanged(List<TableRow> tableRows) {
                getTableRows(tableRows);
//                fillTable();
            }
        });

        filterStartSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filterStart = progress;
                System.out.println("filterStart set to: " + filterStart);
                applyFilter();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something when the user starts a touch gesture
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do something when the user finishes a touch gesture
            }
        });

        filterEndSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filterEnd = progress;
                System.out.println("filterEnd set to: " + filterEnd);
                applyFilter();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do something when the user starts a touch gesture
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do something when the user finishes a touch gesture
            }
        });


        return root;
    }

    public void getTableRows(List<TableRow> tableRows) {
        this.tableRows = tableRows;
        applyFilter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getData(int loanAmount, float interestRate, int loanTermYear, int loanTermMonth, String selectedType, int postponeStartYear, int postponeStartMonth, int postponeEndYear, int postponeEndMonth) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTermYear = loanTermYear;
        this.loanTermMonth = loanTermMonth;
        this.selectedType = selectedType;
        this.postponeStartYear = postponeStartYear;
        this.postponeStartMonth = postponeStartMonth;
        this.postponeEndYear = postponeEndYear;
        this.postponeEndMonth = postponeEndMonth;

        System.out.println("Received data: " + loanAmount + " " + interestRate + " " + loanTermYear + " " + loanTermMonth + " " + selectedType);

//        fillTable();
    }

    public void applyFilter() {
        System.out.println("Applying filter");

        System.out.println("Before removal, child count: " + tableLayout.getChildCount());

        int rowCount = tableLayout.getChildCount();
        for (int i = rowCount - 1; i > 0; i--) {
            tableLayout.removeViewAt(i);
            System.out.println("Removed row: " + i);
        }

        System.out.println("After removal, child count: " + tableLayout.getChildCount());

        // Add the rows that pass the filter to the table
        for (TableRow row : tableRows) {
            TextView monthTextView = (TextView) row.getChildAt(0);
            int month = Integer.parseInt(monthTextView.getText().toString());
            System.out.println("Extracted month: " + month);

            if (month >= filterStart && month <= filterEnd) {
                // Create a new TableRow and add it to the TableLayout
                TableRow newRow = new TableRow(getContext());

                // Check if the old TableRow's layout parameters are null
                if (row.getLayoutParams() != null) {
                    newRow.setLayoutParams(row.getLayoutParams());
                } else {
                    // If they are null, create new layout parameters and set them to the new TableRow
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    newRow.setLayoutParams(params);
                }

                for (int i = 0; i < row.getChildCount(); i++) {
                    TextView oldTextView = (TextView) row.getChildAt(i);
                    TextView newTextView = new TextView(getContext());
                    newTextView.setText(oldTextView.getText());
                    newTextView.setLayoutParams(oldTextView.getLayoutParams());
                    newRow.addView(newTextView);
                }

                tableLayout.addView(newRow);
            }
        }
    }
}