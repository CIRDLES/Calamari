/*
 * Copyright 2016 CIRDLES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cirdles.calamari.prawn;

import org.cirdles.calamari.algorithms.PoissonLimitsCountLessThanEqual100;
import org.cirdles.calamari.algorithms.TukeyBiweight;
import org.cirdles.calamari.algorithms.TukeyBiweightBD;
import org.cirdles.calamari.algorithms.WeightedMeanCalculators;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.IsotopeRatioModelSHRIMP;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.shrimp.ShrimpFraction;
import org.cirdles.calamari.shrimp.ValueModel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.cirdles.calamari.algorithms.WeightedMeanCalculators.wtdLinCorr;
import static org.cirdles.calamari.algorithms.BigDecimalCustomAlgorithms.bigDecimalSqrtBabylonian;

/**
 * Parses run fractions from Prawn files into
 * {@link org.cirdles.calamari.shrimp.ShrimpFraction}s.
 */
public class PrawnFileRunFractionParser {

    private static final int HARD_WIRED_INDEX_OF_BACKGROUND = 2;
    private static final double SQUID_TINY_VALUE = 1e-32;
    private static final double ERROR_VALUE = -9.87654321012346;

    private String fractionID;
    private long dateTimeMilliseconds;
    private double[][] totalCounts;
    private double[][] totalCountsOneSigmaAbs;
    private double[][] totalCountsSBM;
    private BigDecimal[][] totalCountsBD;
    private BigDecimal[][] totalCountsOneSigmaAbsBD;
    private BigDecimal[][] totalCountsSBMBD;
    private int[][] rawPeakData;
    private int[][] rawSBMData;
    private int nSpecies;
    private int nScans;
    private int peakMeasurementsCount;
    private int deadTimeNanoseconds;
    private int sbmZeroCps;
    private List<PrawnFile.Run.RunTable.Entry> runTableEntries;
    private List<PrawnFile.Run.Set.Scan> scans;
    private double[] countTimeSec;
    private String[] namesOfSpecies;
    private double[][] timeStampSec;
    private double[][] trimMass;
    private double[][] netPkCps;
    private double[][] sbmCps;
    private double[][] pkFerr;
    private double[] totalCps;
    private Map<IsotopeNames, Integer> indexToSpeciesMap;
    private Map<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> isotopicRatios;

    public PrawnFileRunFractionParser() {
        dateTimeMilliseconds = 0l;
    }

    /**
     *
     * @param runFraction the value of runFraction
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @return
     */
    public ShrimpFraction processRunFraction(PrawnFile.Run runFraction, boolean useSBM, boolean userLinFits) {

        prepareRunFractionMetaData(runFraction);
        parseRunFractionData();
        calculateTotalPerSpeciesCPS();
        calculateIsotopicRatios(useSBM, userLinFits);

        ShrimpFraction shrimpFraction = new ShrimpFraction(fractionID, isotopicRatios);
        shrimpFraction.setDateTimeMilliseconds(dateTimeMilliseconds);
        shrimpFraction.setDeadTimeNanoseconds(deadTimeNanoseconds);
        shrimpFraction.setSbmZeroCps(sbmZeroCps);
        shrimpFraction.setCountTimeSec(countTimeSec);
        shrimpFraction.setNamesOfSpecies(namesOfSpecies);
        shrimpFraction.setPeakMeasurementsCount(peakMeasurementsCount);
        shrimpFraction.setTotalCounts(totalCounts);
        shrimpFraction.setTotalCountsOneSigmaAbs(totalCountsOneSigmaAbs);
        shrimpFraction.setTotalCountsSBM(totalCountsSBM);
        shrimpFraction.setTotalCountsBD(totalCountsBD);
        shrimpFraction.setTotalCountsOneSigmaAbsBD(totalCountsOneSigmaAbsBD);
        shrimpFraction.setTotalCountsSBMBD(totalCountsSBMBD);
        shrimpFraction.setTimeStampSec(timeStampSec);
        shrimpFraction.setTrimMass(trimMass);
        shrimpFraction.setRawPeakData(rawPeakData);
        shrimpFraction.setRawSBMData(rawSBMData);
        shrimpFraction.setTotalCps(totalCps);
        shrimpFraction.setNetPkCps(netPkCps);
        shrimpFraction.setPkFerr(pkFerr);
        shrimpFraction.setUseSBM(useSBM);
        shrimpFraction.setUserLinFits(userLinFits);

        // determine reference material status
        // hard coded for now
        if (fractionID.startsWith("T")) {
            shrimpFraction.setReferenceMaterial(true);
        }

        return shrimpFraction;
    }

