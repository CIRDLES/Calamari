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

    public static WtdLinCorrResults WtdLinCorr(boolean linReg, double[] y, double[][] sigRho, double[] x) {

        WtdLinCorrResults results = new WtdLinCorrResults();

        int avg1LinRegr2 = linReg ? 2 : 1;
        int n = y.length;
        double[] mswdRatList = new double[]{0.0, 0.1, 0.15, 0.2, 0.2, 0.25};

        double mswdRatToler = (n > 7) ? 0.3 : mswdRatList[n - avg1LinRegr2];
        int maxRej = (int) StrictMath.ceil((n - avg1LinRegr2) / 8);
        boolean[] rej = new boolean[n];

        double minProb = 0.1;
        double wLrej = 0;
        int pass = 0;
        int minIndex = -1;
        double minMSWD = 0.0;
        double maxProb = 0.0;

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

        boolean doContinue = true;
        int nw = n;
        DeletePointResults deletePointResults;
        double[] probW = new double[n + 1];
        double[] mswdW = new double[n + 1];
        double[] sigmaInterW = new double[n + 1];
        double[] interW = new double[n + 1];
        double[] prob = new double[n + 1];
        double[] slopeW = new double[n + 1];
        double[] slopeSigmaW = new double[n + 1];
        double[] covSlopeInterW = new double[n + 1];

        do {
            for (int i = 0; i < (n + 1); i++) {
                if (i > 0) {
                    deletePointResults = deletePoint(i, y1, sigRho1, x1);
                    y2 = deletePointResults.getY2().clone();
                    sigRho2 = deletePointResults.getSigRho2().clone();
                    nw = n - 1;
                }

                if ((nw == 1) && !linReg) {
                    probW[i] = 1.0;
                    mswdW[i] = 0.0;
                    sigmaInterW[i] = 1.0;
                    interW[i] = 1.0;
                } else if (linReg) {
                    // do nothing for now per Simon
                } else {
                    WtdAvCorrResults wtdAvCorrResults = wtdAvCorr(y2, convertCorrelationsToCovariances(sigRho2));
                    interW[i] = wtdAvCorrResults.getMswd();
                    sigmaInterW[i] = wtdAvCorrResults.getMeanValSigma();
                    mswdW[i] = wtdAvCorrResults.getMswd();
                    probW[i] = wtdAvCorrResults.getProb();
                }

                if (i == 0) {
                    if (probW[0] > 0.1) {
                        minIndex = 0;
                        minMSWD = mswdW[0];

                        // exit for loop of i
                        break;
                    }

                    maxProb = probW[0];
                }
            } // for loop of i

            if (minIndex == 0) {
                doContinue = false;
            } else {
                minIndex = 0;
                minMSWD = mswdW[0];

                for (int i = 1; i < (n + 1); i++) {
                    double mswdRat = mswdW[i] / StrictMath.max(1e-32, mswdW[0]);
                    if ((mswdRat < mswdRatToler) && (mswdW[i] < minMSWD) && (probW[i] > minProb)) {
                        rej[i] = true;
                        wLrej++;
                        minIndex = i;
                        maxProb = probW[i];
                        minMSWD = mswdW[i];
                    }
                }

                pass++;

                if ((pass > 0) && ((minIndex == 0) || (pass == maxRej) || (maxProb > 0.1))) {
                    doContinue = false;
                } else {
                    deletePointResults = deletePoint(minIndex - 1, y1, sigRho1, x1);
                    y2 = deletePointResults.getY2().clone();
                    sigRho2 = deletePointResults.getSigRho2().clone();
                    n = n - 1;

                    y1 = new double[n];
                    x1 = new double[n];
                    // HELP
                    sigRho1 = new double[n][n];

                    for (int i = 0; i < n; i++) {
                        y1[i] = y2[i];
                        if (linReg) {
                            x1[i] = x2[i];
                        }
                        for (int j = 0; j < n; j++) {
                            sigRho1[i][j] = sigRho2[i][j];
                        }
                    }
                }
            }
        } while (doContinue);

        double intercept = interW[minIndex];
        double sigmaIntercept = sigmaInterW[minIndex];
        double mswd = mswdW[minIndex];
        double probfit = probW[minIndex];

        double slope = 0.0;
        double sigmaSlope = 0.0;
        double covSlopeInter = 0.0;

        if (linReg && (minIndex > 0)) {
            slope = slopeW[minIndex];
            sigmaSlope = slopeSigmaW[minIndex];
            covSlopeInter = covSlopeInterW[minIndex];
        }
        
        if (probfit < 0.05){
            sigmaIntercept*= StrictMath.sqrt(mswd);
            
            if (linReg){
                sigmaSlope*= StrictMath.sqrt(mswd);
            }
        }
        
        results.setBad(false);
        results.setIntercept(intercept);
        results.setSigmaIntercept(sigmaIntercept);
        results.setMswd(mswd);
        results.setProbFit(probfit);

        return results;
    }

    public static class WtdLinCorrResults {

        private boolean bad;
        private double intercept;
        private double sigmaIntercept;
        private double mswd;
        private double probFit;

        public WtdLinCorrResults() {
            bad = true;
            intercept = 0.0;
            sigmaIntercept = 0.0;
            mswd = 0.0;
            probFit = 0.0;
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
         * @return the intercept
         */
        public double getIntercept() {
            return intercept;
        }

        /**
         * @param intercept the intercept to set
         */
        public void setIntercept(double intercept) {
            this.intercept = intercept;
        }

        /**
         * @return the sigmaIntercept
         */
        public double getSigmaIntercept() {
            return sigmaIntercept;
        }

        /**
         * @param sigmaIntercept the sigmaIntercept to set
         */
        public void setSigmaIntercept(double sigmaIntercept) {
            this.sigmaIntercept = sigmaIntercept;
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
         * @return the probFit
         */
        public double getProbFit() {
            return probFit;
        }

        /**
         * @param probFit the probFit to set
         */
        public void setProbFit(double probFit) {
            this.probFit = probFit;
        }

    }

    public static DeletePointResults deletePoint(int rejPoint, double[] y1, double[][] sigRho1, double[] x1) {

        DeletePointResults results = new DeletePointResults();

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

    public static class DeletePointResults {

        private double[] y2;
        private double[][] sigRho2;
        private double[] x2;

        public DeletePointResults() {
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
        // assume varCov is variance-covariance matrix (i.e. SigRho = false)

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
