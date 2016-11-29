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
package org.cirdles.calamari.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.cirdles.calamari.constants.SquidConstants.SQUID_ERROR_VALUE;
import org.cirdles.calamari.shrimp.IsotopeNames;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class Task implements TaskInterface {

    protected ShrimpFractionExpressionInterface shrimpFraction;
    protected Map<Integer, ExpressionTreeInterface> taskExpressionsOrdered;

    public Task(ShrimpFractionExpressionInterface shrimpFraction) {
        this.shrimpFraction = shrimpFraction;
        this.taskExpressionsOrdered = new TreeMap<>();
    }

    @Override
    public void evaluateTaskExpressions() {
        // first have to build pkInterp etc per expression and then evaluate by scan
        for (Map.Entry entry : taskExpressionsOrdered.entrySet()) {
            ExpressionTreeInterface expression = (ExpressionTreeInterface) entry.getValue();
            List<RawRatioNamesSHRIMP> ratiosOfInterest = expression.getRatiosOfInterest();

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
                double eval = expression.eval(pkInterp[scanNum], isotopeToIndexMap);
                System.out.println(shrimpFraction.getFractionID() + "  " + expression.getPrettyName() + "   " + scanNum + " = " + eval);

            } // end scanNum loop

        }

    }

}
