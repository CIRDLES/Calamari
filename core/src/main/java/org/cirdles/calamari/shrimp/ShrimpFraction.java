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
package org.cirdles.calamari.shrimp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cirdles.calamari.tasks.TaskExpressionEvaluatedModelInterface;

/**
 *
 * @author James F. Bowring
 */
public class ShrimpFraction implements ShrimpFractionExpressionInterface {

    private String fractionID;
    private int spotNumber;
    private String nameOfMount;
    private long dateTimeMilliseconds;
    private int deadTimeNanoseconds;
    private int sbmZeroCps;
    private double[] countTimeSec;
    private String[] namesOfSpecies;
    private int peakMeasurementsCount;
    private Map<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> isotopicRatios;
    private int[][] rawPeakData;
    private int[][] rawSBMData;
    private double[][] totalCounts;
    private double[][] totalCountsOneSigmaAbs;
    private double[][] totalCountsSBM;
    private double[][] timeStampSec;
    private double[][] trimMass;
    private double[] totalCps;
    private double[][] netPkCps;
    private double[][] pkFerr;
    private boolean referenceMaterial;
    private boolean useSBM;
    private boolean userLinFits;

    private double[][] reducedPkHt;
    private double[][] reducedPkHtFerr;
    
    private double[] pkInterpScanArray;
    
    private List<TaskExpressionEvaluatedModelInterface> taskExpressionsEvaluated;

    public ShrimpFraction() {
        fractionID = "NONE";
        spotNumber = -1;
        nameOfMount = "NONE";
        dateTimeMilliseconds = 0l;
        deadTimeNanoseconds = 0;
        sbmZeroCps = 0;
        countTimeSec = new double[0];
        namesOfSpecies = new String[0];
        peakMeasurementsCount = 0;
        isotopicRatios = new HashMap<>();
        rawPeakData = new int[0][0];
        rawSBMData = new int[0][0];
        totalCounts = new double[0][0];
        totalCountsSBM = new double[0][0];
        timeStampSec = new double[0][0];
        trimMass = new double[0][0];
        totalCps = new double[0];
        netPkCps = new double[0][0];
        pkFerr = new double[0][0];
        referenceMaterial = false;

        reducedPkHt = new double[0][0];
        reducedPkHtFerr = new double[0][0];
        
        pkInterpScanArray = new double[0];
        
        taskExpressionsEvaluated = new ArrayList<>();

    }

    public ShrimpFraction(String fractionID, Map<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> isotopicRatios) {
        this();
        this.fractionID = fractionID;
        this.isotopicRatios = isotopicRatios;
    }

    /**
     * 
     * @param speciesName
     * @return 
     */
    @Override
    public int getIndexOfSpeciesByName(IsotopeNames speciesName) {
        int retVal = -1;

        for (int i = 0; i < namesOfSpecies.length; i++) {
            if (namesOfSpecies[i].compareToIgnoreCase(speciesName.getPrawnName()) == 0) {
                retVal = i;
            }
        }
        return retVal;
    }

    /**
     * @return the fractionID
     */
    public String getFractionID() {
        return fractionID;
    }

    /**
     * @param fractionID the fractionID to set
     */
    public void setFractionID(String fractionID) {
        this.fractionID = fractionID;
    }

    /**
     * @return the spotNumber
     */
    public int getSpotNumber() {
        return spotNumber;
    }

