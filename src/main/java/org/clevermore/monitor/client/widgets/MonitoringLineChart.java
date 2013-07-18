/**
 * Copyright (C) 2013 Arman Gal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.clevermore.monitor.client.widgets;

import org.clevermore.monitor.shared.ChartFeed;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.CurveType;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

public class MonitoringLineChart<V extends Number, X extends Number>
    extends Composite {

    private CurveType curveType = CurveType.FUNCTION;
    private LineChart chart;
    private FlowPanel fp = new FlowPanel();

    private ILineType[] lineTypes;
    private String title;
    private String yColumnName;
    private String xColumnname;
    private ChartFeed<V, X> chartFeeds;
    private boolean lastRowAsColumn;
    private boolean initilized = false;

    public MonitoringLineChart(CurveType curveType, ILineType[] lineTypes, String yColumnName, String xColumnname, String title) {
        this(lineTypes, yColumnName, xColumnname, title);
        this.curveType = curveType;
    }

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

    public void updateChart(ChartFeed<V, X> chartFeeds) {
        updateChart(chartFeeds, false);
    }

    /**
     * @param chartFeeds
     * @param hasXLineValues - meaning the the last row in the chartFeeds includes X line values (usually
     *            time)
     */
    public void updateChart(ChartFeed<V, X> chartFeeds, boolean hasXLineValues) {
        try {
            if (!initilized) {
                this.chartFeeds = chartFeeds;
                this.lastRowAsColumn = hasXLineValues;
                return;
            }

            // Prepare the data
            DataTable dataTable = DataTable.create();
            dataTable.addColumn(ColumnType.STRING, xColumnname); // 0 index

            // adding lines
            for (int i = 0; i < lineTypes.length; i++) {
                dataTable.addColumn(lineTypes[i].getColumnType(), lineTypes[i].getName());
            }

            dataTable.addRows(chartFeeds.getValuesLenght());
            Log.debug("Adding:" + chartFeeds.getValuesLenght() + " rows to chart");

            if (hasXLineValues) {
                for (int i = 0; i < chartFeeds.getValuesLenght(); i++) {
                    dataTable.setValue(i, 0, String.valueOf(chartFeeds.getXLineValues()[i]));
                }
            } else {
                // create X values on chart
                for (int i = 0; i < chartFeeds.getValuesLenght(); i++) {
                    dataTable.setValue(i, 0, String.valueOf(i));
                }
            }

            for (int i = 0; i < lineTypes.length; i++) {
                drawLine(chartFeeds, lineTypes[i], dataTable);
            }

            // Set options
            LineChartOptions options = LineChartOptions.create();
            options.setBackgroundColor("#f0f0f0");
            options.setFontName("Tahoma");
            options.setTitle(title);
            options.setHAxis(HAxis.create(xColumnname));
            options.setVAxis(VAxis.create(yColumnName));
            options.setCurveType(curveType);
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
            // Log.error("MonitoringLineChart.clean:, " + e.getMessage(), e);
        }
    }

    private void drawLine(ChartFeed<V, X> chartFeeds, ILineType lineType, DataTable dataTable) {
        @SuppressWarnings("unchecked")
        V prevNormalValue = (V) new Integer(Integer.MIN_VALUE);

        Log.debug("Adding line:" + lineType.getName() + ":" + lineType.getIndex() + " with values:" + chartFeeds.getValuesLenght());
        // Log.debug("Data:" + Arrays.toString(chartFeeds.getValues()[lineType.getIndex()]));
        for (int i = 0; i < chartFeeds.getValuesLenght(); i++) {
            V value = chartFeeds.getValues(lineType.getIndex(), i);

            if (value.intValue() == Integer.MIN_VALUE) {
                // this case means that this line do not have valid value for current "period" or "Y"
                if (prevNormalValue.intValue() != Integer.MIN_VALUE) {
                    // draw previous value is case it's valid one, we're OK to start drawing the line from a
                    // middle of the chart
                    dataTable.setValue(i, lineType.getIndex() + 1, prevNormalValue.doubleValue());
                }
                continue;
            }
            prevNormalValue = value;

            dataTable.setValue(i, lineType.getIndex() + 1, value.doubleValue());
        }

    }

}
