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

    private ILineType[] lineTypes;
    private String title;
    private String yColumnName;
    private String xColumnname;
    private ChartFeed chartFeeds;
    private boolean lastRowAsColumn;
    private boolean initilized = false;

    public MonitoringLineChart(ILineType[] lineTypes, String yColumnName, String xColumnname, String title) {
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
                    initilized = true;
                    if (chartFeeds != null) {
                        updateChart(chartFeeds, lastRowAsColumn);
                    }

                } catch (Exception e) {
                    Log.error(e.getMessage(), e);
                }
            }
        });
        initWidget(fp);
    }

    public void updateChart(ChartFeed chartFeeds) {
        updateChart(chartFeeds, false);
    }

    public void updateChart(ChartFeed chartFeeds, boolean lastRowAsColumn) {
        try {
            if (!initilized) {
                this.chartFeeds = chartFeeds;
                this.lastRowAsColumn = lastRowAsColumn;
                return;
            }

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

            dataTable.addRows(chartFeeds.getValuesLenght());

            if (lastRowAsColumn) {
                for (int i = 0; i < chartFeeds.getValuesLenght(); i++) {
                    dataTable.setValue(i, 0, String.valueOf(chartFeeds.getValues(lineTypes.length, i)));
                }
            } else {
                // create X values on chart
                for (int i = 0; i < chartFeeds.getValuesLenght(); i++) {
                    dataTable.setValue(i, 0, String.valueOf(i));
                }
            }

            for (int i = 0; i < lineTypes.length; i++) {
                double[] line = drawLine(chartFeeds, lineTypes[i], dataTable);
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
            String[] color = new String[lineTypes.length];

            for (int i = 0; i < lineTypes.length; i++) {
                color[i] = lineTypes[i].getLineColor();
            }
            options.setColors(color);

            // Draw the chart
            chart.draw(dataTable, options);
        } catch (Exception e) {
            Log.error("MonitoringLineChart.update:, " + e.getMessage(), e);
        }
    }

    public void clean() {
        try {
            chart.clearChart();
        } catch (Exception e) {
            //Log.error("MonitoringLineChart.clean:, " + e.getMessage(), e);
        }
    }

    private double[] drawLine(ChartFeed chartFeeds, ILineType lineType, DataTable dataTable) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        Log.debug("Adding line:" + lineType.getName() + ":" + lineType.getIndex() + " with values:" + chartFeeds.getValuesLenght());
        for (int i = 0; i < chartFeeds.getValuesLenght(); i++) {
            double value = chartFeeds.getValues(lineType.getIndex(), i);
            if (value > max)
                max = value;
            if (value < min)
                min = value;

            dataTable.setValue(i, lineType.getIndex() + 1, value);
        }

        return new double[] {max, min};
    }

}
