/*
 * Copyright 2006-2016 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.calamari.algorithms;

import java.math.BigDecimal;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.cirdles.calamari.shrimp.ValueModel;

/**
 * From Ken Ludwig's Squid VBA code for use with Shrimp prawn files data
 * reduction. Note code extracted by Simon Bodorkos in emails to bowring
 * Feb.2016
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public final class TukeyBiweight {

    public static ValueModel calculateTukeyBiweightMean(String name, double tuningConstant, double[] values) {
        // guarantee termination
        double epsilon = 1e-10;
        int iterationMax = 100;
        int iterationCounter = 0;

        int n = values.length;
        // initial mean is median
        double mean = calculateMedian(values);

        // initial sigma is median absolute deviation from mean = median (MAD)
        double deviations[] = new double[n];
        for (int i = 0; i < values.length; i++) {
            deviations[i] = StrictMath.abs(values[i] - mean);
        }
        double sigma = calculateMedian(deviations);

        double previousMean;
        double previousSigma;

        do {
            iterationCounter++;
            previousMean = mean;
            previousSigma = sigma;

            // init to zeroes
            double[] deltas = new double[n];
            double[] u = new double[n];
            double sa = 0.0;
            double sb = 0.0;
            double sc = 0.0;

            double tee = tuningConstant * sigma;

            for (int i = 0; i < n; i++) {
                deltas[i] = values[i] - mean;
                if (StrictMath.abs(deltas[i]) < tee) {
                    deltas[i] = values[i] - mean;
                    u[i] = deltas[i] / tee;
                    double uSquared = u[i] * u[i];
                    sa += StrictMath.pow(deltas[i] * StrictMath.pow((1.0 - uSquared), 2), 2);
                    sb += (1.0 - uSquared) * (1.0 - 5.0 * uSquared);
                    sc += u[i] * StrictMath.pow(1.0 - uSquared, 2);
                }
            }
            sigma = StrictMath.sqrt(n * sa) / StrictMath.abs(sb);
            mean = previousMean + tee * sc / sb;

        } // both tests against epsilon must pass OR iterations top out
        // april 2016 Simon B discovered we need 101 iterations possible, hence the "<=" below
        while (((StrictMath.abs(sigma - previousSigma) / sigma > epsilon)//
                || (StrictMath.abs(mean - previousMean) / mean > epsilon))//
                && (iterationCounter <= iterationMax));

        return new ValueModel(name, new BigDecimal(mean), "ABS", new BigDecimal(sigma));
    }

    /**
     * Calculates arithmetic median of array of doubles.
     *
     * @pre values has one element
     * @param values
     * @return
     */
    public static double calculateMedian(double[] values) {
        double median;

        // enforce precondition
        if (values.length == 0) {
            median = 0.0;
        } else {
            DescriptiveStatistics stats = new DescriptiveStatistics();

            // Add the data from the array
            for (int i = 0; i < values.length; i++) {
                stats.addValue(values[i]);
            }
            median = stats.getPercentile(50);
        }

        return median;
    }
}
