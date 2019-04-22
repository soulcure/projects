package com.taku.safe.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


public class PersonAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int person = Float.valueOf(value).intValue();
        return String.valueOf(person) + "人";
    }
}
