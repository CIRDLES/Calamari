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

import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFractionExpressionInterface;

/**
 *
 * @author James F. Bowring
 */
public class SpotSummaryDetails {
    private double[][] values;
    private List<ShrimpFractionExpressionInterface> selectedSpots;

    private SpotSummaryDetails() {
    }

    /**
     *
     * @param values
     * @param selectedSpots
     */
    public SpotSummaryDetails(double[][] values, List<ShrimpFractionExpressionInterface> selectedSpots) {
        this.values = values;
        this.selectedSpots = selectedSpots;
    }

    /**
     * @return the values
     */
    public double[][] getValues() {
        return values;
    }

    /**
     * @return the selectedSpots
     */
    public List<ShrimpFractionExpressionInterface> getSelectedSpots() {
        return selectedSpots;
    }
    
    
    
}
