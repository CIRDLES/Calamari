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

import Jama.Matrix;
import org.apache.commons.math3.distribution.FDistribution;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 * Based on Simon Bodorkos' interpreation of Ludwig's code:
 * https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Step-4
 */
public final class WeightedMeanCalculators {

    public static wtdLinCorrResults WtdLinCorr(boolean linReg, double[] y, double[][] sigRho, double[] x) {

        wtdLinCorrResults results = new wtdLinCorrResults();

        int avg1LinRegr2 = linReg ? 2 : 1;
        int n = y.length;
        double[] mswdRatList = new double[]{0.0, 0.1, 0.15, 0.2, 0.2, 0.25};

        double mswdRatToler = (n > 7) ? 0.3 : mswdRatList[n - avg1LinRegr2];
        int maxRej = (int) StrictMath.ceil((n - avg1LinRegr2) / 8);

        double minProb = 0.1;
        double wLrej = 0;
        double pass = 0;
        double minIndex = -1;

        double[] y1 = y.clone();
        double[] y2 = y.clone();
        double[] x1 = x.clone();
        double[] x2 = x.clone();
        double[][] sigRho1 = sigRho.clone();
        double[][] sigRho2 = sigRho.clone();

        double[] sigmaY = new double[n];
        for (int i = 0; i < n; i++) {
            sigmaY[i] = sigRho[i][i];
        }

        double f = StrictMath.max(TukeyBiweight.calculateMedian(sigmaY), 1e-10);
        for (int i = 0; i < n; i++) {
            sigRho1[i][i] = StrictMath.max(sigRho1[i][i], f);
            sigRho2[i][i] = sigRho1[i][i];
        }

        return results;
    }

    public static class wtdLinCorrResults {

        private boolean bad;

        public wtdLinCorrResults() {
            bad = false;
        }

    }

    public static deletePointResults deletePoint(int rejPoint, double[] y1, double[][] sigRho1, double[] x1) {

        deletePointResults results = new deletePointResults();

        int n = y1.length;
        double[] y2 = new double[n - 1];
        double[][] sigRho2 = new double[n - 1][n - 1];
        double[] x2 = new double[0];
        boolean linReg = false;
        if (x1.length == n) {
            x2 = new double[n - 1];
            linReg = true;
        }

        for (int j = 0; j < n; j++) {
            int m = j + 1;
            int p = j + 2;

            if (j < rejPoint) {
                sigRho2[j][j] = sigRho1[j][j];
                y2[j] = y1[j];
                if (linReg) {
                    x2[j] = x1[j];
                }
            } else if (j < (n - 1)) {
                sigRho2[j][j] = sigRho1[m][m];
                y2[j] = y1[m];
                if (linReg) {
                    x2[j] = x1[m];
                }
            }

            if (j < (rejPoint - 1)) {
                sigRho2[j][m] = sigRho1[j][m];
                sigRho2[m][j] = sigRho1[m][j];
            } else if ((j == (rejPoint - 1)) && (m < (n - 1))) {
                sigRho2[j][m] = 0.0;
                sigRho2[m][j] = 0.0;
            } else if (j < (n - 2)) {
                sigRho2[j][m] = sigRho1[m][p];
                sigRho2[m][j] = sigRho1[p][m];
            }
        }

        results.setY2(y2);
        results.setSigRho2(sigRho2);
        results.setX2(x2);

        return results;
    }

    public static class deletePointResults {

        private double[] y2;
        private double[][] sigRho2;
        private double[] x2;

        public deletePointResults() {
            y2 = new double[0];
            sigRho2 = new double[0][0];
            x2 = new double[0];
        }

        /**
         * @return the y2
         */
        public double[] getY2() {
            return y2;
        }

        /**
         * @param y2 the y2 to set
         */
        public void setY2(double[] y2) {
            this.y2 = y2;
        }

        /**
         * @return the sigRho2
         */
        public double[][] getSigRho2() {
            return sigRho2;
        }

        /**
         * @param sigRho2 the sigRho2 to set
         */
        public void setSigRho2(double[][] sigRho2) {
            this.sigRho2 = sigRho2;
        }

