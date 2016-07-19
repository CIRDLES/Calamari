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
package org.cirdles.calamari.prawn;

import org.cirdles.calamari.shrimp.ShrimpFraction;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author James F. Bowring &lt;bowring at gmail.com&gt;
 */
public class PrawnRunFractionParser {

    private static final PrawnFileRunFractionParser PRAWN_FILE_RUN_FRACTION_PARSER
            = new PrawnFileRunFractionParser();
    
    /**
     *
     * @param runFraction the value of runFraction
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @return 
     */
    @Deprecated
    public static ShrimpFraction processRunFraction(PrawnFile.Run runFraction, boolean useSBM, boolean userLinFits) {
        return PRAWN_FILE_RUN_FRACTION_PARSER.processRunFraction(runFraction, useSBM, userLinFits);
    }

    public static BigDecimal bigDecimalSqrtBabylonian(BigDecimal S) {
        
        BigDecimal guess = new BigDecimal(StrictMath.sqrt(S.doubleValue()));
        
        if (guess.compareTo(BigDecimal.ZERO) > 0) {
            
            BigDecimal precision = BigDecimal.ONE.movePointLeft(34);
            BigDecimal theError = BigDecimal.ONE;
            while (theError.compareTo(precision) > 0) {
                BigDecimal nextGuess = BigDecimal.ZERO;
                try {
                    nextGuess = guess.add(S.divide(guess, MathContext.DECIMAL128)).divide(new BigDecimal(2.0), MathContext.DECIMAL128);
                } catch (java.lang.ArithmeticException e) {
                    System.out.println(e.getMessage());
                }
                theError = guess.subtract(nextGuess, MathContext.DECIMAL128).abs();
                guess = nextGuess;
            }
        }
        
        return guess;
    }

}
