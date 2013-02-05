package com.smexec.monitor.client;

import com.google.gwt.core.shared.GWT;
import com.googlecode.gchart.client.GChart;
import com.smexec.monitor.shared.ChartFeed;

public class LineChart
    extends GChart {

    enum LineType {
        MIN(2), MAX(0), AVG(1), SUBMITED(0), EXECUTED(1), FAILED(2), REJECTED(3), COMPLETED(4);

        int index;

        private LineType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    final static int X_ELEMENTS = 50;

    final int MAX_CURVES = 3;
    int[] insertionPoint = new int[MAX_CURVES];
    int insertionCurve = 0;

    public LineChart() {
        setChartSize(100, 50);
        setBorderStyle("none");
        setStyleName("chart");
        setPadding("5px");
        
        GChart.setDefaultSymbolBorderColors(new String[] {"#c5000b", "#00ff00", "#0000ff", "#004586", "#ff420e", "#ffd320", "#7e0021", "#579d1c", "#83caff",
                                                          "#314004", "#aecf00", "#4b1f6f", "#ff950e", "#c5000b", "#0084d1"});
        // setChartTitle("<span>" + poolName + "</span>");
        setChartFootnotesLeftJustified(true);
        // setChartFootnotes("<ol>" + "<li>Click on empty space to add a new point there." +
        // "<li>Click on any point to delete it."
        // + "<li>Points are added after the last inserted or deleted point." + "</ol>");
        // chart listens to its own click events:
        // addClickHandler(this);
        // add the 1-point "starter curves" along the y-axis.
        // double max = drawLine(chartFeeds, LineType.MAX);
        // drawLine(chartFeeds, LineType.AVG);
        // drawLine(chartFeeds, LineType.MIN);

        // lock in a simple 0..100 range on each axis
        getXAxis().setAxisMin(0);
        getXAxis().setAxisMax(X_ELEMENTS);
        getXAxis().setTickCount(5);
        getXAxis().setHasGridlines(false);
        getYAxis().setAxisMin(0);
        // getYAxis().setAxisMax(max + Math.max((max / 100 * 5), 5));
        getYAxis().setTickCount(5);
        getYAxis().setHasGridlines(false);
        // switch back to GChart's built-in default colors
        GChart.setDefaultSymbolBorderColors(GChart.DEFAULT_SYMBOL_BORDER_COLORS);
    }

    public void updateChart(ChartFeed timeChartFeeds, LineType[] lineTypes) {
        clearCurves();

        double max = Double.MIN_VALUE;
        for (int i = 0; i < lineTypes.length; i++) {
            double line = drawLine(timeChartFeeds, lineTypes[i]);
            if (line > max) {
                max = line;
            }
        }
        getYAxis().setAxisMax(max + +Math.max((max / 100 * 5), 5));

    }

    private double drawLine(ChartFeed timeChartFeeds, LineType lineType) {
        double max = 1d;
        addCurve();
        // getCurve().setLegendLabel(lineType.name());
        getCurve().getSymbol().setHeight(1);
        getCurve().getSymbol().setWidth(1);
        getCurve().getSymbol().setBorderWidth(1);
        getCurve().getSymbol().setSymbolType(SymbolType.LINE);

        int size = GWT.isScript() ? (timeChartFeeds.getValuesLenght() > X_ELEMENTS ? X_ELEMENTS : timeChartFeeds.getValuesLenght()) : 2;
        for (int i = 0; i < size; i++) {
            double value = timeChartFeeds.getValues(lineType.getIndex(), i);
            if (value > max)
                max = value;
            getCurve().addPoint(i, value);
        }
        return max;
    }

}