    private void prepareRunFractionMetaData(PrawnFile.Run runFraction) {
        fractionID = runFraction.getPar().get(0).getValue();
        nSpecies = Integer.parseInt(runFraction.getPar().get(2).getValue());
        nScans = Integer.parseInt(runFraction.getPar().get(3).getValue());
        deadTimeNanoseconds = Integer.parseInt(runFraction.getPar().get(4).getValue());
        sbmZeroCps = Integer.parseInt(runFraction.getPar().get(5).getValue());
        runTableEntries = runFraction.getRunTable().getEntry();
        scans = runFraction.getSet().getScan();
        String[] firstIntegrations = runFraction.getSet().getScan().get(0).getMeasurement().get(0).getData().get(0).getValue().split(",");
        peakMeasurementsCount = firstIntegrations.length;

        String dateTime = runFraction.getSet().getPar().get(0).getValue() + " " + runFraction.getSet().getPar().get(1).getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            dateTimeMilliseconds = dateFormat.parse(dateTime).getTime();
        } catch (ParseException parseException) {
        }

        namesOfSpecies = new String[nSpecies];
        countTimeSec = new double[nSpecies];
        for (int i = 0; i < nSpecies; i++) {
            namesOfSpecies[i] = runTableEntries.get(i).getPar().get(0).getValue();
            countTimeSec[i] = Double.parseDouble(runTableEntries.get(i).getPar().get(4).getValue());
        }

        timeStampSec = new double[nScans][nSpecies];
        trimMass = new double[nScans][nSpecies];
        netPkCps = new double[nScans][nSpecies];
        sbmCps = new double[nScans][nSpecies];
        pkFerr = new double[nScans][nSpecies];

        // april 2016 hard-wired for prototype **********************************
        indexToSpeciesMap = new HashMap<>();
        indexToSpeciesMap.put(IsotopeNames.Zr2O196, 0);
        indexToSpeciesMap.put(IsotopeNames.Pb204, 1);
        indexToSpeciesMap.put(IsotopeNames.BKGND, 2);
        indexToSpeciesMap.put(IsotopeNames.Pb206, 3);
        indexToSpeciesMap.put(IsotopeNames.Pb207, 4);
        indexToSpeciesMap.put(IsotopeNames.Pb208, 5);
        indexToSpeciesMap.put(IsotopeNames.U238, 6);
        indexToSpeciesMap.put(IsotopeNames.ThO248, 7);
        indexToSpeciesMap.put(IsotopeNames.UO254, 8);
        indexToSpeciesMap.put(IsotopeNames.UO270, 9);

