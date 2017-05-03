/* 
 * Copyright 2006-2017 CIRDLES.org.
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
package org.cirdles.calamari.tasks.expressions.functions;

import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.ludwig.squid25.SquidConstants;

/**
 *
 * @author James F. Bowring
 */
public class ConcordiaTW extends Function {

    /**
     * Provides the functionality of Squid's robReg by calling robustReg2 and
     * returning "Slope", "SlopeErr", "Y-Intercept", "Y-IntErr" and encoding the
     * labels for each cell of the values array produced by eval2Array.
     *
     * @see
     * https://raw.githubusercontent.com/CIRDLES/LudwigLibrary/master/vbaCode/squid2.5Basic/Resistant.bas
     */
    public ConcordiaTW() {
        name = "concordiaTW";
        argumentCount = 4;
        precedence = 4;
        rowCount = 1;
        colCount = 4;
        labelsForValues = new String[][]{{"Slope", "SlopeErr", "Y-Intercept", "Y-IntErr"}};
    }

    /**
     * Requires that child 0 and child 1 are each a VariableNode that evaluates
     * to a double array with one column and a row for each member of
     * shrimpFractions. Child 2 and child 3 are each a BooleanNode that
     * evaluates to true or false. Child 2 and 3 are currently ignored but exist
     * for compatibility with Squid2.5.
     *
     * @param childrenET list containing child 0 through 3
     * @param shrimpFractions a list of shrimpFractions
     * @return the double[1][3] array of slope, slopeErr, y-Intercept, y-IntErr
     */
    @Override
    public double[][] eval2Array(
            List<ExpressionTreeInterface> childrenET, List<ShrimpFractionExpressionInterface> shrimpFractions) {

        double[][] retVal;
        try {
            double[] xValues = transposeColumnVector(childrenET.get(0).eval2Array(shrimpFractions), 0);
            double[] yValues = transposeColumnVector(childrenET.get(1).eval2Array(shrimpFractions), 0);
            double[] robustReg2 = org.cirdles.ludwig.isoplot3.Pub.robustReg2(xValues, yValues);
            double slopeErr = Math.abs(robustReg2[2] - robustReg2[1]) / 2.0;
            double yIntErr = Math.abs(robustReg2[6] - robustReg2[5]) / 2.0;
            retVal = new double[][]{{robustReg2[0], slopeErr, robustReg2[3], yIntErr}};
        } catch (ArithmeticException e) {
            retVal = new double[][]{{0.0, 0.0, 0.0, 0.0}};
        }

        return retVal;
    }

    /**
     *
     * @param childrenET the value of childrenET
     * @return
     */
    @Override
    public String toStringMathML(List<ExpressionTreeInterface> childrenET) {
        String retVal
                = "<mrow>"
                + "<mi>RobReg</mi>"
                + "<mfenced>";

        for (int i = 0; i < childrenET.size(); i++) {
            retVal += toStringAnotherExpression(childrenET.get(i)) + "&nbsp;\n";
        }

        retVal += "</mfenced></mrow>\n";

        return retVal;
    }

    public static void main(String[] args) {
        double[] tw = concordiaTW(6.65756816656, 6.65756816656 * 1.87624507122 / 100, 0.0552518706529, 0.0552518706529 * 1.96293438298 / 100);

        System.out.println(tw[0] + "    " + tw[1]*2);
    }

    /**
     * Ludwig: ' Returns Concordia age for T-W concordia data See Concordia
     * function for usage.
     *
     * @param r238U_206Pb
     * @param r238U_206Pb_1SigmaAbs
     * @param r207Pb_206Pb
     * @param r207Pb_206Pb_1SigmaAbs
     * @return
     */
    public static double[] concordiaTW(double r238U_206Pb, double r238U_206Pb_1SigmaAbs, double r207Pb_206Pb, double r207Pb_206Pb_1SigmaAbs) {
        double[] retVal = new double[]{0, 0, 0};

        if ((r238U_206Pb > 0.0) && (r207Pb_206Pb > 0.0)) {
            double[] concConvert = concConvert(r238U_206Pb, r238U_206Pb_1SigmaAbs, r207Pb_206Pb, r207Pb_206Pb_1SigmaAbs, 0.0, true);

            retVal = concordia(concConvert[0], concConvert[1], concConvert[2], concConvert[3], concConvert[4]);
        }

        return retVal;
    }

