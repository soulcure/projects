package com.taku.safe.entity;

import com.taku.safe.R;
import com.taku.safe.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by soulcure on 2017/7/30.
 */

public class MonthChioce {

    private static final int COUNT = 6;

    private List<MonthData> monthList = new ArrayList<>();

    public class MonthData {
        int itemIcon;
        int selectIcon;
        String date;
    }

    int monthItems[] = {R.mipmap.month_1_white, R.mipmap.month_2_white, R.mipmap.month_3_white,
            R.mipmap.month_4_white, R.mipmap.month_5_white, R.mipmap.month_6_white,
            R.mipmap.month_7_white, R.mipmap.month_8_white, R.mipmap.month_9_white,
            R.mipmap.month_10_white, R.mipmap.month_11_white, R.mipmap.month_12_white};

    int monthSelects[] = {R.mipmap.month_1_blue, R.mipmap.month_2_blue, R.mipmap.month_3_blue,
            R.mipmap.month_4_blue, R.mipmap.month_5_blue, R.mipmap.month_6_blue,
            R.mipmap.month_7_blue, R.mipmap.month_8_blue, R.mipmap.month_9_blue,
            R.mipmap.month_10_blue, R.mipmap.month_11_blue, R.mipmap.month_12_blue};

    private static MonthChioce instance;

    private MonthChioce() {
        parseTime();
    }


    public static MonthChioce instance() {
        if (instance == null) {
            instance = new MonthChioce();
        }
        return instance;
    }


    private void parseTime() {
        Calendar calendar = Calendar.getInstance(); //得到当月

        for (int i = 0; i < COUNT; i++) {
            int month = calendar.get(Calendar.MONTH);

            MonthData monthData = new MonthData();
            monthData.itemIcon = monthItems[month];
            monthData.selectIcon = monthSelects[month];
            monthData.date = TimeUtils.MONTH_OF_YEAR.format(calendar.getTime());
            monthList.add(monthData);

            calendar.add(Calendar.MONTH, -1);    //得到前1个月

        }
    }


    public int getMonthItemRes(int index) {
        if (index > COUNT - 1) {
            return 0;
        }
        return monthList.get(index).itemIcon;
    }

    public int getMonthSelectRes(int index) {
        if (index > COUNT - 1) {
            return 0;
        }
        return monthList.get(index).selectIcon;
    }

    public String getMonthDate(int index) {
        if (index > COUNT - 1) {
            return "";
        }
        return monthList.get(index).date;
    }


}
