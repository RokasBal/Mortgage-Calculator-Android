package com.example.mortgagecalculator.ui.notifications;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mortgagecalculator.MonthPayment;
import com.example.mortgagecalculator.R;
import com.example.mortgagecalculator.SharedPaymentModel;
import com.example.mortgagecalculator.SharedViewModel;
import com.example.mortgagecalculator.databinding.FragmentNotificationsBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    List<MonthPayment> monthlyPaymentList = new ArrayList<>();
    LineChart lineChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPaymentModel sharedPaymentModel = new ViewModelProvider(requireActivity()).get(SharedPaymentModel.class);
        sharedPaymentModel.getMonthlyPaymentList().observe(getViewLifecycleOwner(), new Observer<List<MonthPayment>>() {
            @Override
            public void onChanged(List<MonthPayment> monthlyPaymentList) {
                updateGraph(monthlyPaymentList);
            }
        });

        lineChart = root.findViewById(R.id.lineChart);

//        List<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(0f, 1f));
//        entries.add(new Entry(1f, 2f));
//        entries.add(new Entry(10f, 10f));
        // ... add more entries here ...

//        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
//        LineData lineData = new LineData(dataSet);
//        lineChart.setData(lineData);

// Get the XAxis and YAxis instances from the LineChart
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxis = lineChart.getAxisLeft(); // get the left (or right) axis

// Set the text color of the axis labels
        xAxis.setTextColor(Color.WHITE); // change to the color you want
        yAxis.setTextColor(Color.WHITE); // change to the color you want

// Set the text size of the axis labels
        xAxis.setTextSize(12f); // change to the size you want
        yAxis.setTextSize(12f); // change to the size you want

// Set the position of the axis labels
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

// Refresh the chart
        lineChart.invalidate();

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateGraph(List<MonthPayment> monthlyPaymentList) {
        List<Entry> entries = new ArrayList<>();

        for (MonthPayment month : monthlyPaymentList) {
            entries.add(new Entry(month.getMonth(), (float) month.getMonthlyPayment()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Monhtly payments"); // add entries to dataset
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
    }
}