    /**
     * Ludwig: Returns Concordia age for Conv.-concordia data; Input the
     * Concordia X,err,Y,err,RhoXY Output is 1 range of 4 values -- t, t-error
     * (1-sigma apriori),MSWD,Prob-of-fit If a second row is included in the
     * output range, include names of the 4 result-values. Output errors are
     * always 2-sigma.
     *
     * Note: Assume only one data point for now, and 1-sigma absolute
     * uncertainty
     *
     * @param r207Pb_235U
     * @param r207Pb_235U_1SigmaAbs
     * @param r206Pb_238U
     * @param r206Pb_238U_1SigmaAbs
     * @param rho
     * @return
     */
    public static double[] concordia(double r207Pb_235U, double r207Pb_235U_1SigmaAbs, double r206Pb_238U, double r206Pb_238U_1SigmaAbs, double rho) {
        double[] retVal = new double[]{0, 0, 0};

        double inputData[] = new double[5];

        if ((r207Pb_235U > 0.0) && (r206Pb_238U > 0.0)) {
            inputData = new double[]{r207Pb_235U, r207Pb_235U_1SigmaAbs, r206Pb_238U, r206Pb_238U_1SigmaAbs, rho};

            retVal = concordiaAges(inputData);
        }

        return retVal;
    }

    /**
     * Ludwig: Calculate the weighted X-Y mean of the data pts (including error
     * correlation) & the "Concordia Age" & age-error of Xbar, Ybar. The
     * "Concordia Age" is the most probable age of a data point if one can
     * assume that the U/Pb ages of the true data point are precisely
     * concordant. Calculates the age & error both with & without uranium
     * decay-constant errors. See GCA 62, p. 665-676, 1998 for explanation.
     *
     * Note: this implementation only handles the case of one data point with no
     * lambda errors
     *
     * @param inputData
     * @return
     */
    public static double[] concordiaAges(double[] inputData) {
        // move to const
        double MAXLOG = 1E+308;
        double MINLOG = 1E-307;
        int MAXEXP = 709;

        double[] retVal = new double[]{0.0, 0.0, 0.0};

        double xBar = inputData[0];
        double errX = inputData[1];
        double yBar = inputData[2];
        double errY = inputData[3];
        double rhoXY = inputData[4];
        double probEquiv = 1.0;
        double[][] vcXY = new double[2][2];

        double xConc = xBar;
        double yConc = yBar;
        vcXY[0][0] = errX * errX;
        vcXY[1][1] = errY * errY;
        vcXY[0][1] = rhoXY * errX * errY;
        vcXY[1][0] = vcXY[0][1];
        double trialAge = 1.0 + yConc;
        double tNLE = 0.0;

        if ((trialAge >= MINLOG) && (trialAge <= MAXLOG) && (yConc > 0.0)) {
            tNLE = Math.log(trialAge) / SquidConstants.lambda238;
            trialAge = tNLE;

            tNLE = ageNLE(xConc, yConc, vcXY, tNLE)[0];
            if (tNLE > 0.0) {
                double SumsAgeOneNLE = concordiaSums(xConc, yConc, vcXY, tNLE)[0];
                double MswdAgeOneNLE = SumsAgeOneNLE;
                double SigmaAgeNLE = varTcalc(vcXY, tNLE)[0];
//                double SumsAgeManyNLE = SumsXY + SumsAgeOneNLE;
//                double MswdAgeManyNLE = SumsAgeManyNLE / dfT;

                retVal = new double[]{tNLE, SigmaAgeNLE, MswdAgeOneNLE};
            }

        }
        return retVal;
    }