        /**
         * @return the x2
         */
        public double[] getX2() {
            return x2;
        }

        /**
         * @param x2 the x2 to set
         */
        public void setX2(double[] x2) {
            this.x2 = x2;
        }

    }

    public static WtdAvCorrResults wtdAvCorr(double[] values, double[][] varCov) {

        WtdAvCorrResults results = new WtdAvCorrResults();

        int n = varCov.length;
        Matrix omegaInv = new Matrix(varCov);
        Matrix omega = omegaInv.inverse();

        double numer = 0.0;
        double denom = 0.0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                numer += (values[i] + values[j]) * omega.get(i, j);
                denom += omega.get(i, j);
            }
        }

        // test denom
        if (denom > 0.0) {
            double meanVal = numer / denom / 2.0;
            double meanValSigma = Math.sqrt(1.0 / denom);

            double[][] unwtdResidsArray = new double[n][1];
            for (int i = 0; i < n; i++) {
                unwtdResidsArray[i][0] = values[i] - meanVal;
            }

            Matrix unwtdResids = new Matrix(unwtdResidsArray);
            Matrix transUnwtdResids = unwtdResids.transpose();
            Matrix product = transUnwtdResids.times(omega);
            Matrix sumWtdResids = product.times(unwtdResids);

            double mswd = 0.0;
            double prob = 0.0;
            if (n > 1) {
                mswd = sumWtdResids.get(0, 0) / (n - 1);

                // http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/distribution/FDistribution.html
                FDistribution fdist = new org.apache.commons.math3.distribution.FDistribution((n - 1), 1E9);
                prob = 1.0 - fdist.cumulativeProbability(mswd);
            }

            results.setBad(false);
            results.setMeanVal(meanVal);
            results.setMeanValSigma(meanValSigma);
            results.setMswd(mswd);
            results.setProb(prob);
        }

        return results;

    }

    public static class WtdAvCorrResults {

        private boolean bad;
        private double meanVal;
        private double meanValSigma;
        private double mswd;
        private double prob;

        public WtdAvCorrResults() {
            bad = true;
            meanVal = 0.0;
            meanValSigma = 0.0;
            mswd = 0.0;
            prob = 0.0;
        }

        /**
         * @return the bad
         */
        public boolean isBad() {
            return bad;
        }

        /**
         * @param bad the bad to set
         */
        public void setBad(boolean bad) {
            this.bad = bad;
        }

        /**
         * @return the meanVal
         */
        public double getMeanVal() {
            return meanVal;
        }

        /**
         * @param meanVal the meanVal to set
         */
        public void setMeanVal(double meanVal) {
            this.meanVal = meanVal;
        }

        /**
         * @return the meanValSigma
         */
        public double getMeanValSigma() {
            return meanValSigma;
        }

        /**
         * @param meanValSigma the meanValSigma to set
         */
        public void setMeanValSigma(double meanValSigma) {
            this.meanValSigma = meanValSigma;
        }

        /**
         * @return the mswd
         */
        public double getMswd() {
            return mswd;
        }

        /**
         * @param mswd the mswd to set
         */
        public void setMswd(double mswd) {
            this.mswd = mswd;
        }

        /**
         * @return the prob
         */
        public double getProb() {
            return prob;
        }

        /**
         * @param prob the prob to set
         */
        public void setProb(double prob) {
            this.prob = prob;
        }
    }

    public static double[][] convertCorrelationsToCovariances(double[][] correlations) {
        // precondition: square matrix correlations
        int n = correlations.length;

        double[][] covariances = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    covariances[i][j] = correlations[i][j] * correlations[i][j];
                } else {
                    covariances[i][j] = correlations[i][j] * correlations[i][i] * correlations[j][j];
                }
            }
        }

        return covariances;
    }

    public static void main(String[] args) {
        double[] values = new double[]{33, 44, 55};
        double[][] varCov = convertCorrelationsToCovariances(new double[][]{{1, .5, .5}, {.5, .5, 1}});

        WtdAvCorrResults results = wtdAvCorr(values, varCov);

    }

}
