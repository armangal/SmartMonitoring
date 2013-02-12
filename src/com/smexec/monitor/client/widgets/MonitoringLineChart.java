package com.smexec.monitor.client.widgets;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import com.smexec.monitor.shared.ChartFeed;

public class MonitoringLineChart
    extends Composite {

    private LineChart chart;
    private FlowPanel fp = new FlowPanel();

    private LineType[] lineTypes;
    private String title;
    private String yColumnName;
    private String xColumnname;

    public MonitoringLineChart(LineType[] lineTypes, String yColumnName, String xColumnname, String title) {
        this.lineTypes = lineTypes;
        this.yColumnName = yColumnName;
        this.xColumnname = xColumnname;
        this.title = title;

        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                try {
                    // Create and attach the chart
                    chart = new LineChart();
                    chart.setHeight("100%");
                    fp.add(chart);
                } catch (Exception e) {
                    Log.error(e.getMessage());
                }
            }
        });
        initWidget(fp);
    }

    public void updateChart(ChartFeed timeChartFeeds) {
        try {
            // draw();
            // if (1 == 1) {
            // return;
            // }

            double maxYAxis = Double.MIN_VALUE;
            double minYAxis = Double.MAX_VALUE;

            // chart.clearChart();
            // Prepare the data
            DataTable dataTable = DataTable.create();
            dataTable.addColumn(ColumnType.STRING, xColumnname);
            // adding lines
            for (int i = 0; i < lineTypes.length; i++) {
                dataTable.addColumn(ColumnType.NUMBER, lineTypes[i].getName());
            }

            dataTable.addRows(timeChartFeeds.getValuesLenght());

            // create X values on chart
            for (int i = 0; i < timeChartFeeds.getValuesLenght(); i = i + timeChartFeeds.getValuesLenght() / 10) {
                dataTable.setValue(i, 0, String.valueOf(i));
            }

            for (int i = 0; i < lineTypes.length; i++) {
                double[] line = drawLine(timeChartFeeds, lineTypes[i], dataTable);
                if (line[0] > maxYAxis) {
                    maxYAxis = line[0];
                }
                if (line[1] < minYAxis) {
                    minYAxis = line[1];
                }

            }

            // Set options
            LineChartOptions options = LineChartOptions.create();
            options.setBackgroundColor("#f0f0f0");
            options.setFontName("Tahoma");
            options.setTitle(title);
            options.setHAxis(HAxis.create(xColumnname));
            options.setVAxis(VAxis.create(yColumnName));

            // Draw the chart
            chart.draw(dataTable, options);
        } catch (Exception e) {
            Log.error("MonitoringLineChart.update:, " + e.getMessage());
        }
    }

    public void clean() {
        try {
            chart.clearChart();
        } catch (Exception e) {
            Log.error("MonitoringLineChart.clean:, " + e.getMessage());
        }
    }

    private double[] drawLine(ChartFeed timeChartFeeds, LineType lineType, DataTable dataTable) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < timeChartFeeds.getValuesLenght(); i++) {
            double value = timeChartFeeds.getValues(lineType.getIndex(), i);
            if (value > max)
                max = value;
            if (value < min)
                min = value;

            dataTable.setValue(i, lineType.getIndex() + 1, value);
        }

        return new double[] {max, min};
    }

    @Deprecated
    private void draw() {
        String[] lines = new String[] {"line1", "line2", "line3"};
        int[] paramsX = new int[] {2003, 2004, 2005, 2006, 2007, 2008};
        int[][] paramsY = new int[][] { {1336060, 1538156, 1576579, 1600652, 1968113, 1901067}, {400361, 366849, 440514, 434552, 393032, 517206},
                                       {1001582, 1119450, 993360, 1004163, 979198, 916965}};

        // Prepare the data
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.STRING, "Year");
        for (int i = 0; i < lines.length; i++) {
            dataTable.addColumn(ColumnType.NUMBER, lines[i]);
        }

        dataTable.addRows(paramsX.length);

        for (int i = 0; i < paramsX.length; i++) {
            dataTable.setValue(i, 0, String.valueOf(paramsX[i]));
        }
        for (int col = 0; col < paramsY.length; col++) {
            for (int row = 0; row < paramsY[col].length; row++) {
                dataTable.setValue(row, col + 1, paramsY[col][row]);
            }
        }

        // Set options
        LineChartOptions options = LineChartOptions.create();
        options.setBackgroundColor("#f0f0f0");
        options.setFontName("Tahoma");
        options.setTitle("Yearly Coffee ");
        options.setHAxis(HAxis.create("Year"));
        options.setVAxis(VAxis.create("Cups"));

        // Draw the chart
        chart.draw(dataTable, options);
    }
}