    /**
     * @param spotNumber the spotNumber to set
     */
    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }

    /**
     * @return the nameOfMount
     */
    public String getNameOfMount() {
        return nameOfMount;
    }

    /**
     * @param nameOfMount the nameOfMount to set
     */
    public void setNameOfMount(String nameOfMount) {
        this.nameOfMount = nameOfMount;
    }

    /**
     * @return the dateTimeMilliseconds
     */
    public long getDateTimeMilliseconds() {
        return dateTimeMilliseconds;
    }

    /**
     * @param dateTimeMilliseconds the dateTimeMilliseconds to set
     */
    public void setDateTimeMilliseconds(long dateTimeMilliseconds) {
        this.dateTimeMilliseconds = dateTimeMilliseconds;
    }

    /**
     * @return the deadTimeNanoseconds
     */
    public int getDeadTimeNanoseconds() {
        return deadTimeNanoseconds;
    }

    /**
     * @param aDeadTimeNanoseconds the deadTimeNanoseconds to set
     */
    public void setDeadTimeNanoseconds(int aDeadTimeNanoseconds) {
        deadTimeNanoseconds = aDeadTimeNanoseconds;
    }

    /**
     * @return the sbmZeroCps
     */
    public int getSbmZeroCps() {
        return sbmZeroCps;
    }

    /**
     * @param sbmZeroCps the sbmZeroCps to set
     */
    public void setSbmZeroCps(int sbmZeroCps) {
        this.sbmZeroCps = sbmZeroCps;
    }

    /**
     * @return the countTimeSec
     */
    public double[] getCountTimeSec() {
        return countTimeSec.clone();
    }

    /**
     * @param countTimeSec the countTimeSec to set
     */
    public void setCountTimeSec(double[] countTimeSec) {
        this.countTimeSec = countTimeSec.clone();
    }

    /**
     * @return the namesOfSpecies
     */
    public String[] getNamesOfSpecies() {
        return namesOfSpecies.clone();
    }

    /**
     * @param namesOfSpecies the namesOfSpecies to set
     */
    public void setNamesOfSpecies(String[] namesOfSpecies) {
        this.namesOfSpecies = namesOfSpecies.clone();
    }

    /**
     * @return the peakMeasurementsCount
     */
    public int getPeakMeasurementsCount() {
        return peakMeasurementsCount;
    }

    /**
     * @param peakMeasurementsCount the peakMeasurementsCount to set
     */
    public void setPeakMeasurementsCount(int peakMeasurementsCount) {
        this.peakMeasurementsCount = peakMeasurementsCount;
    }

    /**
     * @return the isotopicRatios
     */
    public Map<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> getIsotopicRatios() {
        return isotopicRatios;
    }

    /**
     * @param isotopicRatios the isotopicRatios to set
     */
    public void setIsotopicRatios(Map<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> isotopicRatios) {
        this.isotopicRatios = isotopicRatios;
    }

    /**
     * @return the rawPeakData
     */
    public int[][] getRawPeakData() {
        return rawPeakData.clone();
    }

    /**
     * @param rawPeakData the rawPeakData to set
     */
    public void setRawPeakData(int[][] rawPeakData) {
        this.rawPeakData = rawPeakData.clone();
    }

    /**
     * @return the rawSBMData
     */
    public int[][] getRawSBMData() {
        return rawSBMData.clone();
    }

    /**
     * @param rawSBMData the rawSBMData to set
     */
    public void setRawSBMData(int[][] rawSBMData) {
        this.rawSBMData = rawSBMData.clone();
    }

    /**
     * @return the totalCounts
     */
    public double[][] getTotalCounts() {
        return totalCounts.clone();
    }

    /**
     * @param totalCounts the totalCounts to set
     */
    public void setTotalCounts(double[][] totalCounts) {
        this.totalCounts = totalCounts.clone();
    }

    /**
     * @return the totalCountsOneSigmaAbs
     */
    public double[][] getTotalCountsOneSigmaAbs() {
        return totalCountsOneSigmaAbs.clone();
    }

    /**
     * @param totalCountsOneSigmaAbs the totalCountsOneSigmaAbs to set
     */
    public void setTotalCountsOneSigmaAbs(double[][] totalCountsOneSigmaAbs) {
        this.totalCountsOneSigmaAbs = totalCountsOneSigmaAbs.clone();
    }

    /**
     * @return the totalCountsSBM
     */
    public double[][] getTotalCountsSBM() {
        return totalCountsSBM.clone();
    }

    /**
     * @param totalCountsSBM the totalCountsSBM to set
     */
    public void setTotalCountsSBM(double[][] totalCountsSBM) {
        this.totalCountsSBM = totalCountsSBM.clone();
    }

    /**
     * @return the timeStampSec
     */
    public double[][] getTimeStampSec() {
        return timeStampSec.clone();
    }

    /**
     * @param timeStampSec the timeStampSec to set
     */
    public void setTimeStampSec(double[][] timeStampSec) {
        this.timeStampSec = timeStampSec.clone();
    }

    /**
     * @return the trimMass
     */
    public double[][] getTrimMass() {
        return trimMass.clone();
    }

    /**
     * @param trimMass the trimMass to set
     */
    public void setTrimMass(double[][] trimMass) {
        this.trimMass = trimMass.clone();
    }

    /**
     * @return the totalCps
     */
    public double[] getTotalCps() {
        return totalCps.clone();
    }

    /**
     * @param totalCps the totalCps to set
     */
    public void setTotalCps(double[] totalCps) {
        this.totalCps = totalCps.clone();
    }

    /**
     * @return the netPkCps
     */
    public double[][] getNetPkCps() {
        return netPkCps.clone();
    }

    /**
     * @param aNetPkCps the netPkCps to set
     */
    public void setNetPkCps(double[][] aNetPkCps) {
        netPkCps = aNetPkCps.clone();
    }

    /**
     * @return the pkFerr
     */
    public double[][] getPkFerr() {
        return pkFerr.clone();
    }

    /**
     * @param aPkFerr the pkFerr to set
     */
    public void setPkFerr(double[][] aPkFerr) {
        pkFerr = aPkFerr.clone();
    }

    /**
     * @return the referenceMaterial
     */
    public boolean isReferenceMaterial() {
        return referenceMaterial;
    }

    /**
     * @param referenceMaterial the referenceMaterial to set
     */
    public void setReferenceMaterial(boolean referenceMaterial) {
        this.referenceMaterial = referenceMaterial;
    }

    /**
     * @return the useSBM
     */
    public boolean isUseSBM() {
        return useSBM;
    }

    /**
     * @param useSBM the useSBM to set
     */
    public void setUseSBM(boolean useSBM) {
        this.useSBM = useSBM;
    }

    /**
     * @return the userLinFits
     */
    @Override
    public boolean isUserLinFits() {
        return userLinFits;
    }

    /**
     * @param userLinFits the userLinFits to set
     */
    public void setUserLinFits(boolean userLinFits) {
        this.userLinFits = userLinFits;
    }

    /**
     * @return the reducedPkHt
     */
    public double[][] getReducedPkHt() {
        return reducedPkHt.clone();
    }

    /**
     * @param reducedPkHt the reducedPkHt to set
     */
    public void setReducedPkHt(double[][] reducedPkHt) {
        this.reducedPkHt = reducedPkHt.clone();
    }

    /**
     * @return the reducedPkHtFerr
     */
    @Override
    public double[][] getReducedPkHtFerr() {
        return reducedPkHtFerr.clone();
    }

    /**
     * @param reducedPkHtFerr the reducedPkHtFerr to set
     */
    public void setReducedPkHtFerr(double[][] reducedPkHtFerr) {
        this.reducedPkHtFerr = reducedPkHtFerr.clone();
    }

    /**
     * @return the pkInterpScanArray
     */
    public double[] getPkInterpScanArray() {
        return pkInterpScanArray.clone();
    }

    /**
     * @param pkInterpScanArray the pkInterpScanArray to set
     */
    public void setPkInterpScanArray(double[] pkInterpScanArray) {
        this.pkInterpScanArray = pkInterpScanArray.clone();
    }

    /**
     * @return the taskExpressionsEvaluated
     */
    public List<TaskExpressionEvaluatedModelInterface> getTaskExpressionsEvaluated() {
        return taskExpressionsEvaluated;
    }

    /**
     * @param taskExpressionsEvaluated the taskExpressionsEvaluated to set
     */
    @Override
    public void setTaskExpressionsEvaluated(List<TaskExpressionEvaluatedModelInterface> taskExpressionsEvaluated) {
        this.taskExpressionsEvaluated = taskExpressionsEvaluated;
    }

}
