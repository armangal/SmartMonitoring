package com.smexec.monitor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.googlecode.gchart.client.GChart;
import com.smexec.monitor.shared.ChartFeed;

public class LineChart
    extends GChart {

    public enum LineType {
        MIN(2), MAX(0), AVG(1), SUBMITED(0), EXECUTED(1), FAILED(2), REJECTED(3), COMPLETED(4);

        int index;

        private LineType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public LineChart() {
        setChartSize(200, 150);
        setBorderStyle("none");
        setStyleName("chart");

        GChart.setDefaultSymbolBorderColors(new String[] {"#c5000b", "#00ff00", "#0000ff", "#004586", "#ff420e", "#ffd320", "#7e0021", "#579d1c", "#83caff",
                                                          "#314004", "#aecf00", "#4b1f6f", "#ff950e", "#c5000b", "#0084d1"});
        // setChartTitle("<span>Times</span>");
        setChartFootnotesLeftJustified(true);
        // setChartFootnotes("<ol>" + "<li>Click on empty space to add a new point there.");
        // "<li>Click on any point to delete it."
        // + "<li>Points are added after the last inserted or deleted point." + "</ol>");

        // lock in a simple 0..100 range on each axis
        getXAxis().setAxisMin(0);
        getXAxis().setAxisMax(10);
        getXAxis().setTickCount(5);
        getXAxis().setHasGridlines(true);
        getYAxis().setTickCount(10);
        getYAxis().setHasGridlines(true);
        // switch back to GChart's built-in default colors
        GChart.setDefaultSymbolBorderColors(GChart.DEFAULT_SYMBOL_BORDER_COLORS);
    }

    public void updateChart(ChartFeed timeChartFeeds, LineType[] lineTypes) {
        clearCurves();

        double maxYAxis = Double.MIN_VALUE;
        double minYAxis = Double.MAX_VALUE;
        for (int i = 0; i < lineTypes.length; i++) {
            double[] line = drawLine(timeChartFeeds, lineTypes[i]);
            if (line[0] > maxYAxis) {
                maxYAxis = line[0];
            }
            if (line[1] < minYAxis) {
                minYAxis = line[1];
            }
            
        }
        getYAxis().setAxisMax(maxYAxis + Math.max((maxYAxis / 100 * 5), 5));
        getYAxis().setAxisMin(minYAxis+Math.max((minYAxis / 100 * 5), 5));

        getXAxis().setAxisMax(timeChartFeeds.getValuesLenght());

    }

    private double[] drawLine(ChartFeed timeChartFeeds, LineType lineType) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        addCurve();
        // getCurve().setLegendLabel(lineType.name());
        getCurve().getSymbol().setHeight(2);
        getCurve().getSymbol().setWidth(3);
        getCurve().getSymbol().setBorderWidth(3);
        getCurve().getSymbol().setSymbolType(SymbolType.LINE);

        int size = GWT.isScript() ? (timeChartFeeds.getValuesLenght()) : 2;
        // int size = timeChartFeeds.getValuesLenght();
        for (int i = 0; i < size; i++) {
            double value = timeChartFeeds.getValues(lineType.getIndex(), i);
            if (value > max)
                max = value;
            if (value < min) 
                min = value;
            getCurve().addPoint(i, value);
        }
        return new double[]{max,min};
    }

}
