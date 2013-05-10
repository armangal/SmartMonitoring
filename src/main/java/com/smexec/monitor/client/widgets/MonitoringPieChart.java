package com.smexec.monitor.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;

public class MonitoringPieChart
    extends Composite {

    private PieChart chart;
    private FlowPanel fp = new FlowPanel();

    public MonitoringPieChart() {
        initWidget(fp);
        fp.setWidth("100px");
        fp.setHeight("100px");
        initialize();
    }

    private void initialize() {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                // Create and attach the chart
                chart = new PieChart();
                chart.setWidth("100px");
                chart.setHeight("100px");
                fp.add(chart);
                draw();
            }
        });
    }

    private void draw() {
        // Prepare the data
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.STRING, "Task");
        dataTable.addColumn(ColumnType.NUMBER, "Hours per Day");
        dataTable.addRows(2);
        dataTable.setValue(0, 0, "Work");
        dataTable.setValue(0, 1, 11);
        dataTable.setValue(1, 0, "Sleep");
        dataTable.setValue(1, 1, 7);

        // Set options
        PieChartOptions options = PieChartOptions.create();
        options.setBackgroundColor("#f0f0f0");

        // options.setColors(colors);
        options.setFontName("Tahoma");
        options.setIs3D(true);
        options.setPieResidueSliceColor("#000000");
        options.setPieResidueSliceLabel("Others");
        options.setSliceVisibilityThreshold(0.1);
        options.setTitle("So, how was your day?");
        options.setHeight(100);
        options.setWidth(100);

        // Draw the chart
        chart.draw(dataTable, options);
    }
}
