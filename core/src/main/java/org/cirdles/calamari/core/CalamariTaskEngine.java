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
package org.cirdles.calamari.core;

import java.util.List;
import org.cirdles.calamari.shrimp.ShrimpFraction;
import org.cirdles.calamari.tasks.TaskInterface;
import org.cirdles.calamari.tasks.storedTasks.SquidTask1;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class CalamariTaskEngine {

    public CalamariTaskEngine() {
    }

    protected void performTask(List<ShrimpFraction> shrimpFractions) {
        if (shrimpFractions.size() > 0) {
            for (int f = 0; f < shrimpFractions.size(); f++) {
                TaskInterface task = new SquidTask1(shrimpFractions.get(f));
                task.evaluateTaskExpressions();
            }
        }

    }
}
