package com.taku.safe.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatterMonth implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String res;
        int sum = Float.valueOf(value).intValue();
        int month = sum / 100;
        int date = sum % 100;
        if (date < 10) {
            res = month + "月" + "0" + date + "日";
        } else {
            res = month + "月" + date + "日";
        }
        return res;
    }


}
