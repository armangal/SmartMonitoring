package com.smexec.monitor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.googlecode.gchart.client.GChart;
import com.smexec.monitor.shared.ChartFeed;

public class LineChart
    extends GChart {

    public LineChart() {
        setChartSize(200, 150);
        setBorderStyle("none");
        setStyleName("chart");

        // GChart.setDefaultSymbolBorderColors(new String[] {"#c5000b", "#00ff00", "#0000ff", "#004586",
        // "#ff420e", "#ffd320", "#7e0021", "#579d1c", "#83caff",
        // "#314004", "#aecf00", "#4b1f6f", "#ff950e", "#c5000b", "#0084d1"});
        // setChartTitle("<span>Times</span>");
        setChartFootnotesLeftJustified(true);
        // setChartFootnotes("<ol>" + "<li>Click on empty space to add a new point there.");
        // "<li>Click on any point to delete it."
        // + "<li>Points are added after the last inserted or deleted point." + "</ol>");

        // lock in a simple 0..100 range on each axis
        getXAxis().setAxisMin(0);
        getXAxis().setAxisMax(10);
        getXAxis().setTickCount(5);
        getXAxis().setHasGridlines(false);
        getXAxis().setTicksPerGridline(2); // full-period grid
        getXAxis().setTickLabelFontSize(10);
        getXAxis().setAxisLabelThickness(20);

        getYAxis().setHasGridlines(true);
        getYAxis().setTickCount(5); // ticks at: -1, -0.5, 0, 0.5, 1
        getYAxis().setTicksPerLabel(2); // labels at: -1, 0, 1
        getYAxis().setTickLabelThickness(10);
        getYAxis().setTickLabelFontSize(10);
        getYAxis().setAxisLabelThickness(20);

        setOptimizeForMemory(true);

        // switch back to GChart's built-in default colors
        // GChart.setDefaultSymbolBorderColors(GChart.DEFAULT_SYMBOL_BORDER_COLORS);
    }

    public void updateChart(ChartFeed timeChartFeeds, LineType[] lineTypes) {
        clearCurves();

        if (!GWT.isScript()) {
            return;
        }
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
        getYAxis().setAxisMax(maxYAxis + (maxYAxis / 100 * 5));
        getYAxis().setAxisMin(minYAxis);

        getXAxis().setAxisMax(timeChartFeeds.getValuesLenght() - 1);

    }

    private double[] drawLine(ChartFeed timeChartFeeds, LineType lineType) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        // TODO don'e create line each time!!!!
        addCurve();
        // getCurve().getSymbol().setHeight(1);
        // getCurve().getSymbol().setWidth(1);
        // getCurve().getSymbol().setBorderWidth(1);
        // getCurve().getSymbol().setBrushWidth(DEFAULT_WIDGET_WIDTH_UPPERBOUND);
        // getCurve().getSymbol().setSymbolType(SymbolType.LINE);

        getCurve().getSymbol().setSymbolType(SymbolType.LINE);
        getCurve().getSymbol().setFillThickness(8); // px line width
        getCurve().getSymbol().setBorderColor("#0c6ac1");
        getCurve().getSymbol().setBorderWidth(2);
        getCurve().getSymbol().setBackgroundColor("#c6defa");
        // getCurve().setLegendLabel("sin");
        getCurve().getSymbol().setWidth(0); // remove square symbols
        getCurve().getSymbol().setHeight(0); // marking each data point
        getCurve().getSymbol().setHoverSelectionWidth(1);
        getCurve().getSymbol().setHoverSelectionBackgroundColor("gray");
        // use a vertical line for the selection cursor
        getCurve().getSymbol().setHoverSelectionSymbolType(SymbolType.XGRIDLINE);
        // with annotation on top of this line (above chart)
        getCurve().getSymbol().setHoverAnnotationSymbolType(SymbolType.XGRIDLINE);
        getCurve().getSymbol().setHoverLocation(AnnotationLocation.NORTH);
        // small gap between plot area and hover popup HTML
        getCurve().getSymbol().setHoverYShift(5);
        // note use of custom hover parameter names ${sin}, ${cos}, etc.
        getCurve().getSymbol()
                  .setHovertextTemplate(GChart.formatAsHovertext("<b><tt>&nbsp;sin=${sin}&nbsp;<br>&nbsp;cos=${cos}&nbsp;<br>&nbsp;tan=${tan}</tt></b>"));
        // tall brush so it touches independent of mouse y position
        getCurve().getSymbol().setBrushSize(25, 200);
        // so only point-to-mouse x-distance matters for hit testing
        getCurve().getSymbol().setDistanceMetric(1, 0);

        getCurve().getSymbol().setFillSpacing(0);

        // int size = GWT.isScript() ? (timeChartFeeds.getValuesLenght()) : 2;
        int size = timeChartFeeds.getValuesLenght();
        int nExisting = getCurve().getNPoints();
        for (int i = 0; i < size; i++) {
            double value = timeChartFeeds.getValues(lineType.getIndex(), i);
            if (value > max)
                max = value;
            if (value < min)
                min = value;
            if (i < nExisting) {
                getCurve().getPoint(i).setY(value);
            } else {
                getCurve().addPoint(i, value);
            }
        }
        return new double[] {max, min};
    }

}
