package obelab.com.sdkexample.utils;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anriku on 2019-05-22.
 */
public class GraphUtils {

    private LineChart mChart;
    private String[] mLabels;
    private int[] mColors;
    // x轴显示的最多的点数
    private int mXRangeMaximum;

    public GraphUtils(LineChart mChart, String[] mLabels, int[] mColors) {
        this(mChart, mLabels, mColors, 60);
    }

    public GraphUtils(LineChart mChart, String[] mLabels, int[] mColors, int mXRangeMaximum) {
        this.mChart = mChart;
        this.mLabels = mLabels;
        this.mColors = mColors;
        this.mXRangeMaximum = mXRangeMaximum;
        initChart();
    }

    /**
     * 初始化各个曲线的属性
     */
    private void initChart() {
        mChart.resetTracking();
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(false);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        ArrayList<ILineDataSet> lines = new ArrayList<>();
        for (int i = 0; i < mLabels.length; i++) {
            List<Entry> entries = new ArrayList<>();
            LineDataSet standaloneLine = new LineDataSet(entries, mLabels[i]);

            standaloneLine.setLineWidth(2.5f);
            standaloneLine.setCircleRadius(4f);
            standaloneLine.setColor(mColors[i]);
            standaloneLine.setCircleColor(mColors[i]);
            standaloneLine.setMode(standaloneLine.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                    ? LineDataSet.Mode.LINEAR
                    : LineDataSet.Mode.CUBIC_BEZIER);
            lines.add(standaloneLine);
        }

        LineData allLine = new LineData(lines);
        mChart.setData(allLine);
        mChart.invalidate();
    }

    /**
     * 更新曲线的数据
     *
     * @param index 代表那一对条曲线
     * @param value 添加的数据对应的y值
     */
    public void addEntry(int index, float value) {

        LineData lines = mChart.getData();

        if (lines != null) {
            ILineDataSet standaloneLine = lines.getDataSetByIndex(index);

            if (standaloneLine == null) {
                return;
            }

            int count = standaloneLine.getEntryCount();
            standaloneLine.addEntry(new Entry(count, value));
            lines.notifyDataChanged();

            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(mXRangeMaximum);
            mChart.moveViewToX(lines.getEntryCount());
        }
    }


}
