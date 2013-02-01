package com.smexec.monitor.client;

import java.util.List;

import com.googlecode.gchart.client.GChart;
import com.smexec.monitor.shared.ChartFeed;

public class LineChart
    extends GChart {

    enum LineType {
        MIN, MAX, AVG
    }

    final int MAX_CURVES = 3;
    int[] insertionPoint = new int[MAX_CURVES];
    int insertionCurve = 0;

    public LineChart(String poolName, List<ChartFeed> chartFeeds) {
        setChartSize(200, 100);
        setBorderStyle("none");

        GChart.setDefaultSymbolBorderColors(new String[] {"#c5000b", "#00ff00", "#0000ff", "#004586", "#ff420e", "#ffd320", "#7e0021", "#579d1c", "#83caff",
                                                          "#314004", "#aecf00", "#4b1f6f", "#ff950e", "#c5000b", "#0084d1"});
        setChartTitle("<span>" + poolName + "</span>");
        setChartFootnotesLeftJustified(true);
        // setChartFootnotes("<ol>" + "<li>Click on empty space to add a new point there." +
        // "<li>Click on any point to delete it."
        // + "<li>Points are added after the last inserted or deleted point." + "</ol>");
        // chart listens to its own click events:
        // addClickHandler(this);
        // add the 1-point "starter curves" along the y-axis.
        double max = drawLine(chartFeeds, LineType.MAX);
        drawLine(chartFeeds, LineType.AVG);
        drawLine(chartFeeds, LineType.MIN);

        // lock in a simple 0..100 range on each axis
        getXAxis().setAxisMin(0);
        getXAxis().setAxisMax(100);
        getXAxis().setTickCount(11);
        getXAxis().setHasGridlines(true);
        getYAxis().setAxisMin(0);
        getYAxis().setAxisMax(max + Math.max((max / 100 * 5), 5));
        getYAxis().setTickCount(11);
        getYAxis().setHasGridlines(true);
        // switch back to GChart's built-in default colors
        GChart.setDefaultSymbolBorderColors(GChart.DEFAULT_SYMBOL_BORDER_COLORS);
    }

    public void updateChart(List<ChartFeed> chartFeeds) {
        clearCurves();

        double max = drawLine(chartFeeds, LineType.MAX);
        drawLine(chartFeeds, LineType.AVG);
        drawLine(chartFeeds, LineType.MIN);
        getYAxis().setAxisMax(max + 5);

    }

    private double drawLine(List<ChartFeed> chartFeeds, LineType lineType) {
        double max = 1d;
        addCurve();
        getCurve().setLegendLabel(lineType.name());
        getCurve().getSymbol().setHeight(1);
        getCurve().getSymbol().setWidth(1);
        getCurve().getSymbol().setBorderWidth(1);
        getCurve().getSymbol().setSymbolType(SymbolType.LINE);

        for (int i = 0; i < chartFeeds.size(); i++) {
            double value = getValue(chartFeeds.get(i), lineType);
            if (value > max)
                max = value;
            getCurve().addPoint(i, value);
        }
        return max;
    }

    private double getValue(ChartFeed chartFeed, LineType lineType) {
        switch (lineType) {
            case AVG:
                return chartFeed.getAvg();
            case MAX:
                return chartFeed.getMax();
            case MIN:
                return chartFeed.getMin();
            default:
                return 0d;
        }
    }
}
