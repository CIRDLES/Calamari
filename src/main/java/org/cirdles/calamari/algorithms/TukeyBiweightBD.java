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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import static org.cirdles.calamari.prawn.PrawnRunFractionParser.bigDecimalSqrtBabylonian;
import org.cirdles.calamari.shrimp.ValueModel;

/**
 * From Ken Ludwig's Squid VBA code for use with Shrimp prawn files data
 * reduction. Note code extracted by Simon Bodorkos in emails to bowring
 * Feb.2016
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public final class TukeyBiweightBD {

    public static ValueModel calculateTukeyBiweightMean(String name, double tuningConstant, double[] values) {
        // guarantee termination
        BigDecimal epsilon = BigDecimal.ONE.movePointLeft(10);
        int iterationMax = 100;
        int iterationCounter = 0;

        int n = values.length;
        // initial mean is median
        BigDecimal mean = calculateMedian(values);

        // initial sigma is median absolute deviation from mean = median (MAD)
        double deviations[] = new double[n];
        for (int i = 0; i < values.length; i++) {
            deviations[i] = StrictMath.abs(values[i] - mean.doubleValue());
        }
        BigDecimal sigma = calculateMedian(deviations);

        BigDecimal previousMean;
        BigDecimal previousSigma;

        do {
            iterationCounter++;
            previousMean = mean;
            previousSigma = sigma;

            // init to zeroes
            BigDecimal[] deltas = new BigDecimal[n];
            BigDecimal[] u = new BigDecimal[n];
            BigDecimal sa = BigDecimal.ZERO;
            BigDecimal sb = BigDecimal.ZERO;
            BigDecimal sc = BigDecimal.ZERO;

            BigDecimal tee = new BigDecimal(tuningConstant).multiply(sigma);

            for (int i = 0; i < n; i++) {
                deltas[i] = new BigDecimal(values[i]).subtract(mean);
                if (tee.compareTo(deltas[i].abs()) > 0) {
                    deltas[i] = new BigDecimal(values[i]).subtract(mean);
                    u[i] = deltas[i].divide(tee, MathContext.DECIMAL128);
                    BigDecimal uSquared = u[i].multiply(u[i]);
                    sa = sa.add(deltas[i].multiply(BigDecimal.ONE.subtract(uSquared).pow(2)).pow(2));
                    sb = sb.add(BigDecimal.ONE.subtract(uSquared).multiply(BigDecimal.ONE.subtract(new BigDecimal(5.0).multiply(uSquared))));
                    sc = sc.add(u[i].multiply(BigDecimal.ONE.subtract(uSquared).pow(2)));
                }
            }
            
            sigma = bigDecimalSqrtBabylonian(sa.multiply(new BigDecimal(n))).divide(sb.abs(), MathContext.DECIMAL128);
            mean = previousMean.add(tee.multiply(sc).divide(sb, MathContext.DECIMAL128));

        } // both tests against epsilon must pass OR iterations top out
        // april 2016 Simon B discovered we need 101 iterations possible, hence the "<=" below
        while (((sigma.subtract(previousSigma).abs().divide(sigma, MathContext.DECIMAL128).compareTo(epsilon) > 0)//
                || mean.subtract(previousMean).abs().divide(mean, MathContext.DECIMAL128).compareTo(epsilon) > 0)//
                && (iterationCounter <= iterationMax));

        return new ValueModel(name, mean, "ABS", sigma);
    }

    /**
     * Calculates arithmetic median of array of doubles.
     *
     * @pre values has one element
     * @param values
     * @return
     */
    public static BigDecimal calculateMedian(double[] values) {
        BigDecimal median;

        // enforce precondition
        if (values.length == 0) {
            median = BigDecimal.ZERO;
        } else {
            double[] myValues = values.clone();

            Arrays.sort(myValues);
            int pos1 = (int) StrictMath.floor((myValues.length - 1.0) / 2.0);
            int pos2 = (int) StrictMath.ceil((myValues.length - 1.0) / 2.0);
            if (pos1 == pos2) {
                median = new BigDecimal(myValues[pos1]);
            } else {
                median = new BigDecimal((myValues[pos1] + myValues[pos2]) / 2.0).setScale(1, RoundingMode.HALF_EVEN);
            }
            int a = 2 % 3;
        }
        return median;
    }
}
