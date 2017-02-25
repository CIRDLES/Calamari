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
package org.cirdles.calamari.tasks;

import com.google.common.primitives.Doubles;
import com.thoughtworks.xstream.XStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cirdles.calamari.algorithms.WeightedMeanCalculators;
import static org.cirdles.calamari.algorithms.WeightedMeanCalculators.wtdLinCorr;
import static org.cirdles.calamari.constants.SquidConstants.SQUID_ERROR_VALUE;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMPXMLConverter;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeXMLConverter;
import org.cirdles.calamari.tasks.expressions.constants.ConstantNode;
import org.cirdles.calamari.tasks.expressions.constants.ConstantNodeXMLConverter;
import org.cirdles.calamari.tasks.expressions.functions.Ln;
import org.cirdles.calamari.tasks.expressions.isotopes.ShrimpSpeciesNode;
import org.cirdles.calamari.tasks.expressions.isotopes.ShrimpSpeciesNodeXMLConverter;
import org.cirdles.calamari.tasks.expressions.operations.Add;
import org.cirdles.calamari.tasks.expressions.operations.Divide;
import org.cirdles.calamari.tasks.expressions.operations.Multiply;
import org.cirdles.calamari.tasks.expressions.operations.Operation;
import org.cirdles.calamari.tasks.expressions.operations.OperationXMLConverter;
import org.cirdles.calamari.tasks.expressions.operations.Pow;
import org.cirdles.calamari.tasks.expressions.operations.Subtract;
import org.cirdles.calamari.utilities.xmlSerialization.XMLSerializerInterface;

/**
 *
 * @author James F. Bowring
 */
public class Task implements TaskInterface, XMLSerializerInterface {

    protected String name;
    protected List<ExpressionTreeInterface> taskExpressionsOrdered;
    protected transient List<TaskExpressionEvaluatedModelInterface> taskExpressionsEvaluated;

    public Task() {
        this("NoName");
    }

    public Task(String name) {
        this.name = name;
        this.taskExpressionsOrdered = new ArrayList<>();
        this.taskExpressionsEvaluated = new ArrayList<>();
    }