        isotopicRatios = new TreeMap<>();
        isotopicRatios.put(RawRatioNamesSHRIMP.r204_206w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r204_206w, IsotopeNames.Pb204, IsotopeNames.Pb206));
        isotopicRatios.put(RawRatioNamesSHRIMP.r207_206w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r207_206w, IsotopeNames.Pb207, IsotopeNames.Pb206));
        isotopicRatios.put(RawRatioNamesSHRIMP.r208_206w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r208_206w, IsotopeNames.Pb208, IsotopeNames.Pb206));
        isotopicRatios.put(RawRatioNamesSHRIMP.r238_196w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r238_196w, IsotopeNames.U238, IsotopeNames.Zr2O196));
        isotopicRatios.put(RawRatioNamesSHRIMP.r206_238w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r206_238w, IsotopeNames.Pb206, IsotopeNames.U238));
        isotopicRatios.put(RawRatioNamesSHRIMP.r254_238w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r254_238w, IsotopeNames.UO254, IsotopeNames.U238));
        isotopicRatios.put(RawRatioNamesSHRIMP.r248_254w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r248_254w, IsotopeNames.ThO248, IsotopeNames.UO254));
        isotopicRatios.put(RawRatioNamesSHRIMP.r206_270w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r206_270w, IsotopeNames.Pb206, IsotopeNames.UO270));
        isotopicRatios.put(RawRatioNamesSHRIMP.r270_254w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r270_254w, IsotopeNames.UO270, IsotopeNames.UO254));
        isotopicRatios.put(RawRatioNamesSHRIMP.r206_254w, new IsotopeRatioModelSHRIMP(RawRatioNamesSHRIMP.r206_254w, IsotopeNames.Pb206, IsotopeNames.UO254));

    }

    /**
     * Returns 2D Double array of converted raw data Based on Simon Bodorkos
     * email 4.Feb.2016 interpretation of Squid code Corrected per Phil Main
     * 9.Feb.2016 email
     *
     * @param runFraction
     * @return
     */
    private void parseRunFractionData() {

        totalCounts = new double[nScans][nSpecies];
        totalCountsOneSigmaAbs = new double[nScans][nSpecies];
        totalCountsSBM = new double[nScans][nSpecies];
        totalCountsBD = new BigDecimal[nScans][nSpecies];
        totalCountsOneSigmaAbsBD = new BigDecimal[nScans][nSpecies];
        totalCountsSBMBD = new BigDecimal[nScans][nSpecies];
        rawPeakData = new int[nScans][nSpecies * peakMeasurementsCount];
        rawSBMData = new int[nScans][nSpecies * peakMeasurementsCount];

        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            // there is one measurement per mass per scanNum
            List<PrawnFile.Run.Set.Scan.Measurement> measurements = scans.get(scanNum).getMeasurement();
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                // record the time_stamp_sec
                timeStampSec[scanNum][speciesMeasurementIndex]
                        = Double.parseDouble(measurements.get(speciesMeasurementIndex).getPar().get(2).getValue());
                // record the trim_mass
                trimMass[scanNum][speciesMeasurementIndex]
                        = Double.parseDouble(measurements.get(speciesMeasurementIndex).getPar().get(1).getValue());

                // handle peakMeasurements measurements
                String[] peakMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(0).getValue().split(",");
                double[] peakMeasurements = new double[peakMeasurementsCount];
                for (int i = 0; i < peakMeasurementsCount; i++) {
                    peakMeasurements[i] = Double.parseDouble(peakMeasurementsRaw[i]);
                    rawPeakData[scanNum][speciesMeasurementIndex + speciesMeasurementIndex * (peakMeasurementsCount - 1) + i] = (int) peakMeasurements[i];
                }

                double median = TukeyBiweight.calculateMedian(peakMeasurements);
                double totalCountsPeak;
                double totalCountsSigma;
                BigDecimal totalCountsPeakBD;
                BigDecimal totalCountsSigmaBD;

                if (median > 100.0) {
                    // See Zeringue's pull request #14 for discussion
                    //ValueModel peakTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("PEAK", 9.0, peakMeasurements);
                    ValueModel peakTukeyMean = TukeyBiweightBD.calculateTukeyBiweightMean("PEAK", 9.0, peakMeasurements);

                    // BV is variable used by Ludwig for Tukey Mean fo peak measurements
                    double bV = peakTukeyMean.getValue().doubleValue();
                    double bVcps = bV * peakMeasurementsCount / countTimeSec[speciesMeasurementIndex];
                    double bVcpsDeadTime = bVcps / (1.0 - bVcps * deadTimeNanoseconds / 1E9);

                    totalCountsPeak = bVcpsDeadTime * countTimeSec[speciesMeasurementIndex];
                    double countsSigmaCandidate = StrictMath.max(peakTukeyMean.getOneSigmaAbs().doubleValue(), StrictMath.sqrt(bV));
                    totalCountsSigma = countsSigmaCandidate / StrictMath.sqrt(peakMeasurementsCount) * bVcps * countTimeSec[speciesMeasurementIndex] / bV;

                    ValueModel peakTukeyMeanBD = TukeyBiweightBD.calculateTukeyBiweightMean("PEAK", 9.0, peakMeasurements);

                    BigDecimal bVBD = peakTukeyMeanBD.getValue();
                    BigDecimal bVcpsBD = bVBD.multiply(new BigDecimal(peakMeasurementsCount)).divide(new BigDecimal(countTimeSec[speciesMeasurementIndex]), MathContext.DECIMAL128);
                    BigDecimal bVcpsDeadTimeBD = bVcpsBD.divide(BigDecimal.ONE.subtract(bVcpsBD.multiply(new BigDecimal(deadTimeNanoseconds).movePointLeft(9), MathContext.DECIMAL128)), MathContext.DECIMAL128);

                    totalCountsPeakBD = bVcpsDeadTimeBD.multiply(new BigDecimal(countTimeSec[speciesMeasurementIndex]));
                    BigDecimal countsSigmaCandidateBD = peakTukeyMeanBD.getOneSigmaAbs().max(bigDecimalSqrtBabylonian(bVBD));
                    totalCountsSigmaBD
                            = countsSigmaCandidateBD.divide(bigDecimalSqrtBabylonian(new BigDecimal(peakMeasurementsCount)), MathContext.DECIMAL128)//
                            .multiply(bVcpsBD).multiply(new BigDecimal(countTimeSec[speciesMeasurementIndex])).divide(bVBD, MathContext.DECIMAL128);

                } else if (median >= 0.0) {

                    // remove the one element with first occurrence of largest residual if any.
                    int maxResidualIndex = PoissonLimitsCountLessThanEqual100.determineIndexOfValueWithLargestResidual(median, peakMeasurements);
                    double sumX = 0.0;
                    double sumXsquared = 0.0;
                    for (int i = 0; i < peakMeasurementsCount; i++) {
                        if (i != maxResidualIndex) {
                            sumX += peakMeasurements[i];
                            sumXsquared += peakMeasurements[i] * peakMeasurements[i];
                        }
                    }

                    int countIncludedIntegrations = (maxResidualIndex == -1) ? peakMeasurementsCount : peakMeasurementsCount - 1;
                    double peakMeanCounts = sumX / countIncludedIntegrations;
                    double poissonSigma = StrictMath.sqrt(peakMeanCounts);
                    double sigmaPeakCounts = StrictMath.sqrt((sumXsquared - (sumX * sumX / countIncludedIntegrations)) / (countIncludedIntegrations - 1));

                    double peakCountsPerSecond = peakMeanCounts * peakMeasurementsCount / countTimeSec[speciesMeasurementIndex];
                    double peakCountsPerSecondDeadTime = peakCountsPerSecond / (1.0 - peakCountsPerSecond * deadTimeNanoseconds / 1E9);

                    totalCountsPeak = peakCountsPerSecondDeadTime * countTimeSec[speciesMeasurementIndex];

                    totalCountsSigma = 0.0;
                    if (peakMeanCounts > 0.0) {
                        totalCountsSigma
                                = StrictMath.max(sigmaPeakCounts, poissonSigma) / StrictMath.sqrt(countIncludedIntegrations) * peakCountsPerSecond * countTimeSec[speciesMeasurementIndex] / peakMeanCounts;
                    }

                    BigDecimal peakMeanCountsBD = new BigDecimal(sumX).divide(new BigDecimal(countIncludedIntegrations), MathContext.DECIMAL128);
                    BigDecimal poissonSigmaBD = bigDecimalSqrtBabylonian(peakMeanCountsBD);
                    BigDecimal sigmaPeakCountsBD = bigDecimalSqrtBabylonian(new BigDecimal((sumXsquared - (sumX * sumX / countIncludedIntegrations)) / (countIncludedIntegrations - 1)));

                    BigDecimal peakCountsPerSecondBD = peakMeanCountsBD.multiply(new BigDecimal(peakMeasurementsCount)).divide(new BigDecimal(countTimeSec[speciesMeasurementIndex]), MathContext.DECIMAL128);
                    BigDecimal peakCountsPerSecondDeadTimeBD
                            = peakCountsPerSecondBD.divide(BigDecimal.ONE.subtract(peakCountsPerSecondBD.multiply(new BigDecimal(deadTimeNanoseconds).movePointLeft(9))), MathContext.DECIMAL128);

                    totalCountsPeakBD = peakCountsPerSecondDeadTimeBD.multiply(new BigDecimal(countTimeSec[speciesMeasurementIndex]));

                    totalCountsSigmaBD = BigDecimal.ZERO;
                    if (peakMeanCountsBD.compareTo(BigDecimal.ZERO) > 0) {
                        totalCountsSigmaBD
                                = sigmaPeakCountsBD.max(poissonSigmaBD)//
                                .divide(bigDecimalSqrtBabylonian(new BigDecimal(countIncludedIntegrations)), MathContext.DECIMAL128)//
                                .multiply(new BigDecimal(peakCountsPerSecond).multiply(new BigDecimal(countTimeSec[speciesMeasurementIndex] / peakMeanCounts), MathContext.DECIMAL128));
                    }

                } else {
                    // set flag as this should be impossible for count data
                    totalCountsPeak = -1.0;
                    totalCountsSigma = -1.0;

                    totalCountsPeakBD = BigDecimal.ONE.negate();
                    totalCountsSigmaBD = BigDecimal.ONE.negate();
                }

                totalCounts[scanNum][speciesMeasurementIndex] = totalCountsPeak;
                totalCountsBD[scanNum][speciesMeasurementIndex] = totalCountsPeakBD;
                totalCountsOneSigmaAbs[scanNum][speciesMeasurementIndex] = totalCountsSigma;
                totalCountsOneSigmaAbsBD[scanNum][speciesMeasurementIndex] = totalCountsSigmaBD;

                // handle SBM measurements
                String[] sbmMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(1).getValue().split(",");
                int sbmMeasurementsCount = sbmMeasurementsRaw.length;
                double[] sbm = new double[sbmMeasurementsCount];
                for (int i = 0; i < sbmMeasurementsCount; i++) {
                    sbm[i] = Double.parseDouble(sbmMeasurementsRaw[i]);
                    rawSBMData[scanNum][speciesMeasurementIndex + speciesMeasurementIndex * (sbmMeasurementsCount - 1) + i] = (int) sbm[i];
                }
                ValueModel sbmTukeyMean = TukeyBiweightBD.calculateTukeyBiweightMean("SBM", 6.0, sbm);
                double totalCountsSpeciesSBM = sbmMeasurementsCount * sbmTukeyMean.getValue().doubleValue();
                BigDecimal totalCountsSpeciesSBMBD = new BigDecimal(sbmMeasurementsCount).multiply(sbmTukeyMean.getValue());
                totalCountsSBM[scanNum][speciesMeasurementIndex] = totalCountsSpeciesSBM;
                totalCountsSBMBD[scanNum][speciesMeasurementIndex] = totalCountsSpeciesSBMBD;
            }
        }
    }

    private void calculateTotalPerSpeciesCPS() {
        // Calculate Total CPS per Species = Step 2 of Development for SHRIMP
        // (see wiki: https://github.com/CIRDLES/ET_Redux/wiki/Development-for-SHRIMP:-Step-2)

        double[][] pkCps = new double[nScans][nSpecies];
        double[] backgroundCpsArray = new double[nScans];

        double sumBackgroundCps = 0.0;
        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                // calculate PeakCps
                pkCps[scanNum][speciesMeasurementIndex] = totalCounts[scanNum][speciesMeasurementIndex] / countTimeSec[speciesMeasurementIndex];
                // calculate corrected (by sbmZeroCps) SBMCps
                sbmCps[scanNum][speciesMeasurementIndex] = (totalCountsSBM[scanNum][speciesMeasurementIndex] / countTimeSec[speciesMeasurementIndex]) - sbmZeroCps;

                if (speciesMeasurementIndex == HARD_WIRED_INDEX_OF_BACKGROUND) {
                    backgroundCpsArray[scanNum] = pkCps[scanNum][speciesMeasurementIndex];
                    sumBackgroundCps += pkCps[scanNum][speciesMeasurementIndex];
                }
            }
        }

        // determine backgroundCps if background species exists
        double backgroundCps = 0.0;
        if (HARD_WIRED_INDEX_OF_BACKGROUND >= 0) {
            backgroundCps = sumBackgroundCps / nScans;

            if (backgroundCps >= 10.0) {
                // recalculate
                backgroundCps = TukeyBiweight.calculateTukeyBiweightMean("BACK", 9.0, backgroundCpsArray).getValue().doubleValue();
            }
        }

        // background correct the peaks with fractional error and calculate total cps for peaks
        double[] sumOfCorrectedPeaks = new double[nSpecies];
        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                if (speciesMeasurementIndex != HARD_WIRED_INDEX_OF_BACKGROUND) {
                    // correct PeakCps to NetPkCps
                    netPkCps[scanNum][speciesMeasurementIndex] = pkCps[scanNum][speciesMeasurementIndex] - backgroundCps;
                    sumOfCorrectedPeaks[speciesMeasurementIndex] += netPkCps[scanNum][speciesMeasurementIndex];
                    // calculate fractional error
                    double absNetPeakCps = netPkCps[scanNum][speciesMeasurementIndex];
                    if (absNetPeakCps > 1.0e-6) {
                        double calcVariance
                                = totalCounts[scanNum][speciesMeasurementIndex]//
                                + (StrictMath.abs(backgroundCps) * StrictMath.pow(countTimeSec[speciesMeasurementIndex] / countTimeSec[HARD_WIRED_INDEX_OF_BACKGROUND], 2));
                        pkFerr[scanNum][speciesMeasurementIndex]
                                = StrictMath.sqrt(calcVariance) / absNetPeakCps / countTimeSec[speciesMeasurementIndex];
                    } else {
                        pkFerr[scanNum][speciesMeasurementIndex] = 1.0;
                    }
                }
            }
        }

        totalCps = new double[nSpecies];
        for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
            // calculate total cps
            // this has the effect of setting totalCps[backgroundIndex] to backgroundCps
            totalCps[speciesMeasurementIndex] = (sumOfCorrectedPeaks[speciesMeasurementIndex] / nScans) + backgroundCps;
        }
    }

    /**
     *
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     */
    private void calculateIsotopicRatios(boolean useSBM, boolean userLinFits) {
        // Step 3 of Development for SHRIMP
        // (see wiki: https://github.com/CIRDLES/ET_Redux/wiki/Development-for-SHRIMP:-Step-3)
        // walk the ratios
        isotopicRatios.forEach((rawRatioName, isotopicRatio) -> {
//            if (rawRatioName.compareTo(RawRatioNamesSHRIMP.r206_254w)==0){
            int nDod = nScans - 1;
            int NUM = indexToSpeciesMap.get(isotopicRatio.getNumerator());
            int DEN = indexToSpeciesMap.get(isotopicRatio.getDenominator());

            int aOrd = (DEN > NUM) ? NUM : DEN;
            int bOrd = (DEN > NUM) ? DEN : NUM;

            double totCtsNUM = 0.0;
            double totCtsDEN = 0.0;

            for (int j = 0; j < nScans; j++) {
                totCtsNUM += netPkCps[j][NUM] * countTimeSec[NUM];
                totCtsDEN += netPkCps[j][DEN] * countTimeSec[DEN];
            }

            double ratioVal;
            double ratioFractErr;
            double[] ratioInterpTime;
            double[] interpRatVal;
            double[] ratValFerr;
            double[] ratValSig;
            double[][] sigRho;
            boolean[] zerPkCt;

            List<Double> ratEqTime = new ArrayList<>();
            List<Double> ratEqVal = new ArrayList<>();
            List<Double> ratEqErr = new ArrayList<>();

            if ((totCtsNUM < 32) || (totCtsDEN < 32) || (nDod == 0)) {
                ratioFractErr = 1.0;
                if (totCtsNUM == 0.0) {
                    ratioVal = SQUID_TINY_VALUE;
                } else if (totCtsDEN == 0.0) {
                    ratioVal = 1e16;
                } else {
                    ratioVal = (totCtsNUM / countTimeSec[NUM]) / (totCtsDEN / countTimeSec[DEN]);
                    ratioFractErr = StrictMath.sqrt((1.0 / StrictMath.abs(totCtsNUM)) + (1.0 / StrictMath.abs(totCtsDEN)));
                }

                ratioInterpTime = new double[]{//
                        0.5 * (StrictMath.min(timeStampSec[0][NUM], timeStampSec[0][DEN]) + StrictMath.max(timeStampSec[nScans - 1][NUM], timeStampSec[nScans - 1][DEN]))
                };

                isotopicRatio.setRatioVal(ratioVal);
                isotopicRatio.setRatioFractErr(ratioFractErr);

                ratEqTime.add(ratioInterpTime[0]);
                ratEqVal.add(ratioVal);
                ratEqErr.add(StrictMath.abs(ratioFractErr * ratioVal));

                // flush out for reports to andle empty entries
                for (int i = 0; i < (nDod - 1); i++) {
                    ratEqTime.add(0.0);
                    ratEqVal.add(0.0);
                    ratEqErr.add(0.0);
                }

            } else {
                // main treatment using double interpolation following Dodson (1978): http://dx.doi.org/10.1088/0022-3735/11/4/004)
                double[] pkF = new double[nDod];
                double sumPkF = 0.0;
                for (int j = 0; j < nDod; j++) {
                    pkF[j] = (timeStampSec[j][bOrd] - timeStampSec[j][aOrd]) / (timeStampSec[j + 1][aOrd] - timeStampSec[j][aOrd]);
                    sumPkF += pkF[j];
                }

                double avPkF = sumPkF / nDod;
                double f1 = (1.0 - avPkF) / 2.0;
                double f2 = (1.0 + avPkF) / 2.0;
                double rhoIJ;// = (1.0 - avPkF * avPkF) / (1.0 + avPkF * avPkF) / 2.0;

                ratioInterpTime = new double[nDod];
                interpRatVal = new double[nDod];
                ratValFerr = new double[nDod];
                ratValSig = new double[nDod];
                sigRho = new double[nDod][nDod];
                zerPkCt = new boolean[nScans];

                int rct = -1;

                for (int sNum = 0; sNum < nDod; sNum++) {
                    boolean continueWithScanProcessing = true;
                    int sn1 = sNum + 1;
                    double totT = timeStampSec[sNum][aOrd] + timeStampSec[sNum][bOrd]
                            + timeStampSec[sn1][aOrd] + timeStampSec[sn1][bOrd];
                    double meanT = totT / 4.0;
                    ratioInterpTime[sNum] = meanT;

                    zerPkCt[sNum] = false;
                    zerPkCt[sn1] = false;
//                    boolean hasZerPk = false;

                    double[] aPkCts = new double[2];
                    double[] bPkCts = new double[2];
                    for (int numDenom = 0; numDenom < 2; numDenom++) {
                        if (continueWithScanProcessing) {
                            int k = sNum + numDenom;
                            double aNetCPS = netPkCps[k][aOrd];
                            double bNetCPS = netPkCps[k][bOrd];

                            if ((aNetCPS == ERROR_VALUE) || (bNetCPS == ERROR_VALUE)) {
//                                hasZerPk = true;
                                zerPkCt[k] = true;
                                continueWithScanProcessing = false;
                            }

                            if (continueWithScanProcessing) {
                                aPkCts[numDenom] = aNetCPS * countTimeSec[aOrd];
                                bPkCts[numDenom] = bNetCPS * countTimeSec[bOrd];

                                if (useSBM) {
                                    if ((sbmCps[k][aOrd] <= 0.0) || (sbmCps[k][aOrd] == ERROR_VALUE)
                                            || (sbmCps[k][bOrd] <= 0.0) || (sbmCps[k][aOrd] == ERROR_VALUE)) {
                                        zerPkCt[k] = true;
                                        continueWithScanProcessing = false;
                                    }
                                }
                            }
                        } // test continueWithScanProcessing
                    } // iteration through numDenom

                    if (continueWithScanProcessing) {
                        for (int k = 0; k < 2; k++) {
                            int numDenom = (k == 0) ? 1 : 0;

                            double a = aPkCts[k];
                            double b = aPkCts[numDenom];
                            if ((a <= 0) && (b > 16)) {
                                zerPkCt[sNum + k - 1] = true;
                            }

                            a = bPkCts[k];
                            b = bPkCts[numDenom];
                            if ((a <= 0) && (b > 16)) {
                                zerPkCt[sNum + k - 1] = true;
                            }
                        } // k iteration

                        // test whether to continue
                        if (!zerPkCt[sNum] && !zerPkCt[sn1]) {
                            double aPk1 = netPkCps[sNum][aOrd];
                            double bPk1 = netPkCps[sNum][bOrd];
                            double aPk2 = netPkCps[sn1][aOrd];
                            double bPk2 = netPkCps[sn1][bOrd];

                            if (useSBM) {
                                aPk1 /= sbmCps[sNum][aOrd];
                                bPk1 /= sbmCps[sNum][bOrd];
                                aPk2 /= sbmCps[sn1][aOrd];
                                bPk2 /= sbmCps[sn1][bOrd];
                            }

                            double scanDeltaT = timeStampSec[sn1][aOrd] - timeStampSec[sNum][aOrd];
                            double bTfract = timeStampSec[sNum][bOrd] - timeStampSec[sNum][aOrd];
                            pkF[sNum] = bTfract / scanDeltaT;
                            double ff1 = (1.0 - pkF[sNum]) / 2.0;
                            double ff2 = (1.0 + pkF[sNum]) / 2.0;
                            double aInterp = (ff1 * aPk1) + (ff2 * aPk2);
                            double bInterp = (ff2 * bPk1) + (ff1 * bPk2);

                            double rNum = (NUM < DEN) ? aInterp : bInterp;
                            double rDen = (NUM < DEN) ? bInterp : aInterp;

                            if (rDen != 0.0) {
                                rct++;
                                interpRatVal[rct] = rNum / rDen;
                                double a1PkSig = pkFerr[sNum][aOrd] * aPk1;
                                double a2PkSig = pkFerr[sn1][aOrd] * aPk2;
                                double b1PkSig = pkFerr[sNum][bOrd] * bPk1;
                                double b2PkSig = pkFerr[sn1][bOrd] * bPk2;

                                if (useSBM) {
                                    a1PkSig = StrictMath.sqrt(a1PkSig * a1PkSig
                                            + (aPk1 * aPk1 / sbmCps[sNum][aOrd] / countTimeSec[aOrd]));
                                    a2PkSig = StrictMath.sqrt(a2PkSig * a2PkSig
                                            + (aPk2 * aPk2 / sbmCps[sn1][aOrd] / countTimeSec[aOrd]));
                                    b1PkSig = StrictMath.sqrt(b1PkSig * b1PkSig
                                            + (bPk1 * bPk1 / sbmCps[sNum][bOrd] / countTimeSec[bOrd]));
                                    b2PkSig = StrictMath.sqrt(b2PkSig * b2PkSig
                                            + (bPk2 * bPk2 / sbmCps[sn1][bOrd] / countTimeSec[bOrd]));
                                }

                                if ((aInterp == 0.0) || (bInterp == 0.0)) {
                                    ratValFerr[rct] = 1.0;
                                    ratValSig[rct] = SQUID_TINY_VALUE;
                                    sigRho[rct][rct] = SQUID_TINY_VALUE;
                                } else {
                                    double term1 = ((f1 * a1PkSig) * (f1 * a1PkSig) + (f2 * a2PkSig) * (f2 * a2PkSig));
                                    double term2 = ((f2 * b1PkSig) * (f2 * b1PkSig) + (f1 * b2PkSig) * (f1 * b2PkSig));
                                    double ratValFvar = (term1 / (aInterp * aInterp)) + (term2 / (bInterp * bInterp));
                                    double ratValVar = ratValFvar * (interpRatVal[rct] * interpRatVal[rct]);
                                    ratValFerr[rct] = StrictMath.sqrt(ratValFvar);
                                    ratValSig[rct] = StrictMath.max(1E-10, StrictMath.sqrt(ratValVar));
                                    sigRho[rct][rct] = ratValSig[rct];

                                    if (rct > 0) {
                                        rhoIJ = (zerPkCt[sNum - 1]) ? 0.0 : (1 - pkF[sNum] * pkF[sNum]) / (1 + pkF[sNum] * pkF[sNum]) / 2.0;

                                        sigRho[rct][rct - 1] = rhoIJ;
                                        sigRho[rct - 1][rct] = rhoIJ;
                                    }
                                } // test aInterp andbInterp
                            } // test rDen

                        } // test !zerPkCt[sNum] && !zerPkCt[sn1]

                    } // continueWithScanProcessing is true

                } // iteration through nDod using sNum (see "NextScanNum" in pseudocode)
                switch (rct) {
                    case -1:
                        ratioVal = ERROR_VALUE;
                        ratioFractErr = ERROR_VALUE;

                        ratEqTime.add(ratioInterpTime[0]);
                        ratEqVal.add(ratioVal);
                        ratEqErr.add(ratioFractErr);

                        isotopicRatio.setRatioVal(ratioVal);
                        isotopicRatio.setRatioFractErr(ratioFractErr);

                        break;
                    case 0:
                        ratioVal = interpRatVal[0];
                        if (ratioVal == 0.0) {
                            ratioVal = SQUID_TINY_VALUE;
                            ratioFractErr = 1.0;
                        } else {
                            ratioFractErr = ratValFerr[0];// this is abs not percent
                        }

                        ratEqTime.add(ratioInterpTime[0]);
                        ratEqVal.add(ratioVal);
                        ratEqErr.add(ratioFractErr);

                        isotopicRatio.setRatioVal(ratioVal);
                        isotopicRatio.setRatioFractErr(ratioFractErr);

                        break;
                    default:
                        for (int j = 0; j < (rct + 1); j++) {
                            ratEqTime.add(ratioInterpTime[j]);
                            ratEqVal.add(interpRatVal[j]);
                            ratEqErr.add(StrictMath.abs(ratValFerr[j] * interpRatVal[j]));
                        }

                        // step 4
                        WeightedMeanCalculators.WtdLinCorrResults wtdLinCorrResults;
                        double ratioMean;
                        double ratioMeanSig;

                        if (userLinFits && rct > 3) {
                            wtdLinCorrResults = wtdLinCorr(interpRatVal, sigRho, ratioInterpTime);

                            double midTime = (timeStampSec[nScans - 1][nSpecies - 1] + timeStampSec[0][0]) / 2.0;
                            ratioMean = (wtdLinCorrResults.getSlope() * midTime) + wtdLinCorrResults.getIntercept();
                            ratioMeanSig = StrictMath.sqrt((midTime * wtdLinCorrResults.getSigmaSlope() * midTime * wtdLinCorrResults.getSigmaSlope())//
                                    + wtdLinCorrResults.getSigmaIntercept() * wtdLinCorrResults.getSigmaIntercept() //
                                    + 2.0 * midTime * wtdLinCorrResults.getCovSlopeInter());

                        } else {
                            wtdLinCorrResults = wtdLinCorr(interpRatVal, sigRho, new double[0]);
                            ratioMean = wtdLinCorrResults.getIntercept();
                            ratioMeanSig = wtdLinCorrResults.getSigmaIntercept();
                        }

                        if (wtdLinCorrResults.isBad()) {
                            isotopicRatio.setRatioVal(ERROR_VALUE);
                            isotopicRatio.setRatioFractErr(ERROR_VALUE);
                        } else if (wtdLinCorrResults.getIntercept() == 0.0) {
                            isotopicRatio.setRatioVal(SQUID_TINY_VALUE);
                            isotopicRatio.setRatioFractErr(1.0);
                        } else {
                            isotopicRatio.setRatioVal(ratioMean);
                            isotopicRatio.setRatioFractErr(StrictMath.max(SQUID_TINY_VALUE, ratioMeanSig) / StrictMath.abs(ratioMean));
                        }

                        isotopicRatio.setMinIndex(wtdLinCorrResults.getMinIndex());

                        break;
                }

            } // end decision on which ratio procedure to use

            // store values for reports
            isotopicRatio.setRatEqTime(ratEqTime);
            isotopicRatio.setRatEqVal(ratEqVal);
            isotopicRatio.setRatEqErr(ratEqErr);
//            }
        }); // end iteration through isotopicRatios

    }

}
