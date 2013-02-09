package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.Arrays;

public class ChartFeed
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private long values[][];
    private int valuesLenght;
    private int valuesAmount;

    public ChartFeed() {}

    public ChartFeed(int valuesLenght, int valuesAmount) {
        values = new long[valuesAmount][valuesLenght];
        this.valuesAmount = valuesAmount;
        this.valuesLenght = valuesLenght;
    }

    public long[][] getValues() {
        return values;
    }

    public long getValues(int x, int y) {
        return values[x][y];
    }

    public long getLastValues(int x) {
        return values[x][Math.max(valuesLenght - 1, 0)];
    }

    public int getValuesAmount() {
        return valuesAmount;
    }

    public int getValuesLenght() {
        return valuesLenght;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChartFeed [values=");
        for ( int i =0;i<values.length; i++){
            builder.append(Arrays.toString(values[i]));
        }
        builder.append("]");
        return builder.toString();
    }
}
