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
package com.smexec.monitor.shared;

import java.io.Serializable;
import java.util.Arrays;

/**
 * data holder to be used for charts drawing
 * 
 * @author armang
 */
public class ChartFeed<V extends Number, X extends Number>
    implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * values to draw the lines
     */
    private V values[][];

    private X xLineValues[];
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
     * @param T values[][] = values[the amount of lines][the length of the lines],<br>
     *            example new Integer[5][100] >> five lines each have 100 elements
     */
    public ChartFeed(V values[][], X xLineValues[]) {
        this.values = values;
        this.xLineValues = xLineValues;
        this.valuesAmount = values.length;
        this.valuesLenght = values[0].length;
    }

    public V[][] getValues() {
        return values;
    }

    public X[] getXLineValues() {
        return xLineValues;
    }

    public V getValues(int x, int y) {
        return values[x][y];
    }

    public V getLastValues(int x) {
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
