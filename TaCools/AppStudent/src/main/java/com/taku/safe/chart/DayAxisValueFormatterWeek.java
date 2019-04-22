package com.taku.safe.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;


/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatterWeek implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String res = "";
        int sum = Float.valueOf(value).intValue();
        switch (sum) {
            case 2:
                res = "MON";
                break;
            case 3:
                res = "TUE";
                break;
            case 4:
                res = "WED";
                break;
            case 5:
                res = "THU";
                break;
            case 6:
                res = "FRI";
                break;
            case 7:
                res = "SAT";
                break;
            case 8:
                res = "SUN";
                break;
        }
        return res;
    }


}
