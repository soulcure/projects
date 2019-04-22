
package com.taku.safe.tabs;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.taku.safe.R;
import com.taku.safe.adapter.SchoolAnalysisAdapter;
import com.taku.safe.chart.DayAxisValueFormatterMonth;
import com.taku.safe.chart.PersonAxisValueFormatter;
import com.taku.safe.chart.XYMarkerView;

import java.util.ArrayList;
import java.util.List;

public class BarChartWeekFragment extends Fragment implements OnChartValueSelectedListener {
    private static final String TAG = BarChartWeekFragment.class.getSimpleName();

    private BarChart mChart;

    private RecyclerView mRecyclerView;
    private SchoolAnalysisAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nosign_week, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SchoolAnalysisAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        mChart = (BarChart) view.findViewById(R.id.barChart);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 31 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(31);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setNoDataText("loading...");
        mChart.setDrawGridBackground(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatterMonth();
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new PersonAxisValueFormatter();
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setTextColor(Color.RED);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        mChart.animateY(3000);  // y轴动画

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                test();
            }
        }, 2000);
    }


    private void test() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(getXvalue(4, 1), 8f));
        entries.add(new BarEntry(getXvalue(4, 2), 6f));
        entries.add(new BarEntry(getXvalue(4, 3), 12f));
        entries.add(new BarEntry(getXvalue(4, 4), 18f));
        entries.add(new BarEntry(getXvalue(4, 5), 61f));
        entries.add(new BarEntry(getXvalue(4, 6), 51f));
        entries.add(new BarEntry(getXvalue(4, 7), 31f));
        entries.add(new BarEntry(getXvalue(4, 8), 41f));
        entries.add(new BarEntry(getXvalue(4, 9), 91f));
        entries.add(new BarEntry(getXvalue(4, 10), 71f));
        entries.add(new BarEntry(getXvalue(4, 11), 41f));
        entries.add(new BarEntry(getXvalue(4, 12), 51f));
        entries.add(new BarEntry(getXvalue(4, 13), 58f));
        entries.add(new BarEntry(getXvalue(4, 14), 87f));
        entries.add(new BarEntry(getXvalue(4, 15), 65f));
        entries.add(new BarEntry(getXvalue(4, 16), 15f));
        entries.add(new BarEntry(getXvalue(4, 17), 145f));
        entries.add(new BarEntry(getXvalue(4, 18), 99f));
        entries.add(new BarEntry(getXvalue(4, 19), 77f));
        entries.add(new BarEntry(getXvalue(4, 20), 59f));
        entries.add(new BarEntry(getXvalue(4, 21), 45f));
        entries.add(new BarEntry(getXvalue(4, 22), 11f));
        entries.add(new BarEntry(getXvalue(4, 23), 21f));
        entries.add(new BarEntry(getXvalue(4, 24), 59f));
        entries.add(new BarEntry(getXvalue(4, 25), 77f));
        entries.add(new BarEntry(getXvalue(4, 26), 90f));
        entries.add(new BarEntry(getXvalue(4, 27), 78f));
        entries.add(new BarEntry(getXvalue(4, 28), 56f));
        entries.add(new BarEntry(getXvalue(4, 29), 67f));
        entries.add(new BarEntry(getXvalue(4, 30), 34f));
        entries.add(new BarEntry(getXvalue(4, 31), 41f));

        //BarDataSet dataset = new BarDataSet(entries, "#未签到人数表");
        ColorBarDataSet dataset = new ColorBarDataSet(entries, "#未签到人数表");

        BarData data = new BarData(dataset);
        data.setHighlightEnabled(false);//柱形是否允许高亮选择
        data.setValueTextColor(Color.RED);

        mChart.setData(data);


        /*List<NosignItem> list = new ArrayList<>();
        list.add(new NosignItem("1", "计算机学院", "132"));
        list.add(new NosignItem("2", "生命工程学院", "131"));
        list.add(new NosignItem("3", "音乐学院", "122"));
        list.add(new NosignItem("4", "中文学院", "111"));
        list.add(new NosignItem("5", "化工学院", "99"));
        list.add(new NosignItem("6", "金融学院", "98"));
        list.add(new NosignItem("7", "土木工程学院", "88"));
        list.add(new NosignItem("8", "法学院", "67"));
        list.add(new NosignItem("9", "外国语学院", "56"));
        list.add(new NosignItem("10", "机械工程学院", "33"));
        mAdapter.setList(list);*/
    }


    private float getXvalue(int month, int date) {
        int sum = month * 100 + date;
        return Integer.valueOf(sum).floatValue();
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = new RectF();
        mChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, AxisDependency.LEFT);

        Log.i(TAG, bounds.toString());
        Log.i(TAG, position.toString());

        Log.i(TAG, "low: " + mChart.getLowestVisibleX() + ", high: "
                + mChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
    }


    /**
     * 自定义柱形图颜色
     */
    class ColorBarDataSet extends BarDataSet {
        ColorBarDataSet(List<BarEntry> yVals, String label) {
            super(yVals, label);
            if (isAdded()) {
                setColors(new int[]{R.color.material_green_500, R.color.material_orange_500,
                        R.color.material_red_500}, getContext());
            }
        }

        @Override
        public int getColor(int index) {
            if (isAdded()) {
                if (getEntryForIndex(index).getY() < 80) // less than 80 green
                    return mColors.get(0);
                else if (getEntryForIndex(index).getY() < 100) // less than 100 orange
                    return mColors.get(1);
                else // greater or equal than 100 red
                    return mColors.get(2);
            } else {
                return super.getColor(index);
            }
        }
    }


}