    @Override
    public void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ShrimpSpeciesNodeXMLConverter());
        xstream.alias("ShrimpSpeciesNode", ShrimpSpeciesNode.class);

        xstream.registerConverter(new ConstantNodeXMLConverter());
        xstream.alias("ConstantNode", ConstantNode.class);

        xstream.registerConverter(new OperationXMLConverter());
        xstream.alias("operation", Operation.class);
        xstream.alias("operation", Add.class);
        xstream.alias("operation", Subtract.class);
        xstream.alias("operation", Multiply.class);
        xstream.alias("operation", Divide.class);
        xstream.alias("operation", Pow.class);
        
        xstream.alias("function", Ln.class);

        xstream.registerConverter(new RawRatioNamesSHRIMPXMLConverter());
        xstream.alias("ratio", RawRatioNamesSHRIMP.class);

        xstream.registerConverter(new ExpressionTreeXMLConverter());
        xstream.alias("ExpressionTree", ExpressionTree.class);

        xstream.registerConverter(new TaskXMLConverter());
        xstream.alias("Task", Task.class);
        xstream.alias("Task", this.getClass());
    }

    /**
     * see https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Sub-EqnInterp
     *
     * @param shrimpFraction
     */
    @Override
    public void evaluateTaskExpressions(ShrimpFractionExpressionInterface shrimpFraction) {
        this.taskExpressionsEvaluated = new ArrayList<>();
        if (shrimpFraction != null) {
            // first have to build pkInterp etc per expression and then evaluate by scan
            taskExpressionsOrdered.forEach((expression) -> {
                List<RawRatioNamesSHRIMP> ratiosOfInterest = ((ExpressionTreeWithRatiosInterface) expression).getRatiosOfInterest();

                // entry test
                if (ratiosOfInterest.size() > 0) {

                    int[] isotopeIndices = new int[ratiosOfInterest.size() * 2];
                    Map<IsotopeNames, Integer> isotopeToIndexMap = new HashMap<>();
                    for (int i = 0; i < ratiosOfInterest.size(); i++) {
                        isotopeIndices[2 * i] = shrimpFraction.getIndexOfSpeciesByName(ratiosOfInterest.get(i).getNumerator());
                        isotopeToIndexMap.put(ratiosOfInterest.get(i).getNumerator(), isotopeIndices[2 * i]);

                        isotopeIndices[2 * i + 1] = shrimpFraction.getIndexOfSpeciesByName(ratiosOfInterest.get(i).getDenominator());
                        isotopeToIndexMap.put(ratiosOfInterest.get(i).getDenominator(), isotopeIndices[2 * i + 1]);
                    }

                    int sIndx = shrimpFraction.getReducedPkHt().length - 1;
                    double[][] pkInterp = new double[sIndx][shrimpFraction.getReducedPkHt()[0].length];
                    double[][] pkInterpFerr = new double[sIndx][shrimpFraction.getReducedPkHt()[0].length];
                    boolean singleScan = (sIndx == 1);
                    double interpTime = 0.0;

                    List<Double> eqValList = new ArrayList<>();
                    List<Double> fractErrList = new ArrayList<>();
                    List<Double> absErrList = new ArrayList<>();
                    List<Double> eqTimeList = new ArrayList<>();

                    for (int scanNum = 0; scanNum < sIndx; scanNum++) {
                        boolean doProceed = true;
                        if (!singleScan) {
                            double interpTimeSpan = 0.0;
                            for (int i = 0; i < isotopeIndices.length; i++) {
                                interpTimeSpan
                                        += shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]]
                                        + shrimpFraction.getTimeStampSec()[scanNum + 1][isotopeIndices[i]];
                            }
                            interpTime = interpTimeSpan / isotopeIndices.length / 2.0;
                        } // end check singleScan

                        for (int i = 0; i < isotopeIndices.length; i++) {
                            double fractInterpTime = 0.0;
                            double fractLessInterpTime = 0.0;
                            double redPk2Ht = 0.0;

                            if (!singleScan) {
                                // default value
                                pkInterp[scanNum][isotopeIndices[i]] = SQUID_ERROR_VALUE;
                                double pkTdelt
                                        = shrimpFraction.getTimeStampSec()[scanNum + 1][isotopeIndices[i]]
                                        - shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]];

                                doProceed = (pkTdelt > 0.0);

                                if (doProceed) {
                                    fractInterpTime = (interpTime - shrimpFraction.getTimeStampSec()[scanNum][isotopeIndices[i]]) / pkTdelt;
                                    fractLessInterpTime = 1.0 - fractInterpTime;
                                    redPk2Ht = shrimpFraction.getReducedPkHt()[scanNum + 1][isotopeIndices[i]];
                                }
                            } // end check singleScan
                            if (doProceed) {
                                double redPk1Ht = shrimpFraction.getReducedPkHt()[scanNum][isotopeIndices[i]];

                                if (redPk1Ht == SQUID_ERROR_VALUE || redPk2Ht == SQUID_ERROR_VALUE) {
                                    doProceed = false;
                                }

                                if (doProceed) {
                                    double pkF1 = shrimpFraction.getReducedPkHtFerr()[scanNum][isotopeIndices[i]];

                                    if (singleScan) {
                                        pkInterp[scanNum][isotopeIndices[i]] = redPk1Ht;
                                        pkInterpFerr[scanNum][isotopeIndices[i]] = pkF1;
                                    } else {
                                        pkInterp[scanNum][isotopeIndices[i]] = (fractLessInterpTime * redPk1Ht) + (fractInterpTime * redPk2Ht);
                                        double pkF2 = shrimpFraction.getReducedPkHtFerr()[scanNum + 1][isotopeIndices[i]];
                                        pkInterpFerr[scanNum][isotopeIndices[i]] = StrictMath.sqrt((fractLessInterpTime * pkF1) * (fractLessInterpTime * pkF1)
                                                + (fractInterpTime * pkF2) * (fractInterpTime * pkF2));
                                    }
                                }
                            }
                        }

                        // The next step is to evaluate the equation 'FormulaEval', 
                        // documented separately), and approximate the uncertainties:
                        double eqValTmp = expression.eval(pkInterp[scanNum], isotopeToIndexMap);
                        double eqFerr;

                        if (eqValTmp != 0.0) {
                            // numerical pertubation procedure
                            // EqPkUndupeOrd is here a List of the unique Isotopes in order of acquisition in the expression
                            // Not sure the order is critical here ... 
                            Set<IsotopeNames> eqPkUndupeOrd = ((ExpressionTreeWithRatiosInterface) expression).extractUniqueSpeciesNumbers();
                            Iterator<IsotopeNames> species = eqPkUndupeOrd.iterator();

                            double fVar = 0.0;
                            while (species.hasNext()) {
                                IsotopeNames specie = species.next();
                                int unDupPkOrd = shrimpFraction.getIndexOfSpeciesByName(specie);

                                // clone pkInterp[scanNum] for use in pertubation
                                double[] perturbed = pkInterp[scanNum].clone();
                                perturbed[unDupPkOrd] *= 1.0001;
                                double pertVal = expression.eval(perturbed, isotopeToIndexMap);

                                double fDelt = (pertVal - eqValTmp) / eqValTmp; // improvement suggested by Bodorkos
                                double tA = pkInterpFerr[scanNum][unDupPkOrd];
                                double tB = 1.0001 - 1.0;// --note that Excel 16-bit floating binary gives 9.9999999999989E-05    
                                double tC = fDelt * fDelt;
                                double tD = (tA / tB) * (tA / tB) * tC;
                                fVar += tD;// --fractional internal variance
                            } // end of visiting each isotope and perturbing equation

                            eqFerr = StrictMath.sqrt(fVar);

                            // now that expression and its error are calculated
                            if (eqFerr != 0.0) {
                                eqValList.add(eqValTmp);
                                absErrList.add(StrictMath.abs(eqFerr * eqValTmp));
                                fractErrList.add(eqFerr);
                                double totRatTime = 0.0;
                                int numPksInclDupes = 0;

                                // reset iterator
                                species = eqPkUndupeOrd.iterator();
                                while (species.hasNext()) {
                                    int unDupPkOrd = shrimpFraction.getIndexOfSpeciesByName(species.next());
                                    
                                    totRatTime += shrimpFraction.getTimeStampSec()[scanNum][unDupPkOrd];
                                    numPksInclDupes++;

                                    totRatTime += shrimpFraction.getTimeStampSec()[scanNum + 1][unDupPkOrd];
                                    numPksInclDupes++;
                                }
                                eqTimeList.add(totRatTime / numPksInclDupes);
                            }
                        } // end test of eqValTmp != 0.0 VBA calls this a bailout and has no logic

                    } // end scanNum loop

                    // The final step is to assemble outputs EqTime, EqVal and AbsErr, and
                    // to define SigRho as input for the use of subroutine WtdLinCorr and its sub-subroutines: 
                    // convert to arrays
                    double[] eqVal = Doubles.toArray(eqValList);
                    double[] absErr = Doubles.toArray(absErrList);
                    double[] fractErr = Doubles.toArray(fractErrList);
                    double[] eqTime = Doubles.toArray(eqTimeList);
                    double[][] sigRho = new double[eqVal.length][eqVal.length];

                    for (int i = 0; i < sigRho.length; i++) {
                        sigRho[i][i] = absErr[i];
                        if (i > 1) {
                            sigRho[i][i - 1] = 0.25;
                            sigRho[i - 1][i] = 0.25;
                        }
                    }

                    WeightedMeanCalculators.WtdLinCorrResults wtdLinCorrResults;
                    double meanEq;
                    double meanEqSig;

                    if (shrimpFraction.isUserLinFits() && eqVal.length > 3) {
                        wtdLinCorrResults = wtdLinCorr(eqVal, sigRho, eqTime);

                        double midTime
                                = (shrimpFraction.getTimeStampSec()[sIndx][shrimpFraction.getReducedPkHt()[0].length - 1]
                                + shrimpFraction.getTimeStampSec()[0][0]) / 2.0;
                        double slope = wtdLinCorrResults.getSlope();
                        double sigmaSlope = wtdLinCorrResults.getSigmaSlope();
                        double sigmaIntercept = wtdLinCorrResults.getSigmaIntercept();

                        meanEq = (slope * midTime) + wtdLinCorrResults.getIntercept();
                        meanEqSig = StrictMath.sqrt((midTime * sigmaSlope * midTime * sigmaSlope)//
                                + sigmaIntercept * sigmaIntercept //
                                + 2.0 * midTime * wtdLinCorrResults.getCovSlopeInter());

                    } else {
                        wtdLinCorrResults = wtdLinCorr(eqVal, sigRho, new double[0]);
                        meanEq = wtdLinCorrResults.getIntercept();
                        meanEqSig = wtdLinCorrResults.getSigmaIntercept();
                    }

                    double eqValFerr;
                    if (meanEq == 0.0) {
                        eqValFerr = 1.0;
                    } else {
                        eqValFerr = StrictMath.abs(meanEqSig / meanEq);
                    }

                    // for consistency with Bodorkos documentation
                    double[] ratEqVal = eqVal.clone();
                    double[] ratEqTime = eqTime.clone();
                    double[] ratEqErr = new double[eqVal.length];
                    for (int i = 0; i < ratEqErr.length; i++) {
                        ratEqErr[i] = StrictMath.abs(eqVal[i] * fractErr[i]);
                    }

                    taskExpressionsEvaluated.add(new TaskExpressionEvaluatedModel(
                            expression, ratEqVal, ratEqTime, ratEqErr, meanEq, eqValFerr));
                }// end of entry test
            }); // end of visiting each expression
            shrimpFraction.setTaskExpressionsEvaluated(taskExpressionsEvaluated);

        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the taskExpressionsOrdered
     */
    public List<ExpressionTreeInterface> getTaskExpressionsOrdered() {
        return taskExpressionsOrdered;
    }

    /**
     * @param taskExpressionsOrdered the taskExpressionsOrdered to set
     */
    public void setTaskExpressionsOrdered(List<ExpressionTreeInterface> taskExpressionsOrdered) {
        this.taskExpressionsOrdered = taskExpressionsOrdered;
    }
}