    /**
     * Ludwig: ' Calculate the variance in age for a single assumed-concordant
     * data point on the Conv. U/Pb concordia diagram (with or without taking
     * into account the uranium decay-constant errors). See GCA v62, p665-676,
     * 1998 for explanation.
     *
     * @param covariance
     * @param t
     * @return simaT - uncertainty in age t
     */
    public static double[] varTcalc(double[][] covariance, double t) {

        // move to const
        double MAXLOG = 1E+308;
        double MINLOG = 1E-307;
        int MAXEXP = 709;

        double[] retVal = new double[]{0.0};

        double SigmaT = 0.0;
        double e5 = SquidConstants.lambda235 * t;
        if (Math.abs(e5) <= MAXEXP) {
            e5 = Math.exp(e5);
            double e8 = Math.exp(SquidConstants.lambda238 * t);
            double Q5 = SquidConstants.lambda235 * e5;
            double Q8 = SquidConstants.lambda238 * e8;
            double Xvar = covariance[0][0];
            double Yvar = covariance[1][1];

            double Cov = covariance[0][1];
            double[] inverted = inv2x2(covariance[0][0], covariance[1][1], covariance[0][1]);

            double Fisher = Q5 * Q5 * inverted[0] + Q8 * Q8 * inverted[1] + 2.0 * Q5 * Q8 * inverted[2];
            // Fisher is the expected second derivative with respect to T of the
            //  sums-of-squares of the weighted residuals.
            if (Fisher > 0.0) {
                retVal[0] = Math.sqrt(1.0 / Fisher);
            }

        }

        return retVal;
    }

    /**
     * Ludwig: Calculate the sums of the squares of the weighted residuals for a
     * single Conv.-Conc. X-Y data point, where the true value of each of the
     * data pts is assumed to be on the same point on the concordia curve, &
     * where the decay constants that describe the concordia curve have known
     * uncertainties. See GCA 62, p. 665-676, 1998 for explanation.
     *
     * @param xConc double Concordia x-axis ratio
     * @param yConc double Concordia y-axis ratio
     * @param t Age in annum
     * @return double[1] concordiaSums
     */
    public static double[] concordiaSums(double xConc, double yConc, double[][] covariance, double t) {
        // move to const
        double MAXLOG = 1E+308;
        double MINLOG = 1E-307;
        int MAXEXP = 709;

        double[] retVal = new double[]{0.0};

        double e5 = SquidConstants.lambda235 * t;

        if (Math.abs(e5) <= MAXEXP) {
            e5 = Math.exp(e5);
            double e8 = Math.exp(SquidConstants.lambda238 * t);
            double Ee5 = e5 - 1.0;
            double Ee8 = e8 - 1.0;
            double Rx = xConc - Ee5;
            double Ry = yConc - Ee8;

            double[] inverted = inv2x2(covariance[0][0], covariance[1][1], covariance[0][1]);

            retVal[0] = Rx * Rx * inverted[0] + Ry * Ry * inverted[1] + 2 * Rx * Ry * inverted[2];
        }
        return retVal;
    }

    /**
     * Age No Lambda Errors. Using a 2-D Newton's method, find the age for a
     * presumed-concordant point on the U-Pb Concordia diagram that minimizes
     * Sums, assuming no decay-constant errors. See GCA 62, p. 665-676, 1998 for
     * explanation.
     *
     * @param xVal
     * @param yVal
     * @param covariance double[2][2] covariance matrix
     * @param trialAge
     * @return double[1] Age in annum??
     */
    public static double[] ageNLE(double xVal, double yVal, double[][] covariance, double trialAge) {

        // move to const
        double MAXLOG = 1E+308;
        double MINLOG = 1E-307;
        int MAXEXP = 709;

        double[] retVal = new double[]{0.0};

        int ct = 0;
        double T;

        int maxCT = 1000;
        double toler = 0.000001;
        double testToler = 1.0;

        double[] inverted = inv2x2(covariance[0][0], covariance[1][1], covariance[0][1]);

        double t2 = trialAge;
        do {
            ct++;
            T = t2;
            double e5 = SquidConstants.lambda235 * T;
            if ((ct < maxCT) && (Math.abs(e5) <= MAXEXP)) {
                e5 = Math.exp(e5);
                double e8 = Math.exp(SquidConstants.lambda238 * T);
                double Ee5 = e5 - 1.0;
                double Ee8 = e8 - 1.0;
                double Q5 = SquidConstants.lambda235 * e5;
                double Q8 = SquidConstants.lambda238 * e8;
                double Qq5 = SquidConstants.lambda235 * Q5;
                double Qq8 = SquidConstants.lambda238 * Q8;
                double Rx = xVal - Ee5;
                double Ry = yVal - Ee8;
                // First derivative of T w.r.t. S, times -0.5
                double d1 = Rx * Q5 * inverted[0] + Ry * Q8 * inverted[1] + (Ry * Q5 + Rx * Q8) * inverted[2];
                // Second derivative of T w.r.t. S, times +0.5
                double d2a = (Q5 * Q5 + Qq5 * Rx) * inverted[0] + (Q8 * Q8 + Qq8 * Ry) * inverted[1];
                double d2b = (2 * Q5 * Q8 + Ry * Qq5 + Rx * Qq8) * inverted[2];
                double d2 = d2a + d2b;
                if (d2 != 0.0) {
                    double Incr = d1 / d2;
                    testToler = Math.abs(Incr / T);
                    t2 = T + Incr;
                    retVal[0] = t2;
                }
            }
        } while (testToler < toler);

        return retVal;

    }

