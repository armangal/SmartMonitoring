package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.Arrays;

public class ChartFeed
    implements Serializable {

    private static final long serialVersionUID = 1L;
    private long values[][];
    /**
     * the amount of lines
     */
    private int valuesLenght;
    /**
     * the length of the lines
     */
    private int valuesAmount;

    public ChartFeed() {}

    /**
     * @param valuesLenght -the length of the lines
     * @param valuesAmount -the amount of lines
     */
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

    /**
     * the length of the lines
     */
    public int getValuesAmount() {
        return valuesAmount;
    }

    /**
     * the amount of lines
     */
    public int getValuesLenght() {
        return valuesLenght;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChartFeed [values=");
        for (int i = 0; i < values.length; i++) {
            builder.append(Arrays.toString(values[i]));
        }
        builder.append("]");
        return builder.toString();
    }
}