    /**
     * Ludwig: Invert a symmetric 2x2 matrix.
     *
     * @param xx matrix element 0,0
     * @param yy matrix element 1,1
     * @param xy matrix element 0,1
     * @return double[3] containing inverted iXX, iYY, iXY
     */
    public static double[] inv2x2(double xx, double yy, double xy) {
        double[] retVal = new double[]{0.0, 00, 0.0};

        double determinant = xx * yy - xy * xy;
        if (determinant != 0.0) {
            retVal = new double[]{yy / determinant, xx / determinant, -xy / determinant};
        }

        return retVal;
    }

    /**
     * Ludwig's comments: Convert T-W concordia data to Conv., or vice-versa eg
     * 238/206-207/206[-204/206] to/from 207/235-206/238[-204/238]. This
     * implementation is for 2D only for now.
     *
     * @param ratioX TW 238/206 or WC 207/235
     * @param ratioX_1SigmaAbs 1-sigma uncertainty for ratioX
     * @param ratioY TW 207/206 or WC 206/238
     * @param ratioY_1SigmaAbs 1-sigma uncertainty for ratioY
     * @param rhoXY correlation coefficient between uncertainties in ratioX and
     * ratioY
     * @param inTW true if data is TW, false if WC
     * @return double[5] of conversions: ratioX, ratioX_1SigmaAbs, ratioY,
     * ratioY_1SigmaAbs, rhoXY
     */
    public static double[] concConvert(//isoplot3.cmc.bas
            double ratioX, double ratioX_1SigmaAbs, double ratioY, double ratioY_1SigmaAbs, double rhoXY, boolean inTW) {
        double[] retVal;

        double xP = Math.abs(ratioX_1SigmaAbs / ratioX);
        double yP = Math.abs(ratioY_1SigmaAbs / ratioY);

        double xP2 = xP * xP;
        double yP2 = yP * yP;

        double abP = 0.0;
        double a = 0.0;
        double b = 0.0;
        double aP = 0.0;
        double bP = 0.0;
        double rAB = 0.0;
        double test = xP2 + yP * yP - 2 * xP * yP * rhoXY;

        if (test >= 0.0) {
            abP = Math.sqrt(test);
            if (inTW) {
                a = ratioY / ratioX * SquidConstants.uRatio; // 207/235
                b = 1.0 / ratioX; // 206/238
                if (abP != 0.0) {
                    aP = abP;
                    bP = xP;
                    rAB = (xP - yP * rhoXY) / abP;
                }
            } else {
                a = 1.0 / ratioY;  // 238/206
                b = ratioX / ratioY / SquidConstants.uRatio; // 207/206
                aP = yP;
                bP = abP;
                if (abP != 0.0) {
                    rAB = (yP - xP * rhoXY) / abP;
                }
            }
        }

        if (Math.abs(rAB) > 1.0) {
            retVal = new double[]{a, 0.0, b, 0.0, rAB};
        } else {
            retVal = new double[]{a, aP * a, b, bP * b, rAB};
        }

        return retVal;
    }

}